package org.owasp.jbrofuzz.graph;

import java.io.*;


public class JohnyWalker {

	private FileSystemTreeNode master;
	
	private File directory;
	
	private GraphingPanel x3;
	
	//
	
	private int fileCount, dirCount;
	
	public JohnyWalker(GraphingPanel x3) {
		
		this.directory = new File(System.getProperty("user.dir") + File.separator + "jbrofuzz" + File.separator + "fuzz");
		this.x3 = x3;
		
		if(this.directory.canRead()) {
			master = new FileSystemTreeNode(this.directory.getName());
			master.setAsDirectory();
		} else {
			this.x3.toConsole("Cannot read: " + this.directory.getPath(), true);
		}
		
		fileCount = 0;
		dirCount = 0;
		
	}
	
	public void run() {
		
		listAllFiles(this.directory, this.master);
		
		this.x3.toConsole("Total Files: " + fileCount, true);
		this.x3.toConsole("Total Directories: " + dirCount, true);
		
	}
	
	private void listAllFiles(File directory, FileSystemTreeNode parent) {
		
		if(!directory.canRead()) {
			this.x3.toConsole("Could not read: " + directory.getPath(), true);
			return;
		}
		
		if(this.x3.isStopped()) {
			return;
		}
		
		dirCount++;
		
		File [] children = directory.listFiles();
		
		for(File f : children) {
			
			FileSystemTreeNode node = new FileSystemTreeNode(f.getName());
			
			if(f.isDirectory()) {
				node.setAsDirectory();
				parent.add(node);
				dirCount++;
				listAllFiles(f, node);
			} 
			else if(!f.isDirectory()) {
				parent.add(node);
				fileCount++;
			}
		}
		
	}
		
	public FileSystemTreeNode getFileSystemTreeNode() {
		return master;
	}
	
	public int getMaximum() {
		
		return this.directory.listFiles().length;
		
	}

}
