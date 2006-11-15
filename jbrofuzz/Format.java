/**
 * Format.java
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
package jbrofuzz;

/**
 * <p>Description: Class responsible for holding the description of a number of
 * parameters and constants relating to the application.</p>
 *
 * @author subere@uncon.org
 * @version 0.3
 */
public class Format {
  /**
   * <p>The header used when writting a single fuzz request.</p>
   */
  public static final String HD1 =
      "\n-----JBroFuzz------Start--(1 of 1)------\n";
  /**
   * <p>The footer used when writting a single fuzz request.</p>
   */
  public static final String FT1 =
      "\n-----JBroFuzz------End----(1 of 1)------\n";
  /**
   * <p>The header used when writting any fuzz request (other than a single
   * request).</p>
   */
  public static final String HDS = "\n-----JBroFuzz------Start--(";
  /**
   * <p>The footer used when writting any fuzz request (other than a single
   * request).</p>
   */
  public static final String HDE = "\n-----JBroFuzz------End----(";
  /**
   * <p>The end of the String used to complete both HDS and HDE Strings.</p>
   */
  public static final String FTS = ")------\n";
  /**
   * <p>The system info providing console information and potentially useful
   * for debugging under different JVMs.</p>
   *
   * @since 0.1
   */
  public static final String SYSTEM_INFO =
      "Vendor:  " + System.getProperty("java.vendor") + "\r\n" +
      "Version: " + System.getProperty("java.version") + "\r\n" +
      "Installed at: " + System.getProperty("java.home") + "\r\n" +
      "Website: " + System.getProperty("java.vendor.url") + "\r\n" +
      "User: " + System.getProperty("user.name") + "\r\n" +
      "Home $: " + System.getProperty("user.home") + "\r\n" +
      "Current $: " + System.getProperty("user.dir") + "\r\n" +
      "O/S: " + System.getProperty("os.name") + "\r\n" +
      "Version: " + System.getProperty("os.version") + "\r\n" +
      "Architecture: " + System.getProperty("os.arch") + "\r\n";

  /*
   * @todo replace this data structure with one that can be made final
   */

  /**
   * <p>An array list of all the supported generators.</p>
   * @since 0.2
   */
  public static final String[] GENERATORS = {
      "ZER (Request as is)",
      "BFO (Buffer Overflows)",
      "FSE (Format String Errors)",
      "INT (Integer Overflows)",
      "XSS (Cross Site Scripting)",
      "BIN (Binary Fuzz Type)",
      "OCT (Octal Fuzz Type)",
      "DEC (Decimal Fuzz Type)",
      "HEX (Hexadecimal Fuzz Type)",
      "SQL (SQL Injections)"};
}
