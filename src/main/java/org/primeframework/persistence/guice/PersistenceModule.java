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
package org.primeframework.persistence.guice;

import org.primeframework.persistence.service.guice.ServiceModule;
import org.primeframework.persistence.txn.guice.TransactionModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * This module should be used for JPA or JDBC support. It sets up the injection for all of the JPA classes as well as
 * the JDBC classes.
 *
 * @author Brian Pontarelli
 */
public class PersistenceModule extends AbstractModule {
  private final JDBCModule jdbcModule;
  private final Module jpaModule;

  /**
   * Creates a module for the Prime persistence system. If you pass in a JPAModule, JPA will be enabled. If you pass in
   * null, it will be disabled.
   *
   * @param jdbcModule The JDBC module.
   * @param jpaModule (Optional) The JPA module.
   */
  public PersistenceModule(JDBCModule jdbcModule, Module jpaModule) {
    this.jdbcModule = jdbcModule;
    this.jpaModule = jpaModule;
  }

  /**
   * Configures everything.
   */
  @Override
  protected void configure() {
    install(new ServiceModule());
    install(new TransactionModule());
    install(jdbcModule);

    if (jpaModule != null) {
      install(jpaModule);
    }
  }
}
