/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.proc.CalidProcessor;
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
public class CalidProcessorTest {

    CalidProcessor proc;
    List<File> files;
    
    {
        LogManager.getInstance()
                .setLogger(new ConsolePrinter(Log.MODE_VERBOSE));
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        files = Arrays.asList(new File("test-data/calid",
                "2012060317401700dBZ.vol"), new File("test-data/calid",
                "2012060317402900dBZ.vol"));
        String[] args = ("date=2013-03-18,2013-03-30 Rzeszow,Brzuchania "
                + "ele=0.5 dis=500 range=200 ref=3.0 freq=10").split(" ");
        proc = new CalidProcessor(args);
    }
    
    @After
    public void tearDown() throws Exception {
        
    }
    
    @Test 
    public void shouldConstruct() {
        assertNotNull(proc);
    }
     
    @Test (expected=CalidException.class)
    public void shouldntConstruct() {
        proc = new CalidProcessor(new String[] {""})  ;
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidProcessor#processFile(java.util.List)}.
     */
    @Test @Ignore
    public void shouldProcessFile() {
        File results = new File(AplicationConstans.ETC,
                "calid/RzeszowBrzuchania/500_0.5_3.0_200/20120603.results");
        results.delete();
        proc.processFile(files);
        assertTrue(results.isFile());
        results.delete();
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidProcessor#getProcessName()}.
     */
    @Test
    public void shouldGetProcessName() {
        assertEquals("CALID Process", proc.getProcessName());
    }

}
