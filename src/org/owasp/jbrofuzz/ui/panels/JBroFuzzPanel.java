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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;

import com.Ostermiller.util.Browser;

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

	/**
	 * <p>Constructor, creating a panel and allocating the given String 
	 * as name to that panel.</p>
	 * <p>The parent frame to which this panel belongs to is also 
	 * required at construction.</p>
	 * 
	 * @param name
	 * @param frame
	 */
	public JBroFuzzPanel(final String name, final JBroFuzzWindow frame) {

		super();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		setOpaque(false);
		this.name = name;
		this.frame = frame;
		this.optionsAvailable = new boolean [] {false, false, false, false, false};

	}

	/**
	 * <p>Obtain the JBroFuzzWindow parent frame.</p>
	 * 
	 * @return
	 */
	public final JBroFuzzWindow getFrame() {

		return frame;

	}

	public abstract void start();

	public abstract void stop();

	public abstract void add();

	public abstract void remove();

	public abstract void graph();

	/**
	 * <p>Method for setting the options available for that particular panel. These 
	 * options can be found in the toolbar as well as the menubar under the FileMenuItem
	 * "Panel".</p>
	 * 
	 * @param start
	 * @param stop
	 * @param graph
	 * @param add
	 * @param remove
	 */
	public final void setOptionsAvailable(boolean start, boolean stop, boolean graph, boolean add, boolean remove) {

		optionsAvailable[0] = start;
		optionsAvailable[1] = stop;
		optionsAvailable[2] = graph;
		optionsAvailable[3] = add;
		optionsAvailable[4] = remove;

		getFrame().getJBroToolBar().setEnabledPanelOptions(optionsAvailable);
		getFrame().getJBroMenuBar().setEnabledPanelOptions(optionsAvailable);

	}

	/**
	 * <p>Return the current options available; by default all the options are 
	 * set to false.</p>
	 * 
	 * @return
	 */
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
	
	/**
	 * Method for setting up the right click copy paste cut and select all menu.
	 * 
	 * @param area
	 *            JTextArea
	 */
	public final void popupText(final JTextComponent area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");

		i1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		i2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		i3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK));
		i4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.addSeparator();
		popmenu.add(i4);

		if (!area.isEditable()) {
			i3.setEnabled(false);
		}

		i1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.cut();
			}
		});

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.copy();
			}
		});

		i3.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (area.isEditable()) {
					area.paste();
				}
			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		area.addMouseListener(new MouseAdapter() {
			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				checkForTriggerEvent(e);
			}
		});
	}

	protected final void popupTable(final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i0 = new JMenuItem("Open in Browser");
		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");
		final JMenuItem i5 = new JMenuItem("Properties");

		i0.setEnabled(true);
		i1.setEnabled(false);
		i2.setEnabled(true);
		i3.setEnabled(false);
		i4.setEnabled(true);
		i5.setEnabled(true);

		popmenu.add(i0);
		popmenu.addSeparator();
		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.add(i4);
		popmenu.addSeparator();
		popmenu.add(i5);

		i0.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				Browser.init();

				final String fileName = (String) area.getValueAt(area
						.getSelectedRow(), 0)
						+ ".html";
				final File f = getFrame().getJBroFuzz().getHandler().getFuzzFile(fileName);

				try {
					Browser.displayURL(f.toURI().toString());
				} catch (final IOException ex) {getFrame().log("Could not launch link in external browser");
				}
			}
		});

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Copy
				final StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();
				for (final int element : selection) {
					for (int i = 0; i < area.getRowCount(); i++) {
						selectionBuffer.append(area.getModel().getValueAt(
								element, i));
						if (i < area.getRowCount() - 1) {
							selectionBuffer.append(",");
						}
					}
					selectionBuffer.append("\n");
				}
				final JTextArea myTempArea = new JTextArea();
				myTempArea.setText(selectionBuffer.toString());
				myTempArea.selectAll();
				myTempArea.copy();
				area.removeRowSelectionInterval(0, area.getRowCount() - 1);

			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Select All
				area.selectAll();
			}
		});

		i5.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final int c = area.getSelectedRow();
				final String name = (String) area.getModel().getValueAt(c, 0);
				new WindowViewer(JBroFuzzPanel.this, name, WindowViewer.VIEW_FUZZING_PANEL);

			}
		});

		area.addMouseListener(new MouseAdapter() {
			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				checkForTriggerEvent(e);
			}
		});
	}



}
