/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import pl.imgw.jrat.process.ProcessController;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OverlappingCoordsTest {

    Pair pair;
    
    @Before
    public void setUp() {
        String[] args = new String[] { "-i",
                "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol",
                "--calid a", "-v" };
        ProcessController proc = new ProcessController(args);
        proc.start();
        PairsContainer container = new PairsContainer(proc.getFiles());
        pair = container.getPairs().iterator().next();
        
    }
    
    @Test
    public void calculateCoordsTest() {
        int dist = 500;
        double ele = 0.5;
        OverlappingCoords coords = new OverlappingCoords(pair, ele, dist);
        assertTrue(coords.calculateMatchingPoints());
    }
    
    @Test
    public void writeCoordsTest() {
        
    }
    
    @Test
    public void readCoordsTest() {
        int dist = 500;
        double ele = 0.5;
        OverlappingCoords coords = new OverlappingCoords(pair, ele, dist);
        
    }
    
}
