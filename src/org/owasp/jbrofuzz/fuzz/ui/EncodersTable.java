/**
 * JBroFuzz 2.4
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
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.owasp.jbrofuzz.encode.EncoderHashCore;

/**
 * <p>Class for representing encoders table within the 
 * Fuzzing panel.</p>
 * 
 * @author ranulf
 * @since 2.3
 */
public class EncodersTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1876543211323L;

	// The types of encodings allowed within the encoding column
	public EncodersTable(final EncodersTableModel model) {

		super(model);

		setFont(new Font("Monospaced", Font.BOLD, 12));
		setBackground(Color.BLACK);
		setForeground(Color.WHITE);

		// Set the column widths
		// TableColumn column = null;
		for (int i = 0; i < model.getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(30);
		}

	}
	
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		String[] encodersList = new String[EncoderHashCore.CODES.length+2];
		System.arraycopy(EncoderHashCore.CODES, 0, encodersList, 0, EncoderHashCore.CODES.length);
		System.arraycopy(new String[]{"Match & Replace","Prefix & Suffix"}, 0, encodersList, EncoderHashCore.CODES.length, 2);
		final JComboBox comboxBox = new JComboBox(encodersList);
		comboxBox.setFont(new Font("Monospaced", Font.BOLD, 12));
		comboxBox.setBackground(Color.WHITE);
		comboxBox.setForeground(Color.BLACK);
		final JTextField prefixOrMatch = new JTextField();
		prefixOrMatch.setBackground(Color.WHITE);
		prefixOrMatch.setForeground(Color.BLACK);
		final JTextField suffixOrReplace = new JTextField();
		suffixOrReplace.setBackground(Color.WHITE);
		suffixOrReplace.setForeground(Color.BLACK);
		final int modelColumn = convertColumnIndexToModel( column );

		if (modelColumn == 0) 
			return new DefaultCellEditor(comboxBox);
		if (modelColumn == 1)
			return new DefaultCellEditor(prefixOrMatch);
		if (modelColumn == 2)
			return new DefaultCellEditor(suffixOrReplace);
		else
			return super.getCellEditor(row, column);
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column)	{
		final int modelColumn = convertColumnIndexToModel( column );
		if (modelColumn == 0)
			return new ComboBoxRenderer ();
		else
			return super.getCellRenderer(row, column);
	}
	
	
}

