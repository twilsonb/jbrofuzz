package org.owasp.jbrofuzz.ui.menu;

import java.awt.*;
import javax.swing.*;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ui.panels.JBroFuzzPanel;
import org.owasp.jbrofuzz.util.*;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

import com.Ostermiller.util.Browser;

import java.awt.event.*;
import java.io.IOException;

public class JBroFuzzToolBar extends JToolBar {

	private static final long serialVersionUID = 1L;

	private final JBroFuzzWindow mFrameWindow;

	private final JButton start, stop, graph, add, remove, help, about, website;

	public JBroFuzzToolBar(final JBroFuzzWindow mFrameWindow) {

		this.mFrameWindow = mFrameWindow;

		start = new JButton(ImageCreator.START_IMG);
		start.setToolTipText("Start");
		stop = new JButton(ImageCreator.STOP_IMG);
		stop.setToolTipText("Stop");
		graph = new JButton(ImageCreator.PAUSE_IMG);
		graph.setToolTipText("Graph");
		add = new JButton(ImageCreator.ADD_IMG);
		add.setToolTipText("Add");
		remove = new JButton(ImageCreator.REMOVE_IMG);
		remove.setToolTipText("Remove");
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
		/*
		start.setEnabled(true);
		stop.setEnabled(false);
		graph.setEnabled(false);
		*/
		start.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final SwingWorker3 worker = new SwingWorker3() {
							
							JBroFuzzPanel p;
							@Override
							public Object construct() {
								
								// Get the current panel inside the tab
								int c = getFrame().getTp().getSelectedIndex();
								p = (JBroFuzzPanel) getFrame().getTp().getComponent(c);
								p.start();
								return "start-menu-bar-return";
								
							}

							@Override
							public void finished() {
								
								// Make sure while sniffing you don't stop
								if(!p.getName().equals(" Sniffing ")) {
									p.stop();
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
						
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp().getComponent(c);
						p.stop();
						
					}
				});

			}
		});

		graph.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp().getComponent(c);
						p.graph();
						
					}
				});
			}
		});

		add.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp().getComponent(c);
						p.add();

					}
				});

			}
		});

		remove.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						int c = getFrame().getTp().getSelectedIndex();
						JBroFuzzPanel p = (JBroFuzzPanel) getFrame().getTp().getComponent(c);
						p.remove();

					}
				});

			}
		});

		help.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new JBRHelp(JBroFuzzToolBar.this.getFrame());
					}
				});
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new AboutBox(JBroFuzzToolBar.this.getFrame(),
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
							Browser.displayURL(JBroFuzzFormat.URL_WEBSITE);
						} catch (final IOException ex) {
							JBroFuzzToolBar.this
							.getFrame()
							.log(
									"Could not launch link in external browser");
						}
					}
				});
			}
		});

	}
	
	public void setEnabledPanelOptions(boolean [] b) {
		
		if(b.length == 5) {
			start.setEnabled(b[0]);
			stop.setEnabled(b[1]);
			graph.setEnabled(b[2]);
			add.setEnabled(b[3]);
			remove.setEnabled(b[4]);
		}
	}

	public JBroFuzzWindow getFrame() {

		return mFrameWindow;

	}

}
