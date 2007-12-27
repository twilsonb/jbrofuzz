/**
 * HTTPFuzzing.java 0.7
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

import org.owasp.jbrofuzz.fuzz.HTTPRequestIterator;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.tablemodels.FuzzingTableModel;
import org.owasp.jbrofuzz.ui.tablemodels.WebDirectoriesModel;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.ui.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.ui.util.TextHighlighter;
import org.owasp.jbrofuzz.ui.viewers.WindowPlotter;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;
import org.owasp.jbrofuzz.version.JBRFormat;
import org.owasp.jbrofuzz.ui.menu.GeneratorDialog;;

/**
 * <p>
 * The "HTTP Fuzzing" panel, displayed within the Main Frame Window.
 * </p>
 * <p>
 * This panel performs all HTTP related fuzzing operations, including the
 * addition and removal of generators, reporting back the results into the
 * current window, as well as writting them to file.
 * </p>
 * 
 * @author subere (at) uncon org
 * @version 0.7
 */
public class HTTPFuzzing extends JBRPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9178417123149169115L;
	// Just a string that is being used a lot
	private static final String ADDGENSTRING = "Add Generator";

	// The JPanels
	private final JPanel outputPanel;
	// The JTextField
	private final JTextField target;
	// The port JFormattedTextField
	private final JFormattedTextField port;
	// The JTextArea
	private final NonWrappingTextPane message;
	// The JTable were results are output
	private JTable outputTable;
	// And the table model that goes with it
	private WebDirectoriesModel outputTableModel;
	// The JTable of the generator
	private JTable generatorTable;
	// And the table model that goes with it
	private FuzzingTableModel mFuzzingTableModel;
	// The JButtons
	private final JButton buttonAddGen, buttonRemGen, buttonFuzzStart,
	buttonFuzzStop, buttonPlot;
	// The swing worker used when the button "fuzz" is pressed
	private SwingWorker3 worker;
	// A counter for the number of times fuzz has been clicked
	private int counter, session;
	// The HTTP request iterator responsible for fuzzing
	private HTTPRequestIterator httpIter;

	/**
	 * This constructor is used for the "HTTP Fuzzing Panel" that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
	 * 
	 * @param m
	 *          FrameWindow
	 */
	public HTTPFuzzing(final JBRFrame m) {
		super(m);
		// this.setLayout(null);

		// this.m = m;
		counter = 1;
		session = 0;

		// The target panel
		final JPanel targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Target URI [HTTP/HTTPS] "), BorderFactory.createEmptyBorder(1, 1,
						1, 1)));

		target = new JTextField();
		target.setEditable(true);
		target.setVisible(true);
		target.setFont(new Font("Verdana", Font.BOLD, 12));
		target.setMargin(new Insets(1, 1, 1, 1));
		target.setBackground(Color.WHITE);
		target.setForeground(Color.BLACK);
		getFrame().popup(target);

		target.setPreferredSize(new Dimension(480, 20));
		targetPanel.add(target);

		targetPanel.setBounds(10, 20, 500, 60);
		this.add(targetPanel);
		// The port panel
		final JPanel portPanel = new JPanel();
		portPanel.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory
						.createTitledBorder(" Port "), 
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		port = new JFormattedTextField();

		port.setEditable(true);
		port.setVisible(true);
		port.setFont(new Font("Verdana", Font.BOLD, 12));
		port.setMargin(new Insets(1, 1, 1, 1));
		port.setBackground(Color.WHITE);
		port.setForeground(Color.BLACK);
		getFrame().popup(port);

		port.setPreferredSize(new Dimension(50, 20));
		portPanel.add(port);

		portPanel.setBounds(510, 20, 60, 60);
		this.add(portPanel);

		// The message panel
		final JPanel requestPanel = new JPanel();
		requestPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Request [Header/Body] "), BorderFactory.createEmptyBorder(5, 5,
						5, 5)));

		message = new NonWrappingTextPane();

		message.putClientProperty("charset", "UTF-8");
		message.setEditable(true);
		message.setVisible(true);
		message.setFont(new Font("Verdana", Font.PLAIN, 12));

		message.setMargin(new Insets(1, 1, 1, 1));
		message.setBackground(Color.WHITE);
		message.setForeground(Color.BLACK);
		//
		message.setEditorKit(new StyledEditorKit() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1185615447033483344L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}
		});
		getFrame().popup(message);

		final JScrollPane messageScrollPane = new JScrollPane(message);
		messageScrollPane.setVerticalScrollBarPolicy(20);
		messageScrollPane.setHorizontalScrollBarPolicy(30);
		messageScrollPane.setPreferredSize(new Dimension(540, 160));
		requestPanel.add(messageScrollPane);

		requestPanel.setBounds(10, 80, 560, 200);
		this.add(requestPanel);

		// The add generator button
		buttonAddGen = new JButton(ImageCreator.ADD_IMG);
		buttonAddGen.setToolTipText("Add a Generator");
		buttonAddGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				HTTPFuzzing.this.add();
			}
		});

		// The remove generator button
		buttonRemGen = new JButton(ImageCreator.REMOVE_IMG);
		buttonRemGen.setToolTipText("Remove a Generator");
		buttonRemGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				HTTPFuzzing.this.remove();
			}
		});

		// The generator panel
		final JPanel generatorPanel = new JPanel();
		generatorPanel.setLayout(null);
		generatorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Added Generators Table"), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		mFuzzingTableModel = new FuzzingTableModel(getFrame());
		generatorTable = new JTable();
		generatorTable.setBackground(Color.black);
		generatorTable.setForeground(Color.white);

		generatorTable.setModel(mFuzzingTableModel);
		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < mFuzzingTableModel.getColumnCount(); i++) {
			column = generatorTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(130);
			} 
			else {
				column.setPreferredWidth(40);
				column.setCellRenderer(new DefaultTableCellRenderer(){
					/**
					 * 
					 */
					private static final long serialVersionUID = 21070730038541596L;

					@Override
					public Component getTableCellRendererComponent(final JTable tblDataTable, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column){
						final JLabel ret= (JLabel) super.getTableCellRendererComponent(tblDataTable,value,isSelected,hasFocus,row,column);
						ret.setHorizontalAlignment(SwingConstants.RIGHT);
						return ret;
					}
				});
			}
		}
		generatorTable.setFont(new Font("Verdana", Font.BOLD, 12));

		final JScrollPane generatorScrollPane = new JScrollPane(generatorTable);
		generatorScrollPane.setPreferredSize(new Dimension(180, 100));
		generatorScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		generatorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		generatorScrollPane.setBounds(15, 25, 200, 120);
		buttonRemGen.setBounds(235, 60, 40, 20);
		buttonAddGen.setBounds(235, 25, 40, 20);

		generatorPanel.add(generatorScrollPane);
		generatorPanel.add(buttonRemGen);
		generatorPanel.add(buttonAddGen);

		generatorPanel.setBounds(570, 20, 300, 160);
		this.add(generatorPanel);
		// The fuzz buttons
		buttonFuzzStart = new JButton("Fuzz!", ImageCreator.START_IMG);
		buttonFuzzStart.setBounds(580, 210, 90, 40);
		buttonFuzzStart.setToolTipText("Start Fuzzing!");
		this.add(buttonFuzzStart);
		buttonFuzzStart.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				worker = new SwingWorker3() {
					@Override
					public Object construct() {
						HTTPFuzzing.this.start();
						return "start-window-return";
					}

					@Override
					public void finished() {
						HTTPFuzzing.this.stop();
					}
				};
				worker.start();
			}
		});
		buttonFuzzStop = new JButton("Stop", ImageCreator.STOP_IMG);
		buttonFuzzStop.setEnabled(false);
		buttonFuzzStop.setToolTipText("Stop Fuzzing");
		buttonFuzzStop.setBounds(680, 210, 90, 40);
		this.add(buttonFuzzStop);
		buttonFuzzStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						HTTPFuzzing.this.stop();
					}
				});
			}
		});
		// The plot button
		buttonPlot = new JButton("Bro", ImageCreator.PAUSE_IMG);
		buttonPlot.setEnabled(false);
		buttonPlot.setToolTipText("Plot Fuzzing Results");
		buttonPlot.setBounds(780, 210, 80, 40);
		this.add(buttonPlot);
		buttonPlot.addActionListener(new ActionListener() {
			// public void actionPerformed(final ActionEvent e) {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						HTTPFuzzing.this.fuzzBroButton();
					}
				});

			}
		});

		// The output panel
		outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Responses "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new WebDirectoriesModel();

		outputTable = new JTable();
		outputTable.setModel(outputTableModel);

		outputTable.setFont(new Font("Monospaced", Font.BOLD, 12));
		outputTable.setBackground(Color.BLACK);
		outputTable.setForeground(Color.WHITE);
		outputTable.setSurrendersFocusOnKeystroke(true);
		outputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// getFrameWindow().popup(outputTable);

		final JScrollPane outputScrollPane = new JScrollPane(outputTable);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		outputScrollPane.setPreferredSize(new Dimension(840, 130));
		outputPanel.add(outputScrollPane);
		outputPanel.setBounds(10, 280, 860, 170);
		this.add(outputPanel);

		// Add the action listener for each row
		final ListSelectionModel rowSM = outputTable.getSelectionModel();

		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent e) {
				// Ignore extra messages
				if (e.getValueIsAdjusting()) {
					return;
				}
				final ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					// No rows selected
				} else {
					final int selectedRow = lsm.getMinSelectionIndex();
					final String s = (String) outputTableModel.getValueAt(selectedRow, 0);
					new WindowViewer(getFrame(), s,
							WindowViewer.VIEW_FUZZING_PANEL);
				}
			}
		}); // ListSelectionListener

		// Some value defaults
		target.setText("http://localhost");
		port.setText("80");
		setMessageText(JBRFormat.REQUEST_HTTP);
		message.setCaretPosition(0);
	}

	/**
	 * <p>Method responsible for appending a row in the output table. The string
	 * passed as input represents the value that will be displayed on the
	 * corresponding row.</p>
	 * 
	 * @param s
	 *
	public void addRowInOuputTable(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				outputTableModel.addEmptyRow();
				final int totalRows = outputTableModel.getRowCount();
				outputTableModel.setValueAt(s, totalRows - 1, 0);
				// Set the last row to be visible
				outputTable
				.scrollRectToVisible(outputTable.getCellRect(
						outputTable.getRowCount(), 0, true));
			}
		});

	}
	*/
	
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
					outputTableModel.addRow(inputArray[0],
							inputArray[1], inputArray[2], inputArray[3], inputArray[4],
							inputArray[5]);
					/* Set the last row to be visible
					outputTableModel
							.scrollRectToVisible(outputTable
									.getCellRect(
											outputTable.getRowCount() - 1, 0,
											true));
					*/
				}
			});

		}
	}

	/**
	 * <p>
	 * Method trigered when the stop Bro button is pressed in the current panel.
	 * </p>
	 *
	 */
	public void fuzzBroButton() {
		if (!buttonPlot.isEnabled()) {
			return;
		}
		final WindowPlotter wd = new WindowPlotter("...");
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
	 * Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 */
	public void start() {

		if (!buttonFuzzStart.isEnabled()) {
			return;
		}

		// UI and Colors
		buttonFuzzStart.setEnabled(false);
		buttonFuzzStop.setEnabled(true);
		buttonPlot.setEnabled(true);
		target.setEditable(false);
		target.setBackground(Color.BLACK);
		target.setForeground(Color.WHITE);
		port.setEditable(false);
		port.setBackground(Color.BLACK);
		port.setForeground(Color.WHITE);

		// Check to see if a message is present
		if ("".equals(message.getText())) {
			JOptionPane.showMessageDialog(this, "The request field is blank.\n"
					+ "Specify a request\n", "Empty Request Field",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// Increment the session and reset the counter
		session++;
		counter = 1;
		// Update the border of the output panel
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Responses  "
						+ "Logging in folder (" + JBRFormat.DATE +
						") Session " + session), BorderFactory.createEmptyBorder(5, 5,
								5, 5)));
		// Start the fuzzing
		httpIter = new HTTPRequestIterator(getFrame().getJBroFuzz());
		httpIter.run();

	}

	/**
	 * <p>
	 * Method trigered when attempting to stop any fuzzing taking place.
	 * </p>
	 */
	public void stop() {
		if (!buttonFuzzStop.isEnabled()) {
			return;
		}
		httpIter.stop();
		// UI and Colors
		buttonFuzzStart.setEnabled(true);
		buttonFuzzStop.setEnabled(false);
		target.setEditable(true);
		target.setBackground(Color.WHITE);
		target.setForeground(Color.BLACK);
		port.setEditable(true);
		port.setBackground(Color.WHITE);
		port.setForeground(Color.BLACK);
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
			JOptionPane.showInputDialog(this,
					"An exception was thrown while attempting to get the selected text",
					HTTPFuzzing.ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
			selectedText = "";
		}

		// If no text has been selected, select the first point position
		int sPoint = message.getCaretPosition();
		int fPoint = message.getCaretPosition();

		if (selectedText != null) {
			sPoint = message.getSelectionStart();
			fPoint = message.getSelectionEnd();
		}
		// Call the Generator Dialog that add a row in the generator table
		new GeneratorDialog(getFrame(), sPoint, fPoint);
	}

	/**
	 * <p>
	 * Method for removing a generator. This method operates by removing a row
	 * from the corresponding table model of the generator table.
	 * </p>
	 */
	public void remove() {
		final int rows = generatorTable.getRowCount();
		if (rows < 1) {
			return;
		}
		final String[] fuzzPoints = new String[rows];
		for (int i = 0; i < rows; i++) {
			fuzzPoints[i] = mFuzzingTableModel.getRow(i);
		}

		final String selectedFuzzPoint = (String) JOptionPane.showInputDialog(this,
				"Select the generator to remove:", "Remove Generator",
				JOptionPane.INFORMATION_MESSAGE, null, fuzzPoints, fuzzPoints[0]);

		if (selectedFuzzPoint != null) {
			final String[] splitString = selectedFuzzPoint
			.split(FuzzingTableModel.STRING_SEPARATOR);
			mFuzzingTableModel.removeRow(splitString[0], Integer
					.parseInt(splitString[1]), Integer.parseInt(splitString[2]));
		}
	}

	/**
	 * <p>
	 * Method for returning the counter held within the HTTP Fuzzing Panel which is
	 * responsible for counting the number of requests having been made. This
	 * method is used for generating unique sequential file name and row counts.
	 * </p>
	 * 
	 * @param newCount
	 *          boolean Increment the counter by 1
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
		// Append zeros to the counter [0 - 999999]
		if (counter < 100000) {
			s += "0";
		}
		if (counter < 10000) {
			s += "0";
		}
		if (counter < 1000) {
			s += "0";
		}
		if (counter < 100) {
			s += "0";
		}
		if (counter < 10) {
			s += "0";
		}
		s += counter;

		if (newCount) {
			counter++;
		}

		return s;
	}

	/**
	 * <p>
	 * Get the value of the Message String that is to be transmitted on the given
	 * Socket request that will be created.
	 * </p>
	 * 
	 * @return String
	 */
	public String getMessageText() {

		return message.getText();

	}

	/**
	 * <p>
	 * Get the value of the port String trimming it down to a maximum of 5
	 * characters.
	 * </p>
	 * 
	 * @return String
	 */
	public String getPortText() {

		return port.getText();

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
	 * Method for setting the text displayed in the message editor pane.
	 * </p>
	 * 
	 * @param input
	 */
	public void setMessageText(final String input) {

		message.setText(input);

	}

}
