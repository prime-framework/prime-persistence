/*
 * Copyright (c) 2011, Inversoft Inc., All Rights Reserved
 */
package org.primeframework.persistence.jdbc.convert;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a type converter for the thin JDBC wrappers to handle the conversion between Java objects and SQL
 * types. It currently uses a static inner class to store the converters. This means that there is only a single
 * converter per type and you can't have different converters used in different places. This is the most general
 * handling of converters and the fastest.
 *
 * @author Brian Pontarelli
 */
public interface TypeConverter<T> {
  Object convertToSQL(T object);

  T convertFromSQL(Object object);

  class Converters {
    private static final Map<Class<?>, TypeConverter<?>> converters = new HashMap<Class<?>, TypeConverter<?>>();

    public static <T> void register(Class<T> type, TypeConverter<T> converter) {
      converters.put(type, converter);
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeConverter<T> get(Class<T> type) {
      return (TypeConverter<T>) converters.get(type);
    }
  }
}
