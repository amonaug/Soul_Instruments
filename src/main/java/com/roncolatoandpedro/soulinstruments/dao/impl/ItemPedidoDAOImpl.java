package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider; // Importante para gerenciar o ciclo de vida do EntityManager
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import jakarta.persistence.EntityManager;


public class ItemPedidoDAOImpl implements ItemPedidoDAO {
    private final Provider<EntityManager> entityManagerProvider;
    @Inject
    public ItemPedidoDAOImpl(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    private EntityManager getEntityManager() {
        return entityManagerProvider.get(); // Obtém uma instância do EntityManager
    }
}
