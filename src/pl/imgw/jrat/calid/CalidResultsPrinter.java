/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.calid.CalidParsedParameters.DISTANCE;
import static pl.imgw.jrat.calid.CalidParsedParameters.ELEVATION;
import static pl.imgw.jrat.calid.CalidParsedParameters.REFLECTIVITY;
import static pl.imgw.jrat.calid.CalidParsedParameters.SOURCE;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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
public class CalidResultsPrinter {

    protected CalidParsedParameters params;
    private Scanner scanner;
    
    protected Set<String> headers;
    
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm");
    protected SimpleDateFormat fsdf = new SimpleDateFormat("yyyyMMdd");
    
    /**
     * 
     */
    public CalidResultsPrinter(CalidParsedParameters params) {
        this.params = params;
    }
    
    /**
     * 
     * @return return true if printed results are not empty
     */
    public boolean printResults() {
        ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
        headers = new HashSet<String>();

        CalidContainer cc = new CalidContainer(params);
        Set<File> files = CalidResultFileGetter.getResultFiles(params);
//        List<Date> dates;
        boolean noResults = true;
        if (files.isEmpty()) {
            printer.println("# No results matching selected parameters");
            return false;
        }

        if (areParametersSet()) {

            boolean printHeader = true;
            
            for (File f : files) {
                
                printResultsHeader(f);
                StringBuilder header = new StringBuilder();
                
                header.append("#");
                
                //print horizontal line
                for (int i = 0; i < 70; i++) {
                    header.append("=");
                }
                
                header.append("\n#\tdate \t\tfreq \tmean \tRMS"
                        + " \tmedian \tr1under \tr2under\n");

//                Set<Date> dates = new TreeSet<Date>();
//                setDates(f, dates);
                
                try {
                    Scanner scan = new Scanner(f);
                    while (scan.hasNext()) {
                        String line = scan.nextLine();
                        if (line.startsWith("#")) {
                            continue;
                        }
                        if (!CalidResultIOHandler.parseLine(line, cc,
                                params.getStartDate(), params.getEndDate()))
                            continue;

                        int freq = params.getFrequency();
                        Double mean = cc.getMean(freq);
                        Double rms = cc.getRMS(freq);
                        Double median = cc.getMedian(freq);
                        
                        StringBuilder msg = new StringBuilder(" \t" + cc.getFreq() + " \t" + mean
                                + " \t" + rms + " \t" + median);
                        
                        msg.append("\t" + cc.getR1understate() + "\t"
                                + cc.getR2understate());
                        
                        if(mean != null || rms != null || median != null) {
                            if(printHeader) {
                                printer.print(header.toString());
                                printHeader = false;
                            }
                            noResults = false;
                            printer.println(sdf.format(cc.getDate()) + msg.toString());
                        } 
                        
                    }
                } catch (FileNotFoundException e) {
                    LogHandler.getLogs().displayMsg(
                            "CALID: Results file not found: " + f,
                            Logging.WARNING);
                }

//                if (!printHeader) {
//                    System.out.print("#");
//                    for (int i = 0; i < 54; i++) {
//                        System.out.print("=");
//                    }
//                    System.out.print("\n");
//                }
                
            }
            if(noResults) {
                System.out.println("# No results matching selected parameters");
            }
            printer.print("\n");
        } else {
            printList();
            
            System.out.println("To print results for any particular pair"
                    + " provide its src, ele, dis, ref and date");
            
        }
        return !noResults;
    }
   
    public void printList() {
        
        headers = new HashSet<String>();
        
        Set<File> files = CalidResultFileGetter.getResultFiles(params);
        
        if (files.isEmpty()) {
            System.out.println("No results matching selected parameters");
            return;
        }

        if (!params.getSource1().isEmpty() && !params.getSource1().isEmpty()
                && !params.isDistanceDefault() && !params.isElevationDefault()
                && !params.isReflectivityDefault()) {
            /* all parameters are provided and printing list of dates */

            System.out.println("Printing list of available results...\n");
            int n = 0;
            boolean printHeader = true;
            for (File f : files) {

                if(printHeader) {
                    printResultsHeader(f);
                    printHeader = false;
                }
                int a = printResultsDateNumber(f);
                if (a < 1) {
                    continue;
                }
//                String msg = f.getName().split("\\.")[0] + " number of dates: " + a;
//                System.out.println("\t" + msg);
                n += a;
            }
            if (n > 1)
                System.out.println("\t" + n + " results all together in database.");

        } else {
            /* printing list of available reuslts pairs */

            System.out.println("Printing results list...\n");

            for (File f : files) {
                printResultsHeader(f);
            }

            System.out
                    .println("\nNumber of pairs matching selected parameters: "
                            + headers.size());
            System.out
                    .println("To print list of available dates for any particular pair"
                            + " provide its src, ele, dis and ref");

        }
    }
    
