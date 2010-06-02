/**
 * JBroFuzz 2.2
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
import java.io.IOException;

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
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

class DirsPPanel extends AbstractPrefsPanel {

	private final JTextField dirTextField;
	
	private final PrefDialog dialog;
	
	private final JCheckBox dirBox;
	
	protected DirsPPanel(final PrefDialog dialog) {
		
		super("Directory Locations");
		this.dialog = dialog;
		
		// Directory Locations... Directory Save Browse

		final String dir = dialog.getJBroFuzzWindow().getJBroFuzz().getHandler().getCanonicalPath();

		final JPanel dirPanel = new JPanel();
		dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.LINE_AXIS));
		// A very important line when it comes to BoxLayout
		dirPanel.setAlignmentX(0.0f);
		
		// Tick box for selecting your own directory
		final boolean boolEntry = JBroFuzz.PREFS.getBoolean(JBroFuzzPrefs.DIRS[1].getId(), true);
		dirBox = new JCheckBox(JBroFuzzPrefs.DIRS[1].getTitle(), boolEntry);
		dirBox.setToolTipText(JBroFuzzPrefs.DIRS[1].getTooltip());
		dirBox.setBorderPaintedFlat(true);
		
		add(dirBox);
		add(Box.createRigidArea(new Dimension(0, 20)));
		
		// Preference for the directory location where files are stored
		dirPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(JBroFuzzPrefs.DIRS[0].getTitle()), BorderFactory.createEmptyBorder(
						1, 1, 1, 1)));
		dirPanel.setToolTipText(JBroFuzzPrefs.DIRS[0].getTooltip());
		dirTextField = new JTextField(dir);
		dirTextField.setFont(new Font("Verdana", Font.PLAIN, 12));
		dirTextField.setMargin(new Insets(1, 1, 1, 1));

		final JButton browseDirButton = new JButton("Browse");
		browseDirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		// For 2.0 release
		browseDirButton.setEnabled(true);
		
		dirPanel.add(dirTextField, BorderLayout.NORTH);
		dirPanel.add(browseDirButton);
		add(dirPanel);
		add(Box.createRigidArea(new Dimension(0, 300)));
		
		if(boolEntry) {
			browseDirButton.setEnabled(true);
			dirTextField.setEnabled(true);
		} else {
			browseDirButton.setEnabled(false);
			dirTextField.setEnabled(false);
		}
		// Listener for the tick box
		dirBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				if(dirBox.isSelected()) {
					browseDirButton.setEnabled(true);
					dirTextField.setEnabled(true);
				} else {
					browseDirButton.setEnabled(false);
					dirTextField.setEnabled(false);
				}
				dialog.setApplyEnabled(true);

			}
		});
		
		// Listener for the browse directory
		browseDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						final JFileChooser chooserD = new JFileChooser();

						chooserD.setCurrentDirectory(new File("."));
						chooserD.setDialogTitle(JBroFuzzPrefs.DIRS[0].getTooltip());
						chooserD.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooserD.setAcceptAllFileFilterUsed(false);

						if (chooserD.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
							
							final File selDirFile = chooserD.getSelectedFile();
							if(selDirFile.isDirectory()) {

								try {
									
									dirTextField.setText(selDirFile.getCanonicalPath());
									
								} catch (IOException e) {
									
									Logger.log(e.getMessage(), 4);
									
								}
								
							}

						}

					}
				});
			}
		});

		


	}
	
	public void apply() {
		
		JBroFuzz.PREFS.put(JBroFuzzPrefs.DIRS[0].getId(), dirTextField.getText());
		
		if(dirBox.isSelected()) {
			JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.DIRS[1].getId(), true);
			dialog.getJBroFuzzWindow().getJBroFuzz().getHandler().createNewDirectory();
			dirTextField.setText(dialog.getJBroFuzzWindow().getJBroFuzz().getHandler().getCanonicalPath());
		} else {
			JBroFuzz.PREFS.putBoolean(JBroFuzzPrefs.DIRS[1].getId(), false);
		}
		
	}
}
