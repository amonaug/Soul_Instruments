package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;

import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAOImpl implements ItemPedidoDAO {

    private final Connection conexao;

    public ItemPedidoDAOImpl(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public ItemPedidoDTO salvar(ItemPedidoDTO itemPedido, Long pedidoId, ProdutoDAO produtoDAO) throws SQLException {
        // 1. Buscar o preço do produto
        Optional<ProdutoDTO> produtoOpt = produtoDAO.buscarPorId(itemPedido.getIdProduto());
        if (produtoOpt.isEmpty()) {
            throw new SQLException("Produto com ID " + itemPedido.getIdProduto() + " não encontrado para o item do pedido.");
        }
        ProdutoDTO produto = produtoOpt.get();
        itemPedido.setPrecoUnitarioCompra(produto.getPreco());
        itemPedido.calcularValorTotalItem(); // Calcula o valor total do item



        // Colunas: id Chave Prim, pedido_id (FK), produto_id (FK), quantidade, preco_unitario_compra
        // Assumindo que itemPedido.getIdProduto() se refere ao ID da tabela produto
        String sql = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario_compra, valor_total_item) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, pedidoId);
            stmt.setLong(2, itemPedido.getIdProduto()); // Usando o id numérico do produto
            stmt.setInt(3, itemPedido.getQuantidade());
            stmt.setDouble(4, itemPedido.getPrecoUnitarioCompra());
            stmt.setDouble(5, itemPedido.getValorTotalItem());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        itemPedido.setId(generatedKeys.getLong(1));
                        itemPedido.setIdPedido(pedidoId); // Garante que o pedidoId está no DTO
                    } else {
                        throw new SQLException("Falha ao obter o ID gerado para o item do pedido.");
                    }
                }
            } else {
                throw new SQLException("Falha ao salvar o item do pedido, nenhuma linha afetada.");
            }
            return itemPedido;
        }
    }

    private ItemPedidoDTO mapearResultSetParaItemPedidoDTO(ResultSet rs) throws SQLException {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setId(rs.getLong("id"));
        item.setIdPedido(rs.getLong("pedido_id"));
        item.setIdProduto(rs.getLong("produto_id"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUnitarioCompra(rs.getDouble("preco_unitario_compra"));
        item.setValorTotalItem(rs.getDouble("valor_total_item"));
        item.calcularValorTotalItem();

        return item;
    }

    @Override
    public List<ItemPedidoDTO> buscarPorPedidoId(Long pedidoId) throws SQLException {
        List<ItemPedidoDTO> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_pedido WHERE pedido_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapearResultSetParaItemPedidoDTO(rs));
                }
            }
        }
        return itens;
    }
}