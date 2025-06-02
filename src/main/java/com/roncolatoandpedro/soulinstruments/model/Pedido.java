package com.roncolatoandpedro.soulinstruments.model;

import java.io.Serial;
import java.util.ArrayList
import java.util.Date;

public class Pedido {
    public Serial idPedido;
    public ArrayList<ItemPedido> itensProduto;
    public Date dataPedido;
    public Date dataEntrega;
    public Double ValorTotal;
    public String cnpjFornecedor;
}
