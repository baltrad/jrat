/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.proj.VincentyFormulas;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import pl.imgw.jrat.tools.out.XMLHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OverlappingCoords {

    private static final String COORDSFILE = "coords.xml";

    private static final String ROOT = "pairs";
    private static final String PAIR = "pair";
    private static final String R1LAT = "r1lat";
    private static final String R1LON = "r1lon";
    private static final String R1BINS = "r1bins";
    private static final String R1SCALE = "r1scale";
    private static final String R2LAT = "r2lat";
    private static final String R2LON = "r2lon";
    private static final String R2BINS = "r2bins";
    private static final String R2SCALE = "r2scale";
    private static final String ELEVATION = "elevation";
    private static final String DISTANCE = "distance";
    private static final String POINT = "point";
    private static final String R1BIN = "r1bin";
    private static final String R1RAY = "r1ray";
    private static final String R2BIN = "r2bin";
    private static final String R2RAY = "r2ray";
    private static final String ID = "id";

    private List<RayBin> rayBins = new ArrayList<RayBin>();
    private int id = -1;

    private Pair pair;
    private double elevation = 0;
    private int distance = 0;

    public boolean valid = false;

    private String getXMLPath() {

        String pairsName = pair.getVol1().getSiteName()
                + pair.getVol2().getSiteName();

        String distele = distance + "_" + elevation;

        String folder = "calid/overlapping/" + pairsName + "/" + distele;

        return new File(folder, COORDSFILE).getPath();
    }

    /**
     * @param pair
     * @param elevation
     * @param distance
     */

    public OverlappingCoords(Pair pair, double elevation, int distance) {

        if (pair.isValid()) {
            this.pair = pair;
        } else
            return;

        if (elevation > 0 && distance > 0) {
            this.distance = distance;
            this.elevation = elevation;
        } else
            return;

        valid = true;

    }

    /**
     * helping method, should be used when loading data from file fails
     * 
     * @return
     */
    public boolean calculateMatchingPoints() {
        if (!valid)
            return false;

        Point2D.Double r1coords = new Point2D.Double(pair.getVol1().getLon(),
                pair.getVol1().getLat());
        Point2D.Double r2coords = new Point2D.Double(pair.getVol2().getLon(),
                pair.getVol2().getLat());

        int raddist = 0;
        Double dist = VincentyFormulas.dist(r1coords, r2coords);
        if(dist != null)
            raddist = (int) (dist / 2);

        int bins1 = pair.getVol1().getScan(elevation).getNBins();
        int bins2 = pair.getVol2().getScan(elevation).getNBins();
        
//        System.out.println(pair.getVol1().getSiteName() + " " + pair.getVol2().getSiteName());
//        System.out.println(raddist + "m, " + bins1 * pair.getVol1().getScan(elevation).getRScale());
        
        if (raddist > bins1 * pair.getVol1().getScan(elevation).getRScale()
                || raddist > bins2
                        * pair.getVol2().getScan(elevation).getRScale()) {
            LogHandler.getLogs().displayMsg(
                    "Radars are to far from each other"
                            + " to find overlapping points", Logging.WARNING);
            return false;
        }
        
        /*
        
        double rad1bearing = VincentyFormulas.getBearing(r1coords, r2coords);
        double rad2bearing = VincentyFormulas.getBearing(r2coords, r1coords);

        if (rad1bearing < 0) {
            rad1bearing = 360 + rad1bearing;
        }
        if (rad2bearing < 0) {
            rad2bearing = 360 + rad2bearing;
        }

        int rad1startray = (int) (rad1bearing - 90) - 1;
        int rad1endray = (int) (rad1bearing + 90) + 1;
        int rad2startray = (int) (rad2bearing - 90) - 1;
        int rad2endray = (int) (rad2bearing + 90) + 1;

        if (rad1startray < 0) {
            rad1startray = 360 + rad1startray;
        }
        if (rad2startray < 0) {
            rad2startray = 360 + rad2startray;
        }
        if (rad1endray > 360) {
            rad1endray = rad1endray - 360;
        }
        if (rad2endray > 360) {
            rad2endray = rad2endray - 360;
        }
        for (int b1 = raddist; b1 < bins1; b1++) {
            for (int b2 = raddist; b2 < bins2; b2++) {
                if (b1 != b2)
                    continue;
                for (int r1 = rad1startray; r1 != rad1endray; r1 = (r1 + 1) % 360) {
                    for (int r2 = rad2startray; r2 != rad2endray; r2 = (r2 + 1) % 360) {
                        double calculatedDist1 = Math.cos(Math
                                .toRadians(elevation)) * (b1 + 0.5) * scale1;
                        Point2D.Double p1 = VincentyFormulas.dest(r1coords, r1,
                                calculatedDist1);
                        double calculatedDist2 = Math.cos(Math
                                .toRadians(elevation)) * (b2 + 0.5) * scale2;
                        Point2D.Double p2 = VincentyFormulas.dest(r2coords, r2,
                                calculatedDist2);
                        double calculatedDist = VincentyFormulas.dist(p1, p2);
                        if (calculatedDist < distance) {
                            // System.out.println(r1coords + " " + r1 + " " +
                            // calculatedDist1);
                            // System.out.println(r2coords + " " + r2 + " " +
                            // calculatedDist2);
                            // System.out.println("Calculated dist=" +
                            // calculatedDist);
                            RayBinData rb = new RayBinData(r1, b1, r2, b2);
                            rb.setCoord1(p1);
                            rb.setCoord2(p2);
                            rayBins.add(rb);

                        }
                    }
                }
            }
        }
        // System.out.println("pasujacych: " + rayBins.size());
        saveToFile();
        */
        return true;
    }

    /**
     * helping method
     */
    public int[] getMatchingPointsData(VolumeContainer vol1,
            VolumeContainer vol2) {

        int data[] = new int[rayBins.size()];
        int i = 0;
        Iterator<RayBin> itr = rayBins.iterator();
        // List<RayBinData> rbdlist = new ArrayList<RayBinData>();
        while (itr.hasNext()) {
            RayBinData rbd = (RayBinData) itr.next();
            int r1 = rbd.getRay1();
            int r2 = rbd.getRay2();
            int b1 = rbd.getBin1();
            int b2 = rbd.getBin2();
            int data1 = vol1.getScan(elevation).getArray()
                    .getRawIntPoint(r1, b1);
            int data2 = vol2.getScan(elevation).getArray()
                    .getRawIntPoint(r2, b2);
            if (data1 > 80 || data2 > 80) {
                data[i] = data1 - data2;
            }
            i++;
        }
        return data;
    }

    /**
     * helping method
     */
    private void saveToFile() {
        //
        // if (rayBins.isEmpty()) {
        // return;
        // }
        
        /*
        
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();

        } catch (ParserConfigurationException e) {
            LogHandler.getLogs().displayMsg(
                    "Error while creating XML document object", Logging.ERROR);
        }
        Element root = doc.createElement(ROOT);

        HashSet<Integer> ids = new HashSet<Integer>();
        Document oldDoc = XMLHandler.loadXML(getXMLPath());
        if (oldDoc != null && oldDoc.hasChildNodes()) {
            NodeList list = oldDoc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().matches(PAIR)) {
                    Node oldPair = doc.importNode(list.item(i), true);
                    String id = XMLHandler.getAttributeValue(oldPair, ID);

                    try {
                        ids.add(Integer.parseInt(id));
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    root.appendChild(oldPair);
                }
            }
        }

        Element pair = doc.createElement(PAIR);
        int id = getNewId(ids);

        pair.setAttribute(ID, String.valueOf(id));

        pair.setAttribute(ELEVATION, String.valueOf(elevation));
        pair.setAttribute(DISTANCE, String.valueOf(distance));

        pair.setAttribute(R1LON, String.valueOf(r1coords.x));
        pair.setAttribute(R1LAT, String.valueOf(r1coords.y));
        
        pair.setAttribute(R1BINS, String.valueOf(bins1));
        pair.setAttribute(R1SCALE, String.valueOf(scale1));

        pair.setAttribute(R2LON, String.valueOf(r2coords.x));
        pair.setAttribute(R2LAT, String.valueOf(r2coords.y));
        pair.setAttribute(R2BINS, String.valueOf(bins2));
        pair.setAttribute(R2SCALE, String.valueOf(scale2));

        Iterator<RayBin> itr = rayBins.iterator();
        while (itr.hasNext()) {
            RayBin rb = itr.next();
            Element raybin = doc.createElement(POINT);
            raybin.setAttribute(R1LON, String.valueOf(rb.getCoord1().x));
            raybin.setAttribute(R1LAT, String.valueOf(rb.getCoord1().y));
            raybin.setAttribute(R1BIN, String.valueOf(rb.getBin1()));
            raybin.setAttribute(R1RAY, String.valueOf(rb.getRay1()));
            raybin.setAttribute(R2LAT, String.valueOf(rb.getCoord2().y));
            raybin.setAttribute(R2LON, String.valueOf(rb.getCoord2().x));
            raybin.setAttribute(R2BIN, String.valueOf(rb.getBin2()));
            raybin.setAttribute(R2RAY, String.valueOf(rb.getRay2()));
            pair.appendChild(raybin);
        }

        root.appendChild(pair);

        doc.appendChild(root);

        XMLHandler.saveXMLFile(doc, getXMLPath());
        */
    }

    /**
     * @param ids
     * @return
     */
    private int getNewId(HashSet<Integer> ids) {
        int i = 0;
        while (ids.contains(i)) {
            i++;
        }

        return i;
    }

    /**
     * helping method, returns false if loading data from file fails
     * 
     * @return
     */
    private boolean loadFromFile() {

        /*
        
        boolean found = false;

        Document oldDoc = XMLHandler.loadXML(getXMLPath());
        if (oldDoc != null && oldDoc.hasChildNodes()) {
            NodeList list = oldDoc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {

                if (list.item(i).getNodeName().matches(PAIR)) {
                    Node node = list.item(i);
                    String x = XMLHandler.getAttributeValue(node, R1LON);
                    String y = XMLHandler.getAttributeValue(node, R1LAT);
                    Point2D.Double p1;
                    Point2D.Double p2;
                    double s1;
                    double s2;
                    double ele;
                    int dist;
                    try {
                        p1 = new Point2D.Double(Double.parseDouble(x),
                                Double.parseDouble(y));
                        x = XMLHandler.getAttributeValue(node, R2LON);
                        y = XMLHandler.getAttributeValue(node, R2LAT);
                        p2 = new Point2D.Double(Double.parseDouble(x),
                                Double.parseDouble(y));
                        s1 = Double.parseDouble(XMLHandler.getAttributeValue(
                                node, R1SCALE));
                        s2 = Double.parseDouble(XMLHandler.getAttributeValue(
                                node, R2SCALE));
                        ele = Double.parseDouble(XMLHandler.getAttributeValue(
                                node, ELEVATION));
                        dist = (int) Double.parseDouble(XMLHandler
                                .getAttributeValue(node, DISTANCE));
                        id = Integer.parseInt(XMLHandler.getAttributeValue(
                                node, ID));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    if (ele != elevation)
                        continue;
                    if (dist != distance)
                        continue;
                    if (p1.equals(r1coords) && p2.equals(r2coords)) {
                        if (s1 != scale1)
                            continue;
                        if (s2 != scale2)
                            continue;
                        found = true;
                        NodeList coords = list.item(i).getChildNodes();
                        for (int c = 0; c < coords.getLength(); c++) {
                            if (coords.item(c).getNodeName().matches(POINT)) {
                                try {
                                    x = XMLHandler.getAttributeValue(node,
                                            R1LON);
                                    y = XMLHandler.getAttributeValue(node,
                                            R1LAT);
                                    Point2D.Double r1p = new Point2D.Double(
                                            Double.parseDouble(x),
                                            Double.parseDouble(y));
                                    x = XMLHandler.getAttributeValue(node,
                                            R2LON);
                                    y = XMLHandler.getAttributeValue(node,
                                            R2LAT);
                                    Point2D.Double r2p = new Point2D.Double(
                                            Double.parseDouble(x),
                                            Double.parseDouble(y));
                                    int r1bin = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R1BIN));
                                    int r1ray = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R1RAY));
                                    int r2bin = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R2BIN));
                                    int r2ray = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R2RAY));
                                    RayBinData rbd = new RayBinData(r1ray,
                                            r1bin, r2ray, r2bin);
                                    rbd.setCoord1(r1p);
                                    rbd.setCoord2(r2p);
                                    rayBins.add(rbd);

                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }

                        }
                        if (!rayBins.isEmpty())
                            return true;
                    }
                    if (p1.equals(r2coords) && p2.equals(r1coords)) {
                        if (s1 != scale2)
                            continue;
                        if (s2 != scale1)
                            continue;
                        found = true;
                        NodeList coords = list.item(i).getChildNodes();
                        for (int c = 0; c < coords.getLength(); c++) {
                            if (coords.item(c).getNodeName().matches(POINT)) {
                                try {
                                    x = XMLHandler.getAttributeValue(node,
                                            R1LON);
                                    y = XMLHandler.getAttributeValue(node,
                                            R1LAT);
                                    Point2D.Double r1p = new Point2D.Double(
                                            Double.parseDouble(x),
                                            Double.parseDouble(y));
                                    x = XMLHandler.getAttributeValue(node,
                                            R2LON);
                                    y = XMLHandler.getAttributeValue(node,
                                            R2LAT);
                                    Point2D.Double r2p = new Point2D.Double(
                                            Double.parseDouble(x),
                                            Double.parseDouble(y));

                                    int r1bin = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R1BIN));
                                    int r1ray = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R1RAY));
                                    int r2bin = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R2BIN));
                                    int r2ray = Integer.parseInt(XMLHandler
                                            .getAttributeValue(coords.item(c),
                                                    R2RAY));
                                    RayBinData rbd = new RayBinData(r1ray,
                                            r1bin, r2ray, r2bin);
                                    rbd.setCoord1(r1p);
                                    rbd.setCoord2(r2p);
                                    rayBins.add(rbd);
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }

                        }
                        if (!rayBins.isEmpty())
                            return true;
                    }

                }
            }
        }
        return found;
        */
        return false;
    }

    public int getId() {
        return id;
    }

    public static void main(String[] args) {

    }

}