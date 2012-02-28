/*
 * Copyright (c) 2001-2007, Inversoft Inc., All Rights Reserved
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
package org.primeframework.persistence.service;

/**
 * This class is a transaction facade that provides all the methods that most transactions will require and that all the
 * backing implementations should support.
 *
 * @author Brian Pontarelli
 */
public interface Transaction {
  /**
   * Commits the transaction. This will throw exceptions if the transaction has already been committed, rolled back or
   * the commit fails. Those exceptions are implementation dependent and are all runtime.
   */
  void commit();

  /**
   * Rolls back the transaction. This will throw exceptions if the transaction has already been committed, rolled back
   * or the roll back fails. Those exceptions are implementation dependent and are all runtime.
   */
  void rollback();

  /**
   * Sets the transaction into rollback only state. This will throw exceptions if the transaction has already been
   * committed, rolled back or the setting of the rollback only flag fails. Those exceptions are implementation
   * dependent and are all runtime.
   */
  void setRollbackOnly();

  /**
   * Returns whether or not the transaction is roll back only.
   *
   * @return True if roll back only, false otherwise.
   */
  boolean getRollbackOnly();

  /**
   * Returns if the transaction is still active and has not been committed, rolled back or in any other way closed.
   *
   * @return True if active, false it not.
   */
  boolean isActive();
}