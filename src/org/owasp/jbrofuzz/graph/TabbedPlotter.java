/**
 * JBroFuzz 1.5
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

public class TabbedPlotter extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GraphingPanel graphingPanel;

	private StatusCodeChart st_Chart;
	private ResponseTimeChart rt_Canvas;
	private ResponseSizeChart fs_Canvas;
	private JaccardIndexChart hv_Canvas;
	private HammingDistanceChart cr_Canvas;
	private ResponseHeaderSizeChart rh_Canvas;
	private HelpChart hl_Canvas;

	public TabbedPlotter(GraphingPanel graphingPanel) {

		super(SwingConstants.BOTTOM);

		this.graphingPanel = graphingPanel;

		st_Chart = new StatusCodeChart();
		hv_Canvas = new JaccardIndexChart();
		fs_Canvas = new ResponseSizeChart();
		rt_Canvas = new ResponseTimeChart();
		cr_Canvas = new HammingDistanceChart();
		rh_Canvas = new ResponseHeaderSizeChart();
		hl_Canvas = new HelpChart();

		this.add(" Status Code ", st_Chart.getPlotCanvas());
		this.add(" Response Time ", rt_Canvas.getPlotCanvas());
		this.add(" Response Size ", fs_Canvas.getPlotCanvas());
		this.add(" Jaccard Index ", hv_Canvas.getPlotCanvas());
		this.add(" Hamming Distance ", cr_Canvas.getPlotCanvas());
		this.add(" Response Header ", rh_Canvas.getPlotCanvas());
		this.add(" Help ", hl_Canvas);

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
	public void plot(final File directory) {

		// Set the progress bar to show
		graphingPanel.setProgressBarStart();

		final File[] folderFiles = directory.listFiles();
		// In case its a file & similar, don't bother
		if (folderFiles == null)
			return;

		// Set minimum/max for each graph
		st_Chart = new StatusCodeChart(folderFiles.length);
		rt_Canvas = new ResponseTimeChart(folderFiles.length);
		fs_Canvas = new ResponseSizeChart(folderFiles.length);
		hv_Canvas = new JaccardIndexChart(folderFiles.length);
		cr_Canvas = new HammingDistanceChart(folderFiles.length);
		rh_Canvas = new ResponseHeaderSizeChart(folderFiles.length);

		class Grapher extends SwingWorker<String, Object> {

			@Override
			public String doInBackground() {

				// Loop through the files
				for (int a = 0; a < folderFiles.length; a++) {

					st_Chart.setValueAt(a, folderFiles[a]);
					rt_Canvas.setValueAt(a, folderFiles[a]);
					fs_Canvas.setValueAt(a, folderFiles[a]);
					hv_Canvas.setValueAt(a, folderFiles[a]);
					cr_Canvas.setValueAt(a, folderFiles[a]);
					rh_Canvas.setValueAt(a, folderFiles[a]);

				}

				return "done";
			}

			@Override
			protected void done() {

				st_Chart.createFinalPlotCanvas();
				rt_Canvas.createFinalPlotCanvas();
				fs_Canvas.createFinalPlotCanvas();
				hv_Canvas.createFinalPlotCanvas();
				cr_Canvas.createFinalPlotCanvas();
				rh_Canvas.createFinalPlotCanvas();

				setComponentAt(0, st_Chart.getPlotCanvas());
				setComponentAt(1, rt_Canvas.getPlotCanvas());
				setComponentAt(2, fs_Canvas.getPlotCanvas());
				setComponentAt(3, hv_Canvas.getPlotCanvas());
				setComponentAt(4, cr_Canvas.getPlotCanvas());
				setComponentAt(5, rh_Canvas.getPlotCanvas());

				// Stop the progress bar
				graphingPanel.setProgressBarStop();

			}
		}

		(new Grapher()).execute();

	}

}
