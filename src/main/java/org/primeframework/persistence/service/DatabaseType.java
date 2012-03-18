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
package org.primeframework.persistence.service;

/**
 * This class stores the current database type that the Prime Persistence library is accessing.
 *
 * @author Brian Pontarelli
 */
public class DatabaseType {
  public static enum Database {
    POSTGRESQL,
    MYSQL
  }

  public static Database database;

  /**
   * Sets the database static reference from a system property (upper cased).
   *
   * @param propertyName The property name.
   */
  public static void setFromSystemProperty(String propertyName) {
    database = Database.valueOf(System.getProperty(propertyName).toUpperCase());
  }
}
