/**
 * MainWindow.java 0.3
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2006 yns000 (at) users. sourceforge. net
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
package jbrofuzz.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jbrofuzz.*;
import jbrofuzz.ui.util.*;
/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>Description: The main window of JBroFuzz responsible for the graphical
 * user interface.</p>
 *
 * @author subere@uncon.org
 * @version 0.3
 */
public class MainWindow
    extends JFrame {
  // The main Object behind it all...
  private final JBroFuzz jbrofuzz;
  // The main menu bar attached to this window frame...
  private final MainMenuBar mainMenuBar;
  // The Image Creator
  private final MainImageCreator mainImageCreator;
  // The tabbed pane holding the different views
  private static JTabbedPane tabbedPane;
  // The main sniffing pane
  private final MainSniffingPanel mainSniffingPanel;
  // The main definitions panel
  private final MainDefinitionsPanel mainDefinitionsPanel;
  // The main fuzzing panel
  private final MainFuzzingPanel mainFuzzingPanel;
  /**
   * <p>The constuctor of the main window launched in JBroFuzz. This class
   * should be instantiated as a singleton and never again.</p>
   * @param jbrofuzz JBroFuzz
   */
  public MainWindow(JBroFuzz jbrofuzz) {
    // The frame
    super("Java Bro Fuzzer " + jbrofuzz.getVersion().toString());
    this.jbrofuzz = jbrofuzz;
    // The image creator (must be before the menu bar)
    mainImageCreator = new MainImageCreator(this);
    // The menu bar
    mainMenuBar = new MainMenuBar(this);
    setJMenuBar(mainMenuBar);
    // The container pane
    Container pane = getContentPane();
    pane.setLayout(null);
    // The tabbed panels
    mainFuzzingPanel = new MainFuzzingPanel(this);
    mainSniffingPanel = new MainSniffingPanel(this);
    mainDefinitionsPanel = new MainDefinitionsPanel(this);

    // The tabbed pane
    tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
    // tabbedPane.setPreferredSize(new Dimension(588,368));
    tabbedPane.setBounds(0,0, 895,500);
    tabbedPane.add(" Fuzzing ", mainFuzzingPanel);
    tabbedPane.add(" Sniffing ", mainSniffingPanel);
    tabbedPane.add(" Definitions ",mainDefinitionsPanel);

    tabbedPane.setSelectedComponent(mainFuzzingPanel);
    pane.add(tabbedPane);
  }
  /**
   * <p>Access the main object that is responsible for launching an instance of
   * this class.</p>
   * @return JBroFuzz
   */
  public JBroFuzz getJBroFuzz() {
    return jbrofuzz;
  }
  /**
   * <p>Method for returning the main image creator that gets instantiated
   * through this main window.</p>
   * @return MainImageCreator
   */
  public MainImageCreator getMainImageCreator() {
    return mainImageCreator;
  }
  /**
   * <p>Set the viewing tab, within the frame to be the Definitions Panel.</p>
   */
  public void setTabDefinitions() {
    tabbedPane.setSelectedComponent(mainDefinitionsPanel);
  }
  /**
   * <p>Change whether or not the definitions tab can be accessed by means of
   * tweaking the setEnabled method.</p>
   * @param b boolean
   */
  public void setTabDefinitionsEnabled(final boolean b) {
    tabbedPane.setEnabledAt(2, b);
  }
  /**
   * <p>Set the viewing tab, within the frame to be the Fuzzing Panel.</p>
   */
  public void setTabFuzzing() {
    tabbedPane.setSelectedComponent(mainFuzzingPanel);
  }
  /**
   * <p>Change whether or not the fuzzing tab can be accessed by means of
   * tweaking the setEnabled method.</p>
   * @param b boolean
   */
  public void setTabFuzzingEnabled(final boolean b) {
    tabbedPane.setEnabledAt(0, b);
  }
  /**
   * <p>Set the viewing tab, within the frame to be the Sniffing Panel.</p>
   */
  public void setTabSniffing() {
    tabbedPane.setSelectedComponent(mainSniffingPanel);
  }
  /**
   * <p>Change whether or not the sniffing tab can be accessed by means of
   * tweaking the setEnabled method.</p>
   * @param b boolean
   */
  public void setTabSniffingEnabled(final boolean b) {
    tabbedPane.setEnabledAt(1, b);
  }
  /**
   * <p>Method returning the main sniffing panel that is being instantiated
   * through the main window.</p>
   * @return MainSniffingPanel
   */
  public MainSniffingPanel getSniffingPanel() {
    return mainSniffingPanel;
  }
  /**
   * <p>Method for returning the main menu bar that is being instantiated
   * through the main window.</p>
   * @return MainMenuBar
   */
  public MainMenuBar getMainMenuBar() {
    return mainMenuBar;
  }
  /**
   * <p>Method for returning the main fuzzing panel that is being instantiated
   * through the main window.</p>
   * @return MainFuzzingPanel
   */
  public MainFuzzingPanel getFuzzingPanel() {
    return mainFuzzingPanel;
  }
}
