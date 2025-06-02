package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider; // Importante para gerenciar o ciclo de vida do EntityManager
import com.roncolatoandpedro.soulinstruments.dao.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.model.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class ProdutoDAOImpl implements ProdutoDAO {

    private final Provider<EntityManager> entityManagerProvider;

    @Inject
    public ProdutoDAOImpl(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    private EntityManager getEntityManager() {
        return entityManagerProvider.get(); // Obtém uma instância do EntityManager
    }

    @Override
    public void salvar(Produto produto) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(produto);
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
    public void atualizar(Produto produto) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(produto); // Use merge para atualizar entidades existentes
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
            Produto produto = em.find(Produto.class, id);
            if (produto != null) {
                em.remove(produto);
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
    public Optional<Produto> buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(Produto.class, id));
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Produto> buscarPorCodigoProduto(String codigoProduto) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Produto> query = em.createQuery(
                    "SELECT p FROM Produto p WHERE p.codigoProduto = :codigo", Produto.class);
            query.setParameter("codigo", codigoProduto);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Produto> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p", Produto.class);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}