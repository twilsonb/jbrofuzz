/**
 * JBroFuzz 0.9
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007, 2008 subere@uncon.org
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
package org.owasp.jbrofuzz.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.owasp.jbrofuzz.*;
import org.owasp.jbrofuzz.ui.menu.*;
import org.owasp.jbrofuzz.ui.panels.*;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.version.Format;

/**
 * <p>
 * The main window of JBroFuzz responsible for the graphical user interface.
 * </p>
 * <p>
 * This window holds all the Panels that are attached inside the TabbedPane
 * occupying the entire frame.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 0.9
 */
public class JBroFuzzWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8877330557328054872L;

	/**
	 * Unique int identifier for the Web Directory Panel
	 */
	public static final int ID_PANEL_WEB_DIRECTORIES = 121;

	/**
	 * Unique int identifier for the Sniffing Panel
	 */
	public static final int ID_PANEL_SNIFFING = 123;

	/**
	 * Unique int identifier for the Fuzzing Panel
	 */
	public static final int ID_PANEL_FUZZING = 124;

	/**
	 * Unique int identifier for the Generators Panel
	 */
	public static final int ID_PANEL_PAYLOADS = 125;

	/**
	 * Unique int identifier for the System Panel
	 */
	public static final int ID_PANEL_SYSTEM = 126;

	// The main Object behind it all...
	private final JBroFuzz mJBroFuzz;

	// The main menu bar attached to this window frame...
	private final JBRMenuBar mMenuBar;

	// The tabbed pane holding the different views
	private JTabbedPane tabbedPane;

	// The web directories panel
	private final DirectoriesPanel mWebDirectoriesPanel;

	// The main sniffing panel
	private final SniffingPanel mSniffingPanel;

	// The main definitions panel
	private final PayloadsPanel mDefinitionsPanel;

	// The main fuzzing panel
	private final FuzzingPanel mFuzzingPanel;

	// The system logger panel
	private final SystemPanel mSystemLogger;
	
	// The toolbar of the window
	private JBRToolBar mToolBar;

	/**
	 * <p>
	 * The constructor of the main window launched in JBroFuzz. This class should
	 * be instantiated as a singleton and never again.
	 * </p>
	 * 
	 * @param mJBroFuzz
	 *          JBroFuzz
	 */
	public JBroFuzzWindow(final JBroFuzz mJBroFuzz) {
		// The frame
		super("JBroFuzz " + Format.VERSION);
		this.mJBroFuzz = mJBroFuzz;
		
		// The container pane
		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		// The menu bar
		mMenuBar = new JBRMenuBar(this);
		setJMenuBar(mMenuBar);
		
		// The tool bar
		mToolBar = new JBRToolBar(this);
		
		
		// The tabbed panels
		mWebDirectoriesPanel = new DirectoriesPanel(this);
		mFuzzingPanel = new FuzzingPanel(this);
		mSniffingPanel = new SniffingPanel(this);
		mDefinitionsPanel = new PayloadsPanel(this);
		mSystemLogger = new SystemPanel(this);
		
		mWebDirectoriesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mFuzzingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		mSniffingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mDefinitionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mSystemLogger.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// The tabbed pane, 3 is for bottom orientation
		tabbedPane = new JTabbedPane(3);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		// tabbedPane.setPreferredSize(new Dimension(588,368));
		// tabbedPane.setBounds(0, 0, 895, 500);
		// Do not change the names!!!
		tabbedPane.add(" Fuzzing ", mFuzzingPanel);
		tabbedPane.add(" Sniffing ", mSniffingPanel);
		tabbedPane.add(" Payloads ", mDefinitionsPanel);
		tabbedPane.add(" Web Directories ", mWebDirectoriesPanel);
		
		tabbedPane.setSelectedComponent(mDefinitionsPanel);
		
		/*
		// Close button on the tabs
		ImageIcon closeButton = ImageCreator.EXIT_IMG;
		// closeButton.setPreferredSize(new Dimension(ImageCreator.EXIT_IMG.getIconWidth(), ImageCreator.EXIT_IMG.getIconHeight()));
		ActionListener al = new ActionListener () {
			public void actionPerformed (ActionEvent ae)
			{
				JButton btn = (JButton) ae.getSource ();
				String s1 = btn.getActionCommand ();
				for (int i = 1; i < tabbedPane.getTabCount (); i++)
				{
					JPanel pnl = (JPanel) tabbedPane.getTabComponentAt (i);
					btn = (JButton) pnl.getComponent (0);
					String s2 = btn.getActionCommand ();
					if (s1.equals (s2))
					{
						tabbedPane.removeTabAt (i);
						break;
					}
				}
			}
		};
		closeButton.addActionListener(al);
		
		JPanel pnl = new JPanel ();
		pnl.setOpaque (false);
		pnl.add (closeButton);
		for(int i = 0; i < tabbedPane.getTabCount(); i++) {
			tabbedPane.setTabComponentAt(i, pnl);
		}
		*/
		pane.add(mToolBar, BorderLayout.PAGE_START);
	    pane.add(tabbedPane, BorderLayout.CENTER);
	    
		// The image icon and min size
		setIconImage(ImageCreator.FRAME_IMG.getImage());
		setMinimumSize (new Dimension (400, 300));
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exitProcedure();
			}
		});
	     
		this.setLocation(50, 100);
		this.setSize(950, 600);
		setResizable(true);
		setVisible(true);

		log("System Launch, Welcome!");
	}

	/**
	 * <p>
	 * Method returning the m definitions panel that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mDefinitionsPanel
	 */
	public PayloadsPanel getPanelPayloads() {
		return mDefinitionsPanel;
	}

	/**
	 * <p>
	 * Method for returning the m menu bar that is being instantiated through the
	 * m window.
	 * </p>
	 * 
	 * @return mMenuBar
	 */
	public JBRMenuBar getFrameMenuBar() {
		return mMenuBar;
	}
	
	public JBRToolBar getFrameToolBar() {
		return mToolBar;
	}

	/**
	 * <p>
	 * Method for returning the fuzzing panel that is being instantiated through
	 * this frame window.
	 * </p>
	 * 
	 * @return mFuzzingPanel
	 */
	public FuzzingPanel getPanelFuzzing() {
		return mFuzzingPanel;
	}

	/**
	 * <p>
	 * Access the m object that is responsible for launching an instance of this
	 * class.
	 * </p>
	 * 
	 * @return JBroFuzz
	 */
	public JBroFuzz getJBroFuzz() {
		return mJBroFuzz;
	}

	/**
	 * <p>
	 * Method for accessing the Tabbed Pane within the current Frame Window.
	 * </p>
	 * 
	 * @return JTabbedPane
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * <p>
	 * Method returning the m sniffing panel that is being instantiated through
	 * the m window.
	 * </p>
	 * 
	 * @return mSniffingPanel
	 */
	public SniffingPanel getPanelSniffing() {
		return mSniffingPanel;
	}

	/**
	 * <p>
	 * Method for returning the web directoires panel that is being used.
	 * </p>
	 * 
	 * @return WebDirectoriesPanel
	 */
	public DirectoriesPanel getPanelWebDirectories() {
		return mWebDirectoriesPanel;
	}

	/**
	 * <p>
	 * Method for logging values within the system event log.
	 * </p>
	 * 
	 * @param str
	 *          String
	 */
	public void log(final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mSystemLogger.addLoggingEvent(str);
			}
		});
	}

	/**
	 * Method for setting up the right click copy paste cut and select all menu.
	 * 
	 * @param area
	 *          JTextArea
	 */
	public void popup(final JTextComponent area) {

		final JPopupMenu popmenu = new JPopupMenu();

		final JMenuItem i1 = new JMenuItem("Cut");
		final JMenuItem i2 = new JMenuItem("Copy");
		final JMenuItem i3 = new JMenuItem("Paste");
		final JMenuItem i4 = new JMenuItem("Select All");

		i1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		i2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		i3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK));
		i4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		popmenu.add(i1);
		popmenu.add(i2);
		popmenu.add(i3);
		popmenu.addSeparator();
		popmenu.add(i4);

		if (!area.isEditable()) {
			i3.setEnabled(false);
		}

		i1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.cut();
			}
		});

		i2.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				area.copy();
			}
		});

		i3.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (area.isEditable()) {
					area.paste();
				}
			}
		});

		i4.addActionListener(new ActionListener() {
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

	/**
	 * Set which tab to hide based on the int n of ID values. These are taken from
	 * the FrameWindow.
	 * 
	 * @param n
	 *          int
	 */
	public void setTabHide(final int n) {
		if (n == JBroFuzzWindow.ID_PANEL_PAYLOADS) {
			tabbedPane.remove(mDefinitionsPanel);
		}
		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {
			tabbedPane.remove(mFuzzingPanel);
		}
		if (n == JBroFuzzWindow.ID_PANEL_SNIFFING) {
			tabbedPane.remove(mSniffingPanel);
		}
		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			tabbedPane.remove(mSystemLogger);
		}
		if (n == JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES) {
			tabbedPane.remove(mWebDirectoriesPanel);
		}
		
	}

	/**
	 * Set which tab to show based on the int n of ID values. These are taken from
	 * the FrameWindow.
	 * 
	 * @param n
	 *          int
	 */
	public void setTabShow(final int n) {
		if (n == JBroFuzzWindow.ID_PANEL_PAYLOADS) {
			tabbedPane.addTab(" Payloads ", mDefinitionsPanel);
			tabbedPane.setSelectedComponent(mDefinitionsPanel);
		}
		if (n == JBroFuzzWindow.ID_PANEL_FUZZING) {
			tabbedPane.addTab(" Fuzzing ", mFuzzingPanel);
			tabbedPane.setSelectedComponent(mFuzzingPanel);
		}
		if (n == JBroFuzzWindow.ID_PANEL_SNIFFING) {
			tabbedPane.addTab(" Sniffing ", mSniffingPanel);
			tabbedPane.setSelectedComponent(mSniffingPanel);
		}
		if (n == JBroFuzzWindow.ID_PANEL_SYSTEM) {
			tabbedPane.addTab(" System ", mSystemLogger);
			tabbedPane.setSelectedComponent(mSystemLogger);
		}
		if (n == JBroFuzzWindow.ID_PANEL_WEB_DIRECTORIES) {
			tabbedPane.addTab(" Web Directories ", mWebDirectoriesPanel);
			tabbedPane.setSelectedComponent(mWebDirectoriesPanel);
		}
		
	}
	
	/**
	 * <p>
	 * Method for exiting the entire application.
	 * </p>
	 *
	 */
	public void exitProcedure() {
		
		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();
		
		mFuzzingPanel.stop();
		
		getJBroFuzz().getHandler().deleteEmptryDirectories();
		dispose();
		
	}

}

