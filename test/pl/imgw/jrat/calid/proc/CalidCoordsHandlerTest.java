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
import pl.imgw.jrat.calid.data.RadarsPair;
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
public class CalidCoordsHandlerTest {
    
    static {
        LogManager.getInstance()
                .setLogger(new ConsolePrinter(Log.MODE_VERBOSE));
    }
    
    public static RadarsPair pair;
    public static CalidParameters params = new CalidParameters(0.5, 500, 200, 4.0);
    

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "2011101003102200dBZ.vol"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2011101003102600dBZ.vol"));
        PolarData vol2 = parser.getPolarData();
        pair = new PolarVolumesPair(vol1, vol2);

    }
    
    @Test
    public void shouldGetCoordsByVolumes() {
        List<PairedPoint> points = CalidCoordsHandler.getCoords(params, pair);
        assertNotNull(points);
        assertEquals(90, points.size());
    }

    @Test (expected=CalidException.class)
    public void shouldntGetCoordsWrongPair() {
        pair = new RadarsPair("Nysa", "Przemysl");
        CalidCoordsHandler.getCoords(params, pair);
        
    }
    
    
    
    @Test (expected=CalidException.class)
    public void shouldntGetCoordsEmptyParams() {
        CalidCoordsHandler.getCoords(new CalidParameters(), pair);
    }
    
    @Test (expected=CalidException.class)
    public void shouldntGetCoordsWithoutVolumes() {
        pair = new RadarsPair("Rzeszow", "Brzuchania");
        CalidCoordsHandler.getCoords(new CalidParameters(0.5, 750, 250, 9.0), pair);
    }
    
}
