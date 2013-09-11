/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static pl.imgw.jrat.AplicationConstans.ETC;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidDataHandler {

    protected static Log log = LogManager.getLogger();

    private static final String COORDSFILE = "coords.xml";
    private static final String RESULTSFILE = "results";

    protected static final String PAIR = "pair";
    protected static final String R1LAT = "r1lat";
    protected static final String R1LON = "r1lon";
    protected static final String R1BINS = "r1bins";
    protected static final String R1SCALE = "r1scale";
    protected static final String R2LAT = "r2lat";
    protected static final String R2LON = "r2lon";
    protected static final String R2BINS = "r2bins";
    protected static final String R2SCALE = "r2scale";
    protected static final String POINT = "point";
    protected static final String R1BIN = "r1bin";
    protected static final String R1RAY = "r1ray";
    protected static final String R2BIN = "r2bin";
    protected static final String R2RAY = "r2ray";
    protected static final String SIZE = "size";
    
    protected static final String COMMENTS = "#";

    public static final SimpleDateFormat CALID_DATE_TIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd/HH:mm");
    public static final SimpleDateFormat CALID_DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMdd");
    public static final String NULL = "n";

    // private int id = -1;

    protected static File getCoordsPath(CalidParameters params, RadarsPair pair) {
        return new File(getCalidPath(params, pair), COORDSFILE);
        
    }
    
//    public static File getCoordsPath(CalidResultParameters params) {
//        return new File(getCalidPath(params), COORDSFILE);
//    }

    protected static File getResultsPath(CalidParameters params, Date date, RadarsPair pair) {
        return new File(getCalidPath(params, pair), CALID_DATE_FORMAT.format(date) + "."
                + RESULTSFILE);
    }

    private static String getCalidPath(CalidParameters param, RadarsPair pair) {

        String src1 = pair.getSource1().replaceAll("[^A-Za-z0-9]", "");
        String src2 = pair.getSource2().replaceAll("[^A-Za-z0-9]", "");

        int distance = param.getDistance();
        double elevation = param.getElevation();
        double reflectivity = param.getReflectivity();
        int range = param.getMaxRange();
        String folder = "calid/" + src1 + src2 + "/"
                + getFolderName(distance, elevation, reflectivity, range);

        if (ETC.isEmpty()) {
            new File(folder).mkdirs();
            return new File(folder).getPath();
        }
        new File(ETC, folder).mkdirs();

        return new File(ETC, folder).getPath();
    }

    /**
     * 
     * @param params
     * @return calid results folder name
     */
    public static String getFolderName(CalidParameters params) {
        double reflectivity = params.getReflectivity();
        int distance = params.getDistance();
        Double elevation = params.getElevation();
        Integer range = params.getMaxRange();
        return getFolderName(distance, elevation, reflectivity, range);
    }

    /**
     * 
     * @param distance
     * @param elevation
     * @param reflectivity
     * @param range
     * @return calid results folder name
     */
    public static String getFolderName(int distance, double elevation,
            double reflectivity, int range) {
        return distance + "_" + elevation + "_" + reflectivity + "_" + range;
    }

    /**
     * 
     * @param folderName
     * 
     * @return null if <code>folderName</code> is not a valid calid results
     *         folder
     */
    public static CalidParameters getParamsFromFolderName(
            String folderName) {
        CalidParameters params = null;
        String[] sPar = folderName.split("_");
        if (sPar != null && sPar.length == 4) {

            int distance;
            Double elevation;
            double reflectivity;
            Integer range;
            try {
                distance = Integer.parseInt(sPar[0]);
                elevation = Double.parseDouble(sPar[1]);
                reflectivity = Double.parseDouble(sPar[2]);
                range = Integer.parseInt(sPar[3]);

                params = new CalidParameters(elevation, distance, range,
                        reflectivity);
            } catch (NumberFormatException e) {
            }
        }
        return params;
    }

    /**
     * Receives path name to CALID root folder
     * 
     * @return
     */
    public static String getCalidPath() {
        return new File(ETC, "calid").getPath();
    }

}
