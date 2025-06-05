package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO; // Para buscar o preço

import java.sql.SQLException;
import java.util.List;

public interface ItemPedidoDAO {
    // O ProdutoDAO será necessário para buscar o preço do produto, por isso referência-lo
    ItemPedidoDTO salvar(ItemPedidoDTO itemPedido, Long pedidoId, ProdutoDAO produtoDAO) throws SQLException;
    List<ItemPedidoDTO> buscarPorPedidoId(Long pedidoId) throws SQLException;
}