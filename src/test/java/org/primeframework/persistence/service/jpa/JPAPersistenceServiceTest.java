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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.primeframework.persistence.test.BaseJPATest;
import org.testng.annotations.Test;

import static org.primeframework.persistence.test.JDBCTestHelper.*;
import static org.testng.Assert.*;

/**
 * This tests the PersistenceService.
 *
 * @author Brian Pontarelli
 */
public class JPAPersistenceServiceTest extends BaseJPATest {

  @Test
  public void reloadAttached() throws Exception {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");

    EntityManager em = EntityManagerContext.get();
    User user = (User) em.createQuery("select u from User u where u.name = 'Fred'").getSingleResult();
    user.setName("Brian");

    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    service.reload(user);

    assertEquals("Fred", user.getName());
  }

  @Test
  public void reloadDetached() throws Exception {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");

    EntityManager em = EntityManagerContext.get();
    User user = (User) em.createQuery("select u from User u where u.name = 'Fred'").getSingleResult();
    user.setName("Brian");

    em.clear();
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    try {
      service.reload(user);
      fail("Should have failed because it is detached");
    } catch (Exception e) {
      // expected
    }
  }

  @Test
  public void findAll() throws Exception {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");

    clearTable("BaseSoftDeletableUser");
    executeSQL("insert into BaseSoftDeletableUser (id, insert_date, update_date, name, deleted) " +
      "values (1, 1, 1, 'Fred', false)");
    executeSQL("insert into BaseSoftDeletableUser (id, insert_date, update_date, name, deleted) " +
      "values (2, 1, 1, 'George', true)");

    // This tests that non-soft delete find all works.
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    List<User> users = service.findAllByType(User.class);
    assertEquals(users.size(), 2);
    assertEquals(users.get(0).getName(), "Fred");
    assertEquals(users.get(1).getName(), "George");

    // This tests that non-soft delete find all works even if true is sent in. That param should
    // be ignored
    users = service.findAllByType(User.class);
    assertEquals(users.size(), 2);
    assertEquals(users.get(0).getName(), "Fred");
    assertEquals(users.get(1).getName(), "George");

    // This tests that soft delete objects work correctly when ignoring inactive
    List<BaseSoftDeletableUser> softDeleteUsers = service.findAllByType(BaseSoftDeletableUser.class, false);
    assertEquals(softDeleteUsers.size(), 1);
    assertEquals(softDeleteUsers.get(0).getName(), "Fred");

    // This tests that soft delete objects work correctly when including inactive
    softDeleteUsers = service.findAllByType(BaseSoftDeletableUser.class, true);
    assertEquals(softDeleteUsers.size(), 2);
    assertEquals(softDeleteUsers.get(0).getName(), "Fred");
    assertEquals(softDeleteUsers.get(1).getName(), "George");

    System.out.println("" + users.get(0));
    System.out.println("" + softDeleteUsers.get(0));
  }

  @Test
  public void find() throws Exception {
    clearTable("users");
    for (int i = 0; i < 100; i++) {
      executeSQL("insert into users (id, insert_date, update_date, name) " +
        "values (" + (i + 1) + ", 1, 1, 'Fred" + i + "')");
    }

    clearTable("BaseSoftDeletableUser");
    for (int i = 0; i < 100; i++) {
      executeSQL("insert into BaseSoftDeletableUser (id, insert_date, update_date, name, deleted) " +
        "values (" + (i + 1) + ", 1, 1, 'Fred" + i + "', " + ((i % 2 == 0) ? "false" : "true") + ")");
    }

    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());

    // This tests that we correctly get the paginated results for non-soft delete beans
    for (int i = 0; i < 100; i += 10) {
      List<User> users = service.findByType(User.class, i, 10);
      assertEquals(users.size(), 10);
      for (int j = 0; j < 10; j++) {
        assertEquals(users.get(j).getName(), "Fred" + (j + i));
      }
    }

