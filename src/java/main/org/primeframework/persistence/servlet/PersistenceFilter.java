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
package org.primeframework.persistence.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import org.primeframework.persistence.service.jdbc.JDBCService;
import org.primeframework.persistence.service.jpa.JPAService;

import com.google.inject.Injector;

/**
 * This class is the Prime Persistence workflow that cleans up all the persistence resources after the request has
 * been completed. This closes the JDBC connection, releases the JPA entity manager, and removes the transaction context
 * (if one exists).
 *
 * @author Brian Pontarelli
 */
public class PersistenceFilter implements Filter {
  private Injector injector;

  @Override
  public void init(FilterConfig config) throws ServletException {
    String injectorKey = config.getInitParameter("injector-key");
    if (injectorKey == null) {
      throw new ServletException("You must configure the PersistenceFilter using a parameter named [injector-key] that " +
        "defines the key where the Guice Injector is stored inside the ServletContent.");
    }

    Object injector = config.getServletContext().getAttribute(injectorKey);
    if (injector == null) {
      throw new ServletException("The Guice Injector is not inside the ServletContext under the key [" + injectorKey + "]");
    }

    this.injector = (Injector) injector;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    chain.doFilter(request, response);

    JPAService jpaService = injector.getInstance(JPAService.class);
    JDBCService jdbcService = injector.getInstance(JDBCService.class);
    jdbcService.tearDownConnection();
    jpaService.tearDownEntityManager();
  }

  @Override
  public void destroy() {
  }
}
