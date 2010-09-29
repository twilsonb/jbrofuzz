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
package org.owasp.jbrofuzz.ui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.owasp.jbrofuzz.version.ImageCreator;

/**
 * <p>
 * The about box used in the FrameWindow.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.5
 * @since 1.3
 */
public final class AboutBox extends JDialog implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2826027958413054181L;

	/**
	 * <p>Enum for selected which tab is selected.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 *
	 */
	public enum Tab {
		ABOUT, LICENSE, DISCLAIMER, ACKNOWLEDGEMENTS, CODE
	}

	// Dimensions of the about box
	private static final int SIZE_X = 450;
	private static final int SIZE_Y = 300;

	// The tabbed pane, holding all the different panels and labels
	private final transient JTabbedPane tabbedPane;

	/**
	 * <p>
	 * The main constructor for the AboutBox. This method is private as it is
	 * being called through the singleton method get instance.
	 * </p>
	 * 
	 * @param parent
	 * @param tab
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public AboutBox(final JFrame parent, final Tab selectedTab) {

		super(parent, " JBroFuzz - About ", true);

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		// The tabbed pane holding all the different tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.add(createAbout(), " About ");
		tabbedPane.add(createLicense(), " License ");
		tabbedPane.add(createDisclaimer(), " Disclaimer ");
		tabbedPane.add(createAcknowledgements(), " Acknowledgements ");
		tabbedPane.add(createCode(), " Code ");
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// Set the tab to be displayed
		setTab(selectedTab);

		// OK Button
		final JButton ok = new JButton("  OK  ");
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(ok);
		
		// Listeners
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						AboutBox.this.dispose();
					}
				});
			}
		});
		ok.addKeyListener(this);
		tabbedPane.addKeyListener(this);
		
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Where to show the about box
		this.setLocation(
				parent.getLocation().x + (parent.getWidth() - SIZE_X) / 2, 
				parent.getLocation().y + (parent.getHeight() - SIZE_Y) / 2
		);

		this.setSize(AboutBox.SIZE_X, AboutBox.SIZE_Y);
		setMinimumSize(new Dimension(SIZE_X, SIZE_Y));
		setResizable(false);
		setVisible(true);
	}
	
	public void keyTyped(final KeyEvent kEvent) {
		// 
	}
	
	public void keyPressed(final KeyEvent kEvent) {
		if (kEvent.getKeyCode() == 27) {

			AboutBox.this.dispose();

		}
	}
	
	public void keyReleased(final KeyEvent kEvent) {
		// 
	}
	

	private JComponent createCode() {

		final JLabel codeLabel = new JLabel(ImageCreator.IMG_OWASP);
		codeLabel.setLocation(20, 45);
		codeLabel.setSize(100, 100);

		final URL codeURL = ClassLoader.getSystemClassLoader()
		.getResource("LICENSE/CODE.txt");
		JEditorPane code; 
		try {
			code = new JEditorPane(codeURL);
			
		} catch (final IOException eAbout) {
			code = new JEditorPane();
			code.setText("Code information cannot be found!");
		}
		code.setEditable(false);
		code.addKeyListener(this);
		
		final JScrollPane codeScroll = new JScrollPane(code);
		codeScroll.setVerticalScrollBarPolicy(20);
		codeScroll.setHorizontalScrollBarPolicy(30);
		codeScroll.setLocation(140, 5);
		codeScroll.setSize(290, 185);
		
		final JPanel codePanel = new JPanel();
		codePanel.setLayout(null);
		codePanel.add(codeLabel);
		codePanel.add(codeScroll);
		return codePanel;

	}
	
	private JComponent createAcknowledgements() {

		final URL acknowledgementsURL = ClassLoader.getSystemClassLoader()
		.getResource("LICENSE/ACKNOWLEDGEMENTS.txt");
		// The acknoledgement editor pane
		JEditorPane acknowledgements;
		try {
			acknowledgements = new JEditorPane(acknowledgementsURL);
		} catch (final IOException e) {
			acknowledgements = new JEditorPane();
			acknowledgements.setText("Acknoledgements file cannot be found");
		}
		acknowledgements.setEditable(false);
		acknowledgements.addKeyListener(this);
		
		final JScrollPane ackScrollPane = new JScrollPane(acknowledgements);
		ackScrollPane.setVerticalScrollBarPolicy(20);
		ackScrollPane.setHorizontalScrollBarPolicy(30);
		return ackScrollPane;
	}
	
	private JComponent createDisclaimer() {

		final JLabel disclaimerLabel = new JLabel(ImageCreator.IMG_OWASP);
		disclaimerLabel.setLocation(20, 45);
		disclaimerLabel.setSize(100, 100);

		final URL disclaimerURL = ClassLoader.getSystemClassLoader()
		.getResource("LICENSE/DISCLAIMER.txt");
		JEditorPane disclaimer; // TextPane = new JEditorPane("text/html", JBroFuzzFormat.ABOUT);
		try {
			disclaimer = new JEditorPane(disclaimerURL);
			
		} catch (final IOException eAbout) {
			disclaimer = new JEditorPane();
			disclaimer.setText("About information cannot be found!");
		}
		disclaimer.setEditable(false);
		disclaimer.addKeyListener(this);
		
		final JScrollPane disclaimerScroll = new JScrollPane(disclaimer);
		disclaimerScroll.setVerticalScrollBarPolicy(20);
		disclaimerScroll.setHorizontalScrollBarPolicy(30);
		disclaimerScroll.setLocation(140, 5);
		disclaimerScroll.setSize(290, 185);
		
		final JPanel disclaimerPanel = new JPanel();
		disclaimerPanel.setLayout(null);
		disclaimerPanel.add(disclaimerLabel);
		disclaimerPanel.add(disclaimerScroll);
		return disclaimerPanel;
		
	}
	
	private JComponent createLicense() {
	
		final URL licenseURL = ClassLoader.getSystemClassLoader().getResource(
		"LICENSE/gpl-license.txt");
		// The license editor pane
		JEditorPane license;
		try {
			license = new JEditorPane(licenseURL);

		} catch (final IOException e) {
			license = new JEditorPane();
			license.setText("GPL Licence file cannot be found!");
		}
		license.setEditable(false);
		license.addKeyListener(this);

		final JScrollPane lcsScrollPane = new JScrollPane(license);
		lcsScrollPane.setVerticalScrollBarPolicy(20);
		lcsScrollPane.setHorizontalScrollBarPolicy(30);
		return lcsScrollPane;
	}
	
	private JComponent createAbout() {
		
		final JLabel aboutLabel = new JLabel(ImageCreator.IMG_OWASP);
		aboutLabel.setLocation(20, 45);
		aboutLabel.setSize(100, 100);

		final URL aboutURL = ClassLoader.getSystemClassLoader()
		.getResource("LICENSE/ABOUT.txt");
		JEditorPane about; // TextPane = new JEditorPane("text/html", JBroFuzzFormat.ABOUT);
		try {
			about = new JEditorPane(aboutURL);
			
		} catch (final IOException eAbout) {
			about = new JEditorPane();
			about.setText("About information cannot be found!");
		}
		about.setEditable(false);
		about.addKeyListener(this);
		
		final JScrollPane aboutScroll = new JScrollPane(about);
		aboutScroll.setVerticalScrollBarPolicy(20);
		aboutScroll.setHorizontalScrollBarPolicy(30);
		aboutScroll.setLocation(140, 5);
		aboutScroll.setSize(290, 185);
		
		final JPanel aboutPanel = new JPanel();
		aboutPanel.setLayout(null);
		aboutPanel.add(aboutLabel);
		aboutPanel.add(aboutScroll);
		return aboutPanel;
		
	}
	
	private void setTab(final Tab myTab) {
		switch (myTab) {
		case ABOUT: tabbedPane.setSelectedIndex(0);
			break;
		case LICENSE: tabbedPane.setSelectedIndex(1);
			break;
		case DISCLAIMER: tabbedPane.setSelectedIndex(2);
			break;
		case ACKNOWLEDGEMENTS: tabbedPane.setSelectedIndex(3);
			break;
		case CODE: tabbedPane.setSelectedIndex(4);
			break;
		default: tabbedPane.setSelectedIndex(0);
			break;
		}
	}
}
