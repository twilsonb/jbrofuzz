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

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.*;

import org.owasp.jbrofuzz.version.JBRFormat;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.util.ImageCreator;

import com.Ostermiller.util.Browser;
/**
 * <p>The main menu bar attached to the main frame window.</p>
 *
 * @author subere (at) uncon org
 * @version 0.6
 */
public class JBRMenuBar extends JMenuBar {

	private final JBRFrame mFrameWindow;
	// The menu items
	private final JMenu file, edit, view, panel, options, help;
	// Used under the Panel JMenu as items
	private JMenuItem showAll, hideAll, start, bro, stop, add, remove;
	// Used under the view JMenu as items
	private JCheckBoxMenuItem directories, fuzzing, sniffing, generators, opensource, system;

	/**
	 *
	 * @param mFrameWindow FrameWindow
	 */
	public JBRMenuBar(JBRFrame mFrameWindow) {

		this.mFrameWindow = mFrameWindow;

		file = new JMenu("File");
		edit = new JMenu("Edit");
		view = new JMenu("View");
		panel = new JMenu("Panel");
		options = new JMenu("Options");
		help = new JMenu("Help");

		add(file);
		add(edit);
		add(view);
		add(panel);
		add(options);
		add(help);

		// File
		JMenuItem exit = new JMenuItem("Exit", ImageCreator.EXIT_IMG);
		exit.setAccelerator(KeyStroke.getKeyStroke('1',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		file.add(exit);
		// Edit
		Action cutAction = new CutAction();
		Action copyAction = new CopyAction();
		Action pasteAction = new PasteAction();
		Action selectAllAction = new SelectAllAction();

		JMenuItem cut = new JMenuItem(cutAction);
		JMenuItem copy = new JMenuItem(copyAction);
		JMenuItem paste = new JMenuItem(pasteAction);
		JMenuItem selectAll = new JMenuItem(selectAllAction);

		cut.setAccelerator(KeyStroke.getKeyStroke('X',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		copy.setAccelerator(KeyStroke.getKeyStroke('C',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		paste.setAccelerator(KeyStroke.getKeyStroke('V',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		selectAll.setAccelerator(KeyStroke.getKeyStroke('A',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(selectAll);

		// View
		JMenu showHide = new JMenu("Show/Hide");

		directories = new JCheckBoxMenuItem("Web Directories", true);
		fuzzing = new JCheckBoxMenuItem("TCP Fuzzing", true);
		sniffing = new JCheckBoxMenuItem("TCP Sniffing", true);
		generators = new JCheckBoxMenuItem("Generators", false);
		opensource = new JCheckBoxMenuItem("Open Source", true);
		system = new JCheckBoxMenuItem("System", false);

		showAll = new JMenuItem("Show All");
		hideAll = new JMenuItem("Hide All");

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
		JMenu lookAndFeel = new JMenu("Look and Feel");
		UIManager.LookAndFeelInfo [] installedFeels = 
			UIManager.getInstalledLookAndFeels();
		ButtonGroup group = new ButtonGroup();

		for(int i = 0; i < Math.min(installedFeels.length, 5); i++) {
			JRadioButtonMenuItem rb =
				new JRadioButtonMenuItem( installedFeels[i].getName() );
			group.add(rb);
			lookAndFeel.add(rb);
			rb.setSelected(
					UIManager.getLookAndFeel().getName().equalsIgnoreCase(
							installedFeels[i].getName() ));

			rb.putClientProperty("Look and Feel Name", installedFeels[i]);

			rb.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent ie ) {
					JRadioButtonMenuItem rbi = 
						(JRadioButtonMenuItem) ie.getSource();

					if( rbi.isSelected() ) {
						final UIManager.LookAndFeelInfo info = 
							(UIManager.LookAndFeelInfo)
							rbi.getClientProperty("Look and Feel Name");

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {

								try {
									UIManager.setLookAndFeel(info.getClassName());
									SwingUtilities.
									updateComponentTreeUI( getFrameWindow() );
								}
								catch(UnsupportedLookAndFeelException e) {
									getFrameWindow().log("An error occured while setting the Look & Feel");
								}
								catch(IllegalAccessException e) {
									getFrameWindow().log("An error occured while setting the Look & Feel");
								}
								catch(ClassNotFoundException e) {
									getFrameWindow().log("An error occured while setting the Look & Feel");
								}
								catch(InstantiationException e) {
									getFrameWindow().log("An error occured while setting the Look & Feel");
								}

							}
						});						


					}
				}
			} );
		}
		view.add(lookAndFeel);

		// Panel
		start = new JMenuItem("Start", ImageCreator.START_IMG);
		bro = new JMenuItem("Bro", ImageCreator.PAUSE_IMG);
		stop = new JMenuItem("Stop", ImageCreator.STOP_IMG);
		add = new JMenuItem("Add", ImageCreator.ADD_IMG);
		remove = new JMenuItem("Remove", ImageCreator.REMOVE_IMG);

		add.setAccelerator(KeyStroke.getKeyStroke('=',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		remove.setAccelerator(KeyStroke.getKeyStroke('-',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		bro.setAccelerator(KeyStroke.getKeyStroke('B',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		start.setAccelerator(KeyStroke.getKeyStroke('\n',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		stop.setAccelerator(KeyStroke.getKeyStroke('\b',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		panel.add(start);
		panel.add(bro);
		panel.add(stop);
		panel.addSeparator();
		panel.add(add);
		panel.add(remove);

		// Options
		JMenuItem preferences = new JMenuItem("Preferences");
		JMenuItem updates = new JMenuItem("Check for Updates...");
		JMenuItem repair = new JMenuItem("Detect and Repair...");
		
		preferences.setAccelerator(KeyStroke.getKeyStroke('P',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		
		options.add(updates);
		options.add(repair);
		options.addSeparator();
		options.add(preferences);

		// Help
		JMenuItem topics = new JMenuItem("Topics", ImageCreator.TOPICS_IMG);
		JMenuItem faq = new JMenuItem("FAQ", ImageCreator.TOPICS_IMG);
		JMenuItem tutorial = new JMenuItem("Tutorial");
		JMenuItem website = new JMenuItem("JBroFuzz Website...");
		JMenuItem disclaimer = new JMenuItem("Disclaimer", ImageCreator.DISCLAIMER_IMG);
		JMenuItem about = new JMenuItem("About", ImageCreator.HELP_IMG);

		about.setAccelerator(KeyStroke.getKeyStroke('0',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		
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
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getFrameWindow().dispose();
					}
				});
			}
		});

		directories.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!directories.getState()) {
							getFrameWindow().setTabHide(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						}
						else {
							getFrameWindow().setTabShow(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						}
					}
				});			   
			}
		});

		fuzzing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!fuzzing.getState()) {
							getFrameWindow().setTabHide(JBRFrame.TCP_FUZZING_PANEL_ID);
						}
						else {
							getFrameWindow().setTabShow(JBRFrame.TCP_FUZZING_PANEL_ID);
						}
					}
				});			   
			}
		});

		sniffing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!sniffing.getState()) {
							getFrameWindow().setTabHide(JBRFrame.TCP_SNIFFING_PANEL_ID);
						}
						else {
							getFrameWindow().setTabShow(JBRFrame.TCP_SNIFFING_PANEL_ID);
						}
					}
				});

			}
		});

		generators.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!generators.getState()) {
							getFrameWindow().setTabHide(JBRFrame.GENERATORS_PANEL_ID);
						}
						else {
							getFrameWindow().setTabShow(JBRFrame.GENERATORS_PANEL_ID);
						}
					}
				});

			}
		});

		system.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!system.getState()) {
							getFrameWindow().setTabHide(JBRFrame.SYSTEM_PANEL_ID);
						}
						else {
							getFrameWindow().setTabShow(JBRFrame.SYSTEM_PANEL_ID);
						}
					}
				});

			}
		});

		opensource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!opensource.getState()) {
							getFrameWindow().setTabHide(JBRFrame.OPEN_SOURCE_ID);
						}
						else {
							getFrameWindow().setTabShow(JBRFrame.OPEN_SOURCE_ID);
						}
					}
				});

			}
		});

		showAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getFrameWindow().setTabShow(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						directories.setState(true);
						getFrameWindow().setTabShow(JBRFrame.TCP_FUZZING_PANEL_ID);
						fuzzing.setState(true);
						getFrameWindow().setTabShow(JBRFrame.TCP_SNIFFING_PANEL_ID);
						sniffing.setState(true);
						getFrameWindow().setTabShow(JBRFrame.GENERATORS_PANEL_ID);
						generators.setState(true);
						getFrameWindow().setTabShow(JBRFrame.SYSTEM_PANEL_ID);
						system.setState(true);
						getFrameWindow().setTabShow(JBRFrame.OPEN_SOURCE_ID);
						opensource.setState(true);
					}
				});

			}
		});    

		hideAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getFrameWindow().setTabHide(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						directories.setState(false);
						getFrameWindow().setTabHide(JBRFrame.TCP_FUZZING_PANEL_ID);
						fuzzing.setState(false);
						getFrameWindow().setTabHide(JBRFrame.TCP_SNIFFING_PANEL_ID);
						sniffing.setState(false);
						getFrameWindow().setTabHide(JBRFrame.GENERATORS_PANEL_ID);
						generators.setState(false);
						getFrameWindow().setTabHide(JBRFrame.SYSTEM_PANEL_ID);
						system.setState(false);
						getFrameWindow().setTabHide(JBRFrame.OPEN_SOURCE_ID);
						opensource.setState(false);
					}
				});

			}
		});    

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final SwingWorker3 worker = new SwingWorker3() {
							public Object construct() {
								int currentTab = getFrameWindow().getTabbedPane().getSelectedIndex();
								String s = getFrameWindow().getTabbedPane().getTitleAt(currentTab);
								if (s.equals(" TCP Fuzzing ")) {
									getFrameWindow().getFuzzingPanel().fuzzStartButton();
								}
								if (s.equals(" TCP Sniffing ")) {
									getFrameWindow().getTCPSniffingPanel().buttonStart();
								}
								if (s.equals(" Web Directories ")) {
									getFrameWindow().getWebDirectoriesPanel().buttonStart();
								}
								if (s.equals(" Open Source ")) {
									getFrameWindow().getOpenSourcePanel().checkStartButton();
								}
								return "start-menu-bar-return";
							}

							public void finished() {
								int currentTab = getFrameWindow().getTabbedPane().getSelectedIndex();
								String s = getFrameWindow().getTabbedPane().getTitleAt(currentTab);
								if (s.equals(" TCP Fuzzing ")) {
									getFrameWindow().getFuzzingPanel().fuzzStopButton();
								}
								if (s.equals(" Web Directories ")) {
									getFrameWindow().getWebDirectoriesPanel().buttonStop();
								}
								if (s.equals(" Open Source ")) {
									getFrameWindow().getOpenSourcePanel().checkStopButton();
								}
							}
						}; worker.start();
					}
				});

			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int currentTab = getFrameWindow().getTabbedPane().getSelectedIndex();
						String s = getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							getFrameWindow().getFuzzingPanel().fuzzStopButton();
						}
						if (s.equals(" TCP Sniffing ")) {
							getFrameWindow().getTCPSniffingPanel().buttonStop();
						}
						if (s.equals(" Web Directories ")) {
							getFrameWindow().getWebDirectoriesPanel().buttonStop();
						}
						if (s.equals(" Open Source ")) {
							getFrameWindow().getOpenSourcePanel().checkStopButton();
						}
					}
				});

			}
		});

		bro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int currentTab = getFrameWindow().getTabbedPane().getSelectedIndex();
						String s = getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							getFrameWindow().getFuzzingPanel().fuzzBroButton();
						}
						if (s.equals(" TCP Sniffing ")) {
							getFrameWindow().getTCPSniffingPanel().buttonBro();
						}
					}
				}); 
			}
		});



		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int currentTab = getFrameWindow().getTabbedPane().getSelectedIndex();
						String s = getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							getFrameWindow().getFuzzingPanel().generatorAddButton();
						}
					}
				});

			}
		});

		updates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new CheckForUpdates(getFrameWindow());
					}
				});

			}
		});
		
		repair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new DetectAndRepair(getFrameWindow());
					}
				});

			}
		});
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int currentTab = getFrameWindow().getTabbedPane().getSelectedIndex();
						String s = getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							getFrameWindow().getFuzzingPanel().generatorRemoveButton();
						}
					}
				});

			}
		});
		
		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new JBRPreferences(getFrameWindow());
					}
				});

			}
		});
		
		website.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
					   Browser.init();
					    try {
					      Browser.displayURL(JBRFormat.URL_WEBSITE);
					    }
					    catch (IOException ex) {
					      getFrameWindow().log("Could not launch link in external browser");
					    }
					    }
				});
			}
		});
		


		disclaimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(getFrameWindow(), AboutBox.DISCLAIMER);
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(getFrameWindow(), AboutBox.ABOUT);
					}
				});
			}
		});

	}

	private JBRFrame getFrameWindow() {
		return mFrameWindow;
	}
}

class CutAction extends TextAction {
	public CutAction() {
		super("Cut");
	}

	public void actionPerformed(ActionEvent evt) {
		JTextComponent text = getTextComponent(evt);
		if (text != null) {
			text.cut();
			text.requestFocus();
		}
	}
}

class CopyAction extends TextAction {
	public CopyAction() {
		super("Copy");
	}

	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JTextComponent text = getTextComponent(evt);
				if (text != null) {
					text.copy();
					text.requestFocus();
				}
			}
		});

	}
}

class PasteAction extends TextAction {
	public PasteAction() {
		super("Paste");
	}

	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JTextComponent text = getTextComponent(evt);
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
	public SelectAllAction() {
		super("Select All");
	}

	public void actionPerformed(final ActionEvent evt) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JTextComponent text = getTextComponent(evt);
				if (text != null) {
					text.selectAll();
					text.requestFocus();
				}
			}
		});

	}
}
