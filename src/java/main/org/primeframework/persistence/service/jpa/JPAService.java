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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.inject.ImplementedBy;

/**
 * This interface is used to configure, start and handle JPA interactions. The main function of this class is to create
 * the EntityManagerFactory and provide access to it.
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultJPAService.class)
public interface JPAService {
  /**
   * @return The EntityManagerFactory if JPA is enabled and correctly configured. This will return null if JPA is
   *         disabled.
   */
  EntityManagerFactory getFactory();

  /**
   * This method handles the EntityManager. If there is an EntityManager in the context already, it is returned.
   * Otherwise, this will create a new EntityManager and set it into the context storage so that it can be accessed
   * elsewhere in the application.
   *
   * @return The EntityManager
   */
  EntityManager setupEntityManager();

  /**
   * This method removes the EntityManager from the ThreadLocal storage and cleans things up.
   */
  void tearDownEntityManager();
}
