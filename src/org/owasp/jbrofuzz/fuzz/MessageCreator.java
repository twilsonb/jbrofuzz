/**
 * JBroFuzz 2.3
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
package org.owasp.jbrofuzz.fuzz;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.ui.FuzzerTable;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>
 * The message creator taking as input the message displayed in the request
 * field and performing some required manipulation. Examples include making sure
 * that the POST field has the length of the fuzzing request.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 1.3
 */
class MessageCreator {

	// The message that will be placed on the wire
	private String message;
	// The actual payload that will be incorporated into the original message
	private final String payload;

	/**
	 * <p>
	 * The end of line delimeter to used for messages put on the wire.
	 * </p>
	 * <p>
	 * This value is '\n' or '\r\n'.
	 * </p>
	 */
	private final String END_LINE;

	protected MessageCreator(final String url, final String message, final String encoding, final String payload, final int start, final int finish) {

		// Perform the necessary encoding on the payload specified

		this.payload = doPayloadEncoding(encoding, payload);

		// Split the message and add in-between
		// TODO: Calculate the length of the message
		final StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(message.substring(0, start));
		messageBuffer.append(this.payload);
		messageBuffer.append(message.substring(finish));

		// Set the end of line character from the preferences
		final boolean endLineChar = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[1].getId(), true);
		END_LINE = endLineChar ? "\r\n" : "\n";

		this.message = stringReplace("\n", messageBuffer.toString(),
				END_LINE);


		// By now we have the complete message with the payload in the right
		// location
		this.message = doAppendCRLF(this.message);
		
		// Do a Content-Length re-write, under certain conditions
		this.message = doContentLengthReWrite(this.message);

