/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.util.Date;
import java.util.Iterator;

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

    
    private static void compare(CalidContainer container, ScanContainer scan1,
            ScanContainer scan2, double dbz) {

        LogHandler.getLogs().displayMsg(
                "CALID: Comparing data for " + container.getPair().getSource1()
                        + " and " + container.getPair().getSource2(),
                LogHandler.NORMAL);
        
        Iterator<PairedPoints> itr = container.getPairedPointsList().iterator();
        while (itr.hasNext()) {
            PairedPoints coords = itr.next();
//            RawByteDataArray array1 = (RawByteDataArray) scan1.getArray();
//            RawByteDataArray array2 = (RawByteDataArray) scan2.getArray();
            double val1 = scan1.getArray().getPoint(coords.getRay1(), coords.getBin1());
            double val2 = scan2.getArray().getPoint(coords.getRay2(), coords.getBin2());
            if (val1 >= dbz || val2 >= dbz) {
                // val1 = array1.getPoint(coords.getRay1(), coords.getBin1());
                // val2 = array2.getPoint(coords.getRay2(), coords.getBin2());
                // System.out.println(val1 + " " + val2);
                coords.setDifference(val1 - val2);

            }
        }

        container.setHasResults(true);
        
    }

    /**
     * Load results from file or if not found compares two scans
     * 
     * @return null if failed
     */
    public static void receiveResults(CalidContainer cc, Date date) {

//        double reflectivity = cc.getManager().getReflectivity();
        
        if(cc.getVerifiedElevation() == null)
            return;

        double elevation = cc.getVerifiedElevation();
        
        // LogHandler.getLogs().displayMsg(
        // "Calculating overlapping points for: " + pair.getSource1()
        // + " and " + pair.getSource2(), LogHandler.WARNING);

        if(!CalidFileHandler.loadCoords(cc)) {
            CalidFileHandler.calculateMatchingPoints(cc);
        }

        if (!cc.getPairedPointsList().isEmpty()) {
            LogHandler.getLogs().displayMsg(
                    "CALID: Number of overlapping points: "
                            + cc.getPairedPointsList().size(),
                    LogHandler.NORMAL);

        } else {
            LogHandler.getLogs().displayMsg("CALID: No overlapping points.",
                    LogHandler.WARNING);
            return;
        }

        ScanContainer scan1 = cc.getPair().getVol1().getScan(elevation);
        ScanContainer scan2 = cc.getPair().getVol2().getScan(elevation);
        
        
        if (!CalidFileHandler.loadResults(cc, date)) {
            compare(cc, scan1, scan2, cc.getParsedParameters().getReflectivity());
            CalidFileHandler.saveResults(cc);

        }

        // results = comp.getResults();
        // comp.save(coords.getId(), pair.toString(), pair.getDate());

        LogHandler.getLogs().displayMsg(
                "CALID: Comparison completed for: " + cc.getPair().getSource1()
                        + " and " + cc.getPair().getSource2() + "\n",
                LogHandler.NORMAL);

    }
}
