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
package org.owasp.jbrofuzz.headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.tree.TreePath;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class HeaderLoader {

	private HashMap<String[], Header> headers;

	private HeaderTreeNode myNode;

	private static final String HEADER = "JBroFuzz Headers Collection";

	private static final int MAX_RECURSION = 1024;

	private static final int maxLines = 2048;
	private static final int maxLineLength = 512;
	private static final int maxNumberOfFields = 64;

	private int globalCounter;

	private StringBuffer fileContents;

	public HeaderLoader() {


		myNode = new HeaderTreeNode(HEADER);
		globalCounter = 0;

		int line_counter = 0;
		BufferedReader in = null;

		fileContents = new StringBuffer();

		// Attempt to read from the jar file
		final URL fileURL = ClassLoader.getSystemClassLoader().getResource("headers.jbrofuzz");

		try {

			final URLConnection connection = fileURL.openConnection();
			connection.connect();

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();
			line_counter++;

			while ((line != null) && (line_counter < maxLines)) {

				if (line.length() > maxLineLength) {
					line = line.substring(0, maxLineLength);
				}
				fileContents.append(line);
				fileContents.append('\n');

				line = in.readLine();
			}
			in.close();
		} 
		catch (final IOException e1) {

			System.out.println("Headers file (inside jar): " + fileURL.toString() + " could not be found");

		} 
		finally {

			IOUtils.closeQuietly(in);

		}



	} // constructor

	public void load() {

		// Parse the contents of the StringBuffer to the array of generators

		final String[] fileInput = fileContents.toString().split("\n");
		final int len = fileInput.length;

		headers = new HashMap<String[], Header>(len);

		for (int i = 0; i < len; i++) {

			// The number of fields identified for each category
			int numberOfFields = 0;

			final String line = fileInput[i];
			if ((!line.startsWith("#")) && (line.length() > 5)) {

				// "J:AAA-BBB:" First line contains three fields
				if ((line.charAt(1) == ':') && ((line.charAt(9) == ':') )) {
					final String[] firstLineArray = line.split(":");

					// Check that there are three fields separated by ':' in the
					// first line
					if (firstLineArray.length == 3) {

						// Check that the first character is one of JBROFUZZ
						if ( ("J".equals(firstLineArray[0]))
								|| ("B".equals(firstLineArray[0]))
								|| ("R".equals(firstLineArray[0]))
								|| ("O".equals(firstLineArray[0]))
								|| ("F".equals(firstLineArray[0]))
								|| ("U".equals(firstLineArray[0]))
								|| ("Z".equals(firstLineArray[0])) ) {

							// Check that the ID contains a '-' after 3 characters
							if(firstLineArray[1].charAt(3) == '-') {

								try {
									numberOfFields = Integer.parseInt(firstLineArray[2]);
								} catch (final NumberFormatException e) {
									numberOfFields = 0;
								}

							}
						}
					}
				} // First line check

				// If a positive number of fields is claimed in the first line
				// and the first line is OK
				if ((numberOfFields > 0) && (numberOfFields <= maxNumberOfFields)) {
					// final String[] firstArray = line.split(":");

					// Check that there is enough space to actually
					// add this particular header
					if (i < len - numberOfFields - 2) {

						// Check that the second line starts with a '>'
						String line2 = fileInput[i + 1];
						if (line2.startsWith(">")) {
							line2 = line2.substring(1);
							String[] categoriesArray;

							String commentLine = fileInput[i + 2];
							if(commentLine.startsWith(">>")) {

								// Finally create the header, add the comments
								Header myGen = new Header(i);
								myGen.setComment(commentLine.substring(2));

								// If categories do exist in the second line
								if (line2.contains("|")) {

									globalCounter = 0;
									categoriesArray = line2.split("\\|");
									for(int xa = 0; xa < categoriesArray.length; xa++) {
										categoriesArray[xa] = StringUtils.stripStart(StringUtils.stripEnd(categoriesArray[xa]," ")," ");
									}
									addNodes(categoriesArray, myNode);

								}
								// If no categories have been specified, add a
								// default category
								else {

									categoriesArray = new String[]{"<unknown " + i + ">"};

									myNode.add(new HeaderTreeNode("<unknown " + i + ">"));

								}

								// Add the values for each element
								StringBuffer myBuffer = new StringBuffer();
								for (int j = 1; j <= numberOfFields; j++) {
									myBuffer.append(fileInput[i + 2 + j]);
									myBuffer.append('\n');
								}
								myGen.setHeader(myBuffer.toString());

								// Finally add the generator to the Vector of
								// generators
								// System.out.println(ArrayUtils.toString(categoriesArray));

								headers.put(categoriesArray, myGen);
							}

						}
					}
				}
			}
		}
	}

	/**
	 * <p>Private, recursive method used at construction to populate the master 
	 * <code>HeaderTreeNode</code> that can be accessed via the 
	 * <code>getMasterTreeNode</code> method.</p>
	 * <p>In accessing this method, do not forget to reset the 
	 * <code>globalCounter</code> as if it reaches a value of <code>MAX_RECURSION
	 * </code> this method will simply return.</p>
	 * 
	 * @param categoriesArray The array of nodes to be added.
	 * @param dn The tree node on which to be added.
	 * 
	 * @see #getMasterHeader()
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2 
	 */
	private void addNodes(String[] categoriesArray, HeaderTreeNode dn) {

		if(categoriesArray.length == 0) {
			return;
		}
		if(globalCounter > MAX_RECURSION) {
			return;
		}
		globalCounter++;
		// Find the first element
		String firstElement = StringUtils.stripStart(StringUtils.stripEnd(categoriesArray[0]," ")," ");
		// Use the index to know its position
		int index = 0;
		boolean exists = false;
		for (Enumeration<HeaderTreeNode> e = dn.children() ; e.hasMoreElements() && !exists;) {

			String currentElement = e.nextElement().toString();

			if(currentElement.equalsIgnoreCase(firstElement)) {

				exists = true;

			} else {
				// If firstElement not current, increment
				index++;

			}
		}

		// Does the first element exist?
		if(!exists) {

			dn.add(new HeaderTreeNode(firstElement));
		}

		// Remove the first element, start again
		String [] temp = (String[]) ArrayUtils.subarray(categoriesArray, 1, categoriesArray.length);
		HeaderTreeNode nemp = (HeaderTreeNode) dn.getChildAt(index);

		addNodes(temp, nemp);

	}

	public Header getHeader(final TreePath treePath) {

		if(!((HeaderTreeNode)treePath.getLastPathComponent()).isLeaf()) {
			return new Header(0);
		}
		
		final Object [] path = treePath.getPath();
		// Go through the key set elements of the headers
		for(String[] element : headers.keySet()) {

			int success = path.length - 1;

			for(int i = 0; i < path.length; i++) {

				try {
										
					if(path[i+1].toString().equalsIgnoreCase(element[i])) {
						success--;

					} else {
						i = 32;

					}
				} catch (ArrayIndexOutOfBoundsException e) {
					i = 32;
					
				}

				if(success == 0) {
					return headers.get(element);

				}
			}
		}

		return new Header(0);

	}

	public HeaderTreeNode getMasterTreeNode() {

		return myNode;
	}

	public int size() {

		return headers.size();

	}

}
