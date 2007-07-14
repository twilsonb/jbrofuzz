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
package org.owasp.jbrofuzz.fuzz.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Description: The class responsible for making the connection for the
 * purposes of fuzzing through the corresponding socket.
 *
 * <p>This class gets used to establish each sequencial connection 
 * being made on a given address, port and for a given request.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 * @since 0.1
 */
public class Connection {

  private String reply;
  private StringBuffer conMessage;
  private Socket socket;
  private InputStream in_stream;
  private OutputStream out_stream;
  // The maximum size for the socket I/O
  private final static int SEND_BUF_SIZE = 256 * 1024;
  private final static int RECV_BUF_SIZE = 256 * 1024;
  /**
   * <p>Implement a connection to a particular address, on a given port,
   * specifying the message to be transmitted.</p>
   *
   * @param address String
   * @param port String
   * @param message String
   */
  public Connection(final String address, final String port, final StringBuffer message) {

    final String conAddress = address;
    this.conMessage = message;
    int conPort = 0;
    final byte[] recv = new byte[Connection.RECV_BUF_SIZE];

    // Check the connection port value
    try {
      conPort = Integer.parseInt(port);
    }
    catch (final NumberFormatException e1) {
      conPort = 0;
    }
    if (!((conPort > 0) && (conPort < 65536))) {
      this.reply = "Port has to be within range (0,65536)\n";
      return;
    }
    // Create the Socket to the specified address and port
    try {
      this.socket = new Socket();
      this.socket.bind(null);
      this.socket.connect(new InetSocketAddress(conAddress, conPort), 5000);

      this.socket.setSendBufferSize(Connection.SEND_BUF_SIZE);
      this.socket.setReceiveBufferSize(Connection.RECV_BUF_SIZE);
      this.socket.setSoTimeout(30000);
    }
    catch (final UnknownHostException e) {
      this.reply = "The IP address of the host could not be determined : " +
              e.getMessage() + "\n";
    }
    catch (final IOException e) {
      this.reply = "An IO Error occured when creating the socket : " + e.getMessage() +
              "\n";
    }
    // Assign the input stream
    try {
      this.in_stream = this.socket.getInputStream();
    }
    catch (final IOException e) {
      this.reply = "An IO Error occured when creating the input stream : " +
              e.getMessage() + "\n";
    }
    // Assign the output stream
    try {
      this.out_stream = this.socket.getOutputStream();
    }
    catch (final IOException e) {
      this.reply = "An IO Error occured when creating the output stream : " +
              e.getMessage() + "\n";
    }
    // Write to the output stream
    try {
      this.out_stream.write(this.conMessage.toString().getBytes());
    }
    catch (final IOException e) {
      this.reply =
        "An IO Error occured when attempting to write to the output stream : " +
        e.getMessage() + "\n";
    }
    // Really don't like catching null pointer exceptions...
    catch (final NullPointerException e) {
      this.reply = "The output stream is null : " + e.getMessage();
      return;
    }
    try {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int got;
      while ((got = this.in_stream.read(recv)) > -1) {
        baos.write(recv, 0, got);
      }
      final byte[] allbytes = baos.toByteArray();

      this.reply = new String(allbytes);
    }
    catch (final IOException e) {

    }
    // Close the socket
    try {
      this.socket.close();
    }
    catch (final IOException e) {
      this.reply = "An IO Error occured when attempting to close the socket : " +
              e.getMessage() + "\n";
    }
  }

  /**
   * <p>Return the reply from the Connection that has been made, based on the
   * message that has been transmitted during construction.</p>
   *
   * @return String
   */
  public String getReply() {
    return this.reply;
  }

  /**
   * <p>Return the message request sent on the Socket.</p>
   * @return StringBuffer
   */
  public StringBuffer getMessage() {
    return this.conMessage;
  }
}
