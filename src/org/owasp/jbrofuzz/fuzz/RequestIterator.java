/**
 * RequestIterator.java 0.6
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
package org.owasp.jbrofuzz.fuzz;

import org.owasp.jbrofuzz.*;
import org.owasp.jbrofuzz.fuzz.tcp.*;
import org.owasp.jbrofuzz.io.*;
/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>The request generator instantiates the correct generator,
 * holding the complete set of requests. This class runs through all the
 * requests that a generator has to make holding the complete request being
 * made.</p>
 *
 * <p>For each generator value, through the use of the run method, a socket
 * gets created according through a Connection object that gets created.</p>
 *
 * <p>Effectively, a request generator generates connections when run by means
 * of establishing sockets though the Connection class.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class RequestIterator {

  private JBroFuzz mJBroFuzz;
  // The request being made on the socket
  private StringBuffer request;
  // The location within the request where fuzzing takes place
  private int start, finish, len;
  /*
   * As the generator generates fuzzing request, two variables holding the
   * currentValue of the generator and the maximum value of the generator.
   */
  private long currentValue, maxValue;
  // The type of generator
  private String type;
  // The boolean checking if stop has been pressed
  private boolean generatorStopped;
  /**
   * The main constructor passing a jbrofuzz object, the string to be sent,
   * the start location wihtin that String were the fuzzing point begins from
   * and the finish location and the type of fuzzing.
   * @param mJBroFuzz JBroFuzz
   * @param request String
   * @param start int
   * @param finish int
   * @param type int
   */
  public RequestIterator(JBroFuzz mJBroFuzz, StringBuffer request, int start,
                         int finish, String type) {

    this.mJBroFuzz = mJBroFuzz;
    this.request = request;
    this.type = type;
    this.start = start;
    this.finish = finish;
    // Check start and finish to positive and also within length
    int strlength = request.length();
    if ((start < 0) || (finish < 0) || (start > strlength) ||
        (finish > strlength)) {
      this.len = 0;
    }
    else {
      this.len = finish - start;
    }
    // Check for a minimum length of 1 between start and finish
    if (len < 1) {
      maxValue = 0;
    }
    else {
      maxValue = 0;
      maxValue = (long) getJBroFuzz().getTCPConstructor().getGeneratorLength(
        type);
      // For a recursive generator, generate the corresponding maximum value
      char genType = getJBroFuzz().getTCPConstructor().getGeneratorType(type);
      if (genType == Generator.RECURSIVE) {
        long baseValue = maxValue;
        for (int i = 1; i < len; i++) {
          maxValue *= baseValue;
        }
      }
    }
    currentValue = 0;
    generatorStopped = false;
  }

  /**
   * Get next string value. If no such value exists, return an empty
   * StringBuffer.
   * @return StringBuffer
   */
  public StringBuffer getNext() {
    // Check to see if under maxValue, if not return the original request
    if ((currentValue >= maxValue) || (type.equals("ZER"))) {
      return new StringBuffer("");
    }
    else {
      // Create the fuzz string
      StringBuffer fuzzedValue = new StringBuffer();
      fuzzedValue.append(request.substring(0, start));

      // Append blanks, if generator is recursive
      int blank_count = 0;

      char genType = getJBroFuzz().getTCPConstructor().getGeneratorType(type);
      // Check to see if the generator is recursive
      if (genType == Generator.RECURSIVE) {

        int radix = getJBroFuzz().getTCPConstructor().getGeneratorLength(type);
        blank_count = Long.toString(currentValue, radix).length() - len;
        while (blank_count < 0) {
          fuzzedValue.append("0");
          blank_count++;
        }

        // Append the current value, depending on type
        if (type.equals("DEC")) {
          fuzzedValue.append("" + Long.toString(currentValue, 10));
        }
        if (type.equals("HEX")) {
          fuzzedValue.append("" + Long.toHexString(currentValue));
        }
        if (type.equals("OCT")) {
          fuzzedValue.append("" + Long.toOctalString(currentValue));
        }
        if (type.equals("BIN")) {
          fuzzedValue.append("" + Long.toBinaryString(currentValue));
        }
      }
      // Check to see if the generator is replasive
      if (genType == Generator.REPLASIVE) {
        int v = (int) currentValue;
        StringBuffer b = getJBroFuzz().getTCPConstructor().getGeneratorElement(
          type, v);
        fuzzedValue.append(b);
      }

      currentValue++;
      // Append the end of the string
      fuzzedValue.append(request.substring(finish));
      return fuzzedValue;
    }
  }

  /**
   * <p>Method responsible for doing all the work. This method runs through a
   * number of instances creating a connection and getting the reply for each
   * one.</p>
   * <p>While looping through the total number of connections, it checks to see
   * also if the fuzzing has been stopped by the user. This is checked through
   * the generatorStopped boolean that can be accesses from the main GUI.</p>
   *
   */
  public void run() {
    String target = mJBroFuzz.getFrameWindow().getFuzzingPanel().getTargetText();
    String port = mJBroFuzz.getFrameWindow().getFuzzingPanel().getPortText();
    StringBuffer stout = this.request;

    // If a single request has been specified...
    if (type.equals("ZER")) {

      final String filename = mJBroFuzz.getFrameWindow().getFuzzingPanel().
                              getCounter(false);
      final String header = "(1 of 1)--[" + filename + "]----\n" + "Request: " + target + " Port: " + port + "\n" + stout;
      final String footer = "\n-----JBroFuzz------End----(1 of 1)--[" +
        filename + "]----\n";

      mJBroFuzz.getFrameWindow().getFuzzingPanel().addRowInOuputTable(header);
      FileHandler.writeFuzzFile(header, filename);

      Connection noFuzzingConnection = new Connection(target, port, stout);
      String t = "\nReply:\r\n" + noFuzzingConnection.getReply();

      // mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(t);
      FileHandler.writeFuzzFile(t, filename);

      // mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(footer);
      FileHandler.writeFuzzFile(footer,
        mJBroFuzz.getFrameWindow().getFuzzingPanel().getCounter(true));
    }
    // If multiple requests have been specified...
    else {
      // Skip the first request
      stout = getNext();
      while ((!("".equals(stout.toString()))) && (!(generatorStopped))) {

        String filename = mJBroFuzz.getFrameWindow().getFuzzingPanel().
                          getCounter(false);

        mJBroFuzz.getFrameWindow().getFuzzingPanel().addRowInOuputTable(
          "--(" + currentValue + "-" + maxValue + ")--[" + filename + "]");

        FileHandler.writeFuzzFile("\n-----JBroFuzz------Start--(" +
          currentValue + "-" + maxValue + ")--[" + filename + "]----\n",
          filename);
/*
        mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText("Request: " +
          target + " Port: " + port + "\n" + stout + "\n");
*/
        FileHandler.writeFuzzFile("Request: " + target + " Port: " + port +
          "\r\n" + stout + "\n", filename);

        Connection con = new Connection(target, port, stout);

        final String s = con.getReply();
/*
        mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText("\nReply:\n" +
          s);
*/
        FileHandler.writeFuzzFile("\nReply:\n" + s, filename);
/*
        mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(
          "\n-----JBroFuzz------End----(" + currentValue + "-" + maxValue +
          ")--[" + filename + "]----\n");
*/

        FileHandler.writeFuzzFile("\n-----JBroFuzz------End----(" +
          currentValue + "-" + maxValue + ")--[" + filename + "]----\n",
          mJBroFuzz.getFrameWindow().getFuzzingPanel().getCounter(true));

        stout = getNext();
      }
    }
  }

  /**
   * <p>Stop the generator. This operation is achieved by setting the boolean
   * being checked in the loop of getNext to true. As a result, the current
   * fuzzing request completes and no more requests are executed.</p>
   */
  public void stop() {
    generatorStopped = true;
  }

  /**
   * Return the main constructor object.
   * @return JBroFuzz
   */
  public JBroFuzz getJBroFuzz() {
    return mJBroFuzz;
  }
}
