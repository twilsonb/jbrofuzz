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
package org.owasp.jbrofuzz.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.text.*;

import javax.swing.event.*;

import org.owasp.jbrofuzz.*;
import org.owasp.jbrofuzz.ui.menu.*;
import org.owasp.jbrofuzz.ui.panels.*;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBRFormat;

/**
 * <p>
 * The main window of JBroFuzz responsible for the graphical user interface.
 * </p>
 * <p>
 * This window holds all the Panels that are attached inside the TabbedPane
 * occupying the entire frame.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 */
public class JBroFuzzWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8877330557328054872L;

	/**
	 * Unique int identifier for the Web Directory Panel
	 */
	public static final int ID_PANEL_WEB_DIRECTORIES = 121;

	/**
	 * Unique int identifier for the Sniffing Panel
	 */
	public static final int ID_PANEL_SNIFFING = 123;

	/**
	 * Unique int identifier for the Fuzzing Panel
	 */
	public static final int ID_PANEL_FUZZING = 124;

	/**
	 * Unique int identifier for the Generators Panel
	 */
	public static final int ID_PANEL_PAYLOADS = 125;

	/**
	 * Unique int identifier for the System Panel
	 */
	public static final int ID_PANEL_SYSTEM = 126;

	// The main Object behind it all...
	private final JBroFuzz mJBroFuzz;

	// The main menu bar attached to this window frame...
	private final JBRMenuBar mMenuBar;

	// The tabbed pane holding the different views
	private JTabbedPane tabbedPane;

	// The web directories panel
	private final DirectoriesPanel mWebDirectoriesPanel;

	// The main sniffing panel
	private final SniffingPanel mSniffingPanel;

	// The main definitions panel
	private final PayloadsPanel mPayloadsPanel;

	// The main fuzzing panel
	private final FuzzingPanel mFuzzingPanel;

	// The system logger panel
	private final SystemPanel mSystemPanel;

	// The toolbar of the window
	private JBRToolBar mToolBar;

	/**
	 * <p>
	 * The constructor of the main window launched in JBroFuzz. This class
	 * should be instantiated as a singleton and never again.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *            JBroFuzz
	 */
	public JBroFuzzWindow(final JBroFuzz mJBroFuzz) {
		// The frame
		super("JBroFuzz " + JBRFormat.VERSION);
		this.mJBroFuzz = mJBroFuzz;

		// The container pane
		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		// The menu bar
		mMenuBar = new JBRMenuBar(this);
		setJMenuBar(mMenuBar);

		// The tool bar
		mToolBar = new JBRToolBar(this);
		
		// The panels must be below the toolBar and menuBar
		mWebDirectoriesPanel = new DirectoriesPanel(this);
		mFuzzingPanel = new FuzzingPanel(this);
		mSniffingPanel = new SniffingPanel(this);
		mPayloadsPanel = new PayloadsPanel(this);
		mSystemPanel = new SystemPanel(this);
		
		// Set the corresponding borders for each panel
		mWebDirectoriesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10,
				10, 10));
		mFuzzingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		mSniffingPanel.setBorder(BorderFactory
				.createEmptyBorder(10, 10, 10, 10));
		mPayloadsPanel.setBorder(BorderFactory
				.createEmptyBorder(10, 10, 10, 10));
		mSystemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// The tabbed pane, 3 is for bottom orientation
		tabbedPane = new JTabbedPane(3);
		tabbedPane.setOpaque(false);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		tabbedPane.add(mFuzzingPanel.getName(), mFuzzingPanel);
		tabbedPane.add(mSniffingPanel.getName(), mSniffingPanel);
		tabbedPane.add(mPayloadsPanel.getName(), mPayloadsPanel);
		tabbedPane.add(mWebDirectoriesPanel.getName(), mWebDirectoriesPanel);
		
		
		tabbedPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent evt) {
	            JTabbedPane pane = (JTabbedPane)evt.getSource();
	    
	            // Get current tab
	            int sel = pane.getSelectedIndex();
	            
	            String name = pane.getComponent(sel).getName();
	            if(name.equalsIgnoreCase(mFuzzingPanel.getName())) {
	            	// Set the options enabled taking into account if start has already being triggered
	            	boolean [] optionsEnabled = mFuzzingPanel.getOptionsAvailable();
	            	
	            	mToolBar.setEnabledPanelOptions(optionsEnabled);
	            	mMenuBar.setEnabledPanelOptions(optionsEnabled);
	            }
	            if(name.equalsIgnoreCase(mSniffingPanel.getName())) {
	            	// Set the options enabled taking into account if start has already being triggered
	            	boolean [] optionsEnabled = mSniffingPanel.getOptionsAvailable();
	            	
	            	mToolBar.setEnabledPanelOptions(optionsEnabled);
	            	mMenuBar.setEnabledPanelOptions(optionsEnabled);
	            }
	            if(name.equalsIgnoreCase(mSystemPanel.getName())) {
	            	mToolBar.setEnabledPanelOptions(mSystemPanel.getOptionsAvailable());
	            	mMenuBar.setEnabledPanelOptions(mSystemPanel.getOptionsAvailable());
	            }
	            if(name.equalsIgnoreCase(mPayloadsPanel.getName())) {
	            	mToolBar.setEnabledPanelOptions(mPayloadsPanel.getOptionsAvailable());
	            	mMenuBar.setEnabledPanelOptions(mPayloadsPanel.getOptionsAvailable());
	            }
	            if(name.equalsIgnoreCase(mWebDirectoriesPanel.getName())) {
	            	// Set the options enabled taking into account if start has already being triggered
	            	mToolBar.setEnabledPanelOptions(mWebDirectoriesPanel.getOptionsAvailable());
	            	mMenuBar.setEnabledPanelOptions(mWebDirectoriesPanel.getOptionsAvailable());
	            }
	        }
	    });
		tabbedPane.setSelectedComponent(mFuzzingPanel);

		
		pane.add(mToolBar, BorderLayout.PAGE_START);
		pane.add(tabbedPane, BorderLayout.CENTER);

		// The image icon and min size
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		setMinimumSize(new Dimension(400, 300));

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exitProcedure();
			}
		});

		this.setLocation(50, 100);
		this.setSize(950, 600);
		setResizable(true);
		setVisible(true);

		log("System Launch, Welcome!");
	}

	/**
	 * <p>
	 * Method for exiting the entire application.
	 * </p>
	 * 
	 */
	public void exitProcedure() {

		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();

		mFuzzingPanel.stop();
		
		// Get the prefences on deleting empty dirs on exit
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean deleteBlankDirs = prefs.getBoolean(JBRFormat.PR_1, true);
		if(deleteBlankDirs) {
			getJBroFuzz().getHandler().deleteEmptryDirectories();
		}
		dispose();

	}

	/**
	 * <p>
	 * Method for returning the m menu bar that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mMenuBar
	 */
	public JBRMenuBar getFrameMenuBar() {
		return mMenuBar;
	}

	public JBRToolBar getFrameToolBar() {
		return mToolBar;
	}

	/**
	 * <p>
	 * Access the m object that is responsible for launching an instance of this
	 * class.
	 * </p>
	 * 
	 * @return JBroFuzz
	 */
	public JBroFuzz getJBroFuzz() {
		return mJBroFuzz;
	}

	/**
	 * <p>
	 * Method for returning the fuzzing panel that is being instantiated through
	 * this frame window.
	 * </p>
	 * 
	 * @return mFuzzingPanel
	 */
	public FuzzingPanel getPanelFuzzing() {
		return mFuzzingPanel;
	}

	/**
	 * <p>
	 * Method returning the m definitions panel that is being instantiated
	 * through the m window.
	 * </p>
	 * 
	 * @return mDefinitionsPanel
	 */
	public PayloadsPanel getPanelPayloads() {
		return mPayloadsPanel;
	}

	/**
	 * <p>
	 * Method returning the m sniffing panel that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mSniffingPanel
	 */
	public SniffingPanel getPanelSniffing() {
		return mSniffingPanel;
	}

	/**
	 * <p>
	 * Method for returning the web directoires panel that is being used.
	 * </p>
	 * 
	 * @return WebDirectoriesPanel
	 */
	public DirectoriesPanel getPanelWebDirectories() {
		return mWebDirectoriesPanel;
	}

	/**
	 * <p>
	 * Method for accessing the Tabbed Pane within the current Frame Window.
	 * </p>
	 * 
	 * @return JTabbedPane
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * <p>
	 * Method for logging values within the system event log.
	 * </p>
	 * 
	 * @param str
	 *            String
	 */
	public void log(final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mSystemPanel.start(str);
			}
		});
	}

	/**
	 * Method for setting up the right click copy paste cut and select all menu.
	 * 
	 * @param area
	 *            JTextArea
	 */
	public void popup(final JTextComponent area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");

		i1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		i2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		i3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK));
		i4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.addSeparator();
		popmenu.add(i4);

		if (!area.isEditable()) {
			i3.setEnabled(false);
		}

		i1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.cut();
			}
		});

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.copy();
			}
		});

		i3.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (area.isEditable()) {
					area.paste();
				}
			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		area.addMouseListener(new MouseAdapter() {
			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				checkForTriggerEvent(e);
			}
		});
	}

	/**
	 * Set which tab to hide based on the int n of ID values. These are taken
	 * from the FrameWindow.
	 * 
	 * @param n
	 *            int
	 */
	public void setTabHide(final int n) {
		
		if (n == JBroFuzzWindow.ID_PANEL_PAYLOADS) {
			tabbedPane.remove(mPayloadsPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {
			tabbedPane.remove(mFuzzingPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SNIFFING) {
			tabbedPane.remove(mSniffingPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			tabbedPane.remove(mSystemPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES) {
			tabbedPane.remove(mWebDirectoriesPanel);
		}

	}

	/**
	 * Set which tab to show based on the int n of ID values. These are taken
	 * from the FrameWindow.
	 * 
	 * @param n
	 *            int
	 */
	public void setTabShow(final int n) {
		
		if (n == JBroFuzzWindow.ID_PANEL_PAYLOADS) {
			tabbedPane.addTab(mPayloadsPanel.getName(), mPayloadsPanel);
			tabbedPane.setSelectedComponent(mPayloadsPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {
			tabbedPane.addTab(mFuzzingPanel.getName(), mFuzzingPanel);
			tabbedPane.setSelectedComponent(mFuzzingPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SNIFFING) {
			tabbedPane.addTab(mSniffingPanel.getName(), mSniffingPanel);
			tabbedPane.setSelectedComponent(mSniffingPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			tabbedPane.addTab(mSystemPanel.getName(), mSystemPanel);
			tabbedPane.setSelectedComponent(mSystemPanel);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES) {
			tabbedPane.addTab(mWebDirectoriesPanel.getName(), mWebDirectoriesPanel);
			tabbedPane.setSelectedComponent(mWebDirectoriesPanel);
		}

	}

}


