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
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * Class responsible for generating all the file I/O required for an instance of
 * JBroFuzz to function correctly.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class FileHandler {

	// The max value in bytes of the file being read 32 Mbytes
	public static final int MAX_BYTES = 33554432;

	// The main window frame gui
	private JBroFuzz mJBroFuzz;

	// The fuzz directory of operation
	private File fuzzDirectory;

	// The /jbrofuzz directory created at launch
	private File jbrfDirectory;

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
	public FileHandler(final JBroFuzz mJBroFuzz) {

		this.mJBroFuzz = mJBroFuzz;
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
	 * @version 1.9
	 * @since 1.5
	 */
	public final void createNewDirectory() {

		final StringBuffer directoryLocation = new StringBuffer();
		directoryLocation.append(System.getProperty("user.dir"));
		directoryLocation.append(File.separator);
		directoryLocation.append("jbrofuzz");

		// Create the /jbrofuzz directory in the current folder
		jbrfDirectory = new File(directoryLocation.toString());

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

				mJBroFuzz.getWindow().log(
						"The \"fuzz\" directory being used, already exists", 1);

			} else {

				final boolean success = fuzzDirectory.mkdirs();
				if (!success) {

					mJBroFuzz
					.getWindow()
					.log(
							"Failed to create new \"fuzz\" directory, no data will be written to file.",
							4);
					mJBroFuzz
					.getWindow()
					.log(
							"Are you using Vista? Right click on JBroFuzz and \"Run As Administrator\"",
							0);
				}

			}

		} else {
			// If the directory does not exist, create it
			
			final boolean success = fuzzDirectory.mkdirs();
			if (!success) {

				mJBroFuzz
				.getWindow()
				.log(
						"Failed to create \"fuzz\" directory, no data will be written to file.",
						4);
				mJBroFuzz
				.getWindow()
				.log(
						"Run JBroFuzz from the command line: \"java -jar JBroFuzz.jar\"",
						0);
				mJBroFuzz
				.getWindow()
				.log(
						"Are you using Vista? Right click on JBroFuzz and \"Run As Administrator\"",
						0);

			}

		}
	}

	/**
	 * <p>
	 * Method for deleting the empty directories at the end of a session.
	 * </p>
	 */
	public void deleteEmptryDirectories() {

		if (!fuzzDirectory.exists()) {
			mJBroFuzz
			.getWindow()
			.log("Could not find directory: "
					+ fuzzDirectory.getName(), 3);
			return;
		}

		if (FileUtils.sizeOfDirectory(fuzzDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(fuzzDirectory);

			} catch (final IOException e2) {
				mJBroFuzz.getWindow()
				.log("Could not delete directory: "
						+ fuzzDirectory.getName(), 3);
			}
		}

		final File parent = fuzzDirectory.getParentFile();

		if (!parent.exists()) {
			// g.getWindow.log("Could not find directory: " + parent.getName(), 3);
			return;
		}

		if (FileUtils.sizeOfDirectory(parent) == 0L) {
			try {
				FileUtils.deleteDirectory(parent);

			} catch (final IOException e) {
//				g.getWindow.log("Could not delete directory: "
//						+ parent.getName(), 3);
			}
		}

		if (!jbrfDirectory.exists()) {
//			g.getWindow.log("Could not find directory: "
//					+ jbrfDirectory.getName(), 3);
			return;
		}

		if (FileUtils.sizeOfDirectory(jbrfDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(jbrfDirectory);

			} catch (final IOException e) {
//				g.getWindow.log("Could not delete directory: "
//						+ jbrfDirectory.getName(), 3);

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
	 * @version 1.3
	 * @since 1.2
	 */
	public String getCanonicalPath() {

		try {
			return fuzzDirectory.getCanonicalPath();
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
	 * <p>Set the fuzzing directory where all the files will 
	 * be saved to the directory specified.</p>
	 * 
	 * @param directory
	 * @return true if succcessful, false if anything else
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.9
	 */
	public boolean setFuzzDirectory(File fuzzDirectory) {
		
		if(fuzzDirectory.isDirectory()) {
			this.fuzzDirectory = fuzzDirectory;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Method for returning the contents of a file in the 'fuzz' directory,
	 * created during startup.
	 * </p>
	 * 
	 * @param fileName
	 *            The name of the file, e.g. 01-0000001.html
	 * @return String The contents of the file as a string
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String readFuzzFile(String fileName) {

		final File file = new File(fuzzDirectory, fileName);
		if (file.exists()) {
			if (file.isDirectory()) {
				mJBroFuzz.getWindow().log(
						"File '" + file + "' exists but is a directory", 3);
				return ""; // new String();
			}
			if (file.canRead() == false) {
				mJBroFuzz.getWindow().log("File '" + file + "' cannot be read", 3);
				return ""; // new String();
			}
		} else {
			mJBroFuzz.getWindow().log("File '" + file + "' does not exist", 3);
			return ""; // new String();
		}

		InputStream in = null;
		FileInputStream fis = null;
		StringBuffer fileContents = new StringBuffer();
		try {
			fis = new FileInputStream(file);
			in = new BufferedInputStream(fis);

			int counter = 0;
			int c;
			// Read, having as upper maximum the int maximum
			while (((c = in.read()) > 0) && (counter <= MAX_BYTES)) {
				fileContents.append((char) c);
				counter++;
				if (counter == Integer.MAX_VALUE) {
					mJBroFuzz.getWindow().log(
							"Only displaying the first 2^31-1 bytes of the file '"
							+ file.getName(), 3);
				}
			}

			in.close();
			fis.close();

		} catch (IOException e) {
			mJBroFuzz.getWindow()
			.log(
					"Opening File '" + file.getName()
					+ "' caused an I/O error", 4);

		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(fis);
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
			mJBroFuzz.getWindow().log("Error writting fuzz file: " + fileName, 3);
		}
	}

}
