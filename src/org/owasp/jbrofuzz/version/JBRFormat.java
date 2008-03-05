/**
 * JBRFormat.java 0.8
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.owasp.jbrofuzz.JBroFuzz;

/**
 * <p>
 * Class responsible for holding the description of a number of static
 * parameters and constants relating to the application.
 * </p>
 * <p>
 * Typical parameters found in this class include the file names for the
 * generators and the directories list, user properties.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class JBRFormat {

	/**
	 * <p>
	 * The version of JBroFuzz in String format and always of the form "x.x" where
	 * 'x' is a single digit in the range of [0-9].
	 * </p>
	 */
	public static final String VERSION = "0.8";

	/**
	 * The preferences used for selecting on whether or not to continue upon an
	 * error occurring while in the fuzzing directories tab.
	 */
	public static final String PREF_FUZZ_DIR_ERR = "fuzz.dir.error.continue";
	/**
	 * The web site used via means of the selecting "JBroFuzz Website on the About
	 * menu.
	 */
	public static final String URL_WEBSITE = "http://www.owasp.org/index.php/Category:OWASP_JBroFuzz";
	/**
	 * The generators file name, expected to be read from within the directory
	 * from which JBroFuzz gets launched.
	 */
	public static final String FILE_GEN = "jbrofuzz-generators";

	/**
	 * The directories file name, expected to be read from within the directory
	 * from which JBroFuzz gets launched.
	 */
	public static final String FILE_DIR = "directories.jbrofuzz";

	/**
	 * The GNU Citizen DB file name, expected to be read from within the directory
	 * from which JBroFuzz gets launched.
	 */
	public static final String FILE_GNU = "generators.jbrofuzz";

	/**
	 * The line distinguisher found in a file
	 */
	public static final String LINE_SEPARATOR = "-- jbrf-end -->";

	/**
	 * <p>
	 * The system info providing console information and potentially useful for
	 * debugging under different JVMs.
	 * </p>
	 * 
	 * @since 0.1
	 */
	public static final String SYSTEM_INFO = "Vendor:  "
		+ System.getProperty("java.vendor") + "\r\n" + "Version: "
		+ System.getProperty("java.version") + "\r\n" + "Installed at: "
		+ System.getProperty("java.home") + "\r\n" + "Website: "
		+ System.getProperty("java.vendor.url") + "\r\n" + "User: "
		+ System.getProperty("user.name") + "\r\n" + "Home $: "
		+ System.getProperty("user.home") + "\r\n" + "Current $: "
		+ System.getProperty("user.dir") + "\r\n" + "O/S: "
		+ System.getProperty("os.name") + "\r\n" + "Version: "
		+ System.getProperty("os.version") + "\r\n" + "Architecture: "
		+ System.getProperty("os.arch") + "\r\n";

	/**
	 * <p>
	 * The list of directories used if a file is not found to load the directories
	 * from.
	 * </p>
	 */
	public static final String DEFAULT_DIRS = "images\n" + ".svn\n" + "fuzz\n"
	+ "rss\n" + "live\n";

	/**
	 * <p>
	 * Each version of JBroFuzz has a code name, which is defined by this public
	 * variable.
	 * </p>
	 */
	public static String CODENAME = getCodeName(JBRFormat.VERSION);;

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
	public static final String ABOUTTEXT = "<HTML><B>Java Bro Fuzzer Version:  "
		+ JBRFormat.VERSION
		+ "<BR>"
		+ "Codename: "
		+ JBRFormat.getCodeName(JBRFormat.VERSION)
		+ "</B><BR><BR>"
		+ "JBroFuzz comes with ABSOLUTELY NO WARRANTY. This is free software "
		+ "and you are welcome to redistribute it under the GNU GPL license<BR><BR>"
		+ "<B>Copyright &copy;2007  subere (at) uncon org</B><BR><BR>"
		+ "Running Under  Java " + System.getProperty("java.version")
		+ "<BR></HTML>";

	/**
	 * <p>
	 * The text, in html format, shown in the disclaimer box.
	 * </p>
	 */
	public static final String DISCLAIMER = "<HTML>"
		+ "<B>You should only use this software to test the security of"
		+ "your own network protocol application or those you are "
		+ "authorised to do so.</B><BR><BR> The authors of JBroFuzz take no "
		+ "legal or other responsibility for any problems that "
		+ "might occur while running JBroFuzz on a "
		+ "particular application or network protocol.<BR></HTML>";

	/**
	 * Formatting the date in ISO8601 standard format.
	 */
	public static final String DATE = getDate();

	/**
	 * The String displaying within the Fuzzing Request text area
	 */
	public static final String REQUEST_TCP = "GET / HTTP/1.0\r\n<-new-line->"
		+ "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1\r\n"
		+ "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\n<-new-line->"
		+ "Accept-Language: en-gb,en;q=0.5\r\n<-new-line->"
		+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n\r\n<-new-line->";

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
		final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT, new Locale(
				JBRFormat.ISO_LAN_CODE));
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
	 *          JBroFuzz
	 */
	public static final void setLookAndFeel(final JBroFuzz mJBroFuzz) {
		int errors = 0;
		try {
			String oSystem = System.getProperty("os.name");
			oSystem = oSystem.toLowerCase(new Locale(JBRFormat.ISO_LAN_CODE));
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
	 * The main constructor of this class, setting a number of variables, prior to
	 * the launch of the graphical user interface.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *          JBroFuzz
	 */
	public JBRFormat(final JBroFuzz mJBroFuzz) {

		// Set the look and feel
		JBRFormat.setLookAndFeel(mJBroFuzz);
		// Set some preferences for the mac
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
		"JBroFuzz");
		// Set the application preferences

	}

}
