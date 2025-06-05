package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import java.sql.*;

public abstract class ProdutoDAOImpl implements ProdutoDAO {
    public Connection connection;

    public ProdutoDAOImpl(Connection connection) throws Exception{
        this.connection = connection;
    }

    @Override
    public void salvar(ProdutoDTO produto){
        String sql = "INSERT INTO PRODUTO (marca, modelo, preco, quantidade, instrumento_id, fornecedor_cnpj) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, produto.getMarca());
            stmt.setString(2, produto.getModelo());
            stmt.setDouble(3, produto.getPreco());
            stmt.setLong(3, produto.getQuantidade());
            stmt.setLong(4, produto.getInstrumentoId());
            stmt.setString(5, produto.getCnpj());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}