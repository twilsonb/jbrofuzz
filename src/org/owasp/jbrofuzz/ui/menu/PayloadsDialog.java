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
import java.awt.datatransfer.*;

import java.io.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import org.apache.commons.lang.StringUtils;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.panels.*;
import org.owasp.jbrofuzz.ui.tablemodels.*;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TableSorter;



/**
 * <p>
 * The payloads dialog that appears when a user selects to add a payload.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 0.8
 */
public class PayloadsDialog extends JDialog {

	private static final long serialVersionUID = -1083415577221148132L;
	// Dimensions of the generator dialog box
	private static final int x = 650;
	private static final int y = 400;
	// The buttons
	private JButton ok;
	// The frame that the sniffing panel is attached
	private JBroFuzzWindow m;
	// The JPanels carrying the components
	private JPanel category, name, view;
	// The JTables carrying the data
	private JTable categoryTable, nameTable;
	// The Table Models with a single column
	private SingleColumnModel categoryTableModel, nameTableModel;
	// The non-wrapping text pane
	private NonWrappingTextPane viewTextArea;
	// The JLabel holding any comments
	// private JTextArea commentTextArea;
	// The table sorters
	private TableSorter categoryTableSorter, nameTableSorter;
	// The start and finish of the request
	private int start, end;
	// The last category accessed
	private String lastCategory;
	
