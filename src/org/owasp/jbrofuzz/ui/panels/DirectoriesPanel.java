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

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.table.*;

import org.owasp.jbrofuzz.dir.DRequestIterator;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.tablemodels.SixColumnModel;
import org.owasp.jbrofuzz.ui.viewers.PropertiesViewer;
import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * The web directory panel that is attached to the main frame.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class DirectoriesPanel extends JBroFuzzPanel implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1291872530751555718L;

	// The text areas used in their corresponding panels
	private JTextArea directoryText;

	private JTextField targetText;

	private JFormattedTextField portText;

	// The JButtons present in the user interface
	// private final JButton startButton, stopButton;

	// The JTable holding all the responses
	private JTable responseTable;

	// The corresponding table model
	private SixColumnModel responseTableModel;

	// The request iterator to loop through the directories
	private DRequestIterator cesg;

	// The directory panel that needs to update the line number
	private JPanel directoryPanel, outputPanel;

	// The session count counting how many times start has been hit
	private int session;

	// The progress bar for the site
	private JProgressBar progressBar;

	private boolean stopped;

	/**
	 * The constructor for the Web Directory Panel. This constructor spawns the
	 * main panel involving web directories.
	 * 
	 * @param m
	 *            FrameWindow
	 */
	public DirectoriesPanel(final JBroFuzzWindow m) {
		
		super(" Web Directories ", m);
		setLayout(null);
		// this.m = m;
		session = 0;

		stopped = true;
		
		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, false, false, false);
		
		// Define the directory JPanel
		directoryPanel = new JPanel();
		directoryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory
						.createTitledBorder(" Total Directories to test: "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		directoryPanel.setBounds(630, 20, 230, 430);
		this.add(directoryPanel);

		// Define the target JPanel
		final JPanel targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Target URI [HTTP/HTTPS] "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		targetPanel.setBounds(10, 20, 500, 60);
		this.add(targetPanel);

		// Define the port JPanel
		final JPanel portPanel = new JPanel();
		portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Port "), BorderFactory.createEmptyBorder(
				1, 1, 1, 1)));
		portPanel.setBounds(510, 20, 60, 60);
		this.add(portPanel);

		// Define the output JPanel
		outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		outputPanel.setBounds(10, 150, 610, 300);
		this.add(outputPanel);

		// Define the target text area
		targetText = new JTextField();
		targetText.setEditable(true);
		targetText.setVisible(true);
		targetText.setFont(new Font("Verdana", Font.BOLD, 12));
		targetText.setMargin(new Insets(1, 1, 1, 1));
		targetText.setBackground(Color.WHITE);
		targetText.setForeground(Color.BLACK);
		targetText.setPreferredSize(new Dimension(480, 20));
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(targetText, true, true, true, true);
		
		targetPanel.add(targetText);

		// Define the port text area
		portText = new JFormattedTextField();
		portText.setEditable(true);
		portText.setVisible(true);
		portText.setFont(new Font("Verdana", Font.BOLD, 12));
		portText.setMargin(new Insets(1, 1, 1, 1));
		portText.setBackground(Color.WHITE);
		portText.setForeground(Color.BLACK);
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(portText, true, true, true, true);
		
		portText.setPreferredSize(new Dimension(50, 20));
		portPanel.add(portText);

		// Define the directory text area
		directoryText = new JTextArea(1, 1);
		directoryText.setEditable(true);
		directoryText.setVisible(true);
		directoryText.setFont(new Font("Verdana", Font.BOLD, 12));
		directoryText.setLineWrap(false);
		directoryText.setWrapStyleWord(true);
		directoryText.setMargin(new Insets(1, 1, 1, 1));
		directoryText.setBackground(Color.WHITE);
		directoryText.setForeground(Color.BLACK);
		directoryText.addKeyListener(this);
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(directoryText, true, true, true, true);
		
		final JScrollPane directoryScrollPane = new JScrollPane(directoryText);
		directoryScrollPane.setVerticalScrollBarPolicy(20);
		directoryScrollPane.setHorizontalScrollBarPolicy(30);
		directoryScrollPane.setPreferredSize(new Dimension(210, 390));
		directoryPanel.add(directoryScrollPane);

		responseTableModel = new SixColumnModel();
		responseTableModel.setColumnNames("No", "URI", "Code", "Status Text", "Comments", "Scripts");

		responseTable = new JTable(responseTableModel);

		TableRowSorter<SixColumnModel> sorter = new TableRowSorter<SixColumnModel>(responseTableModel);
		responseTable.setRowSorter(sorter);

		responseTable.getTableHeader().setToolTipText("Click to sort by row");

		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(responseTable, false, false, true, false, true, true);
		
		responseTable.setFont(new Font("Monospaced", Font.BOLD, 12));
		responseTable.setBackground(Color.black);
		responseTable.setForeground(Color.white);
		responseTable.setSurrendersFocusOnKeystroke(true);

		responseTable.setColumnSelectionAllowed(false);
		responseTable.setRowSelectionAllowed(true);
		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < responseTableModel.getColumnCount(); i++) {
			column = responseTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(30);
			}
			if (i == 1) {
				column.setPreferredWidth(300);
			}
			if (i == 2) {
				column.setPreferredWidth(30);
			}
			if (i == 3) {
				column.setPreferredWidth(120);
			}
			if (i == 4) {
				column.setPreferredWidth(20);
			}
			if (i == 5) {
				column.setPreferredWidth(20);
			}

		}

		responseTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {

							StringBuffer output = new StringBuffer();

							for (int i = 0; i < responseTable.getColumnCount(); i++) {

								output.append(responseTable.getColumnName(i) + ": ");
								output.append(responseTable.getModel().getValueAt(responseTable.convertRowIndexToModel(responseTable.getSelectedRow()), i));
								output.append("\n");
							}

							new PropertiesViewer(DirectoriesPanel.this, "Properties", output.toString());

						}
					});
				}
			}
		});

		final JScrollPane listTextScrollPane = new JScrollPane(responseTable);
		listTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		listTextScrollPane.setHorizontalScrollBarPolicy(31);
		listTextScrollPane.setPreferredSize(new Dimension(590, 260));
		listTextScrollPane.setWheelScrollingEnabled(true);
		outputPanel.add(listTextScrollPane);

		progressBar = new JProgressBar(0);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setPreferredSize(new Dimension(310, 20));
		final JPanel progressPanel = new JPanel();
		progressPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Progress "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));
		progressPanel.setBounds(10, 85, 330, 60);
		progressPanel.add(progressBar);
		this.add(progressPanel);

		// startButton.setEnabled(true);
		// stopButton.setEnabled(false);

		targetText.setText("https://www.owasp.org");
		portText.setText("443");
	}

	/**
	 * <p>
	 * Method for adding an extra row to the output response table. The
	 * different fields are identified by \n.
	 * 
	 * @param s
	 *            String
	 * 
	 */
	public void addRow(final String s) {

		final String[] inputArray = s.split("\n");
		if (inputArray.length != 6) {
			final StringBuffer error = new StringBuffer(
					"Web Directory Error! Cannot fit " + inputArray.length
							+ " columns into 6.");
			if (inputArray.length > 1) {
				error.append(" First column was " + inputArray[0]);
			}
			getFrame().log(error.toString());
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					responseTableModel.addRow(inputArray[0], inputArray[1],
							inputArray[2], inputArray[3], inputArray[4],
							inputArray[5]);
					// Set the last row to be visible
					responseTable.scrollRectToVisible(responseTable
							.getCellRect(responseTable.getRowCount() - 1, 0,
									true));
				}
			});

		}
	}

	/**
	 * Get the session number. This number represents the number of times the
	 * start button has been hit.
	 * 
	 * @return String
	 */
	public String getSessionNumber() {
		String s = "";
		if (session < 10) {
			s += "0";
		}
		s += session;
		return s;
	}

	/**
	 * Handle the key pressed event from the text field.
	 * 
	 * @param e
	 *            KeyEvent
	 */
	public void keyPressed(final KeyEvent e) {
		// System.out.println(directoryText.getLineCount() );
	}

	/**
	 * Handle the key released event from the text field.
	 * 
	 * @param e
	 *            KeyEvent
	 */
	public void keyReleased(final KeyEvent e) {
		// System.out.println(directoryText.getLineCount() );
	}

	/**
	 * Handle the key typed event from the text field.
	 * 
	 * @param ke
	 *            KeyEvent
	 */
	public void keyTyped(final KeyEvent ke) {
		directoryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Total Directories to test: "
						+ directoryText.getLineCount() + " "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));
	}
	

	/**
	 * Set the text content of the directories jtextarea.
	 * 
	 * @param s
	 *            StringBuffer
	 */
	public void setDirectoriesText(final StringBuffer s) {
		directoryText.setText(s.toString());
		directoryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Total Directories to test: "
						+ directoryText.getLineCount() + " "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));
		directoryText.setCaretPosition(0);
	}

	/**
	 * Set the progress bar on the display to a value between 0 and 100.
	 * 
	 * @param percent
	 *            int
	 */
	public void setProgressBar(final int percent) {
		if ((percent >= 0) && (percent <= 100)) {
			final SwingWorker3 progressWorker = new SwingWorker3() {
				@Override
				public Object construct() {
					progressBar.setValue(percent);
					return "progress-update-return";
				}

				@Override
				public void finished() {
				}
			};
			progressWorker.start();
		}
	}

	/**
	 * Method triggered when the start button is pressed.
	 */
	public void start() {
		
		if (!stopped) {
			return;
		}	
		stopped = false;
		
		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(false, true, false, false, false);
		
		// Increment the session number
		session++;
		session %= 100;
		// Update the panel, indicating directory
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output " + "[Logging in file \\web-dir\\"
						+ JBroFuzzFormat.DATE + "\\" + getSessionNumber() + ".csv]  "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		// UI and Colors
		targetText.setEditable(false);
		targetText.setBackground(Color.BLACK);
		targetText.setForeground(Color.WHITE);
		portText.setEditable(false);
		portText.setBackground(Color.BLACK);
		portText.setForeground(Color.WHITE);

		String uri = targetText.getText();
		// Add a trailing / if one is not there
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		final String dirs = directoryText.getText();
		final String port = portText.getText();

		responseTable.removeAll();
		responseTableModel.removeAllRows();

		TableRowSorter<SixColumnModel> sorter = new TableRowSorter<SixColumnModel>(
				responseTableModel);
		responseTable.setRowSorter(sorter);

		cesg = new DRequestIterator(getFrame(), uri, dirs, port);
		cesg.run();
		
	}

	/**
	 * <p>
	 * Method for stopping the request iterator.
	 * </p>
	 */
	public void stop() {
		
		if (stopped) {
			return;
		}
		stopped = true;
		
		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, false, false, false);
		
		targetText.setEditable(true);
		targetText.setBackground(Color.WHITE);
		targetText.setForeground(Color.BLACK);
		portText.setEditable(true);
		portText.setBackground(Color.WHITE);
		portText.setForeground(Color.BLACK);

		cesg.stop();
		
	}
	
	public void graph() {
	}
	
	public void add() {
	}
	
	public void remove() {
	}

}
