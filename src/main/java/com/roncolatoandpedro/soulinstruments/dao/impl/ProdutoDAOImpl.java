package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;
import com.roncolatoandpedro.soulinstruments.dto.Categoria;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ProdutoDAOImpl implements ProdutoDAO {
    private final Connection conexao;

    public ProdutoDAOImpl(Connection conexao) {
        this.conexao = conexao;
    }


    @Override
    public ProdutoDTO salvar(ProdutoDTO produto) {
        String sql = "INSERT INTO produto (nome, categoria, codigo_produto, marca, modelo, descricao, preco, quantidade_estoque, fornecedor_id)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement (sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria().name());
            stmt.setString(3, produto.getCodigoProduto());
            stmt.setString(4, produto.getMarca());
            stmt.setString(5, produto.getModelo());
            stmt.setString(6, produto.getDescricao());
            stmt.setInt(7, produto.getQuantidadeEstoque());
            stmt.setDouble(8, produto.getPreco());

            if(produto.getFornecedorId() == null){
                stmt.setLong(9, produto.getFornecedorId());
            } else {
                stmt.setNull(9, java.sql.Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()){
                        produto.setId(generatedKeys.getLong(1)); //define o ID gerado para o produto
                    } else {
                        throw new SQLException("Falha ao obter ID gerado para o produto");
                    }
                }
            } else {
                throw new SQLException("Falha ao salvar Produto, nenhuma linha afetada");
            }
            return produto;
        }
    }

    @Override
    public ProdutoDTO atualizar(ProdutoDTO produto){
        String sql = "UPDATE produto SET nome = ?, categoria = ?, codigo_produto = ?, marca = ?, modelo = ?, descricao = ?, " +
                "preco = ?, quantidade_estoque = ?, fornecedor_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria().name());
            stmt.setString(3, produto.getCodigoProduto());
            stmt.setString(4, produto.getMarca());
            stmt.setString(5, produto.getModelo());
            stmt.setString(6, produto.getDescricao());
            stmt.setInt(7, produto.getQuantidadeEstoque());
            stmt.setDouble(8, produto.getPreco());
            if (produto.getFornecedorId() == null) {
                stmt.setLong(9, produto.getFornecedorId());
            } else {
                stmt.setNull(9, java.sql.Types.BIGINT);
            }
            stmt.setLong(10, produto.getId());

            stmt.executeUpdate();
        }
    }


    @Override
    public ProdutoDTO deletar(ProdutoDTO produto){
        String sql = "DELETE FROM produto WHERE id = ?";
    }



}