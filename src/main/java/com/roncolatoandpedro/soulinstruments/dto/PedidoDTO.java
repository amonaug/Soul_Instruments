package com.roncolatoandpedro.soulinstruments.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {
    private Long idPedido;
    private LocalDate dataPedido;
    private LocalDate dataEntrega;
    private Double valorTotal; // Será calculado a partir dos itens
    private Long fornecedorId;
    private List<ItemPedidoDTO> itens;

    public PedidoDTO() {
        this.itens = new ArrayList<>();
        this.valorTotal = 0.0; // Inicializa
    }

    public PedidoDTO(Long idPedido, LocalDate dataPedido, LocalDate dataEntrega, Long fornecedorId) {
        this.idPedido = idPedido;
        this.dataPedido = dataPedido;
        this.dataEntrega = dataEntrega;
        this.fornecedorId = fornecedorId;
        this.itens = new ArrayList<>();
        this.valorTotal = 0.0; // Inicializa
    }

    // Método para calcular/atualizar o valor total do pedido
    public void calcularValorTotalPedido() {
        this.valorTotal = 0.0;
        if (this.itens != null) {
            for (ItemPedidoDTO item : this.itens) {
                this.valorTotal += item.getValorTotalItem();
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

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Double getValorTotal() {
        // Garante que o valorTotal está atualizado antes de retornar,
        // ou chame calcularValorTotalPedido() explicitamente antes de salvar/exibir.
        // calcularValorTotalPedido(); // Descomente se quiser que seja sempre recalculado no get
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        // Geralmente, o valor total é calculado, mas um setter pode ser útil
        // se você carregar um valor total pré-calculado do banco.
        this.valorTotal = valorTotal;
    }

    public Long getFornecedorId() {
        return fornecedorId;
    }

    public void setFornecedorId(Long fornecedorId) {
        this.fornecedorId = fornecedorId;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
        calcularValorTotalPedido(); // Recalcula quando a lista de itens é definida
    }

    public void addItem(ItemPedidoDTO item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        this.itens.add(item);
        calcularValorTotalPedido(); // Recalcula ao adicionar um item
    }

    public void removerItem(ItemPedidoDTO item) {
        if (this.itens != null) {
            this.itens.remove(item);
            calcularValorTotalPedido(); // Recalcula ao remover um item
        }
    }
}