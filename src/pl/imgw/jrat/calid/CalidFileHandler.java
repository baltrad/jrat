/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.AplicationConstans.ETC;
import static pl.imgw.jrat.calid.CalidParsedParameters.*;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

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
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
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
public class CalidFileHandler {

    private static final String COORDSFILE = "coords.xml";
    private static final String RESULTSFILE = "results";

    private static final String PAIR = "pair";
    private static final String R1LAT = "r1lat";
    private static final String R1LON = "r1lon";
    private static final String R1BINS = "r1bins";
    private static final String R1SCALE = "r1scale";
    private static final String R2LAT = "r2lat";
    private static final String R2LON = "r2lon";
    private static final String R2BINS = "r2bins";
    private static final String R2SCALE = "r2scale";
    private static final String POINT = "point";
    private static final String R1BIN = "r1bin";
    private static final String R1RAY = "r1ray";
    private static final String R2BIN = "r2bin";
    private static final String R2RAY = "r2ray";
    private static final String SIZE = "size";

    public static final SimpleDateFormat CALID_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
    public static final SimpleDateFormat CALID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final String NULL = "n";

//    private int id = -1;
    
    private static String getCoordsPath(CalidContainer cc) {
        return new File(getCalidPath(cc), COORDSFILE).getPath();
    }

    private static String getResultsPath(CalidContainer cc, Date date) {
        return new File(getCalidPath(cc), CALID_DATE_FORMAT.format(date) + "."
                + RESULTSFILE).getPath();
    }

    /**
     * Receives path name to CALID results folder specified by given parameters,
     * different for every pair, distance, elevation and reflectivity
     * 
     * @param pair
     * @param distance
     * @param elevation
     * @param reflectivity
     * @return
     */
    public static String getCalidPath(CalidContainer cc) {

        Double elevation = cc.getVerifiedElevation();
        double reflectivity = cc.getParsedParameters().getReflectivity();
        int distance = cc.getParsedParameters().getDistance();
        
        String pairsName = cc.getPair().getSource1()
                + cc.getPair().getSource2();

        String distele = distance + "_" + elevation + "_" + reflectivity;

        String folder = "calid/" + pairsName + "/" + distele;

        if(ETC.isEmpty()) {
            new File(folder).mkdirs();
            return new File(folder).getPath();
        }
        new File(ETC, folder).mkdirs();

        return new File(ETC, folder).getPath();
    }
    
    /**
     * Receives path name to CALID root folder
     * @return
     */
    public static String getCalidPath() {
        return new File(ETC, "calid").getPath();
    }
    
    
    /**
     * 
     * Load results from default file.
     * 
     * @param results
     *            will be saved in cc
     * @param date
     *            specify exact date of the result
     * @return true if operation ends with success
     */
    public static boolean loadResults(CalidContainer cc, Date date) {
        File file = new File(getResultsPath(cc, date));
        return loadResults(file, cc, date);
    }
    
