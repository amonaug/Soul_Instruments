package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO; // Usar a interface, não a implementação diretamente aqui
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PedidoDAOImpl implements PedidoDAO {

    private static final Logger logger = Logger.getLogger(PedidoDAOImpl.class.getName());

    private final Connection conexao;
    private final ProdutoDAO produtoDAO; // Injetar a interface, não a implementação específica se possível
    private final ItemPedidoDAO itemPedidoDAO;

    /**
     * Construtor para PedidoDAOImpl. Recebe a conexão e as DAOs dependentes.
     * Este é o construtor principal a ser usado pelo DAOFactory.
     *
     * @param conexao A conexão com o banco de dados.
     * @param produtoDAO A implementação de ProdutoDAO.
     * @param itemPedidoDAO A implementação de ItemPedidoDAO.
     */
    public PedidoDAOImpl(Connection conexao, ProdutoDAO produtoDAO, ItemPedidoDAO itemPedidoDAO) {
        this.conexao = conexao;
        this.produtoDAO = produtoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
    }

    // Os construtores redundantes foram removidos para manter apenas o construtor principal
    // que recebe todas as dependências.
    // public PedidoDAOImpl(Connection conexao, Connection conexao1) {
    //     this.conexao = conexao1;
    // }
    // public PedidoDAOImpl(Connection conexao, ProdutoDAO produtoDAO, ItemPedidoDAO itemPedidoDAO, Connection conexao1) {
    //     this.conexao = conexao1;
    // }
    // public PedidoDAOImpl(Connection conexao) {
    //     this.conexao = conexao;
    // }

    @Override
    public PedidoDTO salvar(PedidoDTO pedido) throws SQLException {
        // SQL ajustado para incluir nomeCliente e status, e removido dataEntrega se não for usada.
        // A coluna 'dataPedido' agora está correta.
        String sqlPedido = "INSERT INTO Pedido (nomeCliente, dataPedido, valorTotal, status, idFornecedor) VALUES (?, ?, ?, ?, ?)";

        boolean originalAutoCommit = conexao.getAutoCommit();
        try {
            conexao.setAutoCommit(false); // Inicia a transação

            // Antes de salvar o pedido mestre, precisamos dos preços dos produtos para os itens
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedidoDTO item : pedido.getItens()) {
                    // Se o valor unitário ainda não foi definido no DTO, buscamos o preço do produto
                    if (item.getValorUnitario() == null || item.getValorUnitario() == 0.0) {
                        Optional<ProdutoDTO> produtoOpt = produtoDAO.buscarPorId(item.getIdProduto());
                        if (produtoOpt.isPresent()) {
                            item.setValorUnitario(produtoOpt.get().getPreco());
                        } else {
                            throw new SQLException("Produto com ID " + item.getIdProduto() + " não encontrado para definir o valor unitário do item de pedido.");
                        }
                    }
                    item.calcularValorTotal(); // DTO calcula o valor total do item
                }
            }
            pedido.calcularValorTotalPedido(); // DTO calcula o valor total do pedido com base nos itens

            // Salvar o Pedido
            try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmtPedido.setString(1, pedido.getNomeCliente());
                // Converte java.util.Date para java.sql.Date
                stmtPedido.setDate(2, new java.sql.Date(pedido.getDataPedido().getTime()));
                stmtPedido.setDouble(3, pedido.getValorTotal());
                stmtPedido.setString(4, pedido.getStatus());
                stmtPedido.setLong(5, pedido.getIdFornecedor());

                int affectedRows = stmtPedido.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmtPedido.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            pedido.setIdPedido(generatedKeys.getLong(1));
                            logger.log(Level.INFO, "Pedido mestre salvo com ID: " + pedido.getIdPedido());
                        } else {
                            throw new SQLException("Falha ao obter o ID gerado para o pedido.");
                        }
                    }
                } else {
                    throw new SQLException("Falha ao salvar o pedido, nenhuma linha afetada.");
                }
            }

            // Salvar os Itens do Pedido usando ItemPedidoDAO
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedidoDTO item : pedido.getItens()) {
                    // ATENÇÃO: O ItemPedidoDAO.salvar atualmente espera ProdutoDAOImpl.
                    // O ideal é que a interface ItemPedidoDAO seja atualizada para esperar ProdutoDAO (interface)
                    // para manter a consistência e a boa prática de programar para interfaces.
                    itemPedidoDAO.salvar(item, pedido.getIdPedido(), (ProdutoDAOImpl) produtoDAO);
                    logger.log(Level.INFO, "Item de pedido salvo para Pedido ID: " + pedido.getIdPedido() + ", Produto ID: " + item.getIdProduto());
                }
            }

            conexao.commit();
            logger.log(Level.INFO, "Transação de salvar pedido concluída com sucesso para Pedido ID: " + pedido.getIdPedido());
            return pedido;

        } catch (SQLException e) {
            conexao.rollback();
            logger.log(Level.SEVERE, "Erro ao salvar pedido, rollback da transação. Erro: " + e.getMessage(), e);
            throw e;
        } finally {
            conexao.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public void atualizarStatus(PedidoDTO pedido) throws SQLException {
        // Assume que o PedidoDTO já tem o status atualizado e o idPedido.
        // Se houver necessidade de atualizar outros campos, o SQL e os setters devem ser ajustados.
        String sql = "UPDATE Pedido SET status = ? WHERE idPedido = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(2, pedido.getIdPedido());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.log(Level.WARNING, "Nenhum pedido encontrado com ID " + pedido.getIdPedido() + " para atualizar o status.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar status do pedido ID " + pedido.getIdPedido() + ". Erro: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void remover(Long idPedido) throws SQLException { // Assinatura corrigida
        // Lógica para remover o pedido e seus itens associados.
        // Depende de como suas chaves estrangeiras estão configuradas (ON DELETE CASCADE é o ideal).
        // Se não houver ON DELETE CASCADE, os itens devem ser removidos primeiro.
        String sqlDeleteItens = "DELETE FROM ItemPedido WHERE idPedido = ?";
        String sqlDeletePedido = "DELETE FROM Pedido WHERE idPedido = ?";

        boolean originalAutoCommit = conexao.getAutoCommit();
        try {
            conexao.setAutoCommit(false); // Inicia a transação

            // 1. Remover todos os itens associados a este pedido
            try (PreparedStatement stmtDeleteItens = conexao.prepareStatement(sqlDeleteItens)) {
                stmtDeleteItens.setLong(1, idPedido);
                int deletedItems = stmtDeleteItens.executeUpdate();
                logger.log(Level.INFO, deletedItems + " itens removidos para o pedido ID: " + idPedido);
            }

            // 2. Remover o pedido
            try (PreparedStatement stmtDeletePedido = conexao.prepareStatement(sqlDeletePedido)) {
                stmtDeletePedido.setLong(1, idPedido);
                int affectedRows = stmtDeletePedido.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Pedido com ID " + idPedido + " não encontrado para remoção.");
                } else {
                    logger.log(Level.INFO, "Pedido ID " + idPedido + " removido com sucesso.");
                }
            }
            conexao.commit();
        } catch (SQLException e) {
            conexao.rollback();
            logger.log(Level.SEVERE, "Erro ao remover pedido ID " + idPedido + ", rollback da transação. Erro: " + e.getMessage(), e);
            throw e;
        } finally {
            conexao.setAutoCommit(originalAutoCommit);
        }
    }


    private PedidoDTO mapearResultSetParaPedidoDTO(ResultSet rs) throws SQLException {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(rs.getLong("idPedido"));// Mapeia o nome do client
        pedido.setIdFornecedor(rs.getLong("idFornecedor"));
        // idProduto e quantidade não são mapeados aqui, pois são de ItemPedidoDTO
        // Eles foram inferidos para o PedidoDTO para a UI cadastrarPedidoGUI.java,
        // mas não devem ser colunas na tabela Pedido diretamente.

        return pedido;
    }

    @Override
    public Optional<PedidoDTO> buscarPorId(Long idPedido) throws SQLException {
        String sql = "SELECT idPedido, nomeCliente, dataPedido, valorTotal, status, idFornecedor FROM Pedido WHERE idPedido = ?";
        PedidoDTO pedido = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pedido = mapearResultSetParaPedidoDTO(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedido por ID " + idPedido + ". Erro: " + e.getMessage(), e);
            throw e;
        }

        if (pedido != null) {
            // Carregar os itens do pedido usando ItemPedidoDAO
            List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(idPedido);
            pedido.setItens(itens); // Isso também recalculará o valorTotal do pedido no DTO
            logger.log(Level.INFO, "Pedido ID " + pedido.getIdPedido() + " encontrado e itens carregados.");
            return Optional.of(pedido);
        }
        logger.log(Level.INFO, "Pedido com ID " + idPedido + " não encontrado.");
        return Optional.empty();
    }

    @Override
    public List<PedidoDTO> listarTodos() throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT idPedido, nomeCliente, dataPedido, valorTotal, status, idFornecedor FROM Pedido ORDER BY dataPedido DESC";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                pedido.setItens(itens);
                pedidos.add(pedido);
            }
            logger.log(Level.INFO, "Listados " + pedidos.size() + " pedidos.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar todos os pedidos. Erro: " + e.getMessage(), e);
            throw e;
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorPeriodo(java.util.Date dataInicio, java.util.Date dataFim) throws SQLException { // Assinatura corrigida
        List<PedidoDTO> pedidos = new ArrayList<>();
        // As colunas no SELECT devem ser explícitas e corresponder ao mapeamento
        String sql = "SELECT idPedido, nomeCliente, dataPedido, valorTotal, status, idFornecedor FROM Pedido WHERE dataPedido BETWEEN ? AND ? ORDER BY dataPedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dataInicio.getTime())); // Converte java.util.Date para java.sql.Date
            stmt.setDate(2, new java.sql.Date(dataFim.getTime()));     // Converte java.util.Date para java.sql.Date
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
            logger.log(Level.INFO, "Listados " + pedidos.size() + " pedidos no período de " + dataInicio + " a " + dataFim + ".");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar pedidos por período. Erro: " + e.getMessage(), e);
            throw e;
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorFornecedor(Long idFornecedor) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        // As colunas no SELECT devem ser explícitas e corresponder ao mapeamento
        String sql = "SELECT idPedido, nomeCliente, dataPedido, valorTotal, status, idFornecedor FROM Pedido WHERE idFornecedor = ? ORDER BY dataPedido DESC";
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
            logger.log(Level.INFO, "Listados " + pedidos.size() + " pedidos para o fornecedor ID: " + idFornecedor + ".");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar pedidos por fornecedor. Erro: " + e.getMessage(), e);
            throw e;
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> buscarPorNomeCliente(String nomeClienteParcial) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        // Use LIKE para busca parcial e LOWER para case-insensitivity
        String sql = "SELECT idPedido, nomeCliente, dataPedido, valorTotal, status, idFornecedor FROM Pedido WHERE LOWER(nomeCliente) LIKE LOWER(?) ORDER BY dataPedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nomeClienteParcial + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
            logger.log(Level.INFO, "Listados " + pedidos.size() + " pedidos para o cliente: '" + nomeClienteParcial + "'.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos por nome de cliente. Erro: " + e.getMessage(), e);
            throw e;
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> buscarPorStatus(String status) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        // As colunas no SELECT devem ser explícitas e corresponder ao mapeamento
        String sql = "SELECT idPedido, nomeCliente, dataPedido, valorTotal, status, idFornecedor FROM Pedido WHERE status = ? ORDER BY dataPedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
            logger.log(Level.INFO, "Listados " + pedidos.size() + " pedidos com status: '" + status + "'.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos por status. Erro: " + e.getMessage(), e);
            throw e;
        }
        return pedidos;
    }
}
