package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAOImpl implements PedidoDAO {

    private final Connection conexao;
    // Opcional: Injetar ItemPedidoDAO se preferir delegar a lógica de item_pedido,
    // mas para manter como no PDF, podemos fazer aqui.
    // private final ItemPedidoDAO itemPedidoDAO;

    public PedidoDAOImpl(Connection conexao /*, ItemPedidoDAO itemPedidoDAO (se injetado) */) {
        this.conexao = conexao;
        // this.itemPedidoDAO = itemPedidoDAO;
    }

    @Override
    public PedidoDTO salvar(PedidoDTO pedido) throws SQLException {
        // Colunas: id (SERIAL PK), data_pedido, data_entrega, valor_total, fornecedor_id
        String sqlPedido = "INSERT INTO pedido (data_pedido, data_entrega, valor_total, fornecedor_id) VALUES (?, ?, ?, ?)";

        boolean originalAutoCommit = conexao.getAutoCommit(); // Salva o estado original do autoCommit
        try {
            conexao.setAutoCommit(false); // Inicia a transação

            // 1. Salvar o Pedido mestre
            try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmtPedido.setDate(1, Date.valueOf(pedido.getDataPedido()));
                if (pedido.getDataEntrega() != null) {
                    stmtPedido.setDate(2, Date.valueOf(pedido.getDataEntrega()));
                } else {
                    stmtPedido.setNull(2, java.sql.Types.DATE);
                }
                stmtPedido.setDouble(3, pedido.getValorTotal()); // Assumindo que o valor total já está calculado
                stmtPedido.setLong(4, pedido.getFornecedorId());

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

            // 2. Salvar os Itens do Pedido
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                // Se ItemPedidoDAO não for injetado, implemente a lógica aqui:
                String sqlItem = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario_compra) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtItem = conexao.prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS)) {
                    for (ItemPedidoDTO item : pedido.getItens()) {
                        stmtItem.setLong(1, pedido.getIdPedido()); // ID do pedido mestre
                        stmtItem.setLong(2, item.getIdProduto());    // ID do produto
                        stmtItem.setInt(3, item.getQuantidade());
                        stmtItem.setDouble(4, item.getPrecoUnitarioCompra());

                        int affectedItemRows = stmtItem.executeUpdate();
                        if (affectedItemRows > 0) {
                            try (ResultSet generatedItemKeys = stmtItem.getGeneratedKeys()) {
                                if (generatedItemKeys.next()) {
                                    item.setId(generatedItemKeys.getLong(1));
                                    item.setIdPedido(pedido.getIdPedido());
                                }
                            }
                        } else {
                            throw new SQLException("Falha ao salvar o item do pedido: " + item.getIdProduto());
                        }
                        // stmtItem.addBatch(); // Se fosse usar batch update
                    }
                    // stmtItem.executeBatch(); // Se fosse usar batch update
                }
            }

            conexao.commit(); // Comita a transação se tudo deu certo
            return pedido;

        } catch (SQLException e) {
            conexao.rollback(); // Desfaz a transação em caso de erro
            throw e; // Re-lança a exceção
        } finally {
            conexao.setAutoCommit(originalAutoCommit); // Restaura o estado original do autoCommit
            // A conexão não é fechada aqui.
        }
    }

    @Override
    public void atualizarStatus(PedidoDTO pedido) throws SQLException {
        // Exemplo: Atualizar apenas data de entrega e valor total
        String sql = "UPDATE pedido SET data_entrega = ?, valor_total = ? WHERE id = ?";
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
    public void remover(Long id) throws SQLException {
        // Importante: Remover itens do pedido primeiro para evitar quebra de FK,
        // ou configurar ON DELETE CASCADE no banco.
        String sqlItens = "DELETE FROM item_pedido WHERE pedido_id = ?";
        String sqlPedido = "DELETE FROM pedido WHERE id = ?";

        boolean originalAutoCommit = conexao.getAutoCommit();
        try {
            conexao.setAutoCommit(false);

            try (PreparedStatement stmtItens = conexao.prepareStatement(sqlItens)) {
                stmtItens.setLong(1, id);
                stmtItens.executeUpdate();
            }

            try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido)) {
                stmtPedido.setLong(1, id);
                stmtPedido.executeUpdate();
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
        PedidoDTO pedido = new PedidoDTO(
                rs.getLong("id"),
                rs.getDate("data_pedido").toLocalDate(),
                rs.getDate("data_entrega") != null ? rs.getDate("data_entrega").toLocalDate() : null,
                rs.getDouble("valor_total"),
                rs.getLong("fornecedor_id")
        );
        // Itens serão carregados separadamente
        return pedido;
    }


    @Override
    public Optional<PedidoDTO> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM pedido WHERE id = ?";
        PedidoDTO pedido = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pedido = mapearResultSetParaPedidoDTO(rs);
                }
            }
        }

        if (pedido != null) {
            // Carregar os itens do pedido
            // Se você optou por ter ItemPedidoDAO e ele foi injetado:
            // ItemPedidoDAO itemDao = new ItemPedidoDAOImpl(this.conexao); // Ou obter via DAOFactory se não injetado
            // pedido.setItens(itemDao.buscarPorPedidoId(id));
            // Senão, implemente a lógica de buscar itens aqui:
            String sqlItens = "SELECT * FROM item_pedido WHERE pedido_id = ?";
            List<ItemPedidoDTO> itens = new ArrayList<>();
            try(PreparedStatement stmtItens = conexao.prepareStatement(sqlItens)) {
                stmtItens.setLong(1, id);
                try(ResultSet rsItens = stmtItens.executeQuery()) {
                    ItemPedidoDAOImpl itemPedidoDAOImpl = new ItemPedidoDAOImpl(conexao); // temporário para mapear
                    while(rsItens.next()) {
                        // Precisa de um método para mapear ResultSet para ItemPedidoDTO
                        // ou fazer o mapeamento aqui.
                        // Exemplo:
                        itens.add(new ItemPedidoDTO(
                                rsItens.getLong("id"),
                                rsItens.getLong("pedido_id"),
                                rsItens.getLong("produto_id"), // Assumindo que você busca o ID do produto
                                rsItens.getInt("quantidade"),
                                rsItens.getDouble("preco_unitario_compra")
                        ));
                    }
                }
            }
            pedido.setItens(itens);
            return Optional.of(pedido);
        }
        return Optional.empty();
    }

    @Override
    public List<PedidoDTO> listarTodos() throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido ORDER BY data_pedido DESC";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                // Para cada pedido, buscar seus itens. Isso pode ser ineficiente (N+1 query).
                // Uma alternativa seria fazer um JOIN complexo ou carregar itens sob demanda.
                // Por simplicidade, vamos carregar aqui (mas ciente da performance).
                List<ItemPedidoDTO> itens = new ItemPedidoDAOImpl(conexao).buscarPorPedidoId(pedido.getIdPedido());
                pedido.setItens(itens);
                pedidos.add(pedido);
            }
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido WHERE data_pedido BETWEEN ? AND ? ORDER BY data_pedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = new ItemPedidoDAOImpl(conexao).buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorFornecedor(Long fornecedorId) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido WHERE fornecedor_id = ? ORDER BY data_pedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, fornecedorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = new ItemPedidoDAOImpl(conexao).buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }
}