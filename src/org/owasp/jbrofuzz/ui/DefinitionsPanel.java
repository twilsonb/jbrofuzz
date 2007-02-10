/**
 * DefinitionsPanel.java 0.4
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

import javax.swing.*;

/**
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public class DefinitionsPanel extends JPanel {
  // The frame that the sniffing panel is attached
  private FrameWindow m;
  // The JTable that holds all the data
  private JTextArea listTextArea;
  /**
   * Constructor for the Definitions Panel of the represented as a tab. Only a
   * single instance of this class is constructed.
   *
   * @param m FrameWindow
   */
  public DefinitionsPanel(FrameWindow m) {
    super();
    setLayout(null);
    /*
         setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Generators "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
     */
    this.m = m;
    // Define the JPanel
    JPanel listPanel = new JPanel();

    listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Fuzzing Generators "),
                        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    // Set the bounds
    listPanel.setBounds(10, 90, 870, 370);

    listTextArea = new JTextArea();
    listTextArea.setFont(new Font("Verdana", Font.PLAIN, 13));
    listTextArea.setEditable(false);
    listTextArea.setLineWrap(true);
    listTextArea.setWrapStyleWord(true);
    getFrameWindow().popup(listTextArea);

    JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
    listTextScrollPane.setVerticalScrollBarPolicy(20);
    listTextScrollPane.setHorizontalScrollBarPolicy(31);
    listTextScrollPane.setPreferredSize(new Dimension(860, 340));
    listPanel.add(listTextScrollPane);

    add(listPanel);
    listTextArea.setCaretPosition(0);
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
   * <p>Method for setting the text within the JTextArea displayed as part of
   * this panel. This method simply appends any string given adding a new
   * line (\n) to the end of it.</p>
   *
   * @param str String
   */
  public void setDefinitionsText(String str) {
    listTextArea.append(str + "\n");
  }
}
