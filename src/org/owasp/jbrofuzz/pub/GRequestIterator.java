/**
 * GRequestIterator.java 0.6
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
package org.owasp.jbrofuzz.pub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.jbrofuzz.ui.JBRFrame;

/**
 * <p>Class for looking domain addresses by making google requests.</p>
 * <p>This class makes a total of 5 requests to google using the domain
 * passed to the constructor.</p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class GRequestIterator {
	// The output obtained
	private StringBuffer output;

	public GRequestIterator(final JBRFrame mFrameWindow, final String domain) {
		this.output = new StringBuffer();
		int counter = 1;
		BufferedReader bin = null;

		while(counter <= 5) {
			try {
				// URL request = new URL("http://www.google.com");
				final String urlRequest = "http://www.google.com/search?q=%40" + domain + "&hl=en&lr=&ie=UTF-8&start=" + counter + "0&sa=N";				
				final URL request = new URL(urlRequest);
				final URLConnection connection = request.openConnection();
				connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");				
				connection.connect();

				bin = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line=null;
				while ((line=bin.readLine()) != null) {
					final Pattern p = Pattern.compile("([\\w\\.\\-\\%\\']+)@[\\S]+");
					final Matcher m = p.matcher(line);
					while (m.find()) {
						final String email = this.sanitise(m.group());
						this.output.append(email + " \n");
					}
				}
				bin.close();
			}
			catch(final MalformedURLException e) {
				mFrameWindow.log("Open Source Tab: A malformed URL exception occured");
			}
			catch(final IOException e) {
				mFrameWindow.log("Open Source Tab: An IO exception occured");
			}
			if (bin != null) {
				try {bin.close();} catch (final IOException ex) {}
			}

			mFrameWindow.getOpenSourcePanel().appendOutputText(this.output.toString());
			mFrameWindow.getOpenSourcePanel().setProgressBar(counter);
			counter++;
		} // while loop
	}

	public String getOutput() {
		return this.output.toString();
	}

	private String sanitise(String in) {
		boolean tagFound = false;
		while(!tagFound) {
			tagFound = true;
			final int str = in.indexOf('<');
			if(str > 0) {
				final int end = in.indexOf('>', str);
				if(end > 0) {
					in = in.substring(0, str) + in.substring(end + 1);
					tagFound = false;
				}
			}
		}
		return in;
	}

}
