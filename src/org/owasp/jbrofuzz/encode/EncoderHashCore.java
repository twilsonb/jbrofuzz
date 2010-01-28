package org.owasp.jbrofuzz.encode;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.jbrofuzz.util.B64;

public class EncoderHashCore {
	public static final String[] CODES = {
		"URL US-ASCII", "URL ISO-8859-1", "URL Cp1252", "URL UTF-8",
		"URL UTF-16BE", "URL UTF-16LE", "Base64", "MD5 Hash", "SHA-1 Hash", "SHA-256 Hash",
		"SHA-384 Hash", "SHA-512 Hash", "Hexadecimal (low)", "Hexadecimal (UPP)", 
		"Binary", "www-form-urlencoded", "RFC 1521 MIME (eMail)",
		"Escape: HTML", "Escape: CSV", "Escape: Java", "Escape: JavaScript",
		"Escape: SQL", "Escape: XML"
	};
	
	public final static String CAN_DECODED[] = {
			"URL US-ASCII", "URL ISO-8859-1", "URL Cp1252", "URL UTF-8",
			"URL UTF-16BE", "URL UTF-16LE", "Base64", "Hexadecimal (low)",
			"Hexadecimal (UPP)", "Binary", "www-form-urlencoded", "RFC 1521 MIME (eMail)",
			"Escape: HTML", "Escape: CSV", "Escape: Java", "Escape: JavaScript",
			"Escape: XML"
	};
	
	public static boolean canDecoded(final String type) {
		boolean flag = false;
		for (int i=0; i<CAN_DECODED.length; i++)
			if (type.equalsIgnoreCase(CAN_DECODED[i])) {
				flag = true;
				break;
			}
		return flag;
	}
	
