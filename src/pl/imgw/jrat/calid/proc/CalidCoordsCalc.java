/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidDataSaver;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.CalidSingleResultContainer;
import pl.imgw.jrat.calid.data.PairedPoint;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.calid.data.RayBinData;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.proj.VincentyFormulas;
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
public class CalidCoordsCalc {

    private static Log log = LogManager.getLogger();
    
    /**
     * 
     * @param params
     * @param pair
     * @return <b>null</b> if no overlapping points found
     * @throws CalidException
     */
    public static List<PairedPoint> calculateCoords(CalidParameters params,
            PolarVolumesPair pair) throws CalidException {   

        log.printMsg(
                "Calculating overlapping points for "
                        + pair, Log.TYPE_NORMAL, Log.MODE_VERBOSE);
        
        double ele = params.getElevation();

        Point2D.Double r1coords = new Point2D.Double(pair.getVol1().getLon(),
                pair.getVol1().getLat());
        Point2D.Double r2coords = new Point2D.Double(pair.getVol2().getLon(),
                pair.getVol2().getLat());

        int radHalfDist = 0;
        
        Double dist = VincentyFormulas.dist(r1coords, r2coords);
        if (dist != null)
            radHalfDist = (int) (dist / 2000);

        
        double r1binScale = getScale(pair.getVol1(), ele);
        double r2binScale = getScale(pair.getVol2(), ele);

        int radarRange1 = setRange(params.getMaxRange(), ele, pair.getVol1(), r1binScale);
        int radarRange2 = setRange(params.getMaxRange(), ele, pair.getVol2(), r2binScale);
        
        
        int r1bin0 = (int) (radHalfDist / (r1binScale / 1000));
        int r2bin0 = (int) (radHalfDist / (r2binScale / 1000));
        int r1binMax = (int) (radarRange1 / (r1binScale / 1000));
        int r2binMax = (int) (radarRange2 / (r2binScale / 1000));
        /*===========================================================*/
        
        if (radHalfDist > radarRange1 || radHalfDist > radarRange2) {
            log.printMsg("CALID: Radars are too far from each other"
                    + " and have no overlapping points", Log.TYPE_WARNING,
                    Log.MODE_VERBOSE);
            throw new CalidException(pair + " are too far from each other");
        }

        int r1nrays = pair.getVol1().getScan(ele).getNRays();
        int r2nrays = pair.getVol2().getScan(ele).getNRays();
        
        double r1rayScale = round(360.0 / r1nrays, 2);
        double r2rayScale = round(360.0 / r2nrays, 2);
        
        double rad1bearing = VincentyFormulas.getBearing(r1coords, r2coords);
        double rad2bearing = VincentyFormulas.getBearing(r2coords, r1coords);

        if (rad1bearing < 0) {
            rad1bearing = 360 + rad1bearing;
        }
        if (rad2bearing < 0) {
            rad2bearing = 360 + rad2bearing;
        }

        // limiting area to improve performance, with small buffer=2
        int r1ray0 = (int) (rad1bearing - getAngle(radHalfDist,
                radarRange1)) - 2;
        if (r1ray0 < 0) {
            r1ray0 = 360 + r1ray0;
        }
        int r1rayMax = (int) (rad1bearing + getAngle(radHalfDist, radarRange1)) + 2;

        if (r1rayMax > 360) {
            r1rayMax = r1rayMax - 360;
        }
        
        int r2ray0 = (int) (rad2bearing - getAngle(radHalfDist,
                radarRange2)) - 2;
        int r2rayMax = (int) (rad2bearing + getAngle(radHalfDist, radarRange2)) + 2;
        if (r2ray0 < 0) {
            r2ray0 = 360 + r2ray0;
        }
        if (r2rayMax > 360) {
            r2rayMax = r2rayMax - 360;
        }

        List<PairedPoint> pairedPointsList = new ArrayList<PairedPoint>();
        double a1, a2;
        
        for (int b1 = r1bin0; b1 < r1binMax; b1++) {
            for (int b2 = r2bin0; b2 < r2binMax; b2++) {

                if (b1 != b2)
                    continue;
                for (int r1 = 0; r1 < r1nrays; r1++) {
                    for (int r2 = 0; r2 < r2nrays; r2++) {
                        
                        a1 = r1 * r1rayScale;
                        a2 = r2 * r2rayScale;
                        
                        if(!checkAngle(r1ray0, r1rayMax, a1)) {
                            continue;
                        }
                        
                        if(!checkAngle(r2ray0, r2rayMax, a2)) {
                            continue;
                        }
                        
                        double calculatedDist1 = Math.cos(Math.toRadians(ele))
                                * (b1 + 0.5) * r1binScale;
                        Point2D.Double p1 = VincentyFormulas.dest(r1coords, a1,
                                calculatedDist1);
                        double calculatedDist2 = Math.cos(Math.toRadians(ele))
                                * (b2 + 0.5) * r2binScale;
                        Point2D.Double p2 = VincentyFormulas.dest(r2coords, a2,
                                calculatedDist2);
                        Double calculatedDist = VincentyFormulas.dist(p1, p2);
                        
                        if (calculatedDist < params.getDistance()) {
                            RayBinData rb = new RayBinData(r1, b1, r2, b2);
                            rb.setCoord1(p1);
                            rb.setCoord2(p2);

                            pairedPointsList.add(rb);
                        }
                    }
                }
            }
        }
        
        log.printMsg("Overlapping points: " + pairedPointsList.size() + " for "
                + pair, Log.TYPE_NORMAL, Log.MODE_VERBOSE);
        
        saveCoords(params, pair, pairedPointsList);
        return pairedPointsList.isEmpty() ? null : pairedPointsList;
    }


