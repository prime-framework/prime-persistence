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
package org.primeframework.persistence.util;

import org.apache.commons.lang3.StringUtils;

/**
 * This class provides String helper methods not provided by Apache Commons Lang.
 *
 * @author Brian Pontarelli
 */
public class StringTools {

  /**
   * Converts a camel case string into a non-camel case string using the given separator.
   *
   * @param str       The string.
   * @param lowercase If the string should be lowercased.
   * @param separator The separator to place between camel case pieces.
   * @return The string.
   */
  public static String deCamelCase(String str, boolean lowercase, String separator) {
    if (str == null) {
      return null;
    }

    if (StringUtils.isBlank(str)) {
      return "";
    }

    int length = str.length();
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < length; i++) {
      char c = str.charAt(i);
      if (i != 0 && Character.isUpperCase(c)) {
        buf.append(separator);
      }

      if (lowercase && Character.isUpperCase(c)) {
        buf.append(Character.toLowerCase(c));
      } else {
        buf.append(c);
      }
    }

    return buf.toString();
  }
}
