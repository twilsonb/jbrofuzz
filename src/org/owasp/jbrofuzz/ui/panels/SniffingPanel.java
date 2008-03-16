/**
 * TCPSniffingPanel.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon (dot) org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.owasp.jbrofuzz.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.owasp.jbrofuzz.snif.ConnectionListener;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.ui.tablemodels.SingleColumnModel;
import org.owasp.jbrofuzz.ui.viewers.WindowViewer;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.SwingWorker3;
import org.owasp.jbrofuzz.version.Format;

import com.Ostermiller.util.Browser;

/**
 * <p>
 * The Sniffing Panel User Interface based on the instance of a JPanel. This
 * class constructs the entire panel for the "TCP Sniffing" tab as seen by the
 * user in the main frame.
 * </p>
 * 
 * @author subere (at) uncon (dot) org
 * @version 0.8
 */
public class SniffingPanel extends JBroFuzzPanel {
	/*
	private class SniffingRowListener {//  implements ListSelectionListener {
		
		public void valueChanged(final ListSelectionEvent event) {
			
			if (event.getValueIsAdjusting()) {
				return;
			}
			final int c = sniffingTable.getSelectedRow();
			final String [] value = ((String) tableModel
					.getValueAt(c, 0)).split(" ");
			new WindowViewer(
					getFrame(), value[0],
					WindowViewer.VIEW_SNIFFING_PANEL);
			
		}
	
} */

	/**
	 * 
	 */
	private static final long serialVersionUID = 2185868982471609733L;

	private final JTextField rHostText, rPortText, lHostText, lPortText;

	// The buttons to start and stop the listener, as well as launch a browser
	private final JButton startButton, stopButton, browserButton;

	//
	private final JTable sniffingTable;

	//
	private JPanel listPanel;

	// A counter for the number of times start has been clicked
	private int counter, session;

	// The swing worker used when the button "start" is pressed
	private SwingWorker3 worker;

	// The frame that the sniffing panel is attached
	// private JBRFrame m;

	// The table model
	private SingleColumnModel tableModel;

	// The TCP Connection listener
	private ConnectionListener reflector;

