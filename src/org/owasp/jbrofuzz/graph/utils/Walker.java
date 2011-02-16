package org.owasp.jbrofuzz.graph.utils;

import org.owasp.jbrofuzz.graph.FileSystemTreeNode;

public interface Walker {
	
	public FileSystemTreeNode getFileSystemTreeNode();
	
	public int getMaximum();
	
	public void run();
}
