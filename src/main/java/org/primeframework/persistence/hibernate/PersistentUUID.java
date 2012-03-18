/*
 * Copyright (c) 2001-2011, Inversoft Inc., All Rights Reserved
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
package org.primeframework.persistence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.primeframework.persistence.service.DatabaseType;
import org.primeframework.persistence.service.DatabaseType.Database;
import org.primeframework.persistence.util.UUIDTools;

/**
 * This class is a hibernate type converter for UUIDs. The difficulty here is that MySQL and PostgreSQL use different
 * JDBC handling for their UUIDs.
 * <p/>
 * In PostgreSQL, the column type is <strong>uuid</strong> and it maps directly to the {@link java.util.UUID} class.
 * <p/>
 * In MySQL, the column type is <strong>binary(16)</strong> and it maps to a <strong>byte[]</strong>.
 * <p/>
 * Therefore, we have to determine the database type during the conversion and based on that handle the JDBC
 * interactions differently.
 * <p/>
 * This uses the {@link java.sql.Types#BINARY} type from JDBC to indicate to Hibernate how to properly handle the values.
 *
 * @author Brian Pontarelli
 */
public class PersistentUUID implements UserType {
  /**
   * @return Types.BINARY.
   */
  @Override
  public int[] sqlTypes() {
    if (DatabaseType.database == Database.MYSQL) {
      return new int[] {Types.BINARY};
    }

    return new int[] {Types.OTHER};
  }

  /**
   * @return UUID.class
   */
  @Override
  public Class returnedClass() {
    return UUID.class;
  }

  @Override
  public boolean equals(Object x, Object y) {
    if (x == y) {
      return true;
    }
    if (x == null || y == null) {
      return false;
    }
    UUID ux = (UUID) x;
    UUID uy = (UUID) y;
    return ux.equals(uy);
  }

  @Override
  public int hashCode(Object x) {
    return x.hashCode();
  }

  /**
   * Handles getting the value from the result set. If the database is postgresql, this grabs the value directly from the
   * result set and returns it. If it is MySQL, this converts the byte[] to a UUID.
   *
   * @param rs The result set.
   * @param names The name of the column.
   * @param owner Not used.
   * @return The UUID or null.
   * @throws java.sql.SQLException If the value couldn't be retrieved from the result set.
   */
  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
    Object value = rs.getObject(names[0]);
    if (value == null || rs.wasNull()) {
      return null;
    }

    if (DatabaseType.database == Database.POSTGRESQL) {
      return value;
    }

    return UUIDTools.fromByteArray((byte[]) value);
  }

  /**
   * Sets the UUID into the prepared statement. This sets it in directly if the database is postgresql. If it is MySQL
   * it converts the UUID to a byte array and sets that in.
   *
   * @param st The prepared statement to put the UUID into.
   * @param value The UUID.
   * @param index The index to use.
   * @throws SQLException If the set fails.
   */
  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
    if (DatabaseType.database == Database.POSTGRESQL) {
      st.setObject(index, value);
    } else if (value != null) {
      st.setObject(index, UUIDTools.toByteArray((UUID) value));
    } else {
      st.setNull(index, sqlTypes()[0]);
    }
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  /**
   * @return  False, UUID is not mutable.
   */
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
