/**
 * DefinitionsPanel.java 0.6
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

/**
 * <p>
 * The definitions panel holding a description of the generators loaded.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class Generators extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5848291307114027542L;
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
	private JLabel commentLabel;
	
	/**
	 * Constructor for the Definitions Panel of the represented as a tab. Only a
	 * single instance of this class is constructed.
	 * 
	 * @param m
	 *          FrameWindow
	 */
	public Generators(final JBRFrame m) {
		super();
		this.setLayout(null);
		this.m = m;
		
		// Category
		
		category = new JPanel();
		category.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Category "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		category.setBounds(10, 20, 220, 430);
		this.add(category);
		
		categoryTableModel = new SingleRowTableModel("Category");
		
		categoryTable = new JTable();
		categoryTable.setModel(categoryTableModel);
		categoryTableModel.setData(this.m.getJBroFuzz().getDatabase().getCategories());
		categoryTable.setFont(new Font("Verdana", Font.PLAIN, 14));
		categoryTable.setRowHeight(30);
		categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoryTable.getSelectionModel().addListSelectionListener(new RowListener());
		
		final JScrollPane categoryTableScrollPane = new JScrollPane(categoryTable);
		categoryTableScrollPane.setVerticalScrollBarPolicy(20);
		categoryTableScrollPane.setHorizontalScrollBarPolicy(31);
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
		
		nameTable = new JTable();
		nameTable.setModel(nameTableModel);
		nameTableModel.setData(this.m.getJBroFuzz().getDatabase().getNames());
		nameTable.setFont(new Font("Verdana", Font.PLAIN, 14));
		nameTable.setRowHeight(30);

		final JScrollPane nameTextAreaTextScrollPane = new JScrollPane(nameTable);
		nameTextAreaTextScrollPane.setVerticalScrollBarPolicy(20);
		nameTextAreaTextScrollPane.setHorizontalScrollBarPolicy(31);
		nameTextAreaTextScrollPane.setPreferredSize(new Dimension(200, 310));
		name.add(nameTextAreaTextScrollPane);
		
		// View
		
		view = new JPanel();
		// view.setLayout(null);
		view.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Exploit Information "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		view.setBounds(460, 150, 420, 300);
		this.add(view);	
		
		this.viewTextArea = new NonWrappingTextPane();

		this.viewTextArea.putClientProperty("charset", "UTF-8");
		this.viewTextArea.setEditable(true);
		this.viewTextArea.setVisible(true);
		this.viewTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));
		this.viewTextArea.setMargin(new Insets(1, 1, 1, 1));
		this.viewTextArea.setBackground(Color.WHITE);
		this.viewTextArea.setForeground(Color.BLACK);
		
		final JScrollPane viewTextScrollPane = new JScrollPane(viewTextArea);
		viewTextScrollPane.setVerticalScrollBarPolicy(20);
		viewTextScrollPane.setHorizontalScrollBarPolicy(30);
		viewTextScrollPane.setPreferredSize(new Dimension(400, 100));
		view.add(viewTextScrollPane);
		
		commentLabel = new JLabel("Comment goes here");
		commentLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Comment "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		
		final JScrollPane commentLabelScrollPane = new JScrollPane(commentLabel);
		commentLabelScrollPane.setVerticalScrollBarPolicy(20);
		commentLabelScrollPane.setHorizontalScrollBarPolicy(30);
		commentLabelScrollPane.setPreferredSize(new Dimension(400, 150));
		view.add(commentLabelScrollPane);
	}

	/**
	 * <p>
	 * Method for returning the main window frame that this tab is attached on.
	 * </p>
	 * 
	 * @return Window
	 */
	public JBRFrame getFrameWindow() {
		return this.m;
	}
	
	private class RowListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        for (int c : categoryTable.getSelectedRows()) {
          System.out.println(String.format(" %d", c));
      }
    }
}


}
