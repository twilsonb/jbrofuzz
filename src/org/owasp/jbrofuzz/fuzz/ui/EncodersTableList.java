package org.owasp.jbrofuzz.fuzz.ui;

import java.util.ArrayList;
import org.owasp.jbrofuzz.fuzz.FuzzingPanel;



/**
 * <p>This class is desinged to maintain a list of transform
 * models, which are selectable when a fuzzer is clicked on
 * the left hand side.</p>
 * 
 * @author ranulf
 * @version 2.5
 * @since 2.3
 *
 */
public class EncodersTableList{
	
	ArrayList<EncodersTable>encodersTables;
	FuzzingPanel container;
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since verison 2.5
	 * @return int count of encoders within the table
	 */
	public int getSize(){
		return encodersTables.size();
	}
	
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
		
	}
	
	
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

	public void add(String encoding, String matchOrPrefix, String replaceOrSuffix) {
		EncodersTableModel etm = new EncodersTableModel();
		EncodersTable et = new EncodersTable(etm);
		encodersTables.add(et);
		etm.addRow(encoding, matchOrPrefix, replaceOrSuffix);
		
	}

}
