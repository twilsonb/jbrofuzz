package org.owasp.jbrofuzz.graph;

import javax.swing.tree.*;

public class FileSystemTreeModel extends DefaultTreeModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileSystemTreeModel(FileSystemTreeNode root) {
        super(root, true);
    }
    
    @Override
    public boolean isLeaf(Object node) {

    	return !((FileSystemTreeNode)node).isDirectory();

    }
    
}
