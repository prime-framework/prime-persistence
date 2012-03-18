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

/**
 * <p> This class is a builder for executing arbitrary SQL via JDBC. </p>
 *
 * @author Brian Pontarelli
 */
public class Execute extends BaseOperation<Execute> {
  private final Connection c;
  private String sql;

  public Execute(Connection c) {
    super(true);
    this.c = c;
  }

  public Execute(Connection c, boolean setNullParams) {
    super(setNullParams);
    this.c = c;
  }

  public Execute in(String sql) {
    this.sql = sql;
    return this;
  }

  public int go() {
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql);
      setParams(ps);
      return ps.executeUpdate();
    } catch (SQLException e) {
      throw new ExecuteException(e);
    } finally {
      close(ps);
    }
  }

  public static class ExecuteException extends JDBCException {
    public ExecuteException(Throwable cause) {
      super(cause);
    }
  }
}
