/**
 * FrameMenuBar.java 0.4
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


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker3;
import javax.swing.JCheckBoxMenuItem;

import javax.swing.*;

import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.ver.Format;
/**
 * <p>The main menu bar attached to the main window.</p>
 *
 * @author subere (at) uncon org
 * @version 0.4
 */
public class FrameMenuBar extends JMenuBar {

  private final FrameWindow mFrameWindow;
  private final JMenu file, edit, view, panel, options, help;
  // Used under the Panel JMenu as items
  private JMenuItem start, pause, stop, add, remove;
  // Used under the view JMenu as items
  private JCheckBoxMenuItem fuzzing, sniffing, generators, system;
  /**
   *
   * @param mFrameWindow FrameWindow
   */
  public FrameMenuBar(FrameWindow mFrameWindow) {

    this.mFrameWindow = mFrameWindow;

    file = new JMenu("File");
    edit = new JMenu("Edit");
    view = new JMenu("View");
    panel = new JMenu("Panel");
    options = new JMenu("Options");
    help = new JMenu("Help");

    add(file);
    add(edit);
    add(view);
    add(panel);
    add(options);
    add(help);

    // File
    JMenuItem exit = new JMenuItem("Exit", ImageCreator.exitImageIcon);
    exit.setAccelerator(KeyStroke.getKeyStroke('1',
                                               Toolkit.getDefaultToolkit().
                                               getMenuShortcutKeyMask(), false));

    file.add(exit);
    // Edit
    Action cutAction = new CutAction();
    Action copyAction = new CopyAction();
    Action pasteAction = new PasteAction();
    Action selectAllAction = new SelectAllAction();

    JMenuItem cut = new JMenuItem(cutAction);
    JMenuItem copy = new JMenuItem(copyAction);
    JMenuItem paste = new JMenuItem(pasteAction);
    JMenuItem selectAll = new JMenuItem(selectAllAction);

    cut.setAccelerator(KeyStroke.getKeyStroke('X',
                                              Toolkit.getDefaultToolkit().
                                              getMenuShortcutKeyMask(), false));
    copy.setAccelerator(KeyStroke.getKeyStroke('C',
                                               Toolkit.getDefaultToolkit().
                                               getMenuShortcutKeyMask(), false));
    paste.setAccelerator(KeyStroke.getKeyStroke('V',
                                                Toolkit.getDefaultToolkit().
                                                getMenuShortcutKeyMask(), false));
    selectAll.setAccelerator(KeyStroke.getKeyStroke('A',
      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

    edit.add(cut);
    edit.add(copy);
    edit.add(paste);
    edit.addSeparator();
    edit.add(selectAll);

    // View
    fuzzing = new JCheckBoxMenuItem("TCP Fuzzing", true);
    sniffing = new JCheckBoxMenuItem("TCP Sniffing", true);
    generators = new JCheckBoxMenuItem("Generators", true);
    system = new JCheckBoxMenuItem("System", true);

    view.add(fuzzing);
    view.add(sniffing);
    view.add(generators);
    view.add(system);

    // Panel
    start = new JMenuItem("Start", ImageCreator.startImageIcon);
    pause = new JMenuItem("Pause", ImageCreator.pauseImageIcon);
    stop = new JMenuItem("Stop", ImageCreator.stopImageIcon);
    add = new JMenuItem("Add", ImageCreator.addImageIcon);
    remove = new JMenuItem("Remove", ImageCreator.removeImageIcon);

    add.setAccelerator(KeyStroke.getKeyStroke('=',
                                              Toolkit.getDefaultToolkit().
                                              getMenuShortcutKeyMask(), false));
    remove.setAccelerator(KeyStroke.getKeyStroke('-',
                                                 Toolkit.getDefaultToolkit().
                                                 getMenuShortcutKeyMask(), false));
    start.setAccelerator(KeyStroke.getKeyStroke('\n',
                                                Toolkit.getDefaultToolkit().
                                                getMenuShortcutKeyMask(), false));
    stop.setAccelerator(KeyStroke.getKeyStroke('\b',
                                               Toolkit.getDefaultToolkit().
                                               getMenuShortcutKeyMask(), false));

    panel.add(start);
    panel.add(pause);
    panel.add(stop);
    panel.addSeparator();
    panel.add(add);
    panel.add(remove);

    // Options
    JMenuItem preferences = new JMenuItem("Preferences");

    preferences.setAccelerator(KeyStroke.getKeyStroke('P',
      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

    options.add(preferences);

    // Help
    JMenuItem topics = new JMenuItem("Topics", ImageCreator.topicsImageIcon);
    JMenuItem disclaimer = new JMenuItem("Disclaimer",
                                         ImageCreator.disclaimerImageIcon);
    JMenuItem about = new JMenuItem("About", ImageCreator.helpImageIcon);

    about.setAccelerator(KeyStroke.getKeyStroke('0',
                                                Toolkit.getDefaultToolkit().
                                                getMenuShortcutKeyMask(), false));
    help.add(topics);
    help.addSeparator();
    help.add(disclaimer);
    help.addSeparator();
    help.add(about);

    //
    // Disable some items
    //
    topics.setEnabled(false);
    pause.setEnabled(false);

    //
    // The action listeners for each component...
    //
    exit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getFrameWindow().dispose();
        // System.exit(0);
      }
    });

    fuzzing.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!fuzzing.getState()) {
          getFrameWindow().setTabHide(FrameWindow.TCP_FUZZING_PANEL_ID);
        }
        else {
          getFrameWindow().setTabShow(FrameWindow.TCP_FUZZING_PANEL_ID);
        }
      }
    });

    sniffing.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!sniffing.getState()) {
          getFrameWindow().setTabHide(FrameWindow.TCP_SNIFFING_PANEL_ID);
        }
        else {
          getFrameWindow().setTabShow(FrameWindow.TCP_SNIFFING_PANEL_ID);
        }
      }
    });

    generators.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!generators.getState()) {
          getFrameWindow().setTabHide(FrameWindow.GENERATORS_PANEL_ID);
        }
        else {
          getFrameWindow().setTabShow(FrameWindow.GENERATORS_PANEL_ID);
        }
      }
    });

    system.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!system.getState()) {
          getFrameWindow().setTabHide(FrameWindow.SYSTEM_PANEL_ID);
        }
        else {
          getFrameWindow().setTabShow(FrameWindow.SYSTEM_PANEL_ID);
        }
      }
    });

    disclaimer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String message = Format.DISCLAIMER;
        // JOptionPane.showMessageDialog(
        JOptionPane.showMessageDialog(getFrameWindow(), message,
                                      "Disclaimer JBroFuzz",
                                      JOptionPane.ERROR_MESSAGE,
                                      ImageCreator.aboutImageIcon);
      }
    });

    about.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String message = Format.ABOUTTEXT;

        JOptionPane.showMessageDialog(getFrameWindow(), message,
                                      "About JBroFuzz",
                                      JOptionPane.INFORMATION_MESSAGE,
                                      ImageCreator.aboutImageIcon);
      }
    });

  }

  private FrameWindow getFrameWindow() {
    return mFrameWindow;
  }
}

class CutAction extends TextAction {
  public CutAction() {
    super("Cut");
  }

  public void actionPerformed(ActionEvent evt) {
    JTextComponent text = getTextComponent(evt);
    if(text != null) {
      text.cut();
      text.requestFocus();
    }
  }
}

class CopyAction extends TextAction {
  public CopyAction() {
    super("Copy");
  }

  public void actionPerformed(ActionEvent evt) {
    JTextComponent text = getTextComponent(evt);
    if(text != null) {
      text.copy();
      text.requestFocus();
    }
  }
}

class PasteAction extends TextAction {
  public PasteAction() {
    super("Paste");
  }

  public void actionPerformed(ActionEvent evt) {
    JTextComponent text = getTextComponent(evt);
    if(text != null) {
      text.paste();
      text.requestFocus();
    }
  }
}

class SelectAllAction extends TextAction {
  public SelectAllAction() {
    super("Select All");
  }

  public void actionPerformed(ActionEvent evt) {
    JTextComponent text = getTextComponent(evt);
    if(text != null) {
      text.selectAll();
      text.requestFocus();
    }
  }
}


