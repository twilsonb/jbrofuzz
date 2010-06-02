/**
 * JBroFuzz 2.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.update;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * Class responsible for checking for a newer version of JBroFuzz by visiting
 * the OWASP website and comparing version numbers.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.3
 * @since 1.2
 */
public class CheckForUpdates extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1410800645463737781L;

	// Dimensions of the about box
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;

	// The JPanels inside the main window
	private final JTextArea mainLabel;

	// The start/stop and close button
	private final JButton startStop, close;

	// The boolean checking for a new version
	private boolean newVersionExists;

	/**
	 * <p>
	 * Constructor used to check for a newer version.
	 * </p>
	 * 
	 * @param parent
	 *            The JBroFuzzPanel from to which this JDialog is attached
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public CheckForUpdates(final JBroFuzzWindow parent) {

		super(parent, " JBroFuzz - Check For Updates ", true);
		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
				15, 15));
		final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));

		newVersionExists = false;

		mainLabel = new JTextArea();
		mainLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
		mainLabel.setMargin(new Insets(1, 1, 1, 1));
		mainLabel.setBackground(Color.BLACK);
		mainLabel.setForeground(new Color(0, 128, 255));
		mainLabel.setEditable(false);
		mainLabel.setVisible(true);

		// Right click: Cut, Copy, Paste, Select All
		AbstractPanel.popupText(mainLabel, false, true, false, true);

		// Scroll Panels for the text area and image

		final JLabel imageLabel = new JLabel(ImageCreator.IMG_OWASP);
		final JScrollPane providersTableScrollPane = new JScrollPane(imageLabel);
		providersTableScrollPane.setColumnHeader(null);
		providersTableScrollPane.setVerticalScrollBarPolicy(20);
		providersTableScrollPane.setHorizontalScrollBarPolicy(30);
		imageLabel.setPreferredSize(new Dimension(100, SIZE_Y - 110));
		centerPanel.add(imageLabel);

		final JScrollPane providersTextScrollPane = new JScrollPane(mainLabel);
		providersTextScrollPane.setVerticalScrollBarPolicy(20);
		providersTextScrollPane.setHorizontalScrollBarPolicy(30);
		providersTextScrollPane
		.setPreferredSize(new Dimension(SIZE_X - 150, SIZE_Y - 110));
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

				final class Starter extends SwingWorker<String, Object> {

					@Override
					public String doInBackground() {

						startUpdate();
						return "check-update-return";
					}

					@Override
					protected void done() {

						finishUpdate();

					}
				}

				(new Starter()).execute();

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

		// Where to show the check for updates dialog
		this.setLocation(
				parent.getLocation().x + (parent.getWidth() - SIZE_X) / 2, 
				parent.getLocation().y + (parent.getHeight() - SIZE_Y) / 2
		);

		this.setSize(CheckForUpdates.SIZE_X, CheckForUpdates.SIZE_Y);
		setResizable(false);
		setVisible(true);
	}

	private void finishUpdate() {
		if (!startStop.isEnabled()) {
			return;
		}
		close.setEnabled(true);

		// Remove all action listeners from the start/stop button
		final ActionListener[] acArray = startStop.getActionListeners();
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

	private void startUpdate() {

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
						// TODO: worker.interrupt();
						close.setEnabled(true);
					}
				});
			}
		});

		String response = "";

		mainLabel.setText("Finding JBroFuzz Website...\t\t\t\t");

		try {

			final URL url = new URL(JBroFuzzFormat.URL_WEBSITE);
			final URLConnection urlc = url.openConnection();
			final int statusCode = ((HttpURLConnection) urlc).getResponseCode();
			// If a 200 has been received back...
			if (statusCode == 200) {

				mainLabel.append("[ OK ]\n"
						+ "Checking JBroFuzz Website...\t\t\t\t");

				// byte[] buffer = new byte[65535];
				final BufferedReader instream = new BufferedReader(
						new InputStreamReader(urlc.getInputStream()));

				if (instream != null) {

					// Typically returns -1
					final long contentLength = urlc.getContentLength();

					if (contentLength > Integer.MAX_VALUE) {

						throw new IOException(
								"Content too large to be buffered: "
								+ contentLength + " bytes");

					}

					int c;
					int l = 0;
					final StringBuffer body = new StringBuffer(131072);
					// 
					while (((c = instream.read()) != -1) && (l < 131072)) {
						body.append((char) c);
						l++;
					}
					instream.close();

					response = body.toString();

				} // null-check (should really revisit the distro causing this


			} else { // We have not received back a 200 status code

				mainLabel.append("[FAIL]\n"
						+ "Connection returned the following code: "
						+ statusCode + "\n");

			} // else statement for a 200 response

		} catch (MalformedURLException e) {
			mainLabel.append("[FAIL]\n" + "Malformed URL violation: "
					+ e.getMessage());

		} catch (UnsupportedEncodingException e) {
			mainLabel.append("[FAIL]\n" + "Encoding error: " + e.getMessage());
		} catch (IOException e) {
			mainLabel.append("[FAIL]\n" + "Fatal transport error: "
					+ e.getMessage());
		} finally {
			// 
		}

		if (!response.equalsIgnoreCase("")) {
			mainLabel.append("[ OK ]\n"
					+ "Checking for latest version...\t\t\t\t");

			final Pattern p1 = Pattern.compile("Current version is (\\d.\\d)");
			final Matcher m1 = p1.matcher(response);
			if (m1.find()) {
				mainLabel.append("[ OK ]\n"
						+ "Comparing version numbers...\t\t\t\t");
				final String webVersion = m1.group().substring(19, 22);

				double current = 0.0;
				double latest = 0.0;

				try {

					current = Double.parseDouble(JBroFuzzFormat.VERSION);
					latest = Double.parseDouble(webVersion);

					mainLabel.append("[ OK ]\n\nWebsite Version is: "
							+ webVersion);
					mainLabel.append("\nCurrent Version is: "
							+ JBroFuzzFormat.VERSION + "\n\n");

				} catch (NumberFormatException e) {
					mainLabel.append("[FAIL]\n");
				}

				if (latest != 0.0) {

					mainLabel
					.append("\n"
							+ "Could not interpret JBroFuzz version\nnumbers.\n\nTo check manually, visit:\n\n"
							+ JBroFuzzFormat.URL_WEBSITE);

				} else {
					
					if (latest > current) {
						mainLabel.append("\nJBroFuzz " + latest
								+ " is available for download.");
						newVersionExists = true;
					} else if (latest < current) {
						mainLabel
						.append("\nYou are running a newer (perhaps experimental) version.");
					} else {
						mainLabel
						.append("\nYou are running the latest version.");
					}
					
				}

			} else {
				mainLabel.append("[FAIL]\n\n"
						+ "Could not identify JBroFuzz version at:\n\n"
						+ JBroFuzzFormat.URL_WEBSITE);
			}
		}
	}

}
