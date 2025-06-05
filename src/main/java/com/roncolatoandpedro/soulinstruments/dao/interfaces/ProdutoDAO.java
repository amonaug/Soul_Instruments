package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

public interface ProdutoDAO {
   void salvar(ProdutoDAO produtoDAO) throws Exception;

   void salvar(ProdutoDTO produto);
}