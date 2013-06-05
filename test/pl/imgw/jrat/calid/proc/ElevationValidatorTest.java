/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.calid.data.RadarsPair;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ElevationValidatorTest {


    PolarVolumesPair pair;
    PolarVolumesPair invalidPair;
    
    
    @Before
    public void setUp() throws Exception{
        PolarData vol1;
        PolarData vol2;
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "2011101003102200dBZ.vol"));
        vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2011101003102600dBZ.vol"));
        vol2 = parser.getPolarData();
        pair = new PolarVolumesPair(vol1, vol2);
        
        parser.parse(new File("test-data/pair", "2013051810500000dBZ.vol"));
        vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2013051810500400dBZ.vol"));
        vol2 = parser.getPolarData();
        invalidPair = new PolarVolumesPair(vol1, vol2);
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.ElevationValidator#isValid(pl.imgw.jrat.calid.data.PolarVolumesPair, double)}.
     */
    @Test 
    public void shouldBeValid() {
        assertTrue(ElevationValidator.isValid(pair, 0.5));
        assertTrue(ElevationValidator.isValid(pair, 1.4));
        assertTrue(ElevationValidator.isValid(pair, 2.4));
        assertTrue(ElevationValidator.isValid(pair, 3.4));
        assertTrue(ElevationValidator.isValid(pair, 5.3));
        assertTrue(ElevationValidator.isValid(pair, 7.7));
        assertTrue(ElevationValidator.isValid(pair, 10.6));
        assertTrue(ElevationValidator.isValid(pair, 14.1));
        assertTrue(ElevationValidator.isValid(pair, 18.5));
        
        assertTrue(!ElevationValidator.isValid(pair, 0));
        assertTrue(!ElevationValidator.isValid(pair, 14));
    }

    @Test(expected=CalidException.class)
    public void shouldThrowException1() {
        ElevationValidator.isValid(null, 0);
    }
    
    @Test(expected=CalidException.class)
    public void shouldThrowException2() {
        ElevationValidator.getElevation(null, new CalidParameters());
    }
    
    @Test(expected=CalidException.class)
    public void shouldThrowException3() {
        ElevationValidator.getElevation(pair, null);
    }
    
    @Test(expected=CalidException.class)
    public void shouldThrowException4() {
        ElevationValidator.getElevation(invalidPair, new CalidParameters());
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.ElevationValidator#getElevation(pl.imgw.jrat.calid.data.PolarVolumesPair, pl.imgw.jrat.calid.data.CalidParameters)}.
     */
    @Test
    public void shouldGetElevation() {
       Double ele = ElevationValidator.getElevation(pair, new CalidParameters());
       assertEquals(0.5, ele, 0.001);
       ele = ElevationValidator.getElevation(pair, new CalidParameters(.0,0,0,.0));
       assertNull(ele);
       ele = ElevationValidator.getElevation(pair, new CalidParameters(1.4d,0,0,0d));
       assertEquals(1.4, ele, 0.001);
    }

}
