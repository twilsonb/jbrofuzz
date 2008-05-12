/**
 * JBroFuzz 0.9
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007, 2008 subere@uncon.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.owasp.jbrofuzz.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.*;

import javax.swing.text.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/*
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
	private static final long serialVersionUID = -7741580168036778852L;
	// private Element rootElement;
	private HashMap<String, Color> keywords;
	private MutableAttributeSet style;

	// private Color commentColor = Color.cyan;
	// private Pattern singleLineCommentDelimter = Pattern.compile("//");
	// private Pattern multiLineCommentDelimiterStart = Pattern.compile("/\\*");
	// private Pattern multiLineCommentDelimiterEnd = Pattern.compile("\\*/");

	/**
	 * Default Constructor for the TextHighlighter, extending the DefaultStyled
	 * Document.
	 */
	public TextHighlighter() {

		// this.putProperty( DefaultEditorKit.EndOfLineStringProperty, "\n" );

		// rootElement = getDefaultRootElement();

		keywords = new HashMap<String, Color>();
		keywords.put("GET", Color.gray);
		keywords.put("PUT", Color.gray);
		keywords.put("HEAD", Color.gray);
		keywords.put("TRACE", Color.gray);

		keywords.put("Accept", Color.blue);
		keywords.put("User-Agent", Color.blue);
		keywords.put("Accept-Language", Color.blue);
		keywords.put("Accept-Encoding", Color.blue);
		keywords.put("Accept-Charset", Color.blue);
		keywords.put("Keep-Alive", Color.blue);
		keywords.put("Cache-Control", Color.blue);
		keywords.put("Content-Type", Color.blue);
		keywords.put("Set-Cookie", Color.blue);
		keywords.put("Server", Color.blue);
		keywords.put("Date", Color.blue);
		keywords.put("Host", Color.blue);
		keywords.put("Connection", Color.blue);
		keywords.put("Cookie", Color.blue);
		keywords.put("Content-Length", Color.blue);
		keywords.put("Content-type", Color.blue);
		keywords.put("Location", Color.blue);
		keywords.put("Referer", Color.blue);
		keywords.put("Cache-control", Color.blue);
		keywords.put("Proxy-Connection", Color.blue);
		keywords.put("Pragma", Color.blue);
		keywords.put("Expires", Color.blue);
		keywords.put("Proxy-Connection", Color.blue);
		keywords.put("X-AspNet-Version", Color.blue);
		keywords.put("P3P", Color.blue);
		keywords.put("P3p", Color.blue);
		keywords.put("X-Powered-By", Color.blue);
		keywords.put("X-Powered-by", Color.blue);
		keywords.put("X-Cache-TTL", Color.blue);
		keywords.put("X-Cached-Time", Color.blue);
		keywords.put("Age", Color.blue);
		keywords.put("Mime-Version", Color.blue);
		keywords.put("Last-Modified", Color.blue);
		keywords.put("Last-modified", Color.blue);
		keywords.put("Content-length", Color.blue);
		keywords.put("ETag", Color.blue);
		
		keywords.put("Windows", Color.gray);
		keywords.put("Mozilla", Color.gray);
		keywords.put("Firefox", Color.gray);
		keywords.put("Internet", Color.gray);

		/*
		keywords.put("0", Color.blue);
		keywords.put("1", Color.blue);
		keywords.put("2", Color.blue);
		keywords.put("3", Color.blue);
		keywords.put("4", Color.blue);
		keywords.put("5", Color.blue);
		keywords.put("6", Color.blue);
		keywords.put("7", Color.blue);
		keywords.put("8", Color.blue);
		keywords.put("9", Color.blue);
		*/
		keywords.put("html", Color.magenta);
		keywords.put("head", Color.magenta);
		keywords.put("body", Color.magenta);
		keywords.put("form", Color.magenta);
		keywords.put("title", Color.magenta);
		keywords.put("style", Color.magenta);
		keywords.put("type", Color.magenta);
		keywords.put("table", Color.magenta);
		keywords.put("tr", Color.magenta);
		keywords.put("td", Color.magenta);
		keywords.put("li", Color.magenta);
		keywords.put("ul", Color.magenta);
		keywords.put("code", Color.magenta);

		style = new SimpleAttributeSet();
	}

	public void highlightString(final Color col, final int begin,
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

	public void processChangedLines(final int offset, final int length)
			throws BadLocationException {
		final String text = this.getText(0, getLength());
		highlightString(Color.black, 0, getLength(), true, false);

		final Set<String> keyw = keywords.keySet();
		for (final String keyword : keyw) {
			final Color col = keywords.get(keyword);

			final Pattern p = Pattern.compile("\\b" + keyword + "\\b");
			final Matcher m = p.matcher(text);

			while (m.find()) {
				highlightString(col, m.start(), keyword.length(), true, true);
			}
		}
		/*
		 * Matcher mlcStart = multiLineCommentDelimiterStart.matcher(text); Matcher
		 * mlcEnd = multiLineCommentDelimiterEnd.matcher(text);
		 * 
		 * while(mlcStart.find()) {
		 * 
		 * if(mlcEnd.find( mlcStart.end() )) { highlightString(commentColor,
		 * mlcStart.start(), (mlcEnd.end()-mlcStart.start()), true, true); } else {
		 * highlightString(commentColor, mlcStart.start(), getLength(), true, true); } }
		 * 
		 * Matcher slc = singleLineCommentDelimter.matcher(text);
		 * 
		 * while(slc.find()) { int line = rootElement.getElementIndex(slc.start());
		 * int endOffset = rootElement.getElement(line).getEndOffset() - 1;
		 * 
		 * highlightString(commentColor, slc.start(), (endOffset-slc.start()), true,
		 * true); }
		 */

	}

	@Override
	public void remove(final int offset, final int length)
			throws BadLocationException {
		super.remove(offset, length);
		processChangedLines(offset, length);
	}
}