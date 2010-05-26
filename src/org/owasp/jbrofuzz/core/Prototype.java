/**
 * JBroFuzz 2.2
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
package org.owasp.jbrofuzz.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.io.FileHandler;

/**
 * <p>
 * A prototype holds the definition of a fuzzer. Based on this prototype a
 * fuzzer can be created by means of accessing a database factory.
 * </p>
 * 
 * <p>
 * Effectively, like a fuzzer, a prototype is a collection of payloads and 
 * categories, under a unique id value, given a name.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 1.2
 */
public class Prototype {

	/**
	 * <p>Private method for calculating the expansion
	 * of a payload.</p>
	 * 
	 * <p>As an example 'f(x)= A x 3' would output: 'AAA'</p>
	 * 
	 * <p>The maximum return length of the payload is
	 * MAX_BYTE i.e. 33554432 
	 * 
	 * @param param The payload as String to calculate a 
	 * potential expansion
	 * 
	 * @return Either the original String or the calculated
	 * value
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	private static String calculatePayload(final String param) {

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
		final String parameter = param.substring(5);
		// Chop at x, the variable of f(x)
		final String[] paramArray = parameter.split(" x ");

		// Check to see if you have two elements
		if (paramArray.length != 2) {
			return parameter;
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
			return parameter;
		}

		// Check that what you're creating is not too huge
		final int len = input.length() * times;
		if (len > FileHandler.MAX_BYTES) {
			return parameter;
		}

		final StringBuffer newBuffer = new StringBuffer(len);
		for (int i = 0; i < times; i++) {
			newBuffer.append(input);
		}
		return newBuffer.toString();
	}

	/**
	 * <p>Method to confirm if a fuzzer type specified in the 
	 * form of a single character, is valid.</p>
	 * 
	 * @param inputType The fuzzer type character e.g. 'Z' for
	 * a Zero Fuzzer.
	 * @return true if the fuzzer type exists, else false.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.8
	 */
	public static boolean isValidFuzzerType(final char inputType) {

		// Replacive Fuzzer Type
		if (inputType == 'P') {
			return true;
		}
		// Recursive Fuzzer Type
		if (inputType == 'R') {
			return true;
		}
		// Zero Fuzzer Type
		if (inputType == 'Z') {
			return true;
		}
		// Double Fuzzer Type
		if (inputType == 'D') {
			return true;
		}
		// Cross Product Fuzzer Type
		if (inputType == 'X') {
			return true;
		}
		// Power Fuzzer Type
		if (inputType == 'P') {
			return true;
		}
		// Header Type
		if (inputType == 'H') {
			return true;
		}

		return false;
	}

	private transient List<String> categories, payloads;

	private transient String name, uniqId;

	private transient char type;

	private transient String comment;

