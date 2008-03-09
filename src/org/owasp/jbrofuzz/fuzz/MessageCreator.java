/**
 * MessageCreator.java 0.8
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

/**
 * <p>
 * The message creator taking as input the 
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
 * @version 0.8
 */
public class MessageCreator {

	private String message, payload; 

	private int start, finish;

	public MessageCreator(String message, String payload, int start, int finish) {

		if( (message == null) || (payload == null) || (start < 0) || (finish < 0) || (start > message.length()) || (finish > message.length()) ) {
			
			this.message = message == null ? "" : message;
			this.payload = payload == null ? "" : payload;
			
		}
		else {
			
			// Check if positive and less than the message length
			if( (start >= 0) && (start < message.length()) ) {
				this.start = start;
			}
			else {
				this.start = 0;
			}

			// Check if positive and less than|equal the message length
			if( (finish > 0) && (finish <= message.length()) ) {
				this.finish = finish;
			}
			else {
				this.finish = 0;
			}

			this.payload = payload;

			// Split the message and add in-between
			StringBuffer messageBuffer = new StringBuffer();
			messageBuffer.append(message.substring(0, this.start));
			messageBuffer.append(payload);
			messageBuffer.append(message.substring(this.finish));
			
			this.message = messageBuffer.toString();
		}


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
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * @param fuzzer the payload to set
	 */
	public void setPayload(String payload) {
		this.payload = payload;
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


}
