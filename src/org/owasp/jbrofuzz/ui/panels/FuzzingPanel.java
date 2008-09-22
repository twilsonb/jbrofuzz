/**
 * JBroFuzz 1.1
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.event.*;

import javax.swing.text.*;
import javax.swing.table.*;

import org.owasp.jbrofuzz.fuzz.ConnectionException;
import org.owasp.jbrofuzz.fuzz.MessageCreator;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.tablemodels.SixColumnModel;
import org.owasp.jbrofuzz.ui.tablemodels.ThreeColumnModel;
import org.owasp.jbrofuzz.ui.viewers.WindowPlotter;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;
import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import org.owasp.jbrofuzz.ui.menu.*;
import org.owasp.jbrofuzz.core.*;
import org.owasp.jbrofuzz.fuzz.*;

import java.util.*;
import java.text.*;

import org.apache.commons.lang.*;

/**
 * <p>
 * The main "Fuzzing" panel, displayed within the Main Frame Window.
 * </p>
 * <p>
 * This panel performs all TCP related fuzzing operations, including the
 * addition and removal of generators, reporting back the results into the
 * current window, as well as writting them to file.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.1
 */
public class FuzzingPanel extends JBroFuzzPanel {

	private static final long serialVersionUID = 16982374020211L;
	
	// The output JPanel on the bottom (south) of the tab
	private final JPanel outputPanel;
	
	// The JTextField
	private final JTextField target;
	
	// The JTextArea
	private final NonWrappingTextPane message;
	
	// The JTable were results are outputted
	private JTable outputTable;
	
	// And the table model that goes with it
	private SixColumnModel outputTableModel;
	
	// The JTable of the generator
	private JTable fuzzersTable;
	
	// And the table model that goes with it
	private ThreeColumnModel mFuzzingTableModel;
	
	// The JButtons
	private final JButton buttonAddGen, buttonRemGen;
	
	// A counter for the number of times fuzz has been clicked
	private int counter, session;
	
	// The console
	private JTextArea console;
	
	// The frame window
	private JBroFuzzWindow m;

	private JSplitPane mainPane, topPane;

	private JTabbedPane topRightPanel;

	private int consoleEvent;

	private boolean stopped;

	/**
	 * This constructor is used for the " Fuzzing " panel that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
	 * 
	 * @param m
	 *            FrameWindow
	 */
	public FuzzingPanel(final JBroFuzzWindow m) {
		
		super(" Fuzzing ", m);

		this.m = m;
		counter = 1;
		session = 0;

		stopped = true;
		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, false, true, false);
		
