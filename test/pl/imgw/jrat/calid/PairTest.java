/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.H5Data;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.RainbowData;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PairTest {

    private Pair pair;
    double elevation = 0.5;
    
    @Test
    public void isH5ValidTest() {
        ParserManager pm = new ParserManager();
        pm.setParser(new DefaultParser());
        pm.initialize(new File("test-data/calid", "T_PAGZ41_C_SOWR_20110922004019.h5"));
        VolumeContainer vol1 = new OdimH5Volume((H5Data) pm.getProduct());
        pm.initialize(new File("test-data/calid", "T_PAGZ44_C_SOWR_20110922004021.h5"));
        VolumeContainer vol2 = new OdimH5Volume((H5Data) pm.getProduct());
        pair = new Pair(vol1, vol2);
        Pair pair2 = new Pair(vol2, vol1);
        assertTrue(pair.getVol1() == pair2.getVol1());
        assertTrue("validation is not working well with odim format", pair.isValid());
        assertEquals(57, pair.getVol2().getScan(elevation).getArray().getRawIntPoint(116, 16));
        
    }
    
    @Test
    public void isRB5ValidTest() {
        ParserManager pm = new ParserManager();
        pm.setParser(new DefaultParser());
        pm.initialize(new File("test-data/calid", "2011082113402900dBZ.vol"));
        VolumeContainer vol1 = new RainbowVolume((RainbowData) pm.getProduct());
        pm.initialize(new File("test-data/calid", "2011082113400400dBZ.vol"));
        VolumeContainer vol2 = new RainbowVolume((RainbowData) pm.getProduct());
        pair = new Pair(vol1, vol2);
        assertTrue("validation is not working well with rainbow format", pair.isValid());
        assertEquals(60, pair.getVol2().getScan(elevation).getArray().getRawIntPoint(301, 20));
        assertEquals(-2d, pair.getVol2().getScan(elevation).getArray().getPoint(301, 20), 0.1);
    }
    
}
