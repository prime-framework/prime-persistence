/*
 * Copyright (c) 2001-2010, Inversoft Inc., All Rights Reserved
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
package org.primeframework.persistence.txn;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.primeframework.persistence.txn.annotation.Transactional;

import com.google.inject.Inject;

/**
 * This is the AOP method interceptor that provides transaction handling for methods. This transaction handling is
 * generic such that any database connectivity can be used. This includes JDBC, JPA, etc.
 *
 * @author Brian Pontarelli
 */
public class TransactionMethodInterceptor implements MethodInterceptor {
  private TransactionContextManager manager;

  @Inject
  public void setTransactionMethodInterceptor(TransactionContextManager manager) {
    this.manager = manager;
  }

  /**
   * Intercepts method invocations that have been tagged with the {@link Transactional} annotation. This uses the {@link
   * TransactionContextManager} to get the current {@link TransactionContext}. The {@link TransactionContext} is then
   * used to determine if the transaction is new or embedded. This then handles the starting, committing and rolling
   * back of the transaction based on the result of the method invocation and if the transaction is embedded or not.
   *
   * @param methodInvocation The method invocation.
   * @return The result of the method invocation.
   * @throws Throwable Any exception that the method throws.
   */
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    TransactionResultProcessor processor = processor(methodInvocation);
    TransactionContext txnContext = manager.getCurrent();

    // Check if a new transaction needs to be created
    if (txnContext == null) {
      txnContext = manager.start();
    }

    boolean embedded = txnContext.isStarted();
    if (!embedded) {
      txnContext.start();
    }

    Object result = null;
    Throwable t = null;
    try {
      result = methodInvocation.proceed();
    } catch (Throwable throwable) {
      t = throwable;
      throw throwable;
    } finally {
      // Check if this is the outer call and close the transaction if it is
      if (!embedded) {
        manager.tearDownTransactionContext();
      }

      endTransactions(txnContext, processor, result, t, embedded);
    }

    return result;
  }

  /**
   * Called by the invoke method to get the TransactionResultProcessor from the method invocation.
   *
   * @param methodInvocation The method invocation.
   * @return The result processor.
   * @throws Throwable If anything failed.
   */
  protected TransactionResultProcessor processor(MethodInvocation methodInvocation) throws Throwable {
    Transactional annotation = methodInvocation.getMethod().getAnnotation(Transactional.class);
    Class<? extends TransactionResultProcessor> processorClass = annotation.processor();
    return processorClass.newInstance();
  }

  /**
   * Ends the transaction.
   *
   * @param txnContext The current transaction context.
   * @param processor  The result processor.
   * @param result     The result.
   * @param t          The thrown exception.
   * @param embedded   True if the transaction is embedded or false if it was started by this method invocation.
   * @throws Throwable If ending any of the transactions failed.
   */
  @SuppressWarnings("unchecked")
  private void endTransactions(TransactionContext txnContext, TransactionResultProcessor processor, Object result,
                               Throwable t, boolean embedded)
    throws Throwable {
    boolean rollback = processor.rollback(result, t);
    if (!embedded) {
      if (txnContext.isRollbackOnly() || rollback) {
        txnContext.rollback();
      } else {
        txnContext.commit();
      }
    } else if (rollback) {
      txnContext.setRollbackOnly();
    }
  }
}
