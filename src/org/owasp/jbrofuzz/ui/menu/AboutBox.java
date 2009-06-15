/**
 * JBroFuzz 1.4
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
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
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

import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * The about box used in the FrameWindow.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.3
 */
public class AboutBox extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7515292150164781290L;

	/**
	 * <p>
	 * Constant for the about tab to be displayed.
	 * </p>
	 */
	public static final int ABOUT = 0;

	/**
	 * <p>
	 * Constant for the license tab to be displayed.
	 * </p>
	 */
	public static final int LICENSE = 1;

	/**
	 * <p>
	 * Constant for the disclaimer tab to be displayed.
	 * </p>
	 */
	public static final int DISCLAIMER = 2;

	/**
	 * <p>
	 * Constant for the acknowledgements tab to be displayed.
	 * </p>
	 */
	public static final int ACKNOWLEDGEMENTS = 3;

	// Dimensions of the about box
	private static final int x = 450;
	private static final int y = 300;

	// The tabbed pane, holding all the different panels and labels
	private JTabbedPane tabbedPane;

	// The ok button at the bottom of the box
	private JButton ok;

	/**
	 * <p>
	 * The main constructor for the AboutBox. This method is private as it is
	 * being called through the singleton method get instance.
	 * </p>
	 * 
	 * @param parent
	 * @param tab
	 */
	public AboutBox(final JFrame parent, final int tab) {

		super(parent, " JBroFuzz - About ", true);

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		final URL licenseURL = ClassLoader.getSystemClassLoader().getResource(
				"LICENSE/gpl-license.txt");
		final URL disclaimerURL = ClassLoader.getSystemClassLoader()
				.getResource("LICENSE/NOTICE.txt");

		// The about editor label
		final JLabel about = new JLabel(JBroFuzzFormat.ABOUT,
				ImageCreator.IMG_OWASP, SwingConstants.LEFT);
		about.setIconTextGap(20);
		about.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

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
		final JLabel disclaimer = new JLabel(JBroFuzzFormat.DISCLAIMER,
				ImageCreator.IMG_OWASP, SwingConstants.LEFT);
		disclaimer.setIconTextGap(20);
		disclaimer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

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
		tabbedPane = new JTabbedPane();
		tabbedPane.add(about, " About ");
		tabbedPane.add(lcsScrollPane, " License ");
		tabbedPane.add(disclaimer, " Disclaimer ");
		tabbedPane.add(ackScrollPane, " Acknowledgements ");
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// Set the tab to be displayed
		setTab(tab);

		// OK Button
		ok = new JButton("  OK  ");
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(ok);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						AboutBox.this.dispose();
					}
				});
			}
		});
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math
				.abs(parent.getLocation().y + 100));
		this.setSize(AboutBox.x, AboutBox.y);
		setMinimumSize(new Dimension(x, y));
		setResizable(true);
		setVisible(true);
	}

	private void setTab(final int tab) {
		switch (tab) {
		case 0:
			tabbedPane.setSelectedIndex(AboutBox.ABOUT);
			break;
		case 1:
			tabbedPane.setSelectedIndex(AboutBox.LICENSE);
			break;
		case 2:
			tabbedPane.setSelectedIndex(AboutBox.DISCLAIMER);
			break;
		case 3:
			tabbedPane.setSelectedIndex(AboutBox.ACKNOWLEDGEMENTS);
			break;
		default:
			tabbedPane.setSelectedIndex(0);
			break;
		}
	}
}
