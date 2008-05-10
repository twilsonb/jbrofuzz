/**
 * JBroFuzz 0.9
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007, 2008 subere@uncon.org
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
/**
 * TableSorter.java 0.8
 *
 * TableSorter is a decorator for TableModels; adding sorting
 * functionality to a supplied TableModel. TableSorter does
 * not store or copy the data in its TableModel; instead it maintains
 * a map from the row indexes of the view to the row indexes of the
 * model. As requests are made of the sorter (like getValueAt(row, col))
 * they are passed to the underlying model after the row numbers
 * have been translated via the internal mapping array. This way,
 * the TableSorter appears to hold another copy of the table
 * with the rows in a different order.
 * <p/>
 * TableSorter registers itself as a listener to the underlying model,
 * just as the JTable itself would. Events recieved from the model
 * are examined, sometimes manipulated (typically widened), and then
 * passed on to the TableSorter's listeners (typically the JTable).
 * If a change to the model has invalidated the order of TableSorter's
 * rows, a note of this is made and the sorter will resort the
 * rows the next time a value is requested.
 * <p/>
 * When the tableHeader property is set, either by using the
 * setTableHeader() method or the two argument constructor, the
 * table header may be used as a complete UI for TableSorter.
 * The default renderer of the tableHeader is decorated with a renderer
 * that indicates the sorting status of each column. In addition,
 * a mouse listener is installed with the following behavior:
 * <ul>
 * <li>
 * Mouse-click: Clears the sorting status of all other columns
 * and advances the sorting status of that column through three
 * values: {NOT_SORTED, ASCENDING, DESCENDING} (then back to
 * NOT_SORTED again).
 * <li>
 * SHIFT-mouse-click: Clears the sorting status of all other columns
 * and cycles the sorting status of the column through the same
 * three values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.
 * <li>
 * CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except
 * that the changes to the column do not cancel the statuses of columns
 * that are already sorting - giving a way to initiate a compound
 * sort.
 * </ul>
 * <p/>
 * This is a long overdue rewrite of a class of the same name that
 * first appeared in the swing table demos in 1997.
 *
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @version 2.0 02/27/04
 *
 */
package org.owasp.jbrofuzz.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang.builder.*;
/**
 * <p>
 * This class needs some more work and javadoc is not the only section!
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.6
 */
public class TableSorter extends AbstractTableModel {

	private static class Arrow implements Icon {
		private boolean descending;
		private int size;
		private int priority;

		public Arrow(final boolean descending, final int size, final int priority) {
			this.descending = descending;
			this.size = size;
			this.priority = priority;
		}

		public int getIconHeight() {
			return this.size;
		}

		public int getIconWidth() {
			return this.size;
		}

		public void paintIcon(final Component c, final Graphics g, int x, int y) {
			final Color color = c == null ? Color.GRAY : c.getBackground();
			// In a compound sort, make each succesive triangle 20%
			// smaller than the previous one.
			int dx = (int) (this.size / 2.0 * Math.pow(0.8, this.priority));
			int dy = this.descending ? dx : -dx;
			// Align icon (roughly) with font baseline.
			y = y + 5 * this.size / 6 + (this.descending ? -dy : 0);
			final int shift = this.descending ? 1 : -1;
			g.translate(x, y);

			// Right diagonal.
			g.setColor(color.darker());
			g.drawLine(dx / 2, dy, 0, 0);
			g.drawLine(dx / 2, dy + shift, 0, shift);

			// Left diagonal.
			g.setColor(color.brighter());
			g.drawLine(dx / 2, dy, dx, 0);
			g.drawLine(dx / 2, dy + shift, dx, shift);

			// Horizontal line.
			if (this.descending) {
				g.setColor(color.darker().darker());
			} else {
				g.setColor(color.brighter().brighter());
			}
			g.drawLine(dx, 0, 0, 0);

			g.setColor(color);
			g.translate(-x, -y);
		}
	}

	private static class Directive {
		private int column;
		private int direction;

		public Directive(final int column, final int direction) {
			this.column = column;
			this.direction = direction;
		}
	}