    // This tests that we correctly get the paginated results for non-soft delete beans, even
    // with inactive set to true
    for (int i = 0; i < 100; i += 10) {
      List<User> users = service.findByType(User.class, i, 10);
      assertEquals(users.size(), 10);
      for (int j = 0; j < 10; j++) {
        assertEquals(users.get(j).getName(), "Fred" + (j + i));
      }
    }

    // This tests that we correctly get the paginated results for soft delete beans with inactive
    // set to false
    for (int i = 0; i < 100; i += 20) {
      List<BaseSoftDeletableUser> users = service.findByType(BaseSoftDeletableUser.class, i / 2, 10, false);
      assertEquals(users.size(), 10);
      for (int j = 0; j < 10; j++) {
        assertEquals(users.get(j).getName(), "Fred" + ((j * 2) + i));
      }
    }

    // This tests that we correctly get the paginated results for soft delete beans with inactive
    // set to true
    for (int i = 0; i < 100; i += 10) {
      List<BaseSoftDeletableUser> users = service.findByType(BaseSoftDeletableUser.class, i, 10, true);
      assertEquals(users.size(), 10);
      for (int j = 0; j < 10; j++) {
        assertEquals(users.get(j).getName(), "Fred" + (j + i));
      }
    }
  }

  @Test
  public void findById() throws SQLException {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (3, 1, 1, 'Alan')");

    // This tests that querying by id works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    User user = service.findById(User.class, 1);
    assertNotNull(user);
    assertEquals(user.getName(), "Fred");

    user = service.findById(User.class, 2);
    assertNotNull(user);
    assertEquals(user.getName(), "George");

    user = service.findById(User.class, 3);
    assertNotNull(user);
    assertEquals(user.getName(), "Alan");
  }

  @Test
  public void findByIdNoVerify() throws SQLException {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (3, 1, 1, 'Alan')");

    // This tests that querying by id works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    User user = service.findById(User.class, 1);
    assertNotNull(user);
    assertEquals(user.getName(), "Fred");

    user = service.findById(User.class, 2);
    assertNotNull(user);
    assertEquals(user.getName(), "George");

    user = service.findById(User.class, 3);
    assertNotNull(user);
    assertEquals(user.getName(), "Alan");
  }

  @Test
  public void queryAll() throws Exception {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (3, 1, 1, 'Alan')");

    // This tests that querying with an orderBy clause works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    List<User> users = service.queryAll(User.class, "select u from User u order by u.name");
    assertEquals(users.size(), 3);
    assertEquals(users.get(0).getName(), "Alan");
    assertEquals(users.get(1).getName(), "Fred");
    assertEquals(users.get(2).getName(), "George");
  }

  @Test
  public void queryCount() throws Exception {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (3, 1, 1, 'Alan')");

    // This tests that querying with an orderBy clause works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    long count = service.queryCount("select count(u) from User u where u.name = ?1", "Alan");
    assertEquals(count, 1);

    count = service.queryCount("select count(u) from User u");
    assertEquals(count, 3);
  }

  @Test
  public void query() throws Exception {
    clearTable("users");
    char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    for (int i = 25; i >= 0; i--) {
      executeSQL("insert into users (id, insert_date, update_date, name) " +
        "values (" + (i + 1) + ", 1, 1, 'Fred" + alphabet[i] + "')");
    }

    // This tests that querying with an orderBy clause works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    List<User> users = service.query(User.class, "select u from User u order by u.name", 3, 21);
//        System.out.println("List is \n" + users);
    assertEquals(users.size(), 21);
    for (int i = 0; i < 21; i++) {
      assertEquals(users.get(i).getName(), "Fred" + alphabet[i + 3]);
    }
  }

  @Test
  public void queryAllWithNamedParameters() throws Exception {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (3, 1, 1, 'Alan')");

    // This tests that querying with an orderBy clause works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    List<User> users = service.queryAllWithNamedParameters(User.class, "select u from User u where u.name = :name order by u.name",
      mapNV("name", "Alan"));
    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getName(), "Alan");
  }

  @Test
  public void queryCountWithNamedParameters() throws Exception {
    clearTable("users");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (1, 1, 1, 'Fred')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (2, 1, 1, 'George')");
    executeSQL("insert into users (id, insert_date, update_date, name) " +
      "values (3, 1, 1, 'Alan')");

    // This tests that querying with an orderBy clause works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    long count = service.queryCountWithNamedParameters("select count(u) from User u where u.name = :name",
      mapNV("name", "Alan"));
    assertEquals(count, 1);

    count = service.queryCountWithNamedParameters("select count(u) from User u", mapNV());
    assertEquals(count, 3);
  }

  @Test
  public void queryWithNamedParameters() throws Exception {
    clearTable("users");
    char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    for (int i = 25; i >= 0; i--) {
      executeSQL("insert into users (id, insert_date, update_date, name) " +
        "values (" + (i + 1) + ", 1, 1, 'Fred" + alphabet[i] + "')");
    }

    // This tests that querying with an orderBy clause works
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());
    List<User> users = service.queryWithNamedParameters(User.class, "select u from User u where u.name like :name order by u.name", 3, 21,
      mapNV("name", "%Fred%"));
    assertEquals(users.size(), 21);
    for (int i = 0; i < 21; i++) {
      assertEquals(users.get(i).getName(), "Fred" + alphabet[i + 3]);
    }
  }

  @Test
  public void insert() throws Exception {
    clearTable("users");
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());

    // Test that the persist works and that it correctly handles the dates using the interceptor
    User user = new User();
    user.setName("Fred");
    user.setUuid(UUID.randomUUID());
    service.persist(user);

    user = service.findAllByType(User.class).get(0);
    assertNotNull(user);
    assertEquals(user.getName(), "Fred");
    assertNotNull(user.getUuid());

    // Test the unique key violation
    user = new User();
    user.setName("Fred");
    try {
      service.persist(user);
      fail("Should have failed");
    } catch (PersistenceException e) {
    }
  }

  @Test
  public void update() throws Exception {
    insert();
    EntityManagerContext.get().clear();

    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());

    // Test that the persist works and that it correctly handles the dates using the interceptor
    User user = service.findAllByType(User.class).get(0);
    user.setName("Barry");
    service.persist(user);
    EntityManagerContext.get().clear();

    user = service.findAllByType(User.class).get(0);
    assertNotNull(user);
    assertEquals(user.getName(), "Barry");

    // Test update unique key violation
    user = new User();
    user.setName("Manilow");
    service.persist(user);

    user = service.findAllByType(User.class).get(1);
    user.setName("Barry");
    try {
      service.persist(user);
      fail("Should have failed");
    } catch (PersistenceException e) {
    }
  }

  @Test
  public void updateDetached() throws Exception {
    insert();
    EntityManagerContext.get().clear();

    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());

    // Test that the persist works and that it correctly handles the dates using the interceptor
    User user = service.findAllByType(User.class).get(0);
    EntityManagerContext.get().clear();
    user.setName("Barry");
    service.persist(user);

    user = service.findAllByType(User.class).get(0);
    assertNotNull(user);
    assertEquals(user.getName(), "Barry");
  }

  @Test
  public void persistOuterTransaction() throws Exception {
    clearTable("users");
    EntityManager em = EntityManagerContext.get();
    JPAPersistenceService service = new JPAPersistenceService(em);

    EntityTransaction et = em.getTransaction();
    assertFalse(et.isActive());
    et.begin();

    // This should NOT commit because there is a transaction in progress
    User user = new User();
    user.setName("Fred");
    service.persist(user);

    // Check via JDBC that the data is NOT in the database yet
    RowSet rw = executeQuery("select * from users");
    assertFalse(rw.next());
    rw.close();

    // Now commit the txn
    et.commit();

    // Now check again
    rw = executeQuery("select * from users");
    assertTrue(rw.next());
    assertEquals(rw.getString("name"), "Fred");
    rw.close();
  }

  @Test
  public void remove() throws Exception {
    // Test by id
    doRemove(true, true);
    ensureDeleted();

    // Test by object
    doRemove(false, true);
    ensureDeleted();

    // Test outer transaction by id
    EntityManager em = EntityManagerContext.get();
    EntityTransaction et = em.getTransaction();
    et.begin();
    doRemove(true, false);
    et.commit();
    ensureDeleted();

    // Test outer transaction by object
    et = em.getTransaction();
    et.begin();
    doRemove(false, false);
    et.commit();
    ensureDeleted();
  }

  /**
   * Does the actual removal of the users from the database.
   *
   * @param id           Determines if the ID or Object method should be used.
   * @param verifyExists Determines if the code should verify that the data was added to the DB or that it wasn't added
   *                     to the DB. It won't be added to the DB if there is an outer transaction occurring.
   * @throws Exception If things get dicey.
   */
  private void doRemove(boolean id, boolean verifyExists) throws Exception {
    clearTable("users");
    clearTable("BaseSoftDeletableUser");
    JPAPersistenceService service = new JPAPersistenceService(EntityManagerContext.get());

    User user = new User();
    user.setName("Fred");

    BaseSoftDeletableUser softDeleteUser = new BaseSoftDeletableUser();
    softDeleteUser.setName("Zeus");

    BaseSoftDeletableUser softDeleteUserForce = new BaseSoftDeletableUser();
    softDeleteUserForce.setName("Force");

    service.persist(user);
    service.persist(softDeleteUser);
    service.persist(softDeleteUserForce);

    // Ensure the ids got generated
    assertNotNull(user.getId());
    assertNotNull(softDeleteUser.getId());
    assertNotNull(softDeleteUserForce.getId());

    if (verifyExists) {
      // Verify the data is in there
      RowSet rw = executeQuery("select * from users");
      assertTrue(rw.next());
      assertEquals(rw.getString("name"), "Fred");
      assertFalse(rw.next());
      rw.close();

      rw = executeQuery("select * from BaseSoftDeletableUser where deleted = false");
      assertTrue(rw.next());
      assertEquals(rw.getString("name"), "Zeus");
      assertTrue(rw.next());
      assertEquals(rw.getString("name"), "Force");
      assertFalse(rw.next());
      rw.close();
    } else {
      // Verify the data has not been committed yet because there is an outer transaction
      RowSet rw = executeQuery("select * from users");
      assertFalse(rw.next());
      rw.close();

      rw = executeQuery("select * from BaseSoftDeletableUser");
      assertFalse(rw.next());
      rw.close();
    }

    // Remove and verify it was removed
    if (!id) {
      service.delete(user);
      service.delete(softDeleteUser);
      service.forceDelete(softDeleteUserForce);
    } else {
      assertTrue(service.delete(User.class, user.getId()));
      assertTrue(service.delete(BaseSoftDeletableUser.class, softDeleteUser.getId()));
      assertTrue(service.forceDelete(BaseSoftDeletableUser.class, softDeleteUserForce.getId()));
    }
  }

  /**
   * Can be called to ensure that the values were deleted from the DB.
   *
   * @throws Exception If things get dicey.
   */
  private void ensureDeleted() throws Exception {
    // Ensure it was deleted
    RowSet rw = executeQuery("select * from users");
    assertFalse(rw.next());
    rw.close();

    rw = executeQuery("select * from BaseSoftDeletableUser where deleted = true");
    assertTrue(rw.next());
    assertEquals(rw.getString("name"), "Zeus");
    assertFalse(rw.next());
    rw.close();
  }

  private Map<String, Object> mapNV(String... args) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (int i = 0; i < args.length; i = i + 2) {
      String name = args[i];
      String value = args[i + 1];
      map.put(name, value);
    }

    return map;
  }
}
