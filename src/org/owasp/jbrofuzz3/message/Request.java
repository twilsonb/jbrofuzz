package org.owasp.jbrofuzz3.message;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

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

				"GET / HTTP/1.0\n" +
				"Host: locahost\n" +
				"\n" +
				"username=user_1&password=pass",

				"GET /index.html",

				"POST /someURL.jsp?user=one HTTP/1.0\n" +
				"Host: localhost\n" +
				"\n\n"
		};
		
		for(String message : messages) {
			
			Request r = new Request(message);
			System.out.println(">>> Start -->");
			System.out.println("Message:\n"+r.getRequestForDisplayPurposes());
			debug(r);
			System.out.println("Type size is: " + r.getTypes().size());
			System.out.println("<-- End <<<");
			
		}
		System.out.println("Done!");
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
	}
	
	/**
	 * <p>This method removes up to 16 end of line types (i.e. CRLF 
	 * characters) from the end of the types array.</p>
	 * 
	 * @param types The array of types
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

	private AbstractList<String> getElements() {
		// TODO Auto-generated method stub
		return elements;
	}

	private AbstractList<Byte> getTypes() {
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

		return output.toString();
	}

}
