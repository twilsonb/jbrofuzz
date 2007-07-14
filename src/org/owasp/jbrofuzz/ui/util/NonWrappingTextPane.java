/*
 * Testing to see if this approach would be usefull.
 */
package org.owasp.jbrofuzz.ui.util;

import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;

//Because Swing's JTextPane is retarded by default and doesn't allow you to just flip a switch and turn off the FUCKING line-wrapping
//
// This overridden method was coded by somebody much smarter than I, somebody probably with an EGO the size of SUN MICROSYSTEMS.
//
// At SUN our motto is "Over-engineer everything so that nobody understands it..."
public class NonWrappingTextPane extends JTextPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8295506229408494512L;

	// The method below is coutesy of Core Swing Advanced Programming by Kim Topley
	//
	// Override getScrollableTracksViewportWidth
	// to preserve the full width of the text
	@Override
	public boolean getScrollableTracksViewportWidth() {
		final Component parent = this.getParent();
		final ComponentUI ui = this.getUI();

		return parent != null ? (ui.getPreferredSize(this).width <= parent.getSize().width) : true;
	}
}
