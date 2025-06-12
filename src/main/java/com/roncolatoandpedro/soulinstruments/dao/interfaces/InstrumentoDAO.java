package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional;

public interface InstrumentoDAO {
    InstrumentoDTO salvar(InstrumentoDTO instrumento) throws SQLException;
    void atualizar(InstrumentoDTO instrumento) throws SQLException;
    void remover(Long idInstrumento) throws SQLException;
    Optional<InstrumentoDTO> buscarPorId(Long idInstrumento) throws SQLException;
    List<InstrumentoDTO> listarTodos() throws SQLException;
}
