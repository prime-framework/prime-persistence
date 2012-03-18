/*
 * Copyright (c) 2011, Inversoft Inc., All Rights Reserved
 */
package org.primeframework.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * This class tests the update.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class UpdateTest {
  @Test
  public void noParams() throws SQLException {
    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    expect(ps.executeUpdate()).andReturn(1);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("update foo set bar = 'baz'")).andReturn(ps);
    replay(c);

    Update u = new Update(c, "update foo set bar = 'baz'");
    int num = u.go();
    assertEquals(num, 1);

    verify(ps, c);
  }

  @Test
  public void params() throws SQLException {
    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 1);
    ps.setObject(2, 2);
    expect(ps.executeUpdate()).andReturn(1);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("update foo set bar = ? where id = ?")).andReturn(ps);
    replay(c);

    Update u = new Update(c, "update foo set bar = ? where id = ?");
    u.with(1, 2);
    int num = u.go();
    assertEquals(num, 1);

    verify(ps, c);
  }

  @Test
  public void inClause() throws SQLException {
    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 1);
    ps.setObject(2, 2);
    ps.setObject(3, 3);
    ps.setObject(4, 4);
    expect(ps.executeUpdate()).andReturn(2);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("update foo set bar = ? where id in (?,?,?)")).andReturn(ps);
    replay(c);

    Update u = new Update(c, "update foo set bar = ? where id");
    u.add(1);
    u.startIn();
    u.addToIn(2, 3, 4);
    u.endIn();

    int num = u.go();
    assertEquals(num, 2);

    verify(ps, c);
  }
}
