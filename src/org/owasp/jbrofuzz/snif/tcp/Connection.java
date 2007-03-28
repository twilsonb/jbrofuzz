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

import java.io.*;
import java.net.*;

import org.owasp.jbrofuzz.*;
/**
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

  public Connection(JBroFuzz mJBroFuzz, Socket s, ConnectionMonitor cm,
                    String destHost, int destPort) {

    this.mJBroFuzz = mJBroFuzz;
    srcSocket = s;
    this.cm = cm;
    this.destHost = destHost;
    this.destPort = destPort;

    cm.attemptingConnection(this);

    try {
      // Establish read/write for the socket

      srcIn = s.getInputStream();
      srcOut = s.getOutputStream();

      // Start ourself, so there's no delay in getting back
      // to the server to listen for new connections

      Thread t = new Thread(this);
      t.start();
    }
    catch (IOException e) {
      cm.connectionError(this, "" + e);
    }
  }

  public void run() {
    if (!connectToDest()) {
      closeSrc();
    }
    else {
      // Ok, we're all ready ... since we've gotten this far,
      // add ourselves into the connection list

      cm.addConnection(this);

      // Create our two agents
      fromSrcToDest = new Agent(this.mJBroFuzz, srcIn, destOut, this,
                                "{Local Host => Remote Host}");
      fromDestToSrc = new Agent(this.mJBroFuzz, destIn, srcOut, this,
                                "{Remote Host => Local Host}");

      // No need for our thread to continue, we'll be notified if
      // either of our agents dies
    }
  }

  public synchronized void agentHasDied(Agent a) {
    // When one agent dies, so will the other ... if the
    // connection is already closed then we have already been
    // visited by the first agent ... just return

    if (connectionClosed) {
      return;
    }
    closeSrc();
    closeDest();

    cm.removeConnection(this);
    connectionClosed = true;
  }

  private boolean connectToDest() {
    // Ok, we've got the host name and port to which we wish to
    // connect, try to establish a connection

    try {
      destSocket = new Socket(destHost, destPort);
      destIn = destSocket.getInputStream();
      destOut = destSocket.getOutputStream();
    }
    catch (UnknownHostException ex) {
      cm.connectionError(this,
                         "connect error: " + destHost + "/" + destPort + " " +
                         ex);
      return false;

    }
    catch (IOException ex) {
      cm.connectionError(this,
                         "connect error: " + destHost + "/" + destPort + " " +
                         ex);
      return false;

    }
    return true;

  }

  private void closeSrc() {

    try {
      srcIn.close();
      srcOut.close();
      srcSocket.close();
    }
    catch (IOException ex) {
    }

  }

  private void closeDest() {
    try {
      destIn.close();
      destOut.close();
      destSocket.close();
    }
    catch (IOException ex) {
    }
  }

  public String getSrcHost() {
    return srcSocket.getInetAddress().toString();
  }

  public String getDestHost() {
    return destSocket.getInetAddress().toString();
  }

  public int getDestPort() {
    return destPort;
  }
}
