/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.*;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.scansun.ScansunDRAOSolarFlux;
import pl.imgw.jrat.scansun.ScansunProcessor;
import pl.imgw.jrat.scansun.ScansunOptionsHandler;
import pl.imgw.jrat.scansun.ScansunResultsParsedParameters;

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
	ScansunResultsParsedParameters scansunResults = new ScansunResultsParsedParameters();

	if (cmd.hasOption(SCANSUN_OPT)) {
	    if (!ScansunOptionsHandler.withOptFileHandling(cmd)) {
		return false;
	    }
	}

	if (!cmd.hasOption(SCANSUN_OPT)) {
	    if (!ScansunOptionsHandler.withoutOptFileHandling()) {
		return false;
	    }
	}

	if (cmd.hasOption(SCANSUN_DRAO)) {
	    if (!ScansunDRAOSolarFlux.withDRAOFileHandling(cmd)) {
		return false;
	    }
	}

	if (!cmd.hasOption(SCANSUN_DRAO)) {
	    if (!ScansunDRAOSolarFlux.withoutDRAOFileHandling()) {
		return false;
	    }
	}

	if (scansunResults.initialize(cmd.getOptionValues(SCANSUN_RESULT))) {

	    if (cmd.hasOption(SCANSUN_RESULT_GNUPLOT)) {
		try {
		    new ScansunGnuplotResultsPrinter(scansunResults).generatePlots();
		} catch (IllegalArgumentException e) {
		    LogHandler.getLogs().displayMsg(e.getMessage(), WARNING);
		    LogHandler.getLogs().displayMsg("SCANSUN: Plotting error", ERROR);
		    LogHandler.getLogs().saveErrorLogs(CalidProcessController.class, e);
		}
	    } else {
		try {
		    new ScansunResultsPrinter(scansunResults).printResults();
		} catch (IllegalArgumentException e) {
		    LogHandler.getLogs().displayMsg(e.getMessage(), WARNING);
		}
	    }

	    return true;
	} else {
	    ScansunResultsParsedParameters.printHelp();
	    return false;
	}
    }

    public static FilesProcessor setScansunProcessor(CommandLine cmd) {
	FilesProcessor proc;

	if (cmd.hasOption(SCANSUN_OPT)) {
	    if (!ScansunOptionsHandler.withOptFileHandling(cmd)) {
		proc = null;
		return proc;
	    }
	}

	if (!cmd.hasOption(SCANSUN_OPT)) {
	    if (!ScansunOptionsHandler.withoutOptFileHandling()) {
		proc = null;
		return proc;
	    }
	}

	proc = new ScansunProcessor(cmd.getOptionValues(SCANSUN));
	if (proc.isValid()) {
	    String par = "";
	    for (String s : cmd.getOptionValues(SCANSUN)) {
		par += s + " ";
	    }
	    LogHandler.getLogs().displayMsg("Starting SCANSUN with: " + par, NORMAL);
	}

	return proc;
    }

}