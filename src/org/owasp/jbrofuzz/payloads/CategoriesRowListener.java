/**
 * JBroFuzz 1.2
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
package org.owasp.jbrofuzz.payloads;

import javax.swing.BorderFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import org.owasp.jbrofuzz.ui.tablemodels.SingleColumnModel;

final class CategoriesRowListener implements ListSelectionListener {
	
	/**
	 * 
	 */
	private final PayloadsPanel payloadsPanel;

	/**
	 * 
	 * @param payloadsPanel
	 *
	 * @see 
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2 
	 */
	CategoriesRowListener(PayloadsPanel payloadsPanel) {
		this.payloadsPanel = payloadsPanel;
	}

	/**
	 * <p>Method for the categories table selection row.</p>
	 * 
	 * @param event
	 *            ListSelectionEvent
	 */
	public void valueChanged(final ListSelectionEvent event) {
		
		if (event.getValueIsAdjusting()) {
			return;
		}
		
		String value;
		int c = this.payloadsPanel.categoriesTable.getSelectedRow();
		try {
			
			c = this.payloadsPanel.categoriesTable.convertRowIndexToModel(c);
			value = (String) this.payloadsPanel.categoriesTableModel.getValueAt(c, 0);
			
		} catch (IndexOutOfBoundsException  e) {
			return;
		}
		

		this.payloadsPanel.fuzzersTable.setRowSorter(null);
		this.payloadsPanel.fuzzersTableModel.setData(this.payloadsPanel.getFrame().getJBroFuzz().getDatabase().getPrototypeNamesInCategory(value));
		this.payloadsPanel.sorter2 = new TableRowSorter<SingleColumnModel>(this.payloadsPanel.fuzzersTableModel);
		this.payloadsPanel.fuzzersTable.setRowSorter(this.payloadsPanel.sorter2);

		this.payloadsPanel.payloadsTable.setRowSorter(null);
		this.payloadsPanel.payloadsTableModel.setData(null);
		
		this.payloadsPanel.fuzzersPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" " + value + " "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		this.payloadsPanel.fuzzerInfoTextArea.setText("");
		this.payloadsPanel.fuzzerInfoTextArea.setCaretPosition(0);

		this.payloadsPanel.payloadInfoTextArea.setText("");
		this.payloadsPanel.payloadInfoTextArea.setCaretPosition(0);
		
	}
}