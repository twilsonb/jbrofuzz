/**
 * JbroFuzz 2.5
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
package org.owasp.jbrofuzz.graph.canvas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Align;
import org.owasp.jbrofuzz.version.ImageCreator;

public class ResponseTimeChart {

	private static final int MAX_CHARS = 32;
	// The x-axis filenames
	private final String[] xData;
	// The y-axis data
	private final int[] yData;

	private final DefaultCategoryDataset dataset;

	public ResponseTimeChart() {

		this(0);

	}

	public ResponseTimeChart(final int size) {

		xData = new String[size];
		yData = new int[size];

		dataset = new DefaultCategoryDataset();

	}

	private int calculateValue(final File inputFile) {

		if (inputFile.isDirectory()) {
			return -1;
		}

		int responseTime = 0;

		BufferedReader inBuffReader = null;
		try {

			inBuffReader = new BufferedReader(new FileReader(inputFile));

			final StringBuffer one = new StringBuffer(MAX_CHARS);
			int counter = 0;
			int got;
			while (((got = inBuffReader.read()) > 0) && (counter < MAX_CHARS)) {

				one.append((char) got);
				counter++;

			}
			inBuffReader.close();

			one.delete(0, 5);
			one.delete(one.indexOf("\n--"), one.length());

			responseTime = Integer.parseInt(one.toString());

		} catch (final IOException e1) {

			return -2;

		} catch (final StringIndexOutOfBoundsException e2) {

			return -3;

		} catch (final NumberFormatException e3) {

			return -4;

		} finally {

			IOUtils.closeQuietly(inBuffReader);

		}

		return responseTime;
	}

	/**
	 * <p>
	 * Method for creating the final Chart.
	 * </p>
	 * 
	 * 
	 * @see #ResponseTimeChart(int)
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void createFinalPlotCanvas() {

	}

	public ChartPanel getPlotCanvas() {

		final JFreeChart chart = ChartFactory.createBarChart(
				"JBroFuzz Response Time Bar Chart", // chart title
				"File Name", // domain axis label
				"Response Time (ms)", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				true // URLs?
		);

		final Plot plot = chart.getPlot();
		plot.setBackgroundImage(ImageCreator.IMG_OWASP_MED.getImage());
		plot.setBackgroundImageAlignment(Align.TOP_RIGHT);

		final CategoryItemRenderer renderer = 
										chart.getCategoryPlot().getRenderer();
		renderer.setBaseToolTipGenerator(
									new StandardCategoryToolTipGenerator());

		return new ChartPanel(chart);

	}

	public void setValueAt(final int index, final File inputFile) {

		xData[index] = inputFile.getName();
		yData[index] = calculateValue(inputFile);

		dataset.addValue(yData[index], "Row 1", xData[index]);

	}

}
