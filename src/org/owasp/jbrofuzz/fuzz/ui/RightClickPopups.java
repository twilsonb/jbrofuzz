package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import org.owasp.jbrofuzz.fuzz.FuzzingPanel;
import org.owasp.jbrofuzz.ui.viewers.PropertiesViewer;
import org.owasp.jbrofuzz.ui.viewers.WindowViewerFrame;
import org.owasp.jbrofuzz.util.ImageCreator;

import com.Ostermiller.util.Browser;

public class RightClickPopups {

	public static final void rightClickOutputTable(final FuzzingPanel mFuzzingPanel, final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i5_open_folder = new JMenuItem("Open Containing Folder");
		final JMenuItem i0_open_browser = new JMenuItem("Open in Browser");
		final JMenuItem i1_open_viewer = new JMenuItem("Open in Viewer");
		final JMenuItem i2_clear = new JMenuItem("Clear All Output");
		final JMenuItem i3_copy = new JMenuItem("Copy");
		final JMenuItem i4_select = new JMenuItem("Select All");

		i0_open_browser.setIcon(ImageCreator.IMG_OPENINBROWSER);
		i2_clear.setIcon(ImageCreator.IMG_CLEAR);
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
		final MouseAdapter myOutputMouseAdapter = new MouseAdapter() {

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

		area.getTableHeader().addMouseListener(myOutputMouseAdapter);
		area.addMouseListener(myOutputMouseAdapter);

	}

	public static final void rightClickRequestTextComponent(final FuzzingPanel mFuzzingPanel, final JTextComponent area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i0_add = new JMenuItem("Add");
		final JMenuItem i1_cut = new JMenuItem("Cut");
		final JMenuItem i2_copy = new JMenuItem("Copy");
		final JMenuItem i3_paste = new JMenuItem("Paste");
		final JMenuItem i4_select = new JMenuItem("Select All");

		i0_add.setIcon(ImageCreator.IMG_ADD);
		i1_cut.setIcon(ImageCreator.IMG_CUT);
		i2_copy.setIcon(ImageCreator.IMG_COPY);
		i3_paste.setIcon(ImageCreator.IMG_PASTE);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);

		i1_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		i2_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		i3_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK));
		i4_select.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		popmenu.add(i0_add);
		popmenu.addSeparator();
		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.addSeparator();
		popmenu.add(i4_select);

		if (!area.isEditable()) {
			i3_paste.setEnabled(false);
		}

		i0_add.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				mFuzzingPanel.add();
			}
		});

		i1_cut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.cut();
			}
		});

		i2_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.copy();
			}
		});

		i3_paste.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (area.isEditable()) {
					area.paste();
				}
			}
		});

		i4_select.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		area.addMouseListener(new MouseAdapter() {
			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
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


	public static final void rightClickFuzzersTable(final FuzzingPanel mFuzzingPanel, final JTable area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i0_view_payloads = new JMenuItem("View Payloads");
		final JMenuItem i1_remove_fuzzer = new JMenuItem("Remove Fuzzer");
		final JMenuItem i2_clear = new JMenuItem("Clear All Fuzzers");
		final JMenuItem i3_copy = new JMenuItem("Copy");
		final JMenuItem i4_select = new JMenuItem("Select All");

		// i0_open_browser.setIcon(ImageCreator.IMG_OPENINBROWSER);
		i1_remove_fuzzer.setIcon(ImageCreator.IMG_REMOVE);
		i2_clear.setIcon(ImageCreator.IMG_CLEAR);
		i3_copy.setIcon(ImageCreator.IMG_COPY);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);

		popmenu.add(i0_view_payloads);
		popmenu.add(i1_remove_fuzzer);
		popmenu.addSeparator();
		popmenu.add(i2_clear);
		popmenu.addSeparator();
		popmenu.add(i3_copy);
		popmenu.add(i4_select);

		// View Payloads
		i0_view_payloads.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final int c = area.getSelectedRow();
				if (c < 0) {
					return;
				}

				final String fuzzerRowId = (String) area.getModel()
				.getValueAt(area.convertRowIndexToModel(c), 0);

				final String fuzzerName = 
					mFuzzingPanel.getFrame().getJBroFuzz().
					getDatabase().getName(fuzzerRowId);

				final String[] fuzzerPayloads =
					mFuzzingPanel.getFrame().getJBroFuzz().
					getDatabase().getPayloads(fuzzerRowId);

				StringBuffer myPayloadsBuffer = new StringBuffer();
				for(String si : fuzzerPayloads) {
					myPayloadsBuffer.append(si);
					myPayloadsBuffer.append('\n');
					myPayloadsBuffer.append('\n');
				}

				new PropertiesViewer(mFuzzingPanel,
						"Fuzzer: " + fuzzerName + " (" + fuzzerRowId + ")", 
						myPayloadsBuffer.toString());
			}
		});

		// Remove Fuzzer
		i1_remove_fuzzer.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {


				// If multiple rows are selected the first row is the
				// one
				final int c = area.getSelectedRow();
				if (c < 0) {
					return;
				}

				if (!mFuzzingPanel.isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFuzzingPanel.getFrame(),
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {

						mFuzzingPanel.stop();

						((FuzzersTableModel)area.getModel()).removeRow(area.convertRowIndexToModel(c));
						// .getValueAt(area.convertRowIndexToModel(c), 3);
						// new WindowViewerFrame(mFuzzingPanel, name);						
					}

				} else {

					((FuzzersTableModel)area.getModel()).removeRow(area.convertRowIndexToModel(c));

				}

			}
		});

		// Clear Fuzzers
		i2_clear.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if (!mFuzzingPanel.isStopped()) {

					int choice = JOptionPane.showConfirmDialog(mFuzzingPanel.getFrame(),
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {

						mFuzzingPanel.stop();

						mFuzzingPanel.clearFuzzersTable();

					}

				} else {

					mFuzzingPanel.clearFuzzersTable();

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

		final MouseAdapter myFuZZMouseAdapter = new MouseAdapter() {

			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {

					final Point point = e.getPoint();
					final int row = area.rowAtPoint(point);

					if (row < 0) {
						i0_view_payloads.setEnabled(false);
						i1_remove_fuzzer.setEnabled(false);
						i2_clear.setEnabled(true);
						i3_copy.setEnabled(false);
						i4_select.setEnabled(false);
						// i5_open_folder.setEnabled(false);

					} else {
						i0_view_payloads.setEnabled(true);
						i1_remove_fuzzer.setEnabled(true);
						i2_clear.setEnabled(true);
						i3_copy.setEnabled(true);
						i4_select.setEnabled(true);
						// i5_open_folder.setEnabled(true);

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

		area.getTableHeader().addMouseListener(myFuZZMouseAdapter);
		area.addMouseListener(myFuZZMouseAdapter);
	}


}
