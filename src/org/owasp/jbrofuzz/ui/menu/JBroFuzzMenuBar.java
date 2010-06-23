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
package org.owasp.jbrofuzz.ui.menu;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.owasp.jbrofuzz.encode.EncoderHashFrame;
import org.owasp.jbrofuzz.headers.HeaderFrame;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.actions.CopyAction;
import org.owasp.jbrofuzz.ui.actions.CutAction;
import org.owasp.jbrofuzz.ui.actions.PasteAction;
import org.owasp.jbrofuzz.ui.actions.SelectAllAction;
import org.owasp.jbrofuzz.ui.prefs.PrefDialog;
import org.owasp.jbrofuzz.update.CheckForUpdates;
import org.owasp.jbrofuzz.version.ImageCreator;

/**
 * <p>
 * The main menu bar attached to the main frame window.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.3
 * @since 0.1
 */
public class JBroFuzzMenuBar extends JMenuBar {

	private static final long serialVersionUID = -5135673814026322378L;

	private final JBroFuzzWindow mFrameWindow;
	// The menu items
	private final JMenu view, panel, options; // , help;
	// Used under the Panel JMenu as items
	private final JMenuItem start, pause, stop, add, remove;
	// Used under the view JMenu as items
	private final JCheckBoxMenuItem graphing, fuzzing, payloads, system;
	
