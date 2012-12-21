/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static pl.imgw.jrat.calid.CalidFileHandler.NULL;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultManager {

    public static final int MEAN = 0;
    public static final int MEDIAN = 1;
    public static final int RMS = 2;
    
    
    CalidParsedParameters params = null;

    public CalidResultManager(CalidParsedParameters params) {
        this.params = params;
    }

    private List<File> getResultsFiles() {

        List<File> results = new LinkedList<File>();

        File folder = new File(CalidFileHandler.getCalidPath());
        for (File pairname : folder.listFiles()) {
            if (pairname.isDirectory()) {
                // pair name
                for (File parameters : pairname.listFiles()) {
                    if (parameters.isDirectory()) {
                        // parameters
                        for (File file : parameters.listFiles()) {
                            if (file.isFile()
                                    && file.getName().endsWith("results")) {
                                results.add(file);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    private List<File> getResultFiles(Pair pair, boolean printPairs) {

        Set<String> pairSet = new HashSet<String>();

        List<File> results = getResultsFiles();
        if (printPairs && results.isEmpty()) {
            LogHandler.getLogs().displayMsg("No corresponing results found.",
                    Logging.WARNING);
        }

        List<File> matchingResults = new ArrayList<File>();
        Scanner scanner;
        for (File f : results) {
            // System.out.println(f);
            try {
                scanner = new Scanner(f);
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("# src=")) {
                        // System.out.println(line);
                        String readpair = line.substring(6);
                        readpair = readpair.split(" ")[0];
                        if (pair.hasBothSources()) {
                            if (pair.getSource1().matches(
                                    readpair.split(",")[0])
                                    && pair.getSource2().matches(
                                            readpair.split(",")[1])) {
                                matchingResults.add(f);
                                pairSet.add(line.substring(2));
                            } 
                        } else if (pair.hasOnlyOneSource()) {
                            if (pair.getSource1().matches(
                                    readpair.split(",")[0])
                                    || pair.getSource1().matches(
                                            readpair.split(",")[1])) {
                                matchingResults.add(f);
                                pairSet.add(line.substring(2));
                            }
                        } else {
                            matchingResults.add(f);
                            pairSet.add(line.substring(2));
                        }
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

        if (printPairs)
            for (String s : pairSet)
                System.out.println(s);

        return matchingResults;
    }

    private boolean printResultsDateList(File f) {
        // System.out.println(f);
        int i = 0;
        try {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if (!scanner.hasNextLine()) {
                        System.out.println("No results found.");
                        return true;
                    }
                    continue;
                }
                System.out.println("\t\t" + line.split(" ")[0]);
                if (LogHandler.getLogs().getVerbose() < Logging.NORMAL)
                    i++;
                if (i == 5) {
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        }
        return true;
    }

    private boolean printResultsDateList(File f, Date start, Date end) {
        // System.out.println(f);

        Date readDate = null;
        int i = 0;
        try {
            Scanner scanner = new Scanner(f);
            String header = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if (!scanner.hasNextLine()) {
                        System.out.println("No results found.");
                        return true;
                    }
                    header = line.substring(2);
                    continue;
                }
                readDate = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(line
                        .split(" ")[0]);

                if (!readDate.before(start) && !readDate.after(end)) {

                    if (!header.isEmpty()) {
                        System.out.println(header);
                        header = "";
                    }

                    System.out.println(line);
                    if (LogHandler.getLogs().getVerbose() < Logging.NORMAL)
                        i++;
                    if (i == 5) {
                        System.out
                                .println("And more... (use -v parameter to print all)");
                        return false;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        }
        
        if(i == 0)
            LogHandler.getLogs().displayMsg("No corresponing results found.",
                    Logging.WARNING);
        
        return true;
    }

    public void printPairsList() {
        Pair pair = new Pair(params.getSource1(), params.getSource2());
        List<File> files;
        if (!pair.hasBothSources()) {
            files = getResultFiles(pair, true);
            if (files.isEmpty()) {
                LogHandler.getLogs().displayMsg(
                        "No results for: " + pair.getBothSources(),
                        Logging.SILENT);
            }
        } else {
            boolean printed = false;
            files = getResultFiles(pair, false);
            if (files.isEmpty()) {
                LogHandler.getLogs().displayMsg(
                        "No results for: " + pair.getBothSources(),
                        Logging.SILENT);
            } else {
                for (File f : files) {
                    printed = printResultsDateList(f);
                }
                if (!printed) {
                    System.out
                            .println("And more... (use -v parameter to print all)");
                }
            }
        }

        // System.out.println(files.size());

    }

    /**
     * 
     */
    public void printResults() {
        // System.out.println("Sprawdzam parsowanie daty");
        // System.out.println("Date1: " + calid.getDate1());
        // System.out.println("Date2: " + calid.getDate2());

        if (params.getDate1() == null) {
            LogHandler.getLogs().displayMsg("Must specify date",
                    Logging.WARNING);
            printHelp();
            return;
        }

        Pair pair = new Pair(params.getSource1(), params.getSource2());
        List<File> files;
        files = getResultFiles(pair, false);
        if(files.isEmpty()) {
            LogHandler.getLogs().displayMsg("No corresponing results found.",
                    Logging.WARNING);
        }
        for (File f : files) {
            printResultsDateList(f, params.getDate1(), params.getDate2());
        }

    }

    public void printMethodResults(int method) {

        if (params.getDate1() == null) {
            LogHandler.getLogs().displayMsg("Must specify date",
                    Logging.WARNING);
            printHelp();
            return;
        }

        List<Double> results = new ArrayList<Double>();

        Pair pair = new Pair(params.getSource1(), params.getSource2());
        List<File> files;
        files = getResultFiles(pair, false);

        CalidContainer cc = new CalidContainer(pair, params);
        
        CalidFileHandler.loadCoords(cc);

        for (File f : files) {

            Date readDate = null;
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
                    readDate = CalidFileHandler.CALID_DATE_TIME_FORMAT
                            .parse(line.split(" ")[0]);

                    if (!readDate.before(params.getDate1()) && !readDate.after(params.getDate2())) {
                        String[] words = line.split(" ");
                        if (words.length != cc.getPairedPointsList().size() + 1) {
                            continue;
                        }
                        for (int i = 1; i < words.length; i++) {
                            if (words[i].matches(NULL)) {
                                cc.getPairedPointsList().get(i - 1)
                                        .setDifference(null);
                            } else
                                cc.getPairedPointsList()
                                        .get(i - 1)
                                        .setDifference(
                                                Double.parseDouble(words[i]));
                        }

                    }
                }
            } catch (FileNotFoundException e) {
                LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
                LogHandler.getLogs().saveErrorLogs(this, e);
            } catch (Exception e) {
                LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
                LogHandler.getLogs().saveErrorLogs(this, e);
            }

            if (method == MEAN) {
                results.add(cc.getMean());
                System.out.println(results.get(results.size() - 1));
            } else if (method == MEDIAN) {
                results.add(cc.getMedian());
                System.out.println(results.get(results.size() - 1));
            } else if (method == RMS) {
                results.add(cc.getRMS());
                System.out.println(results.get(results.size() - 1));
            }

        }

    }
    
    public static void printHelp() {
        // LogHandler.getLogs().displayMsg("CALID algorytm usage:\n",
        // Logging.SILENT);

        String src = "src=Source1[,Source2]";
        String date = "date=Start[,End]";
        String rest = "[ele=X] [dis=X]";

        String msg = "CALID algorytm usage: jrat [options]\n";
        msg += "--calid-help\t\tprint this message\n";
        msg += "--calid-list [<args>]\tlist all available pairs\n\t\t\t"
                + "<args> " + src + " " + rest + "\n";
        msg += "--calid-result [<args>]\tdisplay results\n\t\t\t"
                + "<args> "
                + date + " [" + src + "] "
                + rest + "\n"
                + "\t\t\te.g: --calid-result src=Rzeszow"
                + " date=2011-08-21/09:30,2011-08-21/10:30\n";
        

        System.out.println(msg);
    }

}
