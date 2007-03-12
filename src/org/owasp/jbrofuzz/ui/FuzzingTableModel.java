/**
 * FuzzingTableModel.java 0.5
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

import java.util.*;

import javax.swing.table.*;

/**
 * The fuzzing table model used within the generators table of the "TCP Fuzzing"
 * panel.
 *
 * @author subere (at) uncon . org
 * @version 0.5
 */
public class FuzzingTableModel extends AbstractTableModel {

  /**
   * <p>The String used to separate columns when a toString representation of
   * a set number of columns or rows is required. This is typically used in
   * method getRow() that returns a String.</p>
   */
  public static final String STRING_COLUMN_SEPARATOR = "          ";

  private static final int INDEX_GENERATOR = 0;
  private static final int INDEX_START = 1;
  private static final int INDEX_END = 2;

  // The names of the columns within the table of generators
  private static final String[] COLUMNNAMES = {
                                              "Generator", "Start", "End"};
  // The vector of data
  private Vector dataVector;
  // The panel that the model is attached to
  private FuzzingPanel fPanel;

  /**
   * <p>Main Constructor passes the Fuzzing Panel.</p>
   * @param fPanel FuzzingPanel
   */
  public FuzzingTableModel(FuzzingPanel fPanel) {
    this.fPanel = fPanel;
    dataVector = new Vector();
  }

  /**
   * Get a given column name.
   *
   * @param column int
   * @return String
   */
  public String getColumnName(int column) {
    return COLUMNNAMES[column];
  }

  /**
   * Check to see if a generator table is editable
   * @param row int
   * @param column int
   * @return boolean
   */
  public boolean isCellEditable(int row, int column) {
    if (column == INDEX_GENERATOR) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * <p>Get the value within the generator table at a given location of column
   * and row.</p>
   *
   * @param row int
   * @param column int
   * @return Object
   */
  public Object getValueAt(int row, int column) {
    Generator record = (Generator) dataVector.get(row);
    switch (column) {
      case INDEX_GENERATOR:
        return record.getType();
      case INDEX_START:
        return new Integer(record.getStart());
      case INDEX_END:
        return new Integer(record.getEnd());
      default:
        return new Object();
    }
  }

  /**
   * <p>Set a value at the corresponding row and column location.</p>
   * @param value Object
   * @param row int
   * @param column int
   */
  public void setValueAt(Object value, int row, int column) {
    Generator record = (Generator) dataVector.get(row);
    switch (column) {
      case INDEX_GENERATOR:
        record.setType((String) value);
        break;
      case INDEX_START:
        record.setStart(((Integer) value).intValue());
        break;
      case INDEX_END:
        record.setEnd(((Integer) value).intValue());
        break;
      default:
        fPanel.getFrameWindow().log("TCP Fuzzing Panel: Invalid index ");
    }
    fireTableCellUpdated(row, column);
  }

  /**
   * Get a complete row count of the generator table.
   * @return int
   */
  public int getRowCount() {
    return dataVector.size();
  }

  /**
   * Get a complete column count of the generator table
   * @return int
   */
  public int getColumnCount() {
    return COLUMNNAMES.length;
  }

  /**
   * <p>Get a row depending on the corresponding integer given.</p>
   * @param row int
   * @return String
   */
  public String getRow(int row) {
    StringBuffer output = new StringBuffer();
    if ((row > -1) && (row < dataVector.size())) {
      for (int i = 0; i < COLUMNNAMES.length; i++) {
        output.append(getValueAt(row, i) + STRING_COLUMN_SEPARATOR);
      }
    }
    return output.toString();
  }

  /**
   * <p>Check to see if an empty row exists within the generator table.</p>
   * @return boolean
   */
  public boolean hasEmptyRow() {
    if (dataVector.size() == 0) {
      return false;
    }
    Generator gen = (Generator) dataVector.get(dataVector.size() - 1);
    if (gen.getType().trim().equalsIgnoreCase("") &&
        gen.getStart() == 0 &&
        gen.getEnd() == 0) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * <p>Add a row to the generator list.</p>
   *
   * @param generator String
   * @param start int
   * @param end int
   */
  public void addRow(String generator, int start, int end) {
    Generator addingGenerator = new Generator(generator, start, end);
    dataVector.add(addingGenerator);
    fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
  }

  /**
   * <p>Remove a row from the generator list.</p>
   *
   * @param generator String
   * @param start int
   * @param end int
   */
  public void removeRow(String generator, int start, int end) {
    int rowToRemove = -1;
    for (int i = 0; i < dataVector.size(); i++) {
      Generator record = (Generator) dataVector.get(i);
      if (record.getType().equals(generator) && record.getStart() == start &&
          record.getEnd() == end) {
        rowToRemove = i;
      }
    }
    if (rowToRemove > -1) {
      dataVector.removeElementAt(rowToRemove);
      fireTableRowsDeleted(0, rowToRemove);
    }
  }
}

class Generator {
  protected String type;
  protected Integer start;
  protected Integer end;

  public Generator(String generator, int start, int end) {
    type = generator;
    this.start = new Integer(start);
    this.end = new Integer(end);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getStart() {
    return start.intValue();
  }

  public void setStart(int start) {
    this.start = new Integer(start);
  }

  public int getEnd() {
    return end.intValue();
  }

  public void setEnd(int end) {
    this.end = new Integer(end);
  }
}
