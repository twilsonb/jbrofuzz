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
 * @author subere@uncon.org
 * @version 1.0
 */
public abstract class JBroFuzzPanel extends JPanel {

	private JBroFuzzWindow frame;

	private String name;

	private boolean[] optionsAvailable;

	public JBroFuzzPanel(final String name, final JBroFuzzWindow frame) {

		super();
		setLayout(null);
		setOpaque(false);
		this.name = name;
		this.frame = frame;
		this.optionsAvailable = new boolean [] {false, false, false, false, false};

	}

	public final JBroFuzzWindow getFrame() {

		return frame;

	}

	public abstract void start();

	public abstract void stop();

	public abstract void add();

	public abstract void remove();

	public abstract void graph();

	public final void setOptionsAvailable(boolean start, boolean stop, boolean graph, boolean add, boolean remove) {

		optionsAvailable[0] = start;
		optionsAvailable[1] = stop;
		optionsAvailable[2] = graph;
		optionsAvailable[3] = add;
		optionsAvailable[4] = remove;

		getFrame().getFrameToolBar().setEnabledPanelOptions(optionsAvailable);
		getFrame().getFrameMenuBar().setEnabledPanelOptions(optionsAvailable);

	}

	public final boolean[] getOptionsAvailable() {

		return optionsAvailable;

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public int getOptionsLength() {
		
		return optionsAvailable.length;
		
	}

	
	public boolean isStarted() {

		return optionsAvailable[0];

	}
	
	public boolean isStopped() {
		
		return optionsAvailable[1];
	}
	
	public boolean isGraphed() {
		
		return optionsAvailable[2];
		
	}
	
	public boolean isAdded() {
		
		return optionsAvailable[3];
		
	}
	
	public boolean isRemoved() {
		
		return optionsAvailable[4];
		
	}

}
