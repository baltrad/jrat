/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
public class CalidDataSaver extends CalidDataHandler {

    private static final String SOURCE = "src=";
    private static final String ELEVATION = "ele=";
    private static final String REFLECTIVITY = "ref=";
    private static final String DISTANCE = "dis=";

    /**
     * 
     * @param results
     */
    public static void saveResults(CalidSingleResultContainer results) {
        saveResults(
                results,
                getResultsPath(results.getParams(), results
                        .getPolarVolumePair().getDate(), results.getPair()));
    }
    
    /**
     * 
     * @param results
     */
    protected static void saveResults(CalidSingleResultContainer results, File file) {
        
        if (results.getPairedPointsList().isEmpty())
            throw new CalidException("Nothing to save, result list is empty.");
            

        CalidParameters params = results.getParams();

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
                pw.println(COMMENTS + " " + SOURCE
                        + results.getPair().getBothSources() + " " + ELEVATION
                        + params.getElevation() + " " + DISTANCE
                        + params.getDistance() + " " + REFLECTIVITY
                        + params.getReflectivity());
            }

            pw.print(CALID_DATE_TIME_FORMAT.format(results.getPolarVolumePair()
                    .getDate()));

            Iterator<PairedPoint> itr = results.getPairedPointsList().iterator();
            while (itr.hasNext()) {
                pw.print(" ");
                PairedPoint p = itr.next();
                String v = ((p.getDifference() == null) ? NULL : Double
                        .toString(p.getDifference()));
                pw.print(v);
            }
            pw.print(" " + results.getR1understate() + " " + results.getR2understate());
            pw.print("\n");
            log.printMsg("CALID: Saving results complete", Log.TYPE_NORMAL,
                    Log.MODE_VERBOSE);
        } catch (FileNotFoundException e) {
            log.printMsg(
                    "CALID: Cannot create result file in path "
                            + file.getAbsolutePath() + "\n" + e.getMessage(),
                    Log.TYPE_ERROR, Log.MODE_VERBOSE);
        } catch (IOException e) {
            log.printMsg(
                    "CALID: Cannot create result file in path "
                            + file.getAbsolutePath() + "\n" + e.getMessage(),
                    Log.TYPE_ERROR, Log.MODE_VERBOSE);

        } finally {
            if (pw != null)
                pw.close();
        }
    }

    
    public static void saveCoords(CalidSingleResultContainer results) throws CalidException{
        saveCoords(getCoordsPath(results.getParams(), results.getPair()), results);
    }

    protected static void saveCoords(File file, CalidSingleResultContainer results) {
         
        List<PairedPoint> points = results.getPairedPointsList();
        
        if (points.isEmpty()) {
            throw new CalidException("Nothing to save, result list is empty.");
        }
        
        PolarVolumesPair volumes = results.getPolarVolumePair();
//        CalidParameters params = results.getParams();
        double ele = results.getParams().getElevation();

        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();

            doc = builder.newDocument();
            // System.out.println("tutaj");

        } catch (ParserConfigurationException e) {
            log.printMsg("CALID: Error while creating XML document object",
                    Log.TYPE_ERROR, Log.MODE_VERBOSE);
            throw new CalidException(e.getMessage());
        }

        Element pair = doc.createElement(PAIR);

        pair.setAttribute(R1LON,
                String.valueOf(volumes.getVol1().getLon()));
        pair.setAttribute(R1LAT,
                String.valueOf(volumes.getVol1().getLat()));

        pair.setAttribute(
                R1BINS,
                String.valueOf(volumes.getVol1()
                        .getScan(ele).getNBins()));
        pair.setAttribute(
                R1SCALE,
                String.valueOf(volumes.getVol1()
                        .getScan(ele).getRScale()));

        pair.setAttribute(R2LON,
                String.valueOf(volumes.getVol2().getLon()));
        pair.setAttribute(R2LAT,
                String.valueOf(volumes.getVol2().getLat()));
        pair.setAttribute(
                R2BINS,
                String.valueOf(volumes.getVol2()
                        .getScan(ele).getNBins()));
        pair.setAttribute(
                R2SCALE,
                String.valueOf(volumes.getVol2()
                        .getScan(ele).getRScale()));
        pair.setAttribute(SIZE,
                String.valueOf(points.size()));

        /* ----------- */

        Iterator<PairedPoint> itr = points.iterator();
        while (itr.hasNext()) {
            PairedPoint pairPoint = itr.next();
            Element point = doc.createElement(POINT);
            point.setAttribute(R1LON, String.valueOf(pairPoint.getCoord1().x));
            point.setAttribute(R1LAT, String.valueOf(pairPoint.getCoord1().y));
            point.setAttribute(R1BIN, String.valueOf(pairPoint.getBin1()));
            point.setAttribute(R1RAY, String.valueOf(pairPoint.getRay1()));
            point.setAttribute(R2LAT, String.valueOf(pairPoint.getCoord2().y));
            point.setAttribute(R2LON, String.valueOf(pairPoint.getCoord2().x));
            point.setAttribute(R2BIN, String.valueOf(pairPoint.getBin2()));
            point.setAttribute(R2RAY, String.valueOf(pairPoint.getRay2()));
            pair.appendChild(point);
        }

        // root.appendChild(pairElement);

        doc.appendChild(pair);

        XMLHandler.saveXMLFile(doc, file.getPath());
        
    }

}
