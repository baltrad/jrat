/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN;
import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN_OPT;
import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN_RESULT;
import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN_RESULT_GNUPLOT;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.scansun.ScansunGnuplotResultsPrinter;
import pl.imgw.jrat.scansun.ScansunOptionsHandler;
import pl.imgw.jrat.scansun.ScansunProcessor;
import pl.imgw.jrat.scansun.ScansunResultParsedParameters;
import pl.imgw.jrat.scansun.ScansunResultsPrinter;
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
public class ScansunProcessController {

    private static Log log = LogManager.getLogger();

    public static boolean processScansunResult(CommandLine cmd) {
        ScansunResultParsedParameters scansunResult = new ScansunResultParsedParameters();

        if (scansunResult.initialize(cmd.getOptionValues(SCANSUN_RESULT))) {

            if (cmd.hasOption(SCANSUN_OPT)) {
                ScansunOptionsHandler.getOptions().setOptionFile(
                        cmd.getOptionValue(SCANSUN_OPT));

                if (!ScansunOptionsHandler.getOptions().loadRadarParameters()) {
                    log.printMsg("SCANSUN: loading radar parameters error",
                            Log.TYPE_ERROR, Log.MODE_VERBOSE);
                }

            } else if (!cmd.hasOption(SCANSUN_OPT)) {
                if (!ScansunOptionsHandler.getOptions().loadRadarParameters()) {
                    log.printMsg(
                            "SCANSUN: no parameters file in input - calculations without power calibration",
                            Log.TYPE_WARNING, Log.MODE_VERBOSE);
                }
            }

            if (cmd.hasOption(SCANSUN_RESULT_GNUPLOT)) {
                try {
                    new ScansunGnuplotResultsPrinter(scansunResult)
                            .generatePlots();
                } catch (IllegalArgumentException e) {
                    log.printMsg(e.getMessage(), Log.TYPE_WARNING,
                            Log.MODE_VERBOSE);
                    log.printMsg("SCANSUN: Plotting error", Log.TYPE_ERROR,
                            Log.MODE_VERBOSE);
                }
            } else {
                try {
                    new ScansunResultsPrinter(scansunResult).printResults();
                } catch (IllegalArgumentException e) {
                    log.printMsg(e.getMessage(), Log.TYPE_WARNING,
                            Log.MODE_VERBOSE);
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
                log.printMsg("SCANSUN: loading radar parameters error",
                        Log.TYPE_ERROR, Log.MODE_VERBOSE);
            }

        } else if (!cmd.hasOption(SCANSUN_OPT)) {
            if (!ScansunOptionsHandler.getOptions().loadRadarParameters()) {
                log.printMsg(
                        "SCANSUN: no parameters file in input - calculations without power calibration",
                        Log.TYPE_WARNING, Log.MODE_VERBOSE);
            }
        }

        proc = new ScansunProcessor(cmd.getOptionValues(SCANSUN));
        
            String par = "";
            if (cmd.getOptionValue(SCANSUN) == null) {
                par = "no parameters";
            } else
                for (String s : cmd.getOptionValues(SCANSUN)) {
                    par += s + " ";
                }
            log.printMsg("Starting SCANSUN with: " + par, Log.TYPE_NORMAL,
                    Log.MODE_VERBOSE);
        

        return proc;
    }

}
