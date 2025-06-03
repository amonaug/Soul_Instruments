package com.roncolatoandpedro.soulinstruments.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {
    private Long idPedido;
    private LocalDate dataPedido;
    private LocalDate dataEntrega;
    private Double valorTotal; //Pode ser calculado ou armazenado
    private Long fornecedorId; //FK
    private List<ItemPedidoDTO> itens;

    public PedidoDTO() {}

    public PedidoDTO(Long idPedido, LocalDate dataPedido, LocalDate dataEntrega, Double valorTotal, Long fornecedorId) {
        this.idPedido = idPedido;
        this.dataPedido = dataPedido;
        this.dataEntrega = dataEntrega;
        this.valorTotal = valorTotal;
        this.fornecedorId = fornecedorId;
        this.itens = new ArrayList<>();
    }

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
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
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
    }

    public void addItem(ItemPedidoDTO item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        this.itens.add(item);
    }
}
