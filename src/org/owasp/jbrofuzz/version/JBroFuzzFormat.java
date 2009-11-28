/**
 * JBroFuzz 1.7
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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

import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
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
 * @version 1.6
 */
public class JBroFuzzFormat {

	/**
	 * <p>
	 * Each version of JBroFuzz has a code name, which is defined by this public
	 * variable.
	 * </p>
	 */
	public static String CODENAME = getCodeName(JBroFuzzFormat.VERSION);

	/**
	 * Formatting the date in ISO8601 standard format.
	 */
	public static final String DATE = getDate();

	/**
	 * <p>
	 * The text, in html format, shown in the disclaimer box.
	 * </p>
	 */
	public static final String DISCLAIMER = "<HTML>JBroFuzz generates requests and records the responses. It does not attempt to identify if a particular "
			+ "site is vulnerable or not; this requires further human analysis. <BR><BR>However, certain payload categories, like XSS, "
			+ "are crafted to try to successfully exploit flaws. Thus the human analyst would have to review the results "
			+ "in order to recognize if exploitation succeeded or not.<BR><BR>"
			+ "The author of JBroFuzz takes no "
			+ "legal or other responsibility for any problems that "
			+ "might occur while running this program.<BR></HTML>";

	/**
	 * <p>
	 * The file name of the database including all payloads. This is expected to
	 * be included within the jar/exe file of JBroFuzz.
	 * </p>
	 */
	public static final String FILE_GNU = "generators.jbrofuzz";

	/**
	 * <p>
	 * The default ISO language code, set to English (en).
	 * </p>
	 */
	public static final String ISO_LAN_CODE = "en";

	/**
	 * The preferences used for deciding whether or not to delete any blank
	 * directories while exiting.
	 */
	public static final String PR_1 = "prefs.dir.delete";

	/**
	 * The preferences used for deciding whether or not to align the tabs at the
	 * top or bottom of the main window.
	 * 
	 * True represents top.
	 */
	public static final String PR_2 = "prefs.ui.tabs";

	/**
	 * The preferences used for deciding whether or not to check and notify a
	 * user at startup of a new version.
	 * 
	 * True represents yes.
	 */
	public static final String PR_3 = "startup.check.new-version";

	/**
	 * The preferences used for selecting the timeout in the socket created,
	 * while fuzzing.
	 */
	public static final String PR_FUZZ_1 = "fuzz.socket.timeout.max";

	/**
	 * The preferences used for selecting the end of line character to be
	 * appended to each line sent on the wire.
	 */
	public static final String PR_FUZZ_2 = "fuzz.end.of.line";

	/**
	 * The preferences used for keeping the "On The Wire" tab always selected.
	 */
	public static final String PR_FUZZ_3 = "fuzz.ui.show.wire";

	/**
	 * The preferences used for also showing the responses received "On The Wire" tab.
	 */
	public static final String PR_FUZZ_3_1 = "fuzz.ui.wire.responses";
	
	/**
	 * The preferences used for "Re-send POST Data if 100 Continue is received"
	 */
	public static final String PR_FUZZ_4 = "fuzz.100.continue";
	
	/**
	 * The preference for double clicking on an output line and displaying it in a browser
	 */
	public static final String PR_FUZZ_OUTPUT_1 = "fuzz.output.browser";

	/**
	 * If true, the response will wrap when opened in a new window
	 */
	public static final String WRAP_RESPONSE = "wrap.response";

	/**
	 * If true, the request will wrap when viewed in the fuzzing panel
	 */
	public static final String WRAP_REQUEST = "wrap.request";

	/**
	 * The url text saved as a preference
	 */
	public static final String TEXT_URL = "fuzz.ui.url_text";

	/**
	 * The request text saved as a preference
	 */
	public static final String TEXT_REQUEST = "fuzz.ui.request_text";

	/**
	 * The encode text saved as a preference
	 */
	public static final String TEXT_ENCODE = "encode.text";
	
	/**
	 * The decoded/hashed text saved as a preference
	 */
	public static final String TEXT_DECODE = "decode.text";
	
	/**
	 * <p>
	 * The version of JBroFuzz in String format and always of the form "x.x"
	 * where 'x' is a single digit in the range of [0-9].
	 * </p>
	 */
	public static final String VERSION = "1.8";

	/**
	 * <p>
	 * The year in which the current <code>VERSION</code> of JBroFuzz was
	 * released.
	 * </p>
	 */
	public static final String YEAR = "2009";

	/**
	 * The web site used via means of the selecting "JBroFuzz Website on the
	 * About menu.
	 * 
	 */
	public static final String URL_WEBSITE = "http://www.owasp.org/index.php/Category:OWASP_JBroFuzz";

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
	 * <p>
	 * Method for abbreviating the given String to a particular length, by
	 * removing characters from the middle of the String.
	 * </p>
	 * <p>
	 * This method does not, yet guarantee a return String of length len.
	 * </p>
	 * 
	 * @param s
	 * @param len
	 * @return String
	 */
	public static final String centerAbbreviate(String s, int len) {

		if (s.length() <= len) {
			return s;
		}

		if (len < 5) {
			return s;
		}

		if (s.length() < len) {
			return s;
		} else {
			return StringUtils.abbreviate(s, len / 2)
					+ StringUtils.right(s, len / 2);
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

	/**
	 * 
	 * @return
	 */
	private static final String getDate() {

		final String DATE_FORMAT = "DDD-yyyy-MM-dd-HH-mm-ss";

		final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT,
				new Locale(ISO_LAN_CODE));

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
	private static final int setLookAndFeel(final JBroFuzz mJBroFuzz) {
		try {
			String oSystem = System.getProperty("os.name");
			if (oSystem == null) {
				return 1;
			}
			oSystem = oSystem.toLowerCase(new Locale(ISO_LAN_CODE));
			if (oSystem.startsWith("windows")) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
			if (oSystem.startsWith("linux")) {
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
	 * Method for returning the dimension (width & height) of the screen size.
	 * </p>
	 * <p>
	 * In the event of an error it returns a dimension of (0,0).
	 * </p>
	 * 
	 * @return Dimension
	 * 
	 * @version 1.4
	 * @since 1.4
	 */
	public static Dimension getScreenSize() {

		try {

			return Toolkit.getDefaultToolkit().getScreenSize();

		} catch (AWTError e1) {

			return new Dimension(0, 0);

		} catch (HeadlessException e1) {

			return new Dimension(0, 0);
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
	 * 
	 * @version 1.4
	 */
	public JBroFuzzFormat(final JBroFuzz mJBroFuzz) {

		// Set the look and feel
		JBroFuzzFormat.setLookAndFeel(mJBroFuzz);

		// Set some preferences for the mac
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"JBroFuzz");

	}

}
