/**
 * FrameWindow.java 0.6
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
package org.owasp.jbrofuzz.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.owasp.jbrofuzz.*;
import org.owasp.jbrofuzz.ui.menu.JBRMenuBar;
import org.owasp.jbrofuzz.ui.panels.Definitions;
import org.owasp.jbrofuzz.ui.panels.TCPFuzzing;
import org.owasp.jbrofuzz.ui.panels.OpenSource;
import org.owasp.jbrofuzz.ui.panels.TCPSniffing;
import org.owasp.jbrofuzz.ui.panels.SystemLogger;
import org.owasp.jbrofuzz.ui.panels.WebDirectories;
import org.owasp.jbrofuzz.ui.util.*;
import org.owasp.jbrofuzz.version.*;
/**
 * <p>The main window of JBroFuzz responsible for the graphical
 * user interface.</p>
 * <p>This window holds all the Panels that are attached inside the 
 * TabbedPane
 * occupying the entire frame.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class JBRFrame extends JFrame {

	// The main Object behind it all...
	private final JBroFuzz mJBroFuzz;

	// The main menu bar attached to this window frame...
	private final JBRMenuBar mMenuBar;

	// The tabbed pane holding the different views
	private JTabbedPane tabbedPane;

	// The web directories panel
	private final WebDirectories mWebDirectoriesPanel;

	// The main sniffing panel
	private final TCPSniffing mSniffingPanel;

	// The main definitions panel
	private final Definitions mDefinitionsPanel;

	// The main fuzzing panel
	private final TCPFuzzing mFuzzingPanel;

	// The system logger panel
	private final SystemLogger mSystemLogger;

	// The open source panel
	private final OpenSource mOpenSourcePanel;

	/**
	 * Unique int identifier for the Web Directory Panel
	 */
	public static final int WEB_DIRECTORIES_PANEL_ID = 121;

	/**
	 * Unique int identifier for the TCP Sniffing Panel
	 */
	public static final int TCP_SNIFFING_PANEL_ID = 123;

	/**
	 * Unique int identifier for the TCP Fuzzing Panel
	 */
	public static final int TCP_FUZZING_PANEL_ID = 124;

	/**
	 * Unique int identifier for the Generators Panel
	 */
	public static final int GENERATORS_PANEL_ID = 125;

	/**
	 * Unique int identifier for the System Panel
	 */
	public static final int SYSTEM_PANEL_ID = 126;

	/**
	 * Unique int identifier for the Open Source Panel
	 */
	public static final int OPEN_SOURCE_ID = 127;

	/**
	 * <p>The constuctor of the main window launched in JBroFuzz. This class
	 * should be instantiated as a singleton and never again.</p>
	 *
	 * @param mJBroFuzz JBroFuzz
	 */
	public JBRFrame(JBroFuzz mJBroFuzz) {
		// The frame
		super("Java Bro Fuzzer " + JBRFormat.VERSION);
		this.mJBroFuzz = mJBroFuzz;
		// The menu bar
		mMenuBar = new JBRMenuBar(this);
		setJMenuBar(mMenuBar);
		// The container pane
		Container pane = getContentPane();
		pane.setLayout(null);
		// The tabbed panels
		mWebDirectoriesPanel = new WebDirectories(this);
		mFuzzingPanel = new TCPFuzzing(this);
		mSniffingPanel = new TCPSniffing(this);
		mDefinitionsPanel = new Definitions(this);
		mSystemLogger = new SystemLogger(this);
		mOpenSourcePanel = new OpenSource(this);
		// The tabbed pane, 3 is for bottom orientation
		tabbedPane = new JTabbedPane(3);
		// tabbedPane.setPreferredSize(new Dimension(588,368));
		tabbedPane.setBounds(0, 0, 895, 500);
		// Do not change the names!!!
		tabbedPane.add(" Web Directories ", mWebDirectoriesPanel);
		tabbedPane.add(" Open Source ", mOpenSourcePanel);
		tabbedPane.add(" TCP Fuzzing ", mFuzzingPanel);
		tabbedPane.add(" TCP Sniffing ", mSniffingPanel);
		// tabbedPane.add(" Generators ", mDefinitionsPanel);
		// tabbedPane.add(" System ", mSystemLogger);
		tabbedPane.setSelectedComponent(mWebDirectoriesPanel);
		pane.add(tabbedPane);
		// The image icon
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		log("System Launch, Welcome!");
	}

	/**
	 * Method for setting up the right click copy paste cut and select all menu.
	 *
	 * @param area JTextArea
	 */
	public void popup(final JTextComponent area) {

		final JPopupMenu popmenu = new JPopupMenu();

		JMenuItem i1 = new JMenuItem("Cut");
		JMenuItem i2 = new JMenuItem("Copy");
		JMenuItem i3 = new JMenuItem("Paste");
		JMenuItem i4 = new JMenuItem("Select All");

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

		if(!area.isEditable()) {
			i3.setEnabled(false);
		}

		i1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				area.cut();
			}
		});

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				area.copy();
			}
		});

		i3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(area.isEditable()) {
					area.paste();
				}
			}
		});

		i4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				area.selectAll();
			}
		});

		area.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			public void mouseReleased(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			private void checkForTriggerEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * <p>Access the m object that is responsible for launching an instance of
	 * this class.</p>
	 *
	 * @return JBroFuzz
	 */
	public JBroFuzz getJBroFuzz() {
		return mJBroFuzz;
	}

	/**
	 * <p>Method for accessing the Tabbed Pane within the current Frame Window.
	 * </p>
	 *
	 * @return JTabbedPane
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * Set which tab to hide based on the int n of ID values. These are taken
	 * from the FrameWindow.
	 *
	 * @param n int
	 */
	public void setTabHide(int n) {
		if (n == GENERATORS_PANEL_ID) {
			tabbedPane.remove(mDefinitionsPanel);
		}
		if (n == TCP_FUZZING_PANEL_ID) {
			tabbedPane.remove(mFuzzingPanel);
		}
		if (n == TCP_SNIFFING_PANEL_ID) {
			tabbedPane.remove(mSniffingPanel);
		}
		if (n == SYSTEM_PANEL_ID) {
			tabbedPane.remove(mSystemLogger);
		}
		if (n == WEB_DIRECTORIES_PANEL_ID) {
			tabbedPane.remove(mWebDirectoriesPanel);
		}
		if (n == OPEN_SOURCE_ID) {
			tabbedPane.remove(mOpenSourcePanel);
		}
	}

	/**
	 * Set which tab to show based on the int n of ID values. These are taken
	 * from the FrameWindow.
	 *
	 * @param n int
	 */
	public void setTabShow(int n) {
		if (n == GENERATORS_PANEL_ID) {
			tabbedPane.addTab(" Generators ", mDefinitionsPanel);
		}
		if (n == TCP_FUZZING_PANEL_ID) {
			tabbedPane.addTab(" TCP Fuzzing ", mFuzzingPanel);
		}
		if (n == TCP_SNIFFING_PANEL_ID) {
			tabbedPane.addTab(" TCP Sniffing ", mSniffingPanel);
		}
		if (n == SYSTEM_PANEL_ID) {
			tabbedPane.addTab(" System ", mSystemLogger);
		}
		if (n == WEB_DIRECTORIES_PANEL_ID) {
			tabbedPane.addTab(" Web Directories ", mWebDirectoriesPanel);
		}
		if (n == OPEN_SOURCE_ID) {
			tabbedPane.addTab(" Open Source ", mOpenSourcePanel);
		}
	}

	/**
	 * <p>Method returning the m sniffing panel that is being instantiated
	 * through the m window.</p>
	 *
	 * @return mSniffingPanel
	 */
	public TCPSniffing getTCPSniffingPanel() {
		return mSniffingPanel;
	}

	/**
	 * <p>Method returning the m definitions panel that is being instantiated
	 * through the m window.</p>
	 *
	 * @return mDefinitionsPanel
	 */
	public Definitions getDefinitionsPanel() {
		return mDefinitionsPanel;
	}

	/**
	 * <p>Method for returning the m menu bar that is being instantiated
	 * through the m window.</p>
	 *
	 * @return mMenuBar
	 */
	public JBRMenuBar getFrameMenuBar() {
		return mMenuBar;
	}

	/**
	 * <p>Method for returning the web directoires panel that is being used.</p>
	 *
	 * @return WebDirectoriesPanel
	 */
	public WebDirectories getWebDirectoriesPanel() {
		return mWebDirectoriesPanel;
	}

	/**
	 * <p>Method for returning the fuzzing panel that is being instantiated
	 * through this frame window.</p>
	 *
	 * @return mFuzzingPanel
	 */
	public TCPFuzzing getFuzzingPanel() {
		return mFuzzingPanel;
	}

	/**
	 * <p>Method for returning the open source panel that is being instantiated
	 * through this frame window.</p>
	 * 
	 * @return
	 */
	public OpenSource getOpenSourcePanel() {
		return mOpenSourcePanel;
	}

	/**
	 * <p>Method for logging values within the system event log.</p>
	 *
	 * @param str String
	 */
	public void log(final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mSystemLogger.addLoggingEvent(str);
			}
		});
	}
	
}
