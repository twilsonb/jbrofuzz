/**
 * JBroFuzz 2.2
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
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.JBroFuzzFileFilter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>Class Responsible for saving the fuzzing session a user 
 * sees in the "Fuzzing" tab.</p>
 * 
 * @author subere@uncon.org
 * @version 2.3
 * @since 1.2
 */
public class SaveSession {

	public SaveSession(final JBroFuzzWindow mWindow) {

		// Set the Fuzzing Panel as the one to view
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);
		Logger.log("Save Fuzzing Session", 1);

		File file = mWindow.getCurrentFileOpened();

		// If there is no file opened, show a dialog
		if (!mWindow.isCurrentFileOpened()) {

			JBroFuzzFileFilter filter = new JBroFuzzFileFilter();

			final String dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[2].getId(), System.getProperty("user.dir"));
			JFileChooser fc;
			try {
				if( (new File(dirString).isDirectory()) ) {
					fc = new JFileChooser(dirString);
				} else {
					fc = new JFileChooser();
				}
			} catch (SecurityException e1) {
				fc = new JFileChooser();
				Logger.log("A security exception occured, while attempting to save to a directory", 4);
			}
			
			fc.setFileFilter(filter);

			int returnVal = fc.showSaveDialog(mWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				file = fc.getSelectedFile();
				Logger.log("Saving: " + file.getName(), 1);

				String path = file.getAbsolutePath().toLowerCase();
				if (!path.endsWith(".jbrofuzz"))
					file = new File(path += ".jbrofuzz");

				if (file.exists()) {
					int choice = JOptionPane
					.showConfirmDialog(
							fc,
							"File already exists. Do you \nwant to replace it?",
							" JBroFuzz - Save ",
							JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.NO_OPTION)
						return;
				}

			}
		} // if file opened

		// Get the values from the frame
		String _url = mWindow.getPanelFuzzing().getTextURL();
		String _req = mWindow.getPanelFuzzing().getTextRequest();
		String _pld = mWindow.getPanelFuzzing().getTextPayloads();

		// Write the file
		try {

			PrintWriter out = new PrintWriter(file);

			out.println("[JBroFuzz]");
			out.println(JBroFuzzFormat.VERSION);
			out.println("[Fuzzing]");
			out.println(JBroFuzzFormat.DATE);
			out.println("[Comment]");
			out.println("_");
			out.println("[URL]");
			out.println(_url);
			out.println("[Request]");
			out.println(_req);
			out.println("[Payloads]");
			out.println(_pld);
			out.println("[End]");

			if (out.checkError()) {
				Logger.log("An Error Occured while saving", 4);
			}

			out.close();
			// Finally, tell the frame this is the file opened
			// and save the directory location
			mWindow.setOpenFileTo(file);
			final String parentDir = file.getParent();
			if(parentDir != null) {
				JBroFuzz.PREFS.put(JBroFuzzPrefs.DIRS[2].getId(), parentDir);
			}

		} catch (FileNotFoundException e) {
			Logger.log("FileNotFoundException", 4);
		} catch (SecurityException e) {
			Logger.log("SecurityException", 4);
		}

	}
}
