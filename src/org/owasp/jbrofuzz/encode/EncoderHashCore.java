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
package org.owasp.jbrofuzz.encode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Yiannis Marangos
 * @version 2.0
 * @since 2.0
 */
public class EncoderHashCore {
	
	public static final String[] CODES = { "Plain Text",
		"URL Cp1252", "URL UTF-8", "URL UTF-16BE", "URL UTF-16LE", 
		"Base64", "Base32", "Z-Base32", "MD5 Hash", "SHA-1 Hash", "SHA-256 Hash",
		"SHA-384 Hash", "SHA-512 Hash", "Hexadecimal (low)", "Hexadecimal (UPP)", 
		"Binary", "www-form-urlencoded", "RFC 1521 MIME (eMail)",
		"Escape: HTML", "Escape: CSV", "Escape: Java", "Escape: JavaScript",
		"Escape: SQL", "Escape: XML"
	};
	
	private final static String IS_DECODABLE[] = { "Plain Text",
		"URL Cp1252", "URL UTF-8", "URL UTF-16BE", "URL UTF-16LE", "Base64", "Base32", "Z-Base32","Hexadecimal (low)",
		"Hexadecimal (UPP)", "Binary", "www-form-urlencoded", "RFC 1521 MIME (eMail)",
		"Escape: HTML", "Escape: CSV", "Escape: Java", "Escape: JavaScript",
		"Escape: XML"
	};
	
	private static final String[] COMMENTS = {
		"This is a null decoder",
		"This is using the java URL decoder, but with: URLDecoder.decode(decodeText, \"windows-1252\");", 
		"This is using the java URL decoder, but with: URLEncoder.encode(encodeText, \"UTF-8\");",
		"This is using the java URL decoder, but with: URLEncoder.encode(encodeText, \"UTF-16LE\");",
		"This is using the java URL decoder, but with: URLEncoder.encode(encodeText, \"UTF-16BE\");",
		"MIME Base64 uses a 64-character alphabet consisting of upper- and lower-case Roman alphabet characters (A-Z, a-z), the numerals (0-9), and the \"+\" and \"/\" symbols. The \"=\" symbol is also used as a special suffix code.", 
		"MIME Base32 uses a 32-character alphabet, as defined by RFC 4648, consisting of upper-case Roman alphabet characters (A-Z), the numerals (2-7), and the \"=\" symbol.",
		"Z-Base32 is a Base32 encoding designed to be easier for human use and more compact, currently used in Phil Zimmermann's ZRTP protocol.",
		"The 128-bit (16-byte) MD5 hashes (also termed message digests) are typically represented as a sequence of 32 hexadecimal digits. The hash of the zero-length string is: MD5(\"\") = d41d8cd98f00b204e9800998ecf8427e", 
		"SHA-1 (as well as SHA-0) produces a 160-bit digest from a message with a maximum length of (2^64 - 1) bits.  Encoding is performed parsing by getting the bytes in encodeText.getBytes() standard. ", 
		"SHA-256 produces a 256-bit digest from a message with a maximum length of (2^64-1) bits. Encoding is performed parsing by getting the bytes in encodeText.getBytes() standard. ",
		"SHA-384 produces a 384-bit digest from a message with a maximum length of (2^128-1) bits. Encoding is performed parsing by getting the bytes in encodeText.getBytes() standard. ", 
		"SHA-512 produces a 512-bit digest from a message with a maximum length of (2^128-1) bits. Encoding is performed parsing by getting the bytes in encodeText.getBytes() standard. ", 
		"Hexadecimal (low)", 
		"Hexadecimal (UPP)", 
		"Binary : BinaryCodec.toAsciiChars(encodeText.getBytes()", 
		"www-form-urlencoded", 
		"RFC 1521 MIME (eMail) using QuotedPrintableCodec with codec.encode(encodeText);",
		"Supports all known HTML 4.0 entities, including funky accents. Note that the commonly used apostrophe escape character (&apos;) is not a legal entity and so is not supported). ", 
		"Returns a String value for a CSV column enclosed in double quotes, if required. If the value contains a comma, newline or double quote, then the String value is returned enclosed in double quotes. Any double quote characters in the value are escaped with another double quote. If the value does not contain a comma, newline or double quote, then the String value is returned unchanged. See RFC 4180. ", 
		"Escapes the characters in a String using Java String rules. Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) So a tab becomes the characters '\\' and 't'. The only difference between Java strings and JavaScript strings is that in JavaScript, a single quote and forward-slash (/) are escaped.", 
		"Escapes the characters in a String using JavaScript String rules. The only difference between Java strings and JavaScript strings is that in JavaScript, a single quote must be escaped.",
		"Escapes the characters in a String to be suitable to pass to an SQL query. \nFor example, \n\nstatement.executeQuery(\"SELECT * FROM MOVIES WHERE TITLE='\" + StringEscapeUtils.escapeSql(\"McHale's Navy\")  At present, this method only turns single-quotes into doubled single-quotes (\"McHale's Navy\" => \"McHale''s Navy\"). It does not handle the cases of percent (%) or underscore (_) for use in LIKE clauses.\n\nsee http://www.jguru.com/faq/view.jsp?EID=8881 ", 
		"Escapes the characters in a String using XML entities. For example: \"bread\" & \"butter\" => &quot;bread&quot; &amp; &quot;butter&quot;. Supports only the five basic XML entities (gt, lt, quot, amp, apos). Does not support DTDs or external entities. Note that unicode characters greater than 0x7f are as of 3.0, no longer escaped. "
	};
	
