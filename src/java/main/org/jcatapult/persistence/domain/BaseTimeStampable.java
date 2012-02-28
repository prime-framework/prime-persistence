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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * This class implements the Timestamped interface and extends IntegerIdentifiable for a primary key.
 *
 * @author Brian Pontarelli
 */
@MappedSuperclass
public abstract class BaseTimeStampable extends IntegerIdentifiable implements TimeStampable {
  @Type(type = "org.jcatapult.persistence.hibernate.DateTimeType")
  @Column(name = "insert_date", nullable = false, updatable = false)
  public DateTime insertDate;
  
  @Type(type = "org.jcatapult.persistence.hibernate.DateTimeType")
  @Column(name = "update_date", nullable = false)
  public DateTime updateDate;

  /**
   * {@inheritDoc}
   */
  public DateTime getInsertDate() {
    return insertDate;
  }

  /**
   * {@inheritDoc}
   */
  public DateTime getUpdateDate() {
    return updateDate;
  }

  /**
   * Sets the insertDate and updateDate fields to the current date-time.
   */
  @PrePersist
  public void preInsert() {
    insertDate = new DateTime();
    updateDate = new DateTime();
  }

  /**
   * Sets the updateDate field to the current date-time.
   */
  @PreUpdate
  public void preUpdate() {
    updateDate = new DateTime();
  }
}