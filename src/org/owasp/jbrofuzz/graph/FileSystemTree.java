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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.ui.viewers.WindowViewerFrame;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

import com.Ostermiller.util.Browser;

class FileSystemTree extends JTree implements MouseListener {

	private static final long serialVersionUID = -4289004118182074303L;

	private static final String GR_PANEL = "Graphing Panel: ";
	
	private final JPopupMenu popmenu;

	protected FileSystemTree(final GraphingPanel graphingPanel,
			final FileSystemTreeModel fsTreeModel) {

		super(fsTreeModel);

		popmenu = new JPopupMenu();

		final JMenuItem i0_graph = new JMenuItem("Graph");
		final JMenuItem i1_open_folder = new JMenuItem("Open Containing Folder");
		final JMenuItem i2_open_browser = new JMenuItem("Open in Browser");
		final JMenuItem i3_open_viewer = new JMenuItem("Open in Viewer");
		final JMenuItem i4_copy = new JMenuItem("Copy");

		i2_open_browser.setIcon(ImageCreator.IMG_OPENINBROWSER);
		i4_copy.setIcon(ImageCreator.IMG_COPY);

		popmenu.add(i0_graph);
		popmenu.addSeparator();
		popmenu.add(i1_open_folder);
		popmenu.add(i2_open_browser);
		popmenu.add(i3_open_viewer);
		popmenu.addSeparator();
		popmenu.add(i4_copy);

		// Graph
		i0_graph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final String stringFile = 
					getFileFromPath(FileSystemTree.this.getSelectionPath());
				
				if(stringFile.startsWith(GR_PANEL)) {
					
					// An error occured
					graphingPanel.getFrame().log(stringFile, 4);
					
				} else {
					
					final File pathFile = new File(stringFile);
					
					if (pathFile.isFile()) {
						graphingPanel.getFrame()
						.log(GR_PANEL + "Cannot graph individual files",	4);
						
					} else {

						// Let the graphing begin
						graphingPanel.getTabbedPlotter().plot(pathFile);

					}
					
				}


			}

		});

		// Open Containing Folder
		i1_open_folder.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent openEvt) {

				final String stringFile = 
					getFileFromPath(FileSystemTree.this.getSelectionPath());
				
				if(stringFile.startsWith(GR_PANEL)) {
					
					// An error occured
					graphingPanel.getFrame().log(stringFile, 4);
					
				} else {
					
					final File pathFile = new File(stringFile);
					final File parentFile = pathFile.getParentFile();

					Browser.init();
					
					try {
						
						Browser.displayURL(parentFile.toURI().toString());
						
					} catch (final IOException ex) {
						graphingPanel.getFrame()
						.log(GR_PANEL + 
								"Could not open containing folder: " + parentFile.toString(),
								3);
					}
					
				}

			}
		});
		
		// Open in Browser
		i2_open_browser.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent openEvt) {

				final String stringFile = 
					getFileFromPath(FileSystemTree.this.getSelectionPath());
				
				if(stringFile.startsWith(GR_PANEL)) {
					
					// An error occured
					graphingPanel.getFrame().log(stringFile, 4);
					
				} else {
					
					final File pathFile = new File(stringFile);
					Browser.init();
					
					try {
						
						Browser.displayURL(pathFile.toURI().toString());
						
					} catch (final IOException ex) {
						graphingPanel.getFrame().log(
								GR_PANEL + "Could not open file in browser: " + pathFile.toString(),
							3
						);
					}
					
				}

			}
		});
		
		// Open in Viewer
		i3_open_viewer.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent openEvt) {

				final String stringFile = 
					getFileFromPath(FileSystemTree.this.getSelectionPath());
				
				if(stringFile.startsWith(GR_PANEL)) {
					
					// An error occured
					graphingPanel.getFrame().log(stringFile, 4);
					
				} else {
					
					new WindowViewerFrame(graphingPanel, new File(stringFile));
					
				}

			}
		});
				
		// Copy
		i4_copy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent cpEvent) {

				final String stringFile = 
					getFileFromPath(FileSystemTree.this.getSelectionPath());

				if(stringFile.startsWith(GR_PANEL)) {
					
					// An error occured
					graphingPanel.getFrame().log(stringFile, 4);
					
				} else {

					final JTextArea myTempArea = new JTextArea(stringFile);
					myTempArea.selectAll();
					myTempArea.copy();
				}
			} // actionPerformed

		});


		addMouseListener(this);

	}

	/**
	 * <p>Method for obtaining the file location from the tree
	 * path specified.</p>
	 * 
	 * <p>In the event of an error, this method returns a 
	 * String that starts with "Graphing Panel:"</p>
	 * 
	 * @param selectedPath The selected path on the Tree.
	 * 
	 * @return String The toString() of the file location, or a 
	 * value  that starts with "Graphing Panel" in case of an
	 * error.
	 */
	private final String getFileFromPath(final TreePath selectedPath) {
		
		if (selectedPath == null) {
			return GR_PANEL + "The selected path is null";
		}
		// More than 32 directories chill
		if (selectedPath.getPathCount() > 32) {
			return GR_PANEL + "Path has more than 32 locations";
		}

		final Object[] path = selectedPath.getPath();

		// Get the directory location from preferences
		final String dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[1], System.getProperty("user.dir"));

		// Get the file path
		final StringBuffer stringPath = new StringBuffer(dirString);
		stringPath.append(File.separator);
		stringPath.append("jbrofuzz");

		for (Object node : path) {

			stringPath.append(System.getProperty("file.separator"));
			stringPath.append(node.toString());

		}

		final File pathFile = new File(stringPath.toString());
		// If we are talking about something non-existent, we don't want
		// to know
		if (!pathFile.exists()) {
			return GR_PANEL + "Path does not exist: " + pathFile;
		}
		// Similar if we cannot execute the location
		if (!pathFile.canExecute()) {
			return GR_PANEL + "Path cannot be executed: " + pathFile;
		}
		
		return pathFile.toString();
		
	}
	
	private void checkForTriggerEvent(final MouseEvent mEvent1) {
		if (mEvent1.isPopupTrigger()) {
			final TreePath selPath = getPathForLocation(mEvent1.getX(), mEvent1.getY());
			setSelectionPath(selPath);
			popmenu.show(mEvent1.getComponent(), mEvent1.getX(), mEvent1.getY());
		}
	}

	@Override
	public void mouseClicked(final MouseEvent mEvent2) {
		checkForTriggerEvent(mEvent2);

	}

	@Override
	public void mouseEntered(final MouseEvent mEvent3) {
		checkForTriggerEvent(mEvent3);

	}

	@Override
	public void mouseExited(final MouseEvent mEvent4) {
		checkForTriggerEvent(mEvent4);

	}

	@Override
	public void mousePressed(final MouseEvent mEvent5) {
		checkForTriggerEvent(mEvent5);
	}

	@Override
	public void mouseReleased(final MouseEvent mEvent6) {
		checkForTriggerEvent(mEvent6);
	}

}
