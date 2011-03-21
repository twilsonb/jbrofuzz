/**
 * JbroFuzz 2.5
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

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * FuzzerBigInteger implements a fuzzer iterator using BigInteger; as a 
 * result, iterations are not limited to LONG.MAX_VALUE < 16^16. 
 * </p>
 * <p>
 * As this class uses BigInteger, it is much heaver on the memory constraints
 * then the Fuzzer class.
 * <p>
 * A fuzzer is an iterator that is constructed based on a prototype, carrying
 * the payloads and the fuzzer type information.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 1.9
 * @since 1.9
 */
public class FuzzerBigInteger implements Iterator<String> {

	private final transient int len;

	private final transient Prototype prototype;

	private transient List<String> payloads;

	private transient BigInteger cValue, maxValue;

	/**
	 * <p>This constructor is available through the factory method, 
	 * createFuzzerBigInteger(), available in the Database class.</p>
	 * 
	 * <p>The length specifies the number of digits, in terms of characters that
	 * the Fuzzer will be used for. This is required for recursive and zero fuzzers,
	 * where an iteration is taking place.</p>
	 * 
	 * @see Database.createFuzzerBigInteger(String id, int length)
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
	 * @version 2.4
	 * @since 1.2
	 */
	protected FuzzerBigInteger(final Prototype prototype, final int len) throws NoSuchFuzzerException {

		this.prototype = prototype;

		if (prototype == null) {
			
			maxValue = BigInteger.ZERO;
			
		} else {
			
			payloads = this.prototype.getPayloads();

			if (this.prototype.isRecursive()) {

				maxValue = BigInteger.valueOf(payloads.size());
				maxValue = maxValue.pow(len < 0 ? 0 : len);

			} else {
				maxValue = BigInteger.valueOf(payloads.size());

			}
		}

		cValue = BigInteger.ZERO;
		this.len = len;

	}

	/**
	 * <p>Get the current String value that the fuzzer is on.</p>
	 * 
	 * <p>Say that you have the hexadecimal fuzzer with ID 
	 * '031-B16-HEX' of length 10. This implies that there 
	 * will be 16^10 = 2^40 of values to iterate through.</p>
	 * 
	 * <p>This method gives you the ability to know to return on the 
	 * current iteration, the numeric value we are currently on.</p>
	 * 
	 * @return as a String, the numeric value, e.g. "1099511627776"
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
	 * @since 2.4
	 */
	public String getCurrectValue() {

		return cValue.toString();

	}

	/**
	 * <p>Returns the Fuzzer unique ID, in the format of, say, "030-XSS-BRK".</p>
	 * 
	 * <p>This is also the unique ID used by the Prototype and the Database.</p>
	 * 
	 * @return the unique ID as String
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
	 * @since 2.4
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
	 * @version 2.4
	 * @since 2.4
	 */
	public String getMaximumValue() {

		return maxValue.toString();

	}

	/**
	 * <p>Returns the Fuzzer name, as a String, say, 'Hexadecimal Fuzzer'.</p>
	 * 
	 * @return the fuzzer name as String
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
	 * @since 2.4
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
	 * @version 2.4
	 * @since 1.2
	 */
	public boolean hasNext() {

		return cValue.compareTo(maxValue) < 0;

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
	 * @version 2.4
	 * @since 1.2
	 */
	public String next() {

		final StringBuffer output = new StringBuffer("");

		// Replacive Prototype
		if (maxValue.compareTo(BigInteger.valueOf(payloads.size())) == 0) {

			output.append(payloads.get(cValue.intValue()));
			cValue = cValue.add(BigInteger.ONE);

		}
		// Recursive Prototype
		else {

			BigInteger val = cValue;
			// Perform division on a stack
			final Stack<BigInteger> stack = new Stack<BigInteger>();
			while (val.compareTo(BigInteger.valueOf(payloads.size())) >= 0) {

				stack.push(
						val.mod(
								BigInteger.valueOf(payloads.size()) 
						)
				);
				val = val.divide(BigInteger.valueOf(payloads.size()));

			}
			// Append the relevant empty positions with the first element
			// identified
			output.append(StringUtils.leftPad(payloads.get(val.intValue()),
					len - stack.size(), payloads.get(0)));
			while (!stack.isEmpty()) {
				output.append(
						payloads.get(
								stack.pop().intValue()
						)
				);
			}

			cValue = cValue.add(BigInteger.ONE);

		}

		return output.toString();

	}

	/**
	 * <p>This method should not be trusted or used in the conventional
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

		cValue = cValue.subtract(BigInteger.ONE);

	}

}
