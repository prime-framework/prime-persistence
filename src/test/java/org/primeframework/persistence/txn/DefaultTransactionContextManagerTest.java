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

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * This class tests the default transaction context manager.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class DefaultTransactionContextManagerTest {
  @Test
  public void all() throws Exception {
    DefaultTransactionContextManager manager = new DefaultTransactionContextManager();
    TransactionContext txnContext = manager.getCurrent();
    assertNull(txnContext);

    manager.start();
    txnContext = manager.getCurrent();
    assertNotNull(txnContext);
    assertSame(txnContext, manager.getCurrent());

    manager.tearDownTransactionContext();
    assertNull(manager.getCurrent());

    manager.start();
    assertNotSame(txnContext, manager.getCurrent());
  }
}
