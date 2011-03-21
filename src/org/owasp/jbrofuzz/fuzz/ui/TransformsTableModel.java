/**
 * JbroFuzz 2.5
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
package org.owasp.jbrofuzz.fuzz.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * <p>The table model for the encoders, displayed on the bottom,
 * right hand side of the "Fuzzing" tab.</p>
 * 
 * @author ranulf
 * @since 2.3
 */
public class TransformsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8133287501650660903L;

	// The names of the columns within the table of generators
	private static final String[] COLUMNNAMES = 
	{ "Encoder", "Prefix/Match", "Suffix/Replace"};

	// The vector of fuzzer row data
	private final ArrayList<TransformsRow> dataVector;

	/**
	 * <p>Main Constructor passes the Fuzzing Panel.</p>
	 */
	public TransformsTableModel() {
		dataVector = new ArrayList<TransformsRow>();
	}

	/**
	 * <p>Add a fuzzer row to the table</p>
	 */
	public void addRow(String encoding, String matchOrPrefix, String replaceOrSuffix) {
		addRow(new TransformsRow(encoding, matchOrPrefix, replaceOrSuffix));
	}
	
	public void addRow(TransformsRow row) {
		dataVector.add(row);
		dataVector.trimToSize();
		fireTableRowsInserted(dataVector.size(), dataVector.size());
	}

	/**
	 * Get a complete column count of the generator table
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return TransformsTableModel.COLUMNNAMES.length;
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
		return TransformsTableModel.COLUMNNAMES[column];
	}

	/**
	 * <p>
	 * Get a row depending on the corresponding integer given.
	 * </p>
	 * 
	 * @param row
	 *            int
	 * @return TransformsRow
	 */
	public TransformsRow getRow(final int row) {
		return dataVector.get(row);

	}

	/**
	 * Get a complete row count of the generator table.
	 * 
	 * @return int The number of rows present in the table
	 */
	public int getRowCount() {
		return dataVector.size();
	}

	/**
	 * <p>
	 * Get the value within the generator table at a given location of column
	 * and row.
	 * </p>
	 */
	public Object getValueAt(final int row, final int column) {
		final TransformsRow record = dataVector.get(row);
		switch (column) {
		case 0:
			return record.getEncoder();
		case 1:
			return record.getPrefixOrMatch();
		case 2:
			return record.getSuffixOrReplace();
		default:
			return null;
		}
	}

	/**
	 * <p>A cell is always editable within the fuzzers table.</p>
	 */
	@Override
	public boolean isCellEditable(final int row, final int column) {
		return true;
	}

	/**
	 * <p>
	 * Remove a particular row from the table model.
	 * </p>
	 *
	 * @param row
	 *            The row to remove
	 */
	public void removeRow(final int row) {
		if ((row > -1) && (row < dataVector.size())) {
			dataVector.remove(row);
			dataVector.trimToSize();
			fireTableDataChanged();
		}
	}




	/**
	 * <p>
	 * Set a value at the corresponding row and column location.
	 * </p>
	 */
	@Override
	public void setValueAt(final Object value, final int row, final int column) {
		final TransformsRow record = dataVector.get(row);
		if(column == 0) {
			record.setEncoder((String) value);
		}
		if(column == 1) {
			record.setPrefixOrMatch((String) value);
		}
		if(column == 2) {
			record.setSuffixOrReplace((String) value);
		}
		fireTableCellUpdated(row, column);
	}
	

	public void moveRowUpOne(final int row){
		if(row >=1){
			final TransformsRow rowToMove = dataVector.get(row);
			final TransformsRow rowToReplace = dataVector.get(row-1);
			dataVector.set(row-1, rowToMove);
			dataVector.set(row, rowToReplace);
			fireTableDataChanged();
		}
	}
	
	public void moveRowUpAll(final int row){
		if(row>=1){
			TransformsRow rowToMove = dataVector.get(row);
			dataVector.remove(row);
			dataVector.add(0, rowToMove);
			fireTableDataChanged();
		}
	}
	
	public void moveRowDownOne(final int row){
		if(row!=-1 && row < this.getRowCount()){
			final TransformsRow rowToMove = dataVector.get(row);
			final TransformsRow rowToReplace = dataVector.get(row+1);
			dataVector.set(row+1, rowToMove);
			dataVector.set(row, rowToReplace);
			fireTableDataChanged();
		}
	}
	
	public void moveRowDownAll(final int row){
		if(row!=-1 || row < this.getRowCount()){
			TransformsRow rowToMove = dataVector.get(row);
			dataVector.remove(row);
			dataVector.add(dataVector.size(), rowToMove);
			fireTableDataChanged();
		}
	}

}