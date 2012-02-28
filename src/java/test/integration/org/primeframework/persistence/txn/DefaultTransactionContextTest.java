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

import java.sql.SQLException;

import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * This class test the default transaction context.
 *
 * @author Brian Pontarelli
 */
public class DefaultTransactionContextTest {
  @Test
  public void notStarted() {
    DefaultTransactionContext context = new DefaultTransactionContext();

    try {
      context.commit();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    try {
      context.rollback();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    try {
      context.setRollbackOnly();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }
  }

  @Test
  public void committed() throws Exception {
    DefaultTransactionContext context = new DefaultTransactionContext();
    context.start();
    assertFalse(context.isRollbackOnly());
    context.commit();
    context.commit(); // Second call shouldn't fail

    try {
      context.rollback();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    try {
      context.setRollbackOnly();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }
  }

  @Test
  public void rolledBack() throws Exception {
    DefaultTransactionContext context = new DefaultTransactionContext();
    context.start();
    assertFalse(context.isRollbackOnly());
    context.rollback();
    context.rollback(); // Second call shouldn't fail

    try {
      context.commit();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    try {
      context.setRollbackOnly();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }
  }

  @Test
  public void rollbackOnly() throws Exception {
    DefaultTransactionContext context = new DefaultTransactionContext();
    context.start();
    context.setRollbackOnly();
    assertTrue(context.isRollbackOnly());

    try {
      context.commit();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    context.rollback();
  }

  @Test
  public void startAddCommit() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.commit();
    replay(resource);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.start();
    context.add(resource);
    context.commit();
    context.commit(); // Second call shouldn't fail

    try {
      context.rollback();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource);
  }

  @Test
  public void addStartCommit() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.commit();
    replay(resource);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.add(resource);
    context.start();
    context.commit();
    context.commit(); // Second call shouldn't fail

    try {
      context.rollback();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource);
  }

  @Test
  public void startAddRollback() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.rollback();
    replay(resource);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.start();
    context.add(resource);
    context.rollback();
    context.rollback(); // Second call shouldn't fail

    try {
      context.commit();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource);
  }

  @Test
  public void addStartRollback() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.rollback();
    replay(resource);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.add(resource);
    context.start();
    context.rollback();
    context.rollback(); // Second call shouldn't fail

    try {
      context.commit();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource);
  }

  @Test
  public void startAddCommitMultiple() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.commit();
    replay(resource);

    TransactionalResource resource2 = createStrictMock(TransactionalResource.class);
    resource2.start();
    resource2.commit();
    replay(resource2);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.start();
    context.add(resource);
    context.add(resource2);
    context.commit();
    context.commit(); // Second call shouldn't fail

    try {
      context.rollback();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource, resource2);
  }

  @Test
  public void addStartCommitMultiple() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.commit();
    replay(resource);

    TransactionalResource resource2 = createStrictMock(TransactionalResource.class);
    resource2.start();
    resource2.commit();
    replay(resource2);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.add(resource);
    context.add(resource2);
    context.start();
    context.commit();
    context.commit(); // Second call shouldn't fail

    try {
      context.rollback();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource, resource2);
  }

  @Test
  public void startAddRollbackMultiple() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.rollback();
    replay(resource);

    TransactionalResource resource2 = createStrictMock(TransactionalResource.class);
    resource2.start();
    resource2.rollback();
    replay(resource2);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.start();
    context.add(resource);
    context.add(resource2);
    context.rollback();
    context.rollback(); // Second call shouldn't fail

    try {
      context.commit();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource, resource2);
  }

  @Test
  public void addStartRollbackMultiple() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.rollback();
    replay(resource);

    TransactionalResource resource2 = createStrictMock(TransactionalResource.class);
    resource2.start();
    resource2.rollback();
    replay(resource2);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.add(resource);
    context.add(resource2);
    context.start();
    context.rollback();
    context.rollback(); // Second call shouldn't fail

    try {
      context.commit();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource, resource2);
  }

  @Test
  public void oneRollbackFailed() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.rollback();
    replay(resource);

    TransactionalResource resource2 = createStrictMock(TransactionalResource.class);
    resource2.start();
    resource2.rollback();
    expectLastCall().andThrow(new SQLException());
    replay(resource2);

    TransactionalResource resource3 = createStrictMock(TransactionalResource.class);
    resource3.start();
    resource3.rollback();
    replay(resource3);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.add(resource);
    context.add(resource2);
    context.add(resource3);
    context.start();

    try {
      context.rollback();
      fail("Should have failed");
    } catch (Exception e) {
      // Expected
    }

    verify(resource, resource2, resource3);
  }

  @Test
  public void oneCommitFailed() throws Exception {
    TransactionalResource resource = createStrictMock(TransactionalResource.class);
    resource.start();
    resource.commit();
    replay(resource);

    TransactionalResource resource2 = createStrictMock(TransactionalResource.class);
    resource2.start();
    resource2.commit();
    expectLastCall().andThrow(new SQLException());
    replay(resource2);

    TransactionalResource resource3 = createStrictMock(TransactionalResource.class);
    resource3.start();
    resource3.commit();
    replay(resource3);

    DefaultTransactionContext context = new DefaultTransactionContext();
    context.add(resource);
    context.add(resource2);
    context.add(resource3);
    context.start();

    context.commit();

    verify(resource, resource2, resource3);
  }
}
