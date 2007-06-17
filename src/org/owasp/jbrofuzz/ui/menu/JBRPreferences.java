/**
 * JBRPreferences.java 0.7
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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import javax.swing.tree.*;

import java.util.prefs.*;

import org.owasp.jbrofuzz.version.*;
import org.owasp.jbrofuzz.io.*;
/**
 * <p>The preferences panel. This is the panel the user sees when 
 * selecting Options -> Preferences from the main menu bar.</p>
 *
 * @author subere (at) uncon org
 * @version 0.7
 */
public class JBRPreferences extends JDialog implements TreeSelectionListener {

	// The buttons
	private JButton ok;
	// The tree
	private JTree tree;
	// The JPanels
	private JPanel preferences, fuzzing, directories, sniffing;
	// The main split pane
	private JSplitPane splitPane;
	// Dimensions of the about box
	private static final int x = 500;
	private static final int y = 400;
	// The preferences object
	private Preferences prefs;
	
	/**
	 * The main constructor for the Preferences Window.
	 */
	public JBRPreferences(JFrame parent) {
		super(parent, " Preferences ", true);
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));
		// Set the preferences object access
		prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		// Create the nodes
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Preferences");
		createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it. 
		JScrollPane treeView = new JScrollPane(tree);

		// Create the preferences panel
		preferences = new JPanel();
		preferences.setLayout(new BoxLayout(preferences, BoxLayout.PAGE_AXIS));
		initPreferences();

		// Create the fuzzing panel
		fuzzing = new JPanel();
		fuzzing.setLayout(new BoxLayout(fuzzing, BoxLayout.PAGE_AXIS));
		initFuzzing();

		// Create the directories panel
		directories = new JPanel();
		directories.setLayout(new BoxLayout(directories, BoxLayout.PAGE_AXIS));
		initDirectories();

		// Create the sniffing panel
		sniffing = new JPanel();
		sniffing.setLayout(new BoxLayout(sniffing, BoxLayout.PAGE_AXIS));
		initSniffing();

		// Create the top split pane, showing the treeView and the Preferences
		splitPane  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(preferences);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		Dimension minimumSize = new Dimension(x / 4, y / 2);
		preferences.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(x, y));

		//Add the split pane to this panel
		this.getContentPane().add(splitPane, BorderLayout.CENTER);

		// Bottom button
		ok = new JButton("OK");

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add (ok);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						dispose();
					}
				});       
			}
		});

		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		setLocation(Math.abs((parent.getWidth() / 2) - (x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (y / 2) + 100 ));
		setSize(x, y);
		setResizable(false);
		setVisible(true);		
	}

	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;
		DefaultMutableTreeNode leaf = null;

		node = new DefaultMutableTreeNode("Fuzzing");
		top.add(node);

		leaf = new DefaultMutableTreeNode("Directories");
		node.add(leaf);

		node = new DefaultMutableTreeNode("Sniffing");
		top.add(node);
	}

	private void initPreferences() {
		JLabel header = new JLabel("<HTML><H3>&nbsp;Preferences</H3></HTML>");
		preferences.add(header);
		preferences.add(Box.createRigidArea(new Dimension(0,10)));
		
		JLabel firstBox = new JLabel("<html>" + System.getProperty("user.dir") + "</html>");
		firstBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Current Working Directory "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		preferences.add(firstBox);
		preferences.add(Box.createRigidArea(new Dimension(0,10)));
	}

	private void initFuzzing() {
		JLabel header = new JLabel("<HTML><H3>&nbsp;Fuzzing</H3></HTML>");
		fuzzing.add(header);
		header.add(Box.createRigidArea(new Dimension(0,10)));
		
		JLabel secondBox = new JLabel("<html>" + FileHandler.getFuzzDirCanonicalPath()  + "</html>");
		secondBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Fuzzing Directory (where data is saved) "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		fuzzing.add(secondBox);
		fuzzing.add(Box.createRigidArea(new Dimension(0,10)));

	}

	private void initDirectories() {
		JLabel header = new JLabel("<HTML><H3>&nbsp;Fuzzing Directories</H3></HTML>");
		directories.add(header);
		header.add(Box.createRigidArea(new Dimension(0,10)));

		JLabel fourthBox = new JLabel("<html>" + FileHandler.getWebDirCanonicalPath()  + "</html>");
		fourthBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Web Enum Directory (where data is saved) "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		directories.add(fourthBox);
		directories.add(Box.createRigidArea(new Dimension(0,10)));
		
		boolean checkbox = prefs.getBoolean(JBRFormat.PREF_FUZZ_DIR_ERR, false);	
		final JCheckBox errorCheckBox = new JCheckBox(" While Fuzzing, if an error occurs, Continue ", checkbox);
		errorCheckBox.setBorderPaintedFlat(true);
		errorCheckBox.setToolTipText("Continue attempting to Fuzz, even if an error occurs");
    
    errorCheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
        	if(errorCheckBox.isSelected()) {
        		prefs.putBoolean(JBRFormat.PREF_FUZZ_DIR_ERR, true);
        	} else {
        		prefs.putBoolean(JBRFormat.PREF_FUZZ_DIR_ERR, false);
        	}
        }
      });
    directories.add(errorCheckBox);
    header.add(Box.createRigidArea(new Dimension(0,15)));
	}

	private void initSniffing() {
		JLabel header = new JLabel("<HTML><H3>&nbsp;Sniffing</H3></HTML>");
		sniffing.add(header);
		header.add(Box.createRigidArea(new Dimension(0,10)));
		
		JLabel thirdBox = new JLabel("<html>" + FileHandler.getSnifDirCanonicalPath()  + "</html>");
		thirdBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Sniffing Directory (where data is saved) "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		sniffing.add(thirdBox);
		sniffing.add(Box.createRigidArea(new Dimension(0,10)));
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		tree.getLastSelectedPathComponent();

		if (node == null) return;

		String s = node.toString();
		if(s.equalsIgnoreCase("Preferences")) {
			splitPane.setRightComponent(preferences);
		}
		if(s.equalsIgnoreCase("Fuzzing")) {
			splitPane.setRightComponent(fuzzing);
		}
		if(s.equalsIgnoreCase("Directories")) {
			splitPane.setRightComponent(directories);
		}	
		if(s.equalsIgnoreCase("Sniffing")) {
			splitPane.setRightComponent(sniffing);
		}

	}
}
