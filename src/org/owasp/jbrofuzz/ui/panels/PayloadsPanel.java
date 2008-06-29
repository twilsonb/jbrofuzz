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
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableRowSorter;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.viewers.*;
import org.owasp.jbrofuzz.ui.tablemodels.*;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;

import org.apache.commons.lang.*;

/**
 * <p>
 * The definitions panel holding a description of the fuzzers loaded.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 */
public class PayloadsPanel extends JBroFuzzPanel {

	private class CategoriesRowListener implements ListSelectionListener {
		/**
		 * <p>
		 * Method for the categories table selection row.
		 * </p>
		 * 
		 * @param event
		 *            ListSelectionEvent
		 */
		public void valueChanged(final ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			final int c = categoriesTable.getSelectedRow();
			final String value = (String) categoriesTableModel.getValueAt(
					categoriesTable.convertRowIndexToModel(c), 0);

			fuzzersTableModel.setData(getFrame().getJBroFuzz().getDatabase()
					.getGenerators(value));

			fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(" " + value + " "),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));

			fuzzerInfoTextArea.setText("");
			fuzzerInfoTextArea.setCaretPosition(0);

			payloadInfoTextArea.setText("");
			payloadInfoTextArea.setCaretPosition(0);
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
			final String id = getFrame().getJBroFuzz().getDatabase()
					.getIdFromName(name);

			payloadsTableModel.setData(getFrame().getJBroFuzz().getDatabase()
					.getPayloads(id));

