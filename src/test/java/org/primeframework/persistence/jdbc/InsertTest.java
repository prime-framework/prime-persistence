/*
 * Copyright (c) 2011, Inversoft Inc., All Rights Reserved
 */
package org.primeframework.persistence.jdbc;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.primeframework.persistence.jdbc.Insert.GeneratedKeyHandler;
import org.primeframework.persistence.jdbc.Insert.InsertResult;
import org.primeframework.persistence.jdbc.convert.TypeConverter;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * This class tests the insert.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class InsertTest {
  @Test
  public void noParams() throws SQLException {
    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    expect(ps.executeUpdate()).andReturn(1);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("insert into foo (bar) values ('one')", Statement.NO_GENERATED_KEYS)).andReturn(ps);
    replay(c);

    Insert i = new Insert(c, "insert into foo (bar) values ('one')");
    int num = i.go();
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
    expect(c.prepareStatement("insert into foo (bar) values (?,?)", Statement.NO_GENERATED_KEYS)).andReturn(ps);
    replay(c);

    Insert i = new Insert(c, "insert into foo (bar) values (?,?)");
    i.with(1, 2);
    int num = i.go();
    assertEquals(num, 1);

    verify(ps, c);
  }

  @Test
  public void generatedKeys() throws SQLException {
    ResultSet rs = createStrictMock(ResultSet.class);
    expect(rs.next()).andReturn(true);
    expect(rs.getInt(1)).andReturn(123);
    expect(rs.next()).andReturn(false);
    replay(rs);

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 1);
    ps.setObject(2, 2);
    expect(ps.executeUpdate()).andReturn(1);
    expect(ps.getGeneratedKeys()).andReturn(rs);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("insert into foo (bar) values (?,?)", Statement.RETURN_GENERATED_KEYS)).andReturn(ps);
    replay(c);

    Insert i = new Insert(c, "insert into foo (bar) values (?,?)");
    i.with(1, 2);
    InsertResult result = i.go(new GeneratedKeyHandler<Integer>() {
      public Integer handle(ResultSet rs) throws SQLException {
        return rs.getInt(1);
      }
    });
    assertEquals(result.count, 1);
    assertEquals(result.keys.get(0), 123);

    verify(rs, ps, c);
  }

  @Test
  public void bulk() throws SQLException {
    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 1);
    ps.setObject(2, 2);
    ps.setObject(3, 3);
    ps.setObject(4, 4);
    expect(ps.executeUpdate()).andReturn(2);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("insert into foo (bar) values (?,?),(?,?)", Statement.NO_GENERATED_KEYS)).andReturn(ps);
    replay(c);

    Insert i = new Insert(c, "insert into foo (bar) values ");
    i.addBulk(1, 2);
    i.addBulk(3, 4);
    int num = i.go();
    assertEquals(num, 2);

    verify(ps, c);
  }

  @Test
  public void objectsInferred() throws Exception {
    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 1);
    ps.setObject(2, "test1");
    ps.setObject(3, 2);
    ps.setObject(4, "test2");
    expect(ps.executeUpdate()).andReturn(2);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("insert into test_domain_inferred (column_one,column_two) values (?,?),(?,?)", Statement.NO_GENERATED_KEYS)).andReturn(ps);
    replay(c);

    int num = new Insert(c).into(TestDomainInferred.class).objects(new TestDomainInferred(1, "test1"), new TestDomainInferred(2, "test2")).go();
    assertEquals(num, 2);

    verify(ps, c);
  }

  @Test
  public void objectsAnnotated() throws Exception {
    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, 1);
    ps.setObject(2, "test1");
    ps.setObject(3, 2);
    ps.setObject(4, "test2");
    expect(ps.executeUpdate()).andReturn(2);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("insert into foo (column_1,column_2) values (?,?),(?,?)", Statement.NO_GENERATED_KEYS)).andReturn(ps);
    replay(c);

    int num = new Insert(c).into(TestDomainAnnotated.class).objects(new TestDomainAnnotated(1, "test1", "1"), new TestDomainAnnotated(2, "test2", "2")).go();
    assertEquals(num, 2);

    verify(ps, c);
  }

  @Test
  public void objectsConvert() throws Exception {
    TypeConverter.Converters.register(TestType.class, new TestTypeConverter());

    PreparedStatement ps = createStrictMock(PreparedStatement.class);
    ps.setObject(1, "test1");
    ps.setObject(2, "test2");
    expect(ps.executeUpdate()).andReturn(2);
    ps.close();
    replay(ps);

    Connection c = createStrictMock(Connection.class);
    expect(c.prepareStatement("insert into bar (column_one) values (?),(?)", Statement.NO_GENERATED_KEYS)).andReturn(ps);
    replay(c);

    int num = new Insert(c).into(TestDomainConvert.class).
      objects(new TestDomainConvert(new TestType("test1")), new TestDomainConvert(new TestType("test2"))).go();
    assertEquals(num, 2);

    verify(ps, c);
  }

  public static class TestDomainInferred {
    public Integer columnOne;
    public String columnTwo;

    public TestDomainInferred(Integer columnOne, String columnTwo) {
      this.columnOne = columnOne;
      this.columnTwo = columnTwo;
    }
  }

  @Table(name = "foo")
  public static class TestDomainAnnotated {
    @Column(name = "column_1") public Integer columnOne;
    @Column(name = "column_2") public String columnTwo;
    @Transient public String columnThree;

    public TestDomainAnnotated(Integer columnOne, String columnTwo, String columnThree) {
      this.columnOne = columnOne;
      this.columnTwo = columnTwo;
      this.columnThree = columnThree;
    }
  }

  @Table(name = "bar")
  public static class TestDomainConvert {
    public TestType columnOne;

    public TestDomainConvert(TestType columnOne) {
      this.columnOne = columnOne;
    }
  }

  public static class TestType {
    public String test;

    public TestType(String test) {
      this.test = test;
    }
  }

  public static class TestTypeConverter implements TypeConverter<TestType> {
    @Override
    public Object convertToSQL(TestType object) {
      return object.test;
    }

    @Override
    public TestType convertFromSQL(Object object) {
      TestType type = new TestType(object.toString());
      return type;
    }
  }
}
