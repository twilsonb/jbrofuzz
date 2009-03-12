/**
 * JBroFuzz 1.3
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
package org.owasp.jbrofuzz.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

public class CopyAction extends TextAction {

	private static final long	serialVersionUID	= 3537881376042160461L;

	public CopyAction() {
		super("Copy");
	}

	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				if (evt != null) {

					Object o = evt.getSource();

					if (o instanceof JTable) {

						final JTable table = (JTable) o;

						StringBuffer selectionBuffer = new StringBuffer();
						final int[] selection = table.getSelectedRows();

						for (final int element : selection) {
							for (int i = 0; i < table.getColumnCount(); i++) {

								selectionBuffer.append(table.getModel().getValueAt(
										table.convertRowIndexToModel(element), i));
								if (i < table.getColumnCount() - 1) {
									selectionBuffer.append(",");
								}

							}
							selectionBuffer.append("\n");
						}

						final JTextArea myTempArea = new JTextArea();
						myTempArea.setText(selectionBuffer.toString());
						myTempArea.selectAll();
						myTempArea.copy();
						table.removeRowSelectionInterval(0, table.getRowCount() - 1);

					}

					if (o instanceof JTextComponent) {

						final JTextComponent text = CopyAction.this
								.getTextComponent(evt);

						text.copy();
						text.requestFocus();

					}

				} // null evt

			} // run()
		});

	}

}