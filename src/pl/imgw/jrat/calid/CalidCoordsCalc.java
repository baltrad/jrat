/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import pl.imgw.jrat.proj.VincentyFormulas;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidCoordsCalc {

    private int maxRange;
    private CalidContainer cc;
    
    public CalidCoordsCalc(CalidContainer cc, int maxRange) {
        this.cc = cc;
        this.maxRange = maxRange;
    }
    
    /**
     * helping method, should be used when loading data from file fails
     * 
     * @return
     */
    public boolean calculateCoords() {
        
        if (!cc.hasVolumeData()) {
            LogHandler.getLogs().displayMsg(
                    "CALID: no valid volumes to process", Logging.WARNING);
            return false;
        }

        LogHandler.getLogs().displayMsg(
                "CALID: Calculating overlapping points coordinates for: "
                        + cc.getPair().getSource1() + " and "
                        + cc.getPair().getSource2(), LogHandler.NORMAL);
        
        ArrayList<PairedPoints> pairedPointsList = new ArrayList<PairedPoints>();

        double ele = cc.getVerifiedElevation();


        Point2D.Double r1coords = new Point2D.Double(cc.getPair().getVol1()
                .getLon(), cc.getPair().getVol1().getLat());
        Point2D.Double r2coords = new Point2D.Double(cc.getPair().getVol2()
                .getLon(), cc.getPair().getVol2().getLat());

        int radHalfDist = 0;
        
        Double dist = VincentyFormulas.dist(r1coords, r2coords);
        if (dist != null)
            radHalfDist = (int) (dist / 2000);

        double r1binScale = cc.getPair().getVol1().getScan(ele).getRScale();
        double r2binScale = cc.getPair().getVol2().getScan(ele).getRScale();

        int radarRange1 = (int) (cc.getPair().getVol1().getScan(ele).getNBins()
                * r1binScale / 1000);
        int radarRange2 = (int) (cc.getPair().getVol2().getScan(ele).getNBins()
                * r2binScale / 1000);

//        System.out.println("skalowanie: " + r1binScale + " " + r2binScale);
//        System.out.println("pol odleglosci radaru: " + radHalfDist);
//        System.out.println("zasieg radaru: " + radarRange1 + " " + radarRange2);
        
        radarRange1 = (radarRange1 > maxRange) ? maxRange : radarRange1;
        radarRange2 = (radarRange2 > maxRange) ? maxRange : radarRange2;
        

        
        int r1bin0 = (int) (radHalfDist / (r1binScale / 1000));
        int r2bin0 = (int) (radHalfDist / (r2binScale / 1000));
        int r1binMax = (int) (radarRange1 / (r1binScale / 1000));
        int r2binMax = (int) (radarRange2 / (r2binScale / 1000));
        /*===========================================================*/
        
        if (radHalfDist > radarRange1 || radHalfDist > radarRange2) {
            LogHandler.getLogs().displayMsg(
                    "CALID: Radars are too far from each other"
                            + " and have no overlapping points",
                    Logging.WARNING);
            CalidFileHandler.saveCoords(cc);
            return false;
        }

        int r1nrays = cc.getPair().getVol1().getScan(ele).getNRays();
        int r2nrays = cc.getPair().getVol2().getScan(ele).getNRays();
        
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
        int r1rayMax = (int) (rad1bearing + getAngle(radHalfDist, radarRange1)) + 2;
        int r2ray0 = (int) (rad2bearing - getAngle(radHalfDist,
                radarRange2)) - 2;
        int r2rayMax = (int) (rad2bearing + getAngle(radHalfDist, radarRange2)) + 2;

        if (r1ray0 < 0) {
            r1ray0 = 360 + r1ray0;
        }
        if (r2ray0 < 0) {
            r2ray0 = 360 + r2ray0;
        }
        if (r1rayMax > 360) {
            r1rayMax = r1rayMax - 360;
        }
        if (r2rayMax > 360) {
            r2rayMax = r2rayMax - 360;
        }

//
//        System.out.println("bin0, binmax " + r1bin0 + " " + r2bin0 + " " + r1binMax + " "
//                + r2binMax);
//        
//        
//        System.out.println("ray0 i raymax " + r1ray0 + " " + r2ray0 + " " + r1rayMax + " "
//                + r2rayMax + " " + r1rayScale + " " + r2rayScale);
//        System.out.println("nrays " + r1nrays + " " + r2nrays);

        double a1, a2;
        
        for (int b1 = r1bin0; b1 < r1binMax; b1++) {
            for (int b2 = r2bin0; b2 < r2binMax; b2++) {

                // System.out.println(b1 + " koniec: " + radarRange1);
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
                        double calculatedDist = VincentyFormulas.dist(p1, p2);
                        if (calculatedDist < cc.getParsedParameters()
                                .getDistance()) {
                            RayBinData rb = new RayBinData(r1, b1, r2, b2);
                            rb.setCoord1(p1);
                            rb.setCoord2(p2);

                            pairedPointsList.add(rb);
                        }
                    }
                }
            }
        }
        // CalidCoords[] rb = rayBins.toArray(new CalidCoords[0]);

        cc.setPairedPointsList(pairedPointsList);
        CalidFileHandler.saveCoords(cc);
        return true;
    }

    
    private boolean checkAngle(double min, double max, double angle) {
        
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

    public static void main(String[] args) {
        CalidCoordsCalc ccc = new CalidCoordsCalc(null, 0);
        double min = 300;
        double max = 90;
        double angle = 1;
        
        System.out.println(ccc.checkAngle(min, max, angle));
    }
    
    private double round(double value, int decimal) {
        double pow = Math.pow(10, decimal);
        
        value *= pow;
        
        value = Math.round(value);
        
        return value / pow;
    }
    
}
