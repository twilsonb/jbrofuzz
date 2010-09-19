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
package org.owasp.jbrofuzz.encode;

/**
 * ZBase32 - encodes and decodes Zimmmerman Base32 specification
 * 
 */
public class ZBase32 {
	/**
	 * Design decision for z-base-32:
	 * 	- lower case alphabet is easier to read
	 *  - 0 looks like o
	 *  - 2 looks like z or s
	 *  - 8 is ambiguous with 9 and difficult to read
	 *  - order chosen to ensure more easily readable characters appear more commonly
	 *  
	 * z-base-32 alphabet
	 * 	- ybndrfg8ejkmcpqxot1uwisza345h769
	 */
	private static char[] encoding = "ybndrfg8ejkmcpqxot1uwisza345h769".toCharArray();
	private static byte[] decoding;

	public ZBase32() {
		init();
	}

	private static void init() {
		decoding = new byte[0x80];
		for (int i = 0; i < encoding.length; i++) {
			decoding[encoding[i]] = (byte) i;
		}
	}

	public static String encode(String in) {
		return encode(in.getBytes());
	}

	public static String encode(final byte[] in) {
		init();
		final byte[] input         = in;
		final StringBuilder output = new StringBuilder();

		final int special = input.length % 5;
		final int normal  = input.length - special;

		for (int i = 0; i < normal; i += 5) {
			output.append(
					encoding[((input[i] & 0xff) >> 3) & 0x1f]
			);
			output.append(
					encoding[(((input[i] & 0xff) << 2) | ((input[i + 1] & 0xff) >> 6)) & 0x1f]
			);
			output.append(
					encoding[((input[i + 1] & 0xff) >> 1) & 0x1f]
			);
			output.append(
					encoding[(((input[i + 1] & 0xff) << 4) | ((input[i + 2] & 0xff) >> 4)) & 0x1f]
			);
			output.append(
					encoding[(((input[i + 2] & 0xff) << 1) | ((input[i + 3] & 0xff) >> 7)) & 0x1f]
			);
			output.append(
					encoding[((input[i + 3] & 0xff) >> 2) & 0x1f]
			);
			output.append(
					encoding[(((input[i + 3] & 0xff) << 3) | ((input[i + 4] & 0xff) >> 5)) & 0x1f]
			);
			output.append(
					encoding[(input[i + 4] & 0xff) & 0x1f]
			);
		}
		/*
		 * According to the spec, Z-Base-32 does not use padding so I have removed this from the implementation
		 */
		switch(special) {
		case 1:
			output.append(
					encoding[((input[normal] & 0xff) >> 3) & 0x1f]
			);
			output.append(
					encoding[((input[normal] & 0xff) << 2) & 0x1f]
			);
			//         output.append(
					//                 "======"
					//             );
			break;
		case 2:
			output.append(
					encoding[((input[normal] & 0xff) >> 3) & 0x1f]
			);
			output.append(
					encoding[(((input[normal] & 0xff) << 2) | ((input[normal + 1] & 0xff) >> 6)) & 0x1f]
			);
			output.append(
					encoding[((input[normal + 1] & 0xff) >> 1) & 0x1f]
			);
			output.append(
					encoding[((input[normal + 1] & 0xff) << 4) & 0x1f]
					         //            );
			//       output.append(
					//              "===="
			);
			break;
		case 3:
			output.append(
					encoding[((input[normal] & 0xff) >> 3) & 0x1f]
			);
			output.append(
					encoding[(((input[normal] & 0xff) << 2) | ((input[normal + 1] & 0xff) >> 6)) & 0x1f]
			);
			output.append(
					encoding[((input[normal + 1] & 0xff) >> 1) & 0x1f]
			);
			output.append(
					encoding[(((input[normal + 1] & 0xff) << 4) | ((input[normal + 2] & 0xff) >> 4)) & 0x1f]
			);
			output.append(
					encoding[((input[normal + 2] & 0xff) << 1) & 0x1f]
			);
			//       output.append(
					///              "==="
					//         );
			break;
		case 4:
			output.append(
					encoding[((input[normal] & 0xff) >> 3) & 0x1f]
			);
			output.append(
					encoding[(((input[normal] & 0xff) << 2) | ((input[normal + 1] & 0xff) >> 6)) & 0x1f]
			);
			output.append(
					encoding[((input[normal + 1] & 0xff) >> 1) & 0x1f]
			);
			output.append(
					encoding[(((input[normal + 1] & 0xff) << 4) | ((input[normal + 2] & 0xff) >> 4)) & 0x1f]
			);
			output.append(
					encoding[(((input[normal + 2] & 0xff) << 1) | ((input[normal + 3] & 0xff) >> 7)) & 0x1f]
			);
			output.append(
					encoding[((input[normal + 3] & 0xff) >> 2) & 0x1f]
			);
			output.append(
					encoding[((input[normal + 3] & 0xff) << 3) & 0x1f]
			);
			//     output.append(
					//            "="
					//       );
			break;
		}

		return output.toString();
	}

