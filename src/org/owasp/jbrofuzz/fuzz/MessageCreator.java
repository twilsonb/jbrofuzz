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
package org.owasp.jbrofuzz.fuzz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.prefs.Preferences;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.jbrofuzz.fuzz.ui.FuzzerTable;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The message creator taking as input the message displayed in the request
 * field and performing some required manipulation. Examples include making sure
 * that the POST field has the length of the fuzzing request.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 1.8
 * @since 1.3
 */
public class MessageCreator {
	
	// The message that will be placed on the wire
	private String message;
	// The actual payload that will be incorporated into the original message
	private String payload;

	/**
	 * <p>
	 * The end of line delimeter to used for messages put on the wire.
	 * </p>
	 * <p>
	 * This value is '\n' or '\r\n'.
	 * </p>
	 */
	public String END_LINE;

	public MessageCreator(String message, String encoding, String payload, int start, int finish) {

		// Set the end of line character from the preferences
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean endLineChar = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_2, false);
		END_LINE = endLineChar ? "\n" : "\r\n";

		// Perform the necessary encoding on the payload specified

		// Encoding 1: Uppercase
		if(encoding.equals(FuzzerTable.ENCODINGS[1])) {
			this.payload = payload.toUpperCase();
		} else 
			// Encoding 2: Lowercase
			if(encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[2])) {
				this.payload = payload.toLowerCase();
			} else
				// Encoding 3: www-form-urlencoded 
				if(encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[3])) {
					final URLCodec codec = new URLCodec();

					try {
						this.payload = codec.encode(payload, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						this.payload = payload;
						e.printStackTrace();
					} 
				} else 
					// Encoding 4: HTML encode	
					if (encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[4])) {
						this.payload = StringEscapeUtils.escapeHtml(payload);
					} else 
						// Encoding 5: UTF-8
						if (encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[5])) {
							try {
								this.payload = URLEncoder.encode(payload, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								this.payload = payload;
								e.printStackTrace();
							}
						} else {
							this.payload = payload;
						}


		// Split the message and add in-between
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message.substring(0, start));
		messageBuffer.append(this.payload);
		messageBuffer.append(message.substring(finish));

		this.message = stringReplace("\n", messageBuffer.toString(),
				END_LINE);

		// By now we have the complete message with the payload in the right
		// location

		if ((this.message.startsWith("GET"))
				|| (this.message.startsWith("HEAD"))) {

			if (!this.message.endsWith(END_LINE + END_LINE)) {

				this.message += END_LINE + END_LINE;

			}

		}

		if (this.message.startsWith("POST")) {
			// Find the position of "\r\n\r\n"
			int eoh = this.message.indexOf(END_LINE + END_LINE);
			// Provided an ending character sequence has been found
			if (eoh != -1) {
				// Find the location of the "Content-Length:"
				int ctl = this.message.indexOf("Content-Length:");
				// Provided a content length character sequence exists in
				// the request
				if (ctl != -1) {

					int contentLength = 0;
					String postValue = this.message.substring(eoh
							+ (END_LINE + END_LINE).length());

					// Find the next end of line
					int neol = this.message.indexOf(END_LINE, ctl);
					if (neol != -1) {
						// Retrieve the value until the next "\r\n"
						// character
						String contentLengthString = this.message
						.substring(
								ctl + "Content-Length:".length(),
								neol);
						try {
							contentLength = Integer
							.parseInt(contentLengthString);
						} catch (NumberFormatException e) {
							contentLength = 0;
						}

						// If the content length is not the same as the
						// postValue in bytes
						if (contentLength != postValue.getBytes().length) {

							StringBuffer newMessageBuffer = new StringBuffer();
							newMessageBuffer.append(this.message.substring(
									0, ctl + "Content-Length:".length()));
							try {

								newMessageBuffer
								.append(" "
										+ postValue
										.getBytes("ISO-8859-1").length);

							} catch (UnsupportedEncodingException e) {

								newMessageBuffer.append(" "
										+ postValue.getBytes().length);

							}

							// If the remaining header starts with "\r\n"
							// then the "Content-Length was the last header
							// line
							String remainingHeader = this.message
							.substring(neol, eoh
									+ (END_LINE + END_LINE)
									.length());
							if (remainingHeader.startsWith(END_LINE)) {
								newMessageBuffer
								.append(END_LINE + END_LINE);
							} else {
								newMessageBuffer.append(remainingHeader);
							}

							newMessageBuffer.append(postValue);
							// System.out.println("--------" + postValue +
							// "------");

							this.message = newMessageBuffer.toString();

						}
					}

				}
				// The message does not contain the Content-Length line and
				// this needs to be added
				else {
					// TODO Add the Content-Length line
				}
			}
		} // if it's a POST request

		//		}

	}

	/**
	 * <p>
	 * Method for returning the message as constructed by the
	 * {@link #MessageCreator(String, String, int, int)}.
	 * </p>
	 * 
	 * @return The message after it has been processed by the constructor
	 * 
	 * @see #getMessageForDisplayPurposes()
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String getMessage() {

		return message;

	}

	/**
	 * <p>
	 * Method returning the message as constructed by the
	 * {@link #MessageCreator(String, String, int, int)}, but processed so that
	 * to show special characters.
	 * </p>
	 * <p>
	 * This method will display a '\n' character as \\n and also a '\r'
	 * character as a \\r.
	 * </p>
	 * <p>
	 * It can act unexpectedly if {@link #END_LINE} is not a '\n' or a '\r\n'.
	 * </p>
	 * 
	 * @return The message also showing any special characters
	 * 
	 * @see #getMessage()
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String getMessageForDisplayPurposes() {
		// END_LINE can be either a \n or a \r\n
		if (END_LINE.equals("\n")) {
			// \n
			return stringReplace("\n", message, "\\n\n");

		} else {
			// \r\n
			return stringReplace("\r\n", message, "\\r\\n\n");
		}

	}

	/**
	 * <p>
	 * This method has the same algorithm as Connection.getPostDataInMessage()
	 * </p>
	 * 
	 * @since 1.5
	 * @version 1.5
	 * @return
	 */
	public String getPostDataForDisplayPurposes() {

		String postDataString;

		try {

			postDataString = message.split("\r\n\r\n")[1];

		} catch (Exception e1) {

			postDataString = message;

		}

		// END_LINE can be either a \n or a \r\n
		if (END_LINE.equals("\n")) {
			// \n
			return stringReplace("\n", postDataString, "\\n\n");

		} else {
			// \r\n
			return stringReplace("\r\n", postDataString, "\\r\\n\n");
		}

	}

	/**
	 * @return the payload
	 */
	public String getPayload() {

		return payload;

	}

	
	/**
	 * TODO Should really write this a bit better..
	 */
	private static String stringReplace(final String toFind,
			final String original, final String substitute) {

		if (toFind.equals(substitute)) {
			return original;
		}

		int found = 0;
		int start = 0;
		String returnString = original;
		while (found != -1) {
			found = returnString.indexOf(toFind, start);
			if (found != -1) {
				returnString = returnString.substring(0, found).concat(
						substitute).concat(
								returnString.substring(found + toFind.length()));
			}
			start = found + substitute.length();
		}
		return returnString;
	}



}
