/*
 * Copyright (c) 2001-2011, Inversoft Inc., All Rights Reserved
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
package org.primeframework.persistence.jdbc;

/**
 * This is a generic runtime exception that is thrown from the {@link Select}, {@link Insert} and {@link Execute}
 * classes.
 *
 * @author Brian Pontarelli
 */
public class JDBCException extends RuntimeException {
  public JDBCException() {
  }

  public JDBCException(String message) {
    super(message);
  }

  public JDBCException(String message, Throwable cause) {
    super(message, cause);
  }

  public JDBCException(Throwable cause) {
    super(cause);
  }
}
