/**
 * JBroFuzz 2.3
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
package org.owasp.jbrofuzz.help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;

/**
 * <p>
 * The main shortcuts window.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 */
public class Shortcuts extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1726771399839929062L;

	// Dimensions of the about box
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;
	//
	private static final String FILE_NOT_FOUND = "Help file could not be located.";

	/**
	 * <p>
	 * Boolean is true if Shortcuts are already showing.
	 * </p>
	 */
	private static boolean shortcutsShowing = false;

	/**
	 * <p>
	 * The constructor of the help topics JDialog.
	 * </p>
	 * 
	 * @param parent
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public Shortcuts(final JBroFuzzWindow parent) {

		if (shortcutsShowing) {
			return;
		}
		shortcutsShowing = true;

		// super(parent, " JBroFuzz - Help Topics ", true);
		setTitle(" JBroFuzz - Keyboard Shortcuts ");

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		final URL shortcutsURL = ClassLoader.getSystemClassLoader().getResource(
				"help/shortcuts.html");

		final JEditorPane helpPane = new JEditorPane();;
		try {
			helpPane.setPage(shortcutsURL);
		} catch (final IOException e1) {
			helpPane.setContentType("text/html");
			helpPane.setText(FILE_NOT_FOUND);
		}
		helpPane.setEditable(false);

		// Add the split pane to this panel
		getContentPane().add(new JScrollPane(helpPane), BorderLayout.CENTER);

		// Bottom buttons
		final JButton printBun = new JButton(" Print ");
		final JButton okButton = new JButton(" Close ");
		
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(printBun);
		buttonPanel.add(okButton);

		printBun.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				try {
					final boolean complete = helpPane.print();
					if (!complete) {
						Logger.log("User cancelled Printing", 1);
					}
				} catch (final PrinterException prException) {
					Logger.log("A Printing Exception Occured", 4);
				}

			}
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				shortcutsShowing = false;
				Shortcuts.this.dispose();

			}
		});

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Where to show the shortcuts frame
		this.setLocation(
				parent.getLocation().x + (parent.getWidth() - SIZE_X) / 2, 
				parent.getLocation().y + (parent.getHeight() - SIZE_Y) / 2
		);
		
		this.setSize(Shortcuts.SIZE_X, Shortcuts.SIZE_Y);
		setMinimumSize(new Dimension(SIZE_X / 2, SIZE_Y / 2));

		setResizable(true);
		setVisible(true);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent wEvent) {
				shortcutsShowing = false;
				dispose();
			}
		});

	}

}
