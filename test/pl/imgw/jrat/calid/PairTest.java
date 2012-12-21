/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.H5DataContainer;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;

/**
 * 
 * /Class description/
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
        pm.initialize(new File("test-data/calid",
                "T_PAGZ41_C_SOWR_20110922004019.h5"));
        VolumeContainer vol1 = new OdimH5Volume(
                (H5DataContainer) pm.getProduct());
        pm.initialize(new File("test-data/calid",
                "T_PAGZ44_C_SOWR_20110922004021.h5"));
        VolumeContainer vol2 = new OdimH5Volume(
                (H5DataContainer) pm.getProduct());
        pair = new Pair(vol1, vol2);
        Pair pair2 = new Pair(vol2, vol1);
        assertTrue(pair.getVol1() == pair2.getVol1());
        assertTrue("validation is not working well with odim format",
                pair.hasRealVolumes());
        assertEquals(57, pair.getVol2().getScan(elevation).getArray()
                .getRawIntPoint(116, 16));
        String s1 = "WMO:12374";
        String s2 = "WMO:12579";
        Pair pair3 = new Pair(s1, s2);
        assertTrue(pair.getSource1().matches(s2));
        // System.out.println(pair3);
        // System.out.println(pair);

    }

    @Test
    public void isRB5ValidTest() {
        ParserManager pm = new ParserManager();
        pm.setParser(new DefaultParser());
        pm.initialize(new File("test-data/calid", "2011082113402900dBZ.vol"));
        VolumeContainer vol1 = new RainbowVolume(
                (RainbowDataContainer) pm.getProduct());
        pm.initialize(new File("test-data/calid", "2011082113400400dBZ.vol"));
        VolumeContainer vol2 = new RainbowVolume(
                (RainbowDataContainer) pm.getProduct());
        pair = new Pair(vol1, vol2);
        assertTrue("validation is not working well with rainbow format",
                pair.hasRealVolumes());
        assertEquals(60, pair.getVol2().getScan(elevation).getArray()
                .getRawIntPoint(301, 20));
        assertEquals(-2d, pair.getVol2().getScan(elevation).getArray()
                .getPoint(301, 20), 0.1);
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
        ParserManager manager = new ParserManager();
        manager.setParser(new DefaultParser());
        File f1 = new File("test-data/calid/2011082113400400dBZ.vol");
        File f2 = new File("test-data/calid/2011082113402900dBZ.vol");
        manager.initialize(f1);
        VolumeContainer vol1 = new RainbowVolume(
                (RainbowDataContainer) manager.getProduct());
        manager.initialize(f2);
        VolumeContainer vol2 = new RainbowVolume(
                (RainbowDataContainer) manager.getProduct());
        Pair pair1 = new Pair(vol1, vol2);
        Pair pair2 = new Pair(vol2, vol1);
        assertTrue(pair1.equals(pair2));

        f1 = new File("test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5");
        f2 = new File("test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5");
        manager.initialize(f1);
        vol1 = new OdimH5Volume((H5DataContainer) manager.getProduct());
        manager.initialize(f2);
        vol2 = new OdimH5Volume((H5DataContainer) manager.getProduct());
        pair2 = new Pair(vol1, vol2);
        assertTrue(!pair1.equals(pair2));
    }
}
