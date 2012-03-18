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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is a toolkit that provides JDBC helper methods.
 *
 * @author Brian Pontarelli
 */
public class JDBCTools {
  /**
   * Gets the Double and checks for null.
   *
   * @param rs     The result set.
   * @param column The column index.
   * @return The Double or null.
   * @throws SQLException If the get failed.
   */
  public static Double getDouble(ResultSet rs, int column) throws SQLException {
    Double value = rs.getDouble(column);
    if (rs.wasNull()) {
      value = null;
    }

    return value;
  }

  /**
   * Gets the Integer and checks for null.
   *
   * @param rs     The result set.
   * @param column The column index.
   * @return The Double or null.
   * @throws SQLException If the get failed.
   */
  public static Integer getInt(ResultSet rs, int column) throws SQLException {
    Integer value = rs.getInt(column);
    if (rs.wasNull()) {
      value = null;
    }

    return value;
  }
}
