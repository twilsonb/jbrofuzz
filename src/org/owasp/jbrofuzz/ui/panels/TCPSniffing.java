/**
 * TCPSniffingPanel.java 0.6
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker3;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.snif.tcp.ConnectionListener;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.tablemodels.SniffingTableModel;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;

import com.Ostermiller.util.Browser;

/**
 * <p>The Sniffing Panel User Interface based on the instance 
 * of a JPanel. This
 * class constructs the entire panel for the "TCP Sniffing" tab 
 * as seen by the
 * user in the main frame.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class TCPSniffing extends JPanel {

  /**
	 * 
	 */
	private static final long serialVersionUID = 2185868982471609733L;
	private final JTextField rHostText, rPortText, lHostText, lPortText;
  // The buttons to start and stop the listener, as well as launch a browser
  private final JButton startButton, stopButton, browserButton;
  //
  private final JTable sniffingTable;
  //
  private JPanel listPanel;
  // A counter for the number of times start has been clicked
  private int counter, session;
  // The swing worker used when the button "start" is pressed
  private SwingWorker3 worker;
  // The frame that the sniffing panel is attached
  private JBRFrame m;
  // The table model
  private SniffingTableModel tableModel;
  // The boolean if stop has been pressed
  private boolean stopPressed;
  // The TCP Connection listener
  private ConnectionListener reflector;
  // The window viewer frame
  private WindowViewer vew;

  /**
   * <p>Main Constructor for the Sniffing Panel, passing the Main Window
   * (JFrame) as part of the constructor argument.</p>
   *
   * @param m MainWindow
   */
  public TCPSniffing(final JBRFrame m) {
    super();
    this.setLayout(null);
    this.m = m;
    // Set the counter to zero
    this.counter = 0;
    this.session = 0;
    // Define the JPanels
    final JPanel rHostPanel = new JPanel();
    final JPanel rPortPanel = new JPanel();
    final JPanel lHostPanel = new JPanel();
    final JPanel lPortPanel = new JPanel();
    this.listPanel = new JPanel();
    // Set the borders
    rHostPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Remote Host "),
                         BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    rPortPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    lHostPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Local Host "),
                         BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    lPortPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    this.listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" List of Requests "),
                        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    // Set the bounds
    rHostPanel.setBounds(10, 20, 220, 60);
    rPortPanel.setBounds(230, 20, 60, 60);
    lHostPanel.setBounds(300, 20, 220, 60);
    lPortPanel.setBounds(520, 20, 60, 60);
    this.listPanel.setBounds(10, 90, 870, 360);
    // Setup the remote host text
    this.rHostText = new JTextField();
    this.rHostText.setEditable(true);
    this.rHostText.setFont(new Font("Verdana", Font.BOLD, 12));
    this.rHostText.setMargin(new Insets(1, 1, 1, 1));
    this.getFrameWindow().popup(this.rHostText);
    this.rHostText.setPreferredSize(new Dimension(200, 20));
    rHostPanel.add(this.rHostText);
    // Setup the remote host port
    this.rPortText = new JFormattedTextField();
    this.rPortText.setEditable(true);
    this.rPortText.setFont(new Font("Verdana", Font.BOLD, 12));
    // rPortText.setLineWrap(false);
    // rPortText.setWrapStyleWord(true);
    this.rPortText.setMargin(new Insets(1, 1, 1, 1));
    this.getFrameWindow().popup(this.rPortText);
    this.rPortText.setPreferredSize(new Dimension(50, 20));
    rPortPanel.add(this.rPortText);
    // Setup the local host text
    this.lHostText = new JTextField();
    this.lHostText.setEditable(true);
    this.lHostText.setFont(new Font("Verdana", Font.BOLD, 12));
    // lHostText.setLineWrap(false);
    // lHostText.setWrapStyleWord(true);
    this.lHostText.setMargin(new Insets(1, 1, 1, 1));
    this.getFrameWindow().popup(this.lHostText);
    this.lHostText.setPreferredSize(new Dimension(200, 20));
    lHostPanel.add(this.lHostText);
    // Setup the local port text
    this.lPortText = new JFormattedTextField();
    this.lPortText.setEditable(true);
    this.lPortText.setFont(new Font("Verdana", Font.BOLD, 12));
    //lPortText.setLineWrap(false);
    // lPortText.setWrapStyleWord(true);
    this.lPortText.setMargin(new Insets(1, 1, 1, 1));
    this.getFrameWindow().popup(this.lPortText);
    this.lPortText.setPreferredSize(new Dimension(50, 20));
    lPortPanel.add(this.lPortText);
    // The table of list of requests text
    this.tableModel = new SniffingTableModel();

    this.sniffingTable = new JTable();
    this.sniffingTable.setModel(this.tableModel);
    this.sniffingTable.setFont(new Font("Monospaced", Font.BOLD, 12));
    this.sniffingTable.setBackground(Color.black);
    this.sniffingTable.setForeground(Color.white);
    this.sniffingTable.setSurrendersFocusOnKeystroke(true);
    this.sniffingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    final JScrollPane listTextScrollPane = new JScrollPane(this.sniffingTable);
    listTextScrollPane.setVerticalScrollBarPolicy(20);
    listTextScrollPane.setHorizontalScrollBarPolicy(31);
    listTextScrollPane.setPreferredSize(new Dimension(850, 320));
    listTextScrollPane.setWheelScrollingEnabled(true);
    this.listPanel.add(listTextScrollPane);
    // Add the action listener for each row
    final ListSelectionModel rowSM = this.sniffingTable.getSelectionModel();

    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(final ListSelectionEvent e) {
        //Ignore extra messages
        if (e.getValueIsAdjusting()) {
          return;
        }
        final ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty()) {
          // No rows selected
        }
        else {
          final int selectedRow = lsm.getMinSelectionIndex();
          final String s = TCPSniffing.this.tableModel.getValueAt(selectedRow);
          TCPSniffing.this.vew = new WindowViewer(TCPSniffing.this.getFrameWindow(), s, WindowViewer.VIEW_SNIFFING_PANEL);
        }
      }
    }); // ListSelectionListener

    // The start, stop buttons
    this.startButton = new JButton("Start", ImageCreator.START_IMG);
    this.startButton.setBounds(590, 33, 90, 40);
    this.startButton.setEnabled(true);
    this.startButton.setToolTipText("Start Sniffing between Local and Remote Host");
    this.stopButton = new JButton("Stop", ImageCreator.STOP_IMG);
    this.stopButton.setEnabled(false);
    this.stopButton.setBounds(690, 33, 90, 40);
    this.stopButton.setToolTipText("Stop Sniffing");
    this.browserButton = new JButton("Bro", ImageCreator.PAUSE_IMG);
    this.browserButton.setBounds(790, 33, 80, 40);
    this.browserButton.setEnabled(true);
    this.browserButton.setToolTipText("Open in external browser");
    // The action listener for the start button
    this.startButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        // Worker, working...
        TCPSniffing.this.worker = new SwingWorker3() {
          @Override
					public Object construct() {
            TCPSniffing.this.buttonStart();
            return "start-window-return";
          }

          @Override
					public void finished() {
          }
        };
        TCPSniffing.this.worker.start();
      }
    });
    // The action listener for the stop button
    this.stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        TCPSniffing.this.buttonStop();
      }
    });
    // The action listener for the browser button
    this.browserButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
      	TCPSniffing.this.buttonBro();
      }
    });

    // Add the panels
    this.add(rHostPanel);
    this.add(rPortPanel);
    this.add(lHostPanel);
    this.add(lPortPanel);
    this.add(this.startButton);
    this.add(this.stopButton);
    this.add(this.browserButton);
    this.add(this.listPanel);

    // Some default values
    this.rHostText.setText("www.sourceforge.net");
    this.rPortText.setText("80");
    this.lHostText.setText("127.0.0.1");
    this.lPortText.setText("6161");
  }

  /**
   * Method for accessing the Remote Host port field and retrieving it's value.
   *
   * @return String
   */
  public String getRemotePortText() {
    return this.rPortText.getText();
  }

  /**
   * Method for accessing the Local Host port field and retrieving it's value.
   * @return String
   */
  public String getLocalPortText() {
    return this.lPortText.getText();
  }

  /**
   * Method for accessing the Local Host text field and retrieving it's value.
   * This method performs sanitisation attempting to remove protocol as well as
   * trailing slashes.
   *
   * @return String
   */
  public String getLocalHostText() {
    String text = this.lHostText.getText();
    int len = text.length();

    if (text.startsWith("ftp://")) {
      text = text.substring(6, len);
      len = text.length();
      this.lHostText.setText(text);
    }
    if (text.startsWith("http://")) {
      text = text.substring(7, len);
      len = text.length();
      this.lHostText.setText(text);
    }
    if (text.startsWith("https://")) {
      text = text.substring(8, len);
      len = text.length();
      this.lHostText.setText(text);
    }
    if (text.endsWith("/")) {
      text = text.substring(0, len - 1);
      // If another if statement is included, update the len variable here
      this.lHostText.setText(text);
    }
    return text;
  }

  /**
   * Method for accessing the Remote Host text field and retrieving it's value.
   * This method performs sanitisation attempting to remove protocol as well as
   * trailing slashes.
   *
   * @return String
   */
  public String getRemoteHostText() {
    String text = this.rHostText.getText();
    int len = text.length();

    if (text.startsWith("ftp://")) {
      text = text.substring(6, len);
      len = text.length();
      this.rHostText.setText(text);
    }
    if (text.startsWith("http://")) {
      text = text.substring(7, len);
      len = text.length();
      this.rHostText.setText(text);
    }
    if (text.startsWith("https://")) {
      text = text.substring(8, len);
      len = text.length();
      this.rHostText.setText(text);
    }
    if (text.endsWith("/")) {
      text = text.substring(0, len - 1);
      // If another if statement is included, update the len variable here
      this.rHostText.setText(text);
    }
    return text;
  }

  /**
   * <p>Return the status of whether or not the stop button has been pressed.</p>
   * @return boolean
   */
  public boolean getStopStatus() {
    return this.stopPressed;
  }

  /**
   * <p>Method for adding an extra row to the requests / replies JTable
   * @param s String
   */
  public void addRow(final String s) {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        TCPSniffing.this.tableModel.addEmptyRow();
        final int totalRows = TCPSniffing.this.tableModel.getRowCount();
        TCPSniffing.this.tableModel.setValueAt(s, totalRows - 1, 0);
        // Set the last row to be visible
        TCPSniffing.this.sniffingTable.scrollRectToVisible(TCPSniffing.this.sniffingTable.getCellRect(TCPSniffing.this.sniffingTable.
          getRowCount(), 0, true));
      }
    });
  }

  /**
   * <p>Method for returning the MainWindow attached to the current Sniffing
   * Panel.</p>
   * @return MainWindow
   */
  public JBRFrame getFrameWindow() {
    return this.m;
  }

  /**
   * Access the main object that launches and is responsible for the application.
   * @return JBroFuzz
   */
  public JBroFuzz getJBroFuzz() {
    return this.m.getJBroFuzz();
  }


  /**
   * <p>Method for returning the counter held within the Sniffing Panel which
   * is responsible for counting the number of requests having been made. This
   * method is used for generating unique sequential file name and row
   * counts.</p>
   * @return String
   */
  public String getCounter() {
    if (this.counter < 0) {
      this.counter = 1;
    }
    if (this.session < 0) {
      this.session = 1;
    }
    if (this.session > 10) {
      this.session = 1;
    }
    String s = "" + this.session;
    if (this.counter < 100000) {
      s += "0";
    }
    if (this.counter < 10000) {
      s += "0";
    }
    if (this.counter < 1000) {
      s += "0";
    }
    if (this.counter < 100) {
      s += "0";
    }
    if (this.counter < 10) {
      s += "0";
    }
    s += "" + this.counter;
    this.counter++;
    return s;
  }

  /**
   * Method for hitting the stop button.
   */
  public void buttonStop() {
    if (!this.stopButton.isEnabled()) {
      return;
    }
    this.stopButton.setEnabled(false);
    this.startButton.setEnabled(true);
    if (this.reflector != null) {
      this.reflector.stopConnection();
    }

    this.rHostText.setEditable(true);
    this.rPortText.setEditable(true);
    this.lHostText.setEditable(true);
    this.lPortText.setEditable(true);

    this.rHostText.setBackground(Color.white);
    this.rPortText.setBackground(Color.white);
    this.lHostText.setBackground(Color.white);
    this.lPortText.setBackground(Color.white);

    this.rHostText.setForeground(Color.black);
    this.rPortText.setForeground(Color.black);
    this.lHostText.setForeground(Color.black);
    this.lPortText.setForeground(Color.black);

    // Update the border of the output panel
    if (this.counter == 0) {
      this.listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
        createTitledBorder(" List of Requests " + "[Last log was empty] "),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    }
    else {
      this.listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
        createTitledBorder(" List of Requests " + "[Last log was .\\" +
                           this.getJBroFuzz().getFormat().getDate() + "\\" + this.session + "*.txt] "),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    }
  }
  
  public void buttonBro() {
    Browser.init();
    final StringBuffer url = new StringBuffer();
    if (this.getRemotePortText().equals("443")) {
      url.append("https://");
    }
    else {
      url.append("http://");
    }
    url.append(this.getLocalHostText());
    url.append(":");
    url.append(this.getLocalPortText());
    try {
      Browser.displayURL(url.toString());
    }
    catch (final IOException ex) {
      this.getFrameWindow().log("Could not launch link in external browser");
    }
  }

  /**
   * Method for hitting the start button.
   */
  public void buttonStart() {
    if (!this.startButton.isEnabled()) {
      return;
    }
    this.startButton.setEnabled(false);
    this.stopButton.setEnabled(true);
    this.session++;

    this.rHostText.setEditable(false);
    this.rPortText.setEditable(false);
    this.lHostText.setEditable(false);
    this.lPortText.setEditable(false);

    this.rHostText.setBackground(Color.black);
    this.rPortText.setBackground(Color.black);
    this.lHostText.setBackground(Color.black);
    this.lPortText.setBackground(Color.black);

    this.rHostText.setForeground(Color.white);
    this.rPortText.setForeground(Color.white);
    this.lHostText.setForeground(Color.white);
    this.lPortText.setForeground(Color.white);

    final String rh = this.getRemoteHostText();
    final String rp = this.getRemotePortText();
    final String lh = this.getLocalHostText();
    final String lp = this.getLocalPortText();

    // Update the border of the output panel
    this.listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" List of Requests " + "[Logging in folder .\\" +
      		this.getJBroFuzz().getFormat().getDate() + "\\" + this.session + "*.txt]  [" + rh + ":" +
                         rp + " <=> " + lh + ":" + lp + "] "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    this.reflector = new ConnectionListener(this, rh, rp, lh, lp);
    this.reflector.start();
    this.stopPressed = false;
  }
}
