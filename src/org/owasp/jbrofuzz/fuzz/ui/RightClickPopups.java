package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.owasp.jbrofuzz.fuzz.FuzzingPanel;
import org.owasp.jbrofuzz.ui.viewers.WindowViewerFrame;
import org.owasp.jbrofuzz.util.ImageCreator;

import com.Ostermiller.util.Browser;

public class RightClickPopups {

	public static final void rightClickOutputTable(final FuzzingPanel mFuzzingPanel, final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i5_open_folder = new JMenuItem("Open Containing Folder");
		final JMenuItem i0_open_browser = new JMenuItem("Open in Browser");
		final JMenuItem i1_open_viewer = new JMenuItem("Open in Viewer");
		final JMenuItem i2_clear = new JMenuItem("Clear Output");
		final JMenuItem i3_copy = new JMenuItem("Copy");
		final JMenuItem i4_select = new JMenuItem("Select All");

		i0_open_browser.setIcon(ImageCreator.IMG_OPENINBROWSER);
		i3_copy.setIcon(ImageCreator.IMG_COPY);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);
		
		popmenu.add(i5_open_folder);
		popmenu.add(i0_open_browser);
		popmenu.add(i1_open_viewer);
		popmenu.addSeparator();
		popmenu.add(i2_clear);
		popmenu.addSeparator();
		popmenu.add(i3_copy);
		popmenu.add(i4_select);

		// Open Containing Folder
		i5_open_folder.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final int c = area.getSelectedRow();
				if (c < 0) {
					return;
				}

				final File f = mFuzzingPanel.getFrame().getJBroFuzz().getHandler()
				.getFuzzDirectory();