	private class MouseHandler extends MouseAdapter {
		@Override
		public void mouseClicked(final MouseEvent e) {
			final JTableHeader h = (JTableHeader) e.getSource();
			final TableColumnModel columnModel = h.getColumnModel();
			final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			final int column = columnModel.getColumn(viewColumn).getModelIndex();
			if (column != -1) {
				int status = TableSorter.this.getSortingStatus(column);
				if (!e.isControlDown()) {
					TableSorter.this.cancelSorting();
				}
				// Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING}
				// or
				// {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is
				// pressed.
				status = status + (e.isShiftDown() ? -1 : 1);
				status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
				TableSorter.this.setSortingStatus(column, status);
			}
		}
	}

	private class Row implements Comparable {
		private int modelIndex;

		public Row(final int index) {
			this.modelIndex = index;
		}

		public int compareTo(final Object o) {

			// return CompareToBuilder.reflectionCompare(this, o);

			
			final int row1 = this.modelIndex;
			final int row2 = ((Row) o).modelIndex;

			for (final Iterator it = TableSorter.this.sortingColumns.iterator(); it
					.hasNext();) {
				final Directive directive = (Directive) it.next();
				final int column = directive.column;
				final Object o1 = TableSorter.this.tableModel.getValueAt(row1, column);
				final Object o2 = TableSorter.this.tableModel.getValueAt(row2, column);

				int comparison = 0;
				// Define null less than everything, except null.
				if ((o1 == null) && (o2 == null)) {
					comparison = 0;
				} else if (o1 == null) {
					comparison = -1;
				} else if (o2 == null) {
					comparison = 1;
				} else {
					comparison = TableSorter.this.getComparator(column).compare(o1, o2);
				}
				if (comparison != 0) {
					return directive.direction == TableSorter.DESCENDING ? -comparison
							: comparison;
				}
			}
			return 0;
			 
		}

		@Override
		public boolean equals(Object o) {

			boolean check = false;
			if ( o != null ) {
				if( o.hashCode() == Row.class.hashCode())
					check = true;
			}
			return check;

		}

		@Override
		public int hashCode() {

			return new HashCodeBuilder(67,97).append(modelIndex).toHashCode();

		}
	}

	private class SortableHeaderRenderer implements TableCellRenderer {

		private TableCellRenderer tableCellRenderer;

		public SortableHeaderRenderer(final TableCellRenderer tableCellRenderer) {
			this.tableCellRenderer = tableCellRenderer;
		}

		public Component getTableCellRendererComponent(final JTable table,
				final Object value, final boolean isSelected, final boolean hasFocus,
				final int row, final int column) {
			final Component c = this.tableCellRenderer.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			if (c instanceof JLabel) {
				final JLabel l = (JLabel) c;
				l.setHorizontalTextPosition(SwingConstants.LEFT);
				final int modelColumn = table.convertColumnIndexToModel(column);
				l.setIcon(TableSorter.this.getHeaderRendererIcon(modelColumn, l
						.getFont().getSize()));
			}
			return c;
		}
	}

	private class TableModelHandler implements TableModelListener {
		public void tableChanged(final TableModelEvent e) {
			// If we're not sorting by anything, just pass the event along.
			if (!TableSorter.this.isSorting()) {
				TableSorter.this.clearSortingState();
				TableSorter.this.fireTableChanged(e);
				return;
			}

			// If the table structure has changed, cancel the sorting; the
			// sorting columns may have been either moved or deleted from
			// the model.
			if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
				TableSorter.this.cancelSorting();
				TableSorter.this.fireTableChanged(e);
				return;
			}

