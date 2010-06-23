/**
 * JBroFuzz 2.3
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>Class for displaying the preferences, under Ctrl+P,
 * of "Fuzzing: On the Wire".</p>
 * 
 * @author subere@uncon.org
 * @version 2.2
 * @since 2.0
 *
 */
class WirePPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = -6794971039096994742L;

	// The array of check boxes used from the FUZZINGONTHEWIRE preferences
	private final JCheckBox [] checkBoxes = new JCheckBox[JBroFuzzPrefs.FUZZINGONTHEWIRE.length];
	
	// The static array of radio buttons of "what to show on the wire" names
	private final static String [] RADIONAMES = 
								{"Nothing", "Requests", "Responses", "Both" };
	// The array of radio buttons for what to display on the wire
	private final JRadioButton [] rButton1 = new JRadioButton[RADIONAMES.length];

	protected WirePPanel(final PrefDialog dialog) {
		
		super("Fuzzing: On the Wire");
		
		for(int i = 0; i < JBroFuzzPrefs.FUZZINGONTHEWIRE.length; i++) {

			// Skip the radio button
			if(i == 1) {
				continue;
			}
			
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
		
		// What to put on the wire (FUZZINGONTHEWIRE[1] skipped above)
		final ButtonGroup group = new ButtonGroup();
		
		final JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
				25, 0));
		// radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.LINE_AXIS));
		// A very important line when it comes to BoxLayout
		radioPanel.setAlignmentX(0.0f);
		radioPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getTitle()), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));
		radioPanel.setToolTipText(JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getTooltip());
		
		// Get the default value
		final int showOnTheWire = JBroFuzz.PREFS.getInt(
									JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getId(), 3);
		
		for(int j = 0; j < RADIONAMES.length; j++) {
			
			rButton1[j] = new JRadioButton(RADIONAMES[j]);
			group.add(rButton1[j]);
			
			rButton1[j].addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					dialog.setApplyEnabled(true);
					
				}
			});
			
			radioPanel.add(rButton1[j]);
			
			// Set the selected value
			if(showOnTheWire == j) {
				
				rButton1[j].setSelected(true);
				
			}
			
		}

		
		add(radioPanel);
		add(Box.createRigidArea(new Dimension(0, 300)));


	}
	
	@Override
	public void apply() {
		
		for(int i = 0; i < checkBoxes.length; i++) {
		
			// Skip the radio button
			if(i == 1) {
				continue;
			}
			
			JBroFuzz.PREFS.putBoolean(
					JBroFuzzPrefs.FUZZINGONTHEWIRE[i].getId(), 
					checkBoxes[i].isSelected()
			);
		}
		
		// Apply the radio button on what to display on the wire
		for(int j = 0; j < rButton1.length; j++) {
			
			if(rButton1[j].isSelected()) {
				
				JBroFuzz.PREFS.putInt(
						JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getId(),
						j );
			}
			
		}
		
	} 
}
