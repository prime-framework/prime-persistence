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
package org.jcatapult.persistence.txn.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.jcatapult.persistence.txn.TransactionException;
import org.jcatapult.persistence.txn.TransactionalResource;

/**
 * This class models the transaction state for the JDBC connection.
 *
 * @author Brian Pontarelli
 */
public class JDBCTransactionalResource implements TransactionalResource<Connection, SQLException> {
  private final Connection connection;

  public JDBCTransactionalResource(Connection connection) {
    this.connection = connection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Connection wrapped() {
    return connection;
  }

  /**
   * Starts the transaction for the JDBC connection by setting auto-commit to false.
   *
   * @throws SQLException If the start failed.
   */
  @Override
  public void start() throws SQLException {
    if (!connection.getAutoCommit()) {
      throw new TransactionException("The JDBC transaction has already been started and can't be started twice.");
    }

    connection.setAutoCommit(false);
  }

  /**
   * Calls the connection's {@link Connection#commit()} method.
   */
  @Override
  public void commit() throws SQLException {
    connection.commit();
    connection.setAutoCommit(true);
  }

  /**
   * Calls the connection's {@link Connection#rollback()} method.
   */
  @Override
  public void rollback() throws SQLException {
    connection.rollback();
    connection.setAutoCommit(true);
  }
}
