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
import java.io.*;
import java.net.*;
import java.util.prefs.*;

import javax.swing.event.*;
import javax.swing.tree.*;

import org.owasp.jbrofuzz.ui.*;
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
 * @version 0.8
 */


public class JBRPreferences extends JDialog implements TreeSelectionListener {

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

	private JPanel preferences, fuzzing, directories, sniffing;
	
	private Preferences prefs;

	public JBRPreferences(final JFrame parent) {

		super(parent, " JBroFuzz - Preferences ", true);
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));
		
		// Set the preferences object access
		prefs = Preferences.userRoot().node("owasp/jbrofuzz");

		/*
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
		*/

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("Preferences");
		createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it.
		final JScrollPane treeView = new JScrollPane(tree);
		/*
		try {
			faqEditorPane = new JEditorPane(faqURL[0]);
		} catch (final IOException e1) {
			faqEditorPane = new JEditorPane();
			faqEditorPane
					.setText("Frequently Asked Questions file could not be located.");
		}
		*/
		faqScrollPane = new JScrollPane(faqEditorPane);

		// Create the preferences panel
		preferences = new JPanel();
		preferences.setLayout(new BoxLayout(preferences,
				BoxLayout.PAGE_AXIS));
		JLabel header = new JLabel("<HTML><H3>&nbsp;Preferences</H3></HTML>");
		preferences.add(header);
		preferences.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel firstBox = new JLabel("<html>"
				+ System.getProperty("user.dir") + "</html>");
		firstBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Current Working Directory "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		preferences.add(firstBox);
		preferences.add(Box.createRigidArea(new Dimension(0, 10)));


		// Create the fuzzing panel
		fuzzing = new JPanel();
		fuzzing.setLayout(new BoxLayout(fuzzing, BoxLayout.PAGE_AXIS));
		header = new JLabel("<HTML><H3>&nbsp;Fuzzing</H3></HTML>");
		fuzzing.add(header);
		header.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel secondBox = new JLabel("<html>"
				+ FileHandler.getCanonicalPath(FileHandler.DIR_TCPF) + "</html>");
		secondBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Fuzzing Directory (where data is saved) "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		fuzzing.add(secondBox);
		fuzzing.add(Box.createRigidArea(new Dimension(0, 10)));


		// Create the directories panel
		directories = new JPanel();
		directories.setLayout(new BoxLayout(directories,
				BoxLayout.PAGE_AXIS));
		header = new JLabel(
		"<HTML><H3>&nbsp;Fuzzing Directories</H3></HTML>");
		directories.add(header);
		header.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel fourthBox = new JLabel("<html>"
				+ FileHandler.getCanonicalPath(FileHandler.DIR_WEBD) + "</html>");
		fourthBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Web Enum Directory (where data is saved) "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		directories.add(fourthBox);
		directories.add(Box.createRigidArea(new Dimension(0, 10)));

		final boolean checkbox = prefs.getBoolean(Format.PREF_FUZZ_DIR_ERR,
				false);
		final JCheckBox errorCheckBox = new JCheckBox(
				" While Fuzzing, if an error occurs, Continue ", checkbox);
		errorCheckBox.setBorderPaintedFlat(true);
		errorCheckBox
		.setToolTipText("Continue attempting to Fuzz, even if an error occurs");

		errorCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (errorCheckBox.isSelected()) {
					prefs.putBoolean(Format.PREF_FUZZ_DIR_ERR,
							true);
				} else {
					prefs.putBoolean(Format.PREF_FUZZ_DIR_ERR,
							false);
				}
			}
		});
		directories.add(errorCheckBox);
		header.add(Box.createRigidArea(new Dimension(0, 15)));


		// Create the sniffing panel
		sniffing = new JPanel();
		sniffing.setLayout(new BoxLayout(sniffing, BoxLayout.PAGE_AXIS));
		header = new JLabel("<HTML><H3>&nbsp;Sniffing</H3></HTML>");
		sniffing.add(header);
		header.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel thirdBox = new JLabel("<html>"
				+ FileHandler.getCanonicalPath(FileHandler.DIR_SNIF) + "</html>");
		thirdBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Sniffing Directory (where data is saved) "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		sniffing.add(thirdBox);
		sniffing.add(Box.createRigidArea(new Dimension(0, 10)));


		// Create the top split pane, showing the treeView and the Preferences
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(faqScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		final Dimension minimumSize = new Dimension(JBRPreferences.x / 4, JBRPreferences.y / 2);
		faqScrollPane.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(JBRPreferences.x, JBRPreferences.y));

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Bottom button
		
		
		// Buttons
		JButton ok = new JButton("  OK  ");
		JButton cancel = new JButton("Cancel");

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15,
				15));
		buttonPanel.add(ok);
		buttonPanel.add(cancel);

		// Action Listeners for all components

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

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					clickCANCEL();
				}
			}
		});

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math.abs(parent.getLocation().y + 100));
		this.setSize(JBRPreferences.x, JBRPreferences.y);
		setResizable(true);
		setVisible(true);
	}
	
	private void clickOK() {

		// final int c = prefsTable.getSelectedRow();
		// final String name = (String) prefsTable.getModel().getValueAt(c, 0);

		/*
		final String url = providersText.getText();
		StringUtils.trimToNull(url);
		StringUtils.deleteWhitespace(url);
		providersText.setText(url);

		if(url != null) {
			// parent.getEDOL().getEngine().setCurrentProvider(name, url);
			// providersText.setText(parent.getEDOL().getEngine().getCurrentProvider().getURL());
		} 
		 */
		// JBRPreferences.this.dispose();

	}
	
	private void clickCANCEL() {
		JBRPreferences.this.dispose();
	}

	private void createNodes(final DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;
		
		/*
		for (int i = 0; i < NO_LINKS; i++) {
			node = new DefaultMutableTreeNode("Question " + (i + 1));
			top.add(node);
		}
		*/
		node = new DefaultMutableTreeNode("Fuzzing");
		top.add(node);
		
		node = new DefaultMutableTreeNode("Sniffing");
		top.add(node);

		node = new DefaultMutableTreeNode("Web Directories");
		top.add(node);
		
		/*
		node = new DefaultMutableTreeNode("Java requirements");
		top.add(node);
		
		node = new DefaultMutableTreeNode("Directory problems");
		top.add(node);

		node = new DefaultMutableTreeNode("What is a Generator?");
		top.add(node);
		
		node = new DefaultMutableTreeNode("What is a Fuzzer?");
		top.add(node);

		node = new DefaultMutableTreeNode("Sniffing problems");
		top.add(node);
		*/



	}

	public void valueChanged(final TreeSelectionEvent e) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();
		if (s.equalsIgnoreCase("Fuzzing")) {
			/*
			try	 {
				faqEditorPane.setPage(faqURL[9]);
				faqScrollPane.setViewportView(faqEditorPane);
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
			}
			*/
			splitPane.setRightComponent(faqScrollPane);
		}
	}
}

