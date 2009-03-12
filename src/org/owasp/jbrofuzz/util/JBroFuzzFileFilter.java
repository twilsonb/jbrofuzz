/**
 * JBroFuzz 1.3
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
package org.owasp.jbrofuzz.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

/**
 * <p>
 * A file filter implementation, leaving only .jbrofuzz files. Case is ignored.
 * </p>
 * 
 * <p>
 * Originally inspired by the ExampleFileFilter class implemented by Jeff
 * Dinkins (version 1.16, 26/07/04).
 * </p>
 */
public class JBroFuzzFileFilter extends FileFilter {

	private Hashtable<String, JBroFuzzFileFilter>	filters											= null;
	private String																description									= null;
	private String																fullDescription							= null;
	private boolean																useExtensionsInDescription	= true;

	/**
	 * Creates a file filter. If no filters are added, then all files are
	 * accepted.
	 * 
	 * @see #addExtension
	 */
	public JBroFuzzFileFilter() {

		filters = new Hashtable<String, JBroFuzzFileFilter>();
		// add the .jbrofuzz extension and its description
		addExtension("jbrofuzz");
		setDescription("JBroFuzz Fuzzing Session Files");
	}

	/**
	 * Return true if this file should be shown in the directory pane, false if it
	 * shouldn't.
	 * 
	 * Files that begin with "." are ignored.
	 * 
	 * @param f
	 *          The file that should be checked
	 * @return true if this file should be shown
	 * 
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	@Override
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null) {
				return true;
			}
			;
		}
		return false;
	}

	/**
	 * Adds a filetype "dot" extension to filter against.
	 * 
	 * For example: the following code will create a filter that filters out all
	 * files except those that end in ".jpg" and ".tif":
	 * 
	 * ExampleFileFilter filter = new ExampleFileFilter();
	 * filter.addExtension("jpg"); filter.addExtension("tif");
	 * 
	 * Note that the "." before the extension is not needed and will be ignored.
	 */
	private void addExtension(String extension) {
		if (filters == null) {
			filters = new Hashtable<String, JBroFuzzFileFilter>(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}

	/**
	 * <p>
	 * Returns "JBroFuzz Fuzzing Session Files".
	 * </p>
	 * 
	 * 
	 * @return "JBroFuzz Fuzzing Session Files"
	 * @see FileFilter#getDescription
	 */
	@Override
	public String getDescription() {
		if (fullDescription == null) {
			if (description == null || isExtensionListInDescription()) {
				fullDescription = description == null ? "(" : description + " (";
				// build the description from the extension list
				Enumeration<String> extensions = filters.keys();
				if (extensions != null) {
					fullDescription += "." + extensions.nextElement();
					while (extensions.hasMoreElements()) {
						fullDescription += ", ." + extensions.nextElement();
					}
				}
				fullDescription += ")";
			} else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	/**
	 * Return the extension portion of the file's name .
	 * 
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	private String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
			;
		}
		return null;
	}

	/**
	 * Returns whether the extension list (.jpg, .gif, etc) should show up in the
	 * human readable description.
	 * 
	 * Only relevant if a description was provided in the constructor or using
	 * setDescription();
	 * 
	 * @see getDescription
	 * @see setDescription
	 * @see setExtensionListInDescription
	 */
	private boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}

	/**
	 * Sets the human readable description of this filter. For example:
	 * filter.setDescription("JBroFuzz Fuzzing Session Files");
	 * 
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 */
	private void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}
}
