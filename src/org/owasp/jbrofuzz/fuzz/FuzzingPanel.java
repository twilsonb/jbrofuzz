/**
 * JBroFuzz 2.4
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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.StyledEditorKit;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.core.Fuzzer;
import org.owasp.jbrofuzz.core.NoSuchFuzzerException;
import org.owasp.jbrofuzz.db.DTOCreator;
import org.owasp.jbrofuzz.db.SQLiteHandler;
import org.owasp.jbrofuzz.db.dto.SessionDTO;
import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.fuzz.io.FuzzFileUtils;
import org.owasp.jbrofuzz.fuzz.ui.EncodersRow;
import org.owasp.jbrofuzz.fuzz.ui.EncodersTable;
import org.owasp.jbrofuzz.fuzz.ui.EncodersTableList;
import org.owasp.jbrofuzz.fuzz.ui.EncodersTableModel;
import org.owasp.jbrofuzz.fuzz.ui.FuzzSplitPane;
import org.owasp.jbrofuzz.fuzz.ui.FuzzerTable;
import org.owasp.jbrofuzz.fuzz.ui.FuzzersTableModel;
import org.owasp.jbrofuzz.fuzz.ui.OutputTable;
import org.owasp.jbrofuzz.fuzz.ui.OutputTableModel;
import org.owasp.jbrofuzz.fuzz.ui.RightClickPopups;
import org.owasp.jbrofuzz.fuzz.ui.WireTextArea;
import org.owasp.jbrofuzz.io.FileHandler;
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
 * top down to use to encode/hash the fuzzer, append a prefix/suffix or match and
 * replace a string value within the fuzzer.
 * </p>
 * 
 * <p>
 * Finally, all output (apart from being saved to file) is presented in the
 * second tab inside the output table.
 * </p>
 * 
 * @author subere@uncon.org
 * @author ranulf
 * @version 3.2
 * @since 0.2
 */
/**
 * @author ragreen
 *
 */
/**
 * @author ragreen
 *
 */
public class FuzzingPanel extends AbstractPanel {

	private static final long serialVersionUID = 6520864430220861584L;

	// The JTextField
	private final JTextField urlField;

	// The JTextArea
	private JTextPane requestPane;

	// The JTable were results are outputted
	private OutputTable mOutputTable;

	// The JTableRowSorter
	private TableRowSorter<OutputTableModel> outputSorter;

	// And the table model that goes with it
	private OutputTableModel outputTableModel;

	// The JTable of the generator
	private final FuzzerTable fuzzersTable;

	// And the table model that goes with it
	private final FuzzersTableModel mFuzzTableModel;

	// A counter for the number of times fuzz has been clicked
	private int counter;

	// The "On The Wire" console
	private final WireTextArea mWireTextArea;

	private final FuzzSplitPane mainPane, bottomPane;

	// the new main tabbed pane to contain the 3 tabs: input, output and on the wire
	private final JTabbedPane fuzzerWindowPane;

	private boolean stopped;

	// the selected payload 
	private String payload;

	// the top panel, contains the request text area
	private JPanel topPanel;

	// the encoder panel Bottom RHS
	private JPanel encoderPanel;

	// the list of encoders tables which are related to the added fuzzers
	private EncodersTableList encodersTableList;

	// the encoders toolbar which Bottom RHS of the screen
	private EncodersToolBar controlPanel;

	private String encodedPayload;

	private JTextPane outputRequestPane;

	private JTextPane outputResponsePane;

	private int lastIndex;

	private Color HILIT_COLOR  = Color.LIGHT_GRAY;

	private JCheckBox csxReq;

	private JCheckBox csxRes;


