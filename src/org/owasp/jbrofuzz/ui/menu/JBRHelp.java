/**
 * HelpAndFaq.java 0.7
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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
 */
package org.owasp.jbrofuzz.ui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * <p>
 * The about box used in the FrameWindow.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.7
 */
public class JBRHelp extends JDialog implements TreeSelectionListener {

	private static final long serialVersionUID = -7783429547227101140L;
	// Dimensions of the about box
	private static final int x = 650;
	private static final int y = 400;
	//
	private static String FILE_NOT_FOUND = "Help file could not be located.";
	
	// The buttons
	private JButton ok;
	// The tree
	private JTree tree;
	// The corresponding scroll panels
	private JScrollPane helpScrPane, webdScrPane, tcpsScrPane, tcpfScrPane,
			geneScrPane, sysmScrPane, osrcScrPane;
	// The main split pane
	private JSplitPane splitPane;

	public JBRHelp(final JFrame parent) {
		super(parent, " Help Topics ", true);
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		final URL helpURL = ClassLoader.getSystemClassLoader().getResource(
				"help/index.html");
		final URL webdURL = ClassLoader.getSystemClassLoader().getResource(
				"help/webd.html");
		final URL tcpsURL = ClassLoader.getSystemClassLoader().getResource(
				"help/tcps.html");
		final URL tcpfURL = ClassLoader.getSystemClassLoader().getResource(
				"help/tcpf.html");
		final URL geneURL = ClassLoader.getSystemClassLoader().getResource(
				"help/gene.html");
		final URL sysmURL = ClassLoader.getSystemClassLoader().getResource(
				"help/sysm.html");
		final URL osrcURL = ClassLoader.getSystemClassLoader().getResource(
				"help/osrc.html");

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("Help Topics");
		createNodes(top);

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
			helpPane = new JEditorPane(helpURL);
		} catch (final IOException e1) {
			helpPane = new JEditorPane();
			helpPane.setText(FILE_NOT_FOUND);
		}
		helpPane.setEditable(false);
		helpScrPane = new JScrollPane(helpPane);

		JEditorPane webdPane;
		try {
			webdPane = new JEditorPane(webdURL);
		} catch (final IOException e1) {
			webdPane = new JEditorPane();
			webdPane.setText(FILE_NOT_FOUND);
		}
		webdPane.setEditable(false);
		webdScrPane = new JScrollPane(webdPane);

		JEditorPane tcpfPane;
		try {
			tcpfPane = new JEditorPane(tcpfURL);
		} catch (final IOException e1) {
			tcpfPane = new JEditorPane();
			tcpfPane.setText(FILE_NOT_FOUND);
		}
		tcpfPane.setEditable(false);
		tcpfScrPane = new JScrollPane(tcpfPane);

		JEditorPane tcpsPane;
		try {
			tcpsPane = new JEditorPane(tcpsURL);
		} catch (final IOException e1) {
			tcpsPane = new JEditorPane();
			tcpsPane.setText(FILE_NOT_FOUND);
		}
		tcpsPane.setEditable(false);
		tcpsScrPane = new JScrollPane(tcpsPane);

		JEditorPane genePane;
		try {
			genePane = new JEditorPane(geneURL);
		} catch (final IOException e1) {
			genePane = new JEditorPane();
			genePane.setText(FILE_NOT_FOUND);
		}
		genePane.setEditable(false);
		geneScrPane = new JScrollPane(genePane);

		JEditorPane sysmPane;
		try {
			sysmPane = new JEditorPane(sysmURL);
		} catch (final IOException e1) {
			sysmPane = new JEditorPane();
			sysmPane.setText(FILE_NOT_FOUND);
		}
		sysmPane.setEditable(false);
		sysmScrPane = new JScrollPane(sysmPane);

		JEditorPane osrcPane;
		try {
			osrcPane = new JEditorPane(osrcURL);
		} catch (final IOException e1) {
			osrcPane = new JEditorPane();
			osrcPane.setText(FILE_NOT_FOUND);
		}
		osrcPane.setEditable(false);
		osrcScrPane = new JScrollPane(osrcPane);

		// Create the top split pane, showing the treeView and the Preferences
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(helpScrPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		final Dimension minimumSize = new Dimension(JBRHelp.x / 4, JBRHelp.y / 2);
		helpScrPane.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(JBRHelp.x, JBRHelp.y));

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Bottom button
		ok = new JButton("  OK  ");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15,
				15));
		buttonPanel.add(ok);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRHelp.this.dispose();
					}
				});
			}
		});

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2) - (JBRHelp.x / 2 - 100)),
				Math.abs((parent.getHeight() / 2) - (JBRHelp.y / 2) + 100));
		this.setSize(JBRHelp.x, JBRHelp.y);
		setResizable(true);
		setVisible(true);
	}

	private void createNodes(final DefaultMutableTreeNode top) {
		
		DefaultMutableTreeNode leaf = null;

		leaf = new DefaultMutableTreeNode("Fuzzing");
		top.add(leaf);

		leaf = new DefaultMutableTreeNode("Sniffing");
		top.add(leaf);

		leaf = new DefaultMutableTreeNode("Payloads");
		top.add(leaf);

		leaf = new DefaultMutableTreeNode("Web Directories");
		top.add(leaf);

		leaf = new DefaultMutableTreeNode("System");
		top.add(leaf);
		
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
		}
		if (s.equalsIgnoreCase("Web Directories")) {
			splitPane.setRightComponent(webdScrPane);
		}
		if (s.equalsIgnoreCase("Fuzzing")) {
			splitPane.setRightComponent(tcpfScrPane);
		}
		if (s.equalsIgnoreCase("Sniffing")) {
			splitPane.setRightComponent(tcpsScrPane);
		}
		if (s.equalsIgnoreCase("Payloads")) {
			splitPane.setRightComponent(geneScrPane);
		}
		if (s.equalsIgnoreCase("System")) {
			splitPane.setRightComponent(sysmScrPane);
		}

	}
}
