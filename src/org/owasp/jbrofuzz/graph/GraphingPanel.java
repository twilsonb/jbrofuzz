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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;

import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The graphing panel, attached to the main panel.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class GraphingPanel extends AbstractPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3962672183042644437L;

	// The split pane at the centre of the screen
	private JSplitPane mainSplitPanel;
	// The main file tree object
	private FileSystemTree tree;
	// The progress bar displayed
	private JProgressBar progressBar;
	// A boolean to check if we are running or not
	private boolean stopped;
	// Console related trends
	private JTextArea console;
	// The right tabs holding the graphs
	private TabbedPlotter rightPanel;

	/**
	 * The constructor for the Graphing Panel. This constructor spawns the main
	 * panel involving web directories.
	 * 
	 * @param m
	 * 
	 */
	public GraphingPanel(final JBroFuzzWindow m) {

		super(" Graphing ", m);
		setLayout(new BorderLayout());

		stopped = true;

		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, true, false, false);

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
		tree = new FileSystemTree(this, new FileSystemTreeModel(
				new FileSystemTreeNode("...")));

		JScrollPane treeScrollPanel = new JScrollPane(tree);
		treeScrollPanel.setVerticalScrollBarPolicy(20);
		treeScrollPanel.setHorizontalScrollBarPolicy(30);

		final JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Tree View "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		treePanel.add(treeScrollPanel);

		// The left hand side tab and friends
		JTabbedPane leftPanel = new JTabbedPane(SwingConstants.TOP);
		leftPanel.add(" Tree ", treePanel);
		leftPanel.add(" Console ", consolePanel);

		// The right hand side plot panel
		rightPanel = new TabbedPlotter(this);

		// The main split pane and friends
		mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		mainSplitPanel.setOneTouchExpandable(false);
		mainSplitPanel.setDividerLocation(300);
		mainSplitPanel.setLeftComponent(leftPanel);
		mainSplitPanel.setRightComponent(rightPanel);

		// Allow for all areas to be resized to even not be seen
		leftPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		rightPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		// The bottom progress bar and friends
		progressBar = new JProgressBar();
		progressBar.setString("   ");
		progressBar.setStringPainted(true);
		progressBar.setBounds(410, 465, 120, 20);

		// Define the bottom panel with the progress bar
		final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(progressBar);

		// Add all the components to the main pane
		this.add(mainSplitPanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);

	}

	@Override
	public void add() {
	}

	/**
	 * <p>
	 * Method for returning the tabbed plotter within the Graphing Panel.
	 * </p>
	 * 
	 * @return TabbedPlotter used in the Graphing Panel
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public TabbedPlotter getTabbedPlotter() {

		return rightPanel;
	}

	@Override
	public void pause() {
	}

	@Override
	public boolean isStoppedEnabled() {
		return stopped;
	}

	@Override
	public void remove() {
	}

	protected void setProgressBarStart() {

		progressBar.setIndeterminate(true);

	}

	protected void setProgressBarStop() {

		progressBar.setIndeterminate(false);

	}

	/**
	 * Method triggered when the start button is pressed.
	 */
	@Override
	public void start() {

		if (!stopped) {
			return;
		}
		stopped = false;

		// Set the options in the tool bar enabled at startup
		setOptionsAvailable(false, true, true, false, false);

		// Start to do what you need to do
		setProgressBarStart();

		final JohnyWalker jWalker = new JohnyWalker(this);
		jWalker.run();
		tree.setModel(new FileSystemTreeModel(jWalker.getFileSystemTreeNode()));

	}

	/**
	 * <p>
	 * Method for stopping the request iterator.
	 * </p>
	 */
	@Override
	public void stop() {

		if (stopped) {
			return;
		}
		stopped = true;

		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, true, false, false);

		// Stop to do what you need to do
		setProgressBarStop();

	}

	/**
	 * <p>Write output to the console of the Graphing Panel.</p>
	 * 
	 * @param input
	 */
	protected void toConsole(final String input) {

		// Use a FILO for the output to the console, never exceeding 500 lines
		if (console.getLineCount() > 500) {
			try {
				console.select(console.getLineStartOffset(0), console
						.getLineEndOffset(console.getLineCount() - 500));
				console.replaceSelection("...\n");
			} catch (BadLocationException e) {
				getFrame().log("Could not clear the console", 3);
			}
		}

		console.append("> " + input + "\n");
		console.setCaretPosition(console.getText().length());

	}
}
