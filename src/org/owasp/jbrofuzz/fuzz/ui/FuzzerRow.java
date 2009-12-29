/**
 * JBroFuzz 1.8
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
package org.owasp.jbrofuzz.fuzz.ui;

/**
 * <p>Class for representing individual row data types 
 * of the fuzzer table on the right hand side of the 
 * Fuzzing panel.</p>
 * 
 * @author subere@uncon.org
 * @version 1.8
 * @since 1.8
 */
public class FuzzerRow {

	// The fuzzer name, e.g. LDAP Injection
	private String name;
	// The encoding of the fuzzer
	private String encoding;
	// The fuzzer id, e.g. LDP-INJ
	private String id;
	// The fuzzer entry start point
	private int start;
	// The fuzzer end point
	private int end;

	/**
	 * <p>The main constructor for each Fuzzer Row.</p>
	 * 
	 * @param id  The fuzzer id e.g. LDP-INJ
	 * @param start The fuzzer start point
	 * @param end The fuzzer end point
	 */
	public FuzzerRow(String id, String encoding, int start, int end) {

		this.id = id;
		this.encoding = encoding;
		this.start = start;
		this.end = end;

	}

	/**
	 * <p> The constructor for less complicated fuzzers that only have
	 * a single start and end point.</p>
	 * 
	 * <p>This constructor should be used for Recursive and Recursive 
	 * Fuzzers.</p>
	 * 
	 * @param name The fuzzer name e.g. LDAP Injection
	 * @param type The fuzzer type e.g. Replacive, Recursive, Double Fuzzer
	 * @param id  The fuzzer id e.g. LDP-INJ
	 * @param start The fuzzer start point
	 * @param point2 The fuzzer end point
	 */
	public FuzzerRow(String id, int start, int end) {

		this.id = id;
		this.encoding = FuzzerTable.ENCODINGS[0];
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the start
	 */
	public int getStartPoint() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStartPoint(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public int getEndPoint() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEndPoint(int end) {
		this.end = end;
	}

}
