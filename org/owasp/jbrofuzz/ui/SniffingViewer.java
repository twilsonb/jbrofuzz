/**
 * TCPSniffingViewer.java 0.4
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon org
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * @author subere (at) uncon org
 * @version 0.4
 * @since 0.2
 */
public class SniffingViewer extends JFrame {
  // The name of the request which will be displayed
  private String name;
  // The main sniffing panel
  private SniffingPanel m;
  /**
   * <p>The sniffing viewer that gets launched for each each request within the
   * table of the main sniffing panel. This JFrame does get called by
   * MainSniffingPanel.</p>
   * @param m MainSniffingPanel
   * @param name String
   */
  public SniffingViewer(SniffingPanel m, String name) {
    super();
    this.name = name;
    this.m = m;

    String[] input = name.split(" ");
    String number = input[0] + ".txt";
    setTitle("Sniffing Viewer " + number);
    // The container pane
    Container pane = getContentPane();
    pane.setLayout(null);
    // Define the JPanel
    JPanel listPanel = new JPanel();
    listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    // Set the bounds
    listPanel.setBounds(10, 10, 520, 450);
    // The text area
    JTextArea listTextArea = new JTextArea();
    listTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    listTextArea.setEditable(false);
    listTextArea.setLineWrap(false);
    listTextArea.setWrapStyleWord(false);
    m.getFrameWindow().popup(listTextArea);
    /*
    listTextArea.getDocument().putProperty( DefaultEditorKit.EndOfLineStringProperty, "\r\n" );
    */
    JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
    listTextScrollPane.setVerticalScrollBarPolicy(20);
    listTextScrollPane.setHorizontalScrollBarPolicy(30);
    listTextScrollPane.setPreferredSize(new Dimension(500, 410));
    listPanel.add(listTextScrollPane);

    add(listPanel);

    StringBuffer text = m.getFrameWindow().getJBroFuzz().getFileHandler().
                        readSnifFile(this, number);
    //Find the header
    int headerEnd = text.indexOf("]");
    if ((headerEnd < 0)) {
      headerEnd = 0;
    }
    String header = "";
    if (headerEnd != 0) {
      header = text.substring(0, headerEnd + 1);
      text.delete(0, headerEnd + 2);
    }
    listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(header), BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    listTextArea.setText(text.toString());

    // Global Frame Issues
    setLocation(200, 200);
    setSize(550, 500);
    setResizable(false);
    // Don't show the frame unless there is content
    if (listTextArea.getText().length() < 1) {
      setVisible(false);
    }
    else {
      setVisible(true);
    }
    setDefaultCloseOperation(2);

    listTextArea.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == 27) {
          dispose();
        }
      }
    });
  }
}
