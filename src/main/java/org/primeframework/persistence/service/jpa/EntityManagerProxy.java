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
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * Proxies the EntityManager interface so that it can be lazy-injected without checking out a connection or adding the
 * EntityManager to the transaction context until it is used.
 *
 * @author Brian Pontarelli
 */
public class EntityManagerProxy implements EntityManager {
  private final JPAService service;
  private EntityManager proxy;

  public EntityManagerProxy(JPAService service) {
    this.service = service;
  }

  @Override
  public void clear() {
    grabProxy();
    proxy.clear();
  }

  @Override
  public void persist(Object entity) {
    grabProxy();
    proxy.persist(entity);
  }

  @Override
  public <T> T merge(T entity) {
    grabProxy();
    return proxy.merge(entity);
  }

  @Override
  public void remove(Object entity) {
    grabProxy();
    proxy.remove(entity);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey) {
    grabProxy();
    return proxy.find(entityClass, primaryKey);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
    grabProxy();
    return proxy.find(entityClass, primaryKey, properties);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
    grabProxy();
    return proxy.find(entityClass, primaryKey, lockMode);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
    grabProxy();
    return proxy.find(entityClass, primaryKey, lockMode, properties);
  }

  @Override
  public <T> T getReference(Class<T> entityClass, Object primaryKey) {
    grabProxy();
    return proxy.getReference(entityClass, primaryKey);
  }

  @Override
  public void flush() {
    grabProxy();
    proxy.flush();
  }

  @Override
  public void setFlushMode(FlushModeType flushMode) {
    grabProxy();
    proxy.setFlushMode(flushMode);
  }

  @Override
  public FlushModeType getFlushMode() {
    grabProxy();
    return proxy.getFlushMode();
  }

  @Override
  public void lock(Object entity, LockModeType lockMode) {
    grabProxy();
    proxy.lock(entity, lockMode);
  }

  @Override
  public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    grabProxy();
    proxy.lock(entity, lockMode, properties);
  }

  @Override
  public void refresh(Object entity) {
    grabProxy();
    proxy.refresh(entity);
  }

  @Override
  public void refresh(Object entity, Map<String, Object> properties) {
    grabProxy();
    proxy.refresh(entity, properties);
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode) {
    grabProxy();
    proxy.refresh(entity, lockMode);
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    grabProxy();
    proxy.refresh(entity, lockMode, properties);
  }

  @Override
  public void detach(Object entity) {
    grabProxy();
    proxy.detach(entity);
  }

  @Override
  public boolean contains(Object entity) {
    grabProxy();
    return proxy.contains(entity);
  }

  @Override
  public LockModeType getLockMode(Object entity) {
    grabProxy();
    return proxy.getLockMode(entity);
  }

  @Override
  public void setProperty(String propertyName, Object value) {
    grabProxy();
    proxy.setProperty(propertyName, value);
  }

  @Override
  public Map<String, Object> getProperties() {
    grabProxy();
    return proxy.getProperties();
  }

  @Override
  public Query createQuery(String qlString) {
    grabProxy();
    return proxy.createQuery(qlString);
  }

  @Override
  public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
    grabProxy();
    return proxy.createQuery(criteriaQuery);
  }

  @Override
  public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
    grabProxy();
    return proxy.createQuery(qlString, resultClass);
  }

  @Override
  public Query createNamedQuery(String name) {
    grabProxy();
    return proxy.createNamedQuery(name);
  }

  @Override
  public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
    grabProxy();
    return proxy.createNamedQuery(name, resultClass);
  }

  @Override
  public Query createNativeQuery(String sqlString) {
    grabProxy();
    return proxy.createNativeQuery(sqlString);
  }

  @Override
  public Query createNativeQuery(String sqlString, Class resultClass) {
    grabProxy();
    return proxy.createNativeQuery(sqlString, resultClass);
  }

  @Override
  public Query createNativeQuery(String sqlString, String resultSetMapping) {
    grabProxy();
    return proxy.createNativeQuery(sqlString, resultSetMapping);
  }

  @Override
  public void joinTransaction() {
    grabProxy();
    proxy.joinTransaction();
  }

  @Override
  public <T> T unwrap(Class<T> cls) {
    grabProxy();
    return proxy.unwrap(cls);
  }

  @Override
  public Object getDelegate() {
    grabProxy();
    return proxy.getDelegate();
  }

  @Override
  public void close() {
    grabProxy();
    proxy.close();
  }

  @Override
  public boolean isOpen() {
    grabProxy();
    return proxy.isOpen();
  }

  @Override
  public EntityTransaction getTransaction() {
    grabProxy();
    return proxy.getTransaction();
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    grabProxy();
    return proxy.getEntityManagerFactory();
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder() {
    grabProxy();
    return proxy.getCriteriaBuilder();
  }

  @Override
  public Metamodel getMetamodel() {
    grabProxy();
    return proxy.getMetamodel();
  }

  private void grabProxy() {
    if (proxy == null) {
      proxy = service.setupEntityManager();
    }
  }
}
