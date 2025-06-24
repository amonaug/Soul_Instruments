package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProdutoDAOImpl implements ProdutoDAO {

    private static final Logger logger = Logger.getLogger(ProdutoDAOImpl.class.getName());

    private final Connection conexao;

    /**
     * Construtor para ProdutoDAOImpl. Recebe a conexão com o banco de dados.
     *
     * @param conexao A conexão com o banco de dados.
     * @throws IllegalArgumentException Se a conexão fornecida for nula.
     */
    public ProdutoDAOImpl(Connection conexao) {
        if (conexao == null) {
            throw new IllegalArgumentException("A conexão não pode ser nula.");
        }
        this.conexao = conexao;
        logger.log(Level.INFO, "ProdutoDAOImpl inicializado com sucesso.");
    }

    @Override
    public ProdutoDTO salvar(ProdutoDTO produto) throws SQLException {
        // SQL ajustado: idProduto geralmente é AUTO_INCREMENT, então não o inserimos.
        // Ele será gerado pelo banco e recuperado com Statement.RETURN_GENERATED_KEYS.
        // Assegure-se de que 'idInstrumento' e 'idFornecedor' correspondem às colunas FK.
        String sql = "INSERT INTO Produto (marca, modelo, descricao, preco, quantidadeEstoque, idInstrumento, idFornecedor) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        logger.log(Level.INFO, "Salvando produto: Modelo=" + produto.getModelo() + ", Marca=" + produto.getMarca());

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getMarca());
            stmt.setString(2, produto.getModelo());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            stmt.setLong(6, produto.getIdInstrumento());

            // Trata idFornecedor que pode ser nulo
            if (produto.getIdFornecedor() != null) {
                stmt.setLong(7, produto.getIdFornecedor());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT); // Usa BIGINT para tipos Long em SQL
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produto.setIdProduto(generatedKeys.getLong(1)); // Define o ID gerado para o produto
                        logger.log(Level.INFO, "Produto salvo com ID gerado: " + produto.getIdProduto());
                    } else {
                        throw new SQLException("Falha ao obter ID gerado para o produto, mas nenhuma linha afetada.");
                    }
                }
            } else {
                throw new SQLException("Falha ao salvar Produto, nenhuma linha afetada.");
            }
            return produto;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao salvar produto: " + e.getMessage(), e);
            throw e; // Relança a exceção
        }
    }

    @Override
    public void atualizar(ProdutoDTO produto) throws SQLException {
        // SQL corrigido: removida a vírgula extra no final e a duplicação do idFornecedor
        String sql = "UPDATE Produto SET marca = ?, modelo = ?, descricao = ?, preco = ?, " +
                "quantidadeEstoque = ?, idInstrumento = ?, idFornecedor = ? WHERE idProduto = ?";

        logger.log(Level.INFO, "Atualizando produto ID: " + produto.getIdProduto());

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, produto.getMarca());
            stmt.setString(2, produto.getModelo());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            stmt.setLong(6, produto.getIdInstrumento());

            // Trata idFornecedor que pode ser nulo
            if (produto.getIdFornecedor() != null) {
                stmt.setLong(7, produto.getIdFornecedor());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT);
            }
            stmt.setLong(8, produto.getIdProduto()); // ID para a cláusula WHERE

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.log(Level.WARNING, "Nenhum produto encontrado com ID " + produto.getIdProduto() + " para atualização.");
            } else {
                logger.log(Level.INFO, "Produto ID " + produto.getIdProduto() + " atualizado com sucesso.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao atualizar produto ID " + produto.getIdProduto() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void remover(Long idProduto) throws SQLException {
        String sql = "DELETE FROM Produto WHERE idProduto = ?";
        logger.log(Level.INFO, "Removendo produto ID: " + idProduto);

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idProduto);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.log(Level.WARNING, "Nenhum produto encontrado com ID " + idProduto + " para remoção.");
            } else {
                logger.log(Level.INFO, "Produto ID " + idProduto + " removido com sucesso.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao remover produto ID " + idProduto + ": " + e.getMessage(), e);
            throw e; // Relança a exceção original
        }
    }

    /**
     * Mapeia um ResultSet para um objeto ProdutoDTO.
     * Assume que o ResultSet contém as colunas "idProduto", "marca", "modelo", "descricao",
     * "preco", "quantidadeEstoque", "idInstrumento", "idFornecedor".
     *
     * @param resultSet O ResultSet contendo os dados do produto.
     * @return Um objeto ProdutoDTO preenchido.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private ProdutoDTO mapearResultSetParaProdutoDTO(ResultSet resultSet) throws SQLException {
        Long idProduto = resultSet.getLong("idProduto");
        String marca = resultSet.getString("marca");
        String modelo = resultSet.getString("modelo");
        String descricao = resultSet.getString("descricao");
        double preco = resultSet.getDouble("preco");
        int quantidadeEstoque = resultSet.getInt("quantidadeEstoque");
        Long idInstrumento = resultSet.getLong("idInstrumento");

        // Trata idFornecedor que pode ser nulo no banco de dados
        Long idFornecedor = resultSet.getLong("idFornecedor");
        if (resultSet.wasNull()) { // Verifica se o último valor lido era SQL NULL
            idFornecedor = null;
        }
        return new ProdutoDTO(idProduto, marca, modelo, descricao, preco, quantidadeEstoque, idInstrumento, idFornecedor);
    }

    @Override
    public Optional<ProdutoDTO> buscarPorId(Long idProduto) throws SQLException { // Retorno alterado para Optional
        String sql = "SELECT idProduto, marca, modelo, descricao, preco, quantidadeEstoque, idInstrumento, idFornecedor FROM Produto WHERE idProduto = ?";
        logger.log(Level.INFO, "Buscando produto por ID: " + idProduto);

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProdutoDTO produto = mapearResultSetParaProdutoDTO(rs);
                    logger.log(Level.INFO, "Produto com ID " + idProduto + " encontrado: " + produto.getModelo());
                    return Optional.of(produto);
                } else {
                    logger.log(Level.INFO, "Produto com ID " + idProduto + " não encontrado.");
                    return Optional.empty(); // Retorna Optional vazio se não encontrado
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao buscar produto por ID " + idProduto + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ProdutoDTO> listarTodos() throws SQLException {
        String sql = "SELECT idProduto, marca, modelo, descricao, preco, quantidadeEstoque, idInstrumento, idFornecedor FROM Produto ORDER BY idInstrumento";
        List<ProdutoDTO> produtos = new ArrayList<>();
        logger.log(Level.INFO, "Listando todos os produtos.");

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produtos.add(mapearResultSetParaProdutoDTO(rs));
            }
            logger.log(Level.INFO, "Encontrados " + produtos.size() + " produtos.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao listar todos os produtos: " + e.getMessage(), e);
            throw e;
        }
        return produtos;
    }

    @Override
    public List<ProdutoDTO> buscarPorNome(String nomeInstrumento) throws SQLException { // Renomeado parâmetro e adicionado throws SQLException
        List<ProdutoDTO> lista = new ArrayList<>();
        // SQL para buscar produtos pelo nome do instrumento associado (JOIN com Instrumento)
        // Usando LOWER para busca case-insensitive e LIKE para busca parcial
        String sql = "SELECT p.idProduto, p.marca, p.modelo, p.descricao, p.preco, p.quantidadeEstoque, p.idInstrumento, p.idFornecedor " +
                "FROM Produto p JOIN Instrumento i ON p.idInstrumento = i.idInstrumento " +
                "WHERE LOWER(i.nome) LIKE LOWER(?)";

        logger.log(Level.INFO, "Buscando produtos pelo nome do instrumento: " + nomeInstrumento);

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nomeInstrumento + "%"); // Adiciona curingas para busca parcial
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetParaProdutoDTO(rs)); // Usa o método mapeador
                }
            }
            logger.log(Level.INFO, "Encontrados " + lista.size() + " produtos com o nome do instrumento '" + nomeInstrumento + "'.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao buscar produtos por nome de instrumento: " + e.getMessage(), e);
            throw e; // Relança a exceção conforme a interface
        }
        return lista;
    }
}
