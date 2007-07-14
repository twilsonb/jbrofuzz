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
import org.owasp.jbrofuzz.fuzz.tcp.Connection;
import org.owasp.jbrofuzz.fuzz.tcp.Generator;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.version.JBRFormat;
/**
 * <p>The request generator instantiates the correct generator,
 * holding the complete set of requests. This class runs through all the
 * requests that a generator has to make holding the complete request being
 * made.</p>
 *
 * <p>For each generator value, through the use of the run method, a socket
 * gets created according through a Connection object that gets created.</p>
 *
 * <p>Effectively, a request generator generates connections when run by means
 * of establishing sockets though the Connection class.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class RequestIterator {

	private JBroFuzz mJBroFuzz;
	// The request being made on the socket
	private StringBuffer request;
	// The location within the request where fuzzing takes place
	private int start, finish, len;
	// The constructor holding all the fuzzer lists
	private TConstructor mTConstructor;
	/*
	 * As the generator generates fuzzing request, two variables holding the
	 * currentValue of the generator and the maximum value of the generator.
	 */
	private long currentValue, maxValue;
	// The type of generator
	private String type;
	// The boolean checking if stop has been pressed
	private boolean generatorStopped;
//Format the current time in a nice iso 8601 format.
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
    "yyyy-MM-dd HH-mm-ss-SSS");
	/**
	 * The main constructor passing a jbrofuzz object, the string to be sent,
	 * the start location wihtin that String were the fuzzing point begins from
	 * and the finish location and the type of fuzzing.
	 * @param mJBroFuzz JBroFuzz
	 * @param request String
	 * @param start int
	 * @param finish int
	 * @param type int
	 */
	public RequestIterator(final JBroFuzz mJBroFuzz, 
			final StringBuffer request, 
			final int start,
			final int finish, 
			final String type) {

		this.mJBroFuzz = mJBroFuzz;
		this.request = request;
		this.type = type;
		this.start = start;
		this.finish = finish;
		
		this.maxValue = 0;
		this.currentValue = 0;
		this.generatorStopped = false;
		
		this.mTConstructor = new TConstructor(this.getJBroFuzz());
		
		// Check start and finish to positive and also within length
		final int strlength = request.length();
		if ((start < 0) || (finish < 0) || (start > strlength) ||
				(finish > strlength)) {
			this.len = 0;
		}
		else {
			this.len = finish - start;
		}
		// Check for a minimum length of 1 between start and finish
		if (this.len > 0) {
			
			this.maxValue = this.mTConstructor.getGeneratorLength(type);
			// For a recursive generator, generate the corresponding maximum value
			final char genType = this.mTConstructor.getGeneratorType(type);
			if (genType == Generator.RECURSIVE) {
				final long baseValue = this.maxValue;
				for (int i = 1; i < this.len; i++) {
					this.maxValue *= baseValue;
				}
			}
		} // if this.len > 0
		
	}

	/**
	 * Get next string value. If no such value exists, return an empty
	 * StringBuffer.
	 * 
	 * @return StringBuffer
	 */
	private StringBuffer getNext() {
		final StringBuffer fuzzedValue = new StringBuffer("");
		if (this.currentValue < this.maxValue) {

			fuzzedValue.append(this.request.substring(0, this.start));
			int blank_count = 0;
			final char genType = this.mTConstructor.getGeneratorType(this.type);

			// Check to see if the generator is recursive
			if (genType == Generator.RECURSIVE) {
				final int radix = this.mTConstructor.getGeneratorLength(this.type);
				blank_count = Long.toString(this.currentValue, radix).length() - this.len;
				while (blank_count < 0) {
					fuzzedValue.append("0");
					blank_count++;
				}
				// Append the current value, depending on type
				if (this.type.equals("DEC")) {
					fuzzedValue.append("" + Long.toString(this.currentValue, 10));
				}
				if (this.type.equals("HEX")) {
					fuzzedValue.append("" + Long.toHexString(this.currentValue));
				}
				if (this.type.equals("OCT")) {
					fuzzedValue.append("" + Long.toOctalString(this.currentValue));
				}
				if (this.type.equals("BIN")) {
					fuzzedValue.append("" + Long.toBinaryString(this.currentValue));
				}
			}
			// Check to see if the generator is replasive
			if (genType == Generator.REPLASIVE) {
				final int v = (int) this.currentValue;
				final StringBuffer b = this.mTConstructor.getGeneratorElement(this.type, v);
				fuzzedValue.append(b);
			}

			this.currentValue++;
			// Append the end of the string
			fuzzedValue.append(this.request.substring(this.finish));
		}
		return fuzzedValue;		
	}

	/**
	 * <p>Method responsible for doing all the work. This method runs through a
	 * number of instances creating a connection and getting the reply for each
	 * one.</p>
	 * <p>While looping through the total number of connections, it checks to see
	 * also if the fuzzing has been stopped by the user. This is checked through
	 * the generatorStopped boolean that can be accesses from the main GUI.</p>
	 *
	 */
	public void run() {
		final String target = this.mJBroFuzz.getWindow().getFuzzingPanel().getTargetText();
		final String port = this.mJBroFuzz.getWindow().getFuzzingPanel().getPortText();
		StringBuffer stout = this.getNext();
		
		if(stout.toString().equalsIgnoreCase("")) {
			stout = this.request;
		}

		while ( (! stout.toString().equalsIgnoreCase("")) && (! this.generatorStopped) ) {

			final Date currentTime = new Date();
			final String filename = this.mJBroFuzz.getWindow().getFuzzingPanel().getCounter(true);

			this.mJBroFuzz.getWindow().getFuzzingPanel().addRowInOuputTable(
					filename + "          " + 
					target + ":" + port + "          " + 
					this.type + "          " + 
					RequestIterator.dateFormat.format(currentTime) + "          " + 
					this.currentValue + "/" + this.maxValue);
			
			FileHandler.writeFuzzFile("[{" + this.currentValue + "/" + this.maxValue + "}, " 
															+ filename + " " + RequestIterator.dateFormat.format(currentTime) + "] "
															+ "<!-- \r\n" + target + " : " + port + "\r\n"
															+ stout + "\r\n",  
															filename) ;

			final Connection con = new Connection(target, port, stout);

			final String s = con.getReply();
			FileHandler.writeFuzzFile(JBRFormat.LINE_SEPARATOR + "\r\n" + s, filename);
			
			stout = this.getNext();
		}
}

/**
 * <p>Stop the generator. This operation is achieved by setting the boolean
 * being checked in the loop of getNext to true. As a result, the current
 * fuzzing request completes and no more requests are executed.</p>
 */
public void stop() {
	this.generatorStopped = true;
}

/**
 * Return the main constructor object.
 * @return JBroFuzz
 */
public JBroFuzz getJBroFuzz() {
	return this.mJBroFuzz;
}
}
