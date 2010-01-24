package org.owasp.jbrofuzz.fuzz;

public interface AbstractConnection {

	/**
	 * <p>
	 * Get the message being put on the wire in this connection.
	 * </p>
	 * 
	 * @return String message or "[JBROFUZZ REQUEST IS BLANK]" if message is
	 *         empty.
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public abstract String getMessage();

	/**
	 * <p>
	 * Get the port number used in the connection being made.
	 * </p>
	 * 
	 * @return [1-65535] or "[JBROFUZZ PORT IS INVALID]"
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public abstract String getPort();

	/**
	 * <p>
	 * Return the reply from the Connection that has been made, based on the
	 * message that has been transmitted during construction.
	 * </p>
	 * <p>
	 * Revisited this method in JBroFuzz 1.9 in order NOT to throw an exception
	 * if the reply string is empty, see {@link #getMessage()} for old
	 * implementation logic.
	 * </p>
	 * 
	 * @return String The reply string
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.0
	 */
	public abstract String getReply();

	/**
	 * <p>
	 * Return the HTTP status code, e.g. 200, 404, etc.
	 * </p>
	 * <p>
	 * In case of a non-existant code, return "---".
	 * </p>
	 * <p>
	 * In the case of a "100-Continue", return "100/xxx" where "xxx" is defined
	 * above.
	 * </p>
	 * 
	 * @return String of 3 or 7 characters with the code value.
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	public abstract String getStatus();

	/**
	 * <p>
	 * Method for checking if the actual String given is an HTTP/1.1 request.
	 * </p>
	 * <p>
	 * This check entails looking for the first line (as divided by \r\n to be
	 * finishing with the String literal "HTTP/1.1" in upper-case or lower-case.
	 * </p>
	 * 
	 * @param message
	 *            The input string used
	 * @return boolean True if HTTP/1.1 is found on the first line
	 */
	public abstract boolean protocolIsHTTP11(String message);

	/**
	 * <p>
	 * Method for returning the POST Data being submitted.
	 * </p>
	 * <p>
	 * If no POST Data is found the input message is returned.
	 * </p>
	 * 
	 * @param message
	 * @version 1.5
	 * @since 1.5
	 * @return
	 */
	public abstract String getPostDataInMessage();

	public abstract boolean isResponse100Continue();

}