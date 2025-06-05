package com.roncolatoandpedro.soulinstruments.dto;

public class ItemPedidoDTO {
    private Long id;
    private Long idPedido; //FK
    private Long idProduto; //FK
    private int quantidade;
    private double precoUnitarioCompra; //FK
    private double valorTotalItem;    // Calculado: quantidade * precoUnitarioCompra


    public ItemPedidoDTO() {}
    // Construtor para quando estamos criando um novo item (sem ID do item ainda, preço será buscado)
    public ItemPedidoDTO(Long idProduto, int quantidade) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        // precoUnitarioCompra e valorTotalItem serão definidos após buscar o preço do produto
    }
    public ItemPedidoDTO(Long id, Long idPedido, Long idProduto, int quantidade, double precoUnitarioCompra) {
        this.id = id;
        this.idPedido = idPedido;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.precoUnitarioCompra = precoUnitarioCompra;
        this.calcularValorTotalItem();
    }

    // Método para calcular o valor total do item
    public void calcularValorTotalItem() {
        this.valorTotalItem = this.quantidade * this.precoUnitarioCompra;
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
        // Recalcula se a quantidade mudar e o preço já estiver definido
        if (this.precoUnitarioCompra > 0) {
            calcularValorTotalItem();
        }
    }

    public double getPrecoUnitarioCompra() {
        return precoUnitarioCompra;
    }

    public void setPrecoUnitarioCompra(double precoUnitarioCompra) {
        this.precoUnitarioCompra = precoUnitarioCompra;
        // Recalcula se o preço mudar e a quantidade já estiver definida
        if (this.quantidade > 0) {
            calcularValorTotalItem();
        }
    }

    public double getValorTotalItem() {
        return valorTotalItem;
    }

    public void setValorTotalItem(double valorTotalItem) {
        this.valorTotalItem = valorTotalItem;
    }
}
