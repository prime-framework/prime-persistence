/*
 * Copyright (c) 2001-2011, JCatapult.org, All Rights Reserved
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.LongType;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * This class is a Hibernate UserType for converting bigint columns (longs) to DateTime instancees.
 *
 * @author Brian Pontarelli
 */
public class DateTimeType implements UserType {
  @Override
  public int[] sqlTypes() {
    return new int[]{Types.BIGINT};
  }

  @Override
  public Class returnedClass() {
    return DateTime.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return x == y || (x != null && y != null && x.equals(y));
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
    Long l = (Long) LongType.INSTANCE.nullSafeGet(rs, names, session, owner);
    if (l == null) {
      return null;
    }

    return new DateTime(l.longValue(), DateTimeZone.UTC);
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
    DateTime dt = (DateTime) value;
    if (dt != null) {
      LongType.INSTANCE.nullSafeSet(st, dt.getMillis(), index, session);
    } else {
      LongType.INSTANCE.nullSafeSet(st, null, index, session);
    }
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }
}
