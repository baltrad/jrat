/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
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

import pl.imgw.jrat.data.arrays.ArrayData;
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
public class Rainbow125VolTest {

    RawByteDataArray dc;
    File rb53file;
//    Rainbow53VolumeParser rip;
    Rainbow53VolumeParser rip = new Rainbow53VolumeParser();
    DataContainer pdc;
    ParserManager pm;
    
    @Before
    public void setUp() {
        rb53file = new File("test-data", "2013030105301100dBZ.vol");
        pm = new ParserManager();
//        rip = new Rainbow53VolumeParser();
        rip = new Rainbow53VolumeParser();
        pm.setParser(rip);
        
    }
    
    @Test
    public void isValidVolume() {
        pm.initialize(rb53file);
        
        RainbowDataContainer data = (RainbowDataContainer) pm.getProduct();
        
//        System.out.println(data.getArrayList().get("10_rayinfo").getPoint(10, 10));
        
        RainbowVolume vol = new RainbowVolume(data);
        assertTrue(vol.isValid());
        
        ArrayData array = vol.getScan(1.4).getArray();
        for(int x = 0; x < array.getSizeX(); x++) {
            System.out.println(array.getPoint(x, 102));
        }
        
        System.out.println(array.getSizeX() + ", " + array.getSizeY());

    }
    
}