	/**
	 * <p>
	 * Method for constructing a Generator Dialog and then passing the
	 * selection to the HTTP Fuzzing panel generator table.
	 * </p>
	 * 
	 * @param parent
	 * @param start
	 * @param end
	 */
	public PayloadsDialog(final FuzzingPanel parent, final int start, final int end) {
		
		super(parent.getFrame(), " Add a Generator of Payloads ", true);
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		setLayout(null);
		m = parent.getFrame();
		this.start = start;
		this.end = end;
		lastCategory = "";
		// Category

		category = new JPanel();
		category.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Generators "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		category.setBounds(10, 20, 220, y - 70);
		this.add(category);

		categoryTableModel = new SingleColumnModel(" Id ");
		categoryTableSorter = new TableSorter(categoryTableModel);

		categoryTable = new JTable(categoryTableSorter);
		categoryTable.setName("Category");
		categoryTable.setToolTipText("Double click to Add the Payload Category");
		
		categoryTable.getTableHeader().setToolTipText("Click to sort by category");
		popup(categoryTable);
		categoryTableSorter.setTableHeader(categoryTable.getTableHeader());
		categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoryTableModel.setData(m.getJBroFuzz().getDatabase().getAllIds());
		categoryTableSorter.setTableModel(categoryTableModel);
		categoryTableSorter.fireTableDataChanged();
		categoryTable.setFont(new Font("Verdana", Font.BOLD, 14));
		categoryTable.setRowHeight(30);
		categoryTable.getSelectionModel().addListSelectionListener(new IdsRowListener());
		categoryTable.setBackground(Color.BLACK);
		categoryTable.setForeground(Color.WHITE);
		categoryTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(final MouseEvent e){
				if (e.getClickCount() == 2){
					final int c = categoryTable.getSelectedRow();
					final String value = (String) categoryTableSorter.getValueAt(c, 0);
					m.getPanelFuzzing().addPayload(value, start, end);
					PayloadsDialog.this.dispose();
				}
			}
		} );
		categoryTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});
		
		final JScrollPane categoryTableScrollPane = new JScrollPane(categoryTable);
		categoryTableScrollPane.setVerticalScrollBarPolicy(20);
		categoryTableScrollPane.setHorizontalScrollBarPolicy(30);
		categoryTableScrollPane.setPreferredSize(new Dimension(200, y - 110));
		category.add(categoryTableScrollPane);

		// Name 

		name = new JPanel();
		name.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Generator Payloads "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		name.setBounds(240, 20, 220, 250);
		this.add(name);

		nameTableModel = new SingleColumnModel(" Payload ");
		nameTableSorter = new TableSorter(nameTableModel);

		nameTable = new JTable(nameTableSorter);
		nameTable.setName("Name");

		nameTable.getTableHeader().setToolTipText("Click to sort by name");
		nameTableSorter.setTableHeader(nameTable.getTableHeader());
		popup(nameTable);
		nameTable.setFont(new Font("Verdana", Font.BOLD, 14));
		nameTable.setRowHeight(30);
		nameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		nameTable.getSelectionModel().addListSelectionListener(new PayloadsRowListener());
		nameTable.setBackground(Color.BLACK);
		nameTable.setForeground(Color.WHITE);
		/*
		nameTable.addMouseListener(new MouseAdapter(){
	     @Override
			public void mouseClicked(final MouseEvent e){
	      if (e.getClickCount() == 2){
	      	// String exploit = (String) nameTable.getModel().getValueAt(nameTable.getSelectedRow(), 0);
					// new ExploitViewer(m, exploit, ExploitViewer.VIEW_EXPLOIT);
	         }
	      }
	     } );
	    */
		nameTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});
		
		final JScrollPane nameTextAreaTextScrollPane = new JScrollPane(nameTable);
		nameTextAreaTextScrollPane.setVerticalScrollBarPolicy(20);
		nameTextAreaTextScrollPane.setHorizontalScrollBarPolicy(30);
		nameTextAreaTextScrollPane.setPreferredSize(new Dimension(200, y - 190));
		name.add(nameTextAreaTextScrollPane);
		
		
		// View
		
		
		view = new JPanel();
		view.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Payload Length "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		view.setBounds(470, 20, 150, 70);
		view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
		this.add(view);	

		viewTextArea = new NonWrappingTextPane();
		/*
		viewTextArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Characters "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		*/
		viewTextArea.putClientProperty("charset", "UTF-8");
		viewTextArea.setEditable(false);
		viewTextArea.setVisible(true);
		viewTextArea.setFont(new Font("Verdana", Font.BOLD, 14));
		viewTextArea.setMargin(new Insets(1, 1, 1, 1));
		viewTextArea.setBackground(Color.WHITE);
		viewTextArea.setForeground(Color.BLACK);
		parent.getFrame().popup(viewTextArea);
		viewTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PayloadsDialog.this.dispose();
				}
			}
		});
		final JScrollPane viewTextScrollPane = new JScrollPane(viewTextArea);
		viewTextScrollPane.setVerticalScrollBarPolicy(20);
		viewTextScrollPane.setHorizontalScrollBarPolicy(30);
		viewTextScrollPane.setPreferredSize(new Dimension(150, 100));
		view.add(viewTextScrollPane);	
		/*
		final JLabel infoPanel = new JLabel("<html><h6 align=\"left\">For further information<br>on each exploit, view <br>the Generators Tab</h6></html>");
		infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		view.add(infoPanel);
		*/
		// Bottom button
		ok = new JButton(" Add Generator ", ImageCreator.ADD_IMG);
		ok.setBounds(470, 305, 150, 40);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int c = categoryTable.getSelectedRow();
						final String value = (String) categoryTableSorter.getValueAt(c, 0);
						m.getPanelFuzzing().addPayload(value, start, end);
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

		ok.setToolTipText("The selected category will be added to the fuzzing list");
		getContentPane().add(ok);
		
		
		// Global frame issues
		
		this.setLocation(Math.abs((parent.getWidth() / 2) - (PayloadsDialog.x / 2 - 100)),
				Math.abs((parent.getHeight() / 2) - (PayloadsDialog.y / 2) + 100));
		this.setSize(PayloadsDialog.x, PayloadsDialog.y);
		setResizable(true);
		setVisible(true);
	}

	
	private class IdsRowListener implements ListSelectionListener {
		/**
		 * <p>Method for the category table selection row.</p>
		 * @param event ListSelectionEvent
		 */
		public void valueChanged(final ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			final int c = categoryTable.getSelectedRow();
			final String value = (String) categoryTableSorter.getValueAt(c, 0);
			final String exploit = m.getJBroFuzz().getDatabase().getName(value);

			nameTableModel.setData(m.getJBroFuzz().getDatabase().getPayloads(value));
			nameTableSorter.setTableModel(nameTableModel);
			nameTableSorter.fireTableDataChanged();
			
			name.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder(" " + exploit + " "), BorderFactory
					.createEmptyBorder(1, 1, 1, 1)));
			
			viewTextArea.setText("");
			viewTextArea.setCaretPosition(0);

			
		}
	}

	private class PayloadsRowListener implements ListSelectionListener {
		/**
		 * <p>Method for the name table selection row.</p>
		 * @param event ListSelectionEvent
		 */
		public void valueChanged(final ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			final int c = nameTable.getSelectedRow();
			final String value = (String) nameTableSorter.getValueAt(c, 0);

			
			viewTextArea.setText("" + value.length());
			viewTextArea.setCaretPosition(0);
			/*
			commentTextArea.setText(
					"\nLength:\t\t" + value.length() + "\n\n" + 
					"Numeric:\t\t" + StringUtils.isNumeric(value) + "\n" +
					"Alpha:\t\t" + StringUtils.isAlpha(value) + "\n" +
					"Spaces:\t\t" + StringUtils.isWhitespace(value) + "\n" );
			commentTextArea.setCaretPosition(0);
			*/
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

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				
				final int a = area.getSelectedRow();
				String value = (String) area.getModel().getValueAt(a, 0);
				
				StringSelection ss = new StringSelection( value );
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
				
			}
		});
		
		i5.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				//			 Show in Generators Tab
				
				if(area.getName().equalsIgnoreCase("Name")) {
					
					final int c = nameTable.getSelectedRow();
					nameTable.getSelectionModel().setSelectionInterval(c, c);
					final String name = (String) nameTableSorter.getValueAt(c, 0);
					
					int d = categoryTable.getSelectedRow();
					String category = (String) categoryTableSorter.getValueAt(d, 0);
					
					PayloadsDialog.this.dispose();
					
					m.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
					m.getPanelPayloads().setCategoryDisplayed(category);
					m.getPanelPayloads().setNameDisplayed(name, category);
					
				}
				
				if(area.getName().equalsIgnoreCase("Category")) {
					
					final int c = categoryTable.getSelectedRow();
					categoryTable.getSelectionModel().setSelectionInterval(c, c);
					final String value = (String) categoryTableSorter.getValueAt(c, 0);
					
					PayloadsDialog.this.dispose();
					
					m.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
					m.getPanelPayloads().setCategoryDisplayed(value);
					
				}
				
			}
		});

		area.addMouseListener(new MouseAdapter() {
			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();
					
					final JTable myTable = (JTable)e.getSource();
					final int c = myTable.rowAtPoint(new Point(e.getX(), e.getY()));
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