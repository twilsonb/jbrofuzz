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
package org.owasp.jbrofuzz.core.net;

import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.core.NoSuchFuzzerException;

/**
 * <p>A Media Access Control address (MAC address) fuzzer is an iterator
 * for MAC addresses.</p>
 * 
 * <p>For larger values and further customisation, you can use the 
 * FuzzerBigInteger class, in org.owasp.jbrofuzz.core.</p>
 * 
 * @author subere@uncon.org
 * @version 2.5
 * @since 2.5
 */
public class MACAddrFuzzer implements Iterator<String> {
	
	private static final String HEX_DIGITS = "0123456789ABCDEF";
	
	private static final String ERROR_MSG = "Not a valid MAC Address";
	
	private long currentValue, maxValue;	
	
	private char[] hexCharArray = new char[16];
	
	private Separator separator;
	
	/**
	 * <p>A MAC address in human-friendly printable form consists of six
	 * groups of two hexadecimal digits, separated by a separator,
	 * typically a hyphen '-' or a colon ':'.</p>
	 * 
	 * <p>This enum allows for that selection, as well as others, 
	 * to be made.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 *
	 */
	public enum Separator { HYPHEN, COLON, SPACE, UNDERSCORE, NONE };
	
	/**
	 * <p>Construct a MAC address fuzzer, starting at value 
	 * <b>00:00:00:00:00:00</b> and ending at value
	 * <b>FF:FF:FF:FF:FF:FF</b>.
	 * 
	 * <p>This will create an iterator with 16^12 =
	 * 281474976710656 elements, in consecutive order.</p>
	 * 
	 * <p>The separating character used to separate octets is
	 * <b>':'</b>. If other separators are required, 
	 * constructors such as {@link #MACAddrFuzzer(Separator)}
	 * and {@link #MACAddrFuzzer(String, Separator)} should 
	 * be used.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public MACAddrFuzzer() {
		
		this(Separator.COLON);
		
	}
	
	/**
	 * <p>Construct a MAC Address fuzzer, with the specified
	 * separator, starting at value <b>00<->00<->00<->00<->00<->00</b> 
	 * and ending at value <b>FF<->FF<->FF<->FF<->FF<->FF</b>.
	 *  
	 * @param separator The character <-> will be replaced with a ':' 
	 * in case of a COLON, a '-' in case of a HYPHEN, etc. NONE can
	 * be selected for output of the format FFFFFFFFFFFF. In this
	 * latter case, no separating character will be appended
	 * between octets.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public MACAddrFuzzer(final Separator separator) {
	
		this.separator = separator;
		
		this.currentValue = 0L;
		this.maxValue = 281474976710656L;
		this.hexCharArray = HEX_DIGITS.toCharArray();
		
	}
	
	/**
	 * <p>Construct a MAC address fuzzer, starting at value of
	 * <b>macStart</b> and ending at value
	 * <b>FF:FF:FF:FF:FF:FF</b>.
	 * 
	 * <p>The separating character used to separate octets is
	 * <b>':'</b>. If other separators are required, 
	 * constructors such as {@link #MACAddrFuzzer(Separator)}
	 * and {@link #MACAddrFuzzer(String, Separator)} should 
	 * be used.</p>
	 * 
	 * @param macStart specifying e.g. "FF:FF:FF:FF:FF:00" 
	 * will iterate from {"FF:FF:FF:FF:FF:00" - 
	 * "FF:FF:FF:FF:FF:FF"}
	 * 
	 * @throws NoSuchFuzzerException if the format of macStart
	 * is not correct, the check is performed using 
	 * {@link #isValidMACAddress(String)}.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public MACAddrFuzzer(final String macStart) throws NoSuchFuzzerException {
		
		this(macStart, "FF:FF:FF:FF:FF:FF");
		
	}
	
	/**
	 * <p>Construct a MAC address fuzzer, starting at value of
	 * <b>macStart</b> and ending at value
	 * <b>FF:FF:FF:FF:FF:FF</b>.	 
	 *
	 * 
	 * @param macStart specifying e.g. "FF:FF:FF:FF:FF:00" 
	 * will iterate from {"FF:FF:FF:FF:FF:00" - 
	 * "FF:FF:FF:FF:FF:FF"}
	 * 
	 * @param separator The character <-> will be replaced with a ':' 
	 * in case of a COLON, a '-' in case of a HYPHEN, etc. NONE can
	 * be selected for output of the format FFFFFFFFFFFF. In this
	 * latter case, no separating character will be appended
	 * between octets.</p>
	 * 
	 * 
	 * @throws NoSuchFuzzerException if the format of macStart
	 * is not correct, the check is performed using 
	 * {@link #isValidMACAddress(String)}.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public MACAddrFuzzer(final String macStart, final Separator separator) throws NoSuchFuzzerException {
		
		this(macStart, "FF:FF:FF:FF:FF:FF", separator);
		
	}
	
	/**
	 * <p>Construct a MAC address fuzzer, starting at value of
	 * <b>macStart</b> and ending at value
	 * <b>macEnd</b>.	 
	 *
	 * 
	 * @param macStart specifying e.g. "FF:FF:FF:FF:FF:00" 
	 * will iterate from that value, inclusive.
	 * 
	 * @param macEnd specifying e.g. "FF:FF:FF:FF:FF:FF"
	 * will iterate to that value, inclusive.
	 * 
	 * @throws NoSuchFuzzerException if the format of macStart
	 * is not correct, the check is performed using 
	 * {@link #isValidMACAddress(String)}.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public MACAddrFuzzer(final String macStart, final String macEnd) throws NoSuchFuzzerException {
		
		final char charSeparator1 = MACAddrFuzzer.getFirstSeparator(macStart);
		final char charSeparator2 = MACAddrFuzzer.getFirstSeparator(macEnd);
		// Set the char separator off the first MAC address
		this.separator = MACAddrFuzzer.getSeparatorEnum(charSeparator1);
		
		if(!MACAddrFuzzer.isValidMACAddress(macStart, charSeparator1)) {
			throw new NoSuchFuzzerException(ERROR_MSG);
		}
		
		if(!MACAddrFuzzer.isValidMACAddress(macEnd, charSeparator2)) {
			throw new NoSuchFuzzerException(ERROR_MSG);
		}
		
		this.currentValue = MACAddrFuzzer.parseMAC(macStart, charSeparator1);
		this.maxValue = MACAddrFuzzer.parseMAC(macEnd, charSeparator2);
		
		if(Character.isLowerCase(macStart.charAt(0))) {
			this.hexCharArray = HEX_DIGITS.toLowerCase(Locale.ENGLISH).toCharArray();
		} else {
			this.hexCharArray = HEX_DIGITS.toCharArray();
		}
		
	}
	
	/**
	 * <p>Construct a MAC address fuzzer, starting at value of
	 * <b>macStart</b> and ending at value
	 * <b>macEnd</b>.	 
	 *
	 * 
	 * @param macStart specifying e.g. "FF:FF:FF:FF:FF:00" 
	 * will iterate from that value, inclusive.
	 * 
	 * @param macEnd specifying e.g. "FF:FF:FF:FF:FF:FF"
	 * will iterate to that value, inclusive.
	 * 
	 * @param separator The character <-> will be replaced with a ':' 
	 * in case of a COLON, a '-' in case of a HYPHEN, etc. NONE can
	 * be selected for output of the format FFFFFFFFFFFF. In this
	 * latter case, no separating character will be appended
	 * between octets.</p>
	 * 
	 * 
	 * @throws NoSuchFuzzerException if the format of macStart
	 * is not correct, the check is performed using 
	 * {@link #isValidMACAddress(String)}.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public MACAddrFuzzer(final String macStart, final String macEnd, final Separator separator) throws NoSuchFuzzerException {
		
		final char charSeparator1 = MACAddrFuzzer.getFirstSeparator(macStart);
		final char charSeparator2 = MACAddrFuzzer.getFirstSeparator(macEnd);
		
		this.separator = separator;
		
		if(!MACAddrFuzzer.isValidMACAddress(macStart, charSeparator1)) {
			throw new NoSuchFuzzerException(ERROR_MSG);
		}
		
		if(!MACAddrFuzzer.isValidMACAddress(macEnd, charSeparator2)) {
			throw new NoSuchFuzzerException(ERROR_MSG);
		}

		this.currentValue = MACAddrFuzzer.parseMAC(macStart, charSeparator1);
		this.maxValue = MACAddrFuzzer.parseMAC(macEnd, charSeparator2);
		
		if(Character.isLowerCase(macStart.charAt(0))) {
			this.hexCharArray = HEX_DIGITS.toLowerCase(Locale.ENGLISH).toCharArray();
		} else {
			this.hexCharArray = HEX_DIGITS.toCharArray();
		}

	}
	
	/**
	 * <p>Get the current numeric value as a long that the MAC Address
	 * you iterating through.</p>
	 * 
	 * @return long "00:00:00:00:00:00" will return 0L 
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public long getCurrentValue() {
		
		return this.currentValue;
		
	}
	
	/**
	 * <p>Reset to zero i.e. "00:00:00:00:00:00".</p>
	 *
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public void resetCurrentValue() {
		
		currentValue = 0L;
		
	}

	/**
	 * <p>Return "050-MAC-ADD".</p>
	 *
	 * @return String
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public String getId() {
		
		return "050-MAC-ADD";
	}
	
	
	/**
	 * <p>Return the maximum mac address as a long.</p>
	 *
	 * @return long, the max value that can be returned
	 * by this method is <b>281474976710655</b> for
	 * <b>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public long getMaximumValue() {
		
		return maxValue;
		
	}
	
	/**
	 * <p>Return "MAC Address Fuzzer".</p>
	 * 
	 * @return String
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public String getName() {
		
		return "MAC Address Fuzzer";
		
	}
	
	/**
	 * <p>Check whether or not the MAC fuzzer iterator has a next element.</p>
	 * 
	 * @return true if the fuzzer has more elements to return
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public boolean hasNext() {
		
		return currentValue <= maxValue;
		
	}
	
	/**
	 * <p>Return the next MAC Address element.</p>
	 * 
	 * @return String 00<->11<->22<->33<->44<->55 where
	 * <-> could be any of the type specified by the 
	 * Separator enum.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public String next() {
		
		final StringBuffer output = new StringBuffer(17);
		
		long val = currentValue;
		// Perform division on a stack
		final Stack<Integer> stack = new Stack<Integer>();

		while (val >= 16) {

			stack.push( Integer.valueOf((int) val % 16) );

			val = val / 16;

		}
		// Append the relevant empty positions with the first element
		// identified
		output.append(StringUtils.leftPad(Character.toString(hexCharArray[(int)val]),
				12 - stack.size(), "0"));
		while (!stack.isEmpty()) {
			output.append(Character.toString(hexCharArray[stack.pop().intValue()]));
		}

		currentValue++;
		
		if(separator == Separator.NONE) {
			
			return output.toString();
			
		} else {
			char delim;
			//Add the character delimeter			
			switch(separator) {
				case HYPHEN: delim = '-'; 
								break;
				case COLON: delim = ':';
								break;
				case SPACE: delim = ' ';
								break;
				case UNDERSCORE: delim = '_';
								break;
				default: delim = ':';
								break;
			}
			
			for(int index = 2; index < 17; index += 3) {
				output.insert(index, delim);
			}
			

		}
		
		return output.toString();
		
	}
	
	/**
	 * <p>This method should not be trusted or used in the conventional
	 * way that an iterator requires remove() to be implemented.</p>
	 * 
	 * <p>Instead, during fuzzing, remove() can be called to
	 * step back to the previous element.</p>
	 * 
	 * <p>This need is typical, in replay scenarios where something
	 * worth investigating has been discovered and a quick, step
	 * back step forward is executed.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public void remove() {
		currentValue--;
	}
	
	/**
	 * <p>Return the <b>Separator</b> enum type, for a 
	 * given character.</p>
	 * 
	 * <p>By default it returns a ':'.</p>
	 * 
	 * @param separator
	 * 
	 * @return Separator
	 */
	private static Separator getSeparatorEnum(final char separator) {
		
		switch(separator) {
		
			case '-': return Separator.HYPHEN;
			case ':': return Separator.COLON;
			case '_': return Separator.UNDERSCORE;
			case ' ': return Separator.SPACE;
			
			default: return Separator.COLON;
			
		}

	}
	
