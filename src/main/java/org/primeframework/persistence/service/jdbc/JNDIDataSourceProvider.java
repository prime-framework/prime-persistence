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
package org.primeframework.persistence.service.jdbc;

import javax.naming.InitialContext;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.inject.Provider;

/**
 * This class is a provider for a JDBC DataSource and uses JNDI to retrieve it. This is not bound in the Guice module by
 * default, but you can use it if you are using JNDI.
 *
 * @author Brian Pontarelli
 */
public class JNDIDataSourceProvider implements Provider<DataSource> {
  private final String jndiName;

  public JNDIDataSourceProvider(String jndiName) {
    this.jndiName = jndiName;
  }

  @Override
  public DataSource get() {
    try {
      InitialContext context = new InitialContext();
      NameParser parser = context.getNameParser("");
      return (DataSource) context.lookup(parser.parse(jndiName));
    } catch (NamingException ne) {
      throw new IllegalStateException("JNDI tree does not contain a DataSource under the name [" + jndiName + "]");
    }
  }
}
