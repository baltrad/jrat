/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers.test;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.ByteDataContainer;
import pl.imgw.jrat.data.ProductDataContainer;
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

    ByteDataContainer dc;
    File file;
    RainbowParser rip;
    ProductDataContainer pdc;
    
    @Before
    public void setUp() {
        file = new File("test-data", "1.vol");
        
        ParserManager pm = new ParserManager();
        rip = new RainbowParser(new RainbowVolumeFieldsName());
        pm.setParser(rip);
        pm.initialize(file);
        pdc = pm.getProduct();
    }
    
    @Test
    public void initializeTest() {
        
        assertNotNull("Initialization failed", pdc);
        
    }
    
}
