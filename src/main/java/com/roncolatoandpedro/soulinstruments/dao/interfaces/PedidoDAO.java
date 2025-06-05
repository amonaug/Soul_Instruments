package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;

import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
import java.time.LocalDate;

public interface PedidoDAO {
    PedidoDTO salvar(PedidoDTO pedido) throws SQLException; //Retornar o DTO com ID permite que retore o objeto por completo após a persistência
    void atualizarStatus (PedidoDTO pedido) throws SQLException;
    void remover(Long id) ;
    Optional<PedidoDTO> buscarPorId(Long id) throws SQLException;
    Optional<PedidoDTO> buscarPorCnpj(String cnpj) throws SQLException;
    List<PedidoDTO> listarTodos() throws SQLException;
    List<PedidoDTO> listarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) throws SQLException;
    List<PedidoDTO> listarPoFornecedor(Long idFornecedor) throws SQLException;
}
