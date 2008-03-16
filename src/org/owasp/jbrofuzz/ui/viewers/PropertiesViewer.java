/**
 * WindowViewer.java 0.6
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
package org.owasp.jbrofuzz.ui.viewers;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.*;

/**
 * <p>
 * Class extending a JFrame for displaying the contents of each exploit
 * that has been selected.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.8
 */
public class PropertiesViewer extends JFrame {

	private static final long serialVersionUID = 2145212672855056458L;

	/**
	 * <p>Main constructor for the exploit viewer window. This requires
	 * reference to the main JBRFrame, as well as the integer value of 
	 * the exploit passed from the database.</p>
	 * 
	 * @param m
	 * @param display
	 * @param view
	 */
	public PropertiesViewer(final JBroFuzzWindow m, final String header, final String text) {
		super();
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		setTitle(header);

		//	 The container pane
		final Container pane = getContentPane();
		pane.setLayout(null);
		// Define the JPanel
		final JPanel listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		// Set the bounds
		listPanel.setBounds(10, 10, 520, 250);

		//	 The text area
		final JTextArea listTextArea = new JTextArea();
		listTextArea.setFont(new Font("Verdana", Font.BOLD, 12));
		listTextArea.setEditable(false);
		listTextArea.setEditable(false);
		listTextArea.setBackground(Color.BLACK);
		listTextArea.setForeground(Color.WHITE);
		listTextArea.setWrapStyleWord(true);
		listTextArea.setLineWrap(true);
		m.popup(listTextArea);

		final JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(30);
		listTextScrollPane.setPreferredSize(new Dimension(500, 210));
		listPanel.add(listTextScrollPane);
		this.add(listPanel);

		listTextArea.setText(text);
		listTextArea.setCaretPosition(0);

		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" " + header), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));


		//	 Global Frame Issues
		this.setLocation(200, 200);
		this.setSize(550, 300);
		setResizable(false);

		// Don't show the frame unless there is content
		if (listTextArea.getText().length() < 1) {
			setVisible(false);
		} else {
			setVisible(true);
		}
		setDefaultCloseOperation(2);

		listTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PropertiesViewer.this.dispose();
				}
			}
		});
	}
}