		// The target panel
		final JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" URL "), BorderFactory.createEmptyBorder(
				1, 1, 1, 1)));

		target = new JTextField();
		target.setEditable(true);
		target.setVisible(true);
		target.setFont(new Font("Verdana", Font.BOLD, 12));
		target.setMargin(new Insets(1, 1, 1, 1));
		target.setBackground(Color.WHITE);
		target.setForeground(Color.BLACK);
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(target, true, true, true, true);
		
		targetPanel.add(target);

		// The message panel
		final JPanel requestPanel = new JPanel(new BorderLayout());
		requestPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Request "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		message = new NonWrappingTextPane();
		message.putClientProperty("charset", "UTF-8");
		message.setEditable(true);
		message.setVisible(true);
		message.setFont(new Font("Verdana", Font.PLAIN, 12));
		
		message.setMargin(new Insets(1, 1, 1, 1));
		message.setBackground(Color.WHITE);
		message.setForeground(Color.BLACK);
		// Set the editor kit responsible for highlighting
		message.setEditorKit(new StyledEditorKit() {

			private static final long serialVersionUID = -6085642347022880064L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}
			
		});
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(message, true, true, true, true);

		// The message scroll pane where the message pane sits
		final JScrollPane messageScrollPane = new JScrollPane(message);
		messageScrollPane.setVerticalScrollBarPolicy(20);
		messageScrollPane.setHorizontalScrollBarPolicy(30);
		requestPanel.add(messageScrollPane);

		// The add generator button
		buttonAddGen = new JButton(ImageCreator.ADD_IMG);
		buttonAddGen.setToolTipText("Add a Generator");
		buttonAddGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				FuzzingPanel.this.add();
			}
		});

		// The remove generator button
		buttonRemGen = new JButton(ImageCreator.REMOVE_IMG);
		buttonRemGen.setEnabled(false);
		buttonRemGen.setToolTipText("Remove a Generator");
		buttonRemGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				FuzzingPanel.this.remove();
			}
		});

		// The generator panel
		final JPanel generatorPanel = new JPanel();
		generatorPanel.setLayout(null);

		generatorPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Added Payloads Table"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		// The fuzzing table and model
		mFuzzingTableModel = new ThreeColumnModel(getFrame());
		fuzzersTable = new JTable();
		fuzzersTable.setBackground(Color.BLACK);
		fuzzersTable.setForeground(Color.WHITE);

		fuzzersTable.setModel(mFuzzingTableModel);
		// generatorTable.getSelectionModel().addListSelectionListener(new PayloadsRowListener());
		fuzzersTable.getModel().addTableModelListener(new PayloadsModelListener());
		
		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < mFuzzingTableModel.getColumnCount(); i++) {
			column = fuzzersTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(100);
			} else {
				column.setPreferredWidth(50);
			}
		}
		fuzzersTable.setFont(new Font("Monospaced", Font.PLAIN, 12));

		final JScrollPane generatorScrollPane = new JScrollPane(fuzzersTable);
		generatorScrollPane.setVerticalScrollBarPolicy(20);

		generatorScrollPane.setPreferredSize(new Dimension(180, 100));

		generatorScrollPane.setBounds(15, 25, 180, 100);

		buttonAddGen.setBounds(225, 35, 40, 30);
		buttonRemGen.setBounds(225, 80, 40, 30);

		generatorPanel.add(generatorScrollPane);
		generatorPanel.add(buttonRemGen);
		generatorPanel.add(buttonAddGen);

		generatorPanel.setBounds(570, 20, 300, 160);
		
		// The console panel

		JPanel consolePanel = new JPanel();
		consolePanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(" Requests "), 
						BorderFactory.createEmptyBorder(5, 5, 5, 5) 
				)
		);
		

		console = new JTextArea();
		console.setFont(new Font("Verdana", Font.PLAIN, 10));
		console.setEditable(false);
		
		// Right click: Cut, Copy, Paste, Select All
		popupText(console, false, true, false, true);
		
		consoleEvent = 0;

		JScrollPane consoleScrollPane = new JScrollPane(console);
		consolePanel.setLayout(new BorderLayout());
		consolePanel.add(consoleScrollPane, BorderLayout.CENTER);

		// The output panel
		outputPanel = new JPanel(new BorderLayout());

		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new SixColumnModel();
		outputTableModel.setColumnNames("No", "Target", "Timestamp", "Status", "Request", "Response Size");

		outputTable = new JTable(outputTableModel);

		TableRowSorter<SixColumnModel> sorter = new TableRowSorter<SixColumnModel>(outputTableModel);
		outputTable.setRowSorter(sorter);

		outputTable.setBackground(Color.white);
		// Right click: Open, Cut, Copy, Paste, Select All, Properties
		popupTable(outputTable, true, false, true, false, true, true);

		outputTable.setColumnSelectionAllowed(false);
		outputTable.setRowSelectionAllowed(true);

		outputTable.setFont(new Font("Monospaced", Font.BOLD, 12));
		outputTable.setBackground(Color.BLACK);
		outputTable.setForeground(Color.WHITE);
		outputTable.setSurrendersFocusOnKeystroke(true);
		outputTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// Set the column widths
		for (int i = 0; i < outputTableModel.getColumnCount(); i++) {
			column = outputTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(30);
			}
			if (i == 1) {
				column.setPreferredWidth(150);
			}
			if (i == 2) {
				column.setPreferredWidth(120);
			}
			if (i == 3) {
				column.setPreferredWidth(80);
			}
			if (i == 4) {
				column.setPreferredWidth(20);
			}
			if (i == 5) {
				column.setPreferredWidth(60);
			}
		}

		outputTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {

							final int c = outputTable.getSelectedRow();
							final String name = (String) outputTable.getModel()
									.getValueAt(
											outputTable
													.convertRowIndexToModel(c),
											0);
							new WindowViewer(FuzzingPanel.this, name, WindowViewer.VIEW_FUZZING_PANEL);

						}
					});
				}
			}
		});

		final JScrollPane outputScrollPane = new JScrollPane(outputTable);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		// outputScrollPane.setPreferredSize(new Dimension(840, 130));
		outputPanel.add(outputScrollPane);

		// Set the scroll areas
		JPanel topLeftPanel = new JPanel(new BorderLayout());
		topLeftPanel.add(targetPanel, BorderLayout.PAGE_START);
		topLeftPanel.add(requestPanel, BorderLayout.CENTER);

		topRightPanel = new JTabbedPane(2);
		topRightPanel.add(" Payloads ", generatorPanel);
		topRightPanel.add(" On The Wire (0) ", consolePanel);
		topRightPanel.setTabPlacement(JTabbedPane.TOP);

		mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		topPane.setOneTouchExpandable(false);
		topPane.setDividerLocation(600);
		topPane.setLeftComponent(topLeftPanel);
		topPane.setRightComponent(topRightPanel);

		mainPane.setOneTouchExpandable(false);
		mainPane.setDividerLocation(300);
		mainPane.setTopComponent(topPane);
		mainPane.setBottomComponent(outputPanel);

		// Allow for all areas to be resized to even not be seen
		Dimension minimumSize = new Dimension(0, 0);
		topLeftPanel.setMinimumSize(minimumSize);
		topRightPanel.setMinimumSize(minimumSize);
		topPane.setMinimumSize(minimumSize);
		outputPanel.setMinimumSize(minimumSize);

		this.add(mainPane, BorderLayout.CENTER);

		// Some value defaults
		target.setText("https://www.owasp.org");
		setMessageText(JBroFuzzFormat.REQUEST_TCP);

		message.setCaretPosition(0);

	}

	/**
	 * <p>
	 * Method for adding a generator.
	 * </p>
	 */
	public void add() {
		// Check to see what text has been selected
		String selectedText;
		try {
			selectedText = message.getSelectedText();
		} catch (final IllegalArgumentException e) {
			JOptionPane
					.showInputDialog(
							this,
							"An exception was thrown while attempting to get the selected text",
							"Add Generator", JOptionPane.ERROR_MESSAGE);
			selectedText = "";
		}
		
		// Find the location of where the text has been selected
		final int sPoint = message.getSelectionStart();
		final int fPoint = message.getSelectionEnd();

		new PayloadsDialog(this, sPoint, fPoint);

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();
		
	}
	
	public void addPayload(String Id, int start, int end) {

		mFuzzingTableModel.addRow(Id, start, end);

	}
	
	
	public void graph() {
		
		final WindowPlotter wd = new WindowPlotter(m, FileHandler
				.getName(FileHandler.DIR_TCPF));
		wd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					wd.dispose();
				}
			}
		});
		
	}

	/**
	 * <p>
	 * Method for returning the counter held within the Sniffing Panel which is
	 * responsible for counting the number of requests having been made. This
	 * method is used for generating unique sequential file name and row counts.
	 * </p>
	 * 
	 * @param newCount
	 *            boolean Increment the counter by 1
	 * @return String
	 */
	public String getCounter(final boolean newCount) {
		String s = "";
		// Integrity checks and loop calls...
		if ((counter < 0) || (counter > 1000000)) {
			counter = 1;
		}
		if ((session < 0) || (session > 100)) {
			session = 1;
		}

		// Append zeros to the session [0 - 99]
		if (session < 10) {
			s += "0";
		}
		s += session + "-";

		s += StringUtils.leftPad("" + counter, 8, '0');

		if (newCount) {
			counter++;
		}

		return s;
	}

	/**
	 * <p>
	 * Get the value of the Message String that is to be transmitted on the
	 * given Socket request that will be created.
	 * </p>
	 * 
	 * @return String
	 */
	public String getMessageText() {
		return message.getText();
	}

	/**
	 * Get the value of the target String stripping out, any protocol
	 * specifications as well as any trailing slashes.
	 * 
	 * @return String
	 */
	public String getTargetText() {
		
		return target.getText();
		
	}

	/**
	 * <p>
	 * Method for removing a generator. This method operates by removing a row
	 * from the corresponding table model of the generator table.
	 * </p>
	 */
	public void remove() {
		
		if(!isAdded()) {
			return;
		}
		
		final int rows = fuzzersTable.getRowCount();
		if (rows < 1) {
			return;
		}
		final String[] fuzzPoints = new String[rows];
		for (int i = 0; i < rows; i++) {
			fuzzPoints[i] = mFuzzingTableModel.getRow(i);
		}

		final String selectedFuzzPoint = (String) JOptionPane.showInputDialog(
				this, "Select the generator to remove:", "Remove Generator",
				JOptionPane.INFORMATION_MESSAGE, null, fuzzPoints,
				fuzzPoints[0]);

		if (selectedFuzzPoint != null) {
			final String[] splitString = selectedFuzzPoint
					.split(ThreeColumnModel.STRING_SEPARATOR);
			mFuzzingTableModel
					.removeRow(splitString[0],
							Integer.parseInt(splitString[1]), Integer
									.parseInt(splitString[2]));
		}
	}

	/**
	 * <p>
	 * Method for setting the text displayed in the message editor pane.
	 * </p>
	 * 
	 * @param input
	 */
	public void setMessageText(String input) {

		message.setText(input);

	}

	/**
	 * <p>
	 * Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 */
	public void start() {

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		// JButton startButton = getFrame().getFrameToolBar().start;
		// JButton stopButton = getFrame().getFrameToolBar().stop;
		// JButton buttonPlot = getFrame().getFrameToolBar().graph;

		if (!stopped) {
			return;
		}	
		stopped = false;
		// Start, Stop, Graph, Add, Remove
		setOptionsAvailable(false, true, false, false, false);
		buttonAddGen.setEnabled(false);
		buttonRemGen.setEnabled(false);
		
		
		// UI and Colors
		// startButton.setEnabled(false);
		// stopButton.setEnabled(true);
		// buttonPlot.setEnabled(true);
		target.setEditable(false);
		target.setBackground(Color.BLACK);
		target.setForeground(Color.WHITE);
		console.setText("");
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		topRightPanel.setTitleAt(1, " On The Wire ");
		topRightPanel.setSelectedIndex(1);
		consoleEvent = 0;
		// topRightPanel.set
		// port.setEditable(false);
		// port.setBackground(Color.BLACK);
		// port.setForeground(Color.WHITE);

		/*
		 * Check to see if a message is present if
		 * ("".equals(message.getText())) { JOptionPane.showMessageDialog(this,
		 * "The request field is blank.\n" + "Specify a request\n", "Empty
		 * Request Field", JOptionPane.INFORMATION_MESSAGE); return; }
		 */

		// Increment the session and reset the counter
		session++;
		counter = 1;
		// Update the border of the output panel
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output  " + "Logging in folder ("
						+ JBroFuzzFormat.DATE + ") Session " + session), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		final int rows = fuzzersTable.getRowCount();
		if (rows == 0) {

			MessageCreator currentMessage = new MessageCreator(getMessageText(), "", 0, 0);

			final Date now = new Date();
			final SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss-SSS");
			final String timestamp = format.format(now);
			final String filename = getCounter(true);

			outputTableModel.addRow(filename, getTargetText(), timestamp,
					"Sending...", "1 / 1", "0 bytes");
			outputTable.scrollRectToVisible(outputTable.getCellRect(outputTable
					.getRowCount(), 0, true));

			// Put the message on the console as it goes out on the wire
			toConsole(currentMessage.getMessageAsPutOnTheWire());

			try {
				Connection con = new Connection(getTargetText(), currentMessage.getMessage());
				
				outputTableModel.updateLastRow(filename, getTargetText(),
						timestamp, "Finished - " + con.getStatus(), "1 / 1",
						con.getReply().getBytes().length + " bytes");
				
				getFrame().getJBroFuzz().getHandler().writeFuzzFile2(
						"<!-- \r\n[ { 1 / 1 }, " + filename + " " + timestamp
								+ "] " + "\r\n" + getTargetText() + " : "
								+ con.getPort() + "\r\n" + con.getMessage()
								+ "\r\n-->\r\n" + con.getReply(), filename);
				
			} catch (ConnectionException e) {
				outputTableModel.updateLastRow(filename, getTargetText(),
						timestamp, "Exception - " + e.getMessage(), "1 / 1",
						"0 bytes");
				getFrame().getJBroFuzz().getHandler().writeFuzzFile2(
						"<!-- \r\n[ { 1 / 1 }, " + filename + " " + timestamp
								+ "] " + "\r\n" + getTargetText() + " : "
								+ "\r\n" + e.getMessage() + "\r\n-->\r\n",
						filename);
				
			}

		} else {
			for (int i = 0; i < rows; i++) {
				final String category = (String) mFuzzingTableModel.getValueAt(
						i, 0);
				final int start = ((Integer) mFuzzingTableModel
						.getValueAt(i, 1)).intValue();
				final int end = ((Integer) mFuzzingTableModel.getValueAt(i, 2))
						.intValue();

				try {
					for (Fuzzer f = new Fuzzer(category, Math.abs(end - start)); f
							.hasNext();) {

						if (!stopped) {
							String payload = f.next();
							MessageCreator currentMessage = new MessageCreator(
									getMessageText(), payload, start, end);
							final Date now = new Date();
							final SimpleDateFormat format = new SimpleDateFormat(
									"HH-mm-ss-SSS");
							final String timestamp = format.format(now);
							final String filename = getCounter(true);
							outputTableModel.addRow(filename, getTargetText(), timestamp, "Sending...",
									f.getCurrectValue() + "/"
											+ f.getMaximumValue(), "0 bytes");
							outputTable.scrollRectToVisible(outputTable
									.getCellRect(outputTable.getModel()
											.getRowCount(), 0, true));
							
							// Put the message on the console as it goes out on the wire
							toConsole(currentMessage.getMessageAsPutOnTheWire());
							
							try {
								Connection con = new Connection(getTargetText(), currentMessage
										.getMessage());

								getFrame().getJBroFuzz().getHandler()
										.writeFuzzFile2(
												"<!-- \r\n[ { "
														+ f.getCurrectValue()
														+ "/"
														+ f.getMaximumValue()
														+ " }, " + filename
														+ " " + timestamp
														+ "] \r\n"
														+ getTargetText()
														+ " : " + con.getPort()
														+ "\r\n"
														+ con.getMessage()
														+ "\r\n-->\r\n"
														+ con.getReply(),
												filename);

								outputTableModel.updateLastRow(filename, getTargetText(), timestamp,
										"Finished - " + con.getStatus(), f
												.getCurrectValue()
												+ "/" + f.getMaximumValue(),
										con.getReply().getBytes().length
												+ " bytes");
							} catch (ConnectionException e) {

								getFrame().getJBroFuzz().getHandler()
										.writeFuzzFile2(
												"<!-- \r\n[ { "
														+ f.getCurrectValue()
														+ "/"
														+ f.getMaximumValue()
														+ " }, " + filename
														+ " " + timestamp
														+ "] \r\n"
														+ getTargetText()
														+ " : " + "\r\n"
														+ e.getMessage()
														+ "\r\n-->\r\n",
												filename);

								outputTableModel.updateLastRow(filename, getTargetText(), timestamp,
										"Exception - " + e.getMessage(), f
												.getCurrectValue()
												+ "/" + f.getMaximumValue(),
										"0 bytes");
								
							}

							Runtime.getRuntime().gc();
							Runtime.getRuntime().runFinalization();
						}

					}
				} catch (NoSuchFuzzerException e) {

					getFrame().log("The fuzzer could not be found...");
				}

			}
		}

	}

	/**
	 * <p>
	 * Method trigerred when attempting to stop any fuzzing taking place.
	 * </p>
	 */
	public void stop() {

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		// JButton startButton = getFrame().getFrameToolBar().start;
		// JButton stopButton = getFrame().getFrameToolBar().stop;

		if (stopped) {
			return;
		}
		stopped = true;
		// Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, true, true, true);
		int total = 0;
		total = fuzzersTable.getRowCount(); 
		if(total > 0 ) {	
			buttonRemGen.setEnabled(true);
			setOptionRemove(true);
							
		} else {	
			buttonRemGen.setEnabled(false);
			setOptionRemove(false);
		}
		
		buttonAddGen.setEnabled(true);
		if(fuzzersTable.getRowCount() > 0) {
			buttonRemGen.setEnabled(true);
		}

		target.setEditable(true);
		target.setBackground(Color.WHITE);
		target.setForeground(Color.BLACK);
		console.setBackground(Color.WHITE);
		console.setForeground(Color.BLACK);
		
		topRightPanel.setSelectedIndex(0);

	}

	public void toConsole(String input) {

		consoleEvent++;
		topRightPanel.setTitleAt(1, " On The Wire (" + consoleEvent + ") ");

		// Use a FILO for the output to the console, never exceeding 500 lines
		if (console.getLineCount() > 500) {
			try {
				console.select(console.getLineStartOffset(0), console.getLineEndOffset( console.getLineCount() - 500 ));
				console.replaceSelection(null);
			}
			catch (BadLocationException e) {
				m.log("Fuzzing Panel: Could not clear the console");
			}
		} 
		
		console.append(input);
		console.setCaretPosition(console.getText().length());

	}


	/**
	 * <p>Inner class used to detect changes to the data managed by the fuzzers table model,
	 * where all the fuzzers and corresponding payloads are stored.</p>
	 *  
	 * <p>This class implements the TableModelListener interface and is called via 
	 * addTableModelListener() to catch events on the fuzzers table.</p> 
	 * 
	 * @author subere@uncon.org
	 * @since 1.1
	 * 
	 */
	private class PayloadsModelListener implements TableModelListener {
		
		public void tableChanged(final TableModelEvent event) {
			
			int total = 0;
			total = fuzzersTable.getRowCount(); 
			if(total > 0 ) {
				
				buttonRemGen.setEnabled(true);
				setOptionRemove(true);
								
			} else {	
				buttonRemGen.setEnabled(false);
				setOptionRemove(false);
			}
		}
	}
	
}

