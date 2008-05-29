/**
 * JBroFuzz 1.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
 * 
 * Copyright (C) 2007, 2008 subere@uncon.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 * 
 */
package org.owasp.jbrofuzz.snif;

/**
 * <p>
 * The interface to the TCPConnectionMonitor requiring attemptingConnection,
 * addingConnection, removingConnection and connectionError to be implemented.
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public interface ConnectionMonitor {

	public void addConnection(Connection c);

	public void attemptingConnection(Connection c);

	public void connectionError(Connection c, String errMsg);

	public void removeConnection(Connection c);

}
