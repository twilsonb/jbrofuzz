/**
 * JBroFuzz 2.5
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
package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.core.Fuzzer;
import org.owasp.jbrofuzz.core.NoSuchFuzzerException;
import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.fuzz.Connection;
import org.owasp.jbrofuzz.fuzz.ConnectionException;
import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.fuzz.MessageCreator;
import org.owasp.jbrofuzz.payloads.PayloadsDialog;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TextHighlighter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

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
 * Additionally the user can select a list of encoders, applied recursively from
 * top down to use to encode/hash the fuzzer, append a prefix/suffix or match
 * and replace a string value within the fuzzer.
 * </p>
 * 
 * <p>
 * Finally, all output (apart from being saved to file) is presented in the
 * second tab inside the output table.
 * </p>
 */
public class FuzzingPanel extends AbstractPanel {

	private static final long serialVersionUID = 6520864430220861584L;

	private final JTextField urlField;
	private JTextPane requestPane;
	private int counter;
	private final WireTextArea mWireTextArea;
	private final FuzzSplitPane mainPane, bottomPane;
	private final JTabbedPane fuzzerWindowPane;
	private boolean stopped;
	private String payload, encodedPayload;
	private JPanel topPanel;
	private TransformsPanel transformsPanel;
	private FuzzersPanel fuzzersPanel;
	private OutputPanel outputPanel;

	/**
	 * <p>
	 * This constructor is used for the " Fuzzing " panel that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
	 * </p>
	 * 
	 * 
	 * @param mWindow
	 *            FrameWindow
	 */
	public FuzzingPanel(final JBroFuzzWindow mWindow) {

		super(" Fuzzing ", mWindow);

		counter = 0;
		payload = "";
		stopped = true;

		// Set the enabled options: Start, Stop, Pause, Add, Remove
		setOptionsAvailable(true, false, false, true, false);

		// The Target panel
		final JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(" Target "),BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		urlField = new JTextField();
		urlField.setEditable(true);
		urlField.setVisible(true);
		urlField.setFont(new Font("Verdana", Font.BOLD, 12));
		urlField.setToolTipText("[{Protocol} :// {Host} [:{Port}]]");
		urlField.setMargin(new Insets(1, 1, 1, 1));
		urlField.setBackground(Color.WHITE);
		urlField.setForeground(Color.BLACK);

		requestPane = createEditablePane();

		// Right click: Cut, Copy, Paste, Select All
		AbstractPanel.popupText(urlField, true, true, true, true);

		targetPanel.add(urlField);

		// The fuzzers panel
		fuzzersPanel = new FuzzersPanel(this);

		// The on the wire panel
		final JPanel onTheWirePanel = new JPanel();
		onTheWirePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(" Requests "),BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		mWireTextArea = new WireTextArea();

		// Right click: Cut, Copy, Paste, Select All
		RightClickPopups.rightClickOnTheWireTextComponent(this, mWireTextArea);

		final JScrollPane consoleScrollPane = new JScrollPane(mWireTextArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		onTheWirePanel.setLayout(new BorderLayout());
		onTheWirePanel.add(consoleScrollPane, BorderLayout.CENTER);

		// Set the scroll areas
		topPanel = new JPanel(new BorderLayout());
		topPanel.add(targetPanel, BorderLayout.PAGE_START);
		topPanel.add(createScrollingPanel(" Request ", requestPane),BorderLayout.CENTER);

		// create the outlining tabbed pane
		fuzzerWindowPane = new JTabbedPane();

		bottomPane = new FuzzSplitPane(FuzzSplitPane.HORIZONTAL_SPLIT,FuzzSplitPane.FUZZ_BOTTOM);

		bottomPane.setLeftComponent(fuzzersPanel);
		transformsPanel = new TransformsPanel(this);
		bottomPane.setRightComponent(transformsPanel);

		mainPane = new FuzzSplitPane(FuzzSplitPane.VERTICAL_SPLIT,
				FuzzSplitPane.FUZZ_MAIN);
		mainPane.setOneTouchExpandable(false);
		mainPane.setTopComponent(topPanel);
		mainPane.setBottomComponent(bottomPane);

		// Allow for all areas to be resized to even not be seen
		topPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		bottomPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		// Create the outputPanel
		outputPanel = new OutputPanel(this);

		// add the respective panes/panels to the tabbed pane
		fuzzerWindowPane.addTab("Input", mainPane);
		fuzzerWindowPane.addTab("Output", outputPanel);
		fuzzerWindowPane.addTab("On the wire", onTheWirePanel);
		add(fuzzerWindowPane, BorderLayout.CENTER);

		// Display the last displayed url/request
		setTextURL(JBroFuzz.PREFS.get(JBroFuzzPrefs.TEXT_URL, ""));
		setTextRequest(JBroFuzz.PREFS.get(JBroFuzzPrefs.TEXT_REQUEST, ""));
	}

	public static JPanel createScrollingPanel(String title, JTextPane textPane) {
		// The request panel
		final JPanel panel = new JPanel(new BorderLayout());
		// The message scroll pane where the message pane sits
		final JScrollPane scrollPane = new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		return panel;
	}

	private JTextPane createEditablePane() {

		JTextPane textPane = new JTextPane();

		// Get the preferences for wrapping lines of text
		final boolean wrapText = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.FUZZING[2].getId(), false);

		if (wrapText) {
			textPane = new JTextPane();
		} else {
			textPane = new NonWrappingTextPane();
		}

		textPane.putClientProperty("charset", "UTF-8");
		textPane.setEditable(true);
		textPane.setVisible(true);
		textPane.setFont(new Font("Verdana", Font.PLAIN, 12));
		textPane.setMargin(new Insets(1, 1, 1, 1));
		textPane.setBackground(Color.WHITE);
		textPane.setForeground(Color.BLACK);

		// Set the editor kit responsible for highlighting
		textPane.setEditorKit(new StyledEditorKit() {

			private static final long serialVersionUID = -6085642347022880064L;

			public Document createDefaultDocument() {
				return new TextHighlighter();
			}

		});

		// Right click: Cut, Copy, Paste, Select All
		RightClickPopups.rightClickRequestTextComponent(this, textPane);

		return textPane;
	}

