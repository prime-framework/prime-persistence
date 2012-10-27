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
package org.primeframework.persistence.txn.guice;

import org.primeframework.persistence.txn.DefaultTransactionContextManager;
import org.primeframework.persistence.txn.TransactionContextManager;
import org.primeframework.persistence.txn.TransactionMethodInterceptor;
import org.primeframework.persistence.txn.annotation.Transactional;

import com.google.inject.AbstractModule;
import static com.google.inject.matcher.Matchers.*;

/**
 * Binds transaction classes.
 *
 * @author Brian Pontarelli
 */
public class TransactionModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TransactionContextManager.class).to(DefaultTransactionContextManager.class);

    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    requestInjection(interceptor);
    bindInterceptor(any(), annotatedWith(Transactional.class), interceptor);
  }
}
