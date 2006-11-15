/**
 * Generator.java
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
package jbrofuzz.fuzz;

import java.util.Vector;
import java.math.BigInteger;

import jbrofuzz.*;
import jbrofuzz.fuzz.def.*;

/**
 * <p>Title: Java Bro Fuzzer</p>
 *
 * <p>Description: The generator is responsible for creating the resulting
 * strings from the fuzzing process.</p>
 * <p>The run method also iterates through each string and generates the
 * corresponding connection.</p>
 *
 * @author subere@uncon.org
 * @version 0.1
 */
public class Generator {
  private JBroFuzz jbrofuzz;
  // The request string
  private String request;
  // The location within the string where fuzzing takes place
  private int start, finish, len;
  // The generator value
  private long currentValue, maxValue;
  // The type of generator
  private String type;
  // The boolean checking if stop has been pressed
  private boolean generatorStopped;
  /**
   * The main constructor passring a jbrofuzz object, the string, the start
   * location, the finish location and the type of fuzzing.
   * @param jbrofuzz JBroFuzz
   * @param request String
   * @param start int
   * @param finish int
   * @param type int
   */
  public Generator(JBroFuzz jbrofuzz, String request, int start, int finish,
                   String type) {
    /**
     * @todo Change the way a file while fuzzing gets created, as if you
     * add to generators of the same type, the file data gets appended.
     * Example: to bin generators of the same length.
     */
    this.jbrofuzz = jbrofuzz;
    this.request = request;
    this.type = type;
    this.start = start;
    this.finish = finish;
    // Check start and finish to positive and also within length
    int strlength = request.length();
    if ( (start < 0) || (finish < 0) || (start > strlength) ||
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
      // Pass the base of operation
      if (type.equals("DEC")) {
        maxValue = Long.parseLong("10");
      }
      if (type.equals("HEX")) {
        maxValue = Long.parseLong("16");
      }
      if (type.equals("OCT")) {
        maxValue = Long.parseLong("8");
      }
      if (type.equals("BIN")) {
        maxValue = Long.parseLong("2");
      }
      // Establish the maximum value by performing a power operation
      long baseValue = maxValue;
      for (int i = 1; i < len; i++) {
        maxValue *= baseValue;
      }
      // For the generators that do not require a base
      if (type.equals("BFO")) {
        maxValue = DefBufferOverflows.LENGTH;
      }
      if (type.equals("FSE")) {
        maxValue = DefFormatStringErrors.LENGTH;
      }
      if (type.equals("INT")) {
          maxValue = DefIntegerOverflows.LENGTH;
      }
      if (type.equals("SQL")) {
        maxValue = DefSqlInjections.LENGTH;
      }
      if (type.equals("XSS")) {
        maxValue = DefXSSInjections.LENGTH;
      }
    }
    currentValue = 0;
    generatorStopped = false;
  }

  /**
   * Get next string value. If no such value exists, return an empty string.
   * @return String
   */
  public String getNext() {
    // Check to see if under maxValue, if not return the original request
    if ((currentValue >= maxValue) || (type.equals("ZER"))) {
      return "";
    }
    else {
      // Create the fuzz string
      StringBuffer fuzzedValue = new StringBuffer();
      fuzzedValue.append(request.substring(0, start));

      // Append blanks, paying attention to type
      int blank_count = 0;
      if (type.equals("DEC")) {
        blank_count = Long.toString(currentValue, 10).length() - len;
      }
      if (type.equals("HEX")){
        blank_count = Long.toHexString(currentValue).length() - len;
      }
      if (type.equals("OCT")) {
        blank_count = Long.toOctalString(currentValue).length() - len;
      }
      if (type.equals("BIN")) {
        blank_count = Long.toBinaryString(currentValue).length() - len;
      }

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
        fuzzedValue.append("" +Long.toOctalString(currentValue));
      }
      if (type.equals("BIN")) {
        fuzzedValue.append("" + Long.toBinaryString(currentValue));
      }

      if (type.equals("INT")) {
        DefIntegerOverflows ino = new DefIntegerOverflows();
        int v = (int) currentValue;
        fuzzedValue.append(ino.getINTElement(v));
      }
      if (type.equals("BFO")) {
        DefBufferOverflows bfo = new DefBufferOverflows();
        int v = (int) currentValue;
        fuzzedValue.append(bfo.getBFOElement(v));
      }
      if (type.equals("FSE")) {
        DefFormatStringErrors fse = new DefFormatStringErrors();
        int v = (int) currentValue;
        fuzzedValue.append(fse.getFSElement(v));
      }
      if (type.equals("SQL")) {
        DefSqlInjections sin = new DefSqlInjections();
        int v = (int) currentValue;
        fuzzedValue.append(sin.getSQLElement(v));
      }
      if (type.equals("XSS")) {
        DefXSSInjections xss = new DefXSSInjections();
        int v = (int) currentValue;
        fuzzedValue.append(xss.getXSSElement(v));
      }

      currentValue++;
      // Append the end of the string
      fuzzedValue.append(request.substring(finish));
      return fuzzedValue.toString();
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
    String target = jbrofuzz.getMainWindow().getFuzzingPanel().getTargetText();
    String port = jbrofuzz.getMainWindow().getFuzzingPanel().getPortText();
    String stout = this.request;

    // Get the counter value to generate unique file-names
    int counter = jbrofuzz.getMainWindow().getFuzzingPanel().getFuzzCount();

    if (type.equals("ZER")) {

      String fl = "jbrofuzz-session-" + counter + "-1-1.txt";

      jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText(Format.HD1);
      jbrofuzz.getFileHandler().writeFuzzFile(fl, Format.HD1);

      String r = "Request:\n" + target + "\n" + port + "\n" + stout;

      jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText(r);
      jbrofuzz.getFileHandler().writeFuzzFile(fl, r);

      Connection noFuzzingConnection = new Connection(target, port, stout);
      String t = "Reply:\n" + noFuzzingConnection.getReply();

      jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText(t);
      jbrofuzz.getFileHandler().writeFuzzFile(fl, t);

      jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText(Format.FT1);
      jbrofuzz.getFileHandler().writeFuzzFile(fl, Format.FT1);
    }
    else {
      // Skip the first request
      stout = getNext();
      while ((! (stout.equals(""))) && (! (generatorStopped)) ) {

        String fk = "jbrofuzz-session-" + counter + "-" + getCurrentRequest() + "-" + getTotalRequest() +
            ".txt";

        jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText(Format.HDS +
                                               getCurrentRequest() + "-" +
                                               getTotalRequest() + Format.FTS);

        jbrofuzz.getFileHandler().writeFuzzFile(fk, Format.HDS +
                            getCurrentRequest() + "-" +
                            getTotalRequest() + Format.FTS);

        jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText("Request:\n" + target + "\n" + port +
                                               "\n" + stout);
        jbrofuzz.getFileHandler().writeFuzzFile(fk, "Request:\n" + target + "\n" + port + "\n" + stout);

        Connection con = new Connection(target,
                                        port,
                                        stout);
        String s = con.getReply();
        jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText("Reply:\n" + s);
        jbrofuzz.getFileHandler().writeFuzzFile(fk, "Reply:\n" + s);

        jbrofuzz.getMainWindow().getFuzzingPanel().setOutputText(Format.HDE +
                                               getCurrentRequest() + "-" +
                                               getTotalRequest() + Format.FTS);

        jbrofuzz.getFileHandler().writeFuzzFile(fk, Format.HDE +
                            getCurrentRequest() + "-" +
                            getTotalRequest() + Format.FTS);

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
    return Long.toString(currentValue,10);
  }

}
