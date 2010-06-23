/**
 * JBroFuzz 2.3
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.version;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.system.Logger;

/**
 * <p>
 * Class responsible for holding the description of a number of static
 * parameters and constants relating to the application. This class also holds a
 * small number of public and private methods, relating to similar format
 * values.
 * </p>
 * <p>
 * Typical parameters found in this class as public variables include directory
 * names created at run-time, date/time timestamps, as well as user preferences
 * values and disclaimer information displayed in the about box.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.3
 * @since 1.4
 */
public class JBroFuzzFormat {


	private static final Preferences PREFS = Preferences.userRoot().node("owasp/jbrofuzz");

	private final static String DATE_FORMAT = "DDD-yyyy-MM-dd-HH-mm-ss";

	/**
	 * Formatting the date in ISO8601 standard format.
	 */
	public static final String DATE = getDate();

	/**
	 * <p>
	 * The text, in html format, shown in the disclaimer box.
	 * </p>
	 */
	public static final String DISCLAIMER = 
		"<HTML>JBroFuzz generates requests and records the responses. It does not attempt to identify if a particular "
		+ "site is vulnerable or not; this requires further human analysis. <BR><BR>However, certain payload categories, like XSS, "
		+ "are crafted to try to successfully exploit flaws. Thus the human analyst would have to review the results "
		+ "in order to recognize if exploitation succeeded or not.<BR><BR>"
		+ "The authors of JBroFuzz takes no "
		+ "legal or other responsibility for any problems that "
		+ "might occur while running this program.<BR></HTML>";

	/**
	 * <p>
	 * The version of JBroFuzz in String format and always of the form "x.x"
	 * where 'x' is a single digit in the range of [0-9].
	 * </p>
	 */
	public static final String VERSION = "2.3";

	/**
	 * <p>
	 * The year in which the current <code>VERSION</code> of JBroFuzz was
	 * released.
	 * </p>
	 */
	private static final String YEAR = "2010";

	/**
	 * The web site used via means of the selecting "JBroFuzz Website on the
	 * About menu.
	 * 
	 */
	public static final String URL_WEBSITE = "http://www.owasp.org/index.php/JBroFuzz";

	/**
	 * <p>
	 * The text, in html format, shown in the about box.
	 * </p>
	 */
	public static final String ABOUT =

		"<HTML><B>JBroFuzz Version:  " + VERSION + "<BR>" + "Codename: "
		+ JBroFuzzFormat.getCodeName(JBroFuzzFormat.VERSION)
		+ "</B><BR><BR>" + "<B>Copyright &copy; " + YEAR
		+ " subere@uncon.org</B><BR><BR>" + "Running Under  Java "
		+ System.getProperty("java.version") + "<BR><BR>"
		+ "<B>A stateless network protocol fuzzer <BR>"
		+ "for web applications." + "</B><BR></HTML>";

	/**
	 * <p>The text displayed under the code tab, in the
	 * about box.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.3
	 * @sinse 1.8
	 */
	public static final String DEVELOPMENT_TEAM =

		"<HTML><B>Development of JBroFuzz:</B><BR><BR>"
		+ "Yiannis Pavlosoglou<BR>"
		+ "Nathan Sportsman<BR>"
		+ "Ranulf Green<BR>"
		+ "Markus Miedaner<BR>"
		+ "<BR></HTML>";

	public static final Dimension ZERO_DIM = new Dimension(0,0);
	/**
	 * <p>
	 * Method for abbreviating the given String to a particular length, by
	 * removing characters from the middle of the String.
	 * </p>
	 * <p>
	 * This method does not, yet guarantee a return String of length len.
	 * </p>
	 * 
	 * @param input
	 * @param len
	 * @return String
	 */
	public static final String centerAbbreviate(final String input, final int len) {

		if (input.length() <= len) {
			return input;
		}

		if (len < 5) {
			return input;
		}

		if (input.length() < len) {
			return input;
		} else {
			return StringUtils.abbreviate(input, len / 2)
			+ StringUtils.right(input, len / 2);
		}
	}

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
			return "Chrome "; /* - God of Doors */
		}
		if (versionNumber.equalsIgnoreCase("1.9")) {
			return "Uranus "; /* - Father of Saturn */
		}
		if (versionNumber.equalsIgnoreCase("2.0")) {
			return "Maia "; /* - Goddess of Growth */
		}
		if (versionNumber.equalsIgnoreCase("2.1")) {
			return "Neto "; /* - Lusitanian mythology */
		}
		if (versionNumber.equalsIgnoreCase("2.2")) {
			return "Belus "; /* - Babylonia god of war */
		}
		if (versionNumber.equalsIgnoreCase("2.3")) {
			return "Thor "; /* Norse, OWASP Sweden 2010 */
		}
		if (versionNumber.equalsIgnoreCase("2.4")) {
			return "Ogoun "; /* Haitian Vodou, preserves fire */
		}
		if (versionNumber.equalsIgnoreCase("2.5")) {
			return "Oro "; /* - Polynesian mythology */ 
		}
		if (versionNumber.equalsIgnoreCase("2.6")) {
			return "Virtus "; /* Roman God of bravery */
		}
		if (versionNumber.equalsIgnoreCase("2.7")) {
			return "Tyr "; /* Norse, associated with single combat */
		}
		if (versionNumber.equalsIgnoreCase("2.8")) {
			return "Freyja "; /* Goddess of many, many things */
		}
		if (versionNumber.equalsIgnoreCase("2.9")) {
			return "Tohil "; /* Mayan God of the elements & war */
		}
		if (versionNumber.equalsIgnoreCase("3.0")) {
			return "Kali "; /* Kali - Goddess of time & war */
		}

		return "Zeus ";
	}

	/**
	 * 
	 * @return
	 */
	private static final String getDate() {

		final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT,
				Locale.ENGLISH);

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
	 * 
	 * @author subere@uncon.org
	 * @version 2.3
	 * @since 1.5
	 */
	private static final int setLookAndFeel() {

		final boolean metalLook = PREFS.getBoolean(JBroFuzzPrefs.GENERAL[2].getId(), true);
		if(metalLook) {
			Logger.log("Using Default Metal Look & Feel", 0);
			return 0;
		} 

		try {
			String oSystem = System.getProperty("os.name");
			if (oSystem == null) {
				Logger.log("Could not obtain Operating System Name", 2);
				return 1;
			}
			oSystem = oSystem.toLowerCase(Locale.ENGLISH);
			if (oSystem.startsWith("windows")) {
				Logger.log("Setting Windows Look & Feel", 0);
				UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
			if (oSystem.startsWith("linux")) {
				Logger.log("Setting Nimbus Look & Feel", 0);
				UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			}
			
		} catch (final UnsupportedLookAndFeelException e) {
			return 2;
		} catch (final ClassNotFoundException e) {
			return 3;
		} catch (final InstantiationException e) {
			return 4;
		} catch (final IllegalAccessException e) {
			return 5;
		} catch (final SecurityException e) {
			return 6;
		}
		return 0;

	}

	/**
	 * <p>
	 * The main constructor of this class, setting a number of variables, prior
	 * to the launch of the graphical user interface.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *            JBroFuzz
	 * 
	 * @version 1.9
	 * @since 1.4
	 */
	public JBroFuzzFormat() {

		// Set the look and feel
		JBroFuzzFormat.setLookAndFeel();

		// Set some preferences for the mac
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
		"JBroFuzz");

	}

}
