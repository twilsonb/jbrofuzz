/**
 * JBroFuzz 2.0
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


	public String getCurrectValue() {

		return cValue.toString();

	}

	public String getId() {

		return prototype.getId();

	}

	public String getMaximumValue() {

		return maxValue.toString();

	}

	public String getName() {

		return prototype.getName();

	}

	public boolean hasNext() {

		return cValue.compareTo(maxValue) < 0;

	}

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

	public void remove() {

		cValue = cValue.add(BigInteger.ONE);

	}

}