/*

	
	private JPanel preferences, fuzzing, directories, sniffing;

	// private JPanel centerPanel, southPanel;

	private JScrollPane providersTextScrollPane;

	// The preferences object
	private Preferences prefs;

	// The main window
	private JBroFuzzWindow parent;

	public JBRPreferences(final JBroFuzzWindow parent) {

		super(parent, " Preferences ", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.parent = parent;

		// Set the preferences object access
		prefs = Preferences.userRoot().node("owasp/jbrofuzz");

		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));
		
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
			providersTextScrollPane = new JEditorPane(faqURL[0]);
		} catch (final IOException e1) {
			providersTextScrollPane = new JEditorPane();
			providersTextScrollPane
					.setText("Frequently Asked Questions file could not be located.");
		}
		providersTextScrollPane = new JScrollPane(faqEditorPane);

		final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));

		// Create the preferences panel
		preferences = new JPanel();
		preferences.setLayout(new BoxLayout(preferences,
				BoxLayout.PAGE_AXIS));
		JLabel header = new JLabel("<HTML><H3>&nbsp;Preferences</H3></HTML>");
		preferences.add(header);
		preferences.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel firstBox = new JLabel("<html>"
				+ System.getProperty("user.dir") + "</html>");
		firstBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Current Working Directory "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		preferences.add(firstBox);
		preferences.add(Box.createRigidArea(new Dimension(0, 10)));


		// Create the fuzzing panel
		fuzzing = new JPanel();
		fuzzing.setLayout(new BoxLayout(fuzzing, BoxLayout.PAGE_AXIS));
		header = new JLabel("<HTML><H3>&nbsp;Fuzzing</H3></HTML>");
		fuzzing.add(header);
		header.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel secondBox = new JLabel("<html>"
				+ FileHandler.getCanonicalPath(FileHandler.DIR_TCPF) + "</html>");
		secondBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Fuzzing Directory (where data is saved) "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		fuzzing.add(secondBox);
		fuzzing.add(Box.createRigidArea(new Dimension(0, 10)));


		// Create the directories panel
		directories = new JPanel();
		directories.setLayout(new BoxLayout(directories,
				BoxLayout.PAGE_AXIS));
		header = new JLabel(
		"<HTML><H3>&nbsp;Fuzzing Directories</H3></HTML>");
		directories.add(header);
		header.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel fourthBox = new JLabel("<html>"
				+ FileHandler.getCanonicalPath(FileHandler.DIR_WEBD) + "</html>");
		fourthBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Web Enum Directory (where data is saved) "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		directories.add(fourthBox);
		directories.add(Box.createRigidArea(new Dimension(0, 10)));

		final boolean checkbox = prefs.getBoolean(Format.PREF_FUZZ_DIR_ERR,
				false);
		final JCheckBox errorCheckBox = new JCheckBox(
				" While Fuzzing, if an error occurs, Continue ", checkbox);
		errorCheckBox.setBorderPaintedFlat(true);
		errorCheckBox
		.setToolTipText("Continue attempting to Fuzz, even if an error occurs");

		errorCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (errorCheckBox.isSelected()) {
					prefs.putBoolean(Format.PREF_FUZZ_DIR_ERR,
							true);
				} else {
					prefs.putBoolean(Format.PREF_FUZZ_DIR_ERR,
							false);
				}
			}
		});
		directories.add(errorCheckBox);
		header.add(Box.createRigidArea(new Dimension(0, 15)));


		// Create the sniffing panel
		sniffing = new JPanel();
		sniffing.setLayout(new BoxLayout(sniffing, BoxLayout.PAGE_AXIS));
		header = new JLabel("<HTML><H3>&nbsp;Sniffing</H3></HTML>");
		sniffing.add(header);
		header.add(Box.createRigidArea(new Dimension(0, 10)));

		final JLabel thirdBox = new JLabel("<html>"
				+ FileHandler.getCanonicalPath(FileHandler.DIR_SNIF) + "</html>");
		thirdBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Sniffing Directory (where data is saved) "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		sniffing.add(thirdBox);
		sniffing.add(Box.createRigidArea(new Dimension(0, 10)));

		// JTable  & Data

		String [][] data = {{"Preferences"}, {"Fuzzing"}, {"Sniffing"}, {"Directories"}};
		String [] column = {" "};

		prefsTable = new JTable(data, column) {
			public boolean isCellEditable(int r, int c) {	
				return false;	
			}
		};
		prefsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		prefsTable.getTableHeader().setSize(new Dimension(0,0));
		prefsTable.setFont(new Font("Verdana", Font.BOLD, 14));
		prefsTable.setBackground(new Color(0, 128, 255));
		prefsTable.getTableHeader().setVisible(false);
		prefsTable.setForeground(Color.WHITE);
		prefsTable.setRowHeight(30);
		prefsTable.getSelectionModel().addListSelectionListener(new ProviderRowListener());


		// Scroll Pane for the table

		final JScrollPane providersTableScrollPane = new JScrollPane(prefsTable);
		providersTableScrollPane.setColumnHeader(null);
		providersTableScrollPane.setVerticalScrollBarPolicy(20);
		providersTableScrollPane.setHorizontalScrollBarPolicy(30);
		prefsTable.setPreferredSize(new Dimension(100, y - 110));
		// But don't add the scroll pane!
		centerPanel.add(prefsTable);

		providersTextScrollPane = new JScrollPane(preferences);
		providersTextScrollPane.setVerticalScrollBarPolicy(20);
		providersTextScrollPane.setHorizontalScrollBarPolicy(30);
		providersTextScrollPane.setPreferredSize(new Dimension(x - 150, y - 110));
		centerPanel.add(providersTextScrollPane);
		// centerPanel.add(providersText);


		// Buttons
		JButton ok = new JButton("  OK  ");
		JButton cancel = new JButton("Cancel");
		southPanel.add(ok);
		southPanel.add(cancel);

		// Action Listeners for all components

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

		prefsTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					clickCANCEL();
				}
			}
		});

		

		// Add the panels to the dialog

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(southPanel, BorderLayout.SOUTH);

		// Global frame issues

		this.setLocation(
				Math.abs((parent.getWidth() / 2) - (JBRPreferences.x / 2 - 100)), Math
				.abs((parent.getHeight() / 2) - (JBRPreferences.y / 2) + 100));
		this.setSize(JBRPreferences.x, JBRPreferences.y);
		setResizable(false);
		setVisible(true);

	}

	private void clickOK() {

		final int c = prefsTable.getSelectedRow();
		final String name = (String) prefsTable.getModel().getValueAt(c, 0);

		/*
		final String url = providersText.getText();
		StringUtils.trimToNull(url);
		StringUtils.deleteWhitespace(url);
		providersText.setText(url);

		if(url != null) {
			// parent.getEDOL().getEngine().setCurrentProvider(name, url);
			// providersText.setText(parent.getEDOL().getEngine().getCurrentProvider().getURL());
		} 
		 */
		// JBRPreferences.this.dispose();

	// }
	/*
	private void clickCANCEL() {
		JBRPreferences.this.dispose();
	}
	
	private void createNodes(final DefaultMutableTreeNode top) {
		DefaultMutableTreeNode node = null;
		
		/*
		for (int i = 0; i < NO_LINKS; i++) {
			node = new DefaultMutableTreeNode("Question " + (i + 1));
			top.add(node);
		}
		*/
