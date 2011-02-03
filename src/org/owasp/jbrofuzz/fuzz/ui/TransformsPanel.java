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
	// private TransformsTableList transformsTableList;
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

//		// create the transformsTableList
//		transformsTableList = new TransformsTableList(fp);
//		// instantiate the control panel
//		controlPanel = new TransformsToolBar(fp);
//		// add a null transform to start with
//		this.updateTransformsPanel(null);	
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
//	public void updateTransformsPanel(TransformsTable in) {
//		removeAll();
//		JScrollPane scroll = new JScrollPane(in,
//				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		scroll.setVerticalScrollBarPolicy(20);
//		add(scroll, BorderLayout.CENTER);
//
//		updateUI();
//		fp.updateUI();
//		
//
//	}

	public void addTransformsList() {
		TransformsTableModel tm = new TransformsTableModel();
		//tm.addRow(new TransformsRow());
		transformsLists.add(tm);
		System.out.println(transformsLists.size());
		transformsTable.setModel(tm);
		transformsTable.updateUI();
		this.updateUI();
//		transformsTableList.add();
		
	}
	
	public TransformsTableModel getTransforms(int fuzzerRow) {
		return transformsLists.get(fuzzerRow);
	}
	
	
//	public TransformsRow[] getTransforms(int fuzzerRow) {
//
//		TransformsTableModel a = transformsTableList
//				.getTransformsTableModel(fuzzerRow);
//
//		if (a == null) {
//			TransformsRow row = new TransformsRow("Plain Text", "", "");
//			return new TransformsRow[] { row };
//		}
//		return a.getTransforms();
//	}


	public void removeTransformsList(int i) {
		transformsLists.remove(i);
		
	}

//	public TransformsList getTransformsTableList() {
//		return transformsTableList;
//	}
	
	
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
		
		//		transformsTableList.getTransformsTableModel(fuzzerNumber - 1).addRow(
//				transform, prefix, suffix);
		// TransformsTableList.add(transform, prefix, suffix);

	}
	
	public void addTransform(int fuzzer){
		TransformsRow tr = new TransformsRow();
		System.out.println(tr.getEncoder()+ " " + tr.getPrefixOrMatch());
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

//	public void showTransformsForFuzzer(int row) {
//		final int trSize = transformsLists.size();
//		if (trSize < 1) {
//			return;
//		}	
//	
//		
//		if( (row < trSize) && (row >= 0) ) {
//		
//			
//			TransformsTableModel tl = transformsLists.get(row);
//			 transformsTable.setModel(tl);
//			 transformsTable.updateUI();
//			 updateTransformsPanel(new TransformsTable(tl));
//		
//
//			
//		}
//		
//	}

//	public TransformsRow[] addRow(String string, String string2, String string3) {
//		TransformsRow row = new TransformsRow("Plain Text", "", "");
//		return new TransformsRow[] { row };
//		
//	}

//	public static String encodeMany(String payload, TransformsRow[] transforms) {
//		return EncoderHashCore.encodeMany(payload,
//				transforms);
//	}
	
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
