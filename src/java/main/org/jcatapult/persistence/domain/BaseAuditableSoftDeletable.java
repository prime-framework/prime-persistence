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
package org.jcatapult.persistence.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * This class implements the SoftDeletable interface and extends BaseAuditable for all of the audit, timestamp and
 * primary key information.
 *
 * @author Brian Pontarelli
 */
@MappedSuperclass
public abstract class BaseAuditableSoftDeletable extends BaseAuditable implements SoftDeletable {
  @Column(nullable = false)
  public boolean deleted = false;

  /**
   * {@inheritDoc}
   */
  public boolean isDeleted() {
    return deleted;
  }

  /**
   * {@inheritDoc}
   */
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}