package com.roncolatoandpedro.soulinstruments.dto;

public class ItemPedidoDTO {
    private Long idItemPedido;
    private int quantidade;
    private Double valorUnitario; // Preço do produto no momento da compra
    private Double valorTotal;    // Calculado: quantidade * valorUnitario
    private Long idProduto;
    private Long idPedido;

    public ItemPedidoDTO() {}

    public ItemPedidoDTO(Long idProduto, int quantidade) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
    }

    public void calcularValorTotal() {
        if (this.valorUnitario != null && this.quantidade > 0) {
            this.valorTotal = this.quantidade * this.valorUnitario;
        } else {
            this.valorTotal = 0.0;
        }
    }

    public Long getIdItemPedido() {
        return idItemPedido;
    }

    public void setIdItemPedido(Long idItemPedido) {
        this.idItemPedido = idItemPedido;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }
}