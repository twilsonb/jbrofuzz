/**
 * WebDirectoriesPanel.java 0.6
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
import java.io.*;

import javax.swing.*;
import javax.swing.table.*;

import com.Ostermiller.util.*;
import org.owasp.jbrofuzz.dir.*;
import org.owasp.jbrofuzz.ui.util.*;
import org.owasp.jbrofuzz.ver.*;
/**
 * <p>The main panel for the web directory.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class WebDirectoriesPanel extends JPanel implements KeyListener {

  // The frame that the sniffing panel is attached
  private FrameWindow m;

  // The text areas used in their corresponding panels
  private JTextArea targetText, directoryText, portText;

  // The JButtons present in the user interface
  private final JButton startButton, stopButton;

  // The JCheckBox to continue if an error occurs
  private final JCheckBox errorCheckBox;
  
  // The JCheckBox boolean checkbox
  private boolean checkbox;
  
  // The jtable holding all the responses
  private JTable responseTable;

  // The correspondng table model
  private WebDirectoriesModel responseTableModel;

  // The request iterator to loop through the directories
  private DRequestIterator cesg;

  // The directory panel that needs to update the line number
  private JPanel directoryPanel, outputPanel;

  // The session count counting how many times start has been hit
  private int session;

  // The progress bar for the site
  private JProgressBar progressBar;

  // The table sorter
  private TableSorter sorter;

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
    checkbox = false;
    
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
      createTitledBorder(" Target URI [HTTP/HTTPS] "),
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
    outputPanel = new JPanel();
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
    //
    // targetPanel.getInputMap().put(KeyStroke.getKeyStroke("a"), "none");
    //
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
    startButton = new JButton("Start", ImageCreator.START_IMG);
    startButton.setBounds(350, 95, 80, 40);
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

    stopButton = new JButton("Stop", ImageCreator.STOP_IMG);
    stopButton.setBounds(440, 95, 80, 40);
    add(stopButton);
    stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        buttonStop();
      }
    });

    errorCheckBox = new JCheckBox("Continue on Error", false);
    errorCheckBox.setBounds(530, 95, 120, 10);
    add(errorCheckBox);
    errorCheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          checkbox = (!checkbox);
        }
      });
    
    responseTableModel = new WebDirectoriesModel();
    sorter = new TableSorter(responseTableModel);
    responseTable = new JTable(sorter);


    responseTable.getTableHeader().setToolTipText(
      "Click to specify sorting; Control-Click to specify secondary sorting");
    popup(responseTable);
    sorter.setTableHeader(responseTable.getTableHeader());
    responseTable.setFont(new Font("Monospaced", Font.BOLD, 12));
    responseTable.setBackground(Color.black);
    responseTable.setForeground(Color.white);
    responseTable.setSurrendersFocusOnKeystroke(true);
    responseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

    progressBar = new JProgressBar(0);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    progressBar.setMinimum(0);
    progressBar.setMaximum(100);
    progressBar.setPreferredSize(new Dimension(310, 20));
    JPanel progressPanel = new JPanel();
    progressPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Progress "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    progressPanel.setBounds(10, 85, 330, 60);
    progressPanel.add(progressBar);
    add(progressPanel);

    startButton.setEnabled(true);
    stopButton.setEnabled(false);


    targetText.setText("http://localhost");
    portText.setText("80");
  }

  /**
   * Set the text content of the directories jtextarea.
   * @param s StringBuffer
   */
  public void setDirectoriesText(StringBuffer s) {
    directoryText.setText(s.toString());
    directoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Total Directories to test: " +
                         directoryText.getLineCount() + " "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    directoryText.setCaretPosition(0);
  }

  /**
   * Set the progress bar on the display to a value between 0 and 100.
   * @param percent int
   */
  public void setProgressBar(final int percent) {
    if ((percent >= 0) && (percent <= 100)) {
      SwingWorker3 progressWorker = new SwingWorker3() {
        public Object construct() {
          progressBar.setValue(percent);
          return "progress-update-return";
        }

        public void finished() {
        }
      };
      progressWorker.start();
    }
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
   * Method triggered when the start button is pressed.
   */
  public void buttonStart() {
    if (!startButton.isEnabled()) {
      return;
    }
    // Increment the session number
    session++;
    session %= 100;
    // Update the panel, indicating directory
    outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Output " + "[Logging in file \\web-dir\\" +
                         Format.DATE + "\\" + getSessionNumber() + ".csv]  "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));

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
    String port = portText.getText();

    responseTableModel.removeAllRows();

    cesg = new DRequestIterator(getFrameWindow(), uri, dirs, port);
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
  public void addRow(final String s) {

    final String[] inputArray = s.split("\n");
    if (inputArray.length != 6) {
      StringBuffer error = new StringBuffer("Web Directory Error! Cannot fit " +
                                            inputArray.length +
                                            " columns into 6.");
      if (inputArray.length > 1) {
        error.append(" First column was " + inputArray[0]);
      }
      m.log(error.toString());
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          responseTableModel.addRow(inputArray[0], inputArray[1], inputArray[2],
                                    inputArray[3], inputArray[4], inputArray[5]);
          // Set the last row to be visible
          responseTable.scrollRectToVisible(responseTable.getCellRect(
            responseTable.getRowCount() - 1, 0, true));
        }
      });

    }
  }

  /**
   * Handle the key typed event from the text field.
   * @param ke KeyEvent
   */
  public void keyTyped(KeyEvent ke) {
    directoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Total Directories to test: " +
                         directoryText.getLineCount() + " "),
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
    popmenu.addSeparator();
    popmenu.add(i5);

    i2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        area.removeRowSelectionInterval(0, area.getRowCount() - 1);
        int[] a = area.getSelectedRows();
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < a.length; i++) {
          TableSorter tb = (TableSorter) area.getModel();
          WebDirectoriesModel wm = (WebDirectoriesModel) tb.getTableModel();
          String row = wm.getRow(a[i]);
          s.append(row);
        }
        JTextArea myTempArea = new JTextArea(s.toString());
        myTempArea.copy();
      }
    });

    i4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        area.selectAll();
      }
    });

    i5.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Browser.init();
        final String url = (String) area.getValueAt(area.getSelectedRow(),
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
    if (session < 10) {
      s += "0";
    }
    s += session;
    return s;
  }
  
  /**
   * Get the value of the check box, allowing the application to
   * continue running even if an error occurs.
   * 
   * @return boolean
   */
  public boolean getCheckBoxValue() {
	  return checkbox;
  }
}
