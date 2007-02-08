/**
 * TCPConnectionMonitor.java
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon . org
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
 */
package org.owasp.jbrofuzz.snif.tcp;

/**
 * <p>Title: Java Browser Fuzzer</p>
 *
 * <p>Description: The interface to the TCPConnectionMonitor requiring
 * attemptingConnection, addingConnection, removingConnection and connectionError
 * to be implemented.</p>
 *
 * <p>Company: </p>
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public interface ConnectionMonitor {

  public void addConnection(Connection c);

  public void removeConnection(Connection c);

  public void attemptingConnection(Connection c);

  public void connectionError(Connection c, String errMsg);

}
