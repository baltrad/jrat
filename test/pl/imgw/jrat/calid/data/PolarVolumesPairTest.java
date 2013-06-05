/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.VolumeParser;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PolarVolumesPairTest {

    PolarData vol1;
    PolarData vol2;
    PolarData volOther;
    PolarVolumesPair pair;
    
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "2011101003102200dBZ.vol"));
        vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2011101003102600dBZ.vol"));
        vol2 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2011101003002600dBZ.vol"));
        volOther = parser.getPolarData();
    }

    @Test(expected=CalidException.class)
    public void shouldThrowCalidException() {
        new PolarVolumesPair(vol1, volOther);
    }
    
    @Test(expected=Exception.class)
    public void shouldThrowException() {
        new PolarVolumesPair(vol1, null);
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PolarVolumesPair#toString()}.
     */
    @Test
    public void shouldToString() {
        pair = new PolarVolumesPair(vol1, vol2);
        assertEquals("PAIR [2011-10-10/03:10] [Rzeszow and Brzuchania]", pair.toString());
        pair = new PolarVolumesPair(vol2, vol1);
        assertEquals("PAIR [2011-10-10/03:10] [Rzeszow and Brzuchania]", pair.toString());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PolarVolumesPair#getVol1()}.
     */
    @Test
    public void shouldGetVol1() {
        pair = new PolarVolumesPair(vol1, vol2);
        assertNotNull(pair.getVol1());
        assertEquals(vol1, pair.getVol1());
        pair = new PolarVolumesPair(vol2, vol1);
        assertEquals(vol1, pair.getVol1());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PolarVolumesPair#getVol2()}.
     */
    @Test
    public void shouldGetVol2() {
        pair = new PolarVolumesPair(vol1, vol2);
        assertNotNull(pair.getVol2());
        assertEquals(vol2, pair.getVol2());
        pair = new PolarVolumesPair(vol2, vol1);
        assertEquals(vol2, pair.getVol2());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PolarVolumesPair#getDate()}.
     */
    @Test
    public void shouldGetDate() {
        pair = new PolarVolumesPair(vol1, vol2);
        assertEquals(new Date(111, 9, 10, 3, 10), pair.getDate());
    }

}
