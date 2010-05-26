/**
 * JBroFuzz 2.2
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
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/**
 * <p>Class for representing output table within the 
 * Fuzzing panel.</p>
 * 
 * @author subere@uncon.org
 * @version 2.2
 * @since 1.8
 */
public class OutputTable extends JTable {

	private static final long serialVersionUID = 6380470019501381003L;
	
	public OutputTable(final OutputTableModel model) {

		super(model);
		getTableHeader().setReorderingAllowed(false);
		
		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < model.getColumnCount(); i++) {
			column = getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(30);
			}
			if (i == 1) {
				column.setPreferredWidth(150);
			}
			if (i == 2) {
				column.setPreferredWidth(120);
			}
			if (i == 3) {
				column.setPreferredWidth(20);
			}
			if (i == 4) {
				column.setPreferredWidth(20);
			}
			if (i == 5) {
				column.setPreferredWidth(60);
			}
		}

	}
	
	public final boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public final boolean getColumnSelectionAllowed() {
		return false;
	}
	
	public final boolean getRowSelectionAllowed() {
		return true;
	}
	
	public final Font getFont() {
		return new Font("Monospaced", Font.BOLD, 12);
	}
	
	public final boolean getDragEnabled() {
		return false;
	}
	
	public final Color getBackground() {
		return Color.BLACK;
	}
	
	public final Color getForeground() {
		return Color.WHITE;
	}
	
	public final boolean getSurrendersFocusOnKeystroke() {
		return true;
	}
	
}
