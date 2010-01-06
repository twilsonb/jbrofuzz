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
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.prefs.Preferences;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * Description: The class responsible for making the connection for the purposes
 * of fuzzing through the corresponding socket.
 * 
 * <p>
 * This class gets used to establish each connection being made on a given
 * address, port and for a given request.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.9
 * @since 0.1
 */
public class Connection {

	// The maximum size for the socket I/O
	// private final static int SEND_BUF_SIZE = 256 * 1024;
	private final static int RECV_BUF_SIZE = 256 * 1024;

	// Singleton SSLSocket factory used with it's factory
	private static SSLSocketFactory mSSLSocketFactory;

	/**
	 * <p>
	 * Returns a SSL factory instance that trusts all server certificates.
	 * </p>
	 * 
	 * <p>
	 * Used by the Connection constructor for the SSL socket.
	 * </p>
	 * 
	 * <p>
	 * In the event of an exception, the factory method defaults to a normal
	 * SSLSocketFactory.
	 * </p>
	 * 
	 * @return SSLSocketFactory an SSL socket factory
	 * 
	 * @since 1.3
	 */
	private static final SSLSocketFactory getSocketFactory() throws ConnectionException {

		try {
			TrustManager[] tm = new TrustManager[] { new FullyTrustingManager() };
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(new KeyManager[0], tm, new SecureRandom());

			return context.getSocketFactory();

		} catch (KeyManagementException e) {
			throw new ConnectionException("No SSL algorithm support.");
		} catch (NoSuchAlgorithmException e) {
			throw new ConnectionException("Exception when setting up the Naive key management.");
		}

		// return (SSLSocketFactory) SSLSocketFactory.getDefault();
	}

	private String message;
	private Socket mSocket;
	private String reply;
	private URL url = null;
	private int port;

	private String host, protocol;
	private InputStream inStream;

	private OutputStream outStream;

	private int socketTimeout;

	private boolean isResponse100Continue;

	/**
	 * <p>
	 * The constructor for the connection, responsible for creating the
	 * corresponding socket (or SSL socket) and transmitting/receiving data from
	 * the wire.
	 * </p>
	 * 
	 * @param urlString
	 *            The url string from which the protocol (e.g. "https"), the
	 *            host (e.g. www.owasp.org) and the port number will be
	 *            determined.
	 * 
	 * @param message
	 *            of what to put on the wire, once a connection has been
	 *            established.
	 * 
	 * @throws ConnectionException
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 0.1
	 */
	public Connection(final String urlString, final String message)
	throws ConnectionException {

		isResponse100Continue = false;

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e1) {
			reply = "Malformed URL : " + e1.getMessage() + "\n";
			throw new ConnectionException(reply);
		}

		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();

		// Allow only HTTP/S as protocols
		if ((!protocol.equalsIgnoreCase("http"))
				&& (!protocol.equalsIgnoreCase("https"))) {
			reply = "Protocol is not http://, nor is it https://\n";
			throw new ConnectionException(reply);
		}

		// Set default ports
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

		try {

			if (protocol.equalsIgnoreCase("https")) {

				// Make sure we have a factory for the SSL socket
				if (mSSLSocketFactory == null) {
					mSSLSocketFactory = getSocketFactory();
				}

				// Handle HTTPS differently then HTTP
				mSocket = mSSLSocketFactory.createSocket(host, port);
				mSocket.setSoTimeout(socketTimeout);

			} else {

				// Handle HTTP differently then HTTPS
				mSocket = new Socket();
				mSocket.connect(new InetSocketAddress(host, port),
						socketTimeout);
			}

			// Set buffers, streams, smile...
			mSocket.setSendBufferSize(this.message.getBytes().length);
			mSocket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);

			inStream = mSocket.getInputStream();
			outStream = mSocket.getOutputStream();

			// Put message on the wire
			outStream.write(this.message.getBytes());

			// Read response, see what you have back
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int got;

