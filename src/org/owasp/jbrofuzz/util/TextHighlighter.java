/**
 * JBroFuzz 1.3
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

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Syntax Highlighting Test using a JTextPane
 * 
 * By: Frank Hale <frankhale@gmail.com> Date: 12 November 2006
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * A full copy of this license is at: http://www.gnu.org/licenses/gpl.txt
 * 
 */
public class TextHighlighter extends DefaultStyledDocument {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 3812224127502539817L;

	private HashMap<String, Color>	keywords;
	private MutableAttributeSet			style;

	/**
	 * Default Constructor for the TextHighlighter, extending the DefaultStyled
	 * Document.
	 */
	public TextHighlighter() {

		keywords = new HashMap<String, Color>();
		keywords.put("GET", Color.darkGray);
		keywords.put("PUT", Color.black);
		keywords.put("POST", Color.black);
		keywords.put("HEAD", Color.black);
		keywords.put("TRACE", Color.black);
		keywords.put("HTTP/\\d.\\d", Color.black);

		keywords.put("Windows", Color.gray);
		keywords.put("Windows NT \\d.\\d", Color.gray);
		keywords.put("Mozilla/\\d.\\d", Color.gray);
		keywords.put("Firefox", Color.gray);
		keywords.put("Internet", Color.gray);
		keywords.put("Gecko", Color.gray);
		keywords.put("keep-alive", Color.gray);
		keywords.put("localhost", Color.gray);

		keywords.put("ISO-[0-9]{4}-[0-9]", Color.gray);
		keywords.put("[-\\w]+/[\\w\\.\\+]+", Color.gray);
		keywords.put("utf-8", Color.gray);

		keywords.put("[\\d\\.]+", Color.orange);

		keywords.put("[\\w]+=", Color.magenta);

		keywords.put("&", Color.red);

		keywords.put("Accept", Color.blue);
		keywords.put("Accept-Charset", Color.blue);
		keywords.put("Accept-Encoding", Color.blue);
		keywords.put("Accept-Language", Color.blue);
		keywords.put("Age", Color.blue);
		keywords.put("Cache-control", Color.blue);
		keywords.put("Cache-Control", Color.blue);
		keywords.put("Connection", Color.blue);
		keywords.put("Content-Encoding", Color.blue);
		keywords.put("Content-encoding", Color.blue);
		keywords.put("Content-language", Color.blue);
		keywords.put("Content-length", Color.blue);
		keywords.put("Content-Length", Color.blue);
		keywords.put("Content-type", Color.blue);
		keywords.put("Content-Type", Color.blue);
		keywords.put("Cookie", Color.blue);
		keywords.put("Date", Color.blue);
		keywords.put("ETag", Color.blue);
		keywords.put("Expires", Color.blue);
		keywords.put("Host", Color.blue);
		keywords.put("Keep-Alive", Color.blue);
		keywords.put("Last-Modified", Color.blue);
		keywords.put("Last-modified", Color.blue);
		keywords.put("Location", Color.blue);
		keywords.put("Mime-Version", Color.blue);
		keywords.put("P3P", Color.blue);
		keywords.put("P3p", Color.blue);
		keywords.put("Pragma", Color.blue);
		keywords.put("Proxy-Agent", Color.blue);
		keywords.put("Proxy-agent", Color.blue);
		keywords.put("Proxy-Connection", Color.blue);
		keywords.put("Referer", Color.blue);
		keywords.put("Server", Color.blue);
		keywords.put("Set-Cookie", Color.blue);
		keywords.put("Set-cookie", Color.blue);
		keywords.put("Transfer-Encoding", Color.blue);
		keywords.put("UA-CPU", Color.blue);
		keywords.put("UA-color", Color.blue);
		keywords.put("UA-pixel", Color.blue);
		keywords.put("UA-OS", Color.blue);
		keywords.put("User-Agent", Color.blue);
		keywords.put("Vary", Color.blue);
		keywords.put("Via", Color.blue);
		keywords.put("X-AspNet-Version", Color.blue);
		keywords.put("X-Cache-TTL", Color.blue);
		keywords.put("X-Cached-Time", Color.blue);
		keywords.put("X-PHP-Load", Color.blue);
		keywords.put("X-Powered-By", Color.blue);
		keywords.put("X-Powered-by", Color.blue);
		keywords.put("X-powered-by", Color.blue);
		keywords.put("X-Requested-With", Color.blue);
		keywords.put("X-SDCH", Color.blue);
		keywords.put("x-ps3-browser", Color.blue);
		keywords.put("x-wap-profile", Color.blue);

		style = new SimpleAttributeSet();
	}

	private void highlightString(final Color col, final int begin,
			final int length, final boolean flag, final boolean bold) {

		StyleConstants.setForeground(style, col);
		StyleConstants.setBold(style, bold);
		setCharacterAttributes(begin, length, style, flag);

	}

	@Override
	public void insertString(final int offset, final String str,
			final AttributeSet attr) throws BadLocationException {
		super.insertString(offset, str, attr);
		processChangedLines(offset, str.length());
	}

	private void processChangedLines(final int offset, final int length)
			throws BadLocationException {
		final String text = this.getText(0, getLength());
		highlightString(Color.black, 0, getLength(), true, false);

		final Set<String> keyw = keywords.keySet();
		for (final String keyword : keyw) {
			final Color col = keywords.get(keyword);

			final Pattern p = Pattern.compile("\\b" + keyword + "\\b");
			final Matcher m = p.matcher(text);

			while (m.find()) {
				highlightString(col, m.start(), m.group().length(), true, true);
			}
		}

	}

	@Override
	public void remove(final int offset, final int length)
			throws BadLocationException {
		super.remove(offset, length);
		processChangedLines(offset, length);
	}
}