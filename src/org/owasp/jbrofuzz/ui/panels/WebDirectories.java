/**
 * WebDirectoriesPanel.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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
 */
package org.owasp.jbrofuzz.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker3;
import javax.swing.table.TableColumn;

import org.owasp.jbrofuzz.fuzz.dir.DRequestIterator;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.tablemodels.WebDirectoriesModel;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.ui.util.TableSorter;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * The web directory panel that is attached to the main frame.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class WebDirectories extends JBRPanel implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1291872530751555718L;

	// The text areas used in their corresponding panels
	private JTextArea directoryText;

	private JTextField targetText;

	private JFormattedTextField portText;

	// The JButtons present in the user interface
	private final JButton startButton, stopButton;

	// The JTable holding all the responses
	private JTable responseTable;

	// The corresponding table model
	private WebDirectoriesModel responseTableModel;

	// The request iterator to loop through the directories
	private DRequestIterator cesg;

	// The directory panel that needs to update the line number
	private JPanel directoryPanel, outputPanel;

	// The session count counting how many times start has been hit
	private int session;

	// The progress bar for the site
	private JProgressBar progressBar;

	// The table sorter
	private TableSorter sorter;

	/**
	 * The constructor for the Web Directory Panel. This constructor spawns the
	 * main panel involving web directories.
	 * 
	 * @param m
	 *          FrameWindow
	 */
	public WebDirectories(final JBRFrame m) {
		super(m);
		// this.m = m;
		this.session = 0;

		// Define the directory JPanel
		this.directoryPanel = new JPanel();
		this.directoryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Total Directories to test: "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		this.directoryPanel.setBounds(630, 20, 230, 430);
		this.add(this.directoryPanel);

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
				.createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1,
				1)));
		portPanel.setBounds(510, 20, 60, 60);
		this.add(portPanel);

		// Define the output JPanel
		this.outputPanel = new JPanel();
		this.outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "), BorderFactory.createEmptyBorder(1, 1,
				1, 1)));
		this.outputPanel.setBounds(10, 150, 610, 300);
		this.add(this.outputPanel);

		// Define the target text area
		this.targetText = new JTextField();
		this.targetText.setEditable(true);
		this.targetText.setVisible(true);
		this.targetText.setFont(new Font("Verdana", Font.BOLD, 12));
		this.targetText.setMargin(new Insets(1, 1, 1, 1));
		this.targetText.setBackground(Color.WHITE);
		this.targetText.setForeground(Color.BLACK);
		this.targetText.setPreferredSize(new Dimension(480, 20));
		getFrame().popup(this.targetText);
		targetPanel.add(this.targetText);

		// Define the port text area
		this.portText = new JFormattedTextField();
		this.portText.setEditable(true);
		this.portText.setVisible(true);
		this.portText.setFont(new Font("Verdana", Font.BOLD, 12));
		this.portText.setMargin(new Insets(1, 1, 1, 1));
		this.portText.setBackground(Color.WHITE);
		this.portText.setForeground(Color.BLACK);
		getFrame().popup(this.portText);
		this.portText.setPreferredSize(new Dimension(50, 20));
		portPanel.add(this.portText);

		// Define the directory text area
		this.directoryText = new JTextArea(1, 1);
		this.directoryText.setEditable(true);
		this.directoryText.setVisible(true);
		this.directoryText.setFont(new Font("Verdana", Font.BOLD, 12));
		this.directoryText.setLineWrap(false);
		this.directoryText.setWrapStyleWord(true);
		this.directoryText.setMargin(new Insets(1, 1, 1, 1));
		this.directoryText.setBackground(Color.WHITE);
		this.directoryText.setForeground(Color.BLACK);
		this.directoryText.addKeyListener(this);
		getFrame().popup(this.directoryText);
		final JScrollPane directoryScrollPane = new JScrollPane(this.directoryText);
		directoryScrollPane.setVerticalScrollBarPolicy(20);
		directoryScrollPane.setHorizontalScrollBarPolicy(30);
		directoryScrollPane.setPreferredSize(new Dimension(210, 390));
		this.directoryPanel.add(directoryScrollPane);

		// The add generator button
		this.startButton = new JButton("Start", ImageCreator.START_IMG);
		this.startButton.setBounds(420, 95, 90, 40);
		this.startButton
				.setToolTipText("Start Fuzzing through the Directories List");
		this.add(this.startButton);
		this.startButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final SwingWorker3 worker = new SwingWorker3() {
					@Override
					public Object construct() {
						WebDirectories.this.start();
						return "start-window-return";
					}

					@Override
					public void finished() {
						WebDirectories.this.stop();
					}
				};
				worker.start();
			}
		});

		this.stopButton = new JButton("Stop", ImageCreator.STOP_IMG);
		this.stopButton.setBounds(520, 95, 90, 40);
		this.stopButton.setToolTipText("Stop Fuzzing through the Directories List");
		this.add(this.stopButton);
		this.stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				WebDirectories.this.stop();
			}
		});
		
		this.responseTableModel = new WebDirectoriesModel();
		this.sorter = new TableSorter(this.responseTableModel);
		this.responseTable = new JTable(this.sorter);

		this.responseTable.getTableHeader().setToolTipText("Click to sort by row");
		this.popup(this.responseTable);
		this.sorter.setTableHeader(this.responseTable.getTableHeader());
		this.responseTable.setFont(new Font("Monospaced", Font.BOLD, 12));
		this.responseTable.setBackground(Color.black);
		this.responseTable.setForeground(Color.white);
		this.responseTable.setSurrendersFocusOnKeystroke(true);
		// this.responseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.responseTable.setColumnSelectionAllowed(false);
		this.responseTable.setRowSelectionAllowed(true);
		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < this.responseTableModel.getColumnCount(); i++) {
			column = this.responseTable.getColumnModel().getColumn(i);
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

		final JScrollPane listTextScrollPane = new JScrollPane(this.responseTable);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(31);
		listTextScrollPane.setPreferredSize(new Dimension(590, 260));
		listTextScrollPane.setWheelScrollingEnabled(true);
		this.outputPanel.add(listTextScrollPane);

		this.progressBar = new JProgressBar(0);
		this.progressBar.setValue(0);
		this.progressBar.setStringPainted(true);
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(100);
		this.progressBar.setPreferredSize(new Dimension(310, 20));
		final JPanel progressPanel = new JPanel();
		progressPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Progress "), BorderFactory.createEmptyBorder(1,
				1, 1, 1)));
		progressPanel.setBounds(10, 85, 330, 60);
		progressPanel.add(this.progressBar);
		this.add(progressPanel);

		this.startButton.setEnabled(true);
		this.stopButton.setEnabled(false);

		this.targetText.setText("http://localhost");
		this.portText.setText("80");
	}

	/**
	 * <p>
	 * Method for adding an extra row to the output response table. The different
	 * fields are identified by \n.
	 * 
	 * @param s
	 *          String
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
					WebDirectories.this.responseTableModel.addRow(inputArray[0],
							inputArray[1], inputArray[2], inputArray[3], inputArray[4],
							inputArray[5]);
					// Set the last row to be visible
					WebDirectories.this.responseTable
							.scrollRectToVisible(WebDirectories.this.responseTable
									.getCellRect(
											WebDirectories.this.responseTable.getRowCount() - 1, 0,
											true));
				}
			});

		}
	}

	/**
	 * Method triggered when the start button is pressed.
	 */
	public void start() {
		if (!this.startButton.isEnabled()) {
			return;
		}
		// Increment the session number
		this.session++;
		this.session %= 100;
		// Update the panel, indicating directory
		this.outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output " + "[Logging in file \\web-dir\\"
						+ getFrame().getJBroFuzz().getFormat().getDate() + "\\"
						+ this.getSessionNumber() + ".csv]  "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));

		// UI and Colors
		this.startButton.setEnabled(false);
		this.stopButton.setEnabled(true);
		this.targetText.setEditable(false);
		this.targetText.setBackground(Color.BLACK);
		this.targetText.setForeground(Color.WHITE);
		this.portText.setEditable(false);
		this.portText.setBackground(Color.BLACK);
		this.portText.setForeground(Color.WHITE);

		String uri = this.targetText.getText();
		// Add a trailing / if one is not there
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		final String dirs = this.directoryText.getText();
		final String port = this.portText.getText();

		this.responseTableModel.removeAllRows();

		this.cesg = new DRequestIterator(getFrame(), uri, dirs, port);
		this.cesg.run();
	}

	/**
	 * <p>
	 * Method for stopping the request iterator.
	 * </p>
	 */
	public void stop() {
		if (!this.stopButton.isEnabled()) {
			return;
		}

		// UI and Colors
		this.stopButton.setEnabled(false);
		this.startButton.setEnabled(true);
		this.targetText.setEditable(true);
		this.targetText.setBackground(Color.WHITE);
		this.targetText.setForeground(Color.BLACK);
		this.portText.setEditable(true);
		this.portText.setBackground(Color.WHITE);
		this.portText.setForeground(Color.BLACK);

		this.cesg.stop();
	}

	/**
	 * Get the session number. This number represents the number of times the
	 * start button has been hit.
	 * 
	 * @return String
	 */
	public String getSessionNumber() {
		String s = "";
		if (this.session < 10) {
			s += "0";
		}
		s += this.session;
		return s;
	}

	/**
	 * Handle the key pressed event from the text field.
	 * 
	 * @param e
	 *          KeyEvent
	 */
	public void keyPressed(final KeyEvent e) {
		// System.out.println(directoryText.getLineCount() );
	}

	/**
	 * Handle the key released event from the text field.
	 * 
	 * @param e
	 *          KeyEvent
	 */
	public void keyReleased(final KeyEvent e) {
		// System.out.println(directoryText.getLineCount() );
	}

	/**
	 * Handle the key typed event from the text field.
	 * 
	 * @param ke
	 *          KeyEvent
	 */
	public void keyTyped(final KeyEvent ke) {
		this.directoryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Total Directories to test: "
						+ this.directoryText.getLineCount() + " "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));
	}

	/**
	 * Method for setting up the right click copy paste cut and select all menu.
	 * 
	 * @param area
	 *          JTextArea
	 */
	private void popup(final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i4 = new JMenuItem("Select All");
		final JMenuItem i5 = new JMenuItem("Open in Browser");

		i2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		i4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		popmenu.add(i2);
		popmenu.addSeparator();
		popmenu.add(i4);
		popmenu.addSeparator();
		popmenu.add(i5);

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Copy
				StringBuffer selectionBuffer = new StringBuffer();
				int[] selection = area.getSelectedRows();
				for(int element : selection) {
					for(int i = 0; i < area.getRowCount(); i++) {
						selectionBuffer.append(area.getModel().getValueAt(element, i));
						if(i < area.getRowCount() - 1) {
							selectionBuffer.append(",");
						}
					}
					selectionBuffer.append("\n");
				}
				final JTextArea myTempArea = new JTextArea();
				myTempArea.setText(selectionBuffer.toString());
				myTempArea.selectAll();
				myTempArea.copy();
				area.removeRowSelectionInterval(0, area.getRowCount() - 1);
				
				/*
				area.removeRowSelectionInterval(0, area.getRowCount() - 1);
				final int[] a = area.getSelectedRows();
				final StringBuffer s = new StringBuffer();
				for (final int element : a) {
					final TableSorter tb = (TableSorter) area.getModel();
					final WebDirectoriesModel wm = (WebDirectoriesModel) tb
							.getTableModel();
					final String row = wm.getRow(element);
					s.append(row);
				}
				final JTextArea myTempArea = new JTextArea(s.toString());
				myTempArea.selectAll();
				myTempArea.copy();
				*/
			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		i5.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				Browser.init();
				final String url = (String) area.getValueAt(area.getSelectedRow(),
						1 % area.getColumnCount());
				try {
					Browser.displayURL(url);
				} catch (final IOException ex) {
					getFrame().log(
							"Could not launch link in external browser");
				}
			}
		});

		area.addMouseListener(new MouseAdapter() {
			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				this.checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				this.checkForTriggerEvent(e);
			}
		});
	}

	/**
	 * Set the text content of the directories jtextarea.
	 * 
	 * @param s
	 *          StringBuffer
	 */
	public void setDirectoriesText(final StringBuffer s) {
		this.directoryText.setText(s.toString());
		this.directoryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Total Directories to test: "
						+ this.directoryText.getLineCount() + " "), BorderFactory
						.createEmptyBorder(1, 1, 1, 1)));
		this.directoryText.setCaretPosition(0);
	}

	/**
	 * Set the progress bar on the display to a value between 0 and 100.
	 * 
	 * @param percent
	 *          int
	 */
	public void setProgressBar(final int percent) {
		if ((percent >= 0) && (percent <= 100)) {
			final SwingWorker3 progressWorker = new SwingWorker3() {
				@Override
				public Object construct() {
					WebDirectories.this.progressBar.setValue(percent);
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
	 * Get the value of the check box, allowing the application to continue
	 * running even if an error occurs.
	 * 
	 * @return boolean
	 * 
	 * public boolean getCheckBoxValue() { return checkbox; }
	 */

}
