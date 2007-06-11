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
/**
 * <p>The about box used in the FrameWindow.</p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class AboutBox extends JDialog implements ActionListener {

	private JButton ok;
	// Dimensions of the about box
	private static final int x = 400;
	private static final int y = 300;

	private static AboutBox instance = null;

	public static AboutBox getInstance(JFrame parent) {
		if(instance == null) {
			instance = new AboutBox(parent);
		} else {
			instance.setVisible(true);
		}
		return instance;
	}
	/**
	 * The main constructor for the AboutBox.
	 */
	private AboutBox(JFrame parent) {
		super(parent);
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));
		URL licenseURL = ClassLoader.getSystemClassLoader().getResource("LICENSE/gpl-license.txt");


		// The about editor label
		JLabel about = new JLabel ("<HTML><CENTER><B>Some Header</B><P><P>&copy;2007 </HTML>", 
				ImageCreator.OWASP_IMAGE, JLabel.LEFT);
		
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

		// The acknoledgement editor pane
		JEditorPane acknoledgements = new JEditorPane();
		acknoledgements.setEditable(false);
		acknoledgements.setText("Acknoledgements");

		JScrollPane ackScrollPane = new JScrollPane(acknoledgements);
		ackScrollPane.setVerticalScrollBarPolicy(20);
		ackScrollPane.setHorizontalScrollBarPolicy(30);		

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(about, " About ");
		tabbedPane.add(lcsScrollPane, " License ");
		tabbedPane.add(ackScrollPane, " Acknoledgements ");
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// OK Button
		ok = new JButton("OK");
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
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

	public void actionPerformed(ActionEvent newEvent) {
		setVisible(false);
	}  
}
