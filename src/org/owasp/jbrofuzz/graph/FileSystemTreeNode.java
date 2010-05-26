/**
 * JBroFuzz 2.2
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
package org.owasp.jbrofuzz.graph;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * @author subere@uncon.org
 * @version 2.0
 */
public class FileSystemTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 5086899959196495566L;

	private transient boolean nodeIsDirectory;

	public FileSystemTreeNode(final Object fsObject) {
		super(fsObject);
		nodeIsDirectory = false;
	}

	public boolean isDirectory() {
		return nodeIsDirectory;
	}

	protected void setAsDirectory() {
		nodeIsDirectory = true;
	}
}
