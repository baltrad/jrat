/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.Test;


/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultLoaderTest {

    File coordsfile = new File("test-data/calid", "coords.xml");

    File resultsfile = new File("test-data/calid", "20130511.results");
    
    
    @Test
    public void shouldLoadResult() {

        Double[] expected = { 6.5, null, null, null, -1.0, 3.0, 2.0, -2.5, 2.5,
                null, -3.5, 5.5, 6.5, 7.0, -2.0, 2.0, 0.0, 2.0, 5.0, 4.0, 1.5,
                -2.0, -0.5, -2.5, 3.5, -1.5, 3.0, -4.0, -2.0, -1.5, 0.5, 2.0,
                7.5, -1.5, 13.0, 6.5, 8.0, 5.0, 0.5, 7.5, -0.5, 0.5, null,
                -3.0, 2.5, null, null, null };

        int r1understate = 2;
        int r2understate = 1;
        
        CalidSingleResultContainer results = new CalidSingleResultContainer(null, null);
        //DATE
        Date date = new Date(113, 4, 11, 4, 30);
        results.setResultDate(date);
        
        
        List<PairedPoint> points = CalidCoordsLoader.loadCoords(coordsfile, null);
        results.setPairedPointsList(points);
        
        assertTrue(CalidResultLoader.loadSingleResult(results, resultsfile));
        for (int i = 0; i < expected.length; i++)
            assertEquals(expected[i], results.getPairedPointsList().get(i)
                    .getDifference());
        assertEquals(r1understate, results.getR1understate());
        assertEquals(r2understate, results.getR2understate());

    }

    @Test
    public void shouldLoadResultFromLine() {
        String line = "2013-05-11/15:10 6.5 4.0 6.5 7.5 6.5 6.0 7.0 7.5 5.5 10.5 7.0 4.0 " +
        		"6.5 3.5 7.5 3.5 4.5 0.5 1.5 6.5 6.5 7.0 5.0 9.0 2.5 12.0 4.5 2.5 4.5 2.5 " +
        		"5.0 -0.5 5.5 4.0 n 1.0 -2.0 2.5 -0.5 -1.5 2.0 5.5 3.0 0.0 n 11.5 3.5 3.5 2 0";
        Date from = new Date(113,4,11,15,00);
        Date to = new Date(113,4,11,15,20);
        CalidParameters params = new CalidParameters();
        params.setRangeDates(from, to);
        CalidSingleResultContainer result = CalidResultLoader.loadResultsFromLine(line, params);
        assertNotNull(result);
        assertEquals(4.59, CalidStatistics.getMean(result), 0.01);
    }
    
    @Test
    public void shouldntLoadResultFromLine() {
        String line = "2013-05-11/15:10 6.5 4.0 6.5 7.5 6.5 6.0 7.0 7.5 5.5 10.5 7.0 4.0 " +
                "6.5 3.5 7.5 3.5 4.5 0.5 1.5 6.5 6.5 7.0 5.0 9.0 2.5 12.0 4.5 2.5 4.5 2.5 " +
                "5.0 -0.5 5.5 4.0 n 1.0 -2.0 2.5 -0.5 -1.5 2.0 5.5 3.0 0.0 n 11.5 3.5 3.5 2 0";
        Date from = new Date(113,4,11,14,00);
        Date to = new Date(113,4,11,14,20);
        CalidParameters params = new CalidParameters();
        params.setRangeDates(from, to);
        assertNull(CalidResultLoader.loadResultsFromLine(line, params));
        
    }
    
}
