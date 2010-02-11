/**
 * JBroFuzz 1.9
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
package org.owasp.jbrofuzz.fuzz;


/**
 * <p>Class inspired by the Timer.java class of David Reilly.</p>
 * 
 * <p>Once a SocketTimer is started, it must be reset during 
 * the course of a lengthy operation, otherwise the timer will 
 * exit.</p>
 * 
 * <p>This class varies from the original implementation in the
 * primitive data types used (shorts and bytes instead of ints).
 * </p> 
 * 
 * <p>Changed back to int due to memory allocations within 
 * java.</p>
 *  
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 */
public class SocketTimer extends Thread {

	// Rate at which timer is checked
	private final int m_rate = Byte.MAX_VALUE;
	
	// Length of the Timer timeout in milliseconds
	private final int m_length;

	// The time that has elapsed on the counter thread
	private int tElapsed;

	// The Socket attached to this timer
	private final SocketConnection sConnection;
	
	/**
	 * <p>Create a Timer of specified length, specified as a
	 * short with a maximum value of +32767.</p>
	 * 
	 * @param sConnection the socket connection
	 * @param timeInMS Length of time (in ms) before timeout
	 */
	public SocketTimer(final SocketConnection sConnection, final int timeInMS) {
		
		super();
		this.sConnection = sConnection;
		this.m_length = timeInMS;
		this.tElapsed = 0;
		
	}
	
	/**
	 * <p>Method for resetting the timer on the thread.</p>
	 * <p>This method should be called in order for a 
	 * "timeout" not to be triggered.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public void reset() {
		synchronized(this) {
			tElapsed = 0;
		}
	}

	public void run() {
		// Infinite loop...
		for (;;) {
			// Put the timer to sleep
			try { 
				Thread.sleep(m_rate);
			}
			catch (InterruptedException ioe) {
				continue;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized(this) {
				
				// Increment time remaining
				tElapsed += m_rate;

				// Check to see if the time has been exceeded
				if (tElapsed > m_length) {
					// Close the connection, be rude...
					sConnection.close();
					break;
				}
				
			}

		}
	}

}
