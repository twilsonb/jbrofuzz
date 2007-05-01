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
package org.owasp.jbrofuzz.ui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import javax.swing.text.*;

import org.owasp.jbrofuzz.*;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.ver.*;
/**
 * <p>The main "TCP Fuzzing" panel, displayed within the Main Frame Window.</p>
 * <p>This panel performs all TCP related fuzzing operations, including the
 * addition and removal of generators, reporting back the results into the
 * current window, as well as writting them to file.</p>
 *
 * @author subere (at) uncon org
 * @version 0.6
 */
public class FuzzingPanel extends JPanel {
  // The frame that the sniffing panel is attached
  private final FrameWindow m;
  // The JPanels
  private final JPanel outputPanel;
  // The JTextField
  private final JTextField target;
  // The port JFormattedTextField
  private final JFormattedTextField port;
  // The JTextArea
  private final JTextArea message;
  // The JTable were results are outputed
  private JTable outputTable;
  // And the table model that goes with it
  private SniffingTableModel outputTableModel;
  // The JTable of the generator
  private JTable generatorTable;
  // And the table model that goes with it
  private FuzzingTableModel mFuzzingTableModel;
  // The JButtons
  private final JButton buttonAddGen, buttonRemGen,
  buttonFuzzStart, buttonFuzzStop, buttonPlot;
  // The swing worker used when the button "fuzz" is pressed
  private SwingWorker3 worker;
  // A counter for the number of times fuzz has been clicked
  private int counter, session;
  // Just a string that is being used a lot
  private static final String ADDGENSTRING = "Add Generator";
  /**
   * This constructor is used for the "TCP Fuzzing Panel" that resides under the
   * FrameWindow, within the corresponding tabbed panel.
   *
   * @param m FrameWindow
   */
  public FuzzingPanel(FrameWindow m) {
    super();
    setLayout(null);

    this.m = m;
    counter = 1;
    session = 0;

    // The target panel
    JPanel targetPanel = new JPanel();
    targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Target "),
                          BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    target = new JTextField();
    target.setEditable(true);
    target.setVisible(true);
    target.setFont(new Font("Verdana", Font.BOLD, 12));
    target.setMargin(new Insets(1, 1, 1, 1));
    target.setBackground(Color.WHITE);
    target.setForeground(Color.BLACK);
    getFrameWindow().popup(target);

    target.setPreferredSize(new Dimension(480, 20));
    targetPanel.add(target);

    targetPanel.setBounds(10, 20, 500, 60);
    add(targetPanel);
    // The port panel
    JPanel portPanel = new JPanel();
    portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    port = new JFormattedTextField();

    port.setEditable(true);
    port.setVisible(true);
    port.setFont(new Font("Verdana", Font.BOLD, 12));
    port.setMargin(new Insets(1, 1, 1, 1));
    port.setBackground(Color.WHITE);
    port.setForeground(Color.BLACK);
    getFrameWindow().popup(port);

    port.setPreferredSize(new Dimension(50, 20));
    portPanel.add(port);

    portPanel.setBounds(510, 20, 60, 60);
    add(portPanel);
    // The message panel
    JPanel requestPanel = new JPanel();
    requestPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Request "),
                           BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    message = new JTextArea();

    message.setEditable(true);
    message.setVisible(true);
    message.setFont(new Font("Verdana", Font.PLAIN, 12));
    message.setLineWrap(false);
    message.setWrapStyleWord(true);
    message.setMargin(new Insets(1, 1, 1, 1));
    message.setBackground(Color.WHITE);
    message.setForeground(Color.BLACK);
    getFrameWindow().popup(message);

    JScrollPane messageScrollPane = new JScrollPane(message);
    messageScrollPane.setVerticalScrollBarPolicy(20);
    messageScrollPane.setHorizontalScrollBarPolicy(30);
    messageScrollPane.setPreferredSize(new Dimension(480, 160));
    requestPanel.add(messageScrollPane);

    requestPanel.setBounds(10, 80, 500, 200);
    add(requestPanel);

    // The add generator button
    buttonAddGen = new JButton(ImageCreator.ADD_IMG);
    buttonAddGen.setToolTipText("Add a Generator");
    buttonAddGen.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        generatorAddButton();
      }
    });

    // The remove generator button
    buttonRemGen = new JButton(ImageCreator.REMOVE_IMG);
    buttonRemGen.setToolTipText("Remove a Generator");
    buttonRemGen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        generatorRemoveButton();
      }
    });

    // The generator panel
    JPanel generatorPanel = new JPanel();
    generatorPanel.setLayout(null);
    
    generatorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Added Generators Table"),
                             BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    /*
     * Fuzzing Table Model
     */
    mFuzzingTableModel = new FuzzingTableModel(this);
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
      }
      else {
        column.setPreferredWidth(50);
      }
    }
    generatorTable.setFont(new Font("Monospaced", Font.PLAIN, 12));

    JScrollPane generatorScrollPane = new JScrollPane(generatorTable);
    generatorScrollPane.setVerticalScrollBarPolicy(20);

    generatorScrollPane.setPreferredSize(new Dimension(180, 100));
    
    generatorScrollPane.setBounds(15, 25, 180, 100);
    buttonRemGen.setBounds(235, 60, 40, 20);
    buttonAddGen.setBounds(235, 25, 40, 20);
    
    generatorPanel.add(generatorScrollPane);
    generatorPanel.add(buttonRemGen);
    generatorPanel.add(buttonAddGen);
    
    generatorPanel.setBounds(570, 20, 300, 160);
    add(generatorPanel);
    // The fuzz buttons
    buttonFuzzStart = new JButton("Fuzz!", ImageCreator.START_IMG);
    buttonFuzzStart.setBounds(580, 230, 90, 40);
    buttonFuzzStart.setToolTipText("Start Fuzzing!");
    add(buttonFuzzStart);
    buttonFuzzStart.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        worker = new SwingWorker3() {
          public Object construct() {
            fuzzStartButton();
            return "start-window-return";
          }

          public void finished() {
            fuzzStopButton();
          }
        };
        worker.start();
      }
    });
    buttonFuzzStop = new JButton("Stop", ImageCreator.STOP_IMG);
    buttonFuzzStop.setEnabled(false);
    buttonFuzzStop.setToolTipText("Stop Fuzzing");
    buttonFuzzStop.setBounds(680, 230, 90, 40);
    add(buttonFuzzStop);
    buttonFuzzStop.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
      	SwingUtilities.invokeLater(new Runnable() {
          public void run() {
          	fuzzStopButton();
          }
        });       
      }
    });
    // The plot button
    buttonPlot = new JButton("Bro", ImageCreator.PAUSE_IMG);
    buttonPlot.setEnabled(true);
    buttonPlot.setToolTipText("Plot Fuzzing Results");
    buttonPlot.setBounds(780, 230, 80, 40);
    add(buttonPlot);
    buttonPlot.addActionListener(new ActionListener() {
      // public void actionPerformed(final ActionEvent e) {
      	public void actionPerformed(final ActionEvent e) {
        	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	WindowPlotter wd = new WindowPlotter(getFrameWindow(), "Title");
            }
          });
        
      }
    });
   
    // The output panel
    outputPanel = new JPanel();
    outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Output (Last 1000 Lines) "),
                          BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    outputTableModel = new SniffingTableModel();
    
    outputTable = new JTable();
    outputTable.setModel(outputTableModel);
    
    outputTable.setFont(new Font("Monospaced", Font.BOLD, 12));
    outputTable.setBackground(Color.BLACK);
    outputTable.setForeground(Color.WHITE);
    outputTable.setSurrendersFocusOnKeystroke(true);
    outputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // getFrameWindow().popup(outputTable);

    JScrollPane outputScrollPane = new JScrollPane(outputTable);
    outputScrollPane.setVerticalScrollBarPolicy(20);
    outputScrollPane.setPreferredSize(new Dimension(840, 130));
    outputPanel.add(outputScrollPane);
    outputPanel.setBounds(10, 280, 860, 170);
    add(outputPanel);

    // Add the action listener for each row
    ListSelectionModel rowSM = outputTable.getSelectionModel();

    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages
        if (e.getValueIsAdjusting()) {
          return;
        }
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty()) {
          // No rows selected
        }
        else {
          int selectedRow = lsm.getMinSelectionIndex();
          String s = outputTableModel.getValueAt(selectedRow);
          WindowViewer vew = new WindowViewer(getFrameWindow(), s, WindowViewer.VIEW_FUZZING_PANEL);
        }
      }
    }); // ListSelectionListener

    
    // Some value defaults
    target.setText("http://localhost");
    port.setText("80");
    message.setText("GET /index.html HTTP/1.0\r\n" + "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1\r\n" + "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\n" +
                    "Accept-Language: en-gb,en;q=0.5\r\n" +
                    "Accept-Encoding: gzip,deflate\r\n" +
                    "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n" +
                    "Keep-Alive: 300\r\n" + "Connection: keep-alive\r\n\r\n");
    message.setCaretPosition(0);
  }

  /**
   * <p>Method trigered when the fuzz button is pressed in the current panel.
   * </p>
   */
  public void fuzzStartButton() {
    if (!buttonFuzzStart.isEnabled()) {
      return;
    }
    // UI and Colors
    buttonFuzzStart.setEnabled(false);
    buttonFuzzStop.setEnabled(true);
    target.setEditable(false);
    target.setBackground(Color.BLACK);
    target.setForeground(Color.WHITE);
    port.setEditable(false);
    port.setBackground(Color.BLACK);
    port.setForeground(Color.WHITE);
    // Check to see if a message is present
    if ("".equals(message.getText())) {
      JOptionPane.showMessageDialog(this,
                                    "The request field is blank.\n" + "Specify a request\n",
                                    "Empty Request Field",
                                    JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    // Increment the session and reset the counter
    session++;
    counter = 1;
    // Update the border of the output panel
    outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Output (Last 1000 Lines)  " + "Logging in folder (" +
                         Format.DATE +
                         // getJBroFuzz().getVersion().getDate() +
                         ") Session " + session),
                          BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // Clear the text of the output pane
    // outputTable.setText("");

    final int rows = generatorTable.getRowCount();
    if (rows < 1) {
      StringBuffer sbuf = new StringBuffer(message.getText());
      getJBroFuzz().setGenerator(sbuf, 0, 0, "ZER");
      getJBroFuzz().runGenerator();
    }
    else {
      for (int i = 0; i < rows; i++) {
        final String generator = (String) mFuzzingTableModel.getValueAt(i, 0);
        final int start = ((Integer) mFuzzingTableModel.getValueAt(i, 1)).
                          intValue();
        final int end = ((Integer) mFuzzingTableModel.getValueAt(i, 2)).
                        intValue();

        StringBuffer sbuf = new StringBuffer(message.getText());
        getJBroFuzz().setGenerator(sbuf, start, end, generator);
        getJBroFuzz().runGenerator();
      }
    }
  }

  /**
   * <p>Method trigered when attempting to stop any fuzzing taking place.</p>
   */
  public void fuzzStopButton() {
    if (!buttonFuzzStop.isEnabled()) {
      return;
    }
    getJBroFuzz().stopGenerator();
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
   * Get the value of the target String stripping out, any protocol
   * specifications as well as any trailing slashes.
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
   * <p>Get the value of the port String trimming it down to a maximum of 5
   * characters.</p>
   * @return String
   */
  public String getPortText() {
    String text = port.getText();
    return text;
  }

  /**
   * <p>Get the value of the Message String that is to be transmitted on the
   * given Socket request that will be created.</p>
   * @return String
   */
  public String getMessageText() {
    return message.getText();
  }
  
  public void addRowInOuputTable(final String s) {
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	          outputTableModel.addEmptyRow();
	          int totalRows = outputTableModel.getRowCount();
	          outputTableModel.setValueAt(s, totalRows - 1, 0);
	          // Set the last row to be visible
	          outputTable.scrollRectToVisible(outputTable.getCellRect(outputTable.
	            getRowCount(), 0, true));
	        }
	      });

  }

  /**
   * <p>Method for adding a generator.</p>
   */
  public void generatorAddButton() {
    // Check to see what text has been selected
    String selectedText;
    try {
      selectedText = message.getSelectedText();
    }
    catch (IllegalArgumentException e) {
      JOptionPane.showInputDialog(this,
        "An exception was thrown while attempting to get the selected text",
                                  ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
      selectedText = "";
    }
    // If no text has been selected, prompt the user to select some text
    if (selectedText == null) {
      JOptionPane.showMessageDialog(this,
        "Select (highlight) a text range \nfrom the Request field",
                                    ADDGENSTRING, JOptionPane.ERROR_MESSAGE);
    }
    // Else find out the location of where the text has been selected
    else {
      final int sPoint = message.getSelectionStart();
      final int fPoint = message.getSelectionEnd();

      String generators = getJBroFuzz().getTCPConstructor().
                          getAllGeneratorNamesAndComments();
      String[] generatorArray = generators.split(", ");

      // Then prompt the user for the type of fuzzer
      String selectedValue = (String) JOptionPane.showInputDialog(this,
        "Select the type of fuzzing generator:", ADDGENSTRING,
                             JOptionPane.INFORMATION_MESSAGE, null,
                                generatorArray, generatorArray[0]);
      // And finally add the generator
      if ((selectedValue != null)) {
        if (selectedValue.length() > 3) {
          selectedValue = selectedValue.substring(0, 3);
        }
        else {
          selectedValue = "   ";
        }
        mFuzzingTableModel.addRow(selectedValue, sPoint, fPoint);
      }
    }
  }

  /**
   * <p>Method for removing a generator. This method operates by removing a
   * row from the corresponding table model of the generator table.</p>
   */
  public void generatorRemoveButton() {
    int rows = generatorTable.getRowCount();
    if (rows < 1) {
      return;
    }
    String[] fuzzPoints = new String[rows];
    for (int i = 0; i < rows; i++) {
      fuzzPoints[i] = mFuzzingTableModel.getRow(i);
    }

    final String selectedFuzzPoint = (String) JOptionPane.showInputDialog(this,
      "Select the generator to remove:", "Remove Generator",
                                     JOptionPane.INFORMATION_MESSAGE, null,
                                                  fuzzPoints, fuzzPoints[0]);

    if (selectedFuzzPoint != null) {
      String[] splitString = selectedFuzzPoint.split(FuzzingTableModel.
        STRING_SEPARATOR);
      mFuzzingTableModel.removeRow(splitString[0],
                                   Integer.parseInt(splitString[1]),
                                   Integer.parseInt(splitString[2]));
    }
  }

  /**
   * Set the button enable status of the Fuzz! button
   * @param b boolean
   */
  public void setFuzzStartButtonEnable(final boolean b) {
    buttonFuzzStart.setEnabled(b);
  }

  /**
   * Set the button enable status of the Stop button
   * @param b boolean
   */
  public void setFuzzStopButtonEnable(final boolean b) {
    buttonFuzzStop.setEnabled(b);
  }

  /**
   * Access the main object that launches and is responsible for the application.
   * @return JBroFuzz
   */
  public JBroFuzz getJBroFuzz() {
    return m.getJBroFuzz();
  }

  /**
   * Access the main frame window in which this panel is attached to.
   * @return FrameWindow
   */
  public FrameWindow getFrameWindow() {
    return m;
  }

  /**
   * <p>Method for returning the counter held within the Sniffing Panel which
   * is responsible for counting the number of requests having been made. This
   * method is used for generating unique sequential file name and row
   * counts.</p>
   *
   * @param newCount boolean Increment the counter by 1
   * @return String
   */
  public String getCounter(boolean newCount) {
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
  
}
