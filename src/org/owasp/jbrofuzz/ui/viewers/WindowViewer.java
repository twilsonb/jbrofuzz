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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.text.*;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.SwingWorker3;
import org.owasp.jbrofuzz.util.TextHighlighter;

/**
 * <p>
 * Class extending a JFrame for displaying the contents of each TCP sniffing
 * request/reply that has been made.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 0.8
 * @since 0.2
 */
public class WindowViewer extends JFrame {

	private static final long serialVersionUID = 8155212112319053158L;

	/**
	 * <p>
	 * Constant used for specifying within which directory to look for the
	 * corresponding file. Using this value will point to the sniffing directory
	 * used for the corresponding session.
	 * </p>
	 */

	public static final int VIEW_SNIFFING_PANEL = 1;

	/**
	 * <p>
	 * Constant used for specifying within which directory to look for the
	 * corresponding file. Using this value will point to the fuzzing directory
	 * used for the correspondng session.
	 * </p>
	 */
	public static final int VIEW_FUZZING_PANEL = 2;


	/**
	 * <p>The window viewer that gets launched for each request within the
	 * corresponding panel.</p>
	 * 
	 * @param parent
	 * @param name
	 * @param typeOfPanel
	 */
	public WindowViewer(final JBroFuzzWindow parent, final String name, final int typeOfPanel) {
		super("JBroFuzz - File Viewer - " + name + ".html");
		setIconImage(ImageCreator.FRAME_IMG.getImage());

		// The Container Pane
		final Container pane = getContentPane();
		pane.setLayout(null);

		// Define the Panel
		final JPanel listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		listPanel.setBounds(10, 10, 520, 450);

		// Define the Text Area
		final JTextArea listTextArea = new JTextArea();
		listTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		listTextArea.setEditable(false);
		parent.popup(listTextArea);
		/*
		listTextArea.setEditorKit(new StyledEditorKit() {
	
			private static final long serialVersionUID = -6885612347033981164L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}
		});
		*/

		// Define the Scroll Pane for the Text Area
		final JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(30);
		listTextScrollPane.setPreferredSize(new Dimension(500, 410));

		StringBuffer textBuffer = new StringBuffer();
		if (typeOfPanel == WindowViewer.VIEW_SNIFFING_PANEL) {
			textBuffer = parent.getJBroFuzz().getHandler().readSnifFile(name + ".html");
		}
		if (typeOfPanel == WindowViewer.VIEW_FUZZING_PANEL) {
			textBuffer = parent.getJBroFuzz().getHandler().readFuzzFile(name + ".html");
		}
		final String text = textBuffer.toString();

		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(name + ".html"), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));

		listTextArea.setText(text.toString());
		listTextArea.setCaretPosition(0);

		// Define the Progress Bar
		final JProgressBar progressBar = new JProgressBar(0, text.length());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBounds(410, 465, 120, 20);

		// Global Frame Issues
		this.add(listPanel);
		this.add(progressBar);
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math.abs(parent.getLocation().y + 100));
		this.setSize(550, 525);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(2);

		
		listTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					WindowViewer.this.dispose();
				}
			}
		});
		 

		SwingWorker3 worker = new SwingWorker3() {

			public Object construct() {
				int n = 0; 
				
				while(n < text.toString().length()) {
					listTextArea.append("" + text.toString().charAt(n));
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

} // Frame class
