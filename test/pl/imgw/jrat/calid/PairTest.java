/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.VolumeContainer;
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
                (OdimDataContainer) pm.getProduct());
        pm.initialize(new File("test-data/calid",
                "T_PAGZ44_C_SOWR_20110922004021.h5"));
        VolumeContainer vol2 = new OdimH5Volume(
                (OdimDataContainer) pm.getProduct());
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
        assertEquals(52, pair.getVol2().getScan(elevation).getArray()
                .getRawIntPoint(20, 301));
        assertEquals(-6.0d, pair.getVol2().getScan(elevation).getArray()
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
        vol1 = new OdimH5Volume((OdimDataContainer) manager.getProduct());
        manager.initialize(f2);
        vol2 = new OdimH5Volume((OdimDataContainer) manager.getProduct());
        pair2 = new Pair(vol1, vol2);
        assertTrue(!pair1.equals(pair2));
    }
}
