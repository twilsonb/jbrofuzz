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
import java.util.prefs.*;

import javax.swing.tree.*;
import javax.swing.event.*;

import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.*;

import org.apache.commons.lang.StringUtils;

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
		final JScrollPane treeView = new JScrollPane(tree);

		// Create all the right hand panels
		for(int i = 0; i < nodeNames.length; i++) {
			
			panels[i] = new JPanel();
			panels[i].setLayout(new BoxLayout(panels[i], BoxLayout.PAGE_AXIS));
			JLabel header = new JLabel("<HTML><H3>&nbsp;" + nodeNames[i] + "</H3></HTML>");
			panels[i].add(header);
			header.add(Box.createRigidArea(new Dimension(0, 10)));

		}
		
		// Create the options in the preferences panel

		
		
		// Create the options in the directory locations panel
		
		final JLabel firstBox = new JLabel("<html>"
				+ System.getProperty("user.dir") + "</html>");
		firstBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Launch Directory "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		panels[1].add(firstBox);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));
		
		String str = FileHandler.getCanonicalPath(FileHandler.DIR_TCPF);
		str = Format.centerAbbreviate(str, 50);
		
		final JLabel secondBox = new JLabel("<html>" + str + "</html>");
		secondBox
		.setBorder(BorderFactory
				.createCompoundBorder(
						BorderFactory
						.createTitledBorder(" Fuzzing (where data is saved) "),
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		panels[1].add(secondBox);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));

		str = FileHandler.getCanonicalPath(FileHandler.DIR_SNIF);
		str = Format.centerAbbreviate(str, 50);
		
		final JLabel thirdBox = new JLabel("<html>"
				+ str
				+ "</html>");
		thirdBox
		.setBorder(BorderFactory
				.createCompoundBorder(
						BorderFactory
						.createTitledBorder(" Sniffing (where data is saved) "),
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		panels[1].add(thirdBox);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));
		
		str = FileHandler.getCanonicalPath(FileHandler.DIR_WEBD);
		str = Format.centerAbbreviate(str, 50);
		
		final JLabel fourthBox = new JLabel("<html>"
				+ str
				+ "</html>");
		fourthBox
		.setBorder(BorderFactory
				.createCompoundBorder(
						BorderFactory
						.createTitledBorder(" Web Directories (where data is saved) "),
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		panels[1].add(fourthBox);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Create the options in the sniffing panel
		
		
		
		// Create the options in the web directories panel
		
		final boolean checkbox = prefs.getBoolean(Format.PREF_FUZZ_DIR_ERR,
				false);
		final JCheckBox errorCheckBox = new JCheckBox(
				" While Enumerating Directories, if an error occurs, Continue ", checkbox);
		errorCheckBox.setBorderPaintedFlat(true);
		errorCheckBox
		.setToolTipText("Continue attempting to Fuzz, even if an error occurs");

		errorCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (errorCheckBox.isSelected()) {
					prefs.putBoolean(Format.PREF_FUZZ_DIR_ERR, true);
				} else {
					prefs.putBoolean(Format.PREF_FUZZ_DIR_ERR, false);
				}
			}
		});
		panels[4].add(errorCheckBox);

		// Create the top split pane, showing the treeView and the Preferences
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(panels[0]);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerLocation(150);

		// Set the minimum size for all components
		final Dimension minimumSize = new Dimension(0, 0);
		treeView.setMinimumSize(minimumSize);
		for(JPanel jp : panels) {
			jp.setMinimumSize(minimumSize);
		}
		splitPane.setDividerLocation(150);

		// Bottom three buttons
		JButton ok = new JButton("  OK  ");
		JButton cancel = new JButton("Cancel");
		JButton apply = new JButton(" Apply ");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		buttonPanel.add(apply);

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
		
		// Keyboard listener on the treeView for escape to cancel

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					clickCANCEL();
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

	private void clickCANCEL() {
		JBRPreferences.this.dispose();
	}

	private void clickAPPLY() {

	}

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
		// JBRPreferences.this.dispose();
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
			}

		} // for loop
	}
}