    /**
     * @param params
     * @param pair
     * @param pairedPointsList
     */
    private static void saveCoords(CalidParameters params,
            PolarVolumesPair pair, List<PairedPoint> pairedPointsList) {
        CalidSingleResultContainer results = new CalidSingleResultContainer(params, pair);
        results.setCoords(pairedPointsList);
        try {
            CalidDataSaver.saveCoords(results);
        } catch (CalidException e) {
        }
    }


    /**
     * 
     * @param params
     * @param pair
     * @param binScale
     * @return
     */
    private static int setRange(int maxRange, double elevation, PolarData data,
            double binScale) {

        try {
            int radarRange = (int) (data.getScan(elevation).getNBins()
                    * binScale / 1000);
            radarRange = (radarRange > maxRange) ? maxRange : radarRange;
            return radarRange;
        } catch (NullPointerException e) {
            throw new CalidException(data.getSiteName() + " does not have "
                    + elevation + " elevation");
        }
    }


    /**
     * 
     * @param data
     * @param ele
     * @return
     */
    private static double getScale(PolarData data, double ele) {
        try {
            return data.getScan(ele).getRScale();
        } catch (NullPointerException e) {
            throw new CalidException(data.getSiteName()
                    + " does not have " + ele + " elevation");
        }
    }

    
    private static boolean checkAngle(double min, double max, double angle) {
        
        if (min < max) {
            return (angle >= min && angle <= max);
        } else {
            if (min - angle < 0) {
                return angle >= min;
            } else
                return angle <= max;

        }

    }
    
    /**
     * Helping method, calculates angle between two sides of triangle
     * 
     * @param a
     *            length of the adjacent side
     * @param h
     *            length of the hypontenuse
     * @return angle in degrees
     */
    private static int getAngle(double a, double h) {

        double angle = Math.acos(a / h);
        return (int) Math.toDegrees(angle);

    }
    
    private static double round(double value, int decimal) {
        double pow = Math.pow(10, decimal);
        
        value *= pow;
        
        value = Math.round(value);
        
        return value / pow;
    }
    
}
