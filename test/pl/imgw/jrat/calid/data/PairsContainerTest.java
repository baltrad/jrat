/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
public class PairsContainerTest {

    {
        LogManager.getInstance().setLogger(new ConsolePrinter(Log.MODE_SILENT));

    }
    
    PairsContainer pairs;
    
    List<File> files;
    
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        File folder = new File("test-data/pair");
        files = Arrays.asList(folder.listFiles());
        pairs = new PairsContainer(files);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PairsContainer#hasNext()}.
     */
    @Test
    public void shouldHasNext() {
        files = new ArrayList<File>();
        files.add(new File("test-data/pair", "2011101003002600dBZ.vol"));
        files.add(new File("test-data/pair", "2011101003002700dBZ.vol"));
        pairs = new PairsContainer(files);
        assertTrue(pairs.hasNext());
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PairsContainer#next()}.
     */
    @Test
    public void shouldNext() {
        files = new ArrayList<File>();
        files.add(new File("test-data/pair", "2011101003002600dBZ.vol"));
        files.add(new File("test-data/pair", "2011101003002700dBZ.vol"));
        pairs = new PairsContainer(files);
        assertNotNull(pairs.next());
        assertTrue(!pairs.hasNext());
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PairsContainer#next()}.
     */
    @Test 
    public void shouldMultiNext() {
        int i = 0;
        while(pairs.hasNext()) {
            RadarsPair p = pairs.next();
            assertNotNull(p);
            i++;
        }
        assertEquals(pairs.getSize(), i);
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PairsContainer#getSize()}.
     */
    @Test
    public void shouldGetSize() {
        assertEquals(15, pairs.getSize());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.PairsContainer#getPair(java.util.Date, java.lang.String, java.lang.String)}.
     */
    @Test @Ignore
    public void shouldGetPair() {
        Date date = null;
        String r1 = "";
        String r2 = "";
        assertNull(pairs.getPair(date, r1, r2));
        fail("Method not implemented");
        
        date = new Date(111, 9, 10, 14, 30);
        r1 = "Rzeszow";
        r2 = "12579";
        assertNotNull(pairs.getPair(date, r1, r2));
        
    }

}
