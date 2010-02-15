/**
 * JBroFuzz 1.9
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
package org.owasp.jbrofuzz.ui.prefs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

class DirsPPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = -7358976067347647005L;

	protected DirsPPanel(final PrefDialog dialog) {
		super("Directory Locations");
		
		// Directory Locations... Directory Save Browse

		final String dir = dialog.getJBroFuzzWindow().getJBroFuzz().getHandler().getCanonicalPath();

		final JPanel dirPanel = new JPanel();
		dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.LINE_AXIS));
		// A very important line when it comes to BoxLayout
		dirPanel.setAlignmentX(0.0f);
		dirPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Fuzzing Directory (where data is saved) "), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));

		final JTextField dirTextField = new JTextField(dir);
		dirTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		dirTextField.setMargin(new Insets(1, 1, 1, 1));

		final JButton browseDirButton = new JButton("Browse");
		browseDirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		// For 2.0 release
		browseDirButton.setEnabled(false);
		
		JFileChooser chooser;

		browseDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						JFileChooser chooserD = new JFileChooser();

						chooserD.setCurrentDirectory(new File("."));
						chooserD.setDialogTitle("Select Directory to Save Fuzzing Data");
						chooserD.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooserD.setAcceptAllFileFilterUsed(false);

						if (chooserD.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
							
							File selDirFile = chooserD.getSelectedFile();
							if(selDirFile.isDirectory()) {
//								JBroFuzz.PREFS.put("Save.Location", selDirFile.toString());
//								directoryTextField.setText(selDirFile.toString());
								// parent.getJBroFuzz().getHandler().setFuzzDirectory(selDirFile);
							}
//							System.out.println("getCurrentDirectory(): " 
//									+  chooserD.getCurrentDirectory());
//							System.out.println("getSelectedFile() : " 
//									+  chooserD.getSelectedFile());
						}
//						else {
//							System.out.println("No Selection ");
//						}

					}
				});
			}
		});

		dirPanel.add(dirTextField, BorderLayout.NORTH);
		dirPanel.add(browseDirButton);

		// Directory Locations... Directory -> Delete directories check-box

		final boolean deletebox = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.DIRS[0], false);
		final JCheckBox deleteCheckBox = new JCheckBox(
				" On exit, delete any empty directories created at startup ",
				deletebox);

		deleteCheckBox.setBorderPaintedFlat(true);
		deleteCheckBox
		.setToolTipText("Tick this option, if you would like to remove any empty directories");

		deleteCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (deleteCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.DIRS[0], true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.DIRS[0], false);
				}
			}
		});

		add(dirPanel);
		add(Box.createRigidArea(new Dimension(0, 20)));
		add(deleteCheckBox);
		add(Box.createRigidArea(new Dimension(0, 200)));


	}
	
	public void apply() { }
}
