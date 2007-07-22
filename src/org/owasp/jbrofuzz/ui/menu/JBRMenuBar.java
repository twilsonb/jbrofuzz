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
/**
 * <p>The main menu bar attached to the main frame window.</p>
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
	private JCheckBoxMenuItem directories, fuzzing, sniffing, generators, opensource, system;

	/**
	 *
	 * @param mFrameWindow FrameWindow
	 */
	public JBRMenuBar(final JBRFrame mFrameWindow) {

		this.mFrameWindow = mFrameWindow;

		this.file = new JMenu("File");
		this.edit = new JMenu("Edit");
		this.view = new JMenu("View");
		this.panel = new JMenu("Panel");
		this.options = new JMenu("Options");
		this.help = new JMenu("Help");

		this.add(this.file);
		this.add(this.edit);
		this.add(this.view);
		this.add(this.panel);
		this.add(this.options);
		this.add(this.help);

		// File
		final JMenuItem exit = new JMenuItem("Exit", ImageCreator.EXIT_IMG);
		exit.setAccelerator(KeyStroke.getKeyStroke('1',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		this.file.add(exit);
		// Edit
		final Action cutAction = new CutAction();
		final Action copyAction = new CopyAction();
		final Action pasteAction = new PasteAction();
		final Action selectAllAction = new SelectAllAction();

		final JMenuItem cut = new JMenuItem(cutAction);
		final JMenuItem copy = new JMenuItem(copyAction);
		final JMenuItem paste = new JMenuItem(pasteAction);
		final JMenuItem selectAll = new JMenuItem(selectAllAction);

		cut.setAccelerator(KeyStroke.getKeyStroke('X',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		copy.setAccelerator(KeyStroke.getKeyStroke('C',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		paste.setAccelerator(KeyStroke.getKeyStroke('V',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		selectAll.setAccelerator(KeyStroke.getKeyStroke('A',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		this.edit.add(cut);
		this.edit.add(copy);
		this.edit.add(paste);
		this.edit.addSeparator();
		this.edit.add(selectAll);

		// View
		final JMenu showHide = new JMenu("Show/Hide");

		this.directories = new JCheckBoxMenuItem("Web Directories", true);
		this.fuzzing = new JCheckBoxMenuItem("TCP Fuzzing", true);
		this.sniffing = new JCheckBoxMenuItem("TCP Sniffing", true);
		this.generators = new JCheckBoxMenuItem("Generators", false);
		this.opensource = new JCheckBoxMenuItem("Open Source", true);
		this.system = new JCheckBoxMenuItem("System", false);

		this.showAll = new JMenuItem("Show All");
		this.hideAll = new JMenuItem("Hide All");

		showHide.add(this.directories);
		showHide.add(this.fuzzing);
		showHide.add(this.sniffing);
		showHide.add(this.generators);
		showHide.add(this.opensource);
		showHide.add(this.system);

		this.view.add(showHide);
		this.view.addSeparator();
		this.view.add(this.showAll);
		this.view.add(this.hideAll);
		this.view.addSeparator();

		// -> Look and Feel
		final JMenu lookAndFeel = new JMenu("Look and Feel");
		final UIManager.LookAndFeelInfo [] installedFeels = 
			UIManager.getInstalledLookAndFeels();
		final ButtonGroup group = new ButtonGroup();

		for(int i = 0; i < Math.min(installedFeels.length, 5); i++) {
			final JRadioButtonMenuItem rb =
				new JRadioButtonMenuItem( installedFeels[i].getName() );
			group.add(rb);
			lookAndFeel.add(rb);
			rb.setSelected(
					UIManager.getLookAndFeel().getName().equalsIgnoreCase(
							installedFeels[i].getName() ));

			rb.putClientProperty("Look and Feel Name", installedFeels[i]);

			rb.addItemListener( new ItemListener() {
				public void itemStateChanged( final ItemEvent ie ) {
					final JRadioButtonMenuItem rbi = 
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
									updateComponentTreeUI( JBRMenuBar.this.getFrameWindow() );
								}
								catch(final UnsupportedLookAndFeelException e) {
									JBRMenuBar.this.getFrameWindow().log("An error occured while setting the Look & Feel");
								}
								catch(final IllegalAccessException e) {
									JBRMenuBar.this.getFrameWindow().log("An error occured while setting the Look & Feel");
								}
								catch(final ClassNotFoundException e) {
									JBRMenuBar.this.getFrameWindow().log("An error occured while setting the Look & Feel");
								}
								catch(final InstantiationException e) {
									JBRMenuBar.this.getFrameWindow().log("An error occured while setting the Look & Feel");
								}

							}
						});						


					}
				}
			} );
		}
		this.view.add(lookAndFeel);

		// Panel
		this.start = new JMenuItem("Start", ImageCreator.START_IMG);
		this.bro = new JMenuItem("Bro", ImageCreator.PAUSE_IMG);
		this.stop = new JMenuItem("Stop", ImageCreator.STOP_IMG);
		this.add = new JMenuItem("Add", ImageCreator.ADD_IMG);
		this.remove = new JMenuItem("Remove", ImageCreator.REMOVE_IMG);

		this.add.setAccelerator(KeyStroke.getKeyStroke('=',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		this.remove.setAccelerator(KeyStroke.getKeyStroke('-',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		this.bro.setAccelerator(KeyStroke.getKeyStroke('B',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		this.start.setAccelerator(KeyStroke.getKeyStroke('\n',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		this.stop.setAccelerator(KeyStroke.getKeyStroke('\b',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		this.panel.add(this.start);
		this.panel.add(this.bro);
		this.panel.add(this.stop);
		this.panel.addSeparator();
		this.panel.add(this.add);
		this.panel.add(this.remove);

		// Options
		final JMenuItem preferences = new JMenuItem("Preferences");
		final JMenuItem updates = new JMenuItem("Check for Updates...");
		final JMenuItem repair = new JMenuItem("Detect and Repair...");
		
		preferences.setAccelerator(KeyStroke.getKeyStroke('P',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		
		this.options.add(updates);
		this.options.add(repair);
		this.options.addSeparator();
		this.options.add(preferences);

		// Help
		final JMenuItem topics = new JMenuItem("Topics", ImageCreator.TOPICS_IMG);
		final JMenuItem faq = new JMenuItem("FAQ", ImageCreator.TOPICS_IMG);
		final JMenuItem tutorial = new JMenuItem("Tutorial");
		final JMenuItem website = new JMenuItem("JBroFuzz Website...");
		final JMenuItem disclaimer = new JMenuItem("Disclaimer", ImageCreator.DISCLAIMER_IMG);
		final JMenuItem about = new JMenuItem("About", ImageCreator.HELP_IMG);

		about.setAccelerator(KeyStroke.getKeyStroke('0',
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		
		this.help.add(topics);
		this.help.add(faq);
		this.help.addSeparator();
		// help.add(tutorial);
		this.help.add(website);
		this.help.addSeparator();
		this.help.add(disclaimer);
		this.help.add(about);

		
		//
		// The action listeners for each component...
		//
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRMenuBar.this.getFrameWindow().dispose();
					}
				});
			}
		});

		this.directories.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!JBRMenuBar.this.directories.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						}
						else {
							JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						}
					}
				});			   
			}
		});

		this.fuzzing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!JBRMenuBar.this.fuzzing.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.TCP_FUZZING_PANEL_ID);
						}
						else {
							JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.TCP_FUZZING_PANEL_ID);
						}
					}
				});			   
			}
		});

		this.sniffing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!JBRMenuBar.this.sniffing.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.TCP_SNIFFING_PANEL_ID);
						}
						else {
							JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.TCP_SNIFFING_PANEL_ID);
						}
					}
				});

			}
		});

		this.generators.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!JBRMenuBar.this.generators.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.GENERATORS_PANEL_ID);
						}
						else {
							JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.GENERATORS_PANEL_ID);
						}
					}
				});

			}
		});

		this.system.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!JBRMenuBar.this.system.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.SYSTEM_PANEL_ID);
						}
						else {
							JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.SYSTEM_PANEL_ID);
						}
					}
				});

			}
		});

		this.opensource.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!JBRMenuBar.this.opensource.getState()) {
							JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.OPEN_SOURCE_ID);
						}
						else {
							JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.OPEN_SOURCE_ID);
						}
					}
				});

			}
		});

		this.showAll.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						JBRMenuBar.this.directories.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.TCP_FUZZING_PANEL_ID);
						JBRMenuBar.this.fuzzing.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.TCP_SNIFFING_PANEL_ID);
						JBRMenuBar.this.sniffing.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.GENERATORS_PANEL_ID);
						JBRMenuBar.this.generators.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.SYSTEM_PANEL_ID);
						JBRMenuBar.this.system.setState(true);
						JBRMenuBar.this.getFrameWindow().setTabShow(JBRFrame.OPEN_SOURCE_ID);
						JBRMenuBar.this.opensource.setState(true);
					}
				});

			}
		});    

		this.hideAll.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.WEB_DIRECTORIES_PANEL_ID);
						JBRMenuBar.this.directories.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.TCP_FUZZING_PANEL_ID);
						JBRMenuBar.this.fuzzing.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.TCP_SNIFFING_PANEL_ID);
						JBRMenuBar.this.sniffing.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.GENERATORS_PANEL_ID);
						JBRMenuBar.this.generators.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.SYSTEM_PANEL_ID);
						JBRMenuBar.this.system.setState(false);
						JBRMenuBar.this.getFrameWindow().setTabHide(JBRFrame.OPEN_SOURCE_ID);
						JBRMenuBar.this.opensource.setState(false);
					}
				});

			}
		});    

		this.start.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final SwingWorker3 worker = new SwingWorker3() {
							@Override
							public Object construct() {
								int currentTab = JBRMenuBar.this.getFrameWindow().getTabbedPane().getSelectedIndex();
								String s = JBRMenuBar.this.getFrameWindow().getTabbedPane().getTitleAt(currentTab);
								if (s.equals(" TCP Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getFuzzingPanel().fuzzStartButton();
								}
								if (s.equals(" TCP Sniffing ")) {
									JBRMenuBar.this.getFrameWindow().getTCPSniffingPanel().buttonStart();
								}
								if (s.equals(" Web Directories ")) {
									JBRMenuBar.this.getFrameWindow().getWebDirectoriesPanel().buttonStart();
								}
								if (s.equals(" Open Source ")) {
									JBRMenuBar.this.getFrameWindow().getOpenSourcePanel().checkStartButton();
								}
								return "start-menu-bar-return";
							}

							@Override
							public void finished() {
								int currentTab = JBRMenuBar.this.getFrameWindow().getTabbedPane().getSelectedIndex();
								String s = JBRMenuBar.this.getFrameWindow().getTabbedPane().getTitleAt(currentTab);
								if (s.equals(" TCP Fuzzing ")) {
									JBRMenuBar.this.getFrameWindow().getFuzzingPanel().fuzzStopButton();
								}
								if (s.equals(" Web Directories ")) {
									JBRMenuBar.this.getFrameWindow().getWebDirectoriesPanel().buttonStop();
								}
								if (s.equals(" Open Source ")) {
									JBRMenuBar.this.getFrameWindow().getOpenSourcePanel().checkStopButton();
								}
							}
						}; worker.start();
					}
				});

			}
		});

		this.stop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow().getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel().fuzzStopButton();
						}
						if (s.equals(" TCP Sniffing ")) {
							JBRMenuBar.this.getFrameWindow().getTCPSniffingPanel().buttonStop();
						}
						if (s.equals(" Web Directories ")) {
							JBRMenuBar.this.getFrameWindow().getWebDirectoriesPanel().buttonStop();
						}
						if (s.equals(" Open Source ")) {
							JBRMenuBar.this.getFrameWindow().getOpenSourcePanel().checkStopButton();
						}
					}
				});

			}
		});

		this.bro.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow().getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel().fuzzBroButton();
						}
						if (s.equals(" TCP Sniffing ")) {
							JBRMenuBar.this.getFrameWindow().getTCPSniffingPanel().buttonBro();
						}
					}
				}); 
			}
		});



		this.add.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow().getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel().generatorAddButton();
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
		
		this.remove.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRMenuBar.this.getFrameWindow().getTabbedPane().getSelectedIndex();
						final String s = JBRMenuBar.this.getFrameWindow().getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" TCP Fuzzing ")) {
							JBRMenuBar.this.getFrameWindow().getFuzzingPanel().generatorRemoveButton();
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
					    }
					    catch (final IOException ex) {
					      JBRMenuBar.this.getFrameWindow().log("Could not launch link in external browser");
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
		return this.mFrameWindow;
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
		final JTextComponent text = this.getTextComponent(evt);
		if (text != null) {
			text.cut();
			text.requestFocus();
		}
	}
}

class CopyAction extends TextAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3537862376041160965L;

	public CopyAction() {
		super("Copy");
	}

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

class PasteAction extends TextAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2787026188563573773L;

	public PasteAction() {
		super("Paste");
	}

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
	/**
	 * 
	 */
	private static final long serialVersionUID = -3252406791580769338L;

	public SelectAllAction() {
		super("Select All");
	}

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
