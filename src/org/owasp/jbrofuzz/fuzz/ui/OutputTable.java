/**
 * JBroFuzz 2.1
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
import java.text.Collator;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
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
	
	public OutputTable(final ResponseTableModel model) {

		super(model);
		setAutoCreateRowSorter(true);
		setRowSorter(new TableRowSorter<ResponseTableModel>(model));
		final TableRowSorter<ResponseTableModel> sorter = 
			new TableRowSorter<ResponseTableModel>(model) {
			
		    public Comparator<String> getComparator(int column) {
		    	return
		    	new Comparator<String>() {
		    	    public int compare(String s1, String s2) {
		    	        String[] strings1 = s1.split("\\s");
		    	        String[] strings2 = s2.split("\\s");
		    	        return strings1[strings1.length - 1]
		    	            .compareTo(strings2[strings2.length - 1]);
		    	    }
		    	};

//		        Comparator comparator = super.getComparator(column);
//		        if (comparator != null) {
//		            return comparator;
//		        }
//		        Class columnClass = getModel().getColumnClass(column);
//		        if (columnClass == String.class) {
//		            return Collator.getInstance();
//		        }
//		        if (Comparable.class.isAssignableFrom(columnClass)) {
//		            return COMPARABLE_COMPARATOR;
//		        }
//		        return Collator.getInstance();
		    }
		};
		// setRowSorter(sorter);
		
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
	
	public final int getSelectionMode() {
		return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
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
