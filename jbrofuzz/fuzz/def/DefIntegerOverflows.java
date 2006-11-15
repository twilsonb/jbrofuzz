/**
 * DefIntegerOverflow.java
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
package jbrofuzz.fuzz.def;

/**
 * <p>Title: Java Browser Fuzzer</p>
 *
 * <p>Description: A simple application for fuzzing URLs</p>
 *
 * @author subere@uncon.org
 * @version 0.2
 */
public class DefIntegerOverflows {
  public static final int LENGTH = 12;

  private final String [] array;

  public DefIntegerOverflows() {
    String [] array = {
         "-1",
         "0",
         "0x100",
         "0x1000",
         "0x3fffffff",
         "0x7ffffffe",
         "0x7fffffff",
         "0x80000000",
         "0xfffffffe",
         "0xffffffff",
         "0x10000",
         "0x100000"};
     this.array = array;
  }
  /**
   * <p>Method responsible for returning the buffer overflow string from the
   * instantiated class.</p>
   *
   * @param n int
   * @return String
   */
  public String getINTElement(int n) {
    n %= LENGTH;
    return array[n];
  }
}
