/**
 * JBroFuzz 2.3
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
package org.owasp.jbrofuzz.ui.tablemodels;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

/**
 * <p>
 * Table model dedicated to tables with multiple rows, but single columns.
 * </p>
 * 
 * 
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class SingleColumnModel extends AbstractTableModel {

	private static final long serialVersionUID = 7819538656432704429L;

	private final String columnName;

	private final ArrayList<String> dataVector;

	/**
	 * <p>
	 * Constructor passing as argument the name of the column.
	 * </p>
	 * 
	 * @param columnName
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public SingleColumnModel(final String columnName) {

		this.columnName = columnName;
		dataVector = new ArrayList<String>();

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

	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int col) {
		return columnName;
	}

	public int getRowCount() {
		return dataVector.size();
	}

	public Object getValueAt(int row, int col) {
		return dataVector.get(row);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
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

		fireTableRowsDeleted(0, getRowCount());
		dataVector.clear();

		if (values == null) {
			return;
		}

		dataVector.addAll(Arrays.asList(values));
		fireTableRowsInserted(0, values.length);
		dataVector.trimToSize();
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		dataVector.set(row, value.toString());
		fireTableCellUpdated(row, col);
	}

}
