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

import org.primeframework.persistence.service.jpa.EntityManagerContext;

/**
 * This class is a static class that contains helpful methods for setting up and tearing down JPA.
 *
 * @author Brian Pontarelli
 */
public class JPATestHelper {
  public static String persistentUnit = "punit";
  public static EntityManagerFactory emf;

  /**
   * Constructs the EntityManager and puts it in the context.
   */
  public static void setUpEntityManager() {
    EntityManager em = emf.createEntityManager();
    EntityManagerContext.set(em);
  }

  /**
   * This can be called in the Test classes constructor in order to set the JPA persistent unit to use. This is the same
   * as the init parameter for the filter. This defaults to <em>punit</em>
   *
   * @param persistentUnit The persistent unit to use.
   */
  public static void setPersistentUnit(String persistentUnit) {
    JPATestHelper.persistentUnit = persistentUnit;
  }

  /**
   * Constructs the JDBC connection pool, places it in the JNDI tree given and then constructs an EntityManagerFactory.
   *
   * @throws NamingException If putting the DataSource into JNDI fails.
   */
  public static void initialize() throws NamingException {
    Map<String, String> properties = new HashMap<String, String>();
    String dbType = JDBCTestHelper.initialize();
    if (dbType == null || dbType.equals("mysql")) {
      // This is required to tell Hibernate to use transactions
      properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
    } else if (dbType.equals("postgresql")) {
      // This is required to tell Hibernate to use postgres to create the tables
      properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    }

    // Create the JPA EMF
    emf = Persistence.createEntityManagerFactory(persistentUnit, properties);
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
