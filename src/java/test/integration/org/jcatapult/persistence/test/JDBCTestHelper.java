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
package org.jcatapult.persistence.test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Table;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.jcatapult.persistence.service.DatabaseType;
import org.jcatapult.persistence.service.DatabaseType.Database;

import com.sun.rowset.CachedRowSetImpl;

/**
 * This class provides test helper methods for JDBC.
 *
 * @author Brian Pontarelli
 */
public class JDBCTestHelper {
  public static final Logger logger = Logger.getLogger(JDBCTestHelper.class.getName());
  public static DataSource dataSource;
  public static String databaseName;
  public static String databaseType;

  /**
   * Allows test classes to setup different databases. The default is to setup a database connection to the database
   * that has the same name as the project (dashes are replaced by underscores).
   *
   * @param databaseName The database name.
   */
  public static void setDatabaseName(String databaseName) {
    JDBCTestHelper.databaseName = databaseName;
  }

  /**
   * Allows test classes to setup different databases. This is a data source so that PostgreSQL or Oracle database can
   * be setup.
   *
   * @param dataSource The data source to put into the JNDI tree.
   */
  public static void setDataSource(DataSource dataSource) {
    JDBCTestHelper.dataSource = dataSource;
  }

  /**
   * Constructs the JDBC connection pool, places it in the JNDI tree given.
   *
   * @return The database type (either the string <strong>mysql</strong> or <strong>postgresql</strong>)
   * @throws NamingException If setting the data source into the JNDI tree fails.
   */
  public static String initialize() throws NamingException {
    InitialContext jndi = new InitialContext();
    if (dataSource == null) {
      String dbType = System.getProperty("database.type");
      if (dbType == null || dbType.equals("mysql")) {
        logger.info("+++++++++++++++++++++++++++++++ Setting up MySQL data source for testing +++++++++++++++++++++++++++++++");
        dataSource = MySQLTools.setup(jndi, databaseName);
        DatabaseType.database = Database.MYSQL;

        // This is required to tell Hibernate to use transactions
        databaseType = "mysql";
      } else if (dbType.equals("postgresql")) {
        logger.info("+++++++++++++++++++++++++++++++ Setting up PostgreSQL data source for testing +++++++++++++++++++++++++++++++");
        dataSource = PostgreSQLTools.setup(jndi, databaseName);
        DatabaseType.database = Database.POSTGRESQL;

        // This is required to tell Hibernate to use postgres to create the tables
        databaseType = "postgresql";
      } else {
        throw new RuntimeException("Invalid database type for testing [" + dbType + "]");
      }
    }

    return databaseType;
  }

  /**
   * Executes the given SQL statement via plain old JDBC. This will be committed to the database. This SQL statement
   * should be an insert, update or delete statement because it uses the executeUpdate method on Statement.
   *
   * @param sql The SQL to execute.
   * @throws java.sql.SQLException If the execute failed.
   */
  public static void executeSQL(String sql) throws SQLException {
    Connection c = dataSource.getConnection();
    Statement s = c.createStatement();
    s.executeUpdate(sql);
    s.close();
    c.close();
  }

  /**
   * Clears all the data from the given table using JDBC.
   *
   * @param table The Table to clear.
   * @throws java.sql.SQLException If the clear failed.
   */
  public static void clearTable(String table) throws SQLException {
    executeSQL("delete from " + table);
  }

  /**
   * Clears all the data from the table that the given Class is mapped to using JPA.
   *
   * @param klass The JPA class to get the table name from. This first checks if the Class has a JPA Table annotation
   *              and uses that. Otherwise, it uses the Class's simple name.
   * @throws java.sql.SQLException If the clear failed.
   */
  public static void clearTable(Class<?> klass) throws SQLException {
    Table table = klass.getAnnotation(Table.class);
    String tableName;
    if (table == null || table.name().equals("")) {
      tableName = klass.getSimpleName();
    } else {
      tableName = table.name();
    }

    clearTable(tableName);
  }

  /**
   * Runs the given query and returns the results in a detached RowSet.
   *
   * @param query The query to run.
   * @return The results of the query.
   * @throws java.sql.SQLException If the query failed.
   */
  public static RowSet executeQuery(String query) throws SQLException {
    Connection c = dataSource.getConnection();
    Statement s = c.createStatement();
    ResultSet rs = s.executeQuery(query);
    CachedRowSet rowSet = new CachedRowSetImpl();
    rowSet.populate(rs);
    rs.close();
    s.close();
    c.close();
    return rowSet;
  }

  /**
   * @return A connection.
   * @throws java.sql.SQLException on sql exception
   */
  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
