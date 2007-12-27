/**
 * Database.java 0.8
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon org
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
package org.owasp.jbrofuzz.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.owasp.jbrofuzz.JBroFuzz;

import org.owasp.jbrofuzz.version.JBRFormat;

import com.Ostermiller.util.*;
/**
 * <p>
 * A database is a collection of exploits, grouped together under a number of different categories.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.8
 */
public class Database {

	private JBroFuzz mJBroFuzz;

	private String [] [] exploits;

	/**
	 * <p>The main constructor for the database, requiring the main JBroFuzz object to be passed.</p>
	 * 
	 * @param mJBroFuzz
	 */
	public Database(final JBroFuzz mJBroFuzz) {

		this.mJBroFuzz = mJBroFuzz;

		final int maxLines = 10000;
		final int maxLineLength = 10000;

		int line_counter = 0;
		BufferedReader in = null;

		final StringBuffer fileContents = new StringBuffer();

		// Attempt to read from the jar file
		final URL fileURL = ClassLoader.getSystemClassLoader().getResource(JBRFormat.FILE_GNU);

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
				fileContents.append(line + "\n");
				line = in.readLine();
			}
			in.close();
		} 
		catch (final IOException e1) {
			System.out.println("Directories file (inside jar): "
					+ fileURL.toString() + " could not be found");
		} 
		finally {
			IOUtils.closeQuietly( in );
		}
		// Parse the contents of the StringBuffer to the exploits array

		exploits = ExcelCSVParser.parse(fileContents.toString());

		for (final String[] element : exploits) {
			for (int j = 1; j < element.length; j++) {
				int count = 10;
				while((element[j].startsWith(" ")) && (count > -1)) {
					element[j] = element[j].substring(1);
					count--;
				}
				while((element[j].endsWith(" ")) && (count > -1)) {
					element[j] = element[j].substring(0, (element[j].length() - 1));
					count--;
				}
			}
		}

	} // constructor

	/**
	 * <p>Get the complete number of exploits, as the number of rows within the database.</p>
	 * 
	 * @return int
	 */
	public int getTotalExploitCount() {
		return exploits.length;
	}

	/**
	 * <p>Method for returning all exploit names, including
	 * any dublicates that might be present.</p>
	 * 
	 * @return String[]
	 */
	public String[] getAllNames() {
		final String[] output = new String[exploits.length];
		for (int i=1; i<exploits.length; i++){
			if(!exploits[i][0].isEmpty()) {
				output[i] = exploits[i][0];
			}
		}
		return output;
	}

	/**
	 * <p>Method for returning all unique category names, 
	 * as part of the Database.</p>
	 * 
	 * @return String[]
	 */
	public String[] getAllCategories() {

		final ArrayList<String> uniqueCategories = new ArrayList<String>();

		// Loop through the exploit categories
		for (int i=1; i<exploits.length; i++){
			final String[] categoriesLine = exploits[i][3].split(",");

			// Loop through the exploit category names found for that exploit
			for(String categoryItemonLine : categoriesLine) {

				// Remove any trailing or leading white spaces, up to 10
				int count = 10;
				while((categoryItemonLine.startsWith(" ")) && (count > -1)) {
					categoryItemonLine = categoryItemonLine.substring(1);
					count--;
				}
				while((categoryItemonLine.endsWith(" ")) && (count > -1)) {
					categoryItemonLine = categoryItemonLine.substring(0, (categoryItemonLine.length() - 1));
					count--;
				}

				// 
				boolean catFound = false;
				for(final String uniqueCategory : uniqueCategories) {
					if( categoryItemonLine.equals(uniqueCategory) && !categoryItemonLine.isEmpty() ) {
						catFound = true;
					}
				}
				if((!catFound) ) {
					uniqueCategories.add(categoryItemonLine);
				}
			}
		}
		uniqueCategories.trimToSize();
		final String [] outputCategories = new String[uniqueCategories.size()];
		return uniqueCategories.toArray(outputCategories);
	}

	/**
	 * <p>Method for obtaining the complete list of exploit names, as a String
	 * array, for a given category name.</p>
	 * 
	 * @param category
	 * @return String[]
	 */
	public String[] getNames(final String category) {
		final ArrayList<String> uniqueCategories = new ArrayList<String>();

		for (int i=1; i<exploits.length; i++){
			final String[] categoriesLine = exploits[i][3].split(",");
			for(String categoryItemonLine : categoriesLine) {

				//			 Remove any trailing or leading white spaces, up to 10
				int count = 10;
				while((categoryItemonLine.startsWith(" ")) && (count > -1)) {
					categoryItemonLine = categoryItemonLine.substring(1);
					count--;
				}
				while((categoryItemonLine.endsWith(" ")) && (count > -1)) {
					categoryItemonLine = categoryItemonLine.substring(0, (categoryItemonLine.length() - 1));
					count--;
				}

				if(categoryItemonLine.equalsIgnoreCase(category)) {

					boolean nameExists = false;
					for(final String existingName : uniqueCategories) {

						final String currentName = exploits[i][0];
						if(existingName.equalsIgnoreCase(currentName)) {
							nameExists = true;
						}
					}

					if((!nameExists) && (!exploits[i][0].isEmpty())) {
						uniqueCategories.add(exploits[i][0]);
					}
				}
			}
		}
		uniqueCategories.trimToSize();
		final String [] outputCategories = new String[uniqueCategories.size()];
		return uniqueCategories.toArray(outputCategories);
	}

	/**
	 * <p>Method for returning the actual exploit code, as a String
	 * for the given exploit name. If the name does not exist, an empty
	 * String is returned.</p>
	 * 
	 * @param name
	 * @return String
	 */
	public String getExploit(final String name) {
		String output = "";
		for (int i=1; i<exploits.length; i++){
			if((name.equalsIgnoreCase(exploits[i][0])) && (!exploits[i][0].isEmpty())) {
				output = exploits[i][2];
			}
		}
		return output;
	}

	/**
	 * <p>Method for returning the comment for the given exploit name. 
	 * If the name does not exist an empty String is returned.</p>
	 * 
	 * @param name
	 * @return String
	 */
	public String getComment(final String name) {
		final StringBuffer output = new StringBuffer();
		for (int i=1; i<exploits.length; i++){
			if((name.equalsIgnoreCase(exploits[i][0])) && (!exploits[i][0].isEmpty())) {
				output.append("ID: " + i + "\n\n" + exploits[i][1] + "\n\nAuthor: ");
				if(exploits[i].length > 3) {
					output.append(exploits[i][4] + "\n\nURL: ");
				}
				if(exploits[i].length > 4) {
					output.append(exploits[i][5]);
				}
			}
		}
		return output.toString();
	}

	/**
	 * <p>Method returning the main JBroFuzz object used by the constructor.</p>
	 * 
	 * @return JBroFuzz
	 */
	public JBroFuzz getJBroFuzz() {
		return mJBroFuzz;
	}

	/**
	 * <p>Method returns the categories found in which a particular exploit belongs to.
	 * If no categories are found, all categories are returned.</p>
	 * 
	 * @param name
	 * @return
	 */
	public String[] getCategories(final String name) {
		final ArrayList<String> uniqueCategories = new ArrayList<String>();
		boolean nameFound = false;
		// Loop through the exploit categories
		for (int i=1; i<exploits.length; i++){

			if(name.equalsIgnoreCase(exploits[i][0])) {
				nameFound = true;
				final String[] categoriesLine = exploits[i][3].split(",");

				// Loop through the exploit category names found for that exploit
				for(String categoryItemonLine : categoriesLine) {

					// Remove any trailing or leading white spaces, up to 10
					int count = 10;
					while((categoryItemonLine.startsWith(" ")) && (count > -1)) {
						categoryItemonLine = categoryItemonLine.substring(1);
						count--;
					}
					while((categoryItemonLine.endsWith(" ")) && (count > -1)) {
						categoryItemonLine = categoryItemonLine.substring(0, (categoryItemonLine.length() - 1));
						count--;
					}

					// 
					boolean catFound = false;
					for(final String uniqueCategory : uniqueCategories) {
						if( categoryItemonLine.equals(uniqueCategory) && !categoryItemonLine.isEmpty() ) {
							catFound = true;
						}
					}
					if((!catFound) ) {
						uniqueCategories.add(categoryItemonLine);
					}
				}
			}// If name found
		}

		if(nameFound) {
			uniqueCategories.trimToSize();
			final String [] outputCategories = new String[uniqueCategories.size()];
			/*
			for(String asdf : uniqueCategories.toArray(outputCategories)) {
				System.out.println(asdf);
			}
			 */
			return uniqueCategories.toArray(outputCategories);
		}
		else {
			// System.out.println("Name NOT in Database class");
			return getAllCategories();
		}
	}
}
