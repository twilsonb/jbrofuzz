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

public class JBRFaq extends JDialog implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4301858021356404678L;
	// Dimensions of the about box
	private static final int x = 650;
	private static final int y = 400;
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
	// The total number of links present
	private static final int NO_LINKS = 11;

	public JBRFaq(final JFrame parent) {

		super(parent, " Frequently Asked Questions ", true);
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		faqURL = new URL[NO_LINKS];
		for (int i = 0; i < NO_LINKS; i++) {
			if (i == 0) {
				faqURL[i] = ClassLoader.getSystemClassLoader().getResource(
						"help/faq.html");
			} else {
				faqURL[i] = ClassLoader.getSystemClassLoader().getResource(
						"help/faq.html#" + i);
			}
		}

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("FAQ");
		createNodes(top);

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

		// Create the top split pane, showing the treeView and the Preferences
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(faqScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		final Dimension minimumSize = new Dimension(JBRFaq.x / 4, JBRFaq.y / 2);
		faqScrollPane.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(JBRFaq.x, JBRFaq.y));

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Bottom button
		ok = new JButton("Close");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15,
				15));
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
		this.setLocation(Math.abs((parent.getWidth() / 2) - (JBRFaq.x / 2 - 100)),
				Math.abs((parent.getHeight() / 2) - (JBRFaq.y / 2) + 100));
		this.setSize(JBRFaq.x, JBRFaq.y);
		setResizable(true);
		setVisible(true);
	}

	private void createNodes(final DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;

		for (int i = 0; i < NO_LINKS; i++) {
			node = new DefaultMutableTreeNode("Question " + (i + 1));
			top.add(node);
		}

	}

	public void valueChanged(final TreeSelectionEvent e) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();
		if (s.equalsIgnoreCase("Question 16")) {

			try {
				faqEditorPane.setPage(faqURL[9]);
				faqScrollPane.setViewportView(faqEditorPane);
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
			}
			splitPane.setRightComponent(faqScrollPane);
		}
	}
}
