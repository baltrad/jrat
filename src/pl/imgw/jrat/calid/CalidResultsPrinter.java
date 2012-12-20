/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
    
    /**
     * 
     */
    public CalidResultsPrinter(CalidParsedParameters params) {
        this.params = params;
    }
    
    
    public void printList() {
        List<File> files = getResultsFiles();
        for(File f : files)
            printResultsHeader(f);
        
    }
    
    private boolean printResultsHeader(File f) {
        // System.out.println(f);
        int i = 0;
        try {
            scanner = new Scanner(f);
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    System.out.println(line.substring(2));
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
        
        if(params.isEmpty()) {
            return true;
        }
        
        if (!src.isEmpty() && !params.getSource1().isEmpty()) {
            if (!params.getSource2().isEmpty()) {
                if (!src.contains(params.getSource1())
                        || !src.contains(params.getSource2()))
                    return false;
            } else if (!src.contains(params.getSource1())) {
                return false;
            }
        }
        if (!ele.isEmpty())
            if (Double.parseDouble(ele) != params.getElevation())
                return false;
        if(!dis.isEmpty())
            if (Integer.parseInt(dis) != params.getDistance())
                return false;
        if (!ref.isEmpty())
            if (Double.parseDouble(ref) != params.getReflectivity())
                return false;
        
        return true;
        
    }
    
}
