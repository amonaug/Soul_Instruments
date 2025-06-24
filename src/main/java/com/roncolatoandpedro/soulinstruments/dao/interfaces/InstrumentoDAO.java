package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional; // Importe Optional

public interface InstrumentoDAO {
    InstrumentoDTO salvar(InstrumentoDTO instrumento) throws SQLException;
    void atualizar(InstrumentoDTO instrumento) throws SQLException;
    void remover(Long idInstrumento) throws SQLException;

    // Alterado para retornar Optional<InstrumentoDTO> para melhor tratamento de casos onde o ID não é encontrado
    Optional<InstrumentoDTO> buscarPorId(Long idInstrumento) throws SQLException;

    List<InstrumentoDTO> listarTodos() throws SQLException;

    // Adicionado método para buscar instrumento por nome, retornando Optional para segurança
    Optional<InstrumentoDTO> buscarPorNome(String nome) throws SQLException;
}
