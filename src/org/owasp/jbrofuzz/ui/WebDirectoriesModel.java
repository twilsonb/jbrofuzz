/**
 * WebDirectoriesModel.java 0.6
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
package org.owasp.jbrofuzz.ui;

import java.util.*;

import javax.swing.table.*;
/**
 * <p>The Table Model for the Web Directories return table.</p>
 * <p>This table model is used for replies for the web directories.</p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class WebDirectoriesModel extends AbstractTableModel {

  private static final long serialVersionUID = 185093L;
  /**
   * <p>The String used to separate columns when a toString representation of
   * a set number of columns or rows is required. This is typically used in
   * method getRow() that returns a String.</p>
   */
  public static final String STRING_SEPARATOR = ",";

  // The names of the columns within the table of generators
  private static final String[] COLUMNNAMES = {
                                              "No", "URI", "Code",
                                              "Status Text", "Comments",
                                              "Scripts"};
  // The vector of ResponseOutputs
  private Vector dataVector;

  /**
   * Default constructor for this web directory model. This is to be attached
   * to a JTable.
   */
  public WebDirectoriesModel() {
    dataVector = new Vector();
  }

  /**
   * Method for obtaining the String name of a given column.
   *
   * @param column int
   * @return String
   */
  public String getColumnName(int column) {
    String out = "";
    if ((column > -1) && (column < COLUMNNAMES.length)) {
      out = COLUMNNAMES[column];
    }
    return out;
  }

  /**
   * Method for returning a particular value within the existing table model.
   *
   * @param row int
   * @param column int
   * @return Object
   */
  public Object getValueAt(int row, int column) {
    if ((row < 0) || (row > dataVector.size() - 1)) {
      return "";
    }
    else {
      ResponseOutput record = (ResponseOutput) dataVector.get(row);
      switch (column) {
        case 0:
          return record.getID();
        case 1:
          return record.getURI();
        case 2:
          return record.getStatusCode();
        case 3:
          return record.getStatusText();
        case 4:
          return record.getComments();
        case 5:
          return record.getScripts();
        default:
          return "";
      }
    }
  }

  /**
   * Return the total number of rows
   *
   * @return int
   */
  public int getRowCount() {
    return dataVector.size();
  }

  /**
   * Return the total number of columns
   *
   * @return int
   */
  public int getColumnCount() {
    return COLUMNNAMES.length;
  }

  /**
   * Return a given row in String format using the stated separator
   *
   * @param row int
   * @return String
   */
  public String getRow(int row) {
    StringBuffer output = new StringBuffer();
    if ((row > -1) && (row < dataVector.size())) {
      for (int i = 0; i < COLUMNNAMES.length; i++) {
        output.append(getValueAt(row, i));
        if (i < COLUMNNAMES.length - 1) {
          output.append(STRING_SEPARATOR);
        }
      }
      output.append("\n");
    }
    return output.toString();
  }

  /**
   * Add a row to the table model.
   *
   * @param id String
   * @param uri String
   * @param statusCode String
   * @param statusText String
   * @param comments String
   * @param scripts String
   */
  public void addRow(String id, String uri, String statusCode,
                     String statusText, String comments, String scripts) {

    ResponseOutput response = new ResponseOutput(id, uri, statusCode,
                                                 statusText, comments, scripts);
    dataVector.add(response);
    fireTableRowsInserted(0, dataVector.size() - 1);
  }

  /**
   * Remove all the rows within the given table model.
   */
  public void removeAllRows() {
    dataVector.removeAllElements();

    fireTableRowsDeleted(0, 1);
  }
}


class ResponseOutput {
  protected String id;
  protected String uri;
  protected String statusCode;
  protected String statusText;
  protected String comments;
  protected String scripts;

  public ResponseOutput(String id, String uri, String statusCode,
                        String statusText, String comments, String scripts) {

    this.id = id;
    this.uri = uri;
    this.statusCode = statusCode;
    this.statusText = statusText;
    this.comments = comments;
    this.scripts = scripts;
  }

  public String getID() {
    return id;
  }

  public String getURI() {
    return uri;
  }

  public String getStatusCode() {
    return statusCode;
  }

  public String getStatusText() {
    return statusText;
  }

  public String getComments() {
    return comments;
  }

  public String getScripts() {
    return scripts;
  }
}
