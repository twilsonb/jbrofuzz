/**
 * CheckForUpdates.java 0.6
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

import com.Ostermiller.util.Browser;
/**
 * <p>The JPanel used for generating updates.</p>
 *
 * @author subere (at) uncon org
 * @version 0.7
 */
public class CheckForUpdates extends JDialog {

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
	
	public CheckForUpdates(JFrame parent) {
		super(parent, " Check For Updates ", true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));

		newVersionExists = false;
		
		mainLabel = new JLabel ("<HTML>Select \"Check\" to connect to the JBroFuzz website and check for a newer version</HTML>", ImageCreator.OWASP_IMAGE, JLabel.LEFT);

		this.getContentPane().add(mainLabel, BorderLayout.CENTER);
		

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		
		//	Bottom buttons
		startStop = new JButton("Check");
		startStop.setToolTipText("Check online for a latest version");
		
		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				worker = new SwingWorker3() {
					public Object construct() {
						startUpdate();
						return "check-update-return";
					}

					public void finished() {
						finishUpdate();
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
						stopUpdate();
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

	public void startUpdate() {
		if(!startStop.isEnabled()) {
			return;
		}
		startStop.setText("Stop");
		close.setEnabled(false);
		
		StringBuffer output = new StringBuffer();
		BufferedReader bin = null;
		
		try {
			output.append("<HTML>&nbsp;&nbsp;&nbsp;Checking JBroFuzz Website...&nbsp;&nbsp;");
			mainLabel.setText(output.toString() + "</HTML>");
			
			URL request = new URL(JBRFormat.URL_WEBSITE);
			URLConnection connection = request.openConnection();
			connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");				
			connection.connect();

			output.append("<B>OK</B><BR>&nbsp;&nbsp;&nbsp;Identifying Current Version...&nbsp;&nbsp;");
			mainLabel.setText(output.toString() + "</HTML>");
			
			bin = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line=null;
			while ((line=bin.readLine()) != null) {
				Pattern p = Pattern.compile("Current version is (\\d.\\d)");
				Matcher m = p.matcher(line);
				if (m.find()) {
					String version = m.group();
					output.append("<B>OK</B><BR><BR>&nbsp;&nbsp;&nbsp;Is there a new version available?&nbsp;&nbsp;");
					if(version.equalsIgnoreCase("Current version is " + JBRFormat.VERSION)) {
						output.append("<B>No</B><BR><BR>&nbsp;&nbsp;&nbsp;You are using the latest version of JBroFuzz");
						output.append("<BR>&nbsp;&nbsp;&nbsp;There is no need to update");
					} else {
						output.append("<B>Yes</B><BR><BR>&nbsp;&nbsp;&nbsp;You are using a older version of JBroFuzz");
						output.append("<BR>&nbsp;&nbsp;&nbsp;The " + version);
						newVersionExists = true;
						
					}
					mainLabel.setText(output.toString() + "</HTML>");
				}
			}
			bin.close();
		}
		catch(MalformedURLException e) {
			mainLabel.setText("<HTML><BR>&nbsp;&nbsp;&nbsp;An error occured while attempting to connect<BR><BR>The check for updates was unable to complete</HTML>");
		}
		catch(IOException e) {
			mainLabel.setText("<HTML><BR>&nbsp;&nbsp;&nbsp;An error occured while attempting to connect<BR><BR>The check for updates was unable to complete</HTML>");
		}
		if (bin != null) {
			try {bin.close();} catch (IOException ex) {}
		}

	}
	
	public void stopUpdate() {
		if(!startStop.isEnabled()) {
			return;
		}
		startStop.setText("Check");
		close.setEnabled(true);
	}
	
	public void finishUpdate() {
		if(!startStop.isEnabled()) {
			return;
		}
		close.setEnabled(true);
		
		if(newVersionExists) {
			startStop.setText("Download");
			startStop.removeAll();
			startStop.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Browser.init();
					    try {
					      Browser.displayURL(JBRFormat.URL_WEBSITE);
					    }
					    catch (IOException ex) {
					    	mainLabel.setText("<HTML><BR>&nbsp;&nbsp;&nbsp;An error occured while attempting to connect<BR><BR></HTML>");
					    }
						}
					});       
				}
			});
		} 
		else {
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
	}
}
