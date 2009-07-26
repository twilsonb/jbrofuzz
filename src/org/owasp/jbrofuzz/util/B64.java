/**
 * JBroFuzz 1.5
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
package org.owasp.jbrofuzz.util;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * Class responsible for encoding and decoding a given byte array to a Base64
 * String and vice-versa. The operation of the class is performed by invoking
 * the encode and decode methods.
 * </p>
 * 
 * @author subere@uncon.org
 * @since 0.1
 * @version 1.5
 */
public class B64 {

	/**
	 * <p>
	 * Method used to decode a given Base64 string into a byte array.
	 * </p>
	 * 
	 * @param base64
	 *            String
	 * @return byte[]
	 */
	public static byte[] decode(final String base64) {

		int pad = 0;
		for (int i = base64.length() - 1; base64.charAt(i) == '='; i--) {
			pad++;
		}
		final int length = base64.length() * 6 / 8 - pad;
		final byte[] raw = new byte[length];
		int rawIndex = 0;
		for (int i = 0; i < base64.length(); i += 4) {
			final int block = (B64.getValue(base64.charAt(i)) << 18)
					+ (B64.getValue(base64.charAt(i + 1)) << 12)
					+ (B64.getValue(base64.charAt(i + 2)) << 6)
					+ (B64.getValue(base64.charAt(i + 3)));
			for (int j = 0; (j < 3) && (rawIndex + j < raw.length); j++) {
				raw[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xff);
			}
			rawIndex += 3;
		}
		return raw;
	}

	/**
	 * <p>
	 * Method for decoding an input String into normal characters from Base64
	 * format.
	 * </p>
	 * <p>
	 * If the input is not in Base64 format, a blank String is returned "".
	 * </p>
	 * 
	 * @param input
	 * @return String or an empty String "" in case of an error.
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.5
	 * 
	 */
	public static String decodeString(final String input) {

		try {
			return new String(B64.decode(input));
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}

	}

	/**
	 * <p>
	 * Method used to encode a given byte array into a Base64 string.
	 * </p>
	 * 
	 * @param raw
	 *            byte[]
	 * @return String
	 */
	public static String encode(final byte[] raw) {

		final StringBuffer encoded = new StringBuffer();
		for (int i = 0; i < raw.length; i += 3) {
			encoded.append(B64.encodeBlock(raw, i));
		}
		return encoded.toString();
	}

	/**
	 * <p>
	 * Method for encoding an input String into Base64 format.
	 * </p>
	 * <p>
	 * The input String is converted into a byte array and then encoded into
	 * Base64.
	 * </p>
	 * <p>
	 * The encoding for the input array is "ISO-8859-1".
	 * </p>
	 * 
	 * @param input
	 * @return A Base64 input String or an empty String in the event that the
	 *         encoding of "ISO-8859-1 cannot be found.
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.5
	 * 
	 */
	public static String encodeString(final String input) {

		try {
			return encode(input.getBytes("iso-8859-1"));
		} catch (UnsupportedEncodingException e) {
			return "";
		}

	}

	private static char[] encodeBlock(final byte[] raw, final int offset) {
		int block = 0;
		final int slack = raw.length - offset - 1;
		final int end = (slack >= 2) ? 2 : slack;
		for (int i = 0; i <= end; i++) {
			final byte b = raw[offset + i];
			final int neuter = (b < 0) ? b + 256 : b;
			block += neuter << (8 * (2 - i));
		}
		final char[] base64 = new char[4];
		for (int i = 0; i < 4; i++) {
			final int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
			base64[i] = B64.getChar(sixbit);
		}
		if (slack < 1) {
			base64[2] = '=';
		}
		if (slack < 2) {
			base64[3] = '=';
		}
		return base64;
	}

	private static char getChar(final int sixBit) {
		if ((sixBit >= 0) && (sixBit <= 25)) {
			return (char) ('A' + sixBit);
		}
		if ((sixBit >= 26) && (sixBit <= 51)) {
			return (char) ('a' + (sixBit - 26));
		}
		if ((sixBit >= 52) && (sixBit <= 61)) {
			return (char) ('0' + (sixBit - 52));
		}
		if (sixBit == 62) {
			return '+';
		}
		if (sixBit == 63) {
			return '/';
		}
		return '?';
	}

	private static int getValue(final char c) {
		if ((c >= 'A') && (c <= 'Z')) {
			return c - 'A';
		}
		if ((c >= 'a') && (c <= 'z')) {
			return c - 'a' + 26;
		}
		if ((c >= '0') && (c <= '9')) {
			return c - '0' + 52;
		}
		if (c == '+') {
			return 62;
		}
		if (c == '/') {
			return 63;
		}
		if (c == '=') {
			return 0;
		}
		return -1;
	}
}
