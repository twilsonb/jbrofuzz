/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.help;

import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * <p>
 * Class used for constructing a JScrollPane that displays further help
 * information in the graphing tab.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class HelpChart extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2266326648164756664L;

	private static final String FILE_NOT_FOUND = "Help file could not be located.";

	/**
	 * <p>
	 * A help for displaying further information as part of the graphs available
	 * in the graphing tab.
	 * </p>
	 * 
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public HelpChart() {

		super();

		final URL helpURL = ClassLoader.getSystemClassLoader().getResource(
		"help/topics-02.html");

		JEditorPane tcpfPane;
		try {
			tcpfPane = new JEditorPane(helpURL);
		} catch (final IOException e1) {
			tcpfPane = new JEditorPane();
			tcpfPane.setText(FILE_NOT_FOUND);
		}

		tcpfPane.setEditable(false);
		final JScrollPane helpScrollPane = new JScrollPane(tcpfPane);

		helpScrollPane
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		helpScrollPane
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		setViewportView(tcpfPane);

	}

}
