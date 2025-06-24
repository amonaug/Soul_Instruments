package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dao.impl.ProdutoDAOImpl; // Importe a implementação da DAO do Produto
// Assumindo que você tem uma interface ProdutoDAO, preferir importá-la:
// import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO; // Se você tiver esta interface

import java.sql.SQLException;
import java.util.List;

/*
 Essa interface define o contrato para operações de acesso a dados de Itens de Pedido.
 O comentário "Esse IMPL é como se fosse o carrinho de supermercado com os itens
 por isso tem uma lista de itemPedido na linha 15" parece se referir à implementação
 ou a um DTO de Pedido, e não diretamente a esta interface DAO.

 No método salvar, a necessidade de ProdutoDAO para buscar o preço do produto
 indica uma dependência de lógica de negócio (cálculo de valor) no DAO.
 Uma prática comum é que o cálculo de valor unitário e total do itemPedidoDTO
 seja feito na camada de serviço (business logic) antes que o itemPedidoDTO
 seja passado para o DAO para persistência. Isso mantém o DAO focado apenas
 nas operações de persistência e evita que ele precise de outras DAOs.
 No entanto, mantive o parâmetro ProdutoDAO conforme sua intenção atual.
*/
public interface ItemPedidoDAO {
    /**
     * Salva um ItemPedido no banco de dados, associando-o a um Pedido existente.
     * O ProdutoDAO é passado para permitir a busca de informações do produto (como preço)
     * para calcular o valor do item antes de salvar.
     *
     * @param itemPedido O objeto ItemPedidoDTO a ser salvo.
     * @param pedidoId O ID do Pedido ao qual este item pertence.
     * @param produtoDAO A instância de ProdutoDAO para buscar detalhes do produto.
     * @return O ItemPedidoDTO salvo, possivelmente com um ID gerado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    ItemPedidoDTO salvar(ItemPedidoDTO itemPedido, Long pedidoId, ProdutoDAOImpl produtoDAO) throws SQLException;
    // Alterei ProdutoDAO para ProdutoDAOImpl para corresponder ao seu uso em outros lugares,
    // mas o ideal em interfaces é usar a interface (e.g., ProdutoDAO se ela existir)

    ItemPedidoDTO salvar(ItemPedidoDTO itemPedido, Long idPedido, ProdutoDAO produtoDAO) throws SQLException;

    /**
     * Busca todos os itens de pedido associados a um determinado ID de Pedido.
     *
     * @param idPedido O ID do Pedido para o qual buscar os itens.
     * @return Uma lista de ItemPedidoDTO. Retorna uma lista vazia se nenhum item for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ItemPedidoDTO> buscarPorPedidoId(Long idPedido) throws SQLException;
}
