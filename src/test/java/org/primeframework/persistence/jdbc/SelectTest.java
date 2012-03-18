/*
 * Copyright (c) 2011, Inversoft Inc., All Rights Reserved
 */
package org.primeframework.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.primeframework.persistence.jdbc.Select.RowHandler;
import org.primeframework.persistence.jdbc.Select.SelectHandler;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * This class tests the select.
 *
 * @author Brian Pontarelli
 */
public class SelectTest {
  @Test
  public void noParamsNoResults() throws SQLException {
    ResultSet rs = createStrictMock(ResultSet.class);
    expect(rs.next()).andReturn(false);
    replay(rs);

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    expect(ps.executeQuery()).andReturn(rs);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("select foo from bar")).andReturn(ps);
    replay(c);

    Select s = new Select(c, "select foo from bar");
    s.go(new SelectHandler() {
      public void row(ResultSet rs) throws SQLException {
      }
    });

    verify(rs, ps, c);
  }

  @Test
  public void noParamsResults() throws SQLException {
    ResultSet rs = createStrictMock(ResultSet.class);
    expect(rs.next()).andReturn(true);
    expect(rs.getString(1)).andReturn("baz");
    expect(rs.next()).andReturn(false);
    replay(rs);

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    expect(ps.executeQuery()).andReturn(rs);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("select foo from bar")).andReturn(ps);
    replay(c);

    Select s = new Select(c, "select foo from bar");
    s.go(new SelectHandler() {
      public void row(ResultSet rs) throws SQLException {
        assertEquals(rs.getString(1), "baz");
      }
    });

    verify(rs, ps, c);
  }

  @Test
  public void resultsSingle() throws SQLException {
    ResultSet rs = createStrictMock(ResultSet.class);
    expect(rs.next()).andReturn(true);
    expect(rs.getString(1)).andReturn("baz");
    replay(rs);

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    expect(ps.executeQuery()).andReturn(rs);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("select foo from bar")).andReturn(ps);
    replay(c);

    Select s = new Select(c, "select foo from bar");
    String result = s.singleResult(new RowHandler<String>() {
      public String row(ResultSet rs) throws SQLException {
        return rs.getString(1);
      }
    });

    assertEquals(result, "baz");

    verify(rs, ps, c);
  }

  @Test
  public void resultsMultiple() throws SQLException {
    ResultSet rs = createStrictMock(ResultSet.class);
    expect(rs.next()).andReturn(true);
    expect(rs.getString(1)).andReturn("one");
    expect(rs.next()).andReturn(true);
    expect(rs.getString(1)).andReturn("two");
    expect(rs.next()).andReturn(false);
    replay(rs);

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    expect(ps.executeQuery()).andReturn(rs);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("select foo from bar")).andReturn(ps);
    replay(c);

    Select s = new Select(c, "select foo from bar");
    List<String> results = s.multipleResults(new RowHandler<String>() {
      public String row(ResultSet rs) throws SQLException {
        return rs.getString(1);
      }
    });

    assertEquals(results.size(), 2);
    assertEquals(results.get(0), "one");
    assertEquals(results.get(1), "two");

    verify(rs, ps, c);
  }

  @Test
  public void paramsResults() throws SQLException {
    ResultSet rs = createStrictMock(ResultSet.class);
    expect(rs.next()).andReturn(true);
    expect(rs.getString(1)).andReturn("baz");
    expect(rs.next()).andReturn(false);
    replay(rs);

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 123);
    expect(ps.executeQuery()).andReturn(rs);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("select foo from bar where id = ?")).andReturn(ps);
    replay(c);

    Select s = new Select(c, "select foo from bar where id = ?");
    s.add(123);
    s.go(new SelectHandler() {
      public void row(ResultSet rs) throws SQLException {
        assertEquals(rs.getString(1), "baz");
      }
    });

    verify(rs, ps, c);
  }

  @Test
  public void inClause() throws SQLException {
    ResultSet rs = createStrictMock(ResultSet.class);
    expect(rs.next()).andReturn(true);
    expect(rs.getString(1)).andReturn("baz");
    expect(rs.next()).andReturn(false);
    replay(rs);

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 123);
    ps.setObject(2, 456);
    expect(ps.executeQuery()).andReturn(rs);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("select foo from bar where id in (?,?)")).andReturn(ps);
    replay(c);

    Select s = new Select(c, "select foo from bar where id");
    s.startIn();
    s.addToIn(123, 456);
    s.endIn();
    s.go(new SelectHandler() {
      public void row(ResultSet rs) throws SQLException {
        assertEquals(rs.getString(1), "baz");
      }
    });

    verify(rs, ps, c);
  }
}
