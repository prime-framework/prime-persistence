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

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This class is a provider for a JDBC connection. It uses the {@link JDBCService} injected by Guice to get the
 * connection from.
 *
 * @author Brian Pontarelli
 */
public class ConnectionProvider implements Provider<Connection> {
  private final JDBCService service;

  @Inject
  public ConnectionProvider(JDBCService service) {
    this.service = service;
  }

  @Override
  public Connection get() {
    return service.setupConnection();
  }
}
