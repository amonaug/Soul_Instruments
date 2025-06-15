package com.roncolatoandpedro.soulinstruments.dao;

import com.roncolatoandpedro.soulinstruments.dao.impl.*;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOFactory {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SoulInstruments";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "sua_senha_aqui";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver PostgreSQL não encontrado.");
        }
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // --- MÉTODOS FACTORY ---
    public static ProdutoDAO criarProdutoDAO() throws SQLException {
        return new ProdutoDAOImpl(getConexao());
    }

    public static FornecedorDAO criarFornecedorDAO() throws SQLException {
        return new FornecedorDAOImpl(getConexao());
    }

    public static ItemPedidoDAO criarItemPedidoDAO() throws SQLException {
        return new ItemPedidoDAOImpl(getConexao());
    }

    public static PedidoDAO criarPedidoDAO() throws SQLException {
        Connection conexao = getConexao();
        ProdutoDAO produtoDAO = new ProdutoDAOImpl(conexao);
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(conexao);
        return new PedidoDAOImpl(conexao, itemPedidoDAO, produtoDAO);
    }
}