	/**
	 * <p>A Prototype constuctor, effectively building the fuzzer
	 * to be placed into the Database of Fuzzers.</p>
	 * <p>A Prototype represents a fuzzer in its unborn form, i.e.
	 * the type of fuzzer that it is (e.g. 'R' for Recursive, 'P' for 
	 * Replacive, or 'Z' for a Zero Fuzzer), the unique ID that it has (
	 * e.g. 'XSS-101' or 'SQL-MS2-008') and its name (e.g. 'MS 2008 SQL 
	 * Injection').</p> 
	 * 
	 * <p>Prototypes belong to one or more categories, this is represented
	 * by an array list.</p>
	 * <p>Also, Prototypes, hold a number of payloads; these represent the 
	 * basic alphabet that will construct the full list for the fuzzer.</p>
	 * 
	 * @param type 'R' for Recursive, 'P' for Replacive, or 'Z' for a 
	 * Zero Fuzzer
	 * 
	 * @param uniqId e.g. 'XSS-101' or 'SQL-MS2-008'
	 * 
	 * @param name e.g. 'MS 2008 SQL 
	 * Injection'
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public Prototype(final char type, final String uniqId, final String name) {

		this(type, uniqId, name, new ArrayList<String>(), new ArrayList<String>());

	}

	public Prototype(final char type, final String uniqId, final String name,
			final List<String> categories, final List<String> payloads) {

		this.type = type;
		this.uniqId = uniqId;
		this.name = StringUtils.trim(name);
		this.categories = categories;
		this.payloads = payloads;

	}

	/**
	 * <p>Add the Prototype to a particular category e.g. 'Cross 
	 * Site Scripting'.</p>
	 * 
	 * @param category
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public void addCategory(final String category) {

		categories.add(category);
		// categories.trimToSize();

	}

	/**
	 * <p>Add an additional payload, as String to the current
	 * Prototype.</p>
	 * 
	 * @param payload
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void addPayload(final String payload) {

		payloads.add(calculatePayload(payload));
		// payloads.trimToSize();

	}

	/**
	 * <p>Get all the categories, as an array list,
	 * that the Prototype is affiliated to.</p>
	 * 
	 * @return ArrayList<String>
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public List<String> getCategories() {
		return categories;
	}

	/**
	 * <p>Get the ID of the Prototype; this is a unique
	 * identifier, also common in the Database and in the
	 * construction of a new Fuzzer.</p>
	 * 
	 * <p>Examples include 'ZERO-10000' 'XSS-101', etc.)</p>
	 * 
	 * @return The ID as String
	 * 
	 * @see org.owasp.jbrofuzz.core.#Database()
	 * @see org.owasp.jbrofuzz.core.#Fuzzer()
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	protected String getId() {
		return uniqId;
	}

	/**
	 * <p>Get the name of the Prototype; this is not necessarily
	 * unique, but represents the actual name value, also common
	 * in the Database and in the Fuzzer class.</p>
	 * 
	 * @return The name as String
	 * 
	 * @see org.owasp.jbrofuzz.core.#Database()
	 * @see org.owasp.jbrofuzz.core.#Fuzzer()
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Get the alphabet of payloads associated with this
	 * Prototype.</p>
	 *  
	 * @return An array list of Strings, totalling the 
	 * payloads
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public List<String> getPayloads() {
		// payloads.trimToSize();
		return payloads;
	}

	/**
	 * <p>
	 * Return the type of fuzzer prototype. Based on the character type
	 * specified at construction, fuzzer prototypes can have the following
	 * types:
	 * </p>
	 * 
	 * <code>
	 * Recursive
	 * Replacive
	 * Zero
	 * Double
	 * Cross Product
	 * </code>
	 * 
	 * <p>
	 * An empty String is returned in case of an error.
	 * </p>
	 * 
	 * @return String one of "Replacive", "Recursive", "Zero"
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
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
		if (type == 'D') {
			return "Double";
		}
		if (type == 'X') {
			return "Cross Product";
		}

		return "";
	}

	/**
	 * <p>Check if a particular prototype of already a member of 
	 * a particular category.</p>
	 * 
	 * @param category e.g 'Cross Site Scripting'
	 * 
	 * @return true if it is a member of that category
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	protected boolean isAMemberOfCategory(final String category) {

		final String[] categoriesArray = new String[categories.size()];
		categories.toArray(categoriesArray);

		for (String s : categoriesArray) {
			if (s.equalsIgnoreCase(category)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * <p>Is the Prototype that of a recursive Fuzzer?</p>
	 * 
	 * <p>A recursive Fuzzer iterates through the 
	 * payloads it carries by joining them all to make the
	 * corresponding value in the number system of the 
	 * total payloads it carries.</p>
	 * 
	 * @return true if it is a recursive Fuzzer.
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public boolean isRecursive() {
		if (type == 'R') {
			return true;
		}
		return false;
	}

	/**
	 * <p>Is the Prototype that of a zero Fuzzer?</p>
	 * 
	 * <p>A zero Fuzzer does not alter the value of the
	 * message being transmitted.</p>
	 * 
	 * <p>If you want to transmit 1000 identical requests,
	 * you need a zero Fuzzer with 1000 payloads.</p>
	 * 
	 * @return true if it is a zero Fuzzer.
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public boolean isZero() {
		if (type == 'Z') {
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Return the number of payloads that this prototype has.
	 * </p>
	 * 
	 * @return int the number of payloads
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public int size() {
		return payloads.size();
	}
	
	public int getNoOfCategories() {
		return categories.size();
	}

	public void addComment(String comment) {
		this.comment = comment;
		
	}

	public String getComment() {
		return this.comment;
	}

}
