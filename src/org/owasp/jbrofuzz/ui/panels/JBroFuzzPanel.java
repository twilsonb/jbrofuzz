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
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.actions.*;
import org.owasp.jbrofuzz.ui.viewers.PropertiesViewer;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * The super class that is extended for every panel that is implemented.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.1
 */
public abstract class JBroFuzzPanel extends JPanel {

	private JBroFuzzWindow frame;

	private static final long serialVersionUID = -88328054872L;

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
	 * options can be found in the tool bar as well as the menu bar under the 
	 * FileMenuItem "Panel".</p>
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
	
	public final void setOptionRemove(boolean remove) {
		
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
	 * <p>Method for setting up the right click copy paste cut and select all menu.</p>
	 * <p>It passes the parameters of which options in the right click menu are enabled.</p>
	 * @param area
	 *            JTextArea
	 */
	public final void popupText(final JTextComponent area, boolean cut, boolean copy, boolean paste, boolean selectAll) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i1_cut = new JMenuItem("Cut");
		final JMenuItem i2_copy = new JMenuItem("Copy");
		final JMenuItem i3_paste = new JMenuItem("Paste");
		final JMenuItem i4_select = new JMenuItem("Select All");

		i1_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		i2_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		i3_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		i4_select.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.addSeparator();
		popmenu.add(i4_select);

		i1_cut.setEnabled(cut);
		i2_copy.setEnabled(copy);
		i3_paste.setEnabled(paste);
		i4_select.setEnabled(selectAll);
		
		if (!area.isEditable()) {
			i3_paste.setEnabled(false);
		}

		i1_cut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.cut();
			}
		});

		i2_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.copy();
			}
		});

		i3_paste.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (area.isEditable()) {
					area.paste();
				}
			}
		});

		i4_select.addActionListener(new ActionListener() {
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

	public final void popupTable(final JTable area, boolean open, boolean cut, boolean copy, boolean paste, boolean selectAll, boolean properties) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i0_open = new JMenuItem("Open in Browser");
		final JMenuItem i1_cut = new JMenuItem(new CutAction());
		final JMenuItem i2_copy = new JMenuItem(new CopyAction());
		final JMenuItem i3_paste = new JMenuItem(new PasteAction());
		final JMenuItem i4_select = new JMenuItem(new SelectAllAction());
		final JMenuItem i5_props = new JMenuItem("Properties");

		i0_open.setEnabled(open);
		i1_cut.setEnabled(cut);
		i2_copy.setEnabled(copy);
		i3_paste.setEnabled(paste);
		i4_select.setEnabled(selectAll);
		i5_props.setEnabled(properties);

		popmenu.add(i0_open);
		popmenu.addSeparator();
		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.add(i4_select);
		popmenu.addSeparator();
		popmenu.add(i5_props);

		// Open in Browser
		i0_open.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				JTabbedPane pane = getFrame().getTp();
				final int d = pane.getSelectedIndex();
				if(d >= 0) {


					String s = ((JBroFuzzPanel)pane.getComponent(d)).getName();

					if(s.equalsIgnoreCase(getFrame().getPanelFuzzing().getName())) {

						Browser.init();
						final String fileName = (String) area.getModel().getValueAt(area.convertRowIndexToModel(area.getSelectedRow()) , 0) + ".html";
						final File f = getFrame().getJBroFuzz().getHandler().getFuzzFile(fileName);

						try {
							Browser.displayURL(f.toURI().toString());
						} 
						catch (final IOException ex) {
							getFrame().log("Could not launch link in external browser");
						}
					}
					
					
				} // tab selection
			}
		});

		// Cut
		i1_cut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// No need to implement as tables are read only
			}
		});

		// Copy
		i2_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();

				for (final int element : selection) {
					for (int i = 0; i < area.getColumnCount(); i++) {

						selectionBuffer.append(area.getModel().getValueAt(area.convertRowIndexToModel(element), i));
						if (i < area.getColumnCount() - 1) {
							selectionBuffer.append(",");
						}

					}
					selectionBuffer.append("\n");
				}

				final JTextArea myTempArea = new JTextArea();
				myTempArea.setText(selectionBuffer.toString());
				myTempArea.selectAll();
				myTempArea.copy();
				// area.removeRowSelectionInterval(0, area.getRowCount() - 1 );

			}
		});

		// Paste
		i3_paste.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// No need to implement as tables are read only
			}
		});

		// Select All
		i4_select.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		// Properties
		i5_props.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {



				JTabbedPane pane = getFrame().getTp();
				final int d = pane.getSelectedIndex();
				if(d >= 0) {

					String s = ((JBroFuzzPanel)pane.getComponent(d)).getName();

					if(s.equalsIgnoreCase(getFrame().getPanelFuzzing().getName())) {

						// If multiple rows are selected the first row is the one
						final int c = area.getSelectedRow();
						final String name = (String) area.getModel().getValueAt(area.convertRowIndexToModel(c), 0);
						new WindowViewer(JBroFuzzPanel.this, name, WindowViewer.VIEW_FUZZING_PANEL);

					}

					if(s.equalsIgnoreCase(getFrame().getPanelPayloads().getName())) {

						final String payload = (String) area.getModel().getValueAt(area.getSelectedRow(), 0);
						new PropertiesViewer(JBroFuzzPanel.this, "Payload Information", payload);

					}

					if(s.equalsIgnoreCase(getFrame().getPanelSniffing().getName())) {

						Runtime.getRuntime().gc();
						Runtime.getRuntime().runFinalization();

						final int c = area.getSelectedRow();
						final String name = (String) area.getModel().getValueAt(c, 0);
						new WindowViewer(JBroFuzzPanel.this, name.split(" ")[0], WindowViewer.VIEW_SNIFFING_PANEL);


					}
					
					if(s.equalsIgnoreCase(getFrame().getPanelWebDirectories().getName())) {
						
						StringBuffer output = new StringBuffer();

						for (int i = 0; i < area.getColumnCount(); i++) {

							output.append(area.getColumnName(i) + ": ");
							output.append(area.getModel().getValueAt(area.convertRowIndexToModel(area.getSelectedRow()), i));
							output.append("\n");
						}

						new PropertiesViewer(JBroFuzzPanel.this, "Properties", output.toString());


					}
				}

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

	/**
	 * <p>Method for completely expanding or collapsing a given 
	 * <code>JTree</code>.</p>
	 * <p>Originally from the Java Developers Almanac 1.4
	 * 
	 * @param tree The JTree to be expanded/collapsed
	 * @param parent The parent TreePath from which to begin
	 * @param expand If true, expands all nodes in the tree, else collapse all nodes.
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
    
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

}
