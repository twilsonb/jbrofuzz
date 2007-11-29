/**
 * FileHandler.java 0.8
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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

import javax.swing.JOptionPane;

import org.owasp.jbrofuzz.fuzz.TConstructor;
import org.owasp.jbrofuzz.fuzz.dir.DConstructor;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.version.JBRFormat;

import org.apache.commons.io.*;

/**
 * <p>Class responsible for all file handling activities.</p>
 * 
 * <p>At runtime, a number of directories are created within the directory
 * from which JBroFuzz is launched.</p>
 * 
 * <p>Any empty directories are removed when the application is 
 * closed down.</p>
 * 
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class FileHandler {
	// The singleton object
	private static FileHandler singletonFileHandlerObject;

	// The main window frame gui
	private static JBRFrame g;

	// The main format object
	private static JBRFormat f;

	// The current file used for creation
	private static File currentFile;

	// The fuzz directory of operation
	private static File fuzzDirectory;

	// The http fuzz directory of operation
	private static File httpFuzzDirectory;

	// The snif directory of operation
	private static File snifDirectory;

	// The info directory of operation
	private static File webEnumDirectory;

	// A constant for counting file IO errors
	private static int errors = 0;

	// Global constants
	private static final int FUZZ_FILE = 0;

	private static final int SNIF_FILE = 1;

	private static final int WEBD_FILE = 2;

	private static final int HTTP_FILE = 3;

	public static final int DIR_HTTP = 10;
	public static final int DIR_SNIF = 11;
	public static final int DIR_WEBD = 12;
	public static final int DIR_TCPF = 13;

	// The date from the version
	private static String runningDate;

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
			FileHandler.g.log("Cannot find " + file + "Unable to Update");
			FileHandler.errors++;
		} catch (final IOException e) {
			FileHandler.g.log("Cannot Save to File" + file
					+ "A File Write Error Occured");
			FileHandler.errors++;
		} finally {
			IOUtils.closeQuietly( output );
		}
	}

	private static void createFile(final String fileName, 
			final String content,
			final int fileType) {


		if (fileType == FileHandler.HTTP_FILE) {
			try {
				if (FileHandler.errors < 3) {
					FileHandler.currentFile = new File(FileHandler.httpFuzzDirectory,
							fileName);
					if (!FileHandler.currentFile.exists()) {
						boolean success = FileHandler.currentFile.createNewFile();
						if (!success) {
							FileHandler.g.log("Failed to create file");
						}
					}
					FileHandler.appendFile(FileHandler.currentFile, content);
				}
			} catch (final IOException e) {
				FileHandler.g.log("Cannot Create File" + "\n" + fileName
						+ " A File Error Occured");
				FileHandler.errors++;
			}

		}

		if (fileType == FileHandler.FUZZ_FILE) {
			try {
				if (FileHandler.errors < 3) {
					FileHandler.currentFile = new File(FileHandler.fuzzDirectory,
							fileName);
					if (!FileHandler.currentFile.exists()) {
						boolean success = FileHandler.currentFile.createNewFile();
						if (!success) {
							FileHandler.g.log("Failed to create file");
						}
					}
					FileHandler.appendFile(FileHandler.currentFile, content);
				}
			} catch (final IOException e) {
				FileHandler.g.log("Cannot Create File" + "\n" + fileName
						+ " A File Error Occured");
				FileHandler.errors++;
			}

		}

		if (fileType == FileHandler.SNIF_FILE) {
			try {
				if (FileHandler.errors < 3) {
					FileHandler.currentFile = new File(FileHandler.snifDirectory,
							fileName);
					if (!FileHandler.currentFile.exists()) {
						boolean success = FileHandler.currentFile.createNewFile();
						if (!success) {
							FileHandler.g.log("Failed to create file");
						}
					}
					FileHandler.appendFile(FileHandler.currentFile, content);
				}
			} catch (final IOException e) {
				FileHandler.g.log("Cannot Create File" + "\n" + fileName
						+ " A File Error Occured");
				FileHandler.errors++;
			}
		}

		if (fileType == FileHandler.WEBD_FILE) {
			try {
				if (FileHandler.errors < 3) {
					FileHandler.currentFile = new File(FileHandler.webEnumDirectory,
							fileName);
					if (!FileHandler.currentFile.exists()) {
						boolean success = FileHandler.currentFile.createNewFile();
						if (!success) {
							FileHandler.g.log("Failed to create file");
						}
					}
					FileHandler.appendFile(FileHandler.currentFile, content);
				}
			} catch (final IOException e) {
				FileHandler.g.log("Cannot Create File" + "\n" + fileName
						+ " A File Error Occured");
				FileHandler.errors++;
			}
		}
	}

	/**
	 * <p>
	 * Method for returning an integer array of hashes for each file found inside
	 * the session fuzz directory created at runtime.
	 * </p>
	 * <p>
	 * Each hash is an integer between the value of [0 - 1000] and is calculated
	 * by means of consecutive addition of the byte values found on each line.
	 * </p>
	 * <p>
	 * As a result of this, files with small alterations e.g. date and time stamps
	 * will have little differences in their hash value, thus keeping the hamming
	 * distance between them to a minimum.
	 * </p>
	 * 
	 * @return int[] An array of integers, of length the same as the number of
	 *         files
	 * @since 0.6
	 */
	public static int[] getFuzzDirFileHashes() {

		final File[] folderFiles = FileHandler.fuzzDirectory.listFiles();
		final int[] hashValue = new int[folderFiles.length];

		for (int i = 0; i < folderFiles.length; i++) {
			BufferedReader bufRead = null;
			try {
				final FileReader input = new FileReader(folderFiles[i]);
				bufRead = new BufferedReader(input);
				String line;
				boolean passedResponse = false;
				line = bufRead.readLine();
				while (line != null) {
					if (line.startsWith(JBRFormat.LINE_SEPARATOR)) {
						passedResponse = true;
					}
					if (passedResponse) {
						final byte[] b_array = line.getBytes();
						for (final byte element : b_array) {
							hashValue[i] += element;
							hashValue[i] %= 1000;
						}
					}
					line = bufRead.readLine();
				}
				bufRead.close();
			} catch (final ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(FileHandler.g, "Cannot Find Location"
						+ "\n" + folderFiles[i].getName() + "\nAn Array Error Occured",
						"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			} catch (final IOException e) {
				JOptionPane.showMessageDialog(FileHandler.g, "Cannot Read Location"
						+ "\n" + folderFiles[i].getName() + "\nA File Read Error Occured",
						"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				IOUtils.closeQuietly( bufRead );
			}
		}
		return hashValue;
	}

	/**
	 * <p>
	 * Method for returning an integer array of hashes for each file found inside
	 * the session fuzz directory created at runtime.
	 * </p>
	 * <p>
	 * Each hash is an integer between the value of [0 - 1000] and is calculated
	 * by means of consecutive addition of the byte values found on each line.
	 * </p>
	 * <p>
	 * As a result of this, files with small alterations e.g. date and time stamps
	 * will have little differences in their hash value, thus keeping the hamming
	 * distance between them to a minimum.
	 * </p>
	 * 
	 * @return String[] hashValue
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

	/**
	 * <p>Method for returning the name of the directory specified.</p>
	 * <p>If the directory number is unknown, an empty String is returned.</p>
	 * 
	 * @param directory
	 * @return
	 */
	public static String getName(final int directory) {

		switch(directory) {
		case DIR_HTTP: return FileHandler.httpFuzzDirectory.getName();
		case DIR_TCPF: return FileHandler.fuzzDirectory.getName();
		case DIR_WEBD: return FileHandler.webEnumDirectory.getName();
		case DIR_SNIF: return FileHandler.snifDirectory.getName();
		default: return "";
		}

	}




	/**
	 * <p>Method for returning the canonical path of a directory specified.</p>
	 * <p>If the directory number is unknown, an empty String is returned.</p>
	 * 
	 * @param directory
	 * @return 
	 */
	public static String getCanonicalPath(final int directory) {
		try {
			switch(directory) {
			case DIR_HTTP: return FileHandler.httpFuzzDirectory.getCanonicalPath();
			case DIR_TCPF: return FileHandler.fuzzDirectory.getCanonicalPath();
			case DIR_WEBD: return FileHandler.webEnumDirectory.getCanonicalPath();
			case DIR_SNIF: return FileHandler.snifDirectory.getCanonicalPath();
			default: return "";
			}
		}
		catch(final IOException e1) {
			return "";
		}
	}

	/**
	 * <p>
	 * Method for returning the contents of a directories file as a StringBuffer.
	 * </p>
	 * <p>
	 * Comment lines starting with '#' will be ignored and not returned as
	 * contents of the StringBuffer.
	 * </p>
	 * 
	 * @param directoriesFile
	 *          String
	 * @return StringBuffer
	 */
	public static StringBuffer readDirectories(final String directoriesFile) {
		final int maxLines = 100000;
		final int maxLineLength = 256;
		int line_counter = 0;
		final Vector<String> file = new Vector<String>();
		BufferedReader in = null;
		int len = 0;
		// First, attempt to read the file from the same directory
		try {
			in = new BufferedReader(new FileReader(directoriesFile));
			String line = in.readLine();
			line_counter++;
			// Check for max lines and line lengths
			while ((line != null) && (line_counter < maxLines)) {
				if (line.length() > maxLineLength) {
					line = line.substring(0, maxLineLength);
				}
				if (!line.startsWith("#")) {
					file.add(line);
					line_counter++;
				}
				line = in.readLine();
			}
			in.close();
		} catch (final IOException e1) {
			if (FileHandler.g != null) {
				FileHandler.g.log("Directories file: " + directoriesFile
						+ " could not be found");
			}
		} finally {
			IOUtils.closeQuietly( in );
		}
		// Check the file size
		file.trimToSize();
		len = file.size();
		// If reading from directory fails, attempt to read from the jar file
		if (len <= 0) {
			line_counter = 0;

			final URL fileURL = ClassLoader.getSystemClassLoader().getResource(
					JBRFormat.FILE_DIR);

			try {
				final URLConnection connection = fileURL.openConnection();
				connection.connect();

				in = new BufferedReader(new InputStreamReader(connection
						.getInputStream()));
				String line = in.readLine();
				line_counter++;
				while ((line != null) && (line_counter < maxLines)) {
					if (line.length() > maxLineLength) {
						line = line.substring(0, maxLineLength);
					}
					if (!line.startsWith("#")) {
						file.add(line);
						line_counter++;
					}
					line = in.readLine();
				}
				in.close();
			} catch (final IOException e1) {
				if (FileHandler.g != null) {
					FileHandler.g.log("Directories file (inside jar): "
							+ fileURL.toString() + " could not be found");
				}
			} finally {
				IOUtils.closeQuietly( in );
			}
		}
		// Check the file size
		file.trimToSize();
		len = file.size();

		// If reading from directory and jar fails define the generators from a
		// default list
		if (len <= 0) {
			if (FileHandler.g != null) {
				FileHandler.g.log("Loading default directories list");
			}
			final String[] defaultArray = JBRFormat.DEFAULT_DIRS.split("\n");
			len = defaultArray.length;
			file.setSize(len);
			for (int x = 0; x < len; x++) {
				file.add(x, defaultArray[x]);
			}
		}

		final StringBuffer output = new StringBuffer();
		for (int x = 0; x < file.size(); x++) {
			final String s = file.elementAt(x);
			if (s != null) {
				output.append(s + "\n");
			}
		}
		return output;
	}

	/**
	 * <p>
	 * Method for reading fuzz files that have been generated within a fuzzing
	 * session. Typically, the contents of the file are returned within the
	 * StringBuffer. In the event of an error, the StringBuffer returned is set to
	 * "".
	 * </p>
	 * 
	 * @param fileName
	 *          String
	 * @return StringBuffer
	 */
	public static StringBuffer readFuzzFile(final String fileName) {
		final StringBuffer out = new StringBuffer();
		File file;
		try {
			file = new File(FileHandler.fuzzDirectory, fileName);
		} catch (final NullPointerException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Find Location"
					+ "\n" + fileName + "\nA File Read Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");
		}
		BufferedReader bufRead = null;
		try {
			final FileReader input = new FileReader(file);
			bufRead = new BufferedReader(input);
			String line;
			line = bufRead.readLine();
			while (line != null) {
				out.append(line + "\n");
				line = bufRead.readLine();
			}
			bufRead.close();
		} catch (final ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Find Location"
					+ "\n" + fileName + "\nAn Array Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");

		} catch (final IOException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Read Location"
					+ "\n" + fileName + "\nA File Read Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");
		} finally {
			IOUtils.closeQuietly( bufRead );
		}
		return out;
	}

	/**
	 * <p>
	 * Method for reading fuzz files that have been generated within a fuzzing
	 * session. Typically, the contents of the file are returned within the
	 * StringBuffer. In the event of an error, the StringBuffer returned is set to
	 * "".
	 * </p>
	 * 
	 * @param fileName
	 *          String
	 * @return StringBuffer
	 */
	public static StringBuffer readHTTPFuzzFile(final String fileName) {
		final StringBuffer out = new StringBuffer();
		File file;
		try {
			file = new File(FileHandler.httpFuzzDirectory, fileName);
		} catch (final NullPointerException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Find Location"
					+ "\n" + fileName + "\nA File Read Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");
		}
		BufferedReader bufRead = null;
		try {
			final FileReader input = new FileReader(file);
			bufRead = new BufferedReader(input);
			String line;
			line = bufRead.readLine();
			while (line != null) {
				out.append(line + "\n");
				line = bufRead.readLine();
			}
			bufRead.close();
		} catch (final ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Find Location"
					+ "\n" + fileName + "\nAn Array Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");

		} catch (final IOException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Read Location"
					+ "\n" + fileName + "\nA File Read Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");
		} finally {
			IOUtils.closeQuietly( bufRead );
		}
		return out;
	}

	/**
	 * <p>
	 * Method for returning the contents of a generator file as a StringBuffer.
	 * </p>
	 * <p>
	 * Comment lines starting with '#' will be ignored and not returned as
	 * contents of the StringBuffer.
	 * </p>
	 * <p>
	 * This method, initially looks for the file in the same directory as that in
	 * which JBroFuzz has been run.
	 * </p>
	 * <p>
	 * If this is unsuccessful, it attempts to load it from within the jar file.
	 * </p>
	 * <p>
	 * If this is unsuccessful, it loads a default list from the Format file. This
	 * list is a lot shorter than the complete list of generators, inside the two
	 * files.
	 * </p>
	 * 
	 * @param generatorFile
	 *          String
	 * @return StringBuffer
	 */
	public static StringBuffer readGenerators(final String generatorFile) {
		// The maximum number of lines
		final int maxLines = 1024;
		// The maximum line length
		final int maxLineLength = 256;

		int line_counter = 0;
		final Vector<String> file = new Vector<String>();
		BufferedReader in = null;
		int len = 0;
		// First, attempt to read the file from the same directory
		try {
			in = new BufferedReader(new FileReader(generatorFile));
			String line = in.readLine();
			line_counter++;
			while ((line != null) && (line_counter < maxLines)) {
				if (line.length() > maxLineLength) {
					line = line.substring(0, maxLineLength);
				}
				if (!line.startsWith("#")) {
					file.add(line);
					line_counter++;
				}
				line = in.readLine();
			}
			in.close();
		} catch (final IOException e1) {
			if (FileHandler.g != null) {
				FileHandler.g.log("Generator file: " + generatorFile
						+ " could not be found");
			}
		} finally {
			IOUtils.closeQuietly( in );
		}
		// Check the file size
		file.trimToSize();
		len = file.size();

		// If reading from directory fails, attempt to read from the jar file
		if (len <= 0) {
			line_counter = 0;

			final URL fileURL = ClassLoader.getSystemClassLoader().getResource(
					JBRFormat.FILE_GEN);

			try {
				final URLConnection connection = fileURL.openConnection();
				connection.connect();

				in = new BufferedReader(new InputStreamReader(connection
						.getInputStream()));
				String line = in.readLine();
				line_counter++;
				while ((line != null) && (line_counter < maxLines)) {
					if (line.length() > maxLineLength) {
						line = line.substring(0, maxLineLength);
					}
					if (!line.startsWith("#")) {
						file.add(line);
						line_counter++;
					}
					line = in.readLine();
				}
				in.close();
			} catch (final IOException e1) {
				if (FileHandler.g != null) {
					FileHandler.g.log("Generator file (inside jar): "
							+ fileURL.toString() + " could not be found");
				}
			} finally {
				IOUtils.closeQuietly( in );
			}
		}
		// Check the file size
		file.trimToSize();
		len = file.size();

		// If reading from directory and jar fails define the generators from a
		// default list
		if (len <= 0) {
			FileHandler.g.log("Loading default generator list");
			final String[] defaultArray = JBRFormat.DEFAULT_GENS.split("\n");
			len = defaultArray.length;
			file.setSize(len);
			for (int x = 0; x < len; x++) {
				file.add(x, defaultArray[x]);
			}
		}

		final StringBuffer output = new StringBuffer();
		for (int x = 0; x < file.size(); x++) {
			final String s = (String) file.elementAt(x);
			if (s != null) {
				output.append(s + "\n");
			}
		}
		return output;
	}

	/**
	 * <p>
	 * Method for reading files that have been generated during a sniffing
	 * session.
	 * </p>
	 * <p>
	 * When a user selects to start a sniffing session, by selecting the <i>Start</i>
	 * button in the <i>TCP Sniffing</i> tab, any traffic in terms of requests
	 * and replies being made on the corresponding ports is saved in the
	 * corresponding <i>sniffing directory</i>, unique to the current session.
	 * <p>
	 * This method provides the way to access any file within the <i>sniffing
	 * directory</i> used, provided the file name is known.
	 * </p>
	 * <p>
	 * The contents of the file are returned as a StringBuffer. In the event of an
	 * error, the StringBuffer returned is set to an empty String of value "".
	 * </p>
	 * 
	 * @param fileName
	 *          String The name of the file, without any directory reference
	 * @return StringBuffer The StringBuffer with the contents of the file
	 * @since 0.7
	 */
	public static StringBuffer readSnifFile(final String fileName) {
		final StringBuffer out = new StringBuffer();
		File file;
		try {
			file = new File(FileHandler.snifDirectory, fileName);
		} catch (final NullPointerException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Find Location"
					+ "\n" + fileName + "\nA File Read Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");
		}
		BufferedReader bufRead = null;
		try {
			final FileReader input = new FileReader(file);
			bufRead = new BufferedReader(input);
			String line;
			line = bufRead.readLine();
			while (line != null) {
				out.append(line + "\n");
				line = bufRead.readLine();
			}
			bufRead.close();
		} catch (final ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Find Location"
					+ "\n" + fileName + "\nAn Array Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");

		} catch (final IOException e) {
			JOptionPane.showMessageDialog(FileHandler.g, "Cannot Read Location"
					+ "\n" + fileName + "\nA File Read Error Occured",
					"JBroFuzz File Read Error", JOptionPane.ERROR_MESSAGE);
			return new StringBuffer("");
		} finally {
			IOUtils.closeQuietly( bufRead );
		}
		return out;
	}

	/**
	 * <p>
	 * Singleton Constructor responsible for generating the necessary directories
	 * and files for the correct operation of JBroFuzz.
	 * </p>
	 * 
	 * @param g
	 *          FrameWindow
	 * @param f
	 * @return FileHandler
	 */
	public static synchronized FileHandler s(final JBRFrame g, final JBRFormat f) {
		if (FileHandler.singletonFileHandlerObject == null) {
			FileHandler.singletonFileHandlerObject = new FileHandler(g, f);
		}
		return FileHandler.singletonFileHandlerObject;
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
	 * If the file exists, the content is simply appended to the end of the file.
	 * </p>
	 * 
	 * @param content
	 *          String
	 * @param name
	 *          String
	 */
	public static void writeFuzzFile(final String content, final String name) {
		// Actually create the file
		FileHandler.createFile(name + ".html", content, FileHandler.FUZZ_FILE);
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
	 * If the file exists, the content is simply appended to the end of the file.
	 * </p>
	 * 
	 * @param content
	 *          String
	 * @param name
	 *          String
	 */
	public static void writeHTTPFuzzFile(final String content, final String name) {
		// Actually create the file
		FileHandler.createFile(name + ".html", content, FileHandler.FUZZ_FILE);
	}

	/**
	 * <p>
	 * Method for writting a new snif file within the created sniffing directory.
	 * The file name and content is specified as a string input to the method. The
	 * location where this file is saved is within the directory
	 * jbrofuzz\sniffing\[session date]\ .
	 * </p>
	 * <p>
	 * If the file exists, the content is simply appended to the end of the file.
	 * </p>
	 * 
	 * @param name
	 *          String
	 * @param content
	 *          String
	 */
	public static void writeSnifFile(final String name, final String content) {
		// Actually create the file
		FileHandler.createFile(name + ".html", content, FileHandler.SNIF_FILE);
	}

	/**
	 * <p>
	 * Method for writting a new web directories file wtin the created web-dir
	 * directory. The file name and content is specified as a string input to the
	 * method.
	 * </p>
	 * <p>
	 * The location where this file is saved is within the directory
	 * jbrofuzz\web-dir\[session date]\ .
	 * </p>
	 * <p>
	 * If the file exists, the content is simply appended to the end of the file.
	 * </p>
	 * 
	 * @param name
	 *          String The name of the file
	 * @param content
	 *          String The content to be written to disk
	 */
	public static void writeWebDirFile(final String name, final String content) {
		FileHandler.createFile(name + ".csv", content, FileHandler.WEBD_FILE);
	}

	//
	// Private Constructor due to the use of a singleton architecture
	//
	public FileHandler(final JBRFrame g, final JBRFormat f) {

		FileHandler.g = g;
		FileHandler.f = f;

		// Date and current directory
		FileHandler.runningDate = FileHandler.f.getDate();
		final String baseDir = System.getProperty("user.dir");

		// Create the necessary directory with the corresponding timestamp
		FileHandler.fuzzDirectory = new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "fuzzing-tcp" + File.separator + FileHandler.runningDate);

		FileHandler.snifDirectory = new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "sniffing" + File.separator
				+ FileHandler.runningDate);

		FileHandler.webEnumDirectory = new File(baseDir + File.separator
				+ "jbrofuzz" + File.separator + "web-dir" + File.separator
				+ FileHandler.runningDate);

		FileHandler.httpFuzzDirectory = new File(baseDir + File.separator
				+ "jbrofuzz" + File.separator + "fuzzing-http" + File.separator
				+ FileHandler.runningDate);

		int failedDirCounter = 0;

		if (!FileHandler.fuzzDirectory.exists()) {
			boolean success = FileHandler.fuzzDirectory.mkdirs();
			if (!success) {
				g.log("Failed to create \"fuzzing\" directory");
				failedDirCounter++;
			}
		}

		if (!FileHandler.snifDirectory.exists()) {
			boolean success = FileHandler.snifDirectory.mkdirs();
			if (!success) {
				g.log("Failed to create \"sniffing\" directory");
				failedDirCounter++;
			}

		}

		if (!FileHandler.webEnumDirectory.exists()) {
			boolean success = FileHandler.webEnumDirectory.mkdirs();
			if (!success) {
				g.log("Failed to create \"web-dir\" directory");
				failedDirCounter++;
			}
		}

		if (!FileHandler.httpFuzzDirectory.exists()) {
			boolean success = FileHandler.httpFuzzDirectory.mkdirs();
			if (!success) {
				g.log("Failed to create \"fuzzing-http\" directory");
				failedDirCounter++;
			}
		}

		if (failedDirCounter >= 4) {
			g
			.log("\tToo many directories could not be created! Are you launching me through your browser?");
			g.log("\tTry \"java -jar jbrofuzz-" + JBRFormat.VERSION
					+ ".jar\" on command line...");
			failedDirCounter = 0;
		}

		// Load the necessary files into the various panels of the application
		new DConstructor(g.getJBroFuzz());
		new TConstructor(g.getJBroFuzz());
	}

	/**
	 * <p>
	 * This method overides the clone method of Object, so that not to support
	 * cloning for this particular object. This is done to follow singleton best
	 * practice implementation.
	 * </p>
	 * 
	 * @return Object
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * <p>
	 * Method for deleting the empty directories at the end of a session.
	 * </p>
	 * 
	 * @return int the number of dirs deleted
	 */
	public int deleteEmptryDirectories() {
		int count = 0;

		if(FileUtils.sizeOfDirectory(fuzzDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(fuzzDirectory);
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		if(FileUtils.sizeOfDirectory(httpFuzzDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(httpFuzzDirectory);
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		if(FileUtils.sizeOfDirectory(snifDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(snifDirectory);
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		if(FileUtils.sizeOfDirectory(webEnumDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(webEnumDirectory);
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		final String baseDir = System.getProperty("user.dir");

		if(FileUtils.sizeOfDirectory(new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "fuzzing-tcp" + File.separator)) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(baseDir + File.separator + "jbrofuzz"
						+ File.separator + "fuzzing-tcp" + File.separator));
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		if(FileUtils.sizeOfDirectory(new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "sniffing" + File.separator)) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(baseDir + File.separator + "jbrofuzz"
						+ File.separator + "sniffing" + File.separator));
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		if(FileUtils.sizeOfDirectory(new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "web-dir" + File.separator)) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(baseDir + File.separator + "jbrofuzz"
						+ File.separator + "web-dir" + File.separator));
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		if(FileUtils.sizeOfDirectory(new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "fuzzing-http" + File.separator)) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(baseDir + File.separator + "jbrofuzz"
						+ File.separator + "fuzzing-http" + File.separator));
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		if(FileUtils.sizeOfDirectory(new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator)) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(baseDir + File.separator + "jbrofuzz"
						+ File.separator));
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		return count;
	}

}
