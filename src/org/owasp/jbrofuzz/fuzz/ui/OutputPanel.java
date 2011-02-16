/**
 * JBroFuzz 2.5
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
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.fuzz.io.FuzzFileUtils;
import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.system.Logger;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;


/**
 *<p>
 * OutputPanel.java
 *</p>
 *<p>
 * Class representing the outputPanel tab. 
 *</p>
 */
public class OutputPanel extends JPanel{
	
	private static final long serialVersionUID = -8246023219146780640L;
	private TableRowSorter<OutputTableModel> outputSorter;
	private JTextPane outputRequestPane, outputResponsePane;
	private OutputTableModel outputTableModel;
	private OutputTable mOutputTable;
	private JCheckBox csxReq;
	private JCheckBox csxRes;
	private int lastIndex;
	private Color HILIT_COLOR = Color.LIGHT_GRAY;
	private FuzzingPanel fp;
	
	public OutputPanel(FuzzingPanel fp){
		this.fp = fp;
		createOutputPanel();
		
	}
	
	public OutputTable getOutputTable(){
		return mOutputTable;
	}
	
	public void setOutputTable(OutputTable ot){
		this.mOutputTable = ot;
	}
	
	public OutputTableModel getOutputTableModel(){
		return outputTableModel;
	}
	
	public void setOutputTableModel(OutputTableModel otm){
		this.outputTableModel = otm;
	}
	
	public void clear(){
		outputTableModel.clearAllRows();
		outputRequestPane.setText("");
		outputResponsePane.setText("");
	}
	
