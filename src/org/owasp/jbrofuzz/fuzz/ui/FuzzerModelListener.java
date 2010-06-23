package org.owasp.jbrofuzz.fuzz.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.owasp.jbrofuzz.ui.AbstractPanel;


/**
 * <p>
 * Inner class used to detect changes to the data managed by the fuzzers
 * table model, where all the fuzzers and corresponding payloads are stored.
 * </p>
 * 
 * <p>
 * This class implements the TableModelListener interface and is called via
 * addTableModelListener() to catch events on the fuzzers table.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.2
 * @since 2.2
 * 
 */
public class FuzzerModelListener implements TableModelListener {

	private final AbstractPanel p;
	private final FuzzerTable t;
	
	public FuzzerModelListener(final AbstractPanel p, final FuzzerTable t) {
	
		this.p = p;
		this.t = t;
		
	}
	
	public void tableChanged(final TableModelEvent event) {

		final int total = t.getRowCount();
		
		if (total > 0) {

			p.setOptionRemove(true);

		} else {

			p.setOptionRemove(false);
		}
	}
}