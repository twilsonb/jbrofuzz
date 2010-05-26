/**
 * JBroFuzz 2.2
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
 * <p>A power fuzzer is also a fuzzer iterator. It is constructed based on 
 * a prototype, which carries the payloads and the fuzzer information type.</p>
 * 
 * <p>The extension of a power fuzzer is the ability to change the number of 
 * elements that get returned through the use of the nextPower() method.</p>
 * 
 * <p>Similarly to the Fuzzer.next() method of iteration, the nextPower()
 * will return a array of length the power of the fuzzer, set under 
 * construction, of howerever many elements are required.</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class PowerFuzzer extends Fuzzer {

	// The power integer determines the size of the array
	private int power;

	/**
	 * <p>This constructor is available through the factory method, 
	 * createPowerFuzzer(), available in the Database class.</p>
	 * 
	 * <p>The length specifies the number of digits, in terms of characters that
	 * the Fuzzer will be used for. This is required for recursive and zero fuzzers,
	 * where an iteration is taking place.</p>
	 * 
	 * <p>The power specifies the number of elements that will be returned 
	 * through the nextPower() method, as a String array.</p>
	 * 
	 * @see Database.createPowerFuzzer(String id, int length, int power)
	 * 
	 * @param prototype The prototype id, as read from the fuzzers.jbrf file
	 * 					e.g. "031-B16-HEX" for the hexadecimal alphabet 	 
	 * 
	 * @param len		The length of the fuzzer, required for recursive and zero
	 * 					fuzzers. This should always be a positive integer.
	 * 		
	 * @param power		The power of the fuzzer, i.e. how many identical elements
	 * 					it returns through the use of the nextPower() method.
	 * 
	 * @throws NoSuchFuzzerException
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	protected PowerFuzzer(Prototype prototype, int len, int power)
			throws NoSuchFuzzerException {
		
		super(prototype, len);
		this.power = power;
		
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
	public String[] nextPower() {
		
		final String value = super.next();
		final String [] output = new String[power];
		
		for(int i = 0; i < power; i++) {
			output[i] = value;
		}
		
		return output;
		
	}
	
	/**
	 * <p>Method returns the number of power in the fuzzer, i.e. 
	 * the number of elements in the array returned by the 
	 * nextPower() method.</p>
	 * 
	 * @return int	The number of elements i.e. the power of the 
	 * 				fuzzer.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public int getPower() {
		
		return power;
		
	}
	
	/**
	 * <p>Set the number of identical elements (i.e. the power of the 
	 * fuzzer) that are to be returned by the nextPower() method.</p>
	 * 
	 * <p>Note that this value does impact in any way, the return value
	 * of the next() method inhereted from the Fuzzer class.</p>
	 * 
	 * @param power	The number of elements to be returned in the
	 * 				array of nextPower(). The value has to be
	 * 				greater than zero.
	 */
	public void setPower(final int power) {
		if(power > 0) {
			this.power = power;
		}
	}

}
