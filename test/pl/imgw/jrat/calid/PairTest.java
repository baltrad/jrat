/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.FileParser;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.VolumeParser;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PairTest {

    static {
        LogHandler.getLogs().setLoggingVerbose(LogHandler.ALL_MSG);
    }
    
    @Before
    public void setUp() {
        pair = null;
    }
    
    private Pair pair;
    double elevation = 0.5;
    

    @Test
    public void isH5ValidTest() {
        VolumeParser volParser = new DefaultParser();
        
        volParser.initialize(new File("test-data/calid",
                "T_PAGZ41_C_SOWR_20110922004019.h5"));
        
        VolumeContainer vol1 = volParser.getVolume();
        
        volParser.initialize(new File("test-data/calid",
                "T_PAGZ44_C_SOWR_20110922004021.h5"));
        
        VolumeContainer vol2 = volParser.getVolume();
        
        System.out.println("site1=" + vol1.getSiteName() + " site2=" + vol2.getSiteName());
        
        assertTrue(vol1 != null && vol2 != null);
        
        pair = new Pair(vol1, vol2);
        Pair pair2 = new Pair(vol2, vol1);
        
        assertTrue(pair.getVol1() == pair2.getVol1());
        
        assertTrue("validation is not working well with odim format",
                pair.hasRealVolumes());
        
        assertEquals(57, pair.getVol2().getScan(elevation).getArray()
                .getRawIntPoint(16, 116));
        String s1 = "12374";
        String s2 = "12579";
        Pair pair3 = new Pair(s1, s2);
        assertTrue(pair.getSource1().matches(s2));
        // System.out.println(pair3);
        // System.out.println(pair);

    }

    @Test
    public void isRB5ValidTest() {
        VolumeParser volParser = new DefaultParser();
        
        volParser.initialize(new File("test-data/pair", "2011101003002600dBZ.vol"));
        VolumeContainer vol1 = volParser.getVolume();
        volParser.initialize(new File("test-data/pair", "2011101003002700dBZ.vol"));
        VolumeContainer vol2 = volParser.getVolume();
        
        pair = new Pair(vol1, vol2);
        assertTrue("validation is not working well with rainbow format",
                pair.hasRealVolumes());
        assertEquals(57, pair.getVol2().getScan(elevation).getArray()
                .getRawIntPoint(20, 301));
        assertEquals(-3.5d, pair.getVol2().getScan(elevation).getArray()
                .getPoint(20, 301), 0.1);
        System.out.println(pair);
        assertTrue("Comparing pair's source name",
                pair.getSource1().matches("Rzeszow"));
    }

    @Test
    public void pairCreatedFromStringOrderTest() {
        String source1 = "Rzeszow";
        String source2 = "Legionowo";
        pair = new Pair(source1, source2);
        assertFalse(pair.hasRealVolumes());
        assertTrue(pair.getSource2().matches(source2));
        pair = new Pair(source2, source1);
        assertTrue(pair.getSource2().matches(source2));
    }

    @Test
    public void isEqualTest() {
        VolumeParser volParser = new DefaultParser();
        File f1 = new File("test-data/calid/2012060317401700dBZ.vol");
        File f2 = new File("test-data/calid/2012060317402900dBZ.vol");
        volParser.initialize(f1);
        VolumeContainer vol1 = volParser.getVolume();
        volParser.initialize(f2);
        VolumeContainer vol2 = volParser.getVolume();
        Pair pair1 = new Pair(vol1, vol2);
        Pair pair2 = new Pair(vol2, vol1);
        assertTrue(pair1.equals(pair2));

        f1 = new File("test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5");
        f2 = new File("test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5");
        
        vol1 = volParser.getVolume();
        volParser.initialize(f2);
        vol2 = volParser.getVolume();
        pair2 = new Pair(vol1, vol2);
        assertTrue(!pair1.equals(pair2));
    }
}
