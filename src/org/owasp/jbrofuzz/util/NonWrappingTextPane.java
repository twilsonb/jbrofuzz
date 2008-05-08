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

import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;

// Because Swing's JTextPane is retarded by default and doesn't allow you to
// just flip a switch and turn off the FUCKING line-wrapping
//
// This overridden method was coded by somebody much smarter than I, somebody
// probably with an EGO the size of SUN MICROSYSTEMS.
//
// At SUN our motto is "Over-engineer everything so that nobody understands
// it..."
public class NonWrappingTextPane extends JTextPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8295506229408494512L;

	// The method below is coutesy of Core Swing Advanced Programming by Kim
	// Topley
	//
	// Override getScrollableTracksViewportWidth
	// to preserve the full width of the text
	@Override
	public boolean getScrollableTracksViewportWidth() {
		final Component parent = getParent();
		final ComponentUI ui = getUI();

		return parent != null ? (ui.getPreferredSize(this).width <= parent
				.getSize().width) : true;
	}
}
