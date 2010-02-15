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

class WirePPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = 4718844109726649392L;

	protected WirePPanel(final PrefDialog dialog) {
		super("Fuzzing: On the Wire");
		
		// Fuzzing: On The Wire... -> Show on the wire tab after fuzzing finished
		final boolean showwirebox = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[0],
				false);
		final JCheckBox showwireCheckBox = new JCheckBox(
				" Show \"On The Wire\" tab after fuzzing has stopped or finished ",
				showwirebox);

		showwireCheckBox.setBorderPaintedFlat(true);
		showwireCheckBox
		.setToolTipText("Tick this box, if you want to always see the \"On The Wire\" tab");

		showwireCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent swEvent) {
				if (showwireCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[0], true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[0], false);
				}
			}
		});

		add(showwireCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing: On The Wire... -> Display responses inside the On The Wire text area
		final boolean dispBoolean = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[1], false);
		final JCheckBox diskRespBox = new JCheckBox(
				" Display the Requests as well as the Responses received ",
				dispBoolean);

		diskRespBox.setBorderPaintedFlat(true);
		diskRespBox.setToolTipText(
		"Tick this box to display the responses received for each request sent within the \"On The Wire\" tab");

		diskRespBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent disEvent) {
				if (diskRespBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[1], true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.FUZZING[1], false);
				}
			}
		});

		add(diskRespBox);
		add(Box.createRigidArea(new Dimension(0, 20)));


	}
	
	public void apply() {} 
}
