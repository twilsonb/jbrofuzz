/**
 * JbroFuzz 2.5
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
package org.owasp.jbrofuzz3.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * <p>An HTTP Request (regardless of version) can be created 
 * from a java String.</p>
 * 
 * <p>It can be broken down to a number of constant 
 * sub-strings, called elements and a number of different 
 * types.</p>
 * 
 * <p>Thus the process of obtaining the message as it to 
 * be put on the wire, becomes the process of iterating 
 * through the different types and 'padding' the 
 * respective values of elements where it is required.
 * 
 * @author subere@uncon.org
 * @version 2.5
 * @since 2.5
 *
 */
public class Request {

	private ArrayList<Byte> types;

	private ArrayList<String> elements;

	private static byte CRLF = 1;

	private static byte PLAINTEXT = 2;
	
	private final int messageBodyLocation;
	
	private final int contentLengthHeaderLocation;
	
	
	/**
	 * Simple test method, to be removed later on
	 * 
	 * @param args
	 */
	public static void main(String [] args) {
	
		String [] messages =
		{
				"", 

				"\n\n\n\n\n\n\n\n",

				"GET /ig/extern_js/f/CgJlbB/2qJTcRRHu10.js HTTP/1.1\n" +
				"Host: www.google.com\n" +
				"Referer: http://www.google.com/ig?refresh=1" + 
				"\n" +
				"\n",

				"GET /index.html",

				"POST /someURL.jsp?user=one HTTP/1.0\n" +
				"Host: localhost\n" +
				"Content-Length: 61\n" +
				"\n" +
				"project=WIP&surname=last&firstName=first&language=en",
				
				"POST /SomeLoc/SWService HTTP/1.1\n" + 
				"Host: somelocation.com\n" +
				"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13\n" +
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
				"Accept-Language: en-gb,en;q=0.5\n" +
				"Accept-Encoding: gzip,deflate\n" +
				"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n" +
				"Keep-Alive: 115\n" +
				"Connection: keep-alive" +
				"Referer: https://www.theref.com\n" +
				"Content-type: text/xml; charset=utf-8\n" +
				"SOAPAction: \"\"\n" +
				"Content-Length: 413\n" +
				"\n" +
				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
				"<soap:Body>\n" +
				"<getSecToken xmlns=\"http://logon.logonws.directory.ubs.com\">\n" + 
				"<username>asdf</username>\n" +
				"<password>asd</password>\n" +
				"<targetInfo/>\n" +
				"<versionInfo/>\n" +
				"</getSecurityToken>\n" +
				"</soap:Body>\n" +
				"</soap:Envelope>\n"	
		};
		
		for(String message : messages) {
			
			Request r = new Request(message);
			System.out.println(">>> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Start -->");
			System.out.println("Message:\n"+r.getRequestForDisplayPurposes());
			debug(r);
			System.out.println("Type size is: " + r.getTypes().size());
			System.out.println("Has message body: " + r.isMessageBodyPresent());
			System.out.println("Adding a header: Header: MyHeaderOne!");
			r.addHeader("Header: MyHeaderOne!");
			debug(r);
			System.out.println("New Message:\n"+r.getRequestForDisplayPurposes());
			System.out.println("<-- End <<< <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			
		}
		System.out.println("Done!");
	}
	
	public void addHeader(String header) {
		
		if(messageBodyLocation > 0) {
			
			final int lastHeaderType = types.size() - 2;
			types.add(lastHeaderType, Request.CRLF);
			types.add(lastHeaderType, Request.PLAINTEXT);
						
			final int lastHeaderElement = elements.size() - 1;
			elements.add(lastHeaderElement, header);
			
		} else {
			
			if(types.size() > 0) {
				types.add(Request.CRLF);
			}
			types.add(Request.PLAINTEXT);
			
			elements.add(header);
		}
	}
	
	/**
	 * <p>Method to check if a content-length header exists
	 * within the list of types and elements.</p>
	 * 
	 * @return The index within types where the content-length
	 * header is; -1 otherwise.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public int hasContentHeader() {
		
		Iterator<Byte> itrTypes = types.iterator();
		Iterator<String> itrData = elements.iterator();
		
		int counter = 0;
		while(itrTypes.hasNext()) {
			
			byte currentType = itrTypes.next();

			if(currentType != CRLF) {
				
				final String currentString = itrData.next().toLowerCase();
				
				if(currentString.startsWith("content-length:")) {
					return counter;
				}
				
			}
			
			counter++;

		}
		
		return -1;
		
	}
	
	/**
	 * <p>Method to check if the request has a message body.</p>
	 * 
	 * <p>The check is based upon the following pattern, ending
	 * the types array:
	 * <code> PLAINTEXT CRLF CRLF PLAINTEXT </code>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public boolean isMessageBodyPresent() {
		
		return (messageBodyLocation > 0);
		
	}
	
	/**
	 * <p>Method for checking if a content-length header is
	 * present within the request.</p>
	 * 
	 * @return true if present, false otherwise.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public boolean isContentLengthHeaderPresent() {
		
		return (contentLengthHeaderLocation > 0);
		
	}

	/**
	 * <p>Constructor responsible for passing a plain message,
	 * into a Request that can be placed on the wire.</p>
	 * 
	 * @param message e.g.
	 * <code>
	 * "POST /someURL.jsp?user=one HTTP/1.0\n" +
	 * "Host: localhost\n" +
	 * "\n\n"
	 * </code>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public Request(final String message) {

		// Initialise
		types = new ArrayList<Byte>();
		elements = new ArrayList<String>();
		// Break down the message into types & elements
		loadMessage(message, types, elements);
		// Remove any CRLFs from the end of the message
		removeEndCRLFs(types);
		// Check for message type and define it if present
		messageBodyLocation = findMessageBodyLocation();
		// Set the location of the content-length header; 
		// -1 if not present
		contentLengthHeaderLocation = hasContentHeader();
	}
	
	/**
	 * <p>This method removes up to 16 end of line types (i.e. CRLF 
	 * characters) from the end of the types array.</p>
	 * 
	 * @param types The array of types
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	private static void removeEndCRLFs(ArrayList<Byte> types) {

		byte count = 0;
		while(count < 16) {

			int lastType = types.size() - 1;
			
			if(lastType < 0) {
				
				return;
				
			} else {
			
				if(types.get(lastType) == CRLF) {
					types.remove(lastType);
				} else {
					return;
				}

			}
			
			count++;
		}
	}
	
	/**
	 * <p>Method that checks to see if the last element in the
	 * types array is a message body.</p>
	 * 
	 * <p>The check is based upon the following pattern, ending
	 * the types array:
	 * <code> PLAINTEXT CRLF CRLF PLAINTEXT </code>
	 * 
	 * @param types
	 * @return true if the above pattern is present
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	private int findMessageBodyLocation() {
		
		Iterator<Byte> itrTypes = types.iterator();
		int returnValue = -1;
		byte pattern1 = PLAINTEXT;
		byte pattern2 = CRLF;
		byte pattern3 = CRLF;
		byte pattern4 = PLAINTEXT;
		
		Stack<Byte> messageBodyPattern = new Stack<Byte>();
		
		while(itrTypes.hasNext()) {
		
			if(messageBodyPattern.size() == 4) {
				
			}
			messageBodyPattern.push(itrTypes.next());
		}
		
		
		
		return returnValue;
		
	}
	
	/**
	 * <p>Take a message and break it to two arrays:
	 * one of types and one of elements.</p>
	 * 
	 * <p>Thus the following message:
	 * <code>
	 * "POST /someURL.jsp?user=one HTTP/1.0\n" +
	 * "Host: localhost\n" +
	 * "\n\n"
	 * </code>
	 * would result in an array of types (bytes):
	 * <code>
	 * [ PLAINTEXT, CRLF, PLAINTEXT, CRLF, CRLF, CRLF]
	 * </code>
	 * and an array of elements (strings):
	 * <code>
	 * [ "POST /someURL.jsp?user=one HTTP/1.0"
	 *   "Host: localhost" ]
	 * </code>
	 * 
	 * @param message Note that only '\n' is read for CRLF
	 * @param types The array to be populated
	 * @param elements The array to be populated
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	private static void loadMessage(final String message, ArrayList<Byte> types, ArrayList<String> elements) {
		char[] charArray = message.toCharArray();
		StringBuffer sb = new StringBuffer();

		for(char c : charArray) {
			
			if(c == '\n') {
				
				int dataLength = sb.length();
				
				if(dataLength > 0) {
					elements.add(sb.toString());
					sb.delete(0, dataLength);
					types.add(PLAINTEXT);
				}
				types.add(CRLF);

			} else {
				sb.append(c);
			}
		}
		
		int dataLength = sb.length();
		if(dataLength > 0) {
			elements.add(sb.toString());
			sb.delete(0, dataLength);
			types.add(PLAINTEXT);
		}
	}
	
	/**
	 * Debug method to be commented out.
	 * 
	 * @param r
	 */
	private static void debug(Request r) {
		Iterator<Byte> itrTypes = r.getTypes().iterator();
		Iterator<String> itrData = r.getElements().iterator();
		
		System.out.println("Types Array: ");
		while (itrTypes.hasNext()) {
			System.out.print(itrTypes.next() + " ");
		}
		System.out.println("");
		
		System.out.println("Types Data: ");
		while (itrData.hasNext()) {
			System.out.print("[" + itrData.next() + "] ");
		}
		System.out.println("");

	}

	private ArrayList<String> getElements() {
		// TODO Auto-generated method stub
		return elements;
	}

	private ArrayList<Byte> getTypes() {
		// TODO Auto-generated method stub
		return types;
	}

	public String getRequest() {

		StringBuffer output = new StringBuffer();
		
		Iterator<Byte> itrTypes = types.iterator();
		Iterator<String> itrData = elements.iterator();
		
		while(itrTypes.hasNext()) {
			
			byte currentType = itrTypes.next();

			switch (currentType) {
			case 1:  
				output.append('\r');
				output.append('\n');
				break;
			case 2:  
				output.append(itrData.next()); 
				break;

			}

		}
		
		if( (types.size() > 0) && (!isMessageBodyPresent()) ) {
			output.append('\r');
			output.append('\n');
			output.append('\r');
			output.append('\n');			
		}

		return output.toString();
	}
	
	public String getRequestForDisplayPurposes() {

		StringBuffer output = new StringBuffer();
		
		Iterator<Byte> itrTypes = types.iterator();
		Iterator<String> itrData = elements.iterator();
		
		while(itrTypes.hasNext()) {
			
			byte currentType = itrTypes.next();

			switch (currentType) {
			case 1:  
				output.append("CRLF");
				output.append('\n');
				break;
			case 2:  
				output.append(itrData.next()); 
				break;

			}

		}
		
		if( (types.size() > 0) && (!isMessageBodyPresent()) ) {
			output.append("CRLF");
			output.append('\n');
			output.append("CRLF");
			output.append('\n');			
		}

		return output.toString();
	}

}
