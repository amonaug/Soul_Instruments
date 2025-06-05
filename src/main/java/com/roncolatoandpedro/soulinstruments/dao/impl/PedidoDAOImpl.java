package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;

import java.sql.*;

public abstract class PedidoDAOImpl implements PedidoDAO{
    private Connection connection;

    public PedidoDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(PedidoDTO pedido) throws Exception{
        String sql = "INSERT INTO pedido (valorPedido) VALUES (?)";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setDouble(1, pedido.getValorPedido());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}