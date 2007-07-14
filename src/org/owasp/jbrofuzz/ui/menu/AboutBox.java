/**
 * AboutBox.java 0.7
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBRFormat;
/**
 * <p>The about box used in the FrameWindow.</p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class AboutBox extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7515292150164781290L;
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

	/**
	 * <p>The main constructor for the AboutBox. This method is private as it is being called through 
	 * the singleton method get instance.</p>
	 */
	public AboutBox(final JFrame parent, final int tab) {
		super(parent, " About ", true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		this.setFont(new Font ("SansSerif", Font.PLAIN, 12));
		
		final URL licenseURL = ClassLoader.getSystemClassLoader().getResource("LICENSE/gpl-license.txt");
		final URL	disclaimerURL = ClassLoader.getSystemClassLoader().getResource("LICENSE/NOTICE.txt");

		// The about editor label
		final JLabel about = new JLabel (JBRFormat.ABOUTTEXT, ImageCreator.OWASP_IMAGE, SwingConstants.LEFT);
		
		// The license editor pane
		JEditorPane license;
		try {
			license = new JEditorPane(licenseURL);
			
		} catch (final IOException e) {
			license = new JEditorPane();
			license.setText("GPL Licence file cannot be found");
		}
		license.setEditable(false);

		final JScrollPane lcsScrollPane = new JScrollPane(license);
		lcsScrollPane.setVerticalScrollBarPolicy(20);
		lcsScrollPane.setHorizontalScrollBarPolicy(30);

		// The disclaimer editor label
		final JLabel disclaimer = new JLabel( JBRFormat.DISCLAIMER, ImageCreator.OWASP_IMAGE, SwingConstants.LEFT);
		
		// The acknoledgement editor pane
		JEditorPane acknoledgements;
		try {
			acknoledgements = new JEditorPane(disclaimerURL);
		} catch (final IOException e) {
			acknoledgements = new JEditorPane();
			acknoledgements.setText("Acknoledgements file cannot be found");
		}
		acknoledgements.setEditable(false);

		final JScrollPane ackScrollPane = new JScrollPane(acknoledgements);
		ackScrollPane.setVerticalScrollBarPolicy(20);
		ackScrollPane.setHorizontalScrollBarPolicy(30);		

		// The tabbed pane holding all the different tabs
		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.add(about, " About ");
		this.tabbedPane.add(lcsScrollPane, " License ");
		this.tabbedPane.add(disclaimer, " Disclaimer ");
		this.tabbedPane.add(ackScrollPane, " Acknoledgements ");
		this.getContentPane().add(this.tabbedPane, BorderLayout.CENTER);
		// Set the tab to be displayed
		this.setTab(tab);
		// OK Button
		this.ok = new JButton("OK");
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add (this.ok);
		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						AboutBox.this.dispose();
					}
				});       
			}
		});
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs((parent.getWidth() / 2) - (AboutBox.x / 2 - 100) ), 
				Math.abs((parent.getHeight() / 2) - (AboutBox.y / 2) + 100 ));
		this.setSize(AboutBox.x, AboutBox.y);
		this.setResizable(false);
		this.setVisible(true);		
	}
	
	private void setTab(final int tab) {
		switch (tab) {
        case 0:  this.tabbedPane.setSelectedIndex(AboutBox.ABOUT); break;
        case 1:  this.tabbedPane.setSelectedIndex(AboutBox.LICENSE); break;
        case 2:  this.tabbedPane.setSelectedIndex(AboutBox.DISCLAIMER); break;
        case 3:  this.tabbedPane.setSelectedIndex(AboutBox.ACKNOWLEDGEMENTS); break;
        default: this.tabbedPane.setSelectedIndex(0); break;
    }
	}
}
