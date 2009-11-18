/**
 * JBroFuzz 1.7
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
	// The fuzzer type, e.g. Replacive, Recursive, Double Fuzzer, Zero Fuzzer, Cross Product Fuzzer, Power Fuzzer
	private String type;
	// The fuzzer id, e.g. LDP-INJ
	private String id;
	// The fuzzer entry start point
	private int point1;
	// The fuzzer end point
	private int point2;
	// For double fuzzers, cross product fuzzers, the 2nd start point
	private int point3;
	// For double fuzzers, cross product fuzzers, the 2nd end point
	private int point4;
	
	/**
	 * <p>The main constructor for each Fuzzer Row.</p>
	 * 
	 * @param name The fuzzer name e.g. LDAP Injection
	 * @param type The fuzzer type e.g. Replacive, Recursive, Double Fuzzer
	 * @param id  The fuzzer id e.g. LDP-INJ
	 * @param point1 The fuzzer start point
	 * @param point2 The fuzzer end point
	 * @param point3 The fuzzer second start point (if e.g. Double Fuzzer)
	 * @param point4 The fuzzer second end point (if e.g. Double Fuzzer)
	 */
	public FuzzerRow(String name, String encoding, String type, String id, int point1,
			int point2, int point3, int point4) {
		this.name = name;
		this.encoding = encoding;
		this.type = type;
		this.id = id;
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = point3;
		this.point4 = point4;
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
	 * @param point1 The fuzzer start point
	 * @param point2 The fuzzer end point
	 */
	public FuzzerRow(String name, String type, String id, int point1,
			int point2) {
		this.name = name;
		this.encoding = "ASCII";
		this.type = type;
		this.id = id;
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = 0;
		this.point4 = 0;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return the point1
	 */
	public int getPoint1() {
		return point1;
	}

	/**
	 * @param point1 the point1 to set
	 */
	public void setPoint1(int point1) {
		this.point1 = point1;
	}

	/**
	 * @return the point2
	 */
	public int getPoint2() {
		return point2;
	}

	/**
	 * @param point2 the point2 to set
	 */
	public void setPoint2(int point2) {
		this.point2 = point2;
	}

	/**
	 * @return the point3
	 */
	public int getPoint3() {
		return point3;
	}

	/**
	 * @param point3 the point3 to set
	 */
	public void setPoint3(int point3) {
		this.point3 = point3;
	}

	/**
	 * @return the point4
	 */
	public int getPoint4() {
		return point4;
	}

	/**
	 * @param point4 the point4 to set
	 */
	public void setPoint4(int point4) {
		this.point4 = point4;
	}
	
	
}
