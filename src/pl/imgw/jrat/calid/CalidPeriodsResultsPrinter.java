/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

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
import java.util.TreeSet;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidPeriodsResultsPrinter extends CalidResultsPrinter {

    private static final String METHOD = "method=";
    public static final String MEDIAN = "median";
    public static final String MEAN = "mean";
    
    private static final String PERIOD = "period=";
    
    private String method = "";
    private int periodLength = 1;
    
    /**
     * @param params
     */
    public CalidPeriodsResultsPrinter(CalidParsedParameters params, String[] detParams) {
        super(params);
        
        for (String p : detParams) {
            if (p.startsWith(METHOD)) {
                method = p.substring(METHOD.length());
            } else if (p.startsWith(PERIOD)) {
                try {
                    periodLength = Integer.parseInt(p.substring(PERIOD.length()));
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    public CalidPeriodsResultsPrinter(CalidParsedParameters params) {
        super(params);
    }


    /**
     * Print results for all results file that matches Calid Parsed Parameters
     * @return 
     */
    public boolean printResults() {
        if (!areParametersSet()) {
            LogHandler.getLogs().displayMsg("Set all params for CALID",
                    Logging.WARNING);
            return false;
        }
        Set<File> files = CalidResultFileGetter.getResultFiles(params);
        return printResults(files);
    }


    /**
     * Print results for given result files
     * 
     * @param files
     * @return returns true if printed results are not empty
     * @throws IllegalArgumentException
     */
    public boolean printResults(Set<File> files) {

        boolean notEmpty = false;
        
        ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
        headers = new HashSet<String>();
        CalidContainer pairData = new CalidContainer(params);
        
        List<Double> meanRes = new ArrayList<Double>();
        List<Double> rmsRes = new ArrayList<Double>();
//        List<Double> undRes = new ArrayList<Double>();
//        Calendar cal0 = Calendar.getInstance();
        Calendar endOfThePeriod = Calendar.getInstance();
//        cal0.setTime(params.getDate1());
        endOfThePeriod.setTime(params.getStartDate());
        endOfThePeriod.add(Calendar.DATE, periodLength);
        
        for (File f : files) {
            
            printResultsHeader(f);
            
            try {
                
                Date fileDate = fsdf.parse(f.getName());
                String date1 = fsdf.format(params.getStartDate());
                
//                System.out.println("file date=" + fileDate);
//                System.out.println("params date=" + date1);
                
                if (fileDate.before(fsdf.parse(date1))
                        || fileDate.after(params.getEndDate())) {
                    continue;
                }
                
            } catch (ParseException e) {

            }
            
//            Set<Date> dates = new TreeSet<Date>();
//            setDates(f, dates);
            
            // System.out.println(params.getDate1());
//            System.out.println("cal0=" + cal0.getTime());
//            System.out.println("cal1=" + cal1.getTime());

            try {
                Scanner scan = new Scanner(f);
                while (scan.hasNext()) {
                    String line = scan.nextLine();
                    if (line.startsWith("#")) {
                        continue;
                    }
                    if (!CalidResultIOHandler.parseLine(line, pairData,
                            params.getStartDate(), params.getEndDate()))
                        continue;

                    /*
                     * if pair data date is after period end time, quite the loop
                     */
                    if (pairData.getDate().after(params.getEndDate())) {
                        break;
                    }

                    boolean periodChanged = false;

                    /*
                     * updating period window time
                     */
                    while (pairData.getDate().after(endOfThePeriod.getTime())) {
                        // cal0.add(Calendar.DATE, period);
                        endOfThePeriod.add(Calendar.DATE, periodLength);
                        periodChanged = true;
                    }

                    /*
                     * if period window time has been changed, print results
                     */
                    if (periodChanged && !meanRes.isEmpty()) {
                        // endOfThePeriod.add(Calendar.DATE, periodLength);
                        if (meanRes.size() == 0)
                            continue;

                        if (method.matches(MEDIAN)) {
                            printer.println(sdf.format(pairData.getDate())
                                    + "\t" + getMedianResult(meanRes) + "\t"
                                    + getMedianResult(rmsRes)
                            // + "\t"
                            // + getMedianResult(undRes)
                            );

                        } else if (method.matches(MEAN)) {
                            printer.println(sdf.format(pairData.getDate())
                                    + "\t" + getMeanResult(meanRes) + "\t"
                                    + getMeanResult(rmsRes)
                            // + "\t"
                            // + getMeanResult(undRes)
                            );
                        }
                        // undRes.clear();

                        meanRes.clear();
                        rmsRes.clear();
                        notEmpty = true;
                    }

                    int freq = params.getFrequency();
                    Double mean = pairData.getMean(freq);
                    Double rms = pairData.getRMS(freq);
                    // Double und = ((double)(cc.getR1understate() + cc
                    // .getR2understate()) / cc.getPairedPointsList().size());

                    if (mean != null)
                        meanRes.add(mean);
                    if (rms != null)
                        rmsRes.add(rms);
                    // if(und != null)
                    // undRes.add(und);

                }
            } catch (FileNotFoundException e) {
                LogHandler.getLogs().displayMsg(
                        "CALID: Results file not found: " + f,
                        Logging.WARNING);
            }
        }
        
        if(method.matches(MEDIAN)) {
            
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

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @param period the period to set
     */
    public void setPeriod(int period) {
        this.periodLength = period;
    }
    
    
    
}
