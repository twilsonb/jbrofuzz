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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.owasp.jbrofuzz.ui.panels.SniffingPanel;

/**
 * <p>
 * The class responsible for making the connection through the corresponding
 * socket.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class ConnectionListener extends Thread implements ConnectionMonitor {

	private String remoteAddress;
	private int remotePort, localPort;
	private SniffingPanel mn;
	private ServerSocket server = null;
	private Vector<Connection> connections = null;
	// Boolean to allow for termination
	private boolean connectionStopped;

	/**
	 * The main constructor for the Connection Listener.
	 * 
	 * @param mn
	 *          SniffingPanel
	 * @param remoteAddress
	 *          String
	 * @param remotePort
	 *          String
	 * @param localAddress
	 *          String
	 * @param localPort
	 *          String
	 */
	public ConnectionListener(final SniffingPanel mn, final String remoteAddress,
			final String remotePort, final String localAddress, final String localPort) {
		this.mn = mn;
		this.remoteAddress = remoteAddress;
		// Initial variables
		connectionStopped = false;
		try {
			this.remotePort = Integer.parseInt(remotePort);
		} catch (final NumberFormatException e1) {
			this.remotePort = 0;
		}
		if ((this.remotePort < 0) || (this.remotePort > 65535)) {
			this.remotePort = 0;
		}
		try {
			this.localPort = Integer.parseInt(localPort);
		} catch (final NumberFormatException e1) {
			this.localPort = 0;
		}
		if ((this.localPort < 0) || (this.localPort > 65535)) {
			this.localPort = 0;
		}

		try {
			connections = new Vector<Connection>();
			server = new ServerSocket(this.localPort);
			server.setReuseAddress(false);
		} catch (final IOException e) {
			mn.getFrame().log("ServerSocket IOException..." + e.getMessage());
			mn.getFrame().getPanelSniffing().stop();
		}
	}

	public void addConnection(final Connection c) {
		/*
		 * mn.setListText( getClass().getName() + " " + "Connection established
		 * from: " + c.getSrcHost() + ":" + this.lPort + " " + "To : " +
		 * c.getDestHost() + ":" + c.getDestPort() );
		 */
		synchronized (connections) {
			connections.addElement(c);
		}
	}

	public void attemptingConnection(final Connection c) {
		/*
		 * mn.setListText( getClass().getName() + " " + "Connection initiated from: " +
		 * c.getSrcHost() );
		 */
	}

	public void connectionError(final Connection c, final String errMsg) {
		/*
		 * mn.setListText( getClass().getName() + " " + "Error involving connection
		 * from:" + c.getSrcHost() + ":" + this.lPort + " " + errMsg );
		 */
	}

	public InetAddress getServerAddress() {
		return server.getInetAddress();
	}

	public int getServerPort() {
		return server.getLocalPort();
	}

	public void removeConnection(final Connection c) {
		/*
		 * mn.setListText( getClass().getName() + " " + "Removing connection from: " +
		 * c.getSrcHost() + ":" + this.lPort + " " + "To : " + c.getDestHost() + ":" +
		 * c.getDestPort() );
		 */
		synchronized (connections) {
			connections.removeElement(c);
		}
	}

	/**
	 * 
	 */
	@Override
	public void run() {

		while (!connectionStopped) {
			try {
				final Socket clientSocket = server.accept();
				new Connection(mn.getFrame().getJBroFuzz(),
						clientSocket, this, remoteAddress, remotePort);
			} catch (final IOException ex) {
				connectionStopped = true;
				break;
			}
		} // while loop

		try {
			server.close();
		} 
		catch (final IOException ex1) {
		} 
		finally {
			try {
				if(server != null) {
					server.close();
				}
			} 
			catch (final IOException ex2) {
			}
		}

	}

	public void stopConnection() {
		try {
			server.close();
		} catch (final Exception e) {
			connectionStopped = true;
		}
	}
}
