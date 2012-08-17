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
public class CalidManagerTest {

    CalidManager manager = new CalidManager();
    
    @Before
    public void setUp() {
        String[] args = new String[] { "-i",
                "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol",
                "test-data/calid/2011082113402500dBZ.vol",
                "test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5",
                "test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5",
                "--calid a", "-v" };
        ProcessController proc = new ProcessController(args);
        proc.start();
        manager.setFileList(proc.getFiles());
    }
    
    @Test
    public void initializeTest() {
        String[] args = new String[] { "0.5deg", "500m" };
        assertTrue(manager.initialize(args));
        args = new String[] { "0.1deg", "500m" };
        assertTrue(!manager.initialize(args));
    }
    
}
