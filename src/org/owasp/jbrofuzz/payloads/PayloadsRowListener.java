/**
 * JBroFuzz 1.5
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

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;

final class PayloadsRowListener implements ListSelectionListener {

	/**
	 * 
	 */
	private final PayloadsPanel payloadsPanel;

	/**
	 * <p>
	 * Constructor for the row listener in the "Payloads" table within the
	 * "Payloads" tab.
	 * </p>
	 * 
	 * @param payloadsPanel
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	PayloadsRowListener(PayloadsPanel payloadsPanel) {
		this.payloadsPanel = payloadsPanel;
	}

	/**
	 * <p>
	 * Implemented for each row selected in the payloads table.
	 * </p>
	 * 
	 * @param event
	 *            ListSelectionEvent
	 */
	public void valueChanged(final ListSelectionEvent event) {

		if (event.getValueIsAdjusting()) {
			return;
		}

		String payload;
		final int d = payloadsPanel.payloadsTable.getSelectedRow();
		try {

			payload = (String) payloadsPanel.payloadsTableModel
					.getValueAt(d, 0);

		} catch (IndexOutOfBoundsException e) {
			return;
		}

		payloadsPanel.payloadInfoTextArea.setText("\nPayload Length: "
				+ payload.length() + "\n\n" + "Is Numeric? "
				+ StringUtils.isNumeric(payload) + "\n\n" + "Is Alpha? "
				+ StringUtils.isAlpha(payload) + "\n\n" + "Has whitespaces? "
				+ StringUtils.isWhitespace(payload) + "\n\n");
		payloadsPanel.payloadInfoTextArea.setCaretPosition(0);

	}
}