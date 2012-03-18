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
package org.primeframework.persistence.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

import org.primeframework.persistence.domain.Identifiable;
import org.primeframework.persistence.domain.SoftDeletable;
import org.primeframework.persistence.service.Transaction;

import com.google.inject.Inject;

/**
 * This class is the default implementation of the PersistenceService and provides default JPA behavior. It requires
 * that the {@link EntityManagerContext} be constructed correctly using the filter as described in that classes comments
 * (click on the link and read up on how to setup the filter).
 *
 * @author Brian Pontarelli
 */
public class JPAPersistenceService implements PersistenceService {
  private EntityManager entityManager;

  /**
   * Constructs a new JPAPersistenceService that uses the given EntityManager to communicate with the database.
   *
   * @param entityManager The entity manager to use.
   */
  @Inject
  public JPAPersistenceService(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * {@inheritDoc}
   */
  public void clearCache() {
    entityManager.clear();
  }

  /**
   * {@inheritDoc}
   */
  public void reload(Object obj) {
    entityManager.refresh(obj);
  }

  /**
   * {@inheritDoc}
   */
  public boolean contains(Object obj) {
    return entityManager.contains(obj);
  }

  /**
   * Starts a new {@link EntityTransaction} if the current {@link EntityManager} doesn't already have an active
   * transaction. If it does have an active transaction, then it still creates a transaction but it is a proxy that will
   * ignore commit and rollback calls since it is effectively an outer transaction.
   *
   * @return The transaction.
   */
  public Transaction startTransaction() {
    return startTransaction(entityManager);
  }

  private Transaction startTransaction(EntityManager em) {
    final EntityTransaction transaction = em.getTransaction();
    final boolean local = !transaction.isActive();
    if (local) {
      transaction.begin();
    }

    return new Transaction() {
      public void commit() {
        if (local) {
          transaction.commit();
        }
      }

      public void rollback() {
        if (local) {
          transaction.rollback();
        }
      }

      public void setRollbackOnly() {
        transaction.setRollbackOnly();
      }

      public boolean getRollbackOnly() {
        return transaction.getRollbackOnly();
      }

      public boolean isActive() {
        return transaction.isActive();
      }
    };
  }

  /**
   * Locks the object using the JPA {@link EntityManager#lock(Object, LockModeType)} method on the entity manager.
   *
   * @param obj  The object to lock.
   * @param read The read flag to determine the lock mode.
   */
  public void lock(Object obj, boolean read) {
    if (read) {
      entityManager.lock(obj, LockModeType.READ);
    } else {
      entityManager.lock(obj, LockModeType.WRITE);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> findAllByType(Class<T> type) {
    return findAllByTypeInternal(type, true);
  }

  /**
   * {@inheritDoc}
   */
  public <T extends SoftDeletable> List<T> findAllByType(Class<T> type, boolean includeDeleted) {
    return findAllByTypeInternal(type, includeDeleted);
  }

  /**
   * This is the internal method that performs the find but also determines if the Object is a {@link
   * org.primeframework.persistence.domain.SoftDeletable} and appends "where eb.deleted = false" if the includeDeleted
   * is false.
   *
   * @param type           The type to find.
   * @param includeDeleted Determines if this should return all the instances of the Object including those instances
   *                       that are marked as deleted and are {@link org.primeframework.persistence.domain.SoftDeletable}
   *                       objects.
   * @return The list.
   */
  protected <T> List<T> findAllByTypeInternal(Class<T> type, boolean includeDeleted) {
    StringBuilder queryString = new StringBuilder("select eb from ").append(stripPackage(type)).append(" eb");
    if (SoftDeletable.class.isAssignableFrom(type) && !includeDeleted) {
      queryString.append(" where eb.deleted = false");
    }

    TypedQuery<T> q = entityManager.createQuery(queryString.toString(), type);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> long count(Class<T> type) {
    return countInternal(type, true);
  }

  /**
   * {@inheritDoc}
   */
  public <T extends SoftDeletable> long count(Class<T> type, boolean includeDeleted) {
    return countInternal(type, includeDeleted);
  }

  /**
   * This is the internal method that performs the count but also determines if the Object is a {@link
   * org.primeframework.persistence.domain.SoftDeletable} and appends "where eb.deleted = false" if the includeDeleted
   * is false.
   *
   * @param type           The type to count.
   * @param includeDeleted Determines if this should count all the instances of the Object including those instances
   *                       that are marked as deleted and are {@link org.primeframework.persistence.domain.SoftDeletable}
   *                       objects.
   * @return The count.
   */
  protected <T> long countInternal(Class<T> type, boolean includeDeleted) {
    StringBuilder queryString = new StringBuilder("select count(eb) from ").append(stripPackage(type)).append(" eb");
    if (SoftDeletable.class.isAssignableFrom(type) && !includeDeleted) {
      queryString.append(" where eb.deleted = false");
    }

    TypedQuery<Long> q = entityManager.createQuery(queryString.toString(), Long.class);
    return q.getSingleResult();
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> findByType(Class<T> type, int start, int number) {
    return findByTypeInternal(type, start, number, true);
  }

  /**
   * {@inheritDoc}
   */
  public <T extends SoftDeletable> List<T> findByType(Class<T> type, int start, int number, boolean includeInactive) {
    return findByTypeInternal(type, start, number, includeInactive);
  }

  /**
   * This is the internal method that handles finding by type. If the type is {@link
   * org.primeframework.persistence.domain.SoftDeletable} this and the includeInactive flag is false method also appends
   * "where eb.active = true" to the query.
   *
   * @param type           The type to find.
   * @param start          The start location within the results for pagination.
   * @param number         The number to fetch.
   * @param includeDeleted Determines if this should return all the instances of the Object including those instances
   *                       that are marked as deleted and are {@link org.primeframework.persistence.domain.SoftDeletable}
   *                       objects.
   * @return The list of objects found.
   */
  protected <T> List<T> findByTypeInternal(Class<T> type, int start, int number, boolean includeDeleted) {
    StringBuilder queryString = new StringBuilder("select eb from ").append(stripPackage(type)).append(" eb");
    if (SoftDeletable.class.isAssignableFrom(type) && !includeDeleted) {
      queryString.append(" where eb.deleted = false");
    }

    TypedQuery<T> q = entityManager.createQuery(queryString.toString(), type);
    q.setFirstResult(start);
    q.setMaxResults(number);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> queryAll(Class<T> type, String query, Object... params) {
    TypedQuery<T> q = entityManager.createQuery(query, type);
    addParams(q, params);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> query(Class<T> type, String query, int start, int number, Object... params) {
    TypedQuery<T> q = entityManager.createQuery(query, type);
    addParams(q, params);
    q.setFirstResult(start);
    q.setMaxResults(number);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> T queryFirst(Class<T> type, String query, Object... params) {
    TypedQuery<T> q = entityManager.createQuery(query, type);
    q.setFirstResult(0);
    q.setMaxResults(1);
    addParams(q, params);
    List<T> results = q.getResultList();
    if (results.size() > 0) {
      return results.get(0);
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  public long queryCount(String query, Object... params) {
    TypedQuery<Long> q = entityManager.createQuery(query, Long.class);
    addParams(q, params);
    return q.getSingleResult();
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> queryAllWithNamedParameters(Class<T> type, String query, Map<String, Object> params) {
    TypedQuery<T> q = entityManager.createQuery(query, type);
    addNamedParams(q, params);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> queryWithNamedParameters(Class<T> type, String query, int start, int number, Map<String, Object> params) {
    TypedQuery<T> q = entityManager.createQuery(query, type);
    addNamedParams(q, params);
    q.setFirstResult(start);
    q.setMaxResults(number);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> T queryFirstWithNamedParameters(Class<T> type, String query, Map<String, Object> params) {
    TypedQuery<T> q = entityManager.createQuery(query, type);
    q.setFirstResult(0);
    q.setMaxResults(1);
    addNamedParams(q, params);
    List<T> results = q.getResultList();
    if (results.size() > 0) {
      return results.get(0);
    }

    return null;
  }

  public long queryCountWithNamedParameters(String query, Map<String, Object> params) {
    Query q = entityManager.createQuery(query);
    addNamedParams(q, params);
    return (Long) q.getSingleResult();
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> namedQueryAll(Class<T> type, String query, Object... params) {
    TypedQuery<T> q = entityManager.createNamedQuery(query, type);
    addParams(q, params);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> List<T> namedQuery(Class<T> type, String query, int start, int number, Object... params) {
    TypedQuery<T> q = entityManager.createNamedQuery(query, type);
    addParams(q, params);
    q.setFirstResult(start);
    q.setMaxResults(number);
    return q.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  public <T> T namedQueryFirst(Class<T> type, String query, Object... params) {
    TypedQuery<T> q = entityManager.createNamedQuery(query, type);
    addParams(q, params);
    List<T> results = q.getResultList();
    if (results.size() > 0) {
      return results.get(0);
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  public <T extends Identifiable> T findById(Class<T> type, int id) {
    T t = null;
    try {
      t = entityManager.find(type, id);
    } catch (EntityNotFoundException enfe) {
      // This is okay, just return null
    }

    return t;
  }

  /**
   * {@inheritDoc}
   */
  public <T> T findById(Class<T> type, Object id) {
    T t = null;
    try {
      t = entityManager.find(type, id);
    } catch (EntityNotFoundException enfe) {
      // This is okay, just return null
    }

    return t;
  }

  /**
   * {@inheritDoc}
   */
  public void persist(Identifiable obj) {
    if (entityManager.contains(obj) || obj.getId() == null) {
      persist((Object) obj);
    } else {
      merge(obj);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void persist(Object obj) {
    // Check for and possibly start a transaction
    Transaction transaction = startTransaction(entityManager);
    boolean exception = false;
    try {
      entityManager.persist(obj);
    } catch (PersistenceException pe) {
      exception = true;
      throw pe;
    } finally {
      if (!exception && !transaction.getRollbackOnly()) {
        transaction.commit();
      } else {
        transaction.rollback();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void merge(Object obj) {
    // Check for and possibly start a transaction
    Transaction transaction = startTransaction(entityManager);
    boolean exception = false;
    try {
      entityManager.merge(obj);
    } catch (PersistenceException pe) {
      exception = true;
      throw pe;
    } finally {
      if (!exception && !transaction.getRollbackOnly()) {
        transaction.commit();
      } else {
        transaction.rollback();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void flush() {
    EntityTransaction transaction = entityManager.getTransaction();
    if (transaction.isActive()) {
      entityManager.flush();
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T extends Identifiable> boolean delete(Class<T> type, int id) {
    T t = findById(type, id);
    if (t != null) {
      // Check for and possibly start a transaction
      Transaction transaction = startTransaction(entityManager);

      // Remove it for normal entities and soft delete for others
      if (t instanceof SoftDeletable) {
        ((SoftDeletable) t).setDeleted(true);
        entityManager.persist(t);
      } else {
        entityManager.remove(t);
      }

      // No need for a try-catch block because a call to commit that fails will rollback the
      // transaction automatically for us.
      transaction.commit();

      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  public <T> boolean delete(Class<T> type, Object id) {
    T t = findById(type, id);
    if (t != null) {
      // Check for and possibly start a transaction
      Transaction transaction = startTransaction(entityManager);

      // Remove it for normal entities and soft delete for others
      if (t instanceof SoftDeletable) {
        ((SoftDeletable) t).setDeleted(true);
        entityManager.persist(t);
      } else {
        entityManager.remove(t);
      }

      // No need for a try-catch block because a call to commit that fails will rollback the
      // transaction automatically for us.
      transaction.commit();

      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  public <T> boolean forceDelete(Class<T> type, Integer id) {
    T t = findById(type, id);
    if (t != null) {
      // Check for and possibly start a transaction
      Transaction transaction = startTransaction(entityManager);

      // Remove it
      entityManager.remove(t);

      // No need for a try-catch block because a call to commit that fails will rollback the
      // transaction automatically for us.
      transaction.commit();

      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  public void delete(Object obj) {
    // Check for and possibly start a transaction
    Transaction transaction = startTransaction(entityManager);

    // Remove it for normal entities and soft delete for others
    if (obj instanceof SoftDeletable) {
      ((SoftDeletable) obj).setDeleted(true);
      entityManager.persist(obj);
    } else {
      entityManager.remove(obj);
    }

    // No need for a try-catch block because a call to commit that fails will rollback the
    // transaction automatically for us.
    transaction.commit();
  }

  /**
   * {@inheritDoc}
   */
  public void forceDelete(Object obj) {
    // Check for and possibly start a transaction
    Transaction transaction = startTransaction(entityManager);

    // Remove it
    entityManager.remove(obj);

    // No need for a try-catch block because a call to commit that fails will rollback the
    // transaction automatically for us.
    transaction.commit();
  }

  /**
   * {@inheritDoc}
   */
  public int execute(String statement, Object... params) {
    // Check for and possibly start a transaction
    Transaction transaction = startTransaction(entityManager);

    // Create the update and execute it
    Query query = entityManager.createQuery(statement);
    addParams(query, params);
    int results = query.executeUpdate();

    // No need for a try-catch block because a call to commit that fails will rollback the
    // transaction automatically for us.
    transaction.commit();

    return results;
  }

  /**
   * {@inheritDoc}
   */
  public int executeWithNamedParameters(String statement, Map<String, Object> params) {
    // Check for and possibly start a transaction
    Transaction transaction = startTransaction(entityManager);

    // Create the update and execute it
    Query query = entityManager.createQuery(statement);
    addNamedParams(query, params);
    int results = query.executeUpdate();

    // No need for a try-catch block because a call to commit that fails will rollback the
    // transaction automatically for us.
    transaction.commit();

    return results;
  }

  /**
   * {@inheritDoc}
   */
  public EntityManager getEntityManager() {
    return entityManager;
  }

  /**
   * Strips the package name to create a JPA persistable name that can be used in EJB-QL. For example:
   * <p/>
   * <table> <tr><th>Class</th><th>Return</th></tr> <tr><td>org.primeframework.project.domain.Photo</td><td>Photo</td>
   * <tr><td>org.primeframework.project.Foo</td><td>Foo</td> <tr><td>NoPackageClass</td><td>NoPackageClass</td>
   * </table>
   *
   * @param type The class to strip the package name from the fully qualified class name.
   * @return The name.
   */
  protected String stripPackage(Class<?> type) {
    String className = type.getName();
    int index = className.lastIndexOf(".");
    if (index > 0) {
      return className.substring(index + 1);
    }

    return className;
  }

  /**
   * Sets the given parameters to the query. These are positional parameters not named parameters.
   *
   * @param q      The Query to set the parameters into.
   * @param params The parameters to set as positional parameters into the query.
   */
  protected void addParams(Query q, Object... params) {
    if (params.length > 0) {
      int count = 1;
      for (Object param : params) {
        q.setParameter(count++, param);
      }
    }
  }

  /**
   * Sets the given parameters to the query. These are named parameters not positional parameters.
   *
   * @param q      The Query to set the parameters into.
   * @param params The parameters to set as named parameters into the query.
   */
  protected void addNamedParams(Query q, Map<String, Object> params) {
    for (String name : params.keySet()) {
      q.setParameter(name, params.get(name));
    }
  }
}
