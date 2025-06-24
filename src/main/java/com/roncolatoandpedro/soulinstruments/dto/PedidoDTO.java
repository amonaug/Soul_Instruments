package com.roncolatoandpedro.soulinstruments.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections; // Para Collections.unmodifiableList
import java.util.List;

public class PedidoDTO {
    private Long idPedido;
    private LocalDate dataPedido; // Preferível usar java.time.LocalDate para datas
    private Double valorTotal;
    private Long idFornecedor; // O id do fornecedor é do pedido, não de um item
    private List<ItemPedidoDTO> itens;

    public PedidoDTO() {
        this.itens = new ArrayList<>();
        this.valorTotal = 0.0;
        this.dataPedido = LocalDate.now(); // Define a data atual por padrão
    }

    // Se você tiver um construtor que recebe data, use-o
    public PedidoDTO(LocalDate dataPedido, Long idFornecedor) {
        this(); // Chama o construtor padrão para inicializar itens e valorTotal
        this.dataPedido = dataPedido;
        this.idFornecedor = idFornecedor;
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
    }

    // Getters e Setters
    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) { // Adicionado setter para dataPedido
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

    // O getter de itens deve retornar uma lista imutável para evitar modificações externas diretas
    public List<ItemPedidoDTO> getItens() {
        return Collections.unmodifiableList(itens); // Retorna uma visão imutável da lista
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        // Cria uma ArrayList para evitar que a lista passada externamente seja a mesma referência
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
