/**
 * JBroFuzz 1.2
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
package org.owasp.jbrofuzz.help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;

/**
 * <p>The main help window.</p>
 * 
 * @author subere@uncon.org
 * @version 1.2
 */
public class Topics extends JFrame implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1726771399839929062L;

	// Dimensions of the about box
	private static final int x = 650;
	private static final int y = 400;
	//
	private static String FILE_NOT_FOUND = "Help file could not be located.";

	// The final String Array of tree nodes
	private static final String[] nodeNames = { 
		"Help Topics", "Fuzzing", "Graphing", 
		"Payloads", "Headers", "System" 
	};

	// The buttons
	private JButton ok;
	// The tree
	private JTree tree;
	// The corresponding scroll panels
	private JScrollPane helpScrPane, webdScrPane, tcpsScrPane, tcpfScrPane,
	geneScrPane, sysmScrPane;
	// The main split pane
	private JSplitPane splitPane;

	// The list of URLs
	private URL[] topicsURL;

	/**
	 * <p>Boolean is true if Topics are already showing.</p>
	 */
	public static boolean topicsShowing = false;

	/**
	 * <p>The constructor of the help topics JDialog.</p>
	 * 
	 * @param parent
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public Topics(final JBroFuzzWindow parent) {

		if(topicsShowing) {
			return;
		}
		topicsShowing = true;

		//super(parent, " JBroFuzz - Help Topics ", true);
		setTitle(" JBroFuzz - Help Topics ");

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		topicsURL = new URL[nodeNames.length];
		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				nodeNames[0]);
		
		for (int i = 0; i < nodeNames.length; i++) {
			topicsURL[i] = ClassLoader.getSystemClassLoader().getResource(
					"help/topics-0" + i + ".html");
			if(i > 0) {
				top.add(new DefaultMutableTreeNode(nodeNames[i]));
			}
		}

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it.
		final JScrollPane treeView = new JScrollPane(tree);

		JEditorPane helpPane;
		try {
			helpPane = new JEditorPane(topicsURL[0]);
		} catch (final IOException e1) {
			helpPane = new JEditorPane("text/html", FILE_NOT_FOUND);
			//helpPane.setText(FILE_NOT_FOUND);
		}
		helpPane.setEditable(false);
		helpScrPane = new JScrollPane(helpPane);

		JEditorPane webdPane;
		try {
			webdPane = new JEditorPane(topicsURL[2]);
		} catch (final IOException e1) {
			webdPane = new JEditorPane("text/html", FILE_NOT_FOUND);
			//webdPane.setText(FILE_NOT_FOUND);
		}
		webdPane.setEditable(false);
		webdScrPane = new JScrollPane(webdPane);

		JEditorPane tcpfPane;
		try {
			tcpfPane = new JEditorPane(topicsURL[1]);
		} catch (final IOException e1) {
			tcpfPane = new JEditorPane("text/html", FILE_NOT_FOUND);
			//tcpfPane.setText(FILE_NOT_FOUND);
		}
		tcpfPane.setEditable(false);
		tcpfScrPane = new JScrollPane(tcpfPane);

		JEditorPane tcpsPane;
		try {
			tcpsPane = new JEditorPane(topicsURL[3]);
		} catch (final IOException e1) {
			tcpsPane = new JEditorPane("text/html", FILE_NOT_FOUND);
			//tcpsPane.setText(FILE_NOT_FOUND);
		}
		tcpsPane.setEditable(false);
		tcpsScrPane = new JScrollPane(tcpsPane);

		JEditorPane genePane;
		try {
			genePane = new JEditorPane(topicsURL[4]);
		} catch (final IOException e1) {
			genePane = new JEditorPane("text/html", FILE_NOT_FOUND);
			//genePane.setText(FILE_NOT_FOUND);
		}
		genePane.setEditable(false);
		geneScrPane = new JScrollPane(genePane);

		JEditorPane sysmPane;
		try {
			sysmPane = new JEditorPane(topicsURL[5]);
		} catch (final IOException e1) {
			sysmPane = new JEditorPane("text/html", FILE_NOT_FOUND);
			// sysmPane.setText(FILE_NOT_FOUND);
		}
		sysmPane.setEditable(false);
		sysmScrPane = new JScrollPane(sysmPane);

		// Create the top split pane, showing the treeView and the Preferences
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(helpScrPane);
		splitPane.setOneTouchExpandable(false);

		final Dimension minimumSize = new Dimension(0, 0);
		helpScrPane.setMinimumSize(minimumSize);
		webdScrPane.setMinimumSize(minimumSize);
		tcpsScrPane.setMinimumSize(minimumSize);
		tcpfScrPane.setMinimumSize(minimumSize);
		geneScrPane.setMinimumSize(minimumSize);
		sysmScrPane.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(Topics.x, Topics.y));

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Bottom button
		ok = new JButton("  OK  ");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(ok);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				topicsShowing = false;
				Topics.this.dispose();

			}
		});

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		splitPane.setDividerLocation(150);
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math
				.abs(parent.getLocation().y + 100));

		this.setSize(Topics.x, Topics.y);
		this.setMinimumSize(new Dimension(x / 2, y / 2));

		setResizable(true);
		setVisible(true);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				topicsShowing = false;
				dispose();
			}
		});


	}

	public void valueChanged(final TreeSelectionEvent e) {

		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();
		if (s.equalsIgnoreCase("Help Topics")) {
			splitPane.setRightComponent(helpScrPane);
			splitPane.setDividerLocation(150);

		}
		if (s.equalsIgnoreCase("Graphing")) {
			splitPane.setRightComponent(webdScrPane);
			splitPane.setDividerLocation(150);

		}
		if (s.equalsIgnoreCase("Fuzzing")) {
			splitPane.setRightComponent(tcpfScrPane);
			splitPane.setDividerLocation(150);

		}
		if (s.equalsIgnoreCase("Headers")) {
			splitPane.setRightComponent(tcpsScrPane);
			splitPane.setDividerLocation(150);

		}
		if (s.equalsIgnoreCase("Payloads")) {
			splitPane.setRightComponent(geneScrPane);
			splitPane.setDividerLocation(150);

		}
		if (s.equalsIgnoreCase("System")) {
			splitPane.setRightComponent(sysmScrPane);
			splitPane.setDividerLocation(150);

		}

	}
}
