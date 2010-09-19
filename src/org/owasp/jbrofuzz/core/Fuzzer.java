/**
 * JBroFuzz 2.4
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
package org.owasp.jbrofuzz.core;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A fuzzer is an iterator that is constructed based on a prototype, carrying
 * the payloads and the fuzzer type information.
 * </p>
 * 
 * <p>This class is limited by the long primitive data type, i.e. we can only
 * create Fuzzers that iterate up to the value of a 64-bit signed two's 
 * complement integer. The maximum number of values we can iterate 
 * through are: <b>9,223,372,036,854,775,807</b> (inclusive).</p>
 * 
 * <p>For larger values, you can use the FuzzerBigInteger class.</p>
 * 
 * @author subere@uncon.org
 * @version 2.4
 * @since 1.2
 */
public class Fuzzer implements Iterator<String> {

	final private transient int len;

	final private transient Prototype prototype;

	private transient List<String> payloads;

	private transient long cValue, maxValue;

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
	 * @param prototype The prototype id, as read from the fuzzers.jbrf file
	 * 					e.g. "031-B16-HEX" for the hexadecimal alphabet 	 
	 * 
	 * @param len		The length of the fuzzer, required for recursive and zero
	 * 					fuzzers. This should always be a positive integer.
	 * 		
	 * @throws NoSuchFuzzerException
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.2
	 */
	protected Fuzzer(final Prototype prototype, final int len) throws NoSuchFuzzerException {

		this.prototype = prototype;

		if (prototype == null) {
			
			maxValue = 0L;

		} else {
			
			payloads = this.prototype.getPayloads();

			if (this.prototype.isRecursive()) {

				maxValue = Fuzzer.pow(payloads.size(), len);

			} else {
				maxValue = payloads.size();

			}
			
		}

		cValue = 0L;
		this.len = len;

	}

	/**
	 * <p>Get the current numeric value as a long that the fuzzer
	 * is iterating through.</p>
	 * 
	 * <p>Say that you have the hexadecimal fuzzer 'HEX-...' of 
	 * length 5. This implies that there will be 16^5 = 1048576 of
	 * values to iterate through.</p>
	 * 
	 * <p>This method gives you the ability to know to return on the 
	 * current iteration, the numeric value we are currently on.</p>
	 * 
	 * @return as a long, the numeric value, e.g. '1048576'
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.2
	 */
	public long getCurrentValue() {

		return cValue;

	}
	
	/**
	 * <p>Resets the current value back to 0, as the value is set
	 * during construction.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public void resetCurrentValue() {
		
		cValue = 0L;
		
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
	 * <p>Return the maximum value of the iteration as a long.</p>
	 * 
	 * <p>For Zero Fuzzers and Replacive Fuzzers, this value will be
	 * the number of payloads that the fuzzer has, i.e. the length of 
	 * the alphabet that the fuzzer carries.</p>
	 * 
	 * @return as long, the numeric value, e.g. '1048576'
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
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

	/**
	 * <p>Check whether or not the fuzzer iterator has a next element.</p>
	 * 
	 * @return true if the fuzzer has more elements to return during its 
	 * 				iteration
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.2
	 */
	public boolean hasNext() {

		return cValue < maxValue;

	}

	/**
	 * <p>Return the next element of the fuzzer during iteration.</p>
	 * 
	 * <p>This method should be used to access fuzzing payloads, after
	 * construction of the fuzzer object.</p>
	 * 
	 * @return String	The next fuzzer payload, during the iteration 
	 * 					process
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.2
	 */
	public String next() {

		final StringBuffer output = new StringBuffer();
		
		// Replacive Prototype
		if (maxValue == payloads.size()) {

			cValue++;
			output.append(payloads.get((int) cValue - 1));

		}
		// Recursive Prototype
		else {

			long val = cValue;
			// Perform division on a stack
			final Stack<Integer> stack = new Stack<Integer>();

			while (val >= payloads.size()) {

				stack.push( Integer.valueOf((int) val % payloads.size()) );

				val = val / payloads.size();

			}
			// Append the relevant empty positions with the first element
			// identified
			output.append(StringUtils.leftPad(payloads.get((int)val),
					len - stack.size(), payloads.get(0)));
			while (!stack.isEmpty()) {
				output.append(payloads.get(stack.pop().intValue()));
			}

			cValue++;

		}

		return output.toString();


	}

	/**
	 * <p>This method should be trusted or used in the conventional
	 * way that an iterator requires remove to be implemented.</p>
	 * 
	 * <p>Instead, during fuzzing, remove() can be called to
	 * step back to the previous element.</p>
	 * 
	 * <p>This need is typical, in replay scenarios where something
	 * worth investigating has been discovered and a quick, step
	 * back step forward is executed.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
	 * @since 2.4
	 */
	public void remove() {
		cValue--;
	}

	/**
	 * <p>Static method calculating the result of: alpha^beta</p>
	 * <p>If alpha^beta > Long.MAX_VALUE return 0.</p>
	 * 
	 * @param alpha The base
	 * @param beta The power
	 * @return alpha^beta as a long value
	 */
	private static long pow(final int alpha, final int beta) {

		long gamma = (beta)&0x00000000ffffffffL;
		long result = 1L;
		long powerN=alpha;

		while(gamma!=0){
			if((gamma&1)!=0) {
				result*=powerN;
			}
			gamma>>>=1;
			powerN=powerN*powerN;
		}

		return result;
	}

}
