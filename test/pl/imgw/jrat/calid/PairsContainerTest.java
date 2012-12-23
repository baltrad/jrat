/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.process.MainProcessController;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PairsContainerTest {

    private PairsContainer pairs;

    @Before
    public void setUp() {

        String[] args = new String[] { "-i",
                "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol",
                "test-data/calid/2011082113402500dBZ.vol",
                "test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5",
                "test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5", "-v" };

        MainProcessController proc = new MainProcessController(args);
        proc.start();
        pairs = new PairsContainer(proc.getFiles());
    }

    

    @Test
    public void getAllPairsTest() {
        assertEquals(4, pairs.getSize());
    }

    @Test
    public void pairsInRightOrderTest() {
        Pair p1 = pairs.getNext();
        Pair p2 = pairs.getNext();
        assertTrue(p1.getDate().before(p2.getDate()));
    }
    
    @Test
    public void dataValidationTest() {
        Pair p = pairs.getNext();
        assertTrue(!p.getVol1().getSiteName().matches(p.getVol2().getSiteName()));
        
    }
}
