/**
 * DetectAndRepair.java 0.8
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

import javax.swing.BorderFactory;
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
 * <p>
 * The JPanel used for detecting and repairing.
 * </p>
 * 
 * @author subere (at) uncon org
 * @version 0.8
 */
public class DetectAndRepair extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -739329653784884215L;
	// Dimensions of the about box
	private static final int x = 400;
	private static final int y = 300;
	// The JPanels inside the main window
	private JLabel mainLabel;
	// The start/stop and close button
	private JButton startStop, close;
	// The Swing Worker used
	private SwingWorker3 worker;

	public DetectAndRepair(final JFrame parent) {
		super(parent, " Detect And Repair ", true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 10));

		mainLabel = new JLabel(
				"<HTML>Select \"Detect\" to run a diagnostic check on JBroFuzz</HTML>",
				ImageCreator.OWASP_IMAGE, SwingConstants.LEFT);
		mainLabel.setIconTextGap(20);
		mainLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));

		getContentPane().add(mainLabel, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15,
				15));

		// Bottom buttons
		startStop = new JButton("Detect");
		startStop
		.setToolTipText("Detect any potential problems with JBroFuzz");

		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				worker = new SwingWorker3() {
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
				worker.start();
			}
		});
		buttonPanel.add(startStop);

		close = new JButton("Close");
		close.setToolTipText("Close this window");

		close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DetectAndRepair.this.stopCheck();
						DetectAndRepair.this.dispose();
					}
				});
			}
		});
		buttonPanel.add(close);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2)
				- (DetectAndRepair.x / 2 - 100)), Math.abs((parent.getHeight() / 2)
						- (DetectAndRepair.y / 2) + 100));
		this.setSize(DetectAndRepair.x, DetectAndRepair.y);
		setResizable(false);
		setVisible(true);

	}

	private void finishCheck() {
		if (!startStop.isEnabled()) {
			return;
		}
		close.setEnabled(true);
		startStop.setText("Finish");
		startStop.setEnabled(false);
	}

	private void startCheck() {
		if (!startStop.isEnabled()) {
			return;
		}
		startStop.setText("Stop");
		close.setEnabled(false);

		final StringBuffer output = new StringBuffer();
		output.append("<HTML>Checking for the files that should have come with the distribution...&nbsp;&nbsp;<BR>");
		mainLabel.setText(output.toString() + "</HTML>");

		int check = 0;

		
		output.append("<BR>&nbsp;&nbsp;&nbsp;Checking file&nbsp;"
				+ JBRFormat.FILE_GEN + "...&nbsp;&nbsp;");
		mainLabel.setText(output.toString() + "</HTML>");
		// Check for first file
		final File gFile = new File(JBRFormat.FILE_GEN);
		if (gFile.exists()) {
			output.append("<B>Success</B>");
		} else {
			output.append("<B>Failure</B>");
			check++;
		}
		mainLabel.setText(output.toString() + "</HTML>");

		output.append("<BR>&nbsp;&nbsp;&nbsp;Checking  file&nbsp;"
				+ JBRFormat.FILE_DIR + "...&nbsp;&nbsp;");
		mainLabel.setText(output.toString() + "</HTML>");

		// Check for second file
		final File wFile = new File(JBRFormat.FILE_DIR);
		if (wFile.exists()) {
			output.append("<B>Success</B><BR>");
		} else {
			output.append("<B>Failure</B><BR>");
			check++;
		}
		
		// Display action, according to success or failure
		if (check == 0) {
			output
			.append("<BR>Both files were found within the directory that JBroFuzz was launched.<BR>");
			/*
			output
			.append("<BR>You can modify these files appropriately, to load your own data.<BR>");
			output
			.append("<BR>The application would have to be restarted to load the data from file.<BR>");
			*/
		} else {
			output
			.append("<BR>The corresponding files were not found within the directory that JBroFuzz was launched.<BR>");
			/*
			output
			.append("<BR>This limits your ability to modify these files in order to load your own data.<BR>");
			output.append("<BR>The application will still run normally.<BR>");
			*/
		}
		mainLabel.setText(output.toString() + "</HTML>");
	}

	private void stopCheck() {
		if (!startStop.isEnabled()) {
			return;
		}
		close.setEnabled(true);
	}

}
