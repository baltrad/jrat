/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.CALID;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_LIST;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_RESULT;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_RESULT_DETAIL;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_RESULT_GNUPLOT;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.calid.CalidDetailedResultsPrinter;
import pl.imgw.jrat.calid.CalidGnuplotResultPrinter;
import pl.imgw.jrat.calid.CalidParsedParameters;
import pl.imgw.jrat.calid.CalidProcessor;
import pl.imgw.jrat.calid.CalidResultsPrinter;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidProcessController {

    public static boolean processCalidResult(CommandLine cmd) {
        CalidParsedParameters calid = new CalidParsedParameters();
        if (calid.initialize(cmd.getOptionValues(CALID_RESULT))) {
            if (cmd.hasOption(CALID_RESULT_DETAIL)) {
                try {
                    new CalidDetailedResultsPrinter(calid,
                            cmd.getOptionValues(CALID_RESULT_DETAIL))
                            .printResults();
                } catch (IllegalArgumentException e) {
                    LogHandler.getLogs().displayMsg(e.getMessage(), WARNING);
                }
            } else if (cmd.hasOption(CALID_RESULT_GNUPLOT)) {
                String output = cmd.getOptionValue(CALID_RESULT_GNUPLOT);
                try {
                    new CalidGnuplotResultPrinter(calid, output)
                            .generateMeanDifferencePlots();
                } catch (IllegalArgumentException e) {
                    LogHandler.getLogs().displayMsg(e.getMessage(), WARNING);
                } catch (IOException e) {
                    LogHandler.getLogs().displayMsg("Plotting error", ERROR);
                    LogHandler.getLogs().saveErrorLogs(
                            CalidProcessController.class, e);
                }
            } else {
                new CalidResultsPrinter(calid).printResults();
            }
            return true;
        } else {
            CalidParsedParameters.printHelp();
            return false;
        }
    }
    
    public static boolean processCalidList(CommandLine cmd) {
        
        CalidParsedParameters calid = new CalidParsedParameters();
        if (calid.initialize(cmd.getOptionValues(CALID_LIST))) {
            new CalidResultsPrinter(calid).printList();
            return true;
        } else {
            CalidParsedParameters.printHelp();
            return false;
        }
    }
    
    public static void setCalidProcessor(CommandLine cmd, FilesProcessor proc) {
        proc = new CalidProcessor(cmd.getOptionValues(CALID));
        if (proc.isValid()) {
            String par = "";
            if (cmd.getOptionValue(CALID) == null)
                par = "default settings";
            else
                for (String s : cmd.getOptionValues(CALID)) {
                    par += s + " ";
                }
            LogHandler.getLogs().displayMsg("Starting CALID with " + par,
                    NORMAL);
        }
    }
    
}
