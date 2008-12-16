/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
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
 * 
 */
package org.owasp.jbrofuzz.version;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.JBroFuzz;

/**
 * <p>
 * Class responsible for holding the description of a number of static
 * parameters and constants relating to the application. This class also holds a
 * small number of public and private methods, relating to similar format values. 
 * </p>
 * <p>
 * Typical parameters found in this class as public variables include directory names
 * created at run-time, date/time timestamps, as well as user preferences values and 
 * disclaimer information displayed in the about box.  
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.2
 */
public class JBroFuzzFormat {

	/**
	 * <p>
	 * The version of JBroFuzz in String format and always of the form "x.x"
	 * where 'x' is a single digit in the range of [0-9].
	 * </p>
	 */
	public static final String VERSION = "1.2";

	/**
	 * <p>
	 * The year in which the current <code>VERSION</code> of JBroFuzz was released.
	 * </p>
	 */
	public static final String YEAR = "2008";
	
	/**
	 * The preferences used for selecting the timeout in the socket created,
	 * while fuzzing.
	 */
	public static final String PR_FUZZ_1 = "fuzz.socket.timeout.max";
	
	/**
	 * The preferences used to decide whether or not to display binary files
	 * in hex format.
	 */
	public static final String PR_SNIF_1 = "snif.display.hex";
	
	/**
	 * The preferences used for deciding whether or not to delete any blank
	 * directories while exiting.
	 */
	public static final String PR_1 = "prefs.dir.delete";
	
	/**
	 * The preferences used for deciding whether or not to allign the tabs
	 * at the top or bottom of the main window.
	 * 
	 * True represents top.
	 */
	public static final String PR_2 = "prefs.ui.tabs";
	
	/**
	 * The web site used via means of the selecting "JBroFuzz Website on the
	 * About menu.
	 */
	public static final String URL_WEBSITE = "http://www.owasp.org/index.php/Category:OWASP_JBroFuzz";

	/**
	 * <p> The file name of the database including all payloads. This is expected to be 
	 * included within the jar/exe file of JBroFuzz.</p>
	 */
	public static final String FILE_GNU = "generators.jbrofuzz";

	/**
	 * The line distinguisher found in a file
	 */
	public static final String LINE_SEPARATOR = "-->";

	
	/**
	 * <p>
	 * Each version of JBroFuzz has a code name, which is defined by this public
	 * variable.
	 * </p>
	 */
	public static String CODENAME = getCodeName(JBroFuzzFormat.VERSION);;

	/**
	 * <p>
	 * The default ISO language code, set to English (en).
	 * </p>
	 */
	public static final String ISO_LAN_CODE = "en";
	/**
	 * <p>
	 * The text, in html format, shown in the about box.
	 * </p>
	 */
	public static final String ABOUTTEXT =

	"<HTML><B>JBroFuzz Version:  " + VERSION + "<BR>" + "Codename: "
			+ JBroFuzzFormat.getCodeName(JBroFuzzFormat.VERSION) + "</B><BR><BR>"
			+ "<B>Copyright &copy; " + YEAR + " subere@uncon.org</B><BR><BR>"
			+ "Running Under  Java " + System.getProperty("java.version")
			+ "<BR><BR>" + "<B>A stateless network protocol fuzzer <BR>"
			+ "for web applications." + "</B><BR></HTML>";

	/**
	 * <p>
	 * The text, in html format, shown in the disclaimer box.
	 * </p>
	 */
	public static final String DISCLAIMER = "<HTML>This program generates a substantial amount "
			+ "of network traffic for the purpose of fuzzing "
			+ "web applications.<BR><BR>"
			+ "JBroFuzz has been developed with penetration "
			+ "testing in mind.<BR><BR>"
			+ "The author of JBroFuzz takes no "
			+ "legal or other responsibility for any problems that "
			+ "might occur <BR>while running this program.<BR></HTML>";

	/**
	 * Formatting the date in ISO8601 standard format.
	 */
	public static final String DATE = getDate();

	/**
	 * The String displaying within the Fuzzing Request text area
	 */
	public static final String REQUEST_TCP = "GET / HTTP/1.1\n"
			+ "Host: localhost\n"
			+ "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1\n"
			+ "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\n"
			+ "Accept-Language: en-gb,en;q=0.5\n"
			+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n\n";

