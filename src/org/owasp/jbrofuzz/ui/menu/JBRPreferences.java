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

import org.owasp.jbrofuzz.ui.util.ImageCreator;
/**
 * <p>The preferences panel.</p>
 *
 * @author subere (at) uncon org
 * @version 0.7
 */
public class JBRPreferences extends JDialog implements ActionListener, TreeSelectionListener {
	
	// The buttons
	private JButton ok, cancel;
	// The tree
	private JTree tree;
	
	// Dimensions of the about box
	private static final int x = 500;
	private static final int y = 400;
	
	private static JBRPreferences instance = null;
	
	public static JBRPreferences getInstance(JFrame parent) {
		if(instance == null) {
			instance = new JBRPreferences(parent);
		} else {
			// Update Look and Feel
			SwingUtilities.updateComponentTreeUI( instance );
			// Set the singleton to be visible
			instance.setVisible(true);
		}
		return instance;
	}
	/**
	 * The main constructor for the Preferences Window.
	 */
	private JBRPreferences(JFrame parent) {
		super(parent);
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));
		
		// The tree nodes on the left hand side
        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode("Preferences");
        DefaultMutableTreeNode info = new DefaultMutableTreeNode("Info");
        DefaultMutableTreeNode dirs = new DefaultMutableTreeNode("Directories");
        top.add(info);
        top.add(dirs);
        
        // Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
		
        // Create the right hand side panel
        JLabel infoPanel = new JLabel("<HTML><CENTER><B>Some Header</B><P><P>&copy;2007 </HTML>", 
				ImageCreator.OWASP_IMAGE, JLabel.LEFT);
        
        // Top split pane
		JSplitPane prefsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		prefsPane.setLeftComponent(tree);
		prefsPane.setRightComponent(infoPanel);
		
		Dimension minimumSize = new Dimension(100, 50);
		tree.setMinimumSize(minimumSize);
		/*
		prefsPane.setDividerLocation(100); //XXX: ignored in some releases
        //of Swing. bug 4101306
		//workaround for bug 4101306:
		tree.setPreferredSize(new Dimension(100, 100)); 

		prefsPane.setPreferredSize(new Dimension(500, 300));
		*/
		prefsPane.setOneTouchExpandable(false);
		prefsPane.setDividerLocation(150);

		this.getContentPane().add(prefsPane, BorderLayout.CENTER);
		
		// Bottom buttons
		ok = new JButton("OK");
		cancel = new JButton("Cancel");
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add (ok);
		buttonPanel.add(cancel);
		
		ok.addActionListener(this);
		cancel.addActionListener(this);
		
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		setLocation(Math.abs((parent.getWidth() / 2) - (x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (y / 2) + 100 ));
		setSize(x, y);
		setResizable(false);
		setVisible(true);		
	}

	public void actionPerformed(ActionEvent newEvent) {
		setVisible(false);
	}  
	
	public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            System.out.println("Node is Leaf..");
        }
        else {
        	System.out.println("Node is Node...");
        }
    }
}
