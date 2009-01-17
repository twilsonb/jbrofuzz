/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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
package org.owasp.jbrofuzz.fuzz.ui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;


/**
 * <p>
 * The fuzzing table model used within the generators table of the "TCP Fuzzing"
 * panel.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class FuzzersAddedTableModel extends AbstractTableModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6995264785224510555L;

	/**
	 * <p>
	 * The String used to separate columns when a toString representation of a
	 * set number of columns or rows is required. This is typically used in
	 * method getRow() that returns a String.
	 * </p>
	 */
	public static final String STRING_SEPARATOR = "          ";

	private static final int INDEX_GENERATOR = 0;
	private static final int INDEX_START = 1;
	private static final int INDEX_END = 2;

	// The names of the columns within the table of generators
	private static final String[] COLUMNNAMES = { "Category", "Start", "End" };
	// The vector of data
	private Vector<Generator> dataVector;
	// The panel that the model is attached to
	private JBroFuzzWindow fPanel;

	/**
	 * <p>
	 * Main Constructor passes the Fuzzing Panel.
	 * </p>
	 * 
	 * @param fPanel
	 *            FuzzingPanel
	 */
	public FuzzersAddedTableModel(final JBroFuzzWindow fPanel) {
		this.fPanel = fPanel;
		dataVector = new Vector<Generator>();
	}

	/**
	 * <p>
	 * Add a row to the generator list.
	 * </p>
	 * 
	 * @param generator
	 *            String
	 * @param start
	 *            int
	 * @param end
	 *            int
	 */
	public void addRow(final String generator, final int start, final int end) {
		final Generator addingGenerator = new Generator(generator, start, end);
		dataVector.add(addingGenerator);
		dataVector.trimToSize();
		fireTableRowsInserted(dataVector.size(), dataVector.size());
	}

	/**
	 * Get a complete column count of the generator table
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return FuzzersAddedTableModel.COLUMNNAMES.length;
	}

	/**
	 * Get a given column name.
	 * 
	 * @param column
	 *            int
	 * @return String
	 */
	@Override
	public String getColumnName(final int column) {
		return FuzzersAddedTableModel.COLUMNNAMES[column];
	}

	/**
	 * <p>
	 * Get a row depending on the corresponding integer given.
	 * </p>
	 * 
	 * @param row
	 *            int
	 * @return String
	 */
	public String getRow(final int row) {
		final StringBuffer output = new StringBuffer();
		if ((row > -1) && (row < dataVector.size())) {
			for (int i = 0; i < FuzzersAddedTableModel.COLUMNNAMES.length; i++) {
				output.append(getValueAt(row, i)
						+ FuzzersAddedTableModel.STRING_SEPARATOR);
			}
		}
		return output.toString();
	}

	/**
	 * Get a complete row count of the generator table.
	 * 
	 * @return int
	 */
	public int getRowCount() {
		return dataVector.size();
	}

	/**
	 * <p>
	 * Get the value within the generator table at a given location of column
	 * and row.
	 * </p>
	 * 
	 * @param row
	 *            int
	 * @param column
	 *            int
	 * @return Object
	 */
	public Object getValueAt(final int row, final int column) {
		final Generator record = dataVector.get(row);
		switch (column) {
		case INDEX_GENERATOR:
			return record.getType();
		case INDEX_START:
			return Integer.valueOf(record.getStart());
		case INDEX_END:
			return Integer.valueOf(record.getEnd());
		default:
			return new Object();
		}
	}

	/**
	 * <p>
	 * Check to see if an empty row exists within the generator table.
	 * </p>
	 * 
	 * @return boolean
	 */
	public boolean hasEmptyRow() {
		if (dataVector.size() == 0) {
			return false;
		}
		final Generator gen = dataVector.get(dataVector.size() - 1);
		if (gen.getType().trim().equalsIgnoreCase("") && (gen.getStart() == 0)
				&& (gen.getEnd() == 0)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check to see if a generator table is editable
	 * 
	 * @param row
	 *            int
	 * @param column
	 *            int
	 * @return boolean
	 */
	@Override
	public boolean isCellEditable(final int row, final int column) {
		if (column == FuzzersAddedTableModel.INDEX_GENERATOR) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Remove a row from the generator list.
	 * </p>
	 * 
	 * @param generator
	 *            String
	 * @param start
	 *            int
	 * @param end
	 *            int
	 */
	public void removeRow(final String generator, final int start, final int end) {
		int rowToRemove = -1;
		for (int i = 0; i < dataVector.size(); i++) {
			final Generator record = dataVector.get(i);
			if (record.getType().equals(generator)
					&& (record.getStart() == start) && (record.getEnd() == end)) {
				rowToRemove = i;
			}
		}
		if (rowToRemove > -1) {
			dataVector.removeElementAt(rowToRemove);
			fireTableRowsDeleted(0, rowToRemove);
		}
	}
	
	/**
	 * <p>Remove a particular row from the table model.</p>
	 * 
	 * @param row The row to remove
	 *
	 * @see 
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void removeRow(final int row) {		
		
		if((row > -1) && (row < dataVector.size())) {
			dataVector.removeElementAt(row);
			fireTableRowsDeleted(0, row);
		}
	}

	/**
	 * <p>
	 * Set a value at the corresponding row and column location.
	 * </p>
	 * 
	 * @param value
	 *            Object
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	@Override
	public void setValueAt(final Object value, final int row, final int column) {
		final Generator record = dataVector.get(row);
		switch (column) {
		case INDEX_GENERATOR:
			record.setType((String) value);
			break;
		case INDEX_START:
			record.setStart(((Integer) value).intValue());
			break;
		case INDEX_END:
			record.setEnd(((Integer) value).intValue());
			break;
		default:
			fPanel.log("TCP Fuzzing Panel: Invalid index ");
		}
		fireTableCellUpdated(row, column);
	}
}
