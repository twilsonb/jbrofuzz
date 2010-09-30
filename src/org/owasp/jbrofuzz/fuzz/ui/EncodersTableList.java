package org.owasp.jbrofuzz.fuzz.ui;

import java.util.ArrayList;
import org.owasp.jbrofuzz.fuzz.FuzzingPanel;



/**
 * <p>
 * This class is desinged to maintain a list of encodersTables/models which can be selected based on
 * the fuzzer which is selected in the LH pane.
 * </p>
 * 
 * @author ranulf
 * @version 2.5
 * @since 2.3
 *
 */
public class EncodersTableList{
	
	ArrayList<EncodersTable>encodersTables;
	FuzzingPanel container;
	
	public EncodersTableList(FuzzingPanel container){
		this.container = container;
		encodersTables = new ArrayList<EncodersTable>();
		encodersTables.trimToSize();
	}
	
	public int getEncoderCount(int in){
		EncodersTable t = encodersTables.get(in);
		return t.getRowCount();

	}
	
	public void show(int in){
		if(in >= 0){
			EncodersTable t = encodersTables.get(in);
			EncodersTableModel etm = (EncodersTableModel) encodersTables.get(in).getModel();
			etm.fireTableDataChanged();
			container.updateEncoderPanel(t);
			container.getEncoderToolBar().enableAdd();
			if(etm.getRowCount()==1){
				container.getEncoderToolBar().enableDelete();
			}
			if(etm.getRowCount()>1){
				container.getEncoderToolBar().enablePositionModifiers();
			}
		}else{
			container.updateEncoderPanel(null);
			container.getEncoderToolBar().disableAll();
		}
	
	}
	
	public void add(){
		
		EncodersTableModel etm = new EncodersTableModel();
		EncodersTable et = new EncodersTable(etm);
		encodersTables.add(et);
		
////		if(encodersTables.size()==1){
//			etm.fireTableDataChanged();
//			container.updateEncoderPanel(et);			
//			container.getEncoderToolBar().enableAdd();
////		}
	}
	
//	public void add(String encoder){
//		EncodersTableModel etm = new EncodersTableModel();
//		EncodersTable et = new EncodersTable(etm);
//		etm.setValueAt(encoder, etm.getRowCount(), 0);
//		encodersTables.add(et);
//		if(encodersTables.size()==1){
//			etm.fireTableDataChanged();
//			container.updateEncoderPanel(et);
//			container.getEncoderToolBar().enableAdd();
//		}		
//	}
//	
//	public void addAll(String[] encoders){
//		for(int i=0;i<encoders.length;i++){
//			this.add(encoders[i]);
//		}
//	}
	
	
	public EncodersTable getEncoderTable(int index){
		return encodersTables.get(index);
	}
	
	public EncodersTableModel getEncoderTableModel(int index){		
		return (EncodersTableModel) encodersTables.get(index).getModel();
	}
		
	public void remove(int in){
		
		encodersTables.remove(in);
		
		if(encodersTables.size()==0){
			container.getEncoderToolBar().disableAll();
			container.getEncoderToolBar().enableAdd();
		}
		
		int selection = container.getFuzzersTable().getSelectedRow();
		if (selection != -1){
			show(selection);
		}else{
			show(-1);
		}
		
	}

}
