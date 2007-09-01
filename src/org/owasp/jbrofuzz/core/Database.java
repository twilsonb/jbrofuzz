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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.version.JBRFormat;

import com.Ostermiller.util.*;
/**
 * <p>
 * A database is a collection of exploits, grouped together with a number of different tags.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.8
 */
public class Database {

	private JBroFuzz mJBroFuzz;

	private String [] [] exploits;

	public Database(final JBroFuzz mJBroFuzz) {

		this.mJBroFuzz = mJBroFuzz;

		final int maxLines = 10000;
		final int maxLineLength = 10000;
		int line_counter = 0;
		BufferedReader in = null;
		int len = 0;
		StringBuffer fileContents = new StringBuffer();

		// Attempt to read from the jar file
		if (len <= 0) {
			line_counter = 0;

			final URL fileURL = ClassLoader.getSystemClassLoader().getResource(
					JBRFormat.FILE_GNU);

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
			} catch (final IOException e1) {
				System.out.println("Directories file (inside jar): "
						+ fileURL.toString() + " could not be found");
				// }
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (final IOException ex) {
				}
			}
		}
		// Parse the contents of the StringBuffer to the exploits array
		// Parse the data

		exploits = ExcelCSVParser.parse(fileContents.toString());

		// Display the parsed data
		/*
		for (int i=0; i<exploits.length; i++){
		    for (int j=0; j<exploits[i].length; j++){
		        System.out.println(exploits[i][j]);
		    }
		    System.out.println("-----");
		}
		 */
		System.out.println("Row " + exploits.length + " Column " + exploits[0].length);

	}

	public String[] getNames() {
		String[] output = new String[exploits.length];
		for (int i=0; i<exploits.length; i++){
			// for (int j=0; j<exploits[i].length; j++){
			//System.out.println(exploits[i][j]);
			output[i] = exploits[i][0];
			// }
			// System.out.println("-----");
		}
		return output;
	}

	public String[] getCategories() {
		ArrayList<String> uniqueCategories = new ArrayList<String>();

		for (int i=0; i<exploits.length; i++){
			String[] categoriesLine = exploits[i][3].split(",");
			for(String categoryItemonLine : categoriesLine) {
				while(categoryItemonLine.startsWith(" ")) {
					categoryItemonLine = categoryItemonLine.substring(1);
				}
				while(categoryItemonLine.endsWith(" ")) {
					categoryItemonLine = categoryItemonLine.substring(0, (categoryItemonLine.length() - 1));
				}
				
				boolean catFound = false;
				for(String uniqueCategory : uniqueCategories) {
					if(categoryItemonLine.equalsIgnoreCase(uniqueCategory)) {
						catFound = true;
					}
				}
				if(!catFound) {
					uniqueCategories.add(categoryItemonLine);
				}
			}
		}
		uniqueCategories.trimToSize();
		String [] outputCategories = new String[uniqueCategories.size()];
		return uniqueCategories.toArray(outputCategories);
	}
}
