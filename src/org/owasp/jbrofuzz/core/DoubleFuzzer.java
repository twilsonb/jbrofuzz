/**
 * JBroFuzz 2.3
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

/**
 * <p>A double fuzzer is also a fuzzer iterator. It is a power fuzzer of
 * length 2, that is constructed using two prototypes.</p>
 * 
 * <p>The extension of a double fuzzer is a fuzzer that returns two
 * payloads, one for each prototype used during construction.</p>
 * 
 * <p>Similarly to the Fuzzer.next() method of iteration, the nextDouble()
 * will return a 2 element array of payload values.  The first value is 
 * from the first prototype and the second value is from the second.</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class DoubleFuzzer implements Iterator<String[]> {

	// The two fuzzers that constitute the double fuzzer
	private final Fuzzer fuzzer1, fuzzer2;
	// True if the first fuzzer is bigger in no of payloads
	private final boolean isFuzzer1Bigger;
	
		
	/**
	 * <p>This constructor is available through the factory method, 
	 * createDoubleFuzzer(), available in the Database class.</p>
	 * 
	 * <p>The length specifies the number of digits, in terms of characters that
	 * the Fuzzer will be used for. This is required for recursive and zero fuzzers,
	 * where an iteration is taking place.</p>
	 * 
	 * @see 
	 * 	Database.createDoubleFuzzer(String id1, String id2, int length1, int length2)
	 * 
	 * @param proto1 The prototype id, as read from the fuzzers.jbrf file
	 * 					e.g. "031-B16-HEX" for the hexadecimal alphabet 	 
	 * 
	 * @param len1		The length of the first fuzzer, required for recursive 
	 * 					and zero fuzzers. This should always be a positive integer.
	 * 		
	 * @param proto2 	The prototype id, as read from the fuzzers.jbrf file
	 * 					e.g. "031-B16-HEX" for the hexadecimal alphabet 	 
	 * 
	 * @param len2		The length of the first fuzzer, required for recursive 
	 * 					and zero fuzzers. This should always be a positive integer.
	 * 		
	 * @throws NoSuchFuzzerException
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	protected DoubleFuzzer(Prototype proto1, int len1, 
							Prototype proto2, int len2)
			throws NoSuchFuzzerException {

		fuzzer1 = new Fuzzer(proto1, len1);
		fuzzer2 = new Fuzzer(proto2, len2);
		
		// Set the boolean flag to know which fuzzer has more payloads
		if(fuzzer1.getMaximumValue() > fuzzer2.getMaximumValue()) {
			
			isFuzzer1Bigger = true;
			
		} else {
			
			isFuzzer1Bigger = false;
			
		}
	}
	
	/**
	 * <p>Get the current numeric value as a long that the fuzzer
	 * is iterating through.</p>
	 * 
	 * <p>The current value is the maximum of the two fuzzer
	 * current values.</p>
	 * 
	 * @return as a long, the numeric value, e.g. '1048576'
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public long getCurrentValue() {
		
		final long cur1 = fuzzer1.getCurrentValue();
		final long cur2 = fuzzer2.getCurrentValue();
		
		if (cur1 > cur2) {
			return cur1;
		} else {
			return cur2;
		}
		
	}
	
	/**
	 * <p>Return the id of this double fuzzer, as a combination of the 
	 * two individual ids of the two fuzzers.</p>
	 * 
	 * @return String	DoubleFuzzer( FuzzerID1, FuzzerID2)
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public String getId() {
		
		return "DoubleFuzzer( " + fuzzer1.getId() + ", " + fuzzer2.getId() + " )";
	}
	
	/**
	 * <p>Return the maximum value of the two maximum values, 
	 * that each fuzzer has.</p>
	 * 
	 * @return	long	The maximum value of the two fuzzers.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public long getMaximumValue() {

		if(isFuzzer1Bigger) {
			
			return fuzzer1.getMaximumValue();
			
		} else {
			
			return fuzzer2.getMaximumValue();
			
		}
				
	}
	
	/**
	 * <p>Return the name of this double fuzzer, as a combination of the 
	 * two individual names of the two fuzzers.</p>
	 * 
	 * @return String	DoubleFuzzer( FuzzerName1, FuzzerName2)
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public String getName() {
		
		return "DoubleFuzzer( " + fuzzer1.getName() + ", " + fuzzer2.getName() + " )";
		
	}
	
	/**
	 * <p>Return true if the biggest (in terms of payloads) fuzzer,
	 * still has a next element to iterate through.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public boolean hasNext() {
		
		if(isFuzzer1Bigger) {
			
			return fuzzer1.hasNext();
			
		} else {
			
			return fuzzer2.hasNext();
			
		}
		
	}
	
	/**
	 * <p>Return an array of size 2 with the first element being the
	 * payload for the first prototype specified and the second for 
	 * the second payload specified. 
	 * 
	 * @return String[]	The next two fuzzer payloads
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public String[] next() {
		
		if(isFuzzer1Bigger) {
			
			if(!fuzzer2.hasNext()) {
				fuzzer2.resetCurrentValue();
			}
			
		} else {
			
			if(!fuzzer1.hasNext()) {
				fuzzer1.resetCurrentValue();
			}
		}
				
		final String payload1 = fuzzer1.next();
		final String payload2 = fuzzer2.next();
		
		return new String[] {payload1, payload2};
		
	}
	
	public void remove() {
		
		fuzzer1.remove();
		fuzzer2.remove();
		
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
		
		return 2;
		
	}

}
