/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.headers;

class Header {

	private static int headerCounter = 0;
	
	private int headerId, noOfFields;

	private String headerValue;
	
	private final String comment;

	/**
	 * @param headerId
	 * @param noOfFields
	 * @param headerValue
	 * @param comment
	 */
	protected Header(final int noOfFields, 
			final String headerValue, final String comment) {
		
		this.headerId = headerCounter;
		this.noOfFields = noOfFields;
		
		this.headerValue = headerValue;
		this.comment = comment;
		// Increment the counter
		headerCounter++;
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
		return headerValue;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return headerId;
	}

	protected String getInfo() {
		final StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("\nTotal No of Characters: ");
		sBuilder.append(headerValue.length());
		sBuilder.append("\nTotal No of Fields (lines): ");
		sBuilder.append(headerValue.split("\n").length);
		return sBuilder.toString();
	}

	/**
	 * @return the noOfFields
	 */
	public int getNoOfFields() {
		return noOfFields;
	}

// TODO UCdetector: Remove unused code: 
// 	/**
// 	 * @param comment
// 	 *            the comment to set
// 	 */
// 	protected void setComment(final String comment) {
// 		this.comment = comment;
// 	}

	/**
	 * @param headerValue
	 *            the value to set
	 */
	public void setHeader(final String headerValue) {
		this.headerValue = headerValue;
	}

	/**
	 * @param headerId
	 *            the id to set
	 */
	public void setId(final int headerId) {
		this.headerId = headerId;
	}

	/**
	 * @param noOfFields
	 *            the noOfFields to set
	 */
	public void setNoOfFields(final int noOfFields) {
		this.noOfFields = noOfFields;
	}

	protected static final Header ZERO = new Header(0, "", "");
}
