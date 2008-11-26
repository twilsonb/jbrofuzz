/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.owasp.jbrofuzz.graph;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author yiannis
 */
public class FileSystemTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private boolean nodeIsDirectory;
    
    public FileSystemTreeNode(Object o) {
        super(o);
        nodeIsDirectory = false;
    }
    
    public FileSystemTreeNode(Object o, boolean isDirectory) {
        super(o);
        nodeIsDirectory = isDirectory;
    }
    
    public boolean isDirectory() {
        return nodeIsDirectory;
    }
    
    public void setAsDirectory() {
        nodeIsDirectory = true;
    }
}
