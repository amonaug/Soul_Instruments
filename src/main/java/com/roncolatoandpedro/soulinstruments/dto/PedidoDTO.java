package com.roncolatoandpedro.soulinstruments.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {
    private Long idPedido;
    private LocalDate dataPedido;
    private Double valorTotal; //Pode ser calculado ou armazenado
    private String cnpj; //FK
    private List<ItemPedidoDTO> itens;

    public PedidoDTO() {}

    public PedidoDTO(Long idPedido, LocalDate dataPedido, Double valorTotal, String cnpj) {
        this.idPedido = idPedido;
        this.dataPedido = dataPedido;
        this.valorTotal = valorTotal;
        this.cnpj = cnpj;
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

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getFornecedorId() {
        return cnpj;
    }

    public void setFornecedorId(String fornecedorId) {
        this.cnpj = fornecedorId;
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
