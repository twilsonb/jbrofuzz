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
import java.io.UnsupportedEncodingException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.ui.TransformsRow;
import org.owasp.jbrofuzz.fuzz.ui.FuzzersTableModel;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.JBroFuzzFileFilter;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

public class Save {

	private Save() {
		
	}
	
	/**
	 * <p>Method for obtaining a file location, through a 
	 * JFileChooser for the user to save a .jbrofuzz file.</p>
	 * 
	 * <p>In the event of an error or an exception, this
	 * method returns null.</p>
	 * 
	 * @param mWindow
	 * @return
	 */
	public static File showSaveDialog(JBroFuzzWindow mWindow) {
		
		final String dirString = JBroFuzz.PREFS.get(JBroFuzzPrefs.DIRS[2].getId(), System.getProperty("user.dir"));
		
		final File dirLocation = new File(dirString);
		JFileChooser fChooser;
		try {
			if(dirLocation.exists() && dirLocation.isDirectory()) {
				fChooser = new JFileChooser(dirString);
			} else {
				fChooser = new JFileChooser();
			}
		} catch(final SecurityException sException) {
			fChooser = new JFileChooser();
			Logger.log("A security exception occured, while attempting to save as to a directory", 4);
		}
		// Set the filter for the file extension
		fChooser.setFileFilter(new JBroFuzzFileFilter());
		// Talk to the user
		final int retValue = fChooser.showSaveDialog(mWindow);
		// If there is an approval or selection
		if(retValue == JFileChooser.APPROVE_OPTION) {
			
			File returnFile = fChooser.getSelectedFile();
			Logger.log("Saving: " + returnFile.getName(), 1);
			// 
			final String filePath = returnFile.getAbsolutePath().toLowerCase();
			if( ! filePath.endsWith(".jbrofuzz") ) {
				returnFile = new File(filePath + ".jbrofuzz");
			}
			
			if( returnFile.exists() ) {
				
				final int overwrite = JOptionPane.showConfirmDialog(fChooser,
						"File already exists. Do you \nwant to replace it?",
						" JBroFuzz - Save ", JOptionPane.YES_NO_OPTION);
				
				// If the user does not want to overwrite, return null
				if( overwrite == JOptionPane.NO_OPTION) {
					return null;
				}
			}
			// Before returning the file, set the preference
			// for the parent directory
			final String parentDir = returnFile.getParent();
			if(parentDir != null) {
				JBroFuzz.PREFS.put(JBroFuzzPrefs.DIRS[2].getId(), parentDir);
			}
			
			return returnFile;
		}
		
		// If the user cancelled, return nulls
		return null;

	}

	/**
	 * <p>Method for writing a ".jbrofuzz" file, given the file, the URL
	 * the Request string, the Fuzzers, as well as any specified
	 * transforms.</p>
	 * 
	 * @param The file to which data will be written to.
	 * @param The URL string, as specified in the fuzzing tab.
	 * @param The request string, as specified in the fuzzing tab.
	 * @param The fuzzers, specified in consecutive CSV type lines:
	 * e.g.  
	 * <code>
	 * 045-A85-RFC,5,10
	 * 034-B02-BIN,118,128
	 * </code>
	 * @param The transforms, specified also in consecutive CSV type lines:
	 * e.g.
	 * <code>
	 * URL UTF-8,111,222
	 * SHA-1 Hash,FED,THI
	 * </code>
	 */
	public static void writeFile(final File outputFile, final String url, final String request, final String fuzzers, final String transforms) {

		try {

			final PrintWriter out = new PrintWriter(outputFile);
			// Write the file
			out.println("[JBroFuzz]");
			out.println(JBroFuzzFormat.VERSION);
			out.println("[Fuzzing]");
			out.println(JBroFuzzFormat.DATE);
			out.println("[Comment]");
			out.println("_");
			out.println("[URL]");
			out.println(url);
			out.println("[Request]");
			out.println(request);
			out.println("[Fuzzers]");
			out.println(fuzzers);
			out.println("[Transforms]");
			out.println(transforms);
			out.println("[End]");

			if (out.checkError()) {
				Logger.log("Errors occured while saving", 4);
			}

			out.close();

		} catch (final FileNotFoundException e) {
			
			Logger.log("File Could Not Be Found To Save", 4);
		
		} catch (final SecurityException e) {
			
			Logger.log("A Security Exception Occured While Saving", 4);
			
		}
		
	}
	