			if (payloadsTableModel.getRowCount() > 0) {
				payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(" " + name + " - "
								+ id + " "), BorderFactory.createEmptyBorder(1,
								1, 1, 1)));

				fuzzerInfoTextArea.setText("\nFuzzer Name: "
						+ name
						+ "\n"
						+ "Fuzzer Type: "
						+ ((getFrame().getJBroFuzz().getDatabase()
								.getGenerator(id).isRecursive()) ? "Recursive"
								: "Replacive") + "\n" + "Fuzzer Id:   " + id
						+ "\n\n" + "Total Number of Payloads: "
						+ getFrame().getJBroFuzz().getDatabase().getSize(id));
				fuzzerInfoTextArea.setCaretPosition(fuzzerInfoTextArea
						.getText().length());
			}

			payloadInfoTextArea.setText("");
			payloadInfoTextArea.setCaretPosition(0);

		}
	}
	private class PayloadsRowListener implements ListSelectionListener {
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

			final int d = payloadsTable.getSelectedRow();
			final String payload = (String) payloadsTableModel.getValueAt(d, 0);

			payloadInfoTextArea.setText("\nPayload Length: " + payload.length()
					+ "\n\n" + "Is Numeric? " + StringUtils.isNumeric(payload)
					+ "\n\n" + "Is Alpha? " + StringUtils.isAlpha(payload)
					+ "\n\n" + "Has whitespaces? "
					+ StringUtils.isWhitespace(payload) + "\n\n");
			payloadInfoTextArea.setCaretPosition(0);

		}
	}
	private static final long serialVersionUID = -5848191307114097542L;
	// The JPanels carrying the components
	private JPanel categoriesPanel, fuzzersPanel, payloadsPanel;

	// The JTables carrying the data
	private JTable categoriesTable, fuzzersTable, payloadsTable;

	// The Table Models with a single column
	private SingleColumnModel categoriesTableModel, fuzzersTableModel,
			payloadsTableModel;

	// The non-wrapping text panes
	private NonWrappingTextPane fuzzerInfoTextArea, payloadInfoTextArea;

	/**
	 * Constructor for the Payloads Panel.
	 * 
	 * @param m
	 *            FrameWindow
	 */
	public PayloadsPanel(final JBroFuzzWindow m) {
		super(" Payloads ", m);
		setLayout(null);
		
		// Categories: First table with one column of all the different
		// categories

		categoriesPanel = new JPanel();
		categoriesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Categories "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));
		categoriesPanel.setBounds(10, 20, 220, 430);
		this.add(categoriesPanel);

		categoriesTableModel = new SingleColumnModel(" Category Name ");
		categoriesTableModel.setData(m.getJBroFuzz().getDatabase().getAllCategories());

		categoriesTable = new JTable(categoriesTableModel);

		TableRowSorter<SingleColumnModel> sorter = new TableRowSorter<SingleColumnModel>(categoriesTableModel);
		categoriesTable.setRowSorter(sorter);

		categoriesTable.getTableHeader().setToolTipText("Click to sort by row");
		categoriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		categoriesTable.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		categoriesTable.setRowHeight(30);
		categoriesTable.getSelectionModel().addListSelectionListener(new CategoriesRowListener());
		
		categoriesTable.setBackground(Color.BLACK);
		categoriesTable.setForeground(Color.WHITE);
		
		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(categoriesTable, false, false, true, false, false, false);
		
		final JScrollPane categoryTableScrollPane = new JScrollPane(categoriesTable);
		categoryTableScrollPane.setVerticalScrollBarPolicy(20);
		categoryTableScrollPane.setHorizontalScrollBarPolicy(30);
		categoryTableScrollPane.setPreferredSize(new Dimension(200, 390));
		categoriesPanel.add(categoryTableScrollPane);
		
		
		// Generators: Second table with one column of all the generators of the
		// selected category

		fuzzersPanel = new JPanel();
		fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Select a Category "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		fuzzersPanel.setBounds(235, 100, 220, 350);
		this.add(fuzzersPanel);

		fuzzersTableModel = new SingleColumnModel(" Fuzzer Name ");

		fuzzersTable = new JTable(fuzzersTableModel);

		fuzzersTable.getTableHeader().setToolTipText("Click to specify sorting; Control-Click to specify secondary sorting");
		fuzzersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// popupTable(fuzzersTable);

		fuzzersTable.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		fuzzersTable.setRowHeight(30);
		fuzzersTable.getSelectionModel().addListSelectionListener(new FuzzersRowListener());
		fuzzersTable.setBackground(Color.BLACK);
		fuzzersTable.setForeground(Color.WHITE);

		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(fuzzersTable, false, false, true, false, false, false);
		
		final JScrollPane nameTextAreaTextScrollPane = new JScrollPane(fuzzersTable);
		nameTextAreaTextScrollPane.setVerticalScrollBarPolicy(20);
		nameTextAreaTextScrollPane.setHorizontalScrollBarPolicy(30);
		nameTextAreaTextScrollPane.setPreferredSize(new Dimension(200, 310));
		fuzzersPanel.add(nameTextAreaTextScrollPane);

		// Payloads Table: Payload table with one column of all the generators
		// of the selected category

		payloadsTableModel = new SingleColumnModel(" Payloads ");

		payloadsTable = new JTable(payloadsTableModel);
		payloadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		payloadsTable.getTableHeader().setToolTipText("Click to specify sorting; Control-Click to specify secondary sorting");

		payloadsTable
				.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		payloadsTable.setRowHeight(30);
		payloadsTable.getSelectionModel().addListSelectionListener(
				new PayloadsRowListener());
		payloadsTable.setBackground(Color.BLACK);
		payloadsTable.setForeground(Color.WHITE);
		payloadsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					
					final String payload = (String) payloadsTable.getModel().getValueAt(payloadsTable.getSelectedRow(), 0);
					new PropertiesViewer(PayloadsPanel.this, "Payload Information", payload);
				
				}
			}
		});
		
		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(payloadsTable, false, false, true, false, false, true);

		final JScrollPane payloadTableScrollPane = new JScrollPane(payloadsTable);
		payloadTableScrollPane.setVerticalScrollBarPolicy(20);
		payloadTableScrollPane.setHorizontalScrollBarPolicy(30);
		payloadTableScrollPane.setPreferredSize(new Dimension(200, 310));

		// Views inside the payloadsPanel

		payloadsPanel = new JPanel();
		payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Select a Fuzzer "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		payloadsPanel.setBounds(460, 150, 420, 300);
		this.add(payloadsPanel);

		fuzzerInfoTextArea = new NonWrappingTextPane();
		fuzzerInfoTextArea.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Fuzzer Information "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		fuzzerInfoTextArea.putClientProperty("charset", "UTF-8");
		fuzzerInfoTextArea.setEditable(false);
		fuzzerInfoTextArea.setVisible(true);
		fuzzerInfoTextArea.setFont(new Font("Verdana", Font.BOLD, 10));
		fuzzerInfoTextArea.setMargin(new Insets(1, 1, 1, 1));
		fuzzerInfoTextArea.setBackground(Color.WHITE);
		fuzzerInfoTextArea.setForeground(Color.BLACK);
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(fuzzerInfoTextArea, false, true, false, true);

		final JScrollPane viewTextScrollPane = new JScrollPane(
				fuzzerInfoTextArea);
		viewTextScrollPane.setVerticalScrollBarPolicy(20);
		viewTextScrollPane.setHorizontalScrollBarPolicy(30);
		viewTextScrollPane.setPreferredSize(new Dimension(400, 100));

		payloadInfoTextArea = new NonWrappingTextPane();
		payloadInfoTextArea.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Payload Properties "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		payloadInfoTextArea.setEditable(false);

		payloadInfoTextArea.setFont(new Font("Verdana", Font.BOLD, 10));
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(payloadInfoTextArea, false, true, false, true);

		final JScrollPane commentLabelScrollPane = new JScrollPane(
				payloadInfoTextArea);
		commentLabelScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		commentLabelScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		commentLabelScrollPane.setPreferredSize(new Dimension(400, 150));

		JSplitPane bottomSplitPanel = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		bottomSplitPanel.setOneTouchExpandable(false);
		bottomSplitPanel.setRightComponent(commentLabelScrollPane);
		bottomSplitPanel.setLeftComponent(payloadTableScrollPane);

		JSplitPane payloadsSplitPanel = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT);
		payloadsSplitPanel.setOneTouchExpandable(false);
		payloadsSplitPanel.setPreferredSize(new Dimension(410, 260));

		payloadsSplitPanel.setTopComponent(viewTextScrollPane);
		payloadsSplitPanel.setBottomComponent(bottomSplitPanel);

		payloadsPanel.add(payloadsSplitPanel);

		payloadsSplitPanel.setDividerLocation(60);
		bottomSplitPanel.setDividerLocation(250);

		// Allow for all areas to be resized to even not be seen
		Dimension minimumSize = new Dimension(0, 0);
		viewTextScrollPane.setMinimumSize(minimumSize);
		bottomSplitPanel.setMinimumSize(minimumSize);
		commentLabelScrollPane.setMinimumSize(minimumSize);
		payloadTableScrollPane.setMinimumSize(minimumSize);

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
	/*
	protected void popup(final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();
		/*
		 * final JMenuItem i1 = new JMenuItem() final JMenuItem i2 = new
		 * JMenuItem("Copy"); final JMenuItem i4 = new JMenuItem("Select All");
		 * final JMenuItem i5 = new JMenuItem("Properties");
		 * 
		 * i2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		 * ActionEvent.CTRL_MASK));
		 * i4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
		 * ActionEvent.CTRL_MASK));
		 * 
		 * popmenu.add(i2); popmenu.add(i4); popmenu.addSeparator();
		 * popmenu.add(i5);
		 */
	/*
		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");

		i1.setEnabled(false);
		i2.setEnabled(true);
		i3.setEnabled(false);
		i4.setEnabled(false);

		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.add(i4);

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Copy
				final StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();
				for (final int element : selection) {
					selectionBuffer.append(area.getModel().getValueAt(element,
							0));
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
				// Select All
				area.selectAll();
			}
		});
		*/
		/*
		 * i5.addActionListener(new ActionListener() { public void
		 * actionPerformed(final ActionEvent e) { // Properties final String
		 * tableName = area.getName(); if(tableName.equalsIgnoreCase("Name")) {
		 * final String exploit = (String)
		 * area.getModel().getValueAt(area.getSelectedRow(), 0); new
		 * ExploitViewer(getFrame(), exploit, ExploitViewer.VIEW_EXPLOIT); }
		 * if(tableName.equalsIgnoreCase("Category")) { final String exploit =
		 * (String) area.getModel().getValueAt(area.getSelectedRow(), 0); new
		 * ExploitViewer(getFrame(), exploit, ExploitViewer.VIEW_CATEGORY); } }
		 * });
		 */
		/*
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
	*/

	/**
	 * <p>
	 * Method for setting the given category name to be displayed
	 * </p>
	 * 
	 * @param category
	 */
	public void setCategoryDisplayed(final String category) {
		int c = 0;
		final String[] allRows = categoriesTableModel.getAllRows();

		for (int i = 0; i < allRows.length; i++) {
			if (allRows[i].equalsIgnoreCase(category)) {
				c = i;
			}
		}

		categoriesTable.getSelectionModel().setSelectionInterval(
				categoriesTable.convertRowIndexToModel(c),
				categoriesTable.convertRowIndexToModel(c));
	}

	/**
	 * <p>
	 * Method for setting the given name and category name to be displayed
	 * </p>
	 * 
	 * @param fuzzer
	 * @param category
	 */
	public void setFuzzerDisplayed(final String fuzzer, final String category) {

		int c = 0;
		final String[] allRows = categoriesTableModel.getAllRows();
		for (int i = 0; i < allRows.length; i++) {
			if (allRows[i].equalsIgnoreCase(category)) {
				c = i;
			}
		}
		categoriesTable.getSelectionModel().setSelectionInterval(
				categoriesTable.convertRowIndexToModel(c),
				categoriesTable.convertRowIndexToModel(c));

		int d = 0;
		final String[] allNames = fuzzersTableModel.getAllRows();
		for (int j = 0; j < allNames.length; j++) {
			if (allNames[j].equalsIgnoreCase(fuzzer)) {
				d = j;
			}
		}
		fuzzersTable.getSelectionModel().setSelectionInterval(d, d);

	}

	public void setPayloadDisplayed(final String payload, final String fuzzer,
			final String category) {

		int c = 0;
		final String[] allRows = categoriesTableModel.getAllRows();
		for (int i = 0; i < allRows.length; i++) {
			if (allRows[i].equalsIgnoreCase(category)) {
				c = i;
			}
		}
		categoriesTable.getSelectionModel().setSelectionInterval(
				categoriesTable.convertRowIndexToModel(c),
				categoriesTable.convertRowIndexToModel(c));

		int d = 0;
		final String[] allNames = fuzzersTableModel.getAllRows();
		for (int j = 0; j < allNames.length; j++) {
			if (allNames[j].equalsIgnoreCase(fuzzer)) {
				d = j;
			}
		}
		fuzzersTable.getSelectionModel().setSelectionInterval(d, d);

		int e = 0;
		final String[] allPayloads = payloadsTableModel.getAllRows();
		for (int k = 0; k < allPayloads.length; k++) {
			if (allPayloads[k].equalsIgnoreCase(payload)) {
				e = k;
			}
		}
		payloadsTable.getSelectionModel().setSelectionInterval(e, e);

	}
	
	public void start() {
	}
	
	public void stop() {
	}
	
	public void graph() {
	}
	
	public void add() {
	}
	
	public void remove() {
	}


}
