/**
 * JbroFuzz 2.5
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
package org.owasp.jbrofuzz.util;

import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;

/**
 * <p>
 * The implementation of a text pane that does not wrap the text, preserving its
 * full width.
 * </p>
 * 
 * <p>
 * The overriding method was inspired from the book Core Swing Advanced
 * Programming by Kim Topley, where a similar implementation resides.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class NonWrappingTextPane extends JTextPane {

	private static final long serialVersionUID = -6032784558789430963L;

	/**
	 * <p>
	 * This method overrides #getScrollableTracksViewportWidth to preserve the
	 * full width of the text displayed within the text pane.
	 * </p>
	 * 
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {

		final Component parent = getParent();
		final ComponentUI mComponentUI = getUI();

		if (parent != null) {
			return (mComponentUI.getPreferredSize(this).width <= parent.getSize().width);
		}

		return true;

	}
}