	/**
	 * <p>Method for writing a ".jbrofuzz" file, given the file, the URL
	 * the Request string, the Fuzzers, as well as any specified
	 * transforms.</p>
	 * 
	 * @param The file we are writing to
	 * @param The main frame where JBroFuzz will get the URL, Request and
	 * other parameters.
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public static void writeFile(final File outputFile, final JBroFuzzWindow mWindow) {
		
		final String url = mWindow.getPanelFuzzing().getTextURL();
		final String request = mWindow.getPanelFuzzing().getTextRequest();
		final String fuzzers = Save.getTableDataInCSVFormat(mWindow.getPanelFuzzing().getFuzzersPanel().getFuzzersTableModel());
		final String transforms = Save.getTableOfTransformsInCSVFormat(mWindow);
		
		Save.writeFile(outputFile, url, request, fuzzers, transforms);
		
	}
	
	/**
	 * <p>Method for obtaining the CSV output, given a table.</p>
	 * <p>No "\n" is written at the end of the final line.</p>
	 * 
	 * @param The TableModel holding the data.
	 * @return
	 * <code>
	 * 1,Plain Text,,
	 * 2,URL UTF-8,graun,ge3dr
	 * </code>
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 */
	public static String getTableDataInCSVFormat(final TableModel inputTableModel) {
		
		final StringBuffer output = new StringBuffer();
		final int totalRows = inputTableModel.getRowCount();
		final int totalColumns = inputTableModel.getColumnCount();
		
		if (totalRows < 1) {
			return "";
		}

		for (int currentRow = 0; currentRow < totalRows; currentRow++) {

			for (int currentColumn = 0; currentColumn < totalColumns; currentColumn++) {
				output.append(inputTableModel.getValueAt(currentRow, currentColumn));
				// Append a ',' but not for the last value
				if (currentColumn != totalColumns - 1) {
					output.append(',');
				}
			}
			
			// Append a new line, but not for the last line
			if (currentRow != totalRows - 1) {
				output.append('\n');
			}

		}

		return output.toString();

	}

	
	/**
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param inputTableModel
	 * @return
	 */
	public static JSONArray getTableDataInJSON(final TableModel inputTableModel){
		JSONArray tableData = new JSONArray();
		final int totalRows = inputTableModel.getRowCount() -1;
		final int totalColumns = inputTableModel.getColumnCount() -1;
		if (totalRows < 1) {
			return new JSONArray();
		}

		for (int currentRow = 0; currentRow < totalRows; currentRow++) {

			for (int currentColumn = 0; currentColumn < totalColumns-1; currentColumn++) {
				String name = inputTableModel.getColumnName(currentColumn);
				String value = inputTableModel.getValueAt(currentColumn, currentRow).toString();
				String cellString = "{\"" + name  + "\":\"" + value + "\"}";
				JSONObject cell;
				try {
					cell = new JSONObject(cellString);
					tableData.put(cell);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return tableData;
	}
	
	/**
	 * <p>Method for obtaining the data for the transforms and their corresponding fuzzers
	 * in CSV type format.</p>
	 * <p>Examples include:<p>
	 * <code>
	 * 1,URL Cp1252,QUFB,QkJC
	 * 1,Base64,zqbOps6m,zqbOps6m
	 * 2,SHA-512 Hash,zpHOo86U,
	 * </code>
	 * 
	 * @param The main frame where JBroFuzz will get the URL, Request and
	 * other parameters.
	 * 
	 * @return e.g. the example above
	 * 
	 * @author subere@uncon.org
	 * @version 2.5
	 * @since 2.5
	 * 
	 */
	public static String getTableOfTransformsInCSVFormat(final JBroFuzzWindow mWindow) {
		
		FuzzersTableModel inputTableModel = mWindow.getPanelFuzzing().getFuzzersPanel().getFuzzersTableModel();
		
		final StringBuffer output = new StringBuffer();
		final int totalFuzzerRows = inputTableModel.getRowCount();
		
		if (totalFuzzerRows < 1) {
			return "";
		}

		for (int fuzzerRow = 0; fuzzerRow < totalFuzzerRows; fuzzerRow++) {
			
			TransformsRow[] encoderRows = mWindow.getPanelFuzzing().getTransformsPanel().getTransforms(fuzzerRow);
			
			final int totalEncoderRows = encoderRows.length;
			
			for (int transformRow = 0; transformRow <totalEncoderRows; transformRow++) {
				
				output.append( (fuzzerRow + 1) );
				output.append(',');
				output.append( encoderRows[transformRow].getEncoder() );
				output.append(',');
				
				String prefix;
				try {
					prefix = Base64.encodeBase64String( encoderRows[transformRow].getPrefixOrMatch().getBytes("UTF-8") );
					prefix = StringUtils.chomp(prefix);
				} catch (UnsupportedEncodingException e) {
					prefix = "";
				}
				output.append(prefix);
				output.append(',');
				
				String suffix;
				try {
					suffix = Base64.encodeBase64String( encoderRows[transformRow].getSuffixOrReplace().getBytes("UTF-8") );
					suffix = StringUtils.chomp(suffix);
				} catch (UnsupportedEncodingException e) {
					suffix = "";
				}
				output.append(suffix);
				
				// Append a new line, but not for the last line of the last transform
				if ( (fuzzerRow != totalFuzzerRows - 1) || (transformRow != totalEncoderRows - 1) ) {
					output.append('\n');
				}
				
			}

		}

		return output.toString();

	}

}
