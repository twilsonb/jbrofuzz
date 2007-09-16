/**
 * GeneratorDialog.java 0.8
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
package org.owasp.jbrofuzz.ui.menu;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.owasp.jbrofuzz.ui.JBRFrame;

import org.owasp.jbrofuzz.ui.tablemodels.SingleRowTableModel;
import org.owasp.jbrofuzz.ui.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.ui.util.TableSorter;
import org.owasp.jbrofuzz.ui.viewers.ExploitViewer;

/**
 * <p>
 * The about box used in the FrameWindow.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.7
 */
public class GeneratorDialog extends JDialog {

	private static final long serialVersionUID = -9083413577221108159L;
	// Dimensions of the generator dialog box
	private static final int x = 650;
	private static final int y = 400;
	// The buttons
	private JButton ok;
	// The frame that the sniffing panel is attached
	private JBRFrame m;
	// The JPanels carrying the components
	private JPanel category, name, view;
	// The JTables carrying the data
	private JTable categoryTable, nameTable;
	// The Table Models with a single column
	private SingleRowTableModel categoryTableModel, nameTableModel;
	// The non-wrapping text pane
	private NonWrappingTextPane viewTextArea;
	// The JLabel holding any comments
	// private JTextArea commentTextArea;
	// The table sorters
	private TableSorter categoryTableSorter, nameTableSorter;
	// The start and finish of the request
	private int start, end;
	
	public GeneratorDialog(final JBRFrame parent, int start, int end) {
		
		super(parent, " Payload Selector ", true);
		// this.setLayout(new BorderLayout());
		this.setFont(new Font("SansSerif", Font.PLAIN, 12));

		this.setLayout(null);
		this.m = parent;
		this.start = start;
		this.end = end;
		
		// Category

		category = new JPanel();
		category.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Category "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		category.setBounds(10, 20, 220, y - 70);
		this.add(category);

		categoryTableModel = new SingleRowTableModel("Category");
		categoryTableSorter = new TableSorter(categoryTableModel);

		categoryTable = new JTable(categoryTableSorter);
		categoryTable.setName("Category");

		this.categoryTable.getTableHeader().setToolTipText("Click to sort by row");
		// this.popup(this.categoryTable);
		categoryTableSorter.setTableHeader(this.categoryTable.getTableHeader());

		categoryTableModel.setData(this.m.getJBroFuzz().getDatabase().getAllCategories());
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
		categoryTableScrollPane.setPreferredSize(new Dimension(200, y - 110));
		category.add(categoryTableScrollPane);

		// Name 

		name = new JPanel();
		name.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Name "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		name.setBounds(235, 20, 220, 250);
		this.add(name);

		nameTableModel = new SingleRowTableModel("Name");
		nameTableSorter = new TableSorter(nameTableModel);

		nameTable = new JTable(nameTableSorter);
		nameTable.setName("Name");

		this.nameTable.getTableHeader().setToolTipText(
		"Click to specify sorting; Control-Click to specify secondary sorting");
		// this.popup(this.nameTable);
		nameTableSorter.setTableHeader(this.nameTable.getTableHeader());

		nameTable.setFont(new Font("Verdana", Font.BOLD, 14));
		nameTable.setRowHeight(30);
		nameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		nameTextAreaTextScrollPane.setPreferredSize(new Dimension(200, y - 190));
		name.add(nameTextAreaTextScrollPane);

		// View
		
		view = new JPanel();
		view.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Information "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		view.setBounds(460, 20, 170, 200);
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
		parent.popup(viewTextArea);

		final JScrollPane viewTextScrollPane = new JScrollPane(viewTextArea);
		viewTextScrollPane.setVerticalScrollBarPolicy(20);
		viewTextScrollPane.setHorizontalScrollBarPolicy(30);
		viewTextScrollPane.setPreferredSize(new Dimension(150, 100));
		view.add(viewTextScrollPane);	
		
		
		// Bottom button
		this.ok = new JButton("OK");
		this.ok.setBounds(GeneratorDialog.x - 100, GeneratorDialog.y - 100, 60, 20);

		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						m.getHTTPFuzzingPanel().generatorAddRow("", GeneratorDialog.this.start, GeneratorDialog.this.end );
						GeneratorDialog.this.dispose();
						
					}
				});
			}
		});

		this.getContentPane().add(ok);

		// Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2) - (GeneratorDialog.x / 2 - 100)),
				Math.abs((parent.getHeight() / 2) - (GeneratorDialog.y / 2) + 100));
		this.setSize(GeneratorDialog.x, GeneratorDialog.y);
		this.setResizable(true);
		this.setVisible(true);
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
			nameTableModel.setData(m.getJBroFuzz().getDatabase().getNames(value));
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
			viewTextArea.setText(m.getJBroFuzz().getDatabase().getExploit(value));
			viewTextArea.setCaretPosition(0);
			
			// commentTextArea.setText(m.getJBroFuzz().getDatabase().getComment(value));
			// commentTextArea.setCaretPosition(0);
		}
	}


}
