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

import com.google.inject.ImplementedBy;

/**
 * This interface defines the manager that manages the current transaction context.
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultTransactionContextManager.class)
public interface TransactionContextManager {
  /**
   * Starts a new transaction.
   *
   * @return The transaction.
   * @throws Exception If any of the transactional resources that are already open threw an exception when starting a
   *                   transaction for themselves.
   */
  TransactionContext start() throws Exception;

  /**
   * Retrieves the current transaction context.
   *
   * @return The current transaction context.
   */
  TransactionContext getCurrent();

  /**
   * Tears down the current transaction context if there is one.
   */
  void tearDownTransactionContext();
}
