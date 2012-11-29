/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;
import static pl.imgw.jrat.AplicationConstans.ETC;
import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static final String ELEVATION = "ele=";
    private static final String DISTANCE = "dis=";
    private static final String REFLECTIVITY = "ref=";

    // private HashMap<String, CoordsManager> mps = new HashMap<String,
    // CoordsManager>();
    // private PairsContainer pcont;
    // private Set<Pair> pairs = new HashSet<Pair>();

    private double elevation = -1;
    private int distance = -1;
    private double reflectivity = -31.5;

    /**
     * Receives path name to CALID folder specified by given parameters,
     * different for every pair, distance, elevation and reflectivity
     * 
     * @param pair
     * @param distance
     * @param elevation
     * @param reflectivity
     * @return
     */
    public static String getCalidPath(Pair pair, int distance,
            double elevation, double reflectivity) {

        String pairsName = pair.getVol1().getSiteName()
                + pair.getVol2().getSiteName();

        String distele = distance + "_" + elevation + "_" + reflectivity;

        String folder = "calid/" + pairsName + "/" + distele;

        new File(ETC, folder).mkdirs();

        return new File(ETC, folder).getPath();
    }
    
    /**
     * Receives path name to CALID root folder
     * @return
     */
    public static String getCalidPath() {
        return new File(ETC, "calid").getPath();
    }

    // private String[] par = { "0.5deg", "500m" };

    /**
     * 
     * Initializes manager and sets parameters for the algorithm:
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

        String error_msg = "CALID: Arguments for CALID are incorrect, use --help option for details";
        
        if (par == null || par.length < 2) {
            LogHandler.getLogs().displayMsg(
                    error_msg, WARNING);
            return;
        }

        try {
            for (int i = 0; i < par.length; i++) {
                if (par[i].startsWith(ELEVATION)) {
                    elevation = Double.parseDouble(par[i].substring(ELEVATION.length()));
                } else if (par[i].startsWith(DISTANCE)) {
                    distance = Integer.parseInt(par[i].substring(DISTANCE.length()));
                } else if (par[i].startsWith(REFLECTIVITY)) {
                    reflectivity = Double.parseDouble(par[i].substring(REFLECTIVITY.length()));
                }
            }
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    error_msg, WARNING);
            return;
        }

        if (elevation < 0 || distance < 0) {
            return;
        }

    }
    
    /**
     * @return the elevation
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * @return the distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * @return the reflectivity
     */
    public double getReflectivity() {
        return reflectivity;
    }

}
