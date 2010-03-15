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

	private final JCheckBox [] checkBoxes = new JCheckBox[JBroFuzzPrefs.FUZZINGONTHEWIRE.length];

	protected WirePPanel(final PrefDialog dialog) {
		
		super("Fuzzing: On the Wire");
		
		for(int i = 0; i < JBroFuzzPrefs.FUZZINGONTHEWIRE.length; i++) {
			
			final boolean boolEntry = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZINGONTHEWIRE[i].getId(), true);
			checkBoxes[i] = new JCheckBox(JBroFuzzPrefs.FUZZINGONTHEWIRE[i].getTitle(), boolEntry);
			checkBoxes[i].setToolTipText(JBroFuzzPrefs.FUZZINGONTHEWIRE[i].getTooltip());
			checkBoxes[i].setBorderPaintedFlat(true);
			checkBoxes[i].addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					dialog.setApplyEnabled(true);
					
				}
			});
			add(checkBoxes[i]);
			add(Box.createRigidArea(new Dimension(0, 20)));
		}


	}
	
	public void apply() {
		
		for(int i = 0; i < checkBoxes.length; i++) {
			JBroFuzz.PREFS.putBoolean(
					JBroFuzzPrefs.FUZZINGONTHEWIRE[i].getId(), 
					checkBoxes[i].isSelected()
			);
		}
		
	} 
}
