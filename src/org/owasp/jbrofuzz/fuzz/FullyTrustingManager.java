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