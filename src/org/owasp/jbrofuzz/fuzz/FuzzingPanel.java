/**
 * JBroFuzz 1.5
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.core.Fuzzer;
import org.owasp.jbrofuzz.core.NoSuchFuzzerException;
import org.owasp.jbrofuzz.fuzz.ui.FuzzersAddedTableModel;
import org.owasp.jbrofuzz.fuzz.ui.ResponseTableModel;
import org.owasp.jbrofuzz.payloads.PayloadsDialog;
import org.owasp.jbrofuzz.ui.JBroFuzzPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.viewers.WindowViewerFrame;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TextHighlighter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The "Fuzzing" panel, displayed within the main frame window.
 * </p>
 * 
 * <p>
 * This panel performs all HTTP and HTTPS related fuzzing operations.
 * </p>
 * 
 * <p>
 * A user can select their request, specify the target URL and proceed to add
 * and remove any particular fuzzing payloads, using the "Add", "Remove"
 * buttons.
 * </p>
 * 
 * <p>
 * Finally, all output (apart from being saved to file) is presented in the
 * bottom part of the panel inside the output table.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.5
 */
public class FuzzingPanel extends JBroFuzzPanel {

	/**
	 * <p>
	 * Inner class used to detect changes to the data managed by the fuzzers
	 * table model, where all the fuzzers and corresponding payloads are stored.
	 * </p>
	 * 
	 * <p>
	 * This class implements the TableModelListener interface and is called via
	 * addTableModelListener() to catch events on the fuzzers table.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @since 1.1
	 * 
	 */
	private class PayloadsModelListener implements TableModelListener {

		public void tableChanged(final TableModelEvent event) {

			int total = 0;
			total = fuzzersTable.getRowCount();
			if (total > 0) {

				setOptionRemove(true);

			} else {

				setOptionRemove(false);
			}
		}
	}

	private static final long serialVersionUID = 16982374020211L;

	// The output JPanel on the bottom (south) of the tab
	private final JPanel outputPanel;

	// The JTextField
	private final JTextField url_textField;

	// The JTextArea
	private JTextPane request_textPane;

	// The JTable were results are outputted
	private JTable outputTable;

	// And the table model that goes with it
	private ResponseTableModel outputTableModel;

	// The JTable of the generator
	private JTable fuzzersTable;

	// And the table model that goes with it
	private FuzzersAddedTableModel mFuzzingTableModel;

	// A counter for the number of times fuzz has been clicked
	private int counter, session;

	// The "On The Wire" console
	private JTextPane onTheWire_textArea;

	// The frame window
	private JBroFuzzWindow jbrofuzz_MainFrame;

	private JSplitPane mainPane, topPane;

	private JTabbedPane topRightPanel;

	private int onTheWireEvent;

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

		this.jbrofuzz_MainFrame = m;
		counter = 1;
		session = 0;

		stopped = true;
		// Set the enabled options: Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, true, true, false);

