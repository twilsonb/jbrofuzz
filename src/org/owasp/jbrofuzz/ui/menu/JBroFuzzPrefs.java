/**
 * JBroFuzz 1.9
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The preferences panel. This is the panel the user sees when selecting Options
 * -> Preferences from the main menu bar.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.9
 * @since 1.2
 */
public class JBroFuzzPrefs extends JDialog implements TreeSelectionListener {

	private static final long serialVersionUID = -335514965523117410L;
	// Dimensions of the about box
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;
	
	private static final String[] NODENAMES = { "Preferences",
		"Directory Locations", "Fuzzing", "Fuzzing: On The Wire",
	"Fuzzing: Output"};
	// The tree
	private JTree tree;
	// The main split pane
	private JSplitPane splitPane;

	// The buttons displayed
	private JButton ok, cancel;

	private JPanel[] panels = new JPanel[NODENAMES.length];

	/**
	 * <p>
	 * The preferences dialog constructor, passing the main window as argument.
	 * </p>
	 * 
	 * @param parent
	 * 
	 * @author subere@uncon.org
	 * @version 1.5
	 * @since 1.2
	 */
	public JBroFuzzPrefs(final JBroFuzzWindow parent) {

		super(parent, " JBroFuzz - Preferences ", true);
		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("Verdana", Font.PLAIN, 12));

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				NODENAMES[0]);
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
		for (int i = 0; i < NODENAMES.length; i++) {

			panels[i] = new JPanel();
			panels[i].setLayout(new BoxLayout(panels[i],
					BoxLayout.PAGE_AXIS));
			JLabel header = new JLabel("<HTML><H3>&nbsp;" + NODENAMES[i]
			                                                          + "</H3></HTML>");
			panels[i].add(header);
			header.add(Box.createRigidArea(new Dimension(0, 20)));

			if (i > 0) {
				top.add(new DefaultMutableTreeNode(NODENAMES[i]));
			}

		}

		// Preferences -> Show Tabs

		final boolean tabsbottom = JBroFuzz.PREFS.getBoolean("UI.JBroFuzz.Tabs", false);
		final JCheckBox tabsCheckBox = new JCheckBox(
				" Show tabs in the main window at the top of the window (requires restart) ",
				tabsbottom);

		tabsCheckBox.setBorderPaintedFlat(true);
		tabsCheckBox
		.setToolTipText(" Tick this option, if you would like to see the tabs under " +
		"the tool bar, instead of at the bottom of the window ");

		tabsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (tabsCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean("UI.JBroFuzz.Tabs", true);
				} else {
					JBroFuzz.PREFS.putBoolean("UI.JBroFuzz.Tabs", false);
				}
			}
		});
		panels[0].add(tabsCheckBox);
		panels[0].add(Box.createRigidArea(new Dimension(0, 20)));

		// Preferences -> Check for New Version at Startup

		final boolean newVersionCheck = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_3,
				true);
		final JCheckBox newVCheckBox = new JCheckBox(
				" Check for a new version at startup ", newVersionCheck);

		newVCheckBox.setBorderPaintedFlat(true);
		newVCheckBox
		.setToolTipText(" Untick this option, if you do not want to be notified about new versions at startup ");

		newVCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (newVCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_3, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_3, false);
				}
			}
		});
		panels[0].add(newVCheckBox);
		panels[0].add(Box.createRigidArea(new Dimension(0, 20)));

		// Directory Locations...

		final boolean deletebox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_1, false);
		final JCheckBox deleteCheckBox = new JCheckBox(
				" On exit, delete any empty directories created at startup ",
				deletebox);

		deleteCheckBox.setBorderPaintedFlat(true);
		deleteCheckBox
		.setToolTipText("Tick this option, if you would like to remove any empty directories");

		deleteCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (deleteCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_1, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_1, false);
				}
			}
		});
		panels[1].add(deleteCheckBox);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));

		String dir = parent.getJBroFuzz().getHandler().getCanonicalPath();

		JLabel box = new JLabel("<html>"
				+ JBroFuzzFormat.centerAbbreviate(dir, 70) + "</html>");
		box.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Fuzzing (where data is saved) "),
				BorderFactory.createEmptyBorder(1, 1, 1, 5)));
		panels[1].add(box);
		panels[1].add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing... -> Socket Timeout

		final boolean socketbox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_1,
				false);
		final JCheckBox socketCheckBox = new JCheckBox(
				" Extend the socket timeout from 5 seconds to 30 seconds ",
				socketbox);

		socketCheckBox.setBorderPaintedFlat(true);
		socketCheckBox
		.setToolTipText("Tick this box, if you are getting timeout responses");

		socketCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (socketCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_1, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_1, false);
				}
			}
		});
		panels[2].add(socketCheckBox);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing... -> End of Line Character

		final boolean endlinebox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_2,
				false);
		final JCheckBox endlineCheckBox = new JCheckBox(
				" Use \"\\n\" instead of \"\\r\\n\" as an end of line character ",
				endlinebox);

		endlineCheckBox.setBorderPaintedFlat(true);
		endlineCheckBox
		.setToolTipText("Tick this box, if you want to use \"\\n\" for each line put on the wire");

		endlineCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (endlineCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_2, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_2, false);
				}
			}
		});
		panels[2].add(endlineCheckBox);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));


		// Fuzzing... -> Word wrap request text panel

		final boolean wrap_req_box = JBroFuzz.PREFS.getBoolean(
				JBroFuzzFormat.WRAP_REQUEST, false);
		final JCheckBox wrap_req_check_box = new JCheckBox(
				" Word wrap text in the \"Request\" area (requires restart) ",
				wrap_req_box);
		wrap_req_check_box.setBorderPaintedFlat(true);
		wrap_req_check_box
		.setToolTipText("If ticked, the request text area will wrap the text to fit the size of the area");

		wrap_req_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (wrap_req_check_box.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_REQUEST, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_REQUEST, false);
				}
			}
		});

		panels[2].add(wrap_req_check_box);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing... -> Word wrap response text panel

		final boolean wrap_res_bool = JBroFuzz.PREFS.getBoolean(
				JBroFuzzFormat.WRAP_RESPONSE, false);
		final JCheckBox wrap_res_check_box = new JCheckBox(
				" Word wrap text in the \"Response\" window (requires restart) ",
				wrap_res_bool);
		wrap_res_check_box.setBorderPaintedFlat(true);
		wrap_res_check_box
		.setToolTipText("Tick this box, to see all output text wrapped to the size of the response window");

		wrap_res_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (wrap_res_check_box.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_RESPONSE, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_RESPONSE, false);
				}
			}
		});

		panels[2].add(wrap_res_check_box);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing ...-> "Re-send POST Data if 100 Continue is received"
		final boolean cont_bool = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_4,
				true);
		final JCheckBox cont_check_box = new JCheckBox(
				"Re-send POST Data if 100 Continue is received", cont_bool);
		cont_check_box.setBorderPaintedFlat(true);
		cont_check_box
		.setToolTipText("Tick this box, to re-send the POST Data in a HTTP/1.1 message, if a 100 continue is received");

		cont_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event5) {
				if (cont_check_box.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_4, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_4, false);
				}
			}
		});

		panels[2].add(cont_check_box);
		panels[2].add(Box.createRigidArea(new Dimension(0, 20)));


		// Fuzzing: On The Wire... -> Show on the wire tab after fuzzing finished
		final boolean showwirebox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_3,
				false);
		final JCheckBox showwireCheckBox = new JCheckBox(
				" Show \"On The Wire\" tab after fuzzing has stopped or finished ",
				showwirebox);

		showwireCheckBox.setBorderPaintedFlat(true);
		showwireCheckBox
		.setToolTipText("Tick this box, if you want to always see the \"On The Wire\" tab");

		showwireCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (showwireCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_3, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_3, false);
				}
			}
		});

		panels[3].add(showwireCheckBox);
		panels[3].add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing: On The Wire... -> Display responses inside the On The Wire text area
		final boolean displayResponseBox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_3_1, false);
		final JCheckBox displayResponseCheckBox = new JCheckBox(
				" Display the Requests as well as the Responses received ",
				displayResponseBox);

		displayResponseCheckBox.setBorderPaintedFlat(true);
		displayResponseCheckBox.setToolTipText(
		"Tick this box to display the responses received for each request sent within the \"On The Wire\" tab");

		displayResponseCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (displayResponseCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_3_1, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_3_1, false);
				}
			}
		});

		panels[3].add(displayResponseCheckBox);
		panels[3].add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing: Output -> Double click opens up browser or panel
		final boolean fuzzingResponseDoubleClickBox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_OUTPUT_1, true);
		final JCheckBox fuzzingResponseDoubleClickCheckBox = new JCheckBox(
				" Double click on a Response opens it up in a Browser ",
				fuzzingResponseDoubleClickBox);

		fuzzingResponseDoubleClickCheckBox.setBorderPainted(false);
		fuzzingResponseDoubleClickCheckBox.setToolTipText(
		"Tick this box to open up response in a browser, instead of a text-based window");

		fuzzingResponseDoubleClickCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (fuzzingResponseDoubleClickCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_OUTPUT_1, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_OUTPUT_1, false);
				}
			}
		});

		panels[4].add(fuzzingResponseDoubleClickCheckBox);
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
		for (JPanel jp : panels) {
			jp.setMinimumSize(minimumSize);
		}
		splitPane.setDividerLocation(150);

		// Traverse tree from root
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		parent.getPanelPayloads().expandAll(tree, new TreePath(root), true);
		// Select the first row
		tree.setSelectionRow(3);

		// Bottom three buttons
		ok = new JButton("  OK  ");
		cancel = new JButton("Cancel");

		ok.setEnabled(true);
		cancel.setEnabled(true);

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(ok);
		buttonPanel.add(cancel);

		// Action Listeners for all three buttons

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						JBroFuzzPrefs.this.dispose();

					}
				});
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						JBroFuzzPrefs.this.dispose();

					}
				});
			}
		});

		// Keyboard listener on the treeView for escape to cancel

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					JBroFuzzPrefs.this.dispose();

				}
			}
		});

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs(parent.getLocationOnScreen().x + 100), Math
				.abs(parent.getLocationOnScreen().y + 20));
		this.setSize(SIZE_X, SIZE_Y);
		setMinimumSize(new Dimension(SIZE_X, SIZE_Y));
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
		for (int i = 0; i < NODENAMES.length; i++) {

			if (s.equalsIgnoreCase(NODENAMES[i])) {
				splitPane.setRightComponent(panels[i]);
				splitPane.setDividerLocation(splitPane.getDividerLocation());
			}

		} // for loop
	}
}