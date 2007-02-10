/*
 * TCPAgent.java
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

import java.io.*;
import java.text.*;
import java.util.*;

import org.owasp.jbrofuzz.*;

/**
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
class Agent implements Runnable {

  private InputStream inStream = null;
  private OutputStream outStream = null;
  private AgentMonitor agentMonitor = null;
  private byte buffer[] = null;
  private static final int BUFFER_SIZE = 65534;
  private String name = null;
  // The jbrofuzz object attached to this agent
  private JBroFuzz mJBroFuzz;

  // Format the current time in a nice iso 8601 format.
  private static SimpleDateFormat dateFormat = new SimpleDateFormat(
    "yyyy-MM-dd HH-mm-ss-SSS");

  public Agent(JBroFuzz mJBroFuzz, InputStream inStream, OutputStream outStream,
               AgentMonitor agentMonitor, String name) {

    this.mJBroFuzz = mJBroFuzz;

    this.name = name;
    this.outStream = outStream;
    this.inStream = inStream;
    this.agentMonitor = agentMonitor;

    buffer = new byte[BUFFER_SIZE];

    Thread t = new Thread(this);
    t.start();
  }

  public void run() {
    try {
      int bytesRead = 0;

      while (true) {
        // While there are bytes in the input stream, read them
        if ((bytesRead = inStream.read(buffer, 0, BUFFER_SIZE)) == -1) {
          break;
        }
        // Log the incoming/outgoing packets
        log(buffer, bytesRead);
        // Write out to the output stream
        outStream.write(buffer, 0, bytesRead);
      }
    }
    catch (IOException e) {
      mJBroFuzz.getFrameWindow().log("TCPAgent: " + e.getMessage());
    }
    //
    agentMonitor.agentHasDied(this);
  }

  private void log(final byte buffer[], final int nBytes) {
    synchronized (System.out) {
      final Date currentTime = new Date();
      final String fileName = mJBroFuzz.getFrameWindow().getTCPSniffingPanel().
                              getCounter();

      mJBroFuzz.getFileHandler().writeSnifFile(fileName + ".txt",
                                               "[" + name + ", (" + nBytes +
                                               " bytes) " +
                                               dateFormat.format(currentTime) +
                                               "]");

      final StringBuffer row = new StringBuffer(100);
      row.append(fileName);
      row.append("          ");
      row.append(name);
      if (nBytes < 100000) {
        row.append(' ');
      }
      if (nBytes < 10000) {
        row.append(' ');
      }
      if (nBytes < 1000) {
        row.append(' ');
      }
      if (nBytes < 100) {
        row.append(' ');
      }
      if (nBytes < 10) {
        row.append(' ');
      }
      row.append("          (");
      row.append(nBytes);
      row.append(" bytes)          ");
      row.append(dateFormat.format(currentTime));
      // Append a row in the table
      mJBroFuzz.getFrameWindow().getTCPSniffingPanel().addRow(row.toString());
      // formatted string
      StringBuffer sb = new StringBuffer(nBytes);
      // formatted binary string
      StringBuffer pb = new StringBuffer(4 * nBytes);
      // hex buffer string
      StringBuffer hb = new StringBuffer();
      // text buffer string
      StringBuffer tb = new StringBuffer();

      // line position counter
      int line_counter = 0;
      // byte counter
      int byte_counter = 0;
      // binary character counter
      float bin_counter = 0;

      for (int i = 0; i < nBytes; i++) {
        // 32 bit Unicode to 16 bit ASCII
        final int value = (buffer[i] & 0xFF);
        if (value == '\r' || value == '\n' || value == '\t' ||
            (value >= ' ' && value <= '~')) {
          sb.append((char) value); // text character
        }
        else {
          sb.append("[");
          sb.append(hexPad(Integer.toHexString(value), 2));
          sb.append("]");
          bin_counter++;
        }

        if (value >= ' ' && value <= '~') {
          tb.append((char) value); // "printable" character
        }
        else {
          tb.append('.'); // non-printable
        }

        hb.append(hexPad(Integer.toHexString(value), 2));
        if (line_counter == 3 || line_counter == 7 || line_counter == 11) { // for readability, space every 4 chars
          hb.append(' ');
          tb.append(' ');
        }

        if (line_counter == 15) { // 16 characters per line
          pb.append(hexPad(Integer.toHexString(byte_counter), 4));
          pb.append(":  ");
          pb.append(hb);
          pb.append("    ");
          pb.append(tb);
          pb.append("\n");
          line_counter = 0;
          byte_counter += 16;

          // hb = new StringBuffer();
          hb.setLength(0);
          // tb = new StringBuffer();
          tb.setLength(0);
        }
        else {
          line_counter++;
        }
      }
      for (int i = hb.length(); i < 35; i++) {
        hb.append(' '); // pad to length of other lines
      }
      pb.append(hexPad(Integer.toHexString(byte_counter), 4));
      pb.append(":  ");
      pb.append(hb);
      pb.append("    ");
      pb.append(tb);
      pb.append("\n");
      // If less than 5% binary?
      if ((bin_counter / nBytes) < .05) {
        mJBroFuzz.getFileHandler().writeSnifFile(fileName + ".txt", sb.toString());
      }
      else {
        mJBroFuzz.getFileHandler().writeSnifFile(fileName + ".txt", pb.toString());
      }
    }
  }

  /**
   * <p>Method to pre-append zeros to a hexadecimal string.</p>
   * @param input String
   * @param length int
   * @return String
   */
  private static String hexPad(final String input, final int length) {
    final StringBuffer buffer = new StringBuffer(length);
    buffer.append(input);
    for (int j = input.length(); j < length; j++) {
      buffer.append('0');
    }
    return buffer.toString();
  }
}
