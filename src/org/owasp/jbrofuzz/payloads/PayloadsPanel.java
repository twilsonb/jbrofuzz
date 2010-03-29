/**
 * JBroFuzz 2.0
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableRowSorter;

import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.tablemodels.SingleColumnModel;
import org.owasp.jbrofuzz.ui.viewers.PropertiesViewer;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The definitions panel holding a description of the fuzzers loaded.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.5
 */
public class PayloadsPanel extends AbstractPanel {

	private static final long serialVersionUID = 1234567890L;

	// The split pane at the centre of the screen
	private final JSplitPane mainSplitPanel, rightSplitPanel;

	// The JPanels carrying the components
	private final JPanel categoriesPanel;
	
	protected final JPanel payloadsPanel, fuzzersPanel;

	// The JTables carrying the data
	protected final JTable payloadsTable, fuzzersTable, categoriesTable;

	// The Table Models with a single column
	protected final SingleColumnModel payloadsTableModel, fuzzersTableModel,
	categoriesTableModel;

	// The row sorters for the two tables
	private TableRowSorter<SingleColumnModel> sorter;
	protected TableRowSorter<SingleColumnModel> sorter2;
	
	protected final NonWrappingTextPane payloadInfoTextArea,
	fuzzerInfoTextArea;

	/**
	 * Constructor for the Payloads Panel.
	 * 
	 * @param m
	 *            FrameWindow
	 */
	public PayloadsPanel(final JBroFuzzWindow m) {

		super(" Payloads ", m);
		setLayout(new BorderLayout());

		// Categories: First table with one column of all the different
		// categories

		categoriesPanel = new JPanel(new BorderLayout());
		categoriesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Categories "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		// categoriesPanel.setBounds(10, 20, 220, 430);
		// this.add(categoriesPanel);

		categoriesTableModel = new SingleColumnModel(" Category Name ");
		categoriesTableModel.setData(m.getJBroFuzz().getDatabase()
				.getAllCategories());

		categoriesTable = new JTable(categoriesTableModel);

		sorter = new TableRowSorter<SingleColumnModel>(categoriesTableModel);
		categoriesTable.setRowSorter(sorter);

		categoriesTable.getTableHeader().setToolTipText("Click to sort by row");
		categoriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		categoriesTable.setFont(new Font("Lucida Sans Typewriter", Font.BOLD,
				14));
		categoriesTable.setRowHeight(30);
		categoriesTable.getSelectionModel().addListSelectionListener(
				new CategoriesRowListener(this));

		categoriesTable.setBackground(Color.BLACK);
		categoriesTable.setForeground(Color.WHITE);

		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(categoriesTable, false, false, true, false, false, false);

		final JScrollPane categoryTableScrollPane = new JScrollPane(
				categoriesTable);
		categoryTableScrollPane.setVerticalScrollBarPolicy(20);
		categoryTableScrollPane.setHorizontalScrollBarPolicy(30);
		// categoryTableScrollPane.setPreferredSize(new Dimension(200, 390));
		categoriesPanel.add(categoryTableScrollPane);

		// Fuzzers: Second table with one column of all the generators of the
		// 

		fuzzersPanel = new JPanel(new BorderLayout());
		fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Select a Category "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));

		fuzzersTableModel = new SingleColumnModel(" Fuzzer Name ");

		fuzzersTable = new JTable(fuzzersTableModel);

		sorter2 = new TableRowSorter<SingleColumnModel>(fuzzersTableModel);
		fuzzersTable.setRowSorter(sorter2);

		fuzzersTable.getTableHeader().setToolTipText("Click to sort by row");
		fuzzersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		fuzzersTable.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		fuzzersTable.setRowHeight(30);
		fuzzersTable.getSelectionModel().addListSelectionListener(
				new FuzzersRowListener(this));
		fuzzersTable.setBackground(Color.BLACK);
		fuzzersTable.setForeground(Color.WHITE);

		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(fuzzersTable, false, false, true, false, false, false);

		final JScrollPane nameTextAreaTextScrollPane = new JScrollPane(
				fuzzersTable);
		nameTextAreaTextScrollPane.setVerticalScrollBarPolicy(20);
		nameTextAreaTextScrollPane.setHorizontalScrollBarPolicy(30);
		// nameTextAreaTextScrollPane.setPreferredSize(new Dimension(200, 310));
		fuzzersPanel.add(Box.createRigidArea(new Dimension(0, 50)),
				BorderLayout.NORTH);
		fuzzersPanel.add(nameTextAreaTextScrollPane, BorderLayout.CENTER);

		// Payloads Table: Payload table with one column of all the generators
		// of
		// the selected category

		payloadsTableModel = new SingleColumnModel(" Payloads ");

		payloadsTable = new JTable(payloadsTableModel);
		payloadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		payloadsTable
		.getTableHeader()
		.setToolTipText(
				"Click to specify sorting; Control-Click to specify secondary sorting");

		payloadsTable
		.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		payloadsTable.setRowHeight(30);
		payloadsTable.getSelectionModel().addListSelectionListener(
				new PayloadsRowListener(this));
		payloadsTable.setBackground(Color.BLACK);
		payloadsTable.setForeground(Color.WHITE);
		payloadsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {

					final String payload = (String) payloadsTable.getModel()
					.getValueAt(payloadsTable.getSelectedRow(), 0);
					new PropertiesViewer(PayloadsPanel.this,
							"Payload Information", payload);

				}
			}
		});

		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(payloadsTable, false, false, true, false, false, true);

		final JScrollPane payloadTableScrollPane = new JScrollPane(
				payloadsTable);
		payloadTableScrollPane.setVerticalScrollBarPolicy(20);
		payloadTableScrollPane.setHorizontalScrollBarPolicy(30);
		// payloadTableScrollPane.setPreferredSize(new Dimension(200, 310));

		// Views inside the payloads Panel

		payloadsPanel = new JPanel(new BorderLayout());
		payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Select a Fuzzer "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		// payloadsPanel.setBounds(460, 150, 420, 300);
		// this.add(payloadsPanel);

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
		AbstractPanel.popupText(fuzzerInfoTextArea, false, true, false, true);

		final JScrollPane viewTextScrollPane = new JScrollPane(
				fuzzerInfoTextArea);
		viewTextScrollPane.setVerticalScrollBarPolicy(20);
		viewTextScrollPane.setHorizontalScrollBarPolicy(30);
		// viewTextScrollPane.setPreferredSize(new Dimension(400, 100));

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
		// commentLabelScrollPane.setPreferredSize(new Dimension(400, 150));

		final JSplitPane bottomSplitPanel = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		bottomSplitPanel.setOneTouchExpandable(false);
		bottomSplitPanel.setRightComponent(commentLabelScrollPane);
		bottomSplitPanel.setLeftComponent(payloadTableScrollPane);

		final JSplitPane payloadsSplitPanel = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT);
		payloadsSplitPanel.setOneTouchExpandable(false);
		// payloadsSplitPanel.setPreferredSize(new Dimension(410, 260));

		payloadsSplitPanel.setTopComponent(viewTextScrollPane);
		payloadsSplitPanel.setBottomComponent(bottomSplitPanel);

		payloadsPanel.add(Box.createRigidArea(new Dimension(0, 100)),
				BorderLayout.NORTH);
		payloadsPanel.add(payloadsSplitPanel, BorderLayout.CENTER);

		payloadsSplitPanel.setDividerLocation(100);
		bottomSplitPanel.setDividerLocation(300);

		// The right split pane and friends
		rightSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightSplitPanel.setOneTouchExpandable(false);
		rightSplitPanel.setLeftComponent(fuzzersPanel);
		rightSplitPanel.setRightComponent(payloadsPanel);

		// The main split pane and friends
		mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPanel.setOneTouchExpandable(false);
		mainSplitPanel.setLeftComponent(categoriesPanel);
		mainSplitPanel.setRightComponent(rightSplitPanel);

		mainSplitPanel.setDividerLocation(250);
		rightSplitPanel.setDividerLocation(250);

		// Allow for all areas to be resized to even not be seen

		viewTextScrollPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		bottomSplitPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		commentLabelScrollPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		payloadTableScrollPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		categoriesPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		rightSplitPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		payloadsPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		fuzzersPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		// Add all the components to the main pane
		this.add(mainSplitPanel, BorderLayout.CENTER);

	}

	@Override
	public void add() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void remove() {
	}

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
	protected void setFuzzerDisplayed(final String fuzzer, final String category) {

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

		fuzzersTable.getSelectionModel().setSelectionInterval(
				fuzzersTable.convertRowIndexToModel(d),
				fuzzersTable.convertRowIndexToModel(d));

	}

	/**
	 * <p>
	 * Method for setting the given payload, fuzzer and category name to be
	 * displayed
	 * </p>
	 * 
	 * @param payload
	 * @param fuzzer
	 * @param category
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.0
	 */
	protected void setPayloadDisplayed(final String payload, final String fuzzer,
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
		fuzzersTable.getSelectionModel().setSelectionInterval(
				fuzzersTable.convertRowIndexToModel(d),
				fuzzersTable.convertRowIndexToModel(d));

		int e = 0;
		final String[] allPayloads = payloadsTableModel.getAllRows();
		for (int k = 0; k < allPayloads.length; k++) {
			if (allPayloads[k].equalsIgnoreCase(payload)) {
				e = k;
			}
		}
		payloadsTable.getSelectionModel().setSelectionInterval(e, e);

	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

}
