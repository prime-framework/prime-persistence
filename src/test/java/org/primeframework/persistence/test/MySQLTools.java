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
import javax.sql.DataSource;
import java.util.logging.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * This is a toolkit that provides helper methods for working with relational databases. Some of this is specific to
 * MySQL and should eventually be refactored.
 *
 * @author James Humphrey
 * @author Brian Pontarelli
 */
public class MySQLTools {
  private static final Logger logger = Logger.getLogger(MySQLTools.class.getName());

  /**
   * Sets up the connection pool to MySQL and puts that into the JNDI tree.
   *
   * @param jndi     The JDNI context.
   * @param dbName   the db name
   * @param jndiName The JNDI name.
   * @return The DataSource and never null.
   * @throws NamingException If the binding fails.
   */
  public static DataSource setup(InitialContext jndi, String dbName, String jndiName) throws NamingException {
    String url = "jdbc:mysql://localhost:3306/" + dbName + "?user=dev&password=dev";
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL(url);
    dataSource.setAutoReconnect(true);

    jndi.bind(jndiName, dataSource);

    logger.info("DB Url [" + url + "]");
    logger.info("JNDI name is [" + jndiName + "]");
    return dataSource;
  }
}
