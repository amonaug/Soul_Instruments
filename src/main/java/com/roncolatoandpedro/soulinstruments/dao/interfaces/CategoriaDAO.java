package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.Categoria;
import java.util.List;
import java.sql.SQLException;

public interface CategoriaDAO {
    List<Categoria> listarTodos() throws SQLException;
}
