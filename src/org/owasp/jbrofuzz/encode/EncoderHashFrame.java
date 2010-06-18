/**
 * JBroFuzz 2.2
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
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.BackingStoreException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.version.ImageCreator;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;
import org.owasp.jbrofuzz.version.JBroFuzzPrefs;

/**
 * <p>
 * Window inspired from Paros Proxy, in terms of providing an encoder/decoder
 * for a variety of different schemes, as well as hashing functionality.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 2.3
 * @since 1.5
 * 
 * @author Yiannis Marangos
 * @version 2.0
 * @since 2.0
 * 
 */
public class EncoderHashFrame extends JFrame {

	private static final long serialVersionUID = 8808832051334720865L;
	// Dimensions of the frame
	private static final int SIZE_X = 650;
	private static final int SIZE_Y = 400;

	private JSplitPane horizontalSplitPane, verticalSplitPaneLeft, verticalSplitPaneRight, commentSplitPane;

	private JTextPane enTextPane, deTextPane, recordingTextPane;

	// The tree
	private int treeCounter = 0;
	private JTree tree;

	private JButton swap, encode, decode, close;

	private static boolean windowIsShowing = false;
	
	private HashPanel commentPanel;
	
	private JPanel recordingPanel;

	public EncoderHashFrame(final JBroFuzzWindow parent) {
		
		if (windowIsShowing) {
			return;
		}
		windowIsShowing = true;

		// really inspired from Paros Proxy, but as a frame
		setTitle(" JBroFuzz - Encoder/Hash ");

		setIconImage(ImageCreator.IMG_FRAME.getImage());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 12));

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("Codes/Hashes");
		// Create a tree that allows one selection at a time
		tree = new JTree(top);
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// Selection can only contain one path at a time
		tree.getSelectionModel().setSelectionMode(1);

		// Create the scroll pane and add the tree to it.
		final JScrollPane leftScrollPane = new JScrollPane(tree);

		// Create all the right hand panels
		for (int i = 0; i < EncoderHashCore.CODES.length; i++) {
				top.add(new DefaultMutableTreeNode(EncoderHashCore.CODES[i]));
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
		HashPanelRightClick.popupText(enTextPane);

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
		HashPanelRightClick.popupText(deTextPane);

		final JScrollPane decodeScrollPane = new JScrollPane(deTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		decoderPanel.add(decodeScrollPane, BorderLayout.CENTER);

		// Text panes -> Comment
		commentPanel = new HashPanel("");
		recordingPanel = new JPanel(new BorderLayout());
		
		recordingTextPane = new JTextPane();
		recordingTextPane.putClientProperty("charset", "UTF-8");
		recordingTextPane.setEditable(true);
		recordingTextPane.setVisible(true);
		recordingTextPane.setFont(new Font("Verdana", Font.PLAIN, 12));

		recordingTextPane.setMargin(new Insets(1, 1, 1, 1));
		recordingTextPane.setBackground(Color.WHITE);
		enTextPane.setForeground(new Color(51, 102, 102));
		
		final JScrollPane recordingScrollPane = new JScrollPane(recordingTextPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		recordingPanel.add(recordingScrollPane, BorderLayout.CENTER);

		
		// loading content of last runs
		String context = "";
		int loose = 0;
		int i = 0;
		for (i = 0; i <= 50; i++){
			treeCounter = i;
			String encValue = JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[0]+ "." + i, "");
			String decValue = JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[1] + "." + i, "");
			if (encValue.length() > 0){
				context += treeCounter + ":  " + encValue + " => " + decValue + " \n";
			}
			else{
			   loose++;
			}
		}
		treeCounter = i - loose;
		if (treeCounter > 40) treeCounter = 0; // reset and start overwriting existing values;
		if (treeCounter < 0 ) treeCounter = 0; // reset and start overwriting existing values;
		if (i > 0 && i <= 40) treeCounter = i;
		treeCounter++;
		recordingTextPane.setText(context);
		
		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		verticalSplitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		commentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		commentSplitPane.setLeftComponent(verticalSplitPaneLeft);
		commentSplitPane.setRightComponent(verticalSplitPaneRight);
		
		verticalSplitPaneLeft.setTopComponent(encoderPanel);
		verticalSplitPaneLeft.setBottomComponent(decoderPanel);

		verticalSplitPaneRight.setTopComponent(commentPanel);
		verticalSplitPaneRight.setBottomComponent(recordingPanel);
		
		horizontalSplitPane.setLeftComponent(leftScrollPane);
		horizontalSplitPane.setRightComponent(commentSplitPane);

		
		// Set the minimum size for all components
		leftScrollPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		verticalSplitPaneLeft.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		commentSplitPane.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		
		encoderPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		decoderPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		commentPanel.setMinimumSize(JBroFuzzFormat.ZERO_DIM);
		
		horizontalSplitPane.setDividerLocation(180);
		verticalSplitPaneLeft.setDividerLocation(SIZE_Y / 2);
		commentSplitPane.setDividerLocation(280);
		
		// Traverse tree from root
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		parent.getPanelPayloads().expandAll(tree, new TreePath(root), true);

		// Bottom three buttons
		swap = new JButton(" Swap ");
		encode = new JButton(" Encode/Hash ");
		decode = new JButton(" Decode ");
		close = new JButton(" Close ");

		swap.setToolTipText(" Swap the contents of encoded text with the decoded text ");
		final String desc = "Select an encoding or hashing scheme from the left hard side";
		encode.setToolTipText(desc);
		decode.setToolTipText(desc);
		close.setToolTipText(" Close this window ");
		
		swap.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						swapTexts();

					}

				});
			}
		});

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
						windowIsShowing = false;
						saveValues();
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
					windowIsShowing = false;
					saveValues();
					dispose();
				}
			}
		});

		// Keyboard listener on the treeView for Ctrl+Return to Encode
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					windowIsShowing = false;
					saveValues();
				}
			}
		});


		// Keyboard listener on the decoded text pane for escape to cancel
		deTextPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					windowIsShowing = false;
					saveValues();
					dispose();
				}
			}
		});

		// Keyboard listener on the encoded text pane for escape to cancel
		enTextPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					windowIsShowing = false;
					saveValues();
					dispose();
				}
			}
		});

		// Keyboard listeners on the buttons for escape to cancel
		encode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					windowIsShowing = false;
					saveValues();
				}
			}
		});

		decode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					windowIsShowing = false;
				}
			}
		});

		close.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					windowIsShowing = false;
					saveValues();
					dispose();
				}
			}
		});
		
		tree.addTreeSelectionListener( new TreeSelectionListener() {
			public void valueChanged(final TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				
				if (node == null) {
					return;
				}
				final String coderName = node.toString();
				
				decode.setEnabled( EncoderHashCore.isDecoded(coderName) );
				commentPanel.setText( EncoderHashCore.getComment(coderName) );
			}
		});

        // alt+enter to encode
        Action doEncode = new AbstractAction() {

			private static final long serialVersionUID = -7686474340015136816L;

			public void actionPerformed(final ActionEvent e) {
                calculate(true);
            }
        };

        enTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(Event.ENTER, Event.ALT_MASK), "doEncode");
        enTextPane.getActionMap().put("doEncode", doEncode);
        
        // alt+backspace to decode
        Action doDecode = new AbstractAction() {

			private static final long serialVersionUID = 3083350774016663021L;

			public void actionPerformed(final ActionEvent e) {
                calculate(false);
            }
        };

        enTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(Event.BACK_SPACE, Event.ALT_MASK), "doDecode");
        enTextPane.getActionMap().put("doDecode", doDecode);

		// Bottom buttons

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				15, 15));
		buttonPanel.add(swap);
		buttonPanel.add(encode);
		buttonPanel.add(decode);
		buttonPanel.add(close);

		// Add the split pane to this panel
		getContentPane().add(horizontalSplitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// Where to show the encoder/hash frame
		this.setLocation(
				parent.getLocation().x + (parent.getWidth() - SIZE_X) / 2, 
				parent.getLocation().y + (parent.getHeight() - SIZE_Y) / 2
		);

		this.setSize(EncoderHashFrame.SIZE_X, EncoderHashFrame.SIZE_Y);
		setMinimumSize(new Dimension(SIZE_X / 2, SIZE_Y / 2));

		setResizable(true);
		setVisible(true);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				windowIsShowing = false;
				saveValues();
			}
		});

		/*
		// acutally replaced by new window on right side.
		// Load the values of encode/decode from the preferences		
		enTextPane.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[0] + ".0", ""));
	    deTextPane.setText(JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[1] +".0", ""));

		final String encoder_type = JBroFuzz.PREFS.get(JBroFuzzPrefs.ENCODER[2]+".1", "");
		
		for (i=0; i < EncoderHashCore.CODES.length; i++)
			if ( EncoderHashCore.CODES[i].equalsIgnoreCase(encoder_type) ) {
				tree.setSelectionRow( i+1 );
				break;
			}
		 */
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
	private void calculate(boolean isToEncode) {

		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if (node == null) {
			return;
		}
		
		final String s = node.toString();
		if (isToEncode) {
			final String encodeText = enTextPane.getText();
			deTextPane.setText(EncoderHashCore.encode(encodeText, s));
		} else {
			final String decodeText = deTextPane.getText();
			enTextPane.setText(EncoderHashCore.decode(decodeText, s));
		}
	}
	
	/**
	 * <p>Swap the texts in the encoding and decoding panels.</p>
	 * 
	 * @author subere@uncon.org
	 * @version 2.3
	 * @since 2.3
	 */
	private void swapTexts() {
		
		final String enText = enTextPane.getText();
		final String deText = deTextPane.getText();
		
		enTextPane.setText(deText);
		deTextPane.setText(enText);
		
	}
	
	
	private void saveValues() {
		
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		// Save the values of the encode/decode as a preference
		JBroFuzz.PREFS.put(JBroFuzzPrefs.ENCODER[0] + "." + treeCounter, enTextPane.getText());
		JBroFuzz.PREFS.put(JBroFuzzPrefs.ENCODER[1] + "." + treeCounter, deTextPane.getText());
		if (node != null)
			JBroFuzz.PREFS.put(JBroFuzzPrefs.ENCODER[2] + "." + treeCounter, node.toString());
		
		try {
			JBroFuzz.PREFS.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		treeCounter++;
		encode.setText("encode " + treeCounter);
	}
}