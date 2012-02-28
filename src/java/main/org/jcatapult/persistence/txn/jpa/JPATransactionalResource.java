/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.txn.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.jcatapult.persistence.txn.TransactionException;
import org.jcatapult.persistence.txn.TransactionalResource;

/**
 * This is the JPA implementation of the transaction state.
 *
 * @author Brian Pontarelli
 */
public class JPATransactionalResource implements TransactionalResource<EntityManager, PersistenceException> {
  private final EntityManager em;

  public JPATransactionalResource(EntityManager em) {
    this.em = em;
  }

  /**
   * @return The EntityManager.
   */
  @Override
  public EntityManager wrapped() {
    return em;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() throws PersistenceException {
    EntityTransaction et = em.getTransaction();
    if (et.isActive()) {
      throw new TransactionException("The JPA transaction has already been started and can't be started twice.");
    }

    et.begin();
  }

  /**
   * Calls the {@link EntityTransaction#commit()} method.
   */
  @Override
  public void commit() {
    em.getTransaction().commit();
  }

  /**
   * Calls the {@link EntityTransaction#rollback()} method.
   */
  @Override
  public void rollback() {
    em.getTransaction().rollback();
  }
}
