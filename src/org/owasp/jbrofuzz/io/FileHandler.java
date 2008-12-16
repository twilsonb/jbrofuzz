/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
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
 * 
 */
package org.owasp.jbrofuzz.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * Class responsible for all file handling activities.
 * </p>
 * 
 * <p>
 * At runtime, a number of directories are created within the directory from
 * which JBroFuzz is launched.
 * </p>
 * 
 * <p>
 * Any empty directories are removed when the application is closed down.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 1.2
 */
public class FileHandler {

	// The main window frame gui
	private static JBroFuzz g;

	// The current file used for creation
	private static File currentFile;

	// The fuzz directory of operation
	private static File fuzzDirectory;

	// The snif directory of operation
	private static File autoDirectory;

	// A constant for counting file IO errors
	private static int errors = 0;

	// Global constants
	public static final int FILE_FUZZ = 0;
	public static final int FILE_AUTO = 1;

	public static final int DIR_FUZZ = 10;

	public static final int DIR_AUTO = 11;

	// The /jbrofuzz directory created at launch
	private File DIR_JBROFUZZ;

	//
	// Private Constructor due to the use of a singleton architecture
	//
	public FileHandler(final JBroFuzz g) {

		FileHandler.g = g;

		final String baseDir = System.getProperty("user.dir");
		// Create the /jbrofuzz directory in the current folder
		DIR_JBROFUZZ = new File(baseDir + File.separator + "jbrofuzz");

		// Create the necessary directory with the corresponding timestamp
		fuzzDirectory = new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "fuzz" + File.separator
				+ JBroFuzzFormat.DATE);

