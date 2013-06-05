/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.PairedPoint;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;
import pl.imgw.util.ConsolePrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidCoordsCalcTest {

    {
        LogManager.getInstance()
                .setLogger(new ConsolePrinter(Log.MODE_VERBOSE));
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidCoordsCalc#calculateCoords(pl.imgw.jrat.calid.data.CalidParameters, pl.imgw.jrat.calid.data.PolarVolumesPair)}.
     */
    @Test
    public void shouldCalculateCoords() {
        int maxrange = 200;
        CalidParameters params = new CalidParameters(0.5, 500, maxrange, 4.0);
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "T_PAGZ48_C_SOWR_20111010030027.h5"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "T_PAGZ44_C_SOWR_20111010030026.h5"));
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        
        List<PairedPoint> list = CalidCoordsCalc.calculateCoords(params, pair);
        assertNotNull(list);
        assertEquals(90, list.size());
        for(PairedPoint point : list) {
            assertTrue(point.getBin1() == point.getBin2());
            assertTrue(point.getBin1() < maxrange);
        }
    }

    @Test (expected=CalidException.class)
    public void shouldTrhowsExceptionWrongElevation() {
        int maxrange = 200;
        CalidParameters params = new CalidParameters(0.5, 500, maxrange, 4.0);
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "2013051810500000dBZ.vol"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2013051810500400dBZ.vol"));
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        CalidCoordsCalc.calculateCoords(params, pair);
        
    }
    
    @Test (expected=CalidException.class)
    public void shouldTrhowsExceptionRadarsTooFar() {
        int maxrange = 200;
        CalidParameters params = new CalidParameters(0.5, 500, maxrange, 4.0);
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "2013052200201400dBZ.vol"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2013052200202400dBZ.vol"));
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        CalidCoordsCalc.calculateCoords(params, pair);
    }
    
}
