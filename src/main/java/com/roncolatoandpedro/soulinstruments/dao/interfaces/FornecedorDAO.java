package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FornecedorDAO {
    FornecedorDTO salvar(FornecedorDTO fornecedor) throws SQLException;
    void atualizar(FornecedorDTO fornecedor) throws SQLException;
    void remover(Long idFornecedor) throws SQLException;
    Optional<FornecedorDTO> buscarPorId(Long idFornecedor) throws SQLException; // Alterado para Optional
    Optional<FornecedorDTO> buscarPorCnpj(String cnpj) throws SQLException;
    List<FornecedorDTO> listarTodos() throws SQLException;
    List<FornecedorDTO> buscarFornecedoresPorNome(String nomeParcial) throws SQLException;
}