/**
 * JBroFuzz 1.4
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
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.util.prefs.Preferences;

import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import java.awt.Dimension;
import javax.swing.text.StyledDocument;

/**
 *  trivial subclass of JTextPane that supports turning off word wrap.
 *  It will be extended to support other functionality when syntax
 *  highlighting is added.
 */
public class RequestTextPane extends JTextPane
{
    private boolean wrap=false;

    /**
     * constructs a TextEditorPane with a default document.
     */
    public RequestTextPane()
    {
	super();
    }

    /**
     * constructs a TextEditorPane with the specified document.
     * @param doc the document
     */
    public RequestTextPane(StyledDocument doc)
    {
	super(doc);
    }

    /**
     * sets word wrap on or off.
     * @param wrap whether the text editor pane should wrap or not
     */
    public void setWordWrap(boolean wrap)
    {
	this.wrap=wrap;
    }

    /**
     * returns whether the editor wraps text.
     * @return the value of the word wrap property
     */
    public boolean getWordWrap()
    {
	return this.wrap;
    }

    public boolean getScrollableTracksViewportWidth()
    {
	if (!wrap)
	{
	    Component parent=this.getParent();
	    ComponentUI ui=this.getUI();
	    int uiWidth=ui.getPreferredSize(this).width;
	    int parentWidth=parent.getSize().width;
	    boolean bool= (parent !=null)
		? (ui.getPreferredSize(this).width < parent.getSize().width)
		: true;	

	    return bool;
	}
	else return super.getScrollableTracksViewportWidth();
    }

    public void setBounds(int x, int y, int width, int height) 
    {
	if (wrap) 
	    super.setBounds(x, y, width, height);
	else
	{
	    Dimension size = this.getPreferredSize();
	    super.setBounds(x,y,Math.max(size.width, width),Math.max(size.height, height));
	}
    }
}
