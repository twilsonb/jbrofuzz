/**
 * JBroFuzz 1.5
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
