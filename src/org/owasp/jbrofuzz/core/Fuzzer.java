/**
 * JBroFuzz 1.2
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

public class Fuzzer implements Iterator<String> {

	private int len;

	private Database database;

	private Prototype prototype;

	private ArrayList<String> payloads;

	private BigInteger cValue, maxValue;

	public Fuzzer(String generator, int len) throws NoSuchFuzzerException {

		database = new Database();
		if (!database.containsGenerator(generator)) {

			throw new NoSuchFuzzerException(StringUtils.abbreviate(generator,
					10)
					+ " : No Such Fuzzer Found in the Database ");

		}
		this.prototype = database.getGenerator(generator);

		if (generator != null) {

			// System.out.println("Generator Found!");

			payloads = this.prototype.getPayloads();
			if (this.prototype.isReplacive()) {
				maxValue = new BigInteger("" + payloads.size());
			} else {
				maxValue = new BigInteger("" + payloads.size());
				maxValue = maxValue.pow(len < 0 ? 0 : len);
			}

		} else {
			maxValue = new BigInteger("0");
		}

		cValue = new BigInteger("0");
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

		if (cValue.compareTo(maxValue) < 0) {
			return true;
		} else {
			return false;
		}

	}

	public String next() {

		StringBuffer output = new StringBuffer("");

		// Replacive Generator
		if (maxValue.compareTo(new BigInteger("" + payloads.size())) == 0) {

			output.append(payloads.get(cValue.intValue()));
			cValue = cValue.add(new BigInteger("1"));

		}
		// Recursive Generator
		else {

			BigInteger val = cValue;
			// Perform division on a stack
			Stack<BigInteger> stack = new Stack<BigInteger>();
			while (val.compareTo(new BigInteger("" + payloads.size())) >= 0) {

				stack.push(new BigInteger(""
						+ (val.mod(new BigInteger("" + payloads.size())))));
				val = val.divide(new BigInteger("" + payloads.size()));

			}
			// Append the relevant empty positions with the first element
			// identified
			output.append(StringUtils.leftPad(payloads.get((val.intValue())),
					len - stack.size(), payloads.get(0)));
			while (!stack.isEmpty())
				output.append(payloads.get((stack.pop()).intValue()));

			cValue = cValue.add(new BigInteger("1"));

		}

		return output.toString();

	}

	public void remove() {

		cValue = cValue.add(new BigInteger("1"));

	}

}
