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
package org.owasp.jbrofuzz.fuzz;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * The message creator taking as input the message displayed in the request
 * field and performing some required manipulation. Examples include making sure
 * that the POST field has the length of the fuzzing request.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 0.9
 */
public class MessageCreator {

	/**
	 * 
	 */
	private static String stringReplace(final String toFind,
			final String original, final String substitute) {
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

	private int start, finish;

	public MessageCreator(String message, String payload, int start, int finish) {

		if ((message == null) || (payload == null) || (start < 0)
				|| (finish < 0) || (start > message.length())
				|| (finish > message.length())) {

			this.message = message == null ? "" : stringReplace("\n", message,
					"\r\n");
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

			this.message = stringReplace("\n", messageBuffer.toString(), "\r\n");

			// By now we have the complete message with the payload in the right
			// location

			if ((this.message.startsWith("GET"))
					|| (this.message.startsWith("HEAD"))) {
				if (!this.message.endsWith("\r\n\r\n")) {
					this.message += "\r\n\r\n";
				}
			}

			if (this.message.startsWith("POST")) {
				// Find the position of "\r\n\r\n"
				int eoh = this.message.indexOf("\r\n\r\n");
				// Provided an ending character sequence has been found
				if (eoh != -1) {
					// Find the location of the "Content-Length:"
					int ctl = this.message.indexOf("Content-Length:");
					// Provided a content length character sequence exists in
					// the request
					if (ctl != -1) {

						int contentLength = 0;
						String postValue = this.message.substring(eoh
								+ "\r\n\r\n".length());

						// Find the next end of line
						int neol = this.message.indexOf("\r\n", ctl);
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
												+ "\r\n\r\n".length());
								if (remainingHeader.startsWith("\r\n")) {
									newMessageBuffer.append("\r\n\r\n");
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
			}

		}

	}

	/**
	 * @return the finish
	 */
	public int getFinish() {
		return finish;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {

		String test = stringReplace("\r\n", message, "\\r\\n\n");

		// System.out.println(test);

		return message;

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
