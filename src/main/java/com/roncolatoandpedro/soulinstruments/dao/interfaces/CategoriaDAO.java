package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.Categoria;
import java.util.List;
import java.sql.SQLException;

public interface CategoriaDAO {
    /**
     * Lista todas as categorias disponíveis no sistema.
     *
     * @return Uma lista de objetos Categoria.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Categoria> listarTodos() throws SQLException;
}
