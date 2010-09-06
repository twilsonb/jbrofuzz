package org.owasp.jbrofuzz.fuzz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.owasp.jbrofuzz.encode.EncoderHashCore;
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
public class EncodersToolBar extends JToolBar {
	
	private static final long serialVersionUID = 9074400815038517325L;
	private JButton upAll;
	private JButton upOne;
	private JButton downOne;
	private JButton downAll;
	private JButton add;
	private JButton delete;
	private FuzzingPanel container;

	public EncodersToolBar(FuzzingPanel container){
		
		this.container = container;
		
	// TODO: find appropriate images and add them to image creator
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
				int index = container.getFuzzersTable().getSelectedRow();
				if(index!=-1){
					container.getEncodersTableList().getEncoderTableModel(index).addRow(EncoderHashCore.CODES[0], "", "");
					if(container.getEncodersTableList().getEncoderTableModel(index).getRowCount()==1){
						enableDelete();
					}else if(container.getEncodersTableList().getEncoderTableModel(index).getRowCount()==2){
						enablePositionModifiers();
					}	
				}				
			}
		});
				
		// create the upAll button action listener
		upAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getEncodersTableList().getEncoderTable(index).getSelectedRow();
					if(selectedRow!=-1 && selectedRow!=0){
						container.getEncodersTableList().getEncoderTableModel(index).moveRowUpAll(selectedRow);
						container.getEncodersTableList().getEncoderTable(index).getSelectionModel().setSelectionInterval(0, 0);
					}
				}
			}
		});
		
		// create the upOne button action listener
		upOne.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getEncodersTableList().getEncoderTable(index).getSelectedRow();
					if(selectedRow!=-1 && selectedRow!=0){
						container.getEncodersTableList().getEncoderTableModel(index).moveRowUpOne(selectedRow);
						container.getEncodersTableList().getEncoderTable(index).getSelectionModel().setSelectionInterval(selectedRow-1, selectedRow-1);		
					}		
				}
			}
		});
		
		// create the downOne button action listener
		downOne.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getEncodersTableList().getEncoderTable(index).getSelectedRow();
					if(selectedRow!=-1 && selectedRow!=container.getEncodersTableList().getEncoderTable(index).getRowCount()-1){
						container.getEncodersTableList().getEncoderTableModel(index).moveRowDownOne(selectedRow);
						container.getEncodersTableList().getEncoderTable(index).getSelectionModel().setSelectionInterval(selectedRow+1, selectedRow+1);
					}
				}
			}
		});
		
		// create the downAll button action listener
		downAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getEncodersTableList().getEncoderTable(index).getSelectedRow();
					if(selectedRow!=-1 && selectedRow!=container.getEncodersTableList().getEncoderTable(index).getRowCount()-1){
						container.getEncodersTableList().getEncoderTableModel(index).moveRowDownAll(selectedRow);
						container.getEncodersTableList().getEncoderTable(index).getSelectionModel().setSelectionInterval(container.getEncodersTableList().getEncoderTable(index).getRowCount()-1, container.getEncodersTableList().getEncoderTable(index).getRowCount()-1);
					}
				}
			}
		});
		
		// create the delete button action listener
		delete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int index = container.getFuzzersTable().getSelectedRow();
				if(index!=-1){
					int selectedRow = container.getEncodersTableList().getEncoderTable(index).getSelectedRow();
					container.getEncodersTableList().getEncoderTableModel(index).removeRow(selectedRow);
					if(container.getEncodersTableList().getEncoderTableModel(index).getRowCount()==0){
						disableDelete();
					}else if(container.getEncodersTableList().getEncoderTableModel(index).getRowCount()==1){
						disablePositionModifiers();
					}
					container.getEncodersTableList().getEncoderTable(index).getSelectionModel().setSelectionInterval(0,0);
				}
			}
		});
	}

}
