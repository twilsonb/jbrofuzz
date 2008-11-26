/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 * 
 */
package org.owasp.jbrofuzz.graph;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.panels.JBroFuzzPanel;

/**
 * <p>The graphing panel, attached to the main panel.</p>
 * 
 * @author subere@uncon.org
 * @version 1.2
 * @since 1.2
 */
public class GraphingPanel extends JBroFuzzPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3962672183042644437L;
	
	// The split pane at the centre of the screen
	private JSplitPane mainSplitPanel;
	// The main file tree object
	private JTree tree;
	// The progress bar displayed
	private JProgressBar progressBar;
	// A boolean to check if we are running or not
	private boolean stopped;
	// Console related trends
	private JTextArea console;
	
	
	/**
	 * The constructor for the Graphing Panel. This constructor spawns the
	 * main panel involving web directories.
	 * 
	 * @param m
	 *            FrameWindow
	 */
	public GraphingPanel(final JBroFuzzWindow m) {

		super(" Graphing ", m);
		setLayout(new BorderLayout());
		
		stopped = true;

		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, false, false, false);

		
		// The right hand side console and friends 
		console = new JTextArea();
		
		JScrollPane consoleScrollPanel = new JScrollPane(console);
		consoleScrollPanel.setVerticalScrollBarPolicy(20);
		consoleScrollPanel.setHorizontalScrollBarPolicy(30);
		
		final JPanel consolePanel = new JPanel(new BorderLayout());
		consolePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Console "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));
		
		consolePanel.add(consoleScrollPanel);
		
		// The right hand side tree and friends
		tree = new JTree(new FileSystemTreeModel(new FileSystemTreeNode("...")));
		
		JScrollPane treeScrollPanel = new JScrollPane(tree);
		treeScrollPanel.setVerticalScrollBarPolicy(20);
		treeScrollPanel.setHorizontalScrollBarPolicy(30);
		
		final JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Tree View "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));
		
		treePanel.add(treeScrollPanel);
		
		// The right hand side tab and friends
		JTabbedPane rightPanel = new JTabbedPane(JTabbedPane.TOP);
		rightPanel.add(" Tree ", treePanel);
		rightPanel.add(" Console ", consolePanel);
		
		// The main split pane and friends
		
		mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		
		mainSplitPanel.setOneTouchExpandable(false);
		mainSplitPanel.setDividerLocation(300);
		mainSplitPanel.setLeftComponent(rightPanel);
		mainSplitPanel.setRightComponent(new JButton());

		// Allow for all areas to be resized to even not be seen
		Dimension minimumSize = new Dimension(0, 0);
		rightPanel.setMinimumSize(minimumSize);
		
		// The bottom progress bar and friends
		progressBar = new JProgressBar();
		progressBar.setString("   ");
		progressBar.setStringPainted(true);
		progressBar.setBounds(410, 465, 120, 20);

		// Define the bottom panel with the progress bar
		final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(progressBar);
		
		// Add all the components to the main pane
		//pane.add(mX3RToolBar, BorderLayout.PAGE_START);
		this.add(mainSplitPanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		
		/*
		// Run the starter on a separate thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final SwingWorker3 worker = new SwingWorker3() {
										
					@Override
					public Object construct() {
						
						// Get the current panel inside the tab
						start2();
						return "start-menu-bar-return";
						
					}

					@Override
					public void finished() {
						
						stop2();
						
					}
				};
				worker.start();
			}
		});
		*/
	}








	/**
	 * Method triggered when the start button is pressed.
	 */
	public void start() {

		if (!stopped) {
			return;
		}	
		stopped = false;

		// Set the options in the tool bar enabled at startup
		setOptionsAvailable(false, true, false, false, false);

		// Start to do what you need to do
        progressBar.setIndeterminate(true);
        		
		// tree.setFileRoot(d);
		// tree.runFileRoot();
		JohnyWalker j = new JohnyWalker( this );
		j.run();
		tree.setModel(new FileSystemTreeModel(j.getFileSystemTreeNode()));
	}


	/**
	 * <p>
	 * Method for stopping the request iterator.
	 * </p>
	 */
	public void stop() {

		if (stopped) {
			return;
		}
		stopped = true;

		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, false, false, false);
		
		// Stop to do what you need to do
        progressBar.setIndeterminate(false);

	}
	

	public void graph() {
	}

	public void add() {
	}

	public void remove() {
	}
	
	public boolean isStopped() {
		return stopped;
	}

	public void setProgressBar(int current, int total) {
		progressBar.setMaximum(total);
		progressBar.setValue(current);
				
	}
	

	public void toConsole(String input, boolean include) {

		/*
		final Date current = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss", new Locale("en"));
		

		if(include) {
			// consoleEvent++;
			leftPanel.setTitleAt(1, " Console (" + consoleEvent + ") ");
		}
		*/

		// Use a FILO for the output to the console, never exceeding 500 lines
		if (console.getLineCount() > 500) {
			try {
				console.select(console.getLineStartOffset(0), console.getLineEndOffset( console.getLineCount() - 500 ));
				console.replaceSelection("...\n");
			}
			catch (BadLocationException e) {
				toConsole("Could not clear the console", true);
			}
		} 
		
		console.append("> " + input + "\n");
		console.setCaretPosition(console.getText().length());

	}
}
