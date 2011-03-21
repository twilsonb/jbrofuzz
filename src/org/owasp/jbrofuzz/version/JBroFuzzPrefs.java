/**
 * JbroFuzz 2.5
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
 * @version 2.4
 * @since 2.0
 *
 */
public class JBroFuzzPrefs {

	/**
	 * Preferences related to checking for updates
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
	 * @since 2.4
	 * 
	 */
	public static final PrefEntry [] UPDATE =
	{
		// 0 proxy enabled: boolean
		new PrefEntry(
				"update.proxy.enabled",
				" Enable Proxy ",
				" Tick this box to use a proxy when checking for new versions of JBroFuzz "
		),
		// 1, proxy host: string e.g. www.myproxy.com
		new PrefEntry(
				"update.proxy.host",
				" Proxy Host ",
				" Specify the host that will be used for your proxy "
		),
		// 2, proxy port: int e.g. 8080
		new PrefEntry(
				"update.proxy.port",
				" Proxy Port ",
				" Specify the post number [1 - 65535] "
		),
		// 3, proxy requires authentication: boolean
		new PrefEntry(
				"update.proxy.req-auth",
				" Requires Authentication ",
				" Tick this box if your proxy requires authentication "
		),
		// 4, proxy authentication type, basic, ntlm, etc: 
		// integer 1 is basic
		new PrefEntry(
				"update.proxy.auth-type",
				" Authentication Type ",
				" Only Basic Authentication is supported "
		),
		// 5 proxy user
		new PrefEntry(
				"update.proxy.user",
				" Username ",
				" The username for authentication "
		),
		// 6 proxy password
		new PrefEntry(
				"update.proxy.pass",
				" Password ",
				" The password for authentication "
		)

	};
	
	public static final PrefEntry [] DBSETTINGS = {
		// 0 proxy enabled: boolean
		new PrefEntry(
				"dbsettings.proxy.enabled",
				" Enable Proxy ",
				" Tick this box to use a proxy when connection to the database"
		),
		// 1, proxy host: string e.g. www.myproxy.com
		new PrefEntry(
				"dbsettings.proxy.host",
				" Proxy Host ",
				" Specify the host that will be used for your proxy "
		),
		// 2, proxy port: int e.g. 8080
		new PrefEntry(
				"dbsettings.proxy.port",
				" Proxy Port ",
				" Specify the post number [1 - 65535] "
		),
		// 3, proxy requires authentication: boolean
		new PrefEntry(
				"dbsettings.proxy.req-auth",
				" Requires Authentication ",
				" Tick this box if your proxy requires authentication "
		),
		// 4, proxy authentication type, basic, ntlm, etc: 
		// integer 1 is basic
		new PrefEntry(
				"dbsettings.proxy.auth-type",
				" Authentication Type ",
				" Only Basic Authentication is supported "
		),
		// 5 proxy user
		new PrefEntry(
				"dbsettings.proxy.user",
				" Username ",
				" The username for authentication "
		),
		// 6 proxy password
		new PrefEntry(
				"dbsettings.proxy.pass",
				" Password ",
				" The password for authentication "
		),
		
		// 7 db user name
		new PrefEntry(
				"dbsettings.db.user",
				" Username ",
				" The user for authentication "
		),
		
		// 8 db user password
		new PrefEntry(
			    "dbsettings.db.password", 
			    " Password ",
			    " The password for authentication "
	    ),
		
		// 9 db host
		new PrefEntry(
				"dbsettings.db.host",
				" DB Host ",
				" Specify the database host "
		),
		
		// 10 db port
		new PrefEntry(
				"dbsettings.db.port",
				" Database Port ",
				" Specify the port number [1 - 65535] "
		),
		
		// 11 db type
		new PrefEntry(
				"dbsettings.db.type",
				" Database Type ",
				" Specify which kind of DB to use (None, SQLLite, CouchDB) "
		),
		
		// 12 dbName,
		new PrefEntry(
				"dbsettings.db.dbName",
				" DBName ",
				" Specify the name of the DB to be used "
				)
		};
	
	/**
	 * Preferences related to directory locations, creating or 
	 * deleting empty directories.
	 * 
	 * @author subere@uncon.org
	 * @version 2.3
	 * @since 2.0
	 * 
	 */
	public static final PrefEntry [] DIRS = 
	{
		// 0 The actual directory e.g. /opt/data/files
		new PrefEntry(
				"save.dir",
				" Fuzzing Directory (where data is saved) ",
				" Select Directory to Save Fuzzing Data "
		),
		// 1 To allow for files to be written in an 
		// alternative location: boolean
		new PrefEntry(
				"save.dir.select",
				" Specify Fuzzing Directory",
				" Untick this box to save all data in the directory from which JBroFuzz is launched ",
				true
		),
		// 2 Used in: File -> Save, File -> Open, File -> Save As 
		// (show this directory as the last location
		// String: Last save location
		new PrefEntry(
				"save.dir.jbrofuzz.last",
				" Last Save Location Directory for .jbrofuzz Files",
				" Change this value to specify the directory where a .jbrofuzz file was last opened from or saved to"
		),
		// 3 Used in: File -> Load Fuzzers...
		new PrefEntry(
				"save.dir.jbrf.last",
				" Last Load Fuzzers Location Directory for .jbrf Payload Files",
				" Change this value to specify the directory where a .jbrf file was last loaded from"
		)
	};

