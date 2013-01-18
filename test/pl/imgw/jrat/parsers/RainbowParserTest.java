/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.arrays.RainbowVolumeDataArray;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.ScanContainer;
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
public class RainbowParserTest {

    @Test
    public void parseTest() {
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
        Rainbow53VolumeParser rp = new Rainbow53VolumeParser();
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
        
        assertEquals(52, vol.getScan(0.5).getArray()
                .getRawIntPoint(20, 301));
        assertEquals(-6.0d, vol.getScan(0.5).getArray()
                .getPoint(20, 301), 0.1);
        
        assertEquals(ArrayData.NODATA_RAW_INT_POINT, vol.getScan(0.5).getArray()
                .getRawIntPoint(20, 370));
        assertEquals(ArrayData.NODATA_POINT, vol.getScan(0.5).getArray()
                .getPoint(400, 301), 0.1);
        
        RainbowVolumeDataArray array = (RainbowVolumeDataArray) vol.getScan(0.5).getArray();
        assertEquals(355, array.getAzimuth(355), 0.1);
        
//        for(int i = 0; i < array.getSizeY(); i++)
//            System.out.println(i + " " + array.getAzimuth(i));
        
    }
    
}
