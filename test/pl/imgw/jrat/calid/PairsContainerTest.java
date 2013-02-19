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
                "test-data/pair/2011101003002600dBZ.vol",
                "test-data/pair/2011101003002700dBZ.vol",
                "test-data/pair/2011101003102200dBZ.vol",
                "test-data/pair/2011101003102600dBZ.vol", "-v" };

        MainProcessController proc = new MainProcessController(args);
        proc.start();
        pairs = new PairsContainer(proc.getFiles());
    }

    

    @Test
    public void getAllPairsTest() {
        assertEquals(2, pairs.getSize());
    }

    @Test
    public void pairsInRightOrderTest() {
        Pair p1 = pairs.next(); //first pair, 201108211340

        Pair p2 = pairs.next(); //last pair, 2011092200
//        System.out.println(p1.getDate() + ", " + p2.getDate());
        assertTrue(p1.getDate().before(p2.getDate()));
    }
    
    @Test
    public void dataValidationTest() {
        Pair p = pairs.next();
        assertTrue(!p.getVol1().getSiteName().matches(p.getVol2().getSiteName()));
        
    }
}
