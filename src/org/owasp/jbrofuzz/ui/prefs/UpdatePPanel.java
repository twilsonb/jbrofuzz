package org.owasp.jbrofuzz.ui.prefs;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class UpdatePPanel extends AbstractPrefsPanel {

	private final JCheckBox proxyBox;

	protected UpdatePPanel(final PrefDialog dialog) {
		
		super("Check for Updates");
		
		final JPanel proxyPanel = new JPanel();
		proxyPanel.setLayout(new BoxLayout(proxyPanel, BoxLayout.LINE_AXIS));
		// A very important line when it comes to BoxLayout
		proxyPanel.setAlignmentX(0.0f);
		
		// Tick box for selecting your own directory
		final boolean boolEntry = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.UPDATE[0].getId(), false);
		proxyBox = new JCheckBox(JBroFuzzPrefs.UPDATE[0].getTitle(), boolEntry);
		proxyBox.setToolTipText(JBroFuzzPrefs.UPDATE[0].getTooltip());
		proxyBox.setBorderPaintedFlat(true);
		
		add(proxyBox);
		add(Box.createRigidArea(new Dimension(0, 20)));

		
	}
	
	@Override
	protected void apply() {
		// TODO Auto-generated method stub

	}

}
