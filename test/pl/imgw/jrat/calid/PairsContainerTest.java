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
import java.util.TreeSet;

import org.junit.Test;
import org.junit.Before;

import pl.imgw.jrat.data.H5DataContainer;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.process.MainProcessController;
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
