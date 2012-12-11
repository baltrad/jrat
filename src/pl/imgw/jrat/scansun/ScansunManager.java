/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

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

        ConsoleProgressBar.getProgressBar().initialize(5, 10, LogHandler.getLogs().getVerbose() == Logging.PROGRESS_BAR_ONLY);
        for(ScanContainer scan : vol.getAllScans()) {
        	
        	
        	LogHandler.getLogs().displayMsg("elewacja=" + scan.getElevation(), Logging.WARNING);
        	ConsoleProgressBar.getProgressBar().evaluate();
        	
//            System.out.println("elewacja=" + scan.getElevation());
        }

        ConsoleProgressBar.getProgressBar().printDoneMsg();
        System.out.println("Na razie nic nie robie");



        
    }



    
    
}
