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

    public void setFileList(List<FileDate> files) {
        PairsContainer pcont = new PairsContainer(files);
        this.pairs = pcont.getPairs();
    }

    public boolean initialize(String[] args) {
        if (args == null || args.length != 2) {
            LogHandler.getLogs().displayMsg(
                    "Arguments for CALID are incorrect", WARNING);
            return false;

        }

        try {
            for (int i = 0; i < 2; i++) {
                if (args[i].endsWith(DEG)) {
                    elevation = Double.parseDouble(args[i].substring(0,
                            args[i].length() - 3));
                } else if (args[i].endsWith(M)) {
                    distance = Integer.parseInt(args[i].substring(0,
                            args[i].length() - 1));
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

    public void calculate() {

        // long time = System.currentTimeMillis();
        ResultsManager rm = new ResultsManager();
        Iterator<Pair> pairsItr = pairs.iterator();
        while (pairsItr.hasNext()) {
            Pair pair = pairsItr.next();
            ScanContainer scan1 = pair.getVol1().getScan(elevation);
            ScanContainer scan2 = pair.getVol2().getScan(elevation);
            if (scan1 != null && scan2 != null) {
                MatchingPoints mp = null;

            }
        }

    }

}
