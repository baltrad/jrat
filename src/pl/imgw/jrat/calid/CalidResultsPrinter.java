/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;
import java.io.FileNotFoundException;
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
    
    /**
     * 
     */
    public CalidResultsPrinter(CalidParsedParameters params) {
        this.params = params;
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
            
            System.out.println("Printing list of available dates...\n");
            int n = 0;
            for (File f : files) {
                
                printResultsHeader(f);
                n += printResultsDateList(f);
            }
            if(n > 1)
            System.out.println("\t" + n + " dates database.");
        } else {

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
    
    private int printResultsDateList(File f) {
        // System.out.println(f);
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
                System.out.println("\t\t" + line.split(" ")[0]);
                
                i++;
                if (LogHandler.getLogs().getVerbose() < Logging.NORMAL && i == 5) {
                    System.out.println("\t\t\t.\n\t\t\t.\n\t\t\t.");
                    System.out
                            .println("\t(use -v parameter to print all)");
                    return -1;
                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
        }
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
                    System.out.println("\t" + line.substring(2));
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
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
            return false;
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
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
        if(!params.isDistanceDefault()) {
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
        
        return true;
        
    }
    
}
