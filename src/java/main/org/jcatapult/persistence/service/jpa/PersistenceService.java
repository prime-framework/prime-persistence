/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.service.jpa;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.domain.SoftDeletable;
import org.jcatapult.persistence.service.Transaction;

import com.google.inject.ImplementedBy;

/**
 * This interface defines a basic set of persistence methods for objects. This interface is almost entirely JPA
 * specific, but it is nice to have an interface for testing.
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(JPAPersistenceService.class)
public interface PersistenceService {
  /**
   * This is mostly a hack to get around Hibernate's sometimes aggravating caching. This clears any persistence cache
   * that the implementation behind this interface might have. This forces any objects that are fetched to come directly
   * from the database.
   */
  void clearCache();

  /**
   * This forces the given Object to be reloaded from the database.
   *
   * @param obj The object to reload.
   */
  void reload(Object obj);

  /**
   * Determines if the given object is a managed JPA entity inside the current EntityManager context.
   *
   * @param obj The Object to determine if it is managed or not.
   * @return True if the Object is managed, false if it is detached.
   */
  boolean contains(Object obj);

  /**
   * Starts a transaction and returns a transaction facade.
   *
   * @return The transaction facade and never null.
   */
  Transaction startTransaction();

  /**
   * This forces the given Object to be locked at the database level.
   *
   * @param obj  The object to lock.
   * @param read If this is true, the lock will be a read lock. If it is false, the lock will be a write lock. Read
   *             locks provide that the readability of the object will be serial. That is that the most current value in
   *             the database will always be accessible to all transactions. Write locks usually imply that only
   *             transactions that contain the most current data will be allowed to write to the object.
   */
  void lock(Object obj, boolean read);

  /**
   * This locates all instances of an Object by class. This could be extremely heavy weight if the number of Objects of
   * the type are large. Therefore, only use this method when you know that the total number of Object instances in the
   * DB is low.
   *
   * @param type The type of Objects to fetch.
   * @return A List of the Objects found in the database.
   */
  <T> List<T> findAllByType(Class<T> type);

  /**
   * This locates all instances of an Object by class. This could be extremely heavy weight if the number of Objects of
   * the type are large. Therefore, only use this method when you know that the total number of Object instances in the
   * DB is low.
   *
   * @param type           The type of Objects to fetch.
   * @param includeDeleted Determines if this should return all the instances of the Object including those instances
   *                       that are marked as deleted and are {@link org.jcatapult.persistence.domain.SoftDeletable}
   *                       objects.
   * @return A List of the Objects found in the database.
   */
  <T extends SoftDeletable> List<T> findAllByType(Class<T> type, boolean includeDeleted);

  /**
   * This locates a subset of all the instances of an Object by class. This helps when paginating over all of the
   * instances of a specific type.
   *
   * @param type   The type of Objects to fetch.
   * @param start  The location in the total set of possible Objects to start from. This is zero based.
   * @param number The number of results to return in this page.
   * @return A List of the Objects found in the database.
   */
  <T> List<T> findByType(Class<T> type, int start, int number);

  /**
   * This locates a subset of all the instances of an Object by class. This helps when paginating over all of the
   * instances of a specific type.
   *
   * @param type           The type of Objects to fetch.
   * @param start          The location in the total set of possible Objects to start from. This is zero based.
   * @param number         The number of results to return in this page.
   * @param includeDeleted Determines if this should return all the instances of the Object including those instances
   *                       that are marked as deleted and are {@link SoftDeletable} objects.
   * @return A List of the Objects found in the database.
   */
  <T extends SoftDeletable> List<T> findByType(Class<T> type, int start, int number, boolean includeDeleted);

  /**
   * Determines the total number of instances of the type given. If these are {@link SoftDeletable} Objects, this will
   * include all the deleted objects as well.
   *
   * @param type The type of Objects to count.
   * @return A total number of Objects in the database.
   */
  <T> long count(Class<T> type);

  /**
   * Determines the total number of instances of the type given.
   *
   * @param type           The type of Objects to count.
   * @param includeDeleted Determines if this should count all the instances of the Object including those instances
   *                       that are marked as deleted and are {@link SoftDeletable} objects.
   * @return A total number of Objects in the database.
   */
  <T extends SoftDeletable> long count(Class<T> type, boolean includeDeleted);

  /**
   * Executes the given query and returns all of the results from the query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The EJB3 query language query string.
   * @param params A list of parameters that are parameterized within the query string (e.g. select user from User user
   *               where firstName = ?1). These are 1 based within the query String.
   * @return A List of the Objects found in the database.
   */
  <T> List<T> queryAll(Class<T> type, String query, Object... params);

  /**
   * Executes the given query and returns a subset of the results from the query. This method is useful for paginating
   * over a search results from a query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The EJB3 query language query string.
   * @param start  The location in the total set of possible Objects to start from. This is zero based.
   * @param number The number of results to return in this page.
   * @param params A list of parameters that are parameterized within the query string (e.g. select user from User user
   *               where firstName = ?1). These are 1 based within the query String.
   * @return A List of the Objects found in the database.
   */
  <T> List<T> query(Class<T> type, String query, int start, int number, Object... params);

  /**
   * Executes the given query and returns the first result from the query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The EJB3 query language query string.
   * @param params A list of parameters that are parameterized within the query string (e.g. select user from User user
   *               where firstName = ?1). These are 1 based within the query String.
   * @return The first result if there are 1 or more results, otherwise null.
   */
  <T> T queryFirst(Class<T> type, String query, Object... params);

  /**
   * Executes the given query and returns the count. This assumees that the query is a count query rather than a object
   * query.
   *
   * @param query  The EJB3 query language query string that returns a count.
   * @param params A Map of named list of parameters that are parameterized within the query string (e.g. select user
   *               from User user where firstName = ?). These are 1 based within the query String.
   * @return The number of results that executing the query would return.
   */
  long queryCount(String query, Object... params);

  /**
   * Executes the given query and returns all of the results from the query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The EJB3 query language query string.
   * @param params A Map of named parameters that are parameterized within the query string (e.g. select user from User
   *               user where firstName = :firstName).
   * @return A List of the Objects found in the database.
   */
  <T> List<T> queryAllWithNamedParameters(Class<T> type, String query, Map<String, Object> params);

  /**
   * Executes the given query and returns a subset of the results from the query. This method is useful for paginating
   * over a search results from a query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The EJB3 query language query string.
   * @param start  The location in the total set of possible Objects to start from. This is zero based.
   * @param number The number of results to return in this page.
   * @param params A Map of named parameters that are parameterized within the query string (e.g. select user from User
   *               user where firstName = :firstName).
   * @return A List of the Objects found in the database.
   */
  <T> List<T> queryWithNamedParameters(Class<T> type, String query, int start, int number, Map<String, Object> params);

  /**
   * Executes the given query and returns the first result from the query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The EJB3 query language query string.
   * @param params A Map of named parameters that are parameterized within the query string (e.g. select user from User
   *               user where firstName = :firstName).
   * @return The first result if there are 1 or more results, otherwise null.
   */
  <T> T queryFirstWithNamedParameters(Class<T> type, String query, Map<String, Object> params);

  /**
   * Executes the given query and returns the count. This assumees that the query is a count query rather than a object
   * query.
   *
   * @param query  The EJB3 query language query string that returns a count.
   * @param params A Map of named parameters that are parameterized within the query string (e.g. select user from User
   *               user where firstName = :firstName).
   * @return The number of results that executing the query would return.
   */
  long queryCountWithNamedParameters(String query, Map<String, Object> params);

  /**
   * Executes the given named query and returns all the results from the query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The named query.
   * @param params A list of parameters that are parameterized within the query string (e.g. select user from User user
   *               where firstName = ?1). These are 1 based within the query String.
   * @return A List of the Objects found in the database.
   */
  <T> List<T> namedQueryAll(Class<T> type, String query, Object... params);

  /**
   * Executes the given named query and returns a subset of the results from the query. This method is useful for
   * paginating over a search results from a query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The named query.
   * @param start  The location in the total set of possible Objects to start from. This is zero based.
   * @param number The number of results to return in this page.
   * @param params A list of parameters that are parameterized within the query string (e.g. select user from User user
   *               where firstName = ?1). These are 1 based within the query String.
   * @return A List of the Objects found in the database.
   */
  <T> List<T> namedQuery(Class<T> type, String query, int start, int number, Object... params);

  /**
   * Executes the given named query and returns the first result from the query.
   *
   * @param type   The type of Objects to fetch.
   * @param query  The named query.
   * @param params A list of parameters that are parameterized within the query string (e.g. select user from User user
   *               where firstName = ?1). These are 1 based within the query String.
   * @return The first result if there are 1 or more results, otherwise null.
   */
  <T> T namedQueryFirst(Class<T> type, String query, Object... params);

  /**
   * Locates the entity with the given type and primary key (id).
   *
   * @param type The type of Object to fetch.
   * @param id   The primary key of the Object to fetch.
   * @return The Object or null if it doesn't exist.
   */
  <T> T findById(Class<T> type, Object id);

  /**
   * <p> Saves or updates the object to the database. This method attempts to determine if it needs to call persist or
   * merge on the JPA EntityManager based on a few rules: </p> <p/> <ol> <li>If the entity is already stored in the
   * EntityManager (a managed object), this calls persist</li> <li>If the entity doesn't have an ID set, this calls
   * persist</li> <li>In all other cases, this calls merge</li> </ol> <p/> <p> In some cases you might not want this
   * handling, possibly because your class doesn't implement the {@link Identifiable} interface. In this case, you will
   * need to determine which method to call and then use either the {@link #persist(Object)} or {@link #merge(Object)}
   * method. </p>
   *
   * @param obj The object to persist.
   * @throws javax.persistence.PersistenceException
   *          If there were any database issues while persisting the Object.
   */
  void persist(Identifiable obj);

  /**
   * Forcibly calls the EntityManager persist method, but still handles wrapped transactions and error handling.
   *
   * @param obj The object to persist.
   * @throws javax.persistence.PersistenceException
   *          If there were any database issues while persisting the Object.
   */
  void persist(Object obj);

  /**
   * Forcibly calls the EntityManager merge method, but still handles wrapped transactions and error handling.
   *
   * @param obj The object to persist.
   * @throws javax.persistence.PersistenceException
   *          If there were any database issues while persisting the Object.
   */
  void merge(Object obj);

  /**
   * Flushes all of the previous calls (within a transaction) to the database. This is useful for ensuring that previous
   * statements will be committed and won't cause key violations and other similar types of errors. If there is no
   * transaction, this does nothing.
   */
  void flush();

  /**
   * Removes the object with the given type and primary key. Since this uses a primary key to remove the instance, this
   * method will only work with identified instances. In addition, if the object type passed in is {@link
   * org.jcatapult.persistence.domain.SoftDeletable} than this method will update the <strong>deleted</strong> flag and
   * perform an update rather than a delete.
   *
   * @param type The type of Object to remove.
   * @param id   The primary key of the Object to remove.
   * @return True if the entity was removed, false if it wasn't because it doesn't exist.
   */
  <T> boolean delete(Class<T> type, Object id);

  /**
   * Removes the object with the given type and primary key. Since this uses a primary key to remove the instance, this
   * method will only work with identified instances. In addition, if the object type passed in is {@link
   * org.jcatapult.persistence.domain.SoftDeletable} than this method will forcibly remove the instance and not update
   * the <strong>deleted</strong> flag.
   *
   * @param type The type of Object to remove.
   * @param id   The primary key of the Object to remove.
   * @return True if the entity was removed, false if it wasn't because it doesn't exist.
   */
  <T> boolean forceDelete(Class<T> type, Integer id);

  /**
   * Removes the given object. If the object type passed in is {@link org.jcatapult.persistence.domain.SoftDeletable}
   * than this method will update the <strong>deleted</strong> ctive flag and perform an update rather than a delete.
   *
   * @param obj The Object to remove.
   */
  void delete(Object obj);

  /**
   * Removes the given object. If the object type passed in is {@link org.jcatapult.persistence.domain.SoftDeletable}
   * than this method forcibly deletes the object and does not update the deleted flag.
   *
   * @param obj The Object to remove.
   */
  void forceDelete(Object obj);

  /**
   * Allows the execution of arbitrary bulk update/delete statements. These are always implementation dependent, but for
   * JPA these are EJB-QL bulk update and delete statements.
   *
   * @param statement The statement to execute.
   * @param params    Parameters that are passed to the query. These are 1 based according to JPA and must be positional
   *                  parameters inside the statement.
   * @return The number of rows updated or deleted.
   */
  int execute(String statement, Object... params);

  /**
   * Allows the execution of arbitrary bulk update/delete statements. These are always implementation dependent, but for
   * JPA these are EJB-QL bulk update and delete statements.
   *
   * @param statement The statement to execute.
   * @param params    Parameters that are passed to the query. These are named parameters.
   * @return The number of rows updated or deleted.
   */
  int executeWithNamedParameters(String statement, Map<String, Object> params);

  /**
   * @return The EntityManager associated with this instance of the PersistenceService.
   */
  EntityManager getEntityManager();
}