	public static boolean isDecoded(final String type) {
		for (int i=0; i<IS_DECODABLE.length; i++)
			if (type.equalsIgnoreCase(IS_DECODABLE[i])) {
				return true;
			}
		return false;
	}
	
	
	
	protected static String getComment(final String type) {
		
		for (int i=0; i<CODES.length; i++) {
			if(type.equalsIgnoreCase(CODES[i])) {
				return COMMENTS[i];
			}
		}
		
		return "";
		
	}
	
	public static String decode(final String decodeText, final String type) {
			if (!isDecoded(type))
				return "Error: String cannot be decoded...";
			else if (type.equalsIgnoreCase("Plain Text"))
				return decodeText;
			else if (type.equalsIgnoreCase("URL Cp1252"))
				return decodeUrlWindows1252(decodeText);
			else if (type.equalsIgnoreCase("URL UTF-8"))
				return decodeUrlUtf8(decodeText);
			else if (type.equalsIgnoreCase("URL UTF-16BE"))
				return decodeUrlUtf16BE(decodeText);
			else if (type.equalsIgnoreCase("URL UTF-16LE"))
				return decodeUrlUtf16LE(decodeText);
			else if (type.equalsIgnoreCase("Base64"))
				return decodeBase64(decodeText);
			else if (type.equalsIgnoreCase("Base32"))
				return decodeBase32(decodeText);
			else if (type.equalsIgnoreCase("Z-Base32"))
				return decodeZBase32(decodeText);
			else if (type.equalsIgnoreCase("Hexadecimal (low)"))
				return decodeHexLow(decodeText);
			else if (type.equalsIgnoreCase("Hexadecimal (UPP)"))
				return decodeHexUpp(decodeText);
			else if (type.equalsIgnoreCase("Binary"))
				return decodeBinary(decodeText);
			else if (type.equalsIgnoreCase("www-form-urlencoded"))
				return decodeUrlCodec(decodeText);
			else if (type.equalsIgnoreCase("RFC 1521 MIME (eMail)"))
				return decodeRfc1521(decodeText);
			else if (type.equalsIgnoreCase("Escape: HTML"))
				return decodeEscHtml(decodeText);
			else if (type.equalsIgnoreCase("Escape: CSV"))
				return decodeEscCsv(decodeText);
			else if (type.equalsIgnoreCase("Escape: Java"))
				return decodeEscJava(decodeText);
			else if (type.equalsIgnoreCase("Escape: JavaScript"))
				return decodeEscJavaScript(decodeText);
			else if (type.equalsIgnoreCase("Escape: Xml"))
				return decodeEscXml(decodeText);
			else
				return  "Error: Decoding type not found...";
	}
	
	// Decode Base64
	private static String decodeBase64(final String decodeText) {
		return new String(Base64.decodeBase64(decodeText.getBytes()));
	}
	
	// Decode Base32
	private static String decodeBase32(final String decodeText) {
		return new String(Base32.decode(decodeText));
	}
	
	// Decode Z-Base32
	private static String decodeZBase32(final String decodeText) {
		return ZBase32.decode(decodeText);
	}
	
	// Decode Binary
	private static String decodeBinary(final String decodeText) {
		return new String(BinaryCodec.fromAscii(decodeText.getBytes()));
	}

	// CSV Decode
	private static String decodeEscCsv(final String decodeText) {
		return StringEscapeUtils.unescapeCsv(decodeText);
	}
	
	// HTML Decode
	private static String decodeEscHtml(final String decodeText) {
		return StringEscapeUtils.unescapeHtml(decodeText);
	}

	// Java Decode
	private static String decodeEscJava(final String decodeText) {
		return StringEscapeUtils.unescapeJava(decodeText);
	}
	
	// JavaScript Decode
	private static String decodeEscJavaScript(final String decodeText) {
		return StringEscapeUtils.unescapeJavaScript(decodeText);
	}