	/**
	 * <p>
	 * Main Constructor for the Sniffing Panel, passing the Main Window (JFrame)
	 * as part of the constructor argument.
	 * </p>
	 * 
	 * @param m
	 *          MainWindow
	 */
	public SniffingPanel(final JBroFuzzWindow m) {
		super(m );
		// this.setLayout(null);
		// this.m = m;
		// Set the counter to zero
		counter = 0;
		session = 0;
		// Define the JPanels
		final JPanel rHostPanel = new JPanel();
		final JPanel rPortPanel = new JPanel();
		final JPanel lHostPanel = new JPanel();
		final JPanel lPortPanel = new JPanel();
		listPanel = new JPanel();
		// Set the borders
		rHostPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Remote Host "), BorderFactory.createEmptyBorder(
				1, 1, 1, 1)));
		rPortPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1,
				1)));
		lHostPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Local Host "), BorderFactory.createEmptyBorder(1,
				1, 1, 1)));
		lPortPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Port "), BorderFactory.createEmptyBorder(1, 1, 1,
				1)));
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" List of Requests "), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		// Set the bounds
		rHostPanel.setBounds(10, 20, 220, 60);
		rPortPanel.setBounds(230, 20, 60, 60);
		lHostPanel.setBounds(300, 20, 220, 60);
		lPortPanel.setBounds(520, 20, 60, 60);
		listPanel.setBounds(10, 90, 870, 360);
		// Setup the remote host text
		rHostText = new JTextField();
		rHostText.setEditable(true);
		rHostText.setFont(new Font("Verdana", Font.BOLD, 12));
		rHostText.setMargin(new Insets(1, 1, 1, 1));
		getFrame().popup(rHostText);
		rHostText.setPreferredSize(new Dimension(200, 20));
		rHostPanel.add(rHostText);
		// Setup the remote host port
		rPortText = new JFormattedTextField();
		rPortText.setEditable(true);
		rPortText.setFont(new Font("Verdana", Font.BOLD, 12));
		// rPortText.setLineWrap(false);
		// rPortText.setWrapStyleWord(true);
		rPortText.setMargin(new Insets(1, 1, 1, 1));
		getFrame().popup(rPortText);
		rPortText.setPreferredSize(new Dimension(50, 20));
		rPortPanel.add(rPortText);
		// Setup the local host text
		lHostText = new JTextField();
		lHostText.setEditable(true);
		lHostText.setFont(new Font("Verdana", Font.BOLD, 12));
		// lHostText.setLineWrap(false);
		// lHostText.setWrapStyleWord(true);
		lHostText.setMargin(new Insets(1, 1, 1, 1));
		getFrame().popup(lHostText);
		lHostText.setPreferredSize(new Dimension(200, 20));
		lHostPanel.add(lHostText);
		// Setup the local port text
		lPortText = new JFormattedTextField();
		lPortText.setEditable(true);
		lPortText.setFont(new Font("Verdana", Font.BOLD, 12));
		// lPortText.setLineWrap(false);
		// lPortText.setWrapStyleWord(true);
		lPortText.setMargin(new Insets(1, 1, 1, 1));
		getFrame().popup(lPortText);
		lPortText.setPreferredSize(new Dimension(50, 20));
		lPortPanel.add(lPortText);
		
		// The table of list of requests text
		tableModel = new SingleColumnModel(" Requests / Replies ");

		sniffingTable = new JTable();
		sniffingTable.setModel(tableModel);
		sniffingTable.setFont(new Font("Monospaced", Font.BOLD, 12));
		sniffingTable.setBackground(Color.black);
		sniffingTable.setForeground(Color.white);
		sniffingTable.setSurrendersFocusOnKeystroke(true);
		sniffingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// sniffingTable.getSelectionModel().addListSelectionListener(
		//		new SniffingRowListener());
		sniffingTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(final MouseEvent e){
				if (e.getClickCount() == 2){
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							
							final int c = sniffingTable.getSelectedRow();
							final String name = (String) sniffingTable.getModel().getValueAt(c, 0);
							new WindowViewer(getFrame(), name.split(" ")[0], WindowViewer.VIEW_SNIFFING_PANEL);

						}
					});
				}
			}
		} );
		
		final JScrollPane listTextScrollPane = new JScrollPane(sniffingTable);
		listTextScrollPane.setVerticalScrollBarPolicy(20);
		listTextScrollPane.setHorizontalScrollBarPolicy(31);
		listTextScrollPane.setPreferredSize(new Dimension(850, 320));
		listTextScrollPane.setWheelScrollingEnabled(true);
		listPanel.add(listTextScrollPane);

		// The start, stop buttons

		startButton = new JButton("Start", ImageCreator.START_IMG);
		startButton.setBounds(590, 33, 90, 40);
		startButton.setEnabled(true);
		startButton
				.setToolTipText("Start Sniffing between Local and Remote Host");
		stopButton = new JButton("Stop", ImageCreator.STOP_IMG);
		stopButton.setEnabled(false);
		stopButton.setBounds(690, 33, 90, 40);
		stopButton.setToolTipText("Stop Sniffing");
		browserButton = new JButton("Bro", ImageCreator.PAUSE_IMG);
		browserButton.setBounds(790, 33, 80, 40);
		browserButton.setEnabled(true);
		browserButton.setToolTipText("Open in external browser");
		
		// The action listener for the start button
		
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				// Worker, working...
				worker = new SwingWorker3() {
					@Override
					public Object construct() {
						SniffingPanel.this.start();
						return "start-window-return";
					}

					@Override
					public void finished() {
					}
				};
				worker.start();
			}
		});
		// The action listener for the stop button
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SniffingPanel.this.stop();
			}
		});
		// The action listener for the browser button
		browserButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SniffingPanel.this.bro();
			}
		});

		// Add the panels
		this.add(rHostPanel);
		this.add(rPortPanel);
		this.add(lHostPanel);
		this.add(lPortPanel);
		this.add(startButton);
		this.add(stopButton);
		this.add(browserButton);
		this.add(listPanel);

		// Some default values
		rHostText.setText("www.sourceforge.net");
		rPortText.setText("80");
		lHostText.setText("127.0.0.1");
		lPortText.setText("6161");
	}

	/**
	 * <p>
	 * Method for adding an extra row to the requests / replies JTable
	 * 
	 * @param s
	 *          String
	 */
	public void addRow(final String s) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tableModel.addEmptyRow();
				final int totalRows = tableModel.getRowCount();
				tableModel.setValueAt(s, totalRows - 1, 0);
				// Set the last row to be visible
				sniffingTable
						.scrollRectToVisible(sniffingTable.getCellRect(
								sniffingTable.getRowCount(), 0, true));
			}
		});
	}

	public void bro() {
		Browser.init();
		final StringBuffer url = new StringBuffer();
		if (getRemotePortText().equals("443")) {
			url.append("https://");
		} else {
			url.append("http://");
		}
		url.append(getLocalHostText());
		url.append(":");
		url.append(getLocalPortText());
		try {
			Browser.displayURL(url.toString());
		} catch (final IOException ex) {
			getFrame().log("Could not launch link in external browser");
		}
	}

	/**
	 * Method for hitting the start button.
	 */
	public void start() {
		if (!startButton.isEnabled()) {
			return;
		}
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		session++;

		rHostText.setEditable(false);
		rPortText.setEditable(false);
		lHostText.setEditable(false);
		lPortText.setEditable(false);

		rHostText.setBackground(Color.black);
		rPortText.setBackground(Color.black);
		lHostText.setBackground(Color.black);
		lPortText.setBackground(Color.black);

		rHostText.setForeground(Color.white);
		rPortText.setForeground(Color.white);
		lHostText.setForeground(Color.white);
		lPortText.setForeground(Color.white);

		final String rh = getRemoteHostText();
		final String rp = getRemotePortText();
		final String lh = getLocalHostText();
		final String lp = getLocalPortText();

		// Update the border of the output panel
		listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" List of Requests " + "[Logging in folder .\\"
						+ Format.DATE + "\\" + session
						+ "*.txt]  [" + rh + ":" + rp + " <=> " + lh + ":" + lp + "] "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		reflector = new ConnectionListener(this, rh, rp, lh, lp);
		reflector.start();
	}

	/**
	 * Method for hitting the stop button.
	 */
	public void stop() {
		if (!stopButton.isEnabled()) {
			return;
		}
		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		if (reflector != null) {
			reflector.stopConnection();
		}

		rHostText.setEditable(true);
		rPortText.setEditable(true);
		lHostText.setEditable(true);
		lPortText.setEditable(true);

		rHostText.setBackground(Color.white);
		rPortText.setBackground(Color.white);
		lHostText.setBackground(Color.white);
		lPortText.setBackground(Color.white);

		rHostText.setForeground(Color.black);
		rPortText.setForeground(Color.black);
		lHostText.setForeground(Color.black);
		lPortText.setForeground(Color.black);

		// Update the border of the output panel
		if (counter == 0) {
			listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder(" List of Requests " + "[Last log was empty] "),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		} else {
			listPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder(" List of Requests " + "[Last log was .\\"
							+ Format.DATE + "\\" + session
							+ "*.txt] "), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		}
	}

	/**
	 * <p>
	 * Method for returning the counter held within the Sniffing Panel which is
	 * responsible for counting the number of requests having been made. This
	 * method is used for generating unique sequential file name and row counts.
	 * </p>
	 * 
	 * @return String
	 */
	public String getCounter() {
		if (counter < 0) {
			counter = 1;
		}
		if (session < 0) {
			session = 1;
		}
		if (session > 10) {
			session = 1;
		}
		String s = "" + session;
		if (counter < 100000) {
			s += "0";
		}
		if (counter < 10000) {
			s += "0";
		}
		if (counter < 1000) {
			s += "0";
		}
		if (counter < 100) {
			s += "0";
		}
		if (counter < 10) {
			s += "0";
		}
		s += "" + counter;
		counter++;
		return s;
	}

	/**
	 * Method for accessing the Local Host text field and retrieving it's value.
	 * This method performs sanitisation attempting to remove protocol as well as
	 * trailing slashes.
	 * 
	 * @return String
	 */
	public String getLocalHostText() {
		String text = lHostText.getText();
		int len = text.length();

		if (text.startsWith("ftp://")) {
			text = text.substring(6, len);
			len = text.length();
			lHostText.setText(text);
		}
		if (text.startsWith("http://")) {
			text = text.substring(7, len);
			len = text.length();
			lHostText.setText(text);
		}
		if (text.startsWith("https://")) {
			text = text.substring(8, len);
			len = text.length();
			lHostText.setText(text);
		}
		if (text.endsWith("/")) {
			text = text.substring(0, len - 1);
			// If another if statement is included, update the len variable here
			lHostText.setText(text);
		}
		return text;
	}

	/**
	 * Method for accessing the Local Host port field and retrieving it's value.
	 * 
	 * @return String
	 */
	public String getLocalPortText() {
		return lPortText.getText();
	}

	/**
	 * Method for accessing the Remote Host text field and retrieving it's value.
	 * This method performs sanitisation attempting to remove protocol as well as
	 * trailing slashes.
	 * 
	 * @return String
	 */
	public String getRemoteHostText() {
		String text = rHostText.getText();
		int len = text.length();

		if (text.startsWith("ftp://")) {
			text = text.substring(6, len);
			len = text.length();
			rHostText.setText(text);
		}
		if (text.startsWith("http://")) {
			text = text.substring(7, len);
			len = text.length();
			rHostText.setText(text);
		}
		if (text.startsWith("https://")) {
			text = text.substring(8, len);
			len = text.length();
			rHostText.setText(text);
		}
		if (text.endsWith("/")) {
			text = text.substring(0, len - 1);
			// If another if statement is included, update the len variable here
			rHostText.setText(text);
		}
		return text;
	}

	/**
	 * Method for accessing the Remote Host port field and retrieving it's value.
	 * 
	 * @return String
	 */
	public String getRemotePortText() {
		return rPortText.getText();
	}

}
