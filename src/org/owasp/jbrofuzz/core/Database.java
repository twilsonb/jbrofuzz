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
package org.owasp.jbrofuzz.core;

import org.apache.commons.lang.*;
import org.apache.commons.io.*;

import java.util.*;
import java.net.*;
import java.io.*;

public class Database {

	private HashMap<String, Generator> generators;

	public Database() {

		final int maxLines = 1024;
		final int maxLineLength = 512;
		final int maxNumberOfPayloads = 64;
		final int maxFuzzerNameLength = 64;

		int line_counter = 0;
		BufferedReader in = null;

		final StringBuffer fileContents = new StringBuffer();

		// Attempt to read from the jar file
		final URL fileURL = ClassLoader.getSystemClassLoader().getResource(
				"fuzzers.jbrofuzz");

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
		} finally {
			IOUtils.closeQuietly(in);
		}

		// Parse the contents of the StringBuffer to the array of generators

		final String[] fileInput = fileContents.toString().split("\n");
		final int len = fileInput.length;

		generators = new HashMap<String, Generator>(len);

		for (int i = 0; i < len; i++) {

			// The number of payloads identified for each category
			int numberOfPayloads = 0;

			final String line = fileInput[i];
			if ((!line.startsWith("#")) && (line.length() > 5)) {
				// "P:ABC-DEF:" or "P:ABC-DEF-GHI:"
				if ((line.charAt(1) == ':')
						&& ((line.charAt(9) == ':') || (line.charAt(13) == ':'))) {
					final String[] firstLineArray = line.split(":");
					// Check that there are four fields separated by : in the
					// first line
					if (firstLineArray.length == 4) {
						// Check that the name of the identifier is less than
						// maxFuzzerNameLength
						if ((firstLineArray[2].length() < maxFuzzerNameLength)
								&& (firstLineArray[2].length() > 0)) {
							// Check that the first character is either a P or
							// an R
							if (("P".equals(firstLineArray[0]))
									|| ("R".equals(firstLineArray[0]))) {

								try {
									numberOfPayloads = Integer
											.parseInt(firstLineArray[3]);
								} catch (final NumberFormatException e) {
									numberOfPayloads = 0;
								}

							}
						}
					}
				} // First line check

				// If a positive number of payloads is claimed in the first line
				// and the first line is ok
				if ((numberOfPayloads > 0)
						&& (numberOfPayloads <= maxNumberOfPayloads)) {
					final String[] firstArray = line.split(":");

					// Check that there remaining element in the generator
					// Vector
					if (i < len - numberOfPayloads - 1) {

						// Check that the second line starts with a >
						String line2 = fileInput[i + 1];
						if (line2.startsWith(">")) {
							line2 = line2.substring(1);

							// Finally create the generator if all the checks
							// pass
							final Generator myGen = new Generator(firstArray[0]
									.charAt(0), firstArray[1], /* StringUtils.rightPad( */
							firstArray[2] /* , 24) */);

							// If categories do exist in the second line
							if (line2.contains("|")) {

								String[] categoriesArray = line2.split("\\|");
								for (String currentCategory : categoriesArray) {
									// System.out.println(currentCategory);
									myGen
											.addCategory(StringUtils
													.stripStart(
															StringUtils
																	.stripEnd(
																			currentCategory,
																			" "),
															" "));

								}
							}
							// If no categories have been specified, add a
							// default category
							else {

								myGen.addCategory("Default");

							}

							// Add the values for each element
							for (int j = 1; j <= numberOfPayloads; j++) {

								final StringBuffer myBuffer = new StringBuffer();
								myBuffer.append(fileInput[i + 1 + j]);
								myGen.addPayload(myBuffer.toString());

							}
							// Finally add the generator to the Vector of
							// generators
							generators.put(firstArray[1], myGen);
							// }
						}
					}
				}
			}
		}

		// generators.trimToSize();

	} // constructor

	public boolean containsGenerator(String Id) {

		return generators.containsKey(Id);

	}

	/**
	 * <p>
	 * Return all the unique categories found in all the Generators that are
	 * inside the database.
	 * <p>
	 * 
	 * @return String[] uniqueCategories
	 */
	public String[] getAllCategories() {

		HashSet<String> o = new HashSet<String>();

		String[] ids = getAllIds();
		for (String id : ids) {

			ArrayList<String> categoriesArrayList = generators.get(id)
					.getCategories();
			String[] categoriesArray = new String[categoriesArrayList.size()];
			categoriesArrayList.toArray(categoriesArray);

			for (String cCategory : categoriesArray) {

				o.add(cCategory);

			}

		}

		String[] uniqueCategoriesArray = new String[o.size()];
		o.toArray(uniqueCategoriesArray);

		return uniqueCategoriesArray;

	}

	public String[] getAllIds() {

		Set<String> set = generators.keySet();
		final String[] output = new String[set.size()];
		return set.toArray(output);

	}

	public String[] getAllNames() {

		StringBuffer output = new StringBuffer();

		Set<String> set = generators.keySet();
		final String[] input = new String[set.size()];
		set.toArray(input);

		for (String key : input) {
			output.append(generators.get(key).getName() + "\n");
		}

		return output.toString().split("\n");

	}

	public Generator getGenerator(String Id) {

		return generators.get(Id);

	}

	public String[] getGenerators(String category) {

		HashSet<String> o = new HashSet<String>();
		String[] ids = getAllIds();

		for (String id : ids) {

			Generator g = generators.get(id);
			if (g.isAMemberOfCategory(category)) {
				o.add(g.getName());
			}
		}

		String[] uniqueCategoriesArray = new String[o.size()];
		o.toArray(uniqueCategoriesArray);

		return uniqueCategoriesArray;
	}

	public String getIdFromName(String name) {

		String[] ids = getAllIds();
		for (String id : ids) {
			Generator g = generators.get(id);
			// System.out.println("In getAllIds() input name is: -" + name + "-
			// current name is: -" + g.getName() + "-");
			if (name.equalsIgnoreCase(g.getName())) {
				// System.out.println("Found Match! input -" + name + "- output
				// is: -" + g.getName() + "-");
				return id;
			}
		}
		return "";
	}

	public String getName(String id) {

		return generators.get(id).getName();

	}

	public String[] getPayloads(String id) {

		if (containsGenerator(id)) {
			Generator g = generators.get(id);
			final String[] output = new String[g.size()];
			return g.getPayloads().toArray(output);
		} else {
			return new String[0];
		}

	}

	public int getSize(String id) {

		if (containsGenerator(id)) {
			Generator g = generators.get(id);
			return g.size();
		} else {
			return 0;
		}

	}

	public int size() {

		return generators.size();

	}

}
