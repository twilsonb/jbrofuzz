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

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.util.*;
import java.util.prefs.*;

import javax.swing.tree.*;
import javax.swing.event.*;

import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.*;

/**
 * <p>
 * The preferences panel. This is the panel the user sees when selecting Options ->
 * Preferences from the main menu bar.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 */

public class JBRPreferences extends JDialog implements TreeSelectionListener {

	private static final long serialVersionUID = 4301858021356404678L;
	// Dimensions of the about box
	private static final int x = 650;
	private static final int y = 400;
	// The tree
	private JTree tree;
	// The main split pane
	private JSplitPane splitPane;
	// The buttons displayed
	private JButton ok; // , cancel, apply;
	
	// The actual preferences object
	private Preferences prefs;

	private static final String [] nodeNames = {"Preferences", "Directory Locations", "Fuzzing", "Sniffing", "Web Directories"};
	
	private JPanel [] panels = new JPanel[nodeNames.length];

	public JBRPreferences(final JFrame parent) {

		super(parent, " JBroFuzz - Preferences ", true);
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("Verdana", Font.PLAIN, 12));

		// Set the preferences object access
		prefs = Preferences.userRoot().node("owasp/jbrofuzz");

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				nodeNames[0]);
		for (int i = 1; i < nodeNames.length; i++) {
			top.add(new DefaultMutableTreeNode(nodeNames[i]));
		}

		// Create a tree that allows one selection at a time
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it.
		final JScrollPane leftScrollPane = new JScrollPane(tree);

		// Create all the right hand panels
		for(int i = 0; i < nodeNames.length; i++) {
			
			panels[i] = new JPanel();
			panels[i].setLayout(new BoxLayout(panels[i], BoxLayout.PAGE_AXIS));
			JLabel header = new JLabel("<HTML><H3>&nbsp;" + nodeNames[i] + "</H3></HTML>");
			panels[i].add(header);
			header.add(Box.createRigidArea(new Dimension(0, 20)));

		}
		
		// Create the options in the preferences panel

		HashMap<String, String> prefsHash = new HashMap<String, String>(4);
		prefsHash.put(nodeNames[1], "Displays the directory locations used by JBroFuzz, also giving the option to delete any directories created at startup that have not being used and are empty");
		prefsHash.put(nodeNames[2], "Allows for the modification of the socket timeout parameter, while fuzzing, setting its value to 30 seconds");
		prefsHash.put(nodeNames[3], "Modify whether or not to stored binary content in hexadecimal form");
		prefsHash.put(nodeNames[4], "Allows for the alteration of the behaviour in stopping a web directory enumeration taking place in the event of the server stopping to respond to the requests made");
		
		for(String pr : prefsHash.keySet()) {
			
			JLabel box = new JLabel("<html>" + prefsHash.get(pr) + "</html>");
			box.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder(pr),
					BorderFactory.createEmptyBorder(1, 1, 1, 5)));
			panels[0].add(box);
			panels[0].add(Box.createRigidArea(new Dimension(0, 20)));
			
		}
		// Display the directory locations in the Directory Locations panel
		
		final boolean deletebox = prefs.getBoolean(JBRFormat.PR_1, false);
		final JCheckBox deleteCheckBox = new JCheckBox(" On exit, delete any empty directories created at startup ", deletebox);
		
		deleteCheckBox.setBorderPaintedFlat(true);
		deleteCheckBox.setToolTipText("Tick this option, if you would like to remove any empty directories");

		deleteCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (deleteCheckBox.isSelected()) {
					prefs.putBoolean(JBRFormat.PR_1, true);
				} else {
					prefs.putBoolean(JBRFormat.PR_1, false);
				}
			}
		});
		panels[1].add(deleteCheckBox);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));
		
		HashMap<String, String> dirHash = new HashMap<String, String>(4);
		// dirHash.put(" Launch Directory (where JBroFuzz is running from) ", System.getProperty("user.dir"));
		dirHash.put(" Fuzzing (where data is saved) ", FileHandler.getCanonicalPath(FileHandler.DIR_TCPF));
		dirHash.put(" Sniffing (where data is saved) ", FileHandler.getCanonicalPath(FileHandler.DIR_SNIF));
		dirHash.put(" Web Directories (where data is saved) ", FileHandler.getCanonicalPath(FileHandler.DIR_WEBD));
		
		for(String dir : dirHash.keySet()) {
			
			JLabel box = new JLabel("<html>" + JBRFormat.centerAbbreviate(dirHash.get(dir), 70) + "</html>");
			box.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder(dir),
					BorderFactory.createEmptyBorder(1, 1, 1, 5)));
			panels[1].add(box);
			panels[1].add(Box.createRigidArea(new Dimension(0, 20)));
			
		}

		// Create the options in the fuzzing panel
		
		final boolean socketbox = prefs.getBoolean(JBRFormat.PR_FUZZ_1, false);
		final JCheckBox socketCheckBox = new JCheckBox(" Extend the socket timeout from 5 to 30 seconds ", socketbox);
		
		socketCheckBox.setBorderPaintedFlat(true);
		socketCheckBox.setToolTipText("Tick this box, if you are getting timeout responses");

		socketCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (socketCheckBox.isSelected()) {
					prefs.putBoolean(JBRFormat.PR_FUZZ_1, true);
				} else {
					prefs.putBoolean(JBRFormat.PR_FUZZ_1, false);
				}
			}
		});
		panels[2].add(socketCheckBox);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Create the options in the sniffing panel
		
		final boolean hexbox = prefs.getBoolean(JBRFormat.PR_SNIF_1, true);
		final JCheckBox hexBoxCheck = new JCheckBox(" Display and store binary files in hexadecimal format ", hexbox);
		
		hexBoxCheck.setBorderPaintedFlat(true);
		hexBoxCheck.setToolTipText("Tick this box, if you want to store, view and browse sniffed binary content in hexadecimal form");
		
		hexBoxCheck.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (hexBoxCheck.isSelected()) {
					prefs.putBoolean(JBRFormat.PR_SNIF_1, true);
				} else {
					prefs.putBoolean(JBRFormat.PR_SNIF_1, false);
				}
			}
		});
		
		panels[3].add(hexBoxCheck);
		panels[3].add(Box.createRigidArea(new Dimension(0, 20)));
		// Create the options in the web directories panel
		
		final boolean checkbox = prefs.getBoolean(JBRFormat.PR_WEB_DIR_1, false);
		final JCheckBox errorCheckBox = new JCheckBox(" While enumerating directories, continue regardless of the response received", checkbox);
		
		errorCheckBox.setBorderPaintedFlat(true);
		errorCheckBox.setToolTipText("Continue attempting to enumerating directories, even if an error occurs");

		errorCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (errorCheckBox.isSelected()) {
					prefs.putBoolean(JBRFormat.PR_WEB_DIR_1, true);
				} else {
					prefs.putBoolean(JBRFormat.PR_WEB_DIR_1, false);
				}
			}
		});
		panels[4].add(errorCheckBox);
		panels[4].add(Box.createRigidArea(new Dimension(0, 20)));
				
		// Create the top split pane, showing the treeView and the Preferences
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftScrollPane);
		splitPane.setRightComponent(panels[0]);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerLocation(150);

		// Set the minimum size for all components
		final Dimension minimumSize = new Dimension(0, 0);
		leftScrollPane.setMinimumSize(minimumSize);
		for(JPanel jp : panels) {
			jp.setMinimumSize(minimumSize);
		}
		splitPane.setDividerLocation(150);

		// Bottom three buttons
		ok = new JButton("  OK  ");
		// cancel = new JButton("Cancel");
		// apply = new JButton(" Apply ");

		ok.setEnabled(true);
		// cancel.setEnabled(true);
		// apply.setEnabled(false);
		
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add(ok);
		// buttonPanel.add(cancel);
		// buttonPanel.add(apply);

		// Action Listeners for all three buttons

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						clickOK();
					}
				});
			}
		});
		/*
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						clickCANCEL();
					}
				});
			}
		});

		apply.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						clickAPPLY();
					}
				});
			}
		});
		*/
		
		// Keyboard listener on the treeView for escape to cancel

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					clickOK();
				}
			}
		});

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math
				.abs(parent.getLocation().y + 100));
		this.setSize(x, y);
		this.setMinimumSize(new Dimension(x, y));
		setResizable(true);
		setVisible(true);
	}

	/*
	private void clickCANCEL() {
		JBRPreferences.this.dispose();
	}

	private void clickAPPLY() {
		apply.setEnabled(false);
	}
	*/

	private void clickOK() {

		// final int c = prefsTable.getSelectedRow();
		// final String name = (String) prefsTable.getModel().getValueAt(c, 0);

		/*
		 * final String url = providersText.getText();
		 * StringUtils.trimToNull(url); StringUtils.deleteWhitespace(url);
		 * providersText.setText(url);
		 * 
		 * if(url != null) { //
		 * parent.getEDOL().getEngine().setCurrentProvider(name, url); //
		 * providersText.setText(parent.getEDOL().getEngine().getCurrentProvider().getURL()); }
		 */
		JBRPreferences.this.dispose();
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
				// faqScrollPane.setViewportView(faqEditorPane);
				splitPane.setRightComponent(panels[i]);
				splitPane.setDividerLocation(150);
				/* The apply button
				if(i == 0 || i == 3) {
					apply.setEnabled(false);
				} else {
					apply.setEnabled(true);
				}
				*/
				
			}

		} // for loop
	}
}