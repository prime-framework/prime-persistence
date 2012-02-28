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
package org.primeframework.persistence.txn;

/**
 * This interface defines a transactional context. This context can be started, committed, and rolled back. It can also
 * have any transactional resources attached to it while it is active. If it has already be completed, new resource
 * additions will result in an exception.
 *
 * @author Brian Pontarelli
 */
public interface TransactionContext {
  /**
   * Starts the transaction.
   */
  void start();

  /**
   * @return True if the transaction has been started, false otherwise.
   */
  boolean isStarted();

  /**
   * Commits the transaction.
   *
   * @throws Exception If anything went wrong during the commit.
   */
  void commit() throws Exception;

  /**
   * Rolls back the transaction.
   *
   * @throws Exception If anything went wrong during the rollback.
   */
  void rollback() throws Exception;

  /**
   * @return True if this transaction can only be rolled back.
   */
  boolean isRollbackOnly();

  /**
   * Sets that this transaction can only be rolled back.
   */
  void setRollbackOnly();

  /**
   * Adds the given resources {@link TransactionalResource} to the transaction context.
   *
   * @param resource The TransactionalResource for a transactional resource.
   * @throws Exception If the addition (and possibly start) of the transactional resource fails.
   */
  void add(TransactionalResource resource) throws Exception;
}
