/**
 * FuzzingPanel.java 0.4
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon . org
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
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.*;

import javax.swing.SwingWorker3;

import javax.swing.table.TableColumn;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.ver.Format;
/**
 * <p>The main "TCP Fuzzing" panel, displayed within the Main Frame Window.</p>
 * <p>This panel performs all TCP related fuzzing operations, including the
 * addition and removal of generators, reporting back the results into the
 * current window, as well as writting them to file.</p>
 *
 * @author subere (at) uncon org
 * @version 0.4
 */
public class FuzzingPanel extends JPanel {
  // The frame that the sniffing panel is attached
  private final FrameWindow m;
  // The JPanels
  private final JPanel outputPanel;
  // The JTextArea
  private final JTextArea target, port, message, outputTable;
  // The JTable of the generator
  private JTable generatorTable;
  // And the table model that goes with it
  private FuzzingTableModel mFuzzingTableModel;
  // The JButtons
  private final JButton buttonAddGen, buttonRemGen,
  buttonFuzzStart, buttonFuzzStop;
  // The swing worker used when the button "fuzz" is pressed
  private SwingWorker3 worker;
  // A counter for the number of times fuzz has been clicked
  private int counter;
  // The names of the columns within the table of generators
  private static final String[] COLUMNNAMES = {
                                              "Generator", "Start", "End"};

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
    counter = 0;


    // The target panel
    JPanel targetPanel = new JPanel();
    targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Target "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    target = new JTextArea(1, 1);

    target.setEditable(true);
    target.setVisible(true);
    target.setFont(new Font("Verdana", Font.PLAIN, 12));
    target.setLineWrap(false);
    target.setWrapStyleWord(true);
    target.setMargin(new Insets(1, 1, 1, 1));
    target.setBackground(Color.WHITE);
    target.setForeground(Color.BLACK);
    getMainWindow().popup(target);

    JScrollPane targetScrollPane = new JScrollPane(target);
    targetScrollPane.setVerticalScrollBarPolicy(21);
    targetScrollPane.setHorizontalScrollBarPolicy(31);

    targetScrollPane.setPreferredSize(new Dimension(480, 20));
    targetPanel.add(targetScrollPane);


    targetPanel.setBounds(10, 20, 500, 60);
    add(targetPanel);
    // The port panel
    JPanel portPanel = new JPanel();
    portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    port = new JTextArea(1, 1);

    port.setEditable(true);
    port.setVisible(true);
    port.setFont(new Font("Verdana", Font.PLAIN, 12));
    port.setLineWrap(false);
    port.setWrapStyleWord(true);
    port.setMargin(new Insets(1, 1, 1, 1));
    port.setBackground(Color.WHITE);
    port.setForeground(Color.BLACK);
    getMainWindow().popup(port);

    JScrollPane portScrollPane = new JScrollPane(port);
    portScrollPane.setVerticalScrollBarPolicy(21);
    portScrollPane.setHorizontalScrollBarPolicy(31);

    portScrollPane.setPreferredSize(new Dimension(50, 20));
    portPanel.add(portScrollPane);

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
    message.setLineWrap(true);
    message.setWrapStyleWord(true);
    message.setMargin(new Insets(1, 1, 1, 1));
    message.setBackground(Color.WHITE);
    message.setForeground(Color.BLACK);
    getMainWindow().popup(message);

    JScrollPane messageScrollPane = new JScrollPane(message);
    messageScrollPane.setVerticalScrollBarPolicy(20);

    messageScrollPane.setPreferredSize(new Dimension(480, 160));
    requestPanel.add(messageScrollPane);

