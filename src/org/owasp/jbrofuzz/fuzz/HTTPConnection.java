/**
 * JBroFuzz 1.9
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;



/**
 * Description: The class responsible for making the HTTP connection for the purposes
 * of fuzzing using the Apache HTTP Components.
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class HTTPConnection implements AbstractConnection {

	private final static int RECV_BUF_SIZE = 256 * 1024;

	// Singleton SSLSocket factory used with it's factory
	private static SSLSocketFactory mSSLSocketFactory;

	private final String protocol;
	private final String message;
	private final String host;

	private transient String reply;	
	private transient int port;

	private transient InputStream inStream;
	// private transient OutputStream outStream;

	private int statusCode; 
	
	private int socketTimeout;
	
	/**
	 * <p>
	 * The constructor for the HTTP connection. Actually make the connection.
	 * </p>
	 * 
	 * @throws ConnectionException
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	protected HTTPConnection(final String protocol, final String host, final int port, final String message) 
	throws ConnectionException {
		// Object construction
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.message = message;
		
		// Get the file part from the message, rubbish...
		String file;
		try {

			file = message.split("\r\n")[0].split(" ")[1];

		} catch (Exception e1) {

			file = "";
		}

		
		final byte[] recv = new byte[RECV_BUF_SIZE];

		try {

			URL mURL = new URL(protocol, host, port, file);
			
			if (protocol.equalsIgnoreCase("https")) {

				HttpsURLConnection myCon = (HttpsURLConnection) mURL.openConnection();

				// Make sure we have a factory for the SSL socket
				if (mSSLSocketFactory == null) {
					mSSLSocketFactory = Connection.getSocketFactory();
				}
				// that way no invalid certificates will ever complain
				myCon.setSSLSocketFactory(mSSLSocketFactory);
				// Not complete...
				//
			} else {	// HTTP Connection
				
				HttpURLConnection myCon = (HttpURLConnection) mURL.openConnection();
					
					String [] headers = message.split("\r\n\r\n")[0].split("\r\n");
					// Set all headers..
					for(int i = 1; i < headers.length; i++) {
						String[] fieldLine = headers[i].split(":");
						
						StringUtils.trimToEmpty(fieldLine[0]);
						StringUtils.trimToEmpty(fieldLine[1]);
						
						// Possibly do empty checks here later
						
						myCon.addRequestProperty(fieldLine[0], fieldLine[1]);
					}
					
					// Set the method GET / POST / only a handful are supported
					String method = headers[0].split(" ")[0];
					System.out.println(method); // why oh why you print a GET but do a POST?
					myCon.setRequestMethod(method);
					
					// Check if there is post data equivalent present
					if(message.split("\r\n\r\n").length > 1) {
						
						String postData = message.split("\r\n\r\n")[1];
						
						myCon.setDoOutput(true);
						myCon.setUseCaches(false);
						
						myCon.setRequestProperty("Content-Length", 
								"" + Integer.toString(postData.getBytes().length));
						
						// Connect
						OutputStreamWriter out = new OutputStreamWriter(
	                              myCon.getOutputStream());
						
						out.write(postData);
						out.close();

					} else {
						// Connect
						myCon.connect();
					}
					
					// Maybe put the per-below in a separate try-catch block
					inStream = myCon.getInputStream();
					
					// Get the timeout value on the Socket
					socketTimeout = JBroFuzz.PREFS.getInt("Socket.Max.Timeout", 7);
					// validate
					if( (socketTimeout < 1) || (socketTimeout > 51) ) {
						socketTimeout = 7;
					}
					// Start timer
					final SocketTimer timer = new SocketTimer(this, socketTimeout * 1000);
					timer.start();

					// Read response, see what you have back
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int got;
					while ((got = inStream.read(recv)) > -1) {
						baos.write(recv, 0, got);
					}

					// If the timer is not reset, close() will be called
					timer.reset();

					baos.close();

					inStream.close();
					// outStream.close();
					
					myCon.disconnect();
					
					reply = new String(baos.toByteArray());
					

				
				
				
			}


		

		} catch (MalformedURLException e1) {

			reply = "Malformed URL: " + e1.getMessage() + "\n";
			throw new ConnectionException(reply);

		} catch (IOException e3) {

			reply = "An IO Error occured: " + e3.getMessage() + 
			". \n\nThis could also be a Connection Timeout, " +
			"\ntry increasing the value under Preferences ->" +
			" Fuzzing\n";
			throw new ConnectionException(reply);

		} finally {

			IOUtils.closeQuietly(inStream);
			// IOUtils.closeQuietly(outStream);

		}
		
		
		
		
		
		// Proxy credentials, if enabled
		final boolean proxyEnabled = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.PROXY[0], false);

		
		if(proxyEnabled) {
			final String proxyHost = JBroFuzz.PREFS.get(JBroFuzzPrefs.PROXY[1], "");
			final int proxyPort = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.PROXY[2], 0);
			final String proxyUser = JBroFuzz.PREFS.get(JBroFuzzPrefs.PROXY[3], "");
			final String proxyPass = JBroFuzz.PREFS.get(JBroFuzzPrefs.PROXY[4], "");
		}
			



	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#getMessage()
	 */
	public String getMessage() {
		if (message.isEmpty()) {
			return "[JBROFUZZ REQUEST IS BLANK]";
		} else {
			return message;
		}
	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#getPort()
	 */
	public String getPort() {

		if (port == -1) {
			return "[JBROFUZZ PORT IS INVALID]";
		} else {
			return Integer.toString(port);
		}

	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#getReply()
	 */
	public String getReply() {

		if (reply.isEmpty()) {
		return "[JBROFUZZ REPLY IS EMPTY]";
		} else {
		return reply;
		}

	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#getStatus()
	 */
	public String getStatus() {
		return Integer.toString(statusCode);
	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#protocolIsHTTP11(java.lang.String)
	 */
	public final boolean protocolIsHTTP11(final String message) {

		return true;
	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#getPostDataInMessage()
	 */
	public final String getPostDataInMessage() {

		try {

			return message.split("\r\n\r\n")[1];

		} catch (Exception e1) {

			return message;

		}

	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#isResponse100Continue()
	 */
	public boolean isResponse100Continue() {

		return false;

	}

	@Override
	public void close() {

		IOUtils.closeQuietly(inStream);
		// IOUtils.closeQuietly(outStream);

	}
}
