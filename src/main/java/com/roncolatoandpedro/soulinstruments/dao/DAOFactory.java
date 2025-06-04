package com.roncolatoandpedro.soulinstruments.dao;
import com.roncolatoandpedro.soulinstruments.dao.impl.FornecedorDAOImpl;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO;

import java.sql.*;

public class DAOFactory {
    private static final String URL = "jbdc/postgresql://localhost:5433/soulinstruments";
    private static final String USER = "user";
    private static final String PASSWORD = "1234";

    public static Connection CreateConnection() throws Exception{
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static FornecedorDAO CreateFornecedorDAO() throws Exception{
        return new FornecedorDAOImpl(CreateConnection());
    }
}