		// The URL panel
		final JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" URL "), BorderFactory.createEmptyBorder(
				1, 1, 1, 1)));

		url_textField = new JTextField();
		url_textField.setEditable(true);
		url_textField.setVisible(true);
		url_textField.setFont(new Font("Verdana", Font.BOLD, 12));
		url_textField.setMargin(new Insets(1, 1, 1, 1));
		url_textField.setBackground(Color.WHITE);
		url_textField.setForeground(Color.BLACK);

		// Right click: Cut, Copy, Paste, Select All
		popupText(url_textField, true, true, true, true);

		targetPanel.add(url_textField);

		// The request panel
		final JPanel requestPanel = new JPanel(new BorderLayout());
		requestPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Request "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		// Get the preferences for wrapping lines of text
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean wrapText = prefs.getBoolean(JBroFuzzFormat.WRAP_REQUEST, false);

		if (!wrapText) {
			request_textPane = new NonWrappingTextPane();
		} else {
			request_textPane = new JTextPane();
		}

		request_textPane.putClientProperty("charset", "UTF-8");
		request_textPane.setEditable(true);
		request_textPane.setVisible(true);
		request_textPane.setFont(new Font("Verdana", Font.PLAIN, 12));

		request_textPane.setMargin(new Insets(1, 1, 1, 1));
		request_textPane.setBackground(Color.WHITE);
		request_textPane.setForeground(Color.BLACK);

		// Set the editor kit responsible for highlighting
		request_textPane.setEditorKit(new StyledEditorKit() {

			private static final long serialVersionUID = -6085642347022880064L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}

		});

		// Right click: Cut, Copy, Paste, Select All
		popupTargetText(request_textPane);

		// The message scroll pane where the message pane sits
		final JScrollPane requestScrollPane = new JScrollPane(request_textPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		requestPanel.add(requestScrollPane);

		// The generator panel
		final JPanel generatorPanel = new JPanel(new BorderLayout());

		generatorPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Added Payloads Table"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// The fuzzing table and model
		mFuzzingTableModel = new FuzzersAddedTableModel(getFrame());
		fuzzersTable = new JTable();
		fuzzersTable.setBackground(Color.BLACK);
		fuzzersTable.setForeground(Color.WHITE);

		fuzzersTable.setModel(mFuzzingTableModel);
		fuzzersTable.getModel().addTableModelListener(
				new PayloadsModelListener());

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

		final JScrollPane fuzzersScrollPane = new JScrollPane(fuzzersTable);
		fuzzersScrollPane.setVerticalScrollBarPolicy(20);

		generatorPanel.add(fuzzersScrollPane, BorderLayout.CENTER);
		generatorPanel.add(Box.createRigidArea(new Dimension(0, 50)),
				BorderLayout.SOUTH);

		// The on the wire panel
		JPanel onTheWirePanel = new JPanel();
		onTheWirePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Requests "), BorderFactory
						.createEmptyBorder(5, 5, 5, 5)));

		onTheWire_textArea = new JTextPane();
		onTheWire_textArea.setFont(new Font("Verdana", Font.PLAIN, 10));
		onTheWire_textArea.setEditable(false);
		onTheWire_textArea.setBackground(Color.BLACK);
		onTheWire_textArea.setForeground(Color.GREEN);

		// Right click: Cut, Copy, Paste, Select All
		popupText(onTheWire_textArea, false, true, false, true);

		onTheWireEvent = 0;

		JScrollPane consoleScrollPane = new JScrollPane(onTheWire_textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		onTheWirePanel.setLayout(new BorderLayout());
		onTheWirePanel.add(consoleScrollPane, BorderLayout.CENTER);

		// The output panel
		outputPanel = new JPanel(new BorderLayout());

		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new ResponseTableModel();

		outputTable = new JTable(outputTableModel);

		TableRowSorter<ResponseTableModel> sorter = new TableRowSorter<ResponseTableModel>(
				outputTableModel);
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
		outputTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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

							int c = outputTable.getSelectedRow();
							try {
								c = outputTable.convertRowIndexToModel(c);
							} catch (IndexOutOfBoundsException e) {
								return;
							}

							final String name = (String) outputTable.getModel()
									.getValueAt(c, 0);
							new WindowViewerFrame(FuzzingPanel.this, name);

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
		topRightPanel.add(" On The Wire (0) ", onTheWirePanel);
		topRightPanel.setTabPlacement(SwingConstants.TOP);

		mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topPane.setOneTouchExpandable(false);
		topPane.setLeftComponent(topLeftPanel);
		topPane.setRightComponent(topRightPanel);

		mainPane.setOneTouchExpandable(false);
		mainPane.setTopComponent(topPane);
		mainPane.setBottomComponent(outputPanel);

		// Set the divider locations, relative to the screen size
		final Dimension scr_res = JBroFuzzFormat.getScreenSize();
		if ((scr_res.width == 0) || (scr_res.height == 0)) {

			topPane.setDividerLocation(200);
			mainPane.setDividerLocation(150);

		} else {

			final int window_width = scr_res.width - 200;
			final int window_height = scr_res.height - 200;
			// Check that the screen is width/length is +tive
			if ((window_width > 0) && (window_height > 0)) {

				topPane.setDividerLocation(window_width * 2 / 3);
				mainPane.setDividerLocation(window_height / 2);

			} else {

				topPane.setDividerLocation(200);
				mainPane.setDividerLocation(150);

			}
		}

		// Allow for all areas to be resized to even not be seen
		Dimension minimumSize = new Dimension(0, 0);
		topLeftPanel.setMinimumSize(minimumSize);
		topRightPanel.setMinimumSize(minimumSize);
		topPane.setMinimumSize(minimumSize);
		outputPanel.setMinimumSize(minimumSize);

		this.add(mainPane, BorderLayout.CENTER);

		// Display the last displayed url/request
		this.setTextURL(prefs.get(JBroFuzzFormat.TEXT_URL, ""));
		this.setTextRequest(prefs.get(JBroFuzzFormat.TEXT_REQUEST, ""));
	}

	/**
	 * <p>
	 * Method for adding a fuzzer in the payloads table.
	 * </p>
	 * 
	 * @version 1.5
	 */
	@Override
	public void add() {

		// Check to see what text has been selected
		try {

			request_textPane.getSelectedText();

		} catch (final IllegalArgumentException e) {

			JOptionPane
					.showInputDialog(
							this,
							"An exception was thrown while attempting to get the selected text",
							"Add Fuzzer", JOptionPane.ERROR_MESSAGE);

		}

		// Find the location of where the text has been selected
		final int sPoint = request_textPane.getSelectionStart();
		final int fPoint = request_textPane.getSelectionEnd();

		new PayloadsDialog(this, sPoint, fPoint);

	}

	public void addPayload(String Id, int start, int end) {

		mFuzzingTableModel.addRow(Id, start, end);

	}

	/**
	 * <p>
	 * Clear the URL, Request and Payloads and Responses Table Fields. Also, set
	 * the focus on the URL area.
	 * </p>
	 * <p>
	 * Used when opening a file, or with a File -> New operation.
	 * </p>
	 * 
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void clearAllFields() {

		url_textField.setText("");
		request_textPane.setText("");
		onTheWire_textArea.setText("");

		topRightPanel.setTitleAt(1, " On The Wire (0) ");
		topRightPanel.setSelectedIndex(0);

		while (fuzzersTable.getRowCount() > 0) {
			mFuzzingTableModel.removeRow(0);
		}
		while (outputTable.getRowCount() > 0) {
			outputTableModel.removeRow(0);
		}

		url_textField.requestFocusInWindow();
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
	 * Get the values of the Payloads from their table, limited to a maximum of
	 * 1024 rows.
	 * </p>
	 * <p>
	 * Return the values in Comma Separated Fields.
	 * </p>
	 * 
	 * @return The values of Payloads Table as CSV Text
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public String getTextPayloads() {

		final int rows = mFuzzingTableModel.getRowCount();
		if (rows == 0)
			return "";

		StringBuffer output = new StringBuffer();
		// MAX_LINES = 1024
		for (int row = 0; row < Math.min(rows, 1024); row++) {

			for (int column = 0; column < mFuzzingTableModel.getColumnCount(); column++) {
				output.append(mFuzzingTableModel.getValueAt(row, column));
				// Append a ',' but not for the last value
				if (column != mFuzzingTableModel.getColumnCount() - 1) {
					output.append(',');
				}
			}
			// Append a new line, but not for the last line
			if (row != Math.min(rows, 1024) - 1) {
				output.append('\n');
			}
		}

		return output.toString();
	}

	/**
	 * <p>
	 * Get the value of the Request String, limited to a maximum of 16384
	 * characters.
	 * </p>
	 * 
	 * @return String
	 */
	public String getTextRequest() {

		return StringUtils.abbreviate(request_textPane.getText(), 16384);

	}

	/**
	 * <p>
	 * Get the value of the URL String, limited to a maximum of 1024 characters.
	 * </p>
	 * 
	 * @return String
	 */
	public String getTextURL() {

		return StringUtils.abbreviate(url_textField.getText(), 1024);

	}

	@Override
	public void graph() {

	}

	/**
	 * <p>
	 * Check if fuzzing is taking place.
	 * </p>
	 * 
	 * @return True if a fuzzing session is underway.
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * <p>
	 * Method for removing a generator. This method operates by removing a row
	 * from the corresponding table model of the generator table.
	 * </p>
	 */
	@Override
	public void remove() {

		if (!isAddedEnabled()) {
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
				this, "Select fuzzer to remove:", "Remove Fuzzer",
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
	 * <p>
	 * Method for setting the text displayed in the "Request" pane.
	 * </p>
	 * <p>
	 * Also resets the caret position to 0.
	 * </p>
	 * 
	 * @param input
	 *            The String of header lines plus body to be displayed.
	 * @see #setTextURL(String)
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void setTextRequest(String input) {

		request_textPane.setText(input);
		request_textPane.setCaretPosition(0);

	}

	/**
	 * <p>
	 * Method for setting the URL text field.
	 * </p>
	 * 
	 * @param input
	 * 
	 * @see #setTextRequest(String)
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void setTextURL(String input) {

		url_textField.setText(input);

	}

	/**
	 * <p>
	 * Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 */
	@Override
	public void start() {

		if (!stopped) {
			return;
		}
		stopped = false;

		// Start, Stop, Graph, Add, Remove
		setOptionsAvailable(false, true, true, false, false);
		// buttonAddGen.setEnabled(false);
		// buttonRemGen.setEnabled(false);

		url_textField.setEditable(false);
		url_textField.setBackground(Color.BLACK);
		url_textField.setForeground(Color.WHITE);

		Document doc = onTheWire_textArea.getDocument();
		Element e = doc.getDefaultRootElement();
		// Copy attribute Set
		AttributeSet attr = e.getAttributes().copyAttributes();
		try {
			doc.insertString(doc.getLength(),
					"\n--> JBroFuzz Fuzzing Session: " + (session + 1)
							+ " -->\n\n", attr);
		} catch (BadLocationException e2) {
			// TODO Auto-generated catch block
			// e2.printStackTrace();
		}

		onTheWire_textArea.setBackground(Color.BLACK);
		onTheWire_textArea.setForeground(Color.WHITE);

		topRightPanel.setTitleAt(1, " On The Wire (0) ");
		topRightPanel.setSelectedIndex(1);
		onTheWireEvent = 0;

		// Increment the session and reset the counter
		session++;
		counter = 1;
		// Update the border of the output panel
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output  " + "Logging in folder ("
						+ JBroFuzzFormat.DATE + ") Session " + session),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		final int fuzzers_added = fuzzersTable.getRowCount();

		for (int i = 0; i < Math.max(fuzzers_added, 1); i++) {

			String category;
			int start;
			int end;
			// If no fuzzers have been added, send a single plain request
			if (fuzzers_added == 0) {

				category = "ZERO-1";
				start = 0;
				end = 0;

			} else {

				category = (String) mFuzzingTableModel.getValueAt(i, 0);
				start = ((Integer) mFuzzingTableModel.getValueAt(i, 1))
						.intValue();
				end = ((Integer) mFuzzingTableModel.getValueAt(i, 2))
						.intValue();

			}

			try {

				for (Fuzzer f = getFrame().getJBroFuzz().getDatabase()
						.createFuzzer(category, Math.abs(end - start)); f
						.hasNext();) {

					if (stopped) {
						return;
					}

					String payload = f.next();
					MessageCreator currentMessage = new MessageCreator(
							getTextRequest(), payload, start, end);
					MessageWriter outputMessage = new MessageWriter(
							currentMessage, this);

					final int co_k = outputTableModel.addNewRow(outputMessage);

					// Put the message on the console as it goes out on the wire
					toConsole(currentMessage.getMessageForDisplayPurposes());

					try {

						// Connect
						Connection connection = new Connection(getTextURL(),
								currentMessage.getMessage());

						// If a 100 Continue is encountered, print what you put
						// on the wire, typically the post data from the message
						if (connection.isResponse100Continue()) {
							toConsole(currentMessage
									.getPostDataForDisplayPurposes());
						}
						// Update the message writer
						outputMessage.setConnection(connection);
						// Update the last row, indicating success
						outputTableModel.updateRow(outputMessage, co_k);

					} catch (ConnectionException e1) {

						// Update the message writer
						outputMessage.setException(e1);
						// Update the last row, indicating an error
						outputTableModel.updateRow(outputMessage, co_k, e1);

					}

					getFrame().getJBroFuzz().getHandler().writeFuzzFile(
							outputMessage);

				}

			} catch (NoSuchFuzzerException exp) {

				getFrame().log("The fuzzer could not be found...", 3);
			}

		}

	}

	/**
	 * <p>
	 * Method trigerred when attempting to stop any fuzzing taking place.
	 * </p>
	 */
	@Override
	public void stop() {

		if (stopped) {
			return;
		}
		stopped = true;
		// Start, Stop, Graph, Add, Remove
		setOptionsAvailable(true, false, true, true, true);
		int total = 0;
		total = fuzzersTable.getRowCount();
		if (total > 0) {
			// buttonRemGen.setEnabled(true);
			setOptionRemove(true);

		} else {
			// buttonRemGen.setEnabled(false);
			setOptionRemove(false);
		}

		// buttonAddGen.setEnabled(true);
		if (fuzzersTable.getRowCount() > 0) {
			// buttonRemGen.setEnabled(true);
		}

		url_textField.setEditable(true);
		url_textField.setBackground(Color.WHITE);
		url_textField.setForeground(Color.BLACK);
		onTheWire_textArea.setBackground(Color.BLACK);
		onTheWire_textArea.setForeground(Color.GREEN);

		// Get the preference for showing the "On The Wire" tab
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean showWireTab = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_3, false);

		if (showWireTab) {
			topRightPanel.setSelectedIndex(1);
		} else {
			topRightPanel.setSelectedIndex(0);
		}

	}

	/**
	 * <p>
	 * Method for writing content to the "On The Wire" console.
	 * </p>
	 * <p>
	 * Content is written as a 1000 character FILO, i.e. a maximum of 1000
	 * characters is displayed at any one time.
	 * </p>
	 * 
	 * @param input
	 *            The input string.
	 * @version 1.5
	 */
	public void toConsole(String input) {

		onTheWireEvent++;
		topRightPanel.setTitleAt(1, " On The Wire (" + onTheWireEvent + ") ");

		final Document doc = onTheWire_textArea.getDocument();
		final Element e = doc.getDefaultRootElement();
		// Copy attribute Set
		final AttributeSet attr = e.getAttributes().copyAttributes();

		try {
			// Use a FILO for the output to the console, never exceeding 1000
			// characters
			final int totLength = doc.getLength() - 80 * 24;
			if (totLength > 1) {
				doc.remove(0, totLength);
			}

			doc.insertString(doc.getLength(), input, attr);

		} catch (BadLocationException e2) {
			jbrofuzz_MainFrame
					.log(
							"Fuzzing Panel: Could not clear the \"On the Wire\" console",
							3);
		}

		onTheWire_textArea.setCaretPosition(doc.getLength());

	}
}
