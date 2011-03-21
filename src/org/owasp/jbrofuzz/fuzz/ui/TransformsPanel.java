/**
 * JbroFuzz 2.55
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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TransformsPanel extends JPanel{
	
	/**
	 * <p>
	 * The "Transforms Panel" is a self contained JPanel intended to split up & simplify the "Fuzzing Panel".
	 * </p>
	 */
	private static final long serialVersionUID = 7196885404320613786L;
	private TransformsToolBar controlPanel;
	private FuzzingPanel fp;
	
	private TransformsTable transformsTable;
	private ArrayList<TransformsTableModel> transformsLists;
	
	public TransformsPanel(FuzzingPanel fp){
		this.fp = fp;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createCompoundBorder(
						BorderFactory
								.createTitledBorder(" Added Fuzzer Transforms (rules applied top first) "),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		transformsTable = new TransformsTable(new TransformsTableModel());
		
 		transformsLists = new ArrayList<TransformsTableModel>();
 		
		
		controlPanel = new TransformsToolBar(fp);
		
		final JScrollPane fuzzersScrollPane = new JScrollPane(transformsTable);
		fuzzersScrollPane.setVerticalScrollBarPolicy(20);

		add(fuzzersScrollPane, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.EAST);

	}

	public void addTransformsList() {
		TransformsTableModel tm = new TransformsTableModel();
		transformsLists.add(tm);
		transformsTable.setModel(tm);
		transformsTable.updateUI();
		this.updateUI();
		
	}
	
	public TransformsTableModel getTransforms(int fuzzerRow) {
		return transformsLists.get(fuzzerRow);
	}
	
	
	public void removeTransformsList(int i) {
		transformsLists.remove(i);
		
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
		
		TransformsRow tr = new TransformsRow(transform, prefix, suffix);
		transformsLists.get(fuzzerNumber).addRow(tr);
	
	}
	
	public void addTransform(int fuzzer){
		TransformsRow tr = new TransformsRow();
		transformsLists.get(fuzzer).addRow(tr);
	}
	
	public void showTransformsList(int row){
		TransformsTableModel tl = transformsLists.get(row);
		 transformsTable.setModel(tl);
		 transformsTable.updateUI();
			if (row == 0) {
				fp.getControlPanel().disableAll();
				fp.getControlPanel().enableAdd();
			} else if (row == 1) {
				fp.getControlPanel().disableAll();
				fp.getControlPanel().enableAdd();
				fp.getControlPanel().enableDelete();
			} else {
				fp.getControlPanel().enableAll();
			}
	}
	
	public void clear(){
//		while (transformsTable.getRowCount() > 0) {
//			transformsTable.getModel().removeRow(0);
//		}
	}

	public TransformsToolBar getTransformsToolBar() {
		return controlPanel;
	}

	public TransformsTable getTransformsTable() {
		return transformsTable;
	}

	public TransformsTableModel getTransformsTableModel(int index) {
			return transformsLists.get(index);
	}
	
	
	
}
