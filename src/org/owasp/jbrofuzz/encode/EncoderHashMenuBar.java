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
package org.owasp.jbrofuzz.encode;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.owasp.jbrofuzz.ui.menu.AboutBox;
import org.owasp.jbrofuzz.version.ImageCreator;

/**
 * <p>The menu bar for the Encoder/Hash Frame.</p>
 * 
 * @author subere@uncon.org
 * @version 2.5
 * @since 0.1
 */
public class EncoderHashMenuBar extends JMenuBar {

	private static final long serialVersionUID = 4335762453196377717L;

	private final JMenu file, help;
	
	private final JMenuItem exit, disclaimer, about;
	
	public EncoderHashMenuBar(final EncoderHashFrame mEncoderHashFrame) {
		
		file = new JMenu("File");
		help = new JMenu("Help");
		
		exit = new JMenuItem("Exit", ImageCreator.IMG_EXIT);

		exit.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		disclaimer = new JMenuItem("Disclaimer", ImageCreator.IMG_DISCLAIMER);
		
		about = new JMenuItem("About", ImageCreator.IMG_ABOUT);

		about.setAccelerator(KeyStroke.getKeyStroke('0', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		
		// File -> Exit
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						mEncoderHashFrame.closeFrame();
					}
				});
			}
		});
		
		// Help -> Disclaimer
		disclaimer.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(mEncoderHashFrame,
								AboutBox.Tab.DISCLAIMER);
					}
				});
			}
		});

		// Help -> About
		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(mEncoderHashFrame,
								AboutBox.Tab.ABOUT);
					}
				});
			}
		});
		
		
		file.add(exit);
		help.add(disclaimer);
		help.add(about);
		
		this.add(file);
		this.add(help);
	}

}
