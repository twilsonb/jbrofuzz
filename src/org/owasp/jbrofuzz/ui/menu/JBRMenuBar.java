/**
 * JBroFuzz 1.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
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
 * 
 */
package org.owasp.jbrofuzz.ui.menu;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.SwingWorker3;
import org.owasp.jbrofuzz.version.Format;

import org.owasp.jbrofuzz.ui.actions.*;
import com.Ostermiller.util.Browser;



/**
 * <p>
 * The main menu bar attached to the main frame window.
 * </p>
 * 
 * @author subere (at) uncon org
 * @version 0.6
 */
public class JBRMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 519521993186395281L;
	private final JBroFuzzWindow mFrameWindow;
	// The menu items
	private final JMenu file, edit, view, panel, options, help;
	// Used under the Panel JMenu as items
	private JMenuItem showAll, hideAll, start, graph, stop, add, remove;
	// Used under the view JMenu as items
	private JCheckBoxMenuItem directories, fuzzing, sniffing, payloads, system;

	/**
	 * 
	 * @param mFrameWindow
	 *          FrameWindow
	 */
	public JBRMenuBar(final JBroFuzzWindow mFrameWindow) {

		this.mFrameWindow = mFrameWindow;

		file = new JMenu("File");
		edit = new JMenu("Edit");
		view = new JMenu("View");
		panel = new JMenu("Panel");
		options = new JMenu("Options");
		help = new JMenu("Help");

		this.add(file);
		this.add(edit);
		this.add(view);
		this.add(panel);
		this.add(options);
		this.add(help);

		// File
		final JMenuItem exit = new JMenuItem("Exit", ImageCreator.EXIT_IMG);
		exit.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		file.add(exit);
		// Edit
		
		final JMenuItem cut = new JMenuItem(new CutAction());
		final JMenuItem copy = new JMenuItem(new CopyAction());
		final JMenuItem paste = new JMenuItem(new PasteAction());
		final JMenuItem selectAll = new JMenuItem(new SelectAllAction());

		cut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		cut.setIcon(ImageCreator.IMG_CUT);
		copy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		copy.setIcon(ImageCreator.IMG_COPY);
		paste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		paste.setIcon(ImageCreator.IMG_PASTE);
		selectAll.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		selectAll.setIcon(ImageCreator.IMG_SELECTALL);

		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(selectAll);

		// View
		final JMenu showHide = new JMenu("Show/Hide");

		directories = new JCheckBoxMenuItem(" Web Directories ", true);
		fuzzing = new JCheckBoxMenuItem(" Fuzzing ", true);
		sniffing = new JCheckBoxMenuItem(" Sniffing ", true);
		payloads = new JCheckBoxMenuItem(" Payloads ", false);
		system = new JCheckBoxMenuItem(" System ", false);

		showAll = new JMenuItem("Show All");
		hideAll = new JMenuItem("Hide All");

		showHide.add(fuzzing);
		showHide.add(sniffing);
		showHide.add(payloads);
		showHide.add(directories);
		showHide.add(system);
		
		fuzzing.setState(true);
		sniffing.setState(true);
		payloads.setState(true);
		directories.setState(true);
		system.setState(false);
		
		view.add(showHide);
		view.addSeparator();
		view.add(showAll);
		view.add(hideAll);
		view.addSeparator();

		// -> Look and Feel
		final JMenu lookAndFeel = new JMenu("Look and Feel");
		lookAndFeel.setIcon(ImageCreator.IMG_LKF);
		final UIManager.LookAndFeelInfo[] installedFeels = UIManager
				.getInstalledLookAndFeels();
		final ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < Math.min(installedFeels.length, 7); i++) {
			final JRadioButtonMenuItem rb = new JRadioButtonMenuItem(
					installedFeels[i].getName());
			group.add(rb);
			lookAndFeel.add(rb);
			rb.setSelected(UIManager.getLookAndFeel().getName().equalsIgnoreCase(
					installedFeels[i].getName()));

			rb.putClientProperty("Look and Feel Name", installedFeels[i]);

			rb.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent ie) {
					final JRadioButtonMenuItem rbi = (JRadioButtonMenuItem) ie
							.getSource();

					if (rbi.isSelected()) {
						final UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) rbi
								.getClientProperty("Look and Feel Name");

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {

								try {
									UIManager.setLookAndFeel(info.getClassName());
									SwingUtilities.updateComponentTreeUI(JBRMenuBar.this
											.getFrameWindow());
								} catch (final UnsupportedLookAndFeelException e) {
									JBRMenuBar.this.getFrameWindow().log(
											"An error occured while setting the Look & Feel");
								} catch (final IllegalAccessException e) {
									JBRMenuBar.this.getFrameWindow().log(
											"An error occured while setting the Look & Feel");
								} catch (final ClassNotFoundException e) {
									JBRMenuBar.this.getFrameWindow().log(
											"An error occured while setting the Look & Feel");
								} catch (final InstantiationException e) {
									JBRMenuBar.this.getFrameWindow().log(
											"An error occured while setting the Look & Feel");
								}

							}
						});

					}
				}
			});
		}
		view.add(lookAndFeel);

		// Panel
		start = new JMenuItem("Fuzz", ImageCreator.START_IMG);
		graph = new JMenuItem("Graph", ImageCreator.PAUSE_IMG);
		stop = new JMenuItem("Stop", ImageCreator.STOP_IMG);
		add = new JMenuItem("Add", ImageCreator.ADD_IMG);
		remove = new JMenuItem("Remove", ImageCreator.REMOVE_IMG);

		add.setAccelerator(KeyStroke.getKeyStroke('=', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		remove.setAccelerator(KeyStroke.getKeyStroke('-', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		graph.setAccelerator(KeyStroke.getKeyStroke('G', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		start.setAccelerator(KeyStroke.getKeyStroke('\n', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		stop.setAccelerator(KeyStroke.getKeyStroke('\b', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		panel.add(start);
		panel.add(stop);
		panel.addSeparator();
		panel.add(graph);
		panel.addSeparator();
		panel.add(add);
		panel.add(remove);

		// Options
		final JMenuItem preferences = new JMenuItem("Preferences", ImageCreator.IMG_PREFERENCES);
		final JMenuItem updates = new JMenuItem("Check for Updates...", ImageCreator.IMG_UPDATE);

		preferences.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		options.add(updates);
		options.addSeparator();
		options.add(preferences);

		// Help
		final JMenuItem topics = new JMenuItem("Topics", ImageCreator.IMG_TOPICS);
		final JMenuItem faq = new JMenuItem("FAQ", ImageCreator.IMG_FAQ);
		// final JMenuItem tutorial = new JMenuItem("Tutorial");
		final JMenuItem website = new JMenuItem("JBroFuzz Website...", ImageCreator.OWASP_IMAGE_SML);
		final JMenuItem disclaimer = new JMenuItem("Disclaimer",
				ImageCreator.IMG_DISCLAIMER);
		final JMenuItem about = new JMenuItem("About", ImageCreator.IMG_ABOUT);

		about.setAccelerator(KeyStroke.getKeyStroke('0', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		help.add(topics);
		help.add(faq);
		help.addSeparator();
		// help.add(tutorial);
		help.add(website);
		help.addSeparator();
		help.add(disclaimer);
		help.add(about);

		//
		// The action listeners for each component...
		//
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRMenuBar.this.getFrameWindow().exitProcedure();
					}
				});
			}
		});



		directories.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!directories.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES);
						}
					}
				});
			}
		});

		fuzzing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!fuzzing.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBroFuzzWindow.ID_PANEL_FUZZING);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBroFuzzWindow.ID_PANEL_FUZZING);
						}
					}
				});
			}
		});

		sniffing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!sniffing.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBroFuzzWindow.ID_PANEL_SNIFFING);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBroFuzzWindow.ID_PANEL_SNIFFING);
						}
					}
				});

			}
		});

		payloads.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!payloads.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBroFuzzWindow.ID_PANEL_PAYLOADS);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBroFuzzWindow.ID_PANEL_PAYLOADS);
						}
					}
				});

			}
		});

		system.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!system.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBroFuzzWindow.ID_PANEL_SYSTEM);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBroFuzzWindow.ID_PANEL_SYSTEM);
						}
					}
				});

			}
		});


		showAll.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES);
						directories.setState(true);
						
						
						
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBroFuzzWindow.ID_PANEL_FUZZING);
						fuzzing.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBroFuzzWindow.ID_PANEL_SNIFFING);
						sniffing.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBroFuzzWindow.ID_PANEL_PAYLOADS);
						payloads.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBroFuzzWindow.ID_PANEL_SYSTEM);
						system.setState(true);
						
					}
				});

			}
		});

		hideAll.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES);
						directories.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBroFuzzWindow.ID_PANEL_FUZZING);
						fuzzing.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBroFuzzWindow.ID_PANEL_SNIFFING);
						sniffing.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBroFuzzWindow.ID_PANEL_PAYLOADS);
						payloads.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBroFuzzWindow.ID_PANEL_SYSTEM);
						system.setState(false);
						
						
					}
				});

			}
		});

		start.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final SwingWorker3 worker = new SwingWorker3() {
							@Override
							public Object construct() {
								int currentTab = JBRMenuBar.this.getFrameWindow()
										.getTabbedPane().getSelectedIndex();
								String s = JBRMenuBar.this.getFrameWindow().getTabbedPane()
										.getTitleAt(currentTab);
								if (s.equals(" Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getPanelFuzzing()
											.start();
								}
								if (s.equals(" Sniffing ")) {
									JBRMenuBar.this.getFrameWindow().getPanelSniffing()
											.start();
								}
								if (s.equals(" Web Directories ")) {
									JBRMenuBar.this.getFrameWindow().getPanelWebDirectories()
											.start();
								}
								return "start-menu-bar-return";
							}

							@Override
							public void finished() {
								int currentTab = JBRMenuBar.this.getFrameWindow()
										.getTabbedPane().getSelectedIndex();
								String s = JBRMenuBar.this.getFrameWindow().getTabbedPane()
										.getTitleAt(currentTab);
								if (s.equals(" Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getPanelFuzzing()
											.stop();
								}
								if (s.equals(" Web Directories ")) {
									JBRMenuBar.this.getFrameWindow().getPanelWebDirectories()
											.stop();
								}
							}
						};
						worker.start();
					}
				});

			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane()
								.getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getPanelFuzzing()
									.stop();
						}
						if (s.equals(" Sniffing ")) {
							JBRMenuBar.this.getFrameWindow().getPanelSniffing()
									.stop();
						}
						if (s.equals(" Web Directories ")) {
							JBRMenuBar.this.getFrameWindow().getPanelWebDirectories()
									.stop();
						}
					}
				});

			}
		});

		graph.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane()
								.getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getPanelFuzzing()
									.fuzzBroButton();
						}
						if (s.equals(" Sniffing ")) {
							JBRMenuBar.this.getFrameWindow().getPanelSniffing()
									.bro();
						}
					}
				});
			}
		});

		add.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane()
								.getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getPanelFuzzing()
									.add();
						}
						
					}
				});

			}
		});

		updates.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new CheckForUpdates(JBRMenuBar.this.getFrameWindow());
					}
				});

			}
		});

		remove.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						final int currentTab = JBRMenuBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane()
								.getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getPanelFuzzing()
									.remove();
						}
						
					}
				});

			}
		});

		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new JBRPreferences(JBRMenuBar.this.getFrameWindow());
					}
				});

			}
		});
		faq.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new JBRFaq(JBRMenuBar.this.getFrameWindow());
					}
				});
			}
		});

		topics.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new JBRHelp(JBRMenuBar.this.getFrameWindow());
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
							Browser.displayURL(Format.URL_WEBSITE);
						} catch (final IOException ex) {
							JBRMenuBar.this.getFrameWindow().log(
									"Could not launch link in external browser");
						}
					}
				});
			}
		});

		disclaimer.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(JBRMenuBar.this.getFrameWindow(), AboutBox.DISCLAIMER);
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(JBRMenuBar.this.getFrameWindow(), AboutBox.ABOUT);
					}
				});
			}
		});

	}

	private JBroFuzzWindow getFrameWindow() {
		return mFrameWindow;
	}
}

