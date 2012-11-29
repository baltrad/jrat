/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pl.imgw.jrat.data.RawByteDataArray;
import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidComparator {

    private static void compare(List<PairedPoints> results, ScanContainer scan1,
            ScanContainer scan2, double dbz) {

        Iterator<PairedPoints> itr = results.iterator();
        while (itr.hasNext()) {
            PairedPoints coords = itr.next();
            RawByteDataArray array1 = (RawByteDataArray) scan1.getArray();
            RawByteDataArray array2 = (RawByteDataArray) scan2.getArray();
            double val1 = array1.getPoint(coords.getRay1(), coords.getBin1());
            double val2 = array2.getPoint(coords.getRay2(), coords.getBin2());
            if (val1 >= dbz || val2 >= dbz) {
                // val1 = array1.getPoint(coords.getRay1(), coords.getBin1());
                // val2 = array2.getPoint(coords.getRay2(), coords.getBin2());
                // System.out.println(val1 + " " + val2);
                coords.setDifference(val1 - val2);

            }
        }

        LogHandler.getLogs().displayMsg("CALID: Processing data completed",
                LogHandler.WARNING);
    }

    /**
     * Compares two scans in
     * 
     * @return null if failed
     */
    public static ArrayList<PairedPoints> getResult(CalidManager calid, Pair pair) {

        double elevation = calid.getElevation();
        int distance = calid.getDistance();
        double reflectivity = calid.getReflectivity();

        // check if the volumes contain selected elevation
        if (pair.getVol1().getScan(elevation) == null
                || pair.getVol2().getScan(elevation) == null) {
            return null;
        }

        ArrayList<PairedPoints> pairedPointsList = null;

        // LogHandler.getLogs().displayMsg(
        // "Calculating overlapping points for: " + pair.getSource1()
        // + " and " + pair.getSource2(), LogHandler.WARNING);

        CalidContainer container = new CalidContainer(pair, elevation,
                distance, reflectivity);
        pairedPointsList = container.getCoords();

        if (!pairedPointsList.isEmpty()) {
            LogHandler.getLogs().displayMsg(
                    "CALID: Number of overlapping points: "
                            + pairedPointsList.size(), LogHandler.WARNING);

        } else {
            LogHandler.getLogs().displayMsg("CALID: No overlapping points.",
                    LogHandler.WARNING);
            return null;
        }

        if (!container.loadResults(pair.getDate())) {

            compare(pairedPointsList,
                    pair.getVol1().getScan(elevation),
                    pair.getVol2().getScan(elevation), reflectivity);

            container.saveResults();
        }

        // results = comp.getResults();
        // comp.save(coords.getId(), pair.toString(), pair.getDate());

        LogHandler.getLogs().displayMsg(
                "CALID: Comparison completed for: " + pair.getSource1()
                        + " and " + pair.getSource2() + "\n",
                LogHandler.WARNING);

        return pairedPointsList;
    }

}