	/**
	 * The UI preferences for the main and other windows.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public static final PrefEntry [] UI = 
	{
		// 0
		new PrefEntry(
				"ui.jbrofuzz.x",
				" Main window location on the x-axis",
				" Change this value, in order to change where the window is located",
				true
		),

		// 1
		new PrefEntry(
				"ui.jbrofuzz.y",
				" Main window location on the y-axis",
				" Change this value, in order to change where the window is located",
				true
		),

		// 2
		new PrefEntry(
				"ui.jbrofuzz.width",
				" Main window width",
				" Change this value, in order to change the window width",
				true
		),

		// 3
		new PrefEntry(
				"ui.jbrofuzz.height",
				" Main window height",
				" Change this value, in order to change the window height",
				true
		),

		// 4 fuzz panel main pane
		new PrefEntry(
				"ui.f.mainsplitpanel",
				" Fuzzing Panel Main Split Panel",
				" Change this value, in order to change the main divider location",
				true
		),

		// 5 fuzz panel lower pane
		new PrefEntry(
				"ui.f.bottomsplitpanel",
				" Fuzzing Panel Bottom Split Panel",
				" Change this value, in order to change the bottom divider location",
				true
		),
		
		// 6 output pane main frame
		new PrefEntry(
				"ui.o.mainsplitpanel",
				" Output Panel Main Split Panel",
				" Change this value, in order to change the main divider location",
				true
		),
		
		// 7 fuzz panel lower pane
		new PrefEntry(
				"ui.o.bottomsplitpanel",
				" Output Panel Bottom Split Panel",
				" Change this value, in order to change the bottom divider location",
				true
		)

	};

	/**
	 * General preferences for JBroFuzz, examples include checking for a new
	 * version at startup.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public static final PrefEntry [] GENERAL = 
	{
		/**
		 * The preferences used for deciding whether or not to check and notify 
		 * a user at startup of a new version. True represents yes.
		 * 
		 * This is a boolean preference.
		 * 0
		 */
		new PrefEntry(
				"startup.check.new-version",
				" Check for a new version at startup ",
				" Untick this option, if you do not want to be notified about new versions at startup "
		),

		/**
		 * The preference related to the location of the tabs. True implies the 
		 * tab 
		 * 
		 * This is a boolean preference.
		 * 1
		 */
		new PrefEntry(
				"ui.jbrofuzz.tabs",
				" Show tabs in the main window at the top of the window",
				" Tick this option, if you would like to see the tabs under the tool bar, instead of at the bottom of the window ",
				true
		),

		/**
		 * Use the default metal Look & Feel. True implies metal will be set,
		 * regardless
		 * 
		 * This is a boolean preference.
		 * 2
		 */
		new PrefEntry(
				"ui.look.metal",
				" Always use the Metal Look & Feel",
				" Tick this option, if you do not want any customisations at startup regarding look and feel changes",
				true
		)
	};

	public static final PrefEntry [] FUZZING = 
	{

		// 0
		new PrefEntry(
				"fuzz.max.timeout",
				"Specify Socket Connection Timeout (in seconds): ",
				"Increase/Decrease the number of seconds you wait for an open connection"
		),

		// 1
		new PrefEntry(
				"fuzz.end.of.line",
				" Use \"\\r\\n\" instead of \"\\n\" as an end of line character ",
				"Tick this box, if you want to use \"\\r\\n\" for each line put on the wire"
		),

		// 2
		new PrefEntry(
				"fuzz.wrap.request",
				" Word wrap text in the \"Request\" area",
				"If ticked, the request text area will wrap the text to fit the size of the area",
				true
		),

		// 3
		new PrefEntry(
				"fuzz.wrap.response",
				" Word wrap test in the \"Response\" area",
				"Tick this box, to see all output text wrapped to the size of the response window",
				true
		),

		// 4
		new PrefEntry(
				"fuzz.auth.base64",
				" Append BASE64 \"Proxy-Authorization: Basic\" header from URL username:password",
				"Tick this box to create an authorization header that includes the username and password supplied as a base64 encoded value"
		),
		// 5
		new PrefEntry(
				"fuzz.auth.base64",
				" Append \"Connection: close\" header",
				"Tick this box if you wish to ensure Socket connections are appropriately closed"
		)

	};


	public static final PrefEntry [] FUZZINGONTHEWIRE = 
	{
		// 0
		new PrefEntry(
				"fuzz.ui.show.wire",
				" Show \"On The Wire\" tab after fuzzing has stopped or finished ",
				"Tick this box, if you want to always see the \"On The Wire\" tab"
		),

		/**
		 * Select what to display within the "On The Wire" tab inside the Fuzzing Panel. 
		 * 
		 * This is an integer preference.
		 *  0 means Show Nothing
		 *  1 means Show just requests
		 *  2 means Show just responses
		 *  3 means Show everything (both requests and responses 
		 * 
		 * 1 <- First element of the array
		 */
		new PrefEntry(
				"fuzz.ui.wire.responses",
				" Display as output \"On The Wire\": ",
				" Select to display nothing, just the requests, just the responses, or both the requests and responses within the \"On The Wire\" tab"
		)

	};

	public static final PrefEntry [] FUZZINGOUTPUT =
	{
		// 0
		new PrefEntry(
				"fuzz.output.browser",
				" Double click on a Response opens it up in a Browser ",
				"Tick this box to open up response in a browser, instead of a text-based window"
		)

	};

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
