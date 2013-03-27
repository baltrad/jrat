/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.arrays.RawByteDataArray;
import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.ScanContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.Rainbow53VolumeParser;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowVolumeParserTest {

    RawByteDataArray dc;
    File rb53file;
    File rb52file;
//    Rainbow53VolumeParser rip;
    DefaultParser rip = new DefaultParser();
    DataContainer pdc;
    ParserManager pm;
    
    @Before
    public void setUp() {
        rb53file = new File("test-data", "1.vol");
        rb52file = new File("test-data", "old.vol");
        pm = new ParserManager();
//        rip = new Rainbow53VolumeParser();
        rip = new DefaultParser();
        pm.setParser(rip);
        
    }
    
    @Test
    public void isValidVolume() {
        pm.initialize(rb53file);
        
        RainbowDataContainer data = (RainbowDataContainer) pm.getProduct();
        
//        System.out.println(data.getArrayList().get("10_rayinfo").getPoint(10, 10));
        
        RainbowVolume vol = new RainbowVolume(data);
        assertTrue(vol.isValid());

    }
    
    @Test
    public void getAttributeTest() {
        pm.initialize(rb53file);
        RainbowDataContainer data = (RainbowDataContainer) pm.getProduct();
        RainbowVolume vol = new RainbowVolume(data);
        assertTrue(((String) data.getAttributeValue(
                "/volume/scan/slice:refid=0/posangle", "")).matches("0.5"));
        
        assertEquals(1.0, vol.getBeamwidth(), 0.0001);
        assertEquals(0.0531, vol.getWavelength(), 0.0001);
//        assertEquals(0.0, vol.getBeamwidth(), 5.3);
        
        ScanContainer scan = vol.getScan(1.4);
        
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 7, 1, 1, 40, 50);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertTrue(scan.getStartTime().equals(date));
        assertEquals("rays", 361, scan.getNRays());
        assertNotNull(scan.getArray());
        
        assertEquals(3.0, scan.getRPM(), 0.1);
        assertEquals(0.5, scan.getGain(), 0.01);
        assertEquals(-32.0, scan.getOffset(), 0.1);
        
        
        assertEquals(0, (int)scan.getNodata());
        assertEquals(0, (int)scan.getUndetect());
        
        assertEquals(10, vol.getAllScans().size());
        
    }
    
    @Test
    public void arrayValueTest() {
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
        Rainbow53VolumeParser rp = new Rainbow53VolumeParser();
        File f = new File("test-data", "1.vol");
        rp.initialize(f);
        RainbowDataContainer data = (RainbowDataContainer) rp.getProduct();
        RainbowVolume vol = new RainbowVolume(data);
        assertTrue(((String) data.getAttributeValue(
                "/volume/scan/slice:refid=0/posangle", "")).matches("0.5"));
        
        ScanContainer scan = vol.getScan(1.4);
        
        assertEquals("rays", 361, scan.getNRays());
        assertNotNull(scan.getArray());
        
        assertEquals(10, vol.getAllScans().size());
        
        assertEquals(61, vol.getScan(0.5).getArray()
                .getRawIntPoint(55, 1));
        assertEquals(-1.5d, vol.getScan(0.5).getArray()
                .getPoint(55, 1), 0.1);
        
    }
    
    @Test
    public void isValidFileTest() {
        assertTrue("this is not a rainbow file", pm.isValid(rb53file));
        
    }
    
    @Test
    public void isValid52VolumeTest() {
        
        assertTrue("this is not a rainbow 5.2 file", pm.isValid(rb52file));
        assertTrue(pm.initialize(rb52file));
        RainbowVolume vol = new RainbowVolume((RainbowDataContainer) pm.getProduct());
        assertEquals(453.0, vol.getHeight(), 0.01);
        assertEquals(20.079720, vol.getLon(), 0.01);
        assertEquals(50.394169, vol.getLat(), 0.01);
        assertTrue(vol.getSiteName().matches("Brzuchania"));
        
        }
    
    @Test
    public void initializeTest() {
        assertTrue(pm.initialize(rb53file));
        pdc = pm.getProduct();
        assertNotNull("Initialization failed", pdc);
        
    }
    
}
