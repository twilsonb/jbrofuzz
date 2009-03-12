/**
 * JBroFuzz 1.3
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
 * 
 * 
 * @author subere@uncon.org
 * @version 1.3
 * @since 1.2
 */
public class Faq extends JFrame implements TreeSelectionListener {

	private static final long			serialVersionUID	= 4301858021356404678L;

	// Dimensions of the about box
	private static final int			x									= 650;
	private static final int			y									= 400;

	// The final String Array of tree nodes
	private static final String[]	nodeNames					= { "FAQ",
			"System Requirements", "Java", "Installation", "Files & Directories",
			"Fuzzers & Payloads", "Older Features/Versions", "Proxy Settings" };

	/**
	 * <p>
	 * Boolean is true if FAQ is already showing.
	 * </p>
	 */
	private static boolean				faqShowing				= false;
	// The button
	private JButton								ok;
	// The tree
	private JTree									tree;
	// The JEditorPane
	private JEditorPane						faqEditorPane;
	// The corresponding scroll pane
	private JScrollPane						faqScrollPane;
	// The main split pane
	private JSplitPane						splitPane;

	// The list of URLs
	private URL[]									faqURL;

	/**
	 * <p>
	 * Constructor of the FAQ JFrame, attached to the main parent window.
	 * </p>
	 * 
	 * @param parent
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public Faq(final JBroFuzzWindow parent) {

		if (faqShowing) {
			return;
		}
		faqShowing = true;

		setTitle(" JBroFuzz - Frequently Asked Questions ");

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		faqURL = new URL[nodeNames.length];
		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(nodeNames[0]);

		for (int i = 0; i < nodeNames.length; i++) {
			faqURL[i] = ClassLoader.getSystemClassLoader().getResource(
					"help/faq-0" + i + ".html");
			if (i > 0) {
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

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15,
				15));
		buttonPanel.add(ok);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				faqShowing = false;
				Faq.this.dispose();

			}
		});

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		splitPane.setDividerLocation(150);
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math.abs(parent
				.getLocation().y + 100));

		this.setSize(Faq.x, Faq.y);
		setMinimumSize(new Dimension(x / 2, y / 2));

		setResizable(true);
		setVisible(true);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				faqShowing = false;
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
