/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.VolumeContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ScansunManager {

    /**
     * @param args
     */
    public ScansunManager(String[] args) {
        
    }

    /**
     * @param product
     */
    public void calculate(VolumeContainer vol) {
        vol.getScan(0.5).getArray().getPoint(0, 0); //dBZ
        System.out.println("Wczytane wolumy: " + vol.getSiteName() + " " + vol.getTime());

        for(ScanContainer scan : vol.getAllScans()) {
            System.out.println("elewacja=" + scan.getElevation());
        }
        
        System.out.println("Na razie nic nie robie");
        
        
    }



    
    
}
