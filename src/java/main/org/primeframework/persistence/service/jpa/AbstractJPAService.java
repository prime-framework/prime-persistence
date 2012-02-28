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

/**
 * This class is an abstract implementation of the JPA service. It provides the methods to handle the
 * EntityManagerFactory and the EntityManager instances. It leverages the {@link EntityManagerContext} as the
 * ThreadLocal store for EntityManagers as well as tearing down the EntityManager from this context at the end of
 * requests.
 *
 * @author Brian Pontarelli
 */
public abstract class AbstractJPAService implements JPAService, Closeable {
  private final TransactionContextManager txnContextManager;
  protected EntityManagerFactory emf;

  protected AbstractJPAService(TransactionContextManager txnContextManager) {
    this.txnContextManager = txnContextManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityManagerFactory getFactory() {
    return emf;
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
      if (emf == null) {
        return null;
      }

      em = emf.createEntityManager();
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
    if (emf != null) {
      emf.close();
    }
  }
}
