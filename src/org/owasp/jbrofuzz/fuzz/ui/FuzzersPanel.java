package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.owasp.jbrofuzz.core.Database;
import org.owasp.jbrofuzz.system.Logger;

public class FuzzersPanel extends JPanel {
	
	private static final long serialVersionUID = -9150023615230879357L;
	private FuzzersTableModel mFuzzTableModel;
	private FuzzerTable fuzzersTable;
	private FuzzingPanel fp;

	public FuzzersPanel(final FuzzingPanel fp){
		this.fp = fp;
		this.setLayout(new BorderLayout());
		

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Added Fuzzers Table"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		
		// The fuzzing table and model
		mFuzzTableModel = new FuzzersTableModel();
		fuzzersTable = new FuzzerTable(mFuzzTableModel);

		RightClickPopups.rightClickFuzzersTable(fp, fuzzersTable);
//
//		fuzzersTable.getSelectionModel().addListSelectionListener(
//				new ListSelectionListener() {
//
//					@Override
//					public void valueChanged(ListSelectionEvent e) {
//						final int fRow = fuzzersTable.getSelectedRow();
//
//						final int sFuzz = ((Integer) fuzzersTable.getModel()
//								.getValueAt(fRow, 1)).intValue();
//						final int eFuzz = ((Integer) fuzzersTable.getModel()
//								.getValueAt(fRow, 2)).intValue();
//
//						fp.updateRequestPane(sFuzz, eFuzz);
//
//					}
//				});

		
		final JScrollPane fuzzersScrollPane = new JScrollPane(fuzzersTable);
		fuzzersScrollPane.setVerticalScrollBarPolicy(20);

		add(fuzzersScrollPane, BorderLayout.CENTER);
		
		fuzzersTableSelectionListen();
		
		
	}
	
	
	public void clear(){
		while (fuzzersTable.getRowCount() > 0) {
			mFuzzTableModel.removeRow(0);
		}
	}
	
	/**
	 * <p>
	 * Clear the Fuzzers Table. Also, set the focus on the URL area.
	 * </p>
	 * <p>
	 * Used when right clicking on the fuzzers table, or with a File -> Clear
	 * Fuzzers.
	 * </p>
	 * 
	 * 
	 * @author subere@uncon.org
	 * @version 1.8
	 * @since 1.8
	 */
	public void clearFuzzersTable() {

		while (fuzzersTable.getRowCount() > 0) {
			mFuzzTableModel.removeRow(0);
		}
		fp.getTransformsPanel().clear();
		fp.getUrlField().requestFocusInWindow();

	}


	public int getRowCount() {
		return mFuzzTableModel.getRowCount();
	}

	
	
	/**
	 * update FuzzingTable with new data
	 * 
	 * @author daemonmidi@gmail.com
	 * @since version 2.5
	 * @param FuzzingTable
	 *            ft
	 */
	public void updateFuzzerTable(FuzzerTable ft) {
		fuzzersTable.removeAll();
		JScrollPane scroll = new JScrollPane(ft,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(20);
		add(scroll, BorderLayout.CENTER);
		add(fp.getControlPanel(), BorderLayout.EAST);
		updateUI();
	}
	
	

	private void fuzzersTableSelectionListen() {
		fuzzersTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent arg0) {

						int row = fuzzersTable.getSelectedRow();

						if (row > -1) {
							fp.getTransformsPanel().showTransformsList(row);
						}

					}
				});
	}
	
	/**
	 * <p>
	 * Add a fuzzer to the table of fuzzers.
	 * </p>
	 * 
	 * @param fuzzerId
	 * @param point1
	 * @param point2
	 */
	public void addFuzzer(final String fuzzerId, final int point1,
			final int point2) {

		final Database cDatabase = fp.getFrame().getJBroFuzz().getDatabase();

		if (cDatabase.containsPrototype(fuzzerId)) {

			final String type = cDatabase.getType(fuzzerId);

			mFuzzTableModel.addRow(fuzzerId, type, fuzzerId, point1, point2);

			fp.getTransformsPanel().addTransformsList();

		} else {
			Logger.log("Could not add the Fuzzer with ID: " + fuzzerId, 3);
		}

	}


	public String getCategory(int row) {
		return (String) mFuzzTableModel.getValueAt(row, 0);
		
	}


	public int getStart(int row) {
		return ((Integer) mFuzzTableModel.getValueAt(row, 1)).intValue();
	}


	public int getEnd(int row) {
		return ((Integer) mFuzzTableModel.getValueAt(row, 2)).intValue();
	}
	
	public void remove(boolean isAddedEnabled){
		if (!isAddedEnabled) {
			return;
		}

		
		final int rows = getRowCount();
		if (rows < 1) {
			return;
		}

		final String[] fuzzPoints = new String[rows];
		for (int i = 0; i < rows; i++) {
			fuzzPoints[i] = mFuzzTableModel.getRow(i);
		}

		final String selectedFuzzPoint = (String) JOptionPane.showInputDialog(
				this, "Select fuzzer to remove:", "Remove Fuzzer",
				JOptionPane.INFORMATION_MESSAGE, null, fuzzPoints,
				fuzzPoints[0]);

		if (selectedFuzzPoint != null) {

			mFuzzTableModel.removeRow(Integer.parseInt(selectedFuzzPoint
					.split(" - ")[0]));
			fp.getTransformsPanel().removeTransformsList(Integer.parseInt(selectedFuzzPoint
					.split(" - ")[0]));
		}
	}
	
	public FuzzerTable getFuzzersTable(){
		return fuzzersTable;
	}
	
	public void setFuzzersTable(FuzzerTable ft){
		this.fuzzersTable = ft;
	}


	public FuzzersTableModel getFuzzersTableModel() {
		return mFuzzTableModel;
	}
	
	public void setFuzzersTableModel(FuzzersTableModel ftm){
		this.mFuzzTableModel = ftm;
	}
}
