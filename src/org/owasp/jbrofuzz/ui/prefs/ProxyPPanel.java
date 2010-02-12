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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

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
	private boolean validProxy = true;
	
	private final JTextField serverTextField, portTextField;
	
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
		
		// Proxy Configuration Settings
		final boolean enProxyClickBox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PROXY_ENABLED, false);

		enProxyCheckBox = new JCheckBox(
				" Enable Proxy Configuration ",
				enProxyClickBox);

		enProxyCheckBox.setBorderPainted(false);
		enProxyCheckBox.setToolTipText(
		"Tick this box to enable proxy support.");

		final JPanel proxyServerPanel = new JPanel(new BorderLayout());
		proxyServerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" HTTP Proxy Server "), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));

		serverTextField = new JTextField();
		serverTextField.setFont(new Font("Verdana", Font.BOLD, 12));
		serverTextField.setMargin(new Insets(1, 1, 1, 1));
		proxyServerPanel.add(serverTextField, BorderLayout.NORTH);

		final JPanel proxyPortPanel = new JPanel(new BorderLayout());
		proxyPortPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Proxy Port "), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));

		portTextField = new JTextField();
		portTextField.setFont(new Font("Verdana", Font.BOLD, 12));
		portTextField.setMargin(new Insets(1, 1, 1, 1));
		proxyPortPanel.add(portTextField, BorderLayout.NORTH);
		if (enProxyCheckBox.isSelected()) {
			serverTextField.setEditable(true);
			portTextField.setEditable(true);
		} else {
			serverTextField.setEditable(false);
			portTextField.setEditable(false);
		}

		final String proxyServer = JBroFuzz.PREFS.get(JBroFuzzFormat.PROXY_SERVER, "");
		serverTextField.setText(proxyServer);
		final String proxyPort = JBroFuzz.PREFS.get(JBroFuzzFormat.PROXY_PORT, "");
		portTextField.setText(proxyPort);

		enProxyCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent proxyEvent) {
				if (enProxyCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PROXY_ENABLED, true);
					serverTextField.setEditable(true);
					portTextField.setEditable(true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PROXY_ENABLED, false);
					serverTextField.setEditable(false);
					portTextField.setEditable(false);
				}
			}
		});

		final JSplitPane rightPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		rightPane.setOneTouchExpandable(false);
		rightPane.setLeftComponent(proxyServerPanel);
		rightPane.setRightComponent(proxyPortPanel);
		rightPane.setDividerLocation(350);

		add(enProxyCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));
		add(rightPane);
		add(Box.createRigidArea(new Dimension(0, 200)));

	}
	
	public void apply() {
		
		if (enProxyCheckBox.isSelected()) {
			
		    /* validate proxy server and proxy port */
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
		    	dialog.getJBroFuzzWindow().log("Port should be in range " + MIN_PORT + " to " + MAX_PORT, 4);
		    	validProxy = false;
		    }
		    
		    if (validProxy) {
		    	JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PROXY_ENABLED, true);
		    	JBroFuzz.PREFS.put(JBroFuzzFormat.PROXY_PORT, portTextField.getText());
		    	JBroFuzz.PREFS.put(JBroFuzzFormat.PROXY_SERVER, serverTextField.getText());
		    }
			
		}
	}
}