/*
		node = new DefaultMutableTreeNode("Name");
		top.add(node);
		
		node = new DefaultMutableTreeNode("Running the jar");
		top.add(node);

		node = new DefaultMutableTreeNode("Latest version");
		top.add(node);

		node = new DefaultMutableTreeNode("Java requirements");
		top.add(node);
		
		node = new DefaultMutableTreeNode("Directory problems");
		top.add(node);

		node = new DefaultMutableTreeNode("What is a Generator?");
		top.add(node);
		
		node = new DefaultMutableTreeNode("What is a Fuzzer?");
		top.add(node);

		node = new DefaultMutableTreeNode("Sniffing problems");
		top.add(node);



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
	*/


	/*
	private class ProviderRowListener implements ListSelectionListener {

		public void valueChanged(final ListSelectionEvent event) {

			if (event.getValueIsAdjusting())
				return;

			final int c = prefsTable.getSelectedRow();
			final String s  = (String) prefsTable.getModel().getValueAt(c, 0);			

			if (s.equalsIgnoreCase("Preferences")) {
				providersTextScrollPane.setViewportView(preferences);
				providersTextScrollPane.updateUI();
			}
			if (s.equalsIgnoreCase("Fuzzing")) {
				providersTextScrollPane.setViewportView(fuzzing);
				providersTextScrollPane.updateUI();				
			}
			if (s.equalsIgnoreCase("Directories")) {
				providersTextScrollPane.setViewportView(directories);
				providersTextScrollPane.updateUI();
			}
			if (s.equalsIgnoreCase("Sniffing")) {
				providersTextScrollPane.setViewportView(sniffing);
				providersTextScrollPane.updateUI();
			}


		}

	}
	*/

// }
