/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.tools.out.XMLHandler;
import pl.imgw.util.Log;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidCoordsLoader extends CalidDataHandler {


//    public static List<PairedPoint> loadCoords(CalidParameters params)
//            throws CalidException {
//        File xmlfile = getCoordsPath(params);
//        return loadCoords(xmlfile, null);
//
//    }

    /**
     * 
     * @param params
     * @param pair
     * @return <b>null</b> if could not find coordinates file
     * @throws CalidException
     */
    public static List<PairedPoint> loadCoords(CalidParameters params,
            RadarsPair pair) throws CalidException {
        File xmlfile = getCoordsPath(params, pair);
        if (!xmlfile.exists())
            return null;
        return loadCoords(xmlfile, pair);

    }

    /**
     * Loading coordinates from file, prints information messages
     * 
     * @param xmlfile
     * @return
     * @throws CalidException
     *             if file is not valid xml format
     */
    protected static List<PairedPoint> loadCoords(File xmlfile,
            RadarsPair pair) throws CalidException {

        Document oldDoc = XMLHandler.loadXML(xmlfile);

        if (oldDoc == null) {
            throw new CalidException("Cannot load coordinates for " + pair);
        }

        if (!oldDoc.hasChildNodes()
                || !oldDoc.getChildNodes().item(0).getNodeName().matches(PAIR)) {
            throw new CalidException("Cannot load coordinates for " + pair
                    + ", " + xmlfile + " is not a valid XML format");
        }

        log.printMsg("Loading coordinates from file: " + xmlfile,
                Log.TYPE_NORMAL, Log.MODE_VERBOSE);

        ArrayList<PairedPoint> pairedPointsList = new ArrayList<PairedPoint>();

        Node node = oldDoc.getChildNodes().item(0);
        NodeList coords = node.getChildNodes();

        String x = "";
        String y = "";

        if (pair != null && pair instanceof PolarVolumesPair) {
            if (!validateSiteCoordinates((PolarVolumesPair) pair, node)) {
                throw new CalidException(pair
                        + " coordinates have been changed");
            }
        }

        for (int c = 0; c < coords.getLength(); c++) {
            try {
                if (coords.item(c).getNodeName().matches(POINT)) {
                    x = XMLHandler.getAttributeValue(coords.item(c), R1LON);
                    y = XMLHandler.getAttributeValue(coords.item(c), R1LAT);

                    Point2D.Double r1p = new Point2D.Double(
                            Double.parseDouble(x), Double.parseDouble(y));
                    x = XMLHandler.getAttributeValue(coords.item(c), R2LON);
                    y = XMLHandler.getAttributeValue(coords.item(c), R2LAT);
                    Point2D.Double r2p = new Point2D.Double(
                            Double.parseDouble(x), Double.parseDouble(y));
                    int r1bin = Integer.parseInt(XMLHandler.getAttributeValue(
                            coords.item(c), R1BIN));
                    int r1ray = Integer.parseInt(XMLHandler.getAttributeValue(
                            coords.item(c), R1RAY));
                    int r2bin = Integer.parseInt(XMLHandler.getAttributeValue(
                            coords.item(c), R2BIN));
                    int r2ray = Integer.parseInt(XMLHandler.getAttributeValue(
                            coords.item(c), R2RAY));
                    PairedPoint point = new PairedPoint(r1ray, r1bin, r2ray,
                            r2bin);
                    point.setCoord1(r1p);
                    point.setCoord2(r2p);
                    pairedPointsList.add(point);
                }

            } catch (NumberFormatException e) {
                throw new CalidException("Cannot parse coordinates from: "
                        + xmlfile + ", while parsing: " + e.getMessage());
            }
        }

        return pairedPointsList;

    }

    /**
     * @param pair
     * @param node
     */
    private static boolean validateSiteCoordinates(PolarVolumesPair pair,
            Node node) throws CalidException {
        String x;
        String y;
        Point2D.Double p1;
        Point2D.Double p2;
        x = XMLHandler.getAttributeValue(node, R1LON);
        y = XMLHandler.getAttributeValue(node, R1LAT);
        p1 = new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));
        x = XMLHandler.getAttributeValue(node, R2LON);
        y = XMLHandler.getAttributeValue(node, R2LAT);
        p2 = new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));

        Point2D.Double r1coords = new Point2D.Double(pair.getVol1().getLon(),
                pair.getVol1().getLat());
        Point2D.Double r2coords = new Point2D.Double(pair.getVol2().getLon(),
                pair.getVol2().getLat());

        if (!p1.equals(r1coords) || !p2.equals(r2coords)) {
            log.printMsg("Radar coordinates have been changed",
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
            return false;
        }
        return true;
    }

}
