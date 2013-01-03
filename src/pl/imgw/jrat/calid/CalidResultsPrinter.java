/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import static pl.imgw.jrat.calid.CalidParsedParameters.*;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultsPrinter {

    private CalidParsedParameters params;
    private Scanner scanner;
    
    private Set<String> headers;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    /**
     * 
     */
    public CalidResultsPrinter(CalidParsedParameters params) {
        this.params = params;
    }
    
    public void printResults() {
        headers = new HashSet<String>();

        CalidContainer cc = new CalidContainer(params);
        Set<File> files = getResultsFiles();
        List<Date> dates;
        boolean noResults = true;
        if (files.isEmpty()) {
            System.out.println("# No results matching selected parameters");
            return;
        }

        if (!params.getSource1().isEmpty() && !params.getSource1().isEmpty()
                && !params.isDistanceDefault() && !params.isElevationDefault()
                && !params.isReflectivityDefault() && params.getDate1() != null) {

            boolean printHeader = true;
            
            for (File f : files) {
                
                printResultsHeader(f);
                String header = "";
                
                header += "#";
                
                for (int i = 0; i < 54; i++) {
                    header += "=";
                }
                
                header += "\n#\tdate \t\tfreq" + " \tmean" + " \tRMS" + " \tmedian\n";

                
                
                
                dates = getDateList(f);
                for(Date d : dates) {
                    
                    if(d.before(params.getDate1()) || d.after(params.getDate2()))
                        continue;
                    
                    CalidFileHandler.loadResults(f, cc, d);
                    int freq = params.getFrequency();
                    Double mean = cc.getMean(freq);
                    Double rms = cc.getRMS(freq);
                    Double median = cc.getMedian(freq);
                    
                    String msg = " \t" + cc.getFreq() + " \t" + mean
                            + " \t" + rms + " \t" + median; 
                    
                    if(mean != null || rms != null || median != null) {
                        if(printHeader) {
                            System.out.print(header);
                            printHeader = false;
                        }
                        noResults = false;
                        System.out.println(sdf.format(d) + msg);
                    } 
                    
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
            System.out.print("\n");
        } else {
            printList();
            
            System.out.println("To print results for any particular pair"
                    + " provide its src, ele, dis, ref and date");
            
        }
        
        

    }
   
    public void printList() {
        
        headers = new HashSet<String>();
        
        Set<File> files = getResultsFiles();
        
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
    
    private List<Date> getDateList(File f) {

        List<Date> list = new LinkedList<Date>();
        Date date;
        try {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if (!scanner.hasNextLine()) {
                        System.out.println("No results found.");
                        return list;
                    }
                    continue;
                }
                try {
                    date = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(line
                            .split(" ")[0]);
                } catch (ParseException e) {
                    continue;
                }
                list.add(date);

            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        }

        return list;
    }
    
    private int printResultsDateNumber(File f) {
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
        
        System.out.println("\t\t" + date + " number of results: " + i);
        
        return i;
    }
    
    private void printResultsHeader(File f) {
        // System.out.println(f);
        try {
            scanner = new Scanner(f);
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(headers.contains(line))
                    return;
                headers.add(line);
                if (line.startsWith("#")) {
                    System.out.println(line);
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

    private Set<File> getResultsFiles() {
        Set<File> results = new TreeSet<File>();

        File folder = new File(CalidFileHandler.getCalidPath());
        
        if(!folder.isDirectory()) {
            return results;
        }
        
        for (File pairname : folder.listFiles()) {
            if (pairname.isDirectory()) {
                // pair name
                for (File parameters : pairname.listFiles()) {
                    if (parameters.isDirectory()) {
                        // parameters
                        for (File file : parameters.listFiles()) {
                            if (file.isFile()
                                    && file.getName().endsWith("results")) {

                                if (keep(file))
                                    results.add(file);
                            }
                        }
                    }
                }
            }
        }

        return results;
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
    
    public static void printHelp() {
        // LogHandler.getLogs().displayMsg("CALID algorytm usage:\n",
        // Logging.SILENT);

        String src = "src=Source1[,Source2]";
        String date = "date=Start[,End]";
        String rest = "[ele=X] [dis=Y]";

        String msg = "CALID algorytm usage: jrat [options]\n";
        msg += "--calid-help\t\tprint this message\n";
        msg += "--calid-list [<args>]\tlist all available pairs\n\t\t\t"
                + "<args> " + src + " " + rest + "\n";
        msg += "--calid-result [<args>]\tdisplay results\n"
                + "\t\t\t"
                + "<args> "
                + date + " [" + src + "] "
                + rest + " [freq=Z]\n"
                + "\t\t\tdate: sets range of time, if only starting date is selected then\n" 
                + "\t\t\t\tonly this date is taken, valid format is yyyyMMdd/HHmm, but HHmm is optional\n" 
                + "\t\t\tsrc: source name\n" 
                + "\t\t\tele: elevation angle in degrees, from -10.0 to 90.0 \n"
                + "\t\t\tdis: minimal distance between paired points in meters, must be bigger then 0\n"
                + "\t\t\tfreq: minimal frequency percentage of points with precipitation\n"
                + "\t\t\t\tabove given threshold, must be bigger then 0\n" 
                + "\t\t\te.g: --calid-result src=Rzeszow"
                + " date=2011-08-21/09:30,2011-08-21/10:30 freq=10\n";
        

        System.out.println(msg);
    }
    
}
