/**
 * JBroFuzz 1.2
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * Description: The class responsible for making the connection for the purposes
 * of fuzzing through the corresponding socket.
 * 
 * <p>
 * This class gets used to establish each connection being made on a
 * given address, port and for a given request.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.1
 * @since 0.1
 */
public class Connection {

	// The maximum size for the socket I/O
	private final static int SEND_BUF_SIZE = 256 * 1024;
	private final static int RECV_BUF_SIZE = 256 * 1024;

	private String message;
	private Socket socket;
	private String reply;
	private URL url = null;
	private int port;
	private String host, protocol, file, ref;

	private InputStream in_stream;
	private OutputStream out_stream;

	private int socketTimeout;

	/**
	 * 
	 * 
	 * @param urlString
	 * @param message
	 * @throws ConnectionException
	 *
	 * @see 
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public Connection(final String urlString, final String message) throws ConnectionException {

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e1) {
			reply = "Malformed URL : " + e1.getMessage() + "\n";
			throw new ConnectionException(reply);
		}

		protocol = url == null ? "" : url.getProtocol(); // http
		host = url == null ? "" : url.getHost(); // host
		port = url == null ? -1 : url.getPort(); // 443
		file = url == null ? "" : url.getFile(); // index.jsp
		ref = url == null ? "" : url.getRef(); // _top_

		// Set default ports
		//
		if (protocol.equalsIgnoreCase("https") && (port == -1)) {
			port = 443;
		}
		if (protocol.equalsIgnoreCase("http") && (port == -1)) {
			port = 80;
		}
		
		// Set the socket timeout from the preferences
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		
		boolean maxTimeout = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_1, false);
		socketTimeout = maxTimeout ? 30000 : 5000;

		this.message = message;

		final byte[] recv = new byte[Connection.RECV_BUF_SIZE];

		// Proxy settings, if any
		
		String proxyHostText = prefs.get(JBroFuzzFormat.PROXY_HOST, "");
		String proxyPortText = prefs.get(JBroFuzzFormat.PROXY_PORT, "");

		if((!proxyHostText.isEmpty()) && (!proxyPortText.isEmpty())) {
			System.setProperty("socksProxyHost", proxyHostText);
			System.setProperty("socksProxyPort", proxyPortText);
		}

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
			.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {

			throw new ConnectionException(
					"Could not install all-trusting certificates... "
					+ e.getMessage());

		}


		// Create the Socket to the specified address and port
		if (protocol.equalsIgnoreCase("https")) {

			// Create a normal SSL Socket if HTTP/1.1 is not being used
			if (!protocolIsHTTP11(message)) {
				// Creating Client Sockets
				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				SSLSocket sslSocket;
				try {

					sslSocket = (SSLSocket) sslsocketfactory.createSocket(host, port);
					sslSocket.setSendBufferSize(Connection.SEND_BUF_SIZE);
					sslSocket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);
					sslSocket.setSoTimeout(socketTimeout);

					in_stream = sslSocket.getInputStream();
					out_stream = sslSocket.getOutputStream();

					out_stream.write(this.message.getBytes());

					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int got;
					while ((got = in_stream.read(recv)) > -1) {
						baos.write(recv, 0, got);

					} // while loop

					in_stream.close();
					out_stream.close();
					sslSocket.close();

					final byte[] allbytes = baos.toByteArray();
					reply = new String(allbytes);

				} catch (UnknownHostException e) {
					reply = "The IP address of the host could not be determined : "
						+ e.getMessage() + "\n";
					throw new ConnectionException(reply);

				} catch (IOException e) {
					reply = "An IO Error occured on socket : " + e.getMessage()
					+ "\n";
					throw new ConnectionException(reply);

				}
			}
			else { // SSL: If HTTP/1.1 is being used, handle separately

				try {

					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					SSLSocket sslSocket;

					sslSocket = (SSLSocket) sslsocketfactory.createSocket(host,port);
					sslSocket.setSendBufferSize(Connection.SEND_BUF_SIZE);
					sslSocket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);
					sslSocket.setSoTimeout(socketTimeout);

					in_stream = sslSocket.getInputStream();
					out_stream = sslSocket.getOutputStream();

					out_stream.write(this.message.getBytes());

					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int got;
					boolean end_reached = false;
					while ( (!end_reached) && ((got = in_stream.read(recv)) > -1) ) {

						baos.write(recv, 0, got);

						// Check if \r\n has come in, in its many shapes and forms
						final String incoming = new String(baos.toByteArray()); 
						if(incoming.contains("\r\n\r\n") || incoming.contains("\n\n")|| incoming.contains("\r\r") ) {

							// Check if Chunked Encoding is being used
							if(incoming.contains("Transfer-Encoding: chunked")) {

								if(incoming.contains("\r\n0\r\n") || incoming.contains("\n0\n")|| incoming.contains("\r0\r") ) {
									end_reached = true;
								}

							} else {
								end_reached = true;
							}

						}


					} // while loop

					in_stream.close();

					final byte[] allbytes = baos.toByteArray();
					reply = new String(allbytes);


				} catch (MalformedURLException e) {
					reply = "Malformed URL: " + e.getMessage() 
					+ "\n";
					throw new ConnectionException(reply);
				} catch (IOException e) {
					reply = "An IO Error occured on the URL : " + e.getMessage()
					+ "\n";
					throw new ConnectionException(reply);

				}


			}


		}
		// Default protocol is HTTP going over a normal socket
		else {

			// Create a normal Socket if HTTP/1.1 is not being used
			if (!protocolIsHTTP11(message)) {
				try {
					socket = new Socket(host, port);
					socket.setSendBufferSize(Connection.SEND_BUF_SIZE);
					socket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);
					socket.setSoTimeout(socketTimeout);

					in_stream = socket.getInputStream();
					out_stream = socket.getOutputStream();

					out_stream.write(this.message.getBytes());

					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int got;
					while ((got = in_stream.read(recv)) > -1) {
						baos.write(recv, 0, got);

					} // while loop

					in_stream.close();
					out_stream.close();
					socket.close();

					final byte[] allbytes = baos.toByteArray();
					reply = new String(allbytes);

				} catch (final IllegalArgumentException e) {
					reply = "Bad arguments : " + e.getMessage() + "\n";
					throw new ConnectionException(reply);

				} catch (final UnknownHostException e) {
					reply = "The IP address of the host could not be determined : "
						+ e.getMessage() + "\n";
					throw new ConnectionException(reply);

				} catch (final IOException e) {
					reply = "An IO Error occured on socket : " + e.getMessage()
					+ "\n";
					throw new ConnectionException(reply);
				}
			}
			else { // If HTTP/1.1 is being used, handle separately

				try {
					socket = new Socket(host, port);
					socket.setSendBufferSize(Connection.SEND_BUF_SIZE);
					socket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);
					socket.setSoTimeout(socketTimeout);

					in_stream = socket.getInputStream();
					out_stream = socket.getOutputStream();

					out_stream.write(this.message.getBytes());

					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int got;
					boolean end_reached = false;
					while ( (!end_reached) && ((got = in_stream.read(recv)) > -1) ) {

						baos.write(recv, 0, got);

						// Check if \r\n has come in, in its many shapes and forms
						final String incoming = new String(baos.toByteArray()); 
						if(incoming.contains("\r\n\r\n") || incoming.contains("\n\n")|| incoming.contains("\r\r") ) {

							// Check if Chunked Encoding is being used
							if(incoming.contains("Transfer-Encoding: chunked")) {

								if(incoming.contains("\r\n0\r\n") || incoming.contains("\n0\n")|| incoming.contains("\r0\r") ) {
									end_reached = true;
								}

							} else {
								end_reached = true;
							}

						}


					} // while loop

					in_stream.close();
					out_stream.close();
					socket.close();

					final byte[] allbytes = baos.toByteArray();
					reply = new String(allbytes);

				} catch (final IllegalArgumentException e) {
					reply = "Bad arguments : " + e.getMessage() + "\n";
					throw new ConnectionException(reply);

				} catch (final UnknownHostException e) {
					reply = "The IP address of the host could not be determined : "
						+ e.getMessage() + "\n";
					throw new ConnectionException(reply);

				} catch (final IOException e) {
					reply = "An IO Error occured on socket : " + e.getMessage()
					+ "\n";
					throw new ConnectionException(reply);
				}


			}

		}
	}

	public String getMessage() {
		if (message.isEmpty()) {
			return "[JBROFUZZ REQUEST IS BLANK]";
		} else {
			return message;
		}
	}

	public String getPort() {

		if (port == -1) {
			return "[JBROFUZZ PORT IS INVALID]";
		} else {
			return "" + port;
		}

	}

	/**
	 *  <p>
	 * Return the reply from the Connection that has been made, based on the
	 * message that has been transmitted during construction.
	 * </p>
	 * <p>Revisited this method in JBroFuzz 1.2 in order NOT to throw an 
	 * exception if the reply string is empty, see {@link #getMessage()} for
	 * old implementation logic.</p>
	 * 
	 * @return String The reply string
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.0 
	 */
	public String getReply() {

		if (reply.isEmpty()) {
			return "[JBROFUZZ REPLY IS EMPTY]";
		} else {
			return reply;
		}
		

	}

	/**
	 * <p>Return the HTTP status code, e.g. 200, 404, etc.</p>
	 * <p>In case of a non-existant code, return "---".</p>
	 * 
	 * @return String of three characters with the code value.
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public String getStatus() {

		try {
			final String out = reply.split(" ")[1].substring(0, 3);
			
			if(StringUtils.isNumeric(out)) {
				return out;
			} else {
				return "000";
			}
			
		} catch (Exception e) {
			
			return "---";
			
		}

	}

	/**
	 * <p>Method for checking if the actual String given is an HTTP/1.1
	 * request.</p>
	 * <p>This check entails looking for the first line (as divided by \r\n
	 * to be finishing with the String literal "HTTP/1.1" in uppercase or
	 * lowercase.</p>
	 * 
	 * @param message
	 * @return
	 */
	public boolean protocolIsHTTP11(String message) {

		try {

			String line = message.split("\r\n")[0];
			if (line.toLowerCase().endsWith("http/1.1")) {
				return true;
			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
