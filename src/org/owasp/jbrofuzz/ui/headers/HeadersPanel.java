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
package org.owasp.jbrofuzz.ui.headers;

import java.awt.*;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.graph.FileSystemTreeModel;
import org.owasp.jbrofuzz.graph.FileSystemTreeNode;
import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.panels.JBroFuzzPanel;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TextHighlighter;

/**
 * <p>
 * The headers panel showing the headers in their corresponding 
 * categories.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.2
 */
public class HeadersPanel extends JBroFuzzPanel implements TreeSelectionListener {
	
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
		setOptionsAvailable(true, false, false, false, false);

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
				.createTitledBorder(" "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));
		
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
		rHSplitPanel.setDividerLocation(430);
		rHSplitPanel.setLeftComponent(headerPanel);
		rHSplitPanel.setRightComponent(infoPanel);
		
		// The right vertical split panel
		rVSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rVSplitPanel.setOneTouchExpandable(false);
		rVSplitPanel.setDividerLocation(350);
		rVSplitPanel.setTopComponent(rHSplitPanel);
		rVSplitPanel.setBottomComponent(commentPanel);
		
		
		// The main split pane and friends
		mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPanel.setOneTouchExpandable(false);
		mainSplitPanel.setDividerLocation(300);
		mainSplitPanel.setLeftComponent(treePanel);
		mainSplitPanel.setRightComponent(rVSplitPanel);

		
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
		TreeNode root = (TreeNode)tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), true);
        // Select the first row
        tree.setSelectionRow(0);

	}

	
	public void start() {

		if (!stopped) {
			return;
		}	
		stopped = false;

		// Set the options in the tool bar enabled at startup
		setOptionsAvailable(false, true, false, false, false);

		// Start to do what you need to do
        progressBar.setIndeterminate(true);
        		
        mHeadersLoader.load();
		tree.setModel(new DefaultTreeModel(mHeadersLoader.getMasterTreeNode()));
        // Traverse tree from root
		TreeNode root = (TreeNode)tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), true);
        // Select the first row
        tree.setSelectionRow(0);
	}


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

	
	/**
	 * <p>Set the value to be displayed in the 
	 * <code>Header</code> NonWrappingTextPane.</p>
	 * 
	 * @param t The input String
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void setHeader(String t) {
		
		hTxTArea.setText(t);
		hTxTArea.setCaretPosition(0);
	}
	
	
	/**
	 * <p>Set the value to be displayed in the 
	 * <code>Information</code> JTextArea.</p>
	 * 
	 * @param t The input String
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void setInformation(String t) {
		
		iTxTArea.setText(t);
		iTxTArea.setCaretPosition(0);
		
	}
	
	/**
	 * <p>Set the value to be displayed in the 
	 * <code>Comment</code> JTextArea.</p>
	 * 
	 * @param t The input String
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public void setComment(String t) {
		cTxTArea.setText(t);
		cTxTArea.setCaretPosition(0);
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {

		final HeaderTreeNode node = (HeaderTreeNode) tree.getLastSelectedPathComponent();
		
		if (node == null) {
			return;
		}

		
		if (node.isLeaf()) {
			Header hd = mHeadersLoader.getHeader(node.getPath());
			this.setHeader(hd.getHeader());
			this.setInformation(hd.getInfo());
			this.setComment(hd.getComment());
		}

		
	}

}
