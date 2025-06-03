package com.roncolatoandpedro.soulinstruments.dto;

public class ItemPedidoDTO {
    private Long id;
    private Long idPedido; //FK
    private Long idProduto; //FK
    private int quantidade;
    private double precoUnitarioCompra;

    public ItemPedidoDTO() {}
    public ItemPedidoDTO(Long id, Long idPedido, Long idProduto, int quantidade, double precoUnitarioCompra) {
        this.id = id;
        this.idPedido = idPedido;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.precoUnitarioCompra = precoUnitarioCompra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
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

    public double getPrecoUnitarioCompra() {
        return precoUnitarioCompra;
    }

    public void setPrecoUnitarioCompra(double precoUnitarioCompra) {
        this.precoUnitarioCompra = precoUnitarioCompra;
    }
}
