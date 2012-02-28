/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.guice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;

import org.jcatapult.persistence.service.jdbc.ConnectionProvider;
import org.jcatapult.persistence.service.jdbc.DataSourceProvider;
import org.jcatapult.persistence.service.jpa.EntityManagerFactoryProvider;
import org.jcatapult.persistence.service.jpa.EntityManagerProvider;
import org.jcatapult.persistence.txn.TransactionMethodInterceptor;
import org.jcatapult.persistence.txn.annotation.Transactional;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import static com.google.inject.matcher.Matchers.*;
import com.google.inject.name.Names;

/**
 * This module should be used for JPA or JDBC support. It sets up the injection for all of the JPA classes as well
 * as the JDBC classes.
 *
 * @author Brian Pontarelli
 */
public class PersistenceModule extends AbstractModule {
  private final boolean jpaEnabled;
  private final String jpaUnit;
  private final String dataSourceName;

  public PersistenceModule() {
    this.jpaEnabled = false;
    this.jpaUnit = null;
    this.dataSourceName = null;
  }

  public PersistenceModule(boolean jpaEnabled, String jpaUnit, String dataSourceName) {
    this.jpaEnabled = jpaEnabled;
    this.jpaUnit = jpaUnit;
    this.dataSourceName = dataSourceName;
  }

  /**
   * Configures everything.
   */
  @Override
  protected void configure() {
    configureJDBC();
    configureJPA();
    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    requestInjection(interceptor);
    bindInterceptor(any(), annotatedWith(Transactional.class), interceptor);
  }

  /**
   * Sets up injections for the {@link Connection} and {@link DataSource}.
   */
  protected void configureJDBC() {
    bind(DataSource.class).toProvider(DataSourceProvider.class).in(Singleton.class);
    bind(Connection.class).toProvider(ConnectionProvider.class);
    bindConstant().annotatedWith(Names.named("non-jta-data-source")).to(dataSourceName);
  }

  /**
   * Sets up injections for {@link EntityManager} and {@link EntityManagerFactory}.
   */
  protected void configureJPA() {
    bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class).in(Singleton.class);
    bind(EntityManager.class).toProvider(EntityManagerProvider.class);
    bindConstant().annotatedWith(Names.named("jpa.enabled")).to(jpaEnabled);
    bindConstant().annotatedWith(Names.named("jpa.unit")).to(jpaUnit);
  }
}
