package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider; // Importante para gerenciar o ciclo de vida do EntityManager
import com.roncolatoandpedro.soulinstruments.dao.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.model.ItemPedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;


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
