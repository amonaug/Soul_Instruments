package com.roncolatoandpedro.soulinstruments.dto;

import java.util.ArrayList;

public class FornecedorDTO {
    private String nomeFornecedor;
    private String cnpj;
    private String email;
    
    public FornecedorDTO(String nomeFornecedor, String cnpj, String email) {
        this.nomeFornecedor = nomeFornecedor;
        this.cnpj = cnpj;
        this.email = email;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}
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

}
