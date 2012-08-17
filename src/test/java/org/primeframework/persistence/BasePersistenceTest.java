/*
 * Copyright (c) 2001-2011, Inversoft Inc., All Rights Reserved
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
package org.primeframework.persistence;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.primeframework.mock.jndi.MockJNDI;
import org.primeframework.persistence.guice.PersistenceModule;
import org.primeframework.persistence.service.DatabaseType;
import org.primeframework.persistence.service.guice.ServiceModule;
import org.primeframework.persistence.test.JDBCTestHelper;
import org.primeframework.persistence.txn.guice.TransactionModule;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This class is the base test for all persistence.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "integration")
public abstract class BasePersistenceTest {
  public static final MockJNDI jndi = new MockJNDI();
  public static Injector injector;

  @BeforeSuite
  public static void setup() throws NamingException {
    jndi.activate();
    DatabaseType.setFromSystemProperty("database.type");
    JDBCTestHelper.initialize(DatabaseType.database, "prime_persistence_test", "java:comp/env/jdbc/prime-persistence");

    injector = Guice.createInjector(new PersistenceModule(true, "punit") {
      @Override
      protected void bindDataSource() {
        bind(DataSource.class).toInstance(JDBCTestHelper.dataSource);
      }
    }, new ServiceModule(), new TransactionModule());
  }
}
