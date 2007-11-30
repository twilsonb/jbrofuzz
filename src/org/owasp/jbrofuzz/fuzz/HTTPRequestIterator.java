/**
 * HTTPRequestIterator.java 0.8
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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
package org.owasp.jbrofuzz.fuzz;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;

import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;

import org.owasp.jbrofuzz.JBroFuzz;

import org.owasp.jbrofuzz.ui.*;



/**
 * <p>
 * The request iterator instantiates a fuzzing session using HTTP Commons 
 * Library. This class runs through all the requests that an iterator
 * has to make holding the complete request being made.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.8
 */
public class HTTPRequestIterator {

	// The main object for JBroFuzz
	private JBroFuzz mJBroFuzz;
	// The main url read from the GUI
	private String url;
	// The port read from the GUI
	private int port;
	// The boolean to see if the iterator has paused
	private boolean stopped;
	//The integer of the current request count
	private int i;
	// The string corresponding to the fuzzing category
	private String category;
	/**
	 * <p>
	 * Constructor for the HTTP Request Iterator, responsible for constructing
	 * a main HTTP Fuzzing object, capable of gathering all the required 
	 * information in order for a fuzzing session to begin.
	 * </p>
	 * @param mJBroFuzz
	 */
	public HTTPRequestIterator(final JBroFuzz mJBroFuzz, 
			final String category, 
			final int start, 
			final int end) {

		this.mJBroFuzz = mJBroFuzz;
		final JBRFrame mainWindow = this.mJBroFuzz.getWindow();
		url = mainWindow.getHTTPFuzzingPanel().getTargetText();
		stopped = false;
		port = 0;
		this.category = category;
		//	 Check the port
		final String pt = mainWindow.getHTTPFuzzingPanel().getPortText();
		try {
			port = Integer.parseInt(pt);
		} 
		catch (final NumberFormatException e1) {
			port = 0;
			mainWindow.log("HTTP Fuzzing Panel: Specify a valid port: \"" + port + "\"");
		}
		if ((port < 1) || (port > 65535)) {
			port = 0;
			mainWindow.log("HTTP Fuzzing Panel: Port has to be between [1 - 65535]");
		}

		// Establish the protocols, if the port is valid
		if (port != 0) {

			// For https, allow self-signed certificates
			if (url.startsWith("https://")) {
				final Protocol easyhttps = new Protocol("https",
						new EasySSLProtocolSocketFactory(), port);
				Protocol.registerProtocol("https", easyhttps);
			}

			// For http, just show affection
			if (url.startsWith("http://")) {
				final Protocol easyhttp = new Protocol("http",
						new DefaultProtocolSocketFactory(), port);
				Protocol.registerProtocol("http", easyhttp);
			}

		} // If port != 0

	} // Constructor

	/**
	 * <p>
	 * Method used for running the request iterator once it has been initialised.
	 * </p>
	 * <p>
	 * This is method that actually performs the fuzzing.
	 * </p>
	 */
	public void run() {

		// Check for a valid URL and port
		if (url.equalsIgnoreCase("")) {
			return;
		}
		if (url.contains(" ")) {
			return;
		}
		if ((port < 1) || (port > 65536)) {
			return;
		}

		// Main for loop iterating through the exploits
		final String [] exploitArray = mJBroFuzz.getDatabase().getNames(category);
		for (i = 0; i < exploitArray.length; i++) {

			if (stopped) {
				return;
			}

			/*
			String currentURI = "";
			try {
				currentURI = this.url + URIUtil.encodePath(this.directories[this.i]);
			} catch (final URIException ex) {
				currentURI = "";
				this.m.log("Could not encode the URI: " + this.url
						+ this.directories[this.i]);
			}
			 */

			// Checks...
			if (url.equalsIgnoreCase("")) {
				return;
			}
			if ((port <= 0) || (port > 65535)) {
				return;
			}

			final HttpClient client = new HttpClient();
			client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
					Integer.valueOf(10000));

			final GetMethod method = new GetMethod(url);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			method.setFollowRedirects(true);
			method.setDoAuthentication(true);
			/*
			try {
				this.responses[this.i] = this.i + "\n";
				this.responses[this.i] += currentURI + "\n";
				int statusCode = 0;
				//
				statusCode = client.executeMethod(method);
				//
				this.responses[this.i] += statusCode + "\n";
				this.responses[this.i] += method.getStatusText() + "\n";

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
						this.m.log("Web Directories: Unsupported Character Encoding");
						results = "";
					}

					// Check for comments
					if (results.contains("<!--")) {
						this.responses[this.i] += "Yes\n";
					} else {
						this.responses[this.i] += "No\n";
					}
					// Check for scripts
					if ((results.contains("<script")) || (results.contains("<SCRIPT"))) {
						this.responses[this.i] += "Yes\n";
					} else {
						this.responses[this.i] += "No\n";
					}
				}
				// If no ok response has come back just append comments and scripts
				else {
					this.responses[this.i] += "No\n";
					this.responses[this.i] += "No\n";
				}
			} catch (final HttpException e) {
				this.responses[this.i] = this.i + "\n";
				this.responses[this.i] += currentURI + "\n";
				this.responses[this.i] += "000" + "\n";
				this.responses[this.i] += "Fatal protocol violation" + "\n";
				this.responses[this.i] += " \n";
				this.responses[this.i] += " \n";
				// Bomb out...
				final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
				boolean continueOnError = prefs.getBoolean(JBRFormat.PREF_FUZZ_DIR_ERR,
						false);
				if (!continueOnError) {
					this.stop();
				}
			} catch (final IOException e) {
				this.responses[this.i] = this.i + "\n";
				this.responses[this.i] += currentURI + "\n";
				this.responses[this.i] += "000" + "\n";
				this.responses[this.i] += "Fatal transport error" + "\n";
				this.responses[this.i] += " \n";
				this.responses[this.i] += " \n";
				// Bomb out...
				final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
				boolean continueOnError = prefs.getBoolean(JBRFormat.PREF_FUZZ_DIR_ERR,
						false);
				if (!continueOnError) {
					this.stop();
				}
			} finally {
				method.releaseConnection();
			}
			 */
			// Add a row to the displaying table
			// this.m.getWebDirectoriesPanel().addRow(this.responses[this.i]);
			// Create a String to be written to file
			// final StringBuffer outToFile = new StringBuffer();
			// outToFile.append(JBRTime.dateAndTime());
			// String outToFile = Time.dateAndTime();
			// final String[] tempArray = this.responses[this.i].split("\n");
			// for (final String element : tempArray) {
			// 	outToFile.append("," + element);
			// }
			// Write the file
			// FileHandler.writeWebDirFile(this.m.getWebDirectoriesPanel()
			// 		.getSessionNumber(), outToFile.toString());
			// Update the progress bar
			// final double percentage = 100 * ((double) (this.i + 1))
			// 		/ (this.directories.length);
			// this.m.getWebDirectoriesPanel().setProgressBar((int) percentage);
		}
	}

	/**
	 * Stop the Request Iterator, if it currently running.
	 */
	public void stop() {
		stopped = true;
	}	

	/**
	 * Check to see if the current HTTP Request Iterator is stopped.
	 * 
	 * @return boolean
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * <p>
	 * Return the main JBroFuzz object used by the constructor.
	 * </p>
	 * 
	 * @return JBroFuzz
	 */
	public JBroFuzz getJBroFuzz() {
		return mJBroFuzz;
	}
}
