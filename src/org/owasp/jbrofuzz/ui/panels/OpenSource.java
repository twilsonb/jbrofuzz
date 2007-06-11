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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.owasp.jbrofuzz.ui.JBRFrame;
import org.owasp.jbrofuzz.ui.util.ImageCreator;
import org.owasp.jbrofuzz.pub.GRequestIterator;
import org.owasp.jbrofuzz.version.Format;
/**
 * <p>The main "Open Source" panel, displayed within the Main Frame Window.</p>
 * 
 * <p>This panel performs all TCP related fuzzing operations, including the
 * addition and removal of generators, reporting back the results into the
 * current window, as well as writting them to file.</p>
 *
 * @author subere (at) uncon org
 * @version 0.6
 */
public class OpenSource extends JPanel {
	// The frame that the open source panel is attached to
	private JBRFrame mFrameWindow; 
	// The target field
	private final JTextField domain;
	// The output field
	private final JTextArea output;
	// The output panel 
	private JPanel outputPanel;
	//The JButtons
	private final JButton check, stop;
	// The swing worker used when the button "fuzz" is pressed
	private SwingWorker3 worker;
  // The progress bar for the site
  private JProgressBar progressBar;

	public OpenSource(JBRFrame mFrameWindow) {
		super();
		setLayout(null);
		this.mFrameWindow = mFrameWindow;

		//  The domain panel
		JPanel domainPanel = new JPanel();
		domainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createTitledBorder(" Fully Qualified Domain Name [FQDN] "),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		domain = new JTextField();
		domain.setEditable(true);
		domain.setVisible(true);
		domain.setFont(new Font("Verdana", Font.BOLD, 12));
		domain.setMargin(new Insets(1, 1, 1, 1));
		domain.setBackground(Color.WHITE);
		domain.setForeground(Color.BLACK);
		getFrameWindow().popup(domain);

		domain.setPreferredSize(new Dimension(480, 20));
		domainPanel.add(domain);

		domainPanel.setBounds(10, 20, 500, 60);
		add(domainPanel);

		// The output panel
		outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createTitledBorder(" Output "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		output = new JTextArea();

		output.setEditable(false);
		output.setVisible(true);
		output.setFont(new Font("Verdana", Font.BOLD, 12));
		output.setLineWrap(false);
		output.setWrapStyleWord(true);
		output.setMargin(new Insets(1, 1, 1, 1));
		output.setBackground(Color.WHITE);
		output.setForeground(Color.BLACK);
		getFrameWindow().popup(output);

		JScrollPane outputScrollPane = new JScrollPane(output);
		outputScrollPane.setVerticalScrollBarPolicy(20);
		outputScrollPane.setHorizontalScrollBarPolicy(30);
		outputScrollPane.setPreferredSize(new Dimension(480, 160));
		outputPanel.add(outputScrollPane);

		outputPanel.setBounds(10, 80, 500, 200);
		add(outputPanel);

		// The check button
		check = new JButton("Check!", ImageCreator.START_IMG);
		check.setBounds(540, 100, 90, 40);
		check.setToolTipText("Start Checking!");
		add(check);
		check.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				worker = new SwingWorker3() {
					public Object construct() {
						checkStartButton();
						return "start-window-return";
					}

					public void finished() {
						checkStopButton();
					}
				};
				worker.start();
			}
		});

		// The stop button
		stop = new JButton("Stop", ImageCreator.STOP_IMG);
		stop.setEnabled(false);
		stop.setToolTipText("Stop Checking");
		stop.setBounds(640, 100, 90, 40);
		add(stop);
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						checkStopButton();
					}
				});       
			}
		});
		
		// The progress bar
    progressBar = new JProgressBar(0);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    progressBar.setMinimum(0);
    progressBar.setMaximum(5);
    progressBar.setPreferredSize(new Dimension(310, 20));
    JPanel progressPanel = new JPanel();
    progressPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
      createTitledBorder(" Progress "),
      BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    progressPanel.setBounds(520, 20, 330, 60);
    progressPanel.add(progressBar);
    add(progressPanel);		
    
    // The info field
		// The output panel
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createTitledBorder(" Readme "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		JTextArea info = new JTextArea();
		
		info.setText(Format.OPEN_SOURCE_README);
		info.setCaretPosition(0);
		info.setEditable(false);
		info.setVisible(true);
		info.setFont(new Font("Verdana", Font.BOLD, 10));
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setMargin(new Insets(1, 1, 1, 1));
		info.setBackground(Color.WHITE);
		info.setForeground(Color.BLACK);
		getFrameWindow().popup(info);

		JScrollPane infoScrollPane = new JScrollPane(info);
		infoScrollPane.setVerticalScrollBarPolicy(20);
		infoScrollPane.setHorizontalScrollBarPolicy(31);
		infoScrollPane.setPreferredSize(new Dimension(310, 140));
		infoPanel.add(infoScrollPane);

		infoPanel.setBounds(520, 270, 330, 180);
		add(infoPanel);
	}

	/**
	 * Access the main frame window in which this panel is attached to.
	 * @return FrameWindow
	 */
	public JBRFrame getFrameWindow() {
		return mFrameWindow;
	}

	/**
	 * <p>Method trigered when the fuzz button is pressed in the current panel.
	 * </p>
	 */
	public void checkStartButton() {
		if (!check.isEnabled()) {
			return;
		}
		// UI and Colors
		check.setEnabled(false);
		stop.setEnabled(true);
		domain.setEditable(false);
		domain.setBackground(Color.BLACK);
		domain.setForeground(Color.WHITE);
		output.setBackground(Color.BLACK);
		output.setForeground(Color.WHITE);
		progressBar.setValue(0);
		// Check to see if a message is present
		String dm = getDomainText();
		if ("".equals(dm)) {
			JOptionPane.showMessageDialog(this,
					"The domain field is blank.\n" + "Specify a domain\n",
					"Empty Domain Field",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// Update the border of the output panel
		outputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createTitledBorder(" Output Checking domain: " + dm + " "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		output.setText("");    
		GRequestIterator req = new GRequestIterator(mFrameWindow, dm);
		output.setText(req.getOutput());
	}

	/**
	 * <p>Method trigered when attempting to stop any online check taking place.</p>
	 */
	public void checkStopButton() {
		if (!stop.isEnabled()) {
			return;
		}
		// UI and Colors
		check.setEnabled(true);
		stop.setEnabled(false);
		domain.setEditable(true);
		domain.setBackground(Color.WHITE);
		domain.setForeground(Color.BLACK);
		if(output.getText().length() == 0) {
			output.setBackground(Color.WHITE);
			output.setForeground(Color.BLACK);
		}
		else {
			output.setCaretPosition(output.getText().length());
		}
	}  

	public void appendOutputText(final String t) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
    		output.append(t);
    		output.setCaretPosition(output.getText().length());
      }
    });
	}
	
  /**
   * Set the progress bar on the display to a value between 0 and 100.
   * @param percent int
   */
  public void setProgressBar(final int percent) {
    if ((percent >= 0) && (percent <= 5)) {
      SwingWorker3 progressWorker = new SwingWorker3() {
        public Object construct() {
          progressBar.setValue(percent);
          return "progress-four-update-return";
        }

        public void finished() {
        }
      };
      progressWorker.start();
    }
  }
  
  public String getDomainText() {
    String text = domain.getText();
    int len = text.length();

    if (text.startsWith("@")) {
      text = text.substring(1, len);
      len = text.length();
      domain.setText(text);
    }
    if (text.startsWith("[")) {
      text = text.substring(1, len);
      len = text.length();
      domain.setText(text);
    }
    if (text.startsWith(" ")) {
      text = text.substring(1, len);
      len = text.length();
      domain.setText(text);
    }
    if (text.startsWith("\"")) {
      text = text.substring(1, len);
      len = text.length();
      domain.setText(text);
    }
    if (text.startsWith("<")) {
      text = text.substring(1, len);
      len = text.length();
      domain.setText(text);
    }
    if (text.startsWith("http://")) {
      text = text.substring(7, len);
      len = text.length();
      domain.setText(text);
    }
    if (text.startsWith("https://")) {
      text = text.substring(8, len);
      len = text.length();
      domain.setText(text);
    }
    if (text.endsWith("\"")) {
      text = text.substring(0, len - 1);
      len = text.length();
      domain.setText(text);
    }
    if (text.endsWith(">")) {
      text = text.substring(0, len - 1);
      len = text.length();
      domain.setText(text);
    }
    if (text.endsWith(" ")) {
      text = text.substring(0, len - 1);
      len = text.length();
      domain.setText(text);
    }
    if (text.endsWith("]")) {
      text = text.substring(0, len - 1);
      len = text.length();
      domain.setText(text);
    }
    if (text.endsWith("/")) {
      text = text.substring(0, len - 1);
      // If another if statement is included, update the len variable here
      domain.setText(text);
    }
    return text;
  }  
}
