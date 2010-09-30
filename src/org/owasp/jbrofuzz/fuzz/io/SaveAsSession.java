/**
 * JBroFuzz 2.4
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
package org.owasp.jbrofuzz.fuzz.io;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.JBroFuzzFileFilter;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class SaveAsSession {

	public SaveAsSession(final JBroFuzzWindow mWindow) {

		// Set the Fuzzing Panel as the one to view
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);
		Logger.log("Save As Fuzzing Session", 1);

		final JBroFuzzFileFilter filter = new JBroFuzzFileFilter();

		final String dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[2].getId(), System.getProperty("user.dir"));
		JFileChooser fc;
		try {
			if( (new File(dirString).isDirectory()) ) {
				fc = new JFileChooser(dirString);
			} else {
				fc = new JFileChooser();
			}
		} catch (final SecurityException e1) {
			fc = new JFileChooser();
			Logger.log("A security exception occured, while attempting to save as to a directory", 4);
		}

		fc.setFileFilter(filter);

		final int returnVal = fc.showSaveDialog(mWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fc.getSelectedFile();
			Logger.log("Saving: " + file.getName(), 1);

			String path = file.getAbsolutePath().toLowerCase();
			if (!path.endsWith(".jbrofuzz")) {
				file = new File(path += ".jbrofuzz");
			}

			if (file.exists()) {
				final int choice = JOptionPane.showConfirmDialog(fc,
						"File already exists. Do you \nwant to replace it?",
						" JBroFuzz - Save ", JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.NO_OPTION)
					return;
			}
			// call a SaveSession
			new SaveSession(mWindow, file.getName());

			final String parentDir = file.getParent();
			if(parentDir != null) {
				JBroFuzz.PREFS.put(JBroFuzzPrefs.DIRS[2].getId(), parentDir);
			}

		} 

	} // User clicks "Save"

}