	public static String decode(String in) {
		init();
		final String input = in;
		final int expOrgSize = (int)Math.floor(input.length() / 1.6);
		final int expPadSize = ((int)Math.ceil(expOrgSize / 5.0)) * 8;
		final StringBuilder s= new StringBuilder(input);
		for (int i = 0; i < expPadSize; i++) {
			s.append("=");
		}

		final char[] data    = s.toString().toLowerCase().toCharArray();
		int dataLen    = data.length;
		while (dataLen > 0) {
			if (!ignore(data[dataLen - 1]))
				break;

			dataLen--;
		}

		final java.util.List<Byte> output = new java.util.ArrayList<Byte>();
		int i = 0;
		final int e = dataLen - 8;
		for (i = next(data, i, e); i < e; i = next(data, i, e)) {
			final byte b1 = decoding[data[i++]];
			i = next(data, i, e);
			final byte b2 = decoding[data[i++]];
			i = next(data, i, e);
			final byte b3 = decoding[data[i++]];
			i = next(data, i, e);
			final byte b4 = decoding[data[i++]];
			i = next(data, i, e);
			final byte b5 = decoding[data[i++]];
			i = next(data, i, e);
			final byte b6 = decoding[data[i++]];
			i = next(data, i, e);
			final byte b7 = decoding[data[i++]];
			i = next(data, i, e);
			final byte b8 = decoding[data[i++]];

			output.add((byte) ((b1 << 3) | (b2 >> 2)));
			output.add((byte) ((b2 << 6) | (b3 << 1) | (b4 >> 4)));
			output.add((byte) ((b4 << 4) | (b5 >> 1)));
			output.add((byte) ((b5 << 7) | (b6 << 2) | (b7 >> 3)));
			output.add((byte) ((b7 << 5) | b8));
		}

		if (data[dataLen - 6] == '=') {
			output.add((byte) (((decoding[data[dataLen - 8]]) << 3) | (decoding[data[dataLen - 7]] >> 2)));
		} else if(data[dataLen - 4] == '=') {
			output.add((byte) (((decoding[data[dataLen - 8]]) << 3) | (decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((decoding[data[dataLen - 7]]) << 6) | (decoding[data[dataLen - 6]] << 1) | (decoding[data[dataLen - 5]] >> 4)));
		} else if(data[dataLen - 3] == '=') {
			output.add((byte) (((decoding[data[dataLen - 8]]) << 3) | (decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((decoding[data[dataLen - 7]]) << 6) | (decoding[data[dataLen - 6]] << 1) | (decoding[data[dataLen - 5]] >> 4)));
			output.add((byte) (((decoding[data[dataLen - 5]]) << 4) | (decoding[data[dataLen - 4]] >> 1)));
		} else if(data[dataLen - 1] == '=') {
			output.add((byte) (((decoding[data[dataLen - 8]]) << 3) | (decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((decoding[data[dataLen - 7]]) << 6) | (decoding[data[dataLen - 6]] << 1) | (decoding[data[dataLen - 5]] >> 4)));
			output.add((byte) (((decoding[data[dataLen - 5]]) << 4) | (decoding[data[dataLen - 4]] >> 1)));
			output.add((byte) (((decoding[data[dataLen - 4]]) << 7) | (decoding[data[dataLen - 3]] << 2) | (decoding[data[dataLen - 2]] >> 3)));
		} else {
			output.add((byte) (((decoding[data[dataLen - 8]]) << 3) | (decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((decoding[data[dataLen - 7]]) << 6) | (decoding[data[dataLen - 6]] << 1) | (decoding[data[dataLen - 5]] >> 4)));
			output.add((byte) (((decoding[data[dataLen - 5]]) << 4) | (decoding[data[dataLen - 4]] >> 1)));
			output.add((byte) (((decoding[data[dataLen - 4]]) << 7) | (decoding[data[dataLen - 3]] << 2) | (decoding[data[dataLen - 2]] >> 3)));
			output.add((byte) (((decoding[data[dataLen - 2]]) << 5) | (decoding[data[dataLen - 1]])));
		}

		final byte[] b = toPrimitive(output.toArray(new Byte[0]));
		return trim(new String(b));
	}

	private static String trim(String s) {
		final char[] c = s.toCharArray();
		int end  = c.length;

		for (int i = c.length - 1; i >= 0; i--) {
			if ((c[i]) != 0)
				break;

			end = i;
		}

		return s.substring(0, end);
	}

	private static int next(char[] data, int i, int e) {
		while ((i < e) && ignore(data[i]))
			i++;

		return i;
	}

	private static boolean ignore(char c) {
		return (c == '\n') || (c == '\r') || (c == '\t') || (c == ' ') || (c == '-');
	}

	private static byte[] toPrimitive(Byte[] bytes) {
		final byte[] result = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result[i] = bytes[i];
		}

		return result;
	}
}






