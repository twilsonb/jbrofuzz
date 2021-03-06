/**
 * JbroFuzz 2.5
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
import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.FuzzerFileFilter;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class LoadFuzzers {

	public LoadFuzzers(JBroFuzzWindow mWindow) {

		// Set the Payloads Panel as the one to view
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
		Logger.log("Load Fuzzers from file", 1);

		final FuzzerFileFilter filter = new FuzzerFileFilter();

		final String dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[3].getId(), System.getProperty("user.dir"));
		JFileChooser fc;
		try {
			if( (new File(dirString).isDirectory())) {
				fc = new JFileChooser(dirString);
			} else {
				fc = new JFileChooser();
			}
		} catch (final SecurityException e1) {
			fc = new JFileChooser();
			Logger.log("A security exception occured, while attempting to point to a directory", 4);
		}
		
		fc.setFileFilter(filter);

		final int returnVal = fc.showOpenDialog(mWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			final File file = fc.getSelectedFile();
			Logger.log("Opening: " + file.getName(), 1);

			final String path = file.getAbsolutePath().toLowerCase();
			// If the file does not end in .jbrf, return
			if (!path.endsWith(".jbrf")) {

				JOptionPane.showMessageDialog(fc,
						"The file selected is not a valid .jbrf file",
						" JBroFuzz - Open ", JOptionPane.WARNING_MESSAGE);
				return;
			}

			final Database updateDB = new Database(path);
			mWindow.getJBroFuzz().setDatabase(updateDB);
			mWindow.getPanelPayloads().updateFuzzers();
			
			// Finally save as a preference the directory location
			final String parentDir = file.getParent();
			if(parentDir != null) {
				JBroFuzz.PREFS.put(JBroFuzzPrefs.DIRS[3].getId(), parentDir);
			}

		}

	}
}
