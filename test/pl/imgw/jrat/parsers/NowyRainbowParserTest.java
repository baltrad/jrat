/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.parsers.testing.NowyRainbowParser;
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
public class NowyRainbowParserTest {

    @Test
    public void parseTest() {
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
        NowyRainbowParser rp = new NowyRainbowParser();
        File f = new File("test-data/calid", "2011082113402900dBZ.vol");
        rp.initialize(f);
        RainbowDataContainer data = (RainbowDataContainer) rp.getProduct();
        RainbowVolume vol = new RainbowVolume(data);
        assertTrue(((String) data.getAttributeValue(
                "/volume/scan/slice:refid=0/posangle", "")).matches("0.5"));
        
        ScanContainer scan = vol.getScan(1.4);
        
//        Calendar cal = Calendar.getInstance();
//        cal.set(2011, 7, 1, 1, 40, 50);
//        cal.set(Calendar.MILLISECOND, 0);
//        Date date = cal.getTime();
//        assertTrue(scan.getStartTime().equals(date));
        assertEquals("rays", 361, scan.getNRays());
        assertNotNull(scan.getArray());
        
        assertEquals(10, vol.getAllScans().size());
        
        assertEquals(61, vol.getScan(0.5).getArray()
                .getRawIntPoint(301, 20));
        assertEquals(-1.5d, vol.getScan(0.5).getArray()
                .getPoint(301, 20), 0.1);
        
    }
    
}
