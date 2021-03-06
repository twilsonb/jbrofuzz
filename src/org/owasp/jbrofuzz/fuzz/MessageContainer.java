/**
 * JbroFuzz 2.5
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
package org.owasp.jbrofuzz.fuzz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.owasp.jbrofuzz.fuzz.ui.FuzzingPanel;

public class MessageContainer {

	private static final SimpleDateFormat SD_FORMAT = new SimpleDateFormat(
			"zzz-yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH);

	private static final SimpleDateFormat SH_FORMAT = new SimpleDateFormat(
			"DDD-HH-mm-ss-SSS", Locale.ENGLISH);

	private transient final StringBuffer message;

	private transient String filename;
	private transient String textURL;
	private transient Date start;
	private transient Date end;
	private transient String status;
	private transient String payload;
	private transient String encodedPayload;
	private transient int replyByteLength;
	private transient String textRequest;
	private transient int responseTime;
	private transient String reply;
	
	
	public MessageContainer(final FuzzingPanel fuzzingPanel) {

		// Set the start & end time
		start = new Date();
		end = start;
		// Set the filename
		filename = fuzzingPanel.getCounter();
		// Set the text URL
		textURL = fuzzingPanel.getTextURL();
		// Set the default status
		status = "---";
		// Set the current payload
		payload = fuzzingPanel.getPayload();
		// Set the encoded paoylad
		encodedPayload = fuzzingPanel.getEncodedPayload();
		// Initialise the byte length
		replyByteLength = 0;
		// Create the StringBuffer starting with a comment
		message = new StringBuffer("<!--\n");
	}

	public void setReply(String reply){
		this.reply = reply;
	}
	
	public String getReply(){
		return this.reply;
	}
	
	public void setEnd(Date date) {
		this.end = date;
	}
	
	public String getEndDateFull(){
		return SD_FORMAT.format(this.end);
	}
	
	public String getEndDateShort(){
		return SH_FORMAT.format(this.end);
	}
	
	public int getEndDateNumerical(){
		return (int) this.end.getTime();
	}
	
	public int getStartDateNumerical(){
		return (int) this.start.getTime();
	}

	private void append(final int input) {

		message.append(input);
		message.append('\n');
		message.append('-');
		message.append('-');
		message.append('\n');

	}

	private void append(final String input) {

		message.append(input);
		message.append('\n');
		message.append('-');
		message.append('-');
		message.append('\n');

	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public int getByteCount() {

		return replyByteLength;

	}

	public String getFileName() {

		return filename;

	}

	public void setFileName(String fileName) {
		this.filename = fileName;
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
	
	public void setResponseTime(int responseTime){
		this.responseTime = responseTime;
	}
	


	public String getStartDateFull() {

		synchronized (this) {
			return SD_FORMAT.format(start);
		}

	}

	public String getStartDateShort() {

		synchronized (this) {
			return SH_FORMAT.format(start);
		}

	}

	public void setStartDate(Date date) {
		this.start = date;
	}

	public String getStatus() {

		return status;

	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTextURL() {

		return textURL;

	}

	public void setTextURL(String url) {
		this.textURL = url;
	}

	public void setConnection(final Connection connection) {

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


	public String getTextRequest(){
		return textRequest;
	}
	
	public void setTextRequest(String textRequest){
		this.textRequest = textRequest;
	}
	
	public void setException(final ConnectionException conException) {

		// Update the reply
		final String reply = conException.getMessage();

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

	public String getMessage() {
		return this.message.toString();
	}

	public void setMessage(String message) {
		this.message.append(message);
	}

	@Override
	public String toString() {

		return message.toString();

	}

	public String getEncodedPayload() {
		return encodedPayload;
	}

	public void setEncodedPayload(String payload) {
		this.encodedPayload = payload;
	}
}
