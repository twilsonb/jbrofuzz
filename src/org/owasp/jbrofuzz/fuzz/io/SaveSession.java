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
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.fuzz.ui.EncodersRow;
import org.owasp.jbrofuzz.fuzz.ui.FuzzersTableModel;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.JBroFuzzFileFilter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>Class Responsible for saving the fuzzing session a user 
 * sees in the "Fuzzing" tab.</p>
 * @author daemonmidi@gmail.com
 * @version 2.4
 * @since 2.4
 * 
 * 
 * @author subere@uncon.org
 * @version 2.3
 * @since 1.2
 */
public class SaveSession {
	
	private final JBroFuzzWindow mWindow;
	private final FuzzersTableModel mFuzzTableModel;
	
	public SaveSession (final JBroFuzzWindow mWindow){
		this.mWindow = mWindow;
		mFuzzTableModel = mWindow.getPanelFuzzing().getFuzzersTableModel();
		new SaveSession(mWindow, "");
	}

	public SaveSession(final JBroFuzzWindow mWindow, String fileName) {
		this.mWindow = mWindow;
		mFuzzTableModel = mWindow.getPanelFuzzing().getFuzzersTableModel();

		// Set the Fuzzing Panel as the one to view
		mWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);
		Logger.log("Save Fuzzing Session", 1);

		File file = null; 
		
		if (fileName.length() == 0 || fileName.equals("")){
			file = mWindow.getCurrentFileOpened();
			// If there is no file opened, show a dialog
			if (!mWindow.isCurrentFileOpened() || (fileName.length() == 0 && fileName.equals(""))) {

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
					Logger.log("A security exception occured, while attempting to save to a directory", 4);
				}
				
				fc.setFileFilter(filter);

				final int returnVal = fc.showSaveDialog(mWindow);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					file = fc.getSelectedFile();
					Logger.log("Saving: " + file.getName(), 1);

				}
				
				String path = file.getAbsolutePath().toLowerCase();
				if (!path.endsWith(".jbrofuzz"))
					file = new File(path += ".jbrofuzz");

				if (file.exists()) {
					final int choice = JOptionPane.showConfirmDialog(
								fc,
								"File already exists. Do you \nwant to replace it?",
								" JBroFuzz - Save ",
								JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.NO_OPTION) return;
				}
			} 
		}
		else{
			// we do have a file name allready;
			file = new File(fileName);
		}
		// if file opened now

		// Get the values from the frame
		final String _url = mWindow.getPanelFuzzing().getTextURL();
		final String _req = mWindow.getPanelFuzzing().getTextRequest();
		final String _pld = getTextFuzzers();
		final String _trn = getTextTransforms();
		
		// Write the file
		try {

			final PrintWriter out = new PrintWriter(file);

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
			out.println("[Fuzzers]");
			out.println(_pld);
			out.println("[Transforms]");
			out.println(_trn);
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
			
		} catch (final FileNotFoundException e) {
			Logger.log("FileNotFoundException", 4);
		} catch (final SecurityException e) {
			Logger.log("SecurityException", 4);
		}
	}
	
	/**
	 * <p>
	 * Get the values of the Payloads from their table, limited to a maximum of
	 * 1024 rows.
	 * </p>
	 * <p>
	 * Return the values in Comma Separated Fields.
	 * </p>
	 * 
	 * @return The values of Payloads Table as CSV Text
	 * 
	 * @author ranulf
	 * @version 2.5
	 * @since 2.5
	 */
	public String getTextFuzzers() {


		final int rows = mFuzzTableModel.getRowCount();
		if (rows == 0) {
			return "";
		}

		final StringBuffer output = new StringBuffer();
		// MAX_LINES = 1024
		for (int row = 0; row < Math.min(rows, 1024); row++) {

			for (int column = 0; column < 3; column++) {
				output.append(mFuzzTableModel.getValueAt(row, column));
				// Append a ',' but not for the last value
				if (column != mFuzzTableModel.getColumnCount() - 1) {
					output.append(',');
				}
			}
			// Append a new line, but not for the last line
			if (row != Math.min(rows, 1024) - 1) {
				output.append('\n');
			}
			
		}

		return output.toString();
	}
	
	private static String encodersRowString(EncodersRow row){
		
		StringBuffer sbRet = new StringBuffer();
		
		sbRet.append(row.getEncoder());
		sbRet.append(',');
		sbRet.append(EncoderHashCore.encode(row.getPrefixOrMatch(),"Z-Base32"));
		sbRet.append(',');
		sbRet.append(EncoderHashCore.encode(row.getSuffixOrReplace(),"Z-Base32"));
		
		return sbRet.toString();
		
	}

	public String getTextTransforms() {
	
		StringBuffer out = new StringBuffer();

		final int rows = mFuzzTableModel.getRowCount();
		if (rows == 0) {
			return "";
		}

		for (int fuzzerRow = 0; fuzzerRow < Math.min(rows, 1024); fuzzerRow++) {
			
			EncodersRow[] encoderRows = mWindow.getPanelFuzzing().getEncoders(fuzzerRow);
			
			for (int encoderRow = 0; encoderRow < encoderRows.length; encoderRow++) {
				out.append(fuzzerRow);
				out.append(',');
				out.append(SaveSession.encodersRowString(encoderRows[encoderRow]));
				// if we are on the last encoder row, don't append a \n
				if(encoderRow != encoderRows.length - 1) {
					out.append('\n');
				}
			}
			
			if (fuzzerRow != rows - 1) {
				out.append('\n');
			}
			
		}
	
				
		return out.toString();
			
	}
}