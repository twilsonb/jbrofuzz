/**
 * JBroFuzz 2.0
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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <p>Abstract class providing the preferences panel foundation,
 * used for each panel within the preferences dialog 
 * PrefDialog.java</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
abstract class AbstractPrefsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// The name of the panel, as passed by the constructor
	private final String name;

	/**
	 * <p>Abstract preferences panel constructor, passing
	 * the String of the name to be displayed on the right
	 * hand side tree and the top of the left hand side.</p>
	 * 
	 * @param name The name of the preferences panel
	 */
	protected AbstractPrefsPanel(final String name) {
		super();
		this.name = name;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final JLabel header = new JLabel("<HTML><H3>&nbsp;&nbsp;" + name + "</H3></HTML>");
		// A very important line when it comes to BoxLayout component alignment
		header.setAlignmentX(0.0f);
		add(header);

	}

	/**
	 * <p>Return the name of the panel, as specified 
	 * under object construction.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * <p>Method returning a default mutable tree node,
	 * to be added to the right hand side panel of the
	 * preferences menu.</p>
	 * 
	 * @return DefaultMutableTreeNode
	 * 				A default tree node with the name
	 * 				specified during construction.
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0 
	 */
	protected final DefaultMutableTreeNode getTreeNode() {
		return new DefaultMutableTreeNode(name);
	}

	/**
	 * <p>Method required to be implemented, for setting
	 * the preferences changed, in the event of the user
	 * clicking "Apply".</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	protected abstract void apply();


}
