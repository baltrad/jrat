/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.comp;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.MainJRat;
import pl.imgw.jrat.data.hdf5.OdimH5Dataset;
import pl.imgw.jrat.data.hdf5.RadarVolume;
import pl.imgw.jrat.proj.VincentyFormulas;
import pl.imgw.jrat.util.LogsHandler;
import pl.imgw.jrat.util.MessageLogger;
import pl.imgw.jrat.util.XMLHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MatchingPoints {

    private static final String MATCHFILE = "matching.xml";

    private static final String DEG = "deg";
    private static final String M = "m";

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
    private static final String COORDS = "coords";
    private static final String R1BIN = "r1bin";
    private static final String R1RAY = "r1ray";
    private static final String R2BIN = "r2bin";
    private static final String R2RAY = "r2ray";

    private Point2D.Double r1coords = null;
    private Point2D.Double r2coords = null;

    private List<RayBin> rayBins = new ArrayList<RayBin>();

    private double elevation = 0;
    private int distance = 0;

    private Integer bins1;
    private Integer rays1;
    private Double scale1;
    private Integer bins2;
    private Integer rays2;
    private Double scale2;



    private String getXMLPath() {
        return new File(MainJRat.getProgPath(), MATCHFILE).getPath();
    }

    /**
     * Setting mandatory fields from volume data and receiving matching points
     * coordinates
     * 
     * @param vol1
     * @param vol2
     * @param elevation
     *            scan elevation in degrees
     * @param threshold
     *            distance between points in meters
     * @return
     */
    public boolean initialize(RadarVolume vol1, RadarVolume vol2, double elevation, int distance) {

        this.elevation = elevation;
        this.distance = distance;
        OdimH5Dataset dataset1 = vol1.getDataset(elevation);
        OdimH5Dataset dataset2 = vol2.getDataset(elevation);

        double r1height = vol1.getHeight();
        double r2height = vol2.getHeight();

        if (Math.abs(r1height - r2height) > distance) {
            MessageLogger.showMessage(
                    "Warning! Vertical level difference bigger then "
                            + distance, true);
        }

        if (dataset1 == null || dataset2 == null)
            return false;

        if (!vol1.getFullDate().matches(vol2.getFullDate())) {
            MessageLogger.showMessage("Warning! Scan times dont match", true);
        }

        bins1 = dataset1.getNbins();
        rays1 = dataset1.getNrays();
        if (rays1 != 360)
            return false;
        scale1 = dataset1.getRscale();

        bins2 = dataset2.getNbins();
        rays2 = dataset2.getNrays();
        if (rays2 != 360)
            return false;
        scale2 = dataset2.getRscale();

        r1coords = new Point2D.Double(vol1.getLon(), vol1.getLat());
        r2coords = new Point2D.Double(vol2.getLon(), vol2.getLat());

        if (rayBins.isEmpty() || !loadFromFile()) {
            calculateMatchingPoints();
        }
        if (rayBins.isEmpty()) {
            MessageLogger.showMessage("No overlapping points found, "
                    + "try different parameters.", true);
            return false;
        }

        // System.out.println("pasujacych punktow: " + rayBin.size());

        return true;
    }

    /**
     * helping method, should be used when loading data from file fails
     * 
     * @return
     */
    private void calculateMatchingPoints() {

        int raddist = (int) (VincentyFormulas.dist(r1coords, r2coords) / 2000);
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
                            RayBin rb = new RayBin(b1, r1, b2, r2);
                            rayBins.add(rb);

                        }
                    }
                }
            }
        }
        // System.out.println("pasujacych: " + matches.getSize());
        saveToFile();
    }

    /**
     * helping method
     */
    public List<RayBinData> getDataFromVolumes(RadarVolume vol1, RadarVolume vol2) {

        List<RayBinData> rayBinDatas = new ArrayList<RayBinData>();
        Iterator<RayBin> itr = rayBins.iterator();
        while (itr.hasNext()) {
            RayBin rb = itr.next();
            int r1 = rb.getRay1();
            int r2 = rb.getRay2();
            int b1 = rb.getBin1();
            int b2 = rb.getBin2();
            double data1 = vol1.getDataset(elevation).getValue(0, r1, b1);
            double data2 = vol2.getDataset(elevation).getValue(0, r2, b2);
            if (data1 > -32 || data2 > -32) {
                RayBinData rbd = new RayBinData(r1, b1, r2, b2);
                rbd.setData1(data1);
                rbd.setData2(data2);
                double calculatedDist1 = Math.cos(Math.toRadians(elevation))
                        * (b1 + 0.5) * scale1;
                Point2D.Double p1 = VincentyFormulas.dest(r1coords, r1,
                        calculatedDist1);
                double calculatedDist2 = Math.cos(Math.toRadians(elevation))
                        * (b2 + 0.5) * scale2;
                Point2D.Double p2 = VincentyFormulas.dest(r2coords, r2,
                        calculatedDist2);
                rbd.setCoord1(p1);
                rbd.setCoord2(p2);
                rayBinDatas.add(rbd);
            }
        }
        return rayBinDatas;
    }

    /**
     * helping method
     */
    private void saveToFile() {

        if (rayBins.isEmpty()) {
            return;
        }
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();

        } catch (ParserConfigurationException e) {
            LogsHandler
                    .saveProgramLogs("Error while creating XML document object: "
                            + e.getMessage());
        }
        Element root = doc.createElement(ROOT);
        Element pair = doc.createElement(PAIR);
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
            Element raybin = doc.createElement(COORDS);
            raybin.setAttribute(R1BIN, String.valueOf(rb.getBin1()));
            raybin.setAttribute(R1RAY, String.valueOf(rb.getRay1()));
            raybin.setAttribute(R2BIN, String.valueOf(rb.getBin2()));
            raybin.setAttribute(R2RAY, String.valueOf(rb.getRay2()));
            pair.appendChild(raybin);
        }

        root.appendChild(pair);
        Document oldDoc = XMLHandler.loadXML(getXMLPath());
        if (oldDoc != null && oldDoc.hasChildNodes()) {
            NodeList list = oldDoc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().matches(PAIR)) {
                    Node oldPair = doc.importNode(list.item(i), true);
                    root.appendChild(oldPair);
                }
            }
        }
        doc.appendChild(root);

        XMLHandler.saveXMLFile(doc, getXMLPath());
    }

    /**
     * helping method, returns false if loading data from file fails
     * 
     * @return
     */
    private boolean loadFromFile() {

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
                        NodeList coords = list.item(i).getChildNodes();
                        for (int c = 0; c < coords.getLength(); c++) {
                            if (coords.item(c).getNodeName().matches(COORDS)) {
                                try {
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
                                    rayBins.add(new RayBin(r1ray, r1bin, r2ray,
                                            r2bin));
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
                        NodeList coords = list.item(i).getChildNodes();
                        for (int c = 0; c < coords.getLength(); c++) {
                            if (coords.item(c).getNodeName().matches(COORDS)) {
                                try {
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
                                    rayBins.add(new RayBin(r2ray, r2bin, r1ray,
                                            r1bin));
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
        return false;
    }

    public static void main(String[] args) {

    }

}