    protected void setDates(File f, Set<Date> set) {

//        Set<Date> list = new TreeSet<Date>();
        Date date;
        try {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if (!scanner.hasNextLine()) {
                        System.out.println("No results found.");
                        return;
                    }
                    continue;
                }
                try {
                    date = CalidResultIOHandler.CALID_DATE_TIME_FORMAT.parse(line
                            .split(" ")[0]);
                } catch (ParseException e) {
                    continue;
                }
                set.add(date);

            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        }

    }
    
    protected int printResultsDateNumber(File f) {
        ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
        // System.out.println(f);
        String date = "";
        int i = 0;
        try {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if (!scanner.hasNextLine()) {
                        System.out.println("No results found.");
                        return 0;
                    }
                    continue;
                }
//                System.out.println("\t\t" + line.split(" ")[0]);
                
                if(date.isEmpty()) {
                    date = line.split(" ")[0].split("/")[0];
                }
                
                i++;
//                if (LogHandler.getLogs().getVerbose() < Logging.NORMAL && i == 5) {
//                    System.out.println("\t\t\t.\n\t\t\t.\n\t\t\t.");
//                    System.out
//                            .println("\t(use -v parameter to print all)");
//                    return -1;
//                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        }
        
        printer.println("\t\t" + date + " number of results: " + i);
        
        return i;
    }
    
    /**
     * 
     * @param f
     */
    protected void printResultsHeader(File f) {
        ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
        // System.out.println(f);
        try {
            scanner = new Scanner(f);
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(headers.contains(line))
                    return;
                headers.add(line);
                if (line.startsWith("#")) {
                    printer.println(line);
                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        }
    }

    /**
     * 
     * @return files are sorted
     */
    protected Set<File> getResueltsFiles() {
        Set<File> results = new TreeSet<File>();

        File folder = new File(CalidResultIOHandler.getCalidPath());
        
        if(!folder.isDirectory()) {
            return results;
        }
        
        for (File pairname : folder.listFiles()) {
            if (pairname.isDirectory()) {
                // pair name
                String name = pairname.getName();
                
                /*
                 * skip pairs which does not contains source name if they are
                 * set
                 */
                if (!name.isEmpty() && !params.getSource1().isEmpty()) {
                    if (!params.getSource2().isEmpty()) {
                        if (!name.contains(params.getSource1())
                                || !name.contains(params.getSource2()))
                            continue;
                    } else if (!name.contains(params.getSource1())) {
                        continue;
                    }
                }
                
                for (File parameters : pairname.listFiles()) {
                    if (parameters.isDirectory()) {
                        // parameters
                        for (File file : parameters.listFiles()) {
                            if (file.isFile()
                                    && file.getName().endsWith("results")) {

                                if (areParametersUnset() || keep(file))
                                    results.add(file);
                            }
                        }
                    }
                }
            }
        }

        return results;
    }
    
    protected boolean areParametersUnset() {
        if (params.getSource1().isEmpty() && params.getSource2().isEmpty()
                && params.isDistanceDefault() & params.isElevationDefault()
                && params.isReflectivityDefault() & params.isStartDateDefault())
            return true;
               
        return false;
    }
    
    protected boolean areParametersSet() {
        if (params.getSource1().isEmpty() || params.getSource2().isEmpty()
                || params.isDistanceDefault() || params.isElevationDefault()
                || params.isReflectivityDefault() || params.getStartDate() == null)
            return false;
                
        return true;
    }
    
    private boolean keep(File file) {
        
        String src = "";
        String ele = "";
        String dis = "";
        String ref = "";

        try {
            scanner = new Scanner(file);
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    String[] args = line.split(" ");
                    for (String s : args) {
                        if (s.startsWith(SOURCE))
                            src = s.substring(SOURCE.length());
                        else if (s.startsWith(ELEVATION))
                            ele = s.substring(ELEVATION.length());
                        else if (s.startsWith(DISTANCE))
                            dis = s.substring(DISTANCE.length());
                        else if (s.startsWith(REFLECTIVITY))
                            ref = s.substring(REFLECTIVITY.length());
                    }
                } else
                    return false;
            }

            /*
             * filtering out if selected different sources
             */
            if (!src.isEmpty() && !params.getSource1().isEmpty()) {
                if (!params.getSource2().isEmpty()) {
                    if (!src.contains(params.getSource1())
                            || !src.contains(params.getSource2()))
                        return false;
                } else if (!src.contains(params.getSource1())) {
                    return false;
                }
            }

            /*
             * filtering out if selected different elevation
             */
            if (!params.isElevationDefault()) {
                if (Double.parseDouble(ele) != params.getElevation())
                    return false;
            }
            /*
             * filtering out if selected different distance
             */
            if (!params.isDistanceDefault()) {
                if (Integer.parseInt(dis) != params.getDistance())
                    return false;
            }
            /*
             * filtering out if selected different reflectivity
             */
            if (!params.isReflectivityDefault()) {
                if (Double.parseDouble(ref) != params.getReflectivity())
                    return false;
            }
            
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
            return false;
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
            return false;
        }
        
        return true;

    }
    
    protected double round(double value, int decimal) {
        double pow = Math.pow(10, decimal);
        
        value *= pow;
        
        value = Math.round(value);
        
        return value / pow;
    }
    
}