	// XML Decode
	private static String decodeEscXml(final String decodeText) {
		return StringEscapeUtils.unescapeXml(decodeText);
	}
	
	// Decode Hexadecimal lowercase
	private static String decodeHexLow(final String decodeText) {
		try {
			return new String(Hex.decodeHex(decodeText.toCharArray()));
		} catch (final DecoderException e) {
			return "Error: Hex value cannot be decoded...";
		}
	}

	// Decode Hexadecimal uppercase
	private static String decodeHexUpp(final String decodeText) {
		return decodeHexLow(decodeText);
	}
	
	// Decode RFC 1521 MIME (Multipurpose Internet Mail Extensions) 
	// Part One. Rules #3, #4, and #5 of the quoted-printable spec are not implemented yet
	private static String decodeRfc1521(final String decodeText) {
		final QuotedPrintableCodec codec = new QuotedPrintableCodec();
		try {
			return codec.decode(decodeText);
		} catch (final DecoderException e) {
			return "Error: RFC 1521 MIME value cannot be decoded...";
		}
	}

	// Decode www-form-url
	private static String decodeUrlCodec(final String decodeText) {
		final URLCodec codec = new URLCodec();
		try {
			return codec.decode(decodeText, "UTF-8");
		} catch (final DecoderException e) {
			return "Error: www-form-urlencoded value cannot be decoded...";
		} catch (final UnsupportedEncodingException e) {
			return "Error: www-form-urlencoded value cannot be decoded...";
		} 
	}
	
