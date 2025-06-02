package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.roncolatoandpedro.soulinstruments.dao.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.model.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;


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
