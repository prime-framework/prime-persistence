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
package org.primeframework.persistence.test;

import java.sql.SQLException;

import org.primeframework.persistence.service.jdbc.ConnectionContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * This class is a base class that contains helpful methods for setting up and tearing down JDBC.
 *
 * @author Brian Pontarelli
 */
public abstract class BaseJDBCTest extends BasePersistenceTest {
  /**
   * Constructs the Connection and puts it in the context.
   */
  @BeforeMethod
  public void setUp() {
    try {
      ConnectionContext.set(JDBCTestHelper.dataSource.getConnection());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    injector.injectMembers(this);
  }

  /**
   * Closes the Connection and removes it from the context.
   */
  @AfterMethod
  public void tearDown() {
    try {
      ConnectionContext.get().close();
      ConnectionContext.remove();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
