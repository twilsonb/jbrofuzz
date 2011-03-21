/**
 * JbroFuzz 2.5
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
package org.owasp.jbrofuzz.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.actions.CopyAction;
import org.owasp.jbrofuzz.ui.actions.CutAction;
import org.owasp.jbrofuzz.ui.actions.PasteAction;
import org.owasp.jbrofuzz.ui.actions.SelectAllAction;
import org.owasp.jbrofuzz.ui.viewers.PropertiesViewer;
import org.owasp.jbrofuzz.ui.viewers.WindowViewerFrame;
import org.owasp.jbrofuzz.version.ImageCreator;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * The super class that is extended for every panel that is implemented.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.9
 * @since 1.5
 */
public abstract class AbstractPanel extends JPanel {

	private static final long serialVersionUID = -4932876100272401793L;

	private final JBroFuzzWindow frame;

	private final String name;

	private final boolean[] optionsAvailable;

	/**
	 * <p>
	 * Constructor, creating a panel and allocating the given String as name to
	 * that panel.
	 * </p>
	 * <p>
	 * The parent frame to which this panel belongs to is also required at
	 * construction.
	 * </p>
	 * 
	 * @param name
	 * @param frame
	 */
	public AbstractPanel(final String name, final JBroFuzzWindow frame) {

		super();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		setOpaque(false);
		this.name = name;
		this.frame = frame;
		optionsAvailable = new boolean[] { false, false, false, false, false };

	}

	/**
	 * <p>
	 * Method describing what happens in a panel when the 'add' button is
	 * pressed.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public abstract void add();

	/**
	 * <p>
	 * Method for completely expanding or collapsing a given <code>JTree</code>.
	 * </p>
	 * 
	 * <p>
	 * Originally, from the Java Developers Almanac 1.4.
	 * </p>
	 * 
	 * @param tree
	 *            The JTree to be expanded/collapsed
	 * @param parent
	 *            The parent TreePath from which to begin
	 * @param expand
	 *            If true, expands all nodes in the tree, else collapse all
	 *            nodes.
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	@SuppressWarnings("unchecked")
	public void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		final TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {

			for (final Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {

				final TreeNode n = e.nextElement();
				final TreePath path = parent.pathByAddingChild(n);
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

	

	/**
	 * <p>
	 * Obtain the JBroFuzzWindow parent frame.
	 * </p>
	 * 
	 * @return JBroFuzzWindow
	 */
	public final JBroFuzzWindow getFrame() {

		return frame;

	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	public int getOptionsLength() {

		return optionsAvailable.length;

	}

	/**
	 * <p>
	 * Method describing what happens in a panel when the 'pause' button is
	 * pressed.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.8
	 */
	public abstract void pause();

	public boolean isAddedEnabled() {

		return optionsAvailable[3];

	}

	public boolean isPauseEnabled() {

		return optionsAvailable[2];

	}

	public boolean isRemovedEnabled() {

		return optionsAvailable[4];

	}

	public boolean isStartedEnabled() {

		return optionsAvailable[0];

	}

	public boolean isStoppedEnabled() {

		return optionsAvailable[1];
	}

	public final void popupTable(final JTable area, boolean open, boolean cut,
			boolean copy, boolean paste, boolean selectAll, boolean properties) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i0_open = new JMenuItem("Open in Browser");
		final JMenuItem i1_cut = new JMenuItem(new CutAction());
		final JMenuItem i2_copy = new JMenuItem(new CopyAction());
		final JMenuItem i3_paste = new JMenuItem(new PasteAction());
		final JMenuItem i4_select = new JMenuItem(new SelectAllAction());
		final JMenuItem i5_props = new JMenuItem("Properties");

		i0_open.setIcon(ImageCreator.IMG_OPENINBROWSER);
		i1_cut.setIcon(ImageCreator.IMG_CUT);
		i2_copy.setIcon(ImageCreator.IMG_COPY);
		i3_paste.setIcon(ImageCreator.IMG_PASTE);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);

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