	/**
	 * <p>Return the first separator found in the mac
	 * address.</p>
	 * 
	 * <p>In case of an error, return '0'.</p>
	 * 
	 * @param mac e.g. 00:FF:00:FF:00:FF
	 * @return ':'
	 */
	private static char getFirstSeparator(final String mac) {
		
		try {
			
			return mac.charAt(2);
			
		} catch (StringIndexOutOfBoundsException e) {
			
			return '0';
			
		}
		
	}
	
	/**
	 * <p>Method that checks for the validity of a mac address,
	 * assuming the delimeter character is.</p>
	 * 
	 * <p>Examples:<br>
	 * <code>
	 * 01-23-45-67-89-ab returns true, if char is '-'
	 * 01:23:45:67:89:ab returns true, if char is ':'
	 * 0123.4567.89ab returns false
	 * </code>
	 * </p>  
	 * 
	 * @param mac The mac address e.g. 00-00-00-00-FF-FF
	 * @param delim The delimeter character, for the mac address 
	 * 00-00-00-00-FF-FF it should be: '-'
	 * @return true if valid, false otherwise
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public static boolean isValidMACAddress(final String mac) {
	
		return MACAddrFuzzer.isValidMACAddress(mac, ':');
		
	}
	
	/**
	 * <p>Method that checks for the validity of a mac address,
	 * given the delimeter character that separates each octet.</p>
	 * 
	 * <p>Examples:<br>
	 * <code>
	 * 01-23-45-67-89-ab returns true, if char is '-'
	 * 01:23:45:67:89:ab returns true, if char is ':'
	 * 0123.4567.89ab returns false
	 * </code>
	 * </p>  
	 * 
	 * @param mac The mac address e.g. 00-00-00-00-FF-FF
	 * 
	 * @param separator The delimeter character, for the mac address 
	 * 00-00-00-00-FF-FF it should be: '-'
	 * 
	 * @return true if valid, false otherwise
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public static boolean isValidMACAddress(final String mac, final char separator) {
		
		final int macAddrLength = mac.length();
		
		if(macAddrLength != 17) {
			return false;
		}
		
		for(int index = 0; index < macAddrLength; index++) {
			
			final char charAt = mac.charAt(index);
			
			if( (index + 1) % 3 == 0 ) {
				
				if( charAt != separator) {
					return false;
				}
				
			} else {
				
				if( !MACAddrFuzzer.isHexDigit( charAt ) ) {
					return false;
				}
				
			}
			
		}
				
		return true;
		
	}
	
	/**
	 * <p>Method for checking if a particular character is
	 * a hexadecimal digit, i.e. "0123456789", "ABCDEF",
	 * "abcdef".</p>
	 * 
	 * @param currentChar The character to be checked
	 * @return true if the character is in any of
	 * the above three groups of characters, false
	 * otherwise.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public static boolean isHexDigit(final char currentChar) {
		
		for(char current : HEX_DIGITS.toCharArray()) {
			if(current == currentChar) {
				return true;
			}
		}
		
		for(char current : HEX_DIGITS.toLowerCase(Locale.ENGLISH).toCharArray()) {
			if(current == currentChar) {
				return true;
			}
		}
		
		return false;
		
	}

	/**
	 * <p>Method that returns the long value of a MAC address,
	 * if you were to add up all the digits in hexadecimal 
	 * form.</p>
	 * 
	 * <p>Returns -1L if the MAC address is not valid.</p>
	 * 
	 * <p>Examples:<br>
	 * <code>
	 * 00-00-00-00-00-FF returns -1L<br>
	 * 00:00:00:00:00:00 returns 0L<br>
	 * 00:00:00:00:FF:FF returns 65535L<br>
	 * </code>
	 * </p>
	 * 
	 * @param mac The mac address e.g. 00-00-00-00-FF-FF
	 * 
	 * @return The mac address number value, as a long
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public static long parseMAC(final String mac) {
		
		return MACAddrFuzzer.parseMAC(mac, ':');
		
	}
	
	/**
	 * <p>Method that returns the long value of a MAC address,
	 * if you were to add up all the digits in hexadecimal 
	 * form.</p>
	 * 
	 * <p>Returns -1L if the MAC address is not valid.</p>
	 * 
	 * <p>Examples:<br>
	 * <code>
	 * 00-00-00-00-00-00 returns 0L<br>
	 * 00-00-00-00-FF-FF returns 65535L<br>
	 * FF-FF-FF-FF-FF-FF returns 281474976710655<br>
	 * </code>
	 * </p>
	 * 
	 * @param mac The mac address e.g. 00-00-00-00-FF-FF
	 * 
	 * @param delim The delimeter character, for the mac address 
	 * 00-00-00-00-FF-FF it should be: '-'
	 * 
	 * @return The mac address number value, as a long
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public static long parseMAC(final String mac, final char delim) {
		
		if(!MACAddrFuzzer.isValidMACAddress(mac, delim)) {
			return -1L;
		}
		
		final StringTokenizer sTokeniser = new StringTokenizer(mac, Character.toString(delim));
		
		long value = 0L;
		for(int octet = 6; octet > 0; octet--) {
			value += Integer.parseInt(sTokeniser.nextToken(), 16) * Math.pow( 256, octet - 1 );
		}
		return value;
		
	}
}
