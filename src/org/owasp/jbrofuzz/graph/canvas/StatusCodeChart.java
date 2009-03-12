/**
 * JBroFuzz 1.3
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
package org.owasp.jbrofuzz.graph.canvas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.Align;
import org.owasp.jbrofuzz.util.ImageCreator;

public class StatusCodeChart {

	public static final int		MAX_CHARS	= 32;
	// The x-axis filenames
	String[]									x_data;
	// The y-axis data
	int[]											y_data;

	private DefaultPieDataset	dataset;

	public StatusCodeChart() {

		this(0);

	}

	public StatusCodeChart(int size) {

		x_data = new String[size];
		y_data = new int[size];

		dataset = new DefaultPieDataset();
		dataset.setValue("501", 25);
		dataset.setValue("302", 25);
		dataset.setValue("404", 25);
		dataset.setValue("200", 25);
	}

	private int calculateValue(File f) {

		if (f.isDirectory()) {
			return -1;
		}

		int status = 0;

		BufferedReader in = null;
		try {

			in = new BufferedReader(new FileReader(f));

			StringBuffer one = new StringBuffer(MAX_CHARS);
			int counter = 0;
			int c;
			while (((c = in.read()) > 0) && (counter < MAX_CHARS)) {

				one.append((char) c);
				counter++;

			}
			in.close();

			one.delete(0, one.indexOf("\n--\n") + 4);
			one.delete(one.indexOf("\n--"), one.length());

			status = Integer.parseInt(one.toString());

		} catch (final IOException e1) {

			return -2;

		} catch (final StringIndexOutOfBoundsException e2) {

			return -3;

		} catch (final NumberFormatException e3) {

			return 0;

		} finally {

			IOUtils.closeQuietly(in);

		}

		return status;
	}

	/**
	 * <p>
	 * Method for creating the final Chart.
	 * </p>
	 * 
	 * 
	 * @see #StatusCodeChart(int)
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	public void createFinalPlotCanvas() {

		final String ERROR = "---";
		final String ZERO = "000";

		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for (int n : y_data) {

			if (n > 0) {

				final String value = "" + n;

				if (map.containsKey(value))
					map.put(value, map.get(value) + 1);
				else
					map.put(value, 1);

			}
			// Got response but no number identified
			else if ((n == 0)) {
				if (map.containsKey(ZERO))
					map.put(ZERO, map.get(ZERO) + 1);
				else
					map.put(ZERO, 1);

			}
			// Directory, IOException, String out of bounds
			else {
				if (map.containsKey(ERROR))
					map.put(ERROR, map.get(ERROR) + 1);
				else
					map.put(ERROR, 1);

			}

		}

		dataset = new DefaultPieDataset();

		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry) it.next();

			dataset.setValue(entry.getKey(), (double) entry.getValue()
					/ (double) y_data.length);

		}
	}

	public ChartPanel getPlotCanvas() {

		JFreeChart chart = ChartFactory.createPieChart(
				"JBroFuzz Status Code Pie Chart", dataset, true, // legend?
				true, // tooltips?
				false // URLs?
				);

		Plot plot = chart.getPlot();
		plot.setBackgroundImage(ImageCreator.IMG_OWASP_MED.getImage());
		plot.setBackgroundImageAlignment(Align.TOP_RIGHT);

		return new ChartPanel(chart);

	}

	public void setValueAt(int index, File f) {

		x_data[index] = f.getName();
		y_data[index] = calculateValue(f);

	}

}
