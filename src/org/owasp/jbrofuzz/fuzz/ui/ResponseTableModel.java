/**
 * JBroFuzz 1.4
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

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.fuzz.MessageWriter;

/**
 * <p>
 * The Table Model for the output consisting of six fields.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.3
 */
public class ResponseTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// The names of the columns within the table of generators
	private static final String[] COLUMNNAMES = { "No", "Target", "Timestamp",
			"Status", "Response Time", "Response Size" };
	// The vector of ResponseOutputs
	private Vector<ResponseOutput> dataVector;
	// The integer counter
	private int counter;

	/**
	 * Default constructor for this web directory model. This is to be attached
	 * to a JTable.
	 */
	public ResponseTableModel() {

		dataVector = new Vector<ResponseOutput>();
		counter = 0;

	}

	/**
	 * <p>
	 * Methods adds a new row, showing "Sending..." and the remaining default
	 * values.
	 * </p>
	 * 
	 * @param outputMessage
	 *            The message to be outputed
	 * 
	 * @return int the count of elements in the model
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public int addNewRow(MessageWriter outputMessage) {

		dataVector.add(new ResponseOutput(

		outputMessage.getFileName(), outputMessage.getTextURL(), outputMessage
				.getStartDateShort(), "Sending  - ...", "000000 ms",
				"00000000 bytes"

		));

		fireTableRowsInserted(counter, counter);

		counter++;

		return counter;
	}

	/**
	 * Return the total number of columns
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return 6;
	}

	@Override
	public String getColumnName(final int columnIndex) {

		String out = "";

		if ((columnIndex < 6) && (columnIndex >= 0)) {
			out = ResponseTableModel.COLUMNNAMES[columnIndex];
		}

		return out;

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
	 *            int
	 * @param column
	 *            int
	 * @return Object
	 */
	public Object getValueAt(final int row, final int column) {

		if ((row < dataVector.size()) && (row >= 0) && (column < 6)
				&& (column >= 0)) {
			final ResponseOutput record = dataVector.get(row);
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
	 * <p>
	 * Removes a row at a given index.
	 * </p>
	 * 
	 * @param row
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void removeRow(int row) {

		if ((row > -1) && (row < dataVector.size())) {
			dataVector.removeElementAt(row);
			fireTableRowsDeleted(0, row);
			counter--;
		}
	}

	@Override
	public void setValueAt(Object o, int rowIndex, int columnIndex) {

		if ((rowIndex < dataVector.size()) && (rowIndex >= 0)
				&& (columnIndex < 6) && (columnIndex >= 0)) {

			ResponseOutput current = dataVector.get(rowIndex);
			switch (columnIndex) {
			case 0:
				current.one = o.toString();
				break;
			case 1:
				current.two = o.toString();
				break;
			case 2:
				current.three = o.toString();
				break;
			case 3:
				current.four = o.toString();
				break;
			case 4:
				current.five = o.toString();
				break;
			case 5:
				current.six = o.toString();
				break;
			default:
			}

			dataVector.set(rowIndex, current);

		}

	}

	public void updateRow(MessageWriter outputMessage, int row) {

		ResponseOutput response = new ResponseOutput(

		outputMessage.getFileName(), outputMessage.getTextURL(), outputMessage
				.getStartDateShort(),
				"Finished - " + outputMessage.getStatus(), StringUtils.leftPad(
						"" + outputMessage.getResponseTime(), 6, '0')
						+ " ms", StringUtils.leftPad(""
						+ outputMessage.getByteCount(), 8, '0')
						+ " bytes"

		);

		dataVector.setElementAt(response, row - 1);
		fireTableRowsUpdated(row - 1, row - 1);

	}

	public void updateRow(MessageWriter outputMessage, int row, Exception e) {

		ResponseOutput response = new ResponseOutput(

		outputMessage.getFileName(), outputMessage.getTextURL(), outputMessage
				.getStartDateShort(), "[Error]  - " + e.getMessage(),
				StringUtils.leftPad("" + outputMessage.getResponseTime(), 6,
						'0')
						+ " ms", StringUtils.leftPad(""
						+ outputMessage.getByteCount(), 8, '0')
						+ " bytes"

		);

		dataVector.setElementAt(response, row - 1);
		fireTableRowsUpdated(row - 1, row - 1);

	}

}
