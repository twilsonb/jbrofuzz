/**
 * FrameMenuBar.java 0.6
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
import javax.swing.SwingWorker3;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBRFormat;

import com.Ostermiller.util.Browser;

class CopyAction extends TextAction {
	
	private static final long serialVersionUID = 3537862376041160965L;
	
	/**
	 * <p>Main constructor for the copy action as part of a text action.</p>
	 *
	 */
	public CopyAction() {
		super("Copy");
	}

	/**
	 * <p>Method orchistrating the action performing while copying.</p>
	 * 
	 * @param evt ActionEvent
	 */
	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JTextComponent text = CopyAction.this.getTextComponent(evt);
				if (text != null) {
					text.copy();
					text.requestFocus();
				}
			}
		});

	}
}

class CutAction extends TextAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4536811278468856396L;

	public CutAction() {
		super("Cut");
	}

	public void actionPerformed(final ActionEvent evt) {
		final JTextComponent text = getTextComponent(evt);
		if (text != null) {
			text.cut();
			text.requestFocus();
		}
	}
}

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
	private final JBRFrame mFrameWindow;
	// The menu items
	private final JMenu file, edit, view, panel, options, help;
	// Used under the Panel JMenu as items
	private JMenuItem showAll, hideAll, start, bro, stop, add, remove;
	// Used under the view JMenu as items
	private JCheckBoxMenuItem directories, fuzzing, sniffing, generators, httpFuzzing,
			opensource, system;

	/**
	 * 
	 * @param mFrameWindow
	 *          FrameWindow
	 */
	public JBRMenuBar(final JBRFrame mFrameWindow) {

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
		exit.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask(), false));

		file.add(exit);
		// Edit
		final Action cutAction = new CutAction();
		final Action copyAction = new CopyAction();
		final Action pasteAction = new PasteAction();
		final Action selectAllAction = new SelectAllAction();

		final JMenuItem cut = new JMenuItem(cutAction);
		final JMenuItem copy = new JMenuItem(copyAction);
		final JMenuItem paste = new JMenuItem(pasteAction);
		final JMenuItem selectAll = new JMenuItem(selectAllAction);

		cut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask(), false));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask(), false));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		selectAll.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(selectAll);

		// View
		final JMenu showHide = new JMenu("Show/Hide");

		httpFuzzing = new JCheckBoxMenuItem("HTTP/S Fuzzing", true);
		directories = new JCheckBoxMenuItem("Web Directories", true);
		fuzzing = new JCheckBoxMenuItem("TCP Fuzzing", true);
		sniffing = new JCheckBoxMenuItem("TCP Sniffing", true);
		generators = new JCheckBoxMenuItem("Generators", false);
		opensource = new JCheckBoxMenuItem("Open Source", true);
		system = new JCheckBoxMenuItem("System", false);

		showAll = new JMenuItem("Show All");
		hideAll = new JMenuItem("Hide All");

		showHide.add(httpFuzzing);
		showHide.add(directories);
		showHide.add(fuzzing);
		showHide.add(sniffing);
		showHide.add(generators);
		showHide.add(opensource);
		showHide.add(system);

		view.add(showHide);
		view.addSeparator();
		view.add(showAll);
		view.add(hideAll);
		view.addSeparator();

		// -> Look and Feel
		final JMenu lookAndFeel = new JMenu("Look and Feel");
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
		start = new JMenuItem("Start", ImageCreator.START_IMG);
		bro = new JMenuItem("Bro", ImageCreator.PAUSE_IMG);
		stop = new JMenuItem("Stop", ImageCreator.STOP_IMG);
		add = new JMenuItem("Add", ImageCreator.ADD_IMG);
		remove = new JMenuItem("Remove", ImageCreator.REMOVE_IMG);

		add.setAccelerator(KeyStroke.getKeyStroke('=', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		remove.setAccelerator(KeyStroke.getKeyStroke('-', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		bro.setAccelerator(KeyStroke.getKeyStroke('B', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		start.setAccelerator(KeyStroke.getKeyStroke('\n', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		stop.setAccelerator(KeyStroke.getKeyStroke('\b', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		panel.add(start);
		panel.add(bro);
		panel.add(stop);
		panel.addSeparator();
		panel.add(add);
		panel.add(remove);

		// Options
		final JMenuItem preferences = new JMenuItem("Preferences");
		final JMenuItem updates = new JMenuItem("Check for Updates...");
		final JMenuItem repair = new JMenuItem("Detect and Repair...");

		preferences.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		options.add(updates);
		options.add(repair);
		options.addSeparator();
		options.add(preferences);

		// Help
		final JMenuItem topics = new JMenuItem("Topics", ImageCreator.TOPICS_IMG);
		final JMenuItem faq = new JMenuItem("FAQ", ImageCreator.TOPICS_IMG);
		// final JMenuItem tutorial = new JMenuItem("Tutorial");
		final JMenuItem website = new JMenuItem("JBroFuzz Website...");
		final JMenuItem disclaimer = new JMenuItem("Disclaimer",
				ImageCreator.DISCLAIMER_IMG);
		final JMenuItem about = new JMenuItem("About", ImageCreator.HELP_IMG);

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

		httpFuzzing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!httpFuzzing.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBRFrame.HTTP_FUZZING_PANEL_ID);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBRFrame.HTTP_FUZZING_PANEL_ID);
						}
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
									JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBRFrame.WEB_DIRECTORIES_PANEL_ID);
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
									JBRFrame.TCP_FUZZING_PANEL_ID);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBRFrame.TCP_FUZZING_PANEL_ID);
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
									JBRFrame.TCP_SNIFFING_PANEL_ID);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBRFrame.TCP_SNIFFING_PANEL_ID);
						}
					}
				});

			}
		});

		generators.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!generators.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBRFrame.GENERATORS_PANEL_ID);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBRFrame.GENERATORS_PANEL_ID);
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
									JBRFrame.SYSTEM_PANEL_ID);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBRFrame.SYSTEM_PANEL_ID);
						}
					}
				});

			}
		});

		opensource.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!opensource.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(
									JBRFrame.OPEN_SOURCE_ID);
						} else {
							JBRMenuBar.this.getFrameWindow().setTabShow(
									JBRFrame.OPEN_SOURCE_ID);
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
								JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						directories.setState(true);
						
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBRFrame.HTTP_FUZZING_PANEL_ID);
						httpFuzzing.setState(true);
						
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBRFrame.TCP_FUZZING_PANEL_ID);
						fuzzing.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBRFrame.TCP_SNIFFING_PANEL_ID);
						sniffing.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBRFrame.GENERATORS_PANEL_ID);
						generators.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(
								JBRFrame.SYSTEM_PANEL_ID);
						system.setState(true);
						JBRMenuBar.this.getFrameWindow()
								.setTabShow(JBRFrame.OPEN_SOURCE_ID);
						opensource.setState(true);
					}
				});

			}
		});

		hideAll.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						directories.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBRFrame.TCP_FUZZING_PANEL_ID);
						fuzzing.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBRFrame.TCP_SNIFFING_PANEL_ID);
						sniffing.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBRFrame.GENERATORS_PANEL_ID);
						generators.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBRFrame.SYSTEM_PANEL_ID);
						system.setState(false);
						JBRMenuBar.this.getFrameWindow()
								.setTabHide(JBRFrame.OPEN_SOURCE_ID);
						
						JBRMenuBar.this.getFrameWindow().setTabHide(
								JBRFrame.HTTP_FUZZING_PANEL_ID);
						httpFuzzing.setState(false);
						
						opensource.setState(false);
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
								if (s.equals(" TCP Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getFuzzingPanel()
											.start();
								}
								if (s.equals(" TCP Sniffing ")) {
									JBRMenuBar.this.getFrameWindow().getTCPSniffingPanel()
											.start();
								}
								if (s.equals(" Web Directories ")) {
									JBRMenuBar.this.getFrameWindow().getWebDirectoriesPanel()
											.start();
								}
								if (s.equals(" Open Source ")) {
									JBRMenuBar.this.getFrameWindow().getOpenSourcePanel()
											.start();
								}
								if (s.equals(" HTTP/S Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getHTTPFuzzingPanel()
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
								if (s.equals(" TCP Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getFuzzingPanel()
											.stop();
								}
								if (s.equals(" HTTP/S Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getHTTPFuzzingPanel()
											.stop();
								}
								if (s.equals(" Web Directories ")) {
									JBRMenuBar.this.getFrameWindow().getWebDirectoriesPanel()
											.stop();
								}
								if (s.equals(" Open Source ")) {
									JBRMenuBar.this.getFrameWindow().getOpenSourcePanel()
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
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel()
									.stop();
						}
						if (s.equals(" TCP Sniffing ")) {
							JBRMenuBar.this.getFrameWindow().getTCPSniffingPanel()
									.stop();
						}
						if (s.equals(" Web Directories ")) {
							JBRMenuBar.this.getFrameWindow().getWebDirectoriesPanel()
									.stop();
						}
						if (s.equals(" Open Source ")) {
							JBRMenuBar.this.getFrameWindow().getOpenSourcePanel()
									.stop();
						}
						if (s.equals(" HTTP/S Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getHTTPFuzzingPanel()
									.stop();
						}
					}
				});

			}
		});

		bro.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane()
								.getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel()
									.fuzzBroButton();
						}
						if (s.equals(" TCP Sniffing ")) {
							JBRMenuBar.this.getFrameWindow().getTCPSniffingPanel()
									.bro();
						}
						if (s.equals(" HTTP/S Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getHTTPFuzzingPanel()
									.fuzzBroButton();
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
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel()
									.add();
						}
						if (s.equals(" HTTP/S Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getHTTPFuzzingPanel().add();
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

		repair.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new DetectAndRepair(JBRMenuBar.this.getFrameWindow());
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
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel()
									.remove();
						}
						if (s.equals(" HTTP/S Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getHTTPFuzzingPanel()
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
							Browser.displayURL(JBRFormat.URL_WEBSITE);
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

	private JBRFrame getFrameWindow() {
		return mFrameWindow;
	}
}

class PasteAction extends TextAction {
	
	private static final long serialVersionUID = 2787026188563573773L;

	/**
	 * <p>Constuctor for pasting as part of a text action.</p>
	 *
	 */
	public PasteAction() {
		super("Paste");
	}

	/**
	 * <p>Method orchistrating the action performed during a paste action.</p>
	 * 
	 * @param evt ActionEvent
	 */
	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JTextComponent text = PasteAction.this.getTextComponent(evt);
				if (text != null) {
					if (text.isEditable()) {
						text.paste();
						text.requestFocus();
					}
				}
			}
		});

	}
}

class SelectAllAction extends TextAction {
	
	private static final long serialVersionUID = -3252406791511769338L;
	
	/**
	 * <p>Constructor for the select all text action.</p>
	 *
	 */
	public SelectAllAction() {
		super("Select All");
	}

	/**
	 * <p>Method orchistrating the action performed when selecting all.</p>
	 * 
	 * @param evt ActionEvent
	 */
	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JTextComponent text = SelectAllAction.this.getTextComponent(evt);
				if (text != null) {
					text.selectAll();
					text.requestFocus();
				}
			}
		});

	}
}
