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
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.version.ImageCreator;

import com.Ostermiller.util.Browser;

class FileSystemTree extends JTree implements MouseListener {

	private static final long serialVersionUID = -4289004118182074303L;

	private final JPopupMenu popmenu;

	protected FileSystemTree(final GraphingPanel graphingPanel,
			FileSystemTreeModel fileSystemTreeModel) {

		super(fileSystemTreeModel);

		popmenu = new JPopupMenu();

		final JMenuItem i0_graph = new JMenuItem("Graph");
		final JMenuItem i1_cut = new JMenuItem("Cut");
		final JMenuItem i2_copy = new JMenuItem("Copy");
		final JMenuItem i3_paste = new JMenuItem("Paste");
		final JMenuItem i4_select = new JMenuItem("Select All");
		final JMenuItem i5_open = new JMenuItem("Open in Browser");

		// i0_graph.setIcon(ImageCreator.IMG_PAUSE);
		i1_cut.setIcon(ImageCreator.IMG_CUT);
		i2_copy.setIcon(ImageCreator.IMG_COPY);
		i3_paste.setIcon(ImageCreator.IMG_PASTE);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);
		i5_open.setIcon(ImageCreator.IMG_OPENINBROWSER);

		i0_graph.setEnabled(true);
		i1_cut.setEnabled(false);
		i2_copy.setEnabled(true);
		i3_paste.setEnabled(false);
		i4_select.setEnabled(false);
		i5_open.setEnabled(true);

		popmenu.add(i0_graph);
		popmenu.addSeparator();
		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.add(i4_select);
		popmenu.addSeparator();
		popmenu.add(i5_open);

		// Graph
		i0_graph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final TreePath selectedPath = FileSystemTree.this
				.getSelectionPath();

				if (selectedPath == null)
					return;
				// More than 32 directories chill
				if (selectedPath.getPathCount() > 32) {
					graphingPanel.getFrame().log(
							"Graphing Panel: Path has more than 32 locations ",
							3);
					return;
				}

				Object[] path = selectedPath.getPath();

				// Get the file path

				StringBuffer stringPath = new StringBuffer(System
						.getProperty("user.dir"));
				stringPath.append(File.separator);
				stringPath.append("jbrofuzz");

				for (Object node : path) {

					stringPath.append(System.getProperty("file.separator"));
					stringPath.append(node.toString());

				}

				File pathFile = new File(stringPath.toString());
				// If we are talking about something non-existent, we don't want
				// to know
				if (!pathFile.exists()) {
					graphingPanel.getFrame().log(
							"Graphing Panel: Path does not exist: " + pathFile,
							4);
					return;
				}
				// Similar if we cannot execute the location
				if (!pathFile.canExecute()) {
					graphingPanel.getFrame().log(
							"Graphing Panel: Path cannot be executed: "
							+ pathFile, 4);
					return;
				}
				// Or if its an individual file
				if (pathFile.isFile()) {
					graphingPanel
					.getFrame()
					.log(
							"Graphing Panel: Cannot graph individual files ",
							4);
					return;
				}

				// Let the graphing begin
				graphingPanel.getTabbedPlotter().plot(pathFile);

			}

		});

		// Copy
		i2_copy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				FileSystemTreeNode selectedNode = (FileSystemTreeNode) FileSystemTree.this
				.getLastSelectedPathComponent();

				if (selectedNode == null)
					return;

				final String s = selectedNode.toString();
				final JTextArea myTempArea = new JTextArea(s);
				myTempArea.selectAll();
				myTempArea.copy();

			}

		});

		i5_open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final TreePath selectedPath = FileSystemTree.this
				.getSelectionPath();

				if (selectedPath == null)
					return;
				// More than 32 directories chill
				if (selectedPath.getPathCount() > 32) {
					graphingPanel.getFrame().log(
							"Graphing Panel: Path has more than 32 locations ",
							4);
					return;
				}

				Object[] path = selectedPath.getPath();

				// Get the file path

				StringBuffer stringPath = new StringBuffer(System
						.getProperty("user.dir"));
				stringPath.append(File.separator);
				stringPath.append("jbrofuzz");

				for (Object node : path) {

					stringPath.append(System.getProperty("file.separator"));
					stringPath.append(node.toString());

				}

				File pathFile = new File(stringPath.toString());
				// If we are talking about something non-existent, we don't want
				// to know
				if (!pathFile.exists()) {
					graphingPanel.getFrame().log(
							"Graphing Panel: Path does not exist: " + pathFile,
							4);
					return;
				}
				// Similar if we cannot execute the location
				if (!pathFile.canExecute()) {
					graphingPanel.getFrame().log(
							"Graphing Panel: Path cannot be executed: "
							+ pathFile, 4);
					return;
				}

				// Go to the browser
				URL pathURL;
				try {

					pathURL = pathFile.toURI().toURL();
					Browser.init();
					Browser.displayURL(pathURL.toString());

				} catch (MalformedURLException e1) {
					graphingPanel
					.getFrame()
					.log(
							"Graphing Panel: Could not open location: Bad URL Location ",
							4);
					return;
				} catch (IOException e2) {
					graphingPanel
					.getFrame()
					.log(
							"Graphing Panel: Could not open location: IO Issues ",
							4);
					return;
				}

			}

		});

		addMouseListener(this);

	}

	private void checkForTriggerEvent(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			final TreePath selPath = getPathForLocation(e.getX(), e.getY());
			setSelectionPath(selPath);
			popmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		checkForTriggerEvent(e);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		checkForTriggerEvent(e);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		checkForTriggerEvent(e);

	}

	@Override
	public void mousePressed(final MouseEvent e) {
		checkForTriggerEvent(e);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		checkForTriggerEvent(e);
	}

}
