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
 *
 */
package org.primeframework.persistence.txn;

import javax.sql.RowSet;
import java.sql.SQLException;

import org.primeframework.persistence.BaseJPATest;
import org.primeframework.persistence.service.jpa.PersistenceService;
import org.primeframework.persistence.service.jpa.User;
import org.primeframework.persistence.test.JDBCTestHelper;
import org.primeframework.persistence.txn.annotation.Transactional;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import static org.primeframework.persistence.test.JDBCTestHelper.*;
import static org.testng.Assert.*;

/**
 * This class tests the transaction annotation and the defaults at the macro and micro levels.
 *
 * @author Brian Pontarelli
 */
public class JPATransactionTest extends BaseJPATest {
  @Inject public JPATestService service;
  @Inject public JPATopTestService topService;

  @Test
  public void marco() throws SQLException, InterruptedException {
    clearTable("users");

    service.success();
    RowSet rs = executeQuery("select name from users where name = 'JPATransactionTest-success'");
    assertTrue(rs.next());
    rs.close();

    rs = executeQuery("select name from users where name = 'JPATransactionTest-failure'");
    assertFalse(rs.next());
    rs.close();
    try {
      service.failure();
    } catch (Exception e) {
      // Expected
    }
    rs = executeQuery("select name from users where name = 'JPATransactionTest-failure'");
    assertFalse(rs.next());
    rs.close();

    service.returnValueSuccess();
    rs = executeQuery("select name from users where name = 'JPATransactionTest-returnValueSuccess'");
    assertTrue(rs.next());
    rs.close();

    service.returnValueFailure();
    rs = executeQuery("select name from users where name = 'JPATransactionTest-returnValueFailure'");
    assertFalse(rs.next());
    rs.close();
  }

  @Test
  public void macroNested() throws SQLException, InterruptedException {
    clearTable("users");

    topService.success();
    RowSet rs = executeQuery("select name from users where name = 'JPATransactionTest-success'");
    assertTrue(rs.next());
    rs.close();

    rs = executeQuery("select name from users where name = 'JPATransactionTest-failure'");
    assertFalse(rs.next());
    rs.close();
    try {
      topService.failure();
    } catch (Exception e) {
      // Expected
    }
    rs = executeQuery("select name from users where name = 'JPATransactionTest-failure'");
    assertFalse(rs.next());
    rs.close();

    topService.returnValueSuccess();
    rs = executeQuery("select name from users where name = 'JPATransactionTest-returnValueSuccess'");
    assertTrue(rs.next());
    rs.close();

    topService.returnValueFailure();
    rs = executeQuery("select name from users where name = 'JPATransactionTest-returnValueFailure'");
    assertFalse(rs.next());
    rs.close();
  }

  @Test
  public void testMicroResultProcessor() {
    DefaultTransactionResultProcessor processor = new DefaultTransactionResultProcessor();
    assertFalse(processor.rollback(null, new Throwable()));
    assertTrue(processor.rollback(null, new RuntimeException()));
    assertFalse(processor.rollback(new Object(), null));
  }

  public static class JPATestService {
    private final PersistenceService persistenceService;

    @Inject
    public JPATestService(PersistenceService persistenceService) {
      this.persistenceService = persistenceService;
    }

    @Transactional()
    public void success() throws SQLException {
      User user = new User();
      user.setName("JPATransactionTest-success");
      persistenceService.persist(user);

      // Verify that another session can't see the data
      RowSet rs = JDBCTestHelper.executeQuery("select name from users where name = 'JPATransactionTest-success'");
      assertFalse(rs.next());
      rs.close();
    }

    @Transactional()
    public void failure() throws InterruptedException {
      User user = new User();
      user.setName("JPATransactionTest-failure");
      persistenceService.persist(user);
      try {
        RowSet rs = JDBCTestHelper.executeQuery("select name from users where name = 'JPATransactionTest-failure'");
        assertFalse(rs.next());
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      throw new RuntimeException();
    }

    @Transactional(processor = UserProcessor.class)
    public User returnValueSuccess() {
      User user = new User();
      user.setName("JPATransactionTest-returnValueSuccess");
      persistenceService.persist(user);
      return user;
    }

    @Transactional(processor = UserProcessor.class)
    public User returnValueFailure() {
      User user = new User();
      user.setName("JPATransactionTest-returnValueFailure");
      persistenceService.persist(user);
      return user;
    }
  }

  public static class JPATopTestService {
    private final JPATestService service;

    @Inject
    public JPATopTestService(JPATestService service) {
      this.service = service;
    }

    @Transactional()
    public void success() throws SQLException {
      service.success();
    }

    @Transactional()
    public void failure() throws InterruptedException {
      service.failure();
    }

    @Transactional(processor = UserProcessor.class)
    public User returnValueSuccess() {
      return service.returnValueSuccess();
    }

    @Transactional(processor = UserProcessor.class)
    public User returnValueFailure() {
      return service.returnValueFailure();
    }
  }

  public static class UserProcessor extends DefaultTransactionResultProcessor<User> {
    @Override
    public boolean rollback(User result, Throwable throwable) {
      return result.getName().equals("JPATransactionTest-returnValueFailure") ||
        super.rollback(result, throwable);
    }
  }
}
