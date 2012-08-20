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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * This class is a builder for updating.
 *
 * @author Brian Pontarelli
 */
public class Delete extends BaseOperation<Delete> {
  public static final String FROM_UNDEFINED_MSG = "from undefined";
  public static final String EQUALS_AND_IN_AND_LESS_THAN_UNDEFINED_MSG = "where clause defined without '=' or 'in'";

  private final Connection c;
  private String someTable;
  private String someColumn;
  private Object someValue;
  private List<Object> valueList = new ArrayList<Object>();

  boolean equalsDefined = false;
  boolean lessThanDefined = false;
  boolean inDefined = false;

  public Delete(Connection c) {
    super(true);
    this.c = c;
  }

  public Delete from(String someTable) {
    this.someTable = someTable;
    return this;
  }

  public Delete where(String someColumn) {
    this.someColumn = someColumn;
    return this;
  }

  public Delete isEqualTo(Object someValue) {
    this.someValue = someValue;
    this.equalsDefined = true;
    this.inDefined = false;
    this.lessThanDefined = false;
    return this;
  }

  public Delete isLessThan(Object someValue) {
    this.someValue = someValue;
    this.equalsDefined = false;
    this.lessThanDefined = true;
    return this;
  }

  public Delete in(Object... valueList) {
    this.inDefined = true;
    this.lessThanDefined = false;
    this.equalsDefined = false;
    this.valueList.addAll(Arrays.asList(valueList));
    return this;
  }

  /**
   * Performs the deletion.
   *
   * @return The number of rows updated.
   * @throws DeleteException If the update fails.
   */
  public int execute() throws DeleteException {

    boolean fromUndefined = StringUtils.isBlank(someTable);
    boolean whereDefined = !StringUtils.isBlank(someColumn);
    boolean equalsAndInAndLessThanUndefined = !equalsDefined && !inDefined && !lessThanDefined;

    if (fromUndefined) {
      throw new DeleteException(FROM_UNDEFINED_MSG);
    }

    if (whereDefined) {
      if (equalsAndInAndLessThanUndefined) {
        throw new DeleteException(EQUALS_AND_IN_AND_LESS_THAN_UNDEFINED_MSG);
      }
    }

    StringBuilder sql = new StringBuilder();
    sql.append("delete from ").append(someTable);

    if (whereDefined) {
      sql.append(" where ").append(someColumn).append(" ");

      if (equalsDefined) {
        sql.append("= ").append("?");
        add(someValue);
      } else if (inDefined) {
        sql.append("in (");
        for (int i = 0; i < valueList.size(); i++) {
          if (i > 0) {
            sql.append(",");
          }
          sql.append("?");
          add(valueList.get(i));
        }
        sql.append(")");
      } else {
        sql.append("< ").append("?");
        add(someValue);
      }

    }

    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString());
      setParams(ps);

      return ps.executeUpdate();
    } catch (SQLException e) {
      throw new DeleteException(e);
    } finally {
      close(ps);
    }
  }

  public static class DeleteException extends JDBCException {
    public DeleteException() {
      super();
    }

    public DeleteException(String message) {
      super(message);
    }

    public DeleteException(String message, Throwable cause) {
      super(message, cause);
    }

    public DeleteException(Throwable cause) {
      super(cause);
    }
  }
}