class MyTabbedPaneUI extends BasicTabbedPaneUI {
    public MyTabbedPaneUI() {
        super();
    }
 
    protected void paintTab(Graphics g, int tabPlacement,
                            Rectangle[] rects, int tabIndex,
                            Rectangle iconRect, Rectangle textRect) {
 
        super.paintTab(g,tabPlacement,rects,tabIndex,iconRect,textRect);
 
        Rectangle rect=rects[tabIndex];
        g.setColor(Color.black);
        g.drawRect(rect.x+5,rect.y+5,10,10);
        g.drawLine(rect.x+5,rect.y+5,rect.x+15,rect.y+15);
        g.drawLine(rect.x+15,rect.y+5,rect.x+5,rect.y+15);
    }
 
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        return super.calculateTabWidth(tabPlacement,tabIndex,metrics)+20;
    }
 
    protected MouseListener createMouseListener() {
        return new MyMouseHandler();
    }
 
    class MyMouseHandler extends MouseHandler {
        public MyMouseHandler() {
            super();
        }
        public void mouseClicked(MouseEvent e) {
            int x=e.getX();
            int y=e.getY();
            int tabIndex=-1;
            int tabCount = tabPane.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                if (rects[ i ].contains(x, y)) {
                    tabIndex= i;
                    break;
                }
            }
            if (tabIndex >= 0) {
                Rectangle tabRect=rects[tabIndex];
                x=x-tabRect.x;
                y=y-tabRect.y;
                if ((x>=5) && (x<=15) && (y>=5) && (y<=15)) {
                    tabPane.remove(tabIndex);
                }
            }
        }
 
    }
}
