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
 *
 */
package org.jcatapult.persistence.service.jpa;

import javax.persistence.Persistence;
import java.util.logging.Logger;

import org.jcatapult.persistence.txn.TransactionContextManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * This class implements the JPA service. It is a singleton and in the constructor it sets up the EntityManagerFactory.
 * <p/>
 * This class is a singleton since it constructs the EntityManagerFactory in the constructor and holds a reference to
 * it.
 *
 * @author Brian Pontarelli
 */
@Singleton
public class DefaultJPAService extends AbstractJPAService {
  private static final Logger logger = Logger.getLogger(DefaultJPAService.class.getName());

  /**
   * @param txnContextManager The TransactionContextManager.
   * @param jpaEnabled        If true, JPA will be setup, false it will not. A boolean flag controlled by the injection
   *                          property named <strong>jpa.enabled</strong> that controls whether or not JPA will be
   *                          initialized and then setup during each request.
   * @param persistenceUnit   The name of the JPA persistence unit to use if JPA is being setup. This is controlled by
   *                          the injection property named <strong>jpa.unit</strong>.
   */
  @Inject
  public DefaultJPAService(TransactionContextManager txnContextManager, @Named("jpa.enabled") boolean jpaEnabled,
                           @Named("jpa.unit") String persistenceUnit) {
    super(txnContextManager);
    if (jpaEnabled) {
      logger.fine("JPA is enabled");
      emf = Persistence.createEntityManagerFactory(persistenceUnit);
    } else {
      logger.fine("JPA is disabled");
    }
  }
}
