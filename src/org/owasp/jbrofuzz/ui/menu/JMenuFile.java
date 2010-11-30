/**
 * JBroFuzz 2.4
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
package org.owasp.jbrofuzz.ui.menu;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.owasp.jbrofuzz.db.OpenDatabaseDialog;
import org.owasp.jbrofuzz.fuzz.io.OpenSession;
import org.owasp.jbrofuzz.fuzz.io.SaveAsSession;
import org.owasp.jbrofuzz.fuzz.io.SaveSession;
import org.owasp.jbrofuzz.payloads.OpenLocationDialog;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;

public class JMenuFile extends JMenu {

	private static final long serialVersionUID = -202188806275057795L;

	protected JMenuFile(final JBroFuzzMenuBar mainMenuBar) {
	
		super("File");
		final JBroFuzzWindow mFrameWindow = mainMenuBar.getFrame();
		
		// File
		final JMenuItem newFile = new JMenuItem("New", ImageCreator.IMG_NEW);
		final JMenuItem open = new JMenuItem("Open File...", ImageCreator.IMG_OPEN);
		final JMenuItem close = new JMenuItem("Close");

		final JMenuItem openLocation = new JMenuItem("Open Location...");
		final JMenuItem openDatabase = new JMenuItem("Open Database...");
		
		final JMenuItem clearOutput = new JMenuItem("Clear All Output", ImageCreator.IMG_CLEAR);
		final JMenuItem clearFuzzers = new JMenuItem("Clear All Fuzzers", ImageCreator.IMG_CLEAR);
		final JMenuItem clearOnTheWire = new JMenuItem("Clear On The Wire", ImageCreator.IMG_CLEAR);

		final JMenuItem save = new JMenuItem("Save", ImageCreator.IMG_SAVE);
		final JMenuItem saveAs = new JMenuItem("Save as...", ImageCreator.IMG_SAVE_AS);
		final JMenuItem exit = new JMenuItem("Exit", ImageCreator.IMG_EXIT);

		final JMenuItem loadFuzzers = new JMenuItem("Load Fuzzers...");
		
		newFile.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		close.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		openLocation.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		openDatabase.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));
		
		loadFuzzers.setAccelerator(KeyStroke.getKeyStroke('M', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		clearOnTheWire.setAccelerator(KeyStroke.getKeyStroke('K', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		clearOutput.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		exit.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask(), false));

		this.add(newFile);
		this.add(open);
		this.add(close);
		this.addSeparator();
		this.add(openLocation);
		this.add(openDatabase);
		this.addSeparator();
		this.add(save);
		this.add(saveAs);
		this.addSeparator();
		this.add(loadFuzzers);
		this.addSeparator();
		this.add(clearOutput);
		this.add(clearFuzzers);
		this.add(clearOnTheWire);
		this.addSeparator();
		this.add(exit);


		// File -> New
		newFile.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent even) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (mFrameWindow.getPanelFuzzing().isStopped()) {

					mFrameWindow.getPanelFuzzing().clearAllFields();
					mFrameWindow.setTitle("Untitled");
					// Create a new directory to store all data
					mFrameWindow.getJBroFuzz().getHandler()
					.createNewDirectory();

				} else {

					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						mFrameWindow.getPanelFuzzing().clearAllFields();
						mFrameWindow.setTitle("Untitled");
						// Create a new directory to store all data
						mFrameWindow.getJBroFuzz().getHandler()
						.createNewDirectory();
					}

				}

			}

		});

		// File -> Open
		open.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (mFrameWindow.getPanelFuzzing().isStopped()) {

					new OpenSession(mFrameWindow);

				} else {
					
					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						new OpenSession(mFrameWindow);

					}
				}

			}
		});

		// File -> Close
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (mFrameWindow.getPanelFuzzing().isStopped()) {

					mFrameWindow.setCloseFile();
					mFrameWindow.setTitle("Untitled");

				} else {
					
					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						mFrameWindow.setCloseFile();
						mFrameWindow.setTitle("Untitled");

					}

				}

			}
		});

		// File -> Open Location
		openLocation.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						new OpenLocationDialog(mFrameWindow);

					}

				} else {

					new OpenLocationDialog(mFrameWindow);

				}

			}
		});
		
		
		// File -> Open Database
		openDatabase.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent aEvent){

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						new OpenDatabaseDialog(mFrameWindow);

					}

				} else {

					new OpenDatabaseDialog(mFrameWindow);

				}
				
			}
		});
		
		// File -> Load Fuzzers
		loadFuzzers.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				mFrameWindow.getPanelPayloads().start();

			}
		});


		// File -> Clear Output
		clearOutput.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						mFrameWindow.getPanelFuzzing().clearOutputTable();
						// Create a new directory to store all data
						mFrameWindow.getJBroFuzz().getHandler()
						.createNewDirectory();
					}

				} else {

					mFrameWindow.getPanelFuzzing().clearOutputTable();
					// Create a new directory to store all data
					mFrameWindow.getJBroFuzz().getHandler()
					.createNewDirectory();

				}

			}

		});

		// File -> Clear Fuzzers
		clearFuzzers.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						mFrameWindow.getPanelFuzzing().clearFuzzersTable();

					}

				} else {

					mFrameWindow.getPanelFuzzing().clearFuzzersTable();

				}

			}

		});


		// File -> Clear On The Wire
		clearOnTheWire.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

					mFrameWindow.getPanelFuzzing().clearOnTheWire();

			}

		});
		
		// File -> Save
		save.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (mFrameWindow.getPanelFuzzing().isStopped()) {
					try{
						new SaveSession(mFrameWindow);
					}
					catch(Exception e){
						e.printStackTrace();
					}

				} else {

					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();
						
						try{
							new SaveSession(mFrameWindow);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		});

		// File -> Save as
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent aEvent) {

				mainMenuBar.setSelectedPanelCheckBox(JBroFuzzWindow.ID_PANEL_FUZZING);
				mFrameWindow.setTabShow(JBroFuzzWindow.ID_PANEL_FUZZING);

				if (!mFrameWindow.getPanelFuzzing().isStopped()) {

					final int choice = JOptionPane.showConfirmDialog(mFrameWindow,
							"Fuzzing Session Running. Stop Fuzzing?",
							" JBroFuzz - Stop ", JOptionPane.YES_NO_OPTION);

					if (choice == JOptionPane.YES_OPTION) {
						final int c = mFrameWindow.getTp().getSelectedIndex();
						final AbstractPanel p = (AbstractPanel) mFrameWindow.getTp()
						.getComponent(c);
						p.stop();

						new SaveAsSession(mFrameWindow);

					}

				} else {

					new SaveAsSession(mFrameWindow);

				}

			}
		});

		// File -> Exit
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						mFrameWindow.closeFrame();
					}
				});
			}
		});
		
	}
	
}
