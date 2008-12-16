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
import org.owasp.jbrofuzz.fuzz.FuzzingPanel;
import org.owasp.jbrofuzz.graph.GraphingPanel;
import org.owasp.jbrofuzz.ui.headers.HeadersPanel;
import org.owasp.jbrofuzz.ui.menu.*;
import org.owasp.jbrofuzz.ui.panels.*;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

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
 * @version 1.2
 */
public class JBroFuzzWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Unique int identifier for the Graphing Panel
	 */
	public static final int ID_PANEL_GRAPHING = 121;

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
	private final JBroFuzzMenuBar mb;

	// The tabbed pane holding the different views
	private JTabbedPane tp;

	// The web directories panel
	private final GraphingPanel gp;

	// The main sniffing panel
	private final HeadersPanel sp;

	// The main definitions panel
	private final PayloadsPanel pp;

	// The main fuzzing panel
	private final FuzzingPanel fp;

	// The system logger panel
	private final SystemPanel cp;

	// The toolbar of the window
	private JBroFuzzToolBar tb;

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
		super("JBroFuzz " + JBroFuzzFormat.VERSION);
		this.mJBroFuzz = mJBroFuzz;

		// The container pane
		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		// The menu bar
		mb = new JBroFuzzMenuBar(this);
		setJMenuBar(mb);

		// The tool bar
		tb = new JBroFuzzToolBar(this);
		
		// The panels must be below the toolBar and menuBar
		gp = new GraphingPanel(this);
		fp = new FuzzingPanel(this);
		sp = new HeadersPanel(this);
		pp = new PayloadsPanel(this);
		cp = new SystemPanel(this);
		
		// Set the corresponding borders for each panel
		
		// The tabbed pane, setup according to preferences
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean tabsAtTop = prefs.getBoolean(JBroFuzzFormat.PR_2, false);
		if(tabsAtTop) {
			tp = new JTabbedPane(JTabbedPane.TOP);
		} else {
			tp = new JTabbedPane(JTabbedPane.BOTTOM);
		}
		tp.setOpaque(false);
		tp.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		tp.add(fp.getName(), fp);
		tp.add(gp.getName(), gp);
		tp.add(pp.getName(), pp);
		tp.add(sp.getName(), sp);
		
		tp.setSelectedIndex(0);
		
		tp.addChangeListener(new ChangeListener() {
			// Change listener for the tabbed pane
			public void stateChanged(ChangeEvent evt) {

				// Get current tab
	            JTabbedPane pane = (JTabbedPane)evt.getSource();
	            final int c = pane.getSelectedIndex();
	            if(c >= 0) {
	         
	            	boolean [] b = ((JBroFuzzPanel)pane.getComponent(c)).getOptionsAvailable();
	            	// Set the toolbar/menubar options which are enabled
	            	tb.setEnabledPanelOptions(b);
	            	mb.setEnabledPanelOptions(b);
	         
	            }
	            
	            Runtime.getRuntime().gc();
	    		Runtime.getRuntime().runFinalization();
	            
	        }
	    });
		
		pane.add(tb, BorderLayout.PAGE_START);
		pane.add(tp, BorderLayout.CENTER);

		// The image icon and min size
		setIconImage(ImageCreator.IMG_FRAME.getImage());
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

		fp.stop();
		gp.stop();
		pp.stop();
		sp.stop();
		cp.stop();
		
		// Get the prefences on deleting empty dirs on exit
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");
		boolean deleteBlankDirs = prefs.getBoolean(JBroFuzzFormat.PR_1, true);
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
	public JBroFuzzMenuBar getJBroMenuBar() {
		return mb;
	}

	public JBroFuzzToolBar getJBroToolBar() {
		return tb;
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
		return fp;
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
		return pp;
	}

	/**
	 * <p>
	 * Method returning the m sniffing panel that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mSniffingPanel
	 */
	public HeadersPanel getPanelSniffing() {
		return sp;
	}

	/**
	 * <p>
	 * Method for returning the web directoires panel that is being used.
	 * </p>
	 * 
	 * @return WebDirectoriesPanel
	 */
	public GraphingPanel getPanelWebDirectories() {
		return gp;
	}

	/**
	 * <p>
	 * Method for accessing the Tabbed Pane within the current Frame Window.
	 * </p>
	 * 
	 * @return JTabbedPane
	 */
	public JTabbedPane getTp() {
		return tp;
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
				cp.start(str);
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
			tp.remove(pp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {
			tp.remove(fp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SNIFFING) {
			tp.remove(sp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			tp.remove(cp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_GRAPHING) {
			tp.remove(gp);
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
			tp.addTab(pp.getName(), pp);
			tp.setSelectedComponent(pp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {
			tp.addTab(fp.getName(), fp);
			tp.setSelectedComponent(fp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SNIFFING) {
			tp.addTab(sp.getName(), sp);
			tp.setSelectedComponent(sp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			tp.addTab(cp.getName(), cp);
			tp.setSelectedComponent(cp);
		}
		
		if (n == JBroFuzzWindow.ID_PANEL_GRAPHING) {
			tp.addTab(gp.getName(), gp);
			tp.setSelectedComponent(gp);
		}

	}

}


