/**
 * JBroFuzz 2.1
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
package org.owasp.jbrofuzz.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.MessageWriter;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>
 * Class responsible for generating all the file I/O required for an instance of
 * JBroFuzz to function correctly.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 1.2
 */
public class FileHandler {

	// The max value in bytes of the file being read 32 Mbytes
	public static final int MAX_BYTES = 33554432;

	// The fuzz directory of operation
	private File fuzzDirectory;

	// The /jbrofuzz directory created at launch
	private File rootDirectory;

	// A counter for any File -> New directories created
	private int count;

	/**
	 * <p>
	 * Constructor for the file handler, having as parameter the main instance
	 * of JBroFuzz.
	 * </p>
	 * <p>
	 * It will attempt to create the 'fuzz' directory from the current unique
	 * time-stamp.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public FileHandler() {

		count = 0;
		createNewDirectory();

	}

	/**
	 * <p>Method for creating a new directory with the date/time stamp.</p>
	 * 
	 * <p>If the directory with the current timestamp exists, a number from 
	 * 000 to 999 is padded to the current timestamp and a new directory 
	 * is created.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.5
	 */
	public final void createNewDirectory() {

		// Get the root directory location from preferences
		final String dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[0].getId(), System.getProperty("user.dir"));
		
		// Create the /jbrofuzz directory in the current folder
		rootDirectory = new File(dirString);

		final StringBuffer directoryLocation = new StringBuffer();
		directoryLocation.append(dirString);
		directoryLocation.append(File.separator);
		directoryLocation.append("jbrofuzz");

		directoryLocation.append(File.separator);
		directoryLocation.append("fuzz");
		directoryLocation.append(File.separator);
		directoryLocation.append(JBroFuzzFormat.DATE);

		// Create the necessary directory with the corresponding timestamp
		fuzzDirectory = new File(directoryLocation.toString());

		// If the directory is already present, create a directory with a
		// number at the end
		if (fuzzDirectory.exists()) {
			
			count++;
			count %= 1000;

			directoryLocation.append('.');

			if (count < 10) {
				directoryLocation.append('0');
			}
			if (count < 100) {
				directoryLocation.append('0');
			}
			directoryLocation.append(count);

			// Create the necessary directory with the corresponding timestamp
			fuzzDirectory = new File(directoryLocation.toString());

			if (fuzzDirectory.exists()) {

				Logger.log(
						"The \"fuzz\" directory being used, already exists", 1);

			} else {

				final boolean success = fuzzDirectory.mkdirs();
				if (!success) {

					Logger
					.log(
							"Failed to create new \"fuzz\" directory, no data will be written to file.",
							4);
					Logger
					.log(
							"Are you using Vista? Right click on JBroFuzz and \"Run As Administrator\"",
							0);
				}

			}

		} else {
			// If the directory does not exist, create it
			
			final boolean success = fuzzDirectory.mkdirs();
			if (!success) {

				Logger
				.log(
						"Failed to create \"fuzz\" directory, no data will be written to file.",
						4);
				Logger
				.log(
						"Run JBroFuzz from the command line: \"java -jar JBroFuzz.jar\"",
						0);
				Logger
				.log(
						"Are you using Vista? Right click on JBroFuzz and \"Run As Administrator\"",
						0);

			}

		}
	}

	

	/**
	 * <p>
	 * Return the canonical path of the directory location of the 'fuzz'
	 * directory.
	 * </p>
	 * 
	 * @return String the path of the 'fuzz' directory
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.2
	 */
	public String getCanonicalPath() {

		try {
			return rootDirectory.getCanonicalPath();
		} catch (IOException e) {
			return "";
		}

	}

	public File getFuzzFile(String fileName) {

		return new File(fuzzDirectory, fileName);

	}

	/**
	 * <p>Return the directory which is currently being used 
	 * for fuzz data</p>
	 * 
	 * @return File
	 */
	public File getFuzzDirectory() {

		return fuzzDirectory;
	}
	
	/**
	 * <p>Method for returning the contents of any file as 
	 * String.</p>
	 * <p>In the event of an error, the String returned is
	 * the error message triggered.</p>
	 * 
	 * @param inputFile File
	 *            
	 * @return String The contents of the file as a string
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public static String readFile(File inputFile) {

		final String fileName = inputFile.toString();
		
		if (inputFile.exists()) {
			if (inputFile.isDirectory()) {
				
				return "File is a directory:\n\n" + fileName;
			}
			if (!inputFile.canRead()) {
				
				return "File cannot be read:\n\n" + fileName;
				
			}
		} else {
			
			return "File does not exist:\n\n" + fileName;
			
		}

		int counter = 0;
		InputStream in = null;
		FileInputStream fis = null;
		StringBuffer fileContents = new StringBuffer();
		try {
			fis = new FileInputStream(inputFile);
			in = new BufferedInputStream(fis);

			int c;
			// Read, having as upper maximum the int maximum
			while (((c = in.read()) > 0) && (counter <= MAX_BYTES)) {
				fileContents.append((char) c);
				counter++;
				
			}

			in.close();
			fis.close();

		} catch (IOException e) {
			
			return "Attempting to open the file caused an I/O Error:\n\n" + fileName;

		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(fis);
		}
		
		if(counter == MAX_BYTES) {
			fileContents.append("\n... stopped reading after 32 Mbytes.\n");
		}
		return fileContents.toString();
	}

	public void writeFuzzFile(MessageWriter outputMessage) {

		final String fileName = outputMessage.getFileName() + ".html";

		File toWrite = new File(fuzzDirectory, fileName);

		try {
			FileUtils.touch(toWrite);
			FileUtils.writeStringToFile(toWrite, outputMessage.toString());
		} catch (IOException e) {
			Logger.log("Error writting fuzz file: " + fileName, 3);
		}
	}

}
