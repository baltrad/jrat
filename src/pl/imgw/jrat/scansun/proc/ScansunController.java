/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN;
import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN_PLOT;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.process.FilesProcessor;
import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.view.ScansunGnuplotResultPrinter;
import pl.imgw.jrat.scansun.view.ScansunHistogram;
import pl.imgw.jrat.scansun.view.ScansunPlot;
import pl.imgw.jrat.scansun.view.ScansunResultsPrinter;
import pl.imgw.jrat.scansun.view.ScansunScatterplot;
import pl.imgw.jrat.scansun.view.ScansunSolarFluxPlot;
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
public class ScansunController {

	private static Log log = LogManager.getLogger();

	public static void processResult(CommandLine cmd) {
		String[] args = cmd.getOptionValues(SCANSUN_PLOT);

		processResult(args);

		log.printMsg("All results generated successfully.", Log.TYPE_NORMAL,
				Log.MODE_VERBOSE);
	}

	protected static void processResult(String[] args) {
		new ScansunResultsPrinter(args).generateResults();
	}

	public static void processPlots(CommandLine cmd) {
		String[] args = cmd.getOptionValues(SCANSUN_PLOT);

		processPlots(args, new ScansunScatterplot());
		processPlots(args, new ScansunHistogram());
		processPlots(args, new ScansunSolarFluxPlot());

		log.printMsg("All plots created successfully.", Log.TYPE_NORMAL,
				Log.MODE_VERBOSE);
	}

	private static void processPlots(String[] args, ScansunPlot plot) {
		try {
			new ScansunGnuplotResultPrinter(args).generatePlot(plot);
		} catch (IllegalArgumentException e) {
			log.printMsg(e.getMessage(), Log.TYPE_WARNING, Log.MODE_VERBOSE);
		} catch (IOException e) {
			log.printMsg("Plotting error", Log.TYPE_ERROR, Log.MODE_VERBOSE);
		}
	}

	public static FilesProcessor setScansunProcessor(CommandLine cmd) {
		FilesProcessor proc;

		try {
			proc = new ScansunProcessor(cmd.getOptionValues(SCANSUN));
		} catch (ScansunException e) {
			log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}

		String par = "";
		if (cmd.getOptionValue(SCANSUN) == null)
			par = "default settings";
		else
			for (String s : cmd.getOptionValues(SCANSUN)) {
				par += s + " ";
			}
		log.printMsg("Starting SCANSUN with " + par, Log.TYPE_NORMAL,
				Log.MODE_VERBOSE);

		return proc;
	}

}
