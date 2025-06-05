package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;

import java.sql.*;

public class ItemPedidoDAOImpl implements ItemPedidoDAO {
    private final Connection connection;

    public ItemPedidoDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(ItemPedidoDTO itemPedido){
        String sql = "INSERT INTO itempedido (pedido_id, produto_id, quantidade) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setLong(1, itemPedido.getIdPedido());
            stmt.setLong(2, itemPedido.getIdProduto());
            stmt.setInt(3, itemPedido.getQuantidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}