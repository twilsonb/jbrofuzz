/**
 * JBroFuzz 2.4
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
 * <p>Class for displaying the preferences, under Ctrl+P,
 * of "Check for Updates".</p>
 * 
 * @author subere@uncon.org
 * @version 2.5
 * @since 2.4
 *
 */
public class UpdatePPanel extends AbstractPrefsPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6601607213870707446L;

	private final JCheckBox proxyEnabledBox, proxyReqAuthBox;
	
	private final JTextField hostTextField, portTextField, userTextField, passTextField;

	private final JComboBox authTypeBox;
	
	private static final Dimension V_SPACE = new Dimension(0, 20);
	
	protected UpdatePPanel(final PrefDialog dialog) {
		
		super("Check for Updates");
		
		// Tick box for selecting your own directory
		final boolean proxyEnabled = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.UPDATE[0].getId(), false);
		proxyEnabledBox = new JCheckBox(JBroFuzzPrefs.UPDATE[0].getTitle(), proxyEnabled);
		proxyEnabledBox.setToolTipText(JBroFuzzPrefs.UPDATE[0].getTooltip());
		proxyEnabledBox.setBorderPaintedFlat(true);
		
		// Host text & preference
		final String hostEntry = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[1].getId(), "");
		hostTextField = new JTextField(hostEntry);
		hostTextField.setToolTipText(JBroFuzzPrefs.UPDATE[1].getTooltip());
		hostTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		hostTextField.setMargin(new Insets(1, 1, 1, 1));
		
		// Port text & preference
		final String portEntry = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[2].getId(), "");
		portTextField = new JTextField(portEntry);
		portTextField.setToolTipText(JBroFuzzPrefs.UPDATE[2].getTooltip());
		portTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		portTextField.setMargin(new Insets(1, 1, 1, 1));

		// Proxy requires authentication
		final boolean proxyReqAuth = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.UPDATE[3].getId(), false);
		proxyReqAuthBox = new JCheckBox(JBroFuzzPrefs.UPDATE[3].getTitle(), proxyReqAuth);
		proxyReqAuthBox.setToolTipText(JBroFuzzPrefs.UPDATE[3].getTooltip());
		proxyReqAuthBox.setBorderPaintedFlat(true);
		
		// TODO: 4, proxy authentication type, basic, ntlm, etc: 
		// integer 1 is basic
		final String[] authTypeArray = { "HTTP" };
		authTypeBox = new JComboBox(authTypeArray);
		authTypeBox.setToolTipText(JBroFuzzPrefs.UPDATE[4].getTooltip());

		// User text field
		final String userEntry = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[5].getId(), "");
		userTextField = new JTextField(userEntry);
		userTextField.setToolTipText(JBroFuzzPrefs.UPDATE[5].getTooltip());
		userTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		userTextField.setMargin(new Insets(1, 1, 1, 1));
		userTextField.setPreferredSize(new Dimension(80, 20));
		
		// Password text field
		final String passEntry = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[6].getId(), "");
		passTextField = new JTextField(passEntry);
		passTextField.setToolTipText(JBroFuzzPrefs.UPDATE[6].getTooltip());
		passTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		passTextField.setMargin(new Insets(1, 1, 1, 1));
		passTextField.setPreferredSize(new Dimension(80, 20));
		
		// Panels for our friends above: Host & Port
		final JPanel hostPortPanel = new JPanel(new GridBagLayout());
		// A very important line when it comes to BoxLayout
		hostPortPanel.setAlignmentX(0.0f);

		hostPortPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(
						JBroFuzzPrefs.UPDATE[1].getTitle()
						+ " / " +
						JBroFuzzPrefs.UPDATE[2].getTitle()
				), 
				BorderFactory.createEmptyBorder(
		1, 1, 1, 1)));
		
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

		//Panel for our friends above: Username & Password
		final JPanel userPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
				15, 0));
		userPassPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(
						"Authentication"
				), 
				BorderFactory.createEmptyBorder(
		1, 1, 1, 1)));
		// userPassPanel.add(new JLabel("Type"));
		userPassPanel.add(authTypeBox);
		userPassPanel.add(new JLabel(JBroFuzzPrefs.UPDATE[5].getTitle()));
		userPassPanel.add(userTextField);
		userPassPanel.add(new JLabel(JBroFuzzPrefs.UPDATE[6].getTitle()));
		userPassPanel.add(passTextField);
		// A very important property for correct alignment
		userPassPanel.setAlignmentX(0.0f);
		
		// Tick-un-tick, unhighlight, yawn, yawn
		if(proxyEnabledBox.isSelected()) {
			
			hostTextField.setEnabled(true);
			portTextField.setEnabled(true);
			proxyReqAuthBox.setEnabled(true);
			authTypeBox.setEnabled(true);
			userTextField.setEnabled(true);
			passTextField.setEnabled(true);
			
		} else {
			
			hostTextField.setEnabled(false);
			portTextField.setEnabled(false);
			proxyReqAuthBox.setEnabled(false);
			authTypeBox.setEnabled(false);
			userTextField.setEnabled(false);
			passTextField.setEnabled(false);
			
		}
		if(proxyReqAuthBox.isSelected()) {
			
			authTypeBox.setEnabled(true);
			userTextField.setEnabled(true);
			passTextField.setEnabled(true);
			
		} else {
			
			authTypeBox.setEnabled(false);
			userTextField.setEnabled(false);
			passTextField.setEnabled(false);
			
		}
		
		// Listeners for the tick boxes
		proxyEnabledBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent1) {

				if(proxyEnabledBox.isSelected()) {
					
					hostTextField.setEnabled(true);
					portTextField.setEnabled(true);
					proxyReqAuthBox.setEnabled(true);
					authTypeBox.setEnabled(true);
					userTextField.setEnabled(true);
					passTextField.setEnabled(true);
					
					if(proxyReqAuthBox.isSelected()) {
						
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
				
				if(proxyReqAuthBox.isSelected()) {
					
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
		add(Box.createRigidArea(new Dimension(0, 300)));

	}
	
	@Override
	protected void apply() {

		JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.UPDATE[0].getId(), proxyEnabledBox.isSelected());
		if(proxyEnabledBox.isSelected()) {
			JBroFuzz.PREFS.put(JBroFuzzPrefs.UPDATE[1].getId(), hostTextField.getText());
			JBroFuzz.PREFS.put(JBroFuzzPrefs.UPDATE[2].getId(), portTextField.getText());
			
			JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.UPDATE[3].getId(), proxyReqAuthBox.isSelected());
			
			// 4, proxy authentication type, basic, ntlm, etc: 
			// integer 1 is basic

			if(proxyReqAuthBox.isSelected()) {
				JBroFuzz.PREFS.put(JBroFuzzPrefs.UPDATE[5].getId(), userTextField.getText());
				JBroFuzz.PREFS.put(JBroFuzzPrefs.UPDATE[6].getId(), passTextField.getText());				
			}
		}
	}
}
