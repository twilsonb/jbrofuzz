/**
 * JBroFuzz 1.0
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

import org.owasp.jbrofuzz.*;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import org.apache.commons.io.*;

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

	private static void createFile(final String fileName, final String content,
			final int fileType) {

		if (fileType == FileHandler.FILE_FUZZ) {
			try {
				if (FileHandler.errors < 3) {
					FileHandler.currentFile = new File(
							FileHandler.fuzzDirectory, fileName);
					if (!FileHandler.currentFile.exists()) {
						boolean success = FileHandler.currentFile
								.createNewFile();
						if (!success) {
							FileHandler.g.getWindow().log(
									"Failed to create file");
						}
					}
					FileHandler.appendFile(FileHandler.currentFile, content);
				}
			} catch (final IOException e) {
				FileHandler.g.getWindow().log(
						"Cannot Create File" + "\n" + fileName
								+ " A File Error Occured");
				FileHandler.errors++;
			}

		}

		if (fileType == FileHandler.FILE_AUTO) {
			try {
				if (FileHandler.errors < 3) {
					FileHandler.currentFile = new File(
							FileHandler.autoDirectory, fileName);
					if (!FileHandler.currentFile.exists()) {
						boolean success = FileHandler.currentFile
								.createNewFile();
						if (!success) {
							FileHandler.g.getWindow().log(
									"Failed to create file");
						}
					}
					FileHandler.appendFile(FileHandler.currentFile, content);
				}
			} catch (final IOException e) {
				FileHandler.g.getWindow().log(
						"Cannot Create File" + "\n" + fileName
								+ " A File Error Occured");
				FileHandler.errors++;
			}
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
		
		for(File f : folderFiles) {
			if(f.length() > maxValue) {
				maxValue = f.length();
			}
		}
		
		return (int) maxValue;
	}
	
	public static int getFuzzFileSize(String fileName) {
		
		File f = new File(FileHandler.fuzzDirectory, fileName);
		
		int hashValue = 0;
		
		hashValue = (int) (f.length() ); 
		
		return hashValue;
	}

	public static int getFuzzFileHash(String fileName) {

		File f =  new File(FileHandler.fuzzDirectory, fileName);
		
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
						"Cannot Find Location" + "\n"
								+ f.getName()
								+ "\nAn Array Error Occured "
								+ "JBroFuzz File Read Error");
			} catch (final IOException e) {
				g.getWindow().log(
						"Cannot Read Location" + "\n"
								+ f.getName()
								+ "\nA File Read Error Occured"
								+ "JBroFuzz File Read Error");
			} finally {
				IOUtils.closeQuietly(bufRead);
			}
		
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

	/**
	 * <p>
	 * Method for returning the contents of a directories file as a
	 * StringBuffer.
	 * </p>
	 * <p>
	 * Comment lines starting with '#' will be ignored and not returned as
	 * contents of the StringBuffer.
	 * </p>
	 * 
	 * @param directoriesFile
	 *            String
	 * @return StringBuffer
	 */
	public static StringBuffer readDirectories(final String directoriesFile) {
		
		final int maxLines = 100000;
		final int maxLineLength = 256;
		int line_counter = 0;
		final Vector<String> file = new Vector<String>();
		BufferedReader in = null;
		int len = 0;

		// If reading from directory fails, attempt to read from the jar file
		if (len <= 0) {
			line_counter = 0;

			final URL fileURL = ClassLoader.getSystemClassLoader().getResource(
					JBroFuzzFormat.FILE_DIR);

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
					FileHandler.g.getWindow().log(
							"Directories file (inside jar): "
									+ fileURL.toString()
									+ " could not be found");
				}
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		// Check the file size
		file.trimToSize();
		len = file.size();

		// If reading from directory and jar fails define the generators from a
		// default list
		if (len <= 0) {
			if (FileHandler.g != null) {
				FileHandler.g.getWindow().log(
						"Loading default directories list");
			}
			final String[] defaultArray = JBroFuzzFormat.DEFAULT_DIRS.split("\n");
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

	//
	// Private Constructor due to the use of a singleton architecture
	//
	public FileHandler(final JBroFuzz g) {

		FileHandler.g = g;

		final String baseDir = System.getProperty("user.dir");

		// Create the necessary directory with the corresponding timestamp
		FileHandler.fuzzDirectory = new File(baseDir + File.separator
				+ "jbrofuzz" + File.separator + "fuzz" + File.separator
				+ JBroFuzzFormat.DATE);

		FileHandler.autoDirectory = new File(baseDir + File.separator
				+ "jbrofuzz" + File.separator + "auto" + File.separator
				+ JBroFuzzFormat.DATE);

		int failedDirCounter = 0;

		if (!FileHandler.fuzzDirectory.exists()) {
			boolean success = FileHandler.fuzzDirectory.mkdirs();
			if (!success) {
				g.getWindow().log("Failed to create \"fuzzing\" directory");
				failedDirCounter++;
			}
		}

		if (!FileHandler.autoDirectory.exists()) {
			boolean success = FileHandler.autoDirectory.mkdirs();
			if (!success) {
				g.getWindow().log("Failed to create \"sniffing\" directory");
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

		// Load the necessary files into the various panels of the application
		// new DConstructor(g);

	}

	/**
	 * <p>
	 * Method for reading files that have been generated during a sniffing
	 * session.
	 * </p>
	 * <p>
	 * When a user selects to start a sniffing session, by selecting the
	 * <i>Start</i> button in the <i>TCP Sniffing</i> tab, any traffic in
	 * terms of requests and replies being made on the corresponding ports is
	 * saved in the corresponding <i>sniffing directory</i>, unique to the
	 * current session.
	 * <p>
	 * This method provides the way to access any file within the <i>sniffing
	 * directory</i> used, provided the file name is known.
	 * </p>
	 * <p>
	 * The contents of the file are returned as a StringBuffer. In the event of
	 * an error, the StringBuffer returned is set to an empty String of value
	 * "".
	 * </p>
	 * 
	 * @param fileName
	 *            String The name of the file, without any directory reference
	 * @return StringBuffer The StringBuffer with the contents of the file
	 * @since 0.7
	 * 
	 * public static StringBuffer readSnifFile(final String fileName) { final
	 * StringBuffer out = new StringBuffer(); File file; try { file = new
	 * File(FileHandler.snifDirectory, fileName); } catch (final
	 * NullPointerException e) { g.getWindow().log("Cannot Find Location" + "\n" +
	 * fileName + "\nA File Read Error Occured " + "JBroFuzz File Read Error");
	 * return new StringBuffer(""); } BufferedReader bufRead = null; try { final
	 * FileReader input = new FileReader(file); bufRead = new
	 * BufferedReader(input); String line; line = bufRead.readLine(); while
	 * (line != null) { out.append(line + "\n"); line = bufRead.readLine(); }
	 * bufRead.close(); } catch (final ArrayIndexOutOfBoundsException e) {
	 * g.getWindow().log("Cannot Find Location" + "\n" + fileName + "\nAn Array
	 * Error Occured " + "JBroFuzz File Read Error"); return new
	 * StringBuffer(""); } catch (final IOException e) {
	 * g.getWindow().log("Cannot Read Location" + "\n" + fileName + "\nA File
	 * Read Error Occured " + "JBroFuzz File Read Error"); return new
	 * StringBuffer(""); } finally { IOUtils.closeQuietly( bufRead ); } return
	 * out; }
	 */

	/**
	 * <p>
	 * Method for deleting the empty directories at the end of a session.
	 * </p>
	 * 
	 * @return int the number of dirs deleted
	 */
	public int deleteEmptryDirectories() {
		int count = 0;

		if (FileUtils.sizeOfDirectory(fuzzDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(fuzzDirectory);
				count++;
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		if (FileUtils.sizeOfDirectory(autoDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(autoDirectory);
				count++;
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		if (FileUtils.sizeOfDirectory(new File(fuzzDirectory.getParent())) == 0L) {
			try {
				FileUtils.deleteDirectory(new File(fuzzDirectory.getParent()));
				count++;
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		return count;
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
	 * 
	 * public static void writeFuzzFile(final String content, final String name) { //
	 * Actually create the file FileHandler.createFile(name + ".html", content,
	 * FileHandler.FUZZ_FILE); }
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

}
