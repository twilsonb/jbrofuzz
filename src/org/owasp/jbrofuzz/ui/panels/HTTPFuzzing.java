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
import javax.swing.SwingWorker3;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.TRequestIterator;
import org.owasp.jbrofuzz.fuzz.TConstructor;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.tablemodels.FuzzingTableModel;
import org.owasp.jbrofuzz.ui.tablemodels.SingleRowTableModel;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.ui.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.ui.util.TextHighlighter;
import org.owasp.jbrofuzz.ui.viewers.WindowPlotter;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;
import org.owasp.jbrofuzz.version.JBRFormat;

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
public class HTTPFuzzing extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9178417123149169115L;
	// Just a string that is being used a lot
	private static final String ADDGENSTRING = "Add Generator";

	// The frame that the sniffing panel is attached
	private final JBRFrame m;
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
	 * This constructor is used for the "HTTP Fuzzing Panel" that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
	 * 
	 * @param m
	 *          FrameWindow
	 */
	public HTTPFuzzing(final JBRFrame m) {
		super();
		this.setLayout(null);

		this.m = m;
		this.counter = 1;
		this.session = 0;

		// The target panel
		final JPanel targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Target URI [HTTP/HTTPS] "), BorderFactory.createEmptyBorder(1, 1,
				1, 1)));

		this.target = new JTextField();
		this.target.setEditable(true);
		this.target.setVisible(true);
		this.target.setFont(new Font("Verdana", Font.BOLD, 12));
		this.target.setMargin(new Insets(1, 1, 1, 1));
		this.target.setBackground(Color.WHITE);
		this.target.setForeground(Color.BLACK);
		this.getFrameWindow().popup(this.target);

		this.target.setPreferredSize(new Dimension(480, 20));
		targetPanel.add(this.target);

		targetPanel.setBounds(10, 20, 500, 60);
		this.add(targetPanel);
		// The port panel
		final JPanel portPanel = new JPanel();
		portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1,
				1)));

		this.port = new JFormattedTextField();

		this.port.setEditable(true);
		this.port.setVisible(true);
		this.port.setFont(new Font("Verdana", Font.BOLD, 12));
		this.port.setMargin(new Insets(1, 1, 1, 1));
		this.port.setBackground(Color.WHITE);
		this.port.setForeground(Color.BLACK);
		this.getFrameWindow().popup(this.port);

		this.port.setPreferredSize(new Dimension(50, 20));
		portPanel.add(this.port);

		portPanel.setBounds(510, 20, 60, 60);
		this.add(portPanel);
		
		// The message panel
		final JPanel requestPanel = new JPanel();
		requestPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Request [Header/Body] "), BorderFactory.createEmptyBorder(5, 5,
				5, 5)));

		this.message = new NonWrappingTextPane();

		this.message.putClientProperty("charset", "UTF-8");
		this.message.setEditable(true);
		this.message.setVisible(true);
		this.message.setFont(new Font("Verdana", Font.PLAIN, 12));

		this.message.setMargin(new Insets(1, 1, 1, 1));
		this.message.setBackground(Color.WHITE);
		this.message.setForeground(Color.BLACK);
		//
		this.message.setEditorKit(new StyledEditorKit() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1185615447033483344L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}
		});
		this.getFrameWindow().popup(this.message);

		final JScrollPane messageScrollPane = new JScrollPane(this.message);
		messageScrollPane.setVerticalScrollBarPolicy(20);
		messageScrollPane.setHorizontalScrollBarPolicy(30);
		messageScrollPane.setPreferredSize(new Dimension(540, 160));
		requestPanel.add(messageScrollPane);

		requestPanel.setBounds(10, 80, 560, 200);
		this.add(requestPanel);

		// The add generator button
		this.buttonAddGen = new JButton(ImageCreator.ADD_IMG);
		this.buttonAddGen.setToolTipText("Add a Generator");
		this.buttonAddGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				HTTPFuzzing.this.generatorAddButton();
			}
		});

		// The remove generator button
		this.buttonRemGen = new JButton(ImageCreator.REMOVE_IMG);
		this.buttonRemGen.setToolTipText("Remove a Generator");
		this.buttonRemGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				HTTPFuzzing.this.generatorRemoveButton();
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
		this.mFuzzingTableModel = new FuzzingTableModel(HTTPFuzzing.this.getFrameWindow());
		this.generatorTable = new JTable();
		this.generatorTable.setBackground(Color.WHITE);
		this.generatorTable.setForeground(Color.BLACK);

		this.generatorTable.setModel(this.mFuzzingTableModel);
		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < this.mFuzzingTableModel.getColumnCount(); i++) {
			column = this.generatorTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(100);
			} else {
				column.setPreferredWidth(50);
			}
		}
		this.generatorTable.setFont(new Font("Monospaced", Font.PLAIN, 12));

		final JScrollPane generatorScrollPane = new JScrollPane(this.generatorTable);
		generatorScrollPane.setVerticalScrollBarPolicy(20);

		generatorScrollPane.setPreferredSize(new Dimension(180, 100));

		generatorScrollPane.setBounds(15, 25, 180, 100);
		this.buttonRemGen.setBounds(235, 60, 40, 20);
		this.buttonAddGen.setBounds(235, 25, 40, 20);

		generatorPanel.add(generatorScrollPane);
		generatorPanel.add(this.buttonRemGen);
		generatorPanel.add(this.buttonAddGen);

		generatorPanel.setBounds(570, 20, 300, 160);
		this.add(generatorPanel);
		// The fuzz buttons
		this.buttonFuzzStart = new JButton("Fuzz!", ImageCreator.START_IMG);
		this.buttonFuzzStart.setBounds(580, 210, 90, 40);
		this.buttonFuzzStart.setToolTipText("Start Fuzzing!");
		this.add(this.buttonFuzzStart);
		this.buttonFuzzStart.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				HTTPFuzzing.this.worker = new SwingWorker3() {
					@Override
					public Object construct() {
						HTTPFuzzing.this.fuzzStartButton();
						return "start-window-return";
					}

					@Override
					public void finished() {
						HTTPFuzzing.this.fuzzStopButton();
					}
				};
				HTTPFuzzing.this.worker.start();
			}
		});
		this.buttonFuzzStop = new JButton("Stop", ImageCreator.STOP_IMG);
		this.buttonFuzzStop.setEnabled(false);
		this.buttonFuzzStop.setToolTipText("Stop Fuzzing");
		this.buttonFuzzStop.setBounds(680, 210, 90, 40);
		this.add(this.buttonFuzzStop);
		this.buttonFuzzStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						HTTPFuzzing.this.fuzzStopButton();
					}
				});
			}
		});
		// The plot button
		this.buttonPlot = new JButton("Bro", ImageCreator.PAUSE_IMG);
		this.buttonPlot.setEnabled(false);
		this.buttonPlot.setToolTipText("Plot Fuzzing Results");
		this.buttonPlot.setBounds(780, 210, 80, 40);
		this.add(this.buttonPlot);
		this.buttonPlot.addActionListener(new ActionListener() {
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
		this.outputPanel = new JPanel();
		this.outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Responses "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		this.outputTableModel = new SingleRowTableModel(" Output ");

		this.outputTable = new JTable();
		this.outputTable.setModel(this.outputTableModel);

		this.outputTable.setFont(new Font("Monospaced", Font.BOLD, 12));
		this.outputTable.setBackground(Color.BLACK);
		this.outputTable.setForeground(Color.WHITE);
		this.outputTable.setSurrendersFocusOnKeystroke(true);
		this.outputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// getFrameWindow().popup(outputTable);

		final JScrollPane outputScrollPane = new JScrollPane(this.outputTable);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		outputScrollPane.setPreferredSize(new Dimension(840, 130));
		this.outputPanel.add(outputScrollPane);
		this.outputPanel.setBounds(10, 280, 860, 170);
		this.add(this.outputPanel);

		// Add the action listener for each row
		final ListSelectionModel rowSM = this.outputTable.getSelectionModel();

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
					final String s = HTTPFuzzing.this.outputTableModel
							.getValueAt(selectedRow);
					new WindowViewer(HTTPFuzzing.this.getFrameWindow(), s,
							WindowViewer.VIEW_FUZZING_PANEL);
				}
			}
		}); // ListSelectionListener

		// Some value defaults
		this.target.setText("http://localhost");
		this.port.setText("80");
		setMessageText(JBRFormat.REQUEST_HTTP);
		this.message.setCaretPosition(0);
	}

	/**
	 * <p>Method responsible for appending a row in the output table. The string
	 * passed as input represents the value that will be displayed on the
	 * corresponding row.</p>
	 * 
	 * @param s
	 */
	public void addRowInOuputTable(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				HTTPFuzzing.this.outputTableModel.addEmptyRow();
				final int totalRows = HTTPFuzzing.this.outputTableModel.getRowCount();
				HTTPFuzzing.this.outputTableModel.setValueAt(s, totalRows - 1, 0);
				// Set the last row to be visible
				HTTPFuzzing.this.outputTable
						.scrollRectToVisible(HTTPFuzzing.this.outputTable.getCellRect(
								HTTPFuzzing.this.outputTable.getRowCount(), 0, true));
			}
		});

	}

	/**
	 * <p>
	 * Method trigered when the stop Bro button is pressed in the current panel.
	 * </p>
	 *
	 */
	public void fuzzBroButton() {
		if (!this.buttonPlot.isEnabled()) {
			return;
		}
		final WindowPlotter wd = new WindowPlotter(FileHandler.getFuzzDirName());
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
	public void fuzzStartButton() {
		if (!this.buttonFuzzStart.isEnabled()) {
			return;
		}
		// UI and Colors
		this.buttonFuzzStart.setEnabled(false);
		this.buttonFuzzStop.setEnabled(true);
		this.buttonPlot.setEnabled(true);
		this.target.setEditable(false);
		this.target.setBackground(Color.BLACK);
		this.target.setForeground(Color.WHITE);
		this.port.setEditable(false);
		this.port.setBackground(Color.BLACK);
		this.port.setForeground(Color.WHITE);
		// Check to see if a message is present
		if ("".equals(this.message.getText())) {
			JOptionPane.showMessageDialog(this, "The request field is blank.\n"
					+ "Specify a request\n", "Empty Request Field",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// Increment the session and reset the counter
		this.session++;
		this.counter = 1;
		// Update the border of the output panel
		this.outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Responses  "
						+ "Logging in folder (" + this.getJBroFuzz().getFormat().getDate() +
						") Session " + this.session), BorderFactory.createEmptyBorder(5, 5,
				5, 5)));

		final int rows = this.generatorTable.getRowCount();
		if (rows < 1) {
			final StringBuffer sbuf = new StringBuffer(this.getMessageText());
			this.mRIterator = new TRequestIterator(this.getJBroFuzz(), sbuf, 0, 0,
					"ZER");
			this.mRIterator.run();
		} else {
			for (int i = 0; i < rows; i++) {
				final String generator = (String) this.mFuzzingTableModel.getValueAt(i,
						0);
				final int start = ((Integer) this.mFuzzingTableModel.getValueAt(i, 1))
						.intValue();
				final int end = ((Integer) this.mFuzzingTableModel.getValueAt(i, 2))
						.intValue();

				final StringBuffer sbuf = new StringBuffer(this.getMessageText());
				//this.mRIterator = new RequestIterator(this.getJBroFuzz(), sbuf, start,
				// 		end, generator);
				// this.mRIterator.run();
			}
		}
	}

	/**
	 * <p>
	 * Method trigered when attempting to stop any fuzzing taking place.
	 * </p>
	 */
	public void fuzzStopButton() {
		if (!this.buttonFuzzStop.isEnabled()) {
			return;
		}
		this.mRIterator.stop();
		// UI and Colors
		this.buttonFuzzStart.setEnabled(true);
		this.buttonFuzzStop.setEnabled(false);
		this.target.setEditable(true);
		this.target.setBackground(Color.WHITE);
		this.target.setForeground(Color.BLACK);
		this.port.setEditable(true);
		this.port.setBackground(Color.WHITE);
		this.port.setForeground(Color.BLACK);
	}

	/**
	 * <p>
	 * Method for adding a generator.
	 * </p>
	 */
	public void generatorAddButton() {
		// Check to see what text has been selected
		String selectedText;
		try {
			selectedText = this.message.getSelectedText();
		} catch (final IllegalArgumentException e) {
			JOptionPane.showInputDialog(this,
					"An exception was thrown while attempting to get the selected text",
					HTTPFuzzing.ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
			selectedText = "";
		}
		// If no text has been selected, prompt the user to select some text
		if (selectedText == null) {
			JOptionPane.showMessageDialog(this,
					"Select (highlight) a text range \nfrom the Request field",
					HTTPFuzzing.ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
		}
		// Else find out the location of where the text has been selected
		else {
			final int sPoint = this.message.getSelectionStart();
			final int fPoint = this.message.getSelectionEnd();

			final TConstructor mTConstructor = new TConstructor(this.getJBroFuzz());
			final String generators = mTConstructor.getAllGeneratorNamesAndComments();
			final String[] generatorArray = generators.split(", ");

			// Then prompt the user for the type of fuzzer
			String selectedValue = (String) JOptionPane.showInputDialog(this,
					"Select the type of fuzzing generator:", HTTPFuzzing.ADDGENSTRING,
					JOptionPane.INFORMATION_MESSAGE, null, generatorArray,
					generatorArray[0]);
			// And finally add the generator
			if ((selectedValue != null)) {
				if (selectedValue.length() > 3) {
					selectedValue = selectedValue.substring(0, 3);
				} else {
					selectedValue = "   ";
				}
				this.mFuzzingTableModel.addRow(selectedValue, sPoint, fPoint);
			}
		}
	}

	/**
	 * <p>
	 * Method for removing a generator. This method operates by removing a row
	 * from the corresponding table model of the generator table.
	 * </p>
	 */
	public void generatorRemoveButton() {
		final int rows = this.generatorTable.getRowCount();
		if (rows < 1) {
			return;
		}
		final String[] fuzzPoints = new String[rows];
		for (int i = 0; i < rows; i++) {
			fuzzPoints[i] = this.mFuzzingTableModel.getRow(i);
		}

		final String selectedFuzzPoint = (String) JOptionPane.showInputDialog(this,
				"Select the generator to remove:", "Remove Generator",
				JOptionPane.INFORMATION_MESSAGE, null, fuzzPoints, fuzzPoints[0]);

		if (selectedFuzzPoint != null) {
			final String[] splitString = selectedFuzzPoint
					.split(FuzzingTableModel.STRING_SEPARATOR);
			this.mFuzzingTableModel.removeRow(splitString[0], Integer
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
		if ((this.counter < 0) || (this.counter > 1000000)) {
			this.counter = 1;
		}
		if ((this.session < 0) || (this.session > 100)) {
			this.session = 1;
		}

		// Append zeros to the session [0 - 99]
		if (this.session < 10) {
			s += "0";
		}
		s += this.session + "-";
		// Append zeros to the counter [0 - 999999]
		if (this.counter < 100000) {
			s += "0";
		}
		if (this.counter < 10000) {
			s += "0";
		}
		if (this.counter < 1000) {
			s += "0";
		}
		if (this.counter < 100) {
			s += "0";
		}
		if (this.counter < 10) {
			s += "0";
		}
		s += this.counter;

		if (newCount) {
			this.counter++;
		}

		return s;
	}

	/**
	 * Access the main frame window in which this panel is attached to.
	 * 
	 * @return FrameWindow
	 */
	public JBRFrame getFrameWindow() {
		return this.m;
	}

	/**
	 * Access the main object that launches and is responsible for the
	 * application.
	 * 
	 * @return JBroFuzz
	 */
	public JBroFuzz getJBroFuzz() {
		
		return this.m.getJBroFuzz();
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
		
		return this.message.getText();
	
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
		
		return this.port.getText();
		
	}

	/**
	 * Get the value of the target String stripping out, any protocol
	 * specifications as well as any trailing slashes.
	 * 
	 * @return String
	 */
	public String getTargetText() {
		
		return this.target.getText();
		
	}

	/**
	 * Set the button enable status of the Fuzz! button
	 * 
	 * @param b
	 *          boolean
	 */
	public void setFuzzStartButtonEnable(final boolean b) {
		
		this.buttonFuzzStart.setEnabled(b);
		
	}

	/**
	 * Set the button enable status of the Stop button
	 * 
	 * @param b
	 *          boolean
	 */
	public void setFuzzStopButtonEnable(final boolean b) {
		
		this.buttonFuzzStop.setEnabled(b);
		
	}

	/**
	 * <p>
	 * Method for setting the text displayed in the message editor pane.
	 * </p>
	 * 
	 * @param input
	 */
	public void setMessageText(String input) {
		
		this.message.setText(input);
		
	}

}
