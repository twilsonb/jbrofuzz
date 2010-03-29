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
package org.owasp.jbrofuzz.system;

import java.util.*;
import java.io.File;



/**
 * <p>
 * Simplified file monitor.
 * </p>
 * <p>
 * Triggers a FileListener: FileChanged() method when the specified file changes.
 * </p>
 * 
 * @author ranulf@seleucus.net
 * @version 1.0
 * @since 2.0
 */
class SimpleFileMonitor 
{	
	private final Timer	timer;
	
	private final File logFile;     
	
	private long lastModified;
	
	private FileListener listener;
		
	private long lastLength;


	/**
	 * Create a file monitor instance with specified polling interval.
	 * 
	 * @param pollingInterval  Polling interval in milli-seconds.
	 */
	protected SimpleFileMonitor (final long pollingInterval, final File logFile)
	{
		this.logFile = logFile;
		this.lastLength = logFile.length();
		this.lastModified =  logFile.exists() ? logFile.lastModified() : -1;
		this.timer = new Timer (true);
		this.timer.schedule (new FileMonitorNotifier(), 0, pollingInterval);
	}

	/**
	 * Stop the file monitor polling.
	 */
	public void stop() {
		timer.cancel();
	}





	/**
	 * Add listener to this file monitor.
	 * 
	 * @param fileListener  Listener to add.
	 */
	protected void addListener (final FileListener fileListener)
	{
		listener = fileListener;
	}
	
	public long getLastLength() {

		return lastLength;
	}

	/**
	 * This is the timer thread which is executed every n milliseconds
	 * according to the setting of the file monitor. It investigates the
	 * file in question and notifies the listener if changed.
	 */
	private class FileMonitorNotifier extends TimerTask
	{
		public void run()
		{
			final long newModifiedTime  = logFile.exists() ? logFile.lastModified() : -1;
			
			// Check if file has changed
			if (newModifiedTime != lastModified) {

				// update the last modified time
				lastModified = Long.valueOf(newModifiedTime);
				
				// notify the listener
				listener.fileChanged(logFile);
				
				lastLength = Long.valueOf(logFile.length());
				
			}
		}
	}

}


