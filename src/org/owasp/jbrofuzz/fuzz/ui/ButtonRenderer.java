package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer extends JButton implements TableCellRenderer {

	private static final long serialVersionUID = 2240748527651063324L;

	public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)	{

		setBorder(null);
		return this;
	}
}