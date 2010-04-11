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
package org.owasp.jbrofuzz.encode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

class HashPanel extends JPanel {

	private static final long serialVersionUID = -1270470006553668063L;

	private JTextPane hashTextPane;
	
	protected HashPanel(final String header) {
	
		super(new BorderLayout());
		
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(header), BorderFactory.createEmptyBorder(
						5, 5, 5, 5)));
		
		// Text panes -> Comments
		hashTextPane = new JTextPane();
		
		hashTextPane.putClientProperty("charset", "UTF-8");
		hashTextPane.setEditable(false);
		hashTextPane.setVisible(true);
		hashTextPane.setFont(new Font("Verdana", Font.BOLD, 10));

		hashTextPane.setMargin(new Insets(1, 1, 1, 1));
		hashTextPane.setBackground(Color.LIGHT_GRAY);
		hashTextPane.setForeground(new Color(51, 51, 102));

		final JScrollPane hashScrollPane = new JScrollPane(hashTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.add(hashScrollPane, BorderLayout.CENTER);
		
		hashTextPane.setText("Select Alt + Enter to Encode / Alt + Backspace to decode");

	}
	
	protected void setText(final String commentText) {
		hashTextPane.setText(commentText);
	}
}
