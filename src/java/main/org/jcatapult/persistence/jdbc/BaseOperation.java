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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * This class provides some basic JDBC handling methods.
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public abstract class BaseOperation<T extends BaseOperation<T>> {
  private final List<Object> params = new ArrayList<Object>();
  private final boolean setNullParams;

  public BaseOperation(boolean setNullParams) {
    this.setNullParams = setNullParams;
  }

  public T with(Object... params) {
    this.params.clear();
    return add(params);
  }

  public T add(Object... params) {
    this.params.addAll(asList(params));
    return (T) this;
  }

  /**
   * @return The number of parameters added to this statement.
   */
  public int size() {
    return this.params.size();
  }

  void setParams(PreparedStatement ps) throws SQLException {
    if (params != null) {
      for (int i = 0; i < params.size(); i++) {
        Object param = params.get(i);
        if (param != null || setNullParams) {
          ps.setObject(i + 1, param);
        }
      }
    }
  }

  void close(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
      } catch (SQLException e) {
        throw new JDBCException("Unable to close PreparedStatement");
      }
    }
  }
}
