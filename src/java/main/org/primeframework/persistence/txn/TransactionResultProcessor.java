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
 *
 */
package org.primeframework.persistence.txn;

/**
 * This interface can be implemented and passed to the {@link org.primeframework.persistence.txn.annotation.Transactional}
 * annotation. This is useful for determining based on an exception that is thrown or the return value of a method if
 * the transaction should be rolled back.
 *
 * @author Brian Pontarelli
 */
public interface TransactionResultProcessor<T> {
  /**
   * Determines if the transaction should be rolled back based on the return value  of a method invocation or the
   * exception that the method threw.
   *
   * @param result    The return value of a method. This could be null of void methods.
   * @param throwable The exception thrown from the method. This will be null if the method invoked didn't throw an
   *                  exception.
   * @return True if the transaction should be rolled back, false otherwise.
   */
  boolean rollback(T result, Throwable throwable);
}