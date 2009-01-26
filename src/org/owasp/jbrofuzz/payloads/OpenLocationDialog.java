/**
 * JBroFuzz 1.2
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007, 2008, 2009 subere@uncon.org
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
package org.owasp.jbrofuzz.payloads;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>The open location dialog.</p>
 * 
 * @author subere@uncon.org
 * @version 1.2
 */
public class OpenLocationDialog extends JDialog  implements MouseListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5815321460026044259L;
	
	// Dimensions of the generator dialog box
	private static final int x = 340;
	private static final int y = 180;

	// The buttons
	private JButton ok, cancel;

	// The frame that the sniffing panel is attached
	private JBroFuzzWindow m;

	final JPopupMenu popmenu; 

	JComboBox _url, methodBox, charsetBox, versionBox; 

	/**
	 * <p>Constructs a dialog box for (Ctrl+L) input of URL
	 * fields.</p>
	 * 
	 * @param parent JBroFuzzWindow The main window
	 *
	 * @author subere@uncon.org
	 * @version 1.2
	 * @since 1.2
	 */
	public OpenLocationDialog(final JBroFuzzWindow parent) {

		super(parent, " Open URL Location ", true);
		setFont(new Font("SansSerif", Font.BOLD, 10));
		setIconImage(ImageCreator.IMG_FRAME.getImage());

		setLayout(new BorderLayout());
		m = parent;

		// Components

		_url = new JComboBox();
		_url.setPreferredSize(new Dimension(250, 20));
		_url.setEditable(true);

		_url.getEditor().getEditorComponent().addMouseListener(this);
		_url.setToolTipText("Copy/Paste a URL from your browser");
		_url.setFont(new Font("Verdana", Font.PLAIN, 12));

		_url.getEditor().getEditorComponent().addKeyListener(this);

		final String methods[] = {"GET", "POST", "HEAD", "PUT", "DELETE", "TRACE", "PROPFIND", "OPTIONS"};		
		final String versions[] = {"0.9", "1.0", "1.1"};
		final String charsets[] = {"ISO-8859-1"};

		methodBox = new JComboBox(methods);
		charsetBox = new JComboBox(charsets);
		versionBox = new JComboBox(versions);

		methodBox.setFont(new Font("SansSerif", Font.BOLD, 10));
		charsetBox.setFont(new Font("SansSerif", Font.BOLD, 10));
		versionBox.setFont(new Font("SansSerif", Font.BOLD, 10));
		
		methodBox.addKeyListener(this);
		charsetBox.addKeyListener(this);
		versionBox.addKeyListener(this);
		
		methodBox.setMaximumRowCount(3);
		charsetBox.setMaximumRowCount(3);
		versionBox.setMaximumRowCount(3);
		
		methodBox.setBackground(Color.BLACK);
		charsetBox.setBackground(Color.BLACK);
		versionBox.setBackground(Color.BLACK);
		
		methodBox.setForeground(Color.WHITE);
		charsetBox.setForeground(Color.WHITE);
		versionBox.setForeground(Color.WHITE);
		
		versionBox.setSelectedIndex(1);

		// Buttons
		ok = new JButton("  OK  ");
		ok.setBounds(515, 305, 140, 40);
		ok.setToolTipText("Open the URL location in JBroFuzz");

		cancel = new JButton("Cancel");
		cancel.setBounds(515, 305, 140, 40);
		cancel.setToolTipText("Cancel opening a URL location");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						clickOK();
						OpenLocationDialog.this.dispose();

					}
				});
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						OpenLocationDialog.this.dispose();

					}
				});
			}
		});

		// Pop-up menu
		popmenu = new JPopupMenu();

		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");

		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.addSeparator();
		popmenu.add(i4);

		// Cut
		i1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable clipData = clipboard.getContents(clipboard);
				
				if (clipData != null) {
					try {
						if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							((JTextComponent)_url.getEditor().getEditorComponent()).cut();
						}
					}
					catch (Exception e1) {
						parent.log("Open Location: An error occured while cutting");
					}
				}
			}
		});
		
		// Copy
		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable clipData = clipboard.getContents(clipboard);
				
				if (clipData != null) {
					try {
						if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							((JTextComponent)_url.getEditor().getEditorComponent()).copy();
						}
					}
					catch (Exception e1) {
						parent.log("Open Location: An error occured while copying");
					}
				}
			}
		});

		// Paste
		i3.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable clipData = clipboard.getContents(clipboard);
				
				if (clipData != null) {
					try {
						if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							((JTextComponent)_url.getEditor().getEditorComponent()).replaceSelection(
									(String)(clipData.getTransferData(DataFlavor.stringFlavor)));
						}
					}
					catch (Exception e1) {
						parent.log("Open Location: An error occured while pasting");
					}
				}
			}
		});

		// Select All
		i4.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				_url.getEditor().selectAll();

			}
		});

		// Final panels

		final JPanel targetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		targetPanel.add(new JLabel("URL:"));
		targetPanel.add(_url);

		final JPanel propertiesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		propertiesPanel.add(methodBox);
		propertiesPanel.add(charsetBox);
		propertiesPanel.add(versionBox);

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		buttonPanel.add(ok);
		buttonPanel.add(cancel);

		add(targetPanel, BorderLayout.NORTH);
		add(propertiesPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		setLocation(
				Math.abs(parent.getLocation().x + 20), 
				Math.abs(parent.getLocation().y + 20)
		);

		setSize(OpenLocationDialog.x, OpenLocationDialog.y);
		setResizable(true);
		setVisible(true);

	}

	private void checkForTriggerEvent(final MouseEvent e) {

		if (e.isPopupTrigger()) {
			popmenu.show(e.getComponent(), e.getX(), e.getY());
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mouseEntered(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mouseExited(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mousePressed(MouseEvent e) {

		checkForTriggerEvent(e);

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		checkForTriggerEvent(e);

	}
	
	private void clickOK() {
		
		try {
			URL inputURL = new URL(((JTextComponent)_url.getEditor().getEditorComponent()).getText());
			
			StringBuffer out_url = new StringBuffer();
			out_url.append(inputURL.getProtocol());
			out_url.append("://");
			out_url.append(inputURL.getHost());
			if(inputURL.getPort() > 0) {
				out_url.append(":");
				out_url.append(inputURL.getPort());
			}
			
			// System.out.println(out_url.toString());
			
			StringBuffer req_url = new StringBuffer();
			req_url.append(methodBox.getModel().getElementAt(methodBox.getSelectedIndex()));
			req_url.append(' ');
			try {
				req_url.append(URLDecoder.decode(inputURL.getFile(), "UTF-8" ));
			} catch (UnsupportedEncodingException e) {
				m.log("Open Location: Unsupported URL Encoding Exception");
				req_url.append(inputURL.getFile());
			}
			req_url.append(' ');
			req_url.append("HTTP/");
			req_url.append(versionBox.getModel().getElementAt(versionBox.getSelectedIndex()));
			req_url.append(JBroFuzzFormat.URL_REQUEST.substring(JBroFuzzFormat.URL_REQUEST.indexOf('\n')));
			
			// System.out.println(req_url.toString());
			
			m.getPanelFuzzing().setTextURL(out_url.toString());
			m.getPanelFuzzing().setTextRequest(req_url.toString());
			
		} catch (MalformedURLException e) {
			m.log("Open Location: Could not pass the URL provided");
		}
		
		
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		
		if (ke.getKeyCode() == 27) {
			OpenLocationDialog.this.dispose();
		}
		if (ke.getKeyCode() == 10) {
			clickOK();
			OpenLocationDialog.this.dispose();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
