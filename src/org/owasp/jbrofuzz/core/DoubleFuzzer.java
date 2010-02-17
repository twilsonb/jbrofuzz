/**
 * JBroFuzz 1.9
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

/**
 * <p>A double fuzzer is also a fuzzer iterator. It is a power fuzzer of
 * power length 2.</p>
 * 
 * <p>The extension of a double fuzzer is a fuzzer that returns two
 * payloads
 * 
 * <p>Similarly to the Fuzzer.next() method of iteration, the nextDouble()
 * will return a 2 element array of identical payload values.</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class DoubleFuzzer extends Fuzzer {

	private static final int POWER = 2;
	
	/**
	 * <p>This constructor is available through the factory method, 
	 * createDoubleFuzzer(), available in the Database class.</p>
	 * 
	 * <p>The length specifies the number of digits, in terms of characters that
	 * the Fuzzer will be used for. This is required for recursive and zero fuzzers,
	 * where an iteration is taking place.</p>
	 * 
	 * <p>The power specifies the number of elements that will be returned 
	 * through the nextPower() method, as a String array.</p>
	 * 
	 * @see Database.createDoubleFuzzer(String id, int length)
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
	 * @version 2.0
	 * @since 2.0
	 */
	public DoubleFuzzer(Prototype prototype, int len)
			throws NoSuchFuzzerException {

		super(prototype, len);
		
	}
	
	/**
	 * <p>Return an array of identical elements of length the value
	 * of the power integer, set during construction, or by the use
	 * of the setPower(int) method of this class.</p>
	 * 
	 * <p>This method should be used to access the array of fuzzing 
	 * payloads, after construction of the power fuzzer object.</p>
	 * 
	 * @return String[]	The next fuzzer payload array, during the 
	 * 					iteration process
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public String[] nextDouble() {
		
		final String value = super.next();
		return new String[] {value, value};
		
	}
	
	/**
	 * <p>Method returns the number of 2 of elements within
	 * the double fuzzer.</p>
	 * 
	 * @return int	The number of elements i.e. 2
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public int getPower() {
		
		return POWER;
		
	}

}
