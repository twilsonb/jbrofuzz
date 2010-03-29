/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.fuzz.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.fuzz.ui.FuzzerTable;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.JBroFuzzFileFilter;

public class OpenSession {

	// The maximum number of chars to be read from file, regardless
	private final static int MAX_CHARS = Short.MAX_VALUE;

	public OpenSession(JBroFuzzWindow mWindow) {

		// Set the Fuzzing Panel as the one to view
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);
		Logger.log("Open Fuzzing Session", 1);

		JBroFuzzFileFilter filter = new JBroFuzzFileFilter();

		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(mWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fc.getSelectedFile();
			Logger.log("Opening: " + file.getName(), 1);

			String path = file.getAbsolutePath().toLowerCase();
			// If the file does not end in .jbrofuzz, return
			if (!path.endsWith(".jbrofuzz")) {

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
			try {

				in = new BufferedReader(new FileReader(file));

				int counter = 0;
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

			} catch (FileNotFoundException e) {

				Logger.log("FileNotFoundException", 3);

			} catch (IOException e) {

				Logger.log("IOException", 3);

			} finally {

				IOUtils.closeQuietly(in);

			}

			// Validate it to extremes
			final String[] fileInput = fileContents.toString().split("\n");
			final int len = fileInput.length;

			// Check the number of lines
			if (len < 8)
				return;
			// Check the location of each of the fields
			if (!fileInput[0].equals("[JBroFuzz]"))
				return;
			if (!fileInput[2].equals("[Fuzzing]"))
				return;
			if (!fileInput[4].equals("[Comment]"))
				return;
			if (!fileInput[6].equals("[URL]"))
				return;
			if (!fileInput[8].equals("[Request]"))
				return;
			// Check that the file finishes with an 'End'
			if (!fileInput[len - 1].equals("[End]"))
				return;

			// Find the line where the 'Payloads' are
			int payloadsLine = 0;
			for (int i = len - 1; i >= 0; i--) {

				if (fileInput[i].equals("[Payloads]")) {
					// Check that there is only 1 instance
					if (payloadsLine != 0) {
						return;
					} else {
						payloadsLine = i;
					}

				}

			}

			// If you can't find the 'Payloads' line, return
			if (payloadsLine == 0)
				return;

			// Get the request from the file
			StringBuffer _reqBuffer = new StringBuffer();
			for (int i = 9; i < payloadsLine; i++) {
				_reqBuffer.append(fileInput[i] + "\n");
			}

			// If the number of available payload lines is greater than 1024,
			// return
			if (len - 1 - payloadsLine - 1 > 1024)
				return;

			// Get the payloads from the file
			for (int i = payloadsLine + 1; i < len - 1; i++) {

				boolean fuzzer_happy = true;

				String[] payloadArray = fileInput[i].split(",");
				// Each line must have 4 elements
				if (payloadArray.length == 4) {

					String fuzz_id = payloadArray[0];
					String encoding_ = payloadArray[1];
					int start = 0;
					int end = 0;
					// The fuzzer id must also exist in the database
					if (!mWindow.getJBroFuzz().getDatabase().containsPrototype(
							fuzz_id)) {
						fuzzer_happy = false;
					}

					// Work on the encoding you are reading in
					boolean encoding_found = false;
					for (String lamda : FuzzerTable.ENCODINGS) {
						if(lamda.equalsIgnoreCase(encoding_)) {
							encoding_found = true;
						}
					}

					// Set the default encoding, the first one
					if(!encoding_found) {
						encoding_ = FuzzerTable.ENCODINGS[0];
					}


					// The start and end integers should be happy
					try {
						start = Integer.parseInt(payloadArray[2]);
						end = Integer.parseInt(payloadArray[3]);
						// Numbers must be positive
						if ((start < 0) || (end < 0)) {
							fuzzer_happy = false;
						}
						// Numbers must be less than the length of the request
						if ((start > _reqBuffer.length())
								|| (end > _reqBuffer.length())) {
							fuzzer_happy = false;
						}
					} catch (NumberFormatException e) {
						fuzzer_happy = false;
					}

					if (!fuzzer_happy) {
						Logger.log("Could not open and add Fuzzer: "
								+ fileInput[i], 3);
					} else {

						mWindow.getPanelFuzzing().addFuzzer(fuzz_id, encoding_, start, end);

					}
				}
			}

			// These max values of abbreviation are also used in the Fuzzing
			// Panel
			// geters
			String _req = StringUtils.abbreviate(_reqBuffer.toString(), 16384);
			String _url = StringUtils.abbreviate(fileInput[7], 1024);

			mWindow.getPanelFuzzing().setTextRequest(_req);
			mWindow.getPanelFuzzing().setTextURL(_url);
			// Finally, tell the frame this is the file opened
			mWindow.setOpenFileTo(file);

		}

	}
}
