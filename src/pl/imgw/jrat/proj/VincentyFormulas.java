/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.proj;

import java.awt.geom.Point2D;

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/* Vincenty Direct and Inverse Solution of Geodesics on the Ellipsoid                             */
/* Translated form Chris Veness's Java Script functions                                          */
/* http://www.movable-type.co.uk/scripts/latlong-vincenty-direct.html                             */
/* from: Vincenty direct formula - T Vincenty, "Direct and Inverse Solutions of Geodesics on the  */
/*       Ellipsoid with application of nested equations", Survey Review, vol XXII no 176, 1975    */
/*       http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf                                             */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

/**
 * 
 * Vincenty Direct and Inverse Solution of Geodesics on the Ellipsoid
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class VincentyFormulas {

    // WGS-84 ellipsoid:
    public final static double ELIPSE_A = 6378137;

    public final static double ELIPSE_B = 6356752.3142;

    public final static double ELIPSE_F = 1 / 298.257223563;

    /**
     * Calculates geodetic distance between two points specified by
     * latitude/longitude using Vincenty inverse formula for ellipsoids
     * 
     * @param p1
     *            GnuplotCoordinates of the first point
     * @param p2
     *            GnuplotCoordinates of the second point
     * @return Distance in meters, null if formula failed, -1 if no co-incident points
     */
    public static Double dist(Point2D.Double p1, Point2D.Double p2) {

        if(p1 == null || p2 == null)
            return null;
        
        double lat1 = Math.toRadians(p1.y);
        double lon1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.y);
        double lon2 = Math.toRadians(p2.x);

        double L = (lon2 - lon1);
        double U1 = Math.atan((1 - ELIPSE_F) * Math.tan(lat1));
        double U2 = Math.atan((1 - ELIPSE_F) * Math.tan(lat2));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double lambda = L, lambdaP = 2 * Math.PI;
        double iterLimit = 20;

        double cosSqAlpha = 0, sinSigma = 0, cos2SigmaM = 0;
        double cosSigma = 0, sigma = 0, sinLambda = 0, cosLambda = 0;

        while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0) {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0) {
                return new Double(-1); // co-incident points
            }
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            // if (isNaN(cos2SigmaM))
            // cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (ďż˝6)
            double C = ELIPSE_F / 16 * cosSqAlpha
                    * (4 + ELIPSE_F * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L
                    + (1 - C)
                    * ELIPSE_F
                    * sinAlpha
                    * (sigma + C
                            * sinSigma
                            * (cos2SigmaM + C * cosSigma
                                    * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }
        if (iterLimit == 0)
            return null; // formula failed to converge

        double uSq = cosSqAlpha * (ELIPSE_A * ELIPSE_A - ELIPSE_B * ELIPSE_B)
                / (ELIPSE_B * ELIPSE_B);
        double A = 1 + uSq / 16384
                * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B
                * sinSigma
                * (cos2SigmaM + B
                        / 4
                        * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B
                                / 6 * cos2SigmaM
                                * (-3 + 4 * sinSigma * sinSigma)
                                * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        return (ELIPSE_B * A * (sigma - deltaSigma));
    }
    
    /**
     * 
     * Calculates destination point given start point lat/long, bearing &
     * distance, using Vincenty inverse formula for ellipsoids
     * 
     * @param p1
     *            first point coordinates (Point2D.Double) in decimal degrees
     * @param brng
     *            initial bearing in decimal degrees (0 points due North) and clockwise order
     * @param dist
     *            distance along bearing in meters
     * @return
     */
    public static Point2D.Double dest(Point2D.Double p1, double brng,
            double dist) {

        double s = dist;
        double alpha1 = Math.toRadians(brng);
        double sinAlpha1 = Math.sin(alpha1);
        double cosAlpha1 = Math.cos(alpha1);

        double tanU1 = (1 - ELIPSE_F) * Math.tan(Math.toRadians(p1.y));
        double cosU1 = 1 / Math.sqrt((1 + tanU1 * tanU1)), sinU1 = tanU1
                * cosU1;
        double sigma1 = Math.atan2(tanU1, cosAlpha1);
        double sinAlpha = cosU1 * sinAlpha1;
        double cosSqAlpha = 1 - sinAlpha * sinAlpha;
        double uSq = cosSqAlpha * (ELIPSE_A * ELIPSE_A - ELIPSE_B * ELIPSE_B)
                / (ELIPSE_B * ELIPSE_B);
        double A = 1 + uSq / 16384
                * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double sigma = s / (ELIPSE_B * A);
        double sigmaP = 2 * Math.PI;
        double cos2SigmaM = 0, sinSigma = 0, cosSigma = 0;
        while (Math.abs(sigma - sigmaP) > 1e-12) {
            cos2SigmaM = Math.cos(2 * sigma1 + sigma);
            sinSigma = Math.sin(sigma);
            cosSigma = Math.cos(sigma);
            double deltaSigma = B
                    * sinSigma
                    * (cos2SigmaM + B
                            / 4
                            * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B
                                    / 6
                                    * cos2SigmaM
                                    * (-3 + 4 * sinSigma * sinSigma)
                                    * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
            sigmaP = sigma;
            sigma = s / (ELIPSE_B * A) + deltaSigma;
        }

        double tmp = sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1;
        double lat2 = Math.atan2(sinU1 * cosSigma + cosU1 * sinSigma
                * cosAlpha1,
                (1 - ELIPSE_F) * Math.sqrt(sinAlpha * sinAlpha + tmp * tmp));
        double lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma
                - sinU1 * sinSigma * cosAlpha1);
        double C = ELIPSE_F / 16 * cosSqAlpha
                * (4 + ELIPSE_F * (4 - 3 * cosSqAlpha));
        double L = lambda
                - (1 - C)
                * ELIPSE_F
                * sinAlpha
                * (sigma + C
                        * sinSigma
                        * (cos2SigmaM + C * cosSigma
                                * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        double lon2 = (Math.toRadians(p1.x) + L + 3 * Math.PI) % (2 * Math.PI)
                - Math.PI; // normalise to -180...+180

        // double revAz = Math.atan2(sinAlpha, -tmp); // final bearing, if
        // required

        return new Point2D.Double(Math.toDegrees(lon2), Math.toDegrees(lat2));
    }

    /**
     * 
     * @param p1
     * @param p2
     * @return
     */
    public static Double getBearing (Point2D.Double p1, Point2D.Double p2) {

        double lat1 = Math.toRadians(p1.y);
        double lon1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.y);
        double lon2 = Math.toRadians(p2.x);
//        double dLat = (lat2 - lat1);
        double dLon = (lon2 - lon1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        return Math.toDegrees(Math.atan2(y, x));
    
    }
    
    public static void main(String[] args) {

        double x = 17.3421;
        double y = 50.486873;
        double b = 45;
        double d = 141000;

        Point2D.Double r = dest(new Point2D.Double(x, y), b, d);

        double dist = dist(r, new Point2D.Double(x, y));
        double kat = getBearing(new Point2D.Double(x, y), r);

        System.out.println("Wynik:\nx=" + r.x + "\ny=" + r.y + "\ndist=" + dist
                + "\nangle=" + kat);
        //  double radar = 142;
//        double dec = (450 - radar) % 360;
//        System.out.println("dec = " + dec + ", rad = " + Math.toRadians(dec));
    }

}
