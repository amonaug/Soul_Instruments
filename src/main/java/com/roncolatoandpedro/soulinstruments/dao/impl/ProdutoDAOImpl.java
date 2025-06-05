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
    public ProdutoDTO salvar(ProdutoDTO produto) throws SQLException{
        String sql = "INSERT INTO produto (nome, categoria, codigo_produto, marca, modelo, descricao, preco, quantidade_estoque, fornecedor_id)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement (sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria().name());
            stmt.setLong(3, produto.getCodigoProduto());
            stmt.setString(4, produto.getMarca());
            stmt.setString(5, produto.getModelo());
            stmt.setString(6, produto.getDescricao());
            stmt.setInt(7, produto.getQuantidadeEstoque());
            stmt.setDouble(8, produto.getPreco());

            if(produto.getFornecedorId() != null){
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
    public void atualizar(ProdutoDTO produto) throws SQLException{
        String sql = "UPDATE produto SET nome = ?, categoria = ?, codigo_produto = ?, marca = ?, modelo = ?, descricao = ?, " +
                "preco = ?, quantidade_estoque = ?, fornecedor_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria().name());
            stmt.setLong(3, produto.getCodigoProduto());
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
    public void deletar(Long codigoProduto) throws SQLException{
        String sql = "DELETE FROM produto WHERE codigo_produto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, codigoProduto);
            stmt.executeUpdate();
        }
    }





    private ProdutoDTO mapearResultSetParaProdutoDTO(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String nome = resultSet.getString("nome");
        Categoria categoria = Categoria.valueOf(resultSet.getString("categoria"));
        Long codigoProduto = resultSet.getLong("codigo_produto");
        String marca = resultSet.getString("marca");
        String modelo = resultSet.getString("modelo");
        String descricao = resultSet.getString("descricao");
        int quantidadeEstoque = resultSet.getInt("quantidadeEstoque");
        double preco = resultSet.getDouble("preco");
        Long fornecedorId = resultSet.getLong("fornecedor_id");
        if (resultSet.wasNull()) {
            fornecedorId = null;
        }
        return new ProdutoDTO(id, nome, categoria, codigoProduto, marca, modelo, descricao, preco, quantidadeEstoque, fornecedorId);
    }

    @Override
    public Optional<ProdutoDTO> buscarPorId(Long id) throws SQLException{
        String sql = "SELECT * FROM produto WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return Optional.of(mapearResultSetParaProdutoDTO(rs));
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<ProdutoDTO> buscarPorCodigoProduto(String codigoProduto) throws SQLException {
        String sql = "SELECT * FROM produto WHERE codigo_produto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, codigoProduto);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return Optional.of(mapearResultSetParaProdutoDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ProdutoDTO> listarTodos() throws SQLException {
        String sql = "SELECT * FROM produto ORDER BY nome";
        List<ProdutoDTO> produtos = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
                produtos.add(mapearResultSetParaProdutoDTO(rs));
            }
        return produtos;
        }


    @Override
    public List<ProdutoDTO> listarPorCategoria(Categoria categoria) throws SQLException{
        String sql = "SELECT * FROM produto WHERE categoria = ? ORDER BY nome";
        List<ProdutoDTO> produtos = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, categoria.name());
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    produtos.add(mapearResultSetParaProdutoDTO(rs));
                }
            }
        }
        return produtos;
    }
}