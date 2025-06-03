package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.model.Produto; // Sua entidade Produto
import java.util.List;
import java.util.Optional;

public interface ProdutoDAO {
    void salvar(Produto produto);
    void atualizar(Produto produto);
    void remover(Long id);
    Optional<Produto> buscarPorId(Long id);
    Optional<Produto> buscarPorCodigoProduto(String codigoProduto);
    List<Produto> listarTodos();
    // Adicione outros métodos específicos conforme necessário (ex: buscarPorCategoria)
}