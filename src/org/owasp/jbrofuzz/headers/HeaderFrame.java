/**
 * JBroFuzz 2.4
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
package org.owasp.jbrofuzz.headers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.graph.FileSystemTreeModel;
import org.owasp.jbrofuzz.graph.FileSystemTreeNode;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.util.TextHighlighter;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>The headers window showing the headers in their 
 * corresponding categories.</p>
 * 
 * @author subere@uncon.org
 * @version 2.5
 * @since 1.9
 */
public class HeaderFrame extends JFrame implements TreeSelectionListener, KeyListener {

	private static final long serialVersionUID = 8707597613561230771L;

	// Dimensions of the frame
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;

	// The split pane at the centre of the screen
	private JSplitPane mainSplitPanel, rVSplitPanel, rHSplitPanel;
	// The main file tree object
	private JTree tree;
	// The header, info and comment text area
	private NonWrappingTextPane hTxTArea, iTxTArea, cTxTArea;
	// The header's loader
	private HeaderLoader mHeadersLoader;

	public HeaderFrame() {

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setJMenuBar(new HeaderMenuBar(this));
		setTitle(" JBroFuzz - Browser Headers ");
		setLayout(new BorderLayout());

		mHeadersLoader = new HeaderLoader();
		// The left hand side tree and friends

		tree = new JTree(new FileSystemTreeModel(new FileSystemTreeNode("...")));
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tree.addTreeSelectionListener(this);
		tree.addKeyListener(this);

		final JScrollPane treeScrollPanel = new JScrollPane(tree);
		treeScrollPanel.setVerticalScrollBarPolicy(20);
		treeScrollPanel.setHorizontalScrollBarPolicy(30);

		final JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" "), BorderFactory.createEmptyBorder(5, 5,
						5, 5)));

		treePanel.add(treeScrollPanel);

		// The right hand side header area
		hTxTArea = new NonWrappingTextPane();
		hTxTArea.addKeyListener(this);
		hTxTArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// hTxTArea = new NonWrappingTextPane();
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
		// popupText(hTxTArea, true, true, true, true);

		final JScrollPane hScrollPane = new JScrollPane(hTxTArea);
		hScrollPane.setVerticalScrollBarPolicy(20);
		hScrollPane.setHorizontalScrollBarPolicy(30);

		final JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Header "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		headerPanel.add(hScrollPane);

		// The right hand side information area
		iTxTArea = new NonWrappingTextPane();
		iTxTArea.addKeyListener(this);
		iTxTArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		iTxTArea.setEditable(false);
		iTxTArea.setFont(new Font("Verdana", Font.BOLD, 10));
		// Right click: Cut, Copy, Paste, Select All
		// popupText(iTxTArea, false, true, false, true);

		final JScrollPane iScrollPane = new JScrollPane(iTxTArea);
		iScrollPane.setVerticalScrollBarPolicy(20);
		iScrollPane.setHorizontalScrollBarPolicy(30);

		final JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Information "), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		infoPanel.add(iScrollPane);

		// The right hand side comment area
		cTxTArea = new NonWrappingTextPane();
		cTxTArea.addKeyListener(this);
		cTxTArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cTxTArea.setEditable(false);
		cTxTArea.setFont(new Font("Verdana", Font.BOLD, 10));
		// Right click: Cut, Copy, Paste, Select All
		// popupText(cTxTArea, false, true, false, true);

		final JScrollPane cScrollPane = new JScrollPane(cTxTArea);
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


		// Allow for all areas to be resized to even not be seen
		treePanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		commentPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		infoPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		headerPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		// The bottom progress bar and friends
		final JProgressBar progressBar = new JProgressBar();
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
		tree.setSelectionRow(JBroFuzz.PREFS.getInt("UI.H.HeaderSelection", 0));
		
		// Global frame issues & preferences

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent wEvent) {

				closeFrame();

			}
		});

		mainSplitPanel.setDividerLocation(JBroFuzz.PREFS.getInt("UI.H.mainSplitPanel", 162));
		rHSplitPanel.setDividerLocation(JBroFuzz.PREFS.getInt("UI.H.rHSplitPanel", 315));
		rVSplitPanel.setDividerLocation(JBroFuzz.PREFS.getInt("UI.H.rVSplitPanel", 242));

		// Minimum size window 127 x 127
		HeaderFrame.this.setMinimumSize(new Dimension(Byte.MAX_VALUE, Byte.MAX_VALUE));

		int xSize = JBroFuzz.PREFS.getInt("UI.H.Height", SIZE_X);
		if(xSize < Byte.MAX_VALUE) {
			xSize = Byte.MAX_VALUE;
		}

		int ySize = JBroFuzz.PREFS.getInt("UI.H.Width", SIZE_Y);
		if(ySize < Byte.MAX_VALUE) {
			ySize = Byte.MAX_VALUE;
		}

		HeaderFrame.this.setSize(ySize, xSize);

		// Where to show headers frame
