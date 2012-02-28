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

/**
 * This class is a context container for the EntityManager that stores the EntityManager for later use during a single
 * request or operation.
 * <p/>
 * <pre>
 * &lt;filter>
 *   &lt;filter-name>persistence&lt;/filter-name>
 *   &lt;filter-class>org.jcatapult.persistence.servlet.PersistenceFilter&lt;/filter-class>
 * &lt;/filter>
 *
 * &lt;filter-mapping>
 *   &lt;filter-name>persistence&lt;/filter-name>
 *   &lt;url-pattern>/*&lt;/url-pattern>
 * &lt;/filter-mapping>
 * </pre>
 * <p/>
 * This filter <em>MUST</em> be the first filter in the chain otherwise the EntityManager will not be correctly setup.
 *
 * @author Brian Pontarelli
 */
public class EntityManagerContext {
  private static ThreadLocal<EntityManager> holder = new ThreadLocal<EntityManager>();

  /**
   * Sets the EntityManager for this Thread.
   *
   * @param manager The EntityManager to set.
   */
  public static void set(EntityManager manager) {
    holder.set(manager);
  }

  /**
   * Returns the EntityManager for this context. This requires that the entity manager filter be setup according to the
   * class comment.
   *
   * @return The EntityManager or null if the filter is not setup correctly.
   */
  public static EntityManager get() {
    return holder.get();
  }

  /**
   * Removes the EntityManager from the context.
   */
  public static void remove() {
    holder.remove();
  }
}