/**
 * DefinitionsPanel.java 0.6
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

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.owasp.jbrofuzz.ui.JBRFrame;

/**
 * <p>The definitions panel holding a description of the generators 
 * loaded.</p>
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class Generators extends JPanel {
  /**
	 * 
	 */
	private static final long serialVersionUID = -5848591307104017542L;
	// The frame that the sniffing panel is attached
  private JBRFrame m;
  // The JTable that holds all the data
  private JTextArea listTextArea;
  /**
   * Constructor for the Definitions Panel of the represented as a tab. Only a
   * single instance of this class is constructed.
   *
   * @param m FrameWindow
   */
  public Generators(final JBRFrame m) {
    super();
    this.setLayout(null);
    /*
         setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Generators "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
     */
    this.m = m;
    // Define the JPanel
    final JPanel listPanel = new JPanel();

    listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Fuzzing Generators "),
                        BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    // Set the bounds
    listPanel.setBounds(10, 80, 870, 370);

    this.listTextArea = new JTextArea();
    this.listTextArea.setFont(new Font("Verdana", Font.PLAIN, 13));
    this.listTextArea.setEditable(false);
    this.listTextArea.setLineWrap(true);
    this.listTextArea.setWrapStyleWord(true);
    this.getFrameWindow().popup(this.listTextArea);

    final JScrollPane listTextScrollPane = new JScrollPane(this.listTextArea);
    listTextScrollPane.setVerticalScrollBarPolicy(20);
    listTextScrollPane.setHorizontalScrollBarPolicy(31);
    listTextScrollPane.setPreferredSize(new Dimension(850, 330));
    listPanel.add(listTextScrollPane);

    this.add(listPanel);
    this.listTextArea.setCaretPosition(0);
  }

  /**
   * <p>Method for returning the main window frame that this tab is attached on.
   * </p>
   *
   * @return Window
   */
  public JBRFrame getFrameWindow() {
    return this.m;
  }

  /**
   * <p>Method for setting the text within the JTextArea displayed as part of
   * this panel. This method simply appends any string given adding a new
   * line (\n) to the end of it.</p>
   *
   * @param str String
   */
  public void setDefinitionsText(final String str) {
    this.listTextArea.append(str + "\n");
  }
}
