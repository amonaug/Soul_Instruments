package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO; // Sua entidade Produto
// Removida importação de Categoria, pois não é diretamente usada na interface ProdutoDAO
// import com.roncolatoandpedro.soulinstruments.dto.Categoria;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProdutoDAO {
    /**
     * Salva um novo Produto no banco de dados.
     * Retorna o DTO com o ID gerado após a persistência, caso seja gerado pelo banco.
     *
     * @param produto O objeto ProdutoDTO a ser salvo.
     * @return O ProdutoDTO salvo, incluindo o ID gerado pelo banco de dados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    ProdutoDTO salvar(ProdutoDTO produto) throws SQLException;

    /**
     * Atualiza um Produto existente no banco de dados.
     *
     * @param produto O objeto ProdutoDTO contendo os dados atualizados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    void atualizar(ProdutoDTO produto) throws SQLException;

    /**
     * Remove um Produto do banco de dados pelo seu ID.
     *
     * @param idProduto O ID do produto a ser removido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    void remover(Long idProduto) throws SQLException;

    /**
     * Busca um Produto no banco de dados pelo seu ID.
     *
     * @param idProduto O ID do produto a ser buscado.
     * @return Um Optional contendo o ProdutoDTO se encontrado, ou um Optional vazio caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    // Alterado para retornar Optional<ProdutoDTO> para melhor tratamento de casos onde o ID não é encontrado
    Optional<ProdutoDTO> buscarPorId(Long idProduto) throws SQLException;

    /**
     * Lista todos os Produtos existentes no banco de dados.
     *
     * @return Uma lista de ProdutoDTO. Retorna uma lista vazia se nenhum produto for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ProdutoDTO> listarTodos() throws SQLException;

    /**
     * Busca produtos por uma parte do nome (ou nome completo) do instrumento associado.
     * O filtro deve ser aplicado na implementação, geralmente combinando com a tabela de Instrumento.
     *
     * @param nome O nome ou parte do nome do instrumento para buscar.
     * @return Uma lista de ProdutoDTO que correspondem ao nome do instrumento.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    // Adicionado 'throws SQLException' para consistência, e Javadoc.
    List<ProdutoDTO> buscarPorNome(String nome) throws SQLException;
}
