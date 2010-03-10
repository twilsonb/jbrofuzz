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
	public static final PrefEntry [] DIRS = 
	{
		// 0
		new PrefEntry(
				"save.dir",
				" Fuzzing Directory (where data is saved) ",
				" Select Directory to Save Fuzzing Data "
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
		
		// 4 topPane
		new PrefEntry(
				"ui.f.topsplitpanel",
				" Fuzzing Panel Top Split Panel",
				" Change this value, in order to change the top divider location",
				true
		),
		
		// 5 mainPane
		new PrefEntry(
				"ui.f.mainsplitpanel",
				" Fuzzing Panel Main Split Panel",
				" Change this value, in order to change the main divider location",
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
		 */
		new PrefEntry(
				"startup.check.new-version",
				" Check for a new version at startup ",
				" Untick this option, if you do not want to be notified about new versions at startup "
			),
		
		/**
		 * The preference related to the location of the tabs. True implies the 
		 * tab 
		 */
		new PrefEntry(
				"ui.jbrofuzz.tabs",
				" Show tabs in the main window at the top of the window",
				" Tick this option, if you would like to see the tabs under the tool bar, instead of at the bottom of the window ",
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
				" Use \"\\n\" instead of \"\\r\\n\" as an end of line character ",
				"Tick this box, if you want to use \"\\n\" for each line put on the wire"
			),
		
		// 2
		new PrefEntry(
				"fuzz.wrap.request",
				" Word wrap text in the \"Request\" area",
				"If ticked, the request text area will wrap the text to fit the size of the area",
				true
			),
		
		// 2
		new PrefEntry(
				"fuzz.wrap.response",
				" Word wrap test in the \"Response\" area",
				"Tick this box, to see all output text wrapped to the size of the response window",
				true
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

		// 1
		new PrefEntry(
				"fuzz.ui.wire.responses",
				" Display the Requests as well as the Responses received ",
				"Tick this box to display the responses received for each request sent within the \"On The Wire\" tab"
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
