/**
 * JBroFuzz 2.3
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 * hardening for version 2.4 by daemonmidi@gmail.com
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

import com.Ostermiller.util.Browser;

/**
 * <p>Class responsible for checking for a later release at 
 * startup.</p>
 * 
 * <p>The constructor of this class displays a JDialog if a new 
 * version is identified on the website.</p>
 * 
 * @author subere@uncon.org
 * @version 1.7
 * @since 1.3
 */
public class StartUpdateChecker extends JDialog {

	private static final long serialVersionUID = 5920384008550160901L;

	// Dimensions of the update dialog
	private static final int SIZE_X = 440;
	private static final int SIZE_Y = 220;
	
	private final static double ZERO_VERSION = 0.0;


	/**
	 * <p>
	 * Static method for getting the current online version of JBroFuzz.
	 * </p>
	 * 
	 * <p>
	 * This method makes a connection to the OWASP web site, checking for the
	 * latest version number.
	 * </p>
	 * 
	 * @return double of the version or 0.0 in case of an error
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
	 * @since 1.3
	 */
	private static double getWebsiteVersion() {

		String response = "";
		BufferedReader instream = null;

		try {

			final URL url = new URL(JBroFuzzFormat.URL_WEBSITE);
			final URLConnection urlc;

			final boolean proxyEnabled = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.UPDATE[0].getId(), false);
			if(proxyEnabled) {
				
				final String proxy = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[1].getId(), "");
				final int port = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UPDATE[2].getId(), -1);
				// A note here, proxy has no http:// or https://
				Proxy myProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy, port));	
				urlc = url.openConnection(myProxy);
				// Username:Password, yawn
				
				final boolean proxyReqAuth = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.UPDATE[3].getId(), false);
				if(proxyReqAuth) {
					
					final String user = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[4].getId(), "");
					final String pass = JBroFuzz.PREFS.get(JBroFuzzPrefs.UPDATE[5].getId(), "");
					final String encodedPassword = EncoderHashCore.encode(user + ":" + pass, "Base64");
					urlc.setRequestProperty( "Proxy-Authorization", encodedPassword );					
				}
				
			} else {
				
				 urlc = url.openConnection();
				 
			}
			
			final int statusCode = ((HttpURLConnection) urlc).getResponseCode();

			if (statusCode == HttpURLConnection.HTTP_OK) {

				instream = new BufferedReader(new InputStreamReader(urlc
						.getInputStream()));

				if (instream == null) {
					return ZERO_VERSION;
				}

				// Typically returns -1
				final long contentLength = urlc.getContentLength();

				if (contentLength > Integer.MAX_VALUE) {
					return ZERO_VERSION;
				}

				int count;
				int limit = 0;
				final StringBuffer body = new StringBuffer(Character.MAX_VALUE);
				// 
				while (((count = instream.read()) != -1) && (limit < Character.MAX_VALUE)) {
					body.append((char) count);
					limit++;
				}
				instream.close();

				response = body.toString();

			} else {
				return ZERO_VERSION;
			} // else statement for a 200 response

		} catch (final IOException e) {
			return ZERO_VERSION;
		} finally {
			IOUtils.closeQuietly(instream);
		}

		if (!response.equalsIgnoreCase("")) {

			final Pattern pattern1 = Pattern.compile("Current version is (\\d.\\d)");
			final Matcher match1 = pattern1.matcher(response);
			if (match1.find()) {
				final String webVersion = match1.group().substring(19, 22);

				try {
					// Return the value, if found
					return Double.parseDouble(webVersion);

				} catch (final NumberFormatException e) {
					// Return 0.0 if an error occurs
					return ZERO_VERSION;

				}

			} else {
				return ZERO_VERSION;
			}
		}
		return ZERO_VERSION;
	}
	
	/**
	 * <p>
	 * Main constructor that displays a JDialog if a new version is identified
	 * on the website.</p.
	 * 
	 * @param parent
	 *            JBroFuzzwindow the main window
	 * 
	 * @author subere@uncon.org
	 * @version 1.7
	 * @since 1.3
	 */
	protected StartUpdateChecker(final JBroFuzzWindow parent) {

		super(parent, " JBroFuzz - New Version Available ", true);

		// Version comparison to see if we will display or dispose
		final double latest = StartUpdateChecker.getWebsiteVersion();
		if (latest == StartUpdateChecker.ZERO_VERSION) {
			StartUpdateChecker.this.dispose();
			return;
		}
		
		double current;
		try {
			current = Double.parseDouble(JBroFuzzFormat.VERSION);
		} catch (final NumberFormatException e1) {
			current = StartUpdateChecker.ZERO_VERSION;
		}
		if (latest <= current) {
			StartUpdateChecker.this.dispose();
			return;
		}

		final String text = 
			"<html><b>A new version of JBroFuzz is available:&nbsp;"
			+ latest
			+ "<br><br>You are currently running version:&nbsp;"
			+ current
			+ "<br><br>Do you wish to download the <br>new version now?" 
			+ "</b></html>";

		setIconImage(ImageCreator.IMG_FRAME.getImage());

		setLayout(new BorderLayout());
		setFont(new Font("Verdana", Font.PLAIN, 12));

		final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
				15, 15));
		final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));

		// The about editor label
		final JLabel mainLabel = new JLabel(text, ImageCreator.IMG_OWASP,
				SwingConstants.LEFT);
		mainLabel.setIconTextGap(20);
		mainLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));


		centerPanel.add(mainLabel);

		// Bottom buttons
		final JButton download = new JButton("Download");
		final JButton close = new JButton("Close");
		southPanel.add(download);
		southPanel.add(close);

		// Action Listeners

		download.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent even) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Browser.init();
						try {
							Browser.displayURL(JBroFuzzFormat.URL_WEBSITE);
						} catch (final IOException e) {
							System.out.println("An IOException occurred.");
						}
						StartUpdateChecker.this.dispose();
					}
				});

			}
		});

		close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent even) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						StartUpdateChecker.this.dispose();
					}
				});

			}
		});

		// Add the panels to the dialog

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(southPanel, BorderLayout.SOUTH);

		// Global frame issues
		final int xLocation = parent.getLocationOnScreen().x
		- (StartUpdateChecker.SIZE_X / 2) + (parent.getWidth() / 2);
		final int yLocation = parent.getLocationOnScreen().y
		- (StartUpdateChecker.SIZE_Y / 2) + (parent.getHeight() / 2);

		setSize(StartUpdateChecker.SIZE_X, StartUpdateChecker.SIZE_Y);
		setLocation(xLocation, yLocation);

		setMinimumSize(new Dimension(StartUpdateChecker.SIZE_X, StartUpdateChecker.SIZE_Y));
		setResizable(true);
		setVisible(true);

	}

}
