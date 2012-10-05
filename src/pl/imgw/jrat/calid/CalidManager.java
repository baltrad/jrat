/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.tools.out.Logging.WARNING;

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

    // private HashMap<String, CoordsManager> mps = new HashMap<String,
    // CoordsManager>();
    // private PairsContainer pcont;
    // private Set<Pair> pairs = new HashSet<Pair>();

    private double elevation = -1;
    private int distance = -1;

    double[] results;

    // private String[] par = { "0.5deg", "500m" };

    /**
     * 
     * Creating pairs from list of files and setting two parameters for the
     * algorithm:
     * 
     * <p>
     * <tt>elevation</tt> of the scan, in degrees, the proper format for the
     * argument should contain value and word 'deg' e.g. '0.5deg'</pre>
     * 
     * <p>
     * <tt>distance</tt> (maximal) between overlapping pixels in meters, in
     * other words the precision of finding overlapping pixels, the proper
     * format for the argument should contain value and word 'm' e.g.
     * '500m'</pre>
     * 
     * @param par
     *            array of size 2 eg.
     *            <code>String[] par = { "0.5deg", "500m" }</code>
     */
    public CalidManager(String[] par) {

        if (par == null || par.length != 2) {
            LogHandler.getLogs().displayMsg(
                    "Arguments for CALID are incorrect", WARNING);
            return;
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
            return;
        }

        if (elevation < 0 || distance < 0) {
            return;
        }

    }

    /**
     * 
     * 
     * @return 
     */
    public List<CalidCoords> calculate(Pair pair) {
        if (pair.getVol1().getScan(elevation) == null
                || pair.getVol2().getScan(elevation) == null) {
            return null;

        }


        List<CalidCoords> rayBins = null;
        CoordsManager coords = new CoordsManager(pair, elevation, distance);

        results = ResultsManager.loadResults(coords.getId(), pair.toString(),
                pair.getDate());

        if (results == null) {

            LogHandler.getLogs().displayMsg(
                    "Calculating overlapping points for: " + pair.getSource1()
                    + " and " + pair.getSource2(), LogHandler.WARNING);
            rayBins = coords.getCoords();
            Comparator comp = new Comparator(rayBins, pair.getVol1().getScan(
                    elevation), pair.getVol2().getScan(elevation));

            // results = comp.getResults();
            // comp.save(coords.getId(), pair.toString(), pair.getDate());

        }

        LogHandler.getLogs().displayMsg(
                "Calculation completed for: " + pair.getSource1()
                + " and " + pair.getSource2(), LogHandler.WARNING);
        
        
        return rayBins;
    }

    public List<CalidCoords> getResults(Pair pair, Date date, double elevation,
            int distance) {
        return null;
    }

    public void displayResults(Pair pair, Date date, double elevation,
            int distance) {

    }

}
