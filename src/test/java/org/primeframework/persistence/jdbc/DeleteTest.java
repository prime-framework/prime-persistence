/*
 * Copyright (c) 2011, Inversoft Inc., All Rights Reserved
 */
package org.primeframework.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.primeframework.persistence.jdbc.Delete.DeleteException;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;

/**
 * This class tests the Delete.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class DeleteTest {

  @Test
  public void noFrom() throws SQLException {
    Connection c = createStrictMock(Connection.class);
    Delete delete = new Delete(c);

    try {
      delete.execute();
    } catch (DeleteException e) {
      Assert.assertEquals(e.getMessage(), Delete.FROM_UNDEFINED_MSG);
    }
  }

  @Test
  public void noEqualsOrIn() throws SQLException {
    Connection c = createStrictMock(Connection.class);
    Delete delete = new Delete(c);
    delete.from("foo").where("bar");

    try {
      delete.execute();
    } catch (DeleteException e) {
      Assert.assertEquals(e.getMessage(), Delete.EQUALS_AND_IN_UNDEFINED_MSG);
    }
  }

  @Test
  public void equalsAndIn() throws SQLException {
    Connection c = createStrictMock(Connection.class);
    Delete delete = new Delete(c);
    delete.from("foo").where("bar").isEqualTo("baz").in("fred", "waldo");

    try {
      delete.execute();
    } catch (DeleteException e) {
      Assert.assertEquals(e.getMessage(), Delete.EQUALS_AND_IN_DEFINED_MSG);
    }
  }

  @Test
  public void noParams() throws SQLException {

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    expect(ps.executeUpdate()).andReturn(1);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("delete from foo")).andReturn(ps);
    replay(c);

    new Delete(c).from("foo").execute();

    verify(ps, c);
  }

  @Test
  public void withIsEqualTo() throws SQLException {

    String param1 = "baz";

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, param1);
    expect(ps.executeUpdate()).andReturn(1);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("delete from foo where bar = ?")).andReturn(ps);
    replay(c);

    new Delete(c).from("foo").where("bar").isEqualTo(param1).execute();

    verify(ps, c);
  }

  @Test
  public void withIn() throws SQLException {

    String param1 = "baz";
    String param2 = "fred";
    String param3 = "fred";

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, param1);
    ps.setObject(2, param2);
    ps.setObject(3, param3);
    expect(ps.executeUpdate()).andReturn(1);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("delete from foo where bar in (?,?,?)")).andReturn(ps);
    replay(c);

    new Delete(c).from("foo").where("bar").in(param1, param2, param3).execute();

    verify(ps, c);
  }
}
