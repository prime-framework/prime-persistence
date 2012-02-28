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

/**
 * This interface marks a class as being auditable where the user that created the database record is recorded and the
 * user who last updated the record is recorded.
 *
 * @author Brian Pontarelli
 */
public interface Auditable extends TimeStampable {
  /**
   * @return The user that inserted this entity, null if the entity is transient or so type of signifier if the entity
   *         was inserted by someone anonymous.
   */
  String getInsertUser();

  /**
   * @return The user that updated this entity last, null if the entity is transient or so type of signifier if the
   *         entity was updated by someone anonymous.
   */
  String getUpdateUser();
}