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
package org.owasp.jbrofuzz.ui.menu;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.panels.*;
import org.owasp.jbrofuzz.ui.tablemodels.*;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;

/**
 * <p>
 * The payloads dialog that appears when a user selects to add a payload.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
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

			// ok.setEnabled(false);

			final int c = categoriesTable.getSelectedRow();
			final String value = (String) categoriesTableModel.getValueAt(c, 0);

			fuzzersTableModel.setData(m.getJBroFuzz().getDatabase().getGenerators(value));

			fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(" " + value + " "),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));

			fuzzerInfoTextArea.setText("");
			fuzzerInfoTextArea.setCaretPosition(0);

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

			final int d = fuzzersTable.getSelectedRow();
			final String name = (String) fuzzersTableModel.getValueAt(d, 0);
			final String id = m.getJBroFuzz().getDatabase().getIdFromName(name);

			payloadsTableModel.setData(m.getJBroFuzz().getDatabase()
					.getPayloads(id));
			// If there are rows in the payloads, we have a fuzzer to add
			if (payloadsTableModel.getRowCount() > 0) {

				ok.setEnabled(true);

				payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(" "
								+ ((m.getJBroFuzz().getDatabase().getGenerator(
										id).isRecursive()) ? "Recursive"
										: "Replacive") + " ID: " + id + " "),
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));

				fuzzerInfoTextArea.setText("\nFuzzer Name: "
						+ name
						+ "\n"
						+ "Fuzzer Type: "
						+ ((m.getJBroFuzz().getDatabase().getGenerator(id)
								.isRecursive()) ? "Recursive" : "Replacive")
						+ "\n" + "Fuzzer Id:   " + id + "\n\n"
						+ "Total Number of Payloads: "
						+ m.getJBroFuzz().getDatabase().getSize(id));
				fuzzerInfoTextArea.setCaretPosition(fuzzerInfoTextArea
						.getText().length());
			} else {
				ok.setEnabled(false);
			}

			// payloadInfoTextArea.setText("");
			// payloadInfoTextArea.setCaretPosition(0);

		}
	}
	private class PayloadsRowListener implements ListSelectionListener {

		public void valueChanged(final ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}

			// ok.setEnabled(false);
		}
	}
	
	private static final long serialVersionUID = -1083415577221148132L;
	
	// Dimensions of the generator dialog box
	private static final int x = 680;
	private static final int y = 400;

	// The names of the table areas as String variables
	private static final String NAME_CATEGORY = "Category-Table";
	private static final String NAME_FUZZER = "Fuzzer-Table";
	private static final String NAME_PAYLOAD = "Payload-Table";
	
	// The buttons
	private JButton ok;
	
	// The frame that the sniffing panel is attached
	private JBroFuzzWindow m;
	
	// The JPanels carrying the components
	private JPanel categoriesPanel, fuzzersPanel, payloadsPanel;

	/*
	 * private int start, end; // The last category accessed private String
	 * lastCategory;
	 */

	// The JTables carrying the data
	private JTable categoriesTable, fuzzersTable, payloadsTable;

	// The Table Models with a single column
	private SingleColumnModel categoriesTableModel, fuzzersTableModel,
			payloadsTableModel;

	// The non-wrapping text pane
	private NonWrappingTextPane fuzzerInfoTextArea;

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
	public PayloadsDialog(final JBroFuzzPanel parent, final int start, final int end) {

		super(parent.getFrame(), " Add a Fuzzer ", true);
		setFont(new Font("SansSerif", Font.BOLD, 10));

		setLayout(null);
		m = parent.getFrame();
		// this.start = start;
		// this.end = end;
		// lastCategory = "";

		// Categories : The area on the left, displaying the categories table

		categoriesPanel = new JPanel();
		categoriesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Categories "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));
		categoriesPanel.setBounds(10, 20, 190, y - 70);
		this.add(categoriesPanel);

		categoriesTableModel = new SingleColumnModel(" Category Name ");
		categoriesTable = new JTable(categoriesTableModel);
		categoriesTable.setName(NAME_CATEGORY);
		popup(categoriesTable);

		categoriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoriesTableModel.setData(m.getJBroFuzz().getDatabase()
				.getAllCategories());

		categoriesTable.setFont(new Font("Verdana", Font.BOLD, 10));
		categoriesTable.setRowHeight(30);
		categoriesTable.getSelectionModel().addListSelectionListener(new CategoriesRowListener());
		categoriesTable.setBackground(Color.BLACK);
		categoriesTable.setForeground(Color.WHITE);

		categoriesTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});

		final JScrollPane categoryTableScrollPane = new JScrollPane(
				categoriesTable);
		categoryTableScrollPane.setVerticalScrollBarPolicy(20);
		categoryTableScrollPane.setHorizontalScrollBarPolicy(30);
		categoryTableScrollPane.setPreferredSize(new Dimension(170, y - 110));
		categoriesPanel.add(categoryTableScrollPane);

		// Fuzzers : The middle area displaying the list of fuzzers by name

		fuzzersPanel = new JPanel();
		fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Select a Category "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		fuzzersPanel.setBounds(210, 20, 180, 250);
		this.add(fuzzersPanel);

		fuzzersTableModel = new SingleColumnModel(" Fuzzer Name ");
		fuzzersTable = new JTable(fuzzersTableModel);
		fuzzersTable.setName(NAME_FUZZER);
		popup(fuzzersTable);

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
		 * ok.setEnabled(true); // String exploit = (String)
		 * nameTable.getModel().getValueAt(nameTable.getSelectedRow(), 0); //
		 * new ExploitViewer(m, exploit, ExploitViewer.VIEW_EXPLOIT); } } } );
		 */
		fuzzersTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});

		final JScrollPane nameTextAreaTextScrollPane = new JScrollPane(
				fuzzersTable);
		nameTextAreaTextScrollPane.setVerticalScrollBarPolicy(20);
		nameTextAreaTextScrollPane.setHorizontalScrollBarPolicy(30);
		nameTextAreaTextScrollPane
				.setPreferredSize(new Dimension(160, y - 190));
		fuzzersPanel.add(nameTextAreaTextScrollPane);

		// Payloads : A JSplitPane with a table and a textArea

		payloadsPanel = new JPanel();
		payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Select a Fuzzer "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		payloadsPanel.setBounds(400, 20, 260, 190);
		payloadsPanel.setLayout(new BoxLayout(payloadsPanel, BoxLayout.Y_AXIS));
		this.add(payloadsPanel);

		payloadsTableModel = new SingleColumnModel(" Payloads ");
		payloadsTable = new JTable(payloadsTableModel);
		payloadsTable.setName(NAME_PAYLOAD);
		popup(payloadsTable);

		payloadsTable.setFont(new Font("Verdana", Font.BOLD, 10));
		payloadsTable.setRowHeight(30);
		payloadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		payloadsTable.getSelectionModel().addListSelectionListener(
				new PayloadsRowListener());
		payloadsTable.setBackground(Color.BLACK);
		payloadsTable.setForeground(Color.WHITE);

		final JScrollPane payloadsTableScrollPane = new JScrollPane(
				payloadsTable);
		payloadsTableScrollPane.setVerticalScrollBarPolicy(20);
		payloadsTableScrollPane.setHorizontalScrollBarPolicy(30);
		payloadsTableScrollPane.setPreferredSize(new Dimension(200, y - 190));
		// payloadsPanel.add(payloadsTableScrollPane);

		fuzzerInfoTextArea = new NonWrappingTextPane();

		fuzzerInfoTextArea.putClientProperty("charset", "UTF-8");
		fuzzerInfoTextArea.setEditable(false);
		fuzzerInfoTextArea.setVisible(true);
		fuzzerInfoTextArea.setFont(new Font("Verdana", Font.BOLD, 10));
		fuzzerInfoTextArea.setMargin(new Insets(1, 1, 1, 1));
		fuzzerInfoTextArea.setBackground(Color.WHITE);
		fuzzerInfoTextArea.setForeground(Color.BLACK);
		
		// Right click: Cut, Copy, Paste, Select All
		parent.popupText(fuzzerInfoTextArea, false, true, false, true);
		
		fuzzerInfoTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});
		final JScrollPane fuzzerInfoScrollPane = new JScrollPane(
				fuzzerInfoTextArea);
		fuzzerInfoScrollPane.setVerticalScrollBarPolicy(20);
		fuzzerInfoScrollPane.setHorizontalScrollBarPolicy(30);
		fuzzerInfoScrollPane.setPreferredSize(new Dimension(150, 100));
		// payloadsPanel.add(viewTextScrollPane);

		JSplitPane payloadsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		payloadsSplitPane.setOneTouchExpandable(false);
		payloadsSplitPane.setTopComponent(payloadsTableScrollPane);
		payloadsSplitPane.setBottomComponent(fuzzerInfoScrollPane);
		payloadsSplitPane.setDividerLocation(150);

		payloadsPanel.add(payloadsTableScrollPane);

		// Allow for all areas to be resized to even not be seen
		Dimension minimumSize = new Dimension(0, 0);
		payloadsTable.setMinimumSize(minimumSize);
		fuzzerInfoTextArea.setMinimumSize(minimumSize);

		// Bottom button
		ok = new JButton(" Add Fuzzer ", ImageCreator.ADD_IMG);
		ok.setBounds(515, 305, 140, 40);
		ok.setEnabled(false);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						final int c = fuzzersTable.getSelectedRow();
						final String name = (String) fuzzersTableModel.getValueAt(c, 0);
						final String id = m.getJBroFuzz().getDatabase().getIdFromName(name);
						m.getPanelFuzzing().addPayload(id, start, end);
						PayloadsDialog.this.dispose();

					}
				});
			}
		});
		ok.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});

		ok
				.setToolTipText("The selected category will be added to the fuzzing list");
		getContentPane().add(ok);

		// Global frame issues

		this.setLocation(Math.abs(parent.getLocation().x + 100), Math
				.abs(parent.getLocation().y + 100));
		this.setSize(PayloadsDialog.x, PayloadsDialog.y);
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

		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");
		final JMenuItem i5 = new JMenuItem("Properties");

		i1.setEnabled(false);
		i2.setEnabled(true);
		i3.setEnabled(false);
		i4.setEnabled(false);

		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.add(i4);
		popmenu.addSeparator();
		popmenu.add(i5);

		// Copy

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final int a = area.getSelectedRow();
				String value = (String) area.getModel().getValueAt(a, 0);

				StringSelection ss = new StringSelection(value);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
						ss, null);

			}
		});

		// Properties : Points to the Payloads Tab

		i5.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if (area.getName().equalsIgnoreCase(PayloadsDialog.NAME_FUZZER)) {

					final int c = fuzzersTable.getSelectedRow();
					fuzzersTable.getSelectionModel().setSelectionInterval(c, c);
					final String fuzzer = (String) fuzzersTableModel
							.getValueAt(c, 0);

					int d = categoriesTable.getSelectedRow();
					String category = (String) categoriesTableModel.getValueAt(
							d, 0);

					PayloadsDialog.this.dispose();

					m.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
					m.getPanelPayloads().setCategoryDisplayed(category);
					m.getPanelPayloads().setFuzzerDisplayed(fuzzer, category);

				}

				if (area.getName().equalsIgnoreCase(
						PayloadsDialog.NAME_CATEGORY)) {

					final int c = categoriesTable.getSelectedRow();
					categoriesTable.getSelectionModel().setSelectionInterval(c,
							c);
					final String value = (String) categoriesTableModel
							.getValueAt(c, 0);

					PayloadsDialog.this.dispose();

					m.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
					m.getPanelPayloads().setCategoryDisplayed(value);

				}

				if (area.getName()
						.equalsIgnoreCase(PayloadsDialog.NAME_PAYLOAD)) {

					final int c = fuzzersTable.getSelectedRow();
					fuzzersTable.getSelectionModel().setSelectionInterval(c, c);
					final String fuzzer = (String) fuzzersTableModel
							.getValueAt(c, 0);

					int d = categoriesTable.getSelectedRow();
					categoriesTable.getSelectionModel().setSelectionInterval(d,
							d);
					String category = (String) categoriesTableModel.getValueAt(
							d, 0);

					int k = payloadsTable.getSelectedRow();
					payloadsTable.getSelectionModel()
							.setSelectionInterval(k, k);
					String payload = (String) payloadsTableModel.getValueAt(k,
							0);

					PayloadsDialog.this.dispose();

					m.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
					m.getPanelPayloads().setCategoryDisplayed(category);
					m.getPanelPayloads().setFuzzerDisplayed(fuzzer, category);
					m.getPanelPayloads().setPayloadDisplayed(payload, fuzzer,
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
