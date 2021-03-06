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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.primeframework.persistence.service.jpa.EntityManagerFactoryProvider;
import org.primeframework.persistence.service.jpa.EntityManagerProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Binds the JPA classes (EntityManagerFactory, EntityManager, etc). Also, binds the PUnit as a constant name under the
 * name <code>jpa.unit</code>.
 *
 * @author Brian Pontarelli
 */
public class JPAModule extends AbstractModule {
  private final String jpaUnit;

  public JPAModule(String jpaUnit) {
    this.jpaUnit = jpaUnit;
  }

  @Override
  protected void configure() {
    bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class).in(Singleton.class);
    bind(EntityManager.class).toProvider(EntityManagerProvider.class);
    bindConstant().annotatedWith(Names.named("jpa.unit")).to(jpaUnit);
  }
}
