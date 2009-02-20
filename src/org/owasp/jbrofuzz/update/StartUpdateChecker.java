/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

/**
 * <p>Class responsible for checking for a later release at 
 * startup.</p>
 * 
 *
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.3
 */
public class StartUpdateChecker extends JDialog {

	private static final long serialVersionUID = 5920384008550160901L;

	// Dimensions of the about box
	private static final int x = 420;
	private static final int y = 200;

	// The download and close button
	private JButton download, close;

	/**
	 * <p>Main constructor that displays a JDialog if a new
	 * version is identified on the website.</p.
	 * 
	 * @param parent JBroFuzzwindow the main window
	 *
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.3
	 */
	public StartUpdateChecker(final JBroFuzzWindow parent) {

		super(parent, " JBroFuzz - New Version Available ", true);

		// Version comparison to see if we will display or dispose
		double latest = StartUpdateChecker.getWebsiteVersion();
		if (latest == 0.0) {
			StartUpdateChecker.this.dispose();
			return;
		}
		double current;
		try {
			current = Double.parseDouble(JBroFuzzFormat.VERSION);
		} catch (NumberFormatException e1) {
			current = 0.0;
		}
		if (latest <= current) {
			StartUpdateChecker.this.dispose();
			return;
		}

		String text = 
			"<html><b>A new version of JBroFuzz is available:&nbsp;" + latest + 
			"<br><br>You are currently running version:&nbsp;" + current + 
			"<br><br>Do you wish to download the <br>new version now?</b></html>";

		setIconImage(ImageCreator.IMG_FRAME.getImage());

		setLayout(new BorderLayout());
		setFont(new Font("Verdana", Font.PLAIN, 12));

		final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));

		// The about editor label
		final JLabel mainLabel = new JLabel(text, ImageCreator.IMG_OWASP, SwingConstants.LEFT);
		mainLabel.setIconTextGap(20);
		mainLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		centerPanel.add(mainLabel);

		// Bottom buttons
		download = new JButton("Download");
		close = new JButton("Close");
		southPanel.add(download);
		southPanel.add(close);

		// Action Listeners

		download.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Browser.init();
						try {
							Browser.displayURL(JBroFuzzFormat.URL_WEBSITE);
						} catch (IOException e) {

						}
						StartUpdateChecker.this.dispose();
					}
				});

			}
		});

		close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

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
		int xLocation = parent.getLocationOnScreen().x - (StartUpdateChecker.x / 2) + (parent.getWidth() / 2);
		int yLocation = parent.getLocationOnScreen().y - (StartUpdateChecker.y / 2) + (parent.getHeight() / 2);
		this.setSize(StartUpdateChecker.x, StartUpdateChecker.y);
		this.setLocation(xLocation, yLocation);
		setResizable(false);
		setVisible(true);

	}

	/**
	 * <p>Static method for getting the current online version of
	 * JBroFuzz.</p>
	 * 
	 * <p>This method makes a connection to the OWASP website, 
	 * checking for the latest version number.</p>
	 * 
	 * @return double of the version or 0.0 in case of an error
	 *
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.3
	 */
	private static double getWebsiteVersion() {

		final double ZERO_VERSION = 0.0;

		String response = ""; 

		try {

			URL url = new URL(JBroFuzzFormat.URL_WEBSITE);
			URLConnection urlc = url.openConnection();
			int statusCode = ((HttpURLConnection)urlc).getResponseCode();

			if (statusCode != 200) {

				return ZERO_VERSION;

			} else {

				BufferedReader instream = new BufferedReader(
						new InputStreamReader(urlc.getInputStream()));

				if (instream != null) {

					// Typically returns -1
					long contentLength = urlc.getContentLength(); 

					if (contentLength > Integer.MAX_VALUE) {
						return ZERO_VERSION;
					}

					int c;
					int l = 0;
					StringBuffer body = new StringBuffer(131072);
					// 
					while( ((c = instream.read()) != -1) && (l < 131072) ) {
						body.append((char) c);
						l++;
					}	                
					instream.close();

					response = body.toString();

				} // null-check (should really revisit the distro causing this

			} // else statement for a 200 response

		} catch (IOException e) {
			return ZERO_VERSION;
		}

		if (!response.equalsIgnoreCase("")) {

			final Pattern p1 = Pattern.compile("Current version is (\\d.\\d)");
			final Matcher m1 = p1.matcher(response);
			if (m1.find()) {
				final String webVersion = m1.group().substring(19, 22);

				double latest = ZERO_VERSION;
				try {

					latest = Double.parseDouble(webVersion);

				} catch (NumberFormatException e) {
					return ZERO_VERSION;
				}

				return latest;

			} else {
				return ZERO_VERSION;
			}
		}
		return ZERO_VERSION;
	}

}
