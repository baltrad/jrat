/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import static pl.imgw.jrat.calid.data.CalidParametersParser.PERIOD;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_LIST;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_PLOT;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_RESULT;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.view.CalidGnuplotResultPrinter;
import pl.imgw.jrat.calid.view.CalidPeriodsResultsPrinter;
import pl.imgw.jrat.calid.view.CalidResultsPrinter;
import pl.imgw.jrat.calid.view.CalidSingleResultPrinter;
import pl.imgw.jrat.process.FilesProcessor;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidController {

    private static Log log = LogManager.getLogger();

    public static void processPlot(CommandLine cmd, File output) {
        processPlot(cmd.getOptionValues(CALID_PLOT), output);
    }
    
    protected static void processPlot(String[] args, File output) {
        
        try {
            new CalidGnuplotResultPrinter(args, output)
                    .generateMeanDifferencePlots();
        } catch (IllegalArgumentException e) {
            log.printMsg(e.getMessage(), Log.TYPE_WARNING, Log.MODE_VERBOSE);
        } catch (IOException e) {
            log.printMsg("Plotting error", Log.TYPE_ERROR, Log.MODE_VERBOSE);
        }
    }
    
    public static void processResult(CommandLine cmd) {
        if(cmd.getOptionValues(CALID_RESULT) == null) {
            return;
        }
        processResult(cmd.getOptionValues(CALID_RESULT));
    }
    
    protected static void processResult(String[] args) {

        Integer period = getPeriod(args);
        if (period == null)
            new CalidSingleResultPrinter(args).printResults();
        else
            new CalidPeriodsResultsPrinter(args, period).printResults();

    }
    
    public static void processList(CommandLine cmd) {
        processList(cmd.getOptionValues(CALID_LIST));
    }
    
    protected static void processList(String[] args) {
        new CalidResultsPrinter(args).printList();
    }
    
    
    private static Integer getPeriod(String[] args) {
        Integer period = null;
        for (String s : args) {
            if (s.startsWith(PERIOD)) {

                try {
                    
                    period = Integer.parseInt(s.substring(
                            PERIOD.length(), s.length()));
                    if (period > 0)
                        return period;
                    else
                        throw new CalidException("period must be bigger then 0");
                } catch (NumberFormatException e) {
                    throw new CalidException(s + " is not a valid period value");
                }
            }
        }
        return null;
    }
    
    /*
    private static boolean processCalidResult(CommandLine cmd) {
        CalidPairAndParameters calid = null;

        try {
            calid = CalidParametersParser.getParser().parsePairAndParameters(
                    cmd.getOptionValues(CALID_RESULT));
        } catch (CalidException e1) {
            CalidParametersParser.printHelp();
            return false;
        }

        if (hasOnlySourceName(calid.getParameters(), calid.getPair())) {
            try {
                new CalidGnuplotResultPrinter(
                        cmd.getOptionValues(CALID_RESULT), "")
                        .generateMeanDifferencePlots();
            } catch (IOException e) {
                log.printMsg("Plotting error", Log.TYPE_ERROR, Log.MODE_VERBOSE);
            } catch (IllegalArgumentException e) {
                log.printMsg(e.getMessage(), Log.TYPE_WARNING, Log.MODE_VERBOSE);
            }
        } else if (cmd.hasOption(CALID_RESULT_DETAIL)) {
            try {
                new CalidPeriodsResultsPrinter(
                        cmd.getOptionValues(CALID_RESULT_DETAIL), 1)
                        .printResults();
            } catch (IllegalArgumentException e) {
                log.printMsg(e.getMessage(), Log.TYPE_WARNING, Log.MODE_VERBOSE);
            }
        } else if (cmd.hasOption(CALID_RESULT_PLOT)) {
            String output = cmd.getOptionValue(CALID_RESULT_PLOT);
            try {
                new CalidGnuplotResultPrinter(
                        cmd.getOptionValues(CALID_RESULT), output)
                        .generateMeanDifferencePlots();
            } catch (IllegalArgumentException e) {
                log.printMsg(e.getMessage(), Log.TYPE_WARNING, Log.MODE_VERBOSE);
            } catch (IOException e) {
                log.printMsg("Plotting error", Log.TYPE_ERROR, Log.MODE_VERBOSE);
            }
        } else {
//            new CalidResultsPrinter(cmd.getOptionValues(CALID_RESULT))
//                    .printResults();
        }
        return true;

    }
*/
    /*
    private static boolean hasOnlySourceName(CalidParameters params,
            RadarsPair pair) {
        if (pair.getSource1().isEmpty() || pair.getSource2().isEmpty())
            return false;

        return (params.isDistanceDefault() && params.isElevationDefault()
                && params.isEndDateDefault() && params.isFrequencyDefault()
                && params.isReflectivityDefault() && params
                    .isStartDateDefault());
    }

    private static boolean processCalidList(CommandLine cmd) {

        CalidPairAndParameters calid = null;

        try {
            CalidParametersParser.getParser().parsePairAndParameters(
                    cmd.getOptionValues(CALID_LIST));
        } catch (CalidException e) {
            CalidParametersParser.printHelp();
            return false;
        }

        new CalidResultsPrinter(calid).printList();
        return true;

    }
     */

    public static FilesProcessor setCalidProcessor(CommandLine cmd) {
        FilesProcessor proc;
        // if (cmd.hasOption(CALID_OPT)) {
        // CalidOptionsHandler.getOptions().setOptionFile(
        // cmd.getOptionValue(CALID_OPT));
        // }
        try {
            proc = new CalidProcessor(cmd.getOptionValues(CALID));
        } catch (CalidException e) {
            log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
            return null;
        }

        String par = "";
        if (cmd.getOptionValue(CALID) == null)
            par = "default settings";
        else
            for (String s : cmd.getOptionValues(CALID)) {
                par += s + " ";
            }
        log.printMsg("Starting CALID with " + par, Log.TYPE_NORMAL,
                Log.MODE_VERBOSE);

        return proc;
    }
}