		autoDirectory = new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "auto" + File.separator
				+ JBroFuzzFormat.DATE);

		int failedDirCounter = 0;

		if (!FileHandler.fuzzDirectory.exists()) {
			boolean success = FileHandler.fuzzDirectory.mkdirs();
			if (!success) {
				g.getWindow().log("Failed to create \"fuzz\" directory");
				failedDirCounter++;
			}
		}

		if (!FileHandler.autoDirectory.exists()) {
			boolean success = FileHandler.autoDirectory.mkdirs();
			if (!success) {
				g.getWindow().log("Failed to create \"auto\" directory");
				failedDirCounter++;
			}

		}

		if (failedDirCounter >= 4) {
			g
			.getWindow()
			.log(
					"\tToo many directories could not be created! Are you launching me through your browser?");
			g.getWindow().log(
					"\tTry \"java -jar jbrofuzz-" + JBroFuzzFormat.VERSION
					+ ".jar\" on command line...");
			failedDirCounter = 0;
		}

	}

	/**
	 * <p>
	 * Method for deleting the empty directories at the end of a session.
	 * </p>
	 */
	public void deleteEmptryDirectories() {

		if (FileUtils.sizeOfDirectory(fuzzDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(fuzzDirectory);

			} catch (final IOException e) {
				System.out.println("Could not delete directory: "
						+ fuzzDirectory.getName());
			}
		}

		if (FileUtils.sizeOfDirectory(autoDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(autoDirectory);

			} catch (final IOException e) {
				System.out.println("Could not delete directory: "
						+ autoDirectory.getName());
			}
		}

		if (FileUtils.sizeOfDirectory(new File(fuzzDirectory.getParent())) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(fuzzDirectory.getParent()));

			} catch (final IOException e) {
				System.out.println("Could not delete directory: fuzz");
			}
		}

		if (FileUtils.sizeOfDirectory(new File(autoDirectory.getParent())) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(autoDirectory.getParent()));

			} catch (final IOException e) {
				System.out.println("Could not delete directory: auto");

			}
		}

		if (FileUtils.sizeOfDirectory(DIR_JBROFUZZ) == 0L) {
			try {
				FileUtils.deleteDirectory(DIR_JBROFUZZ);

			} catch (final IOException e) {
				System.out.println("Could not delete directory: "
						+ DIR_JBROFUZZ.getName());

			}
		}

	}

	public File getFuzzFile(String fileName) {

		return new File(FileHandler.fuzzDirectory, fileName);

	}

	public StringBuffer readFuzzFile(String fileName) {
		StringBuffer out = new StringBuffer();

		File f = new File(FileHandler.fuzzDirectory, fileName);
		try {
			out.append(FileUtils.readFileToString(f));
		} catch (IOException e) {
			g.getWindow().log("An error reading the fuzz file: " + fileName);
		}

		return out;
	}

	/**
	 * <p>
	 * Method for writting a new fuzz file within the created fuzzing directory.
	 * The content of the file is specified as a String input to the method. The
	 * location where this file is saved is within the directory jbrofuzz\
	 * fuzzing\[session date]\ .
	 * </p>
	 * <p>
	 * The two long values being passed are responsible for the file name.
	 * </p>
	 * <p>
	 * If the file exists, the content is simply appended to the end of the
	 * file.
	 * </p>
	 * 
	 * @param content
	 *            String
	 * @param name
	 *            String
	 */
	public void writeFuzzFile2(final String content, String name) {

		name += ".html";

		File f = new File(FileHandler.fuzzDirectory, name);

		try {
			FileUtils.touch(f);
			FileUtils.writeStringToFile(f, content);
		} catch (IOException e) {
			g.getWindow().log("Error writting fuzz file: " + name);
		}
	}

	//
	// Private method for appending data to a file, given the name and content
	//
	private static void appendFile(final File fileName, final String content) {
		final String file = fileName.toString();
		OutputStream output = null;
		try {
			if (FileHandler.errors < 3) {
				// content += "\r\n";
				final boolean append = true;
				output = new FileOutputStream(file, append);
				final byte buffer[] = content.getBytes();
				output.write(buffer);
				output.close();
			}
		} catch (final FileNotFoundException e) {
			FileHandler.g.getWindow().log(
					"Cannot find " + file + "Unable to Update");
			FileHandler.errors++;
		} catch (final IOException e) {
			FileHandler.g.getWindow()
			.log(
					"Cannot Save to File" + file
					+ "A File Write Error Occured");
			FileHandler.errors++;
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	/**
	 * <p>
	 * Method for returning the canonical path of a directory specified.
	 * </p>
	 * <p>
	 * If the directory number is unknown, an empty String is returned.
	 * </p>
	 * 
	 * @param directory
	 * @return
	 */
	public static String getCanonicalPath(final int directory) {
		try {
			switch (directory) {
			case DIR_FUZZ:
				return FileHandler.fuzzDirectory.getCanonicalPath();
			case DIR_AUTO:
				return FileHandler.autoDirectory.getCanonicalPath();
			default:
				return "";
			}
		} catch (final IOException e1) {
			return "";
		}
	}

	/**
	 * <p>
	 * Method for returning the file names created inside the fuzz directory.
	 * </p>
	 * 
	 * @return String[] filenames
	 * @version 1.0
	 * @since 0.6
	 */
	public static String[] getFileList() {

		final File[] folderFiles = FileHandler.fuzzDirectory.listFiles();
		final String[] hashValue = new String[folderFiles.length];

		for (int i = 0; i < folderFiles.length; i++) {
			hashValue[i] = folderFiles[i].getName();
		}
		return hashValue;

	}

	public static int getFuzzDirBigestFile() {

		final File[] folderFiles = FileHandler.fuzzDirectory.listFiles();
		long maxValue = 0;

		for (File f : folderFiles) {
			if (f.length() > maxValue) {
				maxValue = f.length();
			}
		}

		return (int) maxValue;
	}

	public static int getFuzzFileHash(String fileName) {

		File f = new File(FileHandler.fuzzDirectory, fileName);

		int hashValue = 0;

		BufferedReader bufRead = null;
		try {
			final FileReader input = new FileReader(f);
			bufRead = new BufferedReader(input);
			String line;
			boolean passedResponse = false;
			line = bufRead.readLine();
			while (line != null) {
				if (line.startsWith(JBroFuzzFormat.LINE_SEPARATOR)) {
					passedResponse = true;
				}
				if (passedResponse) {
					final byte[] b_array = line.getBytes();
					for (final byte element : b_array) {
						hashValue += element;
						hashValue %= 1000;
					}
				}
				line = bufRead.readLine();
			}
			bufRead.close();
			// hashValue[i] = (i * 100) % 1000;
		} catch (final ArrayIndexOutOfBoundsException e) {
			g.getWindow().log(
					"Cannot Find Location" + "\n" + f.getName()
					+ "\nAn Array Error Occured "
					+ "JBroFuzz File Read Error");
		} catch (final IOException e) {
			g.getWindow().log(
					"Cannot Read Location" + "\n" + f.getName()
					+ "\nA File Read Error Occured"
					+ "JBroFuzz File Read Error");
		} finally {
			IOUtils.closeQuietly(bufRead);
		}

		return hashValue;
	}

	public static int getFuzzFileSize(String fileName) {

		File f = new File(FileHandler.fuzzDirectory, fileName);

		int hashValue = 0;

		hashValue = (int) (f.length());

		return hashValue;
	}

	/**
	 * <p>
	 * Method for returning the name of the directory specified.
	 * </p>
	 * <p>
	 * If the directory number is unknown, an empty String is returned.
	 * </p>
	 * 
	 * @param directory
	 * @return
	 */
	public static String getName(final int directory) {

		switch (directory) {
		case DIR_FUZZ:
			return FileHandler.fuzzDirectory.getName();
		case DIR_AUTO:
			return FileHandler.autoDirectory.getName();
		default:
			return "";
		}

	}

}