	// URL Decode (UTF-16BE)
	private static String decodeUrlUtf16BE(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "UTF-16BE");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (UTF-16LE)
	private static String decodeUrlUtf16LE(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "UTF-16LE");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (UTF-8)
	private static String decodeUrlUtf8(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (windows-1252)
	private static String decodeUrlWindows1252(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "windows-1252");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	public static String encode(final String encodeText, final String type) {
		if (type.equalsIgnoreCase("URL Cp1252"))
			return encodeUrlWindows1252(encodeText);
		else if (type.equalsIgnoreCase("Plain Text"))
			return encodeText;
		else if (type.equalsIgnoreCase("URL UTF-8"))
			return encodeUrlUtf8(encodeText);
		else if (type.equalsIgnoreCase("URL UTF-16BE"))
			return encodeUrlUtf16BE(encodeText);
		else if (type.equalsIgnoreCase("URL UTF-16LE"))
			return encodeUrlUtf16LE(encodeText);
		else if (type.equalsIgnoreCase("Base64"))
			return encodeBase64(encodeText);
		else if (type.equalsIgnoreCase("Base32"))
			return encodeBase32(encodeText);
		else if (type.equalsIgnoreCase("Z-Base32"))
			return encodeZBase32(encodeText);
		else if (type.equalsIgnoreCase("MD5 Hash"))
			return encodeMd5Hash(encodeText);
		else if (type.equalsIgnoreCase("SHA-1 Hash"))			
			return encodeSha1Hash(encodeText);
		else if (type.equalsIgnoreCase("SHA-256 Hash"))
			return encodeSha256Hash(encodeText);
		else if (type.equalsIgnoreCase("SHA-384 Hash"))
			return encodeSha384Hash(encodeText);
		else if (type.equalsIgnoreCase("SHA-512 Hash"))
			return encodeSha512Hash(encodeText);
		else if (type.equalsIgnoreCase("Hexadecimal (low)"))
			return encodeHexLow(encodeText);
		else if (type.equalsIgnoreCase("Hexadecimal (UPP)"))
			return encodeHexUpp(encodeText);
		else if (type.equalsIgnoreCase("Binary"))
			return encodeBinary(encodeText);
		else if (type.equalsIgnoreCase("www-form-urlencoded"))
			return encodeUrlCodec(encodeText);
		else if (type.equalsIgnoreCase("RFC 1521 MIME (eMail)"))
			return encodeRfc1521(encodeText);
		else if (type.equalsIgnoreCase("Escape: HTML"))
			return encodeEscHtml(encodeText);
		else if (type.equalsIgnoreCase("Escape: CSV"))
			return encodeEscCsv(encodeText);
		else if (type.equalsIgnoreCase("Escape: Java"))
			return encodeEscJava(encodeText);
		else if (type.equalsIgnoreCase("Escape: JavaScript"))
			return encodeEscJavaScript(encodeText);
		else if (type.equalsIgnoreCase("Escape: SQL"))
			return encodeEscSql(encodeText);
		else if (type.equalsIgnoreCase("Escape: XML"))
			return encodeEscXml(encodeText);
		else
			return "Error: Encoding type not found...";
	}

	// Encode Base64
	private static String encodeBase64(final String encodeText) {
		return new String(Base64.encodeBase64(encodeText.getBytes()));
	}
	
	// Encode Base32
	private static String encodeBase32(final String encodeText) {
		return Base32.encode(encodeText.getBytes());
	}
	
	// Encode Z-Base32
	private static String encodeZBase32(final String encodeText) {
		return ZBase32.encode(encodeText);
	}
	
	// Encode Binary
	private static String encodeBinary(final String encodeText) {
		return new String(BinaryCodec.toAsciiChars(encodeText.getBytes()));
	}

	// CSV Encode
	private static String encodeEscCsv(final String encodeText) {
		return StringEscapeUtils.escapeCsv(encodeText);
	}
	
	// HTML Encode
	private static String encodeEscHtml(final String encodeText) {
		return StringEscapeUtils.escapeHtml(encodeText);
	}
	
	// Java Encode
	private static String encodeEscJava(final String encodeText) {
		return StringEscapeUtils.escapeJava(encodeText);
	}
	
	// JavaScript Encode
	private static String encodeEscJavaScript(final String encodeText) {
		return StringEscapeUtils.escapeJavaScript(encodeText);
	}
	
	// SQL Encode
	private static String encodeEscSql(final String encodeText) {
		return StringEscapeUtils.escapeSql(encodeText);
	}
	
	// XML Encode
	private static String encodeEscXml(final String encodeText) {
		return StringEscapeUtils.escapeXml(encodeText);
	}
	
	// Encode Hexadecimal lowercase
	private static String encodeHexLow(final String encodeText) {
		return new String(Hex.encodeHex(encodeText.getBytes()));
	}
	
	// Encode Hexadecimal uppercase
	private static String encodeHexUpp(final String encodeText) {
		return encodeHexLow(encodeText).toUpperCase();
	}
	
	// Encode MD5 Hash
	private static String encodeMd5Hash(final String encodeText) {
		try {
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(encodeText.getBytes(), 0, encodeText.length());
			final byte[] hash = md5.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (final NoSuchAlgorithmException e1) {
			return "Error: MD5 could not be found...";
		}
	}
	
	// Encode RFC 1521 MIME (Multipurpose Internet Mail Extensions) 
	// Part One. Rules #3, #4, and #5 of the quoted-printable spec are not implemented yet
	private static String encodeRfc1521(final String encodeText) {
		final QuotedPrintableCodec codec = new QuotedPrintableCodec();
		try {
			return codec.encode(encodeText);
		} catch (final EncoderException e) {
			return "Error: Sting input cannot be decoded";
		}
	}
	
	// Encode SHA-1 Hash
	private static String encodeSha1Hash(final String encodeText) {
		try {
			final MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			sha1.update(encodeText.getBytes(), 0, encodeText.length());
			final byte[] hash = sha1.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (final NoSuchAlgorithmException e1) {
			return "Error: SHA-1 could not be found...";
		}
	}
	
	// Encode SHA-256 Hash
	private static String encodeSha256Hash(final String encodeText) {
		try {
			final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			sha256.update(encodeText.getBytes(), 0, encodeText.length());
			final byte[] hash = sha256.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (final NoSuchAlgorithmException e1) {
			return "Error: SHA-256 could not be found...";
		}
	}
	
	// Encode SHA-384 Hash
	private static String encodeSha384Hash(final String encodeText) {
		try {
			final MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
			sha384.update(encodeText.getBytes(), 0, encodeText.length());
			final byte[] hash = sha384.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (final NoSuchAlgorithmException e1) {
			return "Error: SHA-384 could not be found...";
		}
	}
	
	// Encode SHA-512 Hash
	private static String encodeSha512Hash(final String encodeText) {
		try {
			final MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
			sha512.update(encodeText.getBytes(), 0, encodeText.length());
			final byte[] hash = sha512.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (final NoSuchAlgorithmException e1) {
			return "Error: SHA-512 could not be found...";
		}
	}
	
	// Encode www-form-url
	private static String encodeUrlCodec(final String encodeText) {
		final URLCodec codec = new URLCodec();
		try {
			return codec.encode(encodeText, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			return "Error: Sting input cannot be decoded";
		}
	}
	
	// URL Encode (UTF-16BE)
	private static String encodeUrlUtf16BE(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "UTF-16BE");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Encode (UTF-16LE)
	private static String encodeUrlUtf16LE(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "UTF-16LE");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Encode (UTF-8)
	private static String encodeUrlUtf8(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Encode (windows-1252)
	private static String encodeUrlWindows1252(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "windows-1252");
		} catch (final UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
}
