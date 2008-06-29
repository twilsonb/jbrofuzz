/**
 * JBroFuzz 1.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for penetration tests.
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
 * 
 */
package org.owasp.jbrofuzz.ui.viewers;

import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

import javax.swing.*;

import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataCurve;
import jcckit.data.DataPlot;
import jcckit.data.DataPoint;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;

import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ui.JBroFuzzWindow;
import org.owasp.jbrofuzz.util.ImageCreator;
import org.owasp.jbrofuzz.util.SwingWorker3;

/**
 * <p>
 * Class extending a JFrame for displaying fuzzed results in 
 * various linear graphs.
 * </p>
 * 
 * @author subere@uncon.org
 * @version 1.0
 * @since 0.6
 */
public class WindowPlotter extends JFrame {

	private static final long serialVersionUID = -5604856799032027536L;

	// The x size of the frame
	private static final int x = 750;
	// The y size of the frame
	private static final int y = 450;

	// The progress bar used
	private JProgressBar progressBar;

	public WindowPlotter(final JBroFuzzWindow parent, final String name) {

		super("JBroFuzz - " + name);
		setLayout(new BorderLayout());
		setIconImage(ImageCreator.FRAME_IMG.getImage());

		// Get the total number of files - early on as its used for the length
		final String[] x_data =  FileHandler.getFileList();

		// Define the Progress Bar - 4 loops to progress through
		progressBar = new JProgressBar(0, x_data.length);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBounds(410, 465, 120, 20);

		// Define the bottom panel with the progress bar
		final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(progressBar);

		// Define the tabs panel
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);

		// Add the corresponding components
		this.add(tabs, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);



		// Create the plot canvas for the first graph
		final int xMin1 = 0;
		final int xMax1 = x_data.length + 1;
		final int yMin1 = 0;
		final int yMax1 = 1000;

		final Properties props1 = new Properties();
		final ConfigParameters config1 = new ConfigParameters(new PropertiesBasedConfigData(props1));
		props1.put("foreground", "0xffffff");
		props1.put("background", "0");

		props1.put("plot/legendVisible", "false");
		props1.put("plot/coordinateSystem/xAxis/minimum", "" + xMin1);
		props1.put("plot/coordinateSystem/xAxis/maximum", "" + xMax1);
		props1.put("plot/coordinateSystem/xAxis/axisLabel", "Fuzzing Instance File Generated");

		props1.put("plot/coordinateSystem/yAxis/minimum", "" + yMin1);
		props1.put("plot/coordinateSystem/yAxis/maximum", "" + yMax1);
		props1.put("plot/coordinateSystem/yAxis/axisLabel", " Hash Value [0 - 1000]");

		props1.put("plot/curveFactory/definitions", "curve");
		props1.put("plot/curveFactory/curve/withLine", "true");

		props1.put("plot/initialHintForNextCurve/className","jcckit.plot.PositionHint");
		props1.put("plot/initialHintForNextCurve/position", "0 0.1");

		props1.put("plot/curveFactory/curve/initialHintForNextPoint/className","jcckit.plot.ShapeAttributesHint");
		props1.put("plot/curveFactory/curve/initialHintForNextPoint/initialAttributes/fillColor", "0x00090");
		props1.put("plot/curveFactory/curve/initialHintForNextPoint/fillColorHSBIncrement", "0.0 0.0 0.018");

		props1.put("plot/curveFactory/curve/symbolFactory/className", "jcckit.plot.CircleSymbolFactory");
		props1.put("plot/curveFactory/curve/symbolFactory/size", "0.015");

		final StringBuffer xAxisMap = new StringBuffer();
		// Go up to 1000 files for generating the index
		for (int i = 0; i <= Math.min(x_data.length, 100000); i++) {
			
			if (i == 0) {
				xAxisMap.append("0=0;");
			} else {
				xAxisMap.append(i + "=" + x_data[i - 1] + ";");
			}
			
		}
		xAxisMap.append("" + (Math.min(x_data.length, 100000) + 1) + "=;" );
		
		props1.put("plot/coordinateSystem/xAxis/ticLabelFormat/className", "jcckit.plot.TicLabelMap");
		props1.put("plot/coordinateSystem/xAxis/ticLabelFormat/map", xAxisMap.toString());