	/**
	 * Return the code name of the current version, based on the version number.
	 * 
	 * @return String
	 */
	private static final String getCodeName(final String versionNumber) {

		if (versionNumber.equalsIgnoreCase("0.1")) {
			return "Vesta "; /* - Goddess of the Home */
		}
		if (versionNumber.equalsIgnoreCase("0.2")) {
			return "Jupiter "; /* - King of the Gods */
		}
		if (versionNumber.equalsIgnoreCase("0.3")) {
			return "Juno "; /* - Queen of the Gods */
		}
		if (versionNumber.equalsIgnoreCase("0.4")) {
			return "Neptune "; /* - God of the Sea */
		}
		if (versionNumber.equalsIgnoreCase("0.5")) {
			return "Pluto "; /* - God of Death */
		}
		if (versionNumber.equalsIgnoreCase("0.6")) {
			return "Apollo "; /* - God of the Sun */
		}
		if (versionNumber.equalsIgnoreCase("0.7")) {
			return "Selene "; /* - Goddess of the Moon */
		}
		if (versionNumber.equalsIgnoreCase("0.8")) {
			return "Mars "; /* - God of War */
		}
		if (versionNumber.equalsIgnoreCase("0.9")) {
			return "Dioni "; /* - Goddess of Love */
		}
		if (versionNumber.equalsIgnoreCase("1.0")) {
			return "Cupid "; /* - God of Love */
		}
		if (versionNumber.equalsIgnoreCase("1.1")) {
			return "Mercury "; /* - Messenger of the Gods */
		}
		if (versionNumber.equalsIgnoreCase("1.2")) {
			return "Athena "; /* - Goddess of Wisdom */
		}
		if (versionNumber.equalsIgnoreCase("1.3")) {
			return "Ceres "; /* - The Earth Goddess */
		}
		if (versionNumber.equalsIgnoreCase("1.4")) {
			return "Proserpine "; /* - Goddess of the Underworld */
		}
		if (versionNumber.equalsIgnoreCase("1.5")) {
			return "Vulcan "; /* - The Smith God */
		}
		if (versionNumber.equalsIgnoreCase("1.6")) {
			return "Bacchus "; /* - God of Wine */
		}
		if (versionNumber.equalsIgnoreCase("1.7")) {
			return "Saturn "; /* - God of Time */
		}
		if (versionNumber.equalsIgnoreCase("1.8")) {
			return "Janus "; /* - God of Doors */
		}
		if (versionNumber.equalsIgnoreCase("1.9")) {
			return "Uranus "; /* - Father of Saturn */
		}
		if (versionNumber.equalsIgnoreCase("2.0")) {
			return "Maia "; /* - Goddess of Growth */
		}
		if (versionNumber.equalsIgnoreCase("2.1")) {
			return "Zeus ";
		}
		return "Zeus ";
	}

	private static final String getDate() {
		
		final String DATE_FORMAT = "DDD yyyy-MM-dd HH-mm-ss";
		
		final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT, new Locale(ISO_LAN_CODE));
		
		return SDF.format(new Date());
	}

	/**
	 * <p>
	 * Method for setting the UI Look and Feel based on the operating system. In
	 * the current version the only distinction that is being made is between
	 * win32 o/s and all other.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *            JBroFuzz
	 */
	public static final void setLookAndFeel(final JBroFuzz mJBroFuzz) {
		int errors = 0;
		try {
			String oSystem = System.getProperty("os.name");
			oSystem = oSystem.toLowerCase(new Locale(JBroFuzzFormat.ISO_LAN_CODE));
			if (oSystem.startsWith("windows")) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
		} catch (final UnsupportedLookAndFeelException e) {
			errors += 100;
		} catch (final ClassNotFoundException e) {
			errors += 100;
		} catch (final InstantiationException e) {
			errors += 100;
		} catch (final IllegalAccessException e) {
			errors += 100;
		} catch (final SecurityException e) {
			errors += 100;
		}
	}
	
	/**
	 * <p>
	 * Method for abbreviating the given String to a particular length,
	 * by removing characters from the middle of the String.
	 * </p>
	 * <p>This method does not, yet guarantee a return String of length len.</p>
	 * 
	 * @param s
	 * @param len
	 * @return String 
	 */
	public static final String centerAbbreviate(String s, int len) {
		
		if(len < 5) {
			return s;
		}
		
		if(s.length() < len) {
			return s;
		} else {
			return StringUtils.abbreviate(s, len / 2) + StringUtils.right(s, len / 2);
		}
	}

	/**
	 * <p>
	 * The main constructor of this class, setting a number of variables, prior
	 * to the launch of the graphical user interface.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *            JBroFuzz
	 */
	public JBroFuzzFormat(final JBroFuzz mJBroFuzz) {

		// Set the look and feel
		JBroFuzzFormat.setLookAndFeel(mJBroFuzz);
		// Set some preferences for the mac
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JBroFuzz");
		// Set the application preferences

	}

	public int getMajorVersion() {

		try {
			return Integer.parseInt(VERSION.split("\\.")[0]);
		} catch (Exception e) {
			return 0;
		}

	}

	public int getMinorVersion() {

		try {
			return Integer.parseInt(VERSION.split("\\.")[1]);
		} catch (Exception e) {
			return 0;
		}

	}

}
