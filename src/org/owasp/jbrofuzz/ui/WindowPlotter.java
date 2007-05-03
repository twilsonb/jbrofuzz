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
	
	import jcckit.GraphicsPlotCanvas;
	import jcckit.data.DataCurve;
	import jcckit.data.DataPlot;
	import jcckit.data.DataPoint;
	import jcckit.util.ConfigParameters;
	import jcckit.util.PropertiesBasedConfigData;
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
			
			int [] input = FileHandler.readFuzzDirectoryFiles(this.m);
			mainPanel.add(new PlotCanvas(input), BorderLayout.CENTER);
			
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
		
		int [] samples = null;
		
		public PlotCanvas(int [] samples) {
			setForeground(Color.white);
			this.samples = samples;
		}
	
		public Dimension getPreferredSize() {
			return new Dimension(700,350);
		}
	
		/*
		 * Paint when the AWT tells us to...
		 */
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Dimension size = getSize();
			// Define the border
			g2.setColor(Color.blue);
			g2.drawRect(0, 0, size.width - 1, size.height - 1);
			// Define the cartesian plane
			g2.setColor(Color.white);
			g2.fillRect(20, 20, size.width - 40, size.height - 40);
			// Put some axes
			g2.setColor(Color.black);
			g2.drawLine(20, 20, 20, size.height - 15);
			g2.drawLine(15, size.height - 20, size.width - 20, size.height - 20);
			int [] a_tr = {20, 15, 25};
			int [] b_tr = {20, 40, 40};
			g2.fillPolygon(a_tr, b_tr, 3);
			int [] s_tr = {size.width - 20, size.width - 40, size.width - 40};
			int [] c_tr = {size.height - 20, size.height - 15, size.height - 25};
			g2.fillPolygon(s_tr, c_tr, 3);
			// Draw the actual points as a bar chart
			int step = (size.width - 40) / samples.length; 
			g2.setColor(Color.blue);
			for(int i = 0; i < samples.length; i++) {
				g2.fillRect(20 + i * step, 20, 10, samples[i] % 2);
			}
			// :)
			g2.setColor(Color.yellow);
			int d = 30; // diameter
			int ed = d/20; // eye diameter
			int x = (size.width - 60);
			int y = d/2 + 10;
			// draw head (color already set to foreground)
			g2.fillOval(x, y, d, d);
			g2.setColor(Color.black);
			g2.drawOval(x, y, d, d);
			// draw eyes
			g2.fillOval(x+d/3-(ed/2), y+d/3-(ed/2), ed, ed);
			g2.fillOval(x+(2*(d/3))-(ed/2), y+d/3-(ed/2), ed, ed);
			// draw mouth
			g2.drawArc(x+d/4, y+2*(d/5), d/2, d/3, 0, -180);
		}
	}
