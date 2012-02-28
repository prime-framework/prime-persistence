/*
 * Copyright (c) 2001-2011, Inversoft Inc., All Rights Reserved
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
package org.primeframework.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a builder for selecting.
 *
 * @author Brian Pontarelli
 */
public class Select extends BaseOperation<Select> {
  private final Connection c;
  private StringBuilder sql = new StringBuilder();
  private boolean in;
  private boolean firstIn;

  public Select(Connection c) {
    super(false);
    this.c = c;
  }

  public Select(Connection c, String sql) {
    super(false);
    this.sql.append(sql);
    this.c = c;
  }

  public Select in(String sql) {
    this.sql = new StringBuilder();
    this.sql.append(sql);
    return this;
  }

  public Select startIn() {
    this.sql.append(" in (");
    this.in = true;
    this.firstIn = true;
    return this;
  }

  public Select endIn() {
    this.sql.append(")");
    this.in = true;
    this.firstIn = true;
    return this;
  }

  /**
   * Adds a parameter to an in block in the where clause.
   *
   * @param params The value parameters.
   * @return This select builder.
   */
  public Select addToIn(Object... params) {
    if (!in) {
      throw new SelectException("You must startIn before adding parameters to the in-clause");
    }

    if (!firstIn) {
      this.sql.append(",");
    } else {
      firstIn = false;
    }

    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        sql.append(",");
      }
      sql.append("?");
      add(params[i]);
    }

    return this;
  }

  public void go(SelectHandler handler) {
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString());
      setParams(ps);

      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        handler.row(rs);
      }
    } catch (SQLException e) {
      handler.exception(e);
    } finally {
      close(ps);
    }
  }

  public <T> List<T> multipleResults(RowHandler<T> handler) {
    List<T> list = new ArrayList<T>();
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString());
      setParams(ps);

      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        T t = handler.row(rs);
        if (t != null) {
          list.add(t);
        }
      }

      return list;
    } catch (SQLException e) {
      handler.exception(e);
      return null;
    } finally {
      close(ps);
    }
  }

  public <T> T singleResult(RowHandler<T> handler) {
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString());
      setParams(ps);

      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return handler.row(rs);
      }

      return null;
    } catch (SQLException e) {
      handler.exception(e);
      return null;
    } finally {
      close(ps);
    }
  }

  public Integer id() {
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString());
      setParams(ps);

      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      }

      return null;
    } catch (SQLException e) {
      throw new SelectException(e);
    } finally {
      close(ps);
    }
  }

  public int count() {
    return id();
  }

  public static abstract class SelectHandler {
    public abstract void row(ResultSet rs) throws SQLException;

    public void exception(SQLException e) {
      throw new SelectException(e);
    }
  }

  public static abstract class RowHandler<T> {
    public abstract T row(ResultSet rs) throws SQLException;

    public void exception(SQLException e) {
      throw new SelectException(e);
    }
  }

  public static class SelectException extends JDBCException {
    public SelectException() {
      super();
    }

    public SelectException(String message) {
      super(message);
    }

    public SelectException(String message, Throwable cause) {
      super(message, cause);
    }

    public SelectException(Throwable cause) {
      super(cause);
    }
  }
}
