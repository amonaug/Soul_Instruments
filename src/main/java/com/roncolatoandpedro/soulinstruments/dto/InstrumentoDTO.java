package com.roncolatoandpedro.soulinstruments.dto;

import com.roncolatoandpedro.soulinstruments.dto.Categoria;

public class InstrumentoDTO {
    private Long id;
    private String nome;
    private Categoria categoria;

    public InstrumentoDTO() {
    }

    public InstrumentoDTO(Long id, String nome, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}