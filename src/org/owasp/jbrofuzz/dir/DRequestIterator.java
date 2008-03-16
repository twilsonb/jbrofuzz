/**
 * DRequestIterator.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon org
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
 */
package org.owasp.jbrofuzz.dir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.URIUtil;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.Format;

/**
 * <p>
 * Class for generating the recursive directory requests.
 * </p>
 * <p>
 * Once the object is instantiated, the run method should be used to run through
 * the requests.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class DRequestIterator {

	// The frame window that the request iterator
	private JBroFuzzWindow m;
	// The original url string
	private String url;
	// The vector of directories to be passed
	private String[] directories;
	// The vector of responses that will come back
	private String[] responses;
	// The boolean to see if the iterator has paused
	private boolean stopped;
	// The integer of the current request count
	private int i;
	// The port on which directory enumeration is taking place
	private int port;

	/**
	 * <p>
	 * Constructor for creating a web directory request iterator that iterates
	 * through the directory listing.
	 * </p>
	 * 
	 * @param m
	 *          FrameWindow
	 * @param url
	 *          String
	 * @param directories
	 *          String
	 * @param port
	 *          int
	 */
	public DRequestIterator(final JBroFuzzWindow m, final String url,
			final String directories, final String port) {
		this.m = m;
		this.url = url;
		this.port = 0;
		this.directories = directories.split("\n");
		responses = new String[directories.length()];
		stopped = false;
		i = 0;

		// Check the port
		try {
			this.port = Integer.parseInt(port);
		} catch (final NumberFormatException e1) {
			this.port = 0;
			m.log("Web Directories Panel: Specify a valid port: \"" + port + "\"");
		}
		if ((this.port < 1) || (this.port > 65535)) {
			this.port = 0;
			m.log("Web Directories Panel: Port has to be between [1 - 65535]");
		}
		// Establish the protocols, if the port is valid
		if (this.port != 0) {

			// For https, allow self-signed certificates
			if (this.url.startsWith("https://")) {
				final Protocol easyhttps = new Protocol("https",
						new EasySSLProtocolSocketFactory(), this.port);
				Protocol.registerProtocol("https", easyhttps);
			}
			// For http, just show affection
			if (this.url.startsWith("http://")) {
				final Protocol easyhttp = new Protocol("http",
						new DefaultProtocolSocketFactory(), this.port);
				Protocol.registerProtocol("http", easyhttp);
			}
		}
	}

	/**
	 * Check to see if the current Request Iterator is stopped.
	 * 
	 * @return boolean
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * Method used for running the request iterator once it has been initialised.
	 */
	public void run() {
		// Check for a valid URL
		if (url.equalsIgnoreCase("")) {
			return;
		}
		if (url.contains(" ")) {
			return;
		}
		if ((port < 1) || (port > 65536)) {
			return;
		}

		for (i = 0; i < directories.length; i++) {
			if (stopped) {
				return;
			}

			String currentURI = "";
			try {
				currentURI = url + URIUtil.encodePath(directories[i]);
			} catch (final URIException ex) {
				currentURI = "";
				m.log("Could not encode the URI: " + url
						+ directories[i]);
			}

			// Checks...
			if (currentURI.equalsIgnoreCase("")) {
				return;
			}
			if ((port <= 0) || (port > 65535)) {
				return;
			}

			final HttpClient client = new HttpClient();
			client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
					Integer.valueOf(10000));

			final GetMethod method = new GetMethod(currentURI);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			method.setFollowRedirects(true);
			method.setDoAuthentication(true);

			try {
				responses[i] = i + "\n";
				responses[i] += currentURI + "\n";
				int statusCode = 0;
				//
				statusCode = client.executeMethod(method);
				//
				responses[i] += statusCode + "\n";
				responses[i] += method.getStatusText() + "\n";

				if (statusCode == HttpStatus.SC_OK) {
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					final InputStream in = method.getResponseBodyAsStream();

					final byte[] buff = new byte[65535];
					int got;
					while ((got = in.read(buff)) > -1) {
						baos.write(buff, 0, got);
					}
					final byte[] allbytes = baos.toByteArray();

					String results = null;
					try {
						results = new String(allbytes, method.getResponseCharSet());
					} catch (final UnsupportedEncodingException ex1) {
						m.log("Web Directories: Unsupported Character Encoding");
						results = "";
					}

					// Check for comments
					if (results.contains("<!--")) {
						responses[i] += "Yes\n";
					} else {
						responses[i] += "No\n";
					}
					// Check for scripts
					if ((results.contains("<script")) || (results.contains("<SCRIPT"))) {
						responses[i] += "Yes\n";
					} else {
						responses[i] += "No\n";
					}
				}
				// If no ok response has come back just append comments and scripts
				else {
					responses[i] += "No\n";
					responses[i] += "No\n";
				}
			} catch (final HttpException e) {
				responses[i] = i + "\n";
				responses[i] += currentURI + "\n";
				responses[i] += "000" + "\n";
				responses[i] += "Fatal protocol violation" + "\n";
				responses[i] += " \n";
				responses[i] += " \n";
				// Bomb out...
				final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
				boolean continueOnError = prefs.getBoolean(Format.PREF_FUZZ_DIR_ERR,
						false);
				if (!continueOnError) {
					stop();
				}
			} catch (final IOException e) {
				responses[i] = i + "\n";
				responses[i] += currentURI + "\n";
				responses[i] += "000" + "\n";
				responses[i] += "Fatal transport error" + "\n";
				responses[i] += " \n";
				responses[i] += " \n";
				// Bomb out...
				final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
				boolean continueOnError = prefs.getBoolean(Format.PREF_FUZZ_DIR_ERR,
						false);
				if (!continueOnError) {
					stop();
				}
			} finally {
				method.releaseConnection();
			}
			// Add a row to the displaying table
			m.getPanelWebDirectories().addRow(responses[i]);
			// Create a String to be written to file
			final StringBuffer outToFile = new StringBuffer();
			
			final Date currentTime = new Date();
			final SimpleDateFormat dateTime = new SimpleDateFormat(
					"dd.MM.yyyy HH:mm:ss", new Locale("en"));
			outToFile.append(dateTime.format(currentTime));
			
			// String outToFile = Time.dateAndTime();
			final String[] tempArray = responses[i].split("\n");
			for (final String element : tempArray) {
				outToFile.append("," + element);
			}
			// Write the file
			FileHandler.writeWebDirFile(m.getPanelWebDirectories()
					.getSessionNumber(), outToFile.toString());
			// Update the progress bar
			final double percentage = 100 * ((double) (i + 1))
					/ (directories.length);
			m.getPanelWebDirectories().setProgressBar((int) percentage);
		}
	}

	/**
	 * Stop the Request Iterator, if it currently running.
	 */
	public void stop() {
		stopped = true;
	}
}
