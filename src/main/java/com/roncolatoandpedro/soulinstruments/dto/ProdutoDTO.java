package com.roncolatoandpedro.soulinstruments.dto;

import com.roncolatoandpedro.soulinstruments.dto.Categoria;

public class ProdutoDTO extends InstrumentoDTO {
    private int codigoProduto;
    private String marca;
    private String modelo;
    private Double preco;
    private int quantidadeEstoque;
    private String cnpj;

    // Construtor padrão
    public ProdutoDTO(Long id, String nome, Categoria categoria, String marca, String modelo,
                      Double preco, int quantidadeEstoque, String cnpj
                      ) {
        super(id, nome, categoria);
        this.marca = marca;
        this.modelo = modelo;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.cnpj = cnpj;
    }

    // Construtor completo
    public ProdutoDTO(Long id, String nome, Categoria categoria, int codigoProduto, String marca, String modelo,
                      Double preco, int quantidadeEstoque, String cnpj) {
        super(id, nome, categoria); // Chama o construtor da superclasse InstrumentoDTO
        this.codigoProduto = codigoProduto;
        this.marca = marca;
        this.modelo = modelo;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.cnpj = cnpj;
    }

    // Getters e Setters para os campos específicos de ProdutoDTO
    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(int codigoProduto) {
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

}