/**
 * DRequestIterator.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon org
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
package org.owasp.jbrofuzz.dir;

import java.io.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.contrib.ssl.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;
import org.apache.commons.httpclient.protocol.*;
import org.apache.commons.httpclient.util.*;
import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.ver.*;
/**
 * <p>Class for generating the recursive directory requests.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class DRequestIterator {

  // The  frame window that the request iterator
  private FrameWindow m;
  // The original url string
  private String url;
  // The vector of directories to be passed
  private String[] directories;
  // The vector of responses that will come back
  private String[] responses;
  // The boolean to see if the iterator has paused
  private boolean stopped;
  // The integer of the current request count
  private int i;
  // The port on which directory enumeration is taking place
  private int port;

  /**
   * <p>Constructor for creating a web directory request iterator that iterates
   * through the directory listing.</p>
   *
   * @param m FrameWindow
   * @param url String
   * @param directories String
   * @param port int
   */
  public DRequestIterator(FrameWindow m, String url, String directories,
                          String port) {
    this.m = m;
    this.url = url;
    this.port = 0;
    this.directories = directories.split("\n");
    this.responses = new String[directories.length()];
    this.stopped = false;
    i = 0;

    // Check the port
    try {
      this.port = Integer.parseInt(port);
    }
    catch (NumberFormatException e1) {
      this.port = 0;
      m.log("Web Directories Panel: Specify a valid port: \"" + port + "\"");
    }
    if ((this.port < 1) || (this.port > 65535)) {
      this.port = 0;
      m.log("Web Directories Panel: Port has to be between [1 - 65535]");
    }
    // Establish the protocols, if the port is valid
    if (this.port != 0) {

      // For https, allow self-signed certificates
      if (this.url.startsWith("https://")) {
        Protocol easyhttps = new Protocol("https",
                                          new EasySSLProtocolSocketFactory(),
                                          this.port);
        Protocol.registerProtocol("https", easyhttps);
      }
      // For http, just show affection
      if (this.url.startsWith("http://")) {
        Protocol easyhttp = new Protocol("http",
                                         new DefaultProtocolSocketFactory(),
                                         this.port);
        Protocol.registerProtocol("http", easyhttp);
      }
    }
  }

  /**
   * Method used for running the request iterator once it has been initialised.
   */
  public void run() {
    // Check for a valid URL
    if (url.equalsIgnoreCase("")) {
      return;
    }
    if (url.contains(" ")) {
      return;
    }
    if ((port < 1) || (port > 65536)) {
      return;
    }

    for (i = 0; i < directories.length; i++) {
      if (stopped) {
        return;
      }

      String currentURI = "";
      try {
        currentURI = url + URIUtil.encodePath(directories[i]);
      }
      catch (URIException ex) {
        currentURI = "";
        m.log("Could not encode the URI: " + url + directories[i]);
      }

      // Checks...
      if (currentURI.equalsIgnoreCase("")) {
        return;
      }
      if ((port <= 0) || (port > 65535)) {
        return;
      }

      HttpClient client = new HttpClient();
      client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
                                      new Integer(10000));

      GetMethod method = new GetMethod(currentURI);
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                                      new DefaultHttpMethodRetryHandler(1, false));
      method.setFollowRedirects(true);
      method.setDoAuthentication(true);

      try {
        responses[i] = i + "\n";
        responses[i] += currentURI + "\n";
        int statusCode = 0;
        //
        statusCode = client.executeMethod(method);
        //
        responses[i] += statusCode + "\n";
        responses[i] += method.getStatusText() + "\n";

        if (statusCode == HttpStatus.SC_OK) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          InputStream in = method.getResponseBodyAsStream();

          byte[] buff = new byte[65535];
          int got;
          while ((got = in.read(buff)) > -1) {
            baos.write(buff, 0, got);
          }
          byte[] allbytes = baos.toByteArray();

          String results = null;
          try {
            results = new String(allbytes, method.getResponseCharSet());
          }
          catch (UnsupportedEncodingException ex1) {
            m.log("Web Directories: Unsupported Character Encoding");
            results = "";
          }

          // Check for comments
          if (results.contains("<!--")) {
            responses[i] += "Yes\n";
          }
          else {
            responses[i] += "No\n";
          }
          // Check for scripts
          if ((results.contains("<script")) || (results.contains("<SCRIPT"))) {
            responses[i] += "Yes\n";
          }
          else {
            responses[i] += "No\n";
          }
        }
        // If no ok response has come back just append comments and scripts
        else {
          responses[i] += "No\n";
          responses[i] += "No\n";
        }
      }
      catch (HttpException e) {
        responses[i] = i + "\n";
        responses[i] += currentURI + "\n";
        responses[i] += "000" + "\n";
        responses[i] += "Fatal protocol violation" + "\n";
        responses[i] += " \n";
        responses[i] += " \n";
        // Bomb out...
        stop();
      }
      catch (IOException e) {
        responses[i] = i + "\n";
        responses[i] += currentURI + "\n";
        responses[i] += "000" + "\n";
        responses[i] += "Fatal transport error" + "\n";
        responses[i] += " \n";
        responses[i] += " \n";
        e.printStackTrace();
        // Bomb out...
        stop();
      }
      finally {
        method.releaseConnection();
      }
      // Add a row to the displaying table
      m.getWebDirectoriesPanel().addRow(responses[i]);
      // Create a String to be written to file
      StringBuffer outToFile = new StringBuffer();
      outToFile.append(Time.dateAndTime());
      // String outToFile = Time.dateAndTime();
      final String[] tempArray = responses[i].split("\n");
      for (int m = 0; m < tempArray.length; m++) {
        outToFile.append("," + tempArray[m]);
      }
      // Write the file
      FileHandler.writeWebDirFile(m.getWebDirectoriesPanel().getSessionNumber(),
                                  outToFile.toString());
      // Update the progress bar
      final double percentage = 100 * ((double) (i + 1)) /
                                ((double) directories.length);
      m.getWebDirectoriesPanel().setProgressBar((int) percentage);
    }
  }

  /**
   * Stop the Request Iterator, if it currently running.
   */
  public void stop() {
    stopped = true;
  }

  /**
   * Check to see if the current Request Iterator is stopped.
   * @return boolean
   */
  public boolean isStopped() {
    return stopped;
  }
}
