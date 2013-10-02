/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.view;

import java.io.File;

import pl.imgw.jrat.scansun.data.ScansunSite;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public abstract class ScansunPlot {

	protected static String PLOT_NAME;

	public String getPlotname() {
		return PLOT_NAME;
	}

	public abstract void generatePlot(File plotfile,
			ScansunGnuplot.GnuplotTerminal terminal, ScansunSite site,
			File datafile);

	public abstract void printResults(ScansunGnuplotResultPrinter printer,
			ScansunSite site);
}
