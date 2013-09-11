/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import pl.imgw.jrat.calid.data.CalidResultLoader;
import pl.imgw.jrat.calid.data.CalidSingleResultContainer;
import pl.imgw.jrat.calid.data.CalidStatistics;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
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
public class CalidPeriodsResultsPrinter extends CalidResultsPrinter {

    public static final String MEDIAN = "median";
    public static final String MEAN = "mean";
    private Integer period = null;

    public CalidPeriodsResultsPrinter(String[] calidargs, int period) {
        super(calidargs);
        this.period = period;
    }

    /**
     * Print results for all results file that matches Calid Parsed Parameters
     * 
     * @return
     */
    public boolean printResults() {
//        if (!areParametersSet()) {
//            log.printMsg("Set all params for CALID", Log.TYPE_WARNING,
//                    Log.MODE_VERBOSE);
//            return false;
//        }
        Set<File> files = CalidResultFileGetter.getResultFiles(pair, params);
        return printResults(files);
    }

    /**
     * Print results for given result files
     * 
     * @param files
     * @return returns true if printed results are not empty
     * @throws IllegalArgumentException
     */
    protected boolean printResults(Set<File> files) {

//        log.printMsg("Printing results by " + period + "-day period",
//                Log.TYPE_NORMAL, Log.MODE_VERBOSE);
        
        boolean notEmpty = false;

        ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
        headers = new HashSet<String>();

        List<Double> meanRes = null;
        List<Double> rmsRes = null;
        Calendar endOfThePeriod = null;
        boolean printHeader = true;
        
        for (File f : files) {

            if(printResultsHeader(f)) {
                meanRes = new ArrayList<Double>();
                rmsRes = new ArrayList<Double>();
                endOfThePeriod = Calendar.getInstance();
                endOfThePeriod.setTime(params.getStartRangeDate());
                endOfThePeriod.add(Calendar.DATE, period);;
                printHeader = true;
            }
            
            StringBuilder header = new StringBuilder();

//          header.append("#");


            header.append("# results by " + period + "-day period\n");
            header.append("#\tdate \t\tmean\trms\n");
          
            try {

                Date fileDate = fsdf.parse(f.getName());
                String date1 = fsdf.format(params.getStartRangeDate());

                if (fileDate.before(fsdf.parse(date1))
                        || fileDate.after(params.getEndRangeDate())) {
                    continue;
                }

            } catch (ParseException e) {

            }

            try {
                Scanner scan = new Scanner(f);
                while (scan.hasNext()) {
                    String line = scan.nextLine();
                    if (line.startsWith("#")) {
                        continue;
                    }
                    
                    CalidSingleResultContainer results;
                    results = CalidResultLoader.loadResultsFromLine(line,
                            params, pair);

                    if(results == null)
                        continue;

                    /*
                     * if pair data date is after period end time, quit the
                     * loop
                     */
                    if (results.getResultDate()
                            .after(params.getEndRangeDate())) {
                        break;
                    }

                    boolean periodChanged = false;

                    /*
                     * updating period window time
                     */
                    while (results.getResultDate().after(
                            endOfThePeriod.getTime())) {
                        endOfThePeriod.add(Calendar.DATE, period);
                        periodChanged = true;
                    }

                    /*
                     * if period window time has been changed, print results
                     */
                    if (periodChanged && !meanRes.isEmpty()) {
                        if (meanRes.size() == 0)
                            continue;

                        /*
                        if (method.matches(MEDIAN)) {
                            printer.println(sdf.format(results.getResultDate())
                                    + "\t"
                                    + getMedianResult(meanRes)
                                    + "\t"
                                    + getMedianResult(rmsRes)
                            );
*/
//                        } else if (method.matches(MEAN)) {
                        if (printHeader) {
                            printer.print(header.toString());
                            printHeader = false;
                        }
                            printer.println(sdf.format(results.getResultDate())
                                    + "\t"
                                    + getMeanResult(meanRes)
                                    + "\t"
                                    + getMeanResult(rmsRes)
                            );
//                        }

                        meanRes.clear();
                        rmsRes.clear();
                        notEmpty = true;
                    }

                    int freq = params.getFrequency();
                    Double mean = CalidStatistics.getMean(results, freq);
                    Double rms = CalidStatistics.getRMS(results, freq);

                    if (mean != null)
                        meanRes.add(mean);
                    if (rms != null)
                        rmsRes.add(rms);

                }
                scan.close();
            } catch (FileNotFoundException e) {
                log.printMsg("CALID: Results file not found: " + f,
                        Log.TYPE_WARNING, Log.MODE_VERBOSE);
            }
        }

        return notEmpty;

    }

    private Double getMedianResult(List<Double> list) {

        if (list.size() == 0) {
            return 0.0;
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        Collections.sort(list);
        int middle = list.size() / 2;

        if (list.size() % 2 == 1) {
            return list.get(middle);
            // return array.get(middle);
        } else {
            return round((list.get(middle - 1) + list.get(middle)) / 2.0, 3);
        }
    }

    private Double getMeanResult(List<Double> list) {

        if (list.size() == 0) {
            return 0.0;
        }

        double mean = 0;
        for (double d : list) {
            mean += d;
        }
        return round(mean / list.size(), 2);
    }

    protected void setPeriod(int per) {
        this.period = per;
    }

}
