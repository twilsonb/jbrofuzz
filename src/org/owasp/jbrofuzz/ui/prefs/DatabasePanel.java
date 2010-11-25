/**
 * JBroFuzz 2.5
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * Configure the DB to connection to for storing results - none if not
 * configured.
 * 
 * @author daemonmidi@gmail.com
 * @version 2.5
 * @since version 2.5
 */
public class DatabasePanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = 6601607213870707446L;

	private final JCheckBox proxyEnabledBox, proxyReqAuthBox;

	private final JTextField hostTextField, portTextField, userTextField,
			passTextField, dbUserTextField, dbPassTextField, dbHostTextField,
			dbPortTextField, dbNameTextField;

	private final JComboBox authTypeBox;

	private final JComboBox dbTypeBox;

	private static final Dimension V_SPACE = new Dimension(0, 20);

	protected DatabasePanel(final PrefDialog dialog) {

		super("Database Configuration");

		// Tick box for selecting your own directory
		final boolean proxyEnabled = JBroFuzz.PREFS.getBoolean(
				JBroFuzzPrefs.DBSETTINGS[0].getId(), false);
		proxyEnabledBox = new JCheckBox(JBroFuzzPrefs.DBSETTINGS[0].getTitle(),
				proxyEnabled);
		proxyEnabledBox
				.setToolTipText(JBroFuzzPrefs.DBSETTINGS[0].getTooltip());
		proxyEnabledBox.setBorderPaintedFlat(true);

		// Host text & preference
		final String hostEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[1].getId(), "");
		hostTextField = new JTextField(hostEntry);
		hostTextField.setToolTipText(JBroFuzzPrefs.DBSETTINGS[1].getTooltip());
		hostTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		hostTextField.setMargin(new Insets(1, 1, 1, 1));

		// Port text & preference
		final String portEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[2].getId(), "");
		portTextField = new JTextField(portEntry);
		portTextField.setToolTipText(JBroFuzzPrefs.DBSETTINGS[2].getTooltip());
		portTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		portTextField.setMargin(new Insets(1, 1, 1, 1));

		// Proxy requires authentication
		final boolean proxyReqAuth = JBroFuzz.PREFS.getBoolean(
				JBroFuzzPrefs.DBSETTINGS[3].getId(), false);
		proxyReqAuthBox = new JCheckBox(JBroFuzzPrefs.DBSETTINGS[3].getTitle(),
				proxyReqAuth);
		proxyReqAuthBox
				.setToolTipText(JBroFuzzPrefs.DBSETTINGS[3].getTooltip());
		proxyReqAuthBox.setBorderPaintedFlat(true);

		// TODO: 4, proxy authentication type, basic, ntlm, etc:
		// integer 1 is basic
		final String[] authTypeArray = { "HTTP" };
		authTypeBox = new JComboBox(authTypeArray);
		authTypeBox.setToolTipText(JBroFuzzPrefs.DBSETTINGS[4].getTooltip());

		// User text field
		final String userEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[5].getId(), "");
		userTextField = new JTextField(userEntry);
		userTextField.setToolTipText(JBroFuzzPrefs.DBSETTINGS[5].getTooltip());
		userTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		userTextField.setMargin(new Insets(1, 1, 1, 1));
		userTextField.setPreferredSize(new Dimension(80, 20));

		// Password text field
		final String passEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[6].getId(), "");
		passTextField = new JTextField(passEntry);
		passTextField.setToolTipText(JBroFuzzPrefs.DBSETTINGS[6].getTooltip());
		passTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		passTextField.setMargin(new Insets(1, 1, 1, 1));
		passTextField.setPreferredSize(new Dimension(80, 20));

		final String[] dbTypeArray = { "SQLLite (embedded)", "CouchDB", "None"};		
		dbTypeBox = new JComboBox(dbTypeArray);
		dbTypeBox.setToolTipText(JBroFuzzPrefs.DBSETTINGS[11].getTooltip()); 
		
		final String dbUserEntry = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[7].getId(), "");
		dbUserTextField = new JTextField(dbUserEntry);
		dbUserTextField.setToolTipText(JBroFuzzPrefs.DBSETTINGS[7].getTooltip());
		dbUserTextField.setFont(new Font("Veranda", Font.PLAIN, 12));
		dbUserTextField.setMargin(new Insets(1, 1, 1, 1));
		dbUserTextField.setPreferredSize(new Dimension(80, 20));

		final String dbPassEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[8].getId(), "");
		dbPassTextField = new JTextField(dbPassEntry);
		dbPassTextField
				.setToolTipText(JBroFuzzPrefs.DBSETTINGS[8].getTooltip());
		dbPassTextField.setFont(new Font("Veranda", Font.PLAIN, 12));
		dbPassTextField.setMargin(new Insets(1, 1, 1, 1));
		dbPassTextField.setPreferredSize(new Dimension(80, 20));

		final String dbHostEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[9].getId(), "");
		dbHostTextField = new JTextField(dbHostEntry);
		dbHostTextField
				.setToolTipText(JBroFuzzPrefs.DBSETTINGS[9].getTooltip());
		dbHostTextField.setFont(new Font("Veranda", Font.PLAIN, 12));
		dbHostTextField.setMargin(new Insets(1, 1, 1, 1));
		dbHostTextField.setPreferredSize(new Dimension(80, 20));

		final String dbPortEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[10].getId(), "");
		dbPortTextField = new JTextField(dbPortEntry);
		dbPortTextField.setToolTipText(JBroFuzzPrefs.DBSETTINGS[10]
				.getTooltip());
		dbPortTextField.setFont(new Font("Veranda", Font.PLAIN, 12));
		dbPortTextField.setMargin(new Insets(1, 1, 1, 1));
		dbPortTextField.setPreferredSize(new Dimension(80, 20));
		
		//	JBroFuzzPrefs.DBSETTINGS[12].getId()  == dbType (SelectBox)
		
		final String dbNameEntry = JBroFuzz.PREFS.get(
				JBroFuzzPrefs.DBSETTINGS[12].getId(), "");
		dbNameTextField = new JTextField(dbNameEntry);
		dbNameTextField.setToolTipText(JBroFuzzPrefs.DBSETTINGS[12]
				.getTooltip());
		dbNameTextField.setFont(new Font("Veranda", Font.PLAIN, 12));
		dbNameTextField.setMargin(new Insets(1, 1, 1, 1));
		dbNameTextField.setPreferredSize(new Dimension(80, 20));

		
		// Panels for our friends above: Host & Port
		final JPanel hostPortPanel = new JPanel(new GridBagLayout());
		// A very important line when it comes to BoxLayout
		hostPortPanel.setAlignmentX(0.0f);

		hostPortPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(JBroFuzzPrefs.DBSETTINGS[1]
						.getTitle()
						+ " / "
						+ JBroFuzzPrefs.DBSETTINGS[2].getTitle()),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.ipadx = 250;
		hostPortPanel.add(hostTextField, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 1;
		c.ipadx = 0;
		hostPortPanel.add(portTextField, c);

		// Panel for DB-Type selection
		final JPanel dbTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
				15, 0));
		dbTypePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("DB-Type"),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		dbTypePanel.add(dbTypeBox);
		dbTypePanel.add(new JLabel(JBroFuzzPrefs.DBSETTINGS[7].getTitle()));
		dbTypePanel.add(dbUserTextField);
		dbTypePanel.add(new JLabel(JBroFuzzPrefs.DBSETTINGS[8].getTitle()));
		dbTypePanel.add(dbPassTextField);
		dbTypePanel.add(new JLabel(JBroFuzzPrefs.DBSETTINGS[9].getTitle()));
		dbTypePanel.add(dbHostTextField);
		dbTypePanel.add(new JLabel(JBroFuzzPrefs.DBSETTINGS[10].getTitle()));
		dbTypePanel.add(dbPortTextField);
		dbTypePanel.add(new JLabel(JBroFuzzPrefs.DBSETTINGS[12].getTitle()));
		dbTypePanel.add(dbNameTextField);
		dbTypePanel.setAlignmentX(0.0f);

		// Panel for our friends above: Username & Password
		final JPanel userPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
				15, 0));
		userPassPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Authentication"),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		// userPassPanel.add(new JLabel("Type"));
		userPassPanel.add(authTypeBox);
		userPassPanel.add(new JLabel(JBroFuzzPrefs.DBSETTINGS[5].getTitle()));
		userPassPanel.add(userTextField);
		userPassPanel.add(new JLabel(JBroFuzzPrefs.DBSETTINGS[6].getTitle()));
		userPassPanel.add(passTextField);
		// A very important property for correct alignment
		userPassPanel.setAlignmentX(0.0f);

		// Tick-un-tick, unhighlight, yawn, yawn
		if (proxyEnabledBox.isSelected()) {
			hostTextField.setEnabled(true);
			portTextField.setEnabled(true);
			proxyReqAuthBox.setEnabled(true);
			authTypeBox.setEnabled(true);
			userTextField.setEnabled(true);
			passTextField.setEnabled(true);
			dbTypeBox.setSelectedIndex(1);

		} else {

			hostTextField.setEnabled(false);
			portTextField.setEnabled(false);
			proxyReqAuthBox.setEnabled(false);
			authTypeBox.setEnabled(false);
			userTextField.setEnabled(false);
			passTextField.setEnabled(false);
			dbTypeBox.setSelectedIndex(0);

		}
		if (proxyReqAuthBox.isSelected()) {

			authTypeBox.setEnabled(true);
			userTextField.setEnabled(true);
			passTextField.setEnabled(true);
			dbTypeBox.setSelectedIndex(1);

		} else {

			authTypeBox.setEnabled(false);
			userTextField.setEnabled(false);
			passTextField.setEnabled(false);
		}

		if (dbTypeBox.getSelectedIndex() == 3) {
			dbUserTextField.setEnabled(false);
			dbPassTextField.setEnabled(false);
			dbHostTextField.setEnabled(false);
			dbPortTextField.setEnabled(false);
			proxyReqAuthBox.setEnabled(false);
			proxyEnabledBox.setSelected(false); 
			proxyEnabledBox.setEnabled(false);
			proxyReqAuthBox.setSelected(false);
			authTypeBox.setEnabled(false); 
		} else {
			dbUserTextField.setEnabled(true);
			dbPassTextField.setEnabled(true);
			dbHostTextField.setEnabled(true);
			dbPortTextField.setEnabled(true);
			proxyEnabledBox.setEnabled(true);
		}

		dbTypeBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent1) {
				if (dbTypeBox.getSelectedIndex() == 3) {
					dbUserTextField.setEnabled(false);
					dbPassTextField.setEnabled(false);
					dbHostTextField.setEnabled(false);
					dbPortTextField.setEnabled(false);
					proxyReqAuthBox.setEnabled(false);
					proxyEnabledBox.setSelected(false); 
					proxyEnabledBox.setEnabled(false);
					proxyReqAuthBox.setSelected(false);
					hostTextField.setText("");
					portTextField.setText("");
					userTextField.setText("");
					passTextField.setText("");
					dbUserTextField.setText("");
					dbPassTextField.setText("");
					dbHostTextField.setText("");
					dbPortTextField.setText("");
					authTypeBox.setEnabled(false); 
				} else {
					dbUserTextField.setEnabled(true);
					dbPassTextField.setEnabled(true);
					dbHostTextField.setEnabled(true);
					dbPortTextField.setEnabled(true);
					proxyEnabledBox.setEnabled(true);
				}
			}
		});

		// Listeners for the tick boxes
		proxyEnabledBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent1) {
				if (proxyEnabledBox.isSelected()) {
					hostTextField.setEnabled(true);
					portTextField.setEnabled(true);
					proxyReqAuthBox.setEnabled(true);
					authTypeBox.setEnabled(true);
					userTextField.setEnabled(true);
					passTextField.setEnabled(true);
					dbTypeBox.setSelectedIndex(1);

					if (proxyReqAuthBox.isSelected()) {
						authTypeBox.setEnabled(true);
						userTextField.setEnabled(true);
						passTextField.setEnabled(true);

					} else {
						authTypeBox.setEnabled(false);
						userTextField.setEnabled(false);
						passTextField.setEnabled(false);
					}

				} else {
					hostTextField.setEnabled(false);
					portTextField.setEnabled(false);
					proxyReqAuthBox.setEnabled(false);
					authTypeBox.setEnabled(false);
					userTextField.setEnabled(false);
					passTextField.setEnabled(false);
				}
				dialog.setApplyEnabled(true);

			}
		});

		proxyReqAuthBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent2) {

				if (proxyReqAuthBox.isSelected()) {

					authTypeBox.setEnabled(true);
					userTextField.setEnabled(true);
					passTextField.setEnabled(true);
				} else {

					authTypeBox.setEnabled(false);
					userTextField.setEnabled(false);
					passTextField.setEnabled(false);
				}
				dialog.setApplyEnabled(true);
			}
		});

		// Additions
		add(proxyEnabledBox);
		add(Box.createRigidArea(V_SPACE));
		add(hostPortPanel);
		add(Box.createRigidArea(V_SPACE));
		add(proxyReqAuthBox);
		add(userPassPanel);
		add(Box.createRigidArea(V_SPACE));
		add(dbTypePanel);
		add(Box.createRigidArea(new Dimension(20, 300)));
		
		
		// select appropriate index.
		if (!JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId() , "").equals("")){
			for (int i = 0; i < dbTypeArray.length; i++){
				if (JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(),"").toLowerCase().trim().equals(dbTypeArray[i].toLowerCase().trim())){
					dbTypeBox.setSelectedIndex(i);
					dbTypeBox.updateUI();
					break; 
				}
			}
		}

	}

	@Override
	protected void apply() {
		System.out.println("apply: " + dbTypeBox.getSelectedIndex());
		JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.DBSETTINGS[0].getId(), proxyEnabledBox.isSelected());
		if (dbTypeBox.getSelectedIndex() == 3) {
			proxyEnabledBox.setSelected(false);
			proxyReqAuthBox.setSelected(false);
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[1].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[2].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[3].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[4].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[5].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[6].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[7].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[8].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[9].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[10].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[11].getId(), "");
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[12].getId(), "");
		} else {
			if (proxyEnabledBox.isSelected()) {
				JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[1].getId(),
						hostTextField.getText());
				JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[2].getId(),
						portTextField.getText());

				JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.DBSETTINGS[3].getId(),
						proxyReqAuthBox.isSelected());

				// 4, proxy authentication type, basic, ntlm, etc:
				// integer 1 is basic

				if (proxyReqAuthBox.isSelected()) {
					JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[4].getId(), 
							authTypeBox.getSelectedItem().toString());
					JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[5].getId(),
							userTextField.getText());
					JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[6].getId(),
							passTextField.getText());
				}
			}
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[7].getId(), dbUserTextField.getText());
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[8].getId(), dbPassTextField.getText());
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[9].getId(), dbHostTextField.getText());
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[10].getId(), dbPortTextField.getText());
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[11].getId(), dbTypeBox.getSelectedItem().toString());
			JBroFuzz.PREFS.put(JBroFuzzPrefs.DBSETTINGS[12].getId(), dbNameTextField.getText());
		}
	}
}