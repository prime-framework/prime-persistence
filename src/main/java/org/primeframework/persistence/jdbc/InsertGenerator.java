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

/**
 * This class assists in building SQL statements.
 *
 * @author Brian Pontarelli
 */
public class InsertGenerator {
  public static final Object NOW = new Object();
  private final StringBuilder build = new StringBuilder();
  private String insert;
  private int valuesCount = 0;

  public InsertGenerator() {
  }

  public InsertGenerator insert(String sql) {
    this.insert = sql;
    build.append(sql);
    return this;
  }

  public InsertGenerator values(Object... values) {
    if (valuesCount != 0 && valuesCount % 10000 == 0) {
      build.append(";\n").append(insert);
    } else if (valuesCount != 0) {
      build.append(",\n");
    }

    int count = 0;
    build.append("(");
    for (Object value : values) {
      if (count > 0) {
        build.append(",");
      }

      if (value == null) {
        build.append("null");
      } else if (value == NOW) {
        build.append("now()");
      } else if (value instanceof Enum || value instanceof String) {
        build.append("'").append(value.toString().replace("'", "''")).append("'");
      } else {
        build.append(value.toString());
      }

      count++;
    }

    build.append(")");
    valuesCount++;
    return this;
  }

  public String toString() {
    build.append(";\n");
    return build.toString();
  }
}
