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
package org.owasp.jbrofuzz.headers;

public class Header {

	private int id, noOfFields;

	private String header, comment;

	/**
	 * @param id
	 */
	public Header(int id) {
		this.id = id;
		noOfFields = 0;
		header = "";
		comment = "";
	}

	/**
	 * @param id
	 * @param noOfFields
	 * @param header
	 * @param comment
	 */
	public Header(int id, int noOfFields, String header, String comment) {
		this.id = id;
		this.noOfFields = noOfFields;
		this.header = header;
		this.comment = comment;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the value
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	protected String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nTotal No of Characters: ");
		sb.append(header.length());
		sb.append("\nTotal No of Fields (lines): ");
		sb.append(header.split("\n").length);
		return sb.toString();
	}

	/**
	 * @return the noOfFields
	 */
	public int getNoOfFields() {
		return noOfFields;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	protected void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setHeader(String value) {
		header = value;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param noOfFields
	 *            the noOfFields to set
	 */
	public void setNoOfFields(int noOfFields) {
		this.noOfFields = noOfFields;
	}

}