		// Define the first data plot of hash values
		final GraphicsPlotCanvas pc1 = new GraphicsPlotCanvas(config1);
		final DataPlot dp1 = new DataPlot();
		dp1.addElement(new DataCurve(""));
		pc1.connect(dp1);

		// Define the plot canvas for the second graph

		final int xMin2 = 0;
		final int xMax2 = x_data.length + 1;
		final int yMin2 = 0;
		final int yMax2 = FileHandler.getFuzzDirBigestFile() + (FileHandler.getFuzzDirBigestFile() / 10);

		final Properties props2 = new Properties();
		final ConfigParameters config2 = new ConfigParameters(new PropertiesBasedConfigData(props2));

		props2.put("foreground", "0xffffff");
		props2.put("background", "0");

		props2.put("plot/legendVisible", "false");
		props2.put("plot/coordinateSystem/xAxis/minimum", "" + xMin2);
		props2.put("plot/coordinateSystem/xAxis/maximum", "" + xMax2);
		props2.put("plot/coordinateSystem/xAxis/axisLabel", "Fuzzing Instance File Generated");

		props2.put("plot/coordinateSystem/yAxis/minimum", "" + yMin2);
		props2.put("plot/coordinateSystem/yAxis/maximum", "" + yMax2);
		props2.put("plot/coordinateSystem/yAxis/axisLabel", " File Size (bytes)");

		props2.put("plot/curveFactory/definitions", "curve");
		props2.put("plot/curveFactory/curve/withLine", "true");

		props2.put("plot/initialHintForNextCurve/className","jcckit.plot.PositionHint");
		props2.put("plot/initialHintForNextCurve/position", "0 0.1");

		props2.put("plot/curveFactory/curve/initialHintForNextPoint/className","jcckit.plot.ShapeAttributesHint");
		props2.put("plot/curveFactory/curve/initialHintForNextPoint/initialAttributes/fillColor", "0x00090");
		props2.put("plot/curveFactory/curve/initialHintForNextPoint/fillColorHSBIncrement", "0.0 0.0 0.018");

		props2.put("plot/curveFactory/curve/symbolFactory/className", "jcckit.plot.CircleSymbolFactory");
		props2.put("plot/curveFactory/curve/symbolFactory/size", "0.015");

		props2.put("plot/coordinateSystem/xAxis/ticLabelFormat/className", "jcckit.plot.TicLabelMap");
		props2.put("plot/coordinateSystem/xAxis/ticLabelFormat/map", xAxisMap.toString());


		// Define the second data plot of file lengths
		final GraphicsPlotCanvas pc2 = new GraphicsPlotCanvas(config2);
		final DataPlot dp2 = new DataPlot();
		dp2.addElement(new DataCurve(""));
		pc2.connect(dp2);

		// Bring in the y data values
		final int[] y_data = new int[x_data.length]; 
		final int[] z_data = new int[x_data.length];

		final DataCurve curve1 = new DataCurve("");
		final DataCurve curve2 = new DataCurve("");

		SwingWorker3 worker = new SwingWorker3() {

			@Override
			public Object construct() {

				for(int a = 0; a < x_data.length; a++) {

					y_data[a] = FileHandler.getFuzzFileHash(x_data[a]);
					z_data[a] = FileHandler.getFuzzFileSize(x_data[a]);

					curve1.addElement(new DataPoint(a, 0));
					curve2.addElement(new DataPoint(a, 0));

					final int x = a + 1;

					curve1.replaceElementAt(a, new DataPoint(x, y_data[a]));
					curve2.replaceElementAt(a, new DataPoint(x, z_data[a]));

					progressBar.setValue(a);

				}
				dp1.replaceElementAt(0, curve1);
				dp2.replaceElementAt(0, curve2);

				progressBar.setValue(x_data.length);
				return "return-worker";
			}

		};
		worker.start();

		// Add the elements to the tab and a key listener to close
		tabs.add(" Hash Value ", pc1.getGraphicsCanvas());
		tabs.add(" File Size ", pc2.getGraphicsCanvas());
		tabs.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					WindowPlotter.this.dispose();
				}
			}
		});
		
		// Global frame issues
		this.setLocation(Math.abs(parent.getLocation().x + 100), Math.abs(parent.getLocation().y + 100));
		setDefaultCloseOperation(2);
		setResizable(true);
		this.setSize(x, y);
		setVisible(true);

	}

} // Frame class
