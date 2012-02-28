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

/**
 * This interface defines the state of the current transaction.
 *
 * @author Brian Pontarelli
 */
public interface TransactionalResource<T, E extends Exception> {
  /**
   * @return The wrapped transaction class. This will vary for different ORMs and database connections. JDBC uses the
   *         {@link java.sql.Connection} interface and JPA uses the {@link javax.persistence.EntityTransaction}.
   */
  T wrapped();

  /**
   * Starts the transaction.
   *
   * @throws E If the transaction could not be started.
   */
  void start() throws E;

  /**
   * Commits the transaction.
   *
   * @throws E Any exception that the underlying transaction API throws.
   */
  void commit() throws E;

  /**
   * Rollsback the transaction.
   *
   * @throws E Any exception that the underlying transaction API throws.
   */
  void rollback() throws E;
}
