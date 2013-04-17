/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.*;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.scansun.ScansunProcessor;
import pl.imgw.jrat.scansun.ScansunOptionsHandler;
import pl.imgw.jrat.scansun.ScansunResultParsedParameters;

import pl.imgw.jrat.scansun.ScansunResultsPrinter;
import pl.imgw.jrat.scansun.ScansunGnuplotResultsPrinter;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunProcessController {

	public static boolean processScansunResult(CommandLine cmd) {
		ScansunResultParsedParameters scansunResult = new ScansunResultParsedParameters();

		if (scansunResult.initialize(cmd.getOptionValues(SCANSUN_RESULT))) {

			if (cmd.hasOption(SCANSUN_OPT)) {
				ScansunOptionsHandler.getOptions().setOptionFile(
						cmd.getOptionValue(SCANSUN_OPT));

				if (!ScansunOptionsHandler.getOptions().loadRadarParameters()) {
					LogHandler.getLogs().displayMsg(
							"SCANSUN: loading radar parameters error", ERROR);
				}

			} else if (!cmd.hasOption(SCANSUN_OPT)) {
				if (!ScansunOptionsHandler.getOptions().loadRadarParameters()) {
					LogHandler
							.getLogs()
							.displayMsg(
									"SCANSUN: no parameters file in input - calculations without power calibration",
									WARNING);
				}
			}

			if (cmd.hasOption(SCANSUN_RESULT_GNUPLOT)) {
				try {
					new ScansunGnuplotResultsPrinter(scansunResult)
							.generatePlots();
				} catch (IllegalArgumentException e) {
					LogHandler.getLogs().displayMsg(e.getMessage(), WARNING);
					LogHandler.getLogs().displayMsg("SCANSUN: Plotting error",
							ERROR);
					LogHandler.getLogs().saveErrorLogs(
							CalidProcessController.class, e);
				}
			} else {
				try {
					new ScansunResultsPrinter(scansunResult).printResults();
				} catch (IllegalArgumentException e) {
					LogHandler.getLogs().displayMsg(e.getMessage(), WARNING);
				}
			}

			return true;
		} else {
			ScansunResultParsedParameters.printHelp();
			return false;
		}
	}

	public static FilesProcessor setScansunProcessor(CommandLine cmd) {
		FilesProcessor proc;

		if (cmd.hasOption(SCANSUN_OPT)) {
			ScansunOptionsHandler.getOptions().setOptionFile(
					cmd.getOptionValue(SCANSUN_OPT));

			if (!ScansunOptionsHandler.getOptions().loadRadarParameters()) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: loading radar parameters error", ERROR);
			}

		} else if (!cmd.hasOption(SCANSUN_OPT)) {
			if (!ScansunOptionsHandler.getOptions().loadRadarParameters()) {
				LogHandler
						.getLogs()
						.displayMsg(
								"SCANSUN: no parameters file in input - calculations without power calibration",
								WARNING);
			}
		}

		proc = new ScansunProcessor(cmd.getOptionValues(SCANSUN));
		if (proc.isValid()) {
			String par = "";
			if (cmd.getOptionValue(SCANSUN) == null) {
				par = "no parameters";
			} else
				for (String s : cmd.getOptionValues(SCANSUN)) {
					par += s + " ";
				}
			LogHandler.getLogs().displayMsg("Starting SCANSUN with: " + par,
					NORMAL);
		}

		return proc;
	}

}
