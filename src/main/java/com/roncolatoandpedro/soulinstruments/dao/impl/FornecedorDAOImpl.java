package com.roncolatoandpedro.soulinstruments.dao.impl;


import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;

import java.sql.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;


public class FornecedorDAOImpl implements FornecedorDAO {
    private final Connection conexao;

    public FornecedorDAOImpl(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public FornecedorDTO salvar(FornecedorDTO fornecedor) throws SQLException {
        String sql = "INSERT INTO fornecedor (nomeFornecedor, cnpj, descricao) VALUES (?, ?, ?)";

        try(PreparedStatement stmt = conexao.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, fornecedor.getNomeFornecedor());
            stmt.setString(2, fornecedor.getCnpj());
            stmt.setString(3, fornecedor.getDescricao());
            int affectedRows = stmt.executeUpdate(); //irá receber o numero de colunas afetadas
            if(affectedRows > 0){ //se for maior que  (o que é bom)
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        fornecedor.setId(generatedKeys.getLong(1)); //obtem o id gerado para o fornecedor
                    } else {
                        throw new SQLException("Falha ao obter o ID gerado para o fornecedor");
                    }
                }
            } else {
                throw new SQLException("Falha ao salvar o fornecedor, nenhuma linha afetada");
            }
            return fornecedor;
        }
    }



    @Override
    public void atualizar(FornecedorDTO fornecedor) throws SQLException {
        String sql = "UPDATE fornecedor SET nome_fantasia = ?, cnpj = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, fornecedor.getNomeFornecedor());
            stmt.setString(2, fornecedor.getCnpj());
            stmt.setString(3, fornecedor.getDescricao());
            stmt.setLong(4, fornecedor.getId());
            stmt.executeUpdate();
        }
    }


    @Override
    public void remover(Long id) throws SQLException {
        // Pedro Adicionar lógica para verificar/tratar dependências (produtos, pedidos) antes de remover,
        // ou configurar o banco para ON DELETE CASCADE/SET NULL, se apropriado.
        String sql = "DELETE FROM fornecedor WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }


    private FornecedorDTO mapearResultSetParaFornecedorDTO(ResultSet rs) throws SQLException {
        return new FornecedorDTO(
                rs.getLong("id"),
                rs.getString("nome_fantasia"),
                rs.getString("cnpj"),
                rs.getString("descricao")
        );
    }

    @Override
    public Optional<FornecedorDTO> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM fornecedor WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearResultSetParaFornecedorDTO(rs));
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<FornecedorDTO> buscarPorCnpj(String cnpj) throws SQLException {
        String sql = "SELECT * FROM fornecedor WHERE cnpj = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearResultSetParaFornecedorDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<FornecedorDTO> listarTodos() throws SQLException {
        List<FornecedorDTO> fornecedores = new ArrayList<>();
        String sql = "SELECT * FROM fornecedor ORDER BY nome_fantasia";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fornecedores.add(mapearResultSetParaFornecedorDTO(rs));
            }
        }
        return fornecedores;
    }


}
