/**
 * JbroFuzz 2.5
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 * hardening for version 2.4 by daemonmidi@gmail.com
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

public class HammingDistanceChart {

	private static final int MAX_CHARS = 1048576;
	// The x-axis filenames
	private final String[] xData;
	// The y-axis data
	private final double[] yData;
	// The data to be displayed
	private final DefaultCategoryDataset dataset;

	// The hash set with all the characters of the first response
	private final StringBuffer firstSet;

	// Constants
	private final static String END_SIGNATURE = "--jbrofuzz-->\n";

	public HammingDistanceChart() {

		this(0);

	}

	public HammingDistanceChart(final int size) {

		xData = new String[size];
		yData = new double[size];

		dataset = new DefaultCategoryDataset();

		firstSet = new StringBuffer();

	}

	private void calculateFirstSet(final File inputFile) {

		BufferedReader inBuffReader = null;
		try {

			inBuffReader = new BufferedReader(new FileReader(inputFile));

			int counter = 0;
			int check = 0;
			int got;
			while (((got = inBuffReader.read()) > 0) && (counter < MAX_CHARS)) {

				// If we are passed "--jbrofuzz-->\n" in the file
				if (check == END_SIGNATURE.length()) {

					firstSet.append((char) got);

				}
				// Else find "--jbrofuzz-->\n" using a counter
				else {
					// Increment the counter for each success
					if (got == END_SIGNATURE.charAt(check)) {
						check++;
					} else {
						check = 0;
					}
				}

				counter++;

			}
			inBuffReader.close();

		} catch (final IOException e1) {
			System.out.println("An IOException occurred.");
		} finally {

			IOUtils.closeQuietly(inBuffReader);
		}

	}

	private int calculateValue(final File inputFile) {

		int hammingDistance = 0;

		BufferedReader inBuffReader = null;
		try {

			inBuffReader = new BufferedReader(new FileReader(inputFile));

			// Counter looping through the first response
			int counter1 = 0;
			int counter = 0;
			int check = 0;
			int got;
			while (((got = inBuffReader.read()) > 0) && (counter < MAX_CHARS)) {

				// If we are passed "--jbrofuzz-->\n" in the file
				if (check == END_SIGNATURE.length()) {

					if (counter1 >= firstSet.length()) {
						// For each extra character add to the distance
						hammingDistance++;

					} else {
						// The current character is not equal to the
						// one in the buffer, increment
						if ((char) got != firstSet.charAt(counter1)) {
							hammingDistance++;
						}
					}

					counter1++;
				}
				// Else find "--jbrofuzz-->\n" using a counter
				else {
					// Increment the counter for each success
					if (got == END_SIGNATURE.charAt(check)) {
						check++;
					} else {
						check = 0;
					}
				}

				counter++;

			}
			inBuffReader.close();

		} catch (final IOException e1) {
			System.out.println("An IOException occurred.");
		} finally {

			IOUtils.closeQuietly(inBuffReader);

		}

		return hammingDistance;
	}

	public void createFinalPlotCanvas() {

	}

	public ChartPanel getPlotCanvas() {

		final JFreeChart chart = ChartFactory.createBarChart(
				"JBroFuzz Hamming Distance Bar Chart", // chart title
				"File Name", // domain axis label
				"Hamming Distance", // range axis label
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

		if (index == 0) {

			calculateFirstSet(inputFile);
			yData[index] = 0;

		} else {

			yData[index] = calculateValue(inputFile);

		}

		dataset.addValue(yData[index], "Row 1", xData[index]);

	}

}
