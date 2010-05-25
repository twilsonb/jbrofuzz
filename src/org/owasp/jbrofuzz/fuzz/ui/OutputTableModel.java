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

import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.fuzz.MessageWriter;

public class OutputTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 8705563285128511669L;
	
	// The names of the columns within the table of generators
	private static final String[] COLUMNNAMES = { 
		"No", "Target", "Payload", "Status Code", 
		"Time Taken (ms)", "Bytes Received" 
	};
	
	public OutputTableModel() {
		super();
		
		for(String column : COLUMNNAMES) {
			this.addColumn(column);
		}
		
	}
	
	public void addNewRow(MessageWriter outputMessage) {

		this.addRow(new Object[] {

				outputMessage.getFileName(), 
				outputMessage.getTextURL(), 
				StringUtils.abbreviate(outputMessage.getPayload(), 50), 
				outputMessage.getStatus(),
				StringUtils.leftPad("" + outputMessage.getResponseTime(), 5, '0'),
				StringUtils.leftPad("" + outputMessage.getByteCount(), 8, '0')

		});
		
	}
	
	public void clearAllRows() {
		
		while(this.getRowCount() > 0) {
			this.removeRow(this.getRowCount() - 1);
		}
	}
}