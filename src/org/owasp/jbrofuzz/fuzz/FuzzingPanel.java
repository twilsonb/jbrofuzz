/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.fuzz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.core.Fuzzer;
import org.owasp.jbrofuzz.core.NoSuchFuzzerException;
import org.owasp.jbrofuzz.fuzz.ui.FuzzerTable;
import org.owasp.jbrofuzz.fuzz.ui.FuzzersTableModel;
import org.owasp.jbrofuzz.fuzz.ui.OutputTable;
import org.owasp.jbrofuzz.fuzz.ui.ResponseTableModel;
import org.owasp.jbrofuzz.fuzz.ui.RightClickPopups;
import org.owasp.jbrofuzz.payloads.PayloadsDialog;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.viewers.WindowViewerFrame;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TextHighlighter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

import com.Ostermiller.util.Browser;

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
 * @version 1.9
 * @since 0.2
 */
public class FuzzingPanel extends AbstractPanel {

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

			final int total = fuzzersTable.getRowCount();
			
			if (total > 0) {

				setOptionRemove(true);

			} else {

				setOptionRemove(false);
			}
		}
	}

	private static final long serialVersionUID = 16982374020211L;

	// The JTextField
	private final JTextField urlField;

	// The JTextArea
	private JTextPane requestPane;

	// The JTable were results are outputted
	private final OutputTable mOutputTable;

	// And the table model that goes with it
	private final ResponseTableModel outputTableModel;

	// The JTable of the generator
	private final FuzzerTable fuzzersTable;

	// And the table model that goes with it
	private final FuzzersTableModel mFuzzTableModel;

	// A counter for the number of times fuzz has been clicked
	private int counter;

	// The "On The Wire" console
	private final JTextPane onTextPane;

	// The frame window
	private final JBroFuzzWindow mWindow;

	private final JSplitPane mainPane, topPane;

	private final JTabbedPane topRightPanel;

	private boolean stopped;

	/**
	 * This constructor is used for the " Fuzzing " panel that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
	 * 
	 * @param mWindow
	 *            FrameWindow
	 */
	public FuzzingPanel(final JBroFuzzWindow mWindow) {

		super(" Fuzzing ", mWindow);

		this.mWindow = mWindow;
		counter = 1;

		stopped = true;
		// Set the enabled options: Start, Stop, Pause, Add, Remove
		setOptionsAvailable(true, false, false, true, false);

		// The URL panel
		final JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" URL "), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));

		urlField = new JTextField();
		urlField.setEditable(true);
		urlField.setVisible(true);
		urlField.setFont(new Font("Verdana", Font.BOLD, 12));
		urlField.setMargin(new Insets(1, 1, 1, 1));
		urlField.setBackground(Color.WHITE);
		urlField.setForeground(Color.BLACK);

		// Right click: Cut, Copy, Paste, Select All
		AbstractPanel.popupText(urlField, true, true, true, true);

		targetPanel.add(urlField);

		// The request panel
		final JPanel requestPanel = new JPanel(new BorderLayout());
		requestPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Request "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		// Get the preferences for wrapping lines of text
		final boolean wrapText = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[2].getId(), false);

		if (wrapText) {
			requestPane = new JTextPane();
		} else {
			requestPane = new NonWrappingTextPane();
		}

		requestPane.putClientProperty("charset", "UTF-8");
		requestPane.setEditable(true);
		requestPane.setVisible(true);
		requestPane.setFont(new Font("Verdana", Font.PLAIN, 12));

		requestPane.setMargin(new Insets(1, 1, 1, 1));
		requestPane.setBackground(Color.WHITE);
		requestPane.setForeground(Color.BLACK);

		// Set the editor kit responsible for highlighting
		requestPane.setEditorKit(new StyledEditorKit() {

			private static final long serialVersionUID = -6085642347022880064L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}

		});

		// Right click: Cut, Copy, Paste, Select All
		RightClickPopups.rightClickRequestTextComponent(this, requestPane);

		// The message scroll pane where the message pane sits
		final JScrollPane requestScrollPane = new JScrollPane(requestPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		requestPanel.add(requestScrollPane);

		// The generator panel
		final JPanel generatorPanel = new JPanel(new BorderLayout());

		generatorPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Added Fuzzers Table"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// The fuzzing table and model
		mFuzzTableModel = new FuzzersTableModel();
		fuzzersTable = new FuzzerTable(mFuzzTableModel);
		fuzzersTable.getModel().addTableModelListener(
				new PayloadsModelListener());
		// fuzzersTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
		RightClickPopups.rightClickFuzzersTable(this, fuzzersTable);
		fuzzersTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent mEvent) {
				
				final int fRow = fuzzersTable.getSelectedRow();

				final int sFuzz = ((Integer) fuzzersTable.getModel().getValueAt(fRow, 2)).intValue();
				final int eFuzz = ((Integer) fuzzersTable.getModel().getValueAt(fRow, 3)).intValue();

				requestPane.grabFocus();
				try {
					requestPane.setCaretPosition(sFuzz);
				} catch (IllegalArgumentException  vad_arg) {
					Logger.log("Could not pinpoint the position where the fuzzer is", 3);
				}
				requestPane.setSelectionStart(sFuzz);
				requestPane.setSelectionEnd(eFuzz);
				
			}
		});

		final JScrollPane fuzzersScrollPane = new JScrollPane(fuzzersTable);
		fuzzersScrollPane.setVerticalScrollBarPolicy(20);

		generatorPanel.add(fuzzersScrollPane, BorderLayout.CENTER);
		generatorPanel.add(Box.createRigidArea(new Dimension(0, 50)),
				BorderLayout.SOUTH);

		// The on the wire panel
		final JPanel onTheWirePanel = new JPanel();
		onTheWirePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Requests "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		onTextPane = new JTextPane();
		onTextPane.setFont(new Font("Verdana", Font.PLAIN, 10));
		onTextPane.setEditable(false);
		onTextPane.setBackground(Color.BLACK);
		onTextPane.setForeground(Color.GREEN);

		// Right click: Cut, Copy, Paste, Select All
		RightClickPopups.rightClickOnTheWireTextComponent(this, onTextPane);

		final JScrollPane consoleScrollPane = new JScrollPane(onTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		onTheWirePanel.setLayout(new BorderLayout());
		onTheWirePanel.add(consoleScrollPane, BorderLayout.CENTER);

		// The output panel
		final JPanel outputPanel = new JPanel(new BorderLayout());

		// Update the border of the output panel
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new ResponseTableModel();
		mOutputTable = new OutputTable(outputTableModel);
		RightClickPopups.rightClickOutputTable(this, mOutputTable);

		mOutputTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent mEvent) {
				if (mEvent.getClickCount() == 2) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {

							int cRow = mOutputTable.getSelectedRow();
							try {
								cRow = mOutputTable.convertRowIndexToModel(cRow);
							} catch (IndexOutOfBoundsException e) {
								return;
							}
							final String name = (String) mOutputTable.getModel()
							.getValueAt(cRow, 0);


							// Get the preferences for the double click
							final boolean openInBrowser = JBroFuzz.PREFS.getBoolean(
									JBroFuzzPrefs.FUZZINGOUTPUT[0].getId(), true);

							final File directory = getFrame().getJBroFuzz().getHandler().getFuzzDirectory();
							final File selFile = new File(directory, name + ".html");
							
							if(openInBrowser) {

							Browser.init();
								try {
									Browser.displayURL(selFile.toURI().toString());
								} catch (final IOException ex) {
									Logger.log(
											"Could not launch link in external browser",
											3);
								}
							} else {

								new WindowViewerFrame(FuzzingPanel.this, selFile);

							}

						}
					});
				}
			}
		});

		final JScrollPane outputScrollPane = new JScrollPane(mOutputTable);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		// outputScrollPane.setPreferredSize(new Dimension(840, 130));
		outputPanel.add(outputScrollPane);

		// Set the scroll areas
		final JPanel topLeftPanel = new JPanel(new BorderLayout());
		topLeftPanel.add(targetPanel, BorderLayout.PAGE_START);
		topLeftPanel.add(requestPanel, BorderLayout.CENTER);

		topRightPanel = new JTabbedPane(2);
		topRightPanel.add(" Payloads ", generatorPanel);
		topRightPanel.add(" On The Wire ", onTheWirePanel);
		topRightPanel.setTabPlacement(SwingConstants.TOP);

		mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topPane.setOneTouchExpandable(false);
		topPane.setLeftComponent(topLeftPanel);
		topPane.setRightComponent(topRightPanel);

		mainPane.setOneTouchExpandable(false);
		mainPane.setTopComponent(topPane);
		mainPane.setBottomComponent(outputPanel);

		// Allow for all areas to be resized to even not be seen
		topLeftPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		topRightPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		topPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		outputPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		topPane.setDividerLocation(JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[4].getId(), 440));
		mainPane.setDividerLocation(JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[5].getId(), 262));
		
		FuzzingPanel.this.add(mainPane, BorderLayout.CENTER);

		// Display the last displayed url/request
		this.setTextURL(JBroFuzz.PREFS.get(JBroFuzzPrefs.TEXT_URL, ""));
		this.setTextRequest(JBroFuzz.PREFS.get(JBroFuzzPrefs.TEXT_REQUEST, ""));
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

			requestPane.getSelectedText();

		} catch (final IllegalArgumentException e) {

			JOptionPane
			.showInputDialog(
					this,
					"An exception was thrown while attempting to get the selected text",
					"Add Fuzzer", JOptionPane.ERROR_MESSAGE);

		}

		// Find the location of where the text has been selected
		final int sPoint = requestPane.getSelectionStart();
		final int fPoint = requestPane.getSelectionEnd();

		new PayloadsDialog(this, sPoint, fPoint);

	}

	/**
	 * <p>Add a fuzzer to the table of fuzzers.</p>
	 * 
	 * @param fuzzerId
	 * @param point1
	 * @param point2
	 */
	public void addFuzzer(final String fuzzerId, final String encoding, final int point1, final int point2) {

		final Database cDatabase = getFrame().getJBroFuzz().getDatabase();

		if(cDatabase.containsPrototype(fuzzerId)) {

			final String type = cDatabase.getType(fuzzerId);

			mFuzzTableModel.addRow(
					fuzzerId,  
					encoding,
					type,  
					fuzzerId,  
					point1,
					point2
			);

		} else {
			Logger.log("Could not add the Fuzzer with ID: " + fuzzerId, 3);
		}

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

		urlField.setText("");
		requestPane.setText("");
		onTextPane.setText("");

		// topRightPanel.setTitleAt(1, " On The Wire ");
		// topRightPanel.setSelectedIndex(0);

		while (fuzzersTable.getRowCount() > 0) {
			mFuzzTableModel.removeRow(0);
		}
		while (mOutputTable.getRowCount() > 0) {
			outputTableModel.removeRow(0);
		}

		urlField.requestFocusInWindow();
	}

	

	/**
	 * <p>
	 * Clear the Responses Table. Also, set
	 * the focus on the URL area.
	 * </p>
	 * <p>
	 * Used when right clicking on the output table, or with a File -> Clear Output.
	 * </p>
	 * 
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.8
	 */
	public void clearOutputTable() {

		while(mOutputTable.getRowCount() > 0) {
			outputTableModel.removeRow(0);
		}

		urlField.requestFocusInWindow();

	}

	/**
	 * <p>
	 * Clear the Fuzzers Table. Also, set
	 * the focus on the URL area.
	 * </p>
	 * <p>
	 * Used when right clicking on the fuzzers table, or with a File -> Clear Fuzzers.
	 * </p>
	 * 
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.8
	 */
	public void clearFuzzersTable() {

		while (fuzzersTable.getRowCount() > 0) {
			mFuzzTableModel.removeRow(0);
		}

		urlField.requestFocusInWindow();

	}
	
	/**
	 * <p>Clear the "On The Wire" text area. Also, set
	 * the focus on the URL area.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.1
	 * @since 2.1
	 */
	public void clearOnTheWire() {
		
		onTextPane.setText("");
		urlField.requestFocusInWindow();

	}

	/**
	 * <p>
	 * Method for returning the counter held within the Sniffing Panel which is
	 * responsible for counting the number of requests having been made. This
	 * method is used for generating unique sequential file name and row counts.
	 * </p>
	 * 
	 * @param newCount boolean Increment the counter by 1
	 * @return String
	 */
	public String getCounter() {

		// Loop the counter after 1 billion requests
		if ((counter < 0) || (counter > 1000000000)) {
			counter = 1;
		}

		counter++;
		return StringUtils.leftPad(Integer.toString(counter), 10, '0');
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

		final int rows = mFuzzTableModel.getRowCount();
		if (rows == 0) {
			return "";
		}

		final StringBuffer output = new StringBuffer();
		// MAX_LINES = 1024
		for (int row = 0; row < Math.min(rows, 1024); row++) {

			for (int column = 0; column < mFuzzTableModel.getColumnCount(); column++) {
				output.append(mFuzzTableModel.getValueAt(row, column));
				// Append a ',' but not for the last value
				if (column != mFuzzTableModel.getColumnCount() - 1) {
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

		return StringUtils.abbreviate(requestPane.getText(), 16384);

	}

	/**
	 * <p>
	 * Get the value of the URL String, limited to a maximum of 1024 characters.
	 * </p>
	 * 
	 * @return String
	 */
	public String getTextURL() {

		return StringUtils.abbreviate(urlField.getText(), 1024);

	}

	@Override
	public void pause() {

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
			fuzzPoints[i] = mFuzzTableModel.getRow(i);
		}

		final String selectedFuzzPoint = (String) JOptionPane.showInputDialog(
				this, "Select fuzzer to remove:", "Remove Fuzzer",
				JOptionPane.INFORMATION_MESSAGE, null, fuzzPoints,
				fuzzPoints[0]);

		if (selectedFuzzPoint != null) {

			mFuzzTableModel.removeRow(Integer.parseInt(selectedFuzzPoint.split(" - ")[0]));

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
	public final void setTextRequest(final String input) {

		requestPane.setText(input);
		requestPane.setCaretPosition(0);

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
	public final void setTextURL(final String input) {

		urlField.setText(input);

	}

	/**
	 * <p>
	 * Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.0
	 */
	@Override
	public void start() {

		if (!stopped) {
			return;
		}
		stopped = false;

		// Start, Stop, Pause, Add, Remove
		setOptionsAvailable(false, true, false, false, false);

		urlField.setEditable(false);
		urlField.setBackground(Color.BLACK);
		urlField.setForeground(Color.WHITE);

		// Don't show anything if setting is such
//		if(showOnTheWire != 0) {
//			toConsole("\n--> [JBROFUZZ FUZZING START] -->\n\n");
//		}

		onTextPane.setBackground(Color.BLACK);
		onTextPane.setForeground(Color.WHITE);

		topRightPanel.setSelectedIndex(1);

		final int fuzzers_added = fuzzersTable.getRowCount();

		for (int i = 0; i < Math.max(fuzzers_added, 1); i++) {

			String category;
			String encoding;
			int start;
			int end;
			// If no fuzzers have been added, send a single plain request
			if (fuzzers_added < 1) {

				category = "999-ZER-ONE";
				encoding = FuzzerTable.ENCODINGS[0];
				start = 0;
				end = 0;

			} else {

				category = (String) mFuzzTableModel.getValueAt(i, 0);
				encoding = (String) mFuzzTableModel.getValueAt(i, 1);
				start = ((Integer) mFuzzTableModel.getValueAt(i, 2))
				.intValue();
				end = ((Integer) mFuzzTableModel.getValueAt(i, 3))
				.intValue();

			}

			try {

				for (final Fuzzer f = getFrame().getJBroFuzz().getDatabase()
						.createFuzzer(category, Math.abs(end - start)); f
						.hasNext();) {

					if (stopped) {
						return;
					}

					// Get the default value
					final int showOnTheWire = JBroFuzz.PREFS.getInt(
												JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getId(), 3);

					final String payload = f.next();
					final MessageCreator currentMessage = new MessageCreator(getTextURL(), getTextRequest(), encoding, payload, start, end);
					final MessageWriter outputMessage = new MessageWriter(this);

					final int co_k = outputTableModel.addNewRow(outputMessage);

					// Put the message on the console as it goes out on the wire
					if( (showOnTheWire == 1) || // 1 show only requests
						(showOnTheWire == 3) ) {// 3 show both requests and responses 
						// Show message
						toConsole(currentMessage.getMessageForDisplayPurposes());
					}

					try {

						// Connect
						final Connection connection = new Connection(getTextURL(),
								currentMessage.getMessage());

						// Update the message writer
						outputMessage.setConnection(connection);

						// Update the console (on the wire tab) with the output
						if( (showOnTheWire == 2) ||	// 2 for showing only responses
							(showOnTheWire == 3) ) {// 3 for showing requests and responses
							
								// toConsole("\n-->\n--> [JBROFUZZ FUZZING RESPONSE] -->\n-->\n");
								toConsole(connection.getReply());
								
						}

						// Update the last row, indicating success
						outputTableModel.updateRow(outputMessage, co_k);

					} catch (ConnectionException e1) {

						// Update the message writer
						outputMessage.setException(e1);

						// Update the console (on the wire tab) with the exception
						if( (showOnTheWire == 2) ||	// 2 for showing only responses
							(showOnTheWire == 3) ) {// 3 for showing requests and responses
							
							// toConsole("\n--> [JBROFUZZ FUZZING RESPONSE] <--\n");
							toConsole(e1.getMessage());
						}

						// Update the last row, indicating an error
						outputTableModel.updateRow(outputMessage, co_k, e1);

					}
					
//					if(showOnTheWire != 0) {
//						toConsole("\n--> [JBROFUZZ FUZZING STOP] -->\n\n");
//					}
					
					getFrame().getJBroFuzz().getHandler().writeFuzzFile(
							outputMessage);

				}

			} catch (NoSuchFuzzerException exp) {

				Logger.log("The fuzzer could not be found...", 3);
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

		JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[5].getId(), mainPane.getDividerLocation());
		JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[4].getId(), topPane.getDividerLocation());

		if (stopped) {
			return;
		}
		stopped = true;
		// Start, Stop, Pause, Add, Remove
		setOptionsAvailable(true, false, false, true, true);
		final int total = fuzzersTable.getRowCount();
		
		if (total > 0) {
			setOptionRemove(true);

		} else {
			setOptionRemove(false);
		}


		urlField.setEditable(true);
		urlField.setBackground(Color.WHITE);
		urlField.setForeground(Color.BLACK);
		onTextPane.setBackground(Color.BLACK);
		onTextPane.setForeground(Color.GREEN);

		// Get the preference for showing the "On The Wire" tab
		final boolean showWireTab = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZINGONTHEWIRE[0].getId(), true);

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
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.5
	 */
	private void toConsole(final String input) {

		final Document eDoc = onTextPane.getDocument();
		final Element eElement = eDoc.getDefaultRootElement();
		// Copy attribute Set
		final AttributeSet attr = eElement.getAttributes().copyAttributes();

		try {
			// Use a FILO for the output to the console
			final int totLength = eDoc.getLength() - Integer.MAX_VALUE;
			if (totLength > 1) {
				eDoc.remove(0, totLength);
			}

			eDoc.insertString(eDoc.getLength(), input, attr);

		} catch (BadLocationException e2) {
			Logger.log("Fuzzing Panel: Could not clear the \"On the Wire\" console", 3);
		}

		onTextPane.setCaretPosition(eDoc.getLength());

	}
}
