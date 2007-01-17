/**
 * FuzzingTableModel.java 0.4
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
import javax.swing.table.*;

public class FuzzingTableModel extends AbstractTableModel {

    private static final int INDEX_GENERATOR = 0;
    private static final int INDEX_START = 1;
    private static final int INDEX_END = 2;

    private String[] columnNames;
    private Vector dataVector;

    public FuzzingTableModel(String[] columnNames) {
        this.columnNames = columnNames;
        dataVector = new Vector();
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public boolean isCellEditable(int row, int column) {
        if (column == INDEX_GENERATOR) return true;
        else return false;
    }

    public Class getColumnClass(int column) {
        switch (column) {
            case INDEX_GENERATOR:
            case INDEX_START:
            case INDEX_END:
               return String.class;
            default:
               return Object.class;
        }
    }

    public Object getValueAt(int row, int column) {
        Generator record = (Generator)dataVector.get(row);
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

    public void setValueAt(Object value, int row, int column) {
        Generator record = (Generator)dataVector.get(row);
        switch (column) {
            case INDEX_GENERATOR:
               record.setType((String)value);
               break;
            case INDEX_START:
               record.setStart(((Integer)value).intValue());
               break;
            case INDEX_END:
               record.setEnd(((Integer)value).intValue());
               break;
            default:
               System.out.println("invalid index");
        }
        fireTableCellUpdated(row, column);
    }

    public int getRowCount() {
        return dataVector.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (dataVector.size() == 0) return false;
        Generator Generator = (Generator)dataVector.get(dataVector.size() - 1);
        if (Generator.getType().trim().equals("") &&
           Generator.getStart() == 0 &&
           Generator.getEnd() == 0)
        {
           return true;
        }
        else return false;
    }

    public void addRow(String generator, int start, int end) {
      Generator addingGenerator = new Generator(generator, start, end);
      dataVector.add(addingGenerator);
      fireTableRowsInserted(
         dataVector.size() - 1,
         dataVector.size() - 1);
    }

    public void removeRow(String generator, int start, int end) {
      int rowToRemove = -1;
      for(int i = 0; i < dataVector.size(); i++) {
        Generator record = (Generator)dataVector.get(i);
        if (record.getType().equals(generator) &&
            record.getStart() == start && record.getEnd() == end) {
          rowToRemove = i;
        }
      }
      if(rowToRemove > -1) {
        dataVector.removeElementAt(rowToRemove);
        fireTableRowsDeleted(0, dataVector.size() - 1);
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
         return type.toString();
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
