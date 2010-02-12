package org.owasp.jbrofuzz.graph.canvas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
import org.owasp.jbrofuzz.util.ImageCreator;

public class JaccardIndexChart {

	private static final int MAX_CHARS = 1048576;
	// The x-axis filenames
	private String[] xData;
	// The y-axis data
	private double[] yData;
	// The data to be displayed
	private DefaultCategoryDataset dataset;

	// The hash set with all the characters of the first response
	private HashSet<Character> firstSet;

	public JaccardIndexChart() {

		this(0);

	}

	public JaccardIndexChart(int size) {

		xData = new String[size];
		yData = new double[size];

		dataset = new DefaultCategoryDataset();

		firstSet = new HashSet<Character>();

	}

	private void calculateFirstSet(File f) {

		final String END_SIGNATURE = "--jbrofuzz-->\n";

		BufferedReader in = null;
		try {

			in = new BufferedReader(new FileReader(f));

			int counter = 0;
			int check = 0;
			int c;
			while (((c = in.read()) > 0) && (counter < MAX_CHARS)) {

				// If we are passed "--jbrofuzz-->\n" in the file
				if (check == END_SIGNATURE.length()) {

					firstSet.add((char) c);

				}
				// Else find "--jbrofuzz-->\n" using a counter
				else {
					// Increment the counter for each success
					if (c == END_SIGNATURE.charAt(check)) {
						check++;
					} else {
						check = 0;
					}
				}

				counter++;

			}
			in.close();

		} catch (final IOException e1) {

		} finally {

			IOUtils.closeQuietly(in);
		}

	}

	private double calculateValue(File f) {

		final String END_SIGNATURE = "--jbrofuzz-->\n";

		HashSet<Character> secondSet = new HashSet<Character>();

		BufferedReader in = null;
		try {

			in = new BufferedReader(new FileReader(f));

			int counter = 0;
			int check = 0;
			int c;
			while (((c = in.read()) > 0) && (counter < MAX_CHARS)) {

				// If we are passed "--jbrofuzz-->\n" in the file
				if (check == END_SIGNATURE.length()) {

					secondSet.add((char) c);

				}
				// Else find "--jbrofuzz-->\n" using a counter
				else {
					// Increment the counter for each success
					if (c == END_SIGNATURE.charAt(check)) {
						check++;
					} else {
						check = 0;
					}
				}

				counter++;

			}
			in.close();

		} catch (final IOException e1) {

		} finally {

			IOUtils.closeQuietly(in);

		}

		// Calculate the Jaccard Index, between the 2 sets of chars
		Set<Character> intersectionSet = new HashSet<Character>(firstSet);
		intersectionSet.retainAll(secondSet);

		Set<Character> unionSet = new HashSet<Character>(firstSet);
		unionSet.addAll(secondSet);
		// The index is the ratio
		return ((double) intersectionSet.size() / (double) unionSet.size());
	}

	public void createFinalPlotCanvas() {

	}

	public ChartPanel getPlotCanvas() {

		JFreeChart chart = ChartFactory.createBarChart(
				"JBroFuzz Jaccard Index Bar Chart", // chart title
				"File Name", // domain axis label
				"Jaccard Similarity Coefficient", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				true // URLs?
		);

		Plot plot = chart.getPlot();
		plot.setBackgroundImage(ImageCreator.IMG_OWASP_MED.getImage());
		plot.setBackgroundImageAlignment(Align.TOP_RIGHT);

		CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
		renderer
		.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		return new ChartPanel(chart);

	}

	public void setValueAt(int index, File f) {

		xData[index] = f.getName();

		if (index == 0) {

			calculateFirstSet(f);
			yData[index] = 1;

		} else {

			yData[index] = calculateValue(f);

		}

		dataset.addValue(yData[index], "Jaccard Index", xData[index]);

	}

}
