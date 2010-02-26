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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>
 * The proxy panel, as part of the preferences. 
 * </p>
 * 
 * @author nathan?
 * @version 2.0
 * @since 2.0
 */
class ProxyPPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = 7858434511884269098L;

	private final PrefDialog dialog;
	
	private final JCheckBox enProxyCheckBox;
	
	private static final int MIN_PORT = 1;  /* min network port */
	private static final int MAX_PORT = 65535; /* max network port */
	private int port;
	private boolean validProxy;
	
	private final JTextField serverTextField, portTextField, userTextField, passTextField;
	
	/**
	 * <p>
	 * The proxy panel constructor, passing the main dialog as argument.
	 * </p>
	 * 
	 * @param dialog PrefDialog
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	protected ProxyPPanel(final PrefDialog dialog) {
		super("Proxy Settings");
		this.dialog = dialog;
		validProxy = true;
		
		// Proxy Configuration Settings
		final boolean enProxyClickBox = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.PROXY[0], false);
		enProxyCheckBox = new JCheckBox(
				" Enable Proxy Configuration ",
				enProxyClickBox);

		enProxyCheckBox.setBorderPainted(false);
		enProxyCheckBox.setToolTipText("Tick this box to enable proxy support.");
		enProxyCheckBox.setAlignmentX(0.0f);
		
		// The main proxy panel, with enabled fields if the above box is ticked
		final JPanel proxyPanel = new JPanel(new GridBagLayout());
		proxyPanel.setAlignmentX(0.0f);
		GridBagConstraints gridStrains = new GridBagConstraints();
		proxyPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Proxy Settings "), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));
		
		// The proxy server fields for server address and port
		final JLabel serverLabel = new JLabel(" Proxy Address: ");
		final JLabel portLabel = new JLabel(" Port: ");
		final JLabel userLabel = new JLabel(" Username: ");
		final JLabel passLabel = new JLabel(" Password: ");

		serverTextField = new JTextField(20);
		portTextField = new JTextField(5);
		userTextField = new JTextField(20);
		passTextField = new JTextField(20);
		
		gridStrains.fill = GridBagConstraints.NONE;
		
		gridStrains.gridx = 0;
		gridStrains.gridy = 0;
		proxyPanel.add(serverLabel, gridStrains);
		gridStrains.gridx = 1;
		gridStrains.gridy = 0;
		proxyPanel.add(serverTextField, gridStrains);
		
		gridStrains.gridx = 2;
		gridStrains.gridy = 0;
		proxyPanel.add(portLabel, gridStrains);
		gridStrains.gridx = 3;
		gridStrains.gridy = 0;
		proxyPanel.add(portTextField, gridStrains);
		
		gridStrains.gridx = 0;
		gridStrains.gridy = 1;
		proxyPanel.add(new JLabel(" "), gridStrains);
		
		
		gridStrains.gridx = 0;
		gridStrains.gridy = 2;
		proxyPanel.add(userLabel, gridStrains);
		gridStrains.gridx = 1;
		gridStrains.gridy = 2;
		proxyPanel.add(userTextField, gridStrains);
		
		gridStrains.gridx = 0;
		gridStrains.gridy = 3;
		proxyPanel.add(passLabel, gridStrains);
		gridStrains.gridx = 1;
		gridStrains.gridy = 3;
		proxyPanel.add(passTextField, gridStrains);
		
		gridStrains.gridx = 0;
		gridStrains.gridy = 4;
		proxyPanel.add(new JLabel(" "), gridStrains);
		
		if (enProxyCheckBox.isSelected()) {
			serverTextField.setEditable(true);
			portTextField.setEditable(true);
			userTextField.setEditable(true);
			passTextField.setEditable(true);
		} else {
			serverTextField.setEditable(false);
			portTextField.setEditable(false);
			userTextField.setEditable(false);
			passTextField.setEditable(false);
		}
		
		
		enProxyCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent proxyEvent) {
				if (enProxyCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.PROXY[0], true);
					serverTextField.setEditable(true);
					portTextField.setEditable(true);
					userTextField.setEditable(true);
					passTextField.setEditable(true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.PROXY[0], false);
					serverTextField.setEditable(false);
					portTextField.setEditable(false);
					userTextField.setEditable(false);
					passTextField.setEditable(false);
				}
				dialog.setApplyEnabled(true);
			}
		});

		
		
		add(enProxyCheckBox);
		add(proxyPanel);
		add(Box.createRigidArea(new Dimension(0, 200)));

    	// Populate the fields
		serverTextField.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.PROXY[1], ""));
		portTextField.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.PROXY[2], ""));
		userTextField.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.PROXY[3], ""));
		passTextField.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.PROXY[4], ""));
		
	}
	
	public void apply() {
		
		JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.PROXY[0], enProxyCheckBox.isSelected());
    	JBroFuzz.PREFS.put(JBroFuzzPrefs.PROXY[1], serverTextField.getText());
    	JBroFuzz.PREFS.put(JBroFuzzPrefs.PROXY[2], portTextField.getText());
    	JBroFuzz.PREFS.put(JBroFuzzPrefs.PROXY[3], userTextField.getText());
    	JBroFuzz.PREFS.put(JBroFuzzPrefs.PROXY[4], passTextField.getText());
    	
    	/*
		if (enProxyCheckBox.isSelected()) {
			
		    // validate proxy server and proxy port
		    try {
		    	InetAddress.getByName(serverTextField.getText());	
		    } catch (UnknownHostException uhe) {
		    	dialog.getJBroFuzzWindow().log("Invalid proxy server address/hostname: " + serverTextField.getText(), 4);
		    	validProxy = false;
		    }
		    
		    try {
		    	port = Integer.parseInt(portTextField.getText());	
		    } catch (NumberFormatException nfe) {
		    	validProxy = false;
		    }
		 
		    if ((port < MIN_PORT) || (port > MAX_PORT)) {
		    	dialog.getJBroFuzzWindow().log("Invalid proxy port number: " + portTextField.getText(), 4);
		    	dialog.getJBroFuzzWindow().log("Port should be in the range of: [" + MIN_PORT + ", " + MAX_PORT + "]", 4);
		    	validProxy = false;
		    }
		    // Finally, write all the preferences
		    if (validProxy) {
		    	
		    	
		    }
			
		}
		*/
		
	}
}
