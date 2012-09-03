/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.RainbowData;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.RawByteDataArray;
import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.Rainbow53Parser;
import pl.imgw.jrat.data.parsers.RainbowVolumeFieldsName;
import static org.junit.Assert.*;
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
    File file;
    Rainbow53Parser rip;
    DataContainer pdc;
    ParserManager pm;
    
    @Before
    public void setUp() {
        file = new File("test-data", "1.vol");
        
        pm = new ParserManager();
        rip = new Rainbow53Parser(new RainbowVolumeFieldsName());
        pm.setParser(rip);
        
    }
    
    @Test
    public void isValidVolume() {
        pm.initialize(file);
        RainbowData data = (RainbowData) pm.getProduct();
        RainbowVolume vol = new RainbowVolume(data);
        assertTrue(vol.isValid());

    }
    
    @Test
    public void getAttributeTest() {
        pm.initialize(file);
        RainbowData data = (RainbowData) pm.getProduct();
        RainbowVolume vol = new RainbowVolume(data);
        assertTrue(((String) data.getAttributeValue(
                "/volume/scan/slice:refid=0/posangle", "")).matches("0.5"));
        
        ScanContainer scan = vol.getScan(1.4);
        
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 7, 1, 1, 40, 50);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertTrue(scan.getStartTime().equals(date));
        assertEquals("rays", 361, scan.getNRays());
        assertNotNull(scan.getArray());
        
    }
    
    @Test
    public void isValidFileTest() {
        assertTrue("this is not a rainbow file", pm.isValid(file));
    }
    
    @Test
    public void initializeTest() {
        assertTrue(pm.initialize(file));
        pdc = pm.getProduct();
        assertNotNull("Initialization failed", pdc);
        
    }
    
}