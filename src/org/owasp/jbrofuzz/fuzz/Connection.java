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
package org.owasp.jbrofuzz.fuzz;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

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
 * @version 2.2
 * @since 0.1
 */
public class Connection {
	
	private final SocketConnection mainConnection;
	
	public Connection(final String urlString, final String message)
	throws ConnectionException {
		
		URL url;
		try {
			url = new URL(urlString);
		} catch (final MalformedURLException e1) {
			throw new ConnectionException("Malformed URL : " + e1.getMessage() + "\n");
		}
		final String protocol = url.getProtocol();
		final String host = url.getHost();
		int port = url.getPort();
		// Allow only HTTP/S as protocols
		if ((!protocol.equalsIgnoreCase("http"))
				&& (!protocol.equalsIgnoreCase("https"))) {
			throw new ConnectionException("Protocol is not http://, nor is it https://\n");
		}
		
		// Set default ports
		if (protocol.equalsIgnoreCase("https") && (port == -1)) {
			port = 443;
		}
		if (protocol.equalsIgnoreCase("http") && (port == -1)) {
			port = 80;
		}
			
		mainConnection = new SocketConnection(protocol, host, port, message);
	}

	public String getMessage() {
		
		return mainConnection.getMessage();
		
	}

	public String getPort() {

		return mainConnection.getPort();

	}

	public String getReply() {
		
		return mainConnection.getReply();
	}

	public String getStatus() {
		
		return mainConnection.getStatus();
		
	}


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
	protected static final SSLSocketFactory getSocketFactory() throws ConnectionException {

		try {
			final TrustManager[] tManager = new TrustManager[] { 
					new FullyTrustingManager() 
			};
			final SSLContext context = SSLContext.getInstance("SSL");
			context.init(new KeyManager[0], tManager, new SecureRandom());

			return context.getSocketFactory();

		} catch (final KeyManagementException e) {
			throw new ConnectionException("No SSL algorithm support.");
		} catch (final NoSuchAlgorithmException e) {
			throw new ConnectionException("Exception when setting up the Naive key management.");
		}

	}

}
