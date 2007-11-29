/**
 * OpenSourcePanel.java 0.6
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker3;

import org.owasp.jbrofuzz.pub.GRequestIterator;
import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBRFormat;

/**
 * <p>
 * The main "Open Source" panel, displayed within the Main Frame Window.
 * </p>
 * 
 * <p>
 * This panel performs all TCP related fuzzing operations, including the
 * addition and removal of generators, reporting back the results into the
 * current window, as well as writting them to file.
 * </p>
 * 
 * @author subere (at) uncon org
 * @version 0.6
 */
public class OpenSource extends JBRPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5881754154644327656L;
	// The frame that the open source panel is attached to
	// private JBRFrame mFrameWindow;
	// The target field
	private final JTextField domain;
	// The output field
	private final JTextArea output;
	// The output panel
	private JPanel outputPanel;
	// The JButtons
	private final JButton check, stop;
	// The swing worker used when the button "fuzz" is pressed
	private SwingWorker3 worker;
	// The progress bar for the site
	private JProgressBar progressBar;

	public OpenSource(final JBRFrame m) {
		super(m);
		// this.setLayout(null);
		// this.mFrameWindow = mFrameWindow;

		// The domain panel
		final JPanel domainPanel = new JPanel();
		domainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Fully Qualified Domain Name [FQDN] "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		this.domain = new JTextField();
		this.domain.setEditable(true);
		this.domain.setVisible(true);
		this.domain.setFont(new Font("Verdana", Font.BOLD, 12));
		this.domain.setMargin(new Insets(1, 1, 1, 1));
		this.domain.setBackground(Color.WHITE);
		this.domain.setForeground(Color.BLACK);
		getFrame().popup(this.domain);

		this.domain.setPreferredSize(new Dimension(480, 20));
		domainPanel.add(this.domain);

		domainPanel.setBounds(10, 20, 500, 60);
		this.add(domainPanel);

		// The output panel
		this.outputPanel = new JPanel();
		this.outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output "), BorderFactory.createEmptyBorder(5, 5,
				5, 5)));

		this.output = new JTextArea();

		this.output.setEditable(false);
		this.output.setVisible(true);
		this.output.setFont(new Font("Verdana", Font.BOLD, 12));
		this.output.setLineWrap(false);
		this.output.setWrapStyleWord(true);
		this.output.setMargin(new Insets(1, 1, 1, 1));
		this.output.setBackground(Color.WHITE);
		this.output.setForeground(Color.BLACK);
		getFrame().popup(this.output);

		final JScrollPane outputScrollPane = new JScrollPane(this.output);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		outputScrollPane.setHorizontalScrollBarPolicy(30);
		outputScrollPane.setPreferredSize(new Dimension(480, 160));
		this.outputPanel.add(outputScrollPane);

		this.outputPanel.setBounds(10, 80, 500, 200);
		this.add(this.outputPanel);

		// The check button
		this.check = new JButton("Check!", ImageCreator.START_IMG);
		this.check.setBounds(540, 100, 90, 40);
		this.check.setToolTipText("Start Checking!");
		this.add(this.check);
		this.check.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				OpenSource.this.worker = new SwingWorker3() {
					@Override
					public Object construct() {
						OpenSource.this.checkStartButton();
						return "start-window-return";
					}

					@Override
					public void finished() {
						OpenSource.this.checkStopButton();
					}
				};
				OpenSource.this.worker.start();
			}
		});

		// The stop button
		this.stop = new JButton("Stop", ImageCreator.STOP_IMG);
		this.stop.setEnabled(false);
		this.stop.setToolTipText("Stop Checking");
		this.stop.setBounds(640, 100, 90, 40);
		this.add(this.stop);
		this.stop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						OpenSource.this.checkStopButton();
					}
				});
			}
		});

		// The progress bar
		this.progressBar = new JProgressBar(0);
		this.progressBar.setValue(0);
		this.progressBar.setStringPainted(true);
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(5);
		this.progressBar.setPreferredSize(new Dimension(310, 20));
		final JPanel progressPanel = new JPanel();
		progressPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Progress "), BorderFactory.createEmptyBorder(1,
				1, 1, 1)));
		progressPanel.setBounds(520, 20, 330, 60);
		progressPanel.add(this.progressBar);
		this.add(progressPanel);

		// The info field
		// The output panel
		final JPanel infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Readme "), BorderFactory.createEmptyBorder(5, 5,
				5, 5)));

		final JTextArea info = new JTextArea();

		info.setText(JBRFormat.OPEN_SOURCE_README);
		info.setCaretPosition(0);
		info.setEditable(false);
		info.setVisible(true);
		info.setFont(new Font("Verdana", Font.BOLD, 10));
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setMargin(new Insets(1, 1, 1, 1));
		info.setBackground(Color.WHITE);
		info.setForeground(Color.BLACK);
		getFrame().popup(info);

		final JScrollPane infoScrollPane = new JScrollPane(info);
		infoScrollPane.setVerticalScrollBarPolicy(20);
		infoScrollPane.setHorizontalScrollBarPolicy(31);
		infoScrollPane.setPreferredSize(new Dimension(310, 140));
		infoPanel.add(infoScrollPane);

		infoPanel.setBounds(520, 270, 330, 180);
		this.add(infoPanel);
	}

	public void appendOutputText(final String t) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				OpenSource.this.output.append(t);
				OpenSource.this.output.setCaretPosition(OpenSource.this.output
						.getText().length());
			}
		});
	}

	/**
	 * <p>
	 * Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 */
	public void checkStartButton() {
		if (!this.check.isEnabled()) {
			return;
		}
		// UI and Colors
		this.check.setEnabled(false);
		this.stop.setEnabled(true);
		this.domain.setEditable(false);
		this.domain.setBackground(Color.BLACK);
		this.domain.setForeground(Color.WHITE);
		this.output.setBackground(Color.BLACK);
		this.output.setForeground(Color.WHITE);
		this.progressBar.setValue(0);
		// Check to see if a message is present
		final String dm = this.getDomainText();
		if ("".equals(dm)) {
			JOptionPane.showMessageDialog(this, "The domain field is blank.\n"
					+ "Specify a domain\n", "Empty Domain Field",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// Update the border of the output panel
		this.outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Output Checking domain: " + dm + " "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		this.output.setText("");
		final GRequestIterator req = new GRequestIterator(getFrame(), dm);
		this.output.setText(req.getOutput());
	}

	/**
	 * <p>
	 * Method trigered when attempting to stop any online check taking place.
	 * </p>
	 */
	public void checkStopButton() {
		if (!this.stop.isEnabled()) {
			return;
		}
		// UI and Colors
		this.check.setEnabled(true);
		this.stop.setEnabled(false);
		this.domain.setEditable(true);
		this.domain.setBackground(Color.WHITE);
		this.domain.setForeground(Color.BLACK);
		if (this.output.getText().length() == 0) {
			this.output.setBackground(Color.WHITE);
			this.output.setForeground(Color.BLACK);
		} else {
			this.output.setCaretPosition(this.output.getText().length());
		}
	}

	public String getDomainText() {
		String text = this.domain.getText();
		int len = text.length();

		if (text.startsWith("@")) {
			text = text.substring(1, len);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.startsWith("[")) {
			text = text.substring(1, len);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.startsWith(" ")) {
			text = text.substring(1, len);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.startsWith("\"")) {
			text = text.substring(1, len);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.startsWith("<")) {
			text = text.substring(1, len);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.startsWith("http://")) {
			text = text.substring(7, len);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.startsWith("https://")) {
			text = text.substring(8, len);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.endsWith("\"")) {
			text = text.substring(0, len - 1);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.endsWith(">")) {
			text = text.substring(0, len - 1);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.endsWith(" ")) {
			text = text.substring(0, len - 1);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.endsWith("]")) {
			text = text.substring(0, len - 1);
			len = text.length();
			this.domain.setText(text);
		}
		if (text.endsWith("/")) {
			text = text.substring(0, len - 1);
			// If another if statement is included, update the len variable here
			this.domain.setText(text);
		}
		return text;
	}

	/**
	 * Set the progress bar on the display to a value between 0 and 100.
	 * 
	 * @param percent
	 *          int
	 */
	public void setProgressBar(final int percent) {
		if ((percent >= 0) && (percent <= 5)) {
			final SwingWorker3 progressWorker = new SwingWorker3() {
				@Override
				public Object construct() {
					OpenSource.this.progressBar.setValue(percent);
					return "progress-four-update-return";
				}

				@Override
				public void finished() {
				}
			};
			progressWorker.start();
		}
	}
}
