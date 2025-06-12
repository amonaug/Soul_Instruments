package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO; // Sua entidade Produto
import com.roncolatoandpedro.soulinstruments.dto.Categoria;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProdutoDAO {
    ProdutoDTO salvar(ProdutoDTO produto) throws SQLException; //Retornar o DTO com ID permite que retore o objeto por completo após a persistência
    void atualizar(ProdutoDTO produto) throws SQLException;
    void deletar(Long codigoProduto) throws SQLException;
    Optional<ProdutoDTO> buscarPorId(Long id) throws SQLException;
    Optional<ProdutoDTO> buscarPorCodigoProduto(String codigoProduto) throws SQLException;
    List<ProdutoDTO> listarTodos() throws SQLException;
    List<ProdutoDTO> listarPorCategoria(Categoria categoria) throws SQLException;
}