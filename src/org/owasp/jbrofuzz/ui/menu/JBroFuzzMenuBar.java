/**
 * JBroFuzz 1.4
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.owasp.jbrofuzz.fuzz.io.OpenSession;
import org.owasp.jbrofuzz.fuzz.io.SaveAsSession;
import org.owasp.jbrofuzz.fuzz.io.SaveSession;
import org.owasp.jbrofuzz.help.Faq;
import org.owasp.jbrofuzz.help.Topics;
import org.owasp.jbrofuzz.payloads.OpenLocationDialog;
import org.owasp.jbrofuzz.ui.JBroFuzzPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.actions.CopyAction;
import org.owasp.jbrofuzz.ui.actions.CutAction;
import org.owasp.jbrofuzz.ui.actions.PasteAction;
import org.owasp.jbrofuzz.ui.actions.SelectAllAction;
import org.owasp.jbrofuzz.update.CheckForUpdates;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * The main menu bar attached to the main frame window.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.3
 */
public class JBroFuzzMenuBar extends JMenuBar {

	private static final long serialVersionUID = -5135673814026322378L;

	private final JBroFuzzWindow mFrameWindow;
	// The menu items
	private final JMenu file, edit, view, panel, options, help;
	// Used under the Panel JMenu as items
	private JMenuItem showAll, hideAll, start, graph, stop, add, remove;
	// Used under the view JMenu as items
	private JCheckBoxMenuItem graphing, fuzzing, headers, payloads, system,
			wordWrap_res, wordWrap_req;

