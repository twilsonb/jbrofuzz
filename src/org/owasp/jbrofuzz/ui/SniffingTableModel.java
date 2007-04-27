/**
 * SniffingTableModel.java
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
 * <p>The table model that is being used within the Sniffing Panel.</p>
 *
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class SniffingTableModel extends AbstractTableModel {

  private String[] columnNames;
  private ArrayList dataVector;

  private static final long serialVersionUID = 188923L;
  /**
   * Main Constructor for the Table Model
   */
  public SniffingTableModel() {
    columnNames = new String[1];
    columnNames[0] = "Requests / Replies";
    dataVector = new ArrayList();
  }

  /**
   * <p>Method for returning the String name of a particular Column.</p>
   * @param column int
   * @return String
   */
  public String getColumnName(final int column) {
    return columnNames[column];
  }

  /**
   * <p>Method for checking if a cell is editable. By default, this method
   * returns false, regardless of the parameters being passed.</p>
   * @param row int
   * @param column int
   * @return boolean
   */
  public boolean isCellEditable(final int row, final int column) {
    return false;
  }

  /**
   * <p>Method for returning the Objectlocated at a particular row/column.</p>
   * @param row int
   * @param column int
   * @return Object
   */
  public Object getValueAt(final int row, final int column) {
    return (String) dataVector.get(row);
  }

  /**
   * <p>Set the value of a particular object at a particular row/column.</p>
   * @param value Object
   * @param row int
   * @param column int
   */
  public void setValueAt(final Object value, final int row, final int column) {
    dataVector.set(row, value.toString());
    fireTableCellUpdated(row, column);
  }

  /**
   * <p>Method for returning the contents of a row as a String.</p>
   * @param row int
   * @return String
   */
  public String getValueAt(final int row) {
    return (String) dataVector.get(row);
  }

  /**
   * <p>Get the total number of rows present within the table.</p>
   * @return int
   */
  public int getRowCount() {
    return dataVector.size();
  }

  /**
   * <p>Get the total number of columns.</p>
   * @return int
   */
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * <p>Method for checking to see if an empty row is present within the current
   * table model.</p>
   * @return boolean
   */
  public boolean hasEmptyRow() {
    boolean returnValue = false;

    if (dataVector.size() == 0) {
      returnValue = false;
    }
    else {
      final String record = (String) dataVector.get(dataVector.size() - 1);
      if ("".equals(record)) {
        returnValue = true;
      }
    }
    return returnValue;
  }

  /**
   * <p>Method for adding an empty row to the table model and thus the table. This
   * method invokes the fireTableRowsInserted method.</p>
   */
  public void addEmptyRow() {
    dataVector.add("");
    fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
  }
}
