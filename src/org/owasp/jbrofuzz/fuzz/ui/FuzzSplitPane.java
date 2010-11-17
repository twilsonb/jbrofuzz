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
package org.owasp.jbrofuzz.fuzz.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;


/**
 * <p>
 * Extension of JSplitPane to handle preferences for the location of the split divider
 * </p>
 * <p>
 * 
 * @author ranulf
 * @version 2.5
 * @since 2.5
 */
public class FuzzSplitPane extends JSplitPane{
	private static final long serialVersionUID = -1581863821695643212L;
	private int TYPE;
	
	// constants for the Pane type in the UI
	public final static int FUZZ_MAIN = 4;
	public final static int FUZZ_BOTTOM = 5;
	public final static int OUTPUT_MAIN = 6;
	public final static int OUTPUT_BOTTOM = 7;


	/**
	 * <p>
	 * Constructor for FuzzSplitPane
	 * 
	 * @param orientation Vertical or Horizontal
	 * @param TYPE types are listed as constants within the class - i.e. fuzz panel bottom, output panel - main
	 */
	public FuzzSplitPane(int orientation, int TYPE){
		super(orientation);
		this.TYPE = TYPE;
		addListeners();
		this.setDividerLocation(JBroFuzz.PREFS.getInt(JBroFuzzPrefs.UI[TYPE].getId(), 400));
	}
	
	
	private void addListeners(){
		this.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName() == JSplitPane.DIVIDER_LOCATION_PROPERTY){
					JBroFuzz.PREFS.putInt(JBroFuzzPrefs.UI[TYPE].getId(), FuzzSplitPane.this.getDividerLocation());
				}
				
			}});
	}
	
	
	

}
