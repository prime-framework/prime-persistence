/*
 * Copyright (c) 2001-2011, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class is a builder for updating.
 *
 * @author Brian Pontarelli
 */
public class Update extends BaseOperation<Update> {
  private final Connection c;
  private StringBuilder sql = new StringBuilder();
  private boolean in;
  private boolean firstIn;

  public Update(Connection c) {
    super(true);
    this.c = c;
  }

  public Update(Connection c, String sql) {
    super(true);
    this.c = c;
    this.sql.append(sql);
  }

  public Update in(String sql) {
    this.sql.append(sql);
    return this;
  }

  public Update startIn() {
    this.sql.append(" in (");
    this.in = true;
    this.firstIn = true;
    return this;
  }

  public Update endIn() {
    this.sql.append(")");
    this.in = true;
    this.firstIn = true;
    return this;
  }

  /**
   * Adds a parameter to an in block in the where clause.
   *
   * @param params The value parameters.
   * @return This update builder.
   */
  public Update addToIn(Object... params) {
    if (!in) {
      throw new UpdateException("You must startIn before adding parameters to the in-clause");
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

  /**
   * Performs the update.
   *
   * @return The number of rows updated.
   * @throws UpdateException If the update fails.
   */
  public int go() throws UpdateException {
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString());
      setParams(ps);

      return ps.executeUpdate();
    } catch (SQLException e) {
      throw new UpdateException(e);
    } finally {
      close(ps);
    }
  }

  public static class UpdateException extends JDBCException {
    public UpdateException() {
      super();
    }

    public UpdateException(String message) {
      super(message);
    }

    public UpdateException(String message, Throwable cause) {
      super(message, cause);
    }

    public UpdateException(Throwable cause) {
      super(cause);
    }
  }
}
