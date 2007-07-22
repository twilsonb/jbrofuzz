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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker3;
import javax.swing.WindowConstants;

import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBRFormat;
/**
 * <p>The JPanel used for detecting and repairing.</p>
 *
 * @author subere (at) uncon org
 * @version 0.7
 */
public class DetectAndRepair extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -739329653784884215L;
	// The JPanels inside the main window
	private JLabel mainLabel;
	// The start/stop and close button
	private JButton startStop, close;
	// Dimensions of the about box
	private static final int x = 400;
	private static final int y = 300;
	// The Swing Worker used
	private SwingWorker3 worker;
	public DetectAndRepair(final JFrame parent) {
		super(parent, " Detect And Repair ", true);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));

		this.mainLabel = new JLabel ("<HTML>Select \"Detect\" to run a diagnostic check on JBroFuzz</HTML>", ImageCreator.OWASP_IMAGE, SwingConstants.LEFT);

		this.getContentPane().add(this.mainLabel, BorderLayout.CENTER);


		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));

		//	Bottom buttons
		this.startStop = new JButton("Detect");
		this.startStop.setToolTipText("Detect any potential problems with JBroFuzz");

		this.startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				DetectAndRepair.this.worker = new SwingWorker3() {
					@Override
					public Object construct() {
						DetectAndRepair.this.startCheck();
						return "check-repair-return";
					}
					
					@Override
					public void finished() {
						DetectAndRepair.this.finishCheck();
					}
				};
				DetectAndRepair.this.worker.start();
			}
		});
		buttonPanel.add (this.startStop);

		this.close = new JButton("Close");
		this.close.setToolTipText("Close this window");

		this.close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DetectAndRepair.this.stopCheck();
						DetectAndRepair.this.dispose();
					}
				});       
			}
		});
		buttonPanel.add(this.close);

		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		//		Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2) - (DetectAndRepair.x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (DetectAndRepair.y / 2) + 100 ));
		this.setSize(DetectAndRepair.x, DetectAndRepair.y);
		this.setResizable(false);
		this.setVisible(true);		

	}

	private void startCheck() {
		if(!this.startStop.isEnabled()) {
			return;
		}
		this.startStop.setText("Stop");
		this.close.setEnabled(false);

		final StringBuffer output = new StringBuffer();
		output.append("<HTML>&nbsp;&nbsp;&nbsp;Checking for the files that should have come with the distribution...&nbsp;&nbsp;<BR>");
		this.mainLabel.setText(output.toString() + "</HTML>");
		
		output.append("<BR>&nbsp;&nbsp;&nbsp;Checking file&nbsp;" + JBRFormat.FILE_GEN + "...&nbsp;&nbsp;");
		this.mainLabel.setText(output.toString() + "</HTML>");
		
		final File gFile = new File(JBRFormat.FILE_GEN);
		if(gFile.exists()) {
			output.append("<B>Success</B>");
		} else {
			output.append("<B>Failure</B>");
		}
		this.mainLabel.setText(output.toString() + "</HTML>");
		
		output.append("<BR>&nbsp;&nbsp;&nbsp;Checking  file&nbsp;" + JBRFormat.FILE_DIR + "...&nbsp;&nbsp;");
		this.mainLabel.setText(output.toString() + "</HTML>");
		
		final File wFile = new File(JBRFormat.FILE_DIR);
		if(wFile.exists()) {
			output.append("<B>Success</B><BR>");
		} else {
			output.append("<B>Failure</B><BR>");
		}
		this.mainLabel.setText(output.toString() + "</HTML>");
	}

	private void finishCheck() {
		if(!this.startStop.isEnabled()) {
			return;
		}
		this.close.setEnabled(true);
		this.startStop.setText("Finish");
		this.startStop.setEnabled(false);
	}

	private void stopCheck() {
		if(!this.startStop.isEnabled()) {
			return;
		}
		this.close.setEnabled(true);
	}


}


