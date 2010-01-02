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
package org.owasp.jbrofuzz.encode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.B64;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

/**
 * <p>
 * Window inspired from Paros Proxy, in terms of providing an encoder/decoder
 * for a variety of different schemes, as well as hashing functionality.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.8
 * @since 1.5
 * 
 */
public class EncoderHashFrame extends JFrame {

	private static final long serialVersionUID = 8808832051334720865L;
	// Dimensions of the frame
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;

	private static final String[] CODES = { "Codes/Hashes", "URL US-ASCII",
		"URL ISO-8859-1", "URL Cp1252", "URL UTF-8", "URL UTF-16BE", 
		"URL UTF-16LE", "Base64", "MD5 Hash", "SHA1 Hash", "SHA-256",
		"SHA-384", "SHA-512", "Hexadecimal (low)", "Hexadecimal (UPP)", 
		"Binary", "www-form-urlencoded", "RFC 1521 MIME (eMail)",
		"Escape: HTML", "Escape: CSV", "Escape: Java", "Escape: Javascript",
		"Escape: SQL", "Escape: XML" };

	private JSplitPane verticalSplitPane, horizontalSplitPane;

	private JTextPane enTextPane, deTextPane;

	// The tree
	private JTree tree;

	private JButton encode, decode, close;

	private static boolean encoderHashIsShowing = false;

