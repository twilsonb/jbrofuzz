/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

public class CutAction extends TextAction {

	private static final long serialVersionUID = -4536111278468156391L;

	public CutAction() {
		super("Cut");
	}

	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JTextComponent text = CutAction.this
				.getTextComponent(evt);
				if (text != null) {
					text.cut();
					text.requestFocus();
				}
			}
		});
	}
}