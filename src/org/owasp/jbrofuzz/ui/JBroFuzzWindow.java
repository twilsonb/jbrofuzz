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
package org.owasp.jbrofuzz.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.FuzzingPanel;
import org.owasp.jbrofuzz.graph.GraphingPanel;
import org.owasp.jbrofuzz.payloads.PayloadsPanel;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.system.SystemPanel;
import org.owasp.jbrofuzz.ui.menu.JBroFuzzMenuBar;
import org.owasp.jbrofuzz.ui.menu.JBroFuzzToolBar;
import org.owasp.jbrofuzz.update.StartUpdateCheck;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

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
 * @version 1.9
 * @since 1.3
 */
public class JBroFuzzWindow extends JFrame {

	private static final long serialVersionUID = -8055724052613595729L;

	// Dimensions of the frame
	private static final int SIZE_X = 700;
	private static final int SIZE_Y = 500;
	
	/**
	 * Unique int identifier for the Graphing Panel
	 */
	public static final int ID_PANEL_GRAPHING = 121;

	/**
	 * Unique int identifier for the Fuzzing Panel
	 */
	public static final int ID_PANEL_FUZZING = 124;

	/**
	 * Unique int identifier for the Payloads Panel
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

	// The main definitions panel
	private final PayloadsPanel pp;

	// The main fuzzing panel
	private final FuzzingPanel fp;

	// The system logger panel
	private final SystemPanel cp;

	// The toolbar of the window
	private final JBroFuzzToolBar tb;

	// The file to which the window saves to
	private File currentFile;
	
	// A boolean to see if the current file is set
	private boolean isCurrentFileSet;

	/**
	 * <p>
	 * The constructor of the main window launched in JBroFuzz.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *            JBroFuzz
	 */
	public JBroFuzzWindow(final JBroFuzz mJBroFuzz) {

		// The frame
		super(" JBroFuzz - Untitled ");
		this.mJBroFuzz = mJBroFuzz;
		currentFile = new File(System.getProperty("user.home") + File.separator
				+ "Untitled.jbrofuzz");
		isCurrentFileSet = false;

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
		pp = new PayloadsPanel(this);
		cp = new SystemPanel(this);

		// Set the corresponding borders for each panel

		// The tabbed pane, setup according to preferences
		final boolean tabsAtTop = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.GENERAL[1].getId(), true);
		if (tabsAtTop) {
			tp = new JTabbedPane(SwingConstants.TOP);
		} else {
			tp = new JTabbedPane(SwingConstants.BOTTOM);
		}
		tp.setOpaque(false);
		tp.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		tp.add(fp.getName(), fp);
		tp.add(gp.getName(), gp);
		tp.add(pp.getName(), pp);
		tp.add(cp.getName(), cp);

		// The selected tab
		tp.setSelectedIndex(0);

		tp.addChangeListener(new ChangeListener() {
			// Change listener for the tabbed pane
			public void stateChanged(ChangeEvent evt) {

				// Get current tab
				final JTabbedPane pane = (JTabbedPane) evt.getSource();
				final int c = pane.getSelectedIndex();
				if (c >= 0) {

					final boolean[] b = new boolean[5];
					b[0] = ((AbstractPanel) pane.getComponent(c))
					.isStartedEnabled();
					b[1] = ((AbstractPanel) pane.getComponent(c))
					.isStoppedEnabled();
					b[2] = ((AbstractPanel) pane.getComponent(c))
					.isPauseEnabled();
					b[3] = ((AbstractPanel) pane.getComponent(c))
					.isAddedEnabled();
					b[4] = ((AbstractPanel) pane.getComponent(c))
					.isRemovedEnabled();

					// Set the toolbar/menubar options which are enabled
					tb.setEnabledPanelOptions(b);
					mb.setEnabledPanelOptions(b);

				}

			}
		});

		pane.add(tb, BorderLayout.PAGE_START);
		pane.add(tp, BorderLayout.CENTER);

		Logger.log("System Launch, Welcome!", 1);