	public EncoderHashFrame(final JBroFuzzWindow parent) {
		
		if (encoderHashIsShowing) {
			return;
		}
		encoderHashIsShowing = true;

		// really inspired from Paros Proxy, but as a frame
		setTitle(" JBroFuzz - Encoder/Hash ");

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(CODES[0]);
		// Create a tree that allows one selection at a time
		tree = new JTree(top);
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// Selection can only contain one path at a time
		tree.getSelectionModel().setSelectionMode(1);

		// Create the scroll pane and add the tree to it.
		final JScrollPane leftScrollPane = new JScrollPane(tree);

		// Create all the right hand panels
		for (int i = 0; i < CODES.length; i++) {

			if (i > 0) {
				top.add(new DefaultMutableTreeNode(CODES[i]));
			}

		}

		final JPanel encoderPanel = new JPanel(new BorderLayout());
		final JPanel decoderPanel = new JPanel(new BorderLayout());

		encoderPanel
		.setBorder(BorderFactory
				.createCompoundBorder(
						BorderFactory
						.createTitledBorder(" Enter the plain text below to be encoded / hashed "),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		decoderPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(" Enter the text below to be decoded "),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Text panes -> Encode
		enTextPane = new JTextPane();

		enTextPane.putClientProperty("charset", "UTF-8");
		enTextPane.setEditable(true);
		enTextPane.setVisible(true);
		enTextPane.setFont(new Font("Verdana", Font.PLAIN, 12));

		enTextPane.setMargin(new Insets(1, 1, 1, 1));
		enTextPane.setBackground(Color.WHITE);
		enTextPane.setForeground(new Color(51, 102, 102));

		// Set the right click for the encode text area
		popupText(enTextPane);

		final JScrollPane encodeScrollPane = new JScrollPane(enTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		encoderPanel.add(encodeScrollPane, BorderLayout.CENTER);

		// Text panes -> Decode
		deTextPane = new JTextPane();

		deTextPane.putClientProperty("charset", "UTF-8");
		deTextPane.setEditable(true);
		deTextPane.setVisible(true);
		deTextPane.setFont(new Font("Verdana", Font.PLAIN, 12));

		deTextPane.setMargin(new Insets(1, 1, 1, 1));
		deTextPane.setBackground(Color.WHITE);
		deTextPane.setForeground(new Color(204, 51, 0));

		// Set the right click for the decode text area
		popupText(deTextPane);

		final JScrollPane decodeScrollPane = new JScrollPane(deTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		decoderPanel.add(decodeScrollPane, BorderLayout.CENTER);

		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		horizontalSplitPane.setLeftComponent(leftScrollPane);
		horizontalSplitPane.setRightComponent(verticalSplitPane);

		verticalSplitPane.setTopComponent(encoderPanel);
		verticalSplitPane.setBottomComponent(decoderPanel);

		// Set the minimum size for all components
		final Dimension minimumSize = new Dimension(0, 0);
		leftScrollPane.setMinimumSize(minimumSize);
		verticalSplitPane.setMinimumSize(minimumSize);
		encoderPanel.setMinimumSize(minimumSize);
		decoderPanel.setMinimumSize(minimumSize);

		horizontalSplitPane.setDividerLocation(180);
		verticalSplitPane.setDividerLocation(SIZE_Y / 2);

		// Traverse tree from root
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		parent.getPanelPayloads().expandAll(tree, new TreePath(root), true);

		// Get the preferences for saving the encode/decode values
		final Preferences prefs = Preferences.userRoot().node("owasp/jbrofuzz");

		// Bottom three buttons
		encode = new JButton(" Encode/Hash ");
		decode = new JButton(" Decode ");
		close = new JButton(" Close ");

		final String desc = "Select an encoding or hashing scheme from the left hard side";
		encode.setToolTipText(desc);
		decode.setToolTipText(desc);

		encode.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						calculate(true);

					}
				});
			}
		});

		decode.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						calculate(false);

					}
				});
			}
		});

		close.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						encoderHashIsShowing = false;

						// Save the values of the encode/decode as a preference
						prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
						prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

						dispose();

					}
				});
			}
		});

		// Keyboard listener on the treeView for escape to cancel
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					encoderHashIsShowing = false;

					// Save the values of the encode/decode as a preference
					prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
					prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

					dispose();

				}
			}
		});

		// Keyboard listener on the treeView for Ctrl+Return to Encode
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					encoderHashIsShowing = false;

					// Save the values of the encode/decode as a preference
					prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
					prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

					dispose();

				}
			}
		});


		// Keyboard listener on the decoded text pane for escape to cancel
		deTextPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					encoderHashIsShowing = false;

					// Save the values of the encode/decode as a preference
					prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
					prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

					dispose();

				}
			}
		});

		// Keyboard listener on the encoded text pane for escape to cancel
		enTextPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					encoderHashIsShowing = false;

					// Save the values of the encode/decode as a preference
					prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
					prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

					dispose();

				}
			}
		});

		// Keyboard listeners on the buttons for escape to cancel
		encode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					encoderHashIsShowing = false;

					// Save the values of the encode/decode as a preference
					prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
					prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

					dispose();

				}
			}
		});

		decode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					encoderHashIsShowing = false;

					// Save the values of the encode/decode as a preference
					prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
					prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

					dispose();

				}
			}
		});

		close.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {

					encoderHashIsShowing = false;

					// Save the values of the encode/decode as a preference
					prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
					prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

					dispose();

				}
			}
		});

		// Bottom buttons

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(encode);
		buttonPanel.add(decode);
		buttonPanel.add(close);

		// Add the split pane to this panel
		getContentPane().add(horizontalSplitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Global frame issues
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math
				.abs(parent.getLocation().y + 100));

		this.setSize(EncoderHashFrame.SIZE_X, EncoderHashFrame.SIZE_Y);
		setMinimumSize(new Dimension(SIZE_X / 2, SIZE_Y / 2));

		setResizable(true);
		setVisible(true);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				encoderHashIsShowing = false;

				// Save the values of the encode/decode as a preference
				prefs.put(JBroFuzzFormat.TEXT_ENCODE, enTextPane.getText());
				prefs.put(JBroFuzzFormat.TEXT_DECODE, deTextPane.getText());

				dispose();
			}
		});

		// Load the values of encode/decode from the preferences
		enTextPane.setText(prefs.get(JBroFuzzFormat.TEXT_ENCODE, ""));
		deTextPane.setText(prefs.get(JBroFuzzFormat.TEXT_DECODE, ""));

	}

	/**
	 * <p>
	 * Calculate the value to be encoded/decoded, based on the selected scheme
	 * from the left hand side tree.
	 * </p>
	 * 
	 * @param enDecode
	 *            false implies decode true implies encode
	 * 
	 * @version 1.6
	 * @since 1.5
	 */
	public void calculate(boolean isToEncode) {

		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		final String s = node.toString();
		for (int i = 1; i < CODES.length; i++) {

			if (s.equalsIgnoreCase(CODES[i])) {

				final String encodeText = enTextPane.getText();
				final String decodeText = deTextPane.getText();

				// 1 implies URL Encode/Decode (US-ASCII)
				if (i == 1) {

					try {
						if (isToEncode) {

							deTextPane.setText(URLEncoder.encode(encodeText,
							"US-ASCII"));

						} else {

							enTextPane.setText(URLDecoder.decode(decodeText,
							"US-ASCII"));
						}
					} catch (UnsupportedEncodingException e) {

						if (isToEncode) {
							deTextPane.setText("Error: String cannot be encoded...");
						} else {
							enTextPane.setText("Error: String cannot be decoded...");
						}

					}
				}

				// 2 implies URL Encode/Decode (ISO-8859-1)
				if (i == 2) {

					try {
						if (isToEncode) {

							deTextPane.setText(URLEncoder.encode(encodeText,
							"ISO-8859-1"));

						} else {

							enTextPane.setText(URLDecoder.decode(decodeText,
							"ISO-8859-1"));
						}
					} catch (UnsupportedEncodingException e) {

						if (isToEncode) {
							deTextPane.setText("Error: String cannot be encoded...");
						} else {
							enTextPane.setText("Error: String cannot be decoded...");
						}

					}
				}

				// 3 implies URL Encode/Decode (windows-1252)
				if (i == 3) {

					try {
						if (isToEncode) {

							deTextPane.setText(URLEncoder.encode(encodeText,
							"windows-1252"));

						} else {

							enTextPane.setText(URLDecoder.decode(decodeText,
							"windows-1252"));
						}
					} catch (UnsupportedEncodingException e) {

						if (isToEncode) {
							deTextPane.setText("Error: String cannot be encoded...");
						} else {
							enTextPane.setText("Error: String cannot be decoded...");
						}

					}
				}

				// 4 implies URL Encode/Decode (UTF-8)
				if (i == 4) {

					try {
						if (isToEncode) {

							deTextPane.setText(URLEncoder.encode(encodeText,
							"UTF-8"));

						} else {

							enTextPane.setText(URLDecoder.decode(decodeText,
							"UTF-8"));
						}
					} catch (UnsupportedEncodingException e) {

						if (isToEncode) {
							deTextPane.setText("Error: String cannot be encoded...");
						} else {
							enTextPane.setText("Error: String cannot be decoded...");
						}

					}
				}

				// 5 implies URL Encode/Decode (UTF-16BE)
				if (i == 5) {

					try {
						if (isToEncode) {

							deTextPane.setText(URLEncoder.encode(encodeText,
									"UTF-16BE"));
						} else {

							enTextPane.setText(URLDecoder.decode(decodeText,
									"UTF-16BE"));

						}

					} catch (UnsupportedEncodingException ech) {
						if (isToEncode) {
							deTextPane.setText("Error: String cannot be encoded...");
						} else {
							enTextPane.setText("Error: String cannot be decoded...");
						}

					}
				}

				// 6 implies: Sixteen-bit Unicode Transformation Format, 
				// little endian UTF-16LE
				if (i == 6) {

					try {
						if (isToEncode) {

							deTextPane.setText(URLEncoder.encode(encodeText,
									"UTF-16LE"));
						} else {

							enTextPane.setText(URLDecoder.decode(decodeText,
									"UTF-16LE"));

						}

					} catch (UnsupportedEncodingException ech) {

						if (isToEncode) {
							deTextPane.setText("Error: String cannot be encoded...");
						} else {
							enTextPane.setText("Error: String cannot be decoded...");
						}

					}
				}

				// 7 implies Base64
				if (i == 7) {

					if (isToEncode) {

						deTextPane.setText(B64.encodeString(encodeText));

					} else {

						enTextPane.setText(B64.decodeString(decodeText));

					}
				}

				// 8 implies MD5Sum
				if (i == 8) {

					if (isToEncode) {

						try {

							final MessageDigest md5 = MessageDigest
							.getInstance("MD5");
							md5.update(encodeText.getBytes("iso-8859-1"), 0,
									encodeText.length());

							byte[] md5hash = new byte[32];
							md5hash = md5.digest();

							final String md5Value = convertToHex(md5hash);
							deTextPane.setText(md5Value);

						} catch (NoSuchAlgorithmException exception1) {

							deTextPane
							.setText("Error: MD5 could not be found...");

						} catch (UnsupportedEncodingException exception2) {

							deTextPane
							.setText("Error: MD5 String cannot be encoded...");

						}
					}
				}

				// 9 implies SHA-1
				if (i == 9) {

					if (isToEncode) {

						try {

							MessageDigest sha1 = MessageDigest
							.getInstance("SHA-1");
							sha1.update(encodeText.getBytes("iso-8859-1"), 0,
									encodeText.length());

							byte[] sha1hash = new byte[40];
							sha1hash = sha1.digest();

							final String sha1Value = convertToHex(sha1hash);
							deTextPane.setText(sha1Value);

						} catch (NoSuchAlgorithmException exception1) {

							deTextPane
							.setText("Error: SHA-1 could not be found...");

						} catch (UnsupportedEncodingException exception2) {

							deTextPane
							.setText("Error: SHA-1 String cannot be encoded...");

						}
					}
				}

				// 10 implies SHA-256
				if (i == 10) {

					if (isToEncode) {

						try {

							final MessageDigest sha256 = MessageDigest
							.getInstance("SHA-256");
							sha256.update(encodeText.getBytes("iso-8859-1"), 0,
									encodeText.length());

							byte[] sha256hash = new byte[64];
							sha256hash = sha256.digest();

							final String sha256Value = convertToHex(sha256hash);
							deTextPane.setText(sha256Value);

						} catch (NoSuchAlgorithmException exception2) {

							deTextPane
							.setText("Error: SHA-256 could not be found...");

						} catch (UnsupportedEncodingException exception2) {

							deTextPane
							.setText("Error: SHA-256 String cannot be encoded...");

						}
					}
				}

				// 11 implies SHA-384
				if (i == 11) {

					if (isToEncode) {

						try {

							final MessageDigest sha384 = MessageDigest
							.getInstance("SHA-384");
							sha384.update(encodeText.getBytes("iso-8859-1"), 0,
									encodeText.length());

							byte[] sha384hash = new byte[96];
							sha384hash = sha384.digest();

							final String sha256Value = convertToHex(sha384hash);
							deTextPane.setText(sha256Value);

						} catch (NoSuchAlgorithmException exception2) {

							deTextPane
							.setText("Error: SHA-384 could not be found...");

						} catch (UnsupportedEncodingException exception2) {

							deTextPane
							.setText("Error: SHA-384 String cannot be encoded...");

						}
					}
				}

				// 12 implies SHA-512
				if (i == 12) {

					if (isToEncode) {

						try {

							MessageDigest sha512 = MessageDigest
							.getInstance("SHA-512");
							sha512.update(encodeText.getBytes("iso-8859-1"), 0,
									encodeText.length());

							byte[] sha512hash = new byte[128];
							sha512hash = sha512.digest();

							final String sha256Value = convertToHex(sha512hash);
							deTextPane.setText(sha256Value);

						} catch (NoSuchAlgorithmException exception2) {

							deTextPane
							.setText("Error: SHA-512 could not be found...");

						} catch (UnsupportedEncodingException exception2) {

							deTextPane
							.setText("Error: SHA-512 String cannot be encoded...");

						}
					}
				}

				// 13 implies Hexadecimal lowercase
				if (i == 13) {					

					if (isToEncode) {

						try {
							final String hexValue = new String(Hex.encodeHex(encodeText.getBytes("iso-8859-1")));
							deTextPane.setText(hexValue);

						} catch (UnsupportedEncodingException e) {
							deTextPane.setText("Error: String input cannot be encoded...");
						} 

					} else {

						try {
							final String normalValue = new String(Hex.decodeHex(decodeText.toCharArray()));
							enTextPane.setText(normalValue);

						} catch (DecoderException e) {
							deTextPane.setText("Error: Hex value cannot be decoded...");
						} 

					}
				}

				// 14 implies Hexadecimal Uppercase
				if (i == 14) {					

					if (isToEncode) {

						try {
							final String hexValue = new String(Hex.encodeHex(encodeText.getBytes("iso-8859-1")));
							deTextPane.setText(hexValue.toUpperCase());

						} catch (UnsupportedEncodingException e) {
							deTextPane.setText("Error: String input cannot be encoded...");
						} 

					} else {

						try {
							final String normalValue = new String(Hex.decodeHex(decodeText.toCharArray()));
							enTextPane.setText(normalValue);

						} catch (DecoderException e) {
							deTextPane.setText("Error: Hex value cannot be decoded...");
						} 

					}
				}

				// 15 implies Binary
				if (i == 15) {					

					if (isToEncode) {

						try {
							final String binValue = new String(BinaryCodec.toAsciiChars(encodeText.getBytes("iso-8859-1")));
							deTextPane.setText(binValue.toUpperCase());

						} catch (UnsupportedEncodingException e) {
							deTextPane.setText("Error: String input cannot be encoded...");
						} 

					} else {

						try {
							final String normalValue = new String(BinaryCodec.fromAscii(decodeText.getBytes("iso-8859-1")));
							enTextPane.setText(normalValue);

						} catch (UnsupportedEncodingException e) {
							deTextPane.setText("Error: Binary value cannot be decoded...");
						} 

					}
				}

				// 16 implies www-form-urlencoded
				if (i == 16) {					

					final URLCodec codec = new URLCodec();

					if (isToEncode) {

						try {
							deTextPane.setText(codec.encode(encodeText, "UTF-8"));

						} catch (UnsupportedEncodingException e) {
							deTextPane.setText("Error: Sting input cannot be decoded");
						}

					} else {

						try {
							enTextPane.setText(codec.decode(decodeText, "UTF-8"));

						} catch (DecoderException e) {
							deTextPane.setText("Error: www-form-urlencoded value cannot be decoded...");
						}
						catch (UnsupportedEncodingException e) {
							deTextPane.setText("Error: www-form-urlencoded value cannot be decoded...");
						} 

					}
				}

				// 17 implies RFC 1521 MIME (Multipurpose Internet Mail Extensions) 
				// Part One. Rules #3, #4, and #5 of the quoted-printable spec are not implemented yet
				if (i == 17) {					

					final QuotedPrintableCodec codec = new QuotedPrintableCodec();

					if (isToEncode) {

						try {
							deTextPane.setText(codec.encode(encodeText));

						} catch (EncoderException e) {
							deTextPane.setText("Error: Sting input cannot be decoded");
						}

					} else {

						try {
							enTextPane.setText(codec.decode(decodeText));

						} catch (DecoderException e) {
							deTextPane.setText("Error: RFC 1521 MIME value cannot be decoded...");
						}

					}
				}

				// 18 implies HTML Encode
				if (i == 18) {

					if (isToEncode) {

						deTextPane.setText(StringEscapeUtils.escapeHtml(encodeText));

					} else {

						enTextPane.setText(StringEscapeUtils.unescapeHtml(decodeText));
					}
				}

				// 19 implies CSV Encode
				if (i == 19) {

					if (isToEncode) {

						deTextPane.setText(StringEscapeUtils.escapeCsv(encodeText));

					} else {

						enTextPane.setText(StringEscapeUtils.unescapeCsv(decodeText));
					}
				}

				// 20 implies Java
				if (i == 20) {

					if (isToEncode) {

						deTextPane.setText(StringEscapeUtils.escapeJava(encodeText));

					} else {

						enTextPane.setText(StringEscapeUtils.unescapeJava(decodeText));
					}
				}

				// 21 implies JavaScript
				if (i == 21) {

					if (isToEncode) {

						deTextPane.setText(StringEscapeUtils.escapeJavaScript(encodeText));

					} else {

						enTextPane.setText(StringEscapeUtils.unescapeJavaScript(decodeText));
					}
				}

				// 22 implies SQL
				if (i == 22) {

					if (isToEncode) {

						deTextPane.setText(StringEscapeUtils.escapeSql(encodeText));

					} else {
						// No reverse method exists within String Escape Utils
						// enTextPane.setText(StringEscapeUtils.unescapeSql(decodeText));
					}
				}

				// 23 implies XML
				if (i == 23) {

					if (isToEncode) {

						deTextPane.setText(StringEscapeUtils.escapeXml(encodeText));

					} else {

						enTextPane.setText(StringEscapeUtils.unescapeXml(decodeText));
					}
				}



			}

		} // for loop
	}

	private static String convertToHex(final byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				}
				else {
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString().toUpperCase(Locale.ENGLISH);
	}

	/**
	 * <p>
	 * Method for setting up the right click copy paste cut and select all menu.
	 * </p>
	 * <p>
	 * It passes the parameters of which options in the right click menu are
	 * enabled.
	 * </p>
	 * 
	 * @param area
	 *            JTextComponent
	 */
	public final void popupText(final JTextComponent area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i1_cut = new JMenuItem("Cut");
		final JMenuItem i2_copy = new JMenuItem("Copy");
		final JMenuItem i3_paste = new JMenuItem("Paste");
		final JMenuItem i4_select = new JMenuItem("Select All");

		i1_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		i2_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		i3_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK));
		i4_select.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		popmenu.add(i1_cut);
		popmenu.add(i2_copy);
		popmenu.add(i3_paste);
		popmenu.addSeparator();
		popmenu.add(i4_select);

		i1_cut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.cut();
			}
		});

		i2_copy.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.copy();
			}
		});

		i3_paste.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (area.isEditable()) {
					area.paste();
				}
			}
		});

		i4_select.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.selectAll();
			}
		});

		area.addMouseListener(new MouseAdapter() {
			private void checkForTriggerEvent(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					area.requestFocus();
					popmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				checkForTriggerEvent(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				checkForTriggerEvent(e);
			}
		});
	}
}
