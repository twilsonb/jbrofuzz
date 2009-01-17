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
package org.owasp.jbrofuzz.ui.tablemodels;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * <p>
 * The table model that is being used within the Sniffing Panel.
 * </p>
 * <p>
 * This model is used for the requests and replies being made.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.1
 */
public class SingleColumnModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8791885283082579428L;

	private String columnName;

	private ArrayList<String> dataVector;

	/**
	 * Main Constructor for the Table Model
	 * 
	 * @param columnName
	 *            String
	 */
	public SingleColumnModel(final String columnName) {

		this.columnName = columnName;
		dataVector = new ArrayList<String>();
		
	}

	/**
	 * <p>
	 * Method for adding an empty row to the table model and thus the table.
	 * This method invokes the fireTableRowsInserted method.
	 * </p>
	 */
	public void addEmptyRow() {
		dataVector.add("");
		dataVector.trimToSize();
		fireTableRowsInserted(dataVector.size(), dataVector.size());
	}

	/**
	 * Return all rows as a string array
	 * 
	 * @return String[]
	 */
	public String[] getAllRows() {
		dataVector.trimToSize();
		final String[] outputCategories = new String[dataVector.size()];
		return dataVector.toArray(outputCategories);
	}

	/**
	 * <p>
	 * Get the total number of columns.
	 * </p>
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return 1;
	}

	/**
	 * <p>
	 * Method for returning the String name of a particular Column.
	 * </p>
	 * 
	 * @param column
	 *            int
	 * @return String
	 */
	@Override
	public String getColumnName(final int column) {
		
		return columnName;
		
	}

	public Class getColumnClass(int column) {

		if (column >= 0 &&
				column <= getColumnCount ())
			return getValueAt (0, column).getClass ();
		else
			return Object.class;
	}


/**
 * Return a given row in String format using the stated separator
 * 
 * @param row
 *            int
 * @return String
 */
public String getRow(final int row) {
	if ((row > -1) && (row < dataVector.size())) {
		return this.getValueAt(row);
	}
	return "";
}

/**
 * <p>
 * Get the total number of rows present within the table.
 * </p>
 * 
 * @return int
 */
public int getRowCount() {
	return dataVector.size();
}

/**
 * <p>
 * Method for returning the contents of a row as a String.
 * </p>
 * 
 * @param row
 *            int
 * @return String
 */
public String getValueAt(final int row) {

	return (String) this.getValueAt(row, 0);

}

/**
 * <p>
 * Method for returning the Object located at a particular row/column.
 * </p>
 * 
 * @param row
 *            int
 * @param column
 *            int
 * @return Object
 */
public Object getValueAt(final int row, final int column) {

	if ((row < dataVector.size()) && (row >= 0) && (column < 1) && (column >= 0)) {

		return dataVector.get(row);
		/*
			switch (column) {
			case 0:
				return record.getFirst();
			case 1:
				return record.getSecond();
			case 2:
				return record.getThird();
			case 3:
				return record.getFourth();
			case 4:
				return record.getFifth();
			case 5:
				return record.getSixth();
			default:
				return "";
			}
		 */

	}
	return "";


	// return this.getValueAt(row);
}

/**
 * <p>
 * Method for checking to see if an empty row is present within the current
 * table model.
 * </p>
 * 
 * @return boolean
 */
public boolean hasEmptyRow() {
	boolean returnValue = false;

	if (dataVector.size() == 0) {
		returnValue = false;
	} else {
		final String record = dataVector.get(dataVector.size() - 1);
		if ("".equals(record)) {
			returnValue = true;
		}
	}
	return returnValue;
}

/**
 * <p>
 * Method for checking if a cell is editable. By default, this method
 * returns false, regardless of the parameters being passed.
 * </p>
 * 
 * @param row
 *            int
 * @param column
 *            int
 * @return boolean
 */
@Override
public boolean isCellEditable(final int row, final int column) {
	return false;
}

/**
 * <p>
 * Method for setting, from scratch all the data within the model.
 * </p>
 * 
 * @param values
 */
public void setData(final String[] values) {
	dataVector.clear();
	fireTableRowsDeleted(0, getRowCount());

	for (final String element : values) {

		dataVector.add(element);
		dataVector.trimToSize();
		fireTableRowsInserted(dataVector.size(), dataVector.size());

	}
}

/**
 * <p>
 * Set the value of a particular object at a particular row/column.
 * </p>
 * 
 * @param value
 *            Object
 * @param rowIndex
 *            int
 * @param columnIndex
 *            int
 */
@Override
public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {

	if ((rowIndex < dataVector.size()) && (rowIndex >= 0) && (columnIndex < 1) && (columnIndex >= 0)) {

		dataVector.set(rowIndex, value.toString());

	}

	// dataVector.set(row, value.toString());
	// fireTableCellUpdated(row, 0);
}

}
