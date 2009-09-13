/**
 * JBroFuzz 1.6
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
package org.owasp.jbrofuzz.headers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.graph.FileSystemTreeModel;
import org.owasp.jbrofuzz.graph.FileSystemTreeNode;
import org.owasp.jbrofuzz.ui.JBroFuzzPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TextHighlighter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The headers panel showing the headers in their corresponding categories.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.4
 */
public class HeadersPanel extends JBroFuzzPanel implements
		TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1835892912239470843L;
	// The split pane at the centre of the screen
	private JSplitPane mainSplitPanel, rVSplitPanel, rHSplitPanel;
	// The main file tree object
	private JTree tree;
	// The progress bar displayed
	private JProgressBar progressBar;
	// A boolean to check if we are running or not
	private boolean stopped;
	// The header, info and comment text area
	private NonWrappingTextPane hTxTArea, iTxTArea, cTxTArea;
	// The header's loader
	private HeaderLoader mHeadersLoader;

	public HeadersPanel(final JBroFuzzWindow m) {

		super(" Headers ", m);
		setLayout(new BorderLayout());

		stopped = true;

		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, true, false, false);

		mHeadersLoader = new HeaderLoader();
		// The left hand side tree and friends

		tree = new JTree(new FileSystemTreeModel(new FileSystemTreeNode("...")));
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tree.addTreeSelectionListener(this);

		JScrollPane treeScrollPanel = new JScrollPane(tree);
		treeScrollPanel.setVerticalScrollBarPolicy(20);
		treeScrollPanel.setHorizontalScrollBarPolicy(30);

		final JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" "), BorderFactory.createEmptyBorder(5, 5,
				5, 5)));

		treePanel.add(treeScrollPanel);

		// The right hand side header area
		hTxTArea = new NonWrappingTextPane();
		hTxTArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		hTxTArea = new NonWrappingTextPane();
		hTxTArea.putClientProperty("charset", "UTF-8");
		hTxTArea.setEditable(true);
		hTxTArea.setVisible(true);
		hTxTArea.setFont(new Font("Verdana", Font.PLAIN, 12));

		hTxTArea.setMargin(new Insets(1, 1, 1, 1));
		hTxTArea.setBackground(Color.WHITE);
		hTxTArea.setForeground(Color.BLACK);
		// Set the editor kit responsible for highlighting
		hTxTArea.setEditorKit(new StyledEditorKit() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4550261337511448681L;

			@Override
			public Document createDefaultDocument() {
				return new TextHighlighter();
			}

		});

		// Right click: Cut, Copy, Paste, Select All
		popupText(hTxTArea, true, true, true, true);

		JScrollPane hScrollPane = new JScrollPane(hTxTArea);
		hScrollPane.setVerticalScrollBarPolicy(20);
		hScrollPane.setHorizontalScrollBarPolicy(30);

		final JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Header "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		headerPanel.add(hScrollPane);

		// The right hand side information area
		iTxTArea = new NonWrappingTextPane();
		iTxTArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		iTxTArea.setEditable(false);
		iTxTArea.setFont(new Font("Verdana", Font.BOLD, 10));
		// Right click: Cut, Copy, Paste, Select All
		popupText(iTxTArea, false, true, false, true);

		JScrollPane iScrollPane = new JScrollPane(iTxTArea);
		iScrollPane.setVerticalScrollBarPolicy(20);
		iScrollPane.setHorizontalScrollBarPolicy(30);

		final JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Information "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		infoPanel.add(iScrollPane);

		// The right hand side comment area
		cTxTArea = new NonWrappingTextPane();
		cTxTArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cTxTArea.setEditable(false);
		cTxTArea.setFont(new Font("Verdana", Font.BOLD, 10));
		// Right click: Cut, Copy, Paste, Select All
		popupText(cTxTArea, false, true, false, true);

		JScrollPane cScrollPane = new JScrollPane(cTxTArea);
		cScrollPane.setVerticalScrollBarPolicy(20);
		cScrollPane.setHorizontalScrollBarPolicy(30);

		final JPanel commentPanel = new JPanel(new BorderLayout());
		commentPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Comment "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		commentPanel.add(cScrollPane);

		// The right horizontal split panel
		rHSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rHSplitPanel.setOneTouchExpandable(false);
		rHSplitPanel.setLeftComponent(headerPanel);
		rHSplitPanel.setRightComponent(infoPanel);

		// The right vertical split panel
		rVSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rVSplitPanel.setOneTouchExpandable(false);
		rVSplitPanel.setTopComponent(rHSplitPanel);
		rVSplitPanel.setBottomComponent(commentPanel);

		// The main split pane and friends
		mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPanel.setOneTouchExpandable(false);
		mainSplitPanel.setLeftComponent(treePanel);
		mainSplitPanel.setRightComponent(rVSplitPanel);

		// Set the divider locations, relative to the screen size
		final Dimension scr_res = JBroFuzzFormat.getScreenSize();
		if ((scr_res.width == 0) || (scr_res.height == 0)) {

			rHSplitPanel.setDividerLocation(430);
			rVSplitPanel.setDividerLocation(350);
			mainSplitPanel.setDividerLocation(300);

		} else {

			final int window_width = scr_res.width - 200;
			final int window_height = scr_res.height - 200;
			// Check that the screen is width/length is +tive
			if ((window_width > 0) && (window_height > 0)) {

				rHSplitPanel.setDividerLocation(window_width * 2 / 5);
				rVSplitPanel.setDividerLocation(window_height / 2);
				mainSplitPanel.setDividerLocation(window_height / 2);

				// topPane.setDividerLocation( window_width * 2 / 3 );
				// mainPane.setDividerLocation( window_height / 2 );

			} else {

				rHSplitPanel.setDividerLocation(430);
				rVSplitPanel.setDividerLocation(350);
				mainSplitPanel.setDividerLocation(300);

			}
		}

		// Allow for all areas to be resized to even not be seen
		Dimension minimumSize = new Dimension(0, 0);
		treePanel.setMinimumSize(minimumSize);
		commentPanel.setMinimumSize(minimumSize);
		infoPanel.setMinimumSize(minimumSize);
		headerPanel.setMinimumSize(minimumSize);

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

		// Load and expand the tree
		mHeadersLoader.load();
		tree.setModel(new DefaultTreeModel(mHeadersLoader.getMasterTreeNode()));
		// Traverse tree from root
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandOne(tree, new TreePath(root));
		// Select the first row
		tree.setSelectionRow(0);

	}

	@Override
	public void add() {
	}

	@Override
	public void graph() {
	}

	@Override
	public boolean isStoppedEnabled() {
		return stopped;
	}

	@Override
	public void remove() {
	}

	/**
	 * <p>
	 * Set the value to be displayed in the <code>Comment</code> JTextArea.
	 * </p>
	 * 
	 * @param t
	 *            The input String
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void setComment(String t) {
		cTxTArea.setText(t);
		cTxTArea.setCaretPosition(0);
	}

	/**
	 * <p>
	 * Set the value to be displayed in the <code>Header</code>
	 * NonWrappingTextPane.
	 * </p>
	 * 
	 * @param t
	 *            The input String
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void setHeader(String t) {

		hTxTArea.setText(t);
		hTxTArea.setCaretPosition(0);
	}

	/**
	 * <p>
	 * Set the value to be displayed in the <code>Information</code> JTextArea.
	 * </p>
	 * 
	 * @param t
	 *            The input String
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void setInformation(String t) {

		iTxTArea.setText(t);
		iTxTArea.setCaretPosition(0);

	}

	@Override
	public void start() {

		if (!stopped) {
			return;
		}
		stopped = false;

		// Set the options in the tool bar enabled at startup
		setOptionsAvailable(false, true, true, false, false);

		// Start to do what you need to do
		progressBar.setIndeterminate(true);

		mHeadersLoader.load();
		tree.setModel(new DefaultTreeModel(mHeadersLoader.getMasterTreeNode()));
		// Traverse tree from root
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), true);
		// Select the first row
		tree.setSelectionRow(0);
	}

	@Override
	public void stop() {

		if (stopped) {
			return;
		}
		stopped = true;

		// Set the options in the toolbar enabled at startup
		setOptionsAvailable(true, false, true, false, false);

		// Stop to do what you need to do
		progressBar.setIndeterminate(false);

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

		final TreePath selectedPath = tree.getSelectionPath();

		if (selectedPath == null)
			return;
		// More than 127 directories chill
		if (selectedPath.getPathCount() > Byte.MAX_VALUE) {
			HeadersPanel.this.getFrame().log(
					"Headers Panel: Path has more than 127 locations ", 3);
			return;
		}

		Header hd = mHeadersLoader.getHeader(selectedPath);
		setHeader(hd.getHeader());
		setInformation(hd.getInfo());
		setComment(hd.getComment());

	}

}