	/**
	 * 
	 * @param mFrameWindow
	 *            FrameWindow
	 */
	public JBroFuzzMenuBar(final JBroFuzzWindow mFrameWindow) {

		this.mFrameWindow = mFrameWindow;

		file = new JMenu("File");
		edit = new JMenu("Edit");
//		format = new JMenu("Format");
		view = new JMenu("View");
		panel = new JMenu("Panel");
		options = new JMenu("Options");
		help = new JMenu("Help");

		this.add(file);
		this.add(edit);
//		this.add(format);
		this.add(view);
		this.add(panel);
		this.add(options);
		this.add(help);

		// File
		final JMenuItem newFile = new JMenuItem("New");
		final JMenuItem open = new JMenuItem("Open File...");
		final JMenuItem close = new JMenuItem("Close");

		final JMenuItem openLocation = new JMenuItem("Open Location...");

		final JMenuItem save = new JMenuItem("Save", ImageCreator.IMG_SAVE);
		final JMenuItem saveAs = new JMenuItem("Save as...");
		final JMenuItem exit = new JMenuItem("Exit", ImageCreator.IMG_EXIT);

		newFile.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		close.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		openLocation.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		exit.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		file.add(newFile);
		file.add(open);
		file.add(close);
		file.addSeparator();
		file.add(openLocation);
		file.addSeparator();
		file.add(save);
		file.add(saveAs);
		file.addSeparator();
		file.add(exit);

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

		// Format

//		final JMenu requests = new JMenu("Requests");
//		final JMenu responses = new JMenu("Responses");

		// Get the two preferences for wrapping lines of text
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean wrapRequest = prefs.getBoolean(JBroFuzzFormat.WRAP_REQUEST, false);
		boolean wrapResponse = prefs.getBoolean(JBroFuzzFormat.WRAP_RESPONSE, false);

		wordWrap_req = new JCheckBoxMenuItem("Word Wrap", wrapRequest);
		wordWrap_res = new JCheckBoxMenuItem("Word Wrap", wrapResponse);

		final JMenuItem font_resp = new JMenuItem("Font...");
		final JMenuItem font_requ = new JMenuItem("Font...");
		
//		requests.add(wordWrap_req);
//		requests.add(font_requ);
//		
//		responses.add(wordWrap_res);
//		responses.add(font_resp);

//		font_resp.setEnabled(false);
//		font_requ.setEnabled(false);
//		
//		format.add(requests);
//		format.add(responses);

		// View
		final JMenu showHide = new JMenu("Show/Hide");

		graphing = new JCheckBoxMenuItem("Graphing", true);
		fuzzing = new JCheckBoxMenuItem("Fuzzing", true);
		headers = new JCheckBoxMenuItem("Headers", true);
		payloads = new JCheckBoxMenuItem("Payloads", false);
		system = new JCheckBoxMenuItem("System", false);

		showAll = new JMenuItem("Show All");
		hideAll = new JMenuItem("Hide All");

		showHide.add(fuzzing);
		showHide.add(headers);
		showHide.add(payloads);
		showHide.add(graphing);
		showHide.add(system);

		fuzzing.setState(true);
		headers.setState(true);
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
			final JRadioButtonMenuItem rb = new JRadioButtonMenuItem(
					installedFeels[i].getName());
			group.add(rb);
			lookAndFeel.add(rb);
			rb.setSelected(UIManager.getLookAndFeel().getName()
					.equalsIgnoreCase(installedFeels[i].getName()));

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
									UIManager.setLookAndFeel(info
											.getClassName());
									SwingUtilities
											.updateComponentTreeUI(JBroFuzzMenuBar.this
													.getFrame());
								} catch (final UnsupportedLookAndFeelException e) {
									JBroFuzzMenuBar.this
											.getFrame()
											.log(
													"An error occured while setting the Look & Feel",
													5);
								} catch (final IllegalAccessException e) {
									JBroFuzzMenuBar.this
											.getFrame()
											.log(
													"An error occured while setting the Look & Feel",
													5);
								} catch (final ClassNotFoundException e) {
									JBroFuzzMenuBar.this
											.getFrame()
											.log(
													"An error occured while setting the Look & Feel",
													5);
								} catch (final InstantiationException e) {
									JBroFuzzMenuBar.this
											.getFrame()
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
		graph = new JMenuItem("Graphing Panel", ImageCreator.IMG_GRAPH);
		stop = new JMenuItem("Stop", ImageCreator.IMG_STOP);
		add = new JMenuItem("Add", ImageCreator.IMG_ADD);
		remove = new JMenuItem("Remove", ImageCreator.IMG_REMOVE);

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
		final JMenuItem preferences = new JMenuItem("Preferences",
				ImageCreator.IMG_PREFERENCES);
		final JMenuItem updates = new JMenuItem("Check for Updates...",
				ImageCreator.IMG_UPDATE);

		preferences.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		options.add(updates);
		options.addSeparator();
		options.add(preferences);

		// Help
		final JMenuItem topics = new JMenuItem("Topics",
				ImageCreator.IMG_TOPICS);
		final JMenuItem faq = new JMenuItem("FAQ", ImageCreator.IMG_FAQ);
		// final JMenuItem tutorial = new JMenuItem("Tutorial");
		final JMenuItem website = new JMenuItem("JBroFuzz Website...",
				ImageCreator.IMG_OWASP_SML);
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

		// File -> New
		newFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fuzzing.setSelected(true);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.stop();

						mFrameWindow.getPanelFuzzing().clearAllFields();
						mFrameWindow.setTitle("Untitled");
					}

				} else {

					mFrameWindow.getPanelFuzzing().clearAllFields();
					mFrameWindow.setTitle("Untitled");

				}

							}
		});

		// File -> Open
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fuzzing.setSelected(true);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.stop();

						new OpenSession(mFrameWindow);

					}

				} else {
					new OpenSession(mFrameWindow);
				}

				// Run garbage collection as well
				// Runtime.getRuntime().gc();
				// Runtime.getRuntime().runFinalization();

			}
		});

		// File -> Close
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fuzzing.setSelected(true);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.stop();

						getFrame().setCloseFile();
						mFrameWindow.setTitle("Untitled");

					}

				} else {

					getFrame().setCloseFile();
					mFrameWindow.setTitle("Untitled");

				}

				// Run garbage collection as well
				// Runtime.getRuntime().gc();
				// Runtime.getRuntime().runFinalization();

			}
		});

		// File -> Open Location
		openLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fuzzing.setSelected(true);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.stop();

						new OpenLocationDialog(getFrame());

					}

				} else {

					new OpenLocationDialog(getFrame());

				}

				// Run garbage collection as well
				// Runtime.getRuntime().gc();
				// Runtime.getRuntime().runFinalization();

			}
		});

		// File -> Save
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fuzzing.setSelected(true);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.stop();

						new SaveSession(mFrameWindow);

					}

				} else {
					new SaveSession(mFrameWindow);

				}

				// Run garbage collection as well
				// Runtime.getRuntime().gc();
				// Runtime.getRuntime().runFinalization();

			}
		});

		// File -> Save as
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fuzzing.setSelected(true);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.stop();

						new SaveAsSession(mFrameWindow);

					}

				} else {

					new SaveAsSession(mFrameWindow);

				}

				// Run garbage collection as well
				// Runtime.getRuntime().gc();
				// Runtime.getRuntime().runFinalization();

			}
		});

		// File -> Exit
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JBroFuzzMenuBar.this.getFrame().exitProcedure();
					}
				});
			}
		});

