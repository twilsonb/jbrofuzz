/**
 * JBRFormat.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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
package org.owasp.jbrofuzz.version;

import java.text.*;
import java.util.*;

import javax.swing.*;

import org.owasp.jbrofuzz.*;
/**
 * <p>Class responsible for holding the description of a number of static
 * parameters and constants relating to the application.</p>
 * <p>Typical parameters found in this class include the file 
 * names for
 * the generators and the directories list, user properties.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class JBRFormat {

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
	 * The line distinguisher found in a file 
	 */
	public static final String LINE_SEPARATOR = "-- jbrf-end -->";

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
	public static final String DEFAULT_DIRS = "images\n" + ".svn\n" + "fuzz\n" +
	"rss\n" + "live\n";

	/**
	 * <p>The list of generators used if a file is not found to load the generators
	 * from.</p>
	 */
	public static final String DEFAULT_GENS;
	static {
		DEFAULT_GENS = 
		"R:HEX:Hexadecimal Fuzz Type:16\n" +
		"> Category String | Characters | Hex\n" + "0\n" + "1\n" + "2\n" + "3\n" +
		"4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "a\n" + "b\n" + "c\n" +
		"d\n" + "e\n" + "f\n" + "\n" + "R:DEC:Decimal Fuzz Type:10\n" +
		"> Category String | Characters | Dec\n" + "0\n" + "1\n" + "2\n" + "3\n" +
		"4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "\n" +
		"R:OCT:Octal Fuzz Type:8\n" + "> Category String | Characters | Octal\n" +
		"0\n" + "1\n" + "2\n" + "3\n" + "4\n" + "5\n" + "6\n" + "7\n" + "\n";
	}

	/**
	 * <p>The version of JBroFuzz in String format and always of the form "x.x"
	 * where 'x' is a single digit in the range of [0-9].</p>
	 */
	public static final String VERSION = "0.7";

	/**
	 * <p>Each version of JBroFuzz has a codename, which is defined by this public
	 * variable.</p>
	 */
	public static final String CODENAME = getCodeName(VERSION);
	
	/**
	 * <p>The text, in html format, shown in the about box.</p>
	 */
	public static final String ABOUTTEXT = 
		"<HTML><B>Java Bro Fuzzer Version:  " + VERSION +
		"<BR>" + "Codename: " + CODENAME + "</B><BR><BR>" +
		"JBroFuzz comes with ABSOLUTELY NO WARRANTY. This is free software " +
		"and you are welcome to redistribute it under the GNU GPL license<BR><BR>" +
		"<B>Copyright &copy;2007  subere (at) uncon org</B><BR><BR>" + 
		"Running Under  Java " + System.getProperty("java.version") + "<BR></HTML>";

	/**
	 * <p>The text, in html format, shown in the disclaimer box.</p>
	 */
	public static final String DISCLAIMER =
		"<HTML>" +
		"<B>You should only use this software to test the security of" +
		"your own network protocol application or those you are " +
		"authorised to do so.</B><BR><BR> The authors of JBroFuzz take no " +
		"legal or other responsibility for any problems that " +
		"might occur while running JBroFuzz on a " +
		"particular application or network protocol.<BR></HTML>";

	/**
	 * <p>The readme text shown in the open source tab.</p> 
	 */
	public static final String OPEN_SOURCE_README =
		"Open Source Fuzzing\n\nUsage:\n\n" +
		"Valid formats are: “mydomain.com”, “myuni.ac.uk”, etc. " +
		"Do not include the ‘@’ symbol or any of the quotes \" " +
		"in the domain.\n\n" +
		"Description:\n\n" + 
		"This tab allows you to harvest email addresses that are " +
		"in the public domain, given the Fully Qualified Domain " +
		"Name (FQDN) that is under scrutiny.\n\n" +
		"This is achieved by submitting a total of five (5) requests " +
		"to Google Web Search and Google Groups with the FQDN provided " +
		"and filtering through the responses.\n\n" +
		"Notes:\n\n" +
		"E-mail addresses returned are not limited to the domain " +
		"specified, but also include addresses which have been indexed " +
		"along side the domain in question.";
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
	public static final void setLookAndFeel(final JBroFuzz mJBroFuzz) {
		try {
			String oSystem = System.getProperty("os.name");
			oSystem = oSystem.toLowerCase(new Locale(ISO_LAN_CODE));
			if (oSystem.startsWith("windows")) {
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
			mJBroFuzz.getFrameWindow().log("An illegal access exception was " +
			"thrown while setting the User Interface Manager");
		}
		catch (SecurityException e) {
			mJBroFuzz.getFrameWindow().log(
					"A security exception was thrown while setting the User " +
			"Interface Manager");
		}
	}
	
	  /**
	   * Return the code name of the current version, based on the version number.
	   * @return String
	   */
	  private static String getCodeName(String versionNumber) {

	    if (versionNumber.equalsIgnoreCase("0.1"))
	      return "Vesta "; /* - Goddess of the Home */
	    if (versionNumber.equalsIgnoreCase("0.2"))
	      return "Jupiter "; /* - King of the Gods */
	    if (versionNumber.equalsIgnoreCase("0.3"))
	      return "Juno "; /* - Queen of the Gods */
	    if (versionNumber.equalsIgnoreCase("0.4"))
	      return "Neptune "; /* - God of the Sea */
	    if (versionNumber.equalsIgnoreCase("0.5"))
	      return "Pluto "; /* - God of Death */
	    if (versionNumber.equalsIgnoreCase("0.6"))
	      return "Apollo "; /* - God of the Sun */
	    if (versionNumber.equalsIgnoreCase("0.7"))
	      return "Selene "; /* - Goddess of the Moon */
	    if (versionNumber.equalsIgnoreCase("0.8"))
	      return "Mars "; /* - God of War */
	    if (versionNumber.equalsIgnoreCase("0.9"))
	      return "Dioni "; /* - Goddess of Love */
	    if (versionNumber.equalsIgnoreCase("1.0"))
	      return "Cupid "; /* - God of Love */
	    if (versionNumber.equalsIgnoreCase("1.1"))
	      return "Mercury "; /* - Messenger of the Gods */
	    if (versionNumber.equalsIgnoreCase("1.2"))
	      return "Athena "; /* - Goddess of Wisdom */
	    if (versionNumber.equalsIgnoreCase("1.3"))
	      return "Ceres "; /* - The Earth Goddess */
	    if (versionNumber.equalsIgnoreCase("1.4"))
	      return "Proserpine "; /* - Goddess of the Underworld */
	    if (versionNumber.equalsIgnoreCase("1.5"))
	      return "Vulcan "; /* - The Smith God */
	    if (versionNumber.equalsIgnoreCase("1.6"))
	      return "Bacchus "; /* - God of Wine */
	    if (versionNumber.equalsIgnoreCase("1.7"))
	      return "Saturn "; /* - God of Time */
	    if (versionNumber.equalsIgnoreCase("1.8"))
	      return "Janus "; /* - God of Doors */
	    if (versionNumber.equalsIgnoreCase("1.9"))
	      return "Uranus "; /* - Father of Saturn */
	    if (versionNumber.equalsIgnoreCase("2.0"))
	      return "Maia "; /* - Goddess of Growth */
	    if (versionNumber.equalsIgnoreCase("2.1"))
	      return "Zeus "; 
	    return "Zeus "; 
	  }
}
