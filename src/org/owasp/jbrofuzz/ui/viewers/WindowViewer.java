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

import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.util.ImageCreator;

/**
 * <p>Class extending a JFrame for displaying the contents of 
 * each TCP sniffing
 * request/reply that has been made.</p>
 *
 * @author subere (at) uncon org
 * @version 0.6
 * @since 0.2
 */
public class WindowViewer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8855212505709053158L;

	/**
	 * <p>Constant used for specifying within which directory to look for the 
	 * corresponding file. Using this value will point to the sniffing directory
	 * used for the corresponding session.</p>
	 */

	public static final int VIEW_SNIFFING_PANEL = 1;
	/**
	 * <p>Constant used for specifying wihtin which directory to look for the
	 * corresponding file. Using this value will point to the fuzzing directory
	 * used for the correspondng session.</p>
	 */
	public static final int VIEW_FUZZING_PANEL = 2;

	/**
	 * <p>The window viewer that gets launched for each request within 
	 * the corresponding panel.</p>
	 * 
	 * @param m FrameWindow
	 * @param name String
	 */
	public WindowViewer(final JBRFrame m, final String name, final int typeOfPanel) {
		super();
		this.setIconImage(ImageCreator.FRAME_IMG.getImage());

		final String[] input = name.split(" ");
		final String number = input[0] + ".html";
		this.setTitle("Window Viewer " + number);

		// The container pane
		final Container pane = this.getContentPane();
		pane.setLayout(null);
		// Define the JPanel
		final JPanel listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		// Set the bounds
		listPanel.setBounds(10, 10, 520, 450);
		// The text area
		final JTextArea listTextArea = new JTextArea();
		listTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		listTextArea.setEditable(false);
		listTextArea.setLineWrap(false);
		listTextArea.setWrapStyleWord(false);
		m.popup(listTextArea);

		final JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(30);
		listTextScrollPane.setPreferredSize(new Dimension(500, 410));
		listPanel.add(listTextScrollPane);

		this.add(listPanel);

		StringBuffer text = new StringBuffer();
		if(typeOfPanel == WindowViewer.VIEW_SNIFFING_PANEL) {
			text = FileHandler.readSnifFile( number );
		}
		if(typeOfPanel == WindowViewer.VIEW_FUZZING_PANEL) {
			text = FileHandler.readFuzzFile( number );
		}
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
		this.setLocation(200, 200);
		this.setSize(550, 500);
		this.setResizable(false);
		// Don't show the frame unless there is content
		if (listTextArea.getText().length() < 1) {
			this.setVisible(false);
		}
		else {
			this.setVisible(true);
		}
		this.setDefaultCloseOperation(2);

		listTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					WindowViewer.this.dispose();
				}
			}
		});
	}

} // Frame class
