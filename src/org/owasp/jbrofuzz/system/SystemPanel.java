/**
 * JBroFuzz 1.9
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
package org.owasp.jbrofuzz.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The panel holding the system logging information that is part of the main
 * frame window.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.1
 * @since 1.8
 */
public class SystemPanel extends AbstractPanel {

	private static final long serialVersionUID = 6122485751450521994L;

	private final JTextPane listTextArea;

	private final DefaultStyledDocument styleDoc;
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

		super(" System (0) ", m);
		lineCount = 0;

		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, true, true, false);

		// Define the JPanel
		final JPanel listPanel = new JPanel(new BorderLayout());

		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" System Logger "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));

		styleDoc = new DefaultStyledDocument();
		listTextArea = new JTextPane(styleDoc);
		listTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		listTextArea.setOpaque(true);
		listTextArea.setEditable(false);
		listTextArea.setBackground(Color.BLACK);

		// Right click: Cut, Copy, Paste, Select All
		AbstractPanel.popupText(listTextArea, false, true, false, true);

		final JScrollPane scrollPane = new JScrollPane(listTextArea);
		scrollPane.setVerticalScrollBarPolicy(20);
		scrollPane.setHorizontalScrollBarPolicy(31);
		// listTextScrollPane.setPreferredSize(new Dimension(850, 320));
		listPanel.add(scrollPane);

		// The top and bottom split components
		final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 33));

		// topPanel.add(infoButton);

		final JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPane.setOneTouchExpandable(false);
		mainPane.setTopComponent(topPanel);
		mainPane.setBottomComponent(listPanel);
		mainPane.setDividerLocation(50);

		// Allow for all areas to be resized to even not be seen
		topPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		listPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		this.add(mainPane, BorderLayout.CENTER);

		listTextArea.setCaretPosition(0);

		this.monitorLog();
	}

	@Override
	public void add() {

		final Date currentTime = new Date();
		final SimpleDateFormat dateTime = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss:SSS", new Locale("en"));

		Logger.log("JBroFuzz Timestamp --- " + dateTime.format(currentTime)
				+ " --- JBroFuzz Timestamp", 0);

	}

	@Override
	public void pause() {
	}

	@Override
	public void remove() {
	}

	@Override
	public void start() {
		
		Logger.log();
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
	 * @version 2.1
	 * @since 1.8
	 */
	private void start(final String str, final int level) {

		final StringBuffer toLog = new StringBuffer();

		Color cColour;
		if (level <= 0) {
			cColour = new Color(152, 229, 255);
		} else if (level == 1) {
			cColour = Color.GREEN;
		} else if (level == 2) {
			cColour = Color.YELLOW;
		} else if (level == 3) {
			cColour = new Color(255, 128, 0);
		} else {
			cColour = Color.RED;
		}
		toLog.append(str);
		toLog.append('\n');

		lineCount++;
		lineCount %= 10;
		
		try {

			final SimpleAttributeSet attr = new SimpleAttributeSet();
			StyleConstants.setForeground(attr, cColour);
			styleDoc.insertString(styleDoc
					.getLength(), toLog.toString(), attr);

			listTextArea.setCaretPosition(styleDoc.getLength());

		} catch (final BadLocationException ex) {

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

	}



	@Override
	public void stop() {

		// Set the enabled options: Start, Stop, Graph, Add, Remove

	}









	private final void monitorLog(){

		// Create the monitor with 1 second polling interval
		final SimpleFileMonitor monitor;
		try {
			monitor = new SimpleFileMonitor (1000, Logger.getLogFile());

			monitor.addListener (new FileListener(){

				@Override
				public void fileChanged(final File file) {
					writeLinesFromFile(monitor);
				}

			});
		} catch (final IOException e) {
			start("Log file is corrupted or does not exist", 4);
		}


	}
	
	private void writeLinesFromFile(final SimpleFileMonitor monitor){
		
		try {
			final long length = monitor.getLastLength();

			final ArrayList<String> linesToWrite = Logger.readLogFile(length);
			
			for(int i=0; i<linesToWrite.size(); i++) {
				
				final String line = linesToWrite.get(i);

				if (line.indexOf("[INFO]")!=-1) {
					start(line,0);
				}else if (line.indexOf("[OPPR]")!=-1) {
					start(line,1);
				}else if (line.indexOf("[WARN]")!=-1) {
					start(line,2);
				}else if (line.indexOf("[SHOT]")!=-1) {
					start(line,3);
				}else {
					start(line,4);
				}
				
			}


		} catch (final FileNotFoundException e) {
			start("Log file not found",4);
		} catch (final IOException e) {
			start("Error reading log file",4);
		}
	}





}