//		wordWrap_req.addActionListener(new ActionListener() {
//			public void actionPerformed(final ActionEvent e) {
//				SwingUtilities.invokeLater(new Runnable() {
//
//					public void run() {
//
//						
//					}
//
//				});
//			}
//		});
		
//		wordWrap_res.addActionListener(new ActionListener() {
//			public void actionPerformed(final ActionEvent e) {
//				SwingUtilities.invokeLater(new Runnable() {
//
//					public void run() {
//
//						final Preferences prefs = Preferences.userRoot().node(
//								"owasp/jbrofuzz");
//
//						if (wordWrap_res.getState()) {
//
//							prefs.putBoolean(JBroFuzzFormat.WRAP_RESPONSE, true);
//
//						} else {
//
//							prefs
//									.putBoolean(JBroFuzzFormat.WRAP_RESPONSE,
//											false);
//
//						}
//					}
//
//				});
//			}
//		});

		graphing.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!graphing.getState()) {
							JBroFuzzMenuBar.this.getFrame().setTabHide(
									JBroFuzzWindow.ID_PANEL_GRAPHING);
						} else {
							JBroFuzzMenuBar.this.getFrame().setTabShow(
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
						if (!fuzzing.getState()) {
							JBroFuzzMenuBar.this.getFrame().setTabHide(
									JBroFuzzWindow.ID_PANEL_FUZZING);
						} else {
							JBroFuzzMenuBar.this.getFrame().setTabShow(
									JBroFuzzWindow.ID_PANEL_FUZZING);
						}
					}
				});
			}
		});

		headers.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (!headers.getState()) {
							JBroFuzzMenuBar.this.getFrame().setTabHide(
									JBroFuzzWindow.ID_PANEL_HEADERS);
						} else {
							JBroFuzzMenuBar.this.getFrame().setTabShow(
									JBroFuzzWindow.ID_PANEL_HEADERS);
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
								JBroFuzzWindow.ID_PANEL_HEADERS);
						headers.setState(true);
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
								JBroFuzzWindow.ID_PANEL_HEADERS);
						headers.setState(false);
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

					JBroFuzzPanel p;

					@Override
					public String doInBackground() {

						int c = getFrame().getTp().getSelectedIndex();
						p = (JBroFuzzPanel) getFrame().getTp().getComponent(c);
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

						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.stop();

					}
				});

			}
		});

		graph.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						getFrame().setTabShow(JBroFuzzWindow.ID_PANEL_GRAPHING);

					}
				});
			}
		});

		add.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
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
						new CheckForUpdates((JBroFuzzPanel) getFrame().getTp()
								.getComponent(
										getFrame().getTp().getSelectedIndex()));
					}
				});

			}
		});

		remove.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp()
								.getComponent(c);
						p.remove();

					}
				});

			}
		});

		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new JBroFuzzPrefs(JBroFuzzMenuBar.this.getFrame());
					}
				});

			}
		});
		faq.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Faq(JBroFuzzMenuBar.this.getFrame());
					}
				});
			}
		});

		topics.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Topics(JBroFuzzMenuBar.this.getFrame());
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
							JBroFuzzMenuBar.this
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
						new AboutBox(JBroFuzzMenuBar.this.getFrame(),
								AboutBox.DISCLAIMER);
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(JBroFuzzMenuBar.this.getFrame(),
								AboutBox.ABOUT);
					}
				});
			}
		});

	}

	private JBroFuzzWindow getFrame() {
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
	 * @version 1.3
	 * @since 1.2
	 */
	public void setEnabledPanelOptions(boolean[] b) {

		if (b.length == 5) {

			start.setEnabled(b[0]);
			stop.setEnabled(b[1]);

			graph.setEnabled(b[2]);

			add.setEnabled(b[3]);
			remove.setEnabled(b[4]);

		}

	}

}
