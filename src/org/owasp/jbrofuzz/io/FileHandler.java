/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.MessageWriter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

public class FileHandler {

	// The main window frame gui
	private JBroFuzz g;

	// The fuzz directory of operation
	private File fuzzDirectory;
	
	// The /jbrofuzz directory created at launch
	private File DIR_JBROFUZZ;

	public FileHandler(final JBroFuzz g) {

		this.g = g;

		final String baseDir = System.getProperty("user.dir");
		// Create the /jbrofuzz directory in the current folder
		DIR_JBROFUZZ = new File(baseDir + File.separator + "jbrofuzz");

		// Create the necessary directory with the corresponding timestamp
		fuzzDirectory = new File(baseDir + File.separator + "jbrofuzz"
				+ File.separator + "fuzz" + File.separator
				+ JBroFuzzFormat.DATE);

		int failedDirCounter = 0;

		if (!fuzzDirectory.exists()) {
			boolean success = fuzzDirectory.mkdirs();
			if (!success) {
				g.getWindow().log("Failed to create \"fuzz\" directory");
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

		if (!fuzzDirectory.exists()) {
			System.out.println("Could not find directory: "
					+ fuzzDirectory.getName());
			return;
		}
		
		if (FileUtils.sizeOfDirectory(fuzzDirectory) == 0L) {
			try {
				FileUtils.deleteDirectory(fuzzDirectory);

			} catch (final IOException e2) {
				System.out.println("Could not delete directory: "
						+ fuzzDirectory.getName());
			}
		}

		final File parent = fuzzDirectory.getParentFile();

		if (!parent.exists()) {
			System.out.println("Could not find directory: "
					+ parent.getName());
			return;
		}
		
		if (FileUtils.sizeOfDirectory(parent) == 0L) {
			try {
				FileUtils.deleteDirectory(parent);

			} catch (final IOException e) {
				System.out.println("Could not delete directory: " 
						+ parent.getName());
			}
		}

		if (!DIR_JBROFUZZ.exists()) {
			System.out.println("Could not find directory: "
					+ DIR_JBROFUZZ.getName());
			return;
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

		return new File(fuzzDirectory, fileName);

	}

	public StringBuffer readFuzzFile(String fileName) {
		StringBuffer out = new StringBuffer();

		File f = new File(fuzzDirectory, fileName);
		try {
			out.append(FileUtils.readFileToString(f));
		} catch (IOException e) {
			g.getWindow().log("An error reading the fuzz file: " + fileName);
		}

		return out;
	}

	public void writeFuzzFile(MessageWriter outputMessage) {

		final String fileName = outputMessage.getFileName() + ".html";
		
		File toWrite = new File(fuzzDirectory, fileName);
		
		try {
			FileUtils.touch(toWrite);
			FileUtils.writeStringToFile(toWrite, outputMessage.toString());
		} catch (IOException e) {
			g.getWindow().log("Error writting fuzz file: " + fileName);
		}
	}

	public String getCanonicalPath() {
		
		try {
			return fuzzDirectory.getCanonicalPath();
		} catch (IOException e) {
			return "";
		}
			
	}

	

}
