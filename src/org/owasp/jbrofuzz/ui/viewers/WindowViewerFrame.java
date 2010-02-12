/**
 * JBroFuzz 1.9
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
package org.owasp.jbrofuzz.ui.viewers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.ui.AbstractPanel;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.NonWrappingTextPane;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * Class extending a JFrame for displaying the contents of a file.
 * Typically, a file represents a request/response that has been sent
 * and received.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 0.2
 */
public class WindowViewerFrame extends JFrame implements DocumentListener {

	private static final int SIZE_X = 550;
	private static final int SIZE_Y = 525;

	private static final long serialVersionUID = -4765698531680118534L;

	private final static Color  HILIT_COLOR = Color.LIGHT_GRAY;
	private final static Color  ERROR_COLOR = Color.PINK;
	private final static String CANCEL_ACTION = "cancel-search";

	private final Color entryBg;
	private final transient Highlighter hilit;
	private final transient Highlighter.HighlightPainter painter;

	private final JTextPane listTextArea;
	private JTextField entry;
	private JLabel status;


	/**
	 * <p>
	 * The window viewer that gets launched for each request within the
	 * corresponding panel.
	 * </p>
	 * 
	 * @param parent
	 * @param name
	 */
	public WindowViewerFrame(final AbstractPanel parent, final String name) {

		super("JBroFuzz - File Viewer - " + name + ".html");
		setIconImage(ImageCreator.IMG_FRAME.getImage());

		// The container pane
		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		// Define the Panel
		final JPanel listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(name + ".html"), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		listPanel.setLayout(new BorderLayout());

		// Get the preferences for wrapping lines of text
		boolean wrapText = JBroFuzz.PREFS
		.getBoolean(JBroFuzzFormat.WRAP_RESPONSE, false);

		if (wrapText) {

			listTextArea = new JTextPane();

		} else {

			listTextArea = new NonWrappingTextPane();
		}

		// Refine the Text Area
		listTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		listTextArea.setEditable(false);

		// Define the search area
		entry = new JTextField(10);
		status = new JLabel("Enter text to search:");

		// Initialise the highlighter on the text area
		hilit = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
		listTextArea.setHighlighter(hilit);

		entryBg = entry.getBackground();
		entry.getDocument().addDocumentListener(this);

		InputMap im = entry.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = entry.getActionMap();
		im.put(KeyStroke.getKeyStroke("ESCAPE"), CANCEL_ACTION);
		am.put(CANCEL_ACTION, new CancelAction());

		// Right click: Cut, Copy, Paste, Select All
		AbstractPanel.popupText(listTextArea, false, true, false, true);

		// Define the Scroll Pane for the Text Area
		final JScrollPane listTextScrollPane = new JScrollPane(listTextArea);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(30);

		// Define the progress bar
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setString("   ");
		progressBar.setStringPainted(true);
		// progressBar.setBounds(410, 265, 120, 20);

		// Define the bottom panel with the progress bar
		final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		bottomPanel.add(status);
		bottomPanel.add(entry);
		bottomPanel.add(progressBar);

		listTextArea.setCaretPosition(0);
		listPanel.add(listTextScrollPane);

		// Global Frame Issues
		pane.add(listPanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);

		this.setLocation(parent.getLocationOnScreen().x + 100, parent.getLocationOnScreen().y + 20);
		this.setSize(SIZE_X, SIZE_Y);

		setResizable(true);
		setVisible(true);
		setMinimumSize(new Dimension(SIZE_X, SIZE_Y));
		setDefaultCloseOperation(2);

				
		listTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					WindowViewerFrame.this.dispose();
				}
			}
		});

// TODO from UCDetector: Local class "WindowViewerFrame$FileLoader" has 0 references
		class FileLoader extends SwingWorker<String, Object> { // NO_UCD

			@Override
			public String doInBackground() {

				progressBar.setIndeterminate(true);

				listTextArea.setText(

						parent.getFrame().getJBroFuzz().getHandler().readFuzzFile(
								name + ".html")

				);

				return "done";
			}

			@Override
			protected void done() {

				progressBar.setIndeterminate(false);
				progressBar.setValue(100);

			}
		}

		(new FileLoader()).execute();

	}

	private void search() {
		hilit.removeAllHighlights();

		String s = entry.getText();
		if (s.length() <= 0) {
			message("Nothing to search");
			return;
		}


		try {
			String content = listTextArea.getDocument().getText(0, listTextArea.getDocument().getLength());
			int index = content.indexOf(s, 0);

			if (index >= 0) {   // match found
				int end = index + s.length();
				hilit.addHighlight(index, end, painter);
				listTextArea.setCaretPosition(index);
				entry.setBackground(entryBg);
				message("Phrase found: '" + s + "'");

			} else {
				entry.setBackground(ERROR_COLOR);
				message("Phrase not found...");
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	private void message(String msg) {
		status.setText(StringUtils.abbreviate(msg, 40));
	}

	// DocumentListener methods

	public void insertUpdate(DocumentEvent ev) {
		search();
	}

	public void removeUpdate(DocumentEvent ev) {
		search();
	}

	public void changedUpdate(DocumentEvent ev) {
	}

	private class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 9875234L;

		public void actionPerformed(ActionEvent ev) {
			hilit.removeAllHighlights();
			entry.setText("");
			entry.setBackground(entryBg);
			WindowViewerFrame.this.dispose();
		}

	}


} // Frame class
