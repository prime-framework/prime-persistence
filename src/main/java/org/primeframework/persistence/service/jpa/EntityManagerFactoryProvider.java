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
package org.primeframework.persistence.service.jpa;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.primeframework.persistence.service.DatabaseType;
import org.primeframework.persistence.service.DatabaseType.Database;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * This class is a Guice provider that provider access to the EntityManagerFactory.
 *
 * @author Brian Pontarelli
 */
public class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {
  private final static Logger logger = Logger.getLogger(EntityManagerFactoryProvider.class.getName());
  private final DataSource dataSource;
  private final String persistenceUnit;

  @Inject
  public EntityManagerFactoryProvider(DataSource dataSource, @Named("jpa.unit") String persistenceUnit) {
    this.dataSource = dataSource;
    this.persistenceUnit = persistenceUnit;
  }

  public EntityManagerFactory get() {
    Map<String, String> properties = new HashMap<String, String>();
    try {
      properties.put("hibernate.dialect", dialect(dataSource));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return Persistence.createEntityManagerFactory(persistenceUnit, properties);
  }

  private String dialect(DataSource dataSource) throws NamingException, SQLException {
    Connection c = dataSource.getConnection();
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
