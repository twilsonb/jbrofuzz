/**
 * JBroFuzz 1.7
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
package org.owasp.jbrofuzz.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.owasp.jbrofuzz.ui.JBroFuzzPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

/**
 * <p>
 * The panel holding the system logging information that is part of the main
 * frame window.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.8
 */
public class SystemPanel extends JBroFuzzPanel {

	private static final long serialVersionUID = 6122485751450521994L;

	private JTextPane listTextArea;

	private DefaultStyledDocument m_defaultStyledDocument;

	// The line count
	private int lineCount;

	/**
	 * Constructor for the System Logger Panel of the represented as a tab. Only
	 * a single instance of this class is constructed.
	 * 
	 * @param m
	 *            FrameWindow
	 */
	public SystemPanel(final JBroFuzzWindow m) {

		super(" System ", m);

		lineCount = 0;

		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, true, true, false);

		// Define the JPanel
		final JPanel listPanel = new JPanel(new BorderLayout());

		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" System Logger "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));

		m_defaultStyledDocument = new DefaultStyledDocument();
		listTextArea = new JTextPane(m_defaultStyledDocument);
		listTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		listTextArea.setEditable(false);
		listTextArea.setBackground(Color.BLACK);

		// Right click: Cut, Copy, Paste, Select All
		popupText(listTextArea, false, true, false, true);

		final JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(31);
		// listTextScrollPane.setPreferredSize(new Dimension(850, 320));
		listPanel.add(listTextScrollPane);

		// The top and bottom split components
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 33));

		// topPanel.add(infoButton);

		JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPane.setOneTouchExpandable(false);
		mainPane.setTopComponent(topPanel);
		mainPane.setBottomComponent(listPanel);
		mainPane.setDividerLocation(50);

		// Allow for all areas to be resized to even not be seen
		Dimension minimumSize = new Dimension(0, 0);
		topPanel.setMinimumSize(minimumSize);
		listPanel.setMinimumSize(minimumSize);

		this.add(mainPane, BorderLayout.CENTER);

		listTextArea.setCaretPosition(0);

	}

	@Override
	public void add() {

		final Date currentTime = new Date();
		final SimpleDateFormat dateTime = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss:SSS", new Locale("en"));

		start("JBroFuzz Timestamp --- " + dateTime.format(currentTime)
				+ " --- JBroFuzz Timestamp", 0);

	}

	@Override
	public void graph() {
	}

	@Override
	public void remove() {
	}

	@Override
	public void start() {

		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(false, false, true, false, false);

		start("[System Health Check Start]", 2);
		start("[System Info Start]", 1);
		final String systemInfo = "  [Java]\r\n" + "    Vendor:  "
				+ System.getProperty("java.vendor") + "\r\n" + "    Version: "
				+ System.getProperty("java.version") + "\r\n"
				+ "    Installed at: " + System.getProperty("java.home")
				+ "\r\n" + "    Website: "
				+ System.getProperty("java.vendor.url") + "\r\n"
				+ "  [User]\r\n" + "    User: "
				+ System.getProperty("user.name") + "\r\n" + "    Home dir: "
				+ System.getProperty("user.home") + "\r\n"
				+ "    Current dir: " + System.getProperty("user.dir") + "\r\n"
				+ "  [O/S]\r\n" + "    Name: " + System.getProperty("os.name")
				+ "\r\n" + "    Version: " + System.getProperty("os.version")
				+ "\r\n" + "    Architecture: " + System.getProperty("os.arch")
				+ "\r\n";

		final String[] info = systemInfo.split("\r\n");

		for (final String element : info) {
			SystemPanel.this.start(element, 0);
		}
		start("[System Info End]", 1);

		start("[Testing Warning Levels Start]", 1);
		start("  Informational - no issue - no tab counter increment", 0);
		start("  Opperational - operation changed - no tab counter increment",
				1);
		start(
				"  Warning - still functional - an operation change did not complete",
				2);
		start(
				"  Shout - controlable error occured - bad news but not the worst",
				3);
		start(
				"  Error - something that was meant to happen, didn't complete as expected",
				4);
		start("[Testing Warning Levels End]", 1);

		start("[System Health Check End]", 2);

	}

	/**
	 * <p>
	 * Method for setting the text within the JTextArea displayed as part of
	 * this panel. This method simply appends any string given adding a new line
	 * (\n) to the end of it.
	 * </p>
	 * 
	 * @param str
	 *            String
	 * 
	 * @param level
	 *            The severity level<br>
	 *            <= 0 => [INFO] Violet Informational<br>
	 *            == 1 => [OPPR] Blue Operational<br>
	 *            == 2 => [WARN] Green Warning<br>
	 *            == 3 => [SHOT] Amber Shout - light error<br>
	 *            >= 4 => [ERRR] Red Error<br>
	 *            
	 * @author subere@uncon.org
	 * @version 1.8
	 */
	public void start(final String str, int level) {

		final Date currentTime = new Date();
		final SimpleDateFormat dateTime = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss", new Locale("en"));

		StringBuffer toLog = new StringBuffer();
		toLog.append('[');
		toLog.append(dateTime.format(currentTime));
		toLog.append(']');

		Color c;
		if (level <= 0) {
			toLog.append("[INFO]");
			c = Color.BLUE;
			lineCount++;
		} else if (level == 1) {
			toLog.append("[OPPR]");
			c = Color.GREEN;
			lineCount++;
		} else if (level == 2) {
			toLog.append("[WARN]");
			c = Color.YELLOW;
			lineCount++;
		} else if (level == 3) {
			toLog.append("[SHOT]");
			c = new Color(255, 128, 0);
			lineCount++;
		} else {
			toLog.append("[ERRR]");
			c = Color.RED;
			lineCount++;
		}
		toLog.append(str);
		toLog.append('\n');

		try {

			SimpleAttributeSet attr = new SimpleAttributeSet();
			StyleConstants.setForeground(attr, c);
			m_defaultStyledDocument.insertString(m_defaultStyledDocument
					.getLength(), toLog.toString(), attr);

			listTextArea.setCaretPosition(m_defaultStyledDocument.getLength());

		} catch (BadLocationException ex) {

			ex.printStackTrace();

		}

		// Fix the disappearing tab problem
		int tab = -1;
		final int totalTabs = getFrame().getTp().getComponentCount();
		for (int i = 0; i < totalTabs; i++) {
			final String title = getFrame().getTp().getTitleAt(i);
			if (title.startsWith(" System")) {
				tab = i;
			}
		}
		if ((tab > -1)) {
			getFrame().getTp().setTitleAt(tab, " System (" + lineCount + ")");
		}

		lineCount %= 10;
	}

	@Override
	public void stop() {

		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, true, true, false);

	}

}
