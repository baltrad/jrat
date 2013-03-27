/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.text.ParseException;
import java.util.Date;

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
    public static final String FREQUENCY = "freq=";
    public static final String RANGE = "range=";
    

    public static Integer DEFAULT_DIS = new Integer(1000);
    public static Double DEFAULT_REF = new Double(0.0);
    public static Double DEFAULT_ELE = new Double(0.0);
    public static Integer DEFAULT_FREQ = new Integer(1);
    public static Integer DEFAULT_RANGE = new Integer(200);
    
    // private HashMap<String, CoordsManager> mps = new HashMap<String,
    // CoordsManager>();
    // private PairsContainer pcont;
    // private Set<Pair> pairs = new HashSet<Pair>();

    private Double elevation = null;
    private Integer distance = null;
    private Integer range = null;
    private Double reflectivity = null;
    private String source1 = "";
    private String source2 = "";
    private Date start = null;
    private Date end = null;
    private Integer freq = null;

    // private String[] par = { "0.5deg", "500m" };

    
    /**
     * 
     * Initializes manager and sets parameters for the algorithm, valid format
     * is name=value
     * 
     * <p>
     * <tt>ele=</tt> elevation of the scan, in degrees, the proper format for
     * the argument should contain double value e.g. ele=0.5</pre>
     * 
     * <p>
     * <tt>dis=</tt> distance (maximal) between overlapping pixels in meters, in
     * other words the precision of finding overlapping pixels, the proper
     * format for the argument should contain integer value e.g. dis=500</pre>
     * 
     * <p>
     * <tt>ref=</tt> reflectivity in dBZ (minimal) that is taken to calculation,
     * all points with reflectivity below this value are skipped (they are
     * treated as no data). The proper format for the argument should contain
     * double value e.g. ref=3.5 </pre>
     * 
     * <p>
     * <tt>date=</tt> date (from [,to]) e.g. date=2012-12-30 or
     * date=2012-12-30/09:10,2012-12-30/10:00</pre>
     * 
     * <p>
     * <tt>freq=</tt> minimal frequency in per cents of non zero results that is
     * taken to calculation e.g. freq=30</pre>
     * 
     * <p>
     * <tt>range=</tt> maximum radar range for overlapping points in km e.g.
     * range=200</pre>
     * 
     * @param par
     *            array of strings each representing parameters.
     *            <p>
     *            <code>String[] par = { "0.5deg", "500m" }</code> - elevation
     *            and distance
     *            <p>
     *            <code>String[] par = { "0.5deg", "500m", "3.5dBZ" }</code> -
     *            elevation, distance and reflectivity
     */
    public boolean initialize(String[] par) {

        resetAllFields();
        
        /*
         * Default settings
         */
        if(par == null) {
//            System.out.println("bez opcji");
            return true;
        }
        
        String error_msg = "CALID: Arguments for CALID are incorrect";
        try {
            for (int i = 0; i < par.length; i++) {
                if (par[i].startsWith(ELEVATION)) {
                    elevation = Double.parseDouble(par[i].substring(ELEVATION.length()));
                } else if (par[i].startsWith(DISTANCE)) {
                    distance = Integer.parseInt(par[i].substring(DISTANCE.length()));
                } else if (par[i].startsWith(RANGE)) {
                    range = Integer.parseInt(par[i].substring(RANGE.length()));
                } else if (par[i].startsWith(REFLECTIVITY)) {
                    reflectivity = Double.parseDouble(par[i].substring(REFLECTIVITY.length()));
                } else if (par[i].startsWith(FREQUENCY)) {
                    freq = Integer.parseInt(par[i].substring(FREQUENCY.length()));
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
                    error_msg + " (" + ELEVATION + getElevation() + ")", WARNING);
            return false;
        }
        if (getDistance() < 0) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + DISTANCE + getDistance() + ")", WARNING);
            return false;
        }
        
        if (getMaxRange() < 1) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + RANGE + getDistance() + ")", WARNING);
            return false;
        }
        
        if (getFrequency() < 1 || getFrequency() > 100) {
            LogHandler.getLogs().displayMsg(
                    error_msg + " (" + FREQUENCY + getFrequency() + ")", WARNING);
            return false;
        }
        
