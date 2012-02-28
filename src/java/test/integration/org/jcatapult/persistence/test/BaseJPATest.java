/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.test;

import javax.naming.NamingException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * This class is a base class that contains helpful methods for setting up and tearing down JPA.
 *
 * @author Brian Pontarelli
 */
public abstract class BaseJPATest extends BasePersistenceTest {
  /**
   * Constructs the EntityManagerFactory.
   *
   * @throws NamingException If the JNDI setup fails.
   */
  @BeforeSuite
  public static void setUpJPA() throws NamingException {
    JPATestHelper.initialize();
  }

  /**
   * Constructs the EntityManager and puts it in the context.
   */
  @BeforeMethod
  public void setUpEntityManager() {
    JPATestHelper.setupForTest();
    injector.injectMembers(this);
  }

  /**
   * Closes the EntityManager and removes it from the context.
   */
  @AfterMethod
  public void tearDownEntityManager() {
    JPATestHelper.tearDownFromTest();
  }

  /**
   * Closes the EntityManagerFactory.
   */
  @AfterSuite
  public static void tearDownJPA() {
    JPATestHelper.tearDownJPA();
  }
}
