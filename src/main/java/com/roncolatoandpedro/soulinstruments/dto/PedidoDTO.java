package com.roncolatoandpedro.soulinstruments.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {
    private Long id; // compatível com o banco
    private LocalDate dataPedido;
    private Double valorPedido; // nome igual ao banco

    private List<ItemPedidoDTO> itens; // ainda útil se você está montando objetos compostos no back

    public PedidoDTO(Double valorPedido) {
        this.valorPedido = valorPedido;
        this.dataPedido = LocalDate.now(); // se quiser definir automaticamente no Java também
        this.itens = new ArrayList<>();
    }

    public PedidoDTO(Long id, LocalDate dataPedido, Double valorPedido) {
        this.id = id;
        this.dataPedido = dataPedido;
        this.valorPedido = valorPedido;
        this.itens = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }

    public Double getValorPedido() {
        return valorPedido;
    }

    public void setValorPedido(Double valorPedido) {
        this.valorPedido = valorPedido;
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
