/**
 * JBroFuzz 1.9
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

import java.io.File;

class JohnyWalker {

	private FileSystemTreeNode master;

	private File directory;

	private GraphingPanel x3;

	//

	private int fileCount, dirCount;

	protected JohnyWalker(GraphingPanel x3) {

		directory = new File(System.getProperty("user.dir") + File.separator
				+ "jbrofuzz" + File.separator + "fuzz");
		this.x3 = x3;

		if (directory.canRead()) {
			master = new FileSystemTreeNode(directory.getName());
			master.setAsDirectory();
		} else {
			this.x3.toConsole("Cannot read: " + directory.getPath());
		}

		fileCount = 0;
		dirCount = 0;

	}

	public FileSystemTreeNode getFileSystemTreeNode() {
		return master;
	}

	public int getMaximum() {

		return directory.listFiles().length;

	}

	private void listAllFiles(File directory, FileSystemTreeNode parent) {

		if (!directory.canRead()) {
			x3.toConsole("Could not read: " + directory.getPath());
			return;
		}

		if (x3.isStoppedEnabled()) {
			return;
		}

		dirCount++;

		File[] children = directory.listFiles();

		for (File f : children) {

			FileSystemTreeNode node = new FileSystemTreeNode(f.getName());

			if (f.isDirectory()) {
				node.setAsDirectory();
				parent.add(node);
				dirCount++;
				listAllFiles(f, node);
			} else if (!f.isDirectory()) {
				parent.add(node);
				fileCount++;
			}
		}

	}

	protected void run() {

		listAllFiles(directory, master);

		x3.toConsole("Total Files: " + fileCount);
		x3.toConsole("Total Directories: " + dirCount);

	}

}