	JTextPane createSimplePane() {
		JTextPane textPane = new JTextPane();
		textPane.setMargin(new Insets(1, 1, 1, 1));
		textPane.setBackground(Color.WHITE);
		textPane.setForeground(Color.BLACK);
		return textPane;

	}

	/**
	 * <p>
	 * Method for adding a fuzzer in the payloads table.
	 * </p>
	 */
	@Override
	public void add() {

		// Check to see what text has been selected
		try {
			requestPane.getSelectedText();
		} catch (final IllegalArgumentException e) {

			JOptionPane.showInputDialog(this,"An exception was thrown while attempting to get the selected text","Add Fuzzer", JOptionPane.ERROR_MESSAGE);
		}
				// Find the location of where the text has been selected
		final int sPoint = requestPane.getSelectionStart();
		final int fPoint = requestPane.getSelectionEnd();

		new PayloadsDialog(this, sPoint, fPoint);

	}

	/**
	 * <p>
	 * Clear the URL, Request and Payloads and Responses Table Fields. Also, set
	 * the focus on the URL area.
	 * </p>
	 * <p>
	 * Used when opening a file, or with a File -> New operation.
	 * </p>
	 */
	public void clearAllFields() {

		urlField.setText("");
		requestPane.setText("");
		mWireTextArea.setText("");

		transformsPanel.clear();
		fuzzersPanel.clear();

		outputPanel.getOutputTableModel().clearAllRows();
		urlField.requestFocusInWindow();
	}

	/**
	 * <p>
	 * Clear the Responses Table. Also, set the focus on the URL area.
	 * </p>
	 * <p>
	 * Used when right clicking on the output table, or with a File -> Clear
	 * Output.
	 * </p>
	 */
	public void clearOutputTable() {
		outputPanel.clear();
	}

	/**
	 * <p>
	 * Clear the "On The Wire" text area. Also, set the focus on the URL area.
	 * </p>
	 */
	public void clearOnTheWire() {
		mWireTextArea.setText("");
		urlField.requestFocusInWindow();
	}

	public void pause() {

	}

	/**
	 * <p>
	 * Check if fuzzing is taking place.
	 * </p>
	 * 
	 * @return True if a fuzzing session is underway.
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

	}



	/**
	 * <p>
	 * Method for setting the URL text field.
	 * </p>
	 * 
	 * @param input
	 * @see #setTextRequest(String)
	 */
	public final void setTextURL(final String input) {

		urlField.setText(input);

	}

	/**
	 * <p>
	 * Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 * <p>
	 * If no encoder or fuzzer selected, use a default value (Plain Text
	 * encoder).
	 * </p>
	 */
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

		final int fuzzers_added = fuzzersPanel.getRowCount();

