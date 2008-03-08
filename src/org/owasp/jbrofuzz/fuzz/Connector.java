/**
 * RequestIterator.java 0.6
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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.version.Format;

/**
 * <p>
 * The request generator instantiates the correct generator, holding the
 * complete set of requests. This class runs through all the requests that a
 * generator has to make holding the complete request being made.
 * </p>
 * 
 * <p>
 * For each generator value, through the use of the run method, a socket gets
 * created according through a Connection object that gets created.
 * </p>
 * 
 * <p>
 * Effectively, a request generator generates connections when run by means of
 * establishing sockets though the Connection class.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class Connector {

	// Format the current time in a nice ISO 8601 format.
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");

	private String host, port, message, payload; 
	
	private int start, finish;

	public Connector(String host, String port, String message, String payload,
			int start, int finish) {

		this.host = host;
		this.port = port;
		this.message = message;
		this.payload = payload;
		this.start = start;
		this.finish = finish;
		
		int selectedTextLength = 0;
		
		if( (this.start >= 0) && (this.finish > 0) ) {
			
		}
		
		// Connection con = new Connection(host, port, message);
		
		

	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the fuzzer
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * @param fuzzer the fuzzer to set
	 */
	public void setPayload(String fuzzer) {
		this.payload = fuzzer;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the finish
	 */
	public int getFinish() {
		return finish;
	}

	/**
	 * @param finish the finish to set
	 */
	public void setFinish(int finish) {
		this.finish = finish;
	}

	/**
	 * @return the dateFormat
	 */
	public static SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	
	/*
	public Connector(String host, String port, String message, String fuzzer, int start, int finish) {


		// Check start and finish to positive and also within length
		final int strlength = request.length();
		if ((start < 0) || (finish < 0) || (start > strlength)
				|| (finish > strlength)) {
			len = 0;
		} else {
			len = finish - start;
		}
		// Check for a minimum length of 1 between start and finish
		if (len > 0) {

		
		} // if this.len > 0

	


		final String target = mJBroFuzz.getWindow().getFuzzingPanel()
				.getTargetText();
		final String port = mJBroFuzz.getWindow().getFuzzingPanel()
				.getPortText();
		StringBuffer stout = new StringBuffer(""); // getNext();

		if (stout.toString().equalsIgnoreCase("")) {
			stout = request;
		}

		while ((!stout.toString().equalsIgnoreCase("")) && (!stopped)) {

			final Date currentTime = new Date();
			final String filename = mJBroFuzz.getWindow().getFuzzingPanel()
					.getCounter(true);

			mJBroFuzz.getWindow().getFuzzingPanel().addRowInOuputTable(
					filename + "          " + target + ":" + port + "          "
							+ id + "          "
							+ Connector.dateFormat.format(currentTime) + "          "
							+ currentValue + "/" + maxValue);

			FileHandler.writeFuzzFile("[{" + currentValue + "/" + maxValue
					+ "}, " + filename + " "
					+ Connector.dateFormat.format(currentTime) + "] " + "<!-- \r\n"
					+ target + " : " + port + "\r\n" + stout + "\r\n", filename);

			final Connection con = new Connection(target, port, stout);

			final String s = con.getReply();
			FileHandler
					.writeFuzzFile(JBRFormat.LINE_SEPARATOR + "\r\n" + s, filename);

		}

	}
	*/
}