//        System.out.println(elevation);
//        System.out.println(distance);
//        System.out.println(source1);
//        System.out.println(source2);
//        System.out.println(reflectivity);
        return true;
    }
    
    private void parseDate(String s) throws ParseException {
        
        String dates = s.substring(DATE.length());
        if (dates.contains(",")) {
            if (dates.split(",")[0].contains("/")) {
                start = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[0]);
            } else {
                start = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[0] + "/00:00");
            }
            if (dates.split(",")[1].contains("/")) {
                end = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[1]);
            } else {
                end = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                        .split(",")[1] + "/23:59");
            }
        } else if (dates.contains("/")) {
            start = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates);
            end = start;
        } else {
            start = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                    + "/00:00");
            end = CalidFileHandler.CALID_DATE_TIME_FORMAT.parse(dates
                    + "/23:59");;
        }
    }
    
    
    private void resetAllFields() {
        elevation = null;
        distance = null;
        reflectivity = null;
        source1 = "";
        source2 = "";
        start = null;
        end = null;
    }
    
    public static void printHelp() {
        // LogHandler.getLogs().displayMsg("CALID algorytm usage:\n",
        // Logging.SILENT);

        String src = "src=Source1[,Source2]";
        String date = "date=Start[,End]";
        String rest = "[ele=] [dis=] [range=] [ref=]";

        StringBuilder msg = new StringBuilder("CALID algorytm usage:\n");
        msg.append("jrat --calid <args> -i files/folder(s) [-v] start comparison\n");
        msg.append("<args> ");
        msg.append(rest);
        msg.append("\n");
        msg.append("ele: elevation angle in degrees, from -10.0 to 90.0\n");
        msg.append("dis: minimal distance between two overlapping points in meters,");
        msg.append(" must be bigger then 0,\n");
        msg.append("range: maximum radar range for overlapping points, (range > 1)\n");
        msg.append("ref: minimal reflectivity (dBZ)\n\n");
        msg.append("e.g.  jrat --calid ele=0.5 dis=500 ref=3.5 ");
        msg.append("-i T_PAGZ48_C_SOWR_20120109233027.h5 T_PAGZ44_C_SOWR_20120109233016.h5 -v\n\n");
        msg.append("jrat --calid-help print this message\n\n");
        msg.append("jrat --calid-list [<args>] list all available pairs.").append("\n<args> ");
        msg.append(src).append(" ").append(rest + "\n\n");
        msg.append("jrat --calid-result [<args>] [-d <args>] display results.");
        msg.append("\n<args> ");
        msg.append(date);
        msg.append(" [").append(src).append("] ");
        msg.append(rest);
        msg.append(" [freq=Z]\n");
        msg.append("date: sets range of time, if only starting date is selected then only ");
        msg.append("this date data is taken, valid format is yyyyMMdd/HHmm, but HHmm is optional,\n");
        msg.append("src: source name\n");
//                + "ele: elevation angle in degrees, from -10.0 to 90.0\n"
//                + "dis: minimal distance between paired points in meters, must be bigger then 0,\n"
//                + "ref: minimal reflectivity (dBZ)\n"
        msg.append("freq: minimal frequency percentage of paired points with precipitation ");
        msg.append("above used threshold (of dBZ), must be bigger then 0,\n");
        msg.append("--calid-detail <args> using specified method to present results, available arguments are:");
        msg.append(" method=median, and period=X where X means number of days (X > 0).\n");
        msg.append("e.g: --calid-result src=Rzeszow");
        msg.append(" date=2011-08-21/09:30,2011-08-21/10:30 freq=10 --calid-detail method=mean period=1\n");
        msg.append("\nuse jrat --help to print general jrat help message");
        /*
*/
        System.out.println(msg);
    }
    
    /**
     * @return the elevation
     */
    public Double getElevation() {
        return (elevation == null) ? DEFAULT_ELE : elevation;
    }

    public boolean isElevationDefault() {
        return (elevation == null) ? true : false;
    }

    /**
     * @return the distance
     */
    public Integer getDistance() {
        return (distance == null) ? DEFAULT_DIS : distance;
    }

    public boolean isDistanceDefault() {
        return (distance == null) ? true : false;
    }

    /**
     * @return the frequency
     */
    public Integer getFrequency() {
        return (freq == null) ? DEFAULT_FREQ : freq;
    }

    public boolean isFrequencyDefault() {
        return (freq == null) ? true : false;
    }
    
    /**
     * @return the reflectivity
     */
    public Double getReflectivity() {
        return (reflectivity == null) ? DEFAULT_REF : reflectivity;
    }

    public boolean isReflectivityDefault() {
        return (reflectivity == null) ? true : false;
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
        return start;
    }

    /**
     * @return the date2
     */
    public Date getDate2() {
        return end;
    }

    /**
     * @return
     */
    public boolean isDate1Default() {
        return (start == null) ? true : false;
    }

    /**
     * @return
     */
    public boolean isDate2Default() {
        return (end == null) ? true: false;
    }

    /**
     * @return
     */
    public int getMaxRange() {
        return (range == null) ? DEFAULT_RANGE : range;
    }
    
}
