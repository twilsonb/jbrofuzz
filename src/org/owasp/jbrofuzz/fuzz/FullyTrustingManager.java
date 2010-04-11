/**
 * JBroFuzz 2.1
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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
/**
 * <p>
 * An extension to the X509TrustManager not throwing any exceptions.
 * </p>
 * 
 * <p>
 * This class allows for self-signed certificates to be accepted when a
 * connection to such a host is made.
 * </p>
 * 
 * 
 * @see javax.net.ssl.X509TrustManager
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class FullyTrustingManager implements X509TrustManager {

	/**
	 * @see javax.net.ssl.X509TrustManager#
	 *      checkClientTrusted(java.security.cert.X509Certificate[], String)
	 */
	public void checkClientTrusted(final X509Certificate[] cert, final String authType)
	throws CertificateException {
	}

	/**
	 * @see javax.net.ssl.X509TrustManager#
	 *      checkServerTrusted(java.security.cert.X509Certificate[], String)
	 */
	public void checkServerTrusted(final X509Certificate[] cert, final String authType)
	throws CertificateException {
	}

	/**
	 * @see javax.net.ssl.X509TrustManager# getAcceptedIssuers()
	 */
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] {};
	}

}