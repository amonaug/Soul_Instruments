package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;

public interface PedidoDAO {

    void salvar(PedidoDTO pedidoDTO) throws Exception;
    void atualizar(PedidoDTO pedidoDTO) throws Exception;
}
