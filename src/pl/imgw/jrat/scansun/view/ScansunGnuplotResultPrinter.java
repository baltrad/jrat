/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.view;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.proc.ScansunDataHandler;
import pl.imgw.jrat.tools.out.FileResultPrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunGnuplotResultPrinter extends ScansunResultsPrinter {

	private static Log log = LogManager.getLogger();

	private static final String BASE_FILENAME = "scansun";
	private static final String SEPARATOR = "-";
	private static final String DATAFILE_EXT = "data";

	private static final ScansunGnuplot.GnuplotTerminal TERMINAL = ScansunGnuplot.GnuplotTerminal.PNG;

	public ScansunGnuplotResultPrinter(String[] args) {
		super(args);
	}

	public void generatePlot(ScansunPlot plot) throws IOException {

		if (!this.eventsContainer.hasResults()) {
			log.printMsg("SCANSUN: No data to generate plot", Log.TYPE_WARNING,
					Log.MODE_VERBOSE);
			return;
		}

		String plotBaseFilename = BASE_FILENAME + SEPARATOR
				+ plot.getPlotname();

		Set<ScansunSite> sites = new HashSet<ScansunSite>();
		if (params.allAvailableSites()) {
			sites.addAll(eventsContainer.getSites());
		} else {
			sites.add(params.getSite());
		}

		for (ScansunSite site : sites) {

			LocalDate firstDay = sitedays.get(site).first();
			LocalDate lastDay = sitedays.get(site).last();

			String plotFilename = plotBaseFilename + SEPARATOR
					+ site.getSiteName() + SEPARATOR + firstDay + SEPARATOR
					+ lastDay + "." + TERMINAL.extension();
			File plotfile = new File(ScansunDataHandler.getScansunPath(),
					plotFilename);

			String dataFilename = plotFilename + "." + DATAFILE_EXT;
			File datafile = new File(AplicationConstans.TMP, dataFilename);

			printer = new FileResultPrinter(datafile);
			plot.printResults(this, site);

			plot.generatePlot(plotfile, TERMINAL, site, datafile);
		}

	}

}