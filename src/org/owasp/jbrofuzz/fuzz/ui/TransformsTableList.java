package org.owasp.jbrofuzz.fuzz.ui;

import java.util.ArrayList;



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
public class TransformsTableList{
	
	ArrayList<TransformsTable>encodersTables;
	FuzzingPanel container;
	
	/**
	 * @author daemonmidi@gmail.com
	 * @since verison 2.5
	 * @return int count of encoders within the table
	 */
	public int getSize(){
		return encodersTables.size();
	}
	
	public TransformsTableList(FuzzingPanel container){
		this.container = container;
		encodersTables = new ArrayList<TransformsTable>();
		encodersTables.trimToSize();
	}
	
	public int getTransformsCount(int in){
		TransformsTable t = encodersTables.get(in);
		return t.getRowCount();

	}
	
	public void show(int in){
		if(in >= 0){
			TransformsTable t = encodersTables.get(in);
			TransformsTableModel etm = (TransformsTableModel) encodersTables.get(in).getModel();
			etm.fireTableDataChanged();
			container.getTransformsPanel().updateTransformsPanel(t);
			container.getTransformsPanel().getTransformsToolBar().enableAdd();
			if(etm.getRowCount()==1){
				container.getTransformsPanel().getTransformsToolBar().enableDelete();
			}
			if(etm.getRowCount()>1){
				container.getTransformsPanel().getTransformsToolBar().enablePositionModifiers();
			}
		}else{
			container.getTransformsPanel().updateTransformsPanel(null);
			container.getTransformsPanel().getTransformsToolBar().disableAll();
		}
	
	}
	
	public void add(){
		
		TransformsTableModel etm = new TransformsTableModel();
		TransformsTable et = new TransformsTable(etm);
		encodersTables.add(et);
		
	}
	
	
	public TransformsTable getEncoderTable(int index){
		return encodersTables.get(index);
	}
	
	public TransformsTableModel getTransformsTableModel(int index){		
		return (TransformsTableModel) encodersTables.get(index).getModel();
	}
		
	public void remove(int in){
		
		encodersTables.remove(in);
		
		if(encodersTables.size()==0){
			container.getTransformsPanel().getTransformsToolBar().disableAll();
			container.getTransformsPanel().getTransformsToolBar().enableAdd();
		}
		
		int selection = container.getFuzzersPanel().getFuzzersTable().getSelectedRow();
		if (selection != -1){
			show(selection);
		}else{
			show(-1);
		}
		
	}

	public void add(String encoding, String matchOrPrefix, String replaceOrSuffix) {
		TransformsTableModel etm = new TransformsTableModel();
		TransformsTable et = new TransformsTable(etm);
		encodersTables.add(et);
		etm.addRow(encoding, matchOrPrefix, replaceOrSuffix);
		
	}

}
