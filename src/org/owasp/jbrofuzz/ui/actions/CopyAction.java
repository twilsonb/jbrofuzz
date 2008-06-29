/**
 * JBroFuzz 1.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 * 
 */
package org.owasp.jbrofuzz.ui.actions;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;

public class CopyAction extends TextAction {

	private static final long serialVersionUID = 3537881376042160461L;

	public CopyAction() {
		super("Copy");
	}

	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				if(evt != null) {
					
					Object o = evt.getSource();
					
					if(o instanceof JTable) {
						
						final JTable table = (JTable) o;
						
						StringBuffer selectionBuffer = new StringBuffer();
						final int[] selection = table.getSelectedRows();
						
						for (final int element : selection) {
							for (int i = 0; i < table.getColumnCount(); i++) {
								
								selectionBuffer.append(table.getModel().getValueAt(table.convertRowIndexToModel(element), i));
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
					
					if(o instanceof JTextComponent) {
						
						final JTextComponent text = (JTextComponent) CopyAction.this.getTextComponent(evt);
						
						text.copy();
						text.requestFocus();
						
					}
					
				} // null evt
				
			} // run()
		});

	}
	
}