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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.owasp.jbrofuzz.dir.*;
import org.owasp.jbrofuzz.ver.*;
import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.ui.util.*;

import com.Ostermiller.util.*;
import java.io.IOException;
/**
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public class WebDirectoriesPanel extends JPanel implements KeyListener {

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

  // The request iterator to loop through the directories
  private RequestIterator cesg;

  // The directory panel that needs to update the line number
  private JPanel directoryPanel;

  // The session count counting how many times start has been hit
  private int session;

  /**
   * The constructor for the Web Directory Panel. This constructor spawns the
   * main panel involving web directories.
   *
   * @param m FrameWindow
   */
  public WebDirectoriesPanel(FrameWindow m) {
    super(null, true);
    this.m = m;
    session = 0;

    // Define the directory JPanel
    directoryPanel = new JPanel();
    directoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Total Directories to test: "),
                             BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    directoryPanel.setBounds(630, 20, 230, 430);
    add(directoryPanel);

    // Define the target JPanel
    JPanel targetPanel = new JPanel();
    targetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Target URI (http/https) "),
                          BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    targetPanel.setBounds(10, 20, 500, 60);
    add(targetPanel);

    // Define the port JPanel
    JPanel portPanel = new JPanel();
    portPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
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
    directoryText.addKeyListener(this);
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
            buttonStop();
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
        buttonStop();
      }
    });

    responseTableModel = new WebDirectoriesModel();
    TableSorter sorter = new TableSorter(responseTableModel);
    responseTable = new JTable(sorter);
    sorter.setTableHeader(responseTable.getTableHeader());
    responseTable.getTableHeader().setToolTipText(
      "Click to specify sorting; Control-Click to specify secondary sorting");
    popup(responseTable);

    responseTable.setFont(new Font("Monospaced", Font.BOLD, 12));
    responseTable.setBackground(Color.black);
    responseTable.setForeground(Color.white);
    responseTable.setSurrendersFocusOnKeystroke(true);
    // responseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    responseTable.setColumnSelectionAllowed(false);
    responseTable.setRowSelectionAllowed(true);
    // Set the column widths
    TableColumn column = null;
    for (int i = 0; i < responseTableModel.getColumnCount(); i++) {
      column = responseTable.getColumnModel().getColumn(i);
      if (i == 0) {
        column.setPreferredWidth(30);
      }
      if (i == 1) {
        column.setPreferredWidth(300);
      }
      if (i == 2) {
        column.setPreferredWidth(30);
      }
      if (i == 3) {
        column.setPreferredWidth(120);
      }
      if (i == 4) {
        column.setPreferredWidth(20);
      }
      if (i == 5) {
        column.setPreferredWidth(20);
      }

    }

    JScrollPane listTextScrollPane = new JScrollPane(responseTable);
    listTextScrollPane.setVerticalScrollBarPolicy(20);
    listTextScrollPane.setHorizontalScrollBarPolicy(31);
    listTextScrollPane.setPreferredSize(new Dimension(590, 260));
    listTextScrollPane.setWheelScrollingEnabled(true);
    outputPanel.add(listTextScrollPane);

    startButton.setEnabled(true);
    stopButton.setEnabled(false);
    pauseButton.setEnabled(false);

    targetText.setText("http://localhost");
    portText.setText("80");
    StringBuffer s = FileHandler.readDirectories(Format.FILE_DIR);
    directoryText.setText(s.toString());
    directoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Total Directories to test: "
                         + directoryText.getLineCount() + " "),
                             BorderFactory.createEmptyBorder(1, 1, 1, 1)));
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
   * @todo Load a file of directories from somewhere}
   */
  /**
   * Method triggered when the start button is pressed.
   */
  public void buttonStart() {

    if (!startButton.isEnabled()) {
      return;
    }
    // Increment the session number
    session++;
    session %= 100;

    // UI and Colors
    startButton.setEnabled(false);
    stopButton.setEnabled(true);
    targetText.setEditable(false);
    targetText.setBackground(Color.BLACK);
    targetText.setForeground(Color.WHITE);
    portText.setEditable(false);
    portText.setBackground(Color.BLACK);
    portText.setForeground(Color.WHITE);

    String uri = targetText.getText();
    // Add a trailing / if one is not there
    if (!uri.endsWith("/")) {
      uri += "/";
    }
    String dirs = directoryText.getText();
    int port = 0;
    try {
      port = Integer.parseInt(portText.getText());
    }
    catch (NumberFormatException ex) {
      m.log("Port has to be between [1 - 65535] in \"Web Directories\" Tab");
    }
    responseTableModel.removeAllRows();

    cesg = new RequestIterator(getFrameWindow(), uri, dirs, port);
    cesg.run();
  }

  /**
   * <p>Method for stopping the request iterator.</p>
   */
  public void buttonStop() {
    if (!stopButton.isEnabled()) {
      return;
    }
    // UI and Colors
    stopButton.setEnabled(false);
    startButton.setEnabled(true);
    targetText.setEditable(true);
    targetText.setBackground(Color.WHITE);
    targetText.setForeground(Color.BLACK);
    portText.setEditable(true);
    portText.setBackground(Color.WHITE);
    portText.setForeground(Color.BLACK);

    cesg.stop();
  }

  /**
   * <p>Method for adding an extra row to the output response table. The
   * different fields are identified by \n.
   * @param s String
   *
   */
  public void addRow(String s) {
    String[] inputArray = s.split("\n");
    if(inputArray.length != 6) {
      String error = "Web Directory Error! Cannot fit " + inputArray.length +
                     " columns into 6.";
      if(inputArray.length > 1) {
        error += " First column was " + inputArray[0];
      }
      m.log(error);
    }
    else {
      responseTableModel.addRow(inputArray[0], inputArray[1], inputArray[2],
                                inputArray[3], inputArray[4], inputArray[5]);
      // Set the last row to be visible
      responseTable.scrollRectToVisible(responseTable.getCellRect(responseTable.
        getRowCount(), 0, true));
    }
  }

  /**
   * Handle the key typed event from the text field.
   * @param e KeyEvent
   */
  public void keyTyped(KeyEvent e) {
    directoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Total Directories to test: "
                         + directoryText.getLineCount() + " "),
                             BorderFactory.createEmptyBorder(1, 1, 1, 1)));
  }

  /**
   * Handle the key pressed event from the text field.
   *
   * @param e KeyEvent
   */
  public void keyPressed(KeyEvent e) {
    // System.out.println(directoryText.getLineCount() );
  }

  /**
   * Handle the key released event from the text field.
   * @param e KeyEvent
   */
  public void keyReleased(KeyEvent e) {
    // System.out.println(directoryText.getLineCount() );
  }

  /**
   * Method for setting up the right click copy paste cut and select all menu.
   * @param area JTextArea
   */
  private void popup(final JTable area) {

    final JPopupMenu popmenu = new JPopupMenu();

    JMenuItem i2 = new JMenuItem("Copy");
    JMenuItem i4 = new JMenuItem("Select All");
    JMenuItem i5 = new JMenuItem("Open in Browser");

    i2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
      ActionEvent.CTRL_MASK));
    i4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
      ActionEvent.CTRL_MASK));

    popmenu.add(i2);
    popmenu.addSeparator();
    popmenu.add(i4);
    popmenu.addSeparator() ;
    popmenu.add(i5);

    i2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        area.removeRowSelectionInterval(0,area.getRowCount() - 1);
        int [] a = area.getSelectedRows();
        StringBuffer s = new StringBuffer() ;
        for(int i = 0; i < a.length ; i++) {
          TableSorter ts = (TableSorter) area.getModel();
          WebDirectoriesModel wm = (WebDirectoriesModel) ts.getTableModel() ;
          String row = wm.getRow(a[i]);
          s.append(row);
        }
        JTextArea myTempArea = new JTextArea(s.toString() );
        myTempArea.copy() ;
      }
    });

    i4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        area.selectAll() ;
      }
    });

    i5.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Browser.init();
        String url = (String) area.getValueAt(area.getSelectedRow() ,
                     1 % area.getColumnCount());
        try {
          Browser.displayURL(url);
        }
        catch (IOException ex) {
          getFrameWindow().log("Could not launch link in external browser");
        }
      }
    });

    area.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        checkForTriggerEvent(e);
      }

      public void mouseReleased(MouseEvent e) {
        checkForTriggerEvent(e);
      }

      private void checkForTriggerEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
          area.requestFocus();
          popmenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
  }

  /**
   * Get the session number. This number represents the number of times the
   * start button has been hit.
   *
   * @return String
   */
  public String getSessionNumber() {
    String s = "";
    if(session < 10) {
      s += "0";
    }
    s += session;
    return s;
  }
}
