package com.roncolatoandpedro.soulinstruments.dao;

import com.roncolatoandpedro.soulinstruments.dao.impl.FornecedorDAOImpl;
import com.roncolatoandpedro.soulinstruments.dao.impl.ItemPedidoDAOImpl;
import com.roncolatoandpedro.soulinstruments.dao.impl.PedidoDAOImpl;
import com.roncolatoandpedro.soulinstruments.dao.impl.ProdutoDAOImpl;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOFactory {
    private static final String URL = "jbdc/postgresql://localhost:5433/soulinstruments";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";


    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL JDBC não encontrado. Verifique o classpath.");
            e.printStackTrace();
        }
    }
    public static Connection getConexao() throws Exception{
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static FornecedorDAO CreateFornecedorDAO() throws Exception{
        return new FornecedorDAOImpl(getConexao());
    }
    public static ProdutoDAO criarProdutoDAO() throws SQLException {
        return new ProdutoDAOImpl(getConexao());
    }

    public static FornecedorDAO criarFornecedorDAO() throws SQLException {
        return new FornecedorDAOImpl(getConexao());
    }

    public static ItemPedidoDAO criarItemPedidoDAO() throws SQLException {
        // Se ItemPedidoDAOImpl não precisar de outras dependências DAO no construtor
        return new ItemPedidoDAOImpl(getConexao());
    }

    public static PedidoDAO criarPedidoDAO() throws SQLException {
        // PedidoDAOImpl precisa de ItemPedidoDAO e ProdutoDAO
        // Neste caso, o DAOFactory precisa instanciá-los.
        Connection conexao = getConexao(); // Obter a conexão uma vez para todos os DAOs nesta operação

        // Criar as dependências DAO usando a mesma conexão para consistência transacional
        // se PedidoDAOImpl for controlar a transação para todos eles.
        // No entanto, no modelo atual, cada DAOImpl gerencia sua própria transação
        // (exceto PedidoDAOImpl que gerencia para si e para os itens).

        // Se PedidoDAOImpl lida com transações internamente para si e seus itens:
        ProdutoDAO produtoDAO = new ProdutoDAOImpl(conexao); // Usando a mesma conexão
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(conexao); // Usando a mesma conexão

        // Se PedidoDAOImpl for controlar a transação para produtoDAO e itemPedidoDAO,
        // a conexão passada para eles deve ser a mesma e PedidoDAOImpl faria conexao.commit/rollback.
        // Na nossa implementação atual de PedidoDAOImpl, ele já recebe os DAOs, e a conexão
        // principal é usada para as operações do pedido, e os DAOs de item/produto usam a conexão deles.
        // Para garantir que tudo ocorra na mesma transação controlada por PedidoDAOImpl,
        // é crucial que PedidoDAOImpl, ItemPedidoDAOImpl e ProdutoDAOImpl (quando chamados por PedidoDAOImpl)
        // operem sobre a *mesma instância* de Connection, e que PedidoDAOImpl controle o commit/rollback.

        // Ajuste para DAOFactory:
        // ProdutoDAO produtoDAO = new ProdutoDAOImpl(conexao); // Passa a conexão
        // ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(conexao); // Passa a conexão
        return new PedidoDAOImpl(conexao, itemPedidoDAO, produtoDAO);
    }
}
