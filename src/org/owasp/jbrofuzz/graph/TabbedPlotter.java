/**
 * JBroFuzz 2.0
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
package org.owasp.jbrofuzz.graph;

import java.io.File;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.owasp.jbrofuzz.graph.canvas.HammingDistanceChart;
import org.owasp.jbrofuzz.graph.canvas.JaccardIndexChart;
import org.owasp.jbrofuzz.graph.canvas.ResponseHeaderSizeChart;
import org.owasp.jbrofuzz.graph.canvas.ResponseSizeChart;
import org.owasp.jbrofuzz.graph.canvas.ResponseTimeChart;
import org.owasp.jbrofuzz.graph.canvas.StatusCodeChart;
import org.owasp.jbrofuzz.help.HelpChart;

class TabbedPlotter extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final GraphingPanel gPanel;

	private StatusCodeChart statusChart;
	private ResponseTimeChart rTimeChart;
	private ResponseSizeChart rSizeChart;
	private JaccardIndexChart jIndexChart;
	private HammingDistanceChart hDistChart;
	private ResponseHeaderSizeChart rHeadChart;
	
	public TabbedPlotter(final GraphingPanel gPanel) {

		super(SwingConstants.BOTTOM);

		this.gPanel = gPanel;

		statusChart = new StatusCodeChart();
		jIndexChart = new JaccardIndexChart();
		rSizeChart = new ResponseSizeChart();
		rTimeChart = new ResponseTimeChart();
		hDistChart = new HammingDistanceChart();
		rHeadChart = new ResponseHeaderSizeChart();

		this.add(" Status Code ", statusChart.getPlotCanvas());
		this.add(" Response Time ", rTimeChart.getPlotCanvas());
		this.add(" Response Size ", rSizeChart.getPlotCanvas());
		this.add(" Jaccard Index ", jIndexChart.getPlotCanvas());
		this.add(" Hamming Distance ", hDistChart.getPlotCanvas());
		this.add(" Response Header ", rHeadChart.getPlotCanvas());
		
		this.add(" Help ", new HelpChart() );

	}

	/**
	 * <p>
	 * Method for plotting on all the graphs available in the tabs of the
	 * Graphing Panel.
	 * </p>
	 * 
	 * @param directory
	 * 
	 * @author subere@uncon.org
	 * @version 1.3
	 * @since 1.2
	 */
	protected void plot(final File directory) {

		// Set the progress bar to show
		gPanel.setProgressBarStart();

		final File[] folderFiles = directory.listFiles();
		// In case its a file & similar, don't bother
		if (folderFiles == null) {
			return;
		}

		// Set minimum/max for each graph
		statusChart = new StatusCodeChart(folderFiles.length);
		rTimeChart = new ResponseTimeChart(folderFiles.length);
		rSizeChart = new ResponseSizeChart(folderFiles.length);
		jIndexChart = new JaccardIndexChart(folderFiles.length);
		hDistChart = new HammingDistanceChart(folderFiles.length);
		rHeadChart = new ResponseHeaderSizeChart(folderFiles.length);

		class Grapher extends SwingWorker<String, Object> {

			@Override
			public String doInBackground() {

				// Loop through the files
				for (int a = 0; a < folderFiles.length; a++) {

					statusChart.setValueAt(a, folderFiles[a]);
					rTimeChart.setValueAt(a, folderFiles[a]);
					rSizeChart.setValueAt(a, folderFiles[a]);
					jIndexChart.setValueAt(a, folderFiles[a]);
					hDistChart.setValueAt(a, folderFiles[a]);
					rHeadChart.setValueAt(a, folderFiles[a]);

				}

				return "done";
			}

			@Override
			protected void done() {

				statusChart.createFinalPlotCanvas();
				rTimeChart.createFinalPlotCanvas();
				rSizeChart.createFinalPlotCanvas();
				jIndexChart.createFinalPlotCanvas();
				hDistChart.createFinalPlotCanvas();
				rHeadChart.createFinalPlotCanvas();

				setComponentAt(0, statusChart.getPlotCanvas());
				setComponentAt(1, rTimeChart.getPlotCanvas());
				setComponentAt(2, rSizeChart.getPlotCanvas());
				setComponentAt(3, jIndexChart.getPlotCanvas());
				setComponentAt(4, hDistChart.getPlotCanvas());
				setComponentAt(5, rHeadChart.getPlotCanvas());

				// Stop the progress bar
				gPanel.setProgressBarStop();

			}
		}

		(new Grapher()).execute();

	}

}
