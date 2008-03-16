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


import java.awt.*;
import javax.swing.*;

import java.awt.event.*;

import javax.swing.text.*;
import javax.swing.table.*;

import org.owasp.jbrofuzz.fuzz.MessageCreator;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.tablemodels.ThreeColumnModel;
import org.owasp.jbrofuzz.ui.viewers.PropertiesViewer;
import org.owasp.jbrofuzz.ui.viewers.WindowPlotter;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;
import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.Format;

import org.owasp.jbrofuzz.ui.menu.*;
import org.owasp.jbrofuzz.ui.tablemodels.*;

import org.owasp.jbrofuzz.core.*;
import org.owasp.jbrofuzz.fuzz.*;

import java.util.*;
import java.io.*;
import java.text.*;

import org.apache.commons.lang.*;

import com.Ostermiller.util.Browser;

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
 * @version 0.8
 */
public class FuzzingPanel extends JBroFuzzPanel {

	private static final long serialVersionUID = 16982374020211L;
	// The frame that the sniffing panel is attached
	// private final JBroFuzzWindow m;
	// The JPanels
	private final JPanel outputPanel;
	// The JTextField
	private final JTextField target;
	// The port JFormattedTextField
	private final JFormattedTextField port;
	// The JTextArea
	private final NonWrappingTextPane message;
	// The JTable were results are outputted
	private JTable outputTable;
	// And the table model that goes with it
	private SixColumnModel outputTableModel;
	// The JTable of the generator
	private JTable generatorTable;
	// And the table model that goes with it
	private ThreeColumnModel mFuzzingTableModel;
	// The JButtons
	private final JButton buttonAddGen, buttonRemGen, buttonFuzzStart,
	buttonFuzzStop, buttonPlot;
	// The swing worker used when the button "fuzz" is pressed
	private SwingWorker3 worker;
	// A counter for the number of times fuzz has been clicked
	private int counter, session;
	// The request iterator performing all the fuzzing
	private MessageCreator mRIterator;
	// The table sorter
	private TableSorter sorter;
	// The frame window
	private JBroFuzzWindow m;

