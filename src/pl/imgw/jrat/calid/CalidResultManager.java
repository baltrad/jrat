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

    CalidManager calid = null;

    public CalidResultManager(CalidManager calid) {
        this.calid = calid;
    }

    private List<File> getResultsFiles() {

        List<File> results = new LinkedList<File>();

        File folder = new File(CalidManager.getCalidPath());
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
        for (File f : results) {
//            System.out.println(f);
            try {
                Scanner scanner = new Scanner(f);
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("# src=")) {
//                        System.out.println(line);
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
            } catch (Exception e) {
                LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            }
        }

        if(printPairs)
            for(String s : pairSet)
                System.out.println(s);
        
        return matchingResults;
    }

    private boolean printResultsDateList(File f) {
//        System.out.println(f);
        int i = 0;
        try {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if(!scanner.hasNextLine()) {
                        System.out.println("No results found.");
                        return true;
                    }
                    continue;
                }
                System.out.println(line.split(" ")[0]);
                if (LogHandler.getLogs().getVerbose() < Logging.WARNING)
                    i++;
                if (i == 5) {
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
        }
        return true;
    }

    private boolean printResultsDateList(File f, Date start, Date end) {
        // System.out.println(f);

        Date readDate = null;
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
                readDate = CalidContainer.calidDateTime
                        .parse(line.split(" ")[0]);

                if (!readDate.before(start) && !readDate.after(end)) {

                    System.out.println(line);
                    if (LogHandler.getLogs().getVerbose() < Logging.WARNING)
                        i++;
                    if (i == 5) {
                        return false;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
        }
        return true;
    }

    
    public void printPairsList() {
        Pair pair = new Pair(calid.getSource1(), calid.getSource2());
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

//        System.out.println(files.size());
        
    }

    public static void printHelp() {
        // LogHandler.getLogs().displayMsg("CALID algorytm usage:\n",
        // Logging.SILENT);
        String msg = "CALID algorytm usage:\n";
        msg += "--calid-result [<args>]\t\tdisplay results\n";
        msg += "--calid-list [<args>]\t\tlist all available pairs with results [name1 name2]";
        System.out.println(msg);
    }
}
