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
package org.owasp.jbrofuzz.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>
 * Utility class which supports static log calls.
 * </p>
 * <p>
 * Logs console events in a designated log file (/log/<date>-log.txt)
 * </p>
 * 
 * @author ranulf@seleucus.net
 * @version 1.0
 * @since 2.0
 */
public final class Logger {

	private static File logDirectory;

	private Logger() { } // Private constructor, to avoid accidents
	
	/**
	 * <p>Method responsible for returning the current log 
	 * file.</p>
	 * 
	 * @return File The current log file
	 * @throws IOException
	 */
	protected static File getLogFile() throws IOException{
		
		Logger.checkOrCreateDirs();
		
		final Date currentTime = new Date();
		final SimpleDateFormat dateTime = new SimpleDateFormat(
				"dd.MM.yyyy", new Locale("en"));
		final String fileName = dateTime.format(currentTime)+"-log.txt";
		final File toWrite = new File(logDirectory, fileName);
		
		return toWrite;
		
	}

	/**
	 * <p>Create or check that the directories within which the log
	 * file will be created, are there.</p>
	 * 
	 * <p>This method will create the "./jbrofuzz/log/" directory
	 * if required.</p>
	 * 
	 * @throws IOException
	 */
	private static void checkOrCreateDirs() throws IOException{
		// Get the root directory location from preferences
		final String dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[0].getId(), System.getProperty("user.dir"));

		new File(dirString);

		final StringBuffer directoryLocation = new StringBuffer();
		directoryLocation.append(dirString);
		directoryLocation.append(File.separator);
		directoryLocation.append("jbrofuzz");

		directoryLocation.append(File.separator);
		directoryLocation.append("log");

		// Create the necessary directory with the corresponding timestamp
		logDirectory = new File(directoryLocation.toString());

		// If the directory does not exist, create it.
		if (!logDirectory.exists()) {
			final boolean success = logDirectory.mkdirs();
			if (!success) {
				throw new IOException();
			}

		}

	}

	
	/**
	 * <p>
	 * Method for logging values within the system event log.
	 * </p>
	 * 
	 * @param str
	 *            String The text to be logged<br>
	 * @param level
	 *            The severity level:<br><br>
	 *            <= 0 => [INFO] Blue Informational <br>
	 *            == 1 => [OPPR] Green Operational <br>
	 *            == 2 => [WARN] Yellow Warning <br>
	 *            == 3 => [SHOT] Amber Shout - light error <br> 
	 *            >= 4 => [ERRR] Red Error <br>
	 * 
	 */
	public static void log(final String str, final int level){
		final Date currentTime = new Date();
		final SimpleDateFormat dateTime = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss", new Locale("en"));

		final StringBuffer toLog = new StringBuffer();
		toLog.append('[');
		toLog.append(dateTime.format(currentTime));
		toLog.append(']');

		if (level <= 0) {
			toLog.append(" [INFO] ");
		} else if (level == 1) {
			toLog.append(" [OPPR] ");
		} else if (level == 2) {
			toLog.append(" [WARN] ");
		} else if (level == 3) {
			toLog.append(" [SHOT] ");
		} else {
			toLog.append(" [ERRR] ");
		}
		toLog.append(str);
		toLog.append('\n');

		// append the contents to the current log file
		try{
			appendToLogFile(toLog.toString());
		}catch(IOException ioe){
			ioe.printStackTrace();
		}


	}


	private static void appendToLogFile(final String error) throws IOException {
		checkOrCreateDirs();
		final Date currentTime = new Date();
		final SimpleDateFormat dateTime = new SimpleDateFormat(
				"dd.MM.yyyy", new Locale("en"));
		final String fileName = dateTime.format(currentTime)+"-log.txt";
		final File toWrite = new File(logDirectory, fileName);
		try {
			
			final FileWriter fWriter = new FileWriter(toWrite,true);
			fWriter.write(error);
			fWriter.close();
			
		} catch (IOException e) {
			throw new IOException();
		}


	}

	protected static ArrayList<String> readLogFile(final long oldLength) 
									throws FileNotFoundException, IOException{
		
		final RandomAccessFile rFile = new RandomAccessFile(getLogFile(),"r");
		rFile.seek(oldLength);
		
		final ArrayList<String> lines = new ArrayList<String>();
		String line;

		while((line = rFile.readLine())!= null){
			lines.add(line);

		}

		return lines;

	}

	/**
	 * <p>Health-check method, showing a variety of properties and 
	 * information.</p>
	 * 
	 */
	protected static void log(){

		log("[System Health Check Start]", 2);
		log("[System Info Start]", 1);
		
		final String systemInfo = 
			"  [Java]=    Vendor:  " + System.getProperty("java.vendor") 
			+ "=    Version: " + System.getProperty("java.version") 
			+ "=    Installed at: " + System.getProperty("java.home")
			+ "=    Website: " + System.getProperty("java.vendor.url") 
			+ "=  [User]=" + "    User: " + System.getProperty("user.name") 
			+ "=    Home dir: " + System.getProperty("user.home") 
			+ "=    Current dir: " + System.getProperty("user.dir") 
			+ "=  [O/S]=    Name: " + System.getProperty("os.name")
			+ "=    Version: " + System.getProperty("os.version")
			+ "=    Architecture: " + System.getProperty("os.arch")
			+ "=";
		
		final String[] info = systemInfo.split("=");

		for (final String element : info) {
			log(element, 0);
		}
		log("[System Info End]", 1);

		log("[Testing Warning Levels Start]", 1);
		log("  Informational - no issue - no tab counter increment", 0);
		log("  Opperational - operation changed - no tab counter increment", 1);
		log("  Warning - still functional - an operation change did not complete", 2);
		log("  Shout - controlable error occured - bad news but not the worst", 3);
		log("  Error - something that was meant to happen, didn't complete as expected", 4);
		log("[Testing Warning Levels End]", 1);
		log("[System Health Check End]", 2);

	}

}
