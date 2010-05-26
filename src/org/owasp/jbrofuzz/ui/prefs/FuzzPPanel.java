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
package org.owasp.jbrofuzz.ui.prefs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

class FuzzPPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = -6307578507750425289L;

	// Declare any static constants
	private static final String[] SOCKET_SECONDS = 
	{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
		"31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
		"41", "42", "43", "44", "45", "46", "47", "48", "49", "50" };

	// The Socket Timeout Combo Box
	private final JComboBox stoBox;
	// The check boxes, excluding the 
	private final JCheckBox [] checkBoxes = new JCheckBox[JBroFuzzPrefs.FUZZING.length - 1];

	public FuzzPPanel(final PrefDialog dialog) {

		super("Fuzzing");

		// Fuzzing... -> Socket Timeout

		int stoPrefValue = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.FUZZING[0].getId(), 7);
		// Validate
		if( (stoPrefValue < 1) || (stoPrefValue > 51) ) {
			stoPrefValue = 7;
		}

		stoBox = new JComboBox(SOCKET_SECONDS);
		stoBox.setSelectedIndex(stoPrefValue - 1);
		stoBox.setMaximumRowCount(6);

		// Re-enable the apply button in the event of a change
		stoBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent stoEvent) {
				dialog.setApplyEnabled(true);
			}
		});

		final JLabel stoLabel = new JLabel(JBroFuzzPrefs.FUZZING[0].getTitle());
		stoLabel.setToolTipText(JBroFuzzPrefs.FUZZING[0].getTooltip());

		final JPanel sTimeOutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		// A very important line when it comes to BoxLayout
		sTimeOutPanel.setAlignmentX(0.0f);
		sTimeOutPanel.add(stoLabel);
		sTimeOutPanel.add(stoBox);

		add(sTimeOutPanel);
		add(Box.createRigidArea(new Dimension(0, 20)));

		for(int i = 0; i < checkBoxes.length; i++) {

			final boolean boolEntry = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[i + 1].getId(), true);
			checkBoxes[i] = new JCheckBox(JBroFuzzPrefs.FUZZING[i + 1].getTitle(), boolEntry);
			checkBoxes[i].setToolTipText(JBroFuzzPrefs.FUZZING[i + 1].getTooltip());
			checkBoxes[i].setBorderPaintedFlat(true);
			checkBoxes[i].addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					dialog.setApplyEnabled(true);

				}
			});
			add(checkBoxes[i]);
			add(Box.createRigidArea(new Dimension(0, 20)));
		}

		add(Box.createRigidArea(new Dimension(0, 200)));



	}

	public void apply() { 

		// Fuzzing... -> Socket Timeout
		JBroFuzz.PREFS.putInt(JBroFuzzPrefs.FUZZING[0].getId(), stoBox.getSelectedIndex() + 1);
		// The rest
		for(int i = 0; i < checkBoxes.length; i++) {
			JBroFuzz.PREFS.putBoolean(
					JBroFuzzPrefs.FUZZING[i+1].getId(), 
					checkBoxes[i].isSelected()
			);
		}

	}

}
