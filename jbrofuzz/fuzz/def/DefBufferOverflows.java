/**
 * DefBufferOverflows.java
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
 *
 * <p>Title: DefBufferOverflows.java</p>
 *
 * <p>Description: The main class responsible for holding the description of all
 * the buffer overflow strings to be attempted when BFO is selected within the
 * Generator class.</p>
 *
 * @author subere@uncon.org
 * @version 0.2
 */
public class DefBufferOverflows {
  /**
   * <p>The total length of the number of buffer overflows that will be checked
   * by the generator while in BFO (Buffer Overflow) type.</p>
   * <p>This final integer is used instead of array.length because typically a
   * generator would require to know the total number of elements prior to
   * initialising the array.</p>
   *
   */
  public static final int LENGTH = 9;
  /**
   * <p>The main array holding the definitions of the buffer overflow strings to
   * be used by the generator.</p>
   */
  private StringBuffer [] array;

  /**
   * <p>The parameterless constructor for the definition of the buffer overflow
   * class.</p>
   */
  public DefBufferOverflows() {

    StringBuffer [] array = {
        createStringBuffer("A", 65559),
        // 1: 17
        createStringBuffer("A", 17),
        // 2: 33
        createStringBuffer("A", 33),
        // 3: 65
        createStringBuffer("A", 65),
        // 4: 129
        createStringBuffer("A", 129),
        // 5: 257
        createStringBuffer("A", 257),
        // 6: 513
        createStringBuffer("A", 513),
        // 7: 1024
        createStringBuffer("A", 1025),
        // 8: 2049
        createStringBuffer("A", 2049),
        // 9: 4097
        createStringBuffer("A", 4097),
        // 10: 8193
        createStringBuffer("A", 8193),
        // 10: 12288
        createStringBuffer("A", 12288)
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
  public String getBFOElement(int n) {
    n %= LENGTH;
    return array[n].toString();
  }
  /**
   * <p>Method for creating one or more StringBuffer of a given length.</p>
   * @param input String
   * @param times int
   * @return StringBuffer
   */
  public StringBuffer createStringBuffer(String input, int times) {
    int len = input.length() * times;
    StringBuffer newBuffer = new StringBuffer(len);
    for(int i = 0; i < times; i++) {
      newBuffer.append(input);
    }
    return newBuffer;
  }
}
