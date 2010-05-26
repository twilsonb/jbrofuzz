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
package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyledDocument;

import org.owasp.jbrofuzz.system.Logger;

public class WireTextArea extends JTextArea {

	private static final long serialVersionUID = 2094145846515459112L;

	public WireTextArea() {
		super();

	}

	@SuppressWarnings("unused")
	private WireTextArea(StyledDocument doc) {
		super(doc);
	}

	public Font getFont() {
		return new Font("Verdana", Font.PLAIN, 10);
	}

	public  boolean isEditable() {
		return false;
	}

	public Color getBackground() {
		return Color.BLACK;
	}

	public Color getForeground() {
		return Color.GREEN;
	}

	protected Document createDefaultModel() {

		return new PlainDocument() {

			private static final long serialVersionUID = 7378012766038129491L;

			// Bytes on the BBC homepage on the 8th of May 2010
			// 0; // Byte.MAX_VALUE; // Character.MAX_VALUE; // 65535; // 133323; // Character.MAX_VALUE;
			private static final int MAX_VALUE = Character.MAX_VALUE;

			public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {

				final int overflow = offs + str.length() - MAX_VALUE;

				if(overflow > 0) {

					super.insertString(offs, str.substring( MAX_VALUE - offs ), a);

				} else {

					super.insertString(offs, str, a);

				}

			}

		};
	}

	public void setText(final String t) {
		
		try {
			
			final Document doc = getDocument();
			doc.remove(0, doc.getLength());
			doc.insertString(0, t, null);
			// Set the caret position
			this.setCaretPosition(doc.getLength());
			
		} catch (BadLocationException e) {
			Logger.log("WireTextArea: Bad Location Exception", 3);
		}
		
	}


}
