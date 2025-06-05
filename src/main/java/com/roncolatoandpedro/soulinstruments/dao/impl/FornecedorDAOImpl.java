package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.sun.jdi.ClassNotPreparedException;
import org.postgresql.core.ConnectionFactory;

import java.sql.*;
import java.sql.PreparedStatement;

public class FornecedorDAOImpl implements FornecedorDAO {
    private final Connection connection;

    public FornecedorDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(FornecedorDTO fornecedor) throws Exception{
        String sql = "INSERT INTO fornecedor (nome, cnpj, email) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, fornecedor.getNomeFornecedor());
            stmt.setString(2, fornecedor.getCnpj());
            stmt.setString(3, fornecedor.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}