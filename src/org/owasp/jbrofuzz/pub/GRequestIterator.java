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

import java.util.regex.*;
import java.net.*;
import java.io.*;

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
	//The  frame window that the request iterator can be referenced from
	private JBRFrame mFrameWindow;
	// The output obtained
	private StringBuffer output;

	public GRequestIterator(JBRFrame mFrameWindow, String domain) {
		this.mFrameWindow = mFrameWindow;
		output = new StringBuffer();
		int counter = 1;
		BufferedReader bin = null;

		while(counter <= 5) {
			try {
				// URL request = new URL("http://www.google.com");
				String urlRequest = "http://www.google.com/search?q=%40" + domain + "&hl=en&lr=&ie=UTF-8&start=" + counter + "0&sa=N";				
				URL request = new URL(urlRequest);
				URLConnection connection = request.openConnection();
				connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");				
				connection.connect();

				bin = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line=null;
				while ((line=bin.readLine()) != null) {
					Pattern p = Pattern.compile("([\\w\\.\\-\\%\\']+)@[\\S]+");
					Matcher m = p.matcher(line);
					while (m.find()) {
						String email = sanitise(m.group());
						output.append(email + " \n");
					}
				}
				bin.close();
			}
			catch(MalformedURLException e) {
				mFrameWindow.log("Open Source Tab: A malformed URL exception occured");
			}
			catch(IOException e) {
				mFrameWindow.log("Open Source Tab: An IO exception occured");
			}
			if (bin != null) {
				try {bin.close();} catch (IOException ex) {}
			}

			mFrameWindow.getOpenSourcePanel().appendOutputText(output.toString());
			mFrameWindow.getOpenSourcePanel().setProgressBar(counter);
			counter++;
		} // while loop
	}

	public String getOutput() {
		return output.toString();
	}

	private String sanitise(String in) {
		boolean tagFound = false;
		while(!tagFound) {
			tagFound = true;
			int str = in.indexOf('<');
			if(str > 0) {
				int end = in.indexOf('>', str);
				if(end > 0) {
					in = in.substring(0, str) + in.substring(end + 1);
					tagFound = false;
				}
			}
		}
		return in;
	}

}
