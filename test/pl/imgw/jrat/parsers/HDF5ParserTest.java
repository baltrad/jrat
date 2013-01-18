/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5CompoImage;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.ScanContainer;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.parsers.FileParser;
import pl.imgw.jrat.data.parsers.OdimH5Parser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
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
    OdimH5CompoImage pdc1;
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
        
        x = 677;
        y = 1190;
        value = 18.5;
        
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
    }
    
    
    //------------ volumes tests ---------------------
        
    @Test
    public void getScanTest() {
        pm.initialize(file2);
        OdimDataContainer data = (OdimDataContainer) pm.getProduct();
        VolumeContainer vol = new OdimH5Volume(data);
        ScanContainer scan = vol.getScan(10.6);
        assertEquals("nbins is wrong", 250, scan.getNBins());
        assertEquals("nrays is wrong", 360, scan.getNRays());
        assertEquals("nbins is wrong", 1000.0, scan.getRScale(), 0.01);
        ArrayData array = scan.getArray();
        assertEquals("array value", 36, array.getRawIntPoint(18, 138));
        assertEquals(10, vol.getAllScans().size());
    }
    
    
    //--------------- general tests ------------------------------
    @Test
    public void initializationTest() {
        
        assertTrue("This is not a hdf5 file", pm.isValid(file1));
        assertTrue("This is not a hdf5 file", pm.isValid(file2));
        
        pm.initialize(file1);
        pdc1 = new OdimH5CompoImage((OdimDataContainer) pm.getProduct());
        
        pm.initialize(file2);
        pdc2 = new OdimH5Volume((OdimDataContainer) pm.getProduct());
        
        assertTrue("Initialization failed", pdc1.isValid());
        assertNotNull("Initializationf of arrays failed", pdc1.getData());
        
        assertEquals(1900, pdc1.getXSize());
        
        assertNotNull("Initialization failed", pdc2);
//        assertEquals(2200, pdc1.getArray(0).getSizeX());
        
    }
    
    @Test
    public void gettingAttributeTest() {
        pm.initialize(file1);
        pdc1 = new OdimH5CompoImage((OdimDataContainer) pm.getProduct());
        
        pm.initialize(file2);
        pdc2 = new OdimH5Volume((OdimDataContainer) pm.getProduct());
        
        int i = pdc1.getXSize();
        assertEquals("xsize in where group is wrong:", 1900, i);
        String s = pdc1.getSourceName();
        assertEquals("source name is wrong:", "ORG:247", s);
        double d = pdc1.getXScale();
        assertEquals("xscale in where group is wrong:", 2000.0, d, 0);
        
        ScanContainer scan = pdc2.getScan(0.5);
        
        assertEquals(3.0, scan.getRPM(), 0.1);
        assertEquals(0.5, scan.getGain(), 0.01);
        assertEquals(-32.0, scan.getOffset(), 0.1);
        
        assertEquals(255, (int)scan.getNodata());
        assertEquals(0, (int)scan.getUndetect());
        
    }

    @Test
    public void gettingDataValues() {
        pm.initialize(file1);
        pdc1 = new OdimH5CompoImage((OdimDataContainer) pm.getProduct());
        
        pm.initialize(file2);
        pdc2 = new OdimH5Volume((OdimDataContainer) pm.getProduct());
        
        byte b  = pdc1.getData().getRawBytePoint(x, y);
        assertEquals((byte)value, b);
        short i = pdc1.getData().getRawIntPoint(x, y);
        assertEquals((int)value, i);
        double d = pdc1.getData().getPoint(x, y);
        assertEquals(value, d, 0.1);
        
        b = pdc2.getScan(0.5).getArray().getRawBytePoint(10, 400);
        assertEquals(ArrayData.NODATA_RAW_BYTE_POINT, b);
        i = pdc2.getScan(0.5).getArray().getRawIntPoint(400, 2);
        assertEquals(ArrayData.NODATA_RAW_INT_POINT, i);
        d = pdc2.getScan(0.5).getArray().getPoint(300, 400);
        assertEquals(ArrayData.NODATA_POINT, d, 0.1);
        
        
        int x = 166;
        int y = 170;
        int val = 84;
        double rv = -32.0 + 0.5 * val;
        
        assertEquals(val, pdc2.getScan(0.5).getArray().getRawIntPoint(x, y));
        assertEquals(rv, pdc2.getScan(0.5).getArray().getPoint(x, y), 0.01);
        
//        System.out.println(pdc2.getScan(0.5).getArray().getPoint(10, 10));
//        System.out.println(pdc2.getScan(0.5).getArray().getRawIntPoint(10, 10));
    }
    
}
