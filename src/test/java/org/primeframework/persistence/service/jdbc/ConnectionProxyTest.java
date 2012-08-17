/*
 * Copyright (c) 2001-2012, JCatapult.org, All Rights Reserved
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
package org.primeframework.persistence.service.jdbc;

import java.sql.Connection;

import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * Tests the proxy to ensure it does the right pass-through for each method and to ensure it only calls the service
 * once.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class ConnectionProxyTest {
  @Test
  public void proxy() throws Exception {
    Connection connection = createStrictMock(Connection.class);
    connection.clearWarnings();
    connection.close();
    connection.commit();
    expect(connection.createArrayOf("foo", null)).andReturn(null);
    expect(connection.createBlob()).andReturn(null);
    expect(connection.createClob()).andReturn(null);
    expect(connection.createNClob()).andReturn(null);
    expect(connection.createSQLXML()).andReturn(null);
    expect(connection.createStatement()).andReturn(null);
    expect(connection.createStatement(0, 0)).andReturn(null);
    expect(connection.createStatement(0, 0, 0)).andReturn(null);
    expect(connection.createStruct("foo", null)).andReturn(null);
    expect(connection.getAutoCommit()).andReturn(false);
    expect(connection.getCatalog()).andReturn("foo");
    expect(connection.getClientInfo()).andReturn(null);
    expect(connection.getClientInfo("foo")).andReturn(null);
    expect(connection.getHoldability()).andReturn(0);
    expect(connection.getMetaData()).andReturn(null);
    expect(connection.getTransactionIsolation()).andReturn(0);
    expect(connection.getTypeMap()).andReturn(null);
    expect(connection.getWarnings()).andReturn(null);
    expect(connection.isClosed()).andReturn(false);
    expect(connection.isReadOnly()).andReturn(false);
    expect(connection.isValid(0)).andReturn(false);
    expect(connection.isWrapperFor(null)).andReturn(false);
    expect(connection.nativeSQL("foo")).andReturn("foo");
    expect(connection.prepareCall("foo")).andReturn(null);
    expect(connection.prepareCall("foo", 0, 0)).andReturn(null);
    expect(connection.prepareCall("foo", 0, 0, 0)).andReturn(null);
    expect(connection.prepareStatement("foo")).andReturn(null);
    expect(connection.prepareStatement("foo", 0)).andReturn(null);
    expect(connection.prepareStatement("foo", 0, 0)).andReturn(null);
    expect(connection.prepareStatement("foo", 0, 0, 0)).andReturn(null);
    int[] columnIndexes = {0};
    expect(connection.prepareStatement("foo", columnIndexes)).andReturn(null);
    String[] columnNames = {"foo"};
    expect(connection.prepareStatement("foo", columnNames)).andReturn(null);
    connection.releaseSavepoint(null);
    connection.rollback();
    connection.rollback(null);
    connection.setAutoCommit(false);
    connection.setCatalog(null);
    connection.setClientInfo("foo", "foo");
    connection.setClientInfo(null);
    connection.setHoldability(0);
    connection.setReadOnly(false);
    expect(connection.setSavepoint()).andReturn(null);
    expect(connection.setSavepoint("foo")).andReturn(null);
    connection.setTransactionIsolation(0);
    connection.setTypeMap(null);
    expect(connection.unwrap(null)).andReturn(null);
    replay(connection);

    JDBCService service = createStrictMock(JDBCService.class);
    expect(service.setupConnection()).andReturn(connection);
    replay(service);

    ConnectionProxy proxy = new ConnectionProxy(service);
    proxy.clearWarnings();
    proxy.close();
    proxy.commit();
    assertEquals(proxy.createArrayOf("foo", null), null);
    assertEquals(proxy.createBlob(), null);
    assertEquals(proxy.createClob(), null);
    assertEquals(proxy.createNClob(), null);
    assertEquals(proxy.createSQLXML(), null);
    assertEquals(proxy.createStatement(), null);
    assertEquals(proxy.createStatement(0, 0), null);
    assertEquals(proxy.createStatement(0, 0, 0), null);
    assertEquals(proxy.createStruct("foo", null), null);
    assertEquals(proxy.getAutoCommit(), false);
    assertEquals(proxy.getCatalog(), "foo");
    assertEquals(proxy.getClientInfo(), null);
    assertEquals(proxy.getClientInfo("foo"), null);
    assertEquals(proxy.getHoldability(), 0);
    assertEquals(proxy.getMetaData(), null);
    assertEquals(proxy.getTransactionIsolation(), 0);
    assertEquals(proxy.getTypeMap(), null);
    assertEquals(proxy.getWarnings(), null);
    assertEquals(proxy.isClosed(), false);
    assertEquals(proxy.isReadOnly(), false);
    assertEquals(proxy.isValid(0), false);
    assertEquals(proxy.isWrapperFor(null), false);
    assertEquals(proxy.nativeSQL("foo"), "foo");
    assertEquals(proxy.prepareCall("foo"), null);
    assertEquals(proxy.prepareCall("foo", 0, 0), null);
    assertEquals(proxy.prepareCall("foo", 0, 0, 0), null);
    assertEquals(proxy.prepareStatement("foo"), null);
    assertEquals(proxy.prepareStatement("foo", 0), null);
    assertEquals(proxy.prepareStatement("foo", 0, 0), null);
    assertEquals(proxy.prepareStatement("foo", 0, 0, 0), null);
    assertEquals(proxy.prepareStatement("foo", columnIndexes), null);
    assertEquals(proxy.prepareStatement("foo", columnNames), null);
    proxy.releaseSavepoint(null);
    proxy.rollback();
    proxy.rollback(null);
    proxy.setAutoCommit(false);
    proxy.setCatalog(null);
    proxy.setClientInfo("foo", "foo");
    proxy.setClientInfo(null);
    proxy.setHoldability(0);
    proxy.setReadOnly(false);
    assertEquals(proxy.setSavepoint(), null);
    assertEquals(proxy.setSavepoint("foo"), null);
    proxy.setTransactionIsolation(0);
    proxy.setTypeMap(null);
    assertEquals(proxy.unwrap(null), null);

    verify(connection, service);
  }
}
