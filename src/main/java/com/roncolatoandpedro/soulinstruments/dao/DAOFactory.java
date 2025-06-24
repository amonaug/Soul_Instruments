package com.roncolatoandpedro.soulinstruments.dao;

import com.roncolatoandpedro.soulinstruments.dao.impl.*;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.*;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOFactory {

    private static final Logger logger = Logger.getLogger(DAOFactory.class.getName());

    // Considere carregar essas informações de um arquivo de configuração (e.g., .properties)
    // para facilitar a mudança em diferentes ambientes (desenvolvimento, produção).
    private static final String DB_URL = "jdbc:postgresql://localhost:543/SoulInstruments";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "sua_senha_aqui"; // ATENÇÃO: Nunca exponha senhas diretamente no código fonte em produção.

    static {
        try {
            // Carrega o driver JDBC para PostgreSQL
            Class.forName("org.postgresql.Driver");
            logger.log(Level.INFO, "Driver PostgreSQL carregado com sucesso.");
        } catch (ClassNotFoundException e) {
            // Erro crítico se o driver não for encontrado. A aplicação não poderá se conectar.
            logger.log(Level.SEVERE, "Driver PostgreSQL não encontrado. Verifique suas dependências.", e);
            throw new ExceptionInInitializerError("Driver PostgreSQL não encontrado.");
        }
    }

    /**
     * Retorna uma nova conexão com o banco de dados.
     * É responsabilidade do chamador fechar esta conexão após o uso.
     *
     * @return Uma nova instância de Connection.
     * @throws SQLException Se ocorrer um erro de conexão com o banco de dados.
     */
    public static Connection getConexao() throws SQLException {
        logger.log(Level.INFO, "Tentando obter conexão com o banco de dados: " + DB_URL);
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter conexão com o banco de dados. URL: " + DB_URL + ", Usuário: " + DB_USER, e);
            throw e; // Relança a exceção para que o chamador possa tratá-la
        }
    }

    // --- MÉTODOS FACTORY ---
    // Cada método factory cria uma nova DAO e injeta uma nova conexão.
    // Lembre-se que cada conexão deve ser fechada individualmente.

    /**
     * Cria e retorna uma nova instância de ProdutoDAOImpl.
     *
     * @return Uma instância de ProdutoDAO.
     * @throws SQLException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static ProdutoDAO criarProdutoDAO() throws SQLException {
        return new ProdutoDAOImpl(getConexao());
    }

    /**
     * Cria e retorna uma nova instância de FornecedorDAOImpl.
     *
     * @return Uma instância de FornecedorDAO.
     * @throws SQLException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static FornecedorDAO criarFornecedorDAO() throws SQLException {
        return new FornecedorDAOImpl(getConexao());
    }

    /**
     * Cria e retorna uma nova instância de ItemPedidoDAOImpl.
     *
     * @return Uma instância de ItemPedidoDAO.
     * @throws SQLException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static ItemPedidoDAO criarItemPedidoDAO() throws SQLException {
        return new ItemPedidoDAOImpl(getConexao()) {
            @Override
            public ItemPedidoDTO salvar(ItemPedidoDTO itemPedido, Long pedidoId, ProdutoDAOImpl produtoDAO) throws SQLException {
                return null;
            }
        };
    }

    /**
     * Cria e retorna uma nova instância de PedidoDAOImpl.
     * Assumindo que PedidoDAOImpl requer uma Connection e, possivelmente, outras DAOs (ProdutoDAO, ItemPedidoDAO)
     * para sua construção, estes são passados no construtor.
     * É crucial que o construtor de PedidoDAOImpl esteja preparado para recebê-los.
     *
     * @return Uma instância de PedidoDAO.
     * @throws SQLException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static PedidoDAO criarPedidoDAO() throws SQLException {
        Connection conexao = getConexao(); // Obtém uma única conexão para este PedidoDAO e suas DAOs dependentes
        // As DAOs dependentes devem compartilhar a mesma conexão para garantir transações consistentes
        // se a lógica de negócio envolver múltiplas operações de banco de dados na mesma transação.
        ProdutoDAO produtoDAO = new ProdutoDAOImpl(conexao); // Passa a conexão para a ProdutoDAO
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(conexao) {
            @Override
            public ItemPedidoDTO salvar(ItemPedidoDTO itemPedido, Long pedidoId, ProdutoDAOImpl produtoDAO) throws SQLException {
                return null;
            }
        }; // Passa a conexão para a ItemPedidoDAO

        // ATENÇÃO: Você precisa ajustar o construtor de PedidoDAOImpl para aceitar
        // Connection, ProdutoDAO e ItemPedidoDAO como parâmetros, conforme a necessidade real.
        // O exemplo abaixo assume um construtor PedidoDAOImpl(Connection conexao, ProdutoDAO produtoDAO, ItemPedidoDAO itemPedidoDAO)
        return new PedidoDAOImpl(conexao, produtoDAO, itemPedidoDAO);
    }

    public static InstrumentoDAO criarInstrumentoDAO() throws SQLException {
        Connection conexao = getConexao(); // Obtém uma única conexão para este PedidoDAO e suas DAOs dependentes
        // As DAOs dependentes devem compartilhar a mesma conexão para garantir transações consistentes
        // se a lógica de negócio envolver múltiplas operações de banco de dados na mesma transação.
        InstrumentoDAO instrumentoDAO = new InstrumentoDAOImpl(conexao); // Passa a conexão para a ProdutoDAO; // Passa a conexão para a ItemPedidoDAO

        // ATENÇÃO: Você precisa ajustar o construtor de PedidoDAOImpl para aceitar
        // Connection, ProdutoDAO e ItemPedidoDAO como parâmetros, conforme a necessidade real.
        // O exemplo abaixo assume um construtor PedidoDAOImpl(Connection conexao, ProdutoDAO produtoDAO, ItemPedidoDAO itemPedidoDAO)
        return new InstrumentoDAOImpl(conexao);
    }
}
