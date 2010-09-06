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
package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * <p>Class for representing fuzzer's table within the 
 * Fuzzing panel.</p>
 * 
 * @author subere@uncon.org
 * @version 1.8
 * @since 1.8
 */
public class FuzzerTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1876543211323L;

	// The types of encodings allowed within the encoding column
	public FuzzerTable(final FuzzersTableModel model) {

		super(model);

		setFont(new Font("Monospaced", Font.BOLD, 12));
		setBackground(Color.BLACK);
		setForeground(Color.WHITE);

		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < model.getColumnCount(); i++) {
			column = getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(50);
			}
			else {
				column.setPreferredWidth(15);
			}
		}

	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		return super.getCellEditor(row, column);

	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column)	{
		return super.getCellRenderer(row, column);

	}


}

