/**
 * DefinitionsPanel.java 0.6
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

import java.io.IOException;
import java.net.*;

import org.owasp.jbrofuzz.ui.util.*;
import org.owasp.jbrofuzz.version.*;
/**
 * <p>The about box used in the FrameWindow.</p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class AboutBox extends JDialog implements ActionListener {

	public static int ABOUT = 0;
	public static int LICENSE = 1;
	public static int DISCLAIMER = 2;
	public static int ACKNOWLEDGEMENTS = 3;
	
	// The tabbed pane, holding all the different panels and labels
	private JTabbedPane tabbedPane;
	// The ok button at the bottom of the box
	private JButton ok;
	// Dimensions of the about box
	private static final int x = 400;
	private static final int y = 300;
	// The singleton instance
	private static AboutBox instance = null;

	/**
	 * <p>Method for returning the singleton instance of the AboutBox JDialog.</p>
	 * 
	 * @param parent JFrame
	 * @return AboutBox
	 */
	public static AboutBox getInstance(JFrame parent, int tab) {
		if(instance == null) {
			instance = new AboutBox(parent, tab);
		} else {
			// Update Look and Feel
			SwingUtilities.updateComponentTreeUI( instance );
			// Set the singleton to be visible
			instance.setVisible(true);
			instance.setTab(tab);
		}
		return instance;
	}
	/**
	 * <p>The main constructor for the AboutBox. This method is private as it is being called through 
	 * the singleton method get instance.</p>
	 */
	private AboutBox(JFrame parent, int tab) {
		super(parent);
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));
		
		URL licenseURL = ClassLoader.getSystemClassLoader().getResource("LICENSE/gpl-license.txt");
		URL	disclaimerURL = ClassLoader.getSystemClassLoader().getResource("LICENSE/NOTICE.txt");

		// The about editor label
		JLabel about = new JLabel (JBRFormat.ABOUTTEXT, ImageCreator.OWASP_IMAGE, JLabel.LEFT);
		
		// The license editor pane
		JEditorPane license;
		try {
			license = new JEditorPane(licenseURL);
			
		} catch (IOException e) {
			license = new JEditorPane();
			license.setText("GPL Licence file cannot be found");
		}
		license.setEditable(false);

		JScrollPane lcsScrollPane = new JScrollPane(license);
		lcsScrollPane.setVerticalScrollBarPolicy(20);
		lcsScrollPane.setHorizontalScrollBarPolicy(30);

		// The disclaimer editor label
		JLabel disclaimer = new JLabel( JBRFormat.DISCLAIMER, ImageCreator.OWASP_IMAGE, JLabel.LEFT);
		
		// The acknoledgement editor pane
		JEditorPane acknoledgements;
		try {
			acknoledgements = new JEditorPane(disclaimerURL);
		} catch (IOException e) {
			acknoledgements = new JEditorPane();
			acknoledgements.setText("Acknoledgements file cannot be found");
		}
		acknoledgements.setEditable(false);

		JScrollPane ackScrollPane = new JScrollPane(acknoledgements);
		ackScrollPane.setVerticalScrollBarPolicy(20);
		ackScrollPane.setHorizontalScrollBarPolicy(30);		

		// The tabbed pane holding all the different tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.add(about, " About ");
		tabbedPane.add(lcsScrollPane, " License ");
		tabbedPane.add(disclaimer, " Disclaimer ");
		tabbedPane.add(ackScrollPane, " Acknoledgements ");
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		// Set the tab to be displayed
		setTab(tab);
		// OK Button
		ok = new JButton("OK");
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add (ok);
		ok.addActionListener(this);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		setLocation(Math.abs((parent.getWidth() / 2) - (x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (y / 2) + 100 ));
		setSize(x, y);
		setResizable(false);
		setVisible(true);		
	}
	
	private void setTab(int tab) {
		switch (tab) {
        case 0:  tabbedPane.setSelectedIndex(ABOUT); break;
        case 1:  tabbedPane.setSelectedIndex(LICENSE); break;
        case 2:  tabbedPane.setSelectedIndex(DISCLAIMER); break;
        case 3:  tabbedPane.setSelectedIndex(ACKNOWLEDGEMENTS); break;
        default: tabbedPane.setSelectedIndex(0); break;
    }
	}
	public void actionPerformed(ActionEvent newEvent) {
		setVisible(false);
	}  
}