			// We can map a cell event through to the view without widening
			// when the following conditions apply:
			//
			// a) all the changes are on one row (e.getFirstRow() == e.getLastRow())
			// and,
			// b) all the changes are in one column (column !=
			// TableModelEvent.ALL_COLUMNS) and,
			// c) we are not sorting on that column (getSortingStatus(column) ==
			// NOT_SORTED) and,
			// d) a reverse lookup will not trigger a sort (modelToView != null)
			//
			// Note: INSERT and DELETE events fail this test as they have column ==
			// ALL_COLUMNS.
			//
			// The last check, for (modelToView != null) is to see if modelToView
			// is already allocated. If we don't do this check; sorting can become
			// a performance bottleneck for applications where cells
			// change rapidly in different parts of the table. If cells
			// change alternately in the sorting column and then outside of
			// it this class can end up re-sorting on alternate cell updates -
			// which can be a performance problem for large tables. The last
			// clause avoids this problem.
			final int column = e.getColumn();
			if ((e.getFirstRow() == e.getLastRow())
					&& (column != TableModelEvent.ALL_COLUMNS)
					&& (TableSorter.this.getSortingStatus(column) == TableSorter.NOT_SORTED)
					&& (TableSorter.this.modelToView != null)) {
				final int viewIndex = TableSorter.this.getModelToView()[e.getFirstRow()];
				TableSorter.this.fireTableChanged(new TableModelEvent(TableSorter.this,
						viewIndex, viewIndex, column, e.getType()));
				return;
			}

