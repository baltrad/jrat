/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidException;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RadarsPairTest {

    String src1 = "Rzeszow";
    String src2 = "Brzuchania";
    String srcEmpty = "";
    String srcNull = null;

    @Test(expected=CalidException.class)
    public void shouldThrowException() {
        new RadarsPair(srcNull, src2);
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.RadarsPair#hasBothSources()}.
     */
    @Test
    public void shouldHasBothSources() {
        RadarsPair pair = new RadarsPair(src1, src2);
        assertTrue(pair.hasBothSources());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.RadarsPair#hasOnlyOneSource()}.
     */
    @Test
    public void shouldHasOnlyOneSource() {
        RadarsPair pair = new RadarsPair(src1, src2);
        assertTrue(!pair.hasOnlyOneSource());
        pair = new RadarsPair(src1, srcEmpty);
        assertTrue(pair.hasOnlyOneSource());
        pair = new RadarsPair(srcEmpty, src2);
        assertTrue(pair.hasOnlyOneSource());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.RadarsPair#getSource1()}.
     */
    @Test
    public void shouldGetSource() {
        RadarsPair pair = new RadarsPair(src1, src2);
        assertEquals(src1, pair.getSource1());
        assertEquals(src2, pair.getSource2());
        
        pair = new RadarsPair(src2, src1);
        assertEquals(src1, pair.getSource1());
        assertEquals(src2, pair.getSource2());
        
    }


    /**
     * Test method for {@link pl.imgw.jrat.calid.data.RadarsPair#getBothSources()}.
     */
    @Test
    public void shouldGetBothSources() {
        RadarsPair pair = new RadarsPair(src1, src2);
        assertTrue(pair.getBothSources().contains(src1) && pair.getBothSources().contains(src2));
    }

}
