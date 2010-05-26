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

import java.util.Iterator;

/**
 * <p>A cross product fuzzer is also a fuzzer iterator. The payloads it 
 * generates represent the cross product of the two prototypes that 
 * the cross product fuzzer is constructed with.</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class CrossProductFuzzer implements Iterator<String[]> {

	// The two fuzzers that constitute the double fuzzer
	private final Fuzzer fuzzer1, fuzzer2;

	private transient long cValue, maxValue;
	
	private transient String payload1, payload2;
		
	/**
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
	protected CrossProductFuzzer(Prototype proto1, int len1, 
							Prototype proto2, int len2)
									throws NoSuchFuzzerException {

		fuzzer1 = new Fuzzer(proto1, len1);
		fuzzer2 = new Fuzzer(proto2, len2);
		
		cValue = 0L;
		maxValue = fuzzer1.getMaximumValue() 
						* fuzzer2.getMaximumValue();
		
	}
	
	/**
	 * <p>Get the current numeric value as a long.</p>
	 * 
	 * @return as a long, the numeric value, e.g. '1048576'
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public long getCurrentValue() {
		
		return cValue;
		
	}
	
	/**
	 * <p>Return the id of this double fuzzer, as a combination of the 
	 * two individual ids of the two fuzzers.</p>
	 * 
	 * @return String	X-Fuzzer( FuzzerID1, FuzzerID2)
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public String getId() {
		
		return "X-Fuzzer( " + fuzzer1.getId() + ", " + fuzzer2.getId() + " )";
		
	}
	
	/**
	 * <p>Return the maximum value, the product of the fuzzer
	 * getMaximumValue().</p>
	 * 
	 * @return	long	The maximum value of the two fuzzers.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public long getMaximumValue() {

		return maxValue;
		
	}
	
	/**
	 * <p>Return the name of this x fuzzer, as a combination of the 
	 * two individual names of the two fuzzers.</p>
	 * 
	 * @return String	X-Fuzzer( FuzzerName1, FuzzerName2)
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public String getName() {
		
		return "X-Fuzzer( " + fuzzer1.getName() + ", " + fuzzer2.getName() + " )";
		
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
		
		return cValue < maxValue;
		
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

		if ( cValue % fuzzer2.getMaximumValue() == 0 ) {
			payload1 = fuzzer1.next();
		}
		
		if (!fuzzer2.hasNext()) {
			fuzzer2.resetCurrentValue();
		}
		
		payload2 = fuzzer2.next();
		
		cValue++;
		
		return new String[] {payload1, payload2};
		
	}
	
	public void remove() {
		
		cValue--;
		
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
