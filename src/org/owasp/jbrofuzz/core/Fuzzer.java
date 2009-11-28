/**
 * JBroFuzz 1.7
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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
package org.owasp.jbrofuzz.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A fuzzer is an iterator that is constructed based on a prototype, carrying
 * the payloads and the fuzzer type information.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 1.8
 * @since 1.2
 */
public class Fuzzer implements Iterator<String> {

	private int len;

	private Prototype prototype;

	private ArrayList<String> payloads;

	private long cValue, maxValue;

	/**
	 * <p>This constructor is available through the factory method, createFuzzer(), 
	 * available in the Database class.</p>
	 * 
	 * <p>The length specifies the number of digits, in terms of characters that
	 * the Fuzzer will be used for. This is required for recursive and zero fuzzers,
	 * where an iteration is taking place.</p>
	 * 
	 * @see Database.createFuzzer(String id, int length)
	 * 
	 * @param prototype
	 * @param len
	 * @throws NoSuchFuzzerException
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.2
	 */
	protected Fuzzer(Prototype prototype, int len) throws NoSuchFuzzerException {

		this.prototype = prototype;

		if (prototype != null) {

			payloads = this.prototype.getPayloads();

			if (this.prototype.isRecursive()) {
				
				maxValue = Fuzzer.pow(payloads.size(), len);

			} else {
				maxValue = payloads.size();

			}

		} else {
			maxValue = 0L;
		}

		cValue = 0L;
		this.len = len;

	}

	/**
	 * <p>Get the current numeric value as a string that the fuzzer
	 * is iterating through.</p>
	 * 
	 * <p>Say that you have the hexadecimal fuzzer 'HEX-...' of 
	 * length 5. This implies that there will be 16^5 = 1048576 of
	 * values to iterate through.</p>
	 * 
	 * <p>This method gives you the ability to know to return on the 
	 * current iteration, the numeric value we are currently on.</p>
	 * 
	 * @return as String, the numeric value, e.g. '1048576'
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.2
	 */
	public long getCurrectValue() {

		return cValue;

	}

	/**
	 * <p>Returns the Fuzzer unique ID, in the format of, say, '024-XSS-101'.</p>
	 * 
	 * <p>This is also the unique ID used by the Prototype and the Database.</p>
	 * 
	 * @return the unique ID as String
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	public String getId() {

		return prototype.getId();

	}

	/**
	 * <p>Return the maximum value of the iteration as a String.</p>
	 * 
	 * <p>For Zero Fuzzers and Replacive Fuzzers, this value will be
	 * the number of payloads that the fuzzer has, i.e. the length of 
	 * the alphabet that the fuzzer carries.</p>
	 * 
	 * @return as String, the numeric value, e.g. '1048576'
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.2
	 */
	public long getMaximumValue() {

		return maxValue;

	}

	/**
	 * <p>Returns the Fuzzer name, as a String, say, 'Hexadecimal Fuzzer'.</p>
	 * 
	 * @return the fuzzer name as String
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	public String getName() {

		return prototype.getName();

	}

	public boolean hasNext() {

		if (cValue < maxValue) {
			return true;
		} else {
			return false;
		}

	}

	public String next() {

		// Replacive Prototype
		if (maxValue == payloads.size()) {

			cValue++;
			return payloads.get((int) cValue - 1);

		}
		// Recursive Prototype
		else {

			StringBuffer output = new StringBuffer("");

			long val = cValue;
			// Perform division on a stack
			Stack<Integer> stack = new Stack<Integer>();
			
			while (val >= payloads.size()) {

				stack.push( new Integer((int) val % payloads.size()) );
				
				val = val / payloads.size();

			}
			// Append the relevant empty positions with the first element
			// identified
			output.append(StringUtils.leftPad(payloads.get((int)val),
					len - stack.size(), payloads.get(0)));
			while (!stack.isEmpty())
				output.append(payloads.get((stack.pop()).intValue()));

			cValue++;
			return output.toString();

		}


	}

	public void remove() {
		cValue--;
	}

	/**
	 * <p>Static method calculating the result of: x^y</p>
	 * <p>If x^y > Long.MAX_VALUE return 0.</p>
	 * 
	 * @param x The base
	 * @param y The power
	 * @return x^y as a long value
	 */
	private static long pow(int x, int y) {

		long b=((long)y)&0x00000000ffffffffL;
		long result = 1L;
		long powerN=x;
		
		while(b!=0){
			if((b&1)!=0) result*=powerN;
			b>>>=1;
			powerN=powerN*powerN;
		}

		return result;
	}

}
