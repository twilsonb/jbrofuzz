/**
 * DetectAndRepair.java 0.7
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
package org.owasp.jbrofuzz.ui.menu;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.regex.*;
import java.net.*;

import javax.swing.*;
import javax.swing.text.*;

import org.owasp.jbrofuzz.version.JBRFormat;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.io.*;

import com.Ostermiller.util.Browser;
/**
 * <p>The JPanel used for detecting and repairing.</p>
 *
 * @author subere (at) uncon org
 * @version 0.7
 */
public class DetectAndRepair extends JDialog {

	// The JPanels inside the main window
	private JLabel mainLabel;
	// The start/stop and close button
	private JButton startStop, close;
	// Dimensions of the about box
	private static final int x = 400;
	private static final int y = 300;
	// The Swing Worker used
	private SwingWorker3 worker;
	// The boolean checking for a new version
	private boolean newVersionExists;

	public DetectAndRepair(JFrame parent) {
		super(parent, " Detect And Repair ", true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));

		newVersionExists = false;

		mainLabel = new JLabel ("<HTML>Select \"Detect\" to run a diagnostic check on JBroFuzz</HTML>", ImageCreator.OWASP_IMAGE, JLabel.LEFT);

		this.getContentPane().add(mainLabel, BorderLayout.CENTER);


		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));

		//	Bottom buttons
		startStop = new JButton("Detect");
		startStop.setToolTipText("Detect any potential problems with JBroFuzz");

		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				worker = new SwingWorker3() {
					public Object construct() {
						startCheck();
						return "check-repair-return";
					}

					public void finished() {
						finishCheck();
					}
				};
				worker.start();
			}
		});
		buttonPanel.add (startStop);

		close = new JButton("Close");
		close.setToolTipText("Close this window");

		close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						stopCheck();
						dispose();
					}
				});       
			}
		});
		buttonPanel.add(close);

		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		//		Global frame issues
		setLocation(Math.abs((parent.getWidth() / 2) - (x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (y / 2) + 100 ));
		setSize(x, y);
		setResizable(false);
		setVisible(true);		

	}

	private void startCheck() {
		if(!startStop.isEnabled()) {
			return;
		}
		startStop.setText("Stop");
		close.setEnabled(false);

		StringBuffer output = new StringBuffer();
		output.append("<HTML>&nbsp;&nbsp;&nbsp;Checking for necessary files...&nbsp;&nbsp;<BR>");
		mainLabel.setText(output.toString() + "</HTML>");
		
		output.append("<BR>&nbsp;&nbsp;&nbsp;Checking file&nbsp;" + JBRFormat.FILE_GEN + "...&nbsp;&nbsp;");
		mainLabel.setText(output.toString() + "</HTML>");
		
		File gFile = new File(JBRFormat.FILE_GEN);
		if(gFile.exists()) {
			output.append("<B>Success</B>");
		} else {
			output.append("<B>Failure</B>");
		}
		mainLabel.setText(output.toString() + "</HTML>");
		
		output.append("<BR>&nbsp;&nbsp;&nbsp;Checking  file&nbsp;" + JBRFormat.FILE_DIR + "...&nbsp;&nbsp;");
		mainLabel.setText(output.toString() + "</HTML>");
		
		File wFile = new File(JBRFormat.FILE_DIR);
		if(wFile.exists()) {
			output.append("<B>Success</B><BR>");
		} else {
			output.append("<B>Failure</B><BR>");
		}
		mainLabel.setText(output.toString() + "</HTML>");
	}

	private void finishCheck() {
		if(!startStop.isEnabled()) {
			return;
		}
		close.setEnabled(true);

		startStop.setText("Finish");
		startStop.removeAll();
		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						startStop.setEnabled(false);
					}
				});       
			}
		});	
	}

	private void stopCheck() {
		if(!startStop.isEnabled()) {
			return;
		}
		close.setEnabled(true);
	}


}


