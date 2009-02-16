/**
 * JBroFuzz 1.2
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
package org.owasp.jbrofuzz.fuzz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.core.Fuzzer;
import org.owasp.jbrofuzz.core.NoSuchFuzzerException;
import org.owasp.jbrofuzz.fuzz.ui.FuzzersAddedTableModel;
import org.owasp.jbrofuzz.fuzz.ui.ResponseTableModel;
import org.owasp.jbrofuzz.payloads.PayloadsDialog;
import org.owasp.jbrofuzz.ui.JBroFuzzPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TextHighlighter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

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
 * @version 1.2
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
	private ResponseTableModel outputTableModel;

	// The JTable of the generator
	private JTable fuzzersTable;

	// And the table model that goes with it
	private FuzzersAddedTableModel mFuzzingTableModel;

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
		setOptionsAvailable(true, false, true, true, false);

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
		buttonAddGen = new JButton(ImageCreator.IMG_ADD);
		buttonAddGen.setToolTipText("Add a Generator");
		buttonAddGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				FuzzingPanel.this.add();
			}
		});

		// The remove generator button
		buttonRemGen = new JButton(ImageCreator.IMG_REMOVE);
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
		mFuzzingTableModel = new FuzzersAddedTableModel(getFrame());
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

		outputTableModel = new ResponseTableModel();

		outputTable = new JTable(outputTableModel);

		TableRowSorter<ResponseTableModel> sorter = new TableRowSorter<ResponseTableModel>(outputTableModel);
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
							new WindowViewer(FuzzingPanel.this, name);

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
		setTextRequest(JBroFuzzFormat.URL_REQUEST);

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
	 * <p>Get the values of the Payloads from their table, limited 
	 * to a maximum of 1024 rows.</p>
	 * <p>Return the values in Comma Separated Fields.</p>
	 * 
	 * @return The values of Payloads Table as CSV Text
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public String getTextPayloads() {

		final int rows = mFuzzingTableModel.getRowCount();
		if (rows == 0) return "";

		StringBuffer output = new StringBuffer();
		// MAX_LINES = 1024
		for(int row = 0; row < Math.min(rows, 1024); row++) {

			for(int column = 0; column < mFuzzingTableModel.getColumnCount(); column++) {
				output.append(mFuzzingTableModel.getValueAt(row, column));
				// Append a ',' but not for the last value
				if(column != mFuzzingTableModel.getColumnCount() - 1) {
					output.append(',');
				}
			}
			// Append a new line, but not for the last line
			if(row != Math.min(rows, 1024) - 1) {
				output.append('\n');
			}
		}

		return output.toString();
	}


	/**
	 * <p>Get the value of the Request String, limited to a maximum of 
	 * 16384 characters.</p>
	 * 
	 * @return String
	 */
	public String getTextRequest() {

		return StringUtils.abbreviate(message.getText(), 16384);

	}

	/**
	 * <p>Get the value of the URL String, limited to a maximum of 1024
	 * characters.</p>
	 * 
	 * @return String
	 */
	public String getTextURL() {

		return StringUtils.abbreviate(target.getText(), 1024);

	}

	/**
	 * <p>Method for setting the text displayed in the "Request" 
	 * pane.</p>
	 * <p>Also resets the caret position to 0.</p>
	 * 
	 * @param input The String of header lines plus body to be
	 * displayed.
	 * @see #setTextURL(String)
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void setTextRequest(String input) {

		message.setText(input);
		message.setCaretPosition(0);

	}

	/**
	 * <p>Method for setting the URL text field.</p>
	 * 
	 * @param input
	 *
	 * @see #setTextRequest(String)
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void setTextURL(String input) {

		target.setText(input);

	}

	/**
	 * <p>
	 * Method for removing a generator. This method operates by removing a row
	 * from the corresponding table model of the generator table.
	 * </p>
	 */
	public void remove() {

		if(!isAddedEnabled()) {
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
			.split(FuzzersAddedTableModel.STRING_SEPARATOR);
			mFuzzingTableModel
			.removeRow(splitString[0],
					Integer.parseInt(splitString[1]), Integer
					.parseInt(splitString[2]));
		}
	}

	/**
	 * <p>Clear the URL, Request and Payloads and Responses Table Fields.
	 * Also, set the focus on the URL area.</p>
	 * <p>Used when opening a file, or with a File -> New operation.</p>
	 * 
	 *
	 * @see 
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void clearAllFields() {

		target.setText("");
		message.setText("");		
		console.setText("");

		topRightPanel.setTitleAt(1, " On The Wire (0) ");
		topRightPanel.setSelectedIndex(0);

		while (fuzzersTable.getRowCount() > 0) {
			mFuzzingTableModel.removeRow(0);
		}
		while (outputTable.getRowCount() > 0) {
			outputTableModel.removeRow(0);
		}

		target.requestFocusInWindow();
	}

	/**
	 * <p>
	 * Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 */
	public void start() {

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		if (!stopped) {
			return;
		}	
		stopped = false;

		// Start, Stop, Graph, Add, Remove
		setOptionsAvailable(false, true, true, false, false);
		buttonAddGen.setEnabled(false);
		buttonRemGen.setEnabled(false);


		target.setEditable(false);
		target.setBackground(Color.BLACK);
		target.setForeground(Color.WHITE);

		console.append("\n--> JBroFuzz Fuzzing Session: " + (session + 1) + " -->\n\n");
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);

		topRightPanel.setTitleAt(1, " On The Wire (0) ");
		topRightPanel.setSelectedIndex(1);
		consoleEvent = 0;

		// Check the certificate
		/*try {
			Connection.installCert(getTextURL());
		} catch (Exception e2) {
			this.getFrame().log("Certificate Related Exception: " + e2.getMessage());
		}*/
		
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

			MessageCreator currentMessage = new MessageCreator(getTextRequest(), "", 0, 0);
			MessageWriter outputMessage = new MessageWriter(currentMessage, this);

			outputTableModel.addNewRow(outputMessage);

			outputTable.scrollRectToVisible(outputTable.getCellRect(outputTable
					.getRowCount(), 0, true));

			// Put the message on the console as it goes out on the wire
			toConsole(currentMessage.getMessageForDisplayPurposes());

			try {

				// Connect
				Connection connection = new Connection(getTextURL(), currentMessage.getMessage());
				// Update the message writer
				outputMessage.setConnection(connection);
				// Update the last row, indicating success
				outputTableModel.updateLastRow(outputMessage);


			} catch (ConnectionException e1) {

				// Update the message writer
				outputMessage.setException(e1);
				// Update the last row, indicating an error
				outputTableModel.updateLastRow(outputMessage, e1);

			}

			getFrame().getJBroFuzz().getHandler().writeFuzzFile(outputMessage);


		} else {
			for (int i = 0; i < rows; i++) {

				final String category = (String) mFuzzingTableModel.getValueAt(i, 0);
				final int start = ((Integer) mFuzzingTableModel.getValueAt(i, 1)).intValue();
				final int end = ((Integer) mFuzzingTableModel.getValueAt(i, 2)).intValue();

				try {

					for (Fuzzer f = getFrame().getJBroFuzz().getDatabase().createFuzzer(category, Math.abs(end - start)); f.hasNext();) {

						if(stopped) {
							return;
						}
						
						String payload = f.next();
						MessageCreator currentMessage = new MessageCreator(getTextRequest(), payload, start, end);
						MessageWriter outputMessage = new MessageWriter(currentMessage, this);

						outputTableModel.addNewRow(outputMessage);


						outputTable.scrollRectToVisible(outputTable.getCellRect(outputTable.getModel()
								.getRowCount(), 0, true));

						// Put the message on the console as it goes out on the wire
						toConsole(currentMessage.getMessageForDisplayPurposes());

						try {

							// Connect
							Connection connection = new Connection(getTextURL(), currentMessage.getMessage());
							// Update the message writer
							outputMessage.setConnection(connection);
							// Update the last row, indicating success
							outputTableModel.updateLastRow(outputMessage);


						} catch (ConnectionException e1) {

							// Update the message writer
							outputMessage.setException(e1);
							// Update the last row, indicating an error
							outputTableModel.updateLastRow(outputMessage, e1);

						}

						getFrame().getJBroFuzz().getHandler().writeFuzzFile(outputMessage);

						Runtime.getRuntime().gc();
						Runtime.getRuntime().runFinalization();
					}

				} catch (NoSuchFuzzerException e) {

					getFrame().log("The fuzzer could not be found...");
				}

			}

		} // else statement for no rows

		// Tidy up matters in case threading chills out...
		stop();

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

		// Get the preference for showing the "On The Wire" tab
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean showWireTab = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_3, false);

		if(showWireTab) {
			topRightPanel.setSelectedIndex(1);
		} else {
			topRightPanel.setSelectedIndex(0);
		}

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

	/**
	 * <p>Check if fuzzing is taking place.</p>
	 * 
	 * @return True if a fuzzing session is underway.
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public boolean isStopped() {
		return stopped;
	}
}

