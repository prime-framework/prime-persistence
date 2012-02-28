/*
 * Copyright (c) 2001-2007, Inversoft Inc., All Rights Reserved
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
package org.primeframework.persistence.test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * This is a toolkit that provides helper methods for working with a PostgreSQL relational databases.
 *
 * @author James Humphrey
 * @author Brian Pontarelli
 */
public class PostgreSQLTools {
  private static final Logger logger = Logger.getLogger(PostgreSQLTools.class.getName());

  /**
   * Sets up the connection pool to PostgreSQL and puts that into the JNDI tree.
   *
   * @param jndi   The JNDI context.
   * @param dbName The database name to connect to as well as the name to put the DataSource into the JNDI context under
   *               (including the java:comp/env/jdbc/ prefix)
   * @return The DataSource and never null.
   * @throws NamingException If the JNDI binding fails.
   */
  public static PGSimpleDataSource setup(InitialContext jndi, String dbName) throws NamingException {
    PGSimpleDataSource pds = new PGSimpleDataSource();
    pds.setDatabaseName(dbName);
    pds.setServerName("localhost");
    pds.setUser("dev");
    pds.setPassword("dev");

    String jndiName = "java:comp/env/jdbc/" + dbName.replace("_", "-").replace("-test", "");
    jndi.bind(jndiName, pds);

    logger.info("JNDI name is [" + jndiName + "]");

    return pds;
  }
}
