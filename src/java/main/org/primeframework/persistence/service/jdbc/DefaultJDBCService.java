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
package org.primeframework.persistence.service.jdbc;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.primeframework.persistence.txn.TransactionContext;
import org.primeframework.persistence.txn.TransactionContextManager;
import org.primeframework.persistence.txn.jdbc.JDBCTransactionalResource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * This is the default implementation of the JDBC service. it uses a single named constant to determine if JDBC is
 * enabled. This constant is named <strong>non-jta-data-source</strong>.
 *
 * @author Brian Pontarelli
 */
@Singleton
public class DefaultJDBCService implements JDBCService {
  private final TransactionContextManager manager;
  private DataSource ds;

  @Inject
  public DefaultJDBCService(TransactionContextManager manager) {
    this.manager = manager;
  }

  @Inject(optional = true)
  public void setDatasourceName(@Named("non-jta-data-source") String dataSourceName) {
    if (dataSourceName == null) {
      return;
    }

    try {
      InitialContext context = new InitialContext();
      ds = (DataSource) context.lookup(dataSourceName);
    } catch (NamingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataSource getDataSource() {
    return ds;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Connection setupConnection() {
    Connection c = ConnectionContext.get();
    if (c != null) {
      return c;
    }

    try {
      if (ds == null) {
        return null;
      }

      c = ds.getConnection();
      ConnectionContext.set(c);

      TransactionContext txnContext = manager.getCurrent();
      if (txnContext != null) {
        txnContext.add(new JDBCTransactionalResource(c));
      }

      return c;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void tearDownConnection() {
    Connection c = ConnectionContext.get();
    if (c != null) {
      ConnectionContext.remove();
      try {
        c.setAutoCommit(true);
        c.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
