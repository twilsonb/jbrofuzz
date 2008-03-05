/**
 * FuzzingPanel.java 0.6
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

import org.owasp.jbrofuzz.fuzz.TRequestIterator;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.tablemodels.FuzzingTableModel;
import org.owasp.jbrofuzz.ui.tablemodels.SingleRowTableModel;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.ui.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.ui.util.TextHighlighter;
import org.owasp.jbrofuzz.ui.viewers.WindowPlotter;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;
import org.owasp.jbrofuzz.util.SwingWorker3;
import org.owasp.jbrofuzz.version.JBRFormat;

/**
 * <p>
 * The main "TCP Fuzzing" panel, displayed within the Main Frame Window.
 * </p>
 * <p>
 * This panel performs all TCP related fuzzing operations, including the
 * addition and removal of generators, reporting back the results into the
 * current window, as well as writting them to file.
 * </p>
 * 
 * @author subere (at) uncon org
 * @version 0.6
 */
public class TCPFuzzing extends JBRPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9148417039346169565L;
	// Just a string that is being used a lot
	private static final String ADDGENSTRING = "Add Generator";

	private static String stringReplace(final String toFind, final String original,
			final String substitute) {
		int found = 0;
		int start = 0;
		String returnString = original;
		while (found != -1) {
			found = returnString.indexOf(toFind, start);
			if (found != -1) {
				returnString = returnString.substring(0, found).concat(substitute)
						.concat(returnString.substring(found + toFind.length()));
			}
			start = found + substitute.length();
		}
		return returnString;
	}

	// The frame that the sniffing panel is attached
	// private final JBRFrame m;
	// The JPanels
	private final JPanel outputPanel;
	// The JTextField
	private final JTextField target;
	// The port JFormattedTextField
	private final JFormattedTextField port;
	// The JTextArea
	private final NonWrappingTextPane message;
	// The JTable were results are outputed
	private JTable outputTable;
	// And the table model that goes with it
	private SingleRowTableModel outputTableModel;
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
	// The request iterator performing all the fuzzing
	private TRequestIterator mRIterator;

	/**
	 * This constructor is used for the "TCP Fuzzing Panel" that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
	 * 
	 * @param m
	 *          FrameWindow
	 */
	public TCPFuzzing(final JBRFrame m) {
		super(m);
		// this.setLayout(null);

		// this.m = m;
		counter = 1;
		session = 0;

		// The target panel
		final JPanel targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Target "), BorderFactory.createEmptyBorder(1, 1,
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
		portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1,
				1)));

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
				.createTitledBorder(" Request "), BorderFactory.createEmptyBorder(5, 5,
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
			private static final long serialVersionUID = -6085642347022880064L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}
		});
		getFrame().popup(message);

		final JScrollPane messageScrollPane = new JScrollPane(message);
		messageScrollPane.setVerticalScrollBarPolicy(20);
		messageScrollPane.setHorizontalScrollBarPolicy(30);
		messageScrollPane.setPreferredSize(new Dimension(480, 160));
		requestPanel.add(messageScrollPane);

		requestPanel.setBounds(10, 80, 500, 200);
		this.add(requestPanel);

		// The add generator button
		buttonAddGen = new JButton(ImageCreator.ADD_IMG);
		buttonAddGen.setToolTipText("Add a Generator");
		buttonAddGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				TCPFuzzing.this.add();
			}
		});

		// The remove generator button
		buttonRemGen = new JButton(ImageCreator.REMOVE_IMG);
		buttonRemGen.setToolTipText("Remove a Generator");
		buttonRemGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				TCPFuzzing.this.remove();
			}
		});

		// The generator panel
		final JPanel generatorPanel = new JPanel();
		generatorPanel.setLayout(null);

		generatorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Added Generators Table"), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));
		/*
		 * Fuzzing Table Model
		 */
		mFuzzingTableModel = new FuzzingTableModel(getFrame());
		generatorTable = new JTable();
		generatorTable.setBackground(Color.WHITE);
		generatorTable.setForeground(Color.BLACK);

		generatorTable.setModel(mFuzzingTableModel);
		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < mFuzzingTableModel.getColumnCount(); i++) {
			column = generatorTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(100);
			} else {
				column.setPreferredWidth(50);
			}
		}
		generatorTable.setFont(new Font("Monospaced", Font.PLAIN, 12));

		final JScrollPane generatorScrollPane = new JScrollPane(generatorTable);
		generatorScrollPane.setVerticalScrollBarPolicy(20);

		generatorScrollPane.setPreferredSize(new Dimension(180, 100));

		generatorScrollPane.setBounds(15, 25, 180, 100);
		buttonRemGen.setBounds(235, 60, 40, 20);
		buttonAddGen.setBounds(235, 25, 40, 20);

		generatorPanel.add(generatorScrollPane);
		generatorPanel.add(buttonRemGen);
		generatorPanel.add(buttonAddGen);

		generatorPanel.setBounds(570, 20, 300, 160);
		this.add(generatorPanel);
		// The fuzz buttons
		buttonFuzzStart = new JButton("Fuzz!", ImageCreator.START_IMG);
		buttonFuzzStart.setBounds(580, 230, 90, 40);
		buttonFuzzStart.setToolTipText("Start Fuzzing!");
		this.add(buttonFuzzStart);
		buttonFuzzStart.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				worker = new SwingWorker3() {
					@Override
					public Object construct() {
						TCPFuzzing.this.start();
						return "start-window-return";
					}

					@Override
					public void finished() {
						TCPFuzzing.this.stop();
					}
				};
				worker.start();
			}
		});
		buttonFuzzStop = new JButton("Stop", ImageCreator.STOP_IMG);
		buttonFuzzStop.setEnabled(false);
		buttonFuzzStop.setToolTipText("Stop Fuzzing");
		buttonFuzzStop.setBounds(680, 230, 90, 40);
		this.add(buttonFuzzStop);
		buttonFuzzStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						TCPFuzzing.this.stop();
					}
				});
			}
		});
		// The plot button
		buttonPlot = new JButton("Bro", ImageCreator.PAUSE_IMG);
		buttonPlot.setEnabled(false);
		buttonPlot.setToolTipText("Plot Fuzzing Results");
		buttonPlot.setBounds(780, 230, 80, 40);
		this.add(buttonPlot);
		buttonPlot.addActionListener(new ActionListener() {
			// public void actionPerformed(final ActionEvent e) {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						TCPFuzzing.this.fuzzBroButton();
					}
				});

			}
		});

		// The output panel
		outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new SingleRowTableModel( " Output ");

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
					final String s = outputTableModel
							.getValueAt(selectedRow);
					new WindowViewer(getFrame(), s,
							WindowViewer.VIEW_FUZZING_PANEL);
				}
			}
		}); // ListSelectionListener

		// Some value defaults
		target.setText("http://localhost");
		port.setText("80");
		setMessageText(JBRFormat.REQUEST_TCP);
		message.setCaretPosition(0);
	}

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

	public void fuzzBroButton() {
		if (!buttonPlot.isEnabled()) {
			return;
		}
		final WindowPlotter wd = new WindowPlotter(FileHandler.getName(FileHandler.DIR_TCPF));
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
				.createTitledBorder(" Output (Last 1000 Lines)  "
						+ "Logging in folder (" + JBRFormat.DATE +
						// getJBroFuzz().getVersion().getDate() +
						") Session " + session), BorderFactory.createEmptyBorder(5, 5,
				5, 5)));

		final int rows = generatorTable.getRowCount();
		if (rows < 1) {
			final StringBuffer sbuf = new StringBuffer(getMessageText());
			mRIterator = new TRequestIterator(getFrame().getJBroFuzz(), sbuf, 0, 0,
					"ZER");
			mRIterator.run();
		} else {
			for (int i = 0; i < rows; i++) {
				final String generator = (String) mFuzzingTableModel.getValueAt(i,
						0);
				final int start = ((Integer) mFuzzingTableModel.getValueAt(i, 1))
						.intValue();
				final int end = ((Integer) mFuzzingTableModel.getValueAt(i, 2))
						.intValue();

				final StringBuffer sbuf = new StringBuffer(getMessageText());
				mRIterator = new TRequestIterator(getFrame().getJBroFuzz(), sbuf, start,
						end, generator);
				mRIterator.run();
			}
		}
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
		mRIterator.stop();
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
					TCPFuzzing.ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
			selectedText = "";
		}
		// If no text has been selected, prompt the user to select some text
		if (selectedText == null) {
			JOptionPane.showMessageDialog(this,
					"Select (highlight) a text range \nfrom the Request field",
					TCPFuzzing.ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
		}
		// Else find out the location of where the text has been selected
		else {
			final int sPoint = message.getSelectionStart();
			final int fPoint = message.getSelectionEnd();

			// final TConstructor mTConstructor = new TConstructor(getFrame().getJBroFuzz());
			final String generators = ""; // mTConstructor.getAllGeneratorNamesAndComments();
			final String[] generatorArray = generators.split(", ");

			// Then prompt the user for the type of fuzzer
			String selectedValue = (String) JOptionPane.showInputDialog(this,
					"Select the type of fuzzing generator:", TCPFuzzing.ADDGENSTRING,
					JOptionPane.INFORMATION_MESSAGE, null, generatorArray,
					generatorArray[0]);
			// And finally add the generator
			if ((selectedValue != null)) {
				if (selectedValue.length() > 3) {
					selectedValue = selectedValue.substring(0, 3);
				} else {
					selectedValue = "   ";
				}
				mFuzzingTableModel.addRow(selectedValue, sPoint, fPoint);
			}
		}
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
	 * Method for returning the counter held within the Sniffing Panel which is
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
		String output = message.getText();
		output = stringReplace("\n", output, "");
		output = stringReplace("\\n", output, "\n");
		output = stringReplace("\\r", output, "\r");
		return output;
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
		final String text = port.getText();
		return text;
	}

	/**
	 * Get the value of the target String stripping out, any protocol
	 * specifications as well as any trailing slashes.
	 * 
	 * @return String
	 */
	public String getTargetText() {
		String text = target.getText();
		int len = text.length();

		if (text.startsWith("ftp://")) {
			text = text.substring(6, len);
			len = text.length();
			target.setText(text);
		}
		if (text.startsWith("http://")) {
			text = text.substring(7, len);
			len = text.length();
			target.setText(text);
		}
		if (text.startsWith("https://")) {
			text = text.substring(8, len);
			len = text.length();
			target.setText(text);
		}
		if (text.endsWith("/")) {
			text = text.substring(0, len - 1);
			// If another if statement is included, update the len variable here
			target.setText(text);
		}
		return text;
	}

	/**
	 * <p>
	 * Method for setting the text displayed in the message editor pane.
	 * </p>
	 * 
	 * @param input
	 */
	public void setMessageText(String input) {

		input = stringReplace("\n", input, "\\n");
		input = stringReplace("\r", input, "\\r");
		input = stringReplace("<-new-line->", input, "\n");
		message.setText(input);
	}

}
