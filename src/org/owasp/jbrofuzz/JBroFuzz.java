/**
 * JBroFuzz 1.7
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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
package org.owasp.jbrofuzz;

import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * Filename: JBroFuzz.java
 * </p>
 * 
 * <p>
 * Description: This class launches JBroFuzz. It instantiates a database of
 * fuzzers, a format object, the main frame window and a file IO handler.
 * </p>
 * 
 * <p>
 * The order in which the last four are instantiated should not be altered.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.5
 * @since 0.1
 */
public class JBroFuzz {

	/**
	 * <p>
	 * The main method launching the constructor of JBroFuzz.
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

	private JBroFuzzFormat mFormat;

	private JBroFuzzWindow mWindow;

	private Database mDatabase;

	/**
	 * <p>
	 * The main creation object, instantiating a Database, a Format Object, a
	 * Window, and a File Hander
	 * </p>
	 * 
	 * <p>
	 * The order in which construction takes place is rather important and
	 * should not be altered.
	 * </p>
	 */
	public JBroFuzz() {

		mDatabase = new Database();

		mFormat = new JBroFuzzFormat(this);
		mWindow = new JBroFuzzWindow(this);
		JBroFuzzWindow.createAndShowGUI(mWindow);

		mHandler = new FileHandler(this);

	}

	/**
	 * <p>
	 * Return the main database of exploits loaded, thus giving access to
	 * fuzzers and the payloads.
	 * </p>
	 * 
	 * @return The Database of Fuzzers
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
	 * @return The Format Object of JBroFuzz
	 */
	public JBroFuzzFormat getFormat() {

		return mFormat;

	}

	/**
	 * <p>
	 * Return the main file handler, thus allowing access to the various
	 * read/write IO methods and functions used at runtime.
	 * </p>
	 * 
	 * @return The file IO Object of JBroFuzz
	 */
	public FileHandler getHandler() {

		return mHandler;

	}

	/**
	 * <p>
	 * Return the main window, thus allowing access to various GUI components.
	 * </p>
	 * 
	 * @return The main frame window displayed
	 */
	public JBroFuzzWindow getWindow() {

		return mWindow;

	}
}
