/**
 * RequestIterator.java 0.4
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
package org.owasp.jbrofuzz.fuzz;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.tcp.Connection;
import org.owasp.jbrofuzz.fuzz.tcp.Generator;
import org.owasp.jbrofuzz.ver.Format;
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
 * @author subere (at) uncon . org
 * @version 0.4
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
   * The main constructor passring a jbrofuzz object, the string, the start
   * location, the finish location and the type of fuzzing.
   * @param mJBroFuzz JBroFuzz
   * @param request String
   * @param start int
   * @param finish int
   * @param type int
   */
  public RequestIterator(JBroFuzz mJBroFuzz, StringBuffer request, int start,
                         int finish, String type) {
    /**
     * @todo Change the way a file while fuzzing gets created, as if you
     * add to generators of the same type, the file data gets appended.
     * Example: two binary generators of the same length will append data to the
     * same file.
     */
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
      maxValue = (long) getJBroFuzz().getConstructor().getGeneratorLength(type);
      // For a recursive generator, generate the corresponding maximum value
      char genType = getJBroFuzz().getConstructor().getGeneratorType(type);
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

      char genType = getJBroFuzz().getConstructor().getGeneratorType(type);
      // Check to see if the generator is recursive
      if (genType == Generator.RECURSIVE) {

        int radix = getJBroFuzz().getConstructor().getGeneratorLength(type);
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
        StringBuffer b = getJBroFuzz().getConstructor().getGeneratorElement(
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

    // Get the counter value to generate unique file-names
    int counter = mJBroFuzz.getFrameWindow().getFuzzingPanel().getFuzzCount();

    if (type.equals("ZER")) {

      String fl = "jbrofuzz-session-" + counter + "-1-1.txt";

      mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(Format.HD1);
      mJBroFuzz.getFileHandler().writeFuzzFile(fl, Format.HD1);

      String r = "Request: " + target + "Port: " + port + "\n" + stout;

      mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(r);
      mJBroFuzz.getFileHandler().writeFuzzFile(fl, r);


      Connection noFuzzingConnection = new Connection(target, port, stout);
      String t = "\nReply:\n" + noFuzzingConnection.getReply();

      mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(t);
      mJBroFuzz.getFileHandler().writeFuzzFile(fl, t);

      mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(Format.FT1);
      mJBroFuzz.getFileHandler().writeFuzzFile(fl, Format.FT1);
    }
    else {

      // Skip the first request
      stout = getNext();
      while ((!("".equals(stout.toString()))) && (!(generatorStopped))) {

        String fk = "jbrofuzz-session-" + counter + "-" + getCurrentRequest() +
                    "-" + getTotalRequest() + ".txt";

        mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(Format.HDS +
          getCurrentRequest() + "-" + getTotalRequest() + Format.FTS);

        mJBroFuzz.getFileHandler().writeFuzzFile(fk,
                                                Format.HDS + getCurrentRequest() +
                                                "-" + getTotalRequest() +
                                                Format.FTS);

        mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText("Request: " +
          target + " Port: " + port + "\n" + stout + "\n");
        mJBroFuzz.getFileHandler().writeFuzzFile(fk,
                                                "Request: " + target + " Port: " +
                                                port + "\n" + stout + "\n");

        Connection con = new Connection(target, port, stout);

        final String s = con.getReply();

        mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(
          "\nReply:\n" + s);
        mJBroFuzz.getFileHandler().writeFuzzFile(fk, "\nReply:\n" + s);

        mJBroFuzz.getFrameWindow().getFuzzingPanel().setOutputText(Format.HDE +
          getCurrentRequest() + "-" + getTotalRequest() + Format.FTS);

        mJBroFuzz.getFileHandler().writeFuzzFile(fk,
                                                Format.HDE + getCurrentRequest() +
                                                "-" + getTotalRequest() +
                                                Format.FTS);

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
   * Return the total number of iterations that the generator will go through.
   * @return String
   */
  public String getTotalRequest() {
    return Long.toString(maxValue, 10);
  }

  /**
   * Return the current iteration that the generator is on.
   * @return String
   */
  public String getCurrentRequest() {
    return Long.toString(currentValue, 10);
  }
  /**
   * Return the main constructor object.
   * @return JBroFuzz
   */
  public JBroFuzz getJBroFuzz() {
    return mJBroFuzz;
  }

  /**
   * <p>Method for altering the length of the Content-Length if the request is
   * a post.</p>
   *
   * @param conMessage StringBuffer
   * @return StringBuffer
   *
  private static StringBuffer processRequest(final StringBuffer conMessage) {
    // Re-write the content length integer if its a POST request (messy)
    if (conMessage.length() >= 4) {
      if (conMessage.substring(0, 4).equals("POST")) {

        final String[] bodyHeader = conMessage.toString().split("\n\n");
        if (bodyHeader.length >= 2) {

          final int lengthHeader = bodyHeader[1].getBytes().length - 1;

          // Find the Content-Length header field
          int indexPoint = conMessage.toString().indexOf("Content-Length:");
          if (indexPoint > -1) {

            indexPoint += "Content-Length".length();
            if (indexPoint < conMessage.length()) {
              final int finalPoint = conMessage.indexOf("\n", indexPoint);
              if (finalPoint > -1) {
                conMessage.delete(indexPoint + 1, finalPoint);
                conMessage.insert(indexPoint + 1, " " + lengthHeader);
              }
            }
          }
        }
      }
    }
    return conMessage;
  }
  */

}
