/**
 * JBroFuzz 2.4
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 * changes for version 2.4 made by daemonmidi@gmail.com
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
package org.owasp.jbrofuzz.fuzz.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.core.Prototype;
import org.owasp.jbrofuzz.db.SQLiteHandler;
import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.JBroFuzzFileFilter;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class OpenSession {

	// The maximum number of chars to be read from file, regardless
	private final static int MAX_CHARS = Short.MAX_VALUE;
	private JBroFuzzWindow mWindow;

	public OpenSession(JBroFuzzWindow mWindow) {
		new OpenSession(mWindow, "");
		
		
		this.mWindow = mWindow;
	}

	public OpenSession(JBroFuzzWindow mWindow, String fileName) {
		this.mWindow = mWindow;
		JBroFuzzPrefs prefs = new JBroFuzzPrefs();
		File file = null;
		// Set the Fuzzing Panel as the one to view
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

		final JBroFuzzFileFilter filter = new JBroFuzzFileFilter();
		final String dirString = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DIRS[2].getId(), System.getProperty("user.dir"));

		JFileChooser fc = new JFileChooser();

		if ((fileName.length() == 0 || fileName.equals(""))
				&& (!prefs.DBSETTINGS[11].getId().equals("SQLite") && !prefs.DBSETTINGS[11]
						.getId().equals("CouchDB"))) {
			try {
				if ((new File(dirString).isDirectory())) {
					fc = new JFileChooser(dirString);
				} else {
					fc = new JFileChooser();
				}
			} catch (final SecurityException e1) {
				fc = new JFileChooser();
				Logger.log(
						"A security exception occured, while attempting to point to a directory",
						4);
			}

			fc.setFileFilter(filter);

			final int returnVal = fc.showOpenDialog(mWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				setUpFromFile(file, fc);
			}
		} else if ((fileName.length() > 0 && !fileName.equals(""))
				&& (!prefs.DBSETTINGS[11].getId().equals("SQLite") && !prefs.DBSETTINGS[11]
						.getId().equals("CouchDB"))) {
			file = new File(fileName);
			setUpFromFile(file, fc);
		} else if ((fileName.length() == 0 || fileName.equals(""))
				&& (prefs.DBSETTINGS[11].getId().equals("SQLite"))) {
			setUpFromSQLite(this.mWindow);
		} else if ((fileName.length() == 0 || fileName.equals(""))
				&& (prefs.DBSETTINGS[11].getId().equals("CouchDB"))) {
			setUpFromCouchDB(this.mWindow);
		}

	}

	/**
	 * load session from SQLiteDB
	 * 
	 * @author daemonmidi@gmail.com
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @since version 2.5
	 */
	public void setUpFromSQLite(JBroFuzzWindow mWindow) {
		
		SQLiteHandler sqlH = new SQLiteHandler();
		Connection conn;
		try {
			conn = sqlH.getConnection(JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[12].getId(), ""));
			sqlH.read(conn, -1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// TODO fill me

	}

	/**
	 * load session from couchdb
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 */
	public void setUpFromCouchDB(JBroFuzzWindow mWindow) {
		// TODO fill me

	}

	/**
	 * load session from file - to be consistent with command line interface
	 * style of *ix
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param file
	 * @param fc
	 */
	private void setUpFromFile(File file, JFileChooser fc) {
		final String path = file.getAbsolutePath().toLowerCase();
		// If the file does not end in .jbrofuzz, return
		JBroFuzzFileFilter jbfff = new JBroFuzzFileFilter();
		if (!path.endsWith(".jbrofuzz") || !jbfff.accept(file)) {
			JOptionPane.showMessageDialog(fc,
					"The file selected is not a valid .jbrofuzz file",
					" JBroFuzz - Open ", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Clear up the display
		mWindow.getPanelFuzzing().clearAllFields();

		// Start opening the file
		final StringBuffer fileContents = new StringBuffer();

		BufferedReader in = null;
		int counter = 0;
		try {

			in = new BufferedReader(new FileReader(file));

			int c;
			while (((c = in.read()) > 0) && (counter < MAX_CHARS)) {
				// Allow the character only if its printable ascii or \n
				if ((CharUtils.isAsciiPrintable((char) c))
						|| (((char) c) == '\n')) {
					fileContents.append((char) c);
				}
				counter++;
			}

			in.close();
		} catch (final FileNotFoundException e) {

			Logger.log("FileNotFoundException", 3);

		} catch (final IOException e) {

			Logger.log("IOException", 3);

		} finally {

			IOUtils.closeQuietly(in);

		}

		// Validate particular lines to particular values
		final String[] fileContentsArray = fileContents.toString().split("\n");
		final int fileNoOfLines = fileContentsArray.length;

		// Cannot have less than 12 lines
		if (fileNoOfLines < 12) {
			Logger.log("Invalid File: Contains less than 8 lines", 3);
			return;
		}

		if (!fileContentsArray[0].equals("[JBroFuzz]")) {
			Logger.log("Invalid File: Line 1 is not [JBroFuzz]", 3);
			return;
		}

		if (!fileContentsArray[2].equals("[Fuzzing]")) {
			Logger.log("Invalid File: Line 3 is not [Fuzzing]", 3);
			return;
		}

		if (!fileContentsArray[4].equals("[Comment]")) {
			Logger.log("Invalid File: Line 5 is not [Comment]", 3);
			return;
		}

		if (!fileContentsArray[6].equals("[URL]")) {
			Logger.log("Invalid File: Line 7 is not [URL]", 3);
			return;
		}

		if (!fileContentsArray[8].equals("[Request]")) {
			Logger.log("Invalid File: Line 9 is not [Request]", 3);
			return;
		}

		// Find the line where the [Fuzzers] line is
		int fuzzersLine = 0;
		for (int fuzzerLineCounter = fileNoOfLines - 1; fuzzerLineCounter > 0; fuzzerLineCounter--) {

			if (fileContentsArray[fuzzerLineCounter].equals("[Fuzzers]")) {
				// Check that there is only 1 instance
				if (fuzzersLine != 0) {
					Logger.log("Invalid File: Found 2 instances of [Fuzzers]",
							3);
					return;
				} else {
					fuzzersLine = fuzzerLineCounter;
				}

			}
		} // If you can't find the [Fuzzers] line, return
		if (fuzzersLine == 0) {
			Logger.log("Invalid File: Cannot find a [Fuzzers] line", 3);
			return;
		}

		// Find the line where the [Transforms] line is
		int transformsLine = 0;
		for (int transformsLineCounter = fileNoOfLines - 1; transformsLineCounter > 0; transformsLineCounter--) {

			if (fileContentsArray[transformsLineCounter].equals("[Transforms]")) {
				// Check that there is only 1 instance
				if (transformsLine != 0) {
					Logger.log(
							"Invalid File: Found 2 instances of [Transforms]",
							3);
					return;
				} else {
					transformsLine = transformsLineCounter;
				}
			}
		}
		if (transformsLine == 0) {
			Logger.log("Invalid File: Cannot find a [Transforms] line", 3);
			return;
		}

		// File ends with [End]
		if (!fileContentsArray[fileNoOfLines - 1].equals("[End]")) {
			Logger.log("Invalid File: Last line is not [End]", 3);
			return;
		}

		// -> Target URL
		final String targetString = StringUtils.abbreviate(
				fileContentsArray[7], MAX_CHARS);
		mWindow.getPanelFuzzing().setTextURL(targetString);

		// -> Request
		final StringBuffer requestBuffer = new StringBuffer();
		for (int reqLineCount = 9; reqLineCount < fuzzersLine; reqLineCount++) {
			requestBuffer.append(fileContentsArray[reqLineCount]);
			requestBuffer.append('\n');
		}
		mWindow.getPanelFuzzing().setTextRequest(requestBuffer.toString());

		// If more than 1024 lines of fuzzers, return
		if (fileNoOfLines - 1 - fuzzersLine - 1 > 1024) {
			Logger.log("Invalid File: More than 1024 Fuzzers Identified", 3);
			return;
		}

		// -> Load Fuzzers to Table
		for (int i = fuzzersLine + 1; i < transformsLine; i++) {

			final String[] payloadArray = fileContentsArray[i].split(",");

			// Each line must have 3 elements
			if (payloadArray.length != 3) {
				Logger.log("Invalid File: Line " + (i + 1)
						+ " does not contain 3 elements", 2);
				continue;
			}

			// Assign local variables for line: 044-USR-AGN,39,177
			// fuzz_id = 044-USR-AGN
			// start = 39
			// end = 177
			final String fuzzerID = payloadArray[0];
			int start = 0;
			int end = 0;

			// The fuzzer id must be valid
			if (!Prototype.isValidFuzzerID(fuzzerID)) {
				Logger.log("Fuzzer Line Syntax Error: " + (i + 1)
						+ " Invalid Fuzzer ID Format", 2);
				continue;
			}

			// The fuzzer id must also exist in the database
			if (!mWindow.getJBroFuzz().getDatabase()
					.containsPrototype(fuzzerID)) {
				Logger.log("Could not find Fuzzer with ID: " + fuzzerID, 2);
				continue;
			}

			// The start and end integers should be happy
			try {
				start = Integer.parseInt(payloadArray[1]);
				end = Integer.parseInt(payloadArray[2]);
			} catch (final NumberFormatException e) {
				Logger.log("Fuzzer Line Syntax Error: Number Format Exception",
						2);
				continue;
			}

			// Numbers must be positive
			if ((start < 0) || (end < 0)) {
				Logger.log("Fuzzer Line Syntax Error: Negative Value", 2);
				continue;
			}
			// Numbers must be less than the length of the request
			if ((start > requestBuffer.length())
					|| (end > requestBuffer.length())) {
				Logger.log(
						"Fuzzer Line Syntax Error: Value Larger than Request",
						2);
				continue;
			}

			Logger.log("Adding Fuzzer Line: " + "\t" + (i + 1) + "\t"
					+ fileContentsArray[i], 1);
			mWindow.getPanelFuzzing().addFuzzer(fuzzerID, start, end);

		}

		// -> Load Transforms to Table

		// If more than 1024 lines of transforms, return
		if (fileNoOfLines - 1 - transformsLine - 1 > 1024) {
			Logger.log("Invalid File: More than 1024 Transforms Identified", 3);
			return;
		}

		for (int j = transformsLine + 1; j < fileNoOfLines - 1; j++) {

			final String[] transformLineArray = fileContentsArray[j].split(",");
			final int noOfElements = transformLineArray.length;

			// Each line must have 4 elements commas with empty ,, give a count
			// of 2
			if ((noOfElements < 2) || (noOfElements > 4)) {
				Logger.log("Invalid File: Line " + (j + 1)
						+ " does not contain 4 elements", 2);
				continue;
			}

			// Assign local variables for line: 1,Hexadecimal (UPP),asdf,fdsa
			// fuzzerNumber = 1
			// encoder = Hexadecimal (UPP)
			// prefix = asdf
			// suffix = fdsa

			int fuzzerNumber = 0;
			// The transform number should be happy
			try {
				fuzzerNumber = Integer.parseInt(transformLineArray[0]);
			} catch (final NumberFormatException e) {
				Logger.log(
						"Transform Line Syntax Error: Number Format Exception",
						2);
				continue;
			}
			// Numbers must be greater than or equal to one
			if (fuzzerNumber < 1) {
				Logger.log("Transform Line Syntax Error: Value Less Than One",
						2);
				continue;
			}
			// Numbers must be less than the total number of fuzzers
			if (fuzzerNumber > transformsLine - fuzzersLine) {
				Logger.log(
						"Transform Line Syntax Error: Transform Outside Fuzzer Range",
						2);
				continue;
			}

			final String encoder = StringUtils.abbreviate(
					transformLineArray[1], MAX_CHARS);
			// The encoder code must be valid
			if (!EncoderHashCore.isValidCode(encoder)) {
				Logger.log(
						"Transform Line Syntax Error: Invalid Encode/Hash Code",
						2);
				continue;
			}

			String prefix = "";
			if (transformLineArray.length >= 3) {

				try {
					prefix = new String(
							Base64.decodeBase64(transformLineArray[2]), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Logger.log(
							"Transform Line Syntax Error: Cannot Decode Prefix",
							2);
					continue;
				}
				prefix = StringUtils.abbreviate(prefix, MAX_CHARS);

			}

			String suffix = "";
			if (transformLineArray.length >= 4) {

				try {
					suffix = new String(
							Base64.decodeBase64(transformLineArray[3]), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Logger.log(
							"Transform Line Syntax Error: Cannot Decode Suffix",
							2);
					continue;
				}
				suffix = StringUtils.abbreviate(suffix, MAX_CHARS);

			}

			Logger.log("Adding Transform Line:\t" + (j + 1)
					+ "\tOn Fuzzer Row:\t" + fuzzerNumber, 1);
			mWindow.getPanelFuzzing().addTransform(fuzzerNumber, encoder,
					prefix, suffix);

		}

		// Finally, tell the frame this is the file opened
		// and save the directory location
		mWindow.setOpenFileTo(file);
		final String parentDir = file.getParent();
		if (parentDir != null) {
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DIRS[2].getId(), parentDir);
		}
	}

	public JBroFuzzWindow getmWindow() {
		return mWindow;
	}
}