			// Something has happened to the data that may have invalidated the row
			// order.
			TableSorter.this.clearSortingState();
			TableSorter.this.fireTableDataChanged();
			return;
		}
	}

	private static final long serialVersionUID = 129903L;

	/**
	 * Value corresponding to the type of sort, in descending order
	 */
	public static final int DESCENDING = -1;

	/**
	 * Value corresponding to the type of sort, in not sorted order
	 */
	public static final int NOT_SORTED = 0;
	/**
	 * Value corresponding to the type of sort, in ascending order
	 */
	public static final int ASCENDING = 1;

	private static final Directive EMPTY_DIRECTIVE = new Directive(-1,
			TableSorter.NOT_SORTED);

	public static final Comparator COMPARABLE_COMPARATOR = new Comparator() {

		public int compare(Object o1, Object o2) {
			return ((Comparable) o1).compareTo(o2);
		}
	};

	public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			return o1.toString().compareTo(o2.toString());
		}
	};
	protected TableModel tableModel;
	private Row[] viewToModel;

	private int[] modelToView;

	private JTableHeader tableHeader;

	private MouseListener mouseListener;

	private TableModelListener tableModelListener;

	private final Map columnComparators = new HashMap();

	private final List sortingColumns = new ArrayList();

	public TableSorter() {
		this.mouseListener = new MouseHandler();
		this.tableModelListener = new TableModelHandler();
	}

	public TableSorter(final TableModel tableModel) {
		this();
		this.setTableModel(tableModel);
	}

	public TableSorter(final TableModel tableModel, final JTableHeader tableHeader) {
		this();
		this.setTableHeader(tableHeader);
		this.setTableModel(tableModel);
	}

	private void cancelSorting() {
		this.sortingColumns.clear();
		this.sortingStatusChanged();
	}

	private void clearSortingState() {
		this.viewToModel = null;
		this.modelToView = null;
	}

	@Override
	public Class getColumnClass(final int column) {
		return this.tableModel.getColumnClass(column);
	}

	public int getColumnCount() {
		return (this.tableModel == null) ? 0 : this.tableModel.getColumnCount();
	}

	@Override
	public String getColumnName(final int column) {
		return this.tableModel.getColumnName(column);
	}

	protected Comparator getComparator(final int column) {
		final Class columnType = this.tableModel.getColumnClass(column);
		final Comparator comparator = (Comparator) this.columnComparators
		.get(columnType);
		if (comparator != null) {
			return comparator;
		}
		if (Comparable.class.isAssignableFrom(columnType)) {
			return TableSorter.COMPARABLE_COMPARATOR;
		}
		return TableSorter.LEXICAL_COMPARATOR;
	}

	private Directive getDirective(final int column) {
		for (int i = 0; i < this.sortingColumns.size(); i++) {
			final Directive directive = (Directive) this.sortingColumns.get(i);
			if (directive.column == column) {
				return directive;
			}
		}
		return TableSorter.EMPTY_DIRECTIVE;
	}

	protected Icon getHeaderRendererIcon(final int column, final int size) {
		final Directive directive = this.getDirective(column);
		if (directive == TableSorter.EMPTY_DIRECTIVE) {
			return null;
		}
		return new Arrow(directive.direction == TableSorter.DESCENDING, size,
				this.sortingColumns.indexOf(directive));
	}

	private int[] getModelToView() {
		if (this.modelToView == null) {
			final int n = this.getViewToModel().length;
			this.modelToView = new int[n];
			for (int i = 0; i < n; i++) {
				this.modelToView[this.modelIndex(i)] = i;
			}
		}
		return this.modelToView;
	}

	public int getRowCount() {
		return (this.tableModel == null) ? 0 : this.tableModel.getRowCount();
	}

	public int getSortingStatus(final int column) {
		return this.getDirective(column).direction;
	}

	// TableModel interface methods

	public JTableHeader getTableHeader() {
		return this.tableHeader;
	}

	public TableModel getTableModel() {
		return this.tableModel;
	}

	public Object getValueAt(int row, int column) {
		// synchronized (listenerList) {
			if (row < 0 || row >= getRowCount())
				row = 0;
			if (column < 0 || column >= getColumnCount())
				column = 0;
			return this.tableModel.getValueAt(this.modelIndex(row), column);
		// }
	}

	private Row[] getViewToModel() {
		if (this.viewToModel == null) {
			final int tableModelRowCount = this.tableModel.getRowCount();
			this.viewToModel = new Row[tableModelRowCount];
			for (int row = 0; row < tableModelRowCount; row++) {
				this.viewToModel[row] = new Row(row);
			}

			if (this.isSorting()) {
				Arrays.sort(this.viewToModel);
			}
		}
		return this.viewToModel;
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return this.tableModel.isCellEditable(this.modelIndex(row), column);
	}

	public boolean isSorting() {
		return this.sortingColumns.size() != 0;
	}

	public int modelIndex(final int viewIndex) {
		return this.getViewToModel()[viewIndex].modelIndex;
	}

	// Helper classes

	public void setColumnComparator(final Class type, final Comparator comparator) {
		if (comparator == null) {
			this.columnComparators.remove(type);
		} else {
			this.columnComparators.put(type, comparator);
		}
	}

	public void setSortingStatus(final int column, final int status) {
		final Directive directive = this.getDirective(column);
		if (directive != TableSorter.EMPTY_DIRECTIVE) {
			this.sortingColumns.remove(directive);
		}
		if (status != TableSorter.NOT_SORTED) {
			this.sortingColumns.add(new Directive(column, status));
		}
		this.sortingStatusChanged();
	}

	public void setTableHeader(final JTableHeader tableHeader) {
		if (this.tableHeader != null) {
			this.tableHeader.removeMouseListener(this.mouseListener);
			final TableCellRenderer defaultRenderer = this.tableHeader
			.getDefaultRenderer();
			if (defaultRenderer instanceof SortableHeaderRenderer) {
				this.tableHeader
				.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
			}
		}
		this.tableHeader = tableHeader;
		if (this.tableHeader != null) {
			this.tableHeader.addMouseListener(this.mouseListener);
			this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(
					this.tableHeader.getDefaultRenderer()));
		}
	}

	public void setTableModel(final TableModel tableModel) {
		if (this.tableModel != null) {
			this.tableModel.removeTableModelListener(this.tableModelListener);
		}

		this.tableModel = tableModel;
		if (this.tableModel != null) {
			this.tableModel.addTableModelListener(this.tableModelListener);
		}

		this.clearSortingState();
		this.fireTableStructureChanged();
	}

	/**
	 * <p>
	 * Set the value at a particular row and column to the given object.
	 * </p>
	 * 
	 * @param aValue
	 *          Object
	 * @param row
	 *          int
	 * @param column
	 *          int
	 */
	@Override
	public void setValueAt(final Object aValue, final int row, final int column) {
		this.tableModel.setValueAt(aValue, this.modelIndex(row), column);
	}

	private void sortingStatusChanged() {
		this.clearSortingState();
		this.fireTableDataChanged();
		if (this.tableHeader != null) {
			this.tableHeader.repaint();
		}
	}
}
