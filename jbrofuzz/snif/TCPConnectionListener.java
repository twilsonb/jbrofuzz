/**
 * TCPConnectionListener.java
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2006 yns000 (at) users. sourceforge. net
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
package jbrofuzz.snif;

import java.io.*;
import java.net.*;
import java.util.*;

import jbrofuzz.*;
import jbrofuzz.ui.*;
/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>Description: The class responsible for making the connection through the
 * corresponding socket.</p>
 *
 * @author subere@uncon.org
 * @version 0.3
 */
public class TCPConnectionListener extends Thread implements TCPConnectionMonitor  {

  private String remoteAddress, localAddress;
  private int remotePort, localPort;
  private MainSniffingPanel mn;
  private ServerSocket server = null;
  private Vector connections = null;
  private TCPConnection con = null;
  // Boolean to allow for termination
  private boolean connectionStopped;

  public TCPConnectionListener(MainSniffingPanel mn,
                               String remoteAddress, String remotePort,
                               String localAddress, String localPort) {
    this.mn = mn;
    this.remoteAddress = remoteAddress;
    this.localAddress = localAddress;
    // Initial variables
    connectionStopped = false;
    /**
     * @todo: Check values being parsed to the interface in terms of address, port
     */
    try {
      this.remotePort = Integer.parseInt(remotePort);
    }
    catch (NumberFormatException e1) {
      this.remotePort = 0;
    }
    try {
      this.localPort = Integer.parseInt(localPort);
    }
    catch (NumberFormatException e1) {
      this.localPort = 0;
    }

    try {
      connections = new Vector();
      server = new ServerSocket(this.localPort);
      server.setReuseAddress(false);
    }
    catch (IOException e) {
      System.out.println("ServerSocket IOException..." + e.getMessage());
    }
  }

  public void run() {

    while(!connectionStopped) {
      try {
        Socket clientSocket = server.accept();
        con = new TCPConnection(mn.getMainWindow().getJBroFuzz(),
                                clientSocket, (TCPConnectionMonitor)this,
                                this.remoteAddress, this.remotePort);
      }
      catch (Exception e) {
        connectionStopped = true;
        break;
      }
    }
    try {
      server.close();
    }
    catch(Exception e) {
      //
    }
    finally {
      try {
        server.close();
      }
      catch(Exception e) {
        //
      }
    }

  }

  public void stopConnection() {
      try {
        server.close();
      }
      catch (Exception e) {
        connectionStopped = true;
      }
  }

  public InetAddress getServerAddress() {
    return server.getInetAddress();
  }

  public int getServerPort() {
    return server.getLocalPort();
  }

  public void attemptingConnection(TCPConnection c) {
    /*
    mn.setListText(
        getClass().getName() + " " +
        "Connection initiated from: " + c.getSrcHost()
        );
    */
  }

  public void addConnection(TCPConnection c) {
    /*
    mn.setListText(
        getClass().getName() + " " +
        "Connection established from: " + c.getSrcHost() + ":" +
        this.lPort + "  " +
        "To : " + c.getDestHost() + ":" +
        c.getDestPort()
        );
    */
    synchronized (connections) {
      connections.addElement(c);
    }
  }

  public void removeConnection(TCPConnection c) {
    /*
    mn.setListText(
        getClass().getName() + " " +
        "Removing connection from: " + c.getSrcHost() + ":" +
        this.lPort + " " +
        "To : " + c.getDestHost() + ":" +
        c.getDestPort()
        );
    */
    synchronized (connections) {
      connections.removeElement(c);
    }
  }

  public void connectionError(TCPConnection c, String errMsg) {
    /*
    mn.setListText(
        getClass().getName() + " " +
        "Error involving connection from:" + c.getSrcHost() + ":" +
        this.lPort + " " +
        errMsg
        );
    */
  }
}

