/**
 * Generators.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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
 */
package org.owasp.jbrofuzz.ui.panels;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.tablemodels.*;
import org.owasp.jbrofuzz.ui.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.ui.util.TableSorter;
import org.owasp.jbrofuzz.ui.viewers.ExploitViewer;
import org.owasp.jbrofuzz.ui.menu.AboutBox;
/**
 * <p>
 * The definitions panel holding a description of the generators loaded.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class Generators extends JBRPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5848191307114097542L;
	// The frame that the sniffing panel is attached
	// private JBRFrame m;
	// The JPanels carrying the components
	private JPanel category, name, view;
	// The JTables carrying the data
	private JTable categoryTable, nameTable;
	// The Table Models with a single column
	private SingleRowTableModel categoryTableModel, nameTableModel;
	// The non-wrapping text pane
	private NonWrappingTextPane viewTextArea;
	// The JLabel holding any comments
	private JTextArea commentTextArea;
	// The table sorters
	private TableSorter categoryTableSorter, nameTableSorter;

	/**
	 * Constructor for the Definitions Panel of the represented as a tab. Only a
	 * single instance of this class is constructed.
	 * 
	 * @param m
	 *          FrameWindow
	 */
	public Generators(final JBRFrame m) {
		super(m);
		// this.setLayout(null);
		// this.m = m;

		// Category

		category = new JPanel();
		category.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Category "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		category.setBounds(10, 20, 220, 430);
		this.add(category);

		categoryTableModel = new SingleRowTableModel("Category");
		categoryTableSorter = new TableSorter(categoryTableModel);

		categoryTable = new JTable(categoryTableSorter);
		categoryTable.setName("Category");

		this.categoryTable.getTableHeader().setToolTipText("Click to sort by row");
		this.popup(this.categoryTable);
		categoryTableSorter.setTableHeader(this.categoryTable.getTableHeader());

		categoryTableModel.setData(m.getJBroFuzz().getDatabase().getAllCategories());
		categoryTableSorter.setTableModel(categoryTableModel);
		categoryTableSorter.fireTableDataChanged();
		categoryTable.setFont(new Font("Verdana", Font.BOLD, 14));
		categoryTable.setRowHeight(30);
		categoryTable.getSelectionModel().addListSelectionListener(new CategoryRowListener());
		categoryTable.setBackground(Color.BLACK);
		categoryTable.setForeground(Color.WHITE);
		categoryTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2){
					String exploit = (String) categoryTable.getModel().getValueAt(categoryTable.getSelectedRow(), 0);
					new ExploitViewer(m, exploit, ExploitViewer.VIEW_CATEGORY);
				}
			}
		} );
		final JScrollPane categoryTableScrollPane = new JScrollPane(categoryTable);
		categoryTableScrollPane.setVerticalScrollBarPolicy(20);
		categoryTableScrollPane.setHorizontalScrollBarPolicy(30);
		categoryTableScrollPane.setPreferredSize(new Dimension(200, 390));
		category.add(categoryTableScrollPane);

		// Name 

		name = new JPanel();
		name.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Name "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		name.setBounds(235, 100, 220, 350);
		this.add(name);

		nameTableModel = new SingleRowTableModel("Name");
		nameTableSorter = new TableSorter(nameTableModel);

		nameTable = new JTable(nameTableSorter);
		nameTable.setName("Name");

		this.nameTable.getTableHeader().setToolTipText(
		"Click to specify sorting; Control-Click to specify secondary sorting");
		this.popup(this.nameTable);
		nameTableSorter.setTableHeader(this.nameTable.getTableHeader());

		nameTable.setFont(new Font("Verdana", Font.BOLD, 14));
		nameTable.setRowHeight(30);
		// nameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		nameTable.getSelectionModel().addListSelectionListener(new NameRowListener());
		nameTable.setBackground(Color.BLACK);
		nameTable.setForeground(Color.WHITE);
		nameTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2){
					String exploit = (String) nameTable.getModel().getValueAt(nameTable.getSelectedRow(), 0);
					new ExploitViewer(m, exploit, ExploitViewer.VIEW_EXPLOIT);
				}
			}
		} );

		final JScrollPane nameTextAreaTextScrollPane = new JScrollPane(nameTable);
		nameTextAreaTextScrollPane.setVerticalScrollBarPolicy(20);
		nameTextAreaTextScrollPane.setHorizontalScrollBarPolicy(30);
		nameTextAreaTextScrollPane.setPreferredSize(new Dimension(200, 310));
		name.add(nameTextAreaTextScrollPane);

		// View

		view = new JPanel();
		view.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Information "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		view.setBounds(460, 150, 420, 300);
		this.add(view);	

		this.viewTextArea = new NonWrappingTextPane();
		this.viewTextArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Payload "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		this.viewTextArea.putClientProperty("charset", "UTF-8");
		this.viewTextArea.setEditable(false);
		this.viewTextArea.setVisible(true);
		this.viewTextArea.setFont(new Font("Verdana", Font.BOLD, 14));
		this.viewTextArea.setMargin(new Insets(1, 1, 1, 1));
		this.viewTextArea.setBackground(Color.WHITE);
		this.viewTextArea.setForeground(Color.BLACK);
		m.popup(viewTextArea);

		final JScrollPane viewTextScrollPane = new JScrollPane(viewTextArea);
		viewTextScrollPane.setVerticalScrollBarPolicy(20);
		viewTextScrollPane.setHorizontalScrollBarPolicy(30);
		viewTextScrollPane.setPreferredSize(new Dimension(400, 100));
		view.add(viewTextScrollPane);

		commentTextArea = new JTextArea();
		commentTextArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Comment "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		commentTextArea.setEditable(false);
		commentTextArea.setWrapStyleWord(true);
		commentTextArea.setLineWrap(true);
		// commentTextArea.setBackground(Color.BLACK);
		// commentTextArea.setForeground(Color.WHITE);
		commentTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));
		m.popup(commentTextArea);

		final JScrollPane commentLabelScrollPane = new JScrollPane(commentTextArea);
		commentLabelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		commentLabelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		commentLabelScrollPane.setPreferredSize(new Dimension(400, 150));
		view.add(commentLabelScrollPane);

		// The about button

		JButton infoButton = new JButton("About");
		infoButton.setBounds(800, 33, 70, 40);
		infoButton.setEnabled(true);
		// The action listener for the info button
		infoButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				new AboutBox(getFrame(), AboutBox.ACKNOWLEDGEMENTS);
			}
		});
		this.add(infoButton);
	}
	
	/**
	 * <p>
	 * Method for setting the given category name to be diplayed
	 * </p>
	 * 
	 * @param category
	 */
	public void setCategoryDisplayed(String category) {
		int c = 0;
		String[] allRows = categoryTableModel.getAllRows();
		
		for(int i = 0; i < allRows.length; i++) {
			if(allRows[i].equalsIgnoreCase(category)) {
				c = i;
			}
		}

		categoryTable.getSelectionModel().setSelectionInterval(c, c);
	}

	/**
	 * <p>
	 * Method for setting the given name and category name to be displayed
	 * </p>
	 * 
	 * @param name
	 * @param category
	 */
	public void setNameDisplayed(String name, String category) {
		
		int c = 0;
		String[] allRows = categoryTableModel.getAllRows();
		for(int i = 0; i < allRows.length; i++) {
			if(allRows[i].equalsIgnoreCase(category)) {
				c = i;
			}
		}
		categoryTable.getSelectionModel().setSelectionInterval(c, c);
		
		int d = 0; 
		String[] allNames = nameTableModel.getAllRows();
		for(int j = 0; j < allNames.length; j++) {
			if(allNames[j].equalsIgnoreCase(name)) {
				d = j;
			}
		}
		nameTable.getSelectionModel().setSelectionInterval(d, d);
	
	}

	private class CategoryRowListener implements ListSelectionListener {
		/**
		 * <p>Method for the category table selection row.</p>
		 * @param event ListSelectionEvent
		 */
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			int c = categoryTable.getSelectedRow();
			String value = (String) categoryTableSorter.getValueAt(c, 0);
			// System.out.println(value);
			nameTableModel.setData(getFrame().getJBroFuzz().getDatabase().getNames(value));
			nameTableSorter.setTableModel(nameTableModel);
			nameTableSorter.fireTableDataChanged();
		}
	}

	private class NameRowListener implements ListSelectionListener {
		/**
		 * <p>Method for the name table selection row.</p>
		 * @param event ListSelectionEvent
		 */
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			int c = nameTable.getSelectedRow();
			String value = (String) nameTableSorter.getValueAt(c, 0);
			viewTextArea.setText(getFrame().getJBroFuzz().getDatabase().getExploit(value));
			viewTextArea.setCaretPosition(0);

			commentTextArea.setText(getFrame().getJBroFuzz().getDatabase().getComment(value));
			commentTextArea.setCaretPosition(0);
		}
	}

	/**
	 * <p>
	 * Method for setting up the right click copy, select all and properties menu.
	 * </p>
	 * 
	 * @param area
	 *          JTextArea
	 */
	private void popup(final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i4 = new JMenuItem("Select All");
		final JMenuItem i5 = new JMenuItem("Properties");

		i2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		i4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		popmenu.add(i2);
		popmenu.add(i4);
		popmenu.addSeparator();
		popmenu.add(i5);

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Copy
				StringBuffer selectionBuffer = new StringBuffer();
				int[] selection = area.getSelectedRows();
				for(int element : selection) {
					selectionBuffer.append(area.getModel().getValueAt(element, 0));
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

		i5.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Properties
				String tableName = area.getName();
				if(tableName.equalsIgnoreCase("Name")) {
					String exploit = (String) area.getModel().getValueAt(area.getSelectedRow(), 0);
					new ExploitViewer(getFrame(), exploit, ExploitViewer.VIEW_EXPLOIT);
				}
				if(tableName.equalsIgnoreCase("Category")) {
					String exploit = (String) area.getModel().getValueAt(area.getSelectedRow(), 0);
					new ExploitViewer(getFrame(), exploit, ExploitViewer.VIEW_CATEGORY);	
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
				this.checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				this.checkForTriggerEvent(e);
			}
		});
	}

}
