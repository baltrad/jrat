/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pl.imgw.jrat.tools.in.FileDate;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * This class is responsible for receiving list of pairs and starting the
 * calculations. To prepare the list it has to be initialized with list of
 * available files containing radar volumes and list of parameters. Each volume
 * must contain a scan from elevation pointed in one of the parameters.
 * 
 * <p>
 * If files are not containing valid data the <code>initialize()</code> method
 * will return false, otherwise the calculation will start and when finish with
 * not empty results the method will return true.
 * 
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidManager {

    private static final String DEG = "deg";
    private static final String M = "m";
    private static final String DBZ = "dBZ";

    // private HashMap<String, CoordsManager> mps = new HashMap<String,
    // CoordsManager>();
    // private PairsContainer pcont;
    // private Set<Pair> pairs = new HashSet<Pair>();

    private double elevation = -1;
    private int distance = -1;
    private double reflectivity = -31.5;

    private CalidContainer container;

    /**
     * Generates path name to CALID folder specified by given parameters,
     * different for every pair, distance and elevation
     * 
     * @param pair
     * @param distance
     * @param elevation
     * @return
     */
    public static String getCalidPath(Pair pair, int distance, double elevation) {

        String pairsName = pair.getVol1().getSiteName()
                + pair.getVol2().getSiteName();

        String distele = distance + "_" + elevation;

        String folder = "calid/overlapping/" + pairsName + "/" + distele;

        new File(folder).mkdirs();

        return new File(folder).getPath();
    }

    // private String[] par = { "0.5deg", "500m" };

    /**
     * 
     * Initializes manager and sets two parameters for the algorithm:
     * 
     * <p>
     * <tt>elevation</tt> of the scan, in degrees, the proper format for the
     * argument should contain numerical value and word 'deg' e.g.
     * '0.5deg'</pre>
     * 
     * <p>
     * <tt>distance</tt> (maximal) between overlapping pixels in meters, in
     * other words the precision of finding overlapping pixels, the proper
     * format for the argument should contain numerical value and word 'm' e.g.
     * '500m'</pre>
     * 
     * <p>
     * <tt>reflectivity</tt> (minimal) that is taken to calculation, all points
     * with reflectivity below this value are skipped (they are treated as no
     * data). The proper format for the argument should contain numerical value
     * and word 'dBZ' e.g. '500m'</pre>
     * 
     * @param par
     *            array of size 2 or 3 eg.
     *            <p>
     *            <code>String[] par = { "0.5deg", "500m" }</code> - elevation
     *            and distance
     *            <p>
     *            <code>String[] par = { "0.5deg", "500m", "3.5dBZ" }</code> -
     *            elevation, distance and reflectivity
     */
    public CalidManager(String[] par) {

        if (par == null || par.length < 2) {
            LogHandler.getLogs().displayMsg(
                    "Arguments for CALID are incorrect", WARNING);
            return;
        }

        try {
            for (int i = 0; i < par.length; i++) {
                if (par[i].endsWith(DEG)) {
                    elevation = Double.parseDouble(par[i].substring(0,
                            par[i].length() - DEG.length()));
                } else if (par[i].endsWith(M)) {
                    distance = Integer.parseInt(par[i].substring(0,
                            par[i].length() - M.length()));
                } else if (par[i].endsWith(DBZ)) {
                    reflectivity = Double.parseDouble(par[i].substring(0,
                            par[i].length() - DBZ.length()));
                }
            }
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    "Format of arguments for CALID is incorrect", WARNING);
            return;
        }

        if (elevation < 0 || distance < 0) {
            return;
        }

    }

    /**
     * Compares two scans in
     * 
     * @return null if failed
     */
    public ArrayList<PairedPoints> compare(Pair pair) {
        // check if the volumes contain selected elevation
        if (pair.getVol1().getScan(elevation) == null
                || pair.getVol2().getScan(elevation) == null) {
            return null;
        }

        ArrayList<PairedPoints> pairedPointsList = null;

        // LogHandler.getLogs().displayMsg(
        // "Calculating overlapping points for: " + pair.getSource1()
        // + " and " + pair.getSource2(), LogHandler.WARNING);

        container = new CalidContainer(pair, elevation, distance, reflectivity);
        pairedPointsList = container.getCoords();

        if (!pairedPointsList.isEmpty()) {
            LogHandler.getLogs().displayMsg(
                    "Number of overlapping points: " + pairedPointsList.size(),
                    LogHandler.WARNING);

        } else {
            LogHandler.getLogs().displayMsg("No overlapping points.",
                    LogHandler.WARNING);
            return null;
        }

        if (!container.loadResults(pair.getDate())) {

            Comparator.compare(pairedPointsList,
                    pair.getVol1().getScan(elevation),
                    pair.getVol2().getScan(elevation), reflectivity);

            container.saveResults();
        }

        // results = comp.getResults();
        // comp.save(coords.getId(), pair.toString(), pair.getDate());

        LogHandler.getLogs().displayMsg(
                "Comparison completed for: " + pair.getSource1() + " and "
                        + pair.getSource2(), LogHandler.WARNING);

        return pairedPointsList;
    }

    public List<PairedPoints> getResults(Pair pair, Date date) {

        return null;
    }

    public void displayResults(Pair pair, Date date) {

    }

}
