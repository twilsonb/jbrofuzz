/**
 * 
 */
package org.owasp.jbrofuzz.ui.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
/*
 * Syntax Highlighting Test using a JTextPane
 *
 * By: Frank Hale <frankhale@gmail.com>
 * Date: 12 November 2006
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A full copy of this license is at: http://www.gnu.org/licenses/gpl.txt
 *  
 */
public class TextHighlighter extends DefaultStyledDocument	{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7741580168036778852L;
	//private Element rootElement;
	private HashMap<String,Color> keywords;
	private MutableAttributeSet style;

	// private Color commentColor = Color.cyan;
	// private Pattern singleLineCommentDelimter = Pattern.compile("//");
	// private Pattern multiLineCommentDelimiterStart = Pattern.compile("/\\*");
	// private Pattern multiLineCommentDelimiterEnd = Pattern.compile("\\*/");

	/**
	 * Default Constructor for the TextHighlighter, extending the DefaultStyled Document.
	 */
	public TextHighlighter() {

		this.putProperty( DefaultEditorKit.EndOfLineStringProperty, "\r\n" );

		//rootElement = getDefaultRootElement();

		this.keywords = new HashMap<String, Color>();
		this.keywords.put( "GET", Color.blue);
		this.keywords.put( "PUT", Color.blue);
		this.keywords.put( "HEAD", Color.blue);

		this.keywords.put("Accept", Color.darkGray);
		this.keywords.put("User\\-Agent", Color.darkGray);
		this.keywords.put("Accept\\-Language", Color.darkGray);
		this.keywords.put("Accept\\-Encoding", Color.darkGray);
		this.keywords.put("Accept\\-Charset", Color.darkGray);
		this.keywords.put("Keep\\-Alive", Color.darkGray);
		this.keywords.put("Connection", Color.darkGray);

		this.keywords.put( "Windows", Color.gray);
		this.keywords.put( "Mozilla", Color.gray);
		this.keywords.put( "Firefox", Color.gray);
		this.keywords.put( "Internet", Color.gray);

		this.keywords.put( "0", Color.blue);
		this.keywords.put( "1", Color.blue);
		this.keywords.put( "2", Color.blue);
		this.keywords.put( "3", Color.blue);
		this.keywords.put( "4", Color.blue);
		this.keywords.put( "5", Color.blue);
		this.keywords.put( "6", Color.blue);
		this.keywords.put( "7", Color.blue);
		this.keywords.put( "8", Color.blue);
		this.keywords.put( "9", Color.blue);
		this.keywords.put( ".", Color.blue);
		this.keywords.put( "/", Color.blue);

		this.style = new SimpleAttributeSet();
	}

	@Override
	public void insertString(final int offset, final String str, final AttributeSet attr) throws BadLocationException	{
		super.insertString(offset, str, attr);
		this.processChangedLines(offset, str.length());
	}

	@Override
	public void remove(final int offset, final int length) throws BadLocationException {
		super.remove(offset, length);
		this.processChangedLines(offset, length);
	}

	public void processChangedLines(final int offset, final int length) throws BadLocationException		{
		final String text = this.getText(0, this.getLength());
		this.highlightString(Color.black, 0, this.getLength(), true, false);

		final Set<String> keyw = this.keywords.keySet();
		for (final String keyword : keyw) {
			final Color col = this.keywords.get(keyword);

			final Pattern p = Pattern.compile("\\b" + keyword + "\\b");
			final Matcher m = p.matcher(text);

			while(m.find()) {
				this.highlightString(col, m.start(), keyword.length(), true, true);
			}
		}
/*
		Matcher mlcStart = multiLineCommentDelimiterStart.matcher(text);
		Matcher mlcEnd = multiLineCommentDelimiterEnd.matcher(text);

		while(mlcStart.find()) {

			if(mlcEnd.find( mlcStart.end() )) {
				highlightString(commentColor, mlcStart.start(), (mlcEnd.end()-mlcStart.start()), true, true);
			}
			else {
				highlightString(commentColor, mlcStart.start(), getLength(), true, true);
			}
		}

		Matcher slc = singleLineCommentDelimter.matcher(text);

		while(slc.find()) {
			int line = rootElement.getElementIndex(slc.start());
			int endOffset = rootElement.getElement(line).getEndOffset() - 1;

			highlightString(commentColor, slc.start(), (endOffset-slc.start()), true, true);
		}
*/
	}

	public void highlightString(final Color col, final int begin, final int length, final boolean flag, final boolean bold) {
		
		StyleConstants.setForeground(this.style, col);
		StyleConstants.setBold(this.style, bold);
		this.setCharacterAttributes(begin, length, this.style, flag);
		
	}
}