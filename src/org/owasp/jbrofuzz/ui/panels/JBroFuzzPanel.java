/**
 * JBroFuzz 1.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 * 
 */
package org.owasp.jbrofuzz.ui.panels;

import javax.swing.*;

import org.owasp.jbrofuzz.ui.*;
/**
 * <p>
 * The super class that is extended for every panel that is implemented.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.8
 */
public abstract class JBroFuzzPanel extends JPanel {

	private JBroFuzzWindow frame;
	
	public JBroFuzzPanel(final JBroFuzzWindow frame) {
		super();
		setLayout(null);
		this.frame = frame;
	}
	
	public JBroFuzzWindow getFrame() {
		return frame;
	}
	
	
}
