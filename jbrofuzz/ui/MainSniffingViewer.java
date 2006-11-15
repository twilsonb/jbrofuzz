/**
 * MainSniffingViewer.java
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2006 yns000 (at) users. sourceforge. net
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
package jbrofuzz.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * @author subere@uncon.org
 * @version 0.2
 * @since 0.2
 */
public class MainSniffingViewer
    extends JFrame {
  // The name of the request which will be displayed
  private String name;
  // The main sniffing panel
  private MainSniffingPanel m;
  /**
   * <p>The sniffing viewer that gets launched for each each request within the
   * table of the main sniffing panel. This JFrame does get called by
   * MainSniffingPanel.</p>
   * @param m MainSniffingPanel
   * @param name String
   */
  public MainSniffingViewer(MainSniffingPanel m, String name) {
    super();
    this.name = name;
    this.m = m;

    String [] input = name.split(" ");
    String number = input[0] + ".txt";
    setTitle("Sniffing Viewer " + number);
    // The container pane
    Container pane = getContentPane();
    pane.setLayout(null);
    // Define the JPanel
    JPanel listPanel = new JPanel();
    listPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(""),
        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    // Set the bounds
    listPanel.setBounds(10, 10, 520, 450);
    // The text area
    JTextArea listTextArea = new JTextArea();
    listTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    listTextArea.setEditable(false);
    listTextArea.setLineWrap(false);
    listTextArea.setWrapStyleWord(false);
    JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
    listTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.
                                                  VERTICAL_SCROLLBAR_AS_NEEDED);
    listTextScrollPane.setHorizontalScrollBarPolicy(JScrollPane.
        HORIZONTAL_SCROLLBAR_AS_NEEDED);
    listTextScrollPane.setPreferredSize(new Dimension(510, 430));
    listPanel.add(listTextScrollPane);

    add(listPanel);

    StringBuffer text = m.getMainWindow().
        getJBroFuzz().getFileHandler().readSnifFile(this, number);
    listTextArea.setText(text.toString());

    // Global Frame Issues
    setLocation(200, 200);
    setSize(550, 500);
    setResizable(false);
    // Don't show the frame unless there is content
    if(listTextArea.getText().length() < 1) {
      setVisible(false);
    }
    else {
      setVisible(true);
    }
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }
}
