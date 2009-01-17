/**
 * JBroFuzz 1.2
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
package org.owasp.jbrofuzz.ui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The preferences panel. This is the panel the user sees when selecting Options ->
 * Preferences from the main menu bar.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.2
 */

public class JBroFuzzPrefs extends JDialog implements TreeSelectionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 594984166923205227L;
	
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

	private static final String [] nodeNames = {"Preferences", "Directory Locations", "Fuzzing"};
	
	private JPanel [] panels = new JPanel[nodeNames.length];

	public JBroFuzzPrefs(final JBroFuzzWindow parent) {

		super(parent, " JBroFuzz - Preferences ", true);
		setIconImage(ImageCreator.IMG_FRAME.getImage());
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
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// Selection can only contain one path at a time
		tree.getSelectionModel().setSelectionMode(1);
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

		final boolean tabsbottom = prefs.getBoolean(JBroFuzzFormat.PR_2, false);
		final JCheckBox tabsCheckBox = new JCheckBox(" Show tabs in the main window at the top of the window (requires restart) ", tabsbottom);
		
		tabsCheckBox.setBorderPaintedFlat(true);
		tabsCheckBox.setToolTipText("Tick this option, if you would like to see the tabs under the tool bar, instead of at the bottom of the window ");
		
		tabsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (tabsCheckBox.isSelected()) {
					prefs.putBoolean(JBroFuzzFormat.PR_2, true);
				} else {
					prefs.putBoolean(JBroFuzzFormat.PR_2, false);
				}
			}
		});
		panels[0].add(tabsCheckBox);
		panels[0].add(Box.createRigidArea(new Dimension(0, 20)));
		
		/*
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
		*/
		
		// Display the directory locations in the Directory Locations panel
		
		final boolean deletebox = prefs.getBoolean(JBroFuzzFormat.PR_1, false);
		final JCheckBox deleteCheckBox = new JCheckBox(" On exit, delete any empty directories created at startup ", deletebox);
		
		deleteCheckBox.setBorderPaintedFlat(true);
		deleteCheckBox.setToolTipText("Tick this option, if you would like to remove any empty directories");

		deleteCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (deleteCheckBox.isSelected()) {
					prefs.putBoolean(JBroFuzzFormat.PR_1, true);
				} else {
					prefs.putBoolean(JBroFuzzFormat.PR_1, false);
				}
			}
		});
		panels[1].add(deleteCheckBox);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));
		
		HashMap<String, String> dirHash = new HashMap<String, String>(4);
		dirHash.put(" Fuzzing (where data is saved) ", parent.getJBroFuzz().getHandler().getCanonicalPath());
		
		for(String dir : dirHash.keySet()) {
			
			JLabel box = new JLabel("<html>" + JBroFuzzFormat.centerAbbreviate(dirHash.get(dir), 70) + "</html>");
			box.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder(dir),
					BorderFactory.createEmptyBorder(1, 1, 1, 5)));
			panels[1].add(box);
			panels[1].add(Box.createRigidArea(new Dimension(0, 20)));
			
		}

		// Fuzzing Panel -> Socket Timeout CheckBox
		
		final boolean socketbox = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_1, false);
		final JCheckBox socketCheckBox = new JCheckBox(" Extend the socket timeout from 5 seconds to 30 seconds ", socketbox);
		
		socketCheckBox.setBorderPaintedFlat(true);
		socketCheckBox.setToolTipText("Tick this box, if you are getting timeout responses");

		socketCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (socketCheckBox.isSelected()) {
					prefs.putBoolean(JBroFuzzFormat.PR_FUZZ_1, true);
				} else {
					prefs.putBoolean(JBroFuzzFormat.PR_FUZZ_1, false);
				}
			}
		});
		panels[2].add(socketCheckBox);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Fuzzing Panel -> End of Line Character
		
		final boolean endlinebox = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_2, false);
		final JCheckBox endlineCheckBox = new JCheckBox(" Use \"\\n\" instead of \"\\r\\n\" as an end of line character ", endlinebox);
		
		endlineCheckBox.setBorderPaintedFlat(true);
		endlineCheckBox.setToolTipText("Tick this box, if you want to use \"\\n\" for each line put on the wire");
		
		endlineCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (endlineCheckBox.isSelected()) {
					prefs.putBoolean(JBroFuzzFormat.PR_FUZZ_2, true);
				} else {
					prefs.putBoolean(JBroFuzzFormat.PR_FUZZ_2, false);
				}
			}
		});
		panels[2].add(endlineCheckBox);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Fuzzing Panel -> Show on the wire tab after fuzzing finished
		
		final boolean showwirebox = prefs.getBoolean(JBroFuzzFormat.PR_FUZZ_3, false);
		final JCheckBox showwireCheckBox = new JCheckBox(" Show \"On The Wire\" tab after fuzzing has stopped or finished ", showwirebox);
		
		showwireCheckBox.setBorderPaintedFlat(true);
		showwireCheckBox.setToolTipText("Tick this box, if you want to always see the \"On The Wire\" tab");
		
		showwireCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (showwireCheckBox.isSelected()) {
					prefs.putBoolean(JBroFuzzFormat.PR_FUZZ_3, true);
				} else {
					prefs.putBoolean(JBroFuzzFormat.PR_FUZZ_3, false);
				}
			}
		});
		
		panels[2].add(showwireCheckBox);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Create the options in the sniffing panel
		

		// Create the options in the graphing panel
		
		
				
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
		this.setLocation(Math.abs(parent.getLocationOnScreen().x + 100), 
							Math.abs(parent.getLocationOnScreen().y + 20));
		this.setSize(x, y);
		this.setMinimumSize(new Dimension(x, y));
		setResizable(true);
		setVisible(true);
	}

	private void clickOK() {
		JBroFuzzPrefs.this.dispose();
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