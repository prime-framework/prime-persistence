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
package org.primeframework.persistence.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaQuery;

import java.util.Map;

import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;

/**
 * Tests the proxy to ensure it does the right pass-through for each method and to ensure it only calls the service
 * once.
 *
 * @author Brian Pontarelli
 */
public class EntityManagerProxyTest {
  @Test
  public void proxy() {
    EntityManager em = createStrictMock(EntityManager.class);
    em.clear();
    em.close();
    expect(em.contains(null)).andReturn(false);
    expect(em.createNamedQuery("foo")).andReturn(null);
    expect(em.createNamedQuery("foo", null)).andReturn(null);
    expect(em.createNativeQuery("foo")).andReturn(null);
    expect(em.createNativeQuery("foo", (Class) null)).andReturn(null);
    expect(em.createNativeQuery("foo", "foo")).andReturn(null);
    expect(em.createQuery("foo")).andReturn(null);
    expect(em.createQuery((CriteriaQuery) null)).andReturn(null);
    expect(em.createQuery("foo", null)).andReturn(null);
    em.detach(null);
    expect(em.find(null, null)).andReturn(null);
    expect(em.find(null, null, (Map) null)).andReturn(null);
    expect(em.find(null, null, (LockModeType) null)).andReturn(null);
    expect(em.find(null, null, null, null)).andReturn(null);
    em.flush();
    expect(em.getCriteriaBuilder()).andReturn(null);
    expect(em.getDelegate()).andReturn(null);
    expect(em.getEntityManagerFactory()).andReturn(null);
    expect(em.getFlushMode()).andReturn(null);
    expect(em.getLockMode(null)).andReturn(null);
    expect(em.getMetamodel()).andReturn(null);
    expect(em.getProperties()).andReturn(null);
    expect(em.getReference(null, null)).andReturn(null);
    expect(em.getTransaction()).andReturn(null);
    expect(em.isOpen()).andReturn(false);
    em.joinTransaction();
    em.lock(null, null);
    em.lock(null, null, null);
    expect(em.merge(null)).andReturn(null);
    em.persist(null);
    em.refresh(null);
    em.refresh(null, (Map) null);
    em.refresh(null, (LockModeType) null);
    em.refresh(null, null, null);
    em.remove(null);
    em.setFlushMode(null);
    em.setProperty("foo", null);
    expect(em.unwrap(null)).andReturn(null);
    replay(em);

    JPAService service = createStrictMock(JPAService.class);
    expect(service.setupEntityManager()).andReturn(em);
    replay(service);

    EntityManagerProxy proxy = new EntityManagerProxy(service);
    em.clear();
    em.close();
    assertEquals(proxy.contains(null), false);
    assertEquals(proxy.createNamedQuery("foo"), null);
    assertEquals(proxy.createNamedQuery("foo", null), null);
    assertEquals(proxy.createNativeQuery("foo"), null);
    assertEquals(proxy.createNativeQuery("foo", (Class) null), null);
    assertEquals(proxy.createNativeQuery("foo", "foo"), null);
    assertEquals(proxy.createQuery("foo"), null);
    assertEquals(proxy.createQuery((CriteriaQuery) null), null);
    assertEquals(proxy.createQuery("foo", null), null);
    em.detach(null);
    assertEquals(proxy.find(null, null), null);
    assertEquals(proxy.find(null, null, (Map) null), null);
    assertEquals(proxy.find(null, null, (LockModeType) null), null);
    assertEquals(proxy.find(null, null, null, null), null);
    em.flush();
    assertEquals(proxy.getCriteriaBuilder(), null);
    assertEquals(proxy.getDelegate(), null);
    assertEquals(proxy.getEntityManagerFactory(), null);
    assertEquals(proxy.getFlushMode(), null);
    assertEquals(proxy.getLockMode(null), null);
    assertEquals(proxy.getMetamodel(), null);
    assertEquals(proxy.getProperties(), null);
    assertEquals(proxy.getReference(null, null), null);
    assertEquals(proxy.getTransaction(), null);
    assertEquals(proxy.isOpen(), false);
    em.joinTransaction();
    em.lock(null, null);
    em.lock(null, null, null);
    assertEquals(proxy.merge(null), null);
    em.persist(null);
    em.refresh(null);
    em.refresh(null, (Map) null);
    em.refresh(null, (LockModeType) null);
    em.refresh(null, null, null);
    em.remove(null);
    em.setFlushMode(null);
    em.setProperty("foo", null);
    assertEquals(proxy.unwrap(null), null);
  }
}