	/**
	 * This constructor is used for the " Fuzzing " panel that resides under the
	 * FrameWindow, within the corresponding tabbed panel.
	 * 
	 * @param m
	 *          FrameWindow
	 */
	public FuzzingPanel(final JBroFuzzWindow m) {
		super(m);
		// this.setLayout(null);

		this.m = m;
		counter = 1;
		session = 0;

		// The target panel
		final JPanel targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" URL "), BorderFactory.createEmptyBorder(1, 1,
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
		/* The port panel
		final JPanel portPanel = new JPanel();
		portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1,
						1)));
		*/
		port = new JFormattedTextField();

		port.setEditable(true);
		port.setVisible(false);
		port.setFont(new Font("Verdana", Font.BOLD, 12));
		port.setMargin(new Insets(1, 1, 1, 1));
		port.setBackground(Color.WHITE);
		port.setForeground(Color.BLACK);
		getFrame().popup(port);

		port.setPreferredSize(new Dimension(50, 20));
		// portPanel.add(port);

		// portPanel.setBounds(510, 20, 60, 60);
		// this.add(portPanel);
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
				FuzzingPanel.this.add();
			}
		});

		// The remove generator button
		buttonRemGen = new JButton(ImageCreator.REMOVE_IMG);
		buttonRemGen.setToolTipText("Remove a Generator");
		buttonRemGen.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				FuzzingPanel.this.remove();
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
		mFuzzingTableModel = new ThreeColumnModel(getFrame());
		generatorTable = new JTable();
		generatorTable.setBackground(Color.BLACK);
		generatorTable.setForeground(Color.WHITE);

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

		buttonAddGen.setBounds(225, 35, 40, 30);
		buttonRemGen.setBounds(225, 80, 40, 30);

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
						FuzzingPanel.this.start();
						return "start-window-return";
					}

					@Override
					public void finished() {
						FuzzingPanel.this.stop();
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
						FuzzingPanel.this.stop();
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
						FuzzingPanel.this.fuzzBroButton();
					}
				});

			}
		});

		// The output panel
		outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new SixColumnModel();
		outputTableModel.setColumnNames("No", "Target", "Timestamp", "Status", "Time Taken", "Response Size");
		sorter = new TableSorter(outputTableModel);

		outputTable = new JTable(sorter);
		sorter.setTableHeader(outputTable.getTableHeader());
		popup(outputTable);
		
		outputTable.setColumnSelectionAllowed(false);
		outputTable.setRowSelectionAllowed(true);
		
		outputTable.setFont(new Font("Monospaced", Font.BOLD, 12));
		outputTable.setBackground(Color.BLACK);
		outputTable.setForeground(Color.WHITE);
		outputTable.setSurrendersFocusOnKeystroke(true);
		outputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		
		outputTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(final MouseEvent e){
				if (e.getClickCount() == 2){
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {

							final int c = outputTable.getSelectedRow();
							final String name = (String) outputTable.getModel().getValueAt(c, 0);
							new WindowViewer(getFrame(), name, WindowViewer.VIEW_FUZZING_PANEL);

						}
					});
				}
			}
		} );

		final JScrollPane outputScrollPane = new JScrollPane(outputTable);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		outputScrollPane.setPreferredSize(new Dimension(840, 130));
		outputPanel.add(outputScrollPane);
		outputPanel.setBounds(10, 280, 860, 170);
		this.add(outputPanel);

		

		// Some value defaults
		target.setText("http://localhost");
		port.setText("80");
		setMessageText(Format.REQUEST_TCP);
		message.setCaretPosition(0);
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

		/* Check to see if a message is present
		if ("".equals(message.getText())) {
			JOptionPane.showMessageDialog(this, "The request field is blank.\n"
					+ "Specify a request\n", "Empty Request Field",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		 */

		// Increment the session and reset the counter
		session++;
		counter = 1;
		// Update the border of the output panel
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output  "
						+ "Logging in folder (" + Format.DATE +
						") Session " + session), BorderFactory.createEmptyBorder(5, 5,
								5, 5)));


		final int rows = generatorTable.getRowCount();
		if (rows == 0) {

			MessageCreator currentMessage = new MessageCreator(getMessageText(), "", 0, 0);

			final Date now = new Date();
			final SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss-SSS");
			final String timestamp = format.format(now);
			final String filename = getCounter(true);

			outputTableModel.addRow(filename, this.getTargetText(), timestamp, "Sending...", "1 / 1", "0 bytes");
			outputTable.scrollRectToVisible(outputTable.getCellRect(outputTable.getRowCount(), 0, true));
			// message.setText(currentMessage.getMessage());
			
			Connection con = new Connection(this.getTargetText(), currentMessage.getMessage());

			outputTableModel.updateLastRow(filename, this.getTargetText() + ":" + con.getPort(), timestamp, "Finished", "1 / 1", con.getReply().getBytes().length + " bytes");


			this.getFrame().getJBroFuzz().getHandler().writeFuzzFile2(
					"<!-- \r\n[ { 1 / 1 }, " + filename + " " + timestamp + "] " 
					+ "\r\n" + this.getTargetText() + " : " + con.getPort() + "\r\n" 
					+ con.getMessage() + "\r\n-->\r\n" 
					+ con.getReply(), filename);

		} else {
			for (int i = 0; i < rows; i++) {
				final String category = (String) mFuzzingTableModel.getValueAt(i,
						0);
				final int start = ((Integer) mFuzzingTableModel.getValueAt(i, 1))
				.intValue();
				final int end = ((Integer) mFuzzingTableModel.getValueAt(i, 2))
				.intValue();

				try {
					for(Fuzzer f = new Fuzzer(category, Math.abs(end - start) ); f.hasNext();) {

						String payload = f.next();
						
						MessageCreator currentMessage = new MessageCreator(getMessageText(),  payload, start, end);

						final Date now = new Date();
						final SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss-SSS");
						final String timestamp = format.format(now);
						final String filename = getCounter(true);

						System.out.println("Filename: " + filename + " " + payload);

						outputTableModel.addRow(filename, this.getTargetText(), timestamp, "Sending...", f.getCurrectValue() + "/" + f.getMaximumValue(), "0 bytes");
						outputTable.scrollRectToVisible(outputTable.getCellRect(outputTable.getModel().getRowCount(), 0, true));
						// message.setText(currentMessage.getMessage());

						Connection con = new Connection(this.getTargetText(), currentMessage.getMessage());

						this.getFrame().getJBroFuzz().getHandler().writeFuzzFile2(
								"<!-- \r\n[ { "  + f.getCurrectValue() + "/" + f.getMaximumValue() + " }, " + filename + " " + timestamp + "] " 
								+ "\r\n" + this.getTargetText() + " : " + con.getPort() + "\r\n" 
								+ con.getMessage() + "\r\n-->\r\n" 
								+ con.getReply(), filename);
						
						outputTableModel.updateLastRow(filename, this.getTargetText() + ":" + con.getPort(), timestamp, "Finished", f.getCurrectValue() + "/" + f.getMaximumValue(), con.getReply().getBytes().length + " bytes");



					}
				} catch (NoSuchFuzzerException e) {

					this.getFrame().log("The fuzzer could not be found...");
				}

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
		// mRIterator.stop();
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
					"Add Generator", JOptionPane.ERROR_MESSAGE);
			selectedText = "";
		}
		/* If no text has been selected, prompt the user to select some text
		if (selectedText == null) {
			JOptionPane.showMessageDialog(this,
					"Select (highlight) a text range \nfrom the Request field",
					FuzzingPanel.ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
		}
		 */
		// Else find out the location of where the text has been selected
		final int sPoint = message.getSelectionStart();
		final int fPoint = message.getSelectionEnd();

		new PayloadsDialog(this, sPoint, fPoint);


	}

	public void addPayload(String Id, int start, int end) {

		mFuzzingTableModel.addRow(Id, start, end);

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
			.split(ThreeColumnModel.STRING_SEPARATOR);
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

		s += StringUtils.leftPad("" + counter, 8, '0');

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
	 *
	public String getPortText() {
		return port.getText();
	}
	*/

	/**
	 * Get the value of the target String stripping out, any protocol
	 * specifications as well as any trailing slashes.
	 * 
	 * @return String
	 */
	public String getTargetText() {
		String text = target.getText();
		/*
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
		*/
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

		message.setText(input);

	}
	
	protected void popup(final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();
		
		final JMenuItem i0 = new JMenuItem("Open in Browser");
		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");
		final JMenuItem i5 = new JMenuItem("Properties");
		
		i0.setEnabled(true);
		i1.setEnabled(false);
		i2.setEnabled(true);
		i3.setEnabled(false);
		i4.setEnabled(true);
		i5.setEnabled(true);
		
		popmenu.add(i0);
		popmenu.addSeparator();
		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.add(i4);
		popmenu.addSeparator();
		popmenu.add(i5);
		
		i0.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				Browser.init();
				
				final String fileName = (String) area.getValueAt(area.getSelectedRow(), 0) + ".html";
				final File f = m.getJBroFuzz().getHandler().getFuzzFile(fileName);
				
				try {
					Browser.displayURL(f.toURI().toString());
				} catch (final IOException ex) {
					getFrame().log(
							"Could not launch link in external browser");
				}
			}
		});
		
		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Copy
				final StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();
				for(final int element : selection) {
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
				
			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Select All
				area.selectAll();
			}
		});

		i5.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				
				final int c = area.getSelectedRow();
				final String name = (String) area.getModel().getValueAt(c, 0);
				new WindowViewer(getFrame(), name, WindowViewer.VIEW_FUZZING_PANEL);
				/*
				StringBuffer output = new StringBuffer();
				
				
				for (int i = 0; i < area.getColumnCount(); i++) {
					
					// TableColumn column = area.getColumnModel().getColumn(i);
					output.append(area.getColumnName(i) + ": ");
					output.append(area.getModel().getValueAt(area.getSelectedRow(), i));
					output.append("\n");
				}
				
				
				// final String exploit = (String) area.getModel().getValueAt(area.getSelectedRow(), 0);
				new PropertiesViewer(getFrame(), "Properties", output.toString());
				*/
				
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
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				checkForTriggerEvent(e);
			}
		});
	}


}
