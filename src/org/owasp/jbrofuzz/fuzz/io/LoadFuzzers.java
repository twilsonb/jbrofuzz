/**
 * JBroFuzz 2.0
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

import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.FuzzerFileFilter;

public class LoadFuzzers {

	// The maximum number of chars to be read from file, regardless
	private final static int MAX_CHARS = Character.MAX_VALUE;

	public LoadFuzzers(JBroFuzzWindow mWindow) {

		// Set the Payloads Panel as the one to view
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_PAYLOADS);
		Logger.log("Load Fuzzers from file", 1);

		FuzzerFileFilter filter = new FuzzerFileFilter();

		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(mWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fc.getSelectedFile();
			Logger.log("Opening: " + file.getName(), 1);

			String path = file.getAbsolutePath().toLowerCase();
			// If the file does not end in .jbrf, return
			if (!path.endsWith(".jbrf")) {

				JOptionPane.showMessageDialog(fc,
						"The file selected is not a valid .jbrf file",
						" JBroFuzz - Open ", JOptionPane.WARNING_MESSAGE);
				return;
			}

			Database updateDB = new Database(path);
			mWindow.getJBroFuzz().setDatabase(updateDB);
			mWindow.getPanelPayloads().updateFuzzers();

		}

	}
}
