/**
 * JBroFuzz 2.45
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

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.owasp.jbrofuzz.encode.EncoderHashCore;

public class TransformsPanel extends JPanel{
	
	/**
	 * <p>
	 * The "Transforms Panel" is a self contained JPanel intended to split up & simplify the "Fuzzing Panel".
	 * </p>
	 */
	private static final long serialVersionUID = 7196885404320613786L;
	private TransformsTableList transformsTableList;
	private TransformsToolBar controlPanel;
	private FuzzingPanel fp;

	public TransformsPanel(FuzzingPanel fp){
		this.fp = fp;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createCompoundBorder(
						BorderFactory
								.createTitledBorder(" Added Fuzzer Transforms (rules applied top first) "),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		// create the transformsTableList
		transformsTableList = new TransformsTableList(fp);
		// instantiate the control panel
		controlPanel = new TransformsToolBar(fp);
		// add a null transform to start with
		this.updateTransformsPanel(null);	
	}

	/**
	 * <b>updateTransformsPanel</b>
	 * <p>
	 * A method to show the transforms table linked to the fuzzer which has been
	 * selected
	 * </p>
	 * 
	 * @param in
	 * @author RG
	 */
	public void updateTransformsPanel(TransformsTable in) {
		removeAll();
		JScrollPane scroll = new JScrollPane(in,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(20);
		add(scroll, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.EAST);
		updateUI();
		fp.updateUI();
		
		System.out.println("adding transform");
	}

	public void addTableList() {
		transformsTableList.add();
		
	}
	
	public TransformsRow[] getTransforms(int fuzzerRow) {

		TransformsTableModel a = transformsTableList
				.getTransformsTableModel(fuzzerRow);

		if (a == null) {
			TransformsRow row = new TransformsRow("Plain Text", "", "");
			return new TransformsRow[] { row };
		}
		return a.getTransforms();
	}


	public void removeList(int i) {
		transformsTableList.remove(i);
		
	}

	public TransformsTableList getTransformsTableList() {
		return transformsTableList;
	}
	
	
	/**
	 * <p>
	 * Method for adding a transform to a fuzzer.
	 * </p>
	 * <p>
	 * This will appear within the transforms table, if the corresponding fuzzer
	 * is clicked.
	 * </p>
	 * 
	 * @param fuzzerNumber
	 * @param transform
	 * @param prefix
	 * @param suffix
	 */
	public void addTransform(int fuzzerNumber, String transform, String prefix,
			String suffix) {

		transformsTableList.getTransformsTableModel(fuzzerNumber - 1).addRow(
				transform, prefix, suffix);
		// TransformsTableList.add(transform, prefix, suffix);

	}

	public void showRow(int row) {
		transformsTableList.show(row);
		
	}

	public TransformsRow[] addRow(String string, String string2, String string3) {
		TransformsRow row = new TransformsRow("Plain Text", "", "");
		return new TransformsRow[] { row };
		
	}

	public static String encodeMany(String payload, TransformsRow[] transforms) {
		return EncoderHashCore.encodeMany(payload,
				transforms);
	}
	
	public void clear(){
		while (transformsTableList.getSize() > 0) {
			removeList(0);
		}
	}

	public TransformsToolBar getTransformsToolBar() {
		return controlPanel;
	}
	
	
	
}
