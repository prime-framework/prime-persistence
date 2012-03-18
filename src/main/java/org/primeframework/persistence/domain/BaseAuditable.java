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
package org.primeframework.persistence.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.primeframework.persistence.security.SecurityContext;

/**
 * This class is an auditing persistable Object instance. Since Java is lacking multiple inheritance I had to force this
 * to extend BaseTimeStampable since that will be the most common case. This means that BaseAuditable will not have the
 * ability to handle objects with natural keys or other types of keys or not have time stamp information.
 * <p/>
 * The user information is pulled from the {@link SecurityContext}.
 *
 * @author Brian Pontarelli
 */
@MappedSuperclass
public abstract class BaseAuditable extends BaseTimeStampable implements Auditable {
  @Column(name = "insert_user", nullable = false, updatable = false)
  public String insertUser;

  @Column(name = "update_user", nullable = false)
  public String updateUser;

  /**
   * {@inheritDoc}
   */
  public String getInsertUser() {
    return insertUser;
  }

  /**
   * {@inheritDoc}
   */
  public String getUpdateUser() {
    return updateUser;
  }

  /**
   * Sets the insertUser and updateUser fields from the {@link SecurityContext} and calls the super method to setup the
   * timestamp information.
   */
  @PrePersist
  @Override
  public void preInsert() {
    super.preInsert();
    insertUser = SecurityContext.getUsername();
    updateUser = SecurityContext.getUsername();
  }

  /**
   * Sets the updateUser field from the {@link SecurityContext} and calls the super method to setup the timestamp
   * information.
   */
  @PreUpdate
  @Override
  public void preUpdate() {
    super.preUpdate();
    updateUser = SecurityContext.getUsername();
  }
}