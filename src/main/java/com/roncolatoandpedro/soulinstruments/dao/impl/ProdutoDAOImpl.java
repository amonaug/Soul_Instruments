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
        String sql = "INSERT INTO Produto (idProduto, marca, modelo, descricao, preco, quantidadeEstoque, idInstrumento, idFornecedor)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement (sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, produto.getIdProduto());
            stmt.setString(2, produto.getMarca());
            stmt.setString(3, produto.getModelo());
            stmt.setString(4, produto.getDescricao());
            stmt.setDouble(5, produto.getPreco());
            stmt.setInt(6, produto.getQuantidadeEstoque());
            stmt.setLong(7, produto.getIdInstrumento());
            if (produto.getIdFornecedor() != null) {
                stmt.setLong(8, produto.getIdFornecedor());
            } else {
                stmt.setNull(8, java.sql.Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()){
                        produto.setIdProduto(generatedKeys.getLong(1)); //define o ID gerado para o produto
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
        String sql = "UPDATE Produto SET marca = ?, modelo = ?, descricao = ?, preco = ?, quantidadeEstoque = ?, idInstrumento = ?, idFornecedor = ? WHERE idProduto = ?, ";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, produto.getMarca());
            stmt.setString(2, produto.getModelo());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            stmt.setLong(6, produto.getIdInstrumento());
            stmt.setLong(7, produto.getIdFornecedor());
            stmt.setLong(8, produto.getIdProduto());
            if (produto.getIdFornecedor() != null) {
                stmt.setLong(9, produto.getIdFornecedor());
            } else {
                stmt.setNull(9, java.sql.Types.BIGINT);
            }
            stmt.executeUpdate();
        }
    }


    @Override
    public void remover(Long idProduto) throws SQLException{
        String sql = "DELETE FROM Produto WHERE idProduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idProduto);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("Falha ao excluir produto do produto");
        }
    }





    private ProdutoDTO mapearResultSetParaProdutoDTO(ResultSet resultSet) throws SQLException {
        Long idProduto = resultSet.getLong("idProduto");
        String marca = resultSet.getString("marca");
        String modelo = resultSet.getString("modelo");
        String descricao = resultSet.getString("descricao");
        double preco = resultSet.getDouble("preco");
        int quantidadeEstoque = resultSet.getInt("quantidadeEstoque");
        Long idInstrumento = resultSet.getLong("idInstrumento");
        Long idFornecedor = resultSet.getLong("idFornecedor");
        if (resultSet.wasNull()) {
            idFornecedor = null;
        }
        return new ProdutoDTO(idProduto, marca, modelo, descricao, preco, quantidadeEstoque, idInstrumento, idFornecedor);
    }

    @Override
    public Optional<ProdutoDTO> buscarPorId(Long idProduto) throws SQLException{
        String sql = "SELECT * FROM Produto WHERE idProduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setLong(1, idProduto);
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
        String sql = "SELECT * FROM Produto ORDER BY idInstrumento"; //aqui lista por idInstrumento - tipo de instrumento
        List<ProdutoDTO> produtos = new ArrayList<>();
        try (Statement stmt = conexao.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
                produtos.add(mapearResultSetParaProdutoDTO(rs));
            }
        return produtos;
        }
}