		for (int i = 0; i < Math.max(fuzzers_added, 1); i++) {

			String category;
//			TransformsRow[] transforms;
			TransformsTableModel transforms;
			
			int start, end;
			// If no fuzzers have been added, send a single plain request
			if (fuzzers_added < 1) {
				category = "000-ZER-ONE";
				transforms = new TransformsTableModel();
				start = end = 0;
			} else {
				category = fuzzersPanel.getCategory(i);
				transforms = transformsPanel.getTransforms(i);
				start = fuzzersPanel.getStart(i);
				end = fuzzersPanel.getEnd(i);
			}

			try {
				for (final Fuzzer f = getFrame().getJBroFuzz().getDatabase().createFuzzer(category, Math.abs(end - start)); f.hasNext();) {

					if (stopped)
						return;

					// Get the default value
					final int showOnTheWire = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getId(), 3);

					// Set the payload, has to be called before the
					// MessageWriter constructor
					payload = f.next();

					// Perform the necessary encoding on the payload specified
//					if(transformsPanel.getRowCount() ) {
//						// encodedPayload = payload;
//					} else {
//						// encodedPayload = EncoderHashCore.encodeMany(payload,transforms);
//					}
					encodedPayload = EncoderHashCore.encodeMany(payload, transforms);
					
					final MessageCreator currentMessage = new MessageCreator(getTextURL(), getTextRequest(), encodedPayload,start, end);
					final MessageContainer outputMessage = new MessageContainer(this);
					outputMessage.setTextRequest(currentMessage.getMessageForDisplayPurposes());
					
					// Put the message on the console as it goes out on the wire
					if ((showOnTheWire == 1) || // 1 show only requests
							(showOnTheWire == 3)) {// 3 show both requests and
													// responses
						// Show message
						mWireTextArea.setText(currentMessage.getMessageForDisplayPurposes());
					}

					try {

						// Connect
						final Connection connection = new Connection(getTextURL(), currentMessage.getMessage());

						// Update the message writer
						outputMessage.setConnection(connection);

						// Update the console (on the wire tab) with the output
						if ((showOnTheWire == 2) || // 2 for showing only
													// responses
								(showOnTheWire == 3)) {// 3 for showing requests
														// and responses

							// toConsole("\n-->\n--> [JBROFUZZ FUZZING RESPONSE] -->\n-->\n");
							mWireTextArea.setText(connection.getReply());

						}

						// Update the last row, indicating success
						outputPanel.getOutputTableModel().addNewRow(outputMessage);

					} catch (final ConnectionException e1) {
						// Update the message writer
						outputMessage.setException(e1);

						// Update the console (on the wire tab) with the exception
						if ((showOnTheWire == 2) || // 2 for showing only
													// responses
								(showOnTheWire == 3)) {// 3 for showing requests
														// and responses

							// toConsole("\n--> [JBROFUZZ FUZZING RESPONSE] <--\n");
							mWireTextArea.setText("A connection exception occurred.");
						}

						// Update the last row, indicating an error
						outputPanel.getOutputTableModel().addNewRow(outputMessage);
					}
					this.getFrame().getJBroFuzz().getStorageHandler().writeFuzzFile(outputMessage);
				}

			} catch (final NoSuchFuzzerException exp) {
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
		if (stopped) {
			return;
		}
		stopped = true;
		// Start, Stop, Pause, Add, Remove
		setOptionsAvailable(true, false, false, true, true);
		final int total = fuzzersPanel.getRowCount();

		if (total > 0) {
			setOptionRemove(true);

		} else {
			setOptionRemove(false);
		}

		urlField.setEditable(true);
		urlField.setBackground(Color.WHITE);
		urlField.setForeground(Color.BLACK);
	}

	public String getPayload() {
		return payload;
	}

	public String getEncodedPayload() {
		return encodedPayload;
	}

	public TransformsPanel getTransformsPanel() {
		return transformsPanel;
	}

	public TransformsToolBar getControlPanel() {
		return transformsPanel.getTransformsToolBar();
	}

	
	public OutputPanel getOutputPanel(){
		return outputPanel;
	}
	
	public void setOutputPanel(OutputPanel op){
		this.outputPanel = op;
	}
/*
	public OutputTable getOutputTable() {
		return outputPanel.getOutputTable();
	}
*/	
	public JComponent getUrlField() {
		return urlField;
	}

	public FuzzersPanel getFuzzersPanel() {
		return fuzzersPanel;
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
	
	/**
	 * <p>
	 * Method for setting the text displayed in the "Request" pane.
	 * </p>
	 * <p>
	 * Also resets the caret position to 0.
	 * </p>
	 * 
	 * @param input
	 *            The String of header lines plus body to be displayed
	 */
	public final void setTextRequest(final String input) {
		requestPane.setText(input);
		requestPane.setCaretPosition(0);
	}
}