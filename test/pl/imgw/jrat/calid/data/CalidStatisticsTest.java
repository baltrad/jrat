/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.calid.proc.CalidComparatorTest;
import pl.imgw.jrat.calid.proc.CalidCoordsHandlerTest;
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
public class CalidStatisticsTest {

    {
        LogManager.getInstance()
                .setLogger(new ConsolePrinter(Log.MODE_VERBOSE));
    }

    
    CalidSingleResultContainer result;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        RadarsPair pair = new RadarsPair("12579", "12568");
        Date date = new Date(111, 9, 10, 03, 10, 0);
        result = CalidResultLoader.loadSingleResult(CalidComparatorTest.params,
                pair, date); 
        System.out.println(result.getPairedPointsList().size());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldGetFreq() {
         Integer freq = CalidStatistics.getFreq(result);
         assertEquals(4, freq.intValue());
    }
    
    @Test
    public void shouldGetMean() {
        int perc = 3; 
        Double mean = CalidStatistics.getMean(result, perc);
        assertEquals(-1.375, mean, 0.01);
        mean = CalidStatistics.getMean(result);
        assertEquals(-1.375, mean, 0.01);
    }

    @Test
    public void shouldGetMedian() {
        int perc = 3; 
        Double median = CalidStatistics.getMedian(result, perc);
        assertEquals(-1.5, median, 0.01);
        median = CalidStatistics.getMedian(result);
        assertEquals(-1.5, median, 0.01);
    }
 
    @Test
    public void shouldGetRMS() {
        int perc = 3; 
        Double rms = CalidStatistics.getRMS(result, perc);
        assertEquals(1.64, rms, 0.01);
        rms = CalidStatistics.getRMS(result);
        assertEquals(1.64, rms, 0.01);
    }
    
}
