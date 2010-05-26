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
package org.owasp.jbrofuzz.version;

/**
 * <p>A preference entry is a class holding the id (e.g.
 * ui.window.width) the title (as displayed in the 
 * Preferences menu, as well as the tool tip (i.e. a short
 * description) for that preference entry.</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class PrefEntry {

	private String id, title, tooltip;

	private boolean requiresRestart;
	
	/**
	 * <p>Constructor, passing the id (e.g. ui.window.width) the title 
	 * (as displayed in the Preferences menu, as well as the tool 
	 * tip (i.e. a short description) for that preference 
	 * entry.</p>
	 * 
	 * @param id
	 * @param title
	 * @param tooltip
	 */
	protected PrefEntry(String id, String title, String tooltip) {
		super();
		this.id = id;
		this.title = title;
		this.tooltip = tooltip;
		requiresRestart = false;
	}

	/**
	 * <p>Constructor, passing the id (e.g. ui.window.width) the title 
	 * (as displayed in the Preferences menu, as well as the tool 
	 * tip (i.e. a short description) for that preference 
	 * entry.</p>
	 * 
	 * @param id
	 * @param title
	 * @param tooltip
	 * @param requiresRestart
	 * 			whether or not the preference requires a restart of 
	 * 			JBroFuzz to be applied correctly
	 */
	protected PrefEntry(String id, String title, String tooltip, boolean requiresRestart) {
		super();
		this.id = id;
		this.title = title;
		this.tooltip = tooltip;
		this.requiresRestart = requiresRestart;
	}

	/**
	 * <p>Get the id, e.g. ui.window.width</p>
	 * 
	 * @return A String value such as the above
	 * 
	 */
	public final String getId() {
		return id;
	}

	/**
	 * <p>Return the title, e.g. "Fuzzing Directory (where data is saved)"</p>
	 * 
	 * <p>If the preference entry has been instaniated using the boolean
	 * "requiresRestart" set to true, then after the title the String 
	 * " (requires restart) " will be appended.</p>
	 * 
	 * @return A String value such as the above
	 * 
	 */
	public final String getTitle() {
		if(requiresRestart) {
			return title + " (requires restart)";
		} else {
			return title;
		}
	}

	/**
	 * <p>Return the tool tip, e.g. " Select Directory to Save Fuzzing Data "</p>
	 * 
	 * @return A String value such as the above
	 * 
	 */
	public final String getTooltip() {
		return tooltip;
	}
}