    /**
     * 
     * Load results from file.
     * 
     * @param file
     *            pointing results file
     * @param results
     *            will be saved in cc
     * @param date
     *            specify exact date of the result
     * @return true if operation ends with success
     */
    public static boolean loadResults(File file, CalidContainer cc, Date date){
        
        if (cc.getPairedPointsList().isEmpty()) {
            if (!loadCoords(cc))
                return false;
        }
        
        try {
            Scanner scan = new Scanner(file);
            
            LogHandler.getLogs().displayMsg(
                    "CALID: Loading results from file: " + file,
                    LogHandler.NORMAL);
            
            while (scan.hasNext()) {
                String line = scan.nextLine();
                if(line.startsWith("#")) {
                    continue;
                }                    
                String[] words = line.split(" ");
                if (words.length != cc.getPairedPointsList().size() + 1) {
                    continue;
                }
                Date dateRead = CALID_DATE_TIME_FORMAT.parse(words[0]);
                if (dateRead.equals(date)) {
                    for (int i = 1; i < words.length; i++) {
                        if (words[i].matches(NULL)) {
                            cc.getPairedPointsList().get(i - 1).setDifference(null);
                        } else
                            cc.getPairedPointsList().get(i - 1).setDifference(
                                    Double.parseDouble(words[i]));
                    }
                    cc.setHasResults(true);
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg("CALID: Results file not found: " + file , Logging.WARNING);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
        } catch (NumberFormatException e) {
            
        }
        return false;
    }
    
    /**
     * 
     * @param cc
     */
    public static void saveResults(CalidContainer cc) {
        if (cc.getPairedPointsList().isEmpty())
            return;

        File file = new File(getResultsPath(cc, cc.getPair().getDate()));

        PrintWriter pw = null;
        boolean newfile = false;
        try {
            if (!file.exists()) {
                file.createNewFile();
                newfile = true;
            }
            pw = new PrintWriter(new FileOutputStream(file, true), true);

            /*
             * Creating header
             */
            if (newfile) {
                pw.println("# " + SOURCE + cc.getPair().getSource1() + ","
                        + cc.getPair().getSource2() + " " + ELEVATION
                        + cc.getVerifiedElevation() + " " + DISTANCE
                        + cc.getParsedParameters().getDistance() + " "
                        + REFLECTIVITY
                        + cc.getParsedParameters().getReflectivity());
            }
            
            pw.print(CALID_DATE_TIME_FORMAT.format(cc.getPair().getDate()));
            
            Iterator<PairedPoints> itr = cc.getPairedPointsList().iterator();
            while (itr.hasNext()) {
                pw.print(" ");
                PairedPoints p = itr.next();
                String v = ((p.getDifference() == null) ? NULL : Double
                        .toString(p.getDifference()));
                pw.print(v);
            }
            pw.print("\n");
            LogHandler.getLogs().displayMsg("CALID: Saving results complete",
                    LogHandler.NORMAL);
        } catch (FileNotFoundException e) {
            LogHandler.getLogs()
                    .displayMsg(
                            "CALID: Cannot create result file in path "
                                    + file.getAbsolutePath() + "\n"
                                    + e.getMessage(), LogHandler.ERROR);
        } catch (IOException e) {
            LogHandler.getLogs()
                    .displayMsg(
                            "CALID: Cannot create result file in path "
                                    + file.getAbsolutePath() + "\n"
                                    + e.getMessage(), LogHandler.ERROR);

        } finally {
            if (pw != null)
                pw.close();
        }
    }
    
    /**
     * helping method, should be used when loading data from file fails
     * 
     * @return
     */
    public static boolean calculateMatchingPoints(CalidContainer cc) {
        if (!cc.hasVolumeData()) {
            LogHandler.getLogs().displayMsg("CALID: no valid volumes to process",
                    Logging.WARNING);
            return false;
        }

        ArrayList<PairedPoints> pairedPointsList = new ArrayList<PairedPoints>();
        
        double ele = cc.getVerifiedElevation();
        
        LogHandler.getLogs().displayMsg(
                "CALID: Calculating overlapping points coordinates for: " + cc.getPair().getSource1()
                        + " and " + cc.getPair().getSource2(), LogHandler.NORMAL);
        
        Point2D.Double r1coords = new Point2D.Double(cc.getPair().getVol1().getLon(),
                cc.getPair().getVol1().getLat());
        Point2D.Double r2coords = new Point2D.Double(cc.getPair().getVol2().getLon(),
                cc.getPair().getVol2().getLat());

        int radHalfDist = 0;
        Double dist = VincentyFormulas.dist(r1coords, r2coords);
        if(dist != null)
            radHalfDist = (int) (dist / 2000);

        double scale1 = cc.getPair().getVol1().getScan(ele)
                .getRScale();
        double scale2 = cc.getPair().getVol2().getScan(ele)
                .getRScale();

        int radarRange1 = (int) (cc.getPair().getVol1()
                .getScan(ele).getNBins()
                * scale1 / 1000);
        int radarRange2 = (int) (cc.getPair().getVol2()
                .getScan(ele).getNBins()
                * scale2 / 1000);

//        System.out.println("radar1 range = " + radarRange1);
//        System.out.println("radar2 range = " + radarRange2);
        
//        System.out.println(pair.getVol1().getSiteName() + " " + pair.getVol2().getSiteName());
//        System.out.println(raddist + "m, " + bins1 * pair.getVol1().getScan(elevation).getRScale());
        
        
        if (radHalfDist > radarRange1 || radHalfDist > radarRange2 ) {
            LogHandler.getLogs().displayMsg(
                    "CALID: Radars are to far from each other"
                            + " and have no overlapping points", Logging.WARNING);
            return false;
        }
        
        double rad1bearing = VincentyFormulas.getBearing(r1coords, r2coords);
        double rad2bearing = VincentyFormulas.getBearing(r2coords, r1coords);

        if (rad1bearing < 0) {
            rad1bearing = 360 + rad1bearing;
        }
        if (rad2bearing < 0) {
            rad2bearing = 360 + rad2bearing;
        }

        //limiting area to improve performance, with small buffer=2
        int rad1startray = (int) (rad1bearing - getAngle(radHalfDist, radarRange1)) - 2;
        int rad1endray = (int) (rad1bearing + getAngle(radHalfDist, radarRange1)) + 2;
        int rad2startray = (int) (rad2bearing - getAngle(radHalfDist, radarRange2)) - 2;
        int rad2endray = (int) (rad2bearing + getAngle(radHalfDist, radarRange2)) + 2;

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
        
        for (int b1 = radHalfDist; b1 < radarRange1; b1++) {
            for (int b2 = radHalfDist; b2 < radarRange2; b2++) {
                
//                System.out.println(b1 + " koniec: " + radarRange1);
                if (b1 != b2)
                    continue;
                for (int r1 = rad1startray; r1 != rad1endray; r1 = (r1 + 1) % 360) {
                    for (int r2 = rad2startray; r2 != rad2endray; r2 = (r2 + 1) % 360) {
                        double calculatedDist1 = Math.cos(Math
                                .toRadians(ele)) * (b1 + 0.5) * scale1;
                        Point2D.Double p1 = VincentyFormulas.dest(r1coords, r1,
                                calculatedDist1);
                        double calculatedDist2 = Math.cos(Math
                                .toRadians(ele)) * (b2 + 0.5) * scale2;
                        Point2D.Double p2 = VincentyFormulas.dest(r2coords, r2,
                                calculatedDist2);
                        double calculatedDist = VincentyFormulas.dist(p1, p2);
                        if (calculatedDist < cc.getParsedParameters().getDistance()) {
                            // System.out.println(r1coords + " " + r1 + " " +
                            // calculatedDist1);
                            // System.out.println(r2coords + " " + r2 + " " +
                            // calculatedDist2);
                            // System.out.println("Calculated dist=" +
                            // calculatedDist);
                            RayBinData rb = new RayBinData(r1, b1, r2, b2);
                            rb.setCoord1(p1);
                            rb.setCoord2(p2);
                            
                            pairedPointsList.add(rb);
                        }
                    }
                }
            }
        }
//        CalidCoords[] rb = rayBins.toArray(new CalidCoords[0]);
        
        cc.setPairedPointsList(pairedPointsList);
        saveCoords(cc);
        return true;
    }

    /**
     * Helping method, calculates angle between two sides
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
    
    /**
     * helping method
     *
    public int[] getMatcdhingPointsData(VolumeContainer vol1,
            VolumeContainer vol2) {

        int data[] = new int[pairedPointsList.size()];
        int i = 0;
        Iterator<PairedPoints> itr = pairedPointsList.iterator();
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
     */

    /**
     * helping method
     */
    private static boolean saveCoords(CalidContainer cc) {
//        if (cc.getPairedPointsList().isEmpty()) {
//            return false;
//        }
        double ele = cc.getVerifiedElevation();
        
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();

        } catch (ParserConfigurationException e) {
            LogHandler.getLogs().displayMsg(
                    "CALID: Error while creating XML document object", Logging.ERROR);
            return false;
        }
//        Element root = doc.createElement(ROOT);

        /*
        HashSet<Integer> ids = new HashSet<Integer>();
        Document oldDoc = XMLHandler.loadXML(getCoordsPath(cc));
        if (oldDoc != null && oldDoc.hasChildNodes()) {
            NodeList list = oldDoc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().matches(PAIR)) {
                    Node oldPair = doc.importNode(list.item(i), true);
//                    String id = XMLHandler.getAttributeValue(oldPair, ID);

                    try {
//                        ids.add(Integer.parseInt(id));
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    root.appendChild(oldPair);
                }
            }
        }
         */
        Element pairElement = doc.createElement(PAIR);
//        int id = getNewId(ids);

        /* pair header */
//        pairElement.setAttribute(ID, String.valueOf(id));

//        pairElement.setAttribute(ELEVATION, String.valueOf(ele));
//        pairElement.setAttribute(DISTANCE, String.valueOf(cc.getManager().getDistance()));

        pairElement
                .setAttribute(R1LON, String.valueOf(cc.getPair().getVol1().getLon()));
        pairElement
                .setAttribute(R1LAT, String.valueOf(cc.getPair().getVol1().getLat()));

        pairElement.setAttribute(R1BINS,
                String.valueOf(cc.getPair().getVol1().getScan(ele).getNBins()));
        pairElement.setAttribute(R1SCALE,
                String.valueOf(cc.getPair().getVol1().getScan(ele).getRScale()));

        pairElement
                .setAttribute(R2LON, String.valueOf(cc.getPair().getVol2().getLon()));
        pairElement
                .setAttribute(R2LAT, String.valueOf(cc.getPair().getVol2().getLat()));
        pairElement.setAttribute(R2BINS,
                String.valueOf(cc.getPair().getVol2().getScan(ele).getNBins()));
        pairElement.setAttribute(R2SCALE,
                String.valueOf(cc.getPair().getVol2().getScan(ele).getRScale()));
        pairElement.setAttribute(SIZE,
                String.valueOf(cc.getPairedPointsList().size()));
        
        /* ----------- */
        
        Iterator<PairedPoints> itr = cc.getPairedPointsList().iterator();
        while (itr.hasNext()) {
            PairedPoints rb = itr.next();
            Element raybin = doc.createElement(POINT);
            raybin.setAttribute(R1LON, String.valueOf(rb.getCoord1().x));
            raybin.setAttribute(R1LAT, String.valueOf(rb.getCoord1().y));
            raybin.setAttribute(R1BIN, String.valueOf(rb.getBin1()));
            raybin.setAttribute(R1RAY, String.valueOf(rb.getRay1()));
            raybin.setAttribute(R2LAT, String.valueOf(rb.getCoord2().y));
            raybin.setAttribute(R2LON, String.valueOf(rb.getCoord2().x));
            raybin.setAttribute(R2BIN, String.valueOf(rb.getBin2()));
            raybin.setAttribute(R2RAY, String.valueOf(rb.getRay2()));
            pairElement.appendChild(raybin);
        }

//        root.appendChild(pairElement);

        doc.appendChild(pairElement);

        XMLHandler.saveXMLFile(doc, getCoordsPath(cc));
        return true;
    }

//    /**
//     * @param ids
//     * @return
//     */
//    private static int getNewId(HashSet<Integer> ids) {
//        int i = 0;
//        while (ids.contains(i)) {
//            i++;
//        }
//
//        return i;
//    }

    /**
     * helping method, returns false if loading data from file fails
     * 
     * @return
     */
    public static boolean loadCoords(CalidContainer cc) {
        ArrayList<PairedPoints> pairedPointsList = new ArrayList<PairedPoints>();
        Document oldDoc = XMLHandler.loadXML(getCoordsPath(cc));

        if (oldDoc != null && oldDoc.hasChildNodes()
                && oldDoc.getChildNodes().item(0).getNodeName().matches(PAIR)) {
            LogHandler.getLogs().displayMsg(
                    "CALID: Loading coordinates from file: "
                            + getCoordsPath(cc), LogHandler.NORMAL);

            Node node = oldDoc.getChildNodes().item(0);
            NodeList coords = node.getChildNodes();

            String x = "";
            String y = "";

            if (cc.getPair().hasRealVolumes()) {
                Point2D.Double p1;
                Point2D.Double p2;
                x = XMLHandler.getAttributeValue(node, R1LON);
                y = XMLHandler.getAttributeValue(node, R1LAT);
                p1 = new Point2D.Double(Double.parseDouble(x),
                        Double.parseDouble(y));
                x = XMLHandler.getAttributeValue(node, R2LON);
                y = XMLHandler.getAttributeValue(node, R2LAT);
                p2 = new Point2D.Double(Double.parseDouble(x),
                        Double.parseDouble(y));
                Point2D.Double r1coords = new Point2D.Double(cc.getPair()
                        .getVol1().getLon(), cc.getPair().getVol1().getLat());
                Point2D.Double r2coords = new Point2D.Double(cc.getPair()
                        .getVol2().getLon(), cc.getPair().getVol2().getLat());
                
                if (!p1.equals(r1coords) || !p2.equals(r2coords)) {
                    return false;
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
                        int r1bin = Integer.parseInt(XMLHandler
                                .getAttributeValue(coords.item(c), R1BIN));
                        int r1ray = Integer.parseInt(XMLHandler
                                .getAttributeValue(coords.item(c), R1RAY));
                        int r2bin = Integer.parseInt(XMLHandler
                                .getAttributeValue(coords.item(c), R2BIN));
                        int r2ray = Integer.parseInt(XMLHandler
                                .getAttributeValue(coords.item(c), R2RAY));
                        RayBinData rbd = new RayBinData(r1ray, r1bin, r2ray,
                                r2bin);
                        rbd.setCoord1(r1p);
                        rbd.setCoord2(r2p);
                        pairedPointsList.add(rbd);
                    }

                } catch (NumberFormatException e) {
                    return false;
                }
            }
//            if (!pairedPointsList.isEmpty()) {
//            }
            cc.setPairedPointsList(pairedPointsList);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(CalidFileHandler.getAngle(125, 250));
    }

}
