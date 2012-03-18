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
package org.primeframework.servlet;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.primeframework.persistence.BaseJPATest;
import org.primeframework.persistence.service.jdbc.ConnectionContext;
import org.primeframework.persistence.service.jdbc.JDBCService;
import org.primeframework.persistence.service.jpa.EntityManagerContext;
import org.primeframework.persistence.service.jpa.JPAService;
import org.primeframework.persistence.servlet.PersistenceFilter;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * This class is the test case for the PersistenceFilter.
 *
 * @author Brian Pontarelli
 */
public class PersistenceFilterTest extends BaseJPATest {
  @Test
  public void runAndClose() throws ServletException, IOException {
    // Setup the thread locals
    JPAService jpaService = injector.getInstance(JPAService.class);
    assertNotNull(jpaService.setupEntityManager());
    JDBCService jdbcService = injector.getInstance(JDBCService.class);
    assertNotNull(jdbcService.setupConnection());

    assertNotNull(EntityManagerContext.get());
    assertNotNull(ConnectionContext.get());

    ServletContext context = createStrictMock(ServletContext.class);
    expect(context.getAttribute("foo")).andReturn(injector);
    replay(context);

    FilterConfig config = createStrictMock(FilterConfig.class);
    expect(config.getInitParameter("injector-key")).andReturn("foo");
    expect(config.getServletContext()).andReturn(context);
    replay(config);

    PersistenceFilter filter = new PersistenceFilter();
    filter.init(config);

    HttpServletRequest request = createStrictMock(HttpServletRequest.class);
    replay(request);

    HttpServletResponse response = createStrictMock(HttpServletResponse.class);
    replay(response);

    FilterChain chain = createStrictMock(FilterChain.class);
    chain.doFilter(request, response);
    replay(chain);

    filter.doFilter(request, response, chain);
    assertNull(EntityManagerContext.get());
    assertNull(ConnectionContext.get());

    verify(context, config, request, response, chain);
  }
}
