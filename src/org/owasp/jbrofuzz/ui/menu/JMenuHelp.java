/**
 * JbroFuzz 2.5
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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.owasp.jbrofuzz.help.Faq;
import org.owasp.jbrofuzz.help.Shortcuts;
import org.owasp.jbrofuzz.help.Topics;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

class JMenuHelp extends JMenu {

	private static final long serialVersionUID = 5161865891267439105L;

	protected JMenuHelp(final JBroFuzzMenuBar mainMenuBar) {
		
		super("Help");
		final JBroFuzzWindow mainFrame = mainMenuBar.getFrame();

		// Help
		final JMenuItem topics = new JMenuItem("Topics",
				ImageCreator.IMG_TOPICS);
		final JMenuItem faq = new JMenuItem("FAQ", ImageCreator.IMG_FAQ);
		
		final JMenuItem shortcuts = new JMenuItem("Keyboard Shortcuts");
		
		final JMenuItem website = new JMenuItem("JBroFuzz Website...",
				ImageCreator.IMG_OWASP_SML);
		
		final JMenuItem disclaimer = new JMenuItem("Disclaimer",
				ImageCreator.IMG_DISCLAIMER);
		final JMenuItem about = new JMenuItem("About", ImageCreator.IMG_ABOUT);

		about.setAccelerator(KeyStroke.getKeyStroke('0', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		this.add(topics);
		this.add(shortcuts);
		this.add(faq);
		this.addSeparator();
		this.addSeparator();
		this.add(website);
		this.addSeparator();
		this.add(disclaimer);
		this.add(about);
		

		faq.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Faq(mainFrame);
					}
				});
			}
		});

		topics.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Topics(mainFrame);
					}
				});
			}
		});
		
		shortcuts.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Shortcuts(mainFrame);
					}
				});
			}
		});

		website.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Browser.init();
						try {
							Browser.displayURL(JBroFuzzFormat.URL_WEBSITE);
						} catch (final IOException ex) {

							Logger
							.log(
									"Could not launch link in external browser",
									3);

						}
					}
				});
			}
		});

		disclaimer.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(mainFrame,
								AboutBox.Tab.DISCLAIMER);
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(mainFrame,
								AboutBox.Tab.ABOUT);
					}
				});
			}
		});

	}

}