    requestPanel.setBounds(10, 80, 500, 200);
    add(requestPanel);
    // The top buttons
    buttonAddGen = new JButton(ADDGENSTRING);
    buttonAddGen.setBounds(580, 30, 130, 20);
    add(buttonAddGen);
    buttonAddGen.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        generatorAdd();
      }
    });
    buttonRemGen = new JButton("Remove Generator");
    buttonRemGen.setBounds(730, 30, 150, 20);
    add(buttonRemGen);
    buttonRemGen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        generatorRemove();
      }
    });

    // The generator panel
    JPanel generatorPanel = new JPanel();
    generatorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Added Generators Table"),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    /*
     * Fuzzing Table Model
     */
    mFuzzingTableModel = new FuzzingTableModel(COLUMNNAMES);
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
    generatorPanel.add(generatorScrollPane);

    generatorPanel.setBounds(680, 60, 200, 160);
    add(generatorPanel);
    // The fuzz buttons
    buttonFuzzStart = new JButton("Fuzz!");
    buttonFuzzStart.setBounds(730, 240, 70, 20);
    add(buttonFuzzStart);
    buttonFuzzStart.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        worker = new SwingWorker3() {
          public Object construct() {
            buttonFuzzStart.setEnabled(false);
            buttonFuzzStop.setEnabled(true);
            // getMainMenuBar().setFuzzStartEnabled(false);
            // getMainMenuBar().setFuzzStopEnabled(true);
            // getMainWindow().setTabSniffingEnabled(false);
            fuzzStart();
            return "start-window-return";
          }

          public void finished() {
            buttonFuzzStart.setEnabled(true);
            buttonFuzzStop.setEnabled(false);
            // getMainMenuBar().setFuzzStartEnabled(true);
            // getMainMenuBar().setFuzzStopEnabled(false);
            // getMainWindow().setTabSniffingEnabled(true);
          }
        };
        worker.start();
      }
    });
    buttonFuzzStop = new JButton("Stop");
    buttonFuzzStop.setEnabled(false);
    buttonFuzzStop.setBounds(810, 240, 70, 20);
    add(buttonFuzzStop);
    buttonFuzzStop.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        fuzzStop();
        buttonFuzzStart.setEnabled(true);
        buttonFuzzStop.setEnabled(false);
        // getMainMenuBar().setFuzzStartEnabled(true);
        // getMainMenuBar().setFuzzStopEnabled(false);
        // getMainWindow().setTabSniffingEnabled(true);
      }
    });
    // The output panel
    outputPanel = new JPanel();
    outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Output (Last 1000 Lines) "),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    outputTable = new JTextArea();

    outputTable.setEditable(false);
    outputTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
    outputTable.setLineWrap(false);
    outputTable.setWrapStyleWord(true);
    outputTable.setMargin(new Insets(3, 3, 3, 3));
    outputTable.setBackground(Color.WHITE);
    outputTable.setForeground(Color.BLACK);
    getMainWindow().popup(outputTable);

    JScrollPane outputScrollPane = new JScrollPane(outputTable);
    outputScrollPane.setVerticalScrollBarPolicy(20);

    outputScrollPane.setPreferredSize(new Dimension(850, 130));
    outputPanel.add(outputScrollPane);

    outputPanel.setBounds(10, 280, 870, 170);
    add(outputPanel);

    // Some value defaults
    target.setText("http://10.255.1.224");
    port.setText("80");
    message.setText(
      "POST / HTTP/1.0\r\nContent-Length: 87\r\n\nuser_name=asdf&password=asdf\r\n\r\n");
  }

  /**
   * <p>Method trigered when the fuzz button is pressed in the current panel.
   * </p>
   */
  public void fuzzStart() {
    // Check to see if a message is present
    message.copy();
    if ("".equals(message.getText())) {
      JOptionPane.showMessageDialog(this,
                                    "The request field is blank.\n" +
                                    "Specify a request\n",
                                    "Empty Request Field",
                                    JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    // Increment the counter
    counter++;
    counter %= 100;
    // Update the border of the output panel
    outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Output (Last 1000 Lines)  " + "Logging in folder (" +
                         Format.DATE +
                         // getJBroFuzz().getVersion().getDate() +
                         ") Session " + counter),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // Clear the text of the output pane
    outputTable.setText("");

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
  public void fuzzStop() {
    getJBroFuzz().stopGenerator();
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
      len = text.length();
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

  /**
   * <p>Get the number of times that fuzzing has been attempted.</p>
   * @return int
   */
  public int getFuzzCount() {
    return counter;
  }

  /**
   * <p>Set the output text to contain the specified String, by appending that
   * String to the already present output String value. If the total number of
   * lines exceeds 1000, proceed to clear the original String value present
   * within the JTextArea prior to appending the given String.</p>
   *
   * @param s String
   */
  public void setOutputText(String s) {
    final int lines = outputTable.getLineCount();
    // Refresh after 1000 lines
    if (lines > 1000) {
      outputTable.setText("");
    }
    outputTable.append(s);

    final int caret = outputTable.getText().length();
    outputTable.setCaretPosition(caret);
  }

  /**
   * <p>Method for adding a generator.</p>
   */
  public void generatorAdd() {
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
                                    ADDGENSTRING,
                                    JOptionPane.ERROR_MESSAGE);
    }
    // Else find out the location of where the text has been selected
    else {
      final int sPoint = message.getSelectionStart();
      final int fPoint = message.getSelectionEnd();

      String generators = getJBroFuzz().getConstructor().
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
   * <p>Method for removing a generator.</p>
   */
  public void generatorRemove() {
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
        STRING_COLUMN_SEPARATOR);
      mFuzzingTableModel.removeRow(splitString[0],
                                   Integer.parseInt(splitString[1]),
                                   Integer.parseInt(splitString[2]));
    }
  }

  public void setFuzzStart(final boolean b) {
    buttonFuzzStart.setEnabled(b);
  }

  public void setFuzzStop(final boolean b) {
    buttonFuzzStop.setEnabled(b);
  }

  public FrameMenuBar getMainMenuBar() {
    return m.getFrameMenuBar();
  }

  public JBroFuzz getJBroFuzz() {
    return m.getJBroFuzz();
  }

  public FrameWindow getMainWindow() {
    return m;
  }

}
