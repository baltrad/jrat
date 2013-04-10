/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.calid.CalidParsedParameters.DISTANCE;
import static pl.imgw.jrat.calid.CalidParsedParameters.ELEVATION;
import static pl.imgw.jrat.calid.CalidParsedParameters.REFLECTIVITY;
import static pl.imgw.jrat.calid.CalidParsedParameters.SOURCE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

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
public class CalidResultFileGetter {

    /**
     * 
     * @param params
     * @return
     */
    public static Set<File> getResultFiles(CalidParsedParameters params){
        Set<File> results = new TreeSet<File>();
        
        File folder = new File(CalidResultIOHandler.getCalidPath());
        // skip not-folders
        if (!folder.isDirectory())
            return results;
        
        /*
         * loop for pairname folders
         */
        for (File pairname : folder.listFiles()) {
            // skip not-folders
            if (!pairname.isDirectory())
                continue;

            // pair name
            String name = pairname.getName();

            /*
             * skip pairs which does not contains source name if they are set
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

            File[] resultFolders = null;
            
            if (areOnlySrcParametersSet(params)) {
//                System.out.println("only src");
                File f = getDefaultFolder(pairname);
                if (f != null) {
                    resultFolders = new File[] { f };
                    params.setParamsFromFolderName(f.getName());

                }
            } else if (areAllParametersUnset(params)) {
//                System.out.println("nothing's set");
                resultFolders = pairname.listFiles();
            } else {
//                System.out.println("all is set");
                File f = getMatchingFolder(pairname, params);
                if (f != null)
                    resultFolders = new File[] { f };
            }
            
            if(resultFolders == null)
                continue;
            
            /*
             * loop for parametername folders
             */
            for (File parameters : resultFolders) {
                //skip not-folders
                if (!parameters.isDirectory())
                    continue;

                // parameters
                for (File file : parameters.listFiles()) {
//                    System.out.println(file);
                    if (file.isFile() && file.getName().endsWith("results")) {
                        // if (areAllParametersUnset(params) || keep(file, params))
                        results.add(file);
                    }
                }

            }

        }
//        System.out.println("size=" + results.size());
        return results;
    }
    
    private static File getDefaultFolder(File pairFolder) {
        int max = 0;
        File biggest = null;
        for (File params : pairFolder.listFiles()) {
            if (params.isDirectory())
                if (params.listFiles().length > max)
                    biggest = params;
        }
        return biggest;
    }
    
    private static File getMatchingFolder(File pairFolder,
            CalidParsedParameters params) {
        for (File paramsFolder : pairFolder.listFiles()) {
            if (paramsFolder.getName().matches(
                    CalidResultIOHandler.getFolderName(params))) {
                return paramsFolder;
            }
        }
        return null;
    }
    
    
    private static boolean areOnlySrcParametersSet(CalidParsedParameters params) {
        if (!params.getSource1().isEmpty() && !params.getSource2().isEmpty()
                && params.isDistanceDefault() & params.isElevationDefault()
                && params.isReflectivityDefault() & params.isStartDateDefault())
            return true;
                
        return false;
    }
    
    private static boolean areAllParametersUnset(CalidParsedParameters params) {
        if (params.getSource1().isEmpty() && params.getSource2().isEmpty()
                && params.isDistanceDefault() & params.isElevationDefault()
                && params.isReflectivityDefault() & params.isStartDateDefault())
            return true;
                
        return false;
    }
    
    
    
    private static boolean keep(File file, CalidParsedParameters params) {
        
        String src = "";
        String ele = "";
        String dis = "";
        String ref = "";

        try {
            Scanner scanner = new Scanner(file);
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
            LogHandler.getLogs().saveErrorLogs(CalidResultFileGetter.class, e);
            return false;
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            LogHandler.getLogs().saveErrorLogs(CalidResultFileGetter.class, e);
            return false;
        }
        
        return true;

    }
}
