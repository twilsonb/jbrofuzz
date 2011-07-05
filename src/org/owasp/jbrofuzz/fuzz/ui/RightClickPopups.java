/**
 * JbroFuzz 2.5
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.encode.EncoderHashCore;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.ui.viewers.PropertiesViewer;
import org.owasp.jbrofuzz.ui.viewers.WindowViewerFrame;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

import com.Ostermiller.util.Browser;

public final class RightClickPopups {

	private RightClickPopups() {} // Private constructor

	public static void rightClickOutputTable(final FuzzingPanel mFuzzingPanel, final JTable area) {

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

				final String sURI = mFuzzingPanel.getFrame().getJBroFuzz().getStorageHandler()
				.getLocationURIString();

				Browser.init();
				try {
					Browser.displayURL(sURI);
				} catch (final IOException ex) {
					Logger
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

				Logger.log("Calling WindowViewer with name: " + name, 3);
					// final String sURi = mFuzzingPanel.getFrame().getJBroFuzz().getStorageHandler().getLocationURIString();
					
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
				final String s = mFuzzingPanel.getFrame().getJBroFuzz().getStorageHandler()
				.getFuzzURIString(fileName);

				Browser.init();
				try {
					Browser.displayURL(s);
				} catch (final IOException ex) {
					Logger
					.log(
							"Could not launch link in external browser",
							3);
				}

			}
		});

		// Clear Output
		i2_clear.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if (mFuzzingPanel.isStopped()) {

					mFuzzingPanel.clearOutputTable();
					// Create a new directory to store all data
					mFuzzingPanel.getFrame().getJBroFuzz().getStorageHandler()
					.createNewLocation();

				} else {
					// Clear all output and create a directory only if a fuzzing session is not
					// currently running
					final int choice = JOptionPane.showConfirmDialog(mFuzzingPanel.getFrame(),
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {

						mFuzzingPanel.stop();

						mFuzzingPanel.clearOutputTable();
						// Create a new directory to store all data
						mFuzzingPanel.getFrame().getJBroFuzz().getStorageHandler().createNewLocation();
					}

				}



			}
		});

		// Copy
		i3_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();

				for (final int element : selection) {
					for (int i = 0; i < area.getColumnCount(); i++) {

						selectionBuffer.append(area.getModel().getValueAt(
								area.convertRowIndexToModel(element), i));
						if (i < area.getColumnCount() - 1) {
							selectionBuffer.append(',');
						}

					}
					selectionBuffer.append('\n');
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


	private static JMenu buildEncodeMenu(final JTextComponent area){
		final JMenu encodeMenu = new JMenu("Encode");

		final JMenuItem[] encodeList = new JMenuItem[EncoderHashCore.CODES.length];

		for(int i=0;i<encodeList.length;i++){

			final int index = i;
			final JMenuItem encode = new JMenuItem(EncoderHashCore.CODES[i]);
			encodeMenu.add(encode);

			encode.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					final String toEncode = area.getSelectedText();
					if(toEncode != null){
						final String encoded = EncoderHashCore.encode(toEncode, EncoderHashCore.CODES[index]);
						area.replaceSelection(encoded);				
					}			
				}
			});

		}





		return encodeMenu;

	}

	private static JMenu buildDecodeMenu(final JTextComponent area){
		final JMenu decodeMenu = new JMenu("Decode");

		final JMenuItem[] decodeList = new JMenuItem[EncoderHashCore.CODES.length];

		for(int i=0;i<decodeList.length;i++){

			final int index = i;

			if(EncoderHashCore.isDecoded(EncoderHashCore.CODES[index])){

				final JMenuItem decode = new JMenuItem(EncoderHashCore.CODES[i]);
				decodeMenu.add(decode);
				decode.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						final String toDecode = area.getSelectedText();
						if(toDecode != null ){
							final String decoded = EncoderHashCore.decode(toDecode, EncoderHashCore.CODES[index]);
							area.replaceSelection(decoded);	
						}
					}
				});
			}
		}


		return decodeMenu;
	}



	public static void rightClickRequestTextComponent(final FuzzingPanel mFuzzingPanel, final JTextComponent area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenu encodeMenu = buildEncodeMenu(area);
		final JMenu decodeMenu = buildDecodeMenu(area);

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

		popmenu.add(i0_add);
		popmenu.addSeparator();
		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.add(encodeMenu);
		popmenu.add(decodeMenu);
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

	public static void rightClickOnTheWireTextComponent(final FuzzingPanel mFuzzingPanel, final JTextArea area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i0_clear = new JMenuItem("Clear");
		final JMenuItem i1_cut = new JMenuItem("Cut");
		final JMenuItem i2_copy = new JMenuItem("Copy");
		final JMenuItem i3_paste = new JMenuItem("Paste");
		final JMenuItem i4_select = new JMenuItem("Select All");

		// Icons
		i0_clear.setIcon(ImageCreator.IMG_CLEAR);
		i1_cut.setIcon(ImageCreator.IMG_CUT);
		i2_copy.setIcon(ImageCreator.IMG_COPY);
		i3_paste.setIcon(ImageCreator.IMG_PASTE);
		i4_select.setIcon(ImageCreator.IMG_SELECTALL);

		// Show -> Nothing , Requests, Responses, Both

		final JMenu ix_show = new JMenu("Show");
		// show.setIcon(ImageCreator.IMG_LKF);
		final String[] showOptions = {"Nothing", "Requests", "Responses", "Both" };
		final ButtonGroup group = new ButtonGroup();

		// Get the default value
		final int showOnTheWire = JBroFuzz.PREFS.getInt(
				JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getId(), 3);

		for (int i = 0; i < showOptions.length; i++) {

			final JRadioButtonMenuItem rButton1 = 
				new JRadioButtonMenuItem(showOptions[i]);
			group.add(rButton1);
			ix_show.add(rButton1);

			if(i == showOnTheWire) {
				rButton1.setSelected(true);
			}

			rButton1.putClientProperty("Show Name", i);

			rButton1.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent iEvent) {
					final JRadioButtonMenuItem rbi = (JRadioButtonMenuItem) iEvent
					.getSource();

					if (rbi.isSelected()) {
						final int selection = (Integer) rbi.getClientProperty("Show Name");

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {

								JBroFuzz.PREFS.putInt(
										JBroFuzzPrefs.FUZZINGONTHEWIRE[1].getId(),
										selection );

							}
						});

					}
				}
			});
		}

		// Add to the popup menu
		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.addSeparator();
		popmenu.add(i0_clear);
		popmenu.add(ix_show);
		popmenu.addSeparator();
		popmenu.add(i4_select);

		if (!area.isEditable()) {
			i3_paste.setEnabled(false);
			i1_cut.setEnabled(false);
		}

		i0_clear.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				mFuzzingPanel.clearOnTheWire();
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


	public static void rightClickFuzzersTable(final FuzzingPanel mFuzzingPanel, final JTable area) {

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

				final StringBuffer myPayloadsBuffer = new StringBuffer();
				for(final String si : fuzzerPayloads) {
					myPayloadsBuffer.append(si);
					myPayloadsBuffer.append("\n\n");
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

				if (mFuzzingPanel.isStopped()) {

					((FuzzersTableModel)area.getModel()).removeRow(area.convertRowIndexToModel(c));

				} else {

					final int choice = JOptionPane.showConfirmDialog(mFuzzingPanel.getFrame(),
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {

						mFuzzingPanel.stop();

						((FuzzersTableModel)area.getModel()).removeRow(area.convertRowIndexToModel(c));
						// .getValueAt(area.convertRowIndexToModel(c), 3);
						// new WindowViewerFrame(mFuzzingPanel, name);						
					}

				}

			}
		});

		// Clear Fuzzers
		i2_clear.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if (mFuzzingPanel.isStopped()) {

					mFuzzingPanel.getFuzzersPanel().clearFuzzersTable();

				} else {
					// Clear the fuzzers if and only if a current fuzzing session is not running
					final int choice = JOptionPane.showConfirmDialog(mFuzzingPanel.getFrame(),
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {

						mFuzzingPanel.stop();

						mFuzzingPanel.getFuzzersPanel().clearFuzzersTable();

					}

				}



			}
		});

		// Copy
		i3_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				final StringBuffer selectionBuffer = new StringBuffer();
				final int[] selection = area.getSelectedRows();

				for (final int element : selection) {
					for (int i = 0; i < area.getColumnCount(); i++) {

						selectionBuffer.append(area.getModel().getValueAt(
								area.convertRowIndexToModel(element), i));
						if (i < area.getColumnCount() - 1) {
							selectionBuffer.append(',');
						}

					}
					selectionBuffer.append('\n');
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
