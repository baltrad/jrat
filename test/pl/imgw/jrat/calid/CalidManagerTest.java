/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.util.Iterator;

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
public class CalidManagerTest {

    CalidManager manager;
    
    @Test
    public void calculateTest() {
        String[] args = new String[] { "-i",
                "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol",
                "test-data/calid/2011082113402500dBZ.vol",
                "test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5",
                "test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5",
                "--calid a", "-v" };
        
        ProcessController proc = new ProcessController(args);
        proc.start();
        PairsContainer pairs = new PairsContainer(proc.getFiles());
        Iterator<Pair> i = pairs.getPairs().iterator();
        
        args = new String[] { "0.5deg", "500m" };
        manager = new CalidManager(args);
        assertTrue(manager != null);
        
        assertTrue(manager.calculate(i.next()).size() > 0);
        
        args = new String[] { "-0.2deg", "0.5km" };
        manager = new CalidManager(args);
        assertTrue(manager != null);
        
        args = new String[] { "0.2deg", "500m" };
        manager = new CalidManager(args);
        assertNull(manager.calculate(i.next()));

        
        
        
    }
   
    
}
