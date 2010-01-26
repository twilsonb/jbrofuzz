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
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.JBroFuzz;

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
public class Connection implements AbstractConnection {
	
	AbstractConnection mainConnection;
	
	public Connection(final String urlString, final String message)
	throws ConnectionException {
		
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e1) {
			throw new ConnectionException("Malformed URL : " + e1.getMessage() + "\n");
		}

		String protocol = url.getProtocol();
		String host = url.getHost();
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

		// Check the message for it being an HTTP 1.1
		if(protocolIsHTTP11(message)) {
			mainConnection = new HTTPConnection(urlString, message);
		} else {
			mainConnection = new SocketConnection(urlString, message);
		}
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

	public final boolean protocolIsHTTP11(String message) {

		if(message.contains("HTTP/1.1")) {
			return true;
		}
		
		return false;
		
	}

	public final String getPostDataInMessage() {

		return mainConnection.getPostDataInMessage();

	}

	public boolean isResponse100Continue() {

		return mainConnection.isResponse100Continue();

	}

}
