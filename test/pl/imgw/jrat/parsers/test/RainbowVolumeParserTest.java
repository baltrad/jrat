/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers.test;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.RawByteDataContainer;
import pl.imgw.jrat.data.ProductContainer;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.RainbowParser;
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

    RawByteDataContainer dc;
    File file;
    RainbowParser rip;
    ProductContainer pdc;
    ParserManager pm;
    
    @Before
    public void setUp() {
        file = new File("test-data", "1.vol");
        
        pm = new ParserManager();
        rip = new RainbowParser(new RainbowVolumeFieldsName());
        pm.setParser(rip);
        
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
