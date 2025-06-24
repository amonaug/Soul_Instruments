package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO; // Usar a interface ProdutoDAO
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ItemPedidoDAOImpl implements ItemPedidoDAO {

    private static final Logger logger = Logger.getLogger(ItemPedidoDAOImpl.class.getName());

    private final Connection conexao;

    /**
     * Construtor para ItemPedidoDAOImpl. Recebe a conexão com o banco de dados.
     * Este é o construtor principal a ser usado pelo DAOFactory.
     *
     * @param conexao A conexão com o banco de dados.
     * @throws IllegalArgumentException Se a conexão fornecida for nula.
     */
    public ItemPedidoDAOImpl(Connection conexao) {
        if (conexao == null) {
            throw new IllegalArgumentException("A conexão não pode ser nula.");
        }
        this.conexao = conexao;
        logger.log(Level.INFO, "ItemPedidoDAOImpl inicializado com sucesso.");
    }

    // O construtor padrão foi removido. A injeção de conexão é obrigatória
    // para evitar NullPointerExceptions e garantir que a DAO sempre tenha uma conexão válida.
    // public ItemPedidoDAOImpl() {}

    @Override
    public ItemPedidoDTO salvar(ItemPedidoDTO itemPedido, Long idPedido, ProdutoDAO produtoDAO) throws SQLException {
        logger.log(Level.INFO, "Tentando salvar item de pedido para Pedido ID: " + idPedido + ", Produto ID: " + itemPedido.getIdProduto());

        // A lógica de buscar o preço unitário e calcular o valor total
        // pode ser feita na camada de serviço (business logic) antes de chamar o DAO.
        // No entanto, mantive a lógica aqui conforme o design atual da sua interface,
        // mas é uma área para refatoração futura se a complexidade aumentar.
        if (itemPedido.getValorUnitario() == null || itemPedido.getValorUnitario() == 0.0) {
            Optional<ProdutoDTO> produtoOpt = produtoDAO.buscarPorId(itemPedido.getIdProduto());
            if (produtoOpt.isEmpty()) {
                throw new SQLException("Produto com ID " + itemPedido.getIdProduto() + " não encontrado para definir o valor unitário do item do pedido.");
            }
            ProdutoDTO produto = produtoOpt.get();
            itemPedido.setValorUnitario(produto.getPreco());
        }
        itemPedido.calcularValorTotal(); // Garante que o valor total do item está atualizado

        // SQL: Assegure-se que os nomes das colunas correspondem exatamente ao seu banco de dados.
        // Colunas: idItemPedido (PK), idPedido (FK), idProduto (FK), quantidade, valorUnitario, valorTotal
        String sql = "INSERT INTO ItemPedido (idPedido, idProduto, quantidade, valorUnitario, valorTotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, idPedido);
            stmt.setLong(2, itemPedido.getIdProduto());
            stmt.setInt(3, itemPedido.getQuantidade());
            stmt.setDouble(4, itemPedido.getValorUnitario());
            stmt.setDouble(5, itemPedido.getValorTotal());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        itemPedido.setIdItemPedido(generatedKeys.getLong(1));
                        itemPedido.setIdPedido(idPedido); // Garante que o pedidoId está no DTO após salvar
                        logger.log(Level.INFO, "Item de pedido salvo com ID: " + itemPedido.getIdItemPedido());
                    } else {
                        throw new SQLException("Falha ao obter o ID gerado para o item do pedido.");
                    }
                }
            } else {
                throw new SQLException("Falha ao salvar o item do pedido, nenhuma linha afetada.");
            }
            return itemPedido;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao salvar item de pedido: " + e.getMessage(), e);
            throw e; // Relança a exceção para a camada superior
        }
    }

    /**
     * Mapeia um ResultSet para um objeto ItemPedidoDTO.
     * Assume que o ResultSet contém as colunas "idItemPedido", "idPedido", "idProduto",
     * "quantidade", "valorUnitario", "valorTotal".
     *
     * @param rs O ResultSet contendo os dados do item de pedido.
     * @return Um objeto ItemPedidoDTO preenchido.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private ItemPedidoDTO mapearResultSetParaItemPedidoDTO(ResultSet rs) throws SQLException {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setIdItemPedido(rs.getLong("idItemPedido"));
        item.setIdPedido(rs.getLong("idPedido"));
        item.setIdProduto(rs.getLong("idProduto"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setValorUnitario(rs.getDouble("valorUnitario"));
        item.setValorTotal(rs.getDouble("valorTotal")); // Mapeia o valorTotal direto do banco, se existir.
        // Se o valorTotal é sempre calculado no DTO, o 'item.calcularValorTotal()'
        // pode ser chamado após o mapeamento.
        // Por consistência, sugiro mapear do banco se a coluna existir e for precisa.
        return item;
    }

    @Override
    public List<ItemPedidoDTO> buscarPorPedidoId(Long idPedido) throws SQLException {
        List<ItemPedidoDTO> itens = new ArrayList<>();
        String sql = "SELECT idItemPedido, idPedido, idProduto, quantidade, valorUnitario, valorTotal FROM ItemPedido WHERE idPedido = ?"; // Colunas explícitas
        logger.log(Level.INFO, "Buscando itens para o Pedido ID: " + idPedido);

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapearResultSetParaItemPedidoDTO(rs));
                }
            }
            logger.log(Level.INFO, "Encontrados " + itens.size() + " itens para o Pedido ID: " + idPedido + ".");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao buscar itens por Pedido ID " + idPedido + ": " + e.getMessage(), e);
            throw e;
        }
        return itens;
    }
}
