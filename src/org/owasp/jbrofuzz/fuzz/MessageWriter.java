/**
 * JBroFuzz 1.3
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
package org.owasp.jbrofuzz.fuzz;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageWriter {

	private static final SimpleDateFormat	SD_Format	= new SimpleDateFormat(
																											"zzz-yyyy-MM-dd-HH-mm-ss-SSS");
	private static final SimpleDateFormat	SH_Format	= new SimpleDateFormat(
																											"DDD-HH-mm-ss-SSS");

	private StringBuffer									message;

	private String												filename;
	private String												textURL;
	private final Date										start;
	private Date													end;
	private String												status;

	private int														replyByteLength;

	public MessageWriter(final MessageCreator currentMessage,
			final FuzzingPanel fuzzingPanel) {

		// Set the start & end time
		start = new Date();
		end = start;
		// Set the filename
		filename = fuzzingPanel.getCounter(true);
		// Set the text URL
		textURL = fuzzingPanel.getTextURL();
		// Set the default status
		status = "---";
		// Initialise the byte length
		replyByteLength = 0;

		// Create the StringBuffer starting with a comment
		message = new StringBuffer("<!--\n");

	}

	private void append(int n) {

		message.append(n);
		message.append('\n');
		message.append("--");
		message.append('\n');

	}

	private void append(String s) {

		message.append(s);
		message.append('\n');
		message.append("--");
		message.append('\n');

	}

	public int getByteCount() {

		return replyByteLength;

	}

	public String getFileName() {

		return filename;

	}

	/**
	 * <p>
	 * Get the response time, in milliseconds
	 * 
	 * @return int the response time in ms
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public int getResponseTime() {

		return (int) (end.getTime() - start.getTime());

	}

	public String getStartDateFull() {

		return SD_Format.format(start);
	}

	public String getStartDateShort() {

		return SH_Format.format(start);

	}

	public String getStatus() {

		return status;

	}

	public String getTextURL() {

		return textURL;

	}

	public void setConnection(Connection connection) {

		// Update the reply
		final String reply = connection.getReply();

		// Update the status
		status = connection.getStatus();
		// Update the end time
		end = new Date();
		// Update the reply byte length
		replyByteLength = reply.getBytes().length;

		this.append(getResponseTime());
		this.append(status);
		this.append(getStartDateFull());
		this.append(filename);
		this.append(textURL);
		// Append the port to the message
		this.append(connection.getPort());
		this.append(connection.getMessage());
		// Append the distinguishing response from reply
		message.append("--jbrofuzz-->\n");
		// Finally write the reply string
		message.append(reply);

	}

	public void setException(ConnectionException exception) {

		// Update the reply
		final String reply = exception.getMessage();

		// Update the end time
		end = new Date();

		this.append(getResponseTime());
		this.append(getStartDateFull());
		this.append(status);
		this.append(filename);
		this.append(textURL);
		// Finally write the reply string
		this.append(reply);
		// Append the distinguishing response from reply
		message.append("--jbrofuzz-->\n");

	}

	@Override
	public String toString() {

		return message.toString();

	}

}
