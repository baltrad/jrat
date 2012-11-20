/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

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
public class Comparator {
    
    public static void compare(List<PairedPoints> results, ScanContainer scan1,
            ScanContainer scan2, double dbz) {

        Iterator<PairedPoints> itr = results.iterator();
        while (itr.hasNext()) {
            PairedPoints coords = itr.next();
            RawByteDataArray array1 = (RawByteDataArray) scan1.getArray();
            RawByteDataArray array2 = (RawByteDataArray) scan2.getArray();
            double val1 = array1.getPoint(coords.getRay1(), coords.getBin1());
            double val2 = array2.getPoint(coords.getRay2(), coords.getBin2());
            if(val1 >= dbz || val2 >= dbz) {
//                val1 = array1.getPoint(coords.getRay1(), coords.getBin1());
//                val2 = array2.getPoint(coords.getRay2(), coords.getBin2());
//            System.out.println(val1 + " " + val2);
                coords.setDifference(val1 - val2);
                
            }
        }
        
        LogHandler.getLogs().displayMsg("Comparison completed",
                LogHandler.WARNING);
    }

}
