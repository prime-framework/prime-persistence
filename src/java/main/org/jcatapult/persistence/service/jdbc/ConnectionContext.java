/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.service.jdbc;

import java.sql.Connection;

/**
 * This class is a context container for the JDBC Connection that might be checked out of the JNDI data source by the
 * provider.
 *
 * @author Brian Pontarelli
 */
public class ConnectionContext {
  private static ThreadLocal<Connection> holder = new ThreadLocal<Connection>();

  /**
   * Sets the Connection for this Thread.
   *
   * @param connection The Connection to set.
   */
  public static void set(Connection connection) {
    holder.set(connection);
  }

  /**
   * Returns the Connection for this context.
   *
   * @return The Connection or null if the filter is not setup correctly.
   */
  public static Connection get() {
    return holder.get();
  }

  /**
   * Removes the Connection from the context.
   */
  public static void remove() {
    holder.remove();
  }
}
