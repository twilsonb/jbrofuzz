/**
 * JBroFuzz 0.9
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
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
 */
package org.owasp.jbrofuzz.ui.tablemodels;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;;

/**
 * <p>
 * The fuzzing table model used within the generators table of the "TCP Fuzzing"
 * panel.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class ThreeColumnModel extends AbstractTableModel {

	private static final long serialVersionUID = 1012351L;

	/**
	 * <p>
	 * The String used to separate columns when a toString representation of a set
	 * number of columns or rows is required. This is typically used in method
	 * getRow() that returns a String.
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
	 *          FuzzingPanel
	 */
	public ThreeColumnModel(final JBroFuzzWindow fPanel) {
		this.fPanel = fPanel;
		dataVector = new Vector<Generator>();
	}

	/**
	 * <p>
	 * Add a row to the generator list.
	 * </p>
	 * 
	 * @param generator
	 *          String
	 * @param start
	 *          int
	 * @param end
	 *          int
	 */
	public void addRow(final String generator, final int start, final int end) {
		final Generator addingGenerator = new Generator(generator, start, end);
		dataVector.add(addingGenerator);
		fireTableRowsInserted(dataVector.size() - 1, dataVector
				.size() - 1);
	}

	/**
	 * Get a complete column count of the generator table
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return ThreeColumnModel.COLUMNNAMES.length;
	}

	/**
	 * Get a given column name.
	 * 
	 * @param column
	 *          int
	 * @return String
	 */
	@Override
	public String getColumnName(final int column) {
		return ThreeColumnModel.COLUMNNAMES[column];
	}

	/**
	 * <p>
	 * Get a row depending on the corresponding integer given.
	 * </p>
	 * 
	 * @param row
	 *          int
	 * @return String
	 */
	public String getRow(final int row) {
		final StringBuffer output = new StringBuffer();
		if ((row > -1) && (row < dataVector.size())) {
			for (int i = 0; i < ThreeColumnModel.COLUMNNAMES.length; i++) {
				output.append(getValueAt(row, i)
						+ ThreeColumnModel.STRING_SEPARATOR);
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
	 * Get the value within the generator table at a given location of column and
	 * row.
	 * </p>
	 * 
	 * @param row
	 *          int
	 * @param column
	 *          int
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
		final Generator gen = dataVector.get(dataVector
				.size() - 1);
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
	 *          int
	 * @param column
	 *          int
	 * @return boolean
	 */
	@Override
	public boolean isCellEditable(final int row, final int column) {
		if (column == ThreeColumnModel.INDEX_GENERATOR) {
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
	 *          String
	 * @param start
	 *          int
	 * @param end
	 *          int
	 */
	public void removeRow(final String generator, final int start, final int end) {
		int rowToRemove = -1;
		for (int i = 0; i < dataVector.size(); i++) {
			final Generator record = dataVector.get(i);
			if (record.getType().equals(generator) && (record.getStart() == start)
					&& (record.getEnd() == end)) {
				rowToRemove = i;
			}
		}
		if (rowToRemove > -1) {
			dataVector.removeElementAt(rowToRemove);
			fireTableRowsDeleted(0, rowToRemove);
		}
	}

	/**
	 * <p>
	 * Set a value at the corresponding row and column location.
	 * </p>
	 * 
	 * @param value
	 *          Object
	 * @param row
	 *          int
	 * @param column
	 *          int
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

class Generator {
	protected String type;
	protected Integer start;
	protected Integer end;

	public Generator(final String generator, final int start, final int end) {
		
		this.start = Integer.valueOf(start);
		this.end = Integer.valueOf(end);
		type = generator;
		
	}

	public int getEnd() {
		return end.intValue();
	}

	public int getStart() {
		return start.intValue();
	}

	public String getType() {
		return type;
	}

	public void setEnd(final int end) {
		this.end = Integer.valueOf(end);
	}

	public void setStart(final int start) {
		this.start = Integer.valueOf(start);
	}

	public void setType(final String generator) {
		type = generator;
	}
}