				Browser.init();
				try {
					Browser.displayURL(f.toURI().toString());
				} catch (final IOException ex) {
					mFuzzingPanel.getFrame()
					.log(
							"Could not launch link in external browser",
							3);
				}

			}
		});
		
		// Open in Viewer
		i1_open_viewer.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				// If multiple rows are selected the first row is the
				// one
				final int c = area.getSelectedRow();
				if (c < 0) {
					return;
				}
				final String name = (String) area.getModel()
				.getValueAt(area.convertRowIndexToModel(c), 0);
				new WindowViewerFrame(mFuzzingPanel, name);

			}
		});

		// Open in Browser
		i0_open_browser.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final int c = area.getSelectedRow();
				if (c < 0) {
					return;
				}
				final String fileName = (String) area.getModel()
				.getValueAt(area.convertRowIndexToModel(c), 0)
				+ ".html";
				final File f = mFuzzingPanel.getFrame().getJBroFuzz().getHandler()
				.getFuzzFile(fileName);

				Browser.init();
				try {
					Browser.displayURL(f.toURI().toString());
				} catch (final IOException ex) {
					mFuzzingPanel.getFrame()
					.log(
							"Could not launch link in external browser",
							3);
				}

			}
		});

		// Clear Output
		i2_clear.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if (!mFuzzingPanel.isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFuzzingPanel.getFrame(),
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {

						mFuzzingPanel.stop();

						mFuzzingPanel.clearOutputTable();
						// Create a new directory to store all data
						mFuzzingPanel.getFrame().getJBroFuzz().getHandler()
						.createNewDirectory();
					}

				} else {

					mFuzzingPanel.clearOutputTable();
					// Create a new directory to store all data
					mFuzzingPanel.getFrame().getJBroFuzz().getHandler()
					.createNewDirectory();

				}



			}
		});

		// Copy
		i3_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();

				for (final int element : selection) {
					for (int i = 0; i < area.getColumnCount(); i++) {

						selectionBuffer.append(area.getModel().getValueAt(
								area.convertRowIndexToModel(element), i));
						if (i < area.getColumnCount() - 1) {
							selectionBuffer.append(",");
						}

					}
					selectionBuffer.append("\n");
				}

				final JTextArea myTempArea = new JTextArea();
				myTempArea.setText(selectionBuffer.toString());
				myTempArea.selectAll();
				myTempArea.copy();

			}
		});

		// Select All
		i4_select.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});
		
		// The mouse adapter used for the table and the table header
		final MouseAdapter myMouseAdapter = new MouseAdapter() {

			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {

					final Point point = e.getPoint();
					final int row = area.rowAtPoint(point);
					
					if (row < 0) {
						i0_open_browser.setEnabled(false);
						i1_open_viewer.setEnabled(false);
						i2_clear.setEnabled(true);
						i3_copy.setEnabled(false);
						i4_select.setEnabled(false);
						i5_open_folder.setEnabled(false);
						
					} else {
						i0_open_browser.setEnabled(true);
						i1_open_viewer.setEnabled(true);
						i2_clear.setEnabled(true);
						i3_copy.setEnabled(true);
						i4_select.setEnabled(true);
						i5_open_folder.setEnabled(true);
						
						if(area.getSelectedRows().length < 2) {
							area.getSelectionModel().setSelectionInterval(row, row);
						}
					}
					
					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				
				} 
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				checkForTriggerEvent(e);
			}


			
		};
			
		area.getTableHeader().addMouseListener(myMouseAdapter);
		area.addMouseListener(myMouseAdapter);
		
	}
	
	public static final void rightClickFuzzersTable(final FuzzingPanel mFuzzingPanel, final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i0_open_browser = new JMenuItem("View Payloads");
		final JMenuItem i1_open_viewer = new JMenuItem("Remove Fuzzer");
		final JMenuItem i2_clear = new JMenuItem("Clear All Fuzzers");
		final JMenuItem i3_copy = new JMenuItem("Copy");
		final JMenuItem i4_select = new JMenuItem("Select All");

		i0_open_browser.setIcon(ImageCreator.IMG_OPENINBROWSER);
		i3_copy.setIcon(ImageCreator.IMG_COPY);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);
		
		popmenu.add(i0_open_browser);
		popmenu.add(i1_open_viewer);
		popmenu.addSeparator();
		popmenu.add(i2_clear);
		popmenu.addSeparator();
		popmenu.add(i3_copy);
		popmenu.add(i4_select);

		// Open in Viewer
		i1_open_viewer.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				// If multiple rows are selected the first row is the
				// one
				final int c = area.getSelectedRow();
				if (c < 0) {
					return;
				}
				final String name = (String) area.getModel()
				.getValueAt(area.convertRowIndexToModel(c), 0);
				new WindowViewerFrame(mFuzzingPanel, name);

			}
		});

		// Open in Browser
		i0_open_browser.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final int c = area.getSelectedRow();
				if (c < 0) {
					return;
				}
				final String fileName = (String) area.getModel()
				.getValueAt(area.convertRowIndexToModel(c), 0)
				+ ".html";
				final File f = mFuzzingPanel.getFrame().getJBroFuzz().getHandler()
				.getFuzzFile(fileName);

				Browser.init();
				try {
					Browser.displayURL(f.toURI().toString());
				} catch (final IOException ex) {
					mFuzzingPanel.getFrame()
					.log(
							"Could not launch link in external browser",
							3);
				}

			}
		});

		// Clear Output
		i2_clear.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if (!mFuzzingPanel.isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFuzzingPanel.getFrame(),
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {

						mFuzzingPanel.stop();

						mFuzzingPanel.clearOutputTable();
						// Create a new directory to store all data
						mFuzzingPanel.getFrame().getJBroFuzz().getHandler()
						.createNewDirectory();
					}

				} else {

					mFuzzingPanel.clearOutputTable();
					// Create a new directory to store all data
					mFuzzingPanel.getFrame().getJBroFuzz().getHandler()
					.createNewDirectory();

				}



			}
		});

		// Copy
		i3_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();

				for (final int element : selection) {
					for (int i = 0; i < area.getColumnCount(); i++) {

						selectionBuffer.append(area.getModel().getValueAt(
								area.convertRowIndexToModel(element), i));
						if (i < area.getColumnCount() - 1) {
							selectionBuffer.append(",");
						}

					}
					selectionBuffer.append("\n");
				}

				final JTextArea myTempArea = new JTextArea();
				myTempArea.setText(selectionBuffer.toString());
				myTempArea.selectAll();
				myTempArea.copy();
				// area.removeRowSelectionInterval(0, area.getRowCount() - 1 );

			}
		});

		// Select All
		i4_select.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});
		
		area.addMouseListener(new MouseAdapter() {

			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {

					final Point point = e.getPoint();
					final int row = area.rowAtPoint(point);
					if (row < 0) {
						return;
					} 
					area.getSelectionModel().setSelectionInterval(row, row);

					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				
				} 
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				checkForTriggerEvent(e);
			}

		});
	}


}
