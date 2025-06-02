package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider; // Importante para gerenciar o ciclo de vida do EntityManager
import com.roncolatoandpedro.soulinstruments.dao.FornecedorDAO;
import com.roncolatoandpedro.soulinstruments.model.Fornecedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;


public class FornecedorDAOImpl implements FornecedorDAO {

    private final Provider<EntityManager> entityManagerProvider;
    @Inject
    public FornecedorDAOImplImpl(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    private EntityManager getEntityManager() {
        return entityManagerProvider.get(); // Obtém uma instância do EntityManager
    }

    @Override
    public void salvar(Fornecedor fornecedor) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(fornecedor);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e; // Re-lança a exceção para ser tratada pela camada superior
        } finally {
            if (em != null) {
                em.close(); // Sempre feche o EntityManager obtido do Provider
            }
        }
    }

    @Override
    public void atualizar(Fornecedor fornecedor) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(fornecedor); // Use merge para atualizar entidades existentes
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void atualizar(Fornecedor fornecedor) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(fornecedor); // Use merge para atualizar entidades existentes
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void remover(Long id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Fornecedor fornecedor = em.find(fornecedor.class, id);
            if (fornecedor != null) {
                em.remove(fornecedor);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Fornecedor> buscarPorCnpj(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(Fornecedor.class, cnpj));
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }




}
