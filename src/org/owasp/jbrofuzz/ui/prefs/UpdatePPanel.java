package org.owasp.jbrofuzz.ui.prefs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class UpdatePPanel extends AbstractPrefsPanel {

	private final JCheckBox proxyEnabledBox, proxyReqAuthBox;
	
	private final JTextField hostTextField, portTextField, userTextField, passTextField;

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
		
		// User text field
		final String userEntry = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[5].getId(), "");
		userTextField = new JTextField(userEntry);
		userTextField.setToolTipText(JBroFuzzPrefs.UPDATE[5].getTooltip());
		userTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		userTextField.setMargin(new Insets(1, 1, 1, 1));
		
		// Password text field
		final String passEntry = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[6].getId(), "");
		passTextField = new JTextField(userEntry);
		passTextField.setToolTipText(JBroFuzzPrefs.UPDATE[6].getTooltip());
		passTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		passTextField.setMargin(new Insets(1, 1, 1, 1));
		
		// Panels for our friends above
		final JPanel hostPortPanel = new JPanel(new GridBagLayout());
		
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

		
		
		// Additions
		add(proxyEnabledBox);
		add(Box.createRigidArea(V_SPACE));
		add(hostPortPanel);
		
		add(Box.createRigidArea(new Dimension(0, 300)));

	}
	
	@Override
	protected void apply() {
		// TODO Auto-generated method stub

	}

}