//		HeaderFrame.this.setLocation(
//				parent.getLocation().x + (parent.getWidth() - xSize) / 2, 
//				parent.getLocation().y + (parent.getHeight() - ySize) / 2
//		);
		
		setResizable(true);
		setVisible(true);

	}

	final void closeFrame() {

		JBroFuzz.PREFS.putInt("UI.H.mainSplitPanel", mainSplitPanel.getDividerLocation());
		JBroFuzz.PREFS.putInt("UI.H.rHSplitPanel", rHSplitPanel.getDividerLocation());
		JBroFuzz.PREFS.putInt("UI.H.rVSplitPanel", rVSplitPanel.getDividerLocation());

		JBroFuzz.PREFS.putInt("UI.H.HeaderSelection", tree.getSelectionCount());
		
		JBroFuzz.PREFS.putInt("UI.H.Height", HeaderFrame.this.getSize().height);
		JBroFuzz.PREFS.putInt("UI.H.Width", HeaderFrame.this.getSize().width);

		HeaderFrame.this.dispose();

	}

	/**
	 * <p>
	 * Set the value to be displayed in the <code>Comment</code> JTextArea.
	 * </p>
	 * 
	 * @param comment
	 *            The input String
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public void setComment(final String comment) {
		cTxTArea.setText(comment);
		cTxTArea.setCaretPosition(0);
	}

	/**
	 * <p>
	 * Set the value to be displayed in the <code>Header</code>
	 * NonWrappingTextPane.
	 * </p>
	 * 
	 * @param header
	 *            The input String
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public void setHeader(final String header) {

		hTxTArea.setText(header);
		hTxTArea.setCaretPosition(0);
	}

	/**
	 * <p>
	 * Set the value to be displayed in the <code>Information</code> JTextArea.
	 * </p>
	 * 
	 * @param info
	 *            The input String
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public void setInformation(final String info) {

		iTxTArea.setText(info);
		iTxTArea.setCaretPosition(0);

	}


	@Override
	public void valueChanged(final TreeSelectionEvent tEvent) {

		final TreePath selectedPath = tree.getSelectionPath();

		if (selectedPath == null) {
			return;
		}

		// If more than 127 directories, chill...
		if (selectedPath.getPathCount() > Byte.MAX_VALUE) {
			return;
		}

		final Header cHeader = mHeadersLoader.getHeader(selectedPath);
		setHeader(cHeader.getHeader());
		setInformation(cHeader.getInfo());
		setComment(cHeader.getComment());

	}

	@Override
	public void keyTyped(final KeyEvent kEvent) {

		if (kEvent.getKeyCode() == 27) {

			closeFrame();

		}

	}

	@Override
	public void keyPressed(final KeyEvent kEvent) {

		if (kEvent.getKeyCode() == 27) {

			closeFrame();

		}
		
	}

	@Override
	public void keyReleased(final KeyEvent kEvent) {

		if (kEvent.getKeyCode() == 27) {

			closeFrame();

		}
		
	}

}