	public static String encode(final String encodeText, final String type) {
		if (type.equalsIgnoreCase("URL US-ASCII"))
			return encodeUrlUsAscii(encodeText);
		else if (type.equalsIgnoreCase("URL ISO-8859-1"))
			return encodeUrlIso88591(encodeText);
		else if (type.equalsIgnoreCase("URL Cp1252"))
			return encodeUrlWindows1252(encodeText);
		else if (type.equalsIgnoreCase("URL UTF-8"))
			return encodeUrlUtf8(encodeText);
		else if (type.equalsIgnoreCase("URL UTF-16BE"))
			return encodeUrlUtf16BE(encodeText);
		else if (type.equalsIgnoreCase("URL UTF-16LE"))
			return encodeUrlUtf16BE(encodeText);
		else if (type.equalsIgnoreCase("Base64"))
			return encodeBase64(encodeText);
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
	
	public static String decode(final String decodeText, final String type) {
			if (!canDecoded(type))
				return "Error: String cannot be decoded...";
			else if (type.equalsIgnoreCase("URL US-ASCII"))
				return decodeUrlUsAscii(decodeText);
			else if (type.equalsIgnoreCase("URL ISO-8859-1"))
				return decodeUrlIso88591(decodeText);
			else if (type.equalsIgnoreCase("URL Cp1252"))
				return decodeUrlWindows1252(decodeText);
			else if (type.equalsIgnoreCase("URL UTF-8"))
				return decodeUrlUtf8(decodeText);
			else if (type.equalsIgnoreCase("URL UTF-16BE"))
				return decodeUrlUtf16BE(decodeText);
			else if (type.equalsIgnoreCase("URL UTF-16LE"))
				return decodeUrlUtf16BE(decodeText);
			else if (type.equalsIgnoreCase("Base64"))
				return decodeBase64(decodeText);
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
	
	// URL Decode (US-ASCII)
	public static String decodeUrlUsAscii(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}

	// URL Encode (US-ASCII)
	public static String encodeUrlUsAscii(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (ISO-8859-1)
	public static String decodeUrlIso88591(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}

	// URL Encode (ISO-8859-1)
	public static String encodeUrlIso88591(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (windows-1252)
	public static String decodeUrlWindows1252(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "windows-1252");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}

	// URL Encode (windows-1252)
	public static String encodeUrlWindows1252(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "windows-1252");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (UTF-8)
	public static String decodeUrlUtf8(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}

	// URL Encode (UTF-8)
	public static String encodeUrlUtf8(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (UTF-16BE)
	public static String decodeUrlUtf16BE(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}

	// URL Encode (UTF-16BE)
	public static String encodeUrlUtf16BE(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// URL Decode (UTF-16LE)
	public static String decodeUrlUtf16LE(final String decodeText) {
		try {
			return URLDecoder.decode(decodeText, "UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}

	// URL Encode (UTF-16LE)
	public static String encodeUrlUtf16LE(final String encodeText) {
		try {
			return URLEncoder.encode(encodeText, "UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			return "Error: String cannot be encoded...";
		}
	}
	
	// Decode Base64
	public static String decodeBase64(final String decodeText) {
		return B64.decodeString(decodeText);
	}
	
	// Encode Base64
	public static String encodeBase64(final String encodeText) {
		return B64.encodeString(encodeText);
	}
	
	// Encode MD5 Hash
	public static String encodeMd5Hash(final String encodeText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(encodeText.getBytes("iso-8859-1"), 0, encodeText.length());
			byte[] hash = new byte[32];
			hash = md.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (NoSuchAlgorithmException e1) {
			return "Error: MD5 could not be found...";
		} catch (UnsupportedEncodingException e2) {
			return "Error: MD5 String cannot be encoded...";
		}
	}
	
	// Encode SHA-1 Hash
	public static String encodeSha1Hash(final String encodeText) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(encodeText.getBytes("iso-8859-1"), 0, encodeText.length());
			byte[] hash = new byte[40];
			hash = md.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (NoSuchAlgorithmException e1) {
			return "Error: SHA-1 could not be found...";
		} catch (UnsupportedEncodingException e2) {
			return "Error: SHA-1 String cannot be encoded...";
		}
	}
	
	// Encode SHA-256 Hash
	public static String encodeSha256Hash(final String encodeText) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(encodeText.getBytes("iso-8859-1"), 0, encodeText.length());
			byte[] hash = new byte[64];
			hash = md.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (NoSuchAlgorithmException e1) {
			return "Error: SHA-256 could not be found...";
		} catch (UnsupportedEncodingException e2) {
			return "Error: SHA-256 String cannot be encoded...";
		}
	}

	// Encode SHA-384 Hash
	public static String encodeSha384Hash(final String encodeText) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-384");
			md.update(encodeText.getBytes("iso-8859-1"), 0, encodeText.length());
			byte[] hash = new byte[96];
			hash = md.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (NoSuchAlgorithmException e1) {
			return "Error: SHA-384 could not be found...";
		} catch (UnsupportedEncodingException e2) {
			return "Error: SHA-384 String cannot be encoded...";
		}
	}
	
	// Encode SHA-512 Hash
	public static String encodeSha512Hash(final String encodeText) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(encodeText.getBytes("iso-8859-1"), 0, encodeText.length());
			byte[] hash = new byte[128];
			hash = md.digest();
			return new String(Hex.encodeHex(hash)).toUpperCase();
		} catch (NoSuchAlgorithmException e1) {
			return "Error: SHA-512 could not be found...";
		} catch (UnsupportedEncodingException e2) {
			return "Error: SHA-512 String cannot be encoded...";
		}
	}

	// Decode Hexadecimal lowercase
	public static String decodeHexLow(final String decodeText) {
		try {
			return new String(Hex.decodeHex(decodeText.toCharArray()));
		} catch (DecoderException e) {
			return "Error: Hex value cannot be decoded...";
		}
	}
	
	// Encode Hexadecimal lowercase
	public static String encodeHexLow(final String encodeText) {
		try {
			return new String(Hex.encodeHex(encodeText.getBytes("iso-8859-1")));
		} catch (UnsupportedEncodingException e) {
			return "Error: String input cannot be encoded...";
		}
	}
	
	// Decode Hexadecimal uppercase
	public static String decodeHexUpp(final String decodeText) {
		return decodeHexLow(decodeText);
	}
	
	// Encode Hexadecimal uppercase
	public static String encodeHexUpp(final String encodeText) {
		return encodeHexLow(encodeText).toUpperCase();
	}
	
	// Decode Binary
	public static String decodeBinary(final String decodeText) {
		try {
			return new String(BinaryCodec.fromAscii(decodeText.getBytes("iso-8859-1")));
		} catch (UnsupportedEncodingException e) {
			return "Error: Binary value cannot be decoded...";
		} 
	}
	
	// Encode Binary
	public static String encodeBinary(final String encodeText) {
		try {
			return new String(BinaryCodec.toAsciiChars(encodeText.getBytes("iso-8859-1")));
		} catch (UnsupportedEncodingException e) {
			return "Error: Binary value cannot be decoded...";
		} 
	}
	
	// Decode www-form-url
	public static String decodeUrlCodec(final String decodeText) {
		URLCodec codec = new URLCodec();
		try {
			return codec.decode(decodeText, "UTF-8");
		} catch (DecoderException e) {
			return "Error: www-form-urlencoded value cannot be decoded...";
		} catch (UnsupportedEncodingException e) {
			return "Error: www-form-urlencoded value cannot be decoded...";
		} 
	}
	
	// Encode www-form-url
	public static String encodeUrlCodec(final String encodeText) {
		URLCodec codec = new URLCodec();
		try {
			return codec.encode(encodeText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "Error: Sting input cannot be decoded";
		}
	}
	
	// Decode RFC 1521 MIME (Multipurpose Internet Mail Extensions) 
	// Part One. Rules #3, #4, and #5 of the quoted-printable spec are not implemented yet
	public static String decodeRfc1521(final String decodeText) {
		QuotedPrintableCodec codec = new QuotedPrintableCodec();
		try {
			return codec.decode(decodeText);
		} catch (DecoderException e) {
			return "Error: RFC 1521 MIME value cannot be decoded...";
		}
	}
	
	// Encode RFC 1521 MIME (Multipurpose Internet Mail Extensions) 
	// Part One. Rules #3, #4, and #5 of the quoted-printable spec are not implemented yet
	public static String encodeRfc1521(final String encodeText) {
		QuotedPrintableCodec codec = new QuotedPrintableCodec();
		try {
			return codec.encode(encodeText);
		} catch (EncoderException e) {
			return "Error: Sting input cannot be decoded";
		}
	}
	
	// HTML Encode
	public static String encodeEscHtml(final String encodeText) {
		return StringEscapeUtils.escapeHtml(encodeText);
	}
	
	// HTML Decode
	public static String decodeEscHtml(final String decodeText) {
		return StringEscapeUtils.unescapeHtml(decodeText);
	}
	
	// CSV Encode
	public static String encodeEscCsv(final String encodeText) {
		return StringEscapeUtils.escapeCsv(encodeText);
	}
	
	// CSV Decode
	public static String decodeEscCsv(final String decodeText) {
		return StringEscapeUtils.unescapeCsv(decodeText);
	}
	
	// Java Encode
	public static String encodeEscJava(final String encodeText) {
		return StringEscapeUtils.escapeJava(encodeText);
	}
	
	// Java Decode
	public static String decodeEscJava(final String decodeText) {
		return StringEscapeUtils.unescapeJava(decodeText);
	}
	
	// JavaScript Encode
	public static String encodeEscJavaScript(final String encodeText) {
		return StringEscapeUtils.escapeJavaScript(encodeText);
	}
	
	// JavaScript Decode
	public static String decodeEscJavaScript(final String decodeText) {
		return StringEscapeUtils.unescapeJavaScript(decodeText);
	}
	
	// SQL Encode
	public static String encodeEscSql(final String encodeText) {
		return StringEscapeUtils.escapeSql(encodeText);
	}
	
	// XML Encode
	public static String encodeEscXml(final String encodeText) {
		return StringEscapeUtils.escapeXml(encodeText);
	}
	
	// XML Decode
	public static String decodeEscXml(final String decodeText) {
		return StringEscapeUtils.unescapeXml(decodeText);
	}
}
