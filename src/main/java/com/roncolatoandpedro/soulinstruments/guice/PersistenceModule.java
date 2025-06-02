package com.roncolatoandpedro.soulinstruments.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersistenceModule extends AbstractModule {

    private static final String PERSISTENCE_UNIT_NAME = "SoulInstrumentsPU"; // Mesmo nome do persistence.xml
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<>();

    @Override
    protected void configure() {
        // Área destinada a bindings específicos de persistência aqui se necessário,
        // mas o principal é o @Provides para EntityManagerFactory e EntityManager.
    }

    @Provides
    @Singleton // Garante que haverá apenas uma instância do EntityManagerFactory
    public EntityManagerFactory provideEntityManagerFactory() {
        // Cria a fábrica de EntityManagers com base no persistence.xml
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    @Provides
    public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        // Este é um exemplo simples. Para aplicações mais complexas, especialmente web,
        // o gerenciamento do ciclo de vida do EntityManager é mais elaborado.
        // Para Swing, você pode criar um novo EntityManager por operação/transação no DAO.
        // Uma abordagem comum é o DAO solicitar um novo EntityManager da Factory.
        //
        // A implementação abaixo é um exemplo básico e pode precisar de ajustes
        // dependendo de como você gerenciará as transações.
        // Para DAOs que injetam Provider<EntityManager>, eles obterão uma nova instância quando chamarem .get()
        // e deverão fechá-la.
        return entityManagerFactory.createEntityManager();
    }
}