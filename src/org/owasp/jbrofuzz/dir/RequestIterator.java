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

import java.util.Vector;

import java.io.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;
import org.owasp.jbrofuzz.ui.FrameWindow;
/**
 * <p>Class for generating the recursive directory requests.</p>
 * @author subere@uncon.org
 * @version 0.5
 */
public class RequestIterator {
  private FrameWindow m;
  // The original url string
  private String url;
  // The vector of directories to be passed
  private String [] directories;
  // The vector of responses that will come back
  private String [] responses;

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
  }
  /**
   * Method used for running the request iterator once it has been initialised.
   */
  public void run() {
    for(int i = 0; i < directories.length; i++) {
      String currentUrl = url + directories[i];

      HttpClient client = new HttpClient();

      GetMethod method = new GetMethod(currentUrl);

      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
        new DefaultHttpMethodRetryHandler(3, false));
      try {
        responses[i] =  i + "\n";
        responses[i] +=  currentUrl + "\n";
        int statusCode = client.executeMethod(method);
        responses[i] += statusCode + "\n";
        responses[i] += method.getStatusText() + "\n";
        responses[i] += "comments?\n";
        responses[i] += "scripts?\n";
/*
 * Still to come...
         if(statusCode != HttpStatus.SC_OK) {
          responses[i] = "Method returned: " + method.getStatusLine();
         }
        else {
          byte[] responseBody = method.getResponseBody();
          responses[i] = new String(responseBody);
        }
*/
      }
      catch(HttpException e) {
        responses[i] =  i + "\n";
        responses[i] +=  currentUrl + "\n";
        responses[i] += "000" + "\n";
        responses[i] += "Fatal protocol violation" + "\n";
        responses[i] += "comments?\n";
        responses[i] += "scripts?\n";
      }
      catch(IOException e) {
        responses[i] =  i + "\n";
        responses[i] +=  currentUrl + "\n";
        responses[i] += "000" + "\n";
        responses[i] += "Fatal transport error" + "\n";
        responses[i] += "comments?\n";
        responses[i] += "scripts?\n";
      }
      finally {
        method.releaseConnection();
      }
      m.getWebDirectoriesPanel().addRow(responses[i]);
    }
  }
}
