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
package org.owasp.jbrofuzz.update;

import javax.swing.SwingWorker;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;

//Check for an updated version
public final class StartUpdateCheck extends SwingWorker<String, Object> {

	private final JBroFuzzWindow mWindow;
	
	public StartUpdateCheck(final JBroFuzzWindow mWindow) {
		this.mWindow = mWindow;
	}
	
	@Override
	public String doInBackground() {

		new StartUpdateChecker(mWindow);
		return "done-checking-updates";

	}

	@Override
	protected void done() {

	}
}