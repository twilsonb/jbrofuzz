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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.owasp.jbrofuzz.help.Topics;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * The main tool bar displayed at the top of the JBroFuzzWindow.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.8
 * @since 1.2
 */
public class JBroFuzzToolBar extends JToolBar {

	private static final long serialVersionUID = 8897142041231978024L;

	private final JBroFuzzWindow mFrameWindow;

	private final JButton start, pause, stop, add, remove, help, about,
	website;

	/**
	 * <p>
	 * The constructor of the tool bar, taking as parameter the main frame
	 * window.
	 * </p>
	 * 
	 * @param mFrameWindow
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public JBroFuzzToolBar(final JBroFuzzWindow mFrameWindow) {

		this.mFrameWindow = mFrameWindow;

		start = new JButton(ImageCreator.IMG_START);
		start.setToolTipText("Start");
		pause = new JButton(ImageCreator.IMG_PAUSE);
		pause.setToolTipText("Pause");

		pause.setEnabled(false);

		stop = new JButton(ImageCreator.IMG_STOP);
		stop.setToolTipText("Stop");
		add = new JButton(ImageCreator.IMG_ADD);
		add.setToolTipText("Add");
		remove = new JButton(ImageCreator.IMG_REMOVE);
		remove.setToolTipText("Remove");
		help = new JButton(ImageCreator.IMG_TOPICS);
		help.setToolTipText("Help Topics");
		about = new JButton(ImageCreator.IMG_ABOUT);
		about.setToolTipText("About");
		website = new JButton(ImageCreator.IMG_OWASP_SML);
		website.setToolTipText("JBroFuzz Website");

		this.addSeparator(new Dimension(13, 0));
		add(start);
		add(pause);
		add(stop);
		// this.addSeparator(new Dimension(6, 0));
		this.addSeparator(new Dimension(13, 0));
		add(add);
		add(remove);
		this.addSeparator(new Dimension(13, 0));
		add(help);
		add(website);
		add(about);

		setFloatable(true);
		setRollover(true);

		start.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final class Starter extends SwingWorker<String, Object> {

					AbstractPanel p;

					@Override
					public String doInBackground() {

						final int c = getFrame().getTp().getSelectedIndex();
						p = (AbstractPanel) getFrame().getTp().getComponent(c);
						p.start();

						return "start-menu-bar-done";

					}

					@Override
					protected void done() {

						p.stop();

					}
				}

				(new Starter()).execute();

			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final int c = getFrame().getTp().getSelectedIndex();
				final AbstractPanel p = (AbstractPanel) getFrame().getTp()
				.getComponent(c);
				p.stop();

			}
		});

		pause.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						Logger.log("Pause functionality has not yet being implemented", 2);

					}
				});
			}
		});

		add.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						final int c = getFrame().getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) getFrame().getTp()
						.getComponent(c);
						p.add();

					}
				});

			}
		});

		remove.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						final int c = getFrame().getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) getFrame().getTp()
						.getComponent(c);
						p.remove();

					}
				});

			}
		});

		help.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Topics(JBroFuzzToolBar.this.getFrame());
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(JBroFuzzToolBar.this.getFrame(),
								AboutBox.Tab.ABOUT);
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

	}

	/**
	 * <p>
	 * Returns the JBroFuzzWindow used to construct this tool bar.
	 * </p>
	 * 
	 * @return The JBroFuzzWindow used to construct this tool bar.
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public JBroFuzzWindow getFrame() {

		return mFrameWindow;

	}

	/**
	 * <p>
	 * Sets the options to be available from the first five buttons on the tool
	 * bar.
	 * </p>
	 * 
	 * @param b
	 *            A boolean array of five elements
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void setEnabledPanelOptions(boolean[] b) {

		if (b.length == 5) {
			start.setEnabled(b[0]);
			stop.setEnabled(b[1]);
			pause.setEnabled(b[2]);
			add.setEnabled(b[3]);
			remove.setEnabled(b[4]);
		}
	}
}