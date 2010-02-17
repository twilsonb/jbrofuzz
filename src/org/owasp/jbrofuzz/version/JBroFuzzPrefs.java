/**
 * JBroFuzz 1.9
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

/**
 * <p>This class holds a number of arrays for each type of 
 * preferences, used by JBroFuzz.</p>
 * <p>An example of how to access the preference related to,
 * say, the deletion of empty directories at shutdown would
 * be: JBroFuzzPrefs.DIRS[0]</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 *
 */
public class JBroFuzzPrefs {

	/**
	 * Preferences related to directory locations, creating or 
	 * deleting empty directories.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public static final String [] DIRS = 
	{
		/**
		 * The preferences used for deciding whether or not to delete any blank 
		 * directories while exiting.
		 * 
		 * This is a boolean preference.
		 */
		"prefs.dir.delete",
		
		/**
		 * The preference of where user data should be created.
		 * 
		 * This is a String preference.
		 */
		"save.dir"
	};

	/**
	 * General preferences for JBroFuzz, examples include checking for a new
	 * version at startup.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public static final String [] GENERAL = 
	{
		/**
		 * The preferences used for deciding whether or not to check and notify 
		 * a user at startup of a new version. True represents yes.
		 * 
		 * This is a boolean preference.
		 */
		"startup.check.new-version"
	};

	public static final String [] FUZZING = 
	{
		/**
		 * The preferences used for keeping the "On The Wire" tab 
		 * always selected.
		 * 
		 * This is a boolean preference.
		 * 
		 * [0]
		 */
		"fuzz.ui.show.wire",

		/**
		 * The preferences used for also showing the responses 
		 * received "On The Wire" tab.
		 * 
		 * This is a boolean preference.
		 * 
		 * [1]
		 */
		"fuzz.ui.wire.responses",

		/**
		 * The preferences used for "Re-send POST Data if 100 
		 * Continue is received"
		 * 
		 * This is a boolean preference.
		 * 
		 * [2]
		 */
		"fuzz.100.continue",

		/**
		 * The preference for double clicking on an output line and 
		 * displaying it in a browser
		 * 
		 * This is a boolean preference.
		 * 
		 * [3]
		 */
		"fuzz.output.browser"
	};

	/**
	 * Proxy preferences for JBroFuzz, examples include if the 
	 * proxy is enabled, the server and the port number.
	 * 
	 * @author nathan
	 * @version 2.0
	 * @since 2.0
	 */
	public static final String [] PROXY =
	{
		/**
		 * The preference for enabling proxy support
		 * 
		 * This is a boolean preference.
		 */
		"proxy.enabled",
		/**
		 * The preference for setting the proxy server
		 * 
		 * This is a String preference.
		 */
		"proxy.server",
		/**
		 * The preference setting the proxy port
		 * 
		 * This is a String preference.
		 */
		"proxy.port"
	};

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

	public static final String [] ENCODER = 
	{
		/**
		 * The encode text saved as a preference
		 */
		"encode.text",
		/**
		 * The decoded/hashed text saved as a preference
		 */
		"decode.text",
		/**
		 * Encode/hash type saved as a preference
		 */
		"encoder.name"
	};

}