	/* This constructor is used for the " Fuzzing " panel that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
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
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Target "), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));


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



		// The generator panel
		final JPanel generatorPanel = new JPanel(new BorderLayout());

		generatorPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Added Fuzzers Table"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));


		encoderPanel = new JPanel(new BorderLayout());

		encoderPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Added Fuzzer Transforms (rules applied top first) "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// The fuzzing table and model
		mFuzzTableModel = new FuzzersTableModel();
		fuzzersTable = new FuzzerTable(mFuzzTableModel);

		RightClickPopups.rightClickFuzzersTable(this, fuzzersTable);

		fuzzersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				final int fRow = fuzzersTable.getSelectedRow();

				final int sFuzz = ((Integer) fuzzersTable.getModel().getValueAt(fRow, 1)).intValue();
				final int eFuzz = ((Integer) fuzzersTable.getModel().getValueAt(fRow, 2)).intValue();

				requestPane.grabFocus();
				try {
					requestPane.setCaretPosition(sFuzz);
				} catch (final IllegalArgumentException  vad_arg) {
					Logger.log("Could not pinpoint the position where the fuzzer is", 3);
				}
				requestPane.setSelectionStart(sFuzz);
				requestPane.setSelectionEnd(eFuzz);

			}
		});

		final JScrollPane fuzzersScrollPane = new JScrollPane(fuzzersTable);
		fuzzersScrollPane.setVerticalScrollBarPolicy(20);

		generatorPanel.add(fuzzersScrollPane, BorderLayout.CENTER);
		//generatorPanel.add(Box.createRigidArea(new Dimension(0, 50)),	BorderLayout.SOUTH);

		// The on the wire panel
		final JPanel onTheWirePanel = new JPanel();
		onTheWirePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Requests "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		mWireTextArea = new WireTextArea();

		// Right click: Cut, Copy, Paste, Select All
		RightClickPopups.rightClickOnTheWireTextComponent(this, mWireTextArea);

		final JScrollPane consoleScrollPane = new JScrollPane(mWireTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		onTheWirePanel.setLayout(new BorderLayout());
		onTheWirePanel.add(consoleScrollPane, BorderLayout.CENTER);








		// Set the scroll areas
		topPanel = new JPanel(new BorderLayout());
		topPanel.add(targetPanel, BorderLayout.PAGE_START);
		topPanel.add(createScrollingPanel(" Request ", requestPane), BorderLayout.CENTER);



		// create the outlining tabbed pane
		fuzzerWindowPane = new JTabbedPane();



		bottomPane = new FuzzSplitPane(FuzzSplitPane.HORIZONTAL_SPLIT,FuzzSplitPane.FUZZ_BOTTOM);


		bottomPane.setLeftComponent(generatorPanel);
		bottomPane.setRightComponent(encoderPanel);


		createEncodingPane();

		mainPane = new FuzzSplitPane(FuzzSplitPane.VERTICAL_SPLIT,FuzzSplitPane.FUZZ_MAIN);

		mainPane.setOneTouchExpandable(false);
		mainPane.setTopComponent(topPanel);
		mainPane.setBottomComponent(bottomPane);


		// Allow for all areas to be resized to even not be seen
		topPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		bottomPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);


		//mainPane.setDividerLocation(JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[5].getId(), 262));


		// add the respective panes/panels to the tabbed pane
		fuzzerWindowPane.addTab("Input",mainPane);
		fuzzerWindowPane.addTab("Output", createOutputPanel());
		fuzzerWindowPane.addTab("On the wire", onTheWirePanel);

		FuzzingPanel.this.add(fuzzerWindowPane, BorderLayout.CENTER);

		// Display the last displayed url/request
		this.setTextURL(JBroFuzz.PREFS.get(JBroFuzzPrefs.TEXT_URL, ""));
		this.setTextRequest(JBroFuzz.PREFS.get(JBroFuzzPrefs.TEXT_REQUEST, ""));

		fuzzersTableSelectionListen();

	}

	private JPanel createScrollingPanel(String title, JTextPane textPane){

		// The request panel
		final JPanel panel = new JPanel(new BorderLayout());
		// The message scroll pane where the message pane sits
		final JScrollPane scrollPane = new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(title), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		return panel;

	}


	private JTextPane createEditablePane(){


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

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}

		});

		// Right click: Cut, Copy, Paste, Select All
		RightClickPopups.rightClickRequestTextComponent(this, textPane);

		return textPane;
	}

	private JTextPane createSimplePane(){
		JTextPane textPane = new JTextPane();
		textPane.setMargin(new Insets(1, 1, 1, 1));
		textPane.setBackground(Color.WHITE);
		textPane.setForeground(Color.BLACK);
		return textPane;

	}
	
	private void outputSearch(JTextPane toSearch, JTextField entry, boolean ignoreCase) {
		Highlighter hilit = new DefaultHighlighter();
		toSearch.setHighlighter(hilit);
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);

		
		final String s = entry.getText();
		

		try {
			final String content = toSearch.getDocument().getText(0, toSearch.getDocument().getLength());
			
			int index = -1;
			if(ignoreCase){
				index = content.toLowerCase().indexOf(s.toLowerCase(), 0);
			}else{
				index = content.indexOf(s, 0);
			}
		
			
			if (lastIndex != 0 && lastIndex >= index){
				
				if(ignoreCase){
					final int tempIndex = content.toLowerCase().indexOf(s.toLowerCase(), lastIndex +1);
					index = tempIndex;
				}else{
					final int tempIndex = content.indexOf(s, lastIndex +1);
					index = tempIndex;
				}

			}
			
			if (index >= 0) {   // match found
				final int end = index + s.length();
				hilit.addHighlight(index, end, painter);
				toSearch.setCaretPosition(index);
				lastIndex = index;
			} else if (lastIndex > 0){
				lastIndex = 0;
			}
			else {
			}

		} catch (final BadLocationException e) {
			e.printStackTrace();
		}

	}


	private JPanel createOutputPanel(){
		// the output split pane model

		final FuzzSplitPane outputMainPane = new FuzzSplitPane(FuzzSplitPane.VERTICAL_SPLIT,FuzzSplitPane.OUTPUT_MAIN);
		final FuzzSplitPane outputBottomPane = new FuzzSplitPane(FuzzSplitPane.HORIZONTAL_SPLIT,FuzzSplitPane.OUTPUT_BOTTOM);


		// The output panel
		final JPanel outputTablePanel = new JPanel(new BorderLayout());

		// Update the border of the output panel
		outputTablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new OutputTableModel();
		mOutputTable = new OutputTable(outputTableModel);
		outputSorter = new TableRowSorter<OutputTableModel>(outputTableModel);
		mOutputTable.setRowSorter(outputSorter);
		final JScrollPane outputScrollPane = new JScrollPane(mOutputTable);
		// mOutputTable.setFillsViewportHeight(true);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		// outputScrollPane.setPreferredSize(new Dimension(840, 130));
		outputTablePanel.add(outputScrollPane);
		outputTablePanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		RightClickPopups.rightClickOutputTable(this, mOutputTable);



		outputRequestPane = createSimplePane();
		outputRequestPane.setEditable(false);
		outputResponsePane = createSimplePane();
		outputResponsePane.setEditable(false);

		JPanel bL = new JPanel(new BorderLayout());
		JPanel bR = new JPanel(new BorderLayout());
		
		bL.add(createScrollingPanel(" Request ", outputRequestPane),BorderLayout.CENTER);
		bR.add(createScrollingPanel(" Response ", outputResponsePane),BorderLayout.CENTER);
		
		final JTextField reqSearch = new JTextField();
		final JTextField resSearch = new JTextField();
	
		csxReq = new JCheckBox("Ignore Case");
		csxRes = new JCheckBox("Ignore Case");
		csxReq.setSelected(true);
		csxRes.setSelected(true);
	
		
		
		resSearch.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 10){
					outputSearch(outputResponsePane, resSearch, csxRes.isSelected());
				}
			}
		});
		reqSearch.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 10){
					outputSearch(outputRequestPane, reqSearch, csxReq.isSelected());
				}
			}
		});
		
		// configure the ignore case check boxes
		JPanel pbL = new JPanel(new BorderLayout());
		pbL.add(csxReq, BorderLayout.WEST);
		pbL.add(reqSearch,BorderLayout.CENTER);
		
		JPanel pbR = new JPanel(new BorderLayout());
		pbR.add(csxRes, BorderLayout.WEST);
		pbR.add(resSearch, BorderLayout.CENTER);
		
		bL.add(pbL,BorderLayout.SOUTH);
		bR.add(pbR,BorderLayout.SOUTH);

		outputBottomPane.setLeftComponent(bL);
		outputBottomPane.setRightComponent(bR);
		outputBottomPane.getLeftComponent().setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		outputBottomPane.getRightComponent().setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		outputBottomPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		outputMainPane.setTopComponent(outputTablePanel);
		outputMainPane.setBottomComponent(outputBottomPane);



		// list selection listener
		mOutputTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {

				int cRow = mOutputTable.getSelectedRow();
				try {
					cRow = mOutputTable.convertRowIndexToModel(cRow);
				} catch (final IndexOutOfBoundsException iob) {
					return;
				}
				final String name = (String) mOutputTable.getModel()
				.getValueAt(cRow, 0);

				class FileLoader extends SwingWorker<String, Object> { // NO_UCD

					String fuzzerLineOutput = new String();
					public String doInBackground() {

						final File directory = getFrame().getJBroFuzz().getHandler().getFuzzDirectory();
						final File selFile = new File(directory, name + ".html");
						fuzzerLineOutput = FileHandler.readFile(selFile);
						return "done";
					}

					protected void done() {
						final String dbType = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "-1");
						String request = new String();
						String response = new String();
						if(dbType.equals("None")){
							response = FuzzFileUtils.getResponse(fuzzerLineOutput);
							request = FuzzFileUtils.getRequest(fuzzerLineOutput);
						}else if(dbType.equals("SQLite")){
							// TODO: validation checks on the database
							Logger.log("SQLITE database not implement - use none", 3);
						}else{
							// TODO: validation checks on the couch DB implementation
							Logger.log("COUCHDB database not implement - use none", 3);
						}
					
					
						outputResponsePane.setText(response);
						outputRequestPane.setText(request);
						outputResponsePane.setCaretPosition(0);
						outputResponsePane.setCaretPosition(0);
						outputResponsePane.repaint();
						outputRequestPane.repaint();
					}
				}
				(new FileLoader()).execute();
			}
		});
		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.add(outputMainPane);
		return outputPanel;
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
	public void addFuzzer(final String fuzzerId, final int point1, final int point2) {

		final Database cDatabase = getFrame().getJBroFuzz().getDatabase();

		if(cDatabase.containsPrototype(fuzzerId)) {

			final String type = cDatabase.getType(fuzzerId);

			mFuzzTableModel.addRow(
					fuzzerId,  
					type,  
					fuzzerId,  
					point1,
					point2
			);

			encodersTableList.add();

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
		mWireTextArea.setText("");

		// topRightPanel.setTitleAt(1, " On The Wire ");
		// topRightPanel.setSelectedIndex(0);

		while (fuzzersTable.getRowCount() > 0) {
			mFuzzTableModel.removeRow(0);
			encodersTableList.remove(0);
		}

		outputTableModel.clearAllRows();
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
	 * @version 2.2
	 * @since 1.8
	 */
	public void clearOutputTable() {

		outputTableModel.clearAllRows();
		urlField.requestFocusInWindow();
		outputRequestPane.setText("");
		outputResponsePane.setText("");
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
			encodersTableList.remove(0);
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

		mWireTextArea.setText("");
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
			encodersTableList.remove(Integer.parseInt(selectedFuzzPoint.split(" - ")[0]));

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
	 * <p>
	 * If no encoder or fuzzer selected, use a default value (Plain Text encoder).
	 * </p>
	 * 
	 * @author daemonmidi@gmail.com
	 * @version 2.4
	 * @since 2.3
	 * 
	 * @author subere@uncon.org
	 * @author ranulf
	 * @version 2.3
	 * @since 1.0
	 */
	@Override
	public void start() {
		if (!stopped) {
			return;
		}
		stopped = false;
			
		SQLiteHandler sqlH = new SQLiteHandler();
		DTOCreator dtoC = new DTOCreator();

		// Start, Stop, Pause, Add, Remove
		setOptionsAvailable(false, true, false, false, false);

		urlField.setEditable(false);
		urlField.setBackground(Color.BLACK);
		urlField.setForeground(Color.WHITE);

		final int fuzzers_added = fuzzersTable.getRowCount();

		for (int i = 0; i < Math.max(fuzzers_added, 1); i++) {

			String category;
			EncodersRow[] encoders;
			int start;
			int end;
			// If no fuzzers have been added, send a single plain request
			if (fuzzers_added < 1) {
				category = "000-ZER-ONE";
				EncodersRow row = new EncodersRow("Plain Text", "", "");
				encoders = new EncodersRow[]{row};
				start = 0;
				end = 0;

			}else {
				category = (String) mFuzzTableModel.getValueAt(i, 0);
				encoders = getEncoders(i);
				start = ((Integer) mFuzzTableModel.getValueAt(i, 1)).intValue();
				end = ((Integer) mFuzzTableModel.getValueAt(i, 2)).intValue();

			}

			try {

				for (final Fuzzer f = getFrame().getJBroFuzz().getDatabase()
						.createFuzzer(category, Math.abs(end - start)); f
						.hasNext();) {

					if (stopped) {
						return;
					}

					// Get the default value
					final int showOnTheWire = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getId(), 3);
					// Set the payload, has to be called 
					// before the MessageWriter constructor
					payload = f.next();
					// Perform the necessary encoding on the payload specified
					encodedPayload = EncoderHashCore.encodeMany(payload, encoders);
					final MessageCreator currentMessage = new MessageCreator(getTextURL(), getTextRequest(), encodedPayload, start, end);
					final MessageWriter outputMessage = new MessageWriter(this);

					// final int co_k = outputTableModel.addNewRow(outputMessage);

					// Put the message on the console as it goes out on the wire
					if( (showOnTheWire == 1) || // 1 show only requests
							(showOnTheWire == 3) ) {// 3 show both requests and responses 
						// Show message
						mWireTextArea.setText(currentMessage.getMessageForDisplayPurposes());
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
							mWireTextArea.setText(connection.getReply());

						}

						// Update the last row, indicating success
						// outputTableModel.updateRow(outputMessage, co_k);
						outputTableModel.addNewRow(outputMessage);
						// outputSorter.sort();
						// mOutputTable.updateUI();

					} catch (final ConnectionException e1) {
						// Update the message writer
						outputMessage.setException(e1);

						// Update the console (on the wire tab) with the exception
						if( (showOnTheWire == 2) ||	// 2 for showing only responses
								(showOnTheWire == 3) ) {// 3 for showing requests and responses

							// toConsole("\n--> [JBROFUZZ FUZZING RESPONSE] <--\n");
							mWireTextArea.setText("A connection exception occurred." );
						}

						// Update the last row, indicating an error
						// outputTableModel.updateRow(outputMessage, co_k, e1);
						outputTableModel.addNewRow(outputMessage);
						// outputSorter.sort();
						// mOutputTable.updateUI();

					}

					//					if(showOnTheWire != 0) {
					//						toConsole("\n--> [JBROFUZZ FUZZING STOP] -->\n\n");
					//					}
					// TODO: update to handle database
					final String dbType = JBroFuzz.PREFS.get(JBroFuzzPrefs.DBSETTINGS[11].getId(), "-1");
					if(dbType.equals("None") || dbType.equals("CouchDB")){
						getFrame().getJBroFuzz().getHandler().writeFuzzFile(outputMessage);	
					}else if(dbType.equals("SQLite")){
						
						// TODO: validation checks on the database
						Logger.log("SQLITE database not implement - use none", 3);
						
						Date dat = new Date();
						String dbName = sqlH.setUpDB();

						java.sql.Connection con = sqlH.getConnection(dbName);
						SessionDTO session = dtoC.createSessionDTO(this.getFrame(), Long.valueOf(dbName), con);
						sqlH.store(session, con);
						
					}
					//TODO: CouchDB will be activated via saveSession
				}

			} catch (final NoSuchFuzzerException exp) {
				Logger.log("The fuzzer could not be found...", 3);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		final int total = fuzzersTable.getRowCount();

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

	public EncodersRow[] getEncoders(int fuzzerRow){

		EncodersTableModel a = encodersTableList.getEncoderTableModel(fuzzerRow);

		if(a==null){
			EncodersRow row = new EncodersRow("Plain Text","","");
			return new EncodersRow[]{row};
		}
		return a.getEncoders();
	}

	/**
	 * <b>updateEncoderPanel</b>
	 * <p>
	 * A method to show the encoders table linked to the fuzzer which has been selected
	 * </p>
	 * 
	 * @param in
	 * @author ranulf
	 */
	public void updateEncoderPanel(EncodersTable in){
		encoderPanel.removeAll();
		JScrollPane scroll = new JScrollPane(in,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(20);
		encoderPanel.add(scroll,BorderLayout.CENTER);		
		encoderPanel.add(controlPanel, BorderLayout.EAST);
		encoderPanel.updateUI();
	}

	public EncodersTableList getEncodersTableList(){
		return encodersTableList;
	}

	private void createEncodingPane(){

		// create the encodersTableList
		encodersTableList = new EncodersTableList(this);
		// instantiate the control panel
		controlPanel = new EncodersToolBar(this);
		// add a null encoder to start with
		this.updateEncoderPanel(null);

	}

	public FuzzerTable getFuzzersTable(){
		return fuzzersTable;
	}

	private void fuzzersTableSelectionListen(){
		fuzzersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {

				int row = fuzzersTable.getSelectedRow();

				if(row > -1){

					FuzzingPanel.this.getEncodersTableList().show(row);
					int c = FuzzingPanel.this.getEncodersTableList().getEncoderCount(row);
					if(c==0){
						controlPanel.disableAll();
						controlPanel.enableAdd();
					}else if(c==1){
						controlPanel.disableAll();
						controlPanel.enableAdd();
						controlPanel.enableDelete();
					}else{
						controlPanel.enableAll();
					}
				}

			}
		});
	}

	public EncodersToolBar getEncoderToolBar(){
		return controlPanel;
	}

	public FuzzersTableModel getFuzzersTableModel() {
		return mFuzzTableModel;
	}

	/**
	 * <p>Method for adding a transform to a fuzzer.</p>
	 * <p>This will appear within the transforms table, if
	 * the corresponding fuzzer is clicked.</p>
	 * 
	 * @param fuzzerNumber
	 * @param encoder
	 * @param prefix
	 * @param suffix
	 */
	public void addTransform(int fuzzerNumber, String encoder, String prefix,
			String suffix) {

		encodersTableList.getEncoderTableModel(fuzzerNumber - 1).addRow(encoder, prefix, suffix);
		//encodersTableList.add(encoder, prefix, suffix);

	}
	
	public OutputTable getOutputTable(){
		return mOutputTable;
	}
}
