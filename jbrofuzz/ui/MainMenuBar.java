/**
 * MainMenuBar.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Toolkit;

import javax.swing.*;
import java.io.Serializable;

import jbrofuzz.ui.util.*;
/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>Description: The main menu bar attached to the main window.</p>
 *
 * @author subere@uncon.org
 * @version 0.1
 */
public class MainMenuBar extends JMenuBar {

  private final MainWindow mainWindow;
  private final JMenuItem startFuzzItem, stopFuzzItem, definitionsHelpItem;
  private final JMenu fileMenu, generatorMenu, fuzzMenu, helpMenu;

  public MainMenuBar(MainWindow mainWindow) {

    this.mainWindow = mainWindow;

    // JMenus...
    fileMenu = new JMenu("File");
    add(fileMenu);
    generatorMenu = new JMenu("Generator");
    add(generatorMenu);
    fuzzMenu = new JMenu("Fuzz");
    add(fuzzMenu);
    helpMenu = new JMenu("Help");
    add(helpMenu);

    // JMenuItems...

    // File -> Exit
    JMenuItem exitFileItem = new JMenuItem("Exit",
                                           this.mainWindow.
                                           getMainImageCreator().
                                           getImageIcon("exit.gif"));
    exitFileItem.setAccelerator(KeyStroke.getKeyStroke('1',
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    fileMenu.add(exitFileItem);
    // Generator -> Add
    JMenuItem addGeneratorItem = new JMenuItem("Add Generator",
                                               this.mainWindow.
                                               getMainImageCreator().
                                               getImageIcon("add.gif"));
    addGeneratorItem.setAccelerator(KeyStroke.getKeyStroke('=',
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    generatorMenu.add(addGeneratorItem);
    // Generator -> Remove
    JMenuItem removeGeneratorItem = new JMenuItem("Remove Generator",
                                                  this.mainWindow.
                                                  getMainImageCreator().
                                                  getImageIcon("remove.gif"));
    removeGeneratorItem.setAccelerator(KeyStroke.getKeyStroke('-',
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    generatorMenu.add(removeGeneratorItem);
    // Fuzz -> Start
    startFuzzItem = new JMenuItem("Start",
                                              this.mainWindow.
                                              getMainImageCreator().
                                              getImageIcon("start.gif"));
    startFuzzItem.setAccelerator(KeyStroke.getKeyStroke('\n',
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    fuzzMenu.add(startFuzzItem);
    // Fuzz -> Stop
    stopFuzzItem = new JMenuItem("Stop",
                                                this.mainWindow.
                                                getMainImageCreator().
                                                getImageIcon("stop.gif"));
    stopFuzzItem.setAccelerator(KeyStroke.getKeyStroke('\b',
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    stopFuzzItem.setEnabled(false);
    fuzzMenu.add(stopFuzzItem);
    // Help -> Definitions
    definitionsHelpItem = new JMenuItem("Definitions...");

    helpMenu.add(definitionsHelpItem);
    // Help -> ___
    helpMenu.addSeparator();
    // Help -> Disclaimer
    JMenuItem helpDisclaimerItem = new JMenuItem("Disclaimer");
    helpMenu.add(helpDisclaimerItem);
    // Help -> ___
    helpMenu.addSeparator();
    // Help -> About
    JMenuItem helpAboutItem = new JMenuItem("About",
                                            this.mainWindow.getMainImageCreator().
                                            getImageIcon("help.gif"));

    helpAboutItem.setAccelerator(KeyStroke.getKeyStroke('0',
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

    helpMenu.add(helpAboutItem);

    // The action listeners for each component...

    // User -> Info, I - Mnemonic
    addGeneratorItem.addActionListener
        (
            new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addGenerator();
      }
    }
    );

    // User -> Info, I - Mnemonic
    removeGeneratorItem.addActionListener
        (
            new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeGenerator();
      }
    }
    );

    // Fuzz -> Numbers
    startFuzzItem.addActionListener
        (
            new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final SwingWorker worker = new SwingWorker() {
          public Object construct() {
            fuzzStart();
            return "start-menu-bar-return";
          }
          public void finished() {
            fuzzFinished();
          }

        };
        worker.start();
      }
    }
    );

    // Fuzz -> Hex Digits
    stopFuzzItem.addActionListener
        (
            new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            fuzzStop();
          }
    }
    );

    // File -> Exit, X - Mnemonic
    exitFileItem.addActionListener
        (
            new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Maybe this should be called from the Gui class...
        System.exit(0);
      }
    }
    );

    // Help -> About, 0 - Mnemonic
    helpAboutItem.addActionListener
        (
            new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutBox();
      }
    });
    // Help -> Definitions, 9 - Mnemonic
    definitionsHelpItem.addActionListener
        (
          new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      setMainWindowTab(3);
    }
  });

    // Help -> About, 0 - Mnemonic
    helpDisclaimerItem.addActionListener
        (
            new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showDisclaimerBox();
      }
    });

  }

  /**
   * Generate a JOptionPane attached to the main window, displaying the about
   * box.
   *
   */
  public void showAboutBox() {
    String message = mainWindow.getJBroFuzz().getVersion().getAboutText();

    JOptionPane.showMessageDialog(mainWindow,
                                  message,
                                  "About JBroFuzz",
                                  JOptionPane.INFORMATION_MESSAGE,
                                  this.mainWindow.getMainImageCreator().
                                  getImageIcon("about.gif"));
  }

  /**
   * Generate a JOptionPane attached to the main window, displaying the
   * disclaimer box.
   *
   */
  public void showDisclaimerBox() {
    String message = mainWindow.getJBroFuzz().getVersion().getDisclaimerText();
    // JOptionPane.showMessageDialog(
    JOptionPane.showMessageDialog(mainWindow,
                                  message,
                                  "Disclaimer JBroFuzz",
                                  JOptionPane.ERROR_MESSAGE,
                                  this.mainWindow.getMainImageCreator().
                                  getImageIcon("about.gif"));
  }

  /**
   * <p>Method responsible for calling the fuzz start method of the main window,
   * thus kicking off a fuzzing process.</p>
   */
  public void fuzzStart() {
    mainWindow.setTabFuzzing();
    stopFuzzItem.setEnabled(true);
    startFuzzItem.setEnabled(false);
    mainWindow.getFuzzingPanel().setFuzzStop(true);
    mainWindow.getFuzzingPanel().setFuzzStart(false);
    mainWindow.setTabSniffingEnabled(false);
    mainWindow.getFuzzingPanel().fuzzStart();
  }

  /**
   * <p>
   * Method responsible for finishing the fuzzing process by reseting the button
   * enables, after a fuzzing process has completed. </p>
   * <p>This method is similar to fuzzStop, but does not call the stop generator</p>
   */
  public void fuzzFinished() {
    mainWindow.setTabFuzzing();
    stopFuzzItem.setEnabled(false);
    startFuzzItem.setEnabled(true);
    mainWindow.getFuzzingPanel().setFuzzStop(false);
    mainWindow.getFuzzingPanel().setFuzzStart(true);
    mainWindow.setTabSniffingEnabled(true);
  }

  /**
   * Method responsible for calling the fuzz stop method of the main window,
   * thus stopping the fuzzing process.
   */
  public void fuzzStop() {
    mainWindow.setTabFuzzing();
    this.mainWindow.getJBroFuzz().stopGenerator();
    stopFuzzItem.setEnabled(false);
    startFuzzItem.setEnabled(true);
    mainWindow.getFuzzingPanel().setFuzzStop(false);
    mainWindow.getFuzzingPanel().setFuzzStart(true);
    mainWindow.setTabSniffingEnabled(true);
  }

  public void addGenerator() {
    this.mainWindow.getFuzzingPanel().generatorAdd();
  }

  public void removeGenerator() {
    this.mainWindow.getFuzzingPanel().generatorRemove();
  }

  public void setFuzzStopEnabled(boolean b) {
    stopFuzzItem.setEnabled(b);
  }

  public void setFuzzStartEnabled(boolean b) {
    startFuzzItem.setEnabled(b);
  }

  /**
   * Set the MainWindow tab that is displayed.
   * @param n int
   */
  private void setMainWindowTab(int n) {
    if (n == 1) {
      mainWindow.setTabFuzzing();
      return;
    }
    if (n == 2) {
      mainWindow.setTabSniffing();
      return;
    }
    if (n == 3) {
      mainWindow.setTabDefinitions();
      return;
    }
  }

  public void setMenuFileEnable(final boolean b) {
    fileMenu.setEnabled(b);
  }
  public void setMenuGeneratorEnable(final boolean b) {
    generatorMenu.setEnabled(b);
  }
  public void setMenuFuzzEnable(final boolean b) {
    fuzzMenu.setEnabled(b);
  }
  public void setMenuHelpEnable(final boolean b) {
    helpMenu.setEnabled(b);
  }
}