	/**
	 * 
	 * @param mFrameWindow
	 *            FrameWindow
	 */
	public JBroFuzzMenuBar(final JBroFuzzWindow mFrameWindow) {
		super();
		this.mFrameWindow = mFrameWindow;

		// final JMenu file = new JMenu("File");
		final JMenu edit = new JMenu("Edit");
		view = new JMenu("View");
		panel = new JMenu("Panel");
		options = new JMenu("Options");

		this.add(new JMenuFile(this));
		this.add(edit);
		this.add(view);
		this.add(panel);
		this.add(options);
		this.add(new JMenuHelp(this));

		// Edit

		final JMenuItem cut = new JMenuItem(new CutAction());
		final JMenuItem copy = new JMenuItem(new CopyAction());
		final JMenuItem paste = new JMenuItem(new PasteAction());
		final JMenuItem selectAll = new JMenuItem(new SelectAllAction());

		cut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		cut.setIcon(ImageCreator.IMG_CUT);
		copy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		copy.setIcon(ImageCreator.IMG_COPY);
		paste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		paste.setIcon(ImageCreator.IMG_PASTE);
		selectAll.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		selectAll.setIcon(ImageCreator.IMG_SELECTALL);

		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(selectAll);

		// View
		final JMenu showHide = new JMenu("Show/Hide");
		showHide.setIcon(ImageCreator.IMG_SHOW_HIDE);

		graphing = new JCheckBoxMenuItem("Graphing", true);
		fuzzing = new JCheckBoxMenuItem("Fuzzing", true);
		payloads = new JCheckBoxMenuItem("Payloads", false);
		system = new JCheckBoxMenuItem("System", false);

		final JMenuItem showAll = new JMenuItem("Show All", ImageCreator.IMG_SHOW_ALL);
		final JMenuItem hideAll = new JMenuItem("Hide All");

		showHide.add(fuzzing);
		showHide.add(payloads);
		showHide.add(graphing);
		showHide.add(system);

		fuzzing.setState(true);
		payloads.setState(true);
		graphing.setState(true);
		system.setState(true);

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

		for (int i = 0; i < Math.min(installedFeels.length, 10); i++) {
			final JRadioButtonMenuItem rButton1 = new JRadioButtonMenuItem(
					installedFeels[i].getName());
			group.add(rButton1);
			lookAndFeel.add(rButton1);
			rButton1.setSelected(UIManager.getLookAndFeel().getName()
					.equalsIgnoreCase(installedFeels[i].getName()));

			rButton1.putClientProperty("Look and Feel Name", installedFeels[i]);

			rButton1.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent iEvent) {
					final JRadioButtonMenuItem rbi = (JRadioButtonMenuItem) iEvent
					.getSource();

					if (rbi.isSelected()) {
						final UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) rbi
						.getClientProperty("Look and Feel Name");

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {

								try {
									UIManager.setLookAndFeel(info
											.getClassName());
									SwingUtilities
									.updateComponentTreeUI(JBroFuzzMenuBar.this
											.getFrame());
								} catch (final UnsupportedLookAndFeelException e) {
									Logger
									.log(
											"An error occured while setting the Look & Feel",
											5);
								} catch (final IllegalAccessException e) {
									Logger
									.log(
											"An error occured while setting the Look & Feel",
											5);
								} catch (final ClassNotFoundException e) {
									Logger
									.log(
											"An error occured while setting the Look & Feel",
											5);
								} catch (final InstantiationException e) {
									Logger
									.log(
											"An error occured while setting the Look & Feel",
											5);
								}

							}
						});

					}
				}
			});
		}
		view.add(lookAndFeel);

		// Panel
		start = new JMenuItem("Start", ImageCreator.IMG_START);
		pause = new JMenuItem("Pause", ImageCreator.IMG_PAUSE);

		pause.setEnabled(false);

		stop = new JMenuItem("Stop", ImageCreator.IMG_STOP);
		add = new JMenuItem("Add", ImageCreator.IMG_ADD);
		remove = new JMenuItem("Remove", ImageCreator.IMG_REMOVE);

		add.setAccelerator(KeyStroke.getKeyStroke('=', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		remove.setAccelerator(KeyStroke.getKeyStroke('-', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		pause.setAccelerator(KeyStroke.getKeyStroke('J', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		start.setAccelerator(KeyStroke.getKeyStroke('\n', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		stop.setAccelerator(KeyStroke.getKeyStroke('\b', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		panel.add(start);
		panel.add(pause);
		panel.add(stop);
		panel.addSeparator();
		panel.add(add);
		panel.add(remove);

		// Options
		
		final JMenuItem headers = new JMenuItem("Browser Headers");
		final JMenuItem encoderHash = new JMenuItem("Encoder/Hash...");
		final JMenuItem updates = new JMenuItem("Check for Updates...",
				ImageCreator.IMG_UPDATE);		
		final JMenuItem preferences = new JMenuItem("Preferences",
				ImageCreator.IMG_PREFERENCES);

		headers.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		
		encoderHash.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		preferences.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		options.add(headers);
		options.addSeparator();
		options.add(encoderHash);
		options.add(updates);
		options.addSeparator();
		options.add(preferences);

		

		//
		// The action listeners for each component...
		//



		graphing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (graphing.getState()) {
							JBroFuzzMenuBar.this.getFrame().setTabShow(
									JBroFuzzWindow.ID_PANEL_GRAPHING);
						} else {
							JBroFuzzMenuBar.this.getFrame().setTabHide(
									JBroFuzzWindow.ID_PANEL_GRAPHING);
						}
					}
				});
			}
		});

		fuzzing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (fuzzing.getState()) {
							JBroFuzzMenuBar.this.getFrame().setTabShow(
									JBroFuzzWindow.ID_PANEL_FUZZING);
						} else {
							JBroFuzzMenuBar.this.getFrame().setTabHide(
									JBroFuzzWindow.ID_PANEL_FUZZING);
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
							JBroFuzzMenuBar.this.getFrame().setTabHide(
									JBroFuzzWindow.ID_PANEL_PAYLOADS);
						} else {
							JBroFuzzMenuBar.this.getFrame().setTabShow(
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
							JBroFuzzMenuBar.this.getFrame().setTabHide(
									JBroFuzzWindow.ID_PANEL_SYSTEM);
						} else {
							JBroFuzzMenuBar.this.getFrame().setTabShow(
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

						JBroFuzzMenuBar.this.getFrame().setTabShow(
								JBroFuzzWindow.ID_PANEL_GRAPHING);
						graphing.setState(true);

						JBroFuzzMenuBar.this.getFrame().setTabShow(
								JBroFuzzWindow.ID_PANEL_FUZZING);
						fuzzing.setState(true);
					
						JBroFuzzMenuBar.this.getFrame().setTabShow(
								JBroFuzzWindow.ID_PANEL_PAYLOADS);
						payloads.setState(true);
						
						JBroFuzzMenuBar.this.getFrame().setTabShow(
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
						JBroFuzzMenuBar.this.getFrame().setTabHide(
								JBroFuzzWindow.ID_PANEL_GRAPHING);
						graphing.setState(false);
						JBroFuzzMenuBar.this.getFrame().setTabHide(
								JBroFuzzWindow.ID_PANEL_FUZZING);
						fuzzing.setState(false);
						
						JBroFuzzMenuBar.this.getFrame().setTabHide(
								JBroFuzzWindow.ID_PANEL_PAYLOADS);
						payloads.setState(false);
						
						JBroFuzzMenuBar.this.getFrame().setTabHide(
								JBroFuzzWindow.ID_PANEL_SYSTEM);
						system.setState(false);

					}
				});

			}
		});

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

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						final int c = getFrame().getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) getFrame().getTp()
						.getComponent(c);
						p.stop();

					}
				});

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

		updates.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new CheckForUpdates(JBroFuzzMenuBar.this.getFrame());
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

		encoderHash.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new EncoderHashFrame();
					}
				});
			}
		});
		
		headers.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new HeaderFrame(JBroFuzzMenuBar.this.getFrame());
					}
				});
			}
		});

		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new PrefDialog(JBroFuzzMenuBar.this.getFrame());
					}
				});

			}
		});
		
	}

	public JBroFuzzWindow getFrame() {
		return mFrameWindow;
	}

	/**
	 * <p>
	 * Set which of the 5 buttons on the menu bar (i.e. 'start', 'stop',
	 * 'graph', 'add', 'remove') will be enabled and can be clicked.
	 * </p>
	 * <p>
	 * The input to this method will only be processed if a 5 element array is
	 * given as input.
	 * </p>
	 * 
	 * @param b
	 *            the boolean array of 5 elements
	 * 
	 * @author subere@uncon.org
	 * @version 1.9
	 * @since 1.2
	 */
	public void setEnabledPanelOptions(final boolean[] b) {

		if (b.length == 5) {

			start.setEnabled(b[0]);
			stop.setEnabled(b[1]);

			pause.setEnabled(b[2]);

			add.setEnabled(b[3]);
			remove.setEnabled(b[4]);

		}

	}
	
	/**
	 * <p>Method for checking which checkbox is set from the 
	 * "Panel" menu item.</p>
	 * 
	 * @param checkBoxID e.g. JBroFuzzWindow.ID_PANEL_FUZZING
	 * 
	 * @author subere@uncon.org
	 * @version 2.1
	 * @since 2.1
	 */
	public void setSelectedPanelCheckBox(int checkBoxID) {
		
		if(checkBoxID == JBroFuzzWindow.ID_PANEL_FUZZING) {
			fuzzing.setEnabled(true);
		}
		if(checkBoxID == JBroFuzzWindow.ID_PANEL_GRAPHING) {
			graphing.setEnabled(true);
		}
		if(checkBoxID == JBroFuzzWindow.ID_PANEL_PAYLOADS) {
			payloads.setEnabled(true);
		}
		if(checkBoxID == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			system.setEnabled(true);
		}

	}

}
