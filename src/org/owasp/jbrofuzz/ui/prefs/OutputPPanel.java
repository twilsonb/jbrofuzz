/**
 * JBroFuzz 1.9
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
package org.owasp.jbrofuzz.ui.prefs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

class OutputPPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = -8848755796748210431L;

	protected OutputPPanel(final PrefDialog dialog) {
		super("Fuzzing: Output");
		
		// Fuzzing: Output -> Double click opens up browser or panel
		final boolean fuzzingResponseDoubleClickBox = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[3], true);
		final JCheckBox fuzzingResponseDoubleClickCheckBox = new JCheckBox(
				" Double click on a Response opens it up in a Browser ",
				fuzzingResponseDoubleClickBox);

		fuzzingResponseDoubleClickCheckBox.setBorderPainted(false);
		fuzzingResponseDoubleClickCheckBox.setToolTipText(
		"Tick this box to open up response in a browser, instead of a text-based window");

		fuzzingResponseDoubleClickCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (fuzzingResponseDoubleClickCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[3], true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[3], false);
				}
			}
		});

		add(fuzzingResponseDoubleClickCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));

	}
	
	public void apply() { }
}
