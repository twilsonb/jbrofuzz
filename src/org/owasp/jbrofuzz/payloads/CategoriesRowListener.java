/**
 * JBroFuzz 1.6
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
	 * @version 1.3
	 * @since 1.2
	 */
	CategoriesRowListener(PayloadsPanel payloadsPanel) {
		this.payloadsPanel = payloadsPanel;
	}

	/**
	 * <p>
	 * Method for the categories table selection row.
	 * </p>
	 * 
	 * @param event
	 *            ListSelectionEvent
	 */
	public void valueChanged(final ListSelectionEvent event) {

		if (event.getValueIsAdjusting()) {
			return;
		}

		String value;
		int c = payloadsPanel.categoriesTable.getSelectedRow();
		try {

			c = payloadsPanel.categoriesTable.convertRowIndexToModel(c);
			value = (String) payloadsPanel.categoriesTableModel
					.getValueAt(c, 0);

		} catch (IndexOutOfBoundsException e) {
			return;
		}

		payloadsPanel.fuzzersTable.setRowSorter(null);
		payloadsPanel.fuzzersTableModel
				.setData(payloadsPanel.getFrame().getJBroFuzz().getDatabase()
						.getPrototypeNamesInCategory(value));
		payloadsPanel.sorter2 = new TableRowSorter<SingleColumnModel>(
				payloadsPanel.fuzzersTableModel);
		payloadsPanel.fuzzersTable.setRowSorter(payloadsPanel.sorter2);

		payloadsPanel.payloadsTable.setRowSorter(null);
		payloadsPanel.payloadsTableModel.setData(null);

		payloadsPanel.fuzzersPanel.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createTitledBorder(" "
						+ value + " "), BorderFactory.createEmptyBorder(1, 1,
						1, 1)));

		payloadsPanel.payloadsPanel.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory
						.createTitledBorder(" Select a Fuzzer "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));

		payloadsPanel.fuzzerInfoTextArea.setText("");
		payloadsPanel.fuzzerInfoTextArea.setCaretPosition(0);

		payloadsPanel.payloadInfoTextArea.setText("");
		payloadsPanel.payloadInfoTextArea.setCaretPosition(0);

	}
}