package com.roncolatoandpedro.soulinstruments.dto;

import com.roncolatoandpedro.soulinstruments.model.Fornecedor;
import com.roncolatoandpedro.soulinstruments.model.Produto;

import java.util.ArrayList;

public class FornecedorDTO {
    private Long id;
    private String nomeFornecedor;
    private String cnpj;
    private String descricao;
    
    public FornecedorDTO() {}
    
    public FornecedorDTO(Long id, String nomeFornecedor, String cnpj, String descricao) {
        this.id = id;
        this.nomeFornecedor = nomeFornecedor;
        this.cnpj = cnpj;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
