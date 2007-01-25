/**
 * Format.java 0.4
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon . org
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
package org.owasp.jbrofuzz.ver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.owasp.jbrofuzz.JBroFuzz;
/**
 * <p>Class responsible for holding the description of a number of static
 * parameters and constants relating to the application.</p>
 *
 * @author subere (at) uncon . org
 * @version 0.4
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
  public static final String SYSTEM_INFO = "Vendor:  " +
                                           System.getProperty("java.vendor") +
                                           "\r\n" + "Version: " +
                                           System.getProperty("java.version") +
                                           "\r\n" + "Installed at: " +
                                           System.getProperty("java.home") +
                                           "\r\n" + "Website: " +
                                           System.getProperty("java.vendor.url") +
                                           "\r\n" + "User: " +
                                           System.getProperty("user.name") +
                                           "\r\n" + "Home $: " +
                                           System.getProperty("user.home") +
                                           "\r\n" + "Current $: " +
                                           System.getProperty("user.dir") +
                                           "\r\n" + "O/S: " +
                                           System.getProperty("os.name") +
                                           "\r\n" + "Version: " +
                                           System.getProperty("os.version") +
                                           "\r\n" + "Architecture: " +
                                           System.getProperty("os.arch") +
                                           "\r\n";

  /**
   * <p>The list of generators used if a file is not found to load the generators
   * from.</p>
   */
  public static final String DEFAULT_GENERATORS =
    "\nR:HEX:Hexadecimal Fuzz Type:16\n" +
    "> Category String | Characters | Hex\n" +
    "0\n1\n2\n3\n4\n5\n6\n7\n8\n9\na\nb\nc\nd\ne\nf\n\n" +
    "R:DEC:Decimal Fuzz Type:10\n" + "> Category String | Characters | Dec\n" +
    "0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n\n" + "R:OCT:Octal Fuzz Type:8\n" +
    "> Category String | Characters | Octal\n" + "0\n1\n2\n3\n4\n5\n6\n7\n\n" +
    "R:BIN:Binary Fuzz Type:2\n" + "> Category String | Characters | Binary\n" +
    "0\n1\n";

  /**
   * <p>Each version of JBroFuzz has a codename, which is defined by this public
   * variable.</p>
   */
  public static final String CODENAME = "Neptune ";
  /**
   * <p>The version of JBroFuzz in String format and always of the form "x.x"
   * where 'x' is a single digit in the range of [0-9].</p>
   */
  public static final String VERSION = "0.4";
  /**
   * <p>The text shown in the about box.</p>
   */
  public static final String ABOUTTEXT =
    "Java Bro Fuzzer Version:  " + VERSION + "\n" +
    "Codename:  " + CODENAME + "\n\n" +
    "JBroFuzz comes with ABSOLUTELY NO WARRANTY. This is free software\n" +
    "and you are welcome to redistribute it under the GNU GPL license\n\n" +
    "Copyright (c) 2007  subere (at) uncon org\n\n" +
    "Running Under  Java " + System.getProperty("java.version") + "\n";
  /**
   * <p>The text shown in the disclaimer box.</p>
   */
  public static final String DISCLAIMER =
    "You should only use this software to test the security\n" +
    "of your own network protocol application or those you are authorised\n" +
    "to do so. The authors of JBroFuzz take no legal or other\n" +
    "responsibility for any problems that might occur while running\n" +
    "JBroFuzz on a particular application or network protocol.\n";
//
// Variables used to define the current date as reference
//
  private static final String DATE_FORMAT = "DDD yyyy-MM-dd HH-mm-ss";
  private static final String ISO_LAN_CODE = "en";
  private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT,
    new Locale(ISO_LAN_CODE));
  /**
   * Formatting the date in ISO8601 standard format.
   */
  public static final String DATE = SDF.format(new Date());
  /**
   * <p>Method for setting the UI Look and Feel based on the operating system.
   * In the current version the only distinction that is being made is between
   * win32 o/s and all other.</p>
   *
   * @param mJBroFuzz JBroFuzz
   * @todo include more o/s specific look and feels, particular focus should be
   * given on the mac and the com.sun.java.swing.plaf.motif.MotifLookAndFeel.
   */
  public static final void setLookAndFeel(JBroFuzz mJBroFuzz) {
    try {
      String os = System.getProperty("os.name");
      os = os.toLowerCase();

      if (os.startsWith("windows")) {
        UIManager.setLookAndFeel(
          "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      }
    }
    catch (UnsupportedLookAndFeelException e) {
      mJBroFuzz.getMainWindow().log("An unsupported look and feel exception was thrown while setting the User Interface Manager");
    }
    catch (ClassNotFoundException e) {
      mJBroFuzz.getMainWindow().log("A class not found exception was thrown while setting the User Interface Manager");
    }
    catch (InstantiationException e) {
      mJBroFuzz.getMainWindow().log(
        "An instantiation exception was thrown while setting the User Interface Manager");
    }
    catch (IllegalAccessException e) {
      mJBroFuzz.getMainWindow().log("An illegal access exception was thrown while setting the User Interface Manager");
    }
    catch (SecurityException e) {
      mJBroFuzz.getMainWindow().log(
        "A security exception was thrown while setting the User Interface Manager");
    }
  }
}