		// Do a Base64, basic auth header append
		final boolean addAuthHeader = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[4].getId(), true);
		if(addAuthHeader) {
			this.message = doBasicAuthHeader(url, this.message);
		}

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
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.2
	 */
	public String getMessageForDisplayPurposes() {
		// END_LINE can be either a \n or a \r\n
		if ("\n".equals(END_LINE)) {
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

		} catch (final Exception e1) {

			postDataString = message;

		}

		// END_LINE can be either a \n or a \r\n
		if ("\n".equals(END_LINE)) {
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
	 * <p>Method returns the encoded payload, based on the encoding provided 
	 * and the payload value inputted.</p>
	 * 
	 * @param encoding
	 * @param payload
	 * @return
	 */
	private String doPayloadEncoding(final String encoding, final String payload) {

		// Encoding 1: Uppercase
		if(encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[1])) {
			return payload.toUpperCase(Locale.ENGLISH);
		} 

		// Encoding 2: Lowercase
		if(encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[2])) {
			return payload.toLowerCase(Locale.ENGLISH);
		} 

		// Encoding 3: www-form-urlencoded 
		if(encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[3])) {

			final URLCodec codec = new URLCodec();

			try {
				return codec.encode(payload, "UTF-8");

			} catch (final UnsupportedEncodingException e) {

				e.printStackTrace();
				return payload;

			}

		}

		// Encoding 4: HTML encode	
		if (encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[4])) {
			return StringEscapeUtils.escapeHtml(payload);
		} 

		// Encoding 5: UTF-8
		if (encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[5])) {

			try {

				return URLEncoder.encode(payload, "UTF-8");

			} catch (final UnsupportedEncodingException e) {

				e.printStackTrace();
				return payload;

			}
		}

		// Encoding 6: UTF-16
		if(encoding.equalsIgnoreCase(FuzzerTable.ENCODINGS[6])) {

			try {

				return URLEncoder.encode(payload, "UTF-16");

			} catch (final UnsupportedEncodingException e) {

				e.printStackTrace();
				return payload;
			}

		}

		// By default, return the payload value
		return payload;


	}

	/**
	 * Method for re-writting the Content-Length String. Returns 
	 * the new message request.
	 *  
	 * @param message
	 * @return
	 */
	private String doContentLengthReWrite(final String message) {

		// If we are not dealing with a POST, no need to rewrite
		if (!message.startsWith("POST")) {

			return message;

		}

		final String ENDCREDITS = END_LINE + END_LINE;
		// If no "end credits" have been found
		final int eoh = message.indexOf(ENDCREDITS);
		if(eoh == -1) {

			return message;

		}

		final String TOBEFOUND = "Content-Length:";
		// Find the location of the "Content-Length:"
		final int ctl = this.message.indexOf(TOBEFOUND);
		// Provided a content length character sequence exists in
		// the request
		if(ctl == -1) {

			// The message does not contain the Content-Length line and
			// this needs to be added
			// TODO Add the Content-Length line
			return message;

		}

		int contentLength = 0;
		final String postValue = message.substring(eoh + (ENDCREDITS).length());

		// Find the next end of line character, after the char to be found
		final int neol = this.message.indexOf(END_LINE, ctl);
		if(neol == -1) {

			return message;

		}

		// Retrieve the value until the next "END_LINE" character
		final String contLengthString = message.substring(ctl + TOBEFOUND.length(), neol);
		try {
			contentLength = Integer.parseInt(contLengthString);
		} catch (final NumberFormatException e) {
			contentLength = 0;
		}

		// If the content length is the same as the postValue in bytes
		if (contentLength == postValue.getBytes().length) {

			return message;

		}

		final StringBuffer newMsgBuffer = new StringBuffer();
		newMsgBuffer.append(message.substring(0, ctl + TOBEFOUND.length()));
		try {

			newMsgBuffer.append(' ');
			newMsgBuffer.append(postValue.getBytes("ISO-8859-1").length);

		} catch (final UnsupportedEncodingException e) {

			// TODO: Log this exception
			newMsgBuffer.append(' ');
			newMsgBuffer.append(postValue.getBytes().length);

		}

		// If the remaining header starts with "END_LINE" then the 
		// "Content-Length was the last header line
		final String remainingHeader = this.message.substring(neol, eoh + (ENDCREDITS).length());
		if (remainingHeader.startsWith(END_LINE)) {
			newMsgBuffer.append(END_LINE);
			newMsgBuffer.append(END_LINE);
		} else {
			newMsgBuffer.append(remainingHeader);
		}

		newMsgBuffer.append(postValue);

		return newMsgBuffer.toString();

	}

	/**
	 * Very often users mistake the need for a double CRLF CRLF feed
	 * at the end of the request.
	 * 
	 * JBroFuzz can add this automatically in the case of a GET or 
	 * a HEAD or an OPTIONS request.
	 * 
	 * @param message
	 * @return
	 */
	private String doAppendCRLF(final String message) {

		final String ENDCREDITS = END_LINE + END_LINE;

		// TODO: Should really tidy up this method...

		if (	(message.startsWith("GET"))
				|| (message.startsWith("HEAD")) 
				|| (message.startsWith("OPTIONS")) ) {

			if (!this.message.endsWith(ENDCREDITS)) {

				return message + ENDCREDITS;

			} else {

				return message;
			}

		} else {

			return message;

		}
	}

	private String doBasicAuthHeader(final String url, final String message) {
		
		// Read proxy username:password value for Base64
		URL currentURL;
		try {
			
			currentURL = new URL(url);
			
		} catch (final MalformedURLException e) {
			
			return message;
		}
		
		final String userInfo = currentURL.getUserInfo();
		if (userInfo == null) {
		
			return message;
			
		}

		final String encoding = new sun.misc.BASE64Encoder().encode(userInfo.getBytes());
		final String authHeader = "Proxy-Authorization: Basic " + encoding + "\n";

		// We must add the authorization header prior to the "end credits"
		final String ENDCREDITS = END_LINE + END_LINE;
		// If no "end credits" have been found
		final int eoh = message.indexOf(ENDCREDITS);
		if(eoh == -1) {
			// Append some end credits to this request
			// make it a bit more healthy perhaps
			return message + authHeader + ENDCREDITS;

		} else {
			
			return stringReplace(ENDCREDITS, message, END_LINE + authHeader + ENDCREDITS);
		}

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
