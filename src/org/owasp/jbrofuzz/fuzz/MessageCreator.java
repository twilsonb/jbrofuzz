/**
 * JBroFuzz 1.5
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
import java.util.prefs.Preferences;

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
 * @version 1.3
 */
public class MessageCreator {

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

	private String message, payload;

	/**
	 * <p>
	 * The end of line delimeter to used for messages put on the wire.
	 * </p>
	 * <p>
	 * This value is '\n' or '\r\n'.
	 * </p>
	 */
	public String END_LINE;

	private int start, finish;

	public MessageCreator(String message, String payload, int start, int finish) {

		// Set the end of line character from the preferences
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean endLineChar = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_2, false);
		END_LINE = endLineChar ? "\n" : "\r\n";

		// Message is not a valid message
		if ((message == null) || (payload == null) || (start < 0)
				|| (finish < 0) || (start > message.length())
				|| (finish > message.length())) {

			this.message = message == null ? "" : stringReplace("\n", message,
					END_LINE);
			this.payload = payload == null ? "" : payload;

		}
		// Message is a valid message
		else {

			// Check if positive and less than the message length
			if ((start >= 0) && (start < message.length())) {
				this.start = start;
			} else {
				this.start = 0;
			}

			// Check if positive and less than|equal the message length
			if ((finish > 0) && (finish <= message.length())) {
				this.finish = finish;
			} else {
				this.finish = 0;
			}

			this.payload = payload;

			// Split the message and add in-between
			StringBuffer messageBuffer = new StringBuffer();
			messageBuffer.append(message.substring(0, this.start));
			messageBuffer.append(payload);
			messageBuffer.append(message.substring(this.finish));

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

		}

	}

	/**
	 * @return the finish
	 */
	public int getFinish() {
		return finish;
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
	 * @return the payload
	 */
	public String getPayload() {

		return payload;

	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param finish
	 *            the finish to set
	 */
	public void setFinish(int finish) {
		this.finish = finish;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {

		this.message = message;

	}

	/**
	 * @param fuzzer
	 *            the payload to set
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

}
