/**
 * DefFormatStringErrors.java
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
 * @version 0.1
 */
public class DefFormatStringErrors {
  /**
   * <p>This variable carries the length of the array. This is used instead of
   * array.length due to the need from a Generator to have knowledge of the
   * length, prior to object instantiation.</p>
   */
  public static final int LENGTH = 19;

  private final String[] array;

  /**
   * <p>The main constructor, putting in place the array of Format String Errors
   * that will be tested through the generator.</p>
   */
  public DefFormatStringErrors() {
    // Hardcore the variables to increase performance
    String[] array = {
        "%s%p%x%d",
        ".1024d",
        "%.2049d",
        "%p%p%p%p",
        "%x%x%x%x",
        "%d%d%d%d",
        "%s%s%s%s",
        "%99999999999s",
        "%08x",
        "%%20d",
        "%%20n",
        "%%20x",
        "%%20s",
        "%s%s%s%s%s%s%s%s%s%s",
        "%p%p%p%p%p%p%p%p%p%p",
        "%#0123456x%08x%x%s%p%d%n%o%u%c%h%l%q%j%z%Z%t%i%e%g%f%a%C%S%08x%%",
        "%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
        "%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s" +
        "%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
        "%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x" +
        "%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x" +
        "%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x" +
        "%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x" +
        "%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x%x"
    };
    this.array = array;
  }

  /**
   * <p>Method responsible for returning the buffer overflow string from the
   * instantiated class.</p>
   *
   * @param n int
   * @return String
   */
  public String getFSElement(int n) {
    n %= LENGTH;
    return array[n];
  }

  /**
   * @todo Maybe include the following format string definitions
   *
        ("%x", 257),
        ("%s", 512), ("%x", 513), ("%x", 1024),
        ("%x", 1026), ("%s", 2047),
        ("%s%n%x%d", 4097),
        (".1024d", 10), ("%.2049d", 13),
        ("%99999999999d", 12)
        ("%99999999999d", 1025),
        ("%99999999999x", 1025),
        ("%99999999999n", 1025),
        ("%08x", 129), ("%%20s", 129),
        ("%08n", 129), ("%%20x", 129),
        ("%08s", 129), ("%%20n", 129),
        ("%%20d", 1025),
        ("%s", 65537)
   %#0123456x%%x%%s%%p%%n%%d%%o%%u%%c%%h%%l%%q%%j%%z%%Z%%t%%i%%e%%g%%f%%a%%C%%S%%08x
   */

}
