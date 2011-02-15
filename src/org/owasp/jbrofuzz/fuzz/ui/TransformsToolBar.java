package org.owasp.jbrofuzz.fuzz.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.owasp.jbrofuzz.version.ImageCreator;

/**
 * <p>
 * The encoders panel toolbar implementation - contains methods to enable/disable and
 * monitor button press actions in the toolbar.
 * </p>
 * 
 * @author ranulf
 *
 */
public class TransformsToolBar extends JToolBar {

	private static final long serialVersionUID = 9074400815038517325L;
	private JButton upAll;
	private JButton upOne;
	private JButton downOne;
	private JButton downAll;
	private JButton add;
	private JButton delete;
	private FuzzingPanel container;

	public TransformsToolBar(FuzzingPanel container){

		this.container = container;

		upAll = new JButton(ImageCreator.IMG_UPALL);
		upOne = new JButton(ImageCreator.IMG_UP);
		downOne = new JButton(ImageCreator.IMG_DOWN);
		downAll = new JButton(ImageCreator.IMG_DOWNALL);
		delete = new JButton(ImageCreator.IMG_REMOVE);
		add = new JButton(ImageCreator.IMG_ADD);
		add(upAll);
		add(upOne);
		add(downOne);
		add(downAll);
		add(delete);
		add(add);
		setOrientation(JToolBar.VERTICAL);
		setFloatable(false);
		addActionListeners();

		upAll.setToolTipText("Move selected encoder to the top");
		upOne.setToolTipText("Move selected encoder up one");
		downOne.setToolTipText("Move selected encoder down one");
		downAll.setToolTipText("Move selected encoder to the bottom");
		delete.setToolTipText("Remove the selected encoder");
		add.setToolTipText("Add a new encoder");
		disableAll();

	}

	public void disableAll(){
		disablePositionModifiers();
		disableDelete();
		disableAdd();
		container.updateUI();
	}

	public void enableAll(){
		enablePositionModifiers();
		enableDelete();
		enableAdd();
		container.updateUI();		
	}


	public void enablePositionModifiers(){
		upAll.setEnabled(true);
		upOne.setEnabled(true);
		downAll.setEnabled(true);
		downOne.setEnabled(true);	
		container.updateUI();
	}

	public void disablePositionModifiers(){
		upAll.setEnabled(false);
		upOne.setEnabled(false);
		downAll.setEnabled(false);
		downOne.setEnabled(false);	
		container.updateUI();
	}

	public void enableAdd(){
		add.setEnabled(true);
		container.updateUI();
	}

	public void enableDelete(){
		delete.setEnabled(true);
		container.updateUI();
	}

	public void disableAdd(){
		add.setEnabled(false);
		container.updateUI();
	}	

	public void disableDelete(){
		delete.setEnabled(false);
		container.updateUI();
	}





	private void addActionListeners(){

		// create the add button action listener
		add.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersPanel().getFuzzersTable().getSelectedRow();
				if(index!=-1){
					TransformsTableModel tl = container.getTransformsPanel().getTransformsTableModel(index);
					tl.addRow(new TransformsRow());
					if(tl.getRowCount() ==1){
						enableDelete();
					}else if(tl.getRowCount() == 2){
						enablePositionModifiers();
					}
					if(container.getTransformsPanel().getTransformsTable().editCellAt(container.getTransformsPanel().getTransformsTableModel(index).getRowCount()-1, 0)){
						container.getTransformsPanel().getTransformsTable().changeSelection(container.getTransformsPanel().getTransformsTableModel(index).getRowCount()-1, 0, true, false);
					}
				}				
			}
		});

		// create the upAll button action listener
		upAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersPanel().getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getTransformsPanel().getTransformsTable().getSelectedRow();
					int selectedColumn = container.getTransformsPanel().getTransformsTable().getSelectedColumn();
					if(selectedRow>0){
						stopEdit();
						container.getTransformsPanel().getTransformsTableModel(index).moveRowUpAll(selectedRow);
						if(container.getTransformsPanel().getTransformsTable().editCellAt(0, selectedColumn)){
							container.getTransformsPanel().getTransformsTable().changeSelection(0, selectedColumn, false, false);
						}
					}
				}
			}
		});

		// create the upOne button action listener
		upOne.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersPanel().getFuzzersTable().getSelectedRow();
				int selectedColumn = container.getTransformsPanel().getTransformsTable().getSelectedColumn();
				if(index!=-1){
					int selectedRow = container.getTransformsPanel().getTransformsTable().getSelectedRow();
					if(selectedRow>0){
						stopEdit();
						container.getTransformsPanel().getTransformsTableModel(index).moveRowUpOne(selectedRow);
						if(container.getTransformsPanel().getTransformsTable().editCellAt(selectedRow-1, selectedColumn)){
							container.getTransformsPanel().getTransformsTable().changeSelection(selectedRow-1, selectedColumn, false, false);
						}
					}
				}
			}
		});

		// create the downOne button action listener
		downOne.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersPanel().getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getTransformsPanel().getTransformsTable().getSelectedRow();
					int selectedColumn = container.getTransformsPanel().getTransformsTable().getSelectedColumn();
					if(selectedRow>-1 && selectedRow < container.getTransformsPanel().getTransformsTableModel(index).getRowCount()-1){
						stopEdit();
						container.getTransformsPanel().getTransformsTableModel(index).moveRowDownOne(selectedRow);
						if(container.getTransformsPanel().getTransformsTable().editCellAt(selectedRow+1, selectedColumn)){
							container.getTransformsPanel().getTransformsTable().changeSelection(selectedRow+1, selectedColumn, false, false);
						}
					}
				}
			}
		});

		// create the downAll button action listener
		downAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersPanel().getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getTransformsPanel().getTransformsTable().getSelectedRow();
					int selectedColumn = container.getTransformsPanel().getTransformsTable().getSelectedColumn();			
					if(selectedRow>-1 && selectedRow < container.getTransformsPanel().getTransformsTableModel(index).getRowCount()-1){
						stopEdit();
						container.getTransformsPanel().getTransformsTableModel(index).moveRowDownAll(selectedRow);
						if(container.getTransformsPanel().getTransformsTable().editCellAt(container.getTransformsPanel().getTransformsTableModel(index).getRowCount()-1, selectedColumn)){
							container.getTransformsPanel().getTransformsTable().changeSelection(container.getTransformsPanel().getTransformsTableModel(index).getRowCount()-1, selectedColumn, false, false);
						}
					}
				}
			}
		});

		// create the delete button action listener
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersPanel().getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getTransformsPanel().getTransformsTable().getSelectedRow();
					if(selectedRow !=-1){
						stopEdit();
						container.getTransformsPanel().getTransformsTableModel(index).removeRow(selectedRow);
						int numRows = container.getTransformsPanel().getTransformsTable().getRowCount();
						if(numRows == 0){
							disableDelete();
						}else if(numRows == 1){
							disablePositionModifiers();
						}
					}
				}
			}
		});
	}


	private void stopEdit(){
		if (container.getTransformsPanel().getTransformsTable().getCellEditor() != null) {
			container.getTransformsPanel().getTransformsTable().getCellEditor().stopCellEditing();
		}
	}
}
