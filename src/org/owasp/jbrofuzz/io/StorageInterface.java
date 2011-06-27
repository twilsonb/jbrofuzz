package org.owasp.jbrofuzz.io;

import java.util.Vector;

import org.owasp.jbrofuzz.fuzz.MessageContainer;
import org.owasp.jbrofuzz.graph.FileSystemTreeNode;
import org.owasp.jbrofuzz.graph.GraphingPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

public interface StorageInterface {

	// The max value in bytes of the file being read 32 Mbytes
	public static final int MAX_BYTES = 33554432;

	/**
	 * <p>Method for creating a new storage location.</p>
	 * 
	 * <p>If the directory with the current timestamp exists, a number from 
	 * 000 to 999 is padded to the current timestamp and a new directory 
	 * is created.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public abstract void createNewLocation();

	/**
	 * <p>
	 * Return the canonical path of the directory location of the 'fuzz'
	 * directory.
	 * </p>
	 * 
	 * @return String the path of the 'fuzz' directory
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.2
	 */
	public abstract String getLocationCanonicalPath();

	/**
	 * <p>For a filename, return the URI string where it is 
	 * stored.</p>
	 * 
	 * @param fileName
	 * @return
	 */
	public abstract String getFuzzURIString(String fileName);

	/**
	 * <p>Return the directory which is currently being used 
	 * for fuzz data</p>
	 * 
	 * @return File
	 */
	public abstract String getLocationURIString();

	public abstract void writeFuzzFile(MessageContainer outputMessage, String sessionName);
	
	
	public abstract Vector<MessageContainer> readFuzzFile(String fileName, String sessionId, JBroFuzzWindow mWindow);
	
	public abstract String[] readTableRow(String sqlStatement);
	
	public FileSystemTreeNode getSystemTreeNodeFromWalker(GraphingPanel gPanel);

}