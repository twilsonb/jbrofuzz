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
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

class JMenuHelp extends JMenu {

	private static final long serialVersionUID = 364420126164053633L;

	protected JMenuHelp(final JBroFuzzMenuBar mainMenuBar) {
		
		super("Help");
		
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
						new Faq(mainMenuBar.getFrame());
					}
				});
			}
		});

		topics.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Topics(mainMenuBar.getFrame());
					}
				});
			}
		});
		
		shortcuts.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Shortcuts(mainMenuBar.getFrame());
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

							mainMenuBar
							.getFrame()
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
						new AboutBox(mainMenuBar.getFrame(),
								AboutBox.DISCLAIMER);
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(mainMenuBar.getFrame(),
								AboutBox.ABOUT);
					}
				});
			}
		});

	}

}

