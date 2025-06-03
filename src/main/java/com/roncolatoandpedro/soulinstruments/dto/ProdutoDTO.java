package com.roncolatoandpedro.soulinstruments.dto;

import com.roncolatoandpedro.soulinstruments.dto.Categoria;

public class ProdutoDTO extends InstrumentoDTO {
    private String codigoProduto;
    private String marca;
    private String modelo;
    private String descricao;
    private Double preco;
    private int quantidadeEstoque;
    private Long fornecedorId;

    // Construtor padrão
    public ProdutoDTO() {
        super();
    }

    // Construtor completo
    public ProdutoDTO(Long id, String nome, Categoria categoria, String codigoProduto, String marca, String modelo,
                      String descricao, Double preco, int quantidadeEstoque, Long fornecedorId) {
        super(id, nome, categoria); // Chama o construtor da superclasse InstrumentoDTO
        this.codigoProduto = codigoProduto;
        this.marca = marca;
        this.modelo = modelo;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.fornecedorId = fornecedorId;
    }

    // Getters e Setters para os campos específicos de ProdutoDTO
    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Long getFornecedorId() {
        return fornecedorId;
    }

    public void setFornecedorId(Long fornecedorId) {
        this.fornecedorId = fornecedorId;
    }
}