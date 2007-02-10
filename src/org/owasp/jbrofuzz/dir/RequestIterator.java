/**
 * RequestIterator.java 0.5
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
import java.net.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.util.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;
import org.owasp.jbrofuzz.ui.*;
/**
 * <p>Class for generating the recursive directory requests.</p>
 *
 * @author subere@uncon.org
 * @version 0.5
 */
public class RequestIterator {
  // The  frame window that the request iterator
  private FrameWindow m;
  // The original url string
  private String url;
  // The vector of directories to be passed
  private String[] directories;
  // The vector of responses that will come back
  private String[] responses;
  // The boolean to see if the iterator has paused
  private boolean paused, stopped;
  // The integer of the current request count
  private int i;

  /**
   * <p>Constructor for creating a web directory request iterator that iterates
   * through the directory listing.</p>
   *
   * @param m FrameWindow
   * @param url String
   * @param directories String
   */
  public RequestIterator(FrameWindow m, String url, String directories) {
    this.m = m;
    this.url = url;
    this.directories = directories.split("\n");
    this.responses = new String[directories.length()];
    this.stopped = false;
    i = 0;
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
        currentURI = url + URIUtil.encodeWithinAuthority(directories[i]);
      }
      catch (URIException ex) {
        currentURI = "";
        m.log("Could not encode the URI: " + url + directories[i]);
      }

      if (currentURI.equalsIgnoreCase("")) {
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
        responses[i] += "N\n";
        responses[i] += "N\n";

        /**
         * @todo How does web scarab check for comments and scripts?
         */
        if (statusCode == HttpStatus.SC_OK) {
          byte[] responseBody = method.getResponseBody();
          String s  = new String(responseBody);
          System.out.println(s);
        }
      }
      catch (HttpException e) {
        responses[i] = i + "\n";
        responses[i] += currentURI + "\n";
        responses[i] += "000" + "\n";
        responses[i] += "Fatal protocol violation" + "\n";
        responses[i] += "N\n";
        responses[i] += "N\n";
      }
      catch (IOException e) {
        responses[i] = i + "\n";
        responses[i] += currentURI + "\n";
        responses[i] += "000" + "\n";
        responses[i] += "Fatal transport error" + "\n";
        responses[i] += "N\n";
        responses[i] += "N\n";
      }
      finally {
        method.releaseConnection();
      }
      m.getWebDirectoriesPanel().addRow(responses[i]);
    }
  }

  public void stop() {
    stopped = true;
  }

  public void pause() {
    paused = true;
  }

  public boolean isStopped() {
    return stopped;
  }

  public boolean isPaused() {
    return paused;
  }
}
