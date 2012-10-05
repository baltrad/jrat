/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import pl.imgw.jrat.process.ProcessController;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OverlappingCoordsTest {

    @Test
    public void calculateCoordsTest() {
        
        new File("calid/overlapping/RzeszowBrzuchania/500_0.5/coords.xml").delete();
        
        String[] args = new String[] { "-i",
                "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol", "--calid", "500m",
                "0.5deg", "-v" };
        ProcessController proc = new ProcessController(args);
        proc.start();
        PairsContainer container = new PairsContainer(proc.getFiles());
        Pair pair = container.getPairs().iterator().next();
        
        int dist = 500;
        double ele = 0.5;
        CoordsManager coords = new CoordsManager(pair, ele, dist);
        /*
        
        //calculating new coords.xml
        assertNotNull(coords.getCoords());
        
        //loading coords from coords.xml
        assertNotNull(coords.getCoords());
        args = new String[] { "-i", "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol", "--calid a", "-v" };

        proc = new ProcessController(args);
        proc.start();
        container = new PairsContainer(proc.getFiles());
        pair = container.getPairs().iterator().next();
        coords = new OverlappingCoords(pair, ele, dist);
        
        //loading coords from coords.xml when radars are switched
        assertNotNull(coords.getCoords());
         */

    }

    @Test
    public void loadCoordsTest() {
        
    }
    
}
