/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;
import static pl.imgw.jrat.AplicationConstans.ETC;
import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
    private static final String SOURCE = "src=";
    private static final String DATE = "date=";

    // private HashMap<String, CoordsManager> mps = new HashMap<String,
    // CoordsManager>();
    // private PairsContainer pcont;
    // private Set<Pair> pairs = new HashSet<Pair>();

    private double elevation = 0;
    private int distance = 0;
    private double reflectivity = -31.5;
    private String source1 = "";
    private String source2 = "";
    private Date date1 = null;
    private Date date2 = null;

    private boolean valid = false;
    
    /**
     * Receives path name to CALID results folder specified by given parameters,
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

        if(ETC.isEmpty()) {
            new File(folder).mkdirs();
            return new File(folder).getPath();
        }
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

    public boolean isValid() {
        return valid;
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

        if(par == null) {
//            System.out.println("bez opcji");
            valid = true;
            return;
        }
        String error_msg = "CALID: Arguments for CALID are incorrect";
        try {
            for (int i = 0; i < par.length; i++) {
                if (par[i].startsWith(ELEVATION)) {
                    elevation = Double.parseDouble(par[i].substring(ELEVATION.length()));
                } else if (par[i].startsWith(DISTANCE)) {
                    distance = Integer.parseInt(par[i].substring(DISTANCE.length()));
                } else if (par[i].startsWith(REFLECTIVITY)) {
                    reflectivity = Double.parseDouble(par[i].substring(REFLECTIVITY.length()));
                } else if (par[i].startsWith(SOURCE)) {
                    String sources = par[i].substring(SOURCE.length());
                    if (sources.contains(",")) {
                        source1 = sources.split(",")[0];
                        source2 = sources.split(",")[1];
                    } else
                        source1 = sources;
                } else if (par[i].startsWith(DATE)) {
                    parseDate(par[i]);
                } else {
                    LogHandler.getLogs().displayMsg(
                            error_msg + " (" + par[i] + ")", WARNING);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + e.getLocalizedMessage() + ")", WARNING);
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            LogHandler.getLogs().displayMsg(
                    error_msg, WARNING);
            return;
        } catch (ParseException e) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + e.getLocalizedMessage() + ")", WARNING);
            return;
        }

        if (elevation < -10 || elevation > 90 || distance < 0) {
            return;
        }

//        System.out.println(elevation);
//        System.out.println(distance);
//        System.out.println(source1);
//        System.out.println(source2);
//        System.out.println(reflectivity);
        
        valid = true;
    }
    
    private void parseDate(String s) throws ParseException {
        
        String dates = s.substring(DATE.length());
        if (dates.contains(",")) {
            if (dates.split(",")[0].contains("/")) {
                date1 = CalidContainer.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[0]);
            } else {
                date1 = CalidContainer.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[0] + "/00:00");
            }
            if (dates.split(",")[1].contains("/")) {
                date2 = CalidContainer.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[1]);
            } else {
                date2 = CalidContainer.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[1] + "/23:59");
            }
        } else if (dates.contains("/")) {
            date1 = CalidContainer.CALID_DATE_TIME_FORMAT.parse(dates);
        } else {
            date1 = CalidContainer.CALID_DATE_TIME_FORMAT.parse(dates
                    + "/00:00");
            date2 = CalidContainer.CALID_DATE_TIME_FORMAT.parse(dates
                    + "/23:59");;
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

    /**
     * @return the source1
     */
    public String getSource1() {
        return source1;
    }

    /**
     * @return the source2
     */
    public String getSource2() {
        return source2;
    }

    /**
     * @return the date1
     */
    public Date getDate1() {
        return date1;
    }

    /**
     * @return the date2
     */
    public Date getDate2() {
        return date2;
    }

    
    
}
