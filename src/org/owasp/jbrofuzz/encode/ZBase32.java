/**
 * JBroFuzz 2.1
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
    
    private static String fix(String in){
    	int a = in.length()-((in.length()/10)*10);
    	if (a == 1 || a == 6){
    		StringBuffer buf = new StringBuffer();
    		buf.append(in).append(" ");
     		return buf.toString();
    	}else if (a == 0 || a == 5){
    		StringBuffer buf = new StringBuffer();
    		buf.append(in).append("  ");
     		return buf.toString();
    	}else{
    		return in;
    	}
    }
    
    public static String encode(String in) {
    	init();
        return encode(fix(in).getBytes());
    }
    
    public static String encode(byte[] in) {
    	init();
        byte[] input         = in;
        StringBuilder output = new StringBuilder();
        
        int special = input.length % 5;
        int normal  = input.length - special;
        
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
        
        switch(special) {
            case 1:
                    output.append(
                            encoding[((input[normal] & 0xff) >> 3) & 0x1f]
                        );
                    output.append(
                            encoding[((input[normal] & 0xff) >> 2) & 0x1f]
                        );
                    output.append(
                            "======"
                        );
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
                        );
                    output.append(
                            "===="
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
                    output.append(
                            "==="
                        );
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
                    output.append(
                            "="
                        );
                    break;
        }
        
        return output.toString();
    }
    
    public static String decode(String in) {
    	init();
    	String input = fix(in);
        int expOrgSize = (int)Math.floor(input.length() / 1.6);
        int expPadSize = ((int)Math.ceil(expOrgSize / 5.0)) * 8;
        StringBuilder s= new StringBuilder(input);
        for (int i = 0; i < expPadSize; i++) {
            s.append("=");
        }
        
        char[] data    = s.toString().toLowerCase().toCharArray();
        int dataLen    = data.length;
        while (dataLen > 0) {
            if (!ignore(data[dataLen - 1]))
                break;
            
            dataLen--;
        }
        
        java.util.List<Byte> output = new java.util.ArrayList<Byte>();
        int i = 0;
        int e = dataLen - 8;
        for (i = next(data, i, e); i < e; i = next(data, i, e)) {
        	byte b1 = decoding[data[i++]];
            i = next(data, i, e);
            byte b2 = decoding[data[i++]];
            i = next(data, i, e);
            byte b3 = decoding[data[i++]];
            i = next(data, i, e);
            byte b4 = decoding[data[i++]];
            i = next(data, i, e);
            byte b5 = decoding[data[i++]];
            i = next(data, i, e);
            byte b6 = decoding[data[i++]];
            i = next(data, i, e);
            byte b7 = decoding[data[i++]];
            i = next(data, i, e);
            byte b8 = decoding[data[i++]];
            
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
        
        byte[] b = toPrimitive(output.toArray(new Byte[0]));
        return trim(new String(b));
    }
    
    private static String trim(String s) {
        char[] c = s.toCharArray();
        int end  = c.length;
        
        for (int i = c.length - 1; i >= 0; i--) {
            if (((int)c[i]) != 0)
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
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        
        return result;
    }
}






