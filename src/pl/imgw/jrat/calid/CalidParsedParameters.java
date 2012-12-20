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
public class CalidParsedParameters {

    public static final String ELEVATION = "ele=";
    public static final String DISTANCE = "dis=";
    public static final String REFLECTIVITY = "ref=";
    public static final String SOURCE = "src=";
    public static final String DATE = "date=";
    public static final String METHOD = "method=";

    private static Integer DEFAULT_DIS = new Integer(1000);
    private static Double DEFAULT_REF = new Double(0.0);
    private static Double DEFAULT_ELE = new Double(0.0);
    
    // private HashMap<String, CoordsManager> mps = new HashMap<String,
    // CoordsManager>();
    // private PairsContainer pcont;
    // private Set<Pair> pairs = new HashSet<Pair>();

    private Double elevation = null;
    private Integer distance = null;
    private Double reflectivity = null;
    private String source1 = "";
    private String source2 = "";
    private Date date1 = null;
    private Date date2 = null;

    private boolean isEmpty = true;
    
    public boolean isEmpty() {
        return isEmpty;
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
    public boolean initialize(String[] par) {

        /*
         * Default settings
         */
        if(par == null) {
//            System.out.println("bez opcji");
            isEmpty = true;
            return true;
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
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + e.getLocalizedMessage() + ")", WARNING);
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            LogHandler.getLogs().displayMsg(
                    error_msg, WARNING);
            return false;
        } catch (ParseException e) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + e.getLocalizedMessage() + ")", WARNING);
            return false;
        }

        /*
         * Checks if provided parameters are valid
         */
        if (getElevation() < -10 || getElevation() > 90) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + getElevation() + ")", WARNING);
            return false;
        }
        if (getDistance() < 0) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + getDistance() + ")", WARNING);
            return false;
        }

//        System.out.println(elevation);
//        System.out.println(distance);
//        System.out.println(source1);
//        System.out.println(source2);
//        System.out.println(reflectivity);
        isEmpty = false;
        return true;
    }
    
    private void parseDate(String s) throws ParseException {
        
        String dates = s.substring(DATE.length());
        if (dates.contains(",")) {
            if (dates.split(",")[0].contains("/")) {
                date1 = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[0]);
            } else {
                date1 = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[0] + "/00:00");
            }
            if (dates.split(",")[1].contains("/")) {
                date2 = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[1]);
            } else {
                date2 = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[1] + "/23:59");
            }
        } else if (dates.contains("/")) {
            date1 = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates);
        } else {
            date1 = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                    + "/00:00");
            date2 = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                    + "/23:59");;
        }
    }
    

    /**
     * @return the elevation
     */
    public Double getElevation() {
        return (elevation == null) ? DEFAULT_ELE : elevation;
    }

    /**
     * @return the distance
     */
    public Integer getDistance() {
        return (distance == null) ? DEFAULT_DIS : distance;
    }

    /**
     * @return the reflectivity
     */
    public Double getReflectivity() {
        return (reflectivity == null) ? DEFAULT_REF : reflectivity;
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