				final JTabbedPane pane = getFrame().getTp();
				final int d = pane.getSelectedIndex();
				if (d >= 0) {

					final String s = ((AbstractPanel) pane.getComponent(d)).getName();

					if (s.equalsIgnoreCase(getFrame().getPanelFuzzing()
							.getName())) {

						final int c = area.getSelectedRow();
						if (c < 0) {
							return;
						}
						final String fileName = (String) area.getModel()
						.getValueAt(area.convertRowIndexToModel(c), 0)
						+ ".html";
						final String sURI = getFrame().getJBroFuzz().getStorageHandler()
						.getFuzzURIString(fileName);

						Browser.init();
						try {
							Browser.displayURL(sURI);
						} catch (final IOException ex) {
							Logger
							.log(
									"Could not launch link in external browser",
									3);
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

				final StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();

				for (final int element : selection) {
					for (int i = 0; i < area.getColumnCount(); i++) {

						selectionBuffer.append(area.getModel().getValueAt(
								area.convertRowIndexToModel(element), i));
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

				final JTabbedPane pane = getFrame().getTp();
				final int d = pane.getSelectedIndex();
				if (d >= 0) {

					final String s = ((AbstractPanel) pane.getComponent(d)).getName();

					if (s.equalsIgnoreCase(getFrame().getPanelFuzzing()
							.getName())) {

						// If multiple rows are selected the first row is the
						// one
						final int c = area.getSelectedRow();
						if (c < 0) {
							return;
						}
						final String name = (String) area.getModel()
						.getValueAt(area.convertRowIndexToModel(c), 0);
						
						final String directory = getFrame().getJBroFuzz().getStorageHandler().getLocationURIString();
						final File selFile = new File(directory, name);
						
						new WindowViewerFrame(AbstractPanel.this, selFile);

					}

					if (s.equalsIgnoreCase(getFrame().getPanelPayloads()
							.getName())) {

						final String payload = (String) area.getModel()
						.getValueAt(area.getSelectedRow(), 0);
						new PropertiesViewer(AbstractPanel.this,
								"Payload Information", payload);

					}

					if (s.equalsIgnoreCase(getFrame().getPanelWebDirectories()
							.getName())) {

						final StringBuffer output = new StringBuffer();

						for (int i = 0; i < area.getColumnCount(); i++) {

							output.append(area.getColumnName(i) + ": ");
							output.append(area.getModel().getValueAt(
									area.convertRowIndexToModel(area
											.getSelectedRow()), i));
							output.append("\n");
						}

						new PropertiesViewer(AbstractPanel.this, "Properties",
								output.toString());

					}
				}

			}
		});

		area.addMouseListener(new MouseAdapter() {

			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {

					final Point point = e.getPoint();
					final int row = area.rowAtPoint(point);
					if (row < 0) {
						return;
					}
					area.getSelectionModel().setSelectionInterval(row, row);

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
	 * <p>
	 * Method for setting up the right click copy paste cut and select all menu.
	 * </p>
	 * <p>
	 * It passes the parameters of which options in the right click menu are
	 * enabled.
	 * </p>
	 * 
	 * @param area
	 *            JTextArea
	 */
	public static final void popupText(final JTextComponent area, boolean cut,
			boolean copy, boolean paste, boolean selectAll) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i1_cut = new JMenuItem("Cut");
		final JMenuItem i2_copy = new JMenuItem("Copy");
		final JMenuItem i3_paste = new JMenuItem("Paste");
		final JMenuItem i4_select = new JMenuItem("Select All");

		i1_cut.setIcon(ImageCreator.IMG_CUT);
		i2_copy.setIcon(ImageCreator.IMG_COPY);
		i3_paste.setIcon(ImageCreator.IMG_PASTE);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);

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

	/**
	 * <p>
	 * Method describing what happens in a panel when the 'remove' button is
	 * pressed.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public abstract void remove();

	public final void setOptionRemove(boolean remove) {

		optionsAvailable[4] = remove;

		getFrame().getJBroToolBar().setEnabledPanelOptions(optionsAvailable);
		getFrame().getJBroMenuBar().setEnabledPanelOptions(optionsAvailable);

	}

	/**
	 * <p>
	 * Method for setting the options available for that particular panel. These
	 * options can be found in the tool bar as well as the menu bar under the
	 * FileMenuItem "Panel".
	 * </p>
	 * 
	 * @param start
	 * @param stop
	 * @param pause
	 * @param add
	 * @param remove
	 */
	public final void setOptionsAvailable(boolean start, boolean stop,
			boolean pause, boolean add, boolean remove) {

		optionsAvailable[0] = start;
		optionsAvailable[1] = stop;
		// optionsAvailable[2] = graph;
		optionsAvailable[3] = add;
		optionsAvailable[4] = remove;

		getFrame().getJBroToolBar().setEnabledPanelOptions(optionsAvailable);
		getFrame().getJBroMenuBar().setEnabledPanelOptions(optionsAvailable);

	}

	/**
	 * <p>
	 * Method describing what action a particular panel can start.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public abstract void start();

	/**
	 * <p>
	 * Method describing what action a particular panel stops, when the stop
	 * button is pressed.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public abstract void stop();

}