	private void createOutputPanel() {
	
		// the output split pane model
		final FuzzSplitPane outputMainPane = new FuzzSplitPane(
				FuzzSplitPane.VERTICAL_SPLIT, FuzzSplitPane.OUTPUT_MAIN);
		final FuzzSplitPane outputBottomPane = new FuzzSplitPane(
				FuzzSplitPane.HORIZONTAL_SPLIT, FuzzSplitPane.OUTPUT_BOTTOM);

		// The output panel
		final JPanel outputTablePanel = new JPanel(new BorderLayout());

		// Update the border of the output panel
		outputTablePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(" Output "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		outputTableModel = new OutputTableModel();
		mOutputTable = new OutputTable(outputTableModel);
		outputSorter = new TableRowSorter<OutputTableModel>(outputTableModel);
		mOutputTable.setRowSorter(outputSorter);
		final JScrollPane outputScrollPane = new JScrollPane(mOutputTable);
		// mOutputTable.setFillsViewportHeight(true);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		// outputScrollPane.setPreferredSize(new Dimension(840, 130));
		outputTablePanel.add(outputScrollPane);
		outputTablePanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		RightClickPopups.rightClickOutputTable(fp, mOutputTable);

		outputRequestPane = fp.createSimplePane();
		outputRequestPane.setEditable(false);
		outputResponsePane = fp.createSimplePane();
		outputResponsePane.setEditable(false);

		JPanel bL = new JPanel(new BorderLayout());
		JPanel bR = new JPanel(new BorderLayout());

		bL.add(FuzzingPanel.createScrollingPanel(" Request ", outputRequestPane),
				BorderLayout.CENTER);
		bR.add(FuzzingPanel.createScrollingPanel(" Response ", outputResponsePane),
				BorderLayout.CENTER);

		final JTextField reqSearch = new JTextField();
		final JTextField resSearch = new JTextField();

		csxReq = new JCheckBox("Ignore Case");
		csxRes = new JCheckBox("Ignore Case");
		csxReq.setSelected(true);
		csxRes.setSelected(true);

		resSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 10) {
					outputSearch(outputResponsePane, resSearch,
							csxRes.isSelected());
				}
			}
		});

		reqSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 10) {
					outputSearch(outputRequestPane, reqSearch,
							csxReq.isSelected());
				}
			}
		});

		// configure the ignore case check boxes
		JPanel pbL = new JPanel(new BorderLayout());
		pbL.add(csxReq, BorderLayout.WEST);
		pbL.add(reqSearch, BorderLayout.CENTER);

		JPanel pbR = new JPanel(new BorderLayout());
		pbR.add(csxRes, BorderLayout.WEST);
		pbR.add(resSearch, BorderLayout.CENTER);

		bL.add(pbL, BorderLayout.SOUTH);
		bR.add(pbR, BorderLayout.SOUTH);

		outputBottomPane.setLeftComponent(bL);
		outputBottomPane.setRightComponent(bR);
		outputBottomPane.getLeftComponent().setMinimumSize(
				JBroFuzzFormat.ZERO_DIM);
		outputBottomPane.getRightComponent().setMinimumSize(
				JBroFuzzFormat.ZERO_DIM);
		outputBottomPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);

		outputMainPane.setTopComponent(outputTablePanel);
		outputMainPane.setBottomComponent(outputBottomPane);

		// list selection listener
		mOutputTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent e) {

						int cRow = mOutputTable.getSelectedRow();
						try {
							cRow = mOutputTable.convertRowIndexToModel(cRow);
						} catch (final IndexOutOfBoundsException iob) {
							return;
						}
						final String name = (String) mOutputTable.getModel()
								.getValueAt(cRow, 0);

						class FileLoader extends SwingWorker<String, Object> { // NO_UCD

							String fuzzerLineOutput = new String();

							public String doInBackground() {

								final String directory = fp.getFrame()
										.getJBroFuzz().getStorageHandler()
										.getLocationURIString();
								final File selFile = new File(directory, name
										+ ".html");
								fuzzerLineOutput = FileHandler
										.readFile(selFile);
								return "done";
							}

							protected void done() {
								final String dbType = JBroFuzz.PREFS.get(
										JBroFuzzPrefs.DBSETTINGS[11].getId(),
										"-1");
								String request = new String();
								String response = new String();
								if (dbType.equals("None")) {
									response = FuzzFileUtils
											.getResponse(fuzzerLineOutput);
									request = FuzzFileUtils
											.getRequest(fuzzerLineOutput);
								} else if (dbType.equals("SQLite")) {
									String sqlStatement1 = "Select textRequest from message where filename='" + name + "'";
									String sqlStatement2 = "Select reply from message where filename='" + name + "'";
									String[] result = fp.getFrame().getJBroFuzz().getStorageHandler().readTableRow(sqlStatement1);
									if (result.length > 0){
										request = result[0];
									}
									String[] result2 = fp.getFrame().getJBroFuzz().getStorageHandler().readTableRow(sqlStatement2);
									if (result2.length > 0){
										response = result[0];
									}
								} else {
									// TODO: validation checks on the couch DB
									// implementation
									Logger.log(
											"COUCHDB database not implement - use none",
											3);
								}

								outputResponsePane.setText(response);
								outputRequestPane.setText(request);
								outputResponsePane.setCaretPosition(0);
								outputResponsePane.setCaretPosition(0);
								outputResponsePane.repaint();
								outputRequestPane.repaint();
							}
						}
						(new FileLoader()).execute();
					}
				});
		
		// add all the components and set the Layout Manager
		setLayout(new BorderLayout());
		add(outputMainPane);
	}
	

	private void outputSearch(JTextPane toSearch, JTextField entry,
			boolean ignoreCase) {
		Highlighter hilit = new DefaultHighlighter();
		toSearch.setHighlighter(hilit);
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
				HILIT_COLOR);

		final String s = entry.getText();

		try {
			final String content = toSearch.getDocument().getText(0,
					toSearch.getDocument().getLength());

			int index = -1;
			if (ignoreCase) {
				index = content.toLowerCase().indexOf(s.toLowerCase(), 0);
			} else {
				index = content.indexOf(s, 0);
			}

			if (lastIndex != 0 && lastIndex >= index) {

				if (ignoreCase) {
					final int tempIndex = content.toLowerCase().indexOf(
							s.toLowerCase(), lastIndex + 1);
					index = tempIndex;
				} else {
					final int tempIndex = content.indexOf(s, lastIndex + 1);
					index = tempIndex;
				}

			}

			if (index >= 0) { // match found
				final int end = index + s.length();
				hilit.addHighlight(index, end, painter);
				toSearch.setCaretPosition(index);
				lastIndex = index;
			} else if (lastIndex > 0) {
				lastIndex = 0;
			} else {
			}

		} catch (final BadLocationException e) {
			e.printStackTrace();
		}

	}
}
