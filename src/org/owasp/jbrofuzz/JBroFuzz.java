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
package org.owasp.jbrofuzz;

import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.core.*;
import org.owasp.jbrofuzz.version.*;

/**
 * <p>
 * Title: JBroFuzz
 * </p>
 * 
 * <p>
 * Description: The central class launching the application. This class
 * instantiates a database, a version, a main frame window and a file handler.
 * The order in which the last three are instantiated should not be altered.
 * </p>
 * <p>
 * In order to fill in the contents of the corresponding text fields
 * (directories and generators), constructors are created to populate the
 * corresponding fields.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 */
public class JBroFuzz {

	/**
	 * <p>
	 * The main method instantiating the constructor.
	 * </p>
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(final String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				new JBroFuzz();

			}

		});

	}

	private FileHandler mHandler;

	private JBRFormat mFormat;

	private JBroFuzzWindow mWindow;

	private Database mDatabase;

	/**
	 * <p>
	 * The main creation object, instantiating a Window, a File Hander and a
	 * Format object. The order in which construction takes place is rather
	 * important.
	 * </p>
	 */
	public JBroFuzz() {

		mDatabase = new Database();

		mFormat = new JBRFormat(this);
		mWindow = new JBroFuzzWindow(this);

		mHandler = new FileHandler(this);

	}

	/**
	 * <p>
	 * Return the main database of exploits loaded, thus giving access to
	 * generators, etc.
	 * </p>
	 * 
	 * @return Database
	 */
	public Database getDatabase() {

		return mDatabase;

	}

	/**
	 * <p>
	 * Return the main format object, thus allowing on top of static, also
	 * dynamic access to various constant variables, methods, etc.
	 * </p>
	 * 
	 * @return mFormat JBRFormat
	 */
	public JBRFormat getFormat() {

		return mFormat;

	}

	/**
	 * <p>
	 * Return the main file handler, thus allowing access to the various
	 * read/write IO methods and functions used at runtime.
	 * </p>
	 * 
	 * @return mHandler FileHandler
	 */
	public FileHandler getHandler() {

		return mHandler;

	}

	/**
	 * <p>
	 * Return the main window, thus allowing access to various GUI components.
	 * </p>
	 * 
	 * @return mWindow JBRFrame
	 */
	public JBroFuzzWindow getWindow() {

		return mWindow;

	}
}
