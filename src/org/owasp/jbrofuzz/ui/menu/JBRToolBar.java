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

import java.awt.*;
import javax.swing.*;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.Format;

import com.Ostermiller.util.Browser;

import java.awt.event.*;
import java.io.IOException;

public class JBRToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JBroFuzzWindow mFrameWindow;

	public final JButton start, stop, graph, add, remove, help, about, website;

	public JBRToolBar(final JBroFuzzWindow mFrameWindow) {

		this.mFrameWindow = mFrameWindow;

		start = new JButton(ImageCreator.START_IMG);
		start.setToolTipText("Fuzz");
		stop = new JButton(ImageCreator.STOP_IMG);
		stop.setToolTipText("Stop");
		graph = new JButton(ImageCreator.PAUSE_IMG);
		graph.setToolTipText("Graph");
		add = new JButton(ImageCreator.ADD_IMG);
		add.setToolTipText("Add Generator");
		remove = new JButton(ImageCreator.REMOVE_IMG);
		remove.setToolTipText("Remove Generator");
		help = new JButton(ImageCreator.IMG_HELP);
		help.setToolTipText("Help Topics");
		about = new JButton(ImageCreator.IMG_ABOUT);
		about.setToolTipText("About");
		website = new JButton(ImageCreator.OWASP_IMAGE_SML);
		website.setToolTipText("JBroFuzz Website");

		this.addSeparator(new Dimension(13, 0));
		add(start);
		add(stop);
		this.addSeparator(new Dimension(6, 0));
		add(graph);
		this.addSeparator(new Dimension(13, 0));
		add(add);
		add(remove);
		this.addSeparator(new Dimension(13, 0));
		add(help);
		// this.addSeparator(new Dimension(6, 0));
		add(website);
		add(about);

		setFloatable(true);
		setRollover(true);

		start.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final SwingWorker3 worker = new SwingWorker3() {
							@Override
							public Object construct() {
								int currentTab = JBRToolBar.this
										.getFrameWindow().getTabbedPane()
										.getSelectedIndex();
								String s = JBRToolBar.this.getFrameWindow()
										.getTabbedPane().getTitleAt(currentTab);
								if (s.equals(" Fuzzing ")) {
									JBRToolBar.this.getFrameWindow()
											.getPanelFuzzing().start();
								}
								if (s.equals(" Sniffing ")) {
									JBRToolBar.this.getFrameWindow()
											.getPanelSniffing().start();
								}
								if (s.equals(" Web Directories ")) {
									JBRToolBar.this.getFrameWindow()
											.getPanelWebDirectories().start();
								}
								return "start-menu-bar-return";
							}

							@Override
							public void finished() {
								int currentTab = JBRToolBar.this
										.getFrameWindow().getTabbedPane()
										.getSelectedIndex();
								String s = JBRToolBar.this.getFrameWindow()
										.getTabbedPane().getTitleAt(currentTab);
								if (s.equals(" Fuzzing ")) {
									JBRToolBar.this.getFrameWindow()
											.getPanelFuzzing().stop();
								}
								if (s.equals(" Web Directories ")) {
									JBRToolBar.this.getFrameWindow()
											.getPanelWebDirectories().stop();
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
						final int currentTab = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRToolBar.this.getFrameWindow().getPanelFuzzing()
									.stop();
						}
						if (s.equals(" Sniffing ")) {
							JBRToolBar.this.getFrameWindow().getPanelSniffing()
									.stop();
						}
						if (s.equals(" Web Directories ")) {
							JBRToolBar.this.getFrameWindow()
									.getPanelWebDirectories().stop();
						}
					}
				});

			}
		});

		graph.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final int currentTab = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRToolBar.this.getFrameWindow().getPanelFuzzing()
									.fuzzBroButton();
						}
						if (s.equals(" Sniffing ")) {
							JBRToolBar.this.getFrameWindow().getPanelSniffing()
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
						final int currentTab = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRToolBar.this.getFrameWindow().getPanelFuzzing()
									.add();
						}

					}
				});

			}
		});

		remove.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						final int currentTab = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getSelectedIndex();
						final String s = JBRToolBar.this.getFrameWindow()
								.getTabbedPane().getTitleAt(currentTab);
						if (s.equals(" Fuzzing ")) {
							JBRToolBar.this.getFrameWindow().getPanelFuzzing()
									.remove();
						}

					}
				});

			}
		});

		help.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new JBRHelp(JBRToolBar.this.getFrameWindow());
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(JBRToolBar.this.getFrameWindow(),
								AboutBox.ABOUT);
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
							JBRToolBar.this
									.getFrameWindow()
									.log(
											"Could not launch link in external browser");
						}
					}
				});
			}
		});

	}

	public JBroFuzzWindow getFrameWindow() {

		return mFrameWindow;

	}

}
