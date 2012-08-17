/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.Before;

import pl.imgw.jrat.data.H5Data;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.RainbowData;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.process.ProcessController;
import pl.imgw.jrat.tools.in.FileSegregatorByDateAndSource;

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
                "test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5",
                "--calid a", "-v" };
        
        ProcessController proc = new ProcessController(args);
        proc.start();
        pairs = new PairsContainer(proc.getFiles());

    }

    @Test
    public void isEqualTest() {
        ParserManager manager = new ParserManager();
        manager.setParser(new DefaultParser());
        File f1 = new File("test-data/calid/2011082113400400dBZ.vol");
        File f2 = new File("test-data/calid/2011082113402900dBZ.vol");
        manager.initialize(f1);
        VolumeContainer vol1 = new RainbowVolume((RainbowData) manager.getProduct());
        manager.initialize(f2);
        VolumeContainer vol2 = new RainbowVolume((RainbowData) manager.getProduct());
        Pair pair1 = new Pair(vol1, vol2);
        Pair pair2 = new Pair(vol2, vol1);
        assertTrue(pair1.equals(pair2));
        
        f1 = new File("test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5");
        f2 = new File("test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5");
        manager.initialize(f1);
        vol1 = new OdimH5Volume((H5Data) manager.getProduct());
        manager.initialize(f2);
        vol2 = new OdimH5Volume((H5Data) manager.getProduct());
        pair2 = new Pair(vol1, vol2);
        assertTrue(!pair1.equals(pair2));
    }
    
    @Test
    public void getAllPairsTest() {
        assertEquals(4, pairs.getPairs().size());
    }
    
    
    @Test 
    public void getPairsByDateTest() {
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 7, 21, 13, 40, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertEquals(3, pairs.getPairs(date).size());
        
    }
}
