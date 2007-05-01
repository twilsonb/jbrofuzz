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
package org.owasp.jbrofuzz.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.owasp.jbrofuzz.io.*;
/**
 * <p>Class extending a JFrame for displaying fuzzed results in a linear 
 * graph.</p>
 *
 * @author subere (at) uncon org
 * @version 0.6
 * @since 0.6
 */
public class WindowPlotter extends JFrame {
	// The main frame window
	private FrameWindow m;
	// The name of the JFrame displayed as a title
	private String name;

	// The x size of the frame
	private static final int x = 750;
	// The y size of the frame
	private static final int y = 450;

	public WindowPlotter(FrameWindow m, String name) {
		super(name);
		this.name = name;
		this.m = m;

		Container pane = getContentPane();
		pane.setLayout(null);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		mainPanel.setBounds(10, 10, x - 20, y - 50);
		
		mainPanel.add(new PlotCanvas(Color.white), BorderLayout.CENTER);
		
		add(mainPanel);
		// Global frame issues
		setLocation(180, 140);
		setSize(x, y);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(2);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					dispose();
				}
			}
		});

		int [] results = FileHandler.readFuzzDirectoryFiles(this.m);
		for(int i = 0; i < results.length; i++) {
			System.out.print(" " + results[i]);
		}
		System.out.println("");
	}


}	// Frame class

/**
 * A canvas which renders a smiley-face to the screen
 *
 * Note: Canvas is a heavyweight superclass, which makes
 * SmileyCanvas also heavyweight.  To convert this class to
 * a lightweight, change "extends Canvas" to "extends Component".
 */
class PlotCanvas extends Canvas {

	public PlotCanvas(Color faceColor) {
		setForeground(faceColor);
	}

	public Dimension getPreferredSize() {
		return new Dimension(700,350);
	}

	/*
	 * Paint when the AWT tells us to...
	 */
	public void paint(Graphics g) {
		// Dynamically calculate size information
		// (the canvas may have been resized externally...)
		Dimension size = getSize();
		int d = Math.min(size.width, size.height); // diameter
		int ed = d/20; // eye diameter
		int x = (size.width - d)/2;
		int y = (size.height - d)/2;

		// draw head (color already set to foreground)
		g.fillOval(x, y, d, d);
		g.setColor(Color.black);
		g.drawOval(x, y, d, d);

		// draw eyes
		g.fillOval(x+d/3-(ed/2), y+d/3-(ed/2), ed, ed);
		g.fillOval(x+(2*(d/3))-(ed/2), y+d/3-(ed/2), ed, ed);

		// draw mouth
		g.drawArc(x+d/4, y+2*(d/5), d/2, d/3, 0, -180);
		// g.fillRect(0, 0, size.width, size.height);
	}
}
