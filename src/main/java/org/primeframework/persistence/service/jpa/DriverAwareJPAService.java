/*
 * Copyright (c) 2001-2010, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.primeframework.persistence.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.io.Closeable;

import org.primeframework.persistence.txn.TransactionContext;
import org.primeframework.persistence.txn.TransactionContextManager;
import org.primeframework.persistence.txn.jpa.JPATransactionalResource;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class implements the JPA service. It is a singleton and in the constructor it sets up the EntityManagerFactory.
 * It determines the Hibernate dialect based on the Connection class retrieved from the DataSource in the JNDI tree.
 * This is nice for projects that might need to support multiple databases and don't want to force users to tweak
 * configuration for their specific database.
 * <p/>
 * Currently, this only supports MySQL and PostgreSQL.
 * <p/>
 * This class is a singleton since it constructs the EntityManagerFactory in the constructor and holds a reference to
 * it.
 *
 * @author Brian Pontarelli
 */
@Singleton
public class DriverAwareJPAService implements JPAService, Closeable {
  private final TransactionContextManager txnContextManager;
  private final EntityManagerFactory entityManagerFactory;

  @Inject
  protected DriverAwareJPAService(TransactionContextManager txnContextManager, EntityManagerFactory entityManagerFactory) {
    this.txnContextManager = txnContextManager;
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityManagerFactory getFactory() {
    return entityManagerFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityManager setupEntityManager() {
    EntityManager em = EntityManagerContext.get();
    if (em != null) {
      return em;
    }

    try {
      if (entityManagerFactory == null) {
        return null;
      }

      em = entityManagerFactory.createEntityManager();
      EntityManagerContext.set(em);

      TransactionContext txnContext = txnContextManager.getCurrent();
      if (txnContext != null) {
        txnContext.add(new JPATransactionalResource(em));
      }

      return em;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void tearDownEntityManager() {
    // Clear out the context just to be safe.
    EntityManager entityManager = EntityManagerContext.get();
    if (entityManager != null) {
      EntityManagerContext.remove();
      entityManager.close();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }
}
