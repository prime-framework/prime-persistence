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

import java.util.UUID;

/**
 * This class provides helper methods for dealing with UUIDs.
 *
 * @author Brian Pontarelli
 */
public class UUIDTools {
  /**
   * Converts the byte array to a UUID using bit shifting.
   *
   * @param ba The byte array.
   * @return The UUID.
   */
  public static UUID fromByteArray(byte[] ba) {
    long msb = 0;
    long lsb = 0;
    for (int i = 0; i < 8; i++) {
      msb = (msb << 8) | (ba[i] & 0xff);
    }
    for (int i = 8; i < 16; i++) {
      lsb = (lsb << 8) | (ba[i] & 0xff);
    }
    return new UUID(msb, lsb);
  }

  /**
   * Converts the UUID to a byte array using the two longs.
   *
   * @param uuid The UUID.
   * @return The byte array.
   */
  public static byte[] toByteArray(UUID uuid) {
    long msb = uuid.getMostSignificantBits();
    long lsb = uuid.getLeastSignificantBits();
    byte[] ba = new byte[16];

    for (int i = 0; i < 8; i++) {
      ba[i] = (byte) (msb >>> 8 * (7 - i));
    }
    for (int i = 8; i < 16; i++) {
      ba[i] = (byte) (lsb >>> 8 * (7 - i));
    }
    return ba;
  }
}
