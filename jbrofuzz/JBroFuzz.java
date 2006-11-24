/**
 * JBroFuzz.java 0.3
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
package jbrofuzz;

import jbrofuzz.fuzz.*;
import jbrofuzz.io.*;
import jbrofuzz.snif.*;
import jbrofuzz.ui.*;
/**
 * @todo Check to see what happens when in fuzzing a socket has been triggered
 * which cannot connect and then a different URL is placed as part of the socket
 * request.
 *
 * @todo Run findbugs and compare with PMD Output; write a paper about what is
 * conflicting and what isn't.
 *
 * @todo Question if it is better to close the thread from the main, as
 * done in other programs.
 *
 * @todo Check out what is happening with POST request lengths
 * @todo Write a generator parser from a file that can be changed.
 */


/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>Description: The central class launching the application. This class
 * instantiates a request iterator, a version, a main window and a file handler.
 * The order in which the last three are instantiated should not be altered.</p>
 *
 * @author subere@uncon.org
 * @version 0.3
 */
public class JBroFuzz {

  private RequestIterator generator;
  private final Version version;
  private static MainWindow mainwindow;
  private static FileHandler filehandler;
  //
  private TCPConnectionListener listener;
  /**
   * <p>The main constructor object, instantiating a MainWindow, a FileHander
   * and a Version object. The order in which construction takes place is
   * rather important.</p>
   */
  public JBroFuzz() {
    version = new Version(3);
    mainwindow = new MainWindow(this);
    filehandler = new FileHandler(mainwindow);
  }

  /**
   * Return the current Version object
   * @return Version
   */
  public Version getVersion() {
    return version;
  }

  /**
   * Return the main window, thus allowing access to Gui components
   * @return MainWindow
   */
  public MainWindow getMainWindow() {
    return mainwindow;
  }

  /**
   * Set the generator responsible for running the fuzzing.
   * @param request String
   * @param start int
   * @param finish int
   * @param type int
   */
  public void setGenerator(final StringBuffer request, final int start,
                           final int finish, final String type) {

    generator = new RequestIterator(this, request, start, finish, type);
  }

  /**
   * Run the generator(s) present in the Generator object.
   */
  public void runGenerator() {
    generator.run();
  }

  /**
   * Stop the generator.
   */
  public void stopGenerator() {
    generator.stop();
  }

  /**
   * Create a sniffer using the address/host values stated here within
   * @param address String
   * @param port String
   * @param lAddress String
   * @param lPort String
   */
  public void createrSniffer(final String address, final String port,
                             final String lAddress, final String lPort) {
    listener =
        new TCPConnectionListener(mainwindow.getSniffingPanel(),
                                  address,
                                  port,
                                  lAddress,
                                  lPort);
  }

  /**
   * Stop the sniffer
   */
  public void stopSniffer() {
    listener.stopConnection();
  }

  /**
   * Get the FileHandler used in this instance of JBroFuzz
   * @return FileHandler
   */
  public FileHandler getFileHandler() {
    return filehandler;
  }

  /**
   * <p>This method is responsible for setting up the main window within the
   * dispatching thread.</p>
   *
   * <p>It is invoked from the main method and deals solely
   * with parameters of the JFrame (the mainWindow extends the JFrame).</p>
   */
  public void setupWindow() {
    // Global Frame Issues
    mainwindow.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    mainwindow.setLocation(100, 100);
    mainwindow.setSize(900, 550);
    mainwindow.setResizable(false);
    mainwindow.setVisible(true);
  }

  /**
   * <p>The main method instantiating the constructor.</p>
   * @param args String[]
   */
  public static void main(final String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        final JBroFuzz centralObject = new JBroFuzz();
        centralObject.setupWindow();
      }
    });

  }
}
