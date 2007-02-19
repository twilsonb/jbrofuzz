/**
 * DRequestIterator.java 0.5
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

import java.nio.charset.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.util.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;

import org.apache.commons.httpclient.contrib.ssl.*;
import org.apache.commons.httpclient.protocol.*;

import org.owasp.jbrofuzz.ui.*;
import org.owasp.jbrofuzz.io.*;
import org.owasp.jbrofuzz.ver.*;
/**
 * <p>Class for generating the recursive directory requests.</p>
 *
 * @author subere@uncon.org
 * @version 0.5
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
                         int port) {
    this.m = m;
    this.url = url;
    this.directories = directories.split("\n");
    this.port = port;
    this.responses = new String[directories.length()];
    this.stopped = false;
    i = 0;

    // Allow for self-signed certificates by
    Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
    Protocol.registerProtocol("https", easyhttps);
  }

  /**
   * Method used for running the request iterator once it has been initialised.
   */
  public void run() {
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
      GetMethod method = new GetMethod(currentURI);
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                                      new DefaultHttpMethodRetryHandler(3, false));
      try {
        responses[i] = i + "\n";
        responses[i] += currentURI + "\n";
        int statusCode = client.executeMethod(method);
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
          /**
           * @todo Get the Charset encoding from the header
           *
          String encoding1 = method.getRequestCharSet();
          String encoding2 = method.getResponseCharSet();
           */
          String results = new String(allbytes, Charset.forName("ISO-8859-1"));

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
      }
      catch (IOException e) {
        responses[i] = i + "\n";
        responses[i] += currentURI + "\n";
        responses[i] += "000" + "\n";
        responses[i] += "Fatal transport error" + "\n";
        responses[i] += " \n";
        responses[i] += " \n";
        e.printStackTrace() ;
      }
      finally {
        method.releaseConnection();
      }
      // Add a row to the displaying table
      m.getWebDirectoriesPanel().addRow(responses[i]);
      // Create a String to be written to file
      String outToFile = Time.dateAndTime();
      String [] tempArray = responses[i].split("\n");
      for(int m = 0; m < tempArray.length; m++) {
        outToFile += "," + tempArray[m];
      }
      // Write the file
      FileHandler.writeWebDirFile(m.getWebDirectoriesPanel().getSessionNumber(),
                                  outToFile);
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
