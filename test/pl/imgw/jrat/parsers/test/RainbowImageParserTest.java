/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import pl.imgw.jrat.data.ArrayDataContainer;
import pl.imgw.jrat.data.ByteDataContainer;
import pl.imgw.jrat.data.ProductDataContainer;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.RainbowImageFieldsName;
import pl.imgw.jrat.data.parsers.RainbowParser;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowImageParserTest {

    ByteDataContainer dc;
    File file;
    RainbowParser rip;
    ProductDataContainer pdc;
    
    @Before
    public void setUp() {
        file = new File("test-data", "1.cmax");
        
        ParserManager pm = new ParserManager();
        rip = new RainbowParser(new RainbowImageFieldsName());
        pm.setParser(rip);
        pm.initialize(file);
        pdc = pm.getProduct();
    }
    
    @Test
    public void initializeTest() {
        
        assertNotNull("Initialization failed", pdc);
        
    }
    
    @Test
    public void attributeTest() {
        
        assertTrue("Attribute initialization failed", rip.initiazlizeRainbowAttributes());
        assertEquals("Bad product name:", "CMAX",
                (String) pdc.getAttributeValue("/product", "name"));
        assertEquals("Bad product time:", "13:00:00",
                (String) pdc.getAttributeValue("/product/data", "time"));
        assertEquals("Bad product projection:", "aeqd",
                (String) pdc.getAttributeValue("/product/data/viewparams/proj",
                        ""));

        ArrayDataContainer adc = pdc.getArray(1);
        
        int point = 51;
        byte bpoint = 51;
        
        assertEquals("Comparing byte value in DATA array failed:", bpoint, adc.getBytePoint(356, 234));
        assertEquals("Comparing int value in DATA array failed:", point, adc.getIntPoint(356, 234));
        assertEquals("Comparing int value in DATA array failed:", -1, adc.getIntPoint(1111, 1111));
    }

}
