/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidPairAndParameters;
import pl.imgw.jrat.calid.data.CalidResultLoader;
import pl.imgw.jrat.calid.data.CalidSingleResultContainer;
import pl.imgw.jrat.calid.data.CalidStatistics;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import pl.imgw.util.Log;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidSingleResultPrinter extends CalidResultsPrinter {

    public CalidSingleResultPrinter(String[] args) throws CalidException{
        super(args);
    }
    
    /**
     * 
     * @return return true if printed results are not empty
     */
    public boolean printResults() {
        
//        log.printMsg("Printing results", Log.TYPE_NORMAL, Log.MODE_VERBOSE);
        
        ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
        headers = new HashSet<String>();

        Set<File> files = CalidResultFileGetter.getResultFiles(pair, params);
        boolean noResults = true;
        if (files.isEmpty()) {
            log.printMsg("# No results matching selected parameters between "
                    + sdf.format(params.getStartRangeDate())
                    + " and "
                    + sdf.format(params.getEndRangeDate()), Log.TYPE_WARNING,
                    Log.MODE_NORMAL);
            return false;
        }

        log.printMsg("# Results between "
                + sdf.format(params.getStartRangeDate()) + " and "
                + sdf.format(params.getEndRangeDate()) + " for freq >="
                + params.getFrequency(), Log.TYPE_WARNING,
                Log.MODE_NORMAL);

        boolean printHeader = true;

        for (File f : files) {

            printResultsHeader(f);
            StringBuilder header = new StringBuilder();

            header.append("#\tdate \t\tfreq \tmean \tRMS"
                    + " \tmedian \tr1under \tr2under\n");

            Set<Date> dates = new TreeSet<Date>();
            setDates(f, dates);
            Scanner scan = null;
            try {
                scan = new Scanner(f);
                while (scan.hasNext()) {
                    String line = scan.nextLine();
                    if (line.startsWith("#")) {
                        continue;
                    }

                    CalidSingleResultContainer results;
                    results = CalidResultLoader.loadResultsFromLine(line,
                            params, pair);

                    if(results == null) {
                        continue;
                    }
                    
                    int freq = params.getFrequency();
                    Double mean = CalidStatistics.getMean(results, freq);
                    Double rms = CalidStatistics.getRMS(results, freq);
                    Double median = CalidStatistics.getMedian(results, freq);

                    StringBuilder msg = new StringBuilder(" \t"
                            + CalidStatistics.getFreq(results) + " \t" + mean
                            + " \t" + rms + " \t" + median);

                    msg.append("\t" + results.getR1understate() + "\t"
                            + results.getR2understate());

                    if (mean != null || rms != null || median != null) {
                        if (printHeader) {
                            printer.print(header.toString());
                            printHeader = false;
                        }
                        noResults = false;
                        printer.println(sdf.format(results.getResultDate())
                                + msg.toString());
                    }
                    results = null;
                }
                scan.close();
            } catch (FileNotFoundException e) {
                log.printMsg("CALID: Results file not found: " + f,
                        Log.TYPE_WARNING, Log.MODE_VERBOSE);
            }

//            if (!printHeader) {
//                printHorizontalLine();
//            }

        }
//        printHorizontalLine();
        
        if (noResults) {
            System.out.println("# No results matching selected parameters");
        }
//        printer.print("\n");

        return !noResults;
    }
    
    
}
