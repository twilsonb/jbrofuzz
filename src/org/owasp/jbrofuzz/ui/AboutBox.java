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
package org.owasp.jbrofuzz.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

//import org.owasp.jbrofuzz.ui.util.ImageCreator;

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

	/**
	 * The main constructor for the AboutBox.
	 */
	public AboutBox(JFrame parent) {
		super(parent);
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.BOLD, 14));
		
		// The about editor pane
		JEditorPane about = new JEditorPane();
		about.setText("");
		
		JScrollPane aboutScrollPane = new JScrollPane(about);
    aboutScrollPane.setVerticalScrollBarPolicy(20);
    aboutScrollPane.setHorizontalScrollBarPolicy(30);

    // The license editor pane
		JEditorPane license = new JEditorPane();
		license.setText("License");
		
		JScrollPane licenseScrollPane = new JScrollPane(license);
		licenseScrollPane.setVerticalScrollBarPolicy(20);
		licenseScrollPane.setHorizontalScrollBarPolicy(30);
		
		// The acknoledgement editor pane
		JEditorPane acknoledgements = new JEditorPane();
		acknoledgements.setText("Acknoledgements");
		
		JScrollPane acknoledgementsScrollPane = new JScrollPane(acknoledgements);
		acknoledgementsScrollPane.setVerticalScrollBarPolicy(20);
		acknoledgementsScrollPane.setHorizontalScrollBarPolicy(30);
		
		/*
			JLabel title = new JLabel ("<HTML><CENTER><B>Some Header</B><P><P>&copy;2007 </HTML>");
		*/

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(aboutScrollPane, " About ");
		tabbedPane.add(licenseScrollPane, " License ");
		tabbedPane.add(acknoledgementsScrollPane, " Acknoledgements ");
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
