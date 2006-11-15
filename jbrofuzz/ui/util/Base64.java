/**
 * Base64.java
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2006 yns000 (at) users. sourceforge. net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package jbrofuzz.ui.util;

/**
 * <p>Class responsible for encoding and decoding a given byte array to a Base64
 * String and vice-versa. The operation of the class is performed by invoking
 * the encode and decode methods.</p>
 *
 * @author subere@uncon.org
 * @since 0.1
 * @version 0.2
 */
public class Base64 {

  /**
   * <p>Method used to encode a given byte array into a Base64 string.</p>
   * @param raw byte[]
   * @return String
   */
  public static String encode(byte[] raw) {

    StringBuffer encoded = new StringBuffer();
    for (int i = 0; i < raw.length; i += 3) {
      encoded.append(encodeBlock(raw, i));
    }
    return encoded.toString();
  }

  /**
   * <p>Method used to decode a given Base64 string into a byte array.</p>
   * @param base64 String
   * @return byte[]
   */
  public static byte[] decode(String base64) {

    int pad = 0;
    for (int i = base64.length() - 1; base64.charAt(i) == '='; i--) {
        pad++;
    }
    int length = base64.length() * 6 / 8 - pad;
    byte[] raw = new byte[length];
    int rawIndex = 0;
    for (int i = 0; i < base64.length(); i += 4) {
      int block = (getValue(base64.charAt(i)) << 18)
          + (getValue(base64.charAt(i + 1)) << 12)
          + (getValue(base64.charAt(i + 2)) << 6)
          + (getValue(base64.charAt(i + 3)));
      for (int j = 0; j < 3 && rawIndex + j < raw.length; j++) {
          raw[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xff);
      }
      rawIndex += 3;
    }
    return raw;
  }

  private static char[] encodeBlock(byte[] raw, int offset) {
    int block = 0;
    int slack = raw.length - offset - 1;
    int end = (slack >= 2) ? 2 : slack;
    for (int i = 0; i <= end; i++) {
      byte b = raw[offset + i];
      int neuter = (b < 0) ? b + 256 : b;
      block += neuter << (8 * (2 - i));
    }
    char[] base64 = new char[4];
    for (int i = 0; i < 4; i++) {
      int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
      base64[i] = getChar(sixbit);
    }
    if (slack < 1) {
        base64[2] = '=';
    }
    if (slack < 2) {
        base64[3] = '=';
    }
    return base64;
  }

  private static char getChar(int sixBit) {
    if (sixBit >= 0 && sixBit <= 25) {
        return (char) ('A' + sixBit);
    }
    if (sixBit >= 26 && sixBit <= 51) {
        return (char) ('a' + (sixBit - 26));
    }
    if (sixBit >= 52 && sixBit <= 61) {
        return (char) ('0' + (sixBit - 52));
    }
    if (sixBit == 62) {
        return '+';
    }
    if (sixBit == 63) {
        return '/';
    }
    return '?';
  }

  private static int getValue(char c) {
    if (c >= 'A' && c <= 'Z') {
        return c - 'A';
    }
    if (c >= 'a' && c <= 'z') {
        return c - 'a' + 26;
    }
    if (c >= '0' && c <= '9') {
        return c - '0' + 52;
    }
    if (c == '+') {
        return 62;
    }
    if (c == '/') {
        return 63;
    }
    if (c == '=') {
        return 0;
    }
    return -1;
  }
}
