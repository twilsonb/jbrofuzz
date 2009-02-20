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
package org.owasp.jbrofuzz.core;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

/**
 * <p>A prototype holds the definition of a fuzzer. Based on this 
 * prototype a fuzzer can be created by means of accessing a 
 * database factory.</p>
 *
 * <p>Effectively, a prototype is a collection of payloads and 
 * categories, under a unique id value, given a name.</p>
 *
 * @author subere@uncon.org
 * @version 1.2
 * @since 1.2
 */
public class Prototype {

	private ArrayList<String> categories;

	private ArrayList<String> payloads;

	private String name;

	private char type;

	private String id;

	public Prototype(char type, String id, String name) {

		this(type, id, name, new ArrayList<String>(), new ArrayList<String>());

	}

	public Prototype(char type, String id, String name,
			ArrayList<String> categories, ArrayList<String> payloads) {

		this.type = type;
		this.id = id;
		this.name = StringUtils.trim(name);
		this.categories = categories;
		this.payloads = payloads;

	}

	public void addCategory(String value) {

		categories.add(value);
		categories.trimToSize();

	}

	public void addPayload(String value) {

		payloads.add(calculatePayload(value));
		payloads.trimToSize();

	}

	private static String calculatePayload(String param) {
	
		String beginning;
		try {
			beginning = param.substring(0, 5);
		} catch (IndexOutOfBoundsException e1) {
			return param;
		}
		if (!beginning.startsWith("f(x)=")) {
			return param;
		}
	
		// Get rid of the first characters
		param = param.substring(5);
		// Chop at x, the variable of f(x)
		final String[] paramArray = param.split(" x ");
	
		// Check to see if you have two elements
		if (paramArray.length != 2) {
			return param;
		}
		// Define the input string
		final String input = paramArray[0];
		// Define the number of times
		int times;
		try {
			times = Integer.parseInt(paramArray[1]);
		} catch (final NumberFormatException e) {
			times = 1;
		}
	
		// Check that times is positive
		if (times <= 0) {
			return param;
		}
	
		final int len = input.length() * times;
	
		final StringBuffer newBuffer = new StringBuffer(len);
		for (int i = 0; i < times; i++) {
			newBuffer.append(input);
		}
		return newBuffer.toString();
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getPayloads() {
		payloads.trimToSize();
		return payloads;
	}

	public boolean isAMemberOfCategory(String category) {

		String[] categoriesArray = new String[categories.size()];
		categories.toArray(categoriesArray);

		for (String s : categoriesArray) {
			if (s.equalsIgnoreCase(category)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * <p>Return the type of fuzzer prototype. Based on the
	 * character type specified at construction, fuzzer 
	 * prototypes can have the following types:</p>
	 * 
	 * <code>
	 * Recursive
	 * Replacive
	 * Zero
	 * </code>
	 * 
	 * <p>An empty String is returned in case of an error.</p>
	 * 
	 * @return String one of "Replacive", "Recursive", "Zero"
	 *
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.3
	 */
	public String getType() {
		
		if (type == 'P') {
			return "Replacive";
		}
		
		if (type == 'R') {
			return "Recursive";
		}
		
		if (type == 'Z') {
			return "Zero";
		}
		
		return "";
	}
	
	public boolean isRecursive() {
		if (type == 'R') {
			return true;
		}
		return false;
	}
	
	public boolean isZero() {
		if (type == 'Z') {
			return true;
		}
		return false;
	}

	/**
	 * <p>Return the number of payloads that this 
	 * prototype has.</p>
	 * 
	 * @return int the number of payloads
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public int size() {
		return payloads.size();
	}

}
