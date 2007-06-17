/**
 * JBroFuzz.java 0.6
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
package org.owasp.jbrofuzz;

import org.owasp.jbrofuzz.fuzz.*;
import org.owasp.jbrofuzz.fuzz.dir.*;
import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.snif.tcp.*;
import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.version.*;

/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>Description: The central class launching the application. This class
 * instantiates a request iterator, a version, a main frame window and 
 * a file handler.
 * The order in which the last three are instantiated should not be altered.</p>
 * <p>In order to fill in the contents of the corresponding text fields
 * (directories and generators), constructors are created to populate the
 * corresponding fields.
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class JBroFuzz {

	private JBRFormat mFormat;
	private JBRFrame mWindow;
  private FileHandler filehandler;

  /**
   * <p>The main creation object, instantiating a mWindow, a FileHander
   * and a Version object. The order in which construction takes place is
   * rather important.</p>
   */
  public JBroFuzz() {
  	mFormat = new JBRFormat(this);
    mWindow = new JBRFrame(this);
    filehandler = FileHandler.createFileHandler(mWindow, mFormat);
  }

  /**
   * Return the main window, thus allowing access to Gui components
   * @return mWindow
   */
  public JBRFrame getFrameWindow() {
    return mWindow;
  }
  
  public JBRFormat getFormat() {
  	return mFormat;
  }
  
  public FileHandler getFileHandler() {
  	return filehandler;
  }

  /**
   * <p>The main method instantiating the constructor.</p>
   * @param args String[]
   */
  public static void main(final String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new JBroFuzz();
      }
    });
  }
}
