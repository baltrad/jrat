/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.VolumeContainer;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Comparator {
    
    List<RayBin> rayBins;

    public Comparator(List<RayBin> rayBins, ScanContainer scan1, ScanContainer scan2) {

        this.rayBins = rayBins;
        
        int i = 0;
        Iterator<RayBin> itr = rayBins.iterator();
        while (itr.hasNext()) {
            RayBin rb = itr.next();
            double val1 = scan1.getArray().getPoint(rb.getRay1(), rb.getBin1());
            double val2 = scan2.getArray().getPoint(rb.getRay2(), rb.getBin2());
            rb.setDifference(val1 - val2);
        }
    }

    public void save(int id, String pair, Date date) {
            
//            ResultsManager.saveResults(id, pair, date, rayBins);
            
    }
    
}
