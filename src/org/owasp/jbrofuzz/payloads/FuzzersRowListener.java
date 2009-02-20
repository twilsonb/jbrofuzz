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

final class FuzzersRowListener implements ListSelectionListener {
	
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
	FuzzersRowListener(PayloadsPanel payloadsPanel) {
		this.payloadsPanel = payloadsPanel;
	}

	/**
	 * <p>
	 * Method for the name table selection row.
	 * </p>
	 * 
	 * @param event
	 *            ListSelectionEvent
	 */
	public void valueChanged(final ListSelectionEvent event) {
		
		if (event.getValueIsAdjusting()) {
			return;
		}

		int d = this.payloadsPanel.fuzzersTable.getSelectedRow();
		d = this.payloadsPanel.fuzzersTable.convertRowIndexToModel(d);
		if(d < 0) {
			return;
		}
		
		final String name = (String) this.payloadsPanel.fuzzersTableModel.getValueAt(d, 0);
		final String id = this.payloadsPanel.getFrame().getJBroFuzz().getDatabase().getIdFromName(name);

		this.payloadsPanel.payloadsTableModel.setData(this.payloadsPanel.getFrame().getJBroFuzz().getDatabase().getPayloads(id));

		if (this.payloadsPanel.payloadsTableModel.getRowCount() >= 0) {
			
			this.payloadsPanel.payloadsPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(" " + name + " - "
							+ id + " "), BorderFactory.createEmptyBorder(1,
							1, 1, 1)));

			this.payloadsPanel.fuzzerInfoTextArea.setText("\nFuzzer Name: "
					+ name
					+ "\n"
					+ "Fuzzer Type: "
					+ ((this.payloadsPanel.getFrame().getJBroFuzz().getDatabase()
							.getPrototype(id).getType())) + "\n" + "Fuzzer Id:   " + id
					+ "\n\n" + "Total Number of Payloads: "
					+ this.payloadsPanel.getFrame().getJBroFuzz().getDatabase().getSize(id));
			this.payloadsPanel.fuzzerInfoTextArea.setCaretPosition(this.payloadsPanel.fuzzerInfoTextArea
					.getText().length());
			
		}

		this.payloadsPanel.payloadInfoTextArea.setText("");
		this.payloadsPanel.payloadInfoTextArea.setCaretPosition(0);

	}
}