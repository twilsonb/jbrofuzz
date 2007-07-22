package org.owasp.jbrofuzz.ui.menu;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class JBRFaq extends JDialog implements TreeSelectionListener {
	
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
	// Dimensions of the about box
	private static final int x = 650;
	private static final int y = 400;
	// The list of URLs
	private URL [] faqURL;
	// The total number of links present
	private final int NO_LINKS = 11;
	
	public JBRFaq(JFrame parent) {
		
		super(parent, " Frequently Asked Questions ", true);
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));
		
		faqURL = new URL[NO_LINKS];
		for(int i = 0; i < NO_LINKS; i++) {
			if(i == 0) {
				faqURL[i] = ClassLoader.getSystemClassLoader().getResource("help/faq.html");
			}
			else {
				faqURL[i] = ClassLoader.getSystemClassLoader().getResource("help/faq.html#" + i);
			}
		}
		
		
		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("Frequently Asked Questions");
		this.createNodes(top);

		// Create a tree that allows one selection at a time.
		this.tree = new JTree(top);
		this.tree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//Listen for when the selection changes.
		this.tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it. 
		final JScrollPane treeView = new JScrollPane(this.tree);
		
		try {
			faqEditorPane = new JEditorPane(faqURL[0]);
		} catch (IOException e1) {
			faqEditorPane = new JEditorPane();
			faqEditorPane.setText("Frequently Asked Questions file could not be located.");
		}
		faqScrollPane = new JScrollPane(faqEditorPane);
		
		// Create the top split pane, showing the treeView and the Preferences
		this.splitPane  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.setLeftComponent(treeView);
		this.splitPane.setRightComponent(faqScrollPane);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setDividerLocation(150);

		final Dimension minimumSize = new Dimension(JBRFaq.x / 4, JBRFaq.y / 2);
		faqScrollPane.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		this.splitPane.setDividerLocation(100);
		this.splitPane.setPreferredSize(new Dimension(JBRFaq.x, JBRFaq.y));

		//Add the split pane to this panel
		this.getContentPane().add(this.splitPane, BorderLayout.CENTER);

		// Bottom button
		this.ok = new JButton("Close");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add (this.ok);

		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRFaq.this.dispose();
					}
				});       
			}
		});

		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2) - (JBRFaq.x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (JBRFaq.y / 2) + 100 ));
		this.setSize(JBRFaq.x, JBRFaq.y);
		this.setResizable(true);
		this.setVisible(true);						
	}
	
	
	private void createNodes(final DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;
		
		for(int i = 0; i < NO_LINKS; i++) {
			node = new DefaultMutableTreeNode("Question " + (i + 1) );
			top.add(node);
		}

	}

	public void valueChanged(final TreeSelectionEvent e) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		this.tree.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();
		if(s.equalsIgnoreCase("Question 16")) {
			
			try {
				this.faqEditorPane.setPage(faqURL[9]);
				this.faqScrollPane.setViewportView(faqEditorPane);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
			}
			this.splitPane.setRightComponent(this.faqScrollPane);
		}	
	}	
}
