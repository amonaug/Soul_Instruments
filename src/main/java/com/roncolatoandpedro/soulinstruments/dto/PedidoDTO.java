package com.roncolatoandpedro.soulinstruments.dto;

import java.util.ArrayList;
import java.util.Collections; // Para Collections.unmodifiableList
import java.util.List;
import java.util.Date; // Alterado para java.util.Date para compatibilidade com java.sql.Date e JDBC

public class PedidoDTO {
    private Long idPedido;
    private Date dataPedido; // Alterado para java.util.Date para compatibilidade com a DAO
    private Double valorTotal;
    private Long idFornecedor;
    private String nomeCliente; // ADICIONADO: Necessário para PedidoDAOImpl e UI
    private String status;      // ADICIONADO: Necessário para PedidoDAOImpl e UI
    private Long idProduto;     // ADICIONADO: Para compatibilidade com cadastrarPedido.java (UI) - nota: idealmente, pertence apenas ao ItemPedidoDTO
    private int quantidade;     // ADICIONADO: Para compatibilidade com cadastrarPedido.java (UI) - nota: idealmente, pertence apenas ao ItemPedidoDTO
    private List<ItemPedidoDTO> itens;

    public PedidoDTO() {
        this.itens = new ArrayList<>();
        this.valorTotal = 0.0;
        this.dataPedido = new Date(); // Define a data atual por padrão (java.util.Date)
        this.status = "PENDENTE"; // Status inicial padrão para novos pedidos
    }

    // Construtor completo ajustado para incluir os novos campos
    public PedidoDTO(Long idPedido, Date dataPedido, Double valorTotal, Long idFornecedor, String nomeCliente, String status, Long idProduto, int quantidade) {
        this(); // Chama o construtor padrão para inicializar itens e outros defaults
        this.idPedido = idPedido;
        this.dataPedido = dataPedido;
        this.valorTotal = valorTotal;
        this.idFornecedor = idFornecedor;
        this.nomeCliente = nomeCliente;
        this.status = status;
        this.idProduto = idProduto; // Usado por algumas UIs de cadastro simplificado
        this.quantidade = quantidade; // Usado por algumas UIs de cadastro simplificado
    }

    public void calcularValorTotalPedido() {
        this.valorTotal = 0.0; // Reinicia o cálculo
        if (this.itens != null) {
            for (ItemPedidoDTO item : this.itens) {
                // Garante que o total do item esteja calculado antes de somar
                item.calcularValorTotal();
                this.valorTotal += item.getValorTotal();
            }
        }
        // Se PedidoDTO também armazena idProduto e quantidade diretamente (como em cadastrarPedido.java),
        // e se não há itens específicos, você pode querer adicionar uma lógica para calcular o valor total
        // baseado nesses campos aqui também, se eles representam um "item principal" do pedido.
        // Ex: if (this.idProduto != null && this.quantidade > 0 && this.valorTotal == 0.0) { ... }
    }

    // Getters e Setters
    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Date getDataPedido() { // Retorna java.util.Date
        return dataPedido;
    }

    public void setDataPedido(Date dataPedido) { // Aceita java.util.Date
        this.dataPedido = dataPedido;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // O getter de itens deve retornar uma lista imutável para evitar modificações externas diretas
    public List<ItemPedidoDTO> getItens() {
        return Collections.unmodifiableList(itens); // Retorna uma visão imutável da lista
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        // Cria uma nova ArrayList para evitar que a lista passada externamente seja a mesma referência
        this.itens = new ArrayList<>(itens);
        calcularValorTotalPedido(); // Recalcula o total ao definir uma nova lista de itens
    }

    public void addItem(ItemPedidoDTO item) {
        if (item != null) {
            this.itens.add(item);
            calcularValorTotalPedido(); // Recalcula o total ao adicionar um item
        }
    }

    public void removeItem(ItemPedidoDTO item) {
        if (item != null && this.itens.remove(item)) {
            calcularValorTotalPedido(); // Recalcula o total ao remover um item
        }
    }

    // Adicionado um método para limpar todos os itens
    public void clearItens() {
        this.itens.clear();
        calcularValorTotalPedido(); // Recalcula o total
    }
}
