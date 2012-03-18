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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;

import org.primeframework.persistence.service.jdbc.ConnectionProvider;
import org.primeframework.persistence.service.jpa.EntityManagerFactoryProvider;
import org.primeframework.persistence.service.jpa.EntityManagerProvider;
import org.primeframework.persistence.txn.TransactionMethodInterceptor;
import org.primeframework.persistence.txn.annotation.Transactional;

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
public abstract class PersistenceModule extends AbstractModule {
  private final boolean jpaEnabled;
  private final String jpaUnit;

  public PersistenceModule(boolean jpaEnabled, String jpaUnit) {
    this.jpaEnabled = jpaEnabled;
    this.jpaUnit = jpaUnit;
  }

  /**
   * Configures everything.
   */
  @Override
  protected void configure() {
    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    requestInjection(interceptor);
    bindInterceptor(any(), annotatedWith(Transactional.class), interceptor);

    configureJDBC();

    if (jpaEnabled) {
      configureJPA();
    }
  }

  /**
   * Sets up injections for the {@link Connection} and {@link DataSource}.
   */
  protected void configureJDBC() {
    bind(Connection.class).toProvider(ConnectionProvider.class);
    bindDataSource();
  }

  /**
   * Must be implemented to setup the DataSource.
   */
  protected abstract void bindDataSource();

  /**
   * Sets up injections for {@link EntityManager} and {@link EntityManagerFactory}.
   */
  protected void configureJPA() {
    bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class).in(Singleton.class);
    bind(EntityManager.class).toProvider(EntityManagerProvider.class);
    bindConstant().annotatedWith(Names.named("jpa.unit")).to(jpaUnit);
  }
}
