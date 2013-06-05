/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.RadarsPair;
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
public class CalidResultFileGetterTest {


   private File folder = new File("test-data/calid");
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.view.CalidResultFileGetter#getResultFiles(pl.imgw.jrat.calid.data.RadarsPair, pl.imgw.jrat.calid.data.CalidParameters)}.
     */
    @Test
    public void shouldGetResultFilesByAllParams() {
        RadarsPair pair = new RadarsPair("Poznan", "Swidwin");
        CalidParameters params = new CalidParameters(0.5, 500, 200, 3.0);
        Date startRangeDate = new Date(113, 5, 1);
        Date endRangeDate = new Date(113, 5, 2);
        params.setRangeDates(startRangeDate, endRangeDate);
        Set<File> set = CalidResultFileGetter.getResultFiles(pair, params, folder);
        assertEquals(2, set.size());
        
    }

    @Test
    public void shouldntGetResultFilesByDateAndSrc() {
        RadarsPair pair = new RadarsPair("Poznan", "Swidwin");
        CalidParameters params = new CalidParameters(0.5, 500, 200, 3.0);
        Date startRangeDate = new Date(113, 5, 5);
        params.setRangeDates(startRangeDate, null);
        Set<File> set = CalidResultFileGetter.getResultFiles(pair, params, folder);
        assertTrue(set.isEmpty());
    }
    
    @Test
    public void shouldGetResultFilesByOneSrcName() {
        RadarsPair pair = new RadarsPair("", "Poznan");
        CalidParameters params = new CalidParameters(null, null, null, null);
        Set<File> set = CalidResultFileGetter.getResultFiles(pair, params, folder);
        assertEquals(8, set.size());
        
    }
    
    @Test
    public void shouldGetResultFilesByOneSrcNameAndDate() {
        RadarsPair pair = new RadarsPair("", "Poznan");
        CalidParameters params = new CalidParameters(null, null, null, null);
        Date startRangeDate = new Date(113, 5, 1);
        Date endRangeDate = new Date(113, 5, 2);
        params.setRangeDates(startRangeDate, endRangeDate);
        Set<File> set = CalidResultFileGetter.getResultFiles(pair, params, folder);
        assertEquals(4, set.size());
        
    }
    
    @Test
    public void shouldGetResultFilesByDate() {
        CalidParameters params = new CalidParameters(null, null, null, null);
        Date startRangeDate = new Date(113, 5, 1);
        Date endRangeDate = new Date(113, 5, 2);
        params.setRangeDates(startRangeDate, endRangeDate);
        Set<File> set = CalidResultFileGetter.getResultFiles(null, params, folder);
        for(File f : set)
            System.out.println(f);
        assertEquals(4, set.size());
        
    }
    
    @Test
    public void shouldGetResultFilesDistance() {
        CalidParameters params = new CalidParameters(null, 500, null, null);
        Set<File> set = CalidResultFileGetter.getResultFiles(null, params, folder);
        assertEquals(4, set.size());
        
    }
    
    @Test
    public void shouldGetResultFilesElevationAndReflectivity() {
        CalidParameters params = new CalidParameters(0.5, null, null, 3.0);
        Set<File> set = CalidResultFileGetter.getResultFiles(null, params, folder);
        assertEquals(8, set.size());
        
    }
    
    @Test
    public void shouldGetResultFilesRangeAndDate() {
        CalidParameters params = new CalidParameters(null, null, 200, null);
        Date startRangeDate = new Date(113, 5, 1);
        Date endRangeDate = new Date(113, 5, 2);
        params.setRangeDates(startRangeDate, endRangeDate);
        Set<File> set = CalidResultFileGetter.getResultFiles(null, params, folder);
        assertEquals(4, set.size());
        
    }
    
}
