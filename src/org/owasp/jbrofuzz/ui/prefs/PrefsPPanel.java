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

class PrefsPPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = -8670706526863295929L;

	public PrefsPPanel(final PrefDialog dialog) {
		super("Preferences");
		
		// Preferences -> Show Tabs

		final boolean tabsbottom = JBroFuzz.PREFS.getBoolean("UI.JBroFuzz.Tabs", false);
		final JCheckBox tabsCheckBox = new JCheckBox(
				" Show tabs in the main window at the top of the window (requires restart) ",
				tabsbottom);

		tabsCheckBox.setBorderPaintedFlat(true);
		tabsCheckBox
		.setToolTipText(" Tick this option, if you would like to see the tabs under " +
		"the tool bar, instead of at the bottom of the window ");

		tabsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (tabsCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean("UI.JBroFuzz.Tabs", true);
				} else {
					JBroFuzz.PREFS.putBoolean("UI.JBroFuzz.Tabs", false);
				}
			}
		});
		add(tabsCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Preferences -> Check for New Version at Startup

		final boolean newVersionCheck = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.GENERAL[0],
				true);
		final JCheckBox newVCheckBox = new JCheckBox(
				" Check for a new version at startup ", newVersionCheck);

		newVCheckBox.setBorderPaintedFlat(true);
		newVCheckBox
		.setToolTipText(" Untick this option, if you do not want to be notified about new versions at startup ");

		newVCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (newVCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.GENERAL[0], true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.GENERAL[0], false);
				}
			}
		});
		add(newVCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));
	}

	public void apply() { }
}
