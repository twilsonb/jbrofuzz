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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.Ostermiller.util.Browser;
/**
 * <p>The JPanel used for generating updates.</p>
 *
 * @author subere (at) uncon org
 * @version 0.7
 */
public class CheckForUpdates extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5651438626206098934L;
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
	
	public CheckForUpdates(final JFrame parent) {
		super(parent, " Check For Updates ", true);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));

		this.newVersionExists = false;
		
		this.mainLabel = new JLabel ("<HTML>Select \"Check\" to connect to the JBroFuzz website and check for a newer version</HTML>", ImageCreator.OWASP_IMAGE, SwingConstants.LEFT);

		this.getContentPane().add(this.mainLabel, BorderLayout.CENTER);
		

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		
		//	Bottom buttons
		this.startStop = new JButton("Check");
		this.startStop.setToolTipText("Check online for a latest version");
		
		this.startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				CheckForUpdates.this.worker = new SwingWorker3() {
					@Override
					public Object construct() {
						CheckForUpdates.this.startUpdate();
						return "check-update-return";
					}

					@Override
					public void finished() {
						CheckForUpdates.this.finishUpdate();
					}
				};
				CheckForUpdates.this.worker.start();
			}
		});
		buttonPanel.add (this.startStop);
		
		this.close = new JButton("Close");
		this.close.setToolTipText("Close this window");
		
		this.close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						CheckForUpdates.this.stopUpdate();
						CheckForUpdates.this.dispose();
					}
				});       
			}
		});
		buttonPanel.add(this.close);
		
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		//		Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2) - (CheckForUpdates.x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (CheckForUpdates.y / 2) + 100 ));
		this.setSize(CheckForUpdates.x, CheckForUpdates.y);
		this.setResizable(false);
		this.setVisible(true);		
	}

	public void startUpdate() {
		if(!this.startStop.isEnabled()) {
			return;
		}
		this.startStop.setText("Stop");
		this.close.setEnabled(false);
		
		final StringBuffer output = new StringBuffer();
		BufferedReader bin = null;
		
		try {
			output.append("<HTML>&nbsp;&nbsp;&nbsp;Checking JBroFuzz Website...&nbsp;&nbsp;");
			this.mainLabel.setText(output.toString() + "</HTML>");
			
			final URL request = new URL(JBRFormat.URL_WEBSITE);
			final URLConnection connection = request.openConnection();
			connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");				
			connection.connect();

			output.append("<B>OK</B><BR>&nbsp;&nbsp;&nbsp;Identifying Current Version...&nbsp;&nbsp;");
			this.mainLabel.setText(output.toString() + "</HTML>");
			
			bin = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line=null;
			while ((line=bin.readLine()) != null) {
				final Pattern p = Pattern.compile("Current version is (\\d.\\d)");
				final Matcher m = p.matcher(line);
				if (m.find()) {
					final String version = m.group();
					output.append("<B>OK</B><BR><BR>&nbsp;&nbsp;&nbsp;Is there a new version available?&nbsp;&nbsp;");
					if(version.equalsIgnoreCase("Current version is " + JBRFormat.VERSION)) {
						output.append("<B>No</B><BR><BR>&nbsp;&nbsp;&nbsp;You are using the latest version of JBroFuzz");
						output.append("<BR>&nbsp;&nbsp;&nbsp;There is no need to update");
					} else {
						output.append("<B>Yes</B><BR><BR>&nbsp;&nbsp;&nbsp;You are using a older version of JBroFuzz");
						output.append("<BR>&nbsp;&nbsp;&nbsp;The " + version);
						this.newVersionExists = true;
						
					}
					this.mainLabel.setText(output.toString() + "</HTML>");
				}
			}
			bin.close();
		}
		catch(final MalformedURLException e) {
			this.mainLabel.setText("<HTML><BR>&nbsp;&nbsp;&nbsp;An error occured while attempting to connect<BR><BR>The check for updates was unable to complete</HTML>");
		}
		catch(final IOException e) {
			this.mainLabel.setText("<HTML><BR>&nbsp;&nbsp;&nbsp;An error occured while attempting to connect<BR><BR>The check for updates was unable to complete</HTML>");
		}
		if (bin != null) {
			try {bin.close();} catch (final IOException ex) {}
		}

	}
	
	public void stopUpdate() {
		if(!this.startStop.isEnabled()) {
			return;
		}
		this.startStop.setText("Check");
		this.close.setEnabled(true);
	}
	
	public void finishUpdate() {
		if(!this.startStop.isEnabled()) {
			return;
		}
		this.close.setEnabled(true);
		
		if(this.newVersionExists) {
			this.startStop.setText("Download");
			this.startStop.removeAll();
			this.startStop.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Browser.init();
					    try {
					      Browser.displayURL(JBRFormat.URL_WEBSITE);
					    }
					    catch (final IOException ex) {
					    	CheckForUpdates.this.mainLabel.setText("<HTML><BR>&nbsp;&nbsp;&nbsp;An error occured while attempting to connect<BR><BR></HTML>");
					    }
						}
					});       
				}
			});
		} 
		else {
			this.startStop.setText("Finish");
			this.startStop.removeAll();
			this.startStop.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							CheckForUpdates.this.startStop.setEnabled(false);
						}
					});       
				}
			});
		}
	}
}
