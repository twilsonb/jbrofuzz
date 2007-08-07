/*
 * Protocol.java 0.6
 *
 * ====================================================================
 *
 *  Copyright 2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 */
package org.apache.commons.httpclient.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to encapsulate the specifics of a protocol. This class class also
 * provides the ability to customize the set and characteristics of the
 * protocols used.
 * 
 * <p>
 * One use case for modifying the default set of protocols would be to set a
 * custom SSL socket factory. This would look something like the following:
 * 
 * <pre>
 * Protocol myHTTPS = new Protocol(&quot;https&quot;, new MySSLSocketFactory(), 443);
 * 
 * Protocol.registerProtocol(&quot;https&quot;, myHTTPS);
 * </pre>
 * 
 * @author Michael Becke
 * @author Jeff Dever
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * 
 * @since 2.0
 */
public class Protocol {

	/** The available protocols */
	private static final Map PROTOCOLS = Collections
			.synchronizedMap(new HashMap());

	/**
	 * Gets the protocol with the given ID.
	 * 
	 * @param id
	 *          the protocol ID
	 * 
	 * @return Protocol a protocol
	 * 
	 * @throws IllegalStateException
	 *           if a protocol with the ID cannot be found
	 */
	public static Protocol getProtocol(final String id)
			throws IllegalStateException {

		if (id == null) {
			throw new IllegalArgumentException("id is null");
		}

		Protocol protocol = (Protocol) Protocol.PROTOCOLS.get(id);

		if (protocol == null) {
			protocol = Protocol.lazyRegisterProtocol(id);
		}

		return protocol;
	}

	/**
	 * Lazily registers the protocol with the given id.
	 * 
	 * @param id
	 *          the protocol ID
	 * 
	 * @return the lazily registered protocol
	 * 
	 * @throws IllegalStateException
	 *           if the protocol with id is not recognized
	 */
	private static Protocol lazyRegisterProtocol(final String id)
			throws IllegalStateException {

		if ("http".equals(id)) {
			final Protocol http = new Protocol("http", DefaultProtocolSocketFactory
					.getSocketFactory(), 80);
			Protocol.registerProtocol("http", http);
			return http;
		}

		if ("https".equals(id)) {
			final Protocol https = new Protocol("https", SSLProtocolSocketFactory
					.getSocketFactory(), 443);
			Protocol.registerProtocol("https", https);
			return https;
		}

		throw new IllegalStateException("unsupported protocol: '" + id + "'");
	}

	/**
	 * Registers a new protocol with the given identifier. If a protocol with the
	 * given ID already exists it will be overridden. This ID is the same one used
	 * to retrieve the protocol from getProtocol(String).
	 * 
	 * @param id
	 *          the identifier for this protocol
	 * @param protocol
	 *          the protocol to register
	 * 
	 * @see #getProtocol(String)
	 */
	public static void registerProtocol(final String id, final Protocol protocol) {

		if (id == null) {
			throw new IllegalArgumentException("id is null");
		}
		if (protocol == null) {
			throw new IllegalArgumentException("protocol is null");
		}
		Protocol.PROTOCOLS.put(id, protocol);
	}

	/**
	 * Unregisters the protocol with the given ID.
	 * 
	 * @param id
	 *          the ID of the protocol to remove
	 */
	public static void unregisterProtocol(final String id) {

		if (id == null) {
			throw new IllegalArgumentException("id is null");
		}

		Protocol.PROTOCOLS.remove(id);
	}

	/** the scheme of this protocol (e.g. http, https) */
	private String scheme;

	/** The socket factory for this protocol */
	private ProtocolSocketFactory socketFactory;

	/** The default port for this protocol */
	private int defaultPort;

	/** True if this protocol is secure */
	private boolean secure;

	/**
	 * Constructs a new Protocol. The created protcol is insecure.
	 * 
	 * @param scheme
	 *          the scheme (e.g. http, https)
	 * @param factory
	 *          the factory for creating sockets for communication using this
	 *          protocol
	 * @param defaultPort
	 *          the port this protocol defaults to
	 */
	public Protocol(final String scheme, final ProtocolSocketFactory factory,
			final int defaultPort) {

		if (scheme == null) {
			throw new IllegalArgumentException("scheme is null");
		}
		if (factory == null) {
			throw new IllegalArgumentException("socketFactory is null");
		}
		if (defaultPort <= 0) {
			throw new IllegalArgumentException("port is invalid: " + defaultPort);
		}

		this.scheme = scheme;
		this.socketFactory = factory;
		this.defaultPort = defaultPort;
		this.secure = false;
	}

	/**
	 * Constructs a new Protocol. The created protcol is secure.
	 * 
	 * @param scheme
	 *          the scheme (e.g. http, https)
	 * @param factory
	 *          the factory for creating sockets for communication using this
	 *          protocol
	 * @param defaultPort
	 *          the port this protocol defaults to
	 */
	public Protocol(final String scheme,
			final SecureProtocolSocketFactory factory, final int defaultPort) {

		if (scheme == null) {
			throw new IllegalArgumentException("scheme is null");
		}
		if (factory == null) {
			throw new IllegalArgumentException("socketFactory is null");
		}
		if (defaultPort <= 0) {
			throw new IllegalArgumentException("port is invalid: " + defaultPort);
		}

		this.scheme = scheme;
		this.socketFactory = factory;
		this.defaultPort = defaultPort;
		this.secure = true;
	}

	/**
	 * Return true if the specified object equals this object.
	 * 
	 * @param obj
	 *          The object to compare against.
	 * @return true if the objects are equal.
	 */
	@Override
	public boolean equals(final Object obj) {

		if (obj instanceof Protocol) {

			final Protocol p = (Protocol) obj;

			return ((this.defaultPort == p.getDefaultPort())
					&& this.scheme.equalsIgnoreCase(p.getScheme())
					&& (this.secure == p.isSecure()) && this.socketFactory.equals(p
					.getSocketFactory()));

		} else {
			return false;
		}

	}

	/**
	 * Returns the defaultPort.
	 * 
	 * @return int
	 */
	public int getDefaultPort() {
		return this.defaultPort;
	}

	/**
	 * Returns the scheme.
	 * 
	 * @return The scheme
	 */
	public String getScheme() {
		return this.scheme;
	}

	/**
	 * Returns the socketFactory. If secure the factory is a
	 * SecureProtocolSocketFactory.
	 * 
	 * @return SocketFactory
	 */
	public ProtocolSocketFactory getSocketFactory() {
		return this.socketFactory;
	}

	/**
	 * Return a hash code for this object
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return this.scheme.hashCode();
	}

	/**
	 * Returns true if this protocol is secure
	 * 
	 * @return true if this protocol is secure
	 */
	public boolean isSecure() {
		return this.secure;
	}

	/**
	 * Resolves the correct port for this protocol. Returns the given port if
	 * valid or the default port otherwise.
	 * 
	 * @param port
	 *          the port to be resolved
	 * 
	 * @return the given port or the defaultPort
	 */
	public int resolvePort(final int port) {
		return port <= 0 ? this.getDefaultPort() : port;
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return a string representation of this object.
	 */
	@Override
	public String toString() {
		return this.scheme + ":" + this.defaultPort;
	}
}
