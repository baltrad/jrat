/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.RawByteDataArray;
import pl.imgw.jrat.data.RainbowCMAX;
import pl.imgw.jrat.data.RainbowData;
import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.RainbowImageFieldsName;
import pl.imgw.jrat.data.parsers.RainbowParser;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowCMAXParserTest {

    RawByteDataArray dc;
    File file;
    RainbowParser rip;
    RainbowCMAX pdc;
    ParserManager pm;

    @Before
    public void setUp() {
        file = new File("test-data", "2012032609103300dBZ.cmax");

        pm = new ParserManager();
        rip = new RainbowParser(new RainbowImageFieldsName());
        pm.setParser(rip);
        
    }

    @Test
    public void isValidFileTest() {
        assertTrue("this is not a rainbow file", pm.isValid(file));
    }
    
    @Test
    public void initializeTest() {
        pm.initialize(file);
        pdc = new RainbowCMAX((RainbowData) pm.getProduct());
        assertTrue("Initialization failed", pdc.isValid());

    }

    @Test
    public void attributeTest() {
        pm.initialize(file);
        pdc = new RainbowCMAX((RainbowData) pm.getProduct());
        
        assertEquals("Bad source name:", "Gdansk", pdc.getSourceName());
        assertEquals("Bad xscale vale:", 1000.0, pdc.getXScale(), 0.1);
        assertEquals("Bad yscale vale:", 1000.0, pdc.getYScale(), 0.1);
        assertEquals("Bad xsize vale:", 500, pdc.getXSize());
        assertEquals("Bad ysize vale:", 500, pdc.getYSize());
        
        ArrayData adc = pdc.getData();

        int point = 130;

        assertEquals("int value in DATA array failed:", point,
                adc.getRawIntPoint(304, 192));
        assertEquals("int value in DATA array failed:", point,
                adc.getRawIntPoint(304, 192));
        assertEquals("dBZ value in DATA array failed:", 33.5, 
                adc.getPoint(304, 192), 0.1);
        assertEquals("dBZ value in DATA array failed:", -9999, 
                adc.getPoint(1304, 2192), 0.1);
        
        assertTrue("byte value in DATA array failed:", 
                adc.getRawBytePoint(304, 192) < 0);
        
        assertEquals("int value in DATA array failed:", -1,
                adc.getRawIntPoint(1111, 1111));
    }

}