		// Check for a new version at startup
		final boolean checkNewVersion = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.GENERAL[0].getId(), true);
		if (checkNewVersion) {
			(new StartUpdateCheck(JBroFuzzWindow.this)).execute();
		}
		
	}

	public static void createAndShowGUI(final JBroFuzzWindow mJBroFuzzWindow) {

		mJBroFuzzWindow.setMinimumSize(new Dimension(Byte.MAX_VALUE, Byte.MAX_VALUE));
		mJBroFuzzWindow.setIconImage(ImageCreator.IMG_FRAME.getImage());


		mJBroFuzzWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mJBroFuzzWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent wEvent) {

				mJBroFuzzWindow.closeFrame();

			}
		});

		// Set the location of the window
		final int xLocation = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[0].getId(), 50);
		final int yLocation = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[1].getId(), 100);
		
		mJBroFuzzWindow.setLocation(xLocation, yLocation);

		// Set the size of the window
		int xSize = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[2].getId(), SIZE_X);
		if(xSize < Byte.MAX_VALUE) {
			xSize = Byte.MAX_VALUE;
		}

		int ySize = JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[3].getId(), SIZE_Y);
		if(ySize < Byte.MAX_VALUE) {
			ySize = Byte.MAX_VALUE;
		}
		
		mJBroFuzzWindow.setSize(new Dimension(xSize, ySize));
		mJBroFuzzWindow.setResizable(true);
		mJBroFuzzWindow.setVisible(true);
	}

	/**
	 * <p>
	 * Method for exiting the entire application.
	 * </p>
	 *
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 1.9
	 */
	public void closeFrame() {

		fp.stop();
		gp.stop();
		pp.stop();
		cp.stop();

		// If the frame is maximised, don't update the JBroFuzz window height & width
		if( (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {

			JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[0].getId(), this.getLocation().x);
			JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[1].getId(), this.getLocation().y);

		} else {
			
			JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[3].getId(), this.getSize().height);
			JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[2].getId(), this.getSize().width);
			
			JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[0].getId(), this.getLocation().x);
			JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[1].getId(), this.getLocation().y);			
		}

		// Save the values of the url/request as a preference
		final String sURL = fp.getTextURL();
		if(sURL.length() < Preferences.MAX_VALUE_LENGTH) {
			JBroFuzz.PREFS.put(JBroFuzzPrefs.TEXT_URL, fp.getTextURL());
		}
		
		final String sRequest = fp.getTextRequest();
		if(sRequest.length() < Preferences.MAX_VALUE_LENGTH) {
			JBroFuzz.PREFS.put(JBroFuzzPrefs.TEXT_REQUEST, fp.getTextRequest());
		}

		dispose();
		Logger.log("System Exit. Goodbye...", 1);
	}

	/**
	 * <p>
	 * Method returning the current file opened.
	 * </p>
	 * 
	 * @return File the file opened, default is "Untitled.jbrofuzz"
	 * 
	 * @see #setCloseFile()
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public File getCurrentFileOpened() {

		return currentFile;

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
	 * Method for returning the m menu bar that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mMenuBar
	 */
	public JBroFuzzMenuBar getJBroMenuBar() {

		return mb;

	}

	/**
	 * <p>
	 * Method for returning the tool bar present within the window.
	 * </p>
	 * 
	 * @return JBroFuzzToolBar The Tool Bar of JBroFuzz
	 * 
	 * @see #getJBroMenuBar()
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public JBroFuzzToolBar getJBroToolBar() {
		return tb;
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
	 * Method returning the payloads panel that is being instantiated through
	 * the main window.
	 * </p>
	 * 
	 * @return PayloadsPanel
	 */
	public PayloadsPanel getPanelPayloads() {
		return pp;
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
	 * Check if you have a filename from an opened file.
	 * </p>
	 * 
	 * @return true if a file has been opened by the user, but not closed.
	 * 
	 * @see #setOpenFileTo(File) {@link #setCloseFile()}
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public boolean isCurrentFileOpened() {

		return isCurrentFileSet;

	}

	/**
	 * <p>
	 * If a tab by the name specified, is open will return true.
	 * </p>
	 * 
	 * @param name
	 *            The name of the tab, e.g. " Fuzzing ", " Payloads ", ...
	 * @return True if tab with that name is open
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	private boolean isTabOpen(String name) {

		for (int i = 0; i < tp.getTabCount(); i++) {

			if (name.equals((((AbstractPanel) tp.getComponent(i)).getName()))) {

				return true;

			}
		}

		return false;

	}


	/**
	 * Close, in UI terms the file being used, setting it to the default
	 * "Untitled"
	 * 
	 * 
	 * @see #setOpenFileTo(File)
	 * @author subere@uncon.org
	 * @version 1.4
	 * @since 1.2
	 */
	public void setCloseFile() {

		isCurrentFileSet = false;

		currentFile = new File(System.getProperty("user.home") + File.separator
				+ "Untitled.jbrofuzz");
		// getPanelFuzzing().setTextURL("https://www.owasp.org");
		// getPanelFuzzing().setTextRequest(JBroFuzzFormat.URL_REQUEST);

	}

	/**
	 * Given a file, set the UI to that file as being opened
	 * 
	 * @param f
	 * 
	 * @see #setCloseFile()
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void setOpenFileTo(final File f) {

		currentFile = f;
		isCurrentFileSet = true;

		String name = f.getName();

		if (name.endsWith(".jbrofuzz")) {

			name = name.substring(0, name.indexOf(".jbrofuzz"));

		}

		setTitle(name);

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
			if (isTabOpen(pp.getName())) {

				tp.remove(pp);

			}
		}

		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {
			if (isTabOpen(fp.getName())) {

				tp.remove(fp);
			}
		}

		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			if (isTabOpen(cp.getName())) {

				tp.remove(cp);
			}
		}

		if (n == JBroFuzzWindow.ID_PANEL_GRAPHING) {
			if (isTabOpen(gp.getName())) {

				tp.remove(gp);
			}
		}

	}

	/**
	 * <p>
	 * Set which tab to show based on the int n of ID values. These are taken
	 * from the JBroFuzzWindow.
	 * </p>
	 * <p>
	 * If the tab is already present and does not need to be added, set the tab
	 * as the selected component.
	 * </p>
	 * 
	 * 
	 * @param n
	 *            The integer value representing each panel, defined in this
	 *            class.
	 */
	public void setTabShow(final int n) {

		if (n == JBroFuzzWindow.ID_PANEL_PAYLOADS) {

			if (!isTabOpen(pp.getName())) {
				tp.addTab(pp.getName(), pp);
			}
			tp.setSelectedComponent(pp);
		}

		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {

			if (!isTabOpen(fp.getName())) {
				tp.addTab(fp.getName(), fp);
			}
			tp.setSelectedComponent(fp);
		}

		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {

			if (!isTabOpen(cp.getName())) {
				tp.addTab(cp.getName(), cp);
			}
			tp.setSelectedComponent(cp);
		}

		if (n == JBroFuzzWindow.ID_PANEL_GRAPHING) {

			if (!isTabOpen(gp.getName())) {
				tp.addTab(gp.getName(), gp);
			}
			tp.setSelectedComponent(gp);
		}

	}

	@Override
	public void setTitle(String s) {

		super
		.setTitle(" JBroFuzz - "
				+ JBroFuzzFormat.centerAbbreviate(s, 256));

	}
}