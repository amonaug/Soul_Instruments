package com.roncolatoandpedro.soulinstruments.guice;

import com.google.inject.AbstractModule;
import com.roncolatoandpedro.soulinstruments.dao.ProdutoDAO; // Supondo que você tenha ProdutoDAO.java
// Importe outras interfaces DAO e suas implementações aqui

// Crie esta classe de implementação
import com.roncolatoandpedro.soulinstruments.dao.impl.ProdutoDAOImpl;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        // Instala outros módulos que este módulo depende
        install(new PersistenceModule());

        // Define os bindings para suas interfaces DAO e implementações
        // Exemplo para ProdutoDAO:
        bind(ProdutoDAO.class).to(ProdutoDAOImpl.class);

        // Adicione bindings para outros DAOs aqui:
        // bind(FornecedorDAO.class).to(FornecedorDAOImpl.class);
        // bind(PedidoDAO.class).to(PedidoDAOImpl.class);
        // bind(ItemPedidoDAO.class).to(ItemPedidoDAOImpl.class);

        // Se você tiver classes de Presenter/Controller para a UI que quer que o Guice gerencie:
        // bind(ProdutoPresenter.class); // Se não tiver interface
    }
}