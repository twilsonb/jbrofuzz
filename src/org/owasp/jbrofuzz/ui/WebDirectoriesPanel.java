/**
 * WebDirectoriesPanel.java 0.4
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

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;

import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.dir.RequestIterator;
import org.owasp.jbrofuzz.ui.util.TableSorter;

import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker3;
import javax.swing.table.TableColumn;
/**
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public class WebDirectoriesPanel extends JPanel {

  // The frame that the sniffing panel is attached
  private FrameWindow m;

  // The text areas used in their corresponding panels
  private JTextArea targetText, directoryText, portText;

  // The jbuttons present in the user interface
  private final JButton startButton, stopButton, pauseButton;

  // The jtable holding all the responses
  private JTable responseTable;

  // The correspondng table model
  private WebDirectoriesModel responseTableModel;

  // The names of the columns within the table of generators
  private static final String[] COLUMNNAMES = {
                                              "ID", "URI", "Code", "Status Text",
                                              "Comments", "Scripts"};

  /**
   * The constructor for the Web Directory Panel. This constructor spawns the
   * main panel involving web directories.
   *
   * @param m FrameWindow
   */
  public WebDirectoriesPanel(FrameWindow m) {
    super();
    setLayout(null);
    this.m = m;

    // Define the directory JPanel
    JPanel directoryPanel = new JPanel();
    directoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Directories "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    directoryPanel.setBounds(630, 20, 230, 430);
    add(directoryPanel);

    // Define the target JPanel
    JPanel targetPanel = new JPanel();
    targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Target URI"),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    targetPanel.setBounds(10, 20, 500, 60);
    add(targetPanel);

    // Define the port JPanel
    JPanel portPanel = new JPanel();
    portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Port "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    portPanel.setBounds(520, 20, 100, 60);
    add(portPanel);

    // Define the output JPanel
    JPanel outputPanel = new JPanel();
    outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Output "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    outputPanel.setBounds(10, 150, 610, 300);
    add(outputPanel);

    // Define the target text area
    targetText = new JTextArea(1, 1);
    targetText.setEditable(true);
    targetText.setVisible(true);
    targetText.setFont(new Font("Verdana", Font.BOLD, 12));
    targetText.setLineWrap(false);
    targetText.setWrapStyleWord(true);
    targetText.setMargin(new Insets(1, 1, 1, 1));
    targetText.setBackground(Color.WHITE);
    targetText.setForeground(Color.BLACK);
    getFrameWindow().popup(targetText);
    JScrollPane targetScrollPane = new JScrollPane(targetText);
    targetScrollPane.setVerticalScrollBarPolicy(21);
    targetScrollPane.setHorizontalScrollBarPolicy(31);
    targetScrollPane.setPreferredSize(new Dimension(480, 20));
    targetPanel.add(targetScrollPane);

    // Define the port text area
    portText = new JTextArea(1, 1);
    portText.setEditable(true);
    portText.setVisible(true);
    portText.setFont(new Font("Verdana", Font.BOLD, 12));
    portText.setLineWrap(false);
    portText.setWrapStyleWord(true);
    portText.setMargin(new Insets(1, 1, 1, 1));
    portText.setBackground(Color.WHITE);
    portText.setForeground(Color.BLACK);
    getFrameWindow().popup(portText);
    JScrollPane portScrollPane = new JScrollPane(portText);
    portScrollPane.setVerticalScrollBarPolicy(21);
    portScrollPane.setHorizontalScrollBarPolicy(31);
    portScrollPane.setPreferredSize(new Dimension(80, 20));
    portPanel.add(portScrollPane);

    // Define the directory text area
    directoryText = new JTextArea(1, 1);
    directoryText.setEditable(true);
    directoryText.setVisible(true);
    directoryText.setFont(new Font("Verdana", Font.BOLD, 12));
    directoryText.setLineWrap(false);
    directoryText.setWrapStyleWord(true);
    directoryText.setMargin(new Insets(1, 1, 1, 1));
    directoryText.setBackground(Color.WHITE);
    directoryText.setForeground(Color.BLACK);
    getFrameWindow().popup(directoryText);
    JScrollPane directoryScrollPane = new JScrollPane(directoryText);
    directoryScrollPane.setVerticalScrollBarPolicy(20);
    directoryScrollPane.setHorizontalScrollBarPolicy(30);
    directoryScrollPane.setPreferredSize(new Dimension(210, 390));
    directoryPanel.add(directoryScrollPane);

    // The add generator button
    startButton = new JButton("Start", ImageCreator.startImageIcon);
    startButton.setBounds(340, 100, 80, 40);
    add(startButton);
    startButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {

        SwingWorker3 worker = new SwingWorker3() {
          public Object construct() {
            buttonStart();
            return "start-window-return";
          }
          public void finished() {
          }
        };
        worker.start();
      }
    });

    pauseButton = new JButton("Pause", ImageCreator.pauseImageIcon);
    pauseButton.setBounds(430, 100, 100, 40);
    add(pauseButton);
    pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {

      }
    });

    stopButton = new JButton("Stop", ImageCreator.stopImageIcon);
    stopButton.setBounds(540, 100, 80, 40);
    add(stopButton);
    stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {

      }
    });

    responseTableModel = new WebDirectoriesModel(COLUMNNAMES);
    TableSorter sorter = new TableSorter(responseTableModel);
    responseTable = new JTable(sorter);
    sorter.setTableHeader(responseTable.getTableHeader());
    responseTable.getTableHeader().setToolTipText(
            "Click to specify sorting; Control-Click to specify secondary sorting");
    responseTable.setFont(new Font("Monospaced", Font.BOLD, 12));
    responseTable.setBackground(Color.black);
    responseTable.setForeground(Color.white);
    responseTable.setSurrendersFocusOnKeystroke(true);
    responseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // Set the column widths
    TableColumn column = null;
    for (int i = 0; i < responseTableModel.getColumnCount(); i++) {
      column = responseTable.getColumnModel().getColumn(i);
      if (i == 0) {
        column.setPreferredWidth(20);
      }
      if (i == 1) {
        column.setPreferredWidth(280);
      }
      if (i == 2) {
        column.setPreferredWidth(30);
      }
      if (i == 3) {
        column.setPreferredWidth(100);
      }

    }
    JScrollPane listTextScrollPane = new JScrollPane(responseTable);
    listTextScrollPane.setVerticalScrollBarPolicy(20);
    listTextScrollPane.setHorizontalScrollBarPolicy(31);
    listTextScrollPane.setPreferredSize(new Dimension(590, 260));
    listTextScrollPane.setWheelScrollingEnabled(true);
    outputPanel.add(listTextScrollPane);

    targetText.setText("http://intranet/");
    portText.setText("80");
    directoryText.setText("images\nfuzz\nrss\nlife\n");
  }
  /**
   * <p>Method for returning the main window frame that this tab is attached on.
   * </p>
   *
   * @return Window
   */
  public FrameWindow getFrameWindow() {
    return m;
  }

  /**
   * @todo Add button disability while directory check is progressing and
   * also add functionality from the menu bar.
   */

  /**
   * Method triggered when the start button is pressed.
   */
  public void buttonStart() {
    String port = portText.getText();
    String uri = targetText.getText();
    // Add a trailing / if one is not there
    if(!uri.endsWith("/")) {
      uri += "/";
    }
    // Add a leading http or https depending on the port
    if((!uri.startsWith("http://")) || (!uri.startsWith("https://"))) {
      if(port.equals("80")) {
        StringBuffer s = new StringBuffer(uri);
        s.insert(0, "http://");
        uri = s.toString();
      }
      if(port.equals("443")) {
        StringBuffer s = new StringBuffer(uri);
        s.insert(0, "https://");
        uri = s.toString();
      }
    }
    String dirs = directoryText.getText();

    responseTableModel.removeAllRows();

    RequestIterator cesg = new RequestIterator(getFrameWindow(), uri, dirs);
    cesg.run();

  }

  /**
   * <p>Method for adding an extra row to the output response table. The
   * different fields are identified by \n.
   * @param s String
   *
   */
  public void addRow(String s) {
    String[] inputArray = s.split("\n");
    responseTableModel.addRow(inputArray[0], inputArray[1], inputArray[2],
      inputArray[3], inputArray[4], inputArray[5]);
    // int totalRows = responseTableModel.getRowCount();
    // responseTableModel.setValueAt(s, totalRows - 1, 0);
    // Set the last row to be visible
    responseTable.scrollRectToVisible(responseTable.getCellRect(responseTable.
      getRowCount(), 0, true));
  }


}
