/**
 * WindowViewer.java 0.6
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon org
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

import java.awt.event.*;

import javax.swing.*;

import org.owasp.jbrofuzz.io.*;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataCurve;
import jcckit.data.DataPlot;
import jcckit.data.DataPoint;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;

/**
 * <p>Class extending a JFrame for displaying fuzzed results in a linear 
 * graph.</p>
 *
 * @author subere (at) uncon org
 * @version 0.6
 * @since 0.6
 */
public class WindowPlotter extends JFrame {
	// The main frame window
	private FrameWindow m;
	// The name of the JFrame displayed as a title
	private String name;

	// The x size of the frame
	private static final int x = 750;
	// The y size of the frame
	private static final int y = 450;

	private int[] _data = null;
	private DataPlot _dataPlot;

	public WindowPlotter(FrameWindow m, String name) {
		super(name);
		this.name = name;
		this.m = m;

		_data = FileHandler.readFuzzDirectoryFiles(this.m);

		GraphicsPlotCanvas plotCanvas = createPlotCanvas();

		_dataPlot = new DataPlot();
		_dataPlot.addElement(new DataCurve(""));
		plotCanvas.connect(_dataPlot);

		setLayout(new BorderLayout());
		add(plotCanvas.getGraphicsCanvas(), BorderLayout.CENTER);
		add(createControlPanel(), BorderLayout.SOUTH);

		// Global frame issues
		setLocation(180, 140);
		setSize(x, y);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(2);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == 27) {
					dispose();
				}
			}
		});
		
		int [] results = FileHandler.readFuzzDirectoryFiles(this.m);
		
		for(int i = 0; i < results.length; i++) {
			System.out.print(" " + results[i]);
		}
		System.out.println("");
	}

	private GraphicsPlotCanvas createPlotCanvas() {
		Properties props = new Properties();
		ConfigParameters config
		= new ConfigParameters(new PropertiesBasedConfigData(props));
		props.put("plot/legendVisible", "false");
		props.put("plot/coordinateSystem/xAxis/minimum", "-0.5");
		props.put("plot/coordinateSystem/xAxis/maximum", "6.5");
		props.put("plot/coordinateSystem/xAxis/axisLabel", "");
		props.put("plot/coordinateSystem/xAxis/ticLabelFormat/className",
		"jcckit.plot.TicLabelMap");
		props.put("plot/coordinateSystem/xAxis/ticLabelFormat/map",
		"0=Mo;1=Tu;2=We;3=Th;4=Fr;5=Sa;6=Su");
		props.put("plot/coordinateSystem/yAxis/axisLabel", "fuzzing fingerprint");
		props.put("plot/coordinateSystem/yAxis/maximum", "1000");
		props.put("plot/coordinateSystem/yAxis/ticLabelFormat", "%d%%");
		props.put("plot/curveFactory/definitions", "curve");
		props.put("plot/curveFactory/curve/withLine", "false");
		props.put("plot/curveFactory/curve/symbolFactory/className", 
		"jcckit.plot.BarFactory");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/className", 
		"jcckit.graphic.ShapeAttributes");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/fillColor", 
		"0xfe8000");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/lineColor", 
		"0");
		props.put("plot/curveFactory/curve/symbolFactory/size", "0.08");
		props.put("plot/initialHintForNextCurve/className", 
		"jcckit.plot.PositionHint");
		props.put("plot/initialHintForNextCurve/position", "0 0.1");

		return new GraphicsPlotCanvas(config);
	}
	
	private Panel createControlPanel() {
		Panel controlPanel = new Panel();
		Button startButton = new Button("animate");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						animate();
					}
				}.start();
			}
		});
		controlPanel.add(startButton);

		return controlPanel;
	}
	

	private void animate() {
		DataCurve curve = new DataCurve("");
		for (int i = 0; i < _data.length; i++) {
			curve.addElement(new DataPoint(i, 0));
		}
		_dataPlot.replaceElementAt(0, curve);

		for (int i = 0; i < _data.length; i++) {
			double x = i;
			double y = 0;
			while (y < _data[i]) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {}
				y = Math.min(_data[i], y + 5);
				curve.replaceElementAt(i, new DataPoint(x, y));
			}
		}
	}

}	// Frame class
