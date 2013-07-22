/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.CalidSingleResultContainer;
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
public class CalidComparatorTest {

    public static CalidParameters params = new CalidParameters(0.5, 500, 200, -10.0);
    public static CalidParameters rzelwow = new CalidParameters(0.5, 500, 200, 3.0);
    
    {
        LogManager.getInstance()
                .setLogger(new ConsolePrinter(Log.MODE_VERBOSE));
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidComparator#getResults(pl.imgw.jrat.calid.data.PolarVolumesPair, java.lang.Double, java.lang.Double, java.lang.Integer, java.lang.Integer)}.
     */
    @Test @Ignore
    public void shouldGetResults() {
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "T_PAGZ44_C_SOWR_20111010031022.h5"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "T_PAGZ48_C_SOWR_20111010031026.h5"));
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        
        CalidSingleResultContainer result = new CalidSingleResultContainer(params, pair);
        CalidComparator.putResult(result);
                
        assertNotNull(result);
//        assertEquals(-1.37, result.getMean(), 0.001);
        
        
    }
    
    @Test @Ignore
    public void shouldGetResults2() {
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/calid/RzeszolLwow", "2013071914501700dBZ.vol"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/calid/RzeszolLwow", "2013071914501800dBZ.vol"));
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        
        CalidSingleResultContainer result = new CalidSingleResultContainer(params, pair);
        CalidComparator.putResult(result);
                
        assertNotNull(result);
//        assertEquals(-1.37, result.getMean(), 0.001);
        
        
    }

}
