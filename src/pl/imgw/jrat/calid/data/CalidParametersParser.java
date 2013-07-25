/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidParametersParser {

    private static final int MAX_ELEVATION = 90;
    private static final int MIN_ELEVATION = -10;
    private static final int MAX_REFLECTIVITY = 100;
    private static final int MIN_REFLECTIVITY = -40;

    private static CalidParametersParser parser = new CalidParametersParser();

    public static CalidParametersParser getParser() {
        return parser;
    }
    
    /**
     * 
     */
    private CalidParametersParser() {
        // TODO Auto-generated constructor stub
    }
    
    private static Log log = LogManager.getLogger();

    public static final String ELEVATION = "ele=";
    public static final String DISTANCE = "dis=";
    public static final String REFLECTIVITY = "ref=";
//    public static final String SOURCE = "src=";
    public static final String DATE = "date=";
    public static final String FREQUENCY = "freq=";
    public static final String RANGE = "range=";
    public static final String PERIOD = "period=";
    
    private static final String error_msg = "CALID: Arguments for CALID are incorrect";
    
    /**
     * 
     * Sets all parameters from String array, valid format for each String is
     * <tt>name=value</tt>
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
     *            <code>String[] par = { "ele=0.5", "dis=500" }</code> -
     *            elevation and distance
     *            <p>
     *            <code>String[] par = { "ele=0.5", "dis=500", "ref=3.5" }</code>
     *            - elevation, distance and reflectivity
     *            <p>
     *            if <code>par</code> is null or empty, the default parameters
     *            are set
     * 
     * @return returns false only if given parameter is not valid e.g. value of
     *         distance is negative, otherwise returns true
     * 
     */
    public CalidParameters parseParameters(String[] par) throws CalidException {

        CalidParameters params = new CalidParameters();

        /*
         * Default settings
         */
        if (par == null || par.length == 0) {
            // System.out.println("bez opcji");
            throw new CalidException(
                    "Cannot parse parameters, string array is empty");
        }

        for (int i = 0; i < par.length; i++) {

            if (par[i].isEmpty()) {
                throw new CalidException(error_msg + ": string " + i
                        + " is empty");
            }

            if (par[i].startsWith(ELEVATION)) {
                setElevation(params, par[i].substring(ELEVATION.length()));
            } else if (par[i].startsWith(DISTANCE)) {
                setDistance(params, par[i].substring(DISTANCE.length()));
            } else if (par[i].startsWith(RANGE)) {
                setRange(params, par[i].substring(RANGE.length()));
            } else if (par[i].startsWith(REFLECTIVITY)) {
                setReflectivity(params, par[i].substring(REFLECTIVITY.length()));
            } else if (par[i].startsWith(FREQUENCY)) {
                setFrequency(params, par[i].substring(FREQUENCY.length()));
            } else if (par[i].startsWith(DATE)) {
                setDates(params, par[i].substring(DATE.length()));
            } else if (par[i].startsWith(PERIOD)) {
                ;
            } else if (par[i].contains("=")) {
                log.printMsg(error_msg + " (" + par[i] + ")", Log.TYPE_WARNING,
                        Log.MODE_VERBOSE);

                throw new CalidException("Cannot parse parameter: " + par[i]);
            }

        }
        log.printMsg("Parsed parameters: " + params, Log.TYPE_NORMAL, Log.MODE_VERBOSE);
        return params;
        
    }
    
    public CalidPairAndParameters parsePairAndParameters(String[] par) throws CalidException {

        CalidParameters params = new CalidParameters();
        RadarsPair pair = null;
        
        /*
         * Default settings
         */
        if (par == null || par.length == 0) {
            // System.out.println("bez opcji");
            throw new CalidException(
                    "Cannot parse parameters, string array is empty");
        }

        for (int i = 0; i < par.length; i++) {
            if(!par[i].contains("=")) {
                pair = setSources(par[i]);
                log.printMsg(pair.toString(), Log.TYPE_NORMAL, Log.MODE_VERBOSE);
            }
        }
        params = parseParameters(par);
        return new CalidPairAndParameters(params, pair);
        
    }
    
    /**
     * Distance, elevation, reflectivity and range
     * 
     * @param folderName
     * @return
     * @throws CalidException
     */
    public CalidParameters getParamsFromFolderName(String folderName)
            throws CalidException {

        CalidParameters params = new CalidParameters();
        String[] sPar = folderName.split("_");
        if (sPar != null && sPar.length == 4) {

            setDistance(params, sPar[0]);
            setElevation(params, sPar[1]);
            setReflectivity(params, sPar[2]);
            setRange(params, sPar[3]);

        } else {
            throw new CalidException(folderName + " is not a valid CALID path");
        }
        return params;
    }
    
    private void setElevation(CalidParameters params, String word)
            throws CalidException {
        Double elevation = null;
        try {
            elevation = Double.parseDouble(word);
        } catch (NumberFormatException e) {
            throw new CalidException(elevation
                    + " is ont a valid elevation value");
        }

        if (elevation == null || elevation < MIN_ELEVATION || elevation > MAX_ELEVATION) {
            throw new CalidException(elevation
                    + " is ont a valid elevation value");
        }

        params.setElevation(elevation);

    }
    
    private void setDistance(CalidParameters params, String word)
            throws CalidException {
        Integer distance = null;
        try {
            distance = Integer.parseInt(word);
        } catch (NumberFormatException e) {
            throw new CalidException(distance
                    + " is ont a valid elevation value");
        }

        if (distance == null || distance < 0) {
            throw new CalidException(distance
                    + " is ont a valid distance value");
        }

        params.setDistance(distance);

    }
    
    private void setRange(CalidParameters params, String word)
            throws CalidException {
        Integer range = null;
        try {
            range = Integer.parseInt(word);
        } catch (NumberFormatException e) {
            throw new CalidException(range + " is ont a valid elevation value");
        }

        if (range == null || range < 1) {
            throw new CalidException(range + " is ont a valid range value");
        }

        params.setMaxRange(range);

    }
    
    private void setReflectivity(CalidParameters params, String word)
            throws CalidException {
        Double reflectivity = null;
        try {
            reflectivity = Double.parseDouble(word);
        } catch (NumberFormatException e) {
            throw new CalidException(reflectivity
                    + " is ont a valid elevation value");
        }

        if (reflectivity == null || reflectivity < MIN_REFLECTIVITY
                || reflectivity > MAX_REFLECTIVITY) {
            throw new CalidException(reflectivity
                    + " is ont a valid reflectivity value");
        }

        params.setReflectivity(reflectivity);

    }
    
    private void setFrequency(CalidParameters params, String word)
            throws CalidException {
        Integer frequency = null;
        try {
            frequency = Integer.parseInt(word);
        } catch (NumberFormatException e) {
            throw new CalidException(frequency + " is ont a valid elevation value");
        }

        if (frequency == null || frequency < 0 || frequency > 100) {
            throw new CalidException(frequency + " is ont a valid frequency value");
        }

        params.setFreq(frequency);

    }
    
    private RadarsPair setSources(String sources) throws CalidException {

        String source1 = "", source2 = "";
        if (sources.contains(",")) {
            source1 = sources.split(",")[0];
            source2 = sources.split(",")[1];
        } else
            source1 = sources;

        return new RadarsPair(source1, source2);
    }
    
    private void setDates(CalidParameters params, String dates) throws CalidException {
        
        String startDate, endDate;
        Date start, end;
        if(dates.contains(",")) {
            startDate = dates.split(",")[0];
            endDate = dates.split(",")[1];
        } else {
            startDate = dates;
            endDate = dates;
        }
        
        start = parseStartingDate(startDate);
        end = parseEndingDate(endDate);
        
        if(end.before(start))
            throw new CalidException("ending date before starting date");
        
        params.setRangeDates(start, end);
    }
    
    private Date parseStartingDate(String s) throws CalidException {

        String date = s.replaceAll("[^\\d]", "");
        int lenght = date.length();
        SimpleDateFormat sdf;
        
        switch (lenght) {
        case 4:
            sdf = new SimpleDateFormat("yyyy");
            break;
        case 6:
            sdf = new SimpleDateFormat("yyyyMM");
            break;
        case 8:
            sdf = new SimpleDateFormat("yyyyMMdd");
            break;
        case 10:
            sdf = new SimpleDateFormat("yyyyMMddHH");
            break;
        case 11:
            sdf = new SimpleDateFormat("yyyyMMddHmm");
            break;
        case 12:
            sdf = new SimpleDateFormat("yyyyMMddHHmm");
            break;
        default:
            throw new CalidException("Parsing date from: '" + s + "' failed");
        }
        
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new CalidException("Parsing date from: '" + s + "' failed");
        }
    }
    
    private Date parseEndingDate(String s) throws CalidException {

        String date = s.replaceAll("[^\\d]", "");
        int lenght = date.length();
        SimpleDateFormat sdf;
        Date endDate;
        Calendar cal = Calendar.getInstance();
        
        try {

            switch (lenght) {
            case 4:
                sdf = new SimpleDateFormat("yyyy");
                endDate = sdf.parse(date);
                cal.setTime(endDate);
                cal.set(Calendar.MONTH, cal.getMaximum(Calendar.MONTH));
                cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                break;
            case 6:
                sdf = new SimpleDateFormat("yyyyMM");
                endDate = sdf.parse(date);
                cal.setTime(endDate);
                cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                break;
            case 8:
                sdf = new SimpleDateFormat("yyyyMMdd");
                endDate = sdf.parse(date);
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                break;
            case 10:
                sdf = new SimpleDateFormat("yyyyMMddHH");
                endDate = sdf.parse(date);
                cal.setTime(endDate);
                cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                break;
            case 11:
                sdf = new SimpleDateFormat("yyyyMMddHmm");
                endDate = sdf.parse(date);
                cal.setTime(endDate);
                cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                break;
            case 12:
                sdf = new SimpleDateFormat("yyyyMMddHHmm");
                endDate = sdf.parse(date);
                cal.setTime(endDate);
                break;
            default:
                throw new CalidException("Parsing date from: '" + s
                        + "' failed");
            }
        } catch (ParseException e) {
            throw new CalidException("Parsing date from: '" + date + "' failed");
        }
        
        return cal.getTime();
    }
    
    public static void printHelp() {
        // log.displayMsg("CALID algorytm usage:\n",
        // Logging.SILENT);

        String src = "Source1[,Source2]";
        String date = "date=Start[,End]";
        String rest = "[ele=] [dis=] [range=] [ref=]";

        StringBuilder msg = new StringBuilder("CALID algorytm usage:\n");
        msg.append("jrat --calid [<args>] [-i files/folder(s)] [-v] [--calid-opt file]\n" +
        		"\tstart comparison\n\t");
        msg.append("<args> ");
        msg.append(rest);
        msg.append(" default options for every pair\n\t");
        msg.append("ele: elevation angle in degrees, from -10.0 to 90.0\n\t");
        msg.append("dis: minimal distance between two overlapping points in meters,");
        msg.append(" must be bigger then 0,\n\t");
        msg.append("range: maximum radar range for overlapping points, (range > 1)\n\t");
        msg.append("ref: minimal reflectivity (dBZ)\n\t");
        msg.append("--calid-opt file: use this file to set options for each pair separately\n\t");
        msg.append("e.g. jrat --calid-opt /opt/baltrad/jrat/etc/calid/radgen_calid.opt " +
        		"--calid ele=0.5 dis=500 ref=3.0 --seq=10\n\n");
        
        msg.append("jrat --calid-list [<args>]\n\tlist all available pairs")
                .append("\n\t<args> ");
        msg.append(src).append(" ").append(rest + "\n\n");
        
        msg.append("jrat --calid-result [<args>] [-d <args>]\n\tprints results");
        msg.append("\n\t<args> ");
        msg.append(date);
        msg.append(" [").append(src).append("] ");
        msg.append(rest);
        msg.append(" [freq=Z]\n\t");
        msg.append("date: sets range of time, if only starting date is selected then only ");
        msg.append("this date data is taken, valid format is yyyyMMdd/HHmm, but HHmm is optional,\n\t");
        msg.append("src: source name\n\n");
        
        msg.append("jrat --calid-plot [<args>] Source1,Source2\n\tlist all available pairs")
        .append("\n\t<args>");
        msg.append(rest);
        msg.append("\n");
        msg.append("\n");
        
        
        msg.append("jrat --calid-help print this message\n\n");
        // + "ele: elevation angle in degrees, from -10.0 to 90.0\n"
        // +
        // "dis: minimal distance between paired points in meters, must be bigger then 0,\n"
        // + "ref: minimal reflectivity (dBZ)\n"
        msg.append("\nuse jrat --help to print general jrat help message");
        /*
*/
        System.out.println(msg);
    }
    
}
