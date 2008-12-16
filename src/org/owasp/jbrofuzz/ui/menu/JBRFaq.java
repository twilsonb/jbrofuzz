/**
 * JBroFuzz 1.0
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

import org.owasp.jbrofuzz.util.ImageCreator;

public class JBRFaq extends JDialog implements TreeSelectionListener {

	private static final long serialVersionUID = 4301858021356404678L;
	// Dimensions of the about box
	private static final int x = 650;
	private static final int y = 400;
	// The final String Array of tree nodes
	private static final String[] nodeNames = { "FAQ", "System Requirements", "Java Issues",
			"Installation", "Files & Directories", "What is a Fuzzer?",
			"Sniffing Ports" };
	// The buttons
	private JButton ok;
	// The tree
	private JTree tree;
	// The JEditorPane
	private JEditorPane faqEditorPane;
	// The corresponding scroll pane
	private JScrollPane faqScrollPane;
	// The main split pane
	private JSplitPane splitPane;
	// The list of URLs
	private URL[] faqURL;

	public JBRFaq(final JFrame parent) {

		super(parent, " JBroFuzz - Frequently Asked Questions ", true);
		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		faqURL = new URL[nodeNames.length];
		// faqURL[0] =
		// ClassLoader.getSystemClassLoader().getResource("help/faq-00.html");
		for (int i = 0; i < nodeNames.length; i++) {
			if (i < 10) {
				faqURL[i] = ClassLoader.getSystemClassLoader().getResource(
						"help/faq-0" + i + ".html");
			} else {
				faqURL[i] = ClassLoader.getSystemClassLoader().getResource(
						"help/faq-" + i + ".html");
			}
		}

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				nodeNames[0]);
		for (int i = 1; i < nodeNames.length; i++) {
			top.add(new DefaultMutableTreeNode(nodeNames[i]));
		}

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it.
		final JScrollPane treeView = new JScrollPane(tree);

		try {
			faqEditorPane = new JEditorPane(faqURL[0]);
		} catch (final IOException e1) {
			faqEditorPane = new JEditorPane();
			faqEditorPane
					.setText("Frequently Asked Questions file could not be located.");
		}
		faqScrollPane = new JScrollPane(faqEditorPane);

		// Create the Split Pane, showing the tree view
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(faqScrollPane);
		splitPane.setOneTouchExpandable(false);

		final Dimension minimumSize = new Dimension(0, 0);
		faqScrollPane.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(100, 0));

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Bottom button
		ok = new JButton("  OK  ");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(ok);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRFaq.this.dispose();
					}
				});
			}
		});

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		splitPane.setDividerLocation(150);
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math
				.abs(parent.getLocation().y + 100));
		this.setSize(JBRFaq.x, JBRFaq.y);
		this.setMinimumSize(new Dimension(x, y));
		setResizable(true);
		setVisible(true);
	}

	public void valueChanged(final TreeSelectionEvent e) {

		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();

		for (int i = 0; i < nodeNames.length; i++) {

			if (s.equalsIgnoreCase(nodeNames[i])) {
				try {
					faqEditorPane.setPage(faqURL[i]);
				} catch (IOException e1) {
					faqEditorPane.setText("Could not find page: " + faqURL[i]);
				}
				faqScrollPane.setViewportView(faqEditorPane);
				splitPane.setRightComponent(faqScrollPane);
				splitPane.setDividerLocation(150);
			}

		} // for loop

	} // value changed
}
