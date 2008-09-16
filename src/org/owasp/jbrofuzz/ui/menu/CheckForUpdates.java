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
package org.owasp.jbrofuzz.ui.menu;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.util.regex.*;

import com.Ostermiller.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.*;
import org.apache.commons.httpclient.methods.*;

import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.*;
import org.owasp.jbrofuzz.ui.panels.*;

public class CheckForUpdates extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Dimensions of the about box
	private static final int x = 650;
	private static final int y = 400;

	// The JPanels inside the main window
	private JTextArea mainLabel;

	// The start/stop and close button
	private JButton startStop, close;

	// The Swing Worker used
	private SwingWorker3 worker;

	// The boolean checking for a new version
	private boolean newVersionExists;

	public CheckForUpdates(final JBroFuzzPanel parent) {

		super(parent.getFrame(), " JBroFuzz - Check For Updates ", true);
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));

		newVersionExists = false;

		mainLabel = new JTextArea();
		mainLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
		mainLabel.setMargin(new Insets(1, 1, 1, 1));
		mainLabel.setBackground(Color.BLACK);
		mainLabel.setForeground(new Color(0, 128, 255));
		mainLabel.setEditable(false);
		mainLabel.setVisible(true);
		
		// Right click: Cut, Copy, Paste, Select All
		parent.popupText(mainLabel, false, true, false, true);

		// Scroll Panels for the text area and image

		final JLabel imageLabel = new JLabel(ImageCreator.OWASP_IMAGE);
		final JScrollPane providersTableScrollPane = new JScrollPane(imageLabel);
		providersTableScrollPane.setColumnHeader(null);
		providersTableScrollPane.setVerticalScrollBarPolicy(20);
		providersTableScrollPane.setHorizontalScrollBarPolicy(30);
		imageLabel.setPreferredSize(new Dimension(100, y - 110));
		centerPanel.add(imageLabel);

		final JScrollPane providersTextScrollPane = new JScrollPane(mainLabel);
		providersTextScrollPane.setVerticalScrollBarPolicy(20);
		providersTextScrollPane.setHorizontalScrollBarPolicy(30);
		providersTextScrollPane
				.setPreferredSize(new Dimension(x - 150, y - 110));
		centerPanel.add(providersTextScrollPane);

		// Bottom buttons

		startStop = new JButton("Check");
		close = new JButton("Close");

		startStop.setToolTipText("Check online for a latest version");
		close.setToolTipText("Close this window");

		southPanel.add(startStop);
		southPanel.add(close);

		// Action Listeners

		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				worker = new SwingWorker3() {
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
				worker.start();
			}
		});

		close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						CheckForUpdates.this.dispose();
					}
				});
			}
		});

		// Add the panels to the dialog

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(southPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs(parent.getLocationOnScreen().x + 100), Math.abs(parent.getLocationOnScreen().y + 20));
		this.setSize(CheckForUpdates.x, CheckForUpdates.y);
		setResizable(false);
		setVisible(true);
	}

	public void finishUpdate() {
		if (!startStop.isEnabled()) {
			return;
		}
		close.setEnabled(true);

		// Remove all action listeners from the start/stop button
		ActionListener[] acArray = startStop.getActionListeners();
		for (ActionListener listener : acArray) {
			startStop.removeActionListener(listener);
		}

		if (newVersionExists) {
			startStop.setText("Download");
			startStop.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Browser.init();
							try {
								Browser.displayURL(JBroFuzzFormat.URL_WEBSITE);
								startStop.setEnabled(false);
								close.setEnabled(true);
							} catch (final IOException ex) {
								mainLabel
										.append("\nAn error occured while attempting to open the browser:\n\n"
												+ JBroFuzzFormat.URL_WEBSITE);
							}
						}
					});
				}
			});
		} else {
			startStop.setText("Finish");
			startStop.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							startStop.setEnabled(false);
							close.setEnabled(true);
						}
					});
				}
			});
		}
	}

	public void startUpdate() {

		if (!startStop.isEnabled()) {
			return;
		}

		// Prior to beginning reset the button's action listener
		startStop.setEnabled(true);
		startStop.setText("Stop");
		close.setEnabled(false);

		// Remove all action listeners
		ActionListener[] acArray = startStop.getActionListeners();
		for (ActionListener listener : acArray) {
			startStop.removeActionListener(listener);
		}

		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						startStop.setEnabled(false);
						worker.interrupt();
						close.setEnabled(true);
					}
				});
			}
		});

		String response = "";

		mainLabel.setText("Finding JBroFuzz Website...\t\t\t\t");

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(JBroFuzzFormat.URL_WEBSITE);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				mainLabel.append("[FAIL]\n"
						+ "Connection returned the following code: "
						+ method.getStatusLine() + "\n");

			} else {
				mainLabel.append("[ OK ]\n" + "Checking JBroFuzz Website...\t\t\t\t");
				
                byte[] buffer = new byte[65535];
				InputStream instream = method.getResponseBodyAsStream();
				if (instream != null) {
					
					// Make sure content length can be handled from the JVM
	                long contentLength = method.getResponseContentLength();
	                
	                if (contentLength > Integer.MAX_VALUE) {
	                	
	                	throw new IOException("Content too large to be buffered: "+ contentLength +" bytes");
	                	
	                }
	                
	                // int limit = method.getParams().getIntParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024*1024);
	                ByteArrayOutputStream outstream = new ByteArrayOutputStream(contentLength > 0 ? (int) contentLength : 32);

	                
	                int len;
	                int count = 0;
	                while ((len = instream.read(buffer)) > 0) {
	                	outstream.write(buffer, 0, len);
	                	System.out.println(count + " total bytes read: " + len);
	                	count++;
	                }
	                outstream.close(); 
				}
				
				// Read the response body.
				// byte[] responseBody = method.getResponseBody();
				System.out.println("The default CharSet is: " + Charset.defaultCharset());
				// response += new String(buffer, "US-ASCII");
				Charset charset = Charset.forName("UTF-8");
				CharsetDecoder decoder = charset.newDecoder();
				ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
				CharBuffer charBuffer = decoder.decode(byteBuffer);

				response = charBuffer.toString();
				
			}

		} catch (HttpException e) {
			mainLabel.append("[FAIL]\n" + "Fatal protocol violation: "
					+ e.getMessage());

		} catch (UnsupportedEncodingException e) {
			mainLabel.append("[FAIL]\n" + "UTF-8 Encoding error: "
					+ e.getMessage());
		} catch (IOException e) {
			mainLabel.append("[FAIL]\n" + "Fatal transport error: "
					+ e.getMessage());
		}finally {
		
			// Release the connection.
			method.releaseConnection();
		}

		if (!response.equalsIgnoreCase("")) {
			mainLabel.append("[ OK ]\n" + "Checking for latest version...\t\t\t\t");

			final Pattern p1 = Pattern.compile("Current version is (\\d.\\d)");
			final Matcher m1 = p1.matcher(response);
			if (m1.find()) {
				mainLabel.append("[ OK ]\n" + "Comparing version numbers...\t\t\t\t");
				final String webVersion = m1.group().substring(19, 22);

				double current = 0.0;
				double latest = 0.0;

				try {

					current = Double.parseDouble(JBroFuzzFormat.VERSION);
					latest = Double.parseDouble(webVersion);

					mainLabel.append("[ OK ]\n\nWebsite Version is: "
							+ webVersion);
					mainLabel.append("\nCurrent Version is: " + JBroFuzzFormat.VERSION
							+ "\n\n");

				} catch (NumberFormatException e) {
					mainLabel.append("[FAIL]\n");
				}

				if (latest != 0.0) {

					if (latest > current) {
						mainLabel.append("\nJBroFuzz " + latest + " is available for download.");
						newVersionExists = true;
					} else if (latest < current) {
						mainLabel.append("\nYou are running a newer (perhaps experimental) version.");
					} else {
						mainLabel
								.append("\nYou are running the latest version.");
					}

				} else {
					mainLabel.append("\n"
									+ "Could not interpret JBroFuzz version\nnumbers.\n\nTo check manually, visit:\n\n"
									+ JBroFuzzFormat.URL_WEBSITE);
				}

			} else {
				mainLabel.append("[FAIL]\n\n"
						+ "Could not identify JBroFuzz version at:\n\n"
						+ JBroFuzzFormat.URL_WEBSITE);
			}
		}
	}

}
