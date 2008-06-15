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
package org.owasp.jbrofuzz.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

/**
 * <p>
 * The panel holding the system logging information that is part of the main
 * frame window.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 */
public class SystemPanel extends JBroFuzzPanel {

	private static final long serialVersionUID = 1L;
	// The JTable that holds all the data
	private JTextArea listTextArea;
	// The info button
	private JButton infoButton;
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
		// setLayout(null);

		lineCount = 0;

		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, false, true, false);

		// Define the JPanel
		final JPanel listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" System Logger "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));

		// Set the bounds
		listPanel.setBounds(10, 90, 870, 360);

		listTextArea = new JTextArea();
		listTextArea.setFont(new Font("Verdana", Font.PLAIN, 10));
		listTextArea.setEditable(false);
		listTextArea.setLineWrap(true);
		listTextArea.setWrapStyleWord(true);
		listTextArea.setBackground(Color.WHITE);
		listTextArea.setForeground(Color.BLACK);
		popupText(listTextArea);

		final JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(31);
		listTextScrollPane.setPreferredSize(new Dimension(850, 320));
		listPanel.add(listTextScrollPane);

		infoButton = new JButton("Info");
		infoButton.setBounds(800, 33, 70, 40);
		infoButton.setEnabled(true);
		// The action listener for the info button
		infoButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {


			}
		});
		this.add(listPanel);
		this.add(infoButton);
		listTextArea.setCaretPosition(0);
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
	 */
	public void start(final String str) {

		final Date currentTime = new Date();
		final SimpleDateFormat dateTime = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss", new Locale("en"));

		lineCount++;
		listTextArea.append("[" + dateTime.format(currentTime) + "] " + str
				+ "\n");
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
			getFrame().getTp().setTitleAt(tab,
					" System (" + lineCount + ")");
		}

	}

	public void start() {

		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(false, false, false, false, false);

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		final String systemInfo = "[System Info Start]\r\n"
			+ "  [Java]\r\n" + "    Vendor:  "
			+ System.getProperty("java.vendor")
			+ "\r\n"
			+ "    Version: "
			+ System.getProperty("java.version")
			+ "\r\n"
			+ "    Installed at: "
			+ System.getProperty("java.home")
			+ "\r\n"
			+ "    Website: "
			+ System.getProperty("java.vendor.url")
			+ "\r\n"
			+ "  [User]\r\n"
			+ "    User: "
			+ System.getProperty("user.name")
			+ "\r\n"
			+ "    Home dir: "
			+ System.getProperty("user.home")
			+ "\r\n"
			+ "    Current dir: "
			+ System.getProperty("user.dir")
			+ "\r\n"
			+ "  [O/S]\r\n"
			+ "    Name: "
			+ System.getProperty("os.name")
			+ "\r\n"
			+ "    Version: "
			+ System.getProperty("os.version")
			+ "\r\n"
			+ "    Architecture: "
			+ System.getProperty("os.arch")
			+ "\r\n"
			+ "[System Info End]\r\n";

		final String[] info = systemInfo.split("\r\n");

		for (final String element : info) {
			SystemPanel.this.start(element);
		}


	}

	public void stop() {

		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, false, true, false);

	}

	public void graph() {
	}

	public void add() {

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		start("Logging Current Timestamp");

	}

	public void remove() {
	}

}
