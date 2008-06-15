/**
 * JBroFuzz 1.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
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
 * 
 */
package org.owasp.jbrofuzz.ui.viewers;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.panels.*;
import org.owasp.jbrofuzz.util.*;

/**
 * <p>
 * Class extending a JFrame for displaying the contents of each exploit that has
 * been selected.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 */
public class PropertiesViewer extends JFrame {

	private static final long serialVersionUID = 2145212672855056458L;

	/**
	 * <p>
	 * Main constructor for the exploit viewer window. This requires reference
	 * to the main JBRFrame, as well as the integer value of the exploit passed
	 * from the database.
	 * </p>
	 * 
	 * @param parent
	 * @param display
	 * @param view
	 */
	public PropertiesViewer(final JBroFuzzPanel parent, final String header,
			final String text) {
		super("JBroFuzz - " + header);
		setIconImage(ImageCreator.FRAME_IMG.getImage());

		// The Container Pane
		final Container pane = getContentPane();
		pane.setLayout(null);

		// Define the JPanel
		final JPanel listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1,
				1, 1)));
		listPanel.setBounds(10, 10, 520, 250);
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" " + header + " "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));

		// Define the Text Area
		final JTextArea listTextArea = new JTextArea();
		listTextArea.setFont(new Font("Verdana", Font.BOLD, 12));
		listTextArea.setEditable(false);
		listTextArea.setEditable(false);
		listTextArea.setBackground(Color.BLACK);
		listTextArea.setForeground(Color.WHITE);
		listTextArea.setWrapStyleWord(true);
		listTextArea.setLineWrap(true);
		parent.popupText(listTextArea);

		// Define the Scroll Area for the Text
		final JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(30);
		listTextScrollPane.setPreferredSize(new Dimension(500, 210));

		// Define the Progress Bar
		final JProgressBar progressBar = new JProgressBar(0, text.length());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBounds(410, 265, 120, 20);

		// Global Frame Issues
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math
				.abs(parent.getLocation().y + 100));
		this.setSize(550, 325);
		this.add(listPanel);
		this.add(progressBar);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(2);

		/*
		 * // Don't show the frame unless there is content if
		 * (listTextArea.getText().length() < 1) { setVisible(false); } else {
		 * setVisible(true); }
		 */

		listTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					PropertiesViewer.this.dispose();
				}
			}
		});

		SwingWorker3 worker = new SwingWorker3() {

			@Override
			public Object construct() {
				int n = 0;
				while (n < text.length()) {
					listTextArea.append("" + text.charAt(n));
					progressBar.setValue(n);
					n++;
				}
				progressBar.setValue(n);
				return "return-worker";
			}

		};
		worker.start();

		listTextArea.setCaretPosition(0);
		listPanel.add(listTextScrollPane);

	}

}
