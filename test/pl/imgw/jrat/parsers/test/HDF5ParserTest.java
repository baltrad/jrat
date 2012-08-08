/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.H5Data;
import pl.imgw.jrat.data.OdimH5Image;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.DataContainer;
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
    OdimH5Image pdc1;
    OdimH5Volume pdc2;
    ParserManager pm;
    int x,y;
    double value;
    @Before
    public void setUp() {
        file1 = new File("test-data", "1img.hdf");
        file2 = new File("test-data", "2vol.h5");
        
        pm = new ParserManager();
        pm.setParser(new OdimH5Parser());
        
        x = 1190;
        y = 677;
        value = 18.5;
    }
    
    @Test
    public void initializationTest() {
        
        assertTrue("This is not a hdf5 file", pm.isValid(file1));
        assertTrue("This is not a hdf5 file", pm.isValid(file2));
        
        pm.initialize(file1);
        pdc1 = new OdimH5Image((H5Data) pm.getProduct());
        
        pm.initialize(file2);
        pdc2 = new OdimH5Volume((H5Data) pm.getProduct());
        
        assertNotNull("Initialization failed", pdc1);
        assertNotNull("Initializationf of arrays failed", pdc1.getData());
        
        assertEquals(1900, pdc1.getXSize());
        
        assertNotNull("Initialization failed", pdc2);
//        assertEquals(2200, pdc1.getArray(0).getSizeX());
        
    }
    
    @Test
    public void gettingAttributeTest() {
        pm.initialize(file1);
        pdc1 = new OdimH5Image((H5Data) pm.getProduct());
        
        pm.initialize(file2);
        pdc2 = new OdimH5Volume((H5Data) pm.getProduct());
        
        int i = pdc1.getXSize();
        assertEquals("xsize in where group is wrong:", 1900, i);
        String s = pdc1.getSourceName();
        assertEquals("xsize in where group is wrong:", "ORG:247", s);
        double d = pdc1.getXScale();
        assertEquals("xsize in where group is wrong:", 2000.0, d, 0);
    }

    @Test
    public void gettingDataValues() {
        pm.initialize(file1);
        pdc1 = new OdimH5Image((H5Data) pm.getProduct());
        
        pm.initialize(file2);
        pdc2 = new OdimH5Volume((H5Data) pm.getProduct());
        
        byte b  = pdc1.getData().getRawBytePoint(x, y);
        assertEquals((byte)value, b);
        short i = pdc1.getData().getRawIntPoint(x, y);
        assertEquals((int)value, i);
        double d = pdc1.getData().getPoint(x, y);
        assertEquals(value, d, 0.1);
    }
    
}
