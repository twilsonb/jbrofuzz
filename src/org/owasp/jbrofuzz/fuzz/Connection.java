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

import org.apache.commons.lang.*;
import javax.net.ssl.*;
import java.net.*;
import java.io.*;

/**
 * Description: The class responsible for making the connection for the purposes
 * of fuzzing through the corresponding socket.
 * 
 * <p>
 * This class gets used to establish each sequencial connection being made on a
 * given address, port and for a given request.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 0.9
 * @since 0.1
 */
public class Connection {

	// The content length header text used for a POST request
	private static final byte[] CONTENT_LENGTH = new String("Content-Length:")
			.getBytes();

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

	/**
	 * <p>
	 * Implement a connection to a particular address, on a given port,
	 * specifying the message to be transmitted.
	 * </p>
	 * 
	 * @param address
	 *            String
	 * @param port
	 *            String
	 * @param message
	 *            String
	 */
	public Connection(final String urlString, final String message)
			throws ConnectionException {

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e1) {
			reply = "Malformed URL : " + e1.getMessage() + "\n";
			throw new ConnectionException(reply);
		}

		protocol = url == null ? "" : url.getProtocol(); // http
		host = url == null ? "" : url.getHost(); // localhost
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

		this.message = message;

		final byte[] recv = new byte[Connection.RECV_BUF_SIZE];

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

			// Creating Client Sockets
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
					.getDefault();
			SSLSocket sslSocket = null;
			try {

				sslSocket = (SSLSocket) sslsocketfactory.createSocket(host,
						port);
				sslSocket.setSendBufferSize(Connection.SEND_BUF_SIZE);
				sslSocket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);
				sslSocket.setSoTimeout(30000);

			} catch (UnknownHostException e) {
				reply = "The IP address of the host could not be determined : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			} catch (IOException e) {
				reply = "An IO Error occured when creating the socket : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			}

			// Assign the input stream
			try {
				in_stream = sslSocket.getInputStream();
			} catch (final IOException e) {
				reply = "An IO Error occured when creating the input stream : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			}
			// Assign the output stream
			try {
				out_stream = sslSocket.getOutputStream();
			} catch (final IOException e) {
				reply = "An IO Error occured when creating the output stream : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			}

			// Write to the output stream
			try {
				out_stream.write(this.message.getBytes());
			} catch (final IOException e) {
				reply = "An IO Error occured when attempting to write to the output stream : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			}
			// Really don't like catching null pointer exceptions...
			catch (final NullPointerException e) {
				reply = "The output stream is null : " + e.getMessage();
				throw new ConnectionException(reply);
			}
			// Read the input stream, once you have finished writing to the
			// output
			try {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int got;
				while ((got = in_stream.read(recv)) > -1) {
					baos.write(recv, 0, got);

				} // while loop

				final byte[] allbytes = baos.toByteArray();
				reply = new String(allbytes);

			} catch (final IOException e) {

				throw new ConnectionException("An IO Exception occured: "
						+ e.getMessage());

			}
			// Close the socket
			try {
				sslSocket.close();
			} catch (final IOException e) {
				reply = "An IO Error occured when attempting to close the socket : "
						+ e.getMessage() + "\n";

				throw new ConnectionException(reply);

			}
			// -----

		}
		// Protocol is http going over a normal socket
		else {
			try {
				socket = new Socket();
				socket.bind(null);
				socket.connect(new InetSocketAddress(host, port), 5000);

				socket.setSendBufferSize(Connection.SEND_BUF_SIZE);
				socket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);
				socket.setSoTimeout(30000);
			} catch (final UnknownHostException e) {
				reply = "The IP address of the host could not be determined : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			} catch (final IOException e) {
				reply = "An IO Error occured when creating the socket : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);
			}

			// Assign the input stream
			try {
				in_stream = socket.getInputStream();
			} catch (final IOException e) {
				reply = "An IO Error occured when creating the input stream : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			}
			// Assign the output stream
			try {
				out_stream = socket.getOutputStream();
			} catch (final IOException e) {
				reply = "An IO Error occured when creating the output stream : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			}

			// Write to the output stream
			try {
				out_stream.write(this.message.getBytes());
			} catch (final IOException e) {
				reply = "An IO Error occured when attempting to write to the output stream : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);

			}
			// Really don't like catching null pointer exceptions...
			catch (final NullPointerException e) {
				reply = "The output stream is null : " + e.getMessage();
				throw new ConnectionException(reply);
			}
			// Read the input stream, once you have finished writing to the
			// output
			try {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int got;
				while ((got = in_stream.read(recv)) > -1) {
					baos.write(recv, 0, got);

				} // while loop

				final byte[] allbytes = baos.toByteArray();
				reply = new String(allbytes);

			} catch (final IOException e) {
				throw new ConnectionException("An IO Exception occured: "
						+ e.getMessage());
			}
			// Close the socket
			try {
				socket.close();
			} catch (final IOException e) {
				reply = "An IO Error occured when attempting to close the socket : "
						+ e.getMessage() + "\n";
				throw new ConnectionException(reply);
			}
		}
	}

	/**
	 * <p>
	 * Return the message request sent on the Socket.
	 * </p>
	 * 
	 * @return StringBuffer
	 */
	public String getMessage() throws ConnectionException {
		if (message.isEmpty()) {
			throw new ConnectionException("The message is blank");
		} else {
			return message;
		}
	}

	public int getPort() {

		return port;

	}

	/**
	 * <p>
	 * Return the reply from the Connection that has been made, based on the
	 * message that has been transmitted during construction.
	 * </p>
	 * 
	 * @return String
	 */
	public String getReply() throws ConnectionException {
		if (reply.isEmpty()) {
			throw new ConnectionException("The reply is blank");
		} else {
			return reply;
		}

	}

	public String getStatus() throws ConnectionException {

		String output = "---";

		String value;
		try {
			value = reply.split(" ")[1];
		} catch (Exception e) {
			throw new ConnectionException("Could not obtain the status");
		}

		if (StringUtils.isNumeric(value)) {
			output = value;
		}

		return output;
	}

}
