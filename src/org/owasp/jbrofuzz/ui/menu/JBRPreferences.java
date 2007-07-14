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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.version.JBRFormat;
/**
 * <p>The preferences panel. This is the panel the user sees when 
 * selecting Options -> Preferences from the main menu bar.</p>
 *
 * @author subere (at) uncon org
 * @version 0.7
 */
public class JBRPreferences extends JDialog implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7783729547247101981L;
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
	public JBRPreferences(final JFrame parent) {
		super(parent, " Preferences ", true);
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));
		// Set the preferences object access
		this.prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("Preferences");
		this.createNodes(top);

		// Create a tree that allows one selection at a time.
		this.tree = new JTree(top);
		this.tree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//Listen for when the selection changes.
		this.tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it. 
		final JScrollPane treeView = new JScrollPane(this.tree);

		// Create the preferences panel
		this.preferences = new JPanel();
		this.preferences.setLayout(new BoxLayout(this.preferences, BoxLayout.PAGE_AXIS));
		this.initPreferences();

		// Create the fuzzing panel
		this.fuzzing = new JPanel();
		this.fuzzing.setLayout(new BoxLayout(this.fuzzing, BoxLayout.PAGE_AXIS));
		this.initFuzzing();

		// Create the directories panel
		this.directories = new JPanel();
		this.directories.setLayout(new BoxLayout(this.directories, BoxLayout.PAGE_AXIS));
		this.initDirectories();

		// Create the sniffing panel
		this.sniffing = new JPanel();
		this.sniffing.setLayout(new BoxLayout(this.sniffing, BoxLayout.PAGE_AXIS));
		this.initSniffing();

		// Create the top split pane, showing the treeView and the Preferences
		this.splitPane  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.setLeftComponent(treeView);
		this.splitPane.setRightComponent(this.preferences);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setDividerLocation(150);

		final Dimension minimumSize = new Dimension(JBRPreferences.x / 4, JBRPreferences.y / 2);
		this.preferences.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		this.splitPane.setDividerLocation(100);
		this.splitPane.setPreferredSize(new Dimension(JBRPreferences.x, JBRPreferences.y));

		//Add the split pane to this panel
		this.getContentPane().add(this.splitPane, BorderLayout.CENTER);

		// Bottom button
		this.ok = new JButton("OK");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add (this.ok);

		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRPreferences.this.dispose();
					}
				});       
			}
		});

		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2) - (JBRPreferences.x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (JBRPreferences.y / 2) + 100 ));
		this.setSize(JBRPreferences.x, JBRPreferences.y);
		this.setResizable(false);
		this.setVisible(true);		
	}

	private void createNodes(final DefaultMutableTreeNode top) {
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
		final JLabel header = new JLabel("<HTML><H3>&nbsp;Preferences</H3></HTML>");
		this.preferences.add(header);
		this.preferences.add(Box.createRigidArea(new Dimension(0,10)));
		
		final JLabel firstBox = new JLabel("<html>" + System.getProperty("user.dir") + "</html>");
		firstBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Current Working Directory "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		this.preferences.add(firstBox);
		this.preferences.add(Box.createRigidArea(new Dimension(0,10)));
	}

	private void initFuzzing() {
		final JLabel header = new JLabel("<HTML><H3>&nbsp;Fuzzing</H3></HTML>");
		this.fuzzing.add(header);
		header.add(Box.createRigidArea(new Dimension(0,10)));
		
		final JLabel secondBox = new JLabel("<html>" + FileHandler.getFuzzDirCanonicalPath()  + "</html>");
		secondBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Fuzzing Directory (where data is saved) "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		this.fuzzing.add(secondBox);
		this.fuzzing.add(Box.createRigidArea(new Dimension(0,10)));

	}

	private void initDirectories() {
		final JLabel header = new JLabel("<HTML><H3>&nbsp;Fuzzing Directories</H3></HTML>");
		this.directories.add(header);
		header.add(Box.createRigidArea(new Dimension(0,10)));

		final JLabel fourthBox = new JLabel("<html>" + FileHandler.getWebDirCanonicalPath()  + "</html>");
		fourthBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Web Enum Directory (where data is saved) "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		this.directories.add(fourthBox);
		this.directories.add(Box.createRigidArea(new Dimension(0,10)));
		
		final boolean checkbox = this.prefs.getBoolean(JBRFormat.PREF_FUZZ_DIR_ERR, false);	
		final JCheckBox errorCheckBox = new JCheckBox(" While Fuzzing, if an error occurs, Continue ", checkbox);
		errorCheckBox.setBorderPaintedFlat(true);
		errorCheckBox.setToolTipText("Continue attempting to Fuzz, even if an error occurs");
    
    errorCheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
        	if(errorCheckBox.isSelected()) {
        		JBRPreferences.this.prefs.putBoolean(JBRFormat.PREF_FUZZ_DIR_ERR, true);
        	} else {
        		JBRPreferences.this.prefs.putBoolean(JBRFormat.PREF_FUZZ_DIR_ERR, false);
        	}
        }
      });
    this.directories.add(errorCheckBox);
    header.add(Box.createRigidArea(new Dimension(0,15)));
	}

	private void initSniffing() {
		final JLabel header = new JLabel("<HTML><H3>&nbsp;Sniffing</H3></HTML>");
		this.sniffing.add(header);
		header.add(Box.createRigidArea(new Dimension(0,10)));
		
		final JLabel thirdBox = new JLabel("<html>" + FileHandler.getSnifDirCanonicalPath()  + "</html>");
		thirdBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
	      createTitledBorder(" Sniffing Directory (where data is saved) "),
	      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		this.sniffing.add(thirdBox);
		this.sniffing.add(Box.createRigidArea(new Dimension(0,10)));
	}

	public void valueChanged(final TreeSelectionEvent e) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		this.tree.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();
		if(s.equalsIgnoreCase("Preferences")) {
			this.splitPane.setRightComponent(this.preferences);
		}
		if(s.equalsIgnoreCase("Fuzzing")) {
			this.splitPane.setRightComponent(this.fuzzing);
		}
		if(s.equalsIgnoreCase("Directories")) {
			this.splitPane.setRightComponent(this.directories);
		}	
		if(s.equalsIgnoreCase("Sniffing")) {
			this.splitPane.setRightComponent(this.sniffing);
		}

	}
}
