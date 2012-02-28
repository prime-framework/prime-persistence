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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This class checks if JDBC is turned on and the DataSource is in the JNDI tree.
 *
 * @author Brian Pontarelli
 */
public class ConnectionTools {
  private static DataSource dataSource;

  /**
   * Attempts to setup the database source and if it succeeds, this returns true.
   *
   * @param jndiName The name to lookup the DataSource in the JNDI tree.
   * @return True if the data source is setup, false if not.
   */
  public static boolean initialize(String jndiName) {
    try {
      InitialContext context = new InitialContext();
      dataSource = (DataSource) context.lookup(jndiName);
      return enabled();
    } catch (NamingException e) {
      return false;
    }
  }

  public static boolean enabled() {
    return dataSource != null;
  }
}
