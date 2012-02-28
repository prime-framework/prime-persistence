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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.primeframework.persistence.service.DatabaseType;
import org.primeframework.persistence.service.DatabaseType.Database;
import org.primeframework.persistence.txn.TransactionContextManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

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
public class DriverAwareJPAService extends AbstractJPAService {
  private static final Logger logger = Logger.getLogger(DefaultJPAService.class.getName());

  @Inject
  public DriverAwareJPAService(TransactionContextManager txnContextManager,
                               @Named("jpa.enabled") boolean jpaEnabled,
                               @Named("jpa.unit") String persistenceUnit,
                               @Named("non-jta-data-source") String dataSourceJndiName)
  throws NamingException, SQLException {
    super(txnContextManager);
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("hibernate.dialect", dialect(dataSourceJndiName));

    if (jpaEnabled) {
      logger.fine("JPA is enabled");
      emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);
    } else {
      logger.fine("JPA is disabled");
    }
  }

  private String dialect(String dataSourceJndiName) throws NamingException, SQLException {
    InitialContext context = new InitialContext();
    DataSource ds = (DataSource) context.lookup(dataSourceJndiName);
    Connection c = ds.getConnection();
    String name = c.toString();
    c.close();

    if (name.contains("mysql")) {
      logger.fine("Connecting to a MySQL database");
      DatabaseType.database = Database.MYSQL;
      return "org.hibernate.dialect.MySQL5InnoDBDialect";
    } else {
      logger.fine("Connecting to a PostgreSQL database");
      DatabaseType.database = Database.POSTGRESQL;
      return "org.hibernate.dialect.PostgreSQLDialect";
    }
  }
}
