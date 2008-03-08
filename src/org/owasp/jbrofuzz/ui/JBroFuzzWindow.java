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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import org.owasp.jbrofuzz.*;
import org.owasp.jbrofuzz.ui.menu.*;
import org.owasp.jbrofuzz.ui.panels.*;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.Format;

/**
 * <p>
 * The main window of JBroFuzz responsible for the graphical user interface.
 * </p>
 * <p>
 * This window holds all the Panels that are attached inside the TabbedPane
 * occupying the entire frame.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class JBroFuzzWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8877330557328054872L;

	/**
	 * Unique int identifier for the Web Directory Panel
	 */
	public static final int WEB_DIRECTORIES_PANEL_ID = 121;

	/**
	 * Unique int identifier for the Sniffing Panel
	 */
	public static final int SNIFFING_PANEL_ID = 123;

	/**
	 * Unique int identifier for the Fuzzing Panel
	 */
	public static final int FUZZING_PANEL_ID = 124;

	/**
	 * Unique int identifier for the Generators Panel
	 */
	public static final int PAYLOADS_PANEL_ID = 125;

	/**
	 * Unique int identifier for the System Panel
	 */
	public static final int SYSTEM_PANEL_ID = 126;

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
	private final PayloadsPanel mDefinitionsPanel;

	// The main fuzzing panel
	private final FuzzingPanel mFuzzingPanel;

	// The system logger panel
	private final SystemPanel mSystemLogger;

	// The HTTP fuzzing panel
	// private final HTTPFuzzing mHTTPFuzzingPanel;

	/**
	 * <p>
	 * The constuctor of the main window launched in JBroFuzz. This class should
	 * be instantiated as a singleton and never again.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *          JBroFuzz
	 */
	public JBroFuzzWindow(final JBroFuzz mJBroFuzz) {
		// The frame
		super("JBroFuzz " + Format.VERSION);
		this.mJBroFuzz = mJBroFuzz;
		// The menu bar
		mMenuBar = new JBRMenuBar(this);
		setJMenuBar(mMenuBar);
		// The container pane
		final Container pane = getContentPane();
		pane.setLayout(null);
		// The tabbed panels
		mWebDirectoriesPanel = new DirectoriesPanel(this);
		mFuzzingPanel = new FuzzingPanel(this);
		mSniffingPanel = new SniffingPanel(this);
		mDefinitionsPanel = new PayloadsPanel(this);
		mSystemLogger = new SystemPanel(this);
		// mHTTPFuzzingPanel = new HTTPFuzzing(this);
		// The tabbed pane, 3 is for bottom orientation
		tabbedPane = new JTabbedPane(3);
		// tabbedPane.setPreferredSize(new Dimension(588,368));
		tabbedPane.setBounds(0, 0, 895, 500);
		// Do not change the names!!!
		// tabbedPane.add(" HTTP/S Fuzzing ", mHTTPFuzzingPanel);
		tabbedPane.add(" Fuzzing ", mFuzzingPanel);
		tabbedPane.add(" Sniffing ", mSniffingPanel);
		tabbedPane.add(" Payloads ", mDefinitionsPanel);
		tabbedPane.add(" Web Directories ", mWebDirectoriesPanel);
		
		// tabbedPane.add(" Payloads ", mDefinitionsPanel);
		// tabbedPane.add(" System ", mSystemLogger);
		tabbedPane.setSelectedComponent(mFuzzingPanel);
		pane.add(tabbedPane);
		// The image icon
		setIconImage(ImageCreator.FRAME_IMG.getImage());

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exitProcedure();
			}
		});

		this.setLocation(100, 100);
		this.setSize(900, 550);
		setResizable(false);
		setVisible(true);

		log("System Launch, Welcome!");
	}

	/**
	 * <p>
	 * Method returning the m definitions panel that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mDefinitionsPanel
	 */
	public PayloadsPanel getDefinitionsPanel() {
		return mDefinitionsPanel;
	}

	/**
	 * <p>
	 * Method for returning the m menu bar that is being instantiated through the
	 * m window.
	 * </p>
	 * 
	 * @return mMenuBar
	 */
	public JBRMenuBar getFrameMenuBar() {
		return mMenuBar;
	}

	/**
	 * <p>
	 * Method for returning the fuzzing panel that is being instantiated through
	 * this frame window.
	 * </p>
	 * 
	 * @return mFuzzingPanel
	 */
	public FuzzingPanel getFuzzingPanel() {
		return mFuzzingPanel;
	}
	
	/**
	 * <p>
	 * Method for returning the HTTP fuzzing panel that is being instantiated through
	 * this frame window.
	 * </p>
	 * 
	 * @return mHTTPFuzzingPanel
	 *
	public HTTPFuzzing getHTTPFuzzingPanel() {
		return mHTTPFuzzingPanel;
	}
	 */

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
	 * Method returning the m sniffing panel that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mSniffingPanel
	 */
	public SniffingPanel getTCPSniffingPanel() {
		return mSniffingPanel;
	}

	/**
	 * <p>
	 * Method for returning the web directoires panel that is being used.
	 * </p>
	 * 
	 * @return WebDirectoriesPanel
	 */
	public DirectoriesPanel getWebDirectoriesPanel() {
		return mWebDirectoriesPanel;
	}

	/**
	 * <p>
	 * Method for logging values within the system event log.
	 * </p>
	 * 
	 * @param str
	 *          String
	 */
	public void log(final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mSystemLogger.addLoggingEvent(str);
			}
		});
	}

	/**
	 * Method for setting up the right click copy paste cut and select all menu.
	 * 
	 * @param area
	 *          JTextArea
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
	 * Set which tab to hide based on the int n of ID values. These are taken from
	 * the FrameWindow.
	 * 
	 * @param n
	 *          int
	 */
	public void setTabHide(final int n) {
		if (n == JBroFuzzWindow.PAYLOADS_PANEL_ID) {
			tabbedPane.remove(mDefinitionsPanel);
		}
		if (n == JBroFuzzWindow.FUZZING_PANEL_ID) {
			tabbedPane.remove(mFuzzingPanel);
		}
		if (n == JBroFuzzWindow.SNIFFING_PANEL_ID) {
			tabbedPane.remove(mSniffingPanel);
		}
		if (n == JBroFuzzWindow.SYSTEM_PANEL_ID) {
			tabbedPane.remove(mSystemLogger);
		}
		if (n == JBroFuzzWindow.WEB_DIRECTORIES_PANEL_ID) {
			tabbedPane.remove(mWebDirectoriesPanel);
		}
		
	}

	/**
	 * Set which tab to show based on the int n of ID values. These are taken from
	 * the FrameWindow.
	 * 
	 * @param n
	 *          int
	 */
	public void setTabShow(final int n) {
		if (n == JBroFuzzWindow.PAYLOADS_PANEL_ID) {
			tabbedPane.addTab(" Payloads ", mDefinitionsPanel);
			tabbedPane.setSelectedComponent(mDefinitionsPanel);
		}
		if (n == JBroFuzzWindow.FUZZING_PANEL_ID) {
			tabbedPane.addTab(" Fuzzing ", mFuzzingPanel);
			tabbedPane.setSelectedComponent(mFuzzingPanel);
		}
		if (n == JBroFuzzWindow.SNIFFING_PANEL_ID) {
			tabbedPane.addTab(" Sniffing ", mSniffingPanel);
			tabbedPane.setSelectedComponent(mSniffingPanel);
		}
		if (n == JBroFuzzWindow.SYSTEM_PANEL_ID) {
			tabbedPane.addTab(" System ", mSystemLogger);
			tabbedPane.setSelectedComponent(mSystemLogger);
		}
		if (n == JBroFuzzWindow.WEB_DIRECTORIES_PANEL_ID) {
			tabbedPane.addTab(" Web Directories ", mWebDirectoriesPanel);
			tabbedPane.setSelectedComponent(mWebDirectoriesPanel);
		}
		
	}
	
	/**
	 * <p>
	 * Method for exiting the entire application.
	 * </p>
	 *
	 */
	public void exitProcedure() {
		getJBroFuzz().getHandler().deleteEmptryDirectories();
		dispose();
	}

}
