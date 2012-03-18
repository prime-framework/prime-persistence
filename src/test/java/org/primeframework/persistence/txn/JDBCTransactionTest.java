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
package org.primeframework.persistence.txn;

import javax.naming.NamingException;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

import org.primeframework.persistence.service.jpa.User;
import org.primeframework.persistence.test.BaseJDBCTest;
import org.primeframework.persistence.test.JDBCTestHelper;
import org.primeframework.persistence.test.JPATestHelper;
import org.primeframework.persistence.txn.annotation.Transactional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import static org.primeframework.persistence.test.JDBCTestHelper.*;
import static org.testng.Assert.*;

/**
 * This class tests the transaction annotation and the defaults at the macro and micro levels.
 *
 * @author Brian Pontarelli
 */
public class JDBCTransactionTest extends BaseJDBCTest {
  @Inject public JDBCTestService service;

  @BeforeClass
  public void setUpJPA() throws NamingException {
    // This will create the tables if this tests is run by itself
    JPATestHelper.initialize();
  }

  @Test
  public void service() throws SQLException {
    JDBCTestHelper.executeSQL("delete from users");
    service.success();
    RowSet rs = executeQuery("select count(*) from users");
    rs.next();
    assertEquals(1, rs.getLong(1));
    rs = executeQuery("select name from users where name = 'TransactionTest-success'");
    assertTrue(rs.next());
    rs.close();

    try {
      service.failure();
      fail("Should have thrown an exception");
    } catch (Exception e) {
      // Expected
    }
    rs = executeQuery("select count(*) from users");
    rs.next();
    assertEquals(1, rs.getLong(1));
    rs = executeQuery("select name from users where name = 'TransactionTest-failure'");
    assertFalse(rs.next());
    rs.close();

    service.returnValueSuccess();
    rs = executeQuery("select count(*) from users");
    rs.next();
    assertEquals(2, rs.getLong(1));
    rs = executeQuery("select name from users where name = 'TransactionTest-returnValueSuccess'");
    assertTrue(rs.next());
    rs.close();

    service.returnValueFailure();
    rs = executeQuery("select count(*) from users");
    rs.next();
    assertEquals(2, rs.getLong(1));
    rs = executeQuery("select name from users where name = 'TransactionTest-returnValueFailure'");
    assertFalse(rs.next());
    rs.close();
  }

  public static class JDBCTestService {
    private final Connection c;

    @Inject
    public JDBCTestService(Connection c) {
      this.c = c;
    }

    @Transactional()
    public void success() throws SQLException {
      c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20001, 1, 'TransactionTest-success', 1)");
    }

    @Transactional()
    public void failure() throws SQLException {
      c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20002, 1, 'TransactionTest-failure', 1)");
      throw new RuntimeException();
    }

    @Transactional(processor = UserProcessor.class)
    public User returnValueSuccess() throws SQLException {
      c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20003, 1, 'TransactionTest-returnValueSuccess', 1)");
      User user = new User();
      user.setName("TransactionTest-returnValueSuccess");
      return user;
    }

    @Transactional(processor = UserProcessor.class)
    public User returnValueFailure() throws SQLException {
      c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20004, 1, 'TransactionTest-returnValueFailure', 1)");
      User user = new User();
      user.setName("TransactionTest-returnValueFailure");
      return user;
    }
  }

  public static class UserProcessor extends DefaultTransactionResultProcessor<User> {
    @Override
    public boolean rollback(User result, Throwable throwable) {
      return result.getName().equals("TransactionTest-returnValueFailure") ||
        super.rollback(result, throwable);
    }
  }
}
