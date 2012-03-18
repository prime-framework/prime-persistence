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
package org.primeframework.persistence.jdbc;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.primeframework.persistence.jdbc.convert.TypeConverter;
import org.primeframework.persistence.util.StringTools;

import static java.util.Arrays.*;

/**
 * This class is a builder for inserting into a table and retrieving any generated keys.
 *
 * @author Brian Pontarelli
 */
public class Insert extends BaseOperation<Insert> {
  private final Connection c;
  private StringBuilder sql = new StringBuilder();

  public Insert(Connection c) {
    super(true);
    this.c = c;
  }

  public Insert(Connection c, String sql) {
    super(true);
    this.c = c;
    this.sql.append(sql);
  }

  public Insert in(String sql) {
    this.sql = new StringBuilder();
    this.sql.append(sql);
    return this;
  }

  /**
   * Allows the insertion of Objects directly via reflection and JPA annotations. This also makes numerous assumptions
   * if there are no JPA annotations.
   *
   * @param type The type of objects to insert.
   * @param <T>  The type.
   * @return The builder that is used to insert the objects with.
   */
  public <T> InsertObject<T> into(Class<T> type) {
    return new InsertObject<T>(type);
  }

  /**
   * Adds a bulk insert block. This appends the (?,?,?) format to the SQL. It also adds the given values ot the
   * parameters.
   *
   * @param params The value parameters.
   * @return This insert builder.
   */
  public Insert addBulk(Object... params) {
    if (sql.charAt(sql.length() - 1) == ')') {
      sql.append(",");
    }

    sql.append("(");
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        sql.append(",");
      }
      sql.append("?");
      add(params[i]);
    }
    sql.append(")");
    return this;
  }

  /**
   * Performs the insert.
   *
   * @return The number of rows inserted.
   * @throws InsertException If the insert fails.
   */
  public int go() throws InsertException {
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString(), Statement.NO_GENERATED_KEYS);
      setParams(ps);

      return ps.executeUpdate();
    } catch (SQLException e) {
      throw new InsertException(e);
    } finally {
      close(ps);
    }
  }

  /**
   * Performs the insert.
   *
   * @param handler The generated keys handler.
   * @return The result.
   * @throws InsertException If the insert fails.
   */
  public <T> InsertResult<T> go(GeneratedKeyHandler<T> handler) throws InsertException {
    PreparedStatement ps = null;
    try {
      ps = c.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
      setParams(ps);

      int results = ps.executeUpdate();
      if (results != 1) {
        throw new InsertException("Inserting a single row failed completely");
      }

      ResultSet keysRS = ps.getGeneratedKeys();
      List<T> keys = new ArrayList<T>();
      while (keysRS.next()) {
        keys.add(handler.handle(keysRS));
      }

      return new InsertResult<T>(results, keys);
    } catch (SQLException e) {
      throw new InsertException(e);
    } finally {
      close(ps);
    }
  }

  public static class InsertException extends JDBCException {
    public InsertException() {
    }

    public InsertException(String message) {
      super(message);
    }

    public InsertException(String message, Throwable cause) {
      super(message, cause);
    }

    public InsertException(Throwable cause) {
      super(cause);
    }
  }

  public abstract static class GeneratedKeyHandler<T> {
    public abstract T handle(ResultSet rs) throws SQLException;
  }

  public static class InsertResult<T> {
    public final int count;
    public final List<T> keys;

    public InsertResult(int count, List<T> keys) {
      this.count = count;
      this.keys = keys;
    }
  }

  /**
   * @param <T>
   */
  public class InsertObject<T> {
    private final Class<T> type;
    private final List<T> objects = new ArrayList<T>();

    public InsertObject(Class<T> type) {
      this.type = type;
    }

    public InsertObject objects(T... objects) {
      this.objects.addAll(asList(objects));
      return this;
    }

    public InsertObject objects(Collection<T> objects) {
      this.objects.addAll(objects);
      return this;
    }

    public int go() throws InsertException {
      List<String> columns = columnNames(type);
      Insert.this.sql.append("insert into ").append(tableName(type)).append(" (").append(StringUtils.join(columns, ",")).
        append(") values ");
      boolean first = true;
      for (T object : objects) {
        if (!first) {
          Insert.this.sql.append(",");
        }
        first = false;

        Insert.this.sql.append("(");
        for (int i = 0; i < columns.size(); i++) {
          Insert.this.sql.append("?");
          if (i < columns.size() - 1) {
            Insert.this.sql.append(",");
          }
        }
        Insert.this.sql.append(")");

        Insert.this.add(values(type, columns.size(), object));
      }

      return Insert.this.go();
    }

    private String tableName(Class<T> type) {
      Table table = type.getAnnotation(Table.class);
      String tableName;
      if (table != null) {
        tableName = table.name();
      } else {
        tableName = StringTools.deCamelCase(type.getSimpleName(), true, "_");
      }

      return tableName;
    }

    private List<String> columnNames(Class<T> type) {
      List<String> columnNames = new ArrayList<String>();
      Field[] fields = type.getFields();
      for (Field field : fields) {
        Transient trans = field.getAnnotation(Transient.class);
        if (trans != null) {
          continue;
        }

        Column column = field.getAnnotation(Column.class);
        String name = "";
        if (column != null) {
          name = column.name();
        }

        if (name.equals("")) {
          name = StringTools.deCamelCase(field.getName(), true, "_");
        }

        columnNames.add(name);
      }

      return columnNames;
    }

    @SuppressWarnings("unchecked")
    private Object[] values(Class<T> type, int size, T object) {
      Object[] values = new Object[size];
      Field[] fields = type.getFields();
      int index = 0;
      for (Field field : fields) {
        Transient trans = field.getAnnotation(Transient.class);
        if (trans != null) {
          continue;
        }

        try {
          values[index] = field.get(object);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }

        Class<?> fieldType = field.getType();
        TypeConverter converter = TypeConverter.Converters.get(fieldType);
        if (converter != null) {
          values[index] = converter.convertToSQL(values[index]);
        }

        index++;
      }

      return values;
    }

    public <T> InsertResult go(GeneratedKeyHandler<T> handler) throws InsertException {
      return Insert.this.go(handler);
    }
  }
}
