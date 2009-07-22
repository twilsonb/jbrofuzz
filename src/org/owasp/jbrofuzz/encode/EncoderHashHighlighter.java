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
package org.owasp.jbrofuzz.encode;

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
public class EncoderHashHighlighter extends DefaultStyledDocument {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Color> keywords;
	private MutableAttributeSet style;

	/**
	 * Default Constructor for the TextHighlighter, extending the DefaultStyled
	 * Document.
	 */
	public EncoderHashHighlighter() {

		keywords = new HashMap<String, Color>();
		

		final Color lightBlue = new Color(51, 102, 102);

		keywords.put("0", lightBlue);
		keywords.put("1", lightBlue);
		keywords.put("2", lightBlue);
		keywords.put("3", lightBlue);
		keywords.put("4", lightBlue);
		keywords.put("5", lightBlue);
		keywords.put("6", lightBlue);
		keywords.put("7", lightBlue);
		keywords.put("8", lightBlue);
		keywords.put("9", lightBlue);
		
		keywords.put("a", Color.blue);
		keywords.put("b", Color.blue);
		keywords.put("c", Color.blue);
		keywords.put("d", Color.blue);
		keywords.put("e", Color.blue);
		keywords.put("f", Color.blue);

		keywords.put("A", Color.blue);
		keywords.put("B", Color.blue);
		keywords.put("C", Color.blue);
		keywords.put("D", Color.blue);
		keywords.put("E", Color.blue);
		keywords.put("F", Color.blue);

		final Color keramidi = new Color(204, 51, 0);

		keywords.put("=", keramidi);
		keywords.put("+", keramidi);
		keywords.put("/", keramidi);

		keywords.put("[\\d\\.]+", Color.orange);
		keywords.put("[\\w]+=", Color.magenta);

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