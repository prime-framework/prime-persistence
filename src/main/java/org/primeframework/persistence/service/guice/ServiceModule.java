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
package org.primeframework.persistence.service.guice;

import org.primeframework.persistence.service.jdbc.DefaultJDBCService;
import org.primeframework.persistence.service.jdbc.JDBCService;
import org.primeframework.persistence.service.jpa.DriverAwareJPAService;
import org.primeframework.persistence.service.jpa.JPAPersistenceService;
import org.primeframework.persistence.service.jpa.JPAService;
import org.primeframework.persistence.service.jpa.PersistenceService;

import com.google.inject.AbstractModule;

/**
 * Binds persistence services.
 *
 * @author Brian Pontarelli
 */
public class ServiceModule extends AbstractModule {
  @Override
  protected void configure() {
    bindJDBCService();
    bindJPAService();
    bindPersistenceService();
  }

  protected void bindJDBCService() {
    bind(JDBCService.class).to(DefaultJDBCService.class);
  }

  protected void bindJPAService() {
    bind(JPAService.class).to(DriverAwareJPAService.class);
  }

  protected void bindPersistenceService() {
    bind(PersistenceService.class).to(JPAPersistenceService.class);
  }
}
