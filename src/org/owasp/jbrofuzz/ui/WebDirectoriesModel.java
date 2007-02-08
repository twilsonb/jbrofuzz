/**
 * WebDirectoriesModel.java 0.4
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
package org.owasp.jbrofuzz.ui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class WebDirectoriesModel extends AbstractTableModel {
  /**
   * <p>The String used to separate columns when a toString representation of
   * a set number of columns or rows is required. This is typically used in
   * method getRow() that returns a String.</p>
   */
  public static final String STRING_COLUMN_SEPARATOR = "          ";

  private String[] columnNames;
  private Vector dataVector;

  public WebDirectoriesModel(String[] columnNames) {
    this.columnNames = columnNames;
    dataVector = new Vector();
  }

  public String getColumnName(int column) {
    return columnNames[column];
  }

  public Object getValueAt(int row, int column) {
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

  public int getRowCount() {
    return dataVector.size();
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public String getRow(int row) {
    String output = "";
    if ((row > -1) && (row < dataVector.size())) {
      for (int i = 0; i < columnNames.length; i++) {
        output += getValueAt(row, i) + STRING_COLUMN_SEPARATOR;
      }
    }
    return output;
  }

  public void addRow(String id, String uri, String statusCode,
                     String statusText, String comments, String scripts) {

    ResponseOutput response = new ResponseOutput(id, uri, statusCode,
                                                 statusText,
                                                 comments, scripts);
    dataVector.add(response);
    fireTableRowsInserted(
      dataVector.size() - 1,
      dataVector.size() - 1);
  }

  public void removeAllRows() {
    dataVector.removeAllElements();
    fireTableRowsDeleted(0, 0);
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
    return id.toString();
  }

  public String getURI() {
    return uri.toString();
  }

  public String getStatusCode() {
    return statusCode.toString();
  }

  public String getStatusText() {
    return statusText.toString();
  }

  public String getComments() {
    return comments.toString();
  }

  public String getScripts() {
    return scripts.toString();
  }
}

/**
 * @todo Find Comments in file/
 *       Find Scripts in file/
 */

/**
 * @todo Escape special characters
 */

/**
 * @todo Right click open in browser
 */