			// Treat HTTP/1.1 differently because of chunked encoding
			if (protocolIsHTTP11(message)) {

				boolean end_reached = false;
				while ((!end_reached) && ((got = inStream.read(recv)) > -1)) {

					baos.write(recv, 0, got);

					// Check if \r\n has come in, in its many shapes and forms
					final String incoming = new String(baos.toByteArray());
					if (incoming.contains("\r\n\r\n")
							|| incoming.contains("\n\n")
							|| incoming.contains("\r\r")) {

						// Check if Chunked Encoding is being used
						if (incoming.contains("Transfer-Encoding: chunked")) {

							if (incoming.contains("\r\n0\r\n")
									|| incoming.contains("\n0\n")
									|| incoming.contains("\r0\r")) {
								end_reached = true;
							}

						} else {
							end_reached = true;
						}

						// Check if a 100 Continue has come back
						if (incoming.contains("HTTP/1.1 100 Continue")) {

							// Check if the preferences are set to re-send the
							// POST Data if a 100 continue
							boolean follow100Continue = prefs.getBoolean(
									JBroFuzzFormat.PR_FUZZ_4, true);

							if (follow100Continue) {

								isResponse100Continue = true;

								// Put the POST data of the message on the wire,
								// also
								// write them as output
								final byte[] postData = (getPostDataInMessage())
								.getBytes();
								outStream.write(postData);
								baos.write(postData, 0, postData.length);
								// If so, check for chunked encoding again (not
								// tidy)
								boolean end_reached2 = false;
								while ((!end_reached2)
										&& ((got = inStream.read(recv)) > -1)) {

									final String cont_100_mark = "\r\n--jbrofuzz--100-continue-->\r\n";
									baos.write(cont_100_mark.getBytes(), 0,
											cont_100_mark.getBytes().length);

									baos.write(recv, 0, got);

									// Check if \r\n has come in, in its many
									// shapes and forms
									final String incoming2 = new String(baos
											.toByteArray());
									if (incoming.contains("\r\n\r\n")
											|| incoming2.contains("\n\n")
											|| incoming2.contains("\r\r")) {

										// Check if Chunked Encoding is being
										// used
										if (incoming2
												.contains("Transfer-Encoding: chunked")) {

											if (incoming2.contains("\r\n0\r\n")
													|| incoming2
													.contains("\n0\n")
													|| incoming2
													.contains("\r0\r")) {
												end_reached2 = true;
											}

										} else {
											end_reached2 = true;
										}
									}

								} // while loop for chunked encoding

							} // if 100 continue is true
						}

					}

				} // while loop

			} else {

				// If no HTTP/1.1 just read the stream, until the end
				while ((got = inStream.read(recv)) > -1) {
					baos.write(recv, 0, got);
				}

			}

			inStream.close();
			outStream.close();
			mSocket.close();

			reply = new String(baos.toByteArray());

		} catch (MalformedURLException e1) {

			reply = "Malformed URL: " + e1.getMessage() + "\n";
			throw new ConnectionException(reply);

		} catch (InterruptedIOException e2) {

			reply = "Connection Timeout after: " + socketTimeout + " ms\n";
			throw new ConnectionException(reply);

		} catch (IOException e3) {

			reply = "An IO Error occured: " + e3.getMessage() + "\n";
			throw new ConnectionException(reply);

		} finally {

			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);

		}

	}

	/**
	 * <p>
	 * Get the message being put on the wire in this connection.
	 * </p>
	 * 
	 * @return String message or "[JBROFUZZ REQUEST IS BLANK]" if message is
	 *         empty.
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String getMessage() {
		if (message.isEmpty()) {
			return "[JBROFUZZ REQUEST IS BLANK]";
		} else {
			return message;
		}
	}

	/**
	 * <p>
	 * Get the port number used in the connection being made.
	 * </p>
	 * 
	 * @return [1-65535] or "[JBROFUZZ PORT IS INVALID]"
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String getPort() {

		if (port == -1) {
			return "[JBROFUZZ PORT IS INVALID]";
		} else {
			return "" + port;
		}

	}

	/**
	 * <p>
	 * Return the reply from the Connection that has been made, based on the
	 * message that has been transmitted during construction.
	 * </p>
	 * <p>
	 * Revisited this method in JBroFuzz 1.9 in order NOT to throw an exception
	 * if the reply string is empty, see {@link #getMessage()} for old
	 * implementation logic.
	 * </p>
	 * 
	 * @return String The reply string
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
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
	 * <p>
	 * Return the HTTP status code, e.g. 200, 404, etc.
	 * </p>
	 * <p>
	 * In case of a non-existant code, return "---".
	 * </p>
	 * <p>
	 * In the case of a "100-Continue", return "100/xxx" where "xxx" is defined
	 * above.
	 * </p>
	 * 
	 * @return String of 3 or 7 characters with the code value.
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	public String getStatus() {
		// If the response was not a 100 continue
		if (!isResponse100Continue) {

			try {
				final String out = reply.split(" ")[1].substring(0, 3);

				if (StringUtils.isNumeric(out)) {
					return out;
				} else {
					return "000";
				}

			} catch (Exception exception1) {

				return "---";

			}
			// else we have received a 100 continue
		} else {

			try {
				final String out2 = reply
				.split("\r\n--jbrofuzz--100-continue-->\r\n")[1];
				final String out3 = out2.split(" ")[1].substring(0, 3);

				if (StringUtils.isNumeric(out3)) {
					return "100/" + out3;
				} else {
					return "100/000";
				}
			} catch (Exception exception2) {

				return "100/---";
			}

		}

	}

	/**
	 * <p>
	 * Method for checking if the actual String given is an HTTP/1.1 request.
	 * </p>
	 * <p>
	 * This check entails looking for the first line (as divided by \r\n to be
	 * finishing with the String literal "HTTP/1.1" in upper-case or lower-case.
	 * </p>
	 * 
	 * @param message
	 *            The input string used
	 * @return boolean True if HTTP/1.1 is found on the first line
	 */
	public final boolean protocolIsHTTP11(String message) {

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

	/**
	 * <p>
	 * Method for returning the POST Data being submitted.
	 * </p>
	 * <p>
	 * If no POST Data is found the input message is returned.
	 * </p>
	 * 
	 * @param message
	 * @version 1.5
	 * @since 1.5
	 * @return
	 */
	public final String getPostDataInMessage() {

		try {

			return message.split("\r\n\r\n")[1];

		} catch (Exception e1) {

			return message;

		}

	}

	public boolean isResponse100Continue() {

		return isResponse100Continue;

	}

}
