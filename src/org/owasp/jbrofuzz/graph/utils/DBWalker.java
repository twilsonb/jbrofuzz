package org.owasp.jbrofuzz.graph.utils;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.owasp.jbrofuzz.graph.FileSystemTreeNode;
import org.owasp.jbrofuzz.graph.GraphingPanel;
import org.owasp.jbrofuzz.io.StorageHandler;
import org.owasp.jbrofuzz.system.Logger;


public class DBWalker {

		private FileSystemTreeNode master;

		// private final File directory;
		private String[] sessionids = null;

		private final GraphingPanel gPanel;

		//

		private int fileCount, dirCount;

		public DBWalker(final GraphingPanel gPanel) {
			// sessionids = this.gPanel.getFrame().getJBroFuzz().getStorageHandler().readTableRow("Select sessionId from session");
			
			StorageHandler sh = new StorageHandler();
			sessionids = sh.readTableRow("Select sessionId from session");
			
			Logger.log("received: "  + sessionids.length + " sessionIds.", 3);
			
			master = new FileSystemTreeNode("DATABASE");
			
			for (int i = 0; i < sessionids.length; i++){
				master.add(new FileSystemTreeNode(sessionids[i]));
			}
			
			this.gPanel = gPanel;
			master.setAsDirectory();

			Logger.log("Constructor: DBWalker: SessionIds: " + sessionids, 3);
			fileCount = 0;
			dirCount = 0;

		}

		public FileSystemTreeNode getFileSystemTreeNode() {
			return master;
		}

		public int getMaximum() {

			if (sessionids != null) {
				return sessionids.length;
			}
			else{
				return 0;
			}

		}
		
		private void listAllFiles(final FileSystemTreeNode parent) {
			if (gPanel.isStoppedEnabled()) {
				return;
			}

			StorageHandler sh = new StorageHandler();
			sessionids = sh.readTableRow("Select sessionId from session");
			
			for (int i = 0; i < sessionids.length; i++){
				parent.add(new FileSystemTreeNode(sessionids[i]));
			}
			
			Logger.log("listAllFiles: SessionIds: " + sessionids, 3);
			dirCount = 1;
			fileCount = sessionids.length;
		}

		public void run() {

			listAllFiles(master);
			
			gPanel.toConsole("Total Files: " + fileCount);
			gPanel.toConsole("Total Directories: " + dirCount);

		}
	}