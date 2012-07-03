/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.ProductDataContainer;
import pl.imgw.jrat.data.parsers.FileParser;
import pl.imgw.jrat.data.parsers.OdimH5Parser;
import pl.imgw.jrat.data.parsers.ParserManager;
import static org.junit.Assert.*;
/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class HDF5ParserTest {
    
    File file1;
    File file2;
    FileParser fp;
    ProductDataContainer pdc1;
    ProductDataContainer pdc2;
    int x,y;
    double value;
    @Before
    public void setUp() {
        file1 = new File("test-data", "1img.hdf");
        file2 = new File("test-data", "2vol.h5");
        
        ParserManager pm = new ParserManager();
        pm.setParser(new OdimH5Parser());
        pm.initialize(file1);
        pdc1 = pm.getProduct();
        
        pm.initialize(file2);
        pdc2 = pm.getProduct();
        
        x = 900;
        y = 1169;
        value = 11.9;
    }
    
    @Test
    public void initializationTest() {
        
        assertNotNull("Initialization failed", pdc1);
        assertTrue("Initializationf of arrays failed", pdc1.getArrayList().size() > 0);
        
        assertEquals(2200, pdc1.getArray(1).getSizeX());
        
        assertNotNull("Initialization failed", pdc2);
        assertTrue("Initializationf of arrays failed", pdc2.getArrayList().size() > 0);
//        assertEquals(2200, pdc1.getArray(0).getSizeX());
        
    }
    
    @Test
    public void gettingAttributeTest() {
        int i = (Integer) pdc1.getAttributeValue("/where", "xsize");
        assertEquals("xsize in where group is wrong:", 1900, i);
        String s = (String) pdc1.getAttributeValue("/what", "source");
        assertEquals("xsize in where group is wrong:", "ORG:247", s);
        double d = (Double) pdc1.getAttributeValue("/where", "xscale");
        assertEquals("xsize in where group is wrong:", 2000.0, d, 0);
    }

    @Test
    public void gettingDataValues() {
        byte b  = pdc1.getArray(1).getBytePoint(x, y);
        assertEquals((byte)value, b);
        short i = pdc1.getArray(1).getIntPoint(x, y);
        assertEquals((int)value, i);
        double d = pdc1.getArray(1).getDoublePoint(x, y);
        assertEquals(value, d, 0.1);
    }
    
}
