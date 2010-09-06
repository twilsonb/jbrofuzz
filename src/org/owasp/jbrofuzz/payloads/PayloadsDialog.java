/**
 * JBroFuzz 2.3
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
package org.owasp.jbrofuzz.payloads;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.tablemodels.SingleColumnModel;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The payloads dialog that appears when a user selects to add a payload.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 */
public class PayloadsDialog extends JDialog {

	private class CategoriesRowListener implements ListSelectionListener {
		/**
		 * <p>
		 * Method for the category table selection row.
		 * </p>
		 * 
		 * @param event
		 *            ListSelectionEvent
		 */
		public void valueChanged(final ListSelectionEvent event) {

			if (event.getValueIsAdjusting()) {
				return;
			}

			String value;
			int count = categoriesTable.getSelectedRow();
			try {

				count = categoriesTable.convertRowIndexToModel(count);
				value = (String) catTableModel.getValueAt(count, 0);

			} catch (final IndexOutOfBoundsException e) {
				return;
			}

			fuzzersTable.setRowSorter(null);
			fTableModel.setData(mWindow.getJBroFuzz().getDatabase()
					.getPrototypeNamesInCategory(value));
			sorter2 = new TableRowSorter<SingleColumnModel>(fTableModel);
			fuzzersTable.setRowSorter(sorter2);

			payloadsTable.setRowSorter(null);
			pTableModel.setData(null);

			fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(" " + value + " "),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));

			payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(" Select a Fuzzer "),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));

			fInfoArea.setText("");
			fInfoArea.setCaretPosition(0);

		}
	}

	private class FuzzersRowListener implements ListSelectionListener {
		/**
		 * <p>
		 * Method for the name table selection row.
		 * </p>
		 * 
		 * @param event
		 *            ListSelectionEvent
		 */
		public void valueChanged(final ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}

			String name;
			int dount = fuzzersTable.getSelectedRow();
			try {

				dount = fuzzersTable.convertRowIndexToModel(dount);
				name = (String) fTableModel.getValueAt(dount, 0);

			} catch (final IndexOutOfBoundsException e) {
				return;
			}
			final String fuzzerID = mWindow.getJBroFuzz().getDatabase().getIdFromName(name);

			pTableModel.setData(mWindow.getJBroFuzz().getDatabase()
					.getPayloads(fuzzerID));
			// If there are rows in the payloads, we have a fuzzer to add
			if (pTableModel.getRowCount() > 0) {

				okBut.setEnabled(true);

				payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(" "
								+ mWindow.getJBroFuzz().getDatabase()
								.getPrototype(fuzzerID).getType() + " ID: "
								+ fuzzerID + " "), BorderFactory.createEmptyBorder(1,
										1, 1, 1)));

				fInfoArea.setText("\nFuzzer Name: "
						+ name
						+ "\n"
						+ "Fuzzer Type: "
						+ mWindow.getJBroFuzz().getDatabase().getPrototype(fuzzerID)
						.getType() + "\n" + "Fuzzer Id:   " + fuzzerID
						+ "\n\n" + "Total Number of Payloads: "
						+ mWindow.getJBroFuzz().getDatabase().getSize(fuzzerID));
				fInfoArea.setCaretPosition(fInfoArea
						.getText().length());
			} else {
				okBut.setEnabled(false);
			}

		}
	}

	private static final long serialVersionUID = -1083415577221148132L;

	// Dimensions of the generator dialog box
	private static final int SIZE_X = 680;
	private static final int SIZE_Y = 400;

	// The names of the table areas as String variables
	private static final String NAME_CATEGORY = "Category-Table";
	private static final String NAME_FUZZER = "Fuzzer-Table";
	private static final String NAME_PAYLOAD = "Payload-Table";

	// The buttons
	private final JButton okBut;

	// The frame that the sniffing panel is attached
	private final JBroFuzzWindow mWindow;

	// The JPanels carrying the components
	// private final JPanel categoriesPanel;
	private final JPanel fuzzersPanel, payloadsPanel;

	// The JTables carrying the data
	private final JTable categoriesTable, fuzzersTable, payloadsTable;

	// The Table Models with a single column
	private final SingleColumnModel catTableModel;
	private final SingleColumnModel fTableModel, pTableModel;

	// The row sorters for the two tables
	// private final TableRowSorter<SingleColumnModel> sorter;
	private TableRowSorter<SingleColumnModel> sorter2;

	// The non-wrapping text pane
	private final NonWrappingTextPane fInfoArea;

	/**
	 * <p>
	 * Method for constructing a Generator Dialog and then passing the selection
	 * to the HTTP Fuzzing panel generator table.
	 * </p>
	 * 
	 * @param parent
	 * @param start
	 * @param end
	 */
	public PayloadsDialog(final AbstractPanel parent, final int start,
			final int end) {

		super(parent.getFrame(), " Add a Fuzzer ", true);
		setFont(new Font("SansSerif", Font.BOLD, 10));

		setLayout(null);
		mWindow = parent.getFrame();

		// Categories : The area on the left, displaying the categories table

		final JPanel categoriesPanel = new JPanel();
		categoriesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Categories "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		categoriesPanel.setBounds(10, 20, 190, SIZE_Y - 70);
		this.add(categoriesPanel);

		catTableModel = new SingleColumnModel(" Category Name ");
		categoriesTable = new JTable(catTableModel);
		categoriesTable.setName(NAME_CATEGORY);
		popup(categoriesTable);

		categoriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		catTableModel.setData(mWindow.getJBroFuzz().getDatabase()
				.getAllCategories());

		final TableRowSorter<SingleColumnModel> sorter = 
			new TableRowSorter<SingleColumnModel>(catTableModel);
		categoriesTable.setRowSorter(sorter);

		categoriesTable.setFont(new Font("Verdana", Font.BOLD, 10));
		categoriesTable.setRowHeight(30);
		categoriesTable.getSelectionModel().addListSelectionListener(
				new CategoriesRowListener());
		categoriesTable.setBackground(Color.BLACK);
		categoriesTable.setForeground(Color.WHITE);

		categoriesTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent kEvent) {
				if (kEvent.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});

		final JScrollPane catScrollPane = new JScrollPane(
				categoriesTable);
		catScrollPane.setVerticalScrollBarPolicy(20);
		catScrollPane.setHorizontalScrollBarPolicy(30);
		catScrollPane.setPreferredSize(new Dimension(170, SIZE_Y - 110));
		categoriesPanel.add(catScrollPane);

		// Fuzzers : The middle area displaying the list of fuzzers by name

		fuzzersPanel = new JPanel();
		fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Select a Category "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		fuzzersPanel.setBounds(210, 20, 180, 250);
		this.add(fuzzersPanel);

		fTableModel = new SingleColumnModel(" Fuzzer Name ");
		fuzzersTable = new JTable(fTableModel);
		fuzzersTable.setName(NAME_FUZZER);
		popup(fuzzersTable);

		sorter2 = new TableRowSorter<SingleColumnModel>(fTableModel);
		fuzzersTable.setRowSorter(sorter2);

		fuzzersTable.setFont(new Font("Verdana", Font.BOLD, 10));
		fuzzersTable.setRowHeight(30);
		fuzzersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fuzzersTable.getSelectionModel().addListSelectionListener(
				new FuzzersRowListener());
		fuzzersTable.setBackground(Color.BLACK);
		fuzzersTable.setForeground(Color.WHITE);
		/*
		 * fuzzersTable.addMouseListener(new MouseAdapter(){ @Override public
		 * void mouseClicked(final MouseEvent e){ if (e.getClickCount() == 1){
		 * okBut.setEnabled(true); // String exploit = (String)
		 * nameTable.getModel().getValueAt(nameTable.getSelectedRow(), 0); //
		 * new ExploitViewer(mWindow, exploit, ExploitViewer.VIEW_EXPLOIT); } } } );
		 */
		fuzzersTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent kEvent) {
				if (kEvent.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});

		final JScrollPane nameScrollPane = new JScrollPane(
				fuzzersTable);
		nameScrollPane.setVerticalScrollBarPolicy(20);
		nameScrollPane.setHorizontalScrollBarPolicy(30);
		nameScrollPane
		.setPreferredSize(new Dimension(160, SIZE_Y - 190));
		fuzzersPanel.add(nameScrollPane);

		// Payloads : A JSplitPane with a table and a textArea

		payloadsPanel = new JPanel();
		payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Select a Fuzzer "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		payloadsPanel.setBounds(400, 20, 260, 190);
		payloadsPanel.setLayout(new BoxLayout(payloadsPanel, BoxLayout.Y_AXIS));
		this.add(payloadsPanel);

		pTableModel = new SingleColumnModel(" Payloads ");
		payloadsTable = new JTable(pTableModel);
		payloadsTable.setName(NAME_PAYLOAD);
		popup(payloadsTable);

		payloadsTable.setFont(new Font("Verdana", Font.BOLD, 10));
		payloadsTable.setRowHeight(30);
		payloadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// payloadsTable.getSelectionModel().addListSelectionListener(
		// new PayloadsRowListener());
		payloadsTable.setBackground(Color.BLACK);
		payloadsTable.setForeground(Color.WHITE);

		final JScrollPane plScrollPane = new JScrollPane(
				payloadsTable);
		plScrollPane.setVerticalScrollBarPolicy(20);
		plScrollPane.setHorizontalScrollBarPolicy(30);
		plScrollPane.setPreferredSize(new Dimension(200, SIZE_Y - 190));
		// payloadsPanel.add(payloadsTableScrollPane);

		fInfoArea = new NonWrappingTextPane();

		fInfoArea.putClientProperty("charset", "UTF-8");
		fInfoArea.setEditable(false);
		fInfoArea.setVisible(true);
		fInfoArea.setFont(new Font("Verdana", Font.BOLD, 10));
		fInfoArea.setMargin(new Insets(1, 1, 1, 1));
		fInfoArea.setBackground(Color.WHITE);
		fInfoArea.setForeground(Color.BLACK);

		// Right click: Cut, Copy, Paste, Select All
		AbstractPanel.popupText(fInfoArea, false, true, false, true);

		fInfoArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent kEvent) {
				if (kEvent.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});
		final JScrollPane fuzzerInfoScrollPane = new JScrollPane(
				fInfoArea);
		fuzzerInfoScrollPane.setVerticalScrollBarPolicy(20);
		fuzzerInfoScrollPane.setHorizontalScrollBarPolicy(30);
		fuzzerInfoScrollPane.setPreferredSize(new Dimension(150, 100));
		// payloadsPanel.add(viewTextScrollPane);

		final JSplitPane payloadsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		payloadsSplitPane.setOneTouchExpandable(false);
		payloadsSplitPane.setTopComponent(plScrollPane);
		payloadsSplitPane.setBottomComponent(fuzzerInfoScrollPane);
		payloadsSplitPane.setDividerLocation(150);

		payloadsPanel.add(plScrollPane);

		// Allow for all areas to be resized to even not be seen
		payloadsTable.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		fInfoArea.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		// Bottom button
		okBut = new JButton(" Add Fuzzer ", ImageCreator.IMG_ADD);
		okBut.setBounds(515, 305, 140, 40);
		okBut.setEnabled(false);

		okBut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						final int selRow = fuzzersTable.getSelectedRow();
						final String name = (String) fTableModel
						.getValueAt(fuzzersTable
								.convertRowIndexToModel(selRow), 0);

						final String selID = mWindow.getJBroFuzz().
						getDatabase().getIdFromName(name);

						mWindow.getPanelFuzzing().addFuzzer(selID, new String[]{EncoderHashCore.CODES[0]}, start, end);
						PayloadsDialog.this.dispose();

					}
				});
			}
		});

		okBut.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent kEvent) {
				if (kEvent.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});

		okBut
		.setToolTipText("The selected category will be added to the fuzzing list");
		getContentPane().add(okBut);

		// Global frame issues

		this.setLocation(parent.getFrame().getLocation().x + 100,
				parent.getFrame().getLocation().y + 100);
		this.setSize(PayloadsDialog.SIZE_X, PayloadsDialog.SIZE_Y);
		setResizable(true);
		setVisible(true);
	}

	/**
	 * <p>
	 * Method for setting up the right click copy, select all and properties
	 * menu.
	 * </p>
	 * 
	 * @param area
	 *            JTextArea
	 */
	private void popup(final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i1_cut = new JMenuItem("Cut");
		final JMenuItem i2_copy = new JMenuItem("Copy");
		final JMenuItem i3_paste = new JMenuItem("Paste");
		final JMenuItem i4_select = new JMenuItem("Select All");
		final JMenuItem i5_properties = new JMenuItem("Properties");

		i1_cut.setEnabled(false);
		i2_copy.setEnabled(true);
		i3_paste.setEnabled(false);
		i4_select.setEnabled(false);

		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.add(i4_select);
		popmenu.addSeparator();
		popmenu.add(i5_properties);

		// Copy

		i2_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				final int selRow = area.getSelectedRow();
				final String value = (String) area.getModel().getValueAt(selRow, 0);

				final StringSelection ss = new StringSelection(value);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
						ss, null);

			}
		});

		// Properties : Points to the Payloads Tab

		i5_properties.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if (area.getName().equalsIgnoreCase(PayloadsDialog.NAME_FUZZER)) {

					final int c = fuzzersTable.getSelectedRow();
					fuzzersTable.getSelectionModel().setSelectionInterval(
							fuzzersTable.convertRowIndexToModel(c),
							fuzzersTable.convertRowIndexToModel(c));
					final String fuzzer = (String) fTableModel
					.getValueAt(fuzzersTable.convertRowIndexToModel(c),
							0);

					final int d = categoriesTable.getSelectedRow();
					final String category = (String) catTableModel.getValueAt(
							categoriesTable.convertRowIndexToModel(d), 0);

					PayloadsDialog.this.dispose();

					mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
					mWindow.getPanelPayloads().setCategoryDisplayed(category);
					mWindow.getPanelPayloads().setFuzzerDisplayed(fuzzer, category);

				}

				if (area.getName().equalsIgnoreCase(
						PayloadsDialog.NAME_CATEGORY)) {

					final int c = categoriesTable.getSelectedRow();
					categoriesTable.getSelectionModel().setSelectionInterval(
							categoriesTable.convertRowIndexToModel(c),
							categoriesTable.convertRowIndexToModel(c));
					final String value = (String) catTableModel
					.getValueAt(categoriesTable
							.convertRowIndexToModel(c), 0);

					PayloadsDialog.this.dispose();

					mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
					mWindow.getPanelPayloads().setCategoryDisplayed(value);

				}

				if (area.getName()
						.equalsIgnoreCase(PayloadsDialog.NAME_PAYLOAD)) {

					final int c = fuzzersTable.getSelectedRow();
					fuzzersTable.getSelectionModel().setSelectionInterval(
							fuzzersTable.convertRowIndexToModel(c),
							fuzzersTable.convertRowIndexToModel(c));
					final String fuzzer = (String) fTableModel
					.getValueAt(fuzzersTable.convertRowIndexToModel(c),
							0);

					final int selCat = categoriesTable.getSelectedRow();
					categoriesTable.getSelectionModel().setSelectionInterval(
							categoriesTable.convertRowIndexToModel(selCat),
							categoriesTable.convertRowIndexToModel(selCat));
					final String category = (String) catTableModel.getValueAt(
							categoriesTable.convertRowIndexToModel(selCat), 0);

					final int k = payloadsTable.getSelectedRow();
					payloadsTable.getSelectionModel()
					.setSelectionInterval(k, k);
					final String payload = (String) pTableModel.getValueAt(k,
							0);

					PayloadsDialog.this.dispose();

					mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);

					mWindow.getPanelPayloads().setCategoryDisplayed(category);
					mWindow.getPanelPayloads().setFuzzerDisplayed(fuzzer, category);
					mWindow.getPanelPayloads().setPayloadDisplayed(payload, fuzzer,
							category);

				}

			}
		});

		area.addMouseListener(new MouseAdapter() {

			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();

					final JTable myTable = (JTable) e.getSource();
					final int c = myTable.rowAtPoint(new Point(e.getX(), e
							.getY()));

					myTable.getSelectionModel().setAnchorSelectionIndex(c);
					myTable.setRowSelectionInterval(c, c);
					myTable.getSelectionModel().setSelectionInterval(c, c);

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
