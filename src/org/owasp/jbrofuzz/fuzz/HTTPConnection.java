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
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;



/**
 * Description: The class responsible for making the HTTP connection for the purposes
 * of fuzzing using the Apache HTTP Components.
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class HTTPConnection implements AbstractConnection {

	private final String protocol;
	private final String host;
	private final int port;
	private final String message;
	
	private String reply;
	
	private int statusCode;
	
	/**
	 * <p>
	 * The constructor for the HTTP connection. Actually make the connection.
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
	 * @version 2.0
	 * @since 2.0
	 */
	public HTTPConnection(final String protocol, final String host, final int port, final String message) 
	throws ConnectionException {
		// Object construction
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.message = message;

		// Parameters (not being used at the moment)
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		// HTTP Method: GET, HEAD, POST, PUT, DELETE, TRACE and OPTIONS
		HttpRequestBase httpMethod = null;
		if(message.startsWith("GET")) {
			httpMethod = new HttpGet(host);
		}

		try {
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpMethod);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				// Read the entity response, copy it into the byte array stream
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream instream = entity.getContent();
				
				int l;
				byte[] tmp = new byte[2048];
				while ((l = instream.read(tmp)) != -1) {
					baos.write(tmp, 0, l);
				}
				
				reply = new String(baos.toByteArray());
			}
			
			reply = "Entity is NULL";
			statusCode = response.getStatusLine().getStatusCode();
			
		} catch(Exception e4) {
			
			reply = "An Exception occured.";
			statusCode = 0;
			throw new ConnectionException(e4.getMessage());
			
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
			return "" + port;
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
		return "" + statusCode;
	}

	/* (non-Javadoc)
	 * @see org.owasp.jbrofuzz.fuzz.AbstractConnection#protocolIsHTTP11(java.lang.String)
	 */
	public final boolean protocolIsHTTP11(String message) {

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

}
