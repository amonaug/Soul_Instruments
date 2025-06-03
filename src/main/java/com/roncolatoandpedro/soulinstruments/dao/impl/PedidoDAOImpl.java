package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import jakarta.persistence.EntityManager;


public class PedidoDAOImpl implements PedidoDAO {
    private final Provider<EntityManager> entityManagerProvider;
    @Inject
    public PedidoDAOImpl(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    private EntityManager getEntityManager() {
        return entityManagerProvider.get(); // Obtém uma instância do EntityManager
    }
}
