/**
 * JBroFuzz 1.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
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
 * 
 */
package org.owasp.jbrofuzz.ui.tablemodels;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

class ResponseOutput {

	protected String one;
	protected String two;
	protected String three;
	protected String four;
	protected String five;
	protected String six;

	public ResponseOutput(
			final String one, final String two, final String three, 
			final String four, final String five, final String six) {

		this.one = one;
		this.two = two;
		this.three = three;
		this.four = four;
		this.five = five;
		this.six = six;

	}

	public String getFirst() {
		return one;
	}

	public String getSecond() {
		return two;
	}

	public String getThird() {
		return three;
	}

	public String getFourth() {
		return four;
	}

	public String getFifth() {
		return five;
	}

	public String getSixth() {
		return six;
	}

}

/**
 * <p>The Table Model for the output consisting of six fields.</p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 */
public class SixColumnModel extends AbstractTableModel {

	private static final long serialVersionUID = 185093L;
	/**
	 * <p>
	 * The String used to separate columns when a toString representation of a set
	 * number of columns or rows is required. This is typically used in method
	 * getRow() that returns a String.
	 * </p>
	 */
	public static final String STRING_SEPARATOR = ",";

	// The names of the columns within the table of generators
	private static String[] COLUMNNAMES = { "No", "URI", "Code",
		"Status Text", "Comments", "Scripts" };
	// The vector of ResponseOutputs
	private Vector<ResponseOutput> dataVector;

	public void setColumnNames(String one, String two, String three, String four, String five, String six) {
		COLUMNNAMES[0] = one;
		COLUMNNAMES[1] = two;
		COLUMNNAMES[2] = three;
		COLUMNNAMES[3] = four;
		COLUMNNAMES[4] = five;
		COLUMNNAMES[5] = six;
	}
	/**
	 * Default constructor for this web directory model. This is to be attached to
	 * a JTable.
	 */
	public SixColumnModel() {

		dataVector = new Vector<ResponseOutput>();
		dataVector.setSize(0);

	}

	/**
	 * Add a row to the table model.
	 * 
	 * @param id
	 *          String
	 * @param uri
	 *          String
	 * @param statusCode
	 *          String
	 * @param statusText
	 *          String
	 * @param comments
	 *          String
	 * @param scripts
	 *          String
	 */

	public void addRow(
			final String one, final String two, final String three, 
			final String four, final String five, final String six) {

		final ResponseOutput response = new ResponseOutput(one, two, three, four, five, six);
		dataVector.add(response);
		fireTableRowsInserted(0, dataVector.size() - 1);
	}

	public void updateLastRow(
			final String one, final String two, final String three, 
			final String four, final String five, final String six) {

		final ResponseOutput response = new ResponseOutput(one, two, three, four, five, six);

		dataVector.setElementAt(response, dataVector.size() - 1);
		fireTableRowsUpdated(0, dataVector.size() - 1);
	}


	/**
	 * Return the total number of columns
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return 6;
	}

	/**
	 * Method for obtaining the String name of a given column.
	 * 
	 * @param column
	 *          int
	 * @return String
	 */
	@Override
	public String getColumnName(final int columnIndex) {
		
		String out = "";
		
		if ((columnIndex < 6) && (columnIndex >= 0)) {
			out = SixColumnModel.COLUMNNAMES[columnIndex];
		}
		
		return out;
		
	}

	/**
	 * Return a given row in String format using the stated separator
	 * 
	 * @param row
	 *          int
	 * @return String
	 */
	public String getRow(final int rowIndex) {
		
		final StringBuffer output = new StringBuffer();
		
		if ( (rowIndex < dataVector.size()) && (rowIndex >= 0) ) {
			
			for (int i = 0; i < SixColumnModel.COLUMNNAMES.length; i++) {
				output.append(getValueAt(rowIndex, i));
				if (i < SixColumnModel.COLUMNNAMES.length - 1) {
					output.append(SixColumnModel.STRING_SEPARATOR);
				}
			}
			output.append("\n");
			
		}
		
		return output.toString();
		
	}



	/**
	 * Return the total number of rows
	 * 
	 * @return int
	 */
	public int getRowCount() {
		return dataVector.size();
	}

	/**
	 * Method for returning a particular value within the existing table model.
	 * 
	 * @param row
	 *          int
	 * @param column
	 *          int
	 * @return Object
	 */
	public Object getValueAt(final int row, final int column) {
		
		if ((row < dataVector.size()) && (row >= 0) && (column < 6) && (column >= 0)) {
			final ResponseOutput record = (ResponseOutput) dataVector.get(row);
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
		}
		return "";
		
	}

	/**
	 * Remove all the rows within the given table model.
	 */
	public void removeAllRows() {
		dataVector.removeAllElements();

		if (dataVector.size() > 0) {
			fireTableRowsDeleted(0, dataVector.size() - 1);
		}
	}

	public void setValueAt(Object o, int rowIndex, int columnIndex) {

		if ((rowIndex < dataVector.size()) && (rowIndex >= 0) && (columnIndex < 6) && (columnIndex >= 0)) {

			ResponseOutput current = dataVector.get(rowIndex);
			switch(columnIndex) {
			case 0: current.one = o.toString(); break;
			case 1: current.two = o.toString(); break;
			case 2: current.three = o.toString(); break;
			case 3: current.four = o.toString(); break;
			case 4: current.five = o.toString(); break;
			case 5: current.six = o.toString(); break;
			default:
			}
			
			dataVector.set(rowIndex, current);

		}

	}
}

