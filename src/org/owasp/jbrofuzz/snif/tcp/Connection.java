/**
 * Connection.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.owasp.jbrofuzz.JBroFuzz;
/**
 * <p>The class responsible for launching a proxy between the two
 * Sockets. This class uses the Agent class in order to achieve
 * this.</p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
class Connection implements Runnable, AgentMonitor {

  private int destPort = -1;
  private Socket srcSocket = null;
  private Socket destSocket = null;
  private InputStream srcIn = null;
  private OutputStream srcOut = null;
  private OutputStream destOut = null;
  private ConnectionMonitor cm = null;
  private String destHost = null;
  private InputStream destIn = null;
  private boolean connectionClosed = false;
  private Agent fromSrcToDest = null;
  private Agent fromDestToSrc = null;

  //
  private JBroFuzz mJBroFuzz;

  public Connection(final JBroFuzz mJBroFuzz, final Socket s, final ConnectionMonitor cm,
                    final String destHost, final int destPort) {

    this.mJBroFuzz = mJBroFuzz;
    this.srcSocket = s;
    this.cm = cm;
    this.destHost = destHost;
    this.destPort = destPort;

    cm.attemptingConnection(this);

    try {
      // Establish read/write for the socket

      this.srcIn = s.getInputStream();
      this.srcOut = s.getOutputStream();

      // Start ourself, so there's no delay in getting back
      // to the server to listen for new connections

      final Thread t = new Thread(this);
      t.start();
    }
    catch (final IOException e) {
      cm.connectionError(this, "" + e);
    }
  }

  public void run() {
    if (!this.connectToDest()) {
      this.closeSrc();
    }
    else {
      // Ok, we're all ready ... since we've gotten this far,
      // add ourselves into the connection list

      this.cm.addConnection(this);

      // Create our two agents
      this.fromSrcToDest = new Agent(this.mJBroFuzz, this.srcIn, this.destOut, this,
                                "{Local Host => Remote Host}");
      this.fromDestToSrc = new Agent(this.mJBroFuzz, this.destIn, this.srcOut, this,
                                "{Remote Host => Local Host}");

      // No need for our thread to continue, we'll be notified if
      // either of our agents dies
    }
  }

  public synchronized void agentHasDied(final Agent a) {
    // When one agent dies, so will the other ... if the
    // connection is already closed then we have already been
    // visited by the first agent ... just return

    if (this.connectionClosed) {
      return;
    }
    this.closeSrc();
    this.closeDest();

    this.cm.removeConnection(this);
    this.connectionClosed = true;
  }

  private boolean connectToDest() {
    // Ok, we've got the host name and port to which we wish to
    // connect, try to establish a connection

    try {
      this.destSocket = new Socket(this.destHost, this.destPort);
      this.destIn = this.destSocket.getInputStream();
      this.destOut = this.destSocket.getOutputStream();
    }
    catch (final UnknownHostException ex) {
      this.cm.connectionError(this,
                         "connect error: " + this.destHost + "/" + this.destPort + " " +
                         ex);
      return false;

    }
    catch (final IOException ex) {
      this.cm.connectionError(this,
                         "connect error: " + this.destHost + "/" + this.destPort + " " +
                         ex);
      return false;

    }
    return true;

  }

  private void closeSrc() {

    try {
      this.srcIn.close();
      this.srcOut.close();
      this.srcSocket.close();
    }
    catch (final IOException ex) {
    }

  }

  private void closeDest() {
    try {
      this.destIn.close();
      this.destOut.close();
      this.destSocket.close();
    }
    catch (final IOException ex) {
    }
  }

  public String getSrcHost() {
    return this.srcSocket.getInetAddress().toString();
  }

  public String getDestHost() {
    return this.destSocket.getInetAddress().toString();
  }

  public int getDestPort() {
    return this.destPort;
  }
}
