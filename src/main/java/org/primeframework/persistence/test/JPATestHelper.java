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
package org.primeframework.persistence.test;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

import org.primeframework.persistence.service.DatabaseType.Database;
import org.primeframework.persistence.service.jpa.EntityManagerContext;

/**
 * This class is a static class that contains helpful methods for setting up and tearing down JPA.
 *
 * @author Brian Pontarelli
 */
public class JPATestHelper {
  public static EntityManagerFactory emf;

  /**
   * Constructs the JDBC connection pool, places it in the JNDI tree given and then constructs an EntityManagerFactory.
   *
   * @param type            The database type.
   * @param databaseName    The database name.
   * @param jndiName        The JNDI name to put the DataSource under.
   * @param persistenceUnit The persistence unit.
   * @throws NamingException If putting the DataSource into JNDI fails.
   */
  public static void initialize(Database type, String databaseName, String jndiName, String persistenceUnit) throws NamingException {
    Map<String, String> properties = new HashMap<String, String>();
    JDBCTestHelper.initialize(type, databaseName, jndiName);
    if (type == Database.MYSQL) {
      properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
    } else if (type == Database.POSTGRESQL) {
      properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    }

    // Create the JPA EMF
    emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);
  }

  /**
   * Constructs EntityManagerFactory and assumes that JNDI and the DataSource are already setup.
   *
   * @param type            The database type.
   * @param persistenceUnit The persistence unit.
   * @throws NamingException If putting the DataSource into JNDI fails.
   */
  public static void initialize(Database type, String persistenceUnit) throws NamingException {
    Map<String, String> properties = new HashMap<String, String>();
    if (type == Database.MYSQL) {
      properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
    } else if (type == Database.POSTGRESQL) {
      properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    }

    // Create the JPA EMF
    emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);
  }

  /**
   * Constructs the EntityManager and puts it in the context.
   */
  public static void setupForTest() {
    EntityManager em = emf.createEntityManager();
    EntityManagerContext.set(em);
  }

  /**
   * Closes the EntityManager and removes it from the context.
   */
  public static void tearDownFromTest() {
    EntityManager em = EntityManagerContext.get();
    if (em != null) {
      EntityManagerContext.remove();
      em.close();
    }
  }

  /**
   * Closes the EntityManagerFactory.
   */
  public static void tearDownJPA() {
    if (emf != null) {
      EntityManagerContext.remove();
      emf.close();
    }
  }
}
