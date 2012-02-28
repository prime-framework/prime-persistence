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
package org.jcatapult.persistence.txn;

import javax.persistence.EntityManager;
import java.sql.Connection;

import org.jcatapult.persistence.service.jdbc.ConnectionContext;
import org.jcatapult.persistence.service.jpa.EntityManagerContext;
import org.jcatapult.persistence.txn.jdbc.JDBCTransactionalResource;
import org.jcatapult.persistence.txn.jpa.JPATransactionalResource;

import com.google.inject.Singleton;

/**
 * This class is the default transaction context manager. It uses a ThreadLocal to store the current transaction
 * context.
 *
 * @author Brian Pontarelli
 */
@Singleton
public class DefaultTransactionContextManager implements TransactionContextManager {
  private final ThreadLocal<TransactionContext> holder = new ThreadLocal<TransactionContext>();

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionContext start() throws Exception {
    TransactionContext txnContext = holder.get();
    if (txnContext != null) {
      throw new TransactionException("A transaction was found in the ThreadLocal.");
    }

    txnContext = new DefaultTransactionContext();
    holder.set(txnContext);

    // Check the thread locals for known resources
    Connection connection = ConnectionContext.get();
    if (connection != null) {
      txnContext.add(new JDBCTransactionalResource(connection));
    }

    EntityManager em = EntityManagerContext.get();
    if (em != null) {
      txnContext.add(new JPATransactionalResource(em));
    }

    return txnContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionContext getCurrent() {
    return holder.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void tearDownTransactionContext() {
    holder.remove();
  }
}
