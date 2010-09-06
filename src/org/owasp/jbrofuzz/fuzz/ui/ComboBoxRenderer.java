package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ComboBoxRenderer extends JComboBox implements TableCellRenderer {

	private static final long serialVersionUID = 2240748527651063324L;

	@Override
	public String getToolTipText() {
		return "Select an Encoding from the Drop-Down Menu";
	}

	public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)	{

		setBorder(null);
		removeAllItems();
		addItem( value );
		return this;
	}
}