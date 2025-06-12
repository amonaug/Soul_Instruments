package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAOImpl implements PedidoDAO {

    private final Connection conexao;
    private final ItemPedidoDAO itemPedidoDAO;
    private final ProdutoDAO produtoDAO; //buscar os precos

    public PedidoDAOImpl(Connection conexao, ItemPedidoDAO itemPedidoDAO, ProdutoDAO produtoDAO) {
        this.conexao = conexao;
        this.itemPedidoDAO = itemPedidoDAO;
        this.produtoDAO = produtoDAO;
    }

    @Override
    public PedidoDTO salvar(PedidoDTO pedido) throws SQLException {
        String sqlPedido = "INSERT INTO Pedido (dataPedido, dataEntrega, valorTotal, idFornecedor) VALUES (?, ?, ?, ?)";

        boolean originalAutoCommit = conexao.getAutoCommit();
        try {
            conexao.setAutoCommit(false); // Inicia a transação

            // Antes de salvar o pedido mestre, precisamos dos preços dos produtos para os itens
            // e para calcular o valor_total do pedido.
            if (pedido.getItens() != null) {
                for (ItemPedidoDTO item : pedido.getItens()) {
                    if (item.getValorUnitario() == 0 && item.getIdProduto() != null) { // Preço ainda não definido
                        Optional<ProdutoDTO> produtoOpt = produtoDAO.buscarPorId(item.getIdProduto());
                        if (produtoOpt.isPresent()) {
                            item.setValorUnitario(produtoOpt.get().getPreco());
                            item.calcularValorTotal(); // DTO calcula o valor do item
                        } else {
                            throw new SQLException("Produto com ID " + item.getIdProduto() + " não encontrado.");
                        }
                    }
                }
            }
            pedido.calcularValorTotalPedido(); // DTO calcula o valor total do pedido

            //Salvar o Pedido
            try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmtPedido.setDate(1, Date.valueOf(pedido.getDataPedido()));
                if (pedido.getDataEntrega() != null) {
                    stmtPedido.setDate(2, Date.valueOf(pedido.getDataEntrega()));
                } else {
                    stmtPedido.setNull(2, java.sql.Types.DATE);
                }
                stmtPedido.setDouble(3, pedido.getValorTotal()); // Agora o valor total está calculado
                stmtPedido.setLong(4, pedido.getIdFornecedor());

                int affectedRows = stmtPedido.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmtPedido.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            pedido.setIdPedido(generatedKeys.getLong(1));
                        } else {
                            throw new SQLException("Falha ao obter o ID gerado para o pedido.");
                        }
                    }
                } else {
                    throw new SQLException("Falha ao salvar o pedido, nenhuma linha afetada.");
                }
            }

            //Salvar os Itens do Pedido usando ItemPedidoDAO
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedidoDTO item : pedido.getItens()) {
                    // O item já deve ter o precoUnitarioCompra e valorTotalItem calculados
                    itemPedidoDAO.salvar(item, pedido.getIdPedido(), produtoDAO); // Passa o produtoDAO se o ItemPedidoDAO.salvar ainda precisar dele
                    // (na nossa versão atual de ItemPedidoDAO.salvar, ele busca o preço)
                }
            }

            conexao.commit();
            return pedido;

        } catch (SQLException e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public void atualizarStatus(PedidoDTO pedido) throws SQLException {
        // Recalcula o valor total caso os itens tenham sido modificados externamente
        // (embora o ideal seja que o DTO gerencie isso ao modificar a lista de itens)
        pedido.calcularValorTotalPedido();

        String sql = "UPDATE Pedido SET dataEntrega = ?, valorTotal = ? WHERE idPedido = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (pedido.getDataEntrega() != null) {
                stmt.setDate(1, Date.valueOf(pedido.getDataEntrega()));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }
            stmt.setDouble(2, pedido.getValorTotal());
            stmt.setLong(3, pedido.getIdPedido());
            stmt.executeUpdate();
        }
    }

    @Override
    public void remover(Long idPedido, Long idItemPedido) throws SQLException {
        String sqlItens = "DELETE FROM ItemPedido WHERE idItemPedido = ?";
        String sqlPedido = "DELETE FROM Pedido WHERE idPedido = ?";

        boolean originalAutoCommit = conexao.getAutoCommit();
        try {
            conexao.setAutoCommit(false);

            try (PreparedStatement stmtItens = conexao.prepareStatement(sqlItens)) {
                stmtItens.setLong(1, idItemPedido);
                stmtItens.executeUpdate();
            }

            try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido)) {
                stmtPedido.setLong(1, idItemPedido);
                int affectedRows = stmtPedido.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Pedido com ID " + idPedido + " não encontrado para remoção.");
                }
            }
            conexao.commit();
        } catch (SQLException e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.setAutoCommit(originalAutoCommit);
        }
    }


    private PedidoDTO mapearResultSetParaPedidoDTO(ResultSet rs) throws SQLException {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(rs.getLong("idPedido"));
        pedido.setDataPedido(rs.getDate("dataPedido").toLocalDate());
        if (rs.getDate("dataEntrega") != null) {
            pedido.setDataEntrega(rs.getDate("dataEntrega").toLocalDate());
        }
        pedido.setIdFornecedor(rs.getLong("idFornecedor"));
        pedido.setValorTotal(rs.getDouble("valorTotal"));
        return pedido;
    }

    @Override
    public Optional<PedidoDTO> buscarPorId(Long idPedido) throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE idPedido = ?";
        PedidoDTO pedido = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pedido = mapearResultSetParaPedidoDTO(rs);
                }
                if (pedido != null) {
                    List<ItemPedidoDTO> itens = this.itemPedidoDAO.buscarPorPedidoId(idPedido); // Usa o DAO injetado
                    pedido.setItens(itens); // Isso recalculará o valorTotal do pedido no DTO
                    return Optional.of(pedido);
                }
            }
        }

        if (pedido != null) {
            // Carregar os itens do pedido usando ItemPedidoDAO
            List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(idPedido);
            pedido.setItens(itens); // Isso também recalculará o valorTotal do pedido
            // Se o valorTotal no banco for o "mestre", você pode carregá-lo
            // e não recalcular, mas geralmente é bom recalcular a partir dos itens para consistência.
            // Se você confia no valor_total da tabela pedido:
            //  // (já feito no mapearResultSetParaPedidoDTO se a coluna existir e for usada)
            // Senão, certifique-se que calcularValorTotalPedido() seja chamado após setItens()
            //  (o que já acontece no PedidoDTO.setItens)
            return Optional.of(pedido);
        }
        return Optional.empty();
    }

    @Override
    public List<PedidoDTO> listarTodos() throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido ORDER BY dataPedido DESC";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                pedido.setItens(itens);
                pedidos.add(pedido);
            }
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE dataPedido BETWEEN ? AND ? ORDER BY dataPedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorFornecedor(Long idFornecedor) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE idFornecedor = ? ORDER BY dataPedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }
}