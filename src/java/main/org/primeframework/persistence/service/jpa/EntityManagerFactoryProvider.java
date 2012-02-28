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
package org.primeframework.persistence.service.jpa;

import javax.persistence.EntityManagerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This class is a Guice provider that provider access to the EntityManagerFactory.
 *
 * @author Brian Pontarelli
 */
public class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {
  private final JPAService service;

  @Inject
  public EntityManagerFactoryProvider(JPAService service) {
    this.service = service;
  }

  public EntityManagerFactory get() {
    return service.getFactory();
  }
}
