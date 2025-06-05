package com.roncolatoandpedro.soulinstruments.dto;

public class ProdutoDTO extends InstrumentoDTO {
    private Long id; // id da tabela PRODUTO (SERIAL)
    private String marca;
    private String modelo;
    private Double preco;
    private int quantidade;
    private String cnpj; // fornecedor_cnpj

    // Construtor simples
    public ProdutoDTO(Long instrumentoId, String nome, Categoria categoria, String marca,
                      String modelo, Double preco, int quantidade, String cnpj) {
        super(instrumentoId, nome, categoria); // herda dados do Instrumento
        this.marca = marca;
        this.modelo = modelo;
        this.preco = preco;
        this.quantidade = quantidade;
        this.cnpj = cnpj;
    }

    // Construtor completo (com id do produto)
    public ProdutoDTO(Long id, Long instrumentoId, String nome, Categoria categoria, String marca,
                      String modelo, Double preco, int quantidade, String cnpj) {
        super(instrumentoId, nome, categoria);
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.preco = preco;
        this.quantidade = quantidade;
        this.cnpj = cnpj;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Long getInstrumentoId() {
        return super.getId();
    }

}
