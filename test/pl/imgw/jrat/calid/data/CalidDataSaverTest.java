/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.proc.ElevationValidator;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;
import pl.imgw.jrat.tools.out.XMLHandler;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidDataSaverTest {

    File filec = new File("test-data/calid", "coords.test");
    File filer = new File("test-data/calid", "results.test");
    PolarVolumesPair pair;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        filec.delete();
        filer.delete();
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "2011101003102200dBZ.vol"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2011101003102600dBZ.vol"));
        PolarData vol2 = parser.getPolarData();
        pair = new PolarVolumesPair(vol1, vol2);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        filec.delete();
        filer.delete();
    }

    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.data.CalidDataSaver#saveResults(pl.imgw.jrat.calid.data.CalidSingleResultContainer, pl.imgw.jrat.calid.data.CalidResultParameters)}
     * .
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void shouldSaveResults() throws FileNotFoundException {
        // PAIR (field variable)
        // DATE from volumes pair
        // POINTS
        List<PairedPoint> points = new ArrayList<PairedPoint>();
        PairedPoint point = new PairedPoint(1, 2, 5, 2);
        point.setDifference(-5.0);
        points.add(point);

        // PARAMETERS
        CalidParameters p = new CalidParameters();
        CalidSingleResultContainer result = new CalidSingleResultContainer(p,
                pair);
        result.setR1understate(2);
        result.setR2understate(8);
        result.setPairedPointsList(points);
        CalidDataSaver.saveResults(result, filer);
        assertTrue(filer.exists());
        Scanner scanner = new Scanner(filer);
        assertEquals(
                "# src=Rzeszow,Brzuchania ele=0.0 dis=1000 ref=0.0",
                scanner.nextLine());
        assertEquals("2011-10-10/03:10 -5.0 2 8", scanner.nextLine());
        scanner.close();

    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidDataSaver#saveCoords(pl.imgw.jrat.calid.data.CalidSingleResultContainer, pl.imgw.jrat.calid.data.CalidResultParameters)}.
     */
    @Test
    public void shouldSaveCoords() {
        
        //PAIR (field variable)
        
        // POINTS
        List<PairedPoint> points = new ArrayList<PairedPoint>();
        PairedPoint point = new PairedPoint(1, 2, 5, 2);
        points.add(point);
        
        //PARAMETERS
        CalidParameters p = new CalidParameters();
        p.setElevation(ElevationValidator.getElevation(pair, p));
        CalidSingleResultContainer result = new CalidSingleResultContainer(p,pair);

        result.setPairedPointsList(points);

        CalidDataSaver.saveCoords(filec, result);
        assertTrue(filec.exists());
        assertNotNull(XMLHandler.loadXML(filec));
    }

}
