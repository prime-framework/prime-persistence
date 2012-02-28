/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.hibernate;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IdentityGenerator.GetGeneratedKeysDelegate;
import org.hibernate.id.PostInsertIdentifierGenerator;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.jcatapult.persistence.domain.Identifiable;

/**
 * This class provides identifier generation that uses the JDBC 3 getGeneratedKeys exclusively.
 * <p/>
 * The only caveat is that if the Entity is Identifiable and has an ID, then that ID is returned.
 *
 * @author Brian Pontarelli
 */
public class GeneratedKeysGenerator implements PostInsertIdentifierGenerator {
  /**
   * Uses the GetGeneratedKeysDelegate.
   *
   * @param persister                 The persister.
   * @param dialect                   The dialect.
   * @param isGetGeneratedKeysEnabled Not used.
   * @return A new GetGeneratedKeysDelegate.
   * @throws HibernateException If the delegate throws.
   */
  @Override
  public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(PostInsertIdentityPersister persister,
                                                                                Dialect dialect,
                                                                                boolean isGetGeneratedKeysEnabled)
    throws HibernateException {
    if (!isGetGeneratedKeysEnabled) {
      throw new HibernateException("You can't use the GeneratedKeysGenerator unless the database and Hibernate are setup to support generated keys");
    }
    return new GetGeneratedKeysDelegate(persister, dialect);
  }

  /**
   * If the Entity is Identifiable and has an ID, this returns that ID. Otherwise, this returns the
   * POST_INSERT_INDICATOR to tell Hibernate to not send anything in the insert statement and let the database generate
   * it.
   *
   * @param s   Not used.
   * @param obj Checked to see if it is Identifiable.
   * @return The Identifiable ID or the POST_INSERT_INDICATOR.
   */
  @Override
  public Serializable generate(SessionImplementor s, Object obj) {
    if (obj instanceof Identifiable) {
      Identifiable i = (Identifiable) obj;
      if (i.getId() != null) {
        return i.getId();
      }
    }

    return IdentifierGeneratorHelper.POST_INSERT_INDICATOR;
  }
}
