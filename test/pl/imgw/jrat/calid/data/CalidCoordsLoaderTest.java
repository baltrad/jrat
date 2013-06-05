/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidException;
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
public class CalidCoordsLoaderTest {

    {
        LogManager.getInstance()
                .setLogger(new ConsolePrinter(Log.MODE_VERBOSE));
    }
    /*
     * Poznan - Legionowo
     */
    File coordsfile = new File("test-data/calid", "coords.xml");
    File invalidcoordsfile = new File("test-data/calid", "coords.invalid");

    File resultsfile = new File("test-data/calid", "20130511.results");
    
    File poz = new File("test-data/calid/poz/2012010100003100dBZ.vol");
    File leg = new File("test-data/calid/leg/2012010100000600dBZ.vol");
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidCoordsLoader#loadCoords(java.io.File)}.
     */
    @Test
    public void shouldLoadCoords() {
        
        List<PairedPoint> list = CalidCoordsLoader.loadCoords(coordsfile, null);
        assertNotNull(list);
        assertEquals(48, list.size());
        for(PairedPoint p : list)
            assertTrue(p.getBin1() == p.getBin2());
    }

    @Test
    public void shouldLoadCoordsWithVolumesValidation() {
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(leg);
        PolarData vol1 = parser.getPolarData();
        parser.parse(poz);
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        List<PairedPoint> list = CalidCoordsLoader.loadCoords(coordsfile, pair);
        assertNotNull(list);
        assertEquals(48, list.size());
        for (PairedPoint p : list)
            assertTrue(p.getBin1() == p.getBin2());
    }
    
    @Test (expected=CalidException.class)
    public void shouldntLoadCoords() {
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(leg);
        PolarData vol1 = parser.getPolarData();
        parser.parse(poz);
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        CalidCoordsLoader.loadCoords(invalidcoordsfile, pair);
    }
    
}
