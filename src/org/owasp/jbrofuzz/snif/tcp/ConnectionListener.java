/**
 * TCPConnectionListener.java 0.6
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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.owasp.jbrofuzz.ui.panels.TCPSniffing;
/**
 * <p>The class responsible for making the connection through the
 * corresponding socket.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class ConnectionListener extends Thread implements ConnectionMonitor {

  private String remoteAddress;
  private int remotePort, localPort;
  private TCPSniffing mn;
  private ServerSocket server = null;
  private Vector connections = null;
  private Connection con = null;
  // Boolean to allow for termination
  private boolean connectionStopped;
  
  /**
   * The main constructor for the Connection Listener.
   *
   * @param mn SniffingPanel
   * @param remoteAddress String
   * @param remotePort String
   * @param localAddress String
   * @param localPort String
   */
  public ConnectionListener(final TCPSniffing mn, final String remoteAddress,
                            final String remotePort, final String localAddress,
                            final String localPort) {
    this.mn = mn;
    this.remoteAddress = remoteAddress;
    // Initial variables
    this.connectionStopped = false;
    try {
      this.remotePort = Integer.parseInt(remotePort);
    }
    catch (final NumberFormatException e1) {
      this.remotePort = 0;
    }
    if ((this.remotePort < 0) || (this.remotePort > 65535)) {
      this.remotePort = 0;
    }
    try {
      this.localPort = Integer.parseInt(localPort);
    }
    catch (final NumberFormatException e1) {
      this.localPort = 0;
    }
    if ((this.localPort < 0) || (this.localPort > 65535)) {
      this.localPort = 0;
    }

    try {
      this.connections = new Vector();
      this.server = new ServerSocket(this.localPort);
      this.server.setReuseAddress(false);
    }
    catch (final IOException e) {
      mn.getFrameWindow().log("ServerSocket IOException..." + e.getMessage());
      mn.getFrameWindow().getTCPSniffingPanel().buttonStop();
    }
  }

  /**
   *
   */
  @Override
	public void run() {

    while (!this.connectionStopped) {
      try {
        final Socket clientSocket = this.server.accept();
        this.con = new Connection(this.mn.getFrameWindow().getJBroFuzz(), clientSocket,
                             this, this.remoteAddress,
                             this.remotePort);
      }
      catch (final IOException ex) {
        this.connectionStopped = true;
        break;
      }
    } // while loop

    try {
      this.server.close();
    }
    catch (final IOException ex1) {
    }
    finally {
      try {
        this.server.close();
      }
      catch (final IOException ex2) {
      }
    }

  }

  public void stopConnection() {
    try {
      this.server.close();
    }
    catch (final Exception e) {
      this.connectionStopped = true;
    }
  }

  public InetAddress getServerAddress() {
    return this.server.getInetAddress();
  }

  public int getServerPort() {
    return this.server.getLocalPort();
  }

  public void attemptingConnection(final Connection c) {
    /*
         mn.setListText(
        getClass().getName() + " " +
        "Connection initiated from: " + c.getSrcHost()
        );
     */
  }

  public void addConnection(final Connection c) {
    /*
         mn.setListText(
        getClass().getName() + " " +
        "Connection established from: " + c.getSrcHost() + ":" +
        this.lPort + "  " +
        "To : " + c.getDestHost() + ":" +
        c.getDestPort()
        );
     */
    synchronized (this.connections) {
      this.connections.addElement(c);
    }
  }

  public void removeConnection(final Connection c) {
    /*
         mn.setListText(
        getClass().getName() + " " +
        "Removing connection from: " + c.getSrcHost() + ":" +
        this.lPort + " " +
        "To : " + c.getDestHost() + ":" +
        c.getDestPort()
        );
     */
    synchronized (this.connections) {
      this.connections.removeElement(c);
    }
  }

  public void connectionError(final Connection c, final String errMsg) {
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
