/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.owasp.jbrofuzz.ui.headers;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author yiannis
 */
public class HeaderTreeNode extends DefaultMutableTreeNode {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1419679735344469281L;
	
	private boolean nodeIsDirectory;
    
    public HeaderTreeNode(Object o) {
        super(o);
        nodeIsDirectory = false;
    }
    
    public HeaderTreeNode(Object o, boolean isDirectory) {
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
