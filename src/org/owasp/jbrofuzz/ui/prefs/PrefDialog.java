/**
 * JBroFuzz 2.4
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
package org.owasp.jbrofuzz.ui.prefs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
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

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The preferences main dialog. This is the panel the user sees when selecting Options
 * -> Preferences from the main menu bar.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.4
 * @since 2.0
 */
public class PrefDialog extends JDialog implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6206219050510937296L;
	
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;

	// The tree
	private final JTree tree;
	// The main split pane
	private final JSplitPane splitPane;

	private final AbstractPrefsPanel[] panels = new AbstractPrefsPanel[6];
	
	public enum PrefsPanel { 
		PREFERENCES, DIRECTORIES, FUZZING, 
		ONTHEWIRE, OUTPUT, UPDATE
	}
	
	private final JBroFuzzWindow parent;
	
	private final JButton applyBut;
	
	/**
	 * <p>
	 * The preferences dialog constructor, passing the main window as argument.
	 * </p>
	 * 
	 * @param parent
	 * 
	 * @author subere@uncon.org
	 * @version 2.4
	 * @since 2.0
	 */
	public PrefDialog(final JBroFuzzWindow parent, PrefsPanel panelType) {

		super(parent, " JBroFuzz - Preferences ", true);
		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("Verdana", Font.PLAIN, 12));

		this.parent = parent;
		
		panels[0] = new PrefsPPanel(this);		
		panels[1] = new DirsPPanel(this);
		panels[2] = new FuzzPPanel(this);
		panels[3] = new WirePPanel(this);
		panels[4] = new OutputPPanel(this);
		panels[5] = new UpdatePPanel(this);

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				panels[0].getName());
		for(int i = 1; i < panels.length; i++) {
			top.add(panels[i].getTreeNode());
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

		// Create the top split pane, showing the treeView and the Preferences
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftScrollPane);
		splitPane.setRightComponent(panels[0]);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerLocation(150);

		// Set the minimum size for all components
		leftScrollPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		for (final JPanel jp : panels) {
			jp.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		}
		splitPane.setDividerLocation(150);

		// Traverse tree from root
		final TreeNode root = (TreeNode) tree.getModel().getRoot();
		parent.getPanelPayloads().expandAll(tree, new TreePath(root), true);

		// Select the row specified by the enum
		switch(panelType) {
			
		case PREFERENCES: tree.setSelectionRow(0); 
			break;
		case DIRECTORIES: tree.setSelectionRow(1); 
			break;
		case FUZZING: tree.setSelectionRow(2); 
			break;
		case ONTHEWIRE: tree.setSelectionRow(3); 
			break;
		case OUTPUT: tree.setSelectionRow(4); 
			break;
		case UPDATE: tree.setSelectionRow(5); 
			break;
			
		default: tree.setSelectionRow(0); 
			break;
		}
		
		
		// Bottom three buttons
		JButton okBut, cancelBut;
		
		okBut = new JButton("  OK  ");
		cancelBut = new JButton("Cancel");
		applyBut = new JButton(" Apply ");
		
		okBut.setEnabled(true);
		cancelBut.setEnabled(true);
		applyBut.setEnabled(false);
		
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(okBut);
		buttonPanel.add(cancelBut);
		buttonPanel.add(applyBut);

		// Action Listeners for all three buttons

		okBut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent okEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						for(final AbstractPrefsPanel p : panels) {
							p.apply();
						}
						PrefDialog.this.dispose();

					}
				});
			}
		});

		cancelBut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent cancelEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						PrefDialog.this.dispose();

					}
				});
			}
		});

		applyBut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent applyEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						for(final AbstractPrefsPanel p : panels) {
							p.apply();
						}
						applyBut.setEnabled(false);

					}
				});
			}
		});

		
		// Keyboard listener on the treeView for escape to cancel

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent kEvent) {
				if (kEvent.getKeyCode() == 27) {

					PrefDialog.this.dispose();

				}
			}
		});

		// Add the split pane to this panel
		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Where to show the preferences box
		this.setLocation(
				parent.getLocation().x + (parent.getWidth() - SIZE_X) / 2, 
				parent.getLocation().y + (parent.getHeight() - SIZE_Y) / 2
		);
		
		this.setSize(SIZE_X, SIZE_Y);
		setMinimumSize(new Dimension(SIZE_X, SIZE_Y));
		setResizable(true);
		setVisible(true);
	}
	

	public void valueChanged(final TreeSelectionEvent tEvent) {

		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		for (int i = 0; i < panels.length; i++) {

			if (node.toString().equalsIgnoreCase(panels[i].getName())) {
				splitPane.setRightComponent(panels[i]);
				splitPane.setDividerLocation(splitPane.getDividerLocation());
			}

		} // for loop
		
		if(applyBut != null) {
			applyBut.setEnabled(true);
		}
		
	}
	
	protected JBroFuzzWindow getJBroFuzzWindow() {
		return this.parent;
	}
	
	protected void setApplyEnabled(final boolean applyOn) {
		applyBut.setEnabled(applyOn);
	}
}