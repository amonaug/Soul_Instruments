package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;

import java.sql.SQLException;
import java.util.Date; // Alterado para java.util.Date para consistência com PedidoDTO
import java.util.List;
import java.util.Optional;

public interface PedidoDAO {
    /**
     * Salva um novo Pedido no banco de dados.
     *
     * @param pedido O objeto PedidoDTO a ser salvo.
     * @return O PedidoDTO salvo, incluindo o ID gerado pelo banco de dados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    PedidoDTO salvar(PedidoDTO pedido) throws SQLException;

    /**
     * Atualiza o status de um Pedido existente no banco de dados.
     * Presume que o PedidoDTO passado já possui o ID e o novo status.
     *
     * @param pedido O PedidoDTO contendo o ID do pedido a ser atualizado e o novo status.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    void atualizarStatus(PedidoDTO pedido) throws SQLException;

    /**
     * Remove um Pedido do banco de dados pelo seu ID.
     *
     * @param idPedido O ID do pedido a ser removido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    // Alterado para remover todo o pedido pelo ID. Se a intenção era remover um item específico,
    // essa lógica deve estar em ItemPedidoDAO ou em uma camada de serviço.
    void remover(Long idPedido) throws SQLException;

    /**
     * Busca um Pedido no banco de dados pelo seu ID.
     *
     * @param idPedido O ID do pedido a ser buscado.
     * @return Um Optional contendo o PedidoDTO se encontrado, ou um Optional vazio caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<PedidoDTO> buscarPorId(Long idPedido) throws SQLException;

    /**
     * Lista todos os Pedidos existentes no banco de dados.
     *
     * @return Uma lista de PedidoDTO. Retorna uma lista vazia se nenhum pedido for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<PedidoDTO> listarTodos() throws SQLException;

    /**
     * Lista Pedidos em um determinado período de datas.
     * Alterado para usar java.util.Date para consistência com PedidoDTO.
     *
     * @param dataInicio A data de início do período (inclusiva).
     * @param dataFim A data de fim do período (inclusiva).
     * @return Uma lista de PedidoDTO que caem no período especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<PedidoDTO> listarPorPeriodo(Date dataInicio, Date dataFim) throws SQLException;

    /**
     * Lista Pedidos associados a um fornecedor específico.
     *
     * @param idFornecedor O ID do fornecedor.
     * @return Uma lista de PedidoDTO.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<PedidoDTO> listarPorFornecedor(Long idFornecedor) throws SQLException;

    /**
     * Lista pedidos por nome de cliente (filtro parcial).
     *
     * @param nomeCliente O nome do cliente para filtrar os pedidos.
     * @return Uma lista de PedidoDTO.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    // Adicionado método para buscar pedidos por nome de cliente, útil para a UI de consulta.
    List<PedidoDTO> buscarPorNomeCliente(String nomeCliente) throws SQLException;

    /**
     * Lista pedidos por status.
     *
     * @param status O status do pedido para filtrar.
     * @return Uma lista de PedidoDTO.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    // Adicionado método para buscar pedidos por status, útil para a UI de consulta.
    List<PedidoDTO> buscarPorStatus(String status) throws SQLException;
}
