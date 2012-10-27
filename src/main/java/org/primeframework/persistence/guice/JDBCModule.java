/*
 * Copyright (c) 2001-2012, JCatapult.org, All Rights Reserved
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
package org.primeframework.persistence.guice;

import java.sql.Connection;

import org.primeframework.persistence.service.jdbc.ConnectionProvider;

import com.google.inject.AbstractModule;

/**
 * Binds the JDBC classes and allows sub-classes to provide the DataSource.
 *
 * @author Brian Pontarelli
 */
public abstract class JDBCModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Connection.class).toProvider(ConnectionProvider.class);
    bindDataSource();
  }

  /**
   * Must be implemented to setup the DataSource.
   */
  protected abstract void bindDataSource();
}
