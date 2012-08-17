/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.tools.in.FileDate;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidManager {

    private static final String DEG = "deg";
    private static final String M = "m";

    private HashMap<String, MatchingPoints> mps = new HashMap<String, MatchingPoints>();
    private Set<Pair> pairs;

    private double elevation = -1;
    private int distance = -1;

    private String[] par = { "0.5deg", "500m" };

    public CalidManager(List<FileDate> files) {
        PairsContainer pcont = new PairsContainer(files);
        this.pairs = pcont.getPairs();
    }

    /**
     * Setting some parameters for the algorithm, including:</br>
     * 
     * elevation of the scan, in degrees, the proper format for the argument
     * should contain value and word 'deg' e.g. '0.5deg'</br>
     * 
     * distance (maximal) between overlapping pixels in meters, in other words
     * the precision of finding overlapping pixels, the proper format for the
     * argument should contain value and word 'm' e.g. '500m'
     * 
     * @param par
     *            array of size 2
     */
    public void setParameters(String[] par) {
        this.par = par;
    }

    /**
     * @return true if parameters are valid and scans with given elevation was
     *         found in the list of files that was set</br> If not set, default
     *         parameters will be use: elevation=0.5deg and distance=500m
     */
    public boolean start() {
        return initialize();
        /*
         * if(!initialize()) return false; return calculate();
         */
    }

    /*
     * parsing parameters, and looking for valid scans in the given files
     */
    private boolean initialize() {
        if (par == null || par.length != 2) {
            LogHandler.getLogs().displayMsg(
                    "Arguments for CALID are incorrect", WARNING);
            return false;

        }

        try {
            for (int i = 0; i < 2; i++) {
                if (par[i].endsWith(DEG)) {
                    elevation = Double.parseDouble(par[i].substring(0,
                            par[i].length() - 3));
                } else if (par[i].endsWith(M)) {
                    distance = Integer.parseInt(par[i].substring(0,
                            par[i].length() - 1));
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }

        if (elevation < 0 || distance < 0) {
            return false;
        }

        Iterator<Pair> i = pairs.iterator();
        while (i.hasNext()) {
            Pair pair = i.next();
            if (pair.getVol1().getScan(elevation).getArray() == null
                    || pair.getVol2().getScan(elevation).getArray() == null) {
                i.remove();
            }
        }

        if (pairs.isEmpty()) {
            LogHandler.getLogs().displayMsg(
                    "No pairs initialized for CALID. "
                            + "Check list of files and/or arguments values.",
                    WARNING);
            return false;
        }
        return true;
    }

    private boolean calculate() {

        if (pairs.isEmpty()) {
            LogHandler.getLogs().displayMsg(
                    "No pairs initialized for CALID. "
                            + "Calculation cannot be performed", WARNING);
            return false;
        }

        // long time = System.currentTimeMillis();
        ResultsManager rm = new ResultsManager();
        Iterator<Pair> pairsItr = pairs.iterator();
        while (pairsItr.hasNext()) {
            Pair pair = pairsItr.next();
            ScanContainer scan1 = pair.getVol1().getScan(elevation);
            ScanContainer scan2 = pair.getVol2().getScan(elevation);

            MatchingPoints mp = null;

        }

        return true;

    }

}
