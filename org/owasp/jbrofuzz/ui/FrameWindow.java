/**
 * FrameWindow.java 0.4
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon . org
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
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.ver.Format;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
/**
 * <p>The main window of JBroFuzz responsible for the graphical
 * user interface.</p>
 * <p>This window holds all the Panels that are attached inside the TabbedPane
 * occupying this entire frame.</p>
 *
 * @author subere (at) uncon . org
 * @version 0.4
 */
public class FrameWindow extends JFrame {
  // The main Object behind it all...
  private final JBroFuzz mJBroFuzz;
  // The main menu bar attached to this window frame...
  private final FrameMenuBar mMenuBar;
  // The tabbed pane holding the different views
  private static JTabbedPane tabbedPane;
  // The main sniffing pane
  private final SniffingPanel mSniffingPanel;
  // The main definitions panel
  private final DefinitionsPanel mDefinitionsPanel;
  // The main fuzzing panel
  private final FuzzingPanel mFuzzingPanel;
  // The system logger panel
  private final SystemLogger mSystemLogger;

  public static final int TCP_SNIFFING_PANEL_ID = 123;
  public static final int TCP_FUZZING_PANEL_ID = 124;
  public static final int GENERATORS_PANEL_ID = 125;
  public static final int SYSTEM_PANEL_ID = 126;

  /**
   * <p>The constuctor of the main window launched in JBroFuzz. This class
   * should be instantiated as a singleton and never again.</p>
   * @param mJBroFuzz JBroFuzz
   */
  public FrameWindow(JBroFuzz mJBroFuzz) {
    // The frame
    super("Java Bro Fuzzer " + Format.VERSION);
    this.mJBroFuzz = mJBroFuzz;
    // The menu bar
    mMenuBar = new FrameMenuBar(this);
    setJMenuBar(mMenuBar);
    // The container pane
    Container pane = getContentPane();
    pane.setLayout(null);
    // The tabbed panels
    mFuzzingPanel = new FuzzingPanel(this);
    mSniffingPanel = new SniffingPanel(this);
    mDefinitionsPanel = new DefinitionsPanel(this);
    mSystemLogger = new SystemLogger(this);
    // The tabbed pane 3 is for bottom orientation
    tabbedPane = new JTabbedPane(3);
    // tabbedPane.setPreferredSize(new Dimension(588,368));
    tabbedPane.setBounds(0, 0, 895, 500);
    tabbedPane.add(" TCP Fuzzing ", mFuzzingPanel);
    tabbedPane.add(" TCP Sniffing ", mSniffingPanel);
    tabbedPane.add(" Generators ", mDefinitionsPanel);
    tabbedPane.add(" System ", mSystemLogger);
    tabbedPane.setSelectedComponent(mFuzzingPanel);
    pane.add(tabbedPane);
    // The image icon
    setIconImage(ImageCreator.frameImageIcon.getImage());
    log("System Launch, Welcome!");
  }

  /**
   * <p>Access the m object that is responsible for launching an instance of
   * this class.</p>
   * @return JBroFuzz
   */
  public JBroFuzz getJBroFuzz() {
    return mJBroFuzz;
  }

  public void setTabHide(int n) {
    if(n == GENERATORS_PANEL_ID) {
      tabbedPane.remove(mDefinitionsPanel);
    }
    if(n == TCP_FUZZING_PANEL_ID) {
      tabbedPane.remove(mFuzzingPanel);
    }
    if(n == TCP_SNIFFING_PANEL_ID) {
      tabbedPane.remove(mSniffingPanel);
    }
    if(n == SYSTEM_PANEL_ID) {
      tabbedPane.remove(mSystemLogger);
    }
  }

  public void setTabShow(int n) {
    if(n == GENERATORS_PANEL_ID) {
      tabbedPane.addTab("Definitions", mDefinitionsPanel);
    }
    if(n == TCP_FUZZING_PANEL_ID) {
      tabbedPane.addTab("TCP Fuzzing", mFuzzingPanel);
    }
    if(n == TCP_SNIFFING_PANEL_ID) {
      tabbedPane.addTab("TCP Sniffing", mSniffingPanel);
    }
    if(n == SYSTEM_PANEL_ID) {
      tabbedPane.addTab("System", mSystemLogger);
    }
  }
  /**
   * <p>Method returning the m sniffing panel that is being instantiated
   * through the m window.</p>
   * @return mSniffingPanel
   */
  public SniffingPanel getTCPSniffingPanel() {
    return mSniffingPanel;
  }

  /**
   * <p>Method returning the m definitions panel that is being instantiated
   * through the m window.</p>
   * @return mDefinitionsPanel
   */
  public DefinitionsPanel getDefinitionsPanel() {
    return mDefinitionsPanel;
  }

  /**
   * <p>Method for returning the m menu bar that is being instantiated
   * through the m window.</p>
   * @return mMenuBar
   */
  public FrameMenuBar getFrameMenuBar() {
    return mMenuBar;
  }


  /**
   * <p>Method for returning the m fuzzing panel that is being instantiated
   * through the m window.</p>
   * @return mFuzzingPanel
   */
  public FuzzingPanel getFuzzingPanel() {
    return mFuzzingPanel;
  }

  /**
   * <p>Method for logging values within the system event log.</p>
   * @param str String
   */
  public void log(String str) {
    mSystemLogger.addLoggingEvent(str);
  